//*****************************************************************************
//
// Connection.java
//
// Represents a connection between two nodes in a neural network.
//
//*****************************************************************************
package agent.ann;
public class Connection {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private NeuralNode from; // the two nodes forming the connection.
    private NeuralNode   to; // from sends signals to to
    private double   weight; // the weight of the connection



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * build a new connection
     */
    public Connection(NeuralNode from, NeuralNode to) {
	this(from, to, 1);
    }

    /**
     * build a new connection
     */
    public Connection(NeuralNode from, NeuralNode to, double weight) {
	this.from   = from;
	this.to     = to;
	this.weight = weight;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public double getWeight() { 
	return weight;
    }

    public void setWeight(double weight) { 
	this.weight = weight;
    }

    public NeuralNode getFrom() {
	return from;
    }

    public NeuralNode getTo() {
	return to;
    }
}
