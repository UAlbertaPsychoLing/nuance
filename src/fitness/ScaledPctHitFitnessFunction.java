//*****************************************************************************
//
// ScaledPctHitFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This fitness function is exactly the same as pct hit, but it first scales
// the data to remove the y-intercept and slope.
//
//*****************************************************************************
package fitness;
import  numerics.Stats;
public class ScaledPctHitFitnessFunction extends ScaledFitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double sensitivity; // what sort of difference constitutes a hit?



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ScaledPctHitFitnessFunction(String target) {
	this(target, 0.1);
    }

    public ScaledPctHitFitnessFunction(String target, double sensitivity) {
	super(target);
	this.sensitivity = sensitivity;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    protected double calcError(double[] t, double[] y, int N, 
			       double a, double b) {
	// now calculate our error
	int hits = 0;
	for(int i = 0; i < N; i++)
	    if(Math.abs(t[i] - (a + b*y[i])) <= sensitivity)
		hits++;
	return (double)hits/(double)N;
    }
}
