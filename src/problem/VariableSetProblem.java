//*****************************************************************************
//
// VariableSetProblem.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// VariableSetProblem is a problem type that is essentially a set of other
// problems of type VariableProblem. Fitness Functions evaluating over this
// problem will typically take an average error or correlation or sum of squared
// error of the Agent on some target variable.
//
//*****************************************************************************
package problem;
import java.util.Random;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import numerics.*;
public class VariableSetProblem implements Problem { 
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Hashtable summary_stats; // the ranges of our various variables
    private Vector         problems; // the list of all problems in here
    private Vector          test_ps; // the problems for this prepare()
    private Vector         varnames; // a list of variables this set has. This 
                                     // is only used when we parse the varnames
                                     // from a file so we can keep them in
                                     // order; if the set is built manually, we 
                                     // don't use this var
    private double         test_pct; // what pct of the total problems do we 
                                     // test per generation?

    // the initial amount of room we make in the set
    public static int DFLT_INITIAL_CAPACITY = 1000;
    private static EmptyMarker     set_null = new EmptyMarker();



    //*************************************************************************
    // Constructors
    //*************************************************************************
    /**
     * Create a new instance of VariableSetProblem. No fancy stuff, here
     */
    public VariableSetProblem() {
	this(1.00);
    }

    /**
     * Create a new instance of VariableSetProblem, Specifying the pct of our
     * total problems we use each test
     */
    public VariableSetProblem(double test_pct) {
	this(test_pct, DFLT_INITIAL_CAPACITY);
    }

    /**
     * Create a new VariableSetProblem, and set its initial capacity
     */
    public VariableSetProblem(double test_pct, int initialCapacity) {
	this.varnames      = null;
	this.summary_stats = new Hashtable();
	this.problems      = new Vector(initialCapacity);
	this.test_ps       = new Vector(initialCapacity);
	this.test_pct      = test_pct;
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    /**
     * Fill our test_ps with random problems for the next generation
     */
    public void prepare() {
	if(test_pct == 0)
	    test_ps = problems;
	else {
	    Random rand = new Random();
	    test_ps.clear();
	    int n = (int)(test_pct * (double)problems.size());

	    // remove datapoints we add, so we don't add them twice
	    for(int i = 0; i < n; i++)
		test_ps.add((VariableProblem)problems.remove(rand.nextInt(problems.size())));

	    // now, put everything back in
	    for(Iterator it = test_ps.iterator(); it.hasNext(); )
		problems.add((VariableProblem)it.next());
	}
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Build our summary statistics for a certain variable
     */
    private SummaryStats buildStats(String variable) {
	// collect all of our numeric entries
	Vector number_entries = new Vector();
	for(Iterator it = problems.iterator(); it.hasNext();) {
	    Object val = ((VariableProblem)it.next()).get(variable);
	    if(val instanceof Number) number_entries.addElement(val);
	}

	// turn them all into doubles
	double vals[] = new double[number_entries.size()];
	for(int i = 0; i < vals.length; i++)
	    vals[i] = ((Number)number_entries.elementAt(i)).doubleValue();
	return new SummaryStats(vals);
    }



    //*************************************************************************
    // Public methods
    //*************************************************************************
    /**
     * Get the summary statistics for our variable
     */
    public SummaryStats summaryStats(String variable) {
	// make sure the variable exists...
	if(((VariableProblem)problems.elementAt(0)).get(variable) == null)
	    return null;
	else {
	    SummaryStats stats = (SummaryStats)summary_stats.get(variable);
	    // if it doesn't exist, build it
	    if(stats == null) {
		stats = buildStats(variable);
		summary_stats.put(variable, stats);
	    }
	    return stats;
	}
    }

    /**
     * Add a new problem to the list
     */
    public void add(VariableProblem problem) {
	problems.add(problem);
    }

    /**
     * Returns the number of VariableProblems in this set
     */
    public int size() {
	return problems.size();
    }

    /**
     * Return how many test problems we have
     */
    public int testSize() {
	return test_ps.size();
    }

    /**
     * Return an iterator over all our problems
     */
    public Iterator iterator() {
	return problems.iterator();
    }

    /**
     * Same as iterator(), but only for our problems for this generation
     */
    public Iterator problems() {
	return test_ps.iterator();
    }

    /**
     * Set the % of problems used for testing each generation
     */
    public void setTestPct(double pct) {
	this.test_pct = pct;
    }

    /**
     * gets the % of problems used for testing each generation
     */
    public double getTestPct() {
	return test_pct;
    }

    /**
     * Return an iterator over our variable types. If varnames is null (i.e. we
     * built the set manually), then we use the first problem as proxy for what
     * types of variables we have. In this cse, It is assumed that all of the
     * entries in this problem have the same variables, so we just return
     * one of the iterators for an individual VariableProblem. If no entries
     * exist, null is returned.
     */
    public Iterator variables() {
	if(varnames != null)
	    return varnames.iterator();
	if(size() == 0)
	    return null;
	return ((VariableProblem)problems.elementAt(0)).variables();
    }

    /**
     * Returns the number of variables in the set
     */
    public int numVariables() {
	if(varnames != null)
	    return varnames.size();
	if(size() == 0)
	    return 0;
	return ((VariableProblem)problems.elementAt(0)).numVariables();
    }

    /**
     * Returns a shallow copy of the VariableSetProblem
     */
    public VariableSetProblem copy() {
	VariableSetProblem newprob = new VariableSetProblem(test_pct,
							    problems.size());
	newprob.varnames      = varnames;
	newprob.summary_stats = summary_stats;
	// copy in all of the VariableProblems
	for(Iterator it = iterator(); it.hasNext();)
	    newprob.add((VariableProblem)it.next());
	return newprob;
    }



    //*************************************************************************
    // private static methods
    //*************************************************************************
    /**
     * Signifies an empty marker within a dataset
     */
    private static class EmptyMarker { };

    /**
     * Returns true if the marker signifies "no value available" in the dataset
     */
    private static boolean isEmptyMarker(String marker) {
	return (marker.length() == 0 || marker.equalsIgnoreCase("n/a") ||
		marker.equalsIgnoreCase("."));
    }



    //*************************************************************************
    // public static methods
    //*************************************************************************
    /**
     * Thrown when we cannot parse a dataset
     */
    public static class MalformedDatasetException extends Exception {
	public MalformedDatasetException(String message) {
	    super(message);
	}
    }

    /**
     * Parses a new VariableSetProblem from the specified file. If an error
     * is encountered, a null problem is returned.
     */
    public static VariableSetProblem parse(String filename) 
	throws MalformedDatasetException {
	VariableSetProblem set = new VariableSetProblem();
	Hashtable     cat_vars = new Hashtable();
	double    curr_cat_val = -834521.4582;
	double    curr_val_inc = 1983.142923;
	BufferedReader      in;
	String            line;
	char         delimeter = '\t';

	try { 
	    in   = new BufferedReader(new FileReader(new File(filename))); 
	    // read in the variable names
	    line = in.readLine();
	}
	catch(Exception e) { 
	    throw new MalformedDatasetException(
                "Error reading from file, or file does not exist."); 
	}

	// figure out which delimeter we're using: tab, space, or comma
	if(line.indexOf('\t') != -1)
	    delimeter = '\t';
	else if(line.indexOf(',') != -1)
	    delimeter = ',';
	else if(line.indexOf(' ') != -1)
	    delimeter = ' ';

	// count how many variables we have
	int num_vars = 1;
	for(int i = 0; i < line.length(); i++)
	    if(line.charAt(i) == delimeter) num_vars++;
	
	// parse them all
	String[] vars = new String[num_vars];
	for(int i = 0; i < num_vars-1; i++) {
	    vars[i] = line.substring(0, line.indexOf(delimeter));
	    line    = line.substring(line.indexOf(delimeter) +1);
	// add the last one
	} vars[vars.length-1] = line;
	
	// create varnames and fill them up
	set.varnames = new Vector();
	for(int i = 0; i < vars.length; i++)
	    set.varnames.addElement(vars[i]);

	// it will eventually map variable names to their position in varnames
	Hashtable nameNumMap = new Hashtable();
	
	// now, read in all the datapoints
	int lnum = 1;
	try {
	    while( (line = in.readLine()) != null) {
		VariableProblem prob = new VariableProblem();
		String[]        vals = new String[num_vars];
		// parse all of our values
		for(int i = 0; i < num_vars-1; i++) {
		    String val = line.substring(0, line.indexOf(delimeter));
		    line       = line.substring(line.indexOf(delimeter) +1);
		    vals[i]    = val;
		    // add the last one
		} vals[vals.length-1] = line;
		
		// add all of our values to the problem. If they're numeric, add
		// them as such. If they're string, make a new categorical val.
		// If they're blank, mark them and make sure to take the avg.
		for(int i = 0; i < num_vars; i++) {
		    try {
			prob.put(vars[i], new Double(vals[i]));
		    } catch(NumberFormatException e) {
			// empty string... 
			// we'll have to give it the var avg later
			if(isEmptyMarker(vals[i])) 
			    prob.put(vars[i], set_null);
			// categorical variable. Create a new entry if needed. 
			else {
			    if(!cat_vars.containsKey(vals[i])) {
				curr_cat_val += curr_val_inc;
			       cat_vars.put(vals[i], new Double(curr_cat_val));
			       set.varnames.addElement(vals[i]);
			    }
			    prob.put(vars[i], cat_vars.get(vals[i]));
			}
		    }
		}
		
		// add our num map and val array to the problem
		// prob.addNumMap(nameNumMap, vals);
		
		// add the problem to the problem set
		set.add(prob);
		lnum++;
	    }
	}
	catch(Exception e) {
	    e.printStackTrace();
	    throw new MalformedDatasetException("Error reading row " + lnum);
	}

	// add entries for all our categorical variables to each row in the set
	if(cat_vars.size() > 0) {
	    for(Iterator it = set.iterator(); it.hasNext();) {
		VariableProblem prob = (VariableProblem)it.next();
		for(Iterator it2 =cat_vars.keySet().iterator();it2.hasNext();) {
		    String key = (String)it2.next();
		    prob.put(key, cat_vars.get(key));
		}
	    }
	}

	// build our mappings between variable names and position in varnames
	for(int i = 0; i < set.varnames.size(); i++)
	    nameNumMap.put(set.varnames.elementAt(i), new Integer(i));

	// build all of our summary stats
	for(int i = 0; i < vars.length; i++)
	    set.summary_stats.put(vars[i], set.buildStats(vars[i]));

	// fill in all of our empty markers with variable averages
	for(Iterator it = set.iterator(); it.hasNext();) {
	    VariableProblem prob = (VariableProblem)it.next();
	    for(Iterator varit = set.varnames.iterator(); varit.hasNext();) {
		String var = (String)varit.next();
		if(prob.get(var) == set_null) {
		    prob.put(var, new Double(set.summaryStats(var).getMean()));
		}
	    }
	}
	
	// clean up and return our set
	try { in.close(); } 
	catch(Exception e) { }

	return set;
    }
}
