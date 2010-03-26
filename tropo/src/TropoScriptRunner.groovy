/*
 * Copyright (c) 2002-2010 Nu Echo Inc.  All rights reserved. 
 */


/**
 * 
 * @author Nu Echo Inc.
 */
class TropoScriptRunner
{
    public static void main(String[] args)
    {
        
        if (!args || args.length != 2)
        {
            println("usage: ${TropoScriptRunner.class.name} [tropoGroovyScriptFile] [testCases]")
            return
        }
        def script = new File(args[0])
        def testScript = new File(args[1])
        def testCases = [];
        
        if (testScript.exists())
        {
            println "Loading test cases";
            GroovyShell shell = new GroovyShell(new Binding());
            testCases = shell.evaluate(testScript)
            println "Using ${testCases}";
        }
        def testCaseIndex = 0;
        
        //def testCases = [[value:"10"], [value:"0"]];
        
        def currentCall = [calledID:"n.a."];
        
        def answer =
                { println "answer: ok" };
        
        def await =
                {timeInMs -> 
                    println "wait: ${timeInMs}ms"
                    //Thread.sleep(timeInMs);
                }
        
        def say = 
                {text -> println ("say: ${text}") }
        
        def ask = 
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
        
        def hangup = 
                { println ("hangup: ok") }
        
        def log =
                { message -> println "log: ${message}" }
        
        if (script.exists())
        {
            println "Evaluating script $script";
            Binding binding = new Binding();
            binding.setVariable("currentCall", currentCall);
            binding.setVariable("answer", answer);
            binding.setVariable("await", await);
            binding.setVariable("say", say);
            binding.setVariable("ask", ask);
            binding.setVariable("hangup", hangup);
            binding.setVariable("log", log);
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(script)
        }
    }
}
