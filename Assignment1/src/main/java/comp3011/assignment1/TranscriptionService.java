package comp3011.assignment1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Service
public class TranscriptionService {
	private final String apiKey;
	//tool used later to parse the JSON string returned by OpenAI
	private final ObjectMapper objectmapper = new ObjectMapper();
	private final TokenUsageTracker tokenUsageTracker;
	private final RestClient client;
	
	//value gets the API from the configure so i don't have to write it in the source code
	public TranscriptionService(@Value("${openai.api.key}") String apiKey, TokenUsageTracker tokenUsageTracker) {
		this.apiKey = apiKey;
		this.tokenUsageTracker = tokenUsageTracker;
		
		// asked Claude for help swapping to JDK's HttpClient-based factory,
		// since the old SimpleClientHttpRequestFactory caps concurrent connections
		// to the same host (OpenAI) at a low default, which would bottleneck under load
		HttpClient httpClient = HttpClient.newBuilder()
			    .connectTimeout(Duration.ofSeconds(5))
			    .build();
			
			
			JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
			factory.setReadTimeout(Duration.ofSeconds(15));

			this.client = RestClient.builder().requestFactory(factory).build();
		
	}
	
	public String transcribe(MultipartFile audioFile) 
	{			String rawResponse;
		try {
			// body(audioFile) didn't work as OpenAi needs multi-part/form-data.
			//So i made Claude's help me to build a MultiValueMap than just body(body) which worked yeah.
	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", audioFile.getResource());
	        body.add("model", "gpt-4o-mini-transcribe");
	        
	        //save it in a variable instead of just return it
			 rawResponse =  client.post()
				.uri("https://api.openai.com/v1/audio/transcriptions")
				//all request to third parties that needs a API will need this type of header
				.header("Authorization", "Bearer " + apiKey)
				.body(body)
				.retrieve()
				.body(String.class);
		} catch(Exception e) {
			return "ERROR: " + e.getMessage();
		}
		// parse the raw JSON string from OpenAI into a queryable object, so text and usage can be pulled out separately
		JsonNode root = objectmapper.readTree(rawResponse);
		long inputTokensUsed = root.path("usage").path("input_tokens").asLong(0);
		long outputTokensUsed = root.path("usage").path("output_tokens").asLong(0);
		// accumulate token usage globally, separate from the text returned to the client
		tokenUsageTracker.record(inputTokensUsed, outputTokensUsed);
		return root.path("text").asString();
		}
	}



