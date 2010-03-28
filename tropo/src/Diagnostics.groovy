/*
 * Copyright (c) 2002-2010 Nu Echo Inc.  All rights reserved. 
 */


/**
 * 
 * @author Nu Echo Inc.
 */

def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";
def debugMode = true;

log("dnis: " + currentCall.calledID);

def sequencer =
        { sequence, closure ->
            for (dtmf in sequence)
            {
                switch (dtmf)
                {
                    case "*":say("${baseAudioUrl}/dtmf/star.wav");; 
                    case "#":say("${baseAudioUrl}/dtmf/pound.wav");
                    default:say("${baseAudioUrl}/dtmf/${dtmf.toLowerCase()}.wav");
                }
            }
            if (closure) return closure()
        }

def debug =
        { message ->
            if (debugMode) log(message);
        }
def responseHandler =
        {
            /* dynamic def*/
        };

def ok0 =
        { 
            await(2000);
            sequencer("b")
                    { await(2000); };
            sequencer("c*1")
                    {
                        debug("handling 0");
                        responseHandler ( ask("Zero. Now what?", [choices: '[DIGITS]']));
                    }; 
        }

def ok1 =
        { 
            await(2000);
            sequencer("b")
                    {await(2000) };
            sequencer("c12")
                    {
                        debug("handling 1");
                        responseHandler (ask("One. Now what?", [choices: '[DIGITS]']));
                    }; 
        }

def ok2 =
        { 
            await(5000);
            sequencer("b")
                    { await(4000) };
            sequencer("c13")
                    {
                        debug("handling 2");
                        responseHandler (ask("Two. Now what?", [choices: '[DIGITS]']));
                    }; 
        }

def ok3 =
        { 
            await(10000);
            sequencer("b")
                    {await(10000); };
            sequencer("c14")
                    {
                        debug("handling 3");
                        responseHandler (ask("Three. Now what?", [choices: '[DIGITS]']));
                    }; 
        }

def ok4 =
        { 
            await(5000);
            sequencer("b")
                    {await(2000); };
            sequencer("c15")
                    {
                        await(3000);
                        debug("handling 4.1");
                        // we don't care about the response here
                        ask("Say something for recording", [choices: '[DIGITS]']);
                        sequencer("b")
                                {
                                    
                                    sequencer("c16")
                                    {
                                        debug("handling 4.1");
                                        result = ask("Four. Now what?", [choices: '[DIGITS]']);
                                        responseHandler (result);
                                    }; 
                                };
                        
                    }; 
        }

def goodbye =
        {
            await(1000);
            sequencer("b")
                    {await(1000); };
            sequencer("c19")
                    {
                        debug("goodbye");
                        say("Goodbye!");
                        await(1000);
                        hangup();
                    }; 
        }
responseHandler = 
        { result ->
            debug("handling response with " + result.value);
            switch (result.value)
            {
                case "0":ok0(); 
                case "1":ok1();
                case "2":ok2();
                case "3":ok3();
                case "4":ok4();
                case "*":goodbye();
                default:say("Sorry. Wrong number.");
            }
        }


def init =
        {
            
            await(4000);
            sequencer("b")
                    {await(1000); };
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
    switch(result.value)
    {
        // normal
        case "10": init();
        // timeout
        case "11":
            ok2 =
            {
                await(3600000); responseHandler( ask("Two. Now what?", [choices: '[DIGITS]']));
            };
            init();
        // unexpected-dtmf
        case "12": 
            ok3 =
            {
                await(10000);
                sequencer("b")
                        {
                        };
                await(10000);
                sequencer("c24")
                        {
                            debug("handling 3");
                            result = ask("Three. Now what?", [choices: '[DIGITS]']);
                            responseHandler (result);
                        }; 
            }; 
            init();
        // system failure
        case "13": 
            ok2 =
            {
                await(5000);
                sequencer("b")
                        {await(4000) };
                sequencer("c*9")
                        {
                            debug("handling 2");
                            await(5000);
                            hangup();
                        };    
            };
            init();
        // application hangup
        case "14": 
            ok3 = {hangup(); }
            init();
        // max no match
        case "15": 
            ok2 = { 
                await(5000);
                sequencer("b")
                        { await(4000) };
                sequencer("c*0")
                        {
                            debug("handling 2");
                            await(5000);
                            hangup();
                        }; 
            }
            init();
        // no data to validate
        case "16": 
            ok2 = { 
                await(5000);
                sequencer("b")
                        { await(4000) };
                sequencer("c*7")
                        {
                            debug("handling 2");
                            await(5000);
                            hangup();
                        }; 
            }
            init();
        // dialog failure
        case "17": 
            ok2 = { 
                await(5000);
                sequencer("b")
                        { await(4000) };
                sequencer("c*8")
                        {
                            debug("handling 2");
                            await(5000);
                            hangup();
                        }; 
            }
            init();
        // ?
        case "18": 
            init();
        default: say("Sorry wrong test case.");
    }
}
say("goodbye");
//stopCallRecording();
hangup();

