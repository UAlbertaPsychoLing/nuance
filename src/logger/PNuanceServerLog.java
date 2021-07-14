//*****************************************************************************
//
// BasicLog.java
//
// Nope, no lumberjacks here! NUANCE can run in many different modes, and with
// both graphical and textual interfaces. All of these things will require
// slightly different methods of logging data. That is what a Logger handles.
//
// This is a basic implementation of a Logger. The log will send info both to
// the screen, and to files in a log directory that is passed in during
// construction.
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
public class PNuanceServerLog extends BasicLog {
    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * PrintStream is the stream we attempt to send generation-by-generation
     * progress updates to. Root is the directory where most of our logging
     * will be completed in.
     */
    public PNuanceServerLog(PrintStream out, String root, FitnessFunction ffunc,
		    Problem prob) {
	this(out, root, ffunc, prob, DFLT_AGENTS_TO_LOG);
    }

    /**
     * PrintStream is the stream we attempt to send generation-by-generation
     * progress updates to. Root is the directory where most of our logging
     * will be completed in. Logsize is how many of the top n agents our
     * generation-by-generation updates will include info for.
     */
    public PNuanceServerLog(PrintStream out, String root, FitnessFunction ffunc,
		    Problem prob, int logsize) {
	this(out, root, ffunc, prob, logsize, null);
    }

    /**
     * PrintStream is the stream we attempt to send generation-by-generation
     * progress updates to. Root is the directory where most of our logging
     * will be completed in. Logsize is how many of the top n agents our
     * generation-by-generation updates will include info for. Reporter is used
     * to log some descriptive info about the best agent we ever found. Can be
     * null.
     */
    public PNuanceServerLog(PrintStream out, String root, FitnessFunction ffunc,
		    Problem prob, int logsize, Reporter reporter) {
	super(out, root, ffunc, prob, logsize, reporter);
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public void log(Agent[] population) {
	// quick! Capture our time
	long now  = System.currentTimeMillis();
	long diff = now - last_log;
	last_log  = now;

	// generation number goes up
	curr_gen++;

	// if we're on our first generation, open up new log files
	if(curr_gen == 1) {
	    run_dir = curr_dir + File.separator + "log";
	    makeLogFiles();
	}

	// do we have a different best agent of the run? If so, test it
	if(population[0] != best_run) {
	    // how well is this agent doing?
	    double eval = ffunc.eval(population[0], problem);

	    // see if we have a new best agent of the run
	    if(eval > best_fitness_run) {
		best_run         = population[0];
		best_fitness_run = eval;
	    }
	    
	    // see if we have a new best agent
	    if(eval > best_fitness) {
		best         = population[0];
		best_fitness = eval;
	    }
	}

	// if we found a new best agent, log it
	if(best == population[0]) {
	    String tolog = 
		"Encountered new best agent: " + nl +
		"  " + (best_fitness > 0 ? "fitness" : "error") + ": " +
		Math.abs(best_fitness) + "\t" + best + nl;

	    // send it to the screen
	    println(tolog);

	    try {
		// append our summary information
		summary.write("" + best_fitness + "\t" + best + nl);
		history.write(tolog);
		summary.flush();
		history.flush();
		buildBestReport();
	    } catch(Exception e) {}
	}
    }



    //*************************************************************************
    // protected methods
    //*************************************************************************
    /**
     * Take our current log dir and make all of our basic files that we'll
     * be logging into.
     */
    protected void makeLogFiles() {
	try {
	    // create the directory
	    File dir = new File(run_dir);
	    dir.mkdirs();

	    // the files...
	    File hfile = new File(run_dir + File.separator + "history.txt");
	    File sfile = new File(run_dir + File.separator + "summary.txt");

	    // and finally the buffers
	    history = new BufferedWriter(new FileWriter(hfile));
	    summary = new BufferedWriter(new FileWriter(sfile));

	    // append the filenames
	    history.write(hfile.getAbsolutePath() + nl + nl);
	    summary.write(sfile.getAbsolutePath() + nl + nl);

	    // append the summary head
	    summary.write("FITNESS\tBEST" + nl);

	} catch(Exception e) {
	    System.err.println("Error making log files in directory, " + 
			       run_dir);
	    System.err.println(e.toString());
	    System.exit(1);
	}
    }    
}
