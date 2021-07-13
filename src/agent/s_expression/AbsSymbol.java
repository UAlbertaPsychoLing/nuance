//*****************************************************************************
//
// AbsSymbol.java
//
// Implementation of a function that returns the absolute value of a child
// s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class AbsSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public AbsSymbol() {
	this(null);
    }

    public AbsSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new AbsSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "abs";
    }

    public double eval(problem.Problem problem) {
	return Math.abs(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	double     lower = Math.abs(r.min());
	double     upper = Math.abs(r.max());
	return new numerics.Range(Math.min(lower, upper), 
				  Math.max(lower, upper));
    }
}
