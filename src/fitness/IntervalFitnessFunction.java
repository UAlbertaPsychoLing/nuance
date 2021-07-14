//*****************************************************************************
//
// IntervalFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// Before the 'real' fitness function is applied, this function will check to
// make sure that there is no problems with the form of the expression (e.g.
// possibly dividing by zero). If a problem exists, then return a fitness of
// negative infinity. Otherwise, carry on with the normal fitness function.
//
//*****************************************************************************
package fitness;
import  problem.Problem;
import  agent.s_expression.SymbolicAgent;
import  agent.s_expression.Symbol;
public class IntervalFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Problem       problem; // the problem we check our ranges on
    private FitnessFunction ffunc; // the real fitness function


    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Takes the problem we check our range on, and the 'real' fitness function
     */
    public IntervalFitnessFunction(FitnessFunction ffunc, Problem problem) {
	this.problem = problem;
	this.ffunc   = ffunc;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    public double rawEval(agent.Agent agent, problem.Problem problem) {
	return ffunc.rawEval(agent, problem);
    }

    public double eval(agent.Agent agent, problem.Problem problem) {
	// if this is our first time being evaluated... check our range
	if(agent.getAge() != 1)
	    return ffunc.eval(agent, problem);
	else {
	    // check to make sure our range is OK
	    Symbol symbol = ((SymbolicAgent)agent).getSExpression();
	    try {
		symbol.range(this.problem);
		return ffunc.eval(agent, problem);
	    }
	    catch(numerics.InvalidRangeException e) {
		return Double.NEGATIVE_INFINITY;
	    }
	}
    }

    /**
     * empty
     */
    public void calibrate(problem.Problem problem) { }
}
