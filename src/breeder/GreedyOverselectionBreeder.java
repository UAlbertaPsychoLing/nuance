//*****************************************************************************
//
// GreedyOverselectionBreeder.java
//
// The Breeder handles a couple aspects of the GP procedure. First, it is the
// thing that will generate the initial population. It also can take in an
// array of Agents, select out the ones it deems good for mating, and mate them.
// Mostly, the Breeder instantiates a selection and breeding scheme.
//
// This breeder gives differential selection to 'elite' agents, as determined
// by fitness. The top elite% of agents are selected with a higher chance than
// the rest of the agents to move onto the next generation. Mating is done
// randomly.
//
//*****************************************************************************
package breeder;
import agent.Agent;
import agent.AgentFactory;
public class GreedyOverselectionBreeder implements Breeder {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private AgentFactory AgentMaker;  // the thing that we make new agents 
                                      // with, either through mating or random
                                      // generation
    private double elite_pct;         // what pct of the top agents are 'elite'
    private double elite_selection;   // what is the chance that an elite
                                      // agent is selected for the next gen
    private double norm_selection;    // same deal, for normal agents.

    // default values for some parameters
    public static double DFLT_ELITE_SELECTION = 0.8;
    public static double DFLT_NORM_SELECTION  = 0.2;
    


    //*************************************************************************
    // constructors
    //*************************************************************************
    public GreedyOverselectionBreeder(AgentFactory factory) {
	this(factory, 0.20);
    }
    
    public GreedyOverselectionBreeder(AgentFactory factory, double elite_pct) {
	this(factory, elite_pct, DFLT_ELITE_SELECTION, DFLT_NORM_SELECTION);
    }

    public GreedyOverselectionBreeder(AgentFactory factory, double elite_pct,
				      double elite_selection, double norm_selection ) {
	this.AgentMaker = factory;
	this.elite_pct  = elite_pct;
	this.elite_selection = elite_selection;
	this.norm_selection  = norm_selection;
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
	int top_parent, elite_cutoff = (int)(elite_pct * (double)agents.length);
	double selection_chance = elite_selection;
	Agent[] newpop = new Agent[agents.length];
	java.util.Random rand = new java.util.Random();
	
	// select our agents for breeding
	for(int i = top_parent = 0; i < elite_cutoff; i++) {
	    if(i >= elite_cutoff)
		selection_chance = norm_selection;
	    if(rand.nextDouble() <= selection_chance)
		newpop[top_parent++] = agents[i];
	}

	// in the very improbable chance we don't get ANY agents...
	if(top_parent == 0)
	    return populate(agents.length);

	// now, mate to fill in the rest of the population
	for(int i = top_parent; i < newpop.length; i++) {
	    Agent parent1 = newpop[rand.nextInt(top_parent)];
	    Agent parent2 = agents[rand.nextInt(top_parent)];
	    Agent baby    = AgentMaker.mate(parent1, parent2);
	    
	    // add the baby
	    newpop[i] = baby;
	}

	return newpop;
    }
}
