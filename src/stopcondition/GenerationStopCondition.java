//*****************************************************************************
//
// GenerationStopCondition.java
//
// Checks to see how many generations have elapsed. If the number is at, or
// exceeds our upper limit, return that we should stop.
//
//*****************************************************************************
package stopcondition;
import  agent.Agent;
public class GenerationStopCondition implements StopCondition {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private int  max_gen; // when we stop
    private int curr_gen; // which generation we're currently on



    //*************************************************************************
    // constructors
    //*************************************************************************
    public GenerationStopCondition(int generations) {
	max_gen = generations;
	reset();
    }



    //*************************************************************************
    // interface methods
    //*************************************************************************
    /**
     * Set our current generation to 0
     */
    public void reset() {
	curr_gen = 0;
    }

    /**
     * up our generation count and see if it is at or exceeds our maximum
     * generation.
     */
    public boolean check(Agent[] population) {
	return ((++curr_gen) >= max_gen);
    }
}
