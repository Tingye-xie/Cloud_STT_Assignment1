package comp3011.assignment1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranscriptionService {
	private final String apiKey;
	private final RestClient client = RestClient.create();
	//value gets the API from the configure so i don't have to write it in the source code
	public TranscriptionService(@Value("${openai.api.key}") String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String transcribe(MultipartFile audioFile) 
	{			
		try {
			// body(audioFile) didn't work as OpenAi needs multi-part/form-data.
			//So i made Claude's help me to build a MultiValueMap than just body(body) which worked yeah.
	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", audioFile.getResource());
	        body.add("model", "gpt-4o-mini-transcribe");
	        
	        
			return client.post()
				.uri("https://api.openai.com/v1/audio/transcriptions")
				//all request to third parties that needs a API will need this type of header
				.header("Authorization", "Bearer " + apiKey)
				.body(body)
				.retrieve()
				.body(String.class);
		} catch(Exception e) {
			return "ERROR: " + e.getMessage();
		}
		
		}
	}



