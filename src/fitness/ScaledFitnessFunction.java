//*****************************************************************************
//
// ScaledFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This is an abstract class fitness functions which scale data can extend to
// cut out most of the legwork involved in scaling the data
//
//*****************************************************************************
package fitness;
import  java.util.Iterator;
import  numerics.Stats;
import  problem.Problem;
import  problem.VariableProblem;
import  problem.VariableSetProblem;
import  agent.  Agent;
public abstract class ScaledFitnessFunction implements FitnessFunction {
    protected String  target; // the variable we're attempting to predict
    private double[]       t; // for holding our targets
    private double[]       y; // for holding our estimated vals

    private static double slope_min = 0.0000001; // to keep the slopes in
    private static double slope_max = 1000000.0; // a respectable range



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ScaledFitnessFunction(String target) {
	this.target = target;
    }



    //*************************************************************************
    // abstract methods
    //*************************************************************************
    /**
     * Calculates error on a list of targets and estimates, given the intercept
     * and slope of the regression equation
     */
    protected abstract double calcError(double[] t, double[] y, int N,
					double   a, double   b);



    //*************************************************************************
    // protected classes
    //*************************************************************************
    /**
     * Perform the scaling and pass the scaled data to our error calculator
     */
    public double eval(Agent agent, Problem problem) {
	VariableSetProblem  p = (VariableSetProblem)problem;
	VariableProblem one_p = null;
	int N = p.testSize();
	if((t == null || t.length < N) ||
	   (y == null || y.length < N)) {
	    t = new double[N];
	    y = new double[N];
	}
	double a;
	double b;
	double avg_y = 0, avg_t = 0;
	double var_y = 0, covar = 0;

	// figure out all of our values for t and y, plus averages
	Iterator it = p.problems();
	for(int i = 0; it.hasNext(); i++) {
	    one_p = (VariableProblem)it.next();
	    t[i] = ((Double)one_p.get(target)).doubleValue();
	    y[i] = ((Double)agent.eval(one_p)).doubleValue();
	    avg_t += t[i];
	    avg_y += y[i];
	}
	avg_t /= N;
	avg_y /= N;
	
	// calculate our variance and covariance
	for(int i = 0; i < N; i++) {
	    covar += (t[i] - avg_t) * (y[i] - avg_y);
	    var_y += Math.pow(y[i] - avg_y, 2);
	}
	
	// catch the case in which there is no variance...
	try {
	    b = covar/var_y;
	} catch(Error e) {
	    b = 0;
	}
	a = avg_t - b*avg_y;

	if(Math.abs(b) < slope_min || Math.abs(b) > slope_max)
	    return Double.NEGATIVE_INFINITY;

	double val = calcError(t, y, N, a, b);
	if(Double.isNaN(val))
	    return Double.NEGATIVE_INFINITY;
	return val;
    }

    /**
     * same as eval
     */
    public double rawEval(Agent agent, Problem problem) {
	return eval(agent, problem);
    }

    /**
     * empty
     */
    public void calibrate(Problem problem) { }
}
