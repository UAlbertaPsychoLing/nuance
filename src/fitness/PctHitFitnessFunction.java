//*****************************************************************************
//
// PctHitFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This class implements a fitness function whereby fitness is gauged by how
// many times an agent exactly predicts some value to be predicted. Fitness
// is bound between 0 and 1, where 1 is the most fit and 0 is the least fit.
// This fitness function will work with two types of problems: VariableProblems
// and VariableSetProblems.
//
//*****************************************************************************
package fitness;
import  java.util.Iterator;
import  problem.Problem;
import  problem.VariableProblem;
import  problem.VariableSetProblem;
import  agent.  Agent;
public class PctHitFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private String      target; // the variable we are trying to predict
    private double sensitivity; // what sort of difference constitutes a hit?



    //*************************************************************************
    // constructors
    //*************************************************************************
    public PctHitFitnessFunction(String target) {
	this(target, 0.01);
    }

    public PctHitFitnessFunction(String target, double sensitivity) {
	this.target      = target;
	this.sensitivity = sensitivity;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    /**
     * Checks the agent for perfect performance on all problems. Returns the
     * percent of the problems posed that the agent had perfect performance on.
     * Takes single VariableProblems and VariableSetProblems
     */
    public double rawEval(Agent agent, Problem problem) {
	// We're just dealing with a single VariableProblem 
	if(problem instanceof VariableProblem)
	    return (checkHit(agent, (VariableProblem)problem) == true ? 1 : 0);

	// We're dealing with a set of variable problems
	else if(problem instanceof VariableSetProblem) {
	    int num_hits = 0;
	    // count up all of our hits
	    for(Iterator it = ((VariableSetProblem)problem).problems(); 
		it.hasNext();)
		if(checkHit(agent, (VariableProblem)it.next()))
		    num_hits++;
	    
	    // divide through by the number of tests to get percent hits
	    return (((double)num_hits) / 
		    ((double)((VariableSetProblem)problem).testSize()));
	}

	// no clue how to handle this problem
	else 
	    return 0;
    }

    /**
     * Same as rawEval
     */
    public double eval(Agent agent, Problem problem) {
	return rawEval(agent, problem);
    }

    /**
     * empty
     */
    public void calibrate(Problem problem) { }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Checks the agent's performance on a single VariableProblem. Returns
     * true if the agent accurately predicts the target, and false if the
     * agent does not.
     */
    private boolean checkHit(Agent agent, VariableProblem problem) {
	return (Math.abs(((Double)agent.eval(problem)).doubleValue() -
			 ((Double)problem.get(target)).doubleValue()) <=
		sensitivity);
    }
}
