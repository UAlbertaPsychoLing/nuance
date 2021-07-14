//*****************************************************************************
//
// VariableProblem.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// VariableProblem is a class of problems where _Agents_ can access the value
// of variables, in an attempt to aid their answer for the _Fitness Function_
//
//*****************************************************************************
package problem;
import  java.util.Hashtable;
public class VariableProblem implements Problem { 
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Hashtable variables = null; // our mappings from variable:value

    // always hashing keys can be a bit of a bottleneck; the variables below
    // are an attempt allow people to look up values by place in an array. It
    // is somewhat of a hack and only works when datasets are parsed from file,
    // but... it's a performance gain, and it's a juicy one.
    private Hashtable  varnums = null; // a mapping from name to position number
    private Object[]      vals = null; // an array of vals in the variable hash



    //*************************************************************************
    // Constructors
    //*************************************************************************
    /**
     * Create a new instance of VariableProblem. No fancy stuff, here
     */
    public VariableProblem() {
	variables = new Hashtable();
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public void prepare() {
	// nothing to do, here
    }



    //*************************************************************************
    // Public methods
    //*************************************************************************
    /**
     * Add a new variable to the VariableProblem
     */
    public void put(String key, Object val) {
	variables.put(key, val);
    }

    /**
     * Get the value of a variable in the VariableProblem
     */
    public Object get(String key) {
	return variables.get(key);
    }

    /**
     * Returns the array number associated with any key
     */
    public int keyGetPos(String key) {
	return ((Integer)varnums.get(key)).intValue();
    }

    /**
     * Get a value by number, rather than hash
     */
    public Object getByPos(int num) {
	return vals[num];
    }

    /**
     * Returns true if we can get a variable by number rather than value
     */
    public boolean canGetByPos() {
	return (vals != null);
    }

    /**
     * adds a map from key to array position number, and an array 
     * of the vals the keys point to
     */
    public void addNumMap(Hashtable varnums, Object[] vals) {
	this.varnums = varnums;
	this.vals    = vals;
    }

    /**
     * Returns an iterator over our variable types
     */
    public java.util.Iterator variables() {
	return variables.keySet().iterator();
    }

    /**
     * Returns the number of variables in the problem
     */
    public int numVariables() {
	return variables.size();
    }
}
