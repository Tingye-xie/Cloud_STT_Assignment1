const startButton = document.getElementById("startButton");
//find the id in html to determine the behaviour for that button
const stopButton = document.getElementById("stopButton");
const status = document.getElementById("status");
let microphone = null;


startButton.addEventListener("click", async() => {
	try{ //audio:true is asking for access option
		 microphone = await navigator.mediaDevices.getUserMedia({audio:true});
		//change the default status to recording....
		status.textContent = "Microphone Access granted";
		//disable start because prevent user pressing twice
		startButton.disabled = true;
	
		stopButton.disabled = false;
		
	} catch (error) {
		status.textContent = "Microphone access was not allowed.";
		    console.error("Microphone access failed:", error);}
	});

stopButton.addEventListener("click", async() => {
	
		if(!microphone)
			return;
		
		microphone.getTracks().forEach((track) => track.stop());
		microphone = null;
		
		status.textContent = "Microphone stopped.";
		//set stop button to false to allow stopping
		stopButton.disabled = true;
		
	}
);

