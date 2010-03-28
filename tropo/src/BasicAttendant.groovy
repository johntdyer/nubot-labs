def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";

def dtmfSequencerEnabled = true;
log("dnis: " + currentCall.calledID);

if (currentCall.calledID == "3473709997")
{ 
    dtmfSequencerEnabled = false;
}

def sequencer =
        { sequence, closure ->
            if (dtmfSequencerEnabled)
            {   
                for (dtmf in sequence)
                {
                    switch (dtmf)
                    {
                        case "*":say("${baseAudioUrl}/dtmf/star.wav");; 
                        case "#":say("${baseAudioUrl}/dtmf/pound.wav");
                        default:say("${baseAudioUrl}/dtmf/${dtmf}.wav");
                    }
                }
                
            }
            
            if (closure) return closure()
        }

def listNames(theContacts)
{
    def results = ''
    
    theContacts.each
            {
                if (results != '')
                { 
                    results = results + ", "
                }
                results += it.key
            }
    
    return results
}

def listOptions(theContacts)
{
    def results = ''
    
    theContacts.each
            {
                if (results != '')
                { 
                    results = results + ", "
                }
                results += it.key + " (" + it.value.nameChoices + ")"
            }
    
    return results;
}

def contacts = [
        "jonathan": [ nameChoices: "Jonathan, Jonathan Taylor", number: "14074434233" ],
        "michael" : [ nameChoices: "Michael, Michael Smith", number: "14074181800" ],
        "stephen" : [ nameChoices: "Stephen, Stephen Neish", number: "14076463131" ] ]


answer( 30 )
sequencer ("C10")
        { say( "Hello, and thank you for calling." ) }
def text = "Who would you like to call? Just say " + listNames( contacts )

def event;

sequencer ("C20")
        {
            event = prompt(text, [
            repeat:3, 
            timeout:7, 
            choices:listOptions( contacts ), 
            onEvent:
            { handlingEvent->
                handlingEvent.onTimeout(
                {
                    sequencer ("C21")
                    {say( "I'm sorry, I didn't hear anything." ) } } )
                handlingEvent.onBadChoice(
                        {
                            sequencer ("C22")
                            {say( "I'm sorry, I didn't understand what you said." ) } } )
            }
            ]
            )
        }
// If a choice was made, transfer to that person
if (event.name == "choice") {
    sequencer ("C30")
            { say( "Ok, you said " + event.value + ". Please hold while I transfer you." ) };
    
    def ne = transfer( "sip:9${contacts[ event.value ].number}@10.6.63.201", [
            answerOnMedia: false,
            callerId:      "tel:+14076179024",
            timeout:       60.3456,
            method:        "bridged", // fixed to bridged currently
            playrepeat:    3,
            playvalue:     "Ring... Ring... Ring...",
            choices:       "1,2,3,4,5,6,7,8,9,0,*,#",
            onSuccess:     { ne-> log("*********transfered to $event.value.calleeId") },  
            onError:
            { ne-> log("*********transfer error") },  
            onTimeout:
            { ne-> log("*********transfer timeout") },  
            onCallFailure:
            { ne-> log("*********transfer failed") },  
            onChoice:
            { ne-> log("*********transfer canceled") }  
            ] )
    
    log "transfer event.name  = $ne.name"
    log "transfer event.value = $ne.value"
    
    sequencer ("C90")
            { say "The other party disconnected.  Goodbye" } 
}

hangup()

