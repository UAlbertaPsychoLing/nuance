//*****************************************************************************
//
// TrueSymbol.java
//
// Implementation of a function that always returns false (i.e. 0).
//
//*****************************************************************************
package agent.s_expression;

public class FalseSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public FalseSymbol() {
	children = new Symbol[0];
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new FalseSymbol();
    }

    public String type() {
	return "false";
    }

    public double eval(problem.Problem problem) {
	return 0d;
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	return new numerics.Range(0, 0);
    }
}
