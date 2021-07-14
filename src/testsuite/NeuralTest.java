//*****************************************************************************
//
// NeuralTest.java
//
// A little testsuite to test the correctness of neural networks
//
//*****************************************************************************
package testsuite;
import  agent.ann.*;
import  problem.Problem;
import  problem.VariableProblem;
import  java.util.*;
public class NeuralTest {
    public static void main(String args[]) {
	NeuralNetwork ann = new NeuralNetwork();

	// let's make a perceptron for solving XOR
	NeuralNode in1 = new NeuralNode(new SigmoidActivation());
	NeuralNode in2 = new NeuralNode(new SigmoidActivation());
	NeuralNode hu1 = new NeuralNode(new SigmoidActivation(), 1.0);
	NeuralNode hu2 = new NeuralNode(new SigmoidActivation(), 1.0);
	NeuralNode out = new NeuralNode(new SigmoidActivation(), 1.75);

	ann.add(in1);
	ann.add(in2);
	ann.add(hu1);
	ann.add(hu2);
	ann.add(out);

	// add all of our nodes in the proper place
	ann.setInput ("IN1", in1);
	ann.setInput ("IN2", in2);
	ann.setLayer (hu1, 1);
	ann.setLayer (hu2, 1);
	ann.setOutput("OUT", out);

	// connect everything up
	ann.connect(in1, hu1,  4.00);
	ann.connect(in2, hu1, -4.00);
	ann.connect(in1, hu2, -4.00);
	ann.connect(in2, hu2,  4.00);
	ann.connect(hu1, out,  3.50);
	ann.connect(hu2, out,  3.50);

	System.out.println("ORIGINAL");
	xor(ann);

	NeuralNetwork copy = ann.copy();

	System.out.println("\r\nCOPY");
	xor(copy);

	System.out.println("\r\nBIAS DIFFERENCE");
	Iterator  it = ann.nodes();
	Iterator cit = copy.nodes();
	while(it.hasNext()) {
	    NeuralNode aNode = (NeuralNode)it.next();
	    NeuralNode cNode = (NeuralNode)cit.next();
	    System.out.println(aNode.getBias() - cNode.getBias());
	}

 	System.out.println("\r\nCONNECTION DIFFERENCE");
 	it  = ann.connections();
 	cit = copy.connections();
 	while(it.hasNext()) {
 	    Connection aConn = (Connection)it.next();
 	    Connection cConn = (Connection)cit.next();
 	    System.out.println(aConn.getWeight() - cConn.getWeight());
 	}
    }

    /**
     * Test the network on the XOR problem
     */
    public static void xor(NeuralNetwork ann) {
	// test the network on all of the bitwise or problems
	for(int i = 0; i <= 1; i++) {
	    for(int j = 0; j <= 1; j++) {
		VariableProblem p = new VariableProblem();
		p.put("IN1", new Double(i));
		p.put("IN2", new Double(j));
		ann.present(p);
		System.out.println(i + "\t" + j + "\t" + 
				   ann.getActivation("OUT"));
	    }
	}
    }
}
