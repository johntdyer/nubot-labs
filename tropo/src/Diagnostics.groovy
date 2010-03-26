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

def ask01234 =
        {
            
            await(4000);
            sequencer("b")
                    {};
            await(1000);
            sequencer("c11")
                    {
                        result = ask("0, 1, 2, 3, or 4?", [choices: '[DIGITS]'])
                        
                        switch (result.value)
                        {
                            case "0":
                                ok0();
                            default:
                                say("Sorry.");
                        }                
                        
                    }; 
            
        }

def ok0 =
        {
            await(2000);
            sequencer("b")
                    {};
            await(2000);
            sequencer("c*1")
                    { result = ask("Zero. Now what?", [choices: '[DIGITS]']) }; 
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
        case "10":
            ask01234();
        default:
            say("sorry.");
    }
}

//stopCallRecording();
hangup();

