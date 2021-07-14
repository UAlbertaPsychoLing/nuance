//*****************************************************************************
//
// ScaledDistanceFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This fitness function is exactly like the distance fitness function, but it
// first scales the values.
//
//*****************************************************************************
package fitness;
import  java.util.Iterator;
import  problem.Problem;
import  problem.VariableProblem;
import  problem.VariableSetProblem;
import  agent.  Agent;
public class ScaledDistanceFitnessFunction extends ScaledFitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double power; // to what power is the distance raised



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ScaledDistanceFitnessFunction(String target) {
	this(target, 2);
    }

    public ScaledDistanceFitnessFunction(String target, double power) {
	super(target);
	this.power = power;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    protected double calcError(double[] t, double[] y, int N, 
			       double a, double b) {
	// now calculate our error
	double error = 0;
	for(int i = 0; i < N; i++)
	    error += Math.abs(Math.pow(t[i] - (a + b*y[i]), power));
	return -error/N;
    }
}
