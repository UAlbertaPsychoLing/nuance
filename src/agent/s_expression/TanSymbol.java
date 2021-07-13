//*****************************************************************************
//
// TanSymbol.java
//
// Implementation of a function that returns the tan of an s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class TanSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public TanSymbol() {
	this(null);
    }

    public TanSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new TanSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "tan";
    }

    public double eval(problem.Problem problem) {
	return Math.tan(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	// incase it throws an exception...
	children[0].range(problem);
	// this is buggy...
	return new numerics.Range( Double.NEGATIVE_INFINITY, 
				  -Double.NEGATIVE_INFINITY);
	//***********
	// FINISH ME
	//***********
    }
}
