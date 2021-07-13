//*****************************************************************************
//
// AgentFactory.java
//
// GP takes a population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// An AgentFactory is the class that generates _Agents_ to seed the initial
// population. An AgentFactory also takes in viable _Agents_ to mate and
// mutate to create the next generation of agents. This interface defines the
// basic functions that are required for any AgentFactory.
//
//*****************************************************************************
package agent;
public interface AgentFactory extends java.io.Serializable {
    /**
     * Generate a brand new, randomly generated Agent, with age and fitness 0
     */
    public Agent create();

    /**
     * Make a brand new agent that is a composition of the two parents.
     */
    public Agent mate(Agent agent1, Agent agent2);

    /**
     * Return a mutated copy of the agent. A mutation is typically a small, 
     * random change or a set of a few small, random changes.
     */
    public Agent mutate(Agent agent);
}
