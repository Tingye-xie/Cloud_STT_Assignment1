package comp3011.assignment1;
import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	// StartTime is captured once when the system starts running, so the time is fixed
Instant StartTime = Instant.now();
//set initial status for the service status
boolean isShuttingDown = false;
	
@GetMapping("/api/v1/admin/uptime")
public ResponseEntity<?> getUpTime() {
	try {
	UptimeResponse uptime = new UptimeResponse();
	//this is when the method is called
	uptime.utcServerStart = StartTime.toString();
	//get the current time
	uptime.utcNow = Instant.now().toString();
	//capture the UpTime using Duration which subtracts b -a
	uptime.serverUptimeSeconds = Duration.between(StartTime,Instant.now()).getSeconds();
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
	if(!isShuttingDown) {
		// change it to shutdown so any repeat requests are treated as a conflict
	isShuttingDown = true;
	downTimeMessage.message = "Graceful shutdown requested.";
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


    
