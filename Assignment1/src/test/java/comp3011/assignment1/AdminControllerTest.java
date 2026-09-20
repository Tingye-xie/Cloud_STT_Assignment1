package comp3011.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class AdminControllerTest {
	//pass into the controller object
	TokenUsageTracker tokenUsageTracker = new TokenUsageTracker();
	// fake context so that context.close() does nothing - a real one would kill the test JVM
	ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
	//Create a Controller with fake context
	AdminController controller = new AdminController(tokenUsageTracker, mockContext);
	//have two variables to keep how many successfully shut down and how many did not shutdown due to in progress already
	AtomicLong successCount = new AtomicLong(0);
	AtomicLong conflictCount = new AtomicLong(0);
	
	
	@Test
	void shutdownConcurrencyTest()throws InterruptedException {
		Runnable task = () -> {
			//the response knows what status code was returned
			ResponseEntity<?> response = controller.getShutdownResponse();
			//get the status code from response than save into statusCode that contains the correct return type.
			HttpStatusCode statusCode = response.getStatusCode();
			// get only the number for the StatusCode
			int statusNumber = statusCode.value();
			
			
			//if 202 than add to success variable, if 409 than add to conflict variable
			if(statusNumber == 202) {
				successCount.incrementAndGet();
			} else if(statusNumber == 409) {
				conflictCount.incrementAndGet();
			}
			
		};
		
		//After creating Runnable interface than we create many new threads that starts the runnable interface
		List<Thread> threads = new ArrayList<>();
		//Create 200+ threads that returns 2 different status code
		for(int i = 0; i < 200; i++) {
			//pass in the runnable behavior as task in every new thread
			Thread test = new Thread(task);
			threads.add(test);
			test.start();
			//don't write join() over here since thread1 was waiting, thread2 don't even exist
		}
		//write it in a new for loop when all thread is already created 
		for(Thread test: threads) {
			test.join();
		}
		
		//if a == b pass the test if not than fail the test immediately when run the test
		assertEquals(1, successCount.get());
		assertEquals(199, conflictCount.get());
		
	}

	
}
