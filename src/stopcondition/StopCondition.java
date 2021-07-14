//*****************************************************************************
//
// StopCondition.java
//
// Depending on the circumstance, we might want to stop a run for various 
// reasons. For instance, a certain amount of time has elapsed, a certain
// number of generations have been iterated, a certain number of generations
// have passed without a change in fitness... These are all StopConditions, and
// should be implemented as such. Different runmodes will take in various
// StopConditions and check them at the end of each generation to see if they
// are met. If they are, the run will be terminated.
//
//*****************************************************************************
package stopcondition;
import  agent.Agent;
public interface StopCondition extends java.io.Serializable {
    /**
     * Resets a stop condition. Should be called at the beginning of each run.
     */
    public void reset();

    /**
     * Checks to see if we need to stop the run, given the current population.
     * A check should be added for each stop condition every generation before
     * breeding has occurred.
     */
    public boolean check(Agent[] population);
}
