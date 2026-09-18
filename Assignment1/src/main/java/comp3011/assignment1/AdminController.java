package comp3011.assignment1;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class AdminController {
	//dependency injection to track tokens and shutdown the server
	private final TokenUsageTracker tokenUsageTracker;
	private final ConfigurableApplicationContext context;
	// StartTime is captured once when the system starts running, so the time is fixed
	Instant StartTime = Instant.now();
	//set initial status for the service status and use atomic to avoid concurrency
	private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

	public AdminController(TokenUsageTracker tokenUsageTracker, ConfigurableApplicationContext context) {
	    this.tokenUsageTracker = tokenUsageTracker;
	    this.context = context;
	}
	
@GetMapping("/api/v1/admin/uptime")
public ResponseEntity<?> getUpTime() {
	try {
	UptimeResponse uptime = new UptimeResponse();
	//this is when the method is called
	uptime.utcServerStart = StartTime.toString();
	//get the current time
	uptime.utcNow = Instant.now().toString();
	//capture the UpTime using Duration which subtracts b -a
	Duration elapsed = Duration.between(StartTime,Instant.now());
	//after capturing it, get seconds + get_Nano to have a decimal point
	uptime.serverUptimeSeconds = elapsed.getSeconds() + elapsed.getNano() / 1_000_000_000.0;
	//allows return more than 1 return type
	return ResponseEntity.ok(uptime);
	//capture run time failure 500
	} catch (Exception e){
		ErrorResponse error = new ErrorResponse();
		error.message = e.getMessage();
		error.path = "/api/v1/admin/uptime";
		error.status = 500;
		error.timestamp = Instant.now().toString();
		error.error = "Internal Server Error";
		//this is the full way of writing for all status code except 200 using OK.()
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}

@PostMapping("/api/v1/admin/shutdown")
public ResponseEntity<?> getShutdownResponse() {

	ShutdownResponse downTimeMessage = new ShutdownResponse();
	try {
		// atomically flips isShuttingDown from false to true; only the first caller succeeds
	if(isShuttingDown.compareAndSet(false, true)) {
		// change it to shutdown so any repeat requests are treated as a conflict
	downTimeMessage.message = "Graceful shutdown requested.";
	 // delay the actual shutdown so this response has time to reach the client first to display the success shut down
    new Thread(() -> {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        context.close();
    }).start();
	
	return ResponseEntity.status(HttpStatus.ACCEPTED).body(downTimeMessage);
	}
	else {
		//when shutting is already in progress return as a conflict
		ErrorResponse error = new ErrorResponse();
		error.message = "Shutdown already in progress.";
	    error.timestamp = Instant.now().toString();
	    error.status = 409;
	    error.error = "Conflict";
	    error.path = "/api/v1/admin/shutdown";
	return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	}
	//capture runtime failure 500
	catch(Exception e) {
		ErrorResponse error = new ErrorResponse();
		error.message = e.getMessage();
		error.path = "/api/v1/admin/shutdown";
		error.status = 500;
		error.timestamp = Instant.now().toString();
		error.error = "Internal Server Error";
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	}


@GetMapping("/api/v1/global/stats")
public ResponseEntity<?> getGlobalStats() {
	try {
		GlobalStats stats = new GlobalStats();
		// read the running totals accumulated across all transcription requests
		stats.inputTokens = tokenUsageTracker.getInputTokens();
		stats.outputTokens = tokenUsageTracker.getOutputTokens();
		return ResponseEntity.ok(stats);
	//capture run time failure 500
	} catch (Exception e){
		ErrorResponse error = new ErrorResponse();
		error.message = e.getMessage();
		error.path = "/api/v1/global/stats";
		error.status = 500;
		error.timestamp = Instant.now().toString();
		error.error = "Internal Server Error";
		//this is the full way of writing for all status code except 200 using OK.()
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
}


    
