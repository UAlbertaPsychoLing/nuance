//*****************************************************************************
//
// SquareSymbol.java
//
// Implementation of a function that squares the value of its child.
//
//*****************************************************************************
package agent.s_expression;

public class SquareSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public SquareSymbol() {
	this(null);
    }

    public SquareSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new SquareSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "square";
    }

    public double eval(problem.Problem problem) {
	return Math.pow(children[0].eval(problem), 2);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	double       min = Math.pow(r.min(), 2);
	double       max = Math.pow(r.max(), 2);
	return new numerics.Range(Math.min(min, max), Math.max(min, max));
    }
}
