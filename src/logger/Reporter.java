//*****************************************************************************
//
// Reporter.java
//
// One thing we often would like to do is build detailed reports for best-of-run
// agents when we're done with a problem. Reporters come in to fill that job.
// Basically, their job is to take an agent and a problem that agent can be run
// on, and build a report on the agent. 
//
//*****************************************************************************
package logger;
import agent.Agent;
import problem.Problem;
import fitness.FitnessFunction;
public interface Reporter {
    /**
     * Build a report of the agent
     */
    public String report(Agent agent);

    /**
     * Build a report of the agent on the specified problem
     */
    public String report(Agent agent, Problem problem);

    /**
     * Build a report of the agent on the problem. Also report a fitness
     */
    public String report(Agent agent, Problem problem, FitnessFunction func);
}
