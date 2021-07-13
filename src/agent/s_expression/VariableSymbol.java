//*****************************************************************************
//
// VariableSymbol.java
//
// Implementation of a function that returns the value of the variable in the
// VariableProblem that this symbol represents.
//
//*****************************************************************************
package agent.s_expression;

public class VariableSymbol extends BasicSymbol {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private String variable;         // the variable from the Problem this 
                                     // symbol represents
    protected int        pos = no_pos; // the pos we can use for prob.getByPos()
    protected boolean trypos = false;  // have we tried getting the position?

    private static final int no_pos = -1;


    //*************************************************************************
    // constructors
    //*************************************************************************
    public VariableSymbol() {
	this(null);
    }

    public VariableSymbol(String variable) {
	children = new Symbol[0];
	this.variable = variable;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * resets our position tracker for our variable in a VariableProblem
     */
    public void reset() {
	trypos = false;
	pos = no_pos;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	VariableSymbol symbol = new VariableSymbol(variable);
	symbol.trypos = trypos;
	symbol.pos    = pos;
	return symbol;
    }

    public String type() {
	return variable;
    }

    public double eval(problem.Problem problem) {
	/*
	try {
	    return ((Number)((problem.VariableProblem)problem).
		    getByPos(pos)).doubleValue();
	} catch(NullPointerException e) {
	    return 0;
	}
	*/
	// if we haven't tried asking for the position number yet, do so...
	if(trypos == false) {
	    try {
		trypos = true;
		pos = ((problem.VariableProblem)problem).keyGetPos(variable);
	    } catch(NullPointerException e) {
		pos = no_pos;
	    }
	}

	// now, try looking up our value
	try {
	    if(pos == no_pos) {
		return ((Number)((problem.VariableProblem)problem).
			get(variable)).doubleValue();
	    }
	    else {
		return ((Number)((problem.VariableProblem)problem).
			getByPos(pos)).doubleValue();
	    }
	} catch(NullPointerException e) {
	    return 0;
	}
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	return ((numerics.SummaryStats)((problem.VariableSetProblem)problem).
		summaryStats(variable)).getRange();
    }
}
