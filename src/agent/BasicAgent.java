//*****************************************************************************
//
// BasicAgent.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This is an abstract class that implements the functions which all agents
// will most likely perform exactly the same (e.g. the setting and getting of
// fitness values).
//
//*****************************************************************************
package agent;
public abstract class BasicAgent implements Agent {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double fitness = 0;  // the current fitness of this agent
    private int        age = 0;  // how many times have we been evaluated?



    //*************************************************************************
    // abstract methods
    //*************************************************************************
    /**
     * Inherited from Agent.java
     * Must be implemented by any class that extends this one
     */
    public abstract Object eval(problem.Problem problem);



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public double getFitness() {
	return fitness;
    }

    public void setFitness(double val) {
	fitness = val;
    }

    public void incrementAge() {
	age++;
    }

    public int getAge() {
	return age;
    }

    public int size() {
	return 0;
    }
}
