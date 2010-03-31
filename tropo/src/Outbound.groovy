
answer();

// Place a phone number here
def phoneNo = 5147657222

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
    //say()
    await(1000)
    result = newCall.ask("http://github.com/pdeschen/nubot-labs/raw/master/audio/dtmf/9.wav", [choices:"ok(for tomorrow the no parking regulation has been lifted)"])
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
