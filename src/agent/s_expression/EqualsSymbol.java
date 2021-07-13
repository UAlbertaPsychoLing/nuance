//*****************************************************************************
//
// EqualsSymbol.java
//
// Implementation of a function that compares the output of two s-expression.
// If the first one is equals the 2nd, the output of the 3rd is returned.
// Otherwise, the output of the 4th is returned.
//
//*****************************************************************************
package agent.s_expression;

public class EqualsSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public EqualsSymbol() {
	this(null, null, null, null);
    }

    public EqualsSymbol(Symbol child1, Symbol child2, Symbol child3, 
		      Symbol child4) {
	children = new Symbol[4];
	children[0] = child1;
	children[1] = child2;
	children[2] = child3;
	children[3] = child4;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new EqualsSymbol((children[0]!=null?children[0].copy():null),
			      (children[1]!=null?children[1].copy():null),
			      (children[2]!=null?children[2].copy():null),
			      (children[3]!=null?children[3].copy():null));
    }

    public String type() {
	return "=";
    }

    public double eval(problem.Problem problem) {
	if(children[0].eval(problem) == children[1].eval(problem))
	    return children[2].eval(problem);
	else
	    return children[3].eval(problem);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	// incase they throw exceptions...
	children[0].range(problem);
	children[1].range(problem);

	numerics.Range   ifRange = children[2].range(problem);
	numerics.Range elseRange = children[3].range(problem);
	return new numerics.Range(Math.min(ifRange.min(), elseRange.min()),
				  Math.max(ifRange.max(), elseRange.max()));
    }
}
