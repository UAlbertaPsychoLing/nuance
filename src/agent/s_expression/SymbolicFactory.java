//*****************************************************************************
//
// SymbolicFactory.java
//
// GP takes a population of _Agents_ and tests them all on a _Problem_ using
// a _Fitness Function_. The _Agents_ that perform best on the _Fitness 
// Function_ are _Mated_ to produce offspring, which replace the _Agents_ that 
// had a poor score on the _Fitness Function_.
//
// This is a factory for creating, mating, and mutating SymbolicAgents.
//
//*****************************************************************************
package agent.s_expression;
import java.util.Random;
import java.util.Vector;
import java.util.LinkedList;
import agent.AgentFactory;
import agent.Agent;
import numerics.Stats;
public class SymbolicFactory implements AgentFactory {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private int         depth; // what is the starting depth of create() agents?
    private double  min_const; // min const possible when making ConstSymbols
    private double  max_const; // max const possible when making ConstSymbols
    private boolean dbl_const; // do we use double or int consts?
    private Vector  variables; // types of SymbolicVariables that can be made
    private Vector  terminals; // a subset of symbols; arity = 0
    private Vector  operators; // the types of operators we can create
    private Random randomizer; // random number generator for making new Agents
    

    public static final int DFLT_DEPTH         = 3;     // variable defaults
    public static final double DFLT_MIN_CONST  = 0;     // ...
    public static final double DFLT_MAX_CONST  = 10;    // ...
    public static final boolean DFLT_DBL_CONST = false; // ...

    // contains a list of all symbolic operators and their names
    public static final SymbolNamePair[] all_ops = {
	new SymbolNamePair(new AddSymbol(),         "addition"),
	new SymbolNamePair(new SubtractionSymbol(), "subtraction"),
	new SymbolNamePair(new DivisionSymbol(),    "division"),
	new SymbolNamePair(new MultiplySymbol(),    "multiplication"),
	new SymbolNamePair(new SqrtSymbol(),        "square root"),
	new SymbolNamePair(new CbrtSymbol(),        "cube root"),
	new SymbolNamePair(new SquareSymbol(),      "square"),
	new SymbolNamePair(new CubeSymbol(),        "cube"),
	new SymbolNamePair(new LnSymbol(),          "natural logarithm"),
	new SymbolNamePair(new AntilnSymbol(),      "antiln"),
	new SymbolNamePair(new AbsSymbol(),         "absolute value"),
	new SymbolNamePair(new RoundSymbol(),       "round"),
	new SymbolNamePair(new SinSymbol(),         "sine"),
	new SymbolNamePair(new CosSymbol(),         "cosine"),
	new SymbolNamePair(new TanSymbol(),         "tangent"),
	new SymbolNamePair(new LessSymbol(),        "less than"),
	new SymbolNamePair(new EqualsSymbol(),      "equals"),
	new SymbolNamePair(new OrSymbol(),          "or"),
	new SymbolNamePair(new AndSymbol(),         "and"),
	new SymbolNamePair(new TrueSymbol(),        "true"),
	new SymbolNamePair(new FalseSymbol(),       "false"),
    };



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SymbolicFactory() {
	this(DFLT_DEPTH);
    }

    public SymbolicFactory(int depth) {
	this(depth, DFLT_MIN_CONST, DFLT_MAX_CONST);
    }

    public SymbolicFactory(int depth, double min_const, double max_const) {
	this(depth, min_const, max_const, DFLT_DBL_CONST);
    }

    public SymbolicFactory(int depth, double min_const, double max_const,
			   boolean dbl_const) {
	this.depth      = depth;
	this.min_const  = min_const;
	this.max_const  = max_const;
	this.dbl_const  = dbl_const;
	this.variables  = new Vector();
	this.terminals  = new Vector();
	this.operators  = new Vector();
	this.randomizer = new Random();

	// add in our default Symbol types. Just terminals for now. It is
	// expected users will add their own functions
	terminals.add(new ConstantSymbol(0));
	terminals.add(new VariableSymbol(null));
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public Agent create() {
	// grow an s-expression and return it with its wrapper
	return new SymbolicAgent(grow(depth));
    }

    public Agent mate(Agent agent1, Agent agent2) {
	// cast us as SymbolicAgents
	SymbolicAgent sAgent1 = (SymbolicAgent)agent1;
	SymbolicAgent sAgent2 = (SymbolicAgent)agent2;

	// Make sure at least one of the agents is greater than 1 Symbol
	if(sAgent1.size() == 1) {
	    // uhh ohh... everything is terminal. Return a copy of agent1
	    if(sAgent2.size() == 1)
		return new SymbolicAgent(sAgent1.getSExpression().copy());
	    // swap us around so we can do crossover
	    else {
		SymbolicAgent tmp = sAgent1;
		sAgent1 = sAgent2;
		sAgent2 = sAgent1;
	    }
	}

	// first, copy the s-expression of one of the agents
	Symbol s_expression = sAgent1.getSExpression().copy();

	// now, find a point for crossover
	int point_num = randomizer.nextInt(sAgent1.size() - sAgent1.leaves()); 
	Symbol point  = getNonTerminal(s_expression, point_num);

	// and find a piece to cross in
	int crossover_num = randomizer.nextInt(sAgent2.size());
	Symbol crossover  = getSymbol(sAgent2.getSExpression(), crossover_num);
	
	// perform the crossover
	int child_num = randomizer.nextInt(point.arity());
	point.setChild(crossover.copy(), child_num);

	// return our expression
	return new SymbolicAgent(s_expression);
    }

    public Agent mutate(Agent agent) {
	// cast us as a SymbolicAgent
	SymbolicAgent sAgent = (SymbolicAgent)agent;

	// make sure we have length > 1
	if(sAgent.size() == 1)
	    return new SymbolicAgent(grow(depth));

	// first, copy its s-expression
	Symbol s_expression = sAgent.getSExpression().copy();

	// now, find a random point to mutate at
	int mutation_num = randomizer.nextInt(sAgent.size() - sAgent.leaves());
	Symbol point     = getNonTerminal(s_expression, mutation_num);

	// and replace a random child
	int child_num = randomizer.nextInt(point.arity());
	point.setChild(grow(depth), child_num);

	// return our expression
	return new SymbolicAgent(s_expression);
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * add a new type of Symbol that can be added to a symbolic expression
     * during mutate() or create()
     */
    public void addSymbol(Symbol symbol) {
	// if its arity is 0, add it to our list of terminals
	if(symbol.arity() == 0)
	    terminals.add(symbol);
	// otherwise, add it to our operators
	else
	    operators.add(symbol);
    }

    /**
     * Add in a new type of variable that can be used during mutate() and
     * create()
     */
    public void addVariable(String variable) {
	variables.add(variable);
    }

    /**
     * Remove a variable from our list of variables
     */
    public void removeVariable(String variable) {
	variables.remove(variable);
    }

    /**
     * returns an iterator over our variables
     */
    public java.util.Iterator variables() {
	return variables.iterator();
    }



    //*************************************************************************
    // public static methods
    //*************************************************************************
    /**
     * Parses an s-expression from the given string
     */
    public static Symbol parse(String string) 
	throws MalformedSExpressionException {
	Symbol s_expression = null;
	string = string.trim();

	// strip off leading and trailing parentheses
	while(string.length() > 0 && string.charAt(0) == '(') {
	    if(string.charAt(string.length()-1) != ')')
		throw new MalformedSExpressionException(
                    "Missing matching bracket in: " + string);
	    string = string.substring(1, string.length()-1);
	}

	// make sure something was actually supplied
	if(string.length() == 0)
	    throw new MalformedSExpressionException(
		"Invalid s-expression - null node exists in tree");

	// figure out our symbol and children
	String symbol;
	int space_index = string.indexOf(' ');
	if(space_index == -1) {
	    symbol = string;
	    string = "";
	}
	else {
	    symbol = string.substring(0, space_index);
	    string = string.substring(space_index).trim();
	}

	// If we have no children. Check if it is a true/false value. If
	// it is not, treat this a numeric value or a variable.
	if(string.length() == 0) {
	    if(symbol.equalsIgnoreCase("true"))
		s_expression = new TrueSymbol();
	    else if(symbol.equalsIgnoreCase("false"))
		s_expression = new FalseSymbol();
	    else try {
		s_expression = new ConstantSymbol(Double.parseDouble(symbol));
	    } catch(NumberFormatException e) {
		s_expression = new VariableSymbol(symbol);
	    }
	}

	// parse our symbol
	else {
	    s_expression = getProtoSymbol(symbol);
	    if(s_expression == null)
		throw new MalformedSExpressionException(
		    "Unrecognized operator: " + symbol);
	    s_expression = s_expression.copy();
	}

	// parse children if we need to
	if(string.length() > 0 || s_expression.arity() > 0) {
	    Symbol[] children = parseMultiSymbols(string);
	    
	    // make sure we've got the proper length
	    if(children.length != s_expression.arity())
		throw new MalformedSExpressionException(
		    "Improper number of children in: " + symbol + " " + string);
	    
	    // attach all of the children
	    for(int i = 0; i < children.length; i++)
		s_expression.setChild(children[i], i);
	}

	// return the resultant expression
	return s_expression;
    }

    /**
     * Parses a list of multiple s-epxression. New expressions start when an
     * old expression's last closing bracket is in place
     */
    public static Symbol[] parseMultiSymbols(String string) 
	throws MalformedSExpressionException {
	String fullstring = string;

        Vector string_symbols = new Vector();
        do {
            string = string.trim();
            int breakpoint = 0;
            int depth      = 0;

	    // go through the string until we're passed an equal number of
	    // opening and closing brackets
            do {
		// we didn't have enough brackets. Uhhohh
		if(breakpoint >= string.length())
		    throw new MalformedSExpressionException(
                        "Not enough parentheses in: " + fullstring);
                if(string.charAt(breakpoint) == '(')
                    depth++;
                else if(string.charAt(breakpoint) == ')')
                    depth--;
		breakpoint++;

		// if we haven't encountered any parentheses, we must be
		// at a terminal. Skip up to the next space to find our end
		if(breakpoint == 1 && depth == 0) {
		    breakpoint = string.indexOf(' ');
		    // we're at the end of the string
		    if(breakpoint == -1) {
			// make sure all of our closing brackets are gone
			if(string.charAt(string.length()-1) == ')')
			    throw new MalformedSExpressionException(
			        "Errant bracket in: " + fullstring);
			breakpoint = string.length();
		    }
		}
            } while(depth > 0);

	    // parse out one symbol
            string_symbols.add(string.substring(0, breakpoint));
            string = string.substring(breakpoint, string.length()).trim();
        } while(string.length() > 0);

	Symbol[] symbols = new Symbol[string_symbols.size()];
	for(int i = 0; i < symbols.length; i++)
	    symbols[i] = parse((String)string_symbols.elementAt(i));
	return symbols;
    }

    /**
     * Returns the prototype symbol (no children) for the symbol 
     * of the given type
     */
    public static Symbol getProtoSymbol(String type) {
	SymbolNamePair pair = getSymbolNamePair(type);
	return (pair != null ? pair.getSymbol() : null);
    }

    /**
     * Returns the SymbolNamePair with the given name or symbol type
     */
    public static SymbolNamePair getSymbolNamePair(String type) {
	for(int i = 0; i < all_ops.length; i++)
	    if(all_ops[i].getSymbol().type().equalsIgnoreCase(type) ||
	       all_ops[i].getName().equalsIgnoreCase(type))
		return all_ops[i];
	return null;
    }

    /**
     * Builds an array of all the symbol types in the string (separated by
     * spaces) and returns the string.
     */
    public static SymbolNamePair[] getSymbolNamePairs(String types) {
	Vector vect = new Vector();
	for(int index=types.indexOf(' '); index != -1;index=types.indexOf(' ')){
	    String one_type = types.substring(0, index);
	    types           = types.substring(index +1);
	    SymbolNamePair pair = getSymbolNamePair(one_type);
	    if(pair != null && !vect.contains(pair))
		vect.addElement(pair);
	}
	// add our last one
	SymbolNamePair pair = getSymbolNamePair(types);
	if(pair != null && !vect.contains(pair))
	    vect.addElement((Object)pair);

	// convert it to an array of SymbolNamePairs
	SymbolNamePair[] pairs = new SymbolNamePair[vect.size()];
	for(int i = 0; i < pairs.length; i++)
	    pairs[i] = (SymbolNamePair)vect.elementAt(i);
	return pairs;
    }

    /**
     * Builds an array of the symbol types not in the string. Each type
     * is separated by a space
     */
    public static SymbolNamePair[] getUnselectedSymbolNamePairs(String types) {
	Vector vect = new Vector();
	for(int i = 0; i < all_ops.length; i++)
	    vect.add(all_ops[i]);

	// go through all of our types and remove the ones we don't want
	for(int index=types.indexOf(' '); index != -1;index=types.indexOf(' ')){
	    String one_type = types.substring(0, index);
	    types           = types.substring(index +1);
	    SymbolNamePair pair = getSymbolNamePair(one_type);
	    vect.removeElement(pair);
	}
	// remove the last one
	SymbolNamePair pair = getSymbolNamePair(types);
	vect.removeElement(pair);

	// convert it to an array of SymbolNamePairs
	SymbolNamePair[] pairs = new SymbolNamePair[vect.size()];
	for(int i = 0; i < pairs.length; i++)
	    pairs[i] = (SymbolNamePair)vect.elementAt(i);
	return pairs;
    }

    /**
     * Looks through the s-expression for the subsymbol, and replace all
     * occurences of it with the replacement. If we made a replacement, return
     * the copied version. Otherwise, return ourself.
     */
    public static Symbol replace(Symbol s_expression, Symbol subsymbol, 
				 Symbol replacement) {
	// did we manage to make a replacement?
	boolean replaced = false;

	// go down and check our children first.
	for(int i = 0; i < s_expression.arity(); i++) {
	    Symbol rpl =replace(s_expression.getChild(i),subsymbol,replacement);
	    if(rpl != s_expression.getChild(i)) {
		s_expression.setChild(rpl, i);
		replaced = true;
	    }
	}

	// if we replaced one of our children, we cannot also replace ourself,
	// since we would be using a replacement as the basis for a new
	// replacement.
	if(replaced == false) {
	    if(s_expression.equals(subsymbol))
		return replacement.copy();
	}

	// no copy found... return ourself
	return s_expression;
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Searches through an s-expression and returns the nth Symbol that has a
     * child.
     */
    private Symbol getNonTerminal(Symbol s_expression, int n) {
	// perform breadth first search on the nth non-terminal symbol
	if(s_expression.arity() == 0)
	    return null;
	else {
	    LinkedList queue = new LinkedList();
	    Symbol child;
	    int i, arity;
	    queue.addLast(s_expression);
	    while( (s_expression = (Symbol)queue.removeFirst()) != null) {
		// we've found our symbol
		if(n == 0)
		    return s_expression;
		else {
		    for(i = 0, arity = s_expression.arity(); i < arity; i++) {
			child = s_expression.getChild(i);
			if(child.arity() != 0)
			    queue.addLast(child);
		    }
		}
		n--;
	    }
	    return null;
	}
    }

    /**
     * Searches through an s-expression and returns the nth symbol encountered
     */
    private Symbol getSymbol(Symbol s_expression, int n) {
	// perform breadth first search on the nth symbol
	LinkedList queue = new LinkedList();
	int i, arity;
	queue.addLast(s_expression);
	while( (s_expression = (Symbol)queue.removeFirst()) != null) {
	    if(n == 0)
		return s_expression;
	    else {
		for(i = 0, arity = s_expression.arity(); i < arity; i++)
		    queue.addLast(s_expression.getChild(i));
	    }
	    n--;
	}
	return null;
    }

    /**
     * Used by grow() and mutate() to make a random VariableSymbol
     */
    private VariableSymbol randVariableSymbol() {
	int var_num = randomizer.nextInt(variables.size());
	return new VariableSymbol((String)variables.elementAt(var_num));
    }

    /**
     * Used by grow() and mutate() to make a random ConstantSymbol
     */
    private ConstantSymbol randConstantSymbol() {
	// first, see if we're making constants or doubles 
	if(dbl_const == false) {
	    int val = (randomizer.nextInt((int)(max_const - min_const + 1)) + 
		       (int)min_const);
	    return new ConstantSymbol(val);
	}
	else {
	    double const_num = Stats.round((randomizer.nextDouble() *
					    (max_const-min_const) + min_const),
					   3);
	    return new ConstantSymbol(const_num);
	}
    }

    /**
     * Attach children to the Symbol passed in until we've grown to the
     * specified depth, or untl we've reached a terminal node
     */
    private Symbol grow(int depth) {
	Symbol symbol  = null;
	Symbol to_copy = null;

	// if we are at depth 1, make sure we return a terminal node
	if(depth == 1) {
	    int rand_num = randomizer.nextInt(terminals.size());
	    to_copy      = (Symbol)terminals.elementAt(rand_num);
	    // generally, there are two types of terminals. Variables and 
	    // Constants. Figure out which type we pulled out, and make a 
	    // randomized copy of it
	    if(to_copy instanceof VariableSymbol)
		symbol = randVariableSymbol();
	    else if(to_copy instanceof ConstantSymbol)
		symbol = randConstantSymbol();
	    // if we're here, there's some sort of custom terminal. 
	    // Just provide a copy of it.
	    else
		symbol = to_copy.copy();
	}
	else {
	    int rand_num = randomizer.nextInt(operators.size());
	    to_copy      = (Symbol)operators.elementAt(rand_num);
	    symbol       = to_copy.copy();
	    // now, fill up all of our children
	    for(int i = 0; i < symbol.arity(); i++)
		symbol.setChild(grow(depth-1), i); 
	}
	return symbol;
    }



    //*************************************************************************
    // classes
    //*************************************************************************
    /**
     * An exception that is thrown when we are trying to parse a malformed
     * s-expression
     */
    public static class MalformedSExpressionException extends Exception {
	public MalformedSExpressionException(String s) {
	    super(s);
	}
    }

    /**
     * contains a symbol:symbol-name pair
     */
    public static class SymbolNamePair implements java.io.Serializable  {
	private String name;
	private Symbol symbol;
	public SymbolNamePair(Symbol symbol, String name) {
	    this.name   = name;
	    this.symbol = symbol;
	}
	public Symbol getSymbol() {
	    return symbol;
	}
	public String getName() {
	    return name;
	}
	public String toString() {
	    return name;
	}
    }
}
