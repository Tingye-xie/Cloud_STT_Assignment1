package comp3011.assignment1;
import java.time.Duration;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class AdminController {
	// StartTime is captured once when the system starts running, so the time is fixed
Instant StartTime = Instant.now();
	
@GetMapping("/api/v1/admin/uptime")
public UptimeResponse getUpTime() {
	UptimeResponse uptime = new UptimeResponse();
	//this is when the method is called
	uptime.utcServerStart = StartTime.toString();
	//get the current time
	uptime.utcNow = Instant.now().toString();
	//capture the UpTime using Duration which subtracts b -a
	uptime.serverUptimeSeconds = Duration.between(StartTime,Instant.now()).getSeconds();
	return uptime;
}


}
