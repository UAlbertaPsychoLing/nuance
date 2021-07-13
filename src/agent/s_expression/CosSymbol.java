//*****************************************************************************
//
// CosSymbol.java
//
// Implementation of a function that returns the cos of an s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class CosSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public CosSymbol() {
	this(null);
    }

    public CosSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new CosSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "cos";
    }

    public double eval(problem.Problem problem) {
	return Math.cos(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem) 
	throws numerics.InvalidRangeException {
	// incase it throws an exception...
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(-1, 1);
    }
}
