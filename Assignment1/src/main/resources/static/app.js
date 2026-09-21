const startButton = document.getElementById("startButton");
//find the id in html to determine the behaviour for that button
const stopButton = document.getElementById("stopButton");
const status = document.getElementById("status");
let transcription = document.getElementById("transcript");
let microphone;
let recorder;
let audio_chunks  = [];


startButton.addEventListener("click", async() => {
	try{ //audio:true is asking for access option
		 microphone = await navigator.mediaDevices.getUserMedia({audio:true});
		 recorder = new MediaRecorder(microphone);
		 recorder.start();
		//change the default status to recording....
		status.textContent = "Recording";
		//disable start to prevent user pressing start twice and activate the stopbutton.
		startButton.disabled = true;
		stopButton.disabled = false;
		
		//reset the array here if want to save another audio.
		audio_chunks = [];
		recorder.addEventListener("dataavailable", (event) => {
			if(event.data.size > 0)
				{
					audio_chunks.push(event.data)
				}
		});
		
	} catch (error) {
		status.textContent = "Microphone access was not allowed.";
		    console.error("Microphone access failed:", error);}
	});

stopButton.addEventListener("click", async() => {
		transcription.textContent = "Transcription in Progress";
		if(!microphone)
			return;
		//wrap the event-based recorder.stop() in a Promise so we can await it
		  const recordingStopped = new Promise((resolve) => {
		      recorder.addEventListener("stop", resolve);
		  });
		  recorder.stop();
		  await recordingStopped;
			//combine all chunks into one audion in webm type
			const audio = new Blob(audio_chunks,{type: "audio/webm"}); 
			
			
			//1. is to match the param name in controller, 2. the blob just created 3. create filename for the form that has the audio
			const formData = new FormData();
			formData.append("audio", audio,"recording.webm")
			
			try {
			       const response = await fetch("http://localhost:8080/api/v1/transcriptions", {
			           method: "POST",
			           body: formData
			       });
			       const data = await response.text(); //read the response body as plain text
			       transcription.textContent = data; //display the transcript
			   } catch (error) {
			       transcription.textContent = "Transcription failed."; //shows the error message if fetch failed
			       console.error(error);
			   }
		

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

