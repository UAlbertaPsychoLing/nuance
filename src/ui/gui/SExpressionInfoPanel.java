//*****************************************************************************
//
// SExpressionInfoPanel.java
//
// Pulls up a panel where people can paste equations, provide a dataset, and
// some descriptive info will be generated for them about the equation.
//
//*****************************************************************************
package ui.gui;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  java.io.File;
import  problem.*;
import  numerics.*;
import  agent.s_expression.*;
import  agent.s_expression.SymbolicFactory.*;
import  config.*;
import  java.util.*;
public class SExpressionInfoPanel extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private JTextField    s_expression; // the expression we're running
    private JTextArea           output; // where we send info to
    private VariableSetProblem    prob; // the problem we're running on
    private JTextField         dataset; // the file our problem is in
    private JComboBox           target; // which variable are we predicting
    private JCheckBox  do_interactions; // display the interaction matrices?
    private JCheckBox     do_estimates; // display targets vs. estimates?
    private JCheckBox    do_individual; // individual variable summaries
    private JCheckBox     do_notations; // display function in other notations
    private JCheckBox       do_summary; // display summary stats of function

    // all of our summary stats
    private double estimates[];
    private double   targets[];
    private double   intercept;
    private double       slope;
    private double correlation;
    private double         mse;
    private double      pcthit;

    // static variables
    private static final int dec = 3;
    private static final String CONFIG_FILE = "config/evaluator.txt";



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SExpressionInfoPanel() {
	setLayout(new BorderLayout(5, 5));
	JButton dataChoose = new JButton("choose");
	JButton      start = new JButton("evaluate");
	do_notations       = new JCheckBox("notations", true);
	do_interactions    = new JCheckBox("interaction matrices", true);
	do_estimates       = new JCheckBox("function estimates", true);
	do_summary         = new JCheckBox("summary statistics", true);
	s_expression       = new JTextField("", 30);
	output             = new JTextArea();
	dataset    = new JTextField("datasets" + File.separator + "set.txt",30);
	target     = new JComboBox();
	dataset.setEditable(false);
	dataset.setEnabled(false);

	// open up our config set
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch(Exception e) {
	    set = new ConfigSet();
	}



	// Organize all of our checkbox options
	JPanel     cPanel = new JPanel(new BorderLayout());
	JPanel checkboxes = new JPanel(new GridLayout(0, 1));
	cPanel.add("North", new JLabel("Display Options",JLabel.CENTER));
	cPanel.add("Center", Utils.makeWrapper(checkboxes));
	checkboxes.add(Utils.makeWrapper(do_notations,
					 new FlowLayout(FlowLayout.LEFT)));
	checkboxes.add(Utils.makeWrapper(do_summary,
					 new FlowLayout(FlowLayout.LEFT)));
	checkboxes.add(Utils.makeWrapper(do_estimates,
					 new FlowLayout(FlowLayout.LEFT)));
	//	checkboxes.add(Utils.makeWrapper(do_individual, 
	//				 new FlowLayout(FlowLayout.LEFT)));
	checkboxes.add(Utils.makeWrapper(do_interactions, 
					 new FlowLayout(FlowLayout.LEFT)));

	// organize our dataset field beside our chooser
	JPanel dPanel = new JPanel(new BorderLayout());
	dPanel.add("West", Utils.makeWrapper(dataset));
	dPanel.add("East",   Utils.makeWrapper(dataChoose));

	// for having our labels beside our fields
	JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
	labelPanel.add(new JLabel("Function", JLabel.RIGHT));
	labelPanel.add(new JLabel("Dataset",  JLabel.RIGHT));
	labelPanel.add(new JLabel("Target",   JLabel.RIGHT));

	// for organizing our fields
	JPanel fieldPanel = new JPanel(new GridLayout(0, 1, 5, 5));
	fieldPanel.add(Utils.makeWrapper(s_expression, 
					 new FlowLayout(FlowLayout.LEFT)));
	fieldPanel.add(dPanel);
	fieldPanel.add(Utils.makeWrapper(target, 
					 new FlowLayout(FlowLayout.LEFT)));

	// join our fields and labels
	JPanel jfPanel = new JPanel(new BorderLayout());
	jfPanel.add("West",   labelPanel);
	jfPanel.add("Center", fieldPanel);

	// make a scroller around our textarea
	JScrollPane scroller = new JScrollPane(output);
	scroller.setEnabled(true);

	// put the scroller in a panel and add some borders around it
	JPanel sPanel = new JPanel(new BorderLayout(5, 5));
	sPanel.add("Center", scroller);
	sPanel.add("North",  Utils.makeWrapper(start));

	// a filler panel to partition our west and east panels with a line
	JPanel filler = new JPanel(new GridLayout(0, 1));
	filler.add(new JLabel(" "));
	filler.setBackground(Color.BLACK);

	// organize the components into panels
	JPanel nPanel = new JPanel(new BorderLayout(5, 5));
	nPanel.add("West",   Utils.makeWrapper(jfPanel));
	nPanel.add("Center", filler);
	nPanel.add("East",   cPanel);

	add("North",  Utils.makeWrapper(nPanel));
	add("Center", sPanel);

	// now, do all of our action listeners
	dataChoose.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    queryDataset();
		}
	    });
	start.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    start();
		}
	    });

	// read in our config settings
	if(set.containsKey("notations"))
	    do_notations.setSelected(Boolean.valueOf(set.get("notations")).
				     booleanValue());
	if(set.containsKey("summary stats"))
	    do_summary.setSelected(Boolean.valueOf(set.get("summary stats")).
				   booleanValue());
	if(set.containsKey("estimates"))
	    do_estimates.setSelected(Boolean.valueOf(set.get("estimates")).
				     booleanValue());
	if(set.containsKey("interactions"))
	   do_interactions.setSelected(Boolean.valueOf(set.get("interactions")).
				       booleanValue());
	if(set.containsKey("dataset")) {
	    dataset.setText(set.get("dataset"));
	    // set up our target JComboBox
	    if((new File(dataset.getText())).exists()) {
		try {
		    prob = VariableSetProblem.parse(dataset.getText());
		}
		catch(Exception e) {
		    prob = new VariableSetProblem();
		    dataset.setText("");
		}
		buildTargetList(prob);
	    }
	    if(set.containsKey("target"))
		target.setSelectedItem(set.get("target"));
	}
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Parse our s-expression and run it on the dataset provided
     */
    private void start() {
	// make sure our s-expression parses properly
	Symbol symbol;
	try {
	    symbol = SymbolicFactory.parse(s_expression.getText());
	} catch(MalformedSExpressionException e) {
	    JOptionPane.showMessageDialog(this, e.getMessage());
	    return;
	}

	// make sure our dataset exists
	if(prob == null) {
	    JOptionPane.showMessageDialog(
		this, "You have not yet specified a dataset.");
	    return;
	}

	// we'll need this for evaluating...
	String target = (String)this.target.getSelectedItem();

	// figure out all of our relevant values...
	estimates = new double[prob.size()];
	targets   = new double[prob.size()];
	pcthit    = 0;

	// iterate across all of our problems, and evaluate ourselves on them
	int i = 0;
	for(java.util.Iterator it = prob.iterator(); it.hasNext(); i++) {
	    VariableProblem p = (VariableProblem)it.next();
	    estimates[i]      = symbol.eval(p);
	    targets[i]        = ((Number)p.get(target)).doubleValue();
	    pcthit           += (estimates[i] == targets[i] ? 1 : 0);
	}

	pcthit     /= prob.size();
	intercept   = Stats.intercept(targets, estimates);
	slope       = Stats.slope(targets, estimates);
	correlation = Stats.correlation(targets, estimates);
	mse         = Stats.scaledSSE(targets, estimates) / prob.size();


	// clear our output window, and start appending stuff to it
	output.setText("");

	if(do_notations.isSelected())
	    displayNotations(symbol);

	if(do_summary.isSelected())
	    displaySummary(symbol);

	if(do_estimates.isSelected())
	    displayEstimates(symbol);

	// now, we want to build 10x10 matrices of all the interactions. We
	// have to use a copy of the s-expression for the hand-built variable
	// sets.
	if(do_interactions.isSelected())
	    displayInteractions(symbol, 10);

	// save our settings
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch(Exception e) {
	    set = new ConfigSet();
	}

	set.put("notations",     new Boolean(do_notations.isSelected()));
	set.put("summary stats", new Boolean(do_summary.isSelected()));
	set.put("estimates",     new Boolean(do_estimates.isSelected()));
	set.put("interactions",  new Boolean(do_interactions.isSelected()));
	set.put("dataset",       dataset.getText());
	set.put("target",        target);
	set.write(CONFIG_FILE);

	// clean up our mess
	System.gc();
    }


    /**
     * Displays target, estimate, and error values
     */
    private void displayEstimates(Symbol symbol) {
	output.append(target.getSelectedItem() + 
		      "\tESTIMATE\tSQUARED ERROR\n");
	for(int i = 0; i < estimates.length; i++) {
	    double scaled_est = intercept + estimates[i]*slope;
	    output.append("" + 
			  Stats.round(targets[i], dec) + "\t" + 
			  Stats.round(scaled_est, dec) + "\t" +
			  Stats.round(Math.pow(targets[i]-scaled_est, 2),dec) +
			  "\n");
	}
	output.append("\n\n\n");
    }


    /**
     * Displays a bunch of useful information about the relationship between
     * the equation and the target value
     */
    private void displaySummary(Symbol symbol) {
	output.append("y-intercept\t" + Stats.round(intercept, dec)   + "\n" +
		      "slope\t"       + Stats.round(slope, dec)       + "\n" +
		      "correlation\t" + Stats.round(correlation, dec) + "\n" +
		      "r-squared\t"   + Stats.round(Math.pow(correlation, 2), 
						    dec) + "\n" +
		      "MSE\t"         + Stats.round(mse, dec)         + "\n" +
		      "pct hit\t"     + Stats.round(pcthit,dec)       + "\n" +
		      "n\t"           + prob.size()                   + "\n" +
		      "\n\n\n");
    }


    /**
     * Displays the function in all of its possible notations
     */
    private void displayNotations(Symbol symbol) {
	output.append("FUNCTION NOTATIONS\n" +
		      "PREFIX NOTATION:\n" + symbol  +"\n\n" +
		      "INFIX NOTATION:\n" + infix(symbol) + "\n\n" +
		      "LATEX NOTATION:\n" + latex(symbol) + "\n\n" +
		      "MATHEMATICA NOTATION:\n" + mathematica(symbol) + "\n\n" +
		      "\n\n\n");
    }

    /**
     * Return the function in a infix-notation style
     */
    private String infix(Symbol symbol) {
	if(symbol.arity() == 2 && !Character.isLetter(symbol.type().charAt(0)))
	     return "(" + infix(symbol.getChild(0)) + " " + symbol.type() + 
		 " " + infix(symbol.getChild(1)) + ")";
	else if(symbol.arity() == 0)
	    return symbol.type();
	else {
	    String string = "(" + symbol.type();
	    for(int i = 0; i < symbol.arity(); i++)
		string += " " + infix(symbol.getChild(i));
	    return string + ")";
	}
    }

    /**
     * Return the functio in a mathematica-notation style
     */
    private String mathematica(Symbol symbol) {
	// check all of our special cases... terminal nodes are themselves
	if(symbol.arity() == 0)
	    return symbol.type();
	// if it's an operator, do infix notation
	else if(symbol.arity() == 2 && 
		!Character.isLetter(symbol.type().charAt(0)))
	    return ("(" + mathematica(symbol.getChild(0)) + " " + symbol.type()+
		    " " + mathematica(symbol.getChild(1)) + ")");
	// logical operators need to be handled a bit wierdly
	else if(symbol.type().equals("=") || symbol.type().equals("<")) {
	    String operator = (symbol.type().equals("=") ? " == " : " < ");
	    return "If[" + 
		mathematica(symbol.getChild(0)) + operator +
		mathematica(symbol.getChild(1)) + ", "   +
		mathematica(symbol.getChild(2)) + ", "   +
		mathematica(symbol.getChild(3)) + "]";
	}
	// assume it's an operator, and display it in a functional form with
	// our head letter capitalized.
	else {
            String string = (Character.toUpperCase(symbol.type().charAt(0)) +
			     symbol.type().substring(1) + "[");
            for(int i = 0; i < symbol.arity()-1; i++)
                string += mathematica(symbol.getChild(i)) + ", ";
            string += mathematica(symbol.getChild(symbol.arity()-1)) + "]";
            return string;
	}
    }

    /**
     * Return the function in a latex-notation style
     */
    private String latex(Symbol symbol) {
	// check all of our special cases...
	if(symbol.arity() == 0)
	    return symbol.type();
	else if(symbol.type().equals("/"))
	    return "\\frac{" + latex(symbol.getChild(0)) + 
		"}{" + latex(symbol.getChild(1)) + "}";
	else if(symbol.type().equals("abs"))
	    return "\\left|" + latex(symbol.getChild(0)) + "\\right|";
	else if(symbol.type().equals("cbrt"))
	    return "\\sqrt[3]{" + latex(symbol.getChild(0)) + "}";
	else if(symbol.type().equals("sqrt"))
	    return "\\sqrt{" + latex(symbol.getChild(0)) + "}";
	else if(symbol.type().equals("antiln"))
	    return "e^{" + latex(symbol.getChild(0)) + "}";
	else if(symbol.type().equals("square")) {
	    String child = latex(symbol.getChild(0));
	    if(child.indexOf(' ') == -1 && 
	       !symbol.getChild(0).type().equals("square"))
		return child + "^2";
	    else
		return "\\left(" + child + "\\right)" + "^2";
	}
	else if(symbol.type().equals("cube")) {
	    String child = latex(symbol.getChild(0));
	    if(child.indexOf(' ') == -1 && 
	       !symbol.getChild(0).type().equals("cube"))
		return child + "^3";
	    else
		return "\\left(" + child + "\\right)" + "^3";
	}
	// check for operators
	if(symbol.arity() == 2 && !Character.isLetter(symbol.type().charAt(0))){
	    String child1 = latex(symbol.getChild(0));
	    String child2 = latex(symbol.getChild(1));
	    return ((child1.indexOf(' ') == -1 ?
		     child1 : "\\left(" + child1 + "\\right)") +
		    " " + symbol.type() + " " +
		    (child2.indexOf(' ') == -1 ?
		     child2 : "\\left(" + child2 + "\\right)"));
	}
	else if(symbol.arity() == 1) {
	    String child = latex(symbol.getChild(0));
	    return ("\\" + symbol.type() + "{" +
		    (child.indexOf(' ') == -1 ?
		     child : "\\left(" + child + "\\right)") + "}");
	}
	else if(symbol.type().equals("<") || symbol.type().equals("=")) {
	    return
		"\\left\\{\\begin{array}{1 1}" +
		latex(symbol.getChild(2)) + 
		" & \\quad \\mbox{" + 
		latex(symbol.getChild(0)) + 
		" $" + symbol.type() + "$ " + 
		latex(symbol.getChild(1)) + "} \\\\ " +
		latex(symbol.getChild(3)) + 
		" & \\quad \\mbox{otherwise} \\\\ \\end{array} \\right.";
	}
	else
	    return "UNKNOWN SYMBOL TYPE";
    }

    /**
     * Plots out the relationship all the variables.
     */
    private void displayInteractions(Symbol symbol, int granularity) {
	String variables[]   = new String[prob.numVariables()];
	SummaryStats stats[] = new SummaryStats[prob.numVariables()];
	double estimates[]   = new double[prob.size()];
	SummaryStats grand_stats;

	// collect all of our variable names
	java.util.Iterator it = prob.variables();
	for(int i = 0; it.hasNext(); i++)
	    variables[i] = (String)it.next();
	
	// now, collect averages, mins, and maxes for all of our variables.
	for(int i = 0; i < variables.length; i++)
	    stats[i] = prob.summaryStats(variables[i]);

	// collect our estimates over all the problems
	it = prob.iterator();
	for(int i = 0; it.hasNext(); i++)
	    estimates[i] = symbol.eval((VariableProblem)it.next());
	grand_stats = new SummaryStats(estimates);


	// go through all of our variable pairs and test them
	for(int i = 0; i < variables.length-1; i++) {
	    if(!symbol.contains(variables[i]))
		continue;
	    for(int j = i+1; j < variables.length; j++) {
		if(!symbol.contains(variables[j]))
		    continue;

		VariableProblem p = new VariableProblem();
		// add in all of our averages
		for(int k = 0; k < variables.length; k++)
		    p.put(variables[k], new Double(stats[k].getMean()));
		
		// don't forget to reset our variable symbols...
		reset(symbol);
		double plot[][] = 
		    plot(symbol, p, variables[i], variables[j], 
			 stats[i].getRange(), stats[j].getRange(), granularity);

		// append general info
		output.append("VARIABLE\tMIN\tAVG\tMAX\n");
		output.append(variables[i] + "\t" +
			      Stats.round(stats[i].getMin(),  dec) + "\t" +
			      Stats.round(stats[i].getMean(), dec) + "\t" +
			      Stats.round(stats[i].getMax(),  dec) + "\t" + 
			      "\n");
		output.append(variables[j] + "\t" +
			      Stats.round(stats[j].getMin(),  dec) + "\t" +
			      Stats.round(stats[j].getMean(), dec) + "\t" +
			      Stats.round(stats[j].getMax(),  dec) + "\t" + 
			      "\n");
		output.append("\n");


		// append values for the first variable
		output.append("\t");
		for(int k = 0; k <= granularity; k++)
		    output.append(variables[i] + "=" +
				  Stats.round(stats[i].getMin() +
					      (stats[i].getMax() - 
					       stats[i].getMin()) * 
					      k / granularity, dec) + "\t");
		output.append("\n");


		// print out all of the values
		for(int k = 0; k < plot.length; k++) {
		    output.append(variables[j] + "=" +
				  Stats.round(stats[j].getMin() +
					      (stats[j].getMax() -
					       stats[j].getMin()) *
					      k / granularity, dec) + "\t");
		    for(int l = 0; l < plot[k].length; l++)
			output.append(Stats.round((intercept + 
						   slope*plot[k][l]),dec)+"\t");
		    output.append("\n");
		}
		output.append("\n\n");
	    }
	}
	output.append("\n\n\n");
    }


    /**
     * Returns an NxN plot of the estimates the symbol provides on the problem,
     * while incrementing the values of the variables. Granularity determines
     * the number of steps in the plot (i.e. it is N-1).
     */
    private double[][] plot(Symbol symbol, VariableProblem p, 
			    String var1, String var2, 
			    Range var1range, Range var2range, int granularity) {
	double[][] matrix = new double[granularity+1][granularity+1];
	
	// fill up the matrix
	for(int i = 0; i <= granularity; i++) {
	    double step = ((var1range.max()-var1range.min())*i)/granularity;
	    p.put(var1, new Double(var1range.min() + step));
	    for(int j = 0; j <= granularity; j++) {
		double step2 =((var2range.max()-var2range.min())*j)/granularity;
		p.put(var2, new Double(var2range.min() + step2));
		matrix[i][j] = symbol.eval(p);
	    }
	}

	return matrix;
    }


    /**
     * Resets all of the VariableSymbols in the s-expression
     */
    private void reset(Symbol symbol) {
	if(symbol instanceof VariableSymbol)
	    ((VariableSymbol)symbol).reset();
	else
	    for(int i = 0; i < symbol.arity(); i++)
		reset(symbol.getChild(i));
    }

    /**
     * build all of the bitstrings up to length n. n must be >= 1
     */
    private boolean[][] bitstrings(int n) {
	if(n == 0)
	    return null;
	else if(n == 1) {
	    boolean strings[][] = new boolean[2][1];
	    strings[0][0]       = false;
	    strings[1][0]       = true;
	    return strings;
	}
	else {
	    boolean substrings[][] = bitstrings(n-1);
	    boolean strings[][]    = new boolean[substrings.length*2][n];
	    for(int i = 0; i < substrings.length; i++) {
		for(int j = 0; j < substrings[i].length; j++) {
		    strings[i][j]                     = substrings[i][j];
		    strings[i + substrings.length][j] = substrings[i][j];
		}
		strings[i][n-1]                     = false;
		strings[i + substrings.length][n-1] = true;
	    }
	    return strings;
	}
    }

    /**
     * Odd name for what it does, but couldn't think of anything better.
     * Returns true IF AND ONLY IF, 
     *   for each i, array1[i] == array2[i] OR array2[i] = false
     */
    private boolean doBoolsMatch(boolean[] array1, boolean[] array2) {
	for(int i = 0; i < array1.length; i++)
	    if(array1[i] != array2[i] && array2[i] == true)
		return false;
	return true;
    }

    /**
     * Figure out which main effects and which interactions are accounting
     * for all of the variance
     */
    private void varianceBreakdown(Symbol symbol) {
	Vector vars = new Vector(); // vector of the variables in our symbol
	final int MAX_VARS_FOR_BREAKDOWN = 6;

	// go through all of our variables, and figure out which ones we use
	for(Iterator it = prob.variables(); it.hasNext();) {
	    String var = (String)it.next();
	    if(symbol.contains(var))
		vars.addElement(var);
	}

	// if we have too many variables to do a breakdown (due to time/memory
	// constraints) then just exit out
	if(vars.size() > MAX_VARS_FOR_BREAKDOWN) {
	    output.append("Too many variables to perform a breakdown.\n\n");
	    return;
	}

	// figure out our target and estimated values
	String target = (String)this.target.getSelectedItem();
	double estimates[] = new double[prob.size()];
	double   targets[] = new double[prob.size()];

	Iterator it = prob.iterator();
	for(int i = 0; i < estimates.length; i++) {
	    VariableProblem p = (VariableProblem)it.next();
	    estimates[i] = symbol.eval(p);
	    targets[i]   = ((Number)p.get(target)).doubleValue();
	}

	// figure out the total amount of variance we account for
	double grand_rsqrd = Math.pow(Stats.correlation(targets, estimates), 2);

	// build all the combinations of our variables
	boolean strings[][] = bitstrings(vars.size());
	int     bit_count[] = new int[strings.length];

	// figure out how many bits are set on each string
	for(int i = 0; i < bit_count.length; i++) {
	    bit_count[i] = 0;
	    for(int j = 0; j < strings[i].length; j++)
		if(strings[i][j])
		    bit_count[i]++;
	}

	// build up all of our r-squared values. Can be done through dynamic
	// programming to make us go much faster if speed becomes an issue
	double  rsqrd[] = new double[strings.length];
	rsqrd[0] = 0; // the case when we have no variables... always 0

	// calculate all of our r-squared values, subtracting any of our
	// lower-order interactions within the variable set we're working with
	for(int len = 1; len <= vars.size(); len++) {
	    for(int i = 1; i < rsqrd.length; i++) {
		// skip us... we'll come back later
		if(bit_count[i] != len)
		    continue;

		// figure out the strength of our interaction when we hold all
		// variables except for our target ones constant
		Symbol newsym = symbol.copy();
		for(int j = 0; j < strings[i].length; j++) {
		    if(strings[i][j] == false) {
			String var = (String)vars.elementAt(j);
			newsym = SymbolicFactory.replace(newsym, 
				new VariableSymbol(var),
				new ConstantSymbol(prob.summaryStats(var).
						    getMean()));
		    }
		}
	    
		// evaluate the new symbol on the problem
		it = prob.iterator();
		for(int j = 0; j < estimates.length; j++) {
		    VariableProblem p = (VariableProblem)it.next();
		    estimates[j] = newsym.eval(p);
		}
	    
		// calculate our variance accounted for
		rsqrd[i] = Math.pow(Stats.correlation(targets, estimates), 2);
	    
		// now subtract out main effects and lower-order interactions
		// embedded in the r-squared value we just calculated
		for(int j = 0; j < bit_count.length; j++) {
		    if(bit_count[j] < bit_count[i] && 
		       doBoolsMatch(strings[i], strings[j])) {
			// subtract out our variance from this subinteraction
			rsqrd[i] -= rsqrd[j];
		    }
		}
	    }
	}


	// print all of this crap out
	for(int i = 0; i < vars.size(); i++)
	    output.append(vars.elementAt(i).toString() + "\t");
	output.append("R^2\tR\n");

	// print them in order of main effect, 2-way interaction, 3-way, etc...
	for(int i = 1; i <= strings.length; i++) {
	    for(int j = 0; j < strings.length; j++) {
		if(bit_count[j] != i)
		    continue;
		for(int k = 0; k < strings[j].length; k++)
		    output.append((strings[j][k] ? "true" : "----") + "\t");
		output.append("" + Stats.round(rsqrd[j], 5) + "\t" +
			      Stats.round((rsqrd[j] > 0 ? 
					   Math.sqrt(rsqrd[j]) : 0), 5) + "\n");
	    }
	}

	// we've made a bit of mess. clean it up
	System.gc();
    }


    /**
     * Pop up a new JFileChooser and ask for a new VariableSetProblem. Check
     * to see if it has an OK form before we allow the change.
     */
    private void queryDataset() {
	JFileChooser fchooser =
	    new JFileChooser((new File(dataset.getText()).exists() ?
			      new File(dataset.getText()).getParent() :
			      System.getProperty("user.dir")));
	fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fchooser.setMultiSelectionEnabled(false);

	// we selected something... see if it parses OK
	if(JFileChooser.APPROVE_OPTION == fchooser.showOpenDialog(this)) {
	    try {
		prob = VariableSetProblem.parse(
		     fchooser.getSelectedFile().getAbsolutePath());
		dataset.setText(fchooser.getSelectedFile().getAbsolutePath());
		buildTargetList(prob);
	    }
	    catch(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage());
	    }
	}
    }

    /**
     * Reconstructs the contents of our target list, given the Problem
     */
    private void buildTargetList(VariableSetProblem prob) {
	Object[] predictors = new Object[prob.numVariables()];
	target.removeAllItems();
	// build our target and predictors list
	for(java.util.Iterator it = prob.variables(); it.hasNext();)
	    target.addItem(it.next());
    }
}
