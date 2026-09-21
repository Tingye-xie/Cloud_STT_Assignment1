package comp3011.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;



//RANDOM_PORT lets Spring pick any free port instead of fighting over 8080
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HighConcurrencyUptimeTest {
	
	@LocalServerPort
	int port;
	
	@Test
	void singleRequestWorks() {
	    RestClient client = RestClient.create();

	    ResponseEntity<String> response = client.get()
	    		//This rest client has matched how i designed for the get method
	        .uri("http://localhost:" + port + "/api/v1/admin/uptime")
	        .retrieve()
	        .toEntity(String.class);
        
	    //make sure it returns 200 status code
	    assertEquals(200, response.getStatusCode().value());
	}
	    @Test
	    void handles200ConcurrentRequests() throws InterruptedException{
	    AtomicLong successCount = new AtomicLong(0);

	    Runnable task = () -> {
	        RestClient client = RestClient.create();
	        ResponseEntity<String> response = client.get()
	            .uri("http://localhost:" + port + "/api/v1/admin/uptime")
	            .retrieve()
	            .toEntity(String.class);

	        if (response.getStatusCode().value() == 200) {
	            successCount.incrementAndGet();
	        }
	    };
	    
	    List<Thread> threads = new ArrayList<>();
	    
	    Instant start = Instant.now();
	    
	    for (int i = 0; i < 220; i++) {
	        Thread t = new Thread(task);
	        threads.add(t);
	        t.start();
	    }

	    for (Thread t : threads) {
	        t.join();
	    }
	    
	    Duration elapsed = Duration.between(start, Instant.now());

	    assertEquals(220, successCount.get());
	    assertTrue(elapsed.toMillis() < 5000);
	    

}
	}

