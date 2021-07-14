//*****************************************************************************
//
// RSquaredFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This class implements a fitness function whereby the fitness is the squared
// correlation of the output with target values. It will take any type of agent
// that returns Doubles, but it must take in a VariableSetProblem.
//
//*****************************************************************************
package fitness;
import  java.util.Iterator;
import  problem.Problem;
import  problem.VariableProblem;
import  problem.VariableSetProblem;
import  agent.Agent;
import  numerics.Stats;
public class RSquaredFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private String target; // the variable we are trying to predict
    private double[]    t; // for holding our targets
    private double[]    y; // for holding our estimated vals

    private static double slope_min = 0.0000001; // to keep the slopes in
    private static double slope_max = 1000000.0; // a respectable range



    //*************************************************************************
    // constructors
    //*************************************************************************
    public RSquaredFitnessFunction(String target) {
	this.target = target;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    /**
     * Returns the correlation between the Agent's predictions, and the
     * actual target value in the problem.
     */
    public double rawEval(Agent agent, Problem problem) {
	VariableSetProblem  p = (VariableSetProblem)problem;
	VariableProblem one_p = null;
	int                 N = p.testSize();
	if((t == null || t.length < N) ||
	   (y == null || y.length < N)) {
	    t = new double[N];
	    y = new double[N];
	}
	double avg_t = 0, avg_y = 0;

	// values for the agent on each of the problems
	Iterator it = p.problems();
	for(int i = 0; it.hasNext(); i++) {
	    one_p  = (VariableProblem)it.next();
            t[i]   = ((Double)one_p.get(target)).doubleValue();
            y[i]   = ((Double)agent.eval(one_p)).doubleValue();
	    avg_t += t[i];
	    avg_y += y[i];
        }
	avg_t /= N;
	avg_y /= N;

	// make sure our slope isn't too extreme
	double slope  = Stats.slope(t, y, avg_t, avg_y, 0, N);
	if(Math.abs(slope) > slope_max || Math.abs(slope) < slope_min)
	    return Double.NEGATIVE_INFINITY;

	double correl = Stats.correlation(t, y, 0, N);
	return Math.pow(correl, 2);
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
}
