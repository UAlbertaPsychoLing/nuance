//*****************************************************************************
//
// SqrtSymbol.java
//
// Implementation of a function that square roots the output of another
// s-expression. If the other s-expression returns a number less than zero,
// NaN is returned.
//
//*****************************************************************************
package agent.s_expression;

public class SqrtSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public SqrtSymbol() {
	this(null);
    }

    public SqrtSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new SqrtSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "sqrt";
    }

    public double eval(problem.Problem problem) {
	double val = children[0].eval(problem);
	if(val < 0)
	    return Double.NaN;
	else
	    return Math.sqrt(val);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	if(r.min() < 0)
	    throw new numerics.InvalidRangeException(
	        "Possible sqrt of negative number");
	else
	    return new numerics.Range(Math.sqrt(r.min()), Math.sqrt(r.max()));
    }
}
