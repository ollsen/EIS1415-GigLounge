function __log(e, data) {
    log.innerHTML += "\n" + e + " " + (data || '');
  }

var audio_context;
var recorder;

function startUserMedia(stream) {
    
    //Create virtual input from empty amplification factor object
    var virtualInput        = audio_context.createGain();
       
    //Assign stream source to html5 audio context object
    var microphone         = audio_context.createMediaStreamSource(stream);
        
    //Connect media source output to virtualInput input
    //So, virtualInput now equals microphone input
    microphone.connect(virtualInput);

    //Set audio quality and assign it to virtualInput
    var analyserNode        = audio_context.createAnalyser();
    analyserNode.fftSize    = 2048;
    virtualInput.connect( analyserNode );

    //Set the stream to RecorderJs from Matt Diamond
    recorder = new Recorder(virtualInput);
    __log('Recorder initialised.');

    //Set volume to zero
    var amplificationFactor = audio_context.createGain();
    amplificationFactor.gain.value     = 0.0;
        
    //We set volume to zero to output, so we cancel echo
    amplificationFactor.connect( audio_context.destination );  
}

function startRecording(button) {
    recorder && recorder.record();
    button.disabled = true;
    button.nextElementSibling.disabled = false;
    __log('Recording...');
}

function stopRecording(button) {
    recorder && recorder.stop();
    button.disabled = true;
    button.previousElementSibling.disabled = false;
    __log('Stopped recording.');
      
    createDownloadLink();
    
    recorder.clear();
}

function createDownloadLink() {
    recorder && recorder.exportWAV(function(blob) {
        /*var url = URL.createObjectURL(blob);
        var li = document.createElement('li');
        var au = document.createElement('audio');
        var hf = document.createElement('a');
        var btn = document.createElement('Button')

        au.controls = true;
        au.src = url;
        hf.href = url;
        hf.download = new Date().toISOString() + '.wav';
        hf.innerHTML = hf.download;
        li.appendChild(au);
        li.appendChild(hf);
        document.getElementById("recordingslist").appendChild(li);*/
    });
}


window.onload = function init() {
    try {
        // webkit shim
        window.AudioContext = window.AudioContext || window.webkitAudioContext;
        navigator.getUserMedia = (navigator.getUserMedia ||
                                  navigator.webkitGetUserMedia ||
                                  navigator.mozGetUserMedia ||
                                  navigator.msGetUserMedia);
        window.URL = window.URL || window.webkitURL;
      
        audio_context = new AudioContext;
        __log('Audio context set up.'+ audio_context);
        __log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
    } catch (e) {
        alert('No web audio support in this browser!');
    }
    
    
    navigator.getUserMedia({audio: true}, startUserMedia, function(e) {
      __log('No live audio input: ' + e);
    });
  };