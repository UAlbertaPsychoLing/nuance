//*****************************************************************************
//
// SubtractionSymbol.java
//
// Implementation of a function that subtracts one s-expression from another.
//
//*****************************************************************************
package agent.s_expression;

public class SubtractionSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public SubtractionSymbol() {
	this(null, null);
    }

    public SubtractionSymbol(Symbol child1, Symbol child2) {
	children = new Symbol[2];
	children[0] = child1;
	children[1] = child2;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new SubtractionSymbol(
           (children[0] != null ? children[0].copy() : null), 
	   (children[1] != null ? children[1].copy() : null));
    }

    public String type() {
	return "-";
    }

    public double eval(problem.Problem problem) {
	return children[0].eval(problem) - children[1].eval(problem);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r1 = children[0].range(problem);
	numerics.Range r2 = children[1].range(problem);
	return new numerics.Range(r1.min() - r2.max(), r1.max() - r2.min());
    }
}
