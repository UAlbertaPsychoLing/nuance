//*****************************************************************************
//
// MutationBreeder.java
//
// The Breeder handles a couple aspects of the GP procedure. First, it is the
// thing that will generate the initial population. It also can take in an
// array of Agents, select out the ones it deems good for mating, and mate them.
// Mostly, the Breeder instantiates a selection and breeding scheme.
//
// This breeder builds on top of another breeder, by allowing for babies to be
// mutated.
//
//*****************************************************************************
package breeder;
import agent.Agent;
import agent.AgentFactory;
public class MutationBreeder implements Breeder {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Breeder        breeder;  // the real breeder
    private AgentFactory   mutater;  // Used for mutating
    private double mutation_chance;  // the chance of mutating a baby



    //*************************************************************************
    // constructors
    //*************************************************************************
    public MutationBreeder(Breeder breeder, AgentFactory mutater,
			   double mutation_chance) {
	this.breeder = breeder;
	this.mutater = mutater;
	this.mutation_chance = mutation_chance;
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public Agent[] populate(int n) {
	return breeder.populate(n);
    }

    public Agent[] breed(Agent[] agents) {
	Agent[] newpop = breeder.breed(agents);
	java.util.Random rand = new java.util.Random();
	// now, go through and mutate all of the new offspring as needed
	for(int i = 0; i < newpop.length; i++)
	    if(newpop[i].getAge() == 0 && rand.nextDouble() < mutation_chance)
		newpop[i] = mutater.mutate(newpop[i]);
	return newpop;
    }
}
