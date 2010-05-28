// --------------------------------------------
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0' )
import groovyx.net.http.* 

// using both speech and touch-tone input
// See http://www.tropo.com for more info
// --------------------------------------------

def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";

// this should be reverted but I wanna have dtmf over skype for now
// so we are desabling the sequencer only for 6172977902 dnis
def dtmfSequencerEnabled = true;

log("dnis: " + currentCall.calledID);

if (currentCall.calledID == "6172977902") { dtmfSequencerEnabled = false;
}

def sequencer = { sequence, closure ->
  
  if (dtmfSequencerEnabled) {	
    for (dtmf in sequence) {
      say("${baseAudioUrl}/dtmf/${dtmf}.wav");
    }
  }
  if (closure) {return closure()
  }
}


answer();

say("${baseAudioUrl}/intro.wav");

sequencer("a") { say("Hello 1. Thank you for calling the Travel Agency Customer Satisfaction Department") };

sequencer("c10") {
  result=ask( "Did you purchase Package A, Package B, or Package C?", 
  [choices:"a( 1, package a), b( 2, package b), c(3, package c)"] )
};

if (result.name=='choice') {
  if (result.value=="a") { 
    sequencer("c20") { 
      result = ask( "Overall, were you satisfied with the service you received while shopping for package A" , [choices: '[BOOLEAN]']) 
      if (result.value) {
        sequencer("c92") { say ('''
					Thank you for your feedback. We are so pleased to provide you with 
					satisfactory service today. We hope we will be a part of all your future 
					travel booking plans! Goodbye.''')}
      }
      else {
        sequencer("c90") { say ('''
					Your opinion is very important to us. Let me transfer you to an agent who 
					will try to resolve any issues you had during the booking process. Please hold...
					''')}
      }
    }
  }
  if (result.value=="b") { sequencer("c30") { say( "Overall, were you satisfied with the service you received while shopping for package B")
    } }
  if (result.value=="c") { sequencer("c40") { say( "Overall, were you satisfied with the service you received while shopping for package C" )
    } }
}

hangup();

