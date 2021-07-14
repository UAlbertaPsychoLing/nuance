//*****************************************************************************
//
// MultiStopCondition.java
//
// Depending on the circumstance, we might want to stop a run for various 
// reasons. For instance, a certain amount of time has elapsed, a certain
// number of generations have been iterated, a certain number of generations
// have passed without a change in fitness... These are all StopConditions, and
// should be implemented as such. Different runmodes will take in various
// StopConditions and check them at the end of each generation to see if they
// are met. If they are, the run will be terminated.
//
// This stop condition is a collection other stop conditions.
//
//*****************************************************************************
package stopcondition;
import  agent.Agent;
import  java.util.Vector;
public class MultiStopCondition implements StopCondition {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Vector conds = new Vector(); // our list of conditions



    //*************************************************************************
    // public methods
    //*************************************************************************
    public void add(StopCondition cond) {
	conds.add(cond);
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public void reset() {
	for(int i = 0; i < conds.size(); i++)
	    ((StopCondition)conds.elementAt(i)).reset();
    }

    public boolean check(Agent[] population) {
	for(int i = 0; i < conds.size(); i++)
	    if(((StopCondition)conds.elementAt(i)).check(population))
		return true;
	return false;
    }
}
