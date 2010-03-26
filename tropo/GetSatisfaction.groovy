// --------------------------------------------
// using both speech and touch-tone input
// See http://www.tropo.com for more info
// --------------------------------------------

answer();

def baseAudioUrl = "http://github.com/pdeschen/nubot-labs/raw/master/audio";

say("${baseAudioUrl}/intro.wav");

say("Thank you for calling the Travel Agency Customer Satisfaction Department");

say("${baseAudioUrl}/dtmf/a.wav");

result=ask( "Did you purchase Package A, Package B, or Package C?", 
			[choices:"a( 1, package a), b( 2, package b), c(3, package c)"] );

if (result.name=='choice')
{
	if (result.value=="a") { say( "Overall, were you satisfied with the service you received while shopping for package A") }
	if (result.value=="b") { say( "Overall, were you satisfied with the service you received while shopping for package A") }
	if (result.value=="c") { say( "Overall, were you satisfied with the service you received while shopping for package A") }
}

hangup();

