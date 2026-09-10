const startButton = document.getElementById("startButton");
//find the id in html to determine the behaviour for that button
const stopButton = document.getElementById("stopButton");
const status = document.getElementById("status");
let microphone;
let recorder;
let audio_chucks  = [];


startButton.addEventListener("click", async() => {
	try{ //audio:true is asking for access option
		 microphone = await navigator.mediaDevices.getUserMedia({audio:true});
		 recorder = new MediaRecorder(microphone);
		 recorder.start();
		//change the default status to recording....
		status.textContent = "Recording";
		//disable start because prevent user pressing twice and activate the stopbutton
		startButton.disabled = true;
		stopButton.disabled = false;
		
		//reset the array here if want to save another audio.
		audio_chunks = [];
		
		recorder.addEventListener("dataavailable", (event) => {
			if(event.data.size > 0)
				{audio_chunks.push(event.data)}
		});
		
	} catch (error) {
		status.textContent = "Microphone access was not allowed.";
		    console.error("Microphone access failed:", error);}
	});

stopButton.addEventListener("click", async() => {
	
		if(!microphone)
			return;
		//stops the recording
		recorder.stop();
		//only disconnet the microphone
		microphone.getTracks().forEach((track) => track.stop());
		// Clears the variable.
		microphone = null;
		
		status.textContent = "Microphone stopped.";
		//after stop was pressed than enable start button to record again.
		stopButton.disabled = true;
        startButton.disabled = false;
		
	}
);

