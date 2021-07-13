//*****************************************************************************
//
// MultiplySymbol.java
//
// Implementation of a function that multiplies the output of two other symbolic
// functions.
//
//*****************************************************************************
package agent.s_expression;

public class MultiplySymbol extends BasicSymbol {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public MultiplySymbol() {
	this(null, null);
    }

    public MultiplySymbol(Symbol child1, Symbol child2) {
	children = new Symbol[2];
	children[0] = child1;
	children[1] = child2;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new MultiplySymbol((children[0]!=null?children[0].copy():null), 
				  (children[1]!=null?children[1].copy():null));
    }

    public String type() {
	return "*";
    }

    public double eval(problem.Problem problem) {
	return children[0].eval(problem) * children[1].eval(problem);
    }

    public numerics.Range range(problem.Problem problem)
	throws numerics.InvalidRangeException {
	numerics.Range r1 = children[0].range(problem);
	numerics.Range r2 = children[1].range(problem);
	double xlyl = r1.min()*r2.min();
	double xlyu = r1.min()*r2.max();
	double xuyl = r1.max()*r2.min();
	double xuxu = r1.max()*r2.max();
	return new numerics.Range(
	    Math.min(xlyl, Math.min(xlyu, Math.min(xuyl, xuxu))),
	    Math.max(xlyl, Math.max(xlyu, Math.max(xuyl, xuxu))));
    }
}
