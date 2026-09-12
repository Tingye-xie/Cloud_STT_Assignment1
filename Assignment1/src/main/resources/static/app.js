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
	
		if(!microphone)
			return;
		//stop the recorder;
			recorder.stop();
		//wait for it to actually finishes than stop the record
		recorder.addEventListener("stop",(event)=>{
			//combine all chunks into one audion in webm type
			const audio = new Blob(audio_chunks,{type: "audio/webm"}); 
			//create aq fake url to access the audio
			const audioUrl = URL.createObjectURL(audio);
			
			
			//1. is to match the param name in controller, 2. the blob just created 3. the filename for the form
			const formData = new FormData();
			formData.append("audio", audio,"recording.webm")
			
			fetch("http://localhost:8080/api/v1/transcriptions", {
				method:"Post", // to match the correct method in controller
				body: formData}) // choose the speicific data to send
			//then happens after successfully requested
			.then(res => res.text()) //after data is fetched transfer the data into text
			.then(data => console.log(data)); //after transfer to text, display it in the console
			});
		

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

