package comp3011.assignment1.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.dto.ErrorResponse;
import comp3011.assignment1.service.TranscriptionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;

@RestController
public class TranscriptionController {
	
	//Use a Dependency Injection
	private final TranscriptionService transcriptionService;
	
	public TranscriptionController(TranscriptionService transcriptionService) {
		this.transcriptionService = transcriptionService;
	}
	
	// @RequestParam("audio") tells Spring to pull the file named "audio" from the incoming multi-part request and bind it to audioFile。
	@PostMapping("/api/v1/transcriptions")
	public ResponseEntity<?> getAudio(@RequestParam("audio") MultipartFile audioFile)
	{
	
	    try {
	        String text = transcriptionService.transcribe(audioFile);
	        return ResponseEntity.ok(text);
	    } catch (Exception e) {
	        ErrorResponse error = new ErrorResponse();
	        error.message = e.getMessage();
	        error.path = "/api/v1/transcriptions";
	        error.status = 500;
	        error.timestamp = Instant.now().toString();
	        error.error = "Internal Server Error";
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	    	}
	
}
}

