//*****************************************************************************
//
// BatchReport.java
//
// takes in a bunch of functions from standard in, and then prints them all
// back out to standard out with descriptive information appended. Requires
// a dataset as an argument.
//
//*****************************************************************************
package testsuite;
import  problem.VariableSetProblem;
import  java.io.*;
import  fitness.*;
import  agent.s_expression.*;
public class BatchReport {
    public static void main(String args[]) throws Exception {
	if(args.length != 4) {
	    System.out.println("A single dataset must be provided as an arg, " +
			       "along with the variable to be predicted, the " +
			       "number of generations, and number of runs.");
	    return;
	}

	// get our target
	String target = args[1];

	// and our gens/runs
	int gens = Integer.parseInt(args[2]);
	int runs = Integer.parseInt(args[3]);

	// try parsing the problem
	VariableSetProblem prob = VariableSetProblem.parse(args[0]);
	prob.prepare();

	// make the fitness functions we'll be testing on
	FitnessFunction cFunc = new CorrelationFitnessFunction(target);
	FitnessFunction eFunc = new ScaledErrorFitness(target);

	// make tables for all of the values we'll be collecting...
	double length[][] = new double[gens][runs];
	double smse[][]   = new double[gens][runs];
	double rsqrd[][]  = new double[gens][runs];

	// make a reader around standard in
	BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
	// collect all of our raw values
	String line;
	for(int i = 0; i < runs; i++) {
	    for(int j = 0; j < gens; j++) {
		try {
		    line = in.readLine();
		    SymbolicAgent agent = 
			new SymbolicAgent(SymbolicFactory.parse(line));
		    length[j][i]   = agent.size();
		    smse[j][i]     = -eFunc.eval(agent, prob);
		    rsqrd[j][i]    = cFunc.eval(agent, prob);
		}
		catch(Exception e) {
		    System.out.print  ("Error at:");
		    System.err.println("run = " + (i+1) + "\t gen = " + (j+1));
		    System.exit(1);
		}
	    }
	}

	// print everything out - length first
	printData("LENGTH", length);
	System.out.println();
	printData("SMSE",   smse);
	System.out.println();
	printData("RSQRD",  rsqrd);
	System.out.println();
    }

    public static void printData(String name, double[][] raw) {
	int gens = raw.length;
	int runs = raw[0].length;

	System.out.println(name);
	// print our headers
	for(int i = 0; i < runs; i++) {
	    System.out.print("RUN" + (i+1) + "\t");
	} System.out.println();

	// now, print our data
	for(int i = 0; i < gens; i++) {
	    for(int j = 0; j < runs; j++) {
		System.out.print(raw[i][j] + "\t");
	    } System.out.println();
	} System.out.println();
    }
}
