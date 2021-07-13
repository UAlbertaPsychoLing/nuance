//*****************************************************************************
//
// GaussianActivation.java
//
// Transforms input with a gaussian function
//
//*****************************************************************************
package agent.ann;
public class GaussianActivation implements ActivationFunction {
    private static double K = 1 / Math.sqrt(2 * Math.PI);
    public double activate(double input) {
	return K * Math.pow(Math.E, -Math.pow(input, 2)/2);
    }
}
