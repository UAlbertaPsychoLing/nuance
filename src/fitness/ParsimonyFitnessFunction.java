//*****************************************************************************
//
// ParsimonyFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// Sometimes we will want to penalize certain agents from being too big. This
// fitness function takes that into account.
//
//*****************************************************************************
package fitness;
public class ParsimonyFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double parsimony;      // how much fitness do we chop off per size?
    private FitnessFunction func;  // the 'real' fitness function



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * func is the function that decides the "raw" fitness of an Agent on some
     * problem. ParsimonyFitnessFunction then transforms that value, based on
     * the size of the agent. Large agents get a penalty to fitness, based on
     * their size and the strength of the parsimony.
     */
    public ParsimonyFitnessFunction(FitnessFunction func, double parsimony) {
	this.parsimony = parsimony;
	this.func      = func;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    public double rawEval(agent.Agent agent, problem.Problem problem) {
	return func.eval(agent, problem);
    }

    public double eval(agent.Agent agent, problem.Problem problem) {
	// adjust the fitness based on agent size
	double penalty = ((double)agent.size()) * parsimony;
	double eval    = func.eval(agent, problem);
	return (eval < 0 ? eval * (1d + penalty) : eval * (1d - penalty));
    }

    /**
     * empty
     */
    public void calibrate(problem.Problem problem) { }
}
