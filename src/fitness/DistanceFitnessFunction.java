//*****************************************************************************
//
// DistanceFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This class implements a fitness function whereby the fitness is the average
// distance between predicted and target variables, raised to some power. Power
// of 1 is cityblock, power of 2 is eucidean, etc... This fitness function will
// work with two types of problems: VariableProblems and VariableSetProblems.
//
//*****************************************************************************
package fitness;
import  java.util.Iterator;
import  problem.Problem;
import  problem.VariableProblem;
import  problem.VariableSetProblem;
import  agent.  Agent;
public class DistanceFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private String target; // the variable we are trying to predict
    private double  power; // what is the power in our calculations?



    //*************************************************************************
    // constructors
    //*************************************************************************
    public DistanceFitnessFunction(String target) {
	this(target, 1);
    }

    public DistanceFitnessFunction(String target, double power) {
	this.target   = target;
	this.power = power;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    /**
     * Returns the inverse average sum of error raised to some value between
     * the target and predicted values.
     */
    public double rawEval(Agent agent, Problem problem) {
	double distance = 0;
	
	// count up all of our hits
	Iterator it = ((VariableSetProblem)problem).problems();
	while(it.hasNext())
	    distance += distance(agent, (VariableProblem)it.next());
	
	distance = -distance/((VariableSetProblem)problem).testSize();

	// return some arbitrarily small number
	if(Double.isNaN(distance))
	    return Double.NEGATIVE_INFINITY;
	return distance;
    }

    /**
     * Same as rawEval
     */
    public double eval(Agent agent, Problem problem) {
	return rawEval(agent, problem);
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Returns the distance, raised to some power
     */
    private double distance(Agent agent, VariableProblem problem) {
	return Math.abs(
           Math.pow(((Double)agent.eval(problem)).doubleValue() - 
		    ((Double)problem.get(target)).doubleValue(), power));
    }

    /**
     * empty
     */
    public void calibrate(Problem problem) { }
}
