def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";

log("dnis: " + currentCall.calledID);

def sequencer =
        { sequence, closure ->
            for (dtmf in sequence)
            {
                say("${baseAudioUrl}/dtmf/${dtmf}.wav");
            }
            if (closure)
            {
                return closure()
            }
        }


answer();

await(2000);

//we need a ftp or http post to do so
//startCallRecording();

say("Welcome to diagnostic application!");

sequencer("c10") { 
    
    result = ask("Select your test case.", [choices: '[DIGITS]']) 
    switch (result.value)
    {
        case 10:
            say("Ok 10");
        default:
            say("sorry.");
    }
}

//stopCallRecording();
hangup();

