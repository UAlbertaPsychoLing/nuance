//*****************************************************************************
//
// NWiseMode.java
//
// NWiseMode allows users to run all n-wise combinations of predictors on the
// target value. It is assumed that the logger and SymbolicFactory are also
// used within the mode we take in as an argument.
//
//*****************************************************************************
package mode;
import  java.util.*;
import  agent.*;
import  agent.s_expression.*;
import  logger.*;
public class NWiseMode extends Mode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private SymbolicFactory factory; // the thing that generates agents
    private Logger           logger; // the thing doing our logging
    private Mode               mode; // the real mode we are wrapping around
    private int                   n; // What is the size of N in N-Wise?



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NWiseMode(SymbolicFactory factory, Logger logger, Mode mode, int n) {
	this.factory = factory;
	this.logger  = logger;
	this.mode    = mode;
	this.n       = n;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    public void start() {
	// first things first... build a list of all our variables
	Vector variables = new Vector();
	for(Iterator it = factory.variables(); it.hasNext();)
	    variables.addElement(it.next());

	// build our list of points that should be used for any one run
	boolean used[] = new boolean[variables.size()];
	for(int i = 0; i < n; i++)
	    used[i] = true;
	for(int i = n; i < used.length; i++)
	    used[i] = false;

	// the min/max of our "on" point in the array of used items
	int bottom = 0;
	int top    = n-1;

	do {
	    // now, remove all of the variables from our factory
	    for(int i = 0; i < variables.size(); i++)
		factory.removeVariable((String)variables.elementAt(i));

	    // add in the variables for this run

	    // change the log directory

	    // do the set of runs

	    // retreive the best

	    // append it to the list of best

	} while(false);
    }



    //*************************************************************************
    // private methods
    //*************************************************************************

}
