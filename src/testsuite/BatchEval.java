//*****************************************************************************
//
// BatchEval.java
//
// Evaluates a bunch of functions. Put their output in columns, separated by
// tabs.
//
//*****************************************************************************
package testsuite;
import  problem.*;
import  java.io.*;
import  java.util.*;
import  fitness.*;
import  agent.s_expression.*;
public class BatchEval {
    public static void main(String args[]) throws Exception {
	if(args.length != 2) {
	    System.out.println("A single dataset must be provided as an arg, " +
			       "along with the number of equations to " +
			       "evaluate.");
	    return;
	}

	// get our number of equations
	int eqtns = Integer.parseInt(args[1]);

	// try parsing the problem
	VariableSetProblem prob = VariableSetProblem.parse(args[0]);
	//	prob.prepare();

	// Make a vector to hold our vector of outputs
	Vector outputs = new Vector();

	// make a reader around standard in
	BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
	// collect all of our raw values
	String line;
	for(int i = 0; i < eqtns; i++) {
	    try {
		line = in.readLine();
		SymbolicAgent agent = 
		    new SymbolicAgent(SymbolicFactory.parse(line));

		// Our vector of estimates
		Vector estimates = new Vector();

		for(Iterator it = prob.iterator(); it.hasNext();)
		    estimates.addElement(agent.eval((Problem)it.next()));
		outputs.addElement(estimates);
	    }
	    catch(Exception e) {
		System.out.print  ("Error at:");
		System.err.println("equation = " + (i+1));
		System.exit(1);
	    }
	}

	// print everything out
	for(int i = 0; i < ((Vector)outputs.elementAt(0)).size(); i++) {
	    Vector vect;
	    for(int j = 0; j < outputs.size(); j++) {
		vect = (Vector)outputs.elementAt(j);
		System.out.print(vect.elementAt(i).toString() +
				 (j == outputs.size() -1 ? "" : "\t"));
	    }
	    System.out.println();
	}
    }
}
