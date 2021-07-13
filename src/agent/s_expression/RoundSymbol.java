//*****************************************************************************
//
// AbsSymbol.java
//
// Rounds the ouput of the child node to the nearest integer
//
//*****************************************************************************
package agent.s_expression;

public class RoundSymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public RoundSymbol() {
	this(null);
    }

    public RoundSymbol(Symbol child) {
	children = new Symbol[1];
	children[0] = child;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new RoundSymbol((children[0]!=null?children[0].copy():null));
    }

    public String type() {
	return "round";
    }

    public double eval(problem.Problem problem) {
	return Math.round(children[0].eval(problem));
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r = children[0].range(problem);
	return new numerics.Range(Math.round(r.min()), Math.round(r.max()));
    }
}
