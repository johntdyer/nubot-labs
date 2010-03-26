def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";

log("dnis: " + currentCall.calledID);

def sequencer =
        { sequence, closure ->
            for (dtmf in sequence)
            {
                if (dtmf == "*")
                    say("${baseAudioUrl}/dtmf/star.wav");
                else if (dtmf == "#")
                    say("${baseAudioUrl}/dtmf/pound.wav");
                else
                    say("${baseAudioUrl}/dtmf/${dtmf}.wav");
            }
            if (closure)
            {
                return closure()
            }
        }



def ok0 =
        { responseHandler ->
            await(2000);
            sequencer("b")
                    {
                    };
            await(2000);
            sequencer("c*1")
                    {
                        responseHandler( ask("Zero. Now what?", [choices: '[DIGITS]']) )
                    }; 
        }

def ok1 =
        { responseHandler ->
            await(2000);
            sequencer("b")
                    {
                    };
            await(2000);
            sequencer("c12")
                    {
                        responseHandler( ask("One. Now what?", [choices: '[DIGITS]']))
                    }; 
        }

def ok2 =
        { responseHandler ->
            await(5000);
            sequencer("b")
                    {
                    };
            await(4000);
            sequencer("c13")
                    {
                        responseHandler( ask("Two. Now what?", [choices: '[DIGITS]']))
                    }; 
        }

def responseHandler = 
        { result ->
            if (result.value == "0")
            {ok0()
                {delegate };
            }
            else if (result.value == "1")
            {ok1()
                {delegate };
            }
            else if (result.value == "2")
            {ok2()
                {delegate };
            }
            else
            { say("Sorry. Wrong number.")
            }
        }


def ask01234 =
        {
            
            await(4000);
            sequencer("b")
                    {
                    };
            await(1000);
            sequencer("c11")
                    {
                        responseHandler( ask("0, 1, 2, 3, or 4?", [choices: '[DIGITS]']))
                    }; 
            
        }

answer();

await(2000);

//we need a ftp or http post to do so
//startCallRecording();

say("Welcome to diagnostic application!");

sequencer("c10") { 
    
    result = ask("Select your test case.", [choices: '[DIGITS]']) 
    if (result.value == "10")
    {
        ask01234();
    }
    else if (result.value == "11")
    {
        ok2 =
                { await(3600000); };
        ask01234();
    }
    else
    {
        say("Sorry wrong test case.");
    }
    
}

//stopCallRecording();
hangup();

