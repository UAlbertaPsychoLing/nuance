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
public class BasicLog implements Logger {
    //*************************************************************************
    // protected variables
    //*************************************************************************
    protected PrintStream         out; // where we're outputting gen-by-gen info
    protected String             root; // where we're logging all this crap to
    protected String         curr_dir; // the current directory we're logging to
    protected String          run_dir; // curr_dir + "/run xxx"
    protected long           last_log; // the time we last logged something
    protected int            curr_gen; // the generation we're currently on
    protected int            curr_run; // the run we're currently on
    protected int             logsize; // how many agents do we log per entry?
    protected Agent          best_run; // the best agent we've found this run
    protected double best_fitness_run; // and its fitness at the time
    protected Agent              best; // the best agent we've found, to date
    protected double     best_fitness; // and its fitness at the time
    protected Reporter       reporter; // used to generate logs for best agent
    protected FitnessFunction   ffunc; // the fitness func we test agents with
    protected Problem         problem; // the problem we test them on

    // Some logs we are often sending data to
    protected BufferedWriter history;
    protected BufferedWriter summary;

    // for any given log entry, how many agents do we send to the log?
    public static int DFLT_AGENTS_TO_LOG = 1;

    // used for logging
    public static String nl = System.getProperty("line.separator");



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * PrintStream is the stream we attempt to send generation-by-generation
     * progress updates to. Root is the directory where most of our logging
     * will be completed in.
     */
    public BasicLog(PrintStream out, String root, FitnessFunction ffunc,
		    Problem prob) {
	this(out, root, ffunc, prob, DFLT_AGENTS_TO_LOG);
    }

    /**
     * PrintStream is the stream we attempt to send generation-by-generation
     * progress updates to. Root is the directory where most of our logging
     * will be completed in. Logsize is how many of the top n agents our
     * generation-by-generation updates will include info for.
     */
    public BasicLog(PrintStream out, String root, FitnessFunction ffunc,
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
    public BasicLog(PrintStream out, String root, FitnessFunction ffunc,
		    Problem prob, int logsize, Reporter reporter) {
	this.out      = out;
	this.ffunc    = ffunc;
	this.problem  = prob;
	this.reporter = reporter;
	this.root     = root;
	this.curr_dir = root;
	this.logsize  = logsize;
	this.curr_gen = 0;
	this.curr_run = 1;
	this.best_fitness = Double.NEGATIVE_INFINITY;
	this.best_fitness_run = Double.NEGATIVE_INFINITY;

	// make the root
	File fl = new File(root);
	fl.mkdirs();

	// set the time we last accessed the logger
	this.last_log = System.currentTimeMillis();

	// prepare our problem for use
	this.problem.prepare();
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

	// close and flush out our writers
	try {
	    buildBestReport();
	    if(history != null) history.close();
	    if(summary != null) summary.close();
	} catch(Exception e) { }
    }

    public void endAllRuns() {
	buildBestReport();
    }

    public void clear() {
	best             = null;
	best_run         = null;
	best_fitness     = Double.NEGATIVE_INFINITY;
	best_fitness_run = Double.NEGATIVE_INFINITY;
	curr_dir         = root;
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

	// if we're on our first generation, open up new log files
	if(curr_gen == 1) {
	    run_dir = curr_dir + File.separator + "run " + curr_run;
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
	try {
	    // append our summary information
	    summary.write("" + curr_run + "\t" + curr_gen + "\t" + diff + "\t" +
			  Stats.round(best_fitness_run, 3) + 
			  "\t" + best_run + nl);
	    history.write(tolog);
	} catch(Exception e) { }
    }

    public void changeDirectory(String directory) {
	curr_dir = root + File.separator + directory;
	File dir = new File(curr_dir);
	dir.mkdirs();
    }

    public String root() {
	return root;
    }

    public void write(String filename, String data) { 
	File file = new File(curr_dir + File.separator + filename);
	try {
	    FileWriter writer = new FileWriter(file);
	    writer.write(data);
	    writer.close();
	} catch(Exception e) { }
    }

    public void append(String filename, String data) {
	File file = new File(curr_dir + File.separator + filename);
	try {
	    FileWriter writer = new FileWriter(file, true);
	    writer.write(data);
	    writer.close();
	} catch(Exception e) { }
    }

    public void print(String s) {
	out.print(s);
    }

    public void println(String s) {
	out.println(s);
    }



    //*************************************************************************
    // protected methods
    //*************************************************************************
    /**
     * Take our current run_dir and make all of our basic files that we'll
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
	    history=new BufferedWriter(new FileWriter(hfile));
	    summary=new BufferedWriter(new FileWriter(sfile));

	    // append the filenames
	    history.write(hfile.getAbsolutePath() + nl + nl);
	    summary.write(sfile.getAbsolutePath() + nl + nl);

	    // append the summary head
	    summary.write("RUN\tGEN\tMSEC\tFITNESS\tBEST" + nl);

	} catch(Exception e) {
	    System.err.println("Error making log files in directory, " + 
			       run_dir);
	    System.err.println(e.toString());
	    System.exit(1);
	}
    }

    /**
     * Compiles a fairly in-depth report on the best agent found
     */
    protected void buildBestReport() {
	// make a special log for the best agent across all runs
	if(reporter != null && best != null) {
	    File file = new File(curr_dir + File.separator + "report.txt");
	    try {
		FileWriter writer = new FileWriter(file);
		writer.write(reporter.report(best));
		writer.close();
	    } catch(Exception e) { }
	}
    }
}
