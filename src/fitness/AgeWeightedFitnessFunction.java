//*****************************************************************************
//
// AgeWeightedFitnessFunction.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// For some problems, we will want to smear fitness across many generations.
// This fitness function takes another fitness function in, and makes it so that
// fitness is now smeared across generations. The amount of weight that is put
// on recent generations depends on gamma. A high gamma means more weight on
// new generations. The relationship 0 < gamma <= 1 is expected to be held
//
//*****************************************************************************
package fitness;
public class AgeWeightedFitnessFunction implements FitnessFunction {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double gamma;          // how much weight is put on recent evals?
    private FitnessFunction func;  // the 'real' fitness function



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * func is the function that decides the "raw" fitness of an Agent on some
     * problem. AgeWeightedFitnessFunction then transforms that value, based on
     * the age of the Agent and the value of gamma. A higher gamma entails more
     * emphasis on more recent generations. The relationship 0 < gamma <= 1 is
     * expected to be held.
     */
    public AgeWeightedFitnessFunction(FitnessFunction func, double gamma) {
	this.gamma = gamma;
	this.func  = func;
    }



    //*************************************************************************
    // implementation of interface and abstract methods
    //*************************************************************************
    public double rawEval(agent.Agent agent, problem.Problem problem) {
	return func.rawEval(agent, problem);
    }

    public double eval(agent.Agent agent, problem.Problem problem) {
	double fitness = func.eval(agent, problem);
	// what we want to to is take a weighted average of all evaluations up
	// to this point. Gamma will determine how much weight is placed on this
	// current evaluation. If this is our first evaluation (our age is 1), 
	// all the weight will always be put on the current evaluation.
	if(agent.getAge() == 1)
	    return fitness;
	else
	    return (fitness < 0 ? 
		    fitness*gamma + agent.getFitness()*(1d + gamma) :
		    fitness*gamma + agent.getFitness()*(1d - gamma));
    }

    /**
     * empty
     */
    public void calibrate(problem.Problem problem) { }
}
