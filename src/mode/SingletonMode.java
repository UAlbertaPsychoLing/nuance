//*****************************************************************************
//
// SingletonMode.java
//
// SingletonMode allows users to run all predictors individually against the
// target value. It is assumed that the logger and SymbolicFactory are also 
// used within the mode we take in as an argument.
//
//*****************************************************************************
package mode;
import  java.util.*;
import  agent.*;
import  agent.s_expression.*;
import  logger.*;
public class SingletonMode extends Mode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private SymbolicFactory factory; // the thing that generates agents
    private Logger           logger; // the thing doing our logging
    private Mode               mode; // the real mode we are wrapping around



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SingletonMode(SymbolicFactory factory, Logger logger, Mode mode) {
	this.factory = factory;
	this.logger  = logger;
	this.mode    = mode;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    public void run() {
	// first things first... build a list of all our variables
	Vector variables = new Vector();
	for(Iterator it = factory.variables(); it.hasNext();)
	    variables.addElement(it.next());
	int n = variables.size();

	// start a monitor to check if we ever get interrupted. If we do,
	// also interrupt our child monitor
	SingletonMonitor monitor = new SingletonMonitor();
	monitor.start();

	logger.append("best_log.txt", 
		      "VARIBLE\tAGENT" + 
		      System.getProperty("line.separator"));

	// go through all of our combiniations...
	for(int i = 0; !interrupt && i < n; i++) {
	    // first, remove all of our variables from the factory
	    for(int j = 0; j < n; j++)
		factory.removeVariable((String)variables.elementAt(j));
		
	    // now add in the variable
	    factory.addVariable((String)variables.elementAt(i));

	    // clear our logger
	    logger.clear();

	    // change our log directory
	    String dir = "" + variables.elementAt(i);
	    logger.changeDirectory(dir);

	    // display a message saying what we're doing
	    logger.println("STARTING RUNS FOR " + variables.elementAt(i));

	    // do the set of runs
	    mode.run();

	    // retreive the best
	    Agent best = logger.best();
		
	    // append it to the list of best
	    logger.append("../best_log.txt", 
			  variables.elementAt(i) + "\t" +
			  best + System.getProperty("line.separator"));
	}
    }



    //*************************************************************************
    // private Classes
    //*************************************************************************
    /**
     * Monitors our interrupt status. If we ever get interrupted, also interrupt
     * our child process
     */
    private class SingletonMonitor extends Thread {
	public void run() {
	    while(!interrupt) {
		try {
		    sleep(1000);
		} catch(Exception e) { }
	    }
	    mode.interrupt = true;
	}
    }
} 
