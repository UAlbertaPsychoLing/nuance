//*****************************************************************************
//
// DivisionSymbol.java
//
// Implementation of a function that divides the output of one s-expression by
// another. If the divisor is 0, NaN is returned.
//
//*****************************************************************************
package agent.s_expression;

public class DivisionSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public DivisionSymbol() {
	this(null, null);
    }

    public DivisionSymbol(Symbol child1, Symbol child2) {
	children = new Symbol[2];
	children[0] = child1;
	children[1] = child2;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new DivisionSymbol((children[0]!=null?children[0].copy():null), 
				  (children[1]!=null?children[1].copy():null));
    }

    public String type() {
	return "/";
    }

    public double eval(problem.Problem problem) {
	double divisor = children[1].eval(problem);
	if(divisor == 0) 
	    return Double.NaN;
	else 
	    return children[0].eval(problem) / divisor;
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range rDenom = children[1].range(problem);
	if(rDenom.contains(0))
	    throw new numerics.InvalidRangeException(
	        "possible division by zero");
	else {
	    // the sign of denomMin and denomMax are both the same. 
	    // We can do this.
	    numerics.Range rNum = children[0].range(problem);
	    double     denomMin = Math.min(1d/rDenom.min(), 1d/rNum.max());
	    double     denomMax = Math.max(1d/rDenom.min(), 1d/rNum.max());
	    return new numerics.Range(denomMin*rNum.min(), denomMax*rNum.max());
	}
    }
}
