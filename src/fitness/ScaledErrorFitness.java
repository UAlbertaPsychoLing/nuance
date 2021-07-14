//*****************************************************************************
//
// ScaledErrorFitness.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// error = (1/n) i from 1 to n ([t_i - (a + by_i)]^2)
//
//*****************************************************************************
package fitness;
import  numerics.Stats;
public class ScaledErrorFitness extends ScaledFitnessFunction {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public ScaledErrorFitness(String target) {
	super(target);
    }



    //*************************************************************************
    // implementation of abstract methods
    //*************************************************************************
    protected double calcError(double[] t, double[] y, int N, 
			       double a, double b) {
	// now calculate our error
	double error = 0;
	for(int i = 0; i < N; i++)
	    error += Math.pow(t[i] - (a + b*y[i]), 2);
	return -error/N;
    }
}
