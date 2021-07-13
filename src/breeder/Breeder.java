//*****************************************************************************
//
// Breeder.java
//
// The Breeder handles a couple aspects of the GP procedure. First, it is the
// thing that will generate the initial population. It also can take in an
// array of Agents, select out the ones it deems good for mating, and mate them.
// Mostly, the Breeder instantiates a selection and breeding scheme.
//
//*****************************************************************************
package breeder;
public interface Breeder extends java.io.Serializable {
    /**
     * Generates a random starting population of size n
     */
    public agent.Agent[] populate(int n);

    /**
     * Take in an array of agents, and return a new array of agents based on
     * the one passed in. The returned array are the agents that have been
     * selected or bred to compete in the next generation. The agents passed 
     * in are expected to be sorted in descending order of fitness.
     */
    public agent.Agent[] breed(agent.Agent[] agents);
}
