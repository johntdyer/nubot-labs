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

def handleResponse = 
        { result ->
            switch (result.value)
            {
                case "0":
                ok0();
                case "1":
                ok1();
                case "2":
                ok2();
                default:
                say("Sorry.");
            }                
        }

def ok0 =
        {
            await(2000);
            sequencer("b")
                    {};
            await(2000);
            sequencer("c*1")
                    { handleResponse( ask("Zero. Now what?", [choices: '[DIGITS]']) ) }; 
        }

def ok1 =
        {
            await(2000);
            sequencer("b")
                    {};
            await(2000);
            sequencer("c12")
                    { handleResponse( ask("One. Now what?", [choices: '[DIGITS]'])) }; 
        }

def ok2 =
        {
            await(5000);
            sequencer("b")
                    {};
            await(4000);
            sequencer("c13")
                    { handleResponse( ask("Two. Now what?", [choices: '[DIGITS]'])) }; 
        }



def ask01234 =
        {
            
            await(4000);
            sequencer("b")
                    {};
            await(1000);
            sequencer("c11")
                    {
                        handleResponse( ask("0, 1, 2, 3, or 4?", [choices: '[DIGITS]']))
                    }; 
            
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
        case "11":
            ok2 =
            { await(3600000);}
            ask01234();
        default:
            say("sorry.");
    }
}

//stopCallRecording();
hangup();

