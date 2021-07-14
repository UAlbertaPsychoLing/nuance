//*****************************************************************************
//
// ProgressStopCondition.java
//
// If we haven't made any progress in the past X generations, halt
//
//*****************************************************************************
package stopcondition;
import  agent.Agent;
public class ProgressStopCondition implements StopCondition {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double best_fitness; // the best fitness to date
    private int      gens_since; // how long since it occured?
    private int        max_gens; // how long can we go with no progress?



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ProgressStopCondition(int max_gens) {
	this.max_gens = max_gens;
	reset();
    }



    //*************************************************************************
    // interface methods
    //*************************************************************************
    /**
     * Set our current generation to 0
     */
    public void reset() {
	best_fitness = Double.NEGATIVE_INFINITY;
	gens_since   = 0;
    }

    /**
     * up our generation count and see if it is at or exceeds our maximum
     * generation.
     */
    public boolean check(Agent[] population) {
	double fitness = numerics.Stats.round(population[0].getFitness(), 4);
	gens_since++;

	if(fitness > best_fitness) {
	    best_fitness = fitness;
	    gens_since   = 0;
	    return false;
	}
	else if(gens_since >= max_gens)
	    return true;
	else
	    return false;
    }
}
