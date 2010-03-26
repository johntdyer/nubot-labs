def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";
def debugMode = true;

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

def debug =
        { message ->
            if (debugMode) log(message);
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
                        debug("handling 0");
                        result = ask("Zero. Now what?", [choices: '[DIGITS]']);
                        debug("post handling 0 response with " + result.value);;
                        responseHandler( result );
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
                        debug("handling 1");
                        ask("One. Now what?", [choices: '[DIGITS]', onChoice: responseHandler]);
                        //debug("handling response with " + result.value);
                        //responseHandler( result );
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
                        debug("handling 2");
                        ask("Two. Now what?", [choices: '[DIGITS]', onChoice: responseHandler]);
                        //responseHandler( result );
                    }; 
        }

def responseHandler = 
        { result ->
            debug("handling response with " + result.value);
            if (result.value == "0")
            {
                ok0(this);
            }
            else if (result.value == "1")
            {
                ok1(this);
            }
            else if (result.value == "2")
            {
                ok2(this);
            }
            else
            { 
                say("Sorry. Wrong number.")
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
                        debug("start test case");
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
    
    debug("handling test case" + result.value);
    
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

say("goodbye");
//stopCallRecording();
hangup();

