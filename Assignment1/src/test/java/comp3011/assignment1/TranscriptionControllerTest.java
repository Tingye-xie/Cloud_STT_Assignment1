package comp3011.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.controller.TranscriptionController;
import comp3011.assignment1.service.TranscriptionService;

public class TranscriptionControllerTest {
	//fake service so this test never actually calls OpenAI
	TranscriptionService mockService = mock(TranscriptionService.class);
	TranscriptionController controller = new TranscriptionController(mockService);
	
	//Create a fake mock audio 1.audio is the filename which must match @RequestParam("audio"), the rest were all fake parameters
	MultipartFile fakeAudio = new MockMultipartFile("audio", "test.webm","audio/webm", "fake audio bytes".getBytes());
	
	//test 200
	@Test
	void returnsTranscribedTextWhenServiceSucceeds() {
		//tell the fake service what to return when transcribe() is called
		when(mockService.transcribe(fakeAudio)).thenReturn("hello world");

		ResponseEntity<?> response = controller.getAudio(fakeAudio);

		assertEquals(200, response.getStatusCode().value());
		assertEquals("hello world", response.getBody());
	}

	//testing 500 statusCode 
	@Test
	void returnsErrorResponseWhenServiceThrows() {
		//tell the fake service to simulate OpenAI failing
		doThrow(new RuntimeException("OpenAI is down")).when(mockService).transcribe(fakeAudio);

		ResponseEntity<?> response = controller.getAudio(fakeAudio);

		assertEquals(500, response.getStatusCode().value());
	}
}
