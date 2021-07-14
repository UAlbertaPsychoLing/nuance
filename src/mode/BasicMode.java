//*****************************************************************************
//
// BasicMode.java
//
// NUANCE can run in various modes. It can run in a solo mode, a networked
// client mode, and a networked server mode, to name a few. All NUANCE modes
// must extend this class.
//
// BasicMode is a basic extention of Mode. It has lots of the generic guts
// filled in, of which other Modes can make use of.
//
//*****************************************************************************
package mode;
import agent.Agent;
import logger.Logger;
import fitness.FitnessFunction;
import problem.Problem;
import breeder.Breeder;
import stopcondition.StopCondition;
import java.util.Vector;
public class BasicMode extends Mode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Agent[] population;      // a vector of all our agents
    private Breeder breeder;         // the guy who makes each generation
    private FitnessFunction fitness; // the function we use to test agents with
    private Problem[] problems;      // the problem(s) we test agents on
    private boolean retest;          // do we need to test each gen?
    private Logger log;              // where we spew out info each generation
    private int runs;                // total number of runs we're doing
    private int popsize;             // how big is our starting population?
    private StopCondition cond;      // our stop condition



    //*************************************************************************
    // constructors
    //*************************************************************************
    public BasicMode(Breeder breeder, FitnessFunction fitness, Problem problem,
		     Logger log, int runs, int popsize,
		     StopCondition condition) {
	this(breeder, fitness, new Problem[1], log, runs, popsize, condition);
	this.problems[0] = problem;
    }

    public BasicMode(Breeder breeder,FitnessFunction fitness,Problem[] problems,
		     Logger log, int runs, int popsize,
		     StopCondition condition) {
	this(breeder, fitness, problems, true, log, runs, popsize, condition);
    }

    public BasicMode(Breeder breeder,FitnessFunction fitness,Problem[] problems,
		     boolean retest, Logger log, int runs, int popsize,
		     StopCondition condition) {
	this.retest      = retest;
	this.population  = null;
	this.popsize     = popsize;
	this.runs        = runs;
	this.breeder     = breeder;
	this.fitness     = fitness;
	this.problems    = problems;
	this.log         = log;
	this.cond        = condition;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public void run() {
	// i runs or until interrupted
	for(int i = 0; !interrupt && i < runs; i++) {
	    // if this is ever true, we will terminate the run
	    boolean hit_stop_condition = false;

	    // initialize our population
	    population = breeder.populate(popsize);

	    // prepare all of our stopping conditions for this run
	    cond.reset();

	    // loop until one of our stopping conditions is met
	    while(!interrupt && !hit_stop_condition) {
		// prepare our problems for the new generation
		for(int j = 0; j < problems.length; j++)
		    problems[j].prepare();

		// test all of the agents and increase their ages
		for(int j = 0; j < population.length; j++) {
		    population[j].incrementAge();
		    // if we don't need to retest (i.e. we're using 100% of
		    // our training set) then skip agents over 1 generation old
		    if(!retest && population[j].getAge() > 1)
			continue;
		    double curr_fitness = 0;
		    for(int l = 0; l < problems.length; l++)
			curr_fitness += fitness.eval(population[j],problems[l]);
		    curr_fitness /= problems.length;
		    population[j].setFitness(curr_fitness);
		}

		// sort by fitness
		java.util.Arrays.sort(population,new agent.FitnessComparator());

		// do our logging
		log.log(population);

		// check all of our stopping conditions
		if(cond.check(population))
		    hit_stop_condition = true;

		// select out the best and mate them if we are not to stop
		if(!hit_stop_condition)
		    population = breeder.breed(population);

		// do our garbage collection
		System.gc();
	    }

	    // do all of the cleanup work for finishing this run
	    log.endRun();
	}

	// do all of the cleanup work for finishing all our runs
	log.endAllRuns();
	log.println("ALL RUNS COMPLETE");
    }
}
