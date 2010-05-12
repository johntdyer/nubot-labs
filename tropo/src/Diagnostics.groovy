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
def noinputCount = 0;

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

def ok0 = { 
  await(2000);
  sequencer("b") { await(2000); };
  sequencer("c*2") {
    debug("handling 0");
    //                        say("Operator.");
    //                        await(2000);
    //                        hangup();
    responseHandler ( ask("Zero. Now what?", [choices: '[DIGITS]']));
  };
}

def ok1 = { 
  await(2000);
  sequencer("b") {await(2000) };
  sequencer("c12") {
    debug("handling 1");
    result = ask("One. Now what?", [choices: '[DIGITS]'])
    
    if ( result.name == 'timeout' ) {
      if (noinputCount <= 2) {
        noinputCount++
        ok1()
      }
      else {
        say("max no noinput")
      }
    }
    
    responseHandler (result);
  };
}

def ok2 = { 
  await(5000);
  sequencer("b") { await(4000) };
  sequencer("c13") {
    debug("handling 2");
    responseHandler (ask("Two. Now what?", [choices: '[DIGITS]']));
  };
}

def ok3 = { 
  await(10000);
  sequencer("b") {await(10000); };
  sequencer("c14") {
    debug("handling 3");
    responseHandler (ask("Three. Now what?", [choices: '[DIGITS]']));
  };
}

def ok4 = { 
  await(5000);
  sequencer("b") { await(2000); };
  sequencer("c15") {
    await(3000);
    debug("handling 4.1");
    // we don't care about the response here
    ask("Say something for recording", [choices: '[DIGITS]']);
    sequencer("b") {
      sequencer("c16") {
        debug("handling 4.1");
        result = ask("Four. Now what?", [choices: '[DIGITS]', onEvent: { event ->
          if (event.name=='badChoice') { say( "I'm sorry, I didn't udnerstand what you said.")
          }
          if (event.name=='timeout')   { say( "I'm sorry. I didn't hear anything.")
          }
        }
        ]);
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
    hangup();
  };
}
responseHandler = { result ->
  debug("handling response with " + result.value);
  
  noinputCount = 0
  
  switch (result.value) {
    case "0":ok0();break; 
    case "1":ok1();break; 
    case "2":ok2();break; 
    case "3":ok3();break; 
    case "4":ok4();break; 
    case "*":goodbye();break; 
    default:say("Sorry. Wrong number.");
  }
}


def init = {
  
  await(4000);
  sequencer("b") {await(1000); };
  sequencer("c11") {
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
  switch(result.value) {
    // normal
    case "10": 
      init();
      break; 
    // timeout
    case "11":
      ok2 =
      {
        await(3600000); responseHandler( ask("Two. Now what?", [choices: '[DIGITS]']));
      };
      init();
      break; 
    // unexpected-dtmf
    case "12": 
      ok3 =
      {
        await(10000);
        sequencer("b") { };
        await(10000);
        sequencer("c24") {
          debug("handling 3");
          result = ask("Three. Now what?", [choices: '[DIGITS]']);
          responseHandler (result);
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

