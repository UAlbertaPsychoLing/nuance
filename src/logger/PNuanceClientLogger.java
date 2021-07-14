//*****************************************************************************
//
// PNuanceClientLog.java
//
// Nope, no lumberjacks here! NUANCE can run in many different modes, and with
// both graphical and textual interfaces. All of these things will require
// slightly different methods of logging data. That is what a Logger handles.
//
// This is the log utility used when running a PNuance client. Note we don't do
// any logging to disk, because all of that is handled centrally by the PNuance
// server.
//
//*****************************************************************************
package logger;
import agent.Agent;
import java.io.PrintStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import fitness.FitnessFunction;
import problem.Problem;
import numerics.Stats;
public class PNuanceClientLogger implements Logger {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private PrintStream         out; // where we're outputting gen-by-gen info
    private long           last_log; // the time we last logged something
    private int            curr_gen; // the generation we're currently on
    private int            curr_run; // the run we're currently on
    private int             logsize; // how many agents do we log per entry?
    private Agent          best_run; // the best agent we've found this run
    private double best_fitness_run; // and its fitness at the time
    private Agent              best; // the best agent we've found, to date
    private double     best_fitness; // and its fitness at the time

    // Some logs we are often sending data to
    BufferedWriter history;
    BufferedWriter summary;

    // for any given log entry, how many agents do we send to the log?
    public static int DFLT_AGENTS_TO_LOG = 1;

    // used for logging
    public static String nl = System.getProperty("line.separator");



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * out is the stream we attempt to send generation-by-generation progress 
     * updates to.
     */
    public PNuanceClientLogger(PrintStream out) {
	this(out, DFLT_AGENTS_TO_LOG);
    }

    /**
     * out is the stream we attempt to send generation-by-generation progress 
     * updates to. Logsize is how many of the top n agents our generation-by-
     * generation updates will include info for.
     */
    public PNuanceClientLogger(PrintStream out, int logsize) {
	this.out      = out;
	this.logsize  = logsize;
	this.curr_gen = 0;
	this.curr_run = 1;
	this.best_fitness     = Double.NEGATIVE_INFINITY;
	this.best_fitness_run = Double.NEGATIVE_INFINITY;

	// set the time we last accessed the logger
	this.last_log = System.currentTimeMillis();
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public void endRun() {
	// run number goes up
	curr_run++;

	// generation gets reset
	curr_gen = 0;

	// reset our best-of-run stuff
	best_run = null;
	best_fitness_run = Double.NEGATIVE_INFINITY;
    }

    public void endAllRuns() {
	// nothing...
    }

    public void clear() {
	best             = null;
	best_run         = null;
	best_fitness     = Double.NEGATIVE_INFINITY;
	best_fitness_run = Double.NEGATIVE_INFINITY;
	curr_gen         = 0;
	curr_run         = 1;
    }

    public Agent best() {
	return best;
    }

    public void log(Agent[] population) {
	// quick! Capture our time
	long now  = System.currentTimeMillis();
	long diff = now - last_log;
	last_log  = now;

	// generation number goes up
	curr_gen++;

	// do we have a different best agent of the run? If so, test it
	if(population[0] != best_run) {
	    // how well is this agent doing?
	    double eval = population[0].getFitness();

	    // see if we have a new best agent of the run
	    if(eval >= best_fitness_run) {
		best_run         = population[0];
		best_fitness_run = eval;
	    }
	    
	    // see if we have a new best agent
	    if(eval >= best_fitness) {
		best         = population[0];
		best_fitness = eval;
	    }
	}

	// go through our top agents, and log them all. Also write summary info
	String tolog = "Run " + curr_run + ", Generation " + curr_gen + " took " + 
	    Stats.round(((double)diff)/1000d, 2) + "sec" + nl;
	for(int i = 0; i < logsize; i++) {
	    double fitness = Stats.round(population[i].getFitness(), 3);
	    tolog += "  " + (i+1) + ") " + (fitness > 0 ? "fitness" : "error") +
		": " + Math.abs(fitness) + "\t" + population[i] + nl;
	}

	// print this all out to our output stream
	println(tolog);
    }

    public void changeDirectory(String directory) {
	// nothing...
    }

    public String root() {
	return ".";
    }

    public void write(String filename, String data) { 
	// nothing...
    }

    public void append(String filename, String data) {
	// nothing...
    }

    public void print(String s) {
	out.print(s);
    }

    public void println(String s) {
	out.println(s);
    }
}
