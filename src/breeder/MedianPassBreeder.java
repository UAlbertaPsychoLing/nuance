//*****************************************************************************
//
// MedianPassBreeder.java
//
// The Breeder handles a couple aspects of the GP procedure. First, it is the
// thing that will generate the initial population. It also can take in an
// array of Agents, select out the ones it deems good for mating, and mate them.
// Mostly, the Breeder instantiates a selection and breeding scheme.
//
// This is perhaps the simplest of all Breeders; it chops off the bottom half
// of the population, and re-fills it with random matings between all of the
// rest of the population.
//
//*****************************************************************************
package breeder;
import agent.Agent;
import agent.AgentFactory;
public class MedianPassBreeder implements Breeder {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private AgentFactory AgentMaker;  // the thing that we make new agents 
                                      // with, either through mating or random
                                      // generation



    //*************************************************************************
    // constructors
    //*************************************************************************
    public MedianPassBreeder(AgentFactory factory) {
	this.AgentMaker = factory;
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public Agent[] populate(int n) {
	Agent[] pop = new Agent[n];
	for(int i = 0; i < n; i++)
	    pop[i] = AgentMaker.create();
	return pop;
    }

    public Agent[] breed(Agent[] agents) {
	int i = 0;
	int half = agents.length/2;
	Agent[] newpop = new Agent[agents.length];
	
	// copy in our top half
	for(; i < half; i++)
	    newpop[i] = agents[i];

	// now, mate to fill in the rest of the population
	java.util.Random rand = new java.util.Random();
	for(; i < agents.length; i++) {
	    Agent parent1 = agents[rand.nextInt(half)];
	    Agent parent2 = agents[rand.nextInt(half)];
	    Agent baby    = AgentMaker.mate(parent1, parent2);

	    // add the baby
	    newpop[i] = baby;
	}

	return newpop;
    }
}
