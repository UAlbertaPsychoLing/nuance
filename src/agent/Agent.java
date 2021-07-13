//*****************************************************************************
//
// Agent.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// These are the basic functions that any _Agent_ must implement
//
//*****************************************************************************
package agent;
public interface Agent extends java.io.Serializable {
    /**
     * Returns the agent's solution to some problem. In its typical form for 
     * NUANCE, this will simply be an estimate of of some target number, given 
     * a set of inputs to base the estimate on. However, 
     */
    public Object eval(problem.Problem problem);

    /**
     * Although the fitness of an agent really isn't part of it, we keep it on
     * the agent for simplicity's sake, because every agent needs a fitness. If
     * it wasn't recorded here, we'd just have to create some other class that
     * holds agent:fitness pairs. A higher value should always mean greater
     * fitness. Fitness should always be greater or equal to 0
     */
    public double getFitness();

    /**
     * See getFitness() for the full deal of why this is here; this function 
     * sets an agent's fitness value
     */
    public void setFitness(double val);

    /**
     * Agents have an "age" set to them. This might be useful for various setups
     * of the GP system. For instance, we might want agents to die after a
     * specified amount of time, or certain fitness functions might even
     * incorporate age into the process of calculating a fitness. Every time
     * an agent is evaluated on a problem by a fitness function, the agent's
     * age will be incremented by 1. Age starts at 0.
     */
    public void incrementAge();

    /**
     * returns the age of the Agent (i.e. how many times it has been evaluated
     * on a problem
     */
    public int getAge();

    /**
     * In some architectures, agents can be rated as "bigger" and "smaller".
     * size should return some numeric value representing the size of the
     * agent. If the architecture the agent implements does not "grow" in any
     * way, size should always be 0.
     */
    public int size();
}
