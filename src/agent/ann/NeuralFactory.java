//*****************************************************************************
//
// NeuralFactory.java
//
// GP takes a population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// NeuralFactories generate neural networks with pre-specified numbers of 
// inputs, outputs, and hidden units.
//
//*****************************************************************************
package agent.ann;
import  agent.*;
import  java.util.Random;
import  java.util.Iterator;
public class NeuralFactory implements AgentFactory {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Random      randomizer; // for generating random numbers
    private NeuralNetwork skeleton; // the connectivity of the network we use
    private String             out; // the name of our network's output
    private double min_conn_weight; // minimum starting connection weight
    private double max_conn_weight; // maximum starting connection weight



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NeuralFactory(NeuralNetwork skeleton, String out) {
	this(skeleton, out, -1, 1);
    }

    public NeuralFactory(NeuralNetwork skeleton, String out,
			 double min_conn_weight,
			 double max_conn_weight) {
	this.randomizer      = new Random();
	this.skeleton        = skeleton;
	this.out             = out;
	this.min_conn_weight = min_conn_weight;
	this.max_conn_weight = max_conn_weight;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    /**
     * Generate a brand new, randomly generated Agent, with age and fitness 0
     */
    public Agent create() {
	NeuralNetwork net = skeleton.copy();
	// make random connection weights
	for(Iterator it = net.connections(); it.hasNext();) {
	    ((Connection)it.next()).setWeight(min_conn_weight + 
	       randomizer.nextDouble() * (max_conn_weight - min_conn_weight));
	}

	return new NeuralAgent(net, out);
    }

    /**
     * Make a brand new agent that is a composition of the two parents.
     */
    public Agent mate(Agent agent1, Agent agent2) {
	NeuralNetwork net1 = ((NeuralAgent)agent1).getNet();
	NeuralNetwork net2 = ((NeuralAgent)agent2).getNet();
	NeuralNetwork  net = net1.copy();
	double      weight = 0;
	double        bias = 0;
	double        rand = 0;

	Iterator net1it = net1.connections(); Connection net1conn;
	Iterator net2it = net2.connections(); Connection net2conn;
	Iterator netit  = net.connections();  Connection  netconn;
	while(net1it.hasNext()) {
	    net1conn = (Connection)net1it.next();
	    net2conn = (Connection)net2it.next();
	    netconn  = (Connection)netit.next();

	    // 3:1 chance of taking the weight from either parent
	    rand = randomizer.nextDouble();
	    if(rand < 0.75)
		weight = net1conn.getWeight();
	    else
		weight = net2conn.getWeight();

	    // add roughly +/- 0.1 to the weight
	    weight += 0.05 * randomizer.nextGaussian();

	    netconn.setWeight(weight);
	}

	net1it = net1.nodes(); NeuralNode net1node;
	net2it = net2.nodes(); NeuralNode net2node;
	netit  = net.nodes();  NeuralNode  netnode;
	while(net1it.hasNext()) {
	    net1node = (NeuralNode)net1it.next();
	    net2node = (NeuralNode)net2it.next();
	    netnode  = (NeuralNode)netit.next();

	    // 3:1 chance of taking bias from either parent
	    rand = randomizer.nextDouble();
	    if(rand < 0.8)
		bias = net1node.getBias();
	    else
		bias = net2node.getBias();

	    // add roughly +/- 0.1 to the weight
	    bias += 0.05 * randomizer.nextGaussian();

	    netnode.setBias(bias);
	}


	return new NeuralAgent(net, out);
    }

    /**
     * Return a mutated copy of the agent. A mutation is typically a small, 
     * random change or a set of a few small, random changes.
     */
    public Agent mutate(Agent agent) {
	NeuralNetwork newnet = ((NeuralAgent)agent).getNet().copy();

	// collect all of the connections, and mutate one with a small prob.
	Connection conns[] = new Connection[newnet.numConnections()];
	int i = 0;
	for(java.util.Iterator it = newnet.connections(); it.hasNext();)
	    conns[i++] = (Connection)it.next();

	// select a random one and mutate the weight
	Connection conn = conns[randomizer.nextInt(conns.length)];
	conn.setWeight(conn.getWeight() + randomizer.nextGaussian());

	return new NeuralAgent(newnet, out);
    }
}
