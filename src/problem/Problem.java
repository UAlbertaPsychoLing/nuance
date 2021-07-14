//*****************************************************************************
//
// Problem.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// These are the basic functions that any _Problem_ must implement
//
//*****************************************************************************
package problem;
public interface Problem extends java.io.Serializable { 
    /**
     * Some problems are not stationary but, rather, change from generation to
     * generation. Each generation, if a problem needs to be changed, it can
     * be done through this function.
     */
    public void prepare();
}
