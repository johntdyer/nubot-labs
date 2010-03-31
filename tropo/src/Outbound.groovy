
answer();

// Place a phone number here
def phoneNo = 5147657222
//def phoneNo = 5142220264

event = call("sip:${phoneNo}@10.6.63.201", 
        [
        answerOnMedia: false,
        callerID:      "tel:+666666666666",
        timeout:        15,
        // Error in debugger if event.value.calleeId is used
        onAnswer:       { event-> log("******************** Answered from ") },
        onError:
        { log("******************** oops , error *********************") },
        onTimeout:
        { log("******************** timeout *********************") },
        onCallFailure:
        { log("******************** call failed *********************") }
        ] )

if(event.name=='answer')
{
    newCall = event.value;
    await(5000)
    say("http://github.com/pdeschen/nubot-labs/raw/master/audio/dtmf/9.wav")
    //await()
    result = newCall.ask("ok", [choices:"ok(for tomorrow the no parking regulation has been lifted)"])
    
    //log(result.choice.xml);
    //log(result.choice.confidence);
    //log(result.choice.interpretation);
    //log(result.choice.utterance);
    
    if (result.name == 'choice' && result.value=="ok")
    {
        log("Lifted");
    }
    else
    {
        log("NOT Lifted");
    }
    
    // Error in debugger if event.value.calleeId is used
    log("Outgoing call gets answered by ");
    
    newCall.hangup()
}
