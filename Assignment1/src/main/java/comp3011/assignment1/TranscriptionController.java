package comp3011.assignment1;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TranscriptionController {
	// @RequestParam("audio") tells Spring to pull the file named "audio" from the incoming multipart request and bind it to audioFile。

@PostMapping("/api/v1/transcriptions")
	public String getAudio(@RequestParam("audio") MultipartFile audioFile)
	{
	return "received file: " + audioFile.getOriginalFilename();
	}
	
}

