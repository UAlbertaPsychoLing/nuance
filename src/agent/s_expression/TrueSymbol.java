//*****************************************************************************
//
// TrueSymbol.java
//
// Implementation of a function that always returns true (i.e. 1).
//
//*****************************************************************************
package agent.s_expression;

public class TrueSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public TrueSymbol() {
	children = new Symbol[0];
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new TrueSymbol();
    }

    public String type() {
	return "true";
    }

    public double eval(problem.Problem problem) {
	return 1d;
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	return new numerics.Range(1, 1);
    }
}
