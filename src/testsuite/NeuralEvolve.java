//*****************************************************************************
//
// NeuralEvolve.java
//
// Checks to make sure neural networks are evolving OK
//
//*****************************************************************************
package testsuite;
import  agent.ann.*;
import  agent.*;
import  problem.*;
import  logger.*;
import  stopcondition.*;
import  breeder.*;
import  fitness.*;
import  mode.*;
import  java.util.*;
public class NeuralEvolve {
    public static void main(String args[]) {
	NeuralNetwork     net = buildANN();
	NeuralFactory factory = new NeuralFactory(net, "OUT", -10, 10);
	Breeder       breeder = new MedianPassBreeder(factory);
	FitnessFunction ffunc = new RSquaredFitnessFunction("OUT");
	
	VariableSetProblem  p = new VariableSetProblem();
	for(int i = 0; i <= 1; i++) {
	    for(int j = 0; j <= 1; j++) {
		VariableProblem prob = new VariableProblem();
		prob.put("IN1", new Double(i));
		prob.put("IN2", new Double(j));
		prob.put("OUT", new Double((i+j) % 2));
		p.add(prob);
	    }
	}


	BasicLog log = new BasicLog(System.out, "../log/neuroevolve", ffunc,
				    p.copy());
	Mode mode = new BasicMode(breeder, ffunc, p, log, 1, 100,
				  new GenerationStopCondition(100));
	mode.start();
    }


    /**
     * Builds a neural network
     */
    public static NeuralNetwork buildANN() {
	NeuralNetwork ann = new NeuralNetwork();

	// perceptron with 2 hidden units
	NeuralNode in1 = new NeuralNode(new SigmoidActivation());
	NeuralNode in2 = new NeuralNode(new SigmoidActivation());
	NeuralNode hu1 = new NeuralNode(new SigmoidActivation());
	NeuralNode hu2 = new NeuralNode(new SigmoidActivation());
	NeuralNode out = new NeuralNode(new SigmoidActivation());

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
	ann.connect(in1, hu1);
	ann.connect(in2, hu1);
	ann.connect(in1, hu2);
	ann.connect(in2, hu2);
	ann.connect(hu1, out);
	ann.connect(hu2, out);

	return ann;
    }
}
