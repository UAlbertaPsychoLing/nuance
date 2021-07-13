//*****************************************************************************
//
// VariableSymbol.java
//
// Implementation of a Symbol that always returns the same (numeric) value
//
//*****************************************************************************
package agent.s_expression;

public class ConstantSymbol extends BasicSymbol {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private double value; // the value we return



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ConstantSymbol() {
	this(Double.NaN);
    }

    public ConstantSymbol(double value) {
	children   = new Symbol[0];
	this.value = value;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Symbol copy() {
	return new ConstantSymbol(value);
    }

    public String type() {
	return String.valueOf(value);
    }

    public double eval(problem.Problem problem) {
	return value;
    }

    public numerics.Range range(problem.Problem problem) {
	return new numerics.Range(value, value);
    }
}
