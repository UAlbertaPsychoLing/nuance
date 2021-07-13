//*****************************************************************************
//
// NeuralAgent.java
//
// Wraps an Agent class around NeuralNetworks so they can be evolved.
//
//*****************************************************************************
package agent.ann;
import agent.BasicAgent;
public class NeuralAgent extends BasicAgent {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private NeuralNetwork net; // the net we're wrapping around
    private String        out; // the name of the output node



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NeuralAgent(NeuralNetwork net, String out) {
	this.net = net;
	this.out = out;
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public Object eval(problem.Problem problem) {
	net.present(problem);
	return new Double(net.getActivation(out));
    }

    public String toString() {
	return "";
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Return the neural network we're wrapping around
     */
    public NeuralNetwork getNet() {
	return net;
    }
}
