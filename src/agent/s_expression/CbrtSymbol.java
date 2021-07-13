//*****************************************************************************
//
// CbrtSymbol.java
//
// Implementation of a function that cube roots the output of another 
// s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class CbrtSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public CbrtSymbol() {
	this(null);
    }

    public CbrtSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new CbrtSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "cbrt";
    }

    public double eval(problem.Problem problem) {
	return Math.pow(children[0].eval(problem), 1d/3d);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(Math.pow(r.min(), 1d/3d),
				  Math.pow(r.max(), 1d/3d));
    }
}
