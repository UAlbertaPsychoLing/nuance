//*****************************************************************************
//
// BasicModeTest.java
//
// This is a test that tries to pull everything together, and get BasicMode
// up and running.
//
//*****************************************************************************
package testsuite;
public class BasicModeTest {
    public static void main(String args[]) {
	// make all of the classes we need
	agent.s_expression.SymbolicFactory factory = 
	    new agent.s_expression.SymbolicFactory();
	breeder.MedianPassBreeder breeder          = 
	    new breeder.MedianPassBreeder(factory);
	fitness.PctHitFitnessFunction fitness      = 
	    new fitness.PctHitFitnessFunction("TARGET");
	problem.VariableSetProblem problem         = 
	    new problem.VariableSetProblem(0.5);

	// set up our symbolic factory for use
	factory.addSymbol(new agent.s_expression.AddSymbol());
	factory.addSymbol(new agent.s_expression.MultiplySymbol());
	factory.addSymbol(new agent.s_expression.DivisionSymbol());
	factory.addVariable("A");
	factory.addVariable("B");
	factory.addVariable("C");

	// initialize our problem
	for(int i = 0; i < 1000; i++) {
	    // the problem is:
	    //   TARGET = A*B + A*B*C + A*C + C + 15
	    // where:
	    //   A = i + 1
	    //   B = 5
	    //   C = i
	    problem.VariableProblem prob = new problem.VariableProblem();
	    int a = i+1;
	    int b = 5;
	    int c = i;
	    prob.put("TARGET", new Double(a*b + a*b*c + a*c + c + 15));
	    prob.put("A",      new Double(a));
	    prob.put("B",      new Double(b));
	    prob.put("C",      new Double(c));
	    problem.add(prob);
	}

	logger.BasicLog log                        =
	    new logger.BasicLog(System.out, "tmplogfile", fitness, 
				problem.copy());

	// start up a BasicMode
	mode.Mode mode = new mode.BasicMode(breeder, fitness, problem, log, 
		1, 1000,
		new stopcondition.GenerationStopCondition(100));
	mode.start();
    }
}
