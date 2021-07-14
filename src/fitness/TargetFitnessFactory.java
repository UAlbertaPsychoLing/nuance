//*****************************************************************************
//
// TargetFitnessFactory.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// FitnessFactory is a static class that can create all of the fitness
// functions that take a string 'target' argument. These are the fitness
// functions we use on regression problems - the main problems NUANCE runs on.
//
//*****************************************************************************
package fitness;
public class TargetFitnessFactory {
    //*************************************************************************
    // public variables
    //*************************************************************************
    // a string list of all our fitness functions
    public static final String[] fitnessFuncs = {
	"R squared",
	"percent hit",
// 	"Negative MSE",
    };
    // and the corresponding numeric values
    public static final int FITNESS_RSQRD         = 0;
    public static final int FITNESS_PCT_HIT       = 1;
//     public static final int FITNESS_MSE           = 2;



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Return a new fitness function of the specified type
     */
    public static FitnessFunction make(int num, String target) {
	return make(fitnessFuncs[num], target);
    }

    /**
     * Return a new fitness function of the specified name
     */
    public static FitnessFunction make(String name, String target) {
	if(name.equalsIgnoreCase(fitnessFuncs[FITNESS_PCT_HIT]))
	    return new PctHitFitnessFunction(target);
	if(name.equalsIgnoreCase(fitnessFuncs[FITNESS_RSQRD]))
	    return new RSquaredFitnessFunction(target);
// 	if(name.equalsIgnoreCase(fitnessFuncs[FITNESS_MSE]))
// 	    return new DistanceFitnessFunction(target, 2);
	return null;
    }

    /**
     * Returns the index assocciated with the specified function name. -1
     * for no match
     */
    public static int getNum(String ffunc) {
	for(int i = 0; i < fitnessFuncs.length; i++)
	    if(ffunc.equalsIgnoreCase(fitnessFuncs[i]))
		return i;
	return -1;
    }
}
