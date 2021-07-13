//*****************************************************************************
//
// AntilnSymbol.java
//
// Implementation of a function that returns e^(output of child)
//
//*****************************************************************************
package agent.s_expression;

public class AntilnSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public AntilnSymbol() {
	this(null);
    }

    public AntilnSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new AntilnSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "antiln";
    }

    public double eval(problem.Problem problem) {
	return Math.pow(Math.E, children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(Math.pow(Math.E, r.min()),
				  Math.pow(Math.E, r.max()));
    }
}
