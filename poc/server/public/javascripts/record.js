function __log(e, data) {
    log.innerHTML += "\n" + e + " " + (data || '');
  }

var audio_context;
var recorder;
var wavesurfer = Object.create(WaveSurfer);
//var original_buffer;
//var new_buffer

function startUserMedia(stream) {
    
    //Create virtual input from empty amplification factor object
    var virtualInput        = this.audio_context.createGain();
       
    //Assign stream source to html5 audio context object
    var microphone         = this.audio_context.createMediaStreamSource(stream);
        
    //Connect media source output to virtualInput input
    //So, virtualInput now equals microphone input
    microphone.connect(virtualInput);

    //Set audio quality and assign it to virtualInput
    var analyserNode        = this.audio_context.createAnalyser();
    analyserNode.fftSize    = 2048;
    virtualInput.connect( analyserNode );

    //Set the stream to RecorderJs from Matt Diamond
    recorder = new Recorder(virtualInput);
    __log('Recorder initialised.');

    //Set volume to zero
    var amplificationFactor = this.audio_context.createGain();
    amplificationFactor.gain.value     = 0.0;
        
    //We set volume to zero to output, so we cancel echo
    amplificationFactor.connect( this.audio_context.destination );  
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
        var url = URL.createObjectURL(blob);
        var li = document.createElement('li');
        var au = document.createElement('audio');
        var hf = document.createElement('a');

        wavesurfer.init({
            AudioContext: audio_context,
            container: document.querySelector('#wave'),
            backend: 'WebAudio',
            waveColor: 'violet',
            progressColor: 'purple'
        });

        wavesurfer.on('ready', function () {
            wavesurfer.play();
            
        alert(getMethods(window.AudioBuffer));
        });
        

        wavesurfer.load(url);
        
        
        
            /* Regions */
        wavesurfer.enableDragSelection({
            color: 'rgba(0,100,255,0.3)',
            resize: true
        });

        /*wavesurfer.on('ready', function () {
            if (localStorage.regions) {
                loadRegions(JSON.parse(localStorage.regions));
            } else {
                wavesurfer.util.ajax({
                    responseType: 'json',
                    url: 'annotations.json'
                }).on('success', function (data) {
                    loadRegions(data);
                    saveRegions();
                });
            }
        });*/
        /**
 * Save annotations to localStorage.
 */
function saveRegions() {
    localStorage.regions = JSON.stringify(
        Object.keys(wavesurfer.regions.list).map(function (id) {
            var region = wavesurfer.regions.list[id];
            return {
                start: region.start,
                end: region.end,
                data: region.data
            };
        })
    );
}


/**
 * Load regions from localStorage.
 */
function loadRegions(regions) {
    regions.forEach(function (region) {
        region.color = randomColor(0.1);
        wavesurfer.addRegion(region);
    });
}


/**
 * Detect regions separated by silence.
 */
function detectRegions() {
    // Silence params
    var minValue = 0.0015;
    var minSeconds = 0.25;

    var peaks = wavesurfer.backend.peaks;
    var length = peaks.length;
    var duration = wavesurfer.getDuration();
    var coef = duration / length;
    var minLen = (minSeconds / duration) * length;

    var regions = [];
    var i = 0;
    var start;
    var extend;
    while (i < length) {
        if (peaks[i] < minValue) {
            i += 1;
        } else {
            start = i;
            do {
                while (peaks[i] >= minValue) {
                    i += 1;
                }
                if (i - start < minLen) {
                    i += 1;
                } else {
                    var j = i;
                    while (peaks[j] < minValue) {
                        j += 1;
                    }
                    if (j - i < minLen) {
                        i = j;
                        extend = true;
                    } else {
                        regions.push({
                            start: Math.round(start * coef * 10) / 10,
                            end: Math.round(i * coef * 10) / 10
                        });
                        i += 1;
                        extend = false;
                    }
                }
            } while (extend)
        }
    }
    return regions;
}
        wavesurfer.on('region-click', function (region, e) {
            e.stopPropagation();
            // Play on click, loop on shift click
            e.shiftKey ? region.playLoop() : region.play();
        });
        wavesurfer.on('region-click', editAnnotation);
        wavesurfer.on('region-updated', saveRegions);
        wavesurfer.on('region-removed', saveRegions);

        wavesurfer.on('region-play', function (region) {
            region.once('out', function () {
                wavesurfer.play(region.start);
                wavesurfer.pause();
            });
        });
        
        /**
     * Edit annotation for a region.
     */
    function editAnnotation (region) {
        var form = document.forms.edit;
        form.style.opacity = 1;
        form.elements.start.value = Math.round(region.start * 10) / 10,
        form.elements.end.value = Math.round(region.end * 10) / 10;
        form.elements.note.value = region.data.note || '';
        form.onsubmit = function (e) {
            e.preventDefault();
            region.update({
                start: form.elements.start.value,
                end: form.elements.end.value,
                data: {
                    note: form.elements.note.value
                }
            });
            
            cut(wavesurfer, region);
            //form.style.opacity = 0;
        };
        form.onreset = function () {
            form.style.opacity = 0;
            form.dataset.region = null;
        };
        form.dataset.region = region.id;
    }


        au.controls = true;
        au.src = url;
        hf.href = url;
        hf.download = new Date().toISOString() + '.wav';
        hf.innerHTML = hf.download;
        li.appendChild(au);
        li.appendChild(hf);
        document.getElementById("recordingslist").appendChild(li);
        __log('appendChild');
    });
}

function wavePlay() {
    wavesurfer.playPause();
}

function cut(instance, region){
  var startPosition;
    var endPosition;
    var selection = {
        startPosition : region.start,
        endPosition : region.end
    }
  //var selection = instance.getSelection();
  if(selection){
    var original_buffer = instance.backend.buffer;
      __log(original_buffer.numberOfChannels);
    var new_buffer      = instance.backend.ac.createBuffer(original_buffer.numberOfChannels, original_buffer.length, original_buffer.sampleRate);
      __log(original_buffer);
    var first_list_index        = (selection.startPosition * original_buffer.sampleRate);
    var second_list_index       = (selection.endPosition * original_buffer.sampleRate);
    var second_list_mem_alloc   = (original_buffer.length - (selection.endPosition * original_buffer.sampleRate));

    var new_list        = new Float32Array( parseInt( first_list_index ));
    var second_list     = new Float32Array( parseInt( second_list_mem_alloc ));
    var combined        = new Float32Array( original_buffer.length );
    new_list = original_buffer.getChannelData(0);
    //original_buffer.copyFromChannel(new_list, 0);
    original_buffer.copyFromChannel(second_list, 0, second_list_index)

    combined.set(new_list)
    combined.set(second_list, first_list_index)

    new_buffer.copyToChannel(combined, 0);

    instance.loadDecodedBuffer(new_buffer);
  }else{
    __log('did not find selection');
  }
}
    
function getMethods(obj) {
  var result = [];
  for (var id in obj) {
    try {
      if (typeof(obj[id]) == "function") {
        result.push(id + ": " + obj[id].toString());
      }
    } catch (err) {
      result.push(id + ": inaccessible");
    }
  }
  return result;
}

window.onload = function init() {
    try {
        // webkit shim
        window.AudioContext = window.AudioContext || window.webkitAudioContext;
        navigator.getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia);
        window.URL = window.URL || window.webkitURL;
      
        audio_context = new AudioContext;
        __log('Audio context set up.');
        __log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
    } catch (e) {
        alert('No web audio support in this browser!');
    }
    
    
    navigator.getUserMedia({audio: true}, startUserMedia, function(e) {
      __log('No live audio input: ' + e);
    });
  };