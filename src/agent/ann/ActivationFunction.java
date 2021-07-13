//*****************************************************************************
//
// ActivationFunction.java
//
// Translates a weighted sum of inputs into a new a new value. Used by nodes in
// Neural Networks to calculate internal activity.
//
//*****************************************************************************
package agent.ann;
public interface ActivationFunction {
    /**
     * Transform the weighted sum of inputs into a new value
     */
    public double activate(double input);
}
