package comp3011.assignment1;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TranscriptionController {
	
	//Use a Dependency Injection
	private final TranscriptionService transcriptionService;
	
	public TranscriptionController(TranscriptionService transcriptionService) {
		this.transcriptionService = transcriptionService;
	}
	
	// @RequestParam("audio") tells Spring to pull the file named "audio" from the incoming multi-part request and bind it to audioFile。
	@PostMapping("/api/v1/transcriptions")
	public String getAudio(@RequestParam("audio") MultipartFile audioFile)
	{
	//return what service return to controller to client
	return transcriptionService.transcribe(audioFile);
	
	}
	
}

