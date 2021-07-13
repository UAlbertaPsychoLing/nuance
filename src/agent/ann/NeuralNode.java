//*****************************************************************************
//
// NeuralNode.java
//
// Represents a node in a neural network. Takes in inputs from sources, and 
// transforms the summed value with an activation function to represent the
// node's internal state.
//
//*****************************************************************************
package agent.ann;
public class NeuralNode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private ActivationFunction afunc; // what turns input into activation
    private double        activation; // the current level of internal activity
    private double             input; // the summed input value
    private double              bias; // the bias of the node



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NeuralNode(ActivationFunction afunc) {
	this(afunc, 0);
    }

    public NeuralNode(ActivationFunction afunc, double bias) {
	this.afunc      = afunc;
	this.bias       = bias;
	this.activation = 0;
	this.input      = 0;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public void activate() {
	activation = afunc.activate(input - bias);
	input      = 0;
    }

    public double getActivation() {
	return activation;
    }

    public void addInput(double amnt) {
	this.input += amnt;
    }

    public void clear() {
	this.activation = 0;
	this.input      = 0;
    }

    public double getBias() {
	return bias;
    }

    public void setBias(double val) {
	this.bias = val;
    }

    public NeuralNode copy() {
	return new NeuralNode(afunc, bias);
    }
}
