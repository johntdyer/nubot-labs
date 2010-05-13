/*
 * Copyright (c) 2002-2010 Nu Echo Inc.  All rights reserved. 
 */

/**
 * 
 * @author Nu Echo Inc.
 */

def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";
def debugMode = true;
def timing = true;
def noInputCount = 0;
def noMatchCount = 0;
def askAttributes = [timeout: 7, choices: '[DIGITS]']

log("dnis: " + currentCall.calledID);



def sequencer = { sequence, closure ->
  println ("Sequencing with: ${sequence}");
  for (dtmf in sequence) {
    switch (dtmf) {
      case "*":say("${baseAudioUrl}/dtmf/star.wav");break; 
      case "#":say("${baseAudioUrl}/dtmf/pound.wav");break;
      case "b":
        if (!timing) break
      default:say("${baseAudioUrl}/dtmf/${dtmf.toLowerCase()}.wav");
    }
  }
  if (closure) return closure()
}

def debug = { message ->
  if (debugMode) log(message);
}
def responseHandler = {
  /* dynamic def*/
};
def ok0 = {}

ok0 = { 
  await(2000);
  sequencer("b") { await(2000); };
  sequencer("c*2") {
    debug("handling 0");
    //                        say("Operator.");
    //                        await(2000);
    //                        hangup();
    responseHandler ( ask("Zero. Now what?", askAttributes), ok0);
  };
}
def ok1 = {}
ok1 = { 
  await(2000);
  sequencer("b") {await(2000) };
  sequencer("c12") {
    debug("handling 1");
    result = ask("One. Now what?", askAttributes)
    responseHandler(result, ok1);
  };
}
def ok2 = {}
ok2 = { 
  await(5000);
  sequencer("b") { await(4000) };
  sequencer("c13") {
    debug("handling 2");
    responseHandler (ask("Two. Now what?", askAttributes), this);
  };
}

def ok3 = {}
ok3 = { 
  await(10000);
  sequencer("b") {await(10000); };
  sequencer("c14") {
    debug("handling 3");
    responseHandler (ask("Three. Now what?", askAttributes));
  };
}
def ok4 = {}
ok4 = { 
  await(5000);
  sequencer("b") { await(2000); };
  sequencer("c15") {
    await(3000);
    debug("handling 4.1");
    // we don't care about the response here
    ask("Say something for recording", askAttributes);
    sequencer("b") {
      sequencer("c16") {
        debug("handling 4.1");
        result = ask("Four. Now what?", askAttributes);
        responseHandler (result);
      };
    };
  };
}

def goodbye = {
  await(2000);
  sequencer("b") {await(2000); };
  sequencer("c19") {
    debug("goodbye");
    say("Bye!");
    await(10000);
    hangup();
  };
}

def maxnoinput = {
  await(2000);
  sequencer("b") {await(2000); };
  sequencer("c*1") {
    debug("max no input");
    say("Max No Input. Bye!");
    hangup();
  };
}
def maxnomatch = {
  await(2000);
  sequencer("b") {await(2000); };
  sequencer("c*0") {
    debug("max no match");
    say("Max No match. Bye!");
    hangup();
  };
  
}
responseHandler = { result, state ->
  debug("handling response with " + result.value);
  
  if (result.name=='badChoice') {
    noMatchCount++
    if (noMatchCount >=3) {
      maxnomatch();        
    }
    say( "no match.")
    // reprompt...go back to state
    if (state) state()
  }
  else if (result.name=='timeout')   { 
    noInputCount++
    if (noInputCount >=3) {
      maxnoinput();        
    }
    say( "no input.")
    // reprompt...go back to state
    if (state) state()
  }
  // otherwise
  noInputCount = 0;
  noMatchCount = 0;
  
  switch (result.value) {
    case "0":ok0();break; 
    case "1":ok1();break; 
    case "2":ok2();break; 
    case "3":ok3();break; 
    case "4":ok4();break; 
    case "*":goodbye();break; 
    default:
      say("Error");
  }
}

def init = {}
init = {
  
  await(4000);
  sequencer("b") {await(1000); };
  sequencer("c11") {
    debug("start test case");
    responseHandler( ask("0, 1, 2, 3, or 4?", askAttributes), init)
  };
}

answer();

await(2000);

//we need a ftp or http post to do so
//startCallRecording();

say("Welcome to diagnostic application!");

sequencer("c10") { 
  
  result = ask("Select your test case.", askAttributes) 
  
  debug("handling test case" + result.value);
  switch(result.value) {
    // normal
    case "10": 
      init();
      break; 
    // timeout
    case "11":
      ok2 =
      {
        await(3600000); responseHandler( ask("Two. Now what?", askAttributes), ok2);
      };
      init();
      break; 
    // unexpected-dtmf
    case "12": 
      ok3 =
      {
        await(10000);
        sequencer("b") {
        };
        await(10000);
        sequencer("c24") {
          debug("handling 3");
          result = ask("Three. Now what?", askAttributes);
          responseHandler (result, ok3);
        };
      }; 
      init();
      break; 
    // system failure
    case "13": 
      ok2 =
      {
        await(5000);
        sequencer("b") {await(4000) };
        sequencer("c*9") {
          debug("handling 2");
          await(5000);
          hangup();
        };
      };
      init();
      break; 
    // application hangup
    case "14": 
      ok2 = {hangup(); }
      init();
      break; 
    // max no match
    case "15": 
      ok2 = { 
        await(5000);
        sequencer("b") { await(4000) };
        sequencer("c*0") {
          debug("handling 2");
          await(5000);
          hangup();
        };
      }
      init();
    //break; 
    // no data to validate
    case "16": 
      ok2 = { 
        await(5000);
        sequencer("b") { await(4000) };
        sequencer("c*7") {
          debug("handling 2");
          await(5000);
          hangup();
        };
      }
      init();
      break; 
    // dialog failure
    case "17": 
      ok2 = { 
        await(5000);
        sequencer("b") { await(4000) };
        sequencer("c*8") {
          debug("handling 2");
          await(5000);
          hangup();
        };
      }
      init();
      break; 
    // ?
    case "18": 
      timing = false;
      init();
      break;
    default: say("Sorry wrong test case.");
  }
}
say("goodbye");
//stopCallRecording();
hangup();

