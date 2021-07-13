//*****************************************************************************
//
// OrSymbol.java
//
// Implementation of a function that computes boolean or.
//
//*****************************************************************************
package agent.s_expression;

public class OrSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public OrSymbol() {
	this(null, null);
    }

    public OrSymbol(Symbol child1, Symbol child2) {
	children = new Symbol[2];
	children[0] = child1;
	children[1] = child2;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new OrSymbol((children[0] != null ? children[0].copy() : null), 
			    (children[1] != null ? children[1].copy() : null));
    }

    public String type() {
	return "||";
    }

    public double eval(problem.Problem problem) {
	return (children[0].eval(problem) != 0 || 
		children[1].eval(problem) != 0 ? 1 : 0);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	// incase our children have issues...
	numerics.Range r1 = children[0].range(problem);
	numerics.Range r2 = children[1].range(problem);
	return new numerics.Range(0, 1);
    }
}
