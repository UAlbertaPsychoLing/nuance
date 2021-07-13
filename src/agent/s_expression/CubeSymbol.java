//*****************************************************************************
//
// CubeSymbol.java
//
// Implementation of a function that cubes the value of its child.
//
//*****************************************************************************
package agent.s_expression;

public class CubeSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public CubeSymbol() {
	this(null);
    }

    public CubeSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new CubeSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "cube";
    }

    public double eval(problem.Problem problem) {
	return Math.pow(children[0].eval(problem), 3);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(Math.pow(r.min(), 3), Math.pow(r.max(), 3));
    }
}
