//*****************************************************************************
//
// BasicSymbol.java
//
// s-expressions (symbolic expressions) are the heart of NUANCE. NUANCE was
// origionally developed to perform nonlinear regressions; regression equations
// took the form of symbolic expressions (trees of mathematical operators, 
// numbers, and variables). There are many different types of symbols (different
// functions, constant numbers, variables) -- all of which must share a basic
// set of functions for a SymbolicAgent to be usable during GP. This is an
// interface of those basic functions that all Symbols must share.
//
// BasicSymbol is an abstract implementation of a symbol; this implements all
// of the stuff that is common across all symbols, like attaching/detaching
// child symbols, returning the length of the s-expression rooted at this 
// symbol, and printing out the s_expression in different notations.
//
//*****************************************************************************
package agent.s_expression;

public abstract class BasicSymbol implements Symbol {
    //*************************************************************************
    // protected variables
    //*************************************************************************
    protected Symbol[] children; // any class that extends BasicSymbol must
                                 // use an array[] named children to store
                                 // all of its child symbols, or overwrite the
                                 // methods inherited from BasicSymbol



    //*************************************************************************
    // public methods
    //*************************************************************************
    public String toString() {
	if(children.length == 0)
	    return type();
	else {
	    String string = "(" + type();
	    for(int i = 0; i < children.length; i++)
		string += " " + children[i];
	    return string + ")";
	}
    }



    //*************************************************************************
    // implementation of methods inherited from Symbol interface
    //*************************************************************************
    /**
     * Documentation in the Symbol interface
     */
    public int size() {
	int size = 1; // +1 for ourself
	for(int i = 0; i < children.length; i++)
	    size += children[i].size();
	return size;
    }

    /**
     * Documentation in the Symbol interface
     */
    public int leaves() {
	if(children.length == 0)
	    return 1;
	else {
	    int leaves = 0;
	    for(int i = 0; i < children.length; i++)
		leaves += children[i].leaves();
	    return leaves;
	}
    }

    /**
     * Documentation in the Symbol interface
     */
    public boolean equals(Symbol s_expression) {
	if(s_expression == null) 
	    return false;

	// start by comparing our type... if it's not equal, we're not the same
	if(!type().equals(s_expression.type()))
	    return false;

	// now, check all of our children
	for(int i = 0; i < children.length; i++)
	    if(!children[i].equals(s_expression.getChild(i)))
		return false;

	// all checks ok. We match up
	return true;
    }

    /**
     * Documentation in the Symbol interface
     */
    public int arity() {
	return children.length;
    }

    /**
     * Documentation in the Symbol interface
     */
    public Symbol getChild(int n) {
	return children[n];
    }

    /**
     * Documentation in the Symbol interface
     */
    public void setChild(Symbol symbol, int n) {
	children[n] = symbol;
    }

    /**
     * Documentation in the Symbol interface
     */
    public boolean contains(String type) {
	// start by seeing if we are of this type
	if(type().equals(type))
	    return true;

	// check all of our children
	for(int i = 0; i < children.length; i++)
	    if(children[i].contains(type))
		return true;

	// a Symbol of the specified type was not found anywhere
	return false;
    }
}
