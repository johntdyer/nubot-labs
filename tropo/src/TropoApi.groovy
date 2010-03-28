 /*
 * Copyright (c) 2002-2010 Nu Echo Inc.  All rights reserved. 
 */


/**
 * @author Nu Echo Inc.
 */
class TropoApi
{
    def static realtime = true;
    def static testCaseIndex = 0;
    def static testCases = [];
    
    def static sleep =
    {ms ->
        if (realtime)
        {
            Thread.sleep(ms)
        }
    }
    
    def static currentCall = [calledID:"n.a.", callerID:"n.a."];
    
    def static answer =
    { 
        timeoutInSeconds -> println "answer(${timeoutInSeconds}): ok"
    };
    
    def static await =
    {timeInMs -> 
        println "wait: ${timeInMs}ms"
        sleep(timeInMs);
    }
    
    def static say = 
    {text -> 
        println ("say: ${text}")
    }
    
    def static hangup = 
    { 
        println ("hangup: ok")
    }
    
    def static log =
    { 
        message -> println "log: ${message}"
    }
    
    def static ask = 
    {text, attributes ->
        println ("ask: ${text}, ${attributes}")
        
        if (testCases.size <= testCaseIndex)
        {
            println("No more test cases. Aborting.");
            return;
        }
        def result = testCases[testCaseIndex];
        println ("\t=>test case result: ${result}")
        testCaseIndex++;
        return result;
        
    }
    def static prompt = ask
    
    def static record =
    {text, attributes ->
        println ("record: ${text}, ${attributes}")
        def onRecordClosure = attributes.onRecord;
        if (onRecordClosure) onRecordClosure();
        if (testCases.size <= testCaseIndex)
        {
            println("No more test cases. Aborting.");
            return;
        }
        def result = testCases[testCaseIndex];
        println ("\t=>test case result: ${result}")
        testCaseIndex++;
        return result;
    }
    
}
