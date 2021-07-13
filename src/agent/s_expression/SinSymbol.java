//*****************************************************************************
//
// SinSymbol.java
//
// Implementation of a function that returns the sin of an s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class SinSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public SinSymbol() {
	this(null);
    }

    public SinSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new SinSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "sin";
    }

    public double eval(problem.Problem problem) {
	return Math.sin(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	// incase it throws an exception...
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(-1, 1);
    }
}
