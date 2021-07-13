//*****************************************************************************
//
// NeuralNetwork.java
//
// Implements a neural network. Assumes feed-forward connectivity.
//
//*****************************************************************************
package agent.ann;
import java.util.*;
import problem.Problem;
import problem.VariableProblem;
public class NeuralNetwork {
    //*************************************************************************
    // protected variables
    //*************************************************************************
    protected LinkedList        nodes; // a list of all our nodes
    protected LinkedList  connections; // a list of all our connections
    protected Vector          in_vars; // the name of our input variables
    protected Vector         out_vars; // the name of our output variables
    protected Map              layers; // maps layer number to vector of nodes
    protected Map            conn_map; // map from node to outgoing connections
    protected Map              inputs; // map from variable name to input node
    protected Map             outputs; // map from variable name to output node
    protected Map         node_layers; // map from node to layer num
    protected int          num_layers; // the number of hidden layers we have



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NeuralNetwork() {
	nodes       = new LinkedList();
	connections = new LinkedList();
	in_vars     = new Vector();
	out_vars    = new Vector();
	conn_map    = new Hashtable();
	inputs      = new Hashtable();
	outputs     = new Hashtable();
	node_layers = new Hashtable();
	layers      = new Hashtable();
	num_layers  = 0;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * add a new node to the network
     */
    public void add(NeuralNode node) {
	nodes.addLast(node);
    }

    /**
     * Map a new input variable to the given node
     */
    public void setInput(String name, NeuralNode node) {
	inputs.put(name, node);
	in_vars.addElement(name);
    }

    /**
     * Map a new output variable to the given node
     */
    public void setOutput(String name, NeuralNode node) {
	outputs.put(name, node);
	out_vars.addElement(name);
    }

    /**
     * Assign the node to a layer
     */
    public void setLayer(NeuralNode node, int layer) {
	if(layer > num_layers)
	    num_layers = layer;

	// turn our layer into an object
	Integer intLayer = new Integer(layer);

	// get the list of nodes at this layer
	Vector nodes = (Vector)layers.get(intLayer);

	// of no nodes exist in this layer yet, create a new list and add it
	if(nodes == null) {
	    nodes = new Vector(10);
	    layers.put(intLayer, nodes);
	}

	// add the new node
	nodes.addElement(node);
	node_layers.put(node, intLayer);
    }

    /**
     * gets the activation of the output node with the given name
     */
    public double getActivation(String output) {
	NeuralNode node = (NeuralNode)outputs.get(output);
	return (node != null ? node.getActivation() : 0);
    }

    /**
     * builds a new connection between two nodes
     */
    public void connect(NeuralNode from, NeuralNode to) {
	connect(from, to, 0);
    }

    /**
     * builds a connection between two nodes. sets the starting weight
     */
    public void connect(NeuralNode from, NeuralNode to, double weight) {
	Connection conn = new Connection(from, to, weight);
	connections.addLast(conn);
	
	// check to see if we have a list of connections
	Vector conns = (Vector)conn_map.get(from);
	// if we don't have a list of connections, create a new one
	if(conns == null) {
	    conns = new Vector();
	    conn_map.put(from, conns);
	}

	// add the new connection
	conns.addElement(conn);
    }

    /**
     * runs the problem through the network
     */
    public void present(Problem problem) {
	VariableProblem p = (VariableProblem)problem;
	NeuralNode n, to;
	String         v;
	Vector     nodes;
	int         i, j;

	// first, activate all of our inputs and send off their signals
	int size = in_vars.size();
	for(i = 0; i < size; i++) {
	    v = (String)in_vars.elementAt(i);
	    n = (NeuralNode)inputs.get(v);
	    n.addInput(((Double)p.get(v)).doubleValue());
	    n.activate();
	    sendSignals(n);
	}

	// go through the hidden layers and activate/send signals for all nodes
	for(i = 1; i <= num_layers; i++) {
	    nodes = (Vector)layers.get(new Integer(i));
	    if(nodes != null) {
		for(j = 0; j < nodes.size(); j++) {
		    n = (NeuralNode)nodes.elementAt(j);
		    n.activate();
		    sendSignals(n);
		}
	    }
	}

	// finally, transform all of our output's inputs to internal activity
	size = out_vars.size();
	for(i = 0; i < size; i++) {
	    v = (String)out_vars.elementAt(i);
	    n = (NeuralNode)outputs.get(v);
	    n.activate();
	}
    }

    /**
     * create a copy of the network
     */
    public NeuralNetwork copy() {
	// variables we'll need
	NeuralNode    node;
	NeuralNode      to;
	NeuralNode newnode;
	NeuralNode   newto;
	Connection    conn;
	String         key;

	// make a new network
	NeuralNetwork net = new NeuralNetwork();

	// make all of our new nodes, and have them mapped by the old ones
	Map nodemap = new Hashtable();

	// make all of our new inputs and add them to the network
	for(Iterator it = nodes.iterator(); it.hasNext();) {
	    node    = (NeuralNode)it.next();
	    newnode = node.copy();
	    nodemap.put(node, newnode);
	    net.add(newnode);
	}

	// set up all of our new inputs
	for(int i = 0; i < in_vars.size(); i++) {
	    key  = (String)in_vars.elementAt(i);
	    node = (NeuralNode)inputs.get(key);
	    net.setInput(key, (NeuralNode)nodemap.get(node));
	}

	// set up all of our new outputs
	for(int i = 0; i < out_vars.size(); i++) {
	    key  = (String)out_vars.elementAt(i);
	    node = (NeuralNode)outputs.get(key);
	    net.setOutput(key, (NeuralNode)nodemap.get(node));
	}

	// set up all of our new hidden units
	for(Iterator it = node_layers.keySet().iterator(); it.hasNext();) {
	    node = (NeuralNode)it.next();
	    net.setLayer((NeuralNode)nodemap.get(node), 
			 ((Integer)node_layers.get(node)).intValue());
	}

	// make all of our new connections
	for(Iterator it = connections.iterator(); it.hasNext();) {
	    conn = (Connection)it.next();

	    // find the new from-node
	    newnode = (NeuralNode)nodemap.get(conn.getFrom());

	    // find the new to-node
	    newto   = (NeuralNode)nodemap.get(conn.getTo());

	    // link the two nodes
	    net.connect(newnode, newto, conn.getWeight());
	}

	return net;
    }

    /**
     * Returns an iterator over our connections
     */
    public Iterator connections() {
	return connections.iterator();
    }

    /**
     * Returns the number of connections
     */
    public int numConnections() {
	return connections.size();
    }

    /**
     * Returns an iterator over our nodes
     */
    public Iterator nodes() {
	return nodes.iterator();
    }

    /**
     * Returns the number of nodes
     */
    public int numNodes() {
	return nodes.size();
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Looks at all of the node's connections and sends signals out through
     * each one, based on the node's internal activity
     */
    private void sendSignals(NeuralNode node) {
	Vector conns = (Vector)conn_map.get(node);
	Connection c = null;
	if(conns != null) {
	    int size = conns.size();
	    for(int i = 0; i < size; i++) {
		c = (Connection)conns.elementAt(i);
		c.getTo().addInput(node.getActivation() * c.getWeight());
	    }
	}
    }
}
