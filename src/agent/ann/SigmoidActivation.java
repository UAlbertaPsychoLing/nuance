//*****************************************************************************
//
// SigmoidActivation.java
//
// Transforms input with a sigmoid function
//
//*****************************************************************************
package agent.ann;
public class SigmoidActivation implements ActivationFunction {
    public double activate(double input) {
	return 1d / (1d + Math.pow(10, -input));
    }
}
