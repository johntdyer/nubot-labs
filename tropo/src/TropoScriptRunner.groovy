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
        
        if (testScript.exists())
        {
            println "Loading test cases";
            GroovyShell shell = new GroovyShell(new Binding());
            TropoApi.testCases = shell.evaluate(testScript)
            println "Using ${TropoApi.testCases}";
        }
        if (script.exists())
        {
            println "Evaluating script $script";
            Binding binding = new Binding();
            binding.setVariable("currentCall", TropoApi.currentCall);
            binding.setVariable("answer", TropoApi.answer);
            binding.setVariable("await", TropoApi.await);
            binding.setVariable("say", TropoApi.say);
            binding.setVariable("ask", TropoApi.ask);
            binding.setVariable("prompt", TropoApi.prompt);
            binding.setVariable("hangup", TropoApi.hangup);
            binding.setVariable("log", TropoApi.log);
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(script)
        }
    }
}
