//*****************************************************************************
//
// FitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This is the basic interface for a FitnessFunction. Any FitnessFunction that
// is created must implement these basic methods.
//
//*****************************************************************************
package fitness;
public interface FitnessFunction extends java.io.Serializable {
    /**
     * Evaluate the agent passed in on a problem. Fitness must follow a
     * monotonic increase; an Agent with fitness f+1 is considered to be fitter
     * than an agent with fitnesss f
     */
    public double eval(agent.Agent agent, problem.Problem problem);

    /**
     * Like eval, but does not set the agent's fitness or increment its age.
     * This function also should not take into consideration any information
     * except the agent's output and the problem. Some fitness functions
     * integrate age and previous fitness values into the output for eval()
     * this function should NOT do any of that stuff
     */
    public double rawEval(agent.Agent agent, problem.Problem problem);

    /**
     * Sometimes fitness functions will need calibration on a given problem.
     * This should be called before a fitness function is used.
     */
    public void calibrate(problem.Problem problem);
}
