//*****************************************************************************
//
// Symbol.java
//
// s-expressions (symbolic expressions) are the heart of NUANCE. NUANCE was
// origionally developed to perform nonlinear regressions; regression equations
// took the form of symbolic expressions (trees of mathematical operators, 
// numbers, and variables). There are many different types of symbols (different
// functions, constant numbers, variables) -- all of which must share a basic
// set of functions for a SymbolicAgent to be usable during GP. This is an
// interface of those basic functions that all Symbols must share.
//
//*****************************************************************************
package agent.s_expression;

public interface Symbol extends java.io.Serializable {
    /**
     * Evaluate the return value of the symbol, given the problem provided.
     * Problem must be a datapoint, containing values for variables this
     * s-expression might contain. Eval is not expected to be able to handle
     * null children.
     */
    public double eval(problem.Problem problem);

    /**
     * All symbols must have a type. This can be a name like cosine, or 
     * operator like + this is a neccessity for printing out s-expressions
     */
    public String type();

    /**
     * Returns the number of Symbols in the s_expression rooted at this symbol.
     * Size is not expected to be able to handle null children.
     */ 
    public int size();

    /**
     * Returns the number of leaves (terminal Symbols) in the s-expression 
     */
    public int leaves();

    /**
     * As per the standard implementation of equals(), this function must abide
     * by the following rules:
     *
     *   It is reflexive: for any non-null reference value x, x.equals(x) 
     *                    should return true.
     *   It is symmetric: for any non-null reference values x and y, x.equals(y)
     *                    should return true if and only if y.equals(x) returns
     *                    true.
     *   It is transitive: for any non-null reference values x, y, and z, if 
     *                    x.equals(y) returns true and y.equals(z) returns true,
     *                    then x.equals(z) should return true.
     *   It is consistent: for any non-null reference values x and y, multiple 
     *                    invocations of x.equals(y) consistently return true or
     *                    consistently return false, provided no information 
     *                    used in equals comparisons on the objects is modified.
     *   For any non-null reference value x, x.equals(null) should return false.
     *
     * equals is not expected to be able to handle null children.
     */
    public boolean equals(Symbol s_expression);

    /**
     * Returns a deep copy of the s-expression rooted at this Symbol. Ideally:
     *   x.clone() != x
     *   x.clone().getClass() == x.getClass()
     *   x.clone.equals(x)
     *
     * The Symbol IS expected to be able to handle the copying of null children.
     * This is primarily to aid in the creation of random Agents with the
     * SymbolicFactory class.
     */
    public Symbol copy();

    /**
     * Returns the number of child Symbols this symbol can have
     */
    public int arity();

    /**
     * Returns the child with the nth child of this Symbolic function.
     * Only accepts n's s.t. 0 <= n < arity()
     */
    public Symbol getChild(int n);

    /**
     * Sets the nth child of this Symbolic function to the Symbol passed in.
     * Only accepts n's s.t. 0 <= n < arity()
     */
    public void setChild(Symbol symbol, int n);

    /**
     * Returns true if a symbol of the given type exists in the s_expression
     * rooted at this symbol, and false otherwise. Contains is not expected to
     * be able to handle null children.
     */
    public boolean contains(String type);

    /**
     * outputs the range of values this s-expression can evaluage to on the
     * given problem.
     */
    public numerics.Range range(problem.Problem prob)
	throws numerics.InvalidRangeException;
}
