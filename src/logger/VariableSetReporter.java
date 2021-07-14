//*****************************************************************************
//
// VariableSetReporter.java
//
// One thing we often would like to do is build detailed reports for best-of-run
// agents when we're done with a problem. Reporters come in to fill that job.
// Basically, their job is to take an agent and a problem that agent can be run
// on, and build a report on the agent. 
//
// This provides some basic descriptive reporting on the agent's performance on
// a Problem of type VariableSet.
//
//*****************************************************************************
package logger;
import agent.Agent;
import problem.Problem;
import problem.VariableProblem;
import problem.VariableSetProblem;
import fitness.FitnessFunction;
public class VariableSetReporter implements Reporter {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private VariableSetProblem problem;    // the problem we're reporting on
    private VariableSetProblem validation; // an optional 'validation' problem
    private FitnessFunction fitness;       // an optional fitness function
    private String target;                 // the variable we're predicting 

    // used for printing reports
    public static String nl = System.getProperty("line.separator");



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * The most basic of constructors. Only provides output on the problem
     */
    public VariableSetReporter(VariableSetProblem problem, String target) {
	this(problem, target, null);
    }

    /**
     * We also report on an additional validation set.
     */
    public VariableSetReporter(VariableSetProblem problem, 
			       VariableSetProblem validation,
			       String target) {
	this(problem, validation, target, null);
    }

    /**
     * We build a report on the problem, and also spew out a fitness
     */
    public VariableSetReporter(VariableSetProblem problem, String target,
			       FitnessFunction fitness) {
	this(problem, null, target, fitness);
    }

    /**
     * The whole shebang
     */
    public VariableSetReporter(VariableSetProblem problem,
			       VariableSetProblem validation,
			       String target,
			       FitnessFunction fitness) {
	this.problem    = problem;
	this.validation = validation;
	this.target     = target;
	this.fitness    = fitness;
	this.problem.prepare();
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public String report(Agent agent) {
	// print out our agent
	String string = "";
	string += "Agent: " + agent + nl + nl;

	// first, add a report for our main problem
	string += 
	    "*************************" + nl +
	    "***  PROBLEM REPORT   ***" + nl + 
	    "*************************" + nl +
	    nl + 
	    report(agent, problem) + nl;

	// see if we can build a validation report
	if(validation != null) {
	    string += 
		"*************************" + nl +
		"*** VALIDATION REPORT ***" + nl + 
		"*************************" + nl +
		nl + 
		report(agent, validation) + nl;
	}

	// do some 10x10 graph reporting
	//***********
	// FINISH ME
	//***********

	return string;
    }

    public String report(Agent agent, Problem problem) {
	return report(agent, problem, fitness);
    }

    public String report(Agent agent, Problem problem, FitnessFunction func) {
	// a l'il bit of casting first...
	VariableSetProblem vProblem = (VariableSetProblem)problem;

	String string = target + "\tNUANCE" + nl;

	// we want to iterate across all of the problems and evaluate the agent
	for(java.util.Iterator it = vProblem.iterator(); it.hasNext();) {
	    VariableProblem oneProb = (VariableProblem)it.next();
	    string  += oneProb.get(target) + "\t" + agent.eval(oneProb) + nl;
	}

	// if we can print a fitness, do it now
	if(func != null)
	    string += nl + "fitness: " + func.eval(agent, problem) + nl;

	return string;
    }
}
