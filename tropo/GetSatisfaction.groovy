// --------------------------------------------
// using both speech and touch-tone input
// See http://www.tropo.com for more info
// --------------------------------------------

def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";
def dtmfSequencerEnabled = true;

log("dnis: " + currentCall.calledID);
log("dnis: " + currentCall.calleeID);

def sequencer = { sequence, closure ->

	if (dtmfSequencerEnabled)
	{	
		for (dtmf in sequence) {
			say("${baseAudioUrl}/dtmf/${dtmf}.wav");
		}
	}
	if (closure) {return closure() }
}


answer();

say("${baseAudioUrl}/intro.wav");

sequencer("a") { say("Hello 1. Thank you for calling the Travel Agency Customer Satisfaction Department") };

result=ask( "Did you purchase Package A, Package B, or Package C?", 
			[choices:"a( 1, package a), b( 2, package b), c(3, package c)"] );

if (result.name=='choice')
{
	if (result.value=="a") { 
		sequencer("c20") { say( "Overall, were you satisfied with the service you received while shopping for package A" ) }
	}
	if (result.value=="b") { sequencer("c30") { say( "Overall, were you satisfied with the service you received while shopping for package B") } }
	if (result.value=="c") { sequencer("c40") { say( "Overall, were you satisfied with the service you received while shopping for package C" ) } }
}

hangup();

