//*****************************************************************************
//
// SymbolicTest.java
//
// This is a small testsuite for ensuring that s-expressions, SymbolicAgents,
// and SymbolicFactories are all working properly
//
//*****************************************************************************
package testsuite;
import  agent.s_expression.*;
public class SymbolicTest {
    public static void main(String args[]) {
 	// build up our SymbolicFactory
 	SymbolicFactory factory = new SymbolicFactory();
 	factory.addVariable("ON");
 	factory.addVariable("OFREQ");
 	factory.addSymbol(new AddSymbol());

 	// generate a couple random Agents
 	agent.Agent mama = factory.create();
 	agent.Agent papa = factory.create();

 	// print them both out
 	System.out.println(mama);
 	System.out.println(papa);

 	// make a baby and print it
 	agent.Agent baby = factory.mate(mama, papa);
 	System.out.println(baby);
    }
}
