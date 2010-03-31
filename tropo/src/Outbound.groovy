
answer();

// Place a phone number here
def phoneNo = 15142220264

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
    newCall.say("This is a Tropo call, thank you for answering!")
    // Error in debugger if event.value.calleeId is used
    log("Outgoing call gets answered by ");
    
    newCall.hangup()
}
