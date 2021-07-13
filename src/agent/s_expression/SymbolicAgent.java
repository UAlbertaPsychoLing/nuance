//*****************************************************************************
//
// SymbolicAgent.java
//
// GP takes a Population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This is the implementation of an agent that embodies a symbolic expression.
// The problems posed to a SymbolicAgent must be VariableProblems
//
//*****************************************************************************
package agent.s_expression;
public class SymbolicAgent extends agent.BasicAgent {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Symbol s_expression;  // the s-expression we embody
    private int leaves;           // the number of leaves in said s-expression
    private int size;             // and the total number of nodes



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Create a new SymbolicAgent, with the given s-expression as the heart of
     * the agent.
     */
    public SymbolicAgent(Symbol s_expression) {
	this.s_expression = s_expression;
	// only calculate this stuff if we need to... save us some time
	this.leaves       = -1;//s_expression.leaves();
	this.size         = -1;//s_expression.size();
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    /**
     * Inherited from BasicAgent
     */
    public Object eval(problem.Problem problem) {
	return new Double(s_expression.eval(problem));
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Return a pointer to the s_expression this Agent uses for evaluation
     */
    public Symbol getSExpression() {
	return s_expression;
    }

    /**
     * Recalculating an s-expression's size can be time-consuming. Let's just
     * cache it here.
     */
    public int size() {
	if(size == -1)
	    size = s_expression.size();
	return size;
    }

    /**
     * Recalculating the number of leaves an s-expressio has can be
     * time-consuming. Let's just cache it here.
     */
    public int leaves() {
	if(leaves == -1)
	    leaves = s_expression.leaves();
	return leaves;
    }

    /**
     * Return a string representation of our s-expression
     */
    public String toString() {
	return s_expression.toString();
    }
}
