//*****************************************************************************
//
// LnSymbol.java
//
// Implementation of a function that takes the natural logarithm of a child
// s-expression.
//
//*****************************************************************************
package agent.s_expression;

public class LnSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public LnSymbol() {
	this(null);
    }

    public LnSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new LnSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "ln";
    }

    public double eval(problem.Problem problem) {
	return Math.log(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	if(r.min() <= 0)
	    throw new numerics.InvalidRangeException(
                "possible ln of non-positive number");
	else
	    return new numerics.Range(Math.log(r.min()), Math.log(r.max()));
    }
}
