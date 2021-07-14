//*****************************************************************************
//
// NeuralNetSetup.java
//
// This is the interface for setting up NUANCE to run with neural networks.
//
//*****************************************************************************
package ui.gui;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.border.*;
import  config.*;
import  fitness.*;
import  java.io.*;
import  logger.*;
import  problem.*;
import  breeder.*;
import  stopcondition.*;
import  agent.ann.*;


public class NeuralNetSetup extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private GuiNuance      parent; // the gui we're embedded in
    private JComboBox      ffuncs; // the types of fitness functions available
    private JComboBox      target; // the variable we're trying to predict
    private JComboBox     breeder; // which kind of selection strat do we use?
    private JComboBox  activation; // what type of activation functions?
    private JSpinner         gens; // # of generations
    private JSpinner       noprog; // how long can we go w/o making progress?
    private JSpinner         runs; // # of runs
    private JSpinner      popsize; // # of population members
    private JSpinner     mutation; // mutation rate
    private JSpinner    minweight; // the minimum starting weight
    private JSpinner    maxweight; // the maximum starting weight
    private JSpinner hidden_units; // number of hidden units per layer
    private JSpinner       layers; // the number of layers
    private JSpinner      subsets; // how many subsets for amtf?
    private JSpinner      setsize; // how big is each subset?
    private JCheckBox        host; // are we a host?
    private JTextField    dataset; // the dataset we're using
    private JTextField     logdir; // the dir we're logging to
    private MoveSelectionPanel varPanel; // a panel for our variables
    private VariableSetProblem     prob; // the problem we're being run on

    // some private constants we need
    private static final String[] breeders = {
	"greedy overselection",
	"median pass" };
    private static final int BREEDER_GREEDY_OVERSELECTION = 0;
    private static final int BREEDER_MEDIAN_PASS          = 1;

    private static final String[] activation_funcs = {
	"sigmoid",
	"gaussian" };
    private static final int ACTIVATION_SIGMOID           = 0;
    private static final int ACTIVATION_GAUSSIAN          = 1;

    // where we save/load our config set
    private static final String CONFIG_FILE = "config/neural_config.txt";



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NeuralNetSetup(GuiNuance parent) {
	super(new BorderLayout());
	this.parent = parent;
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch(Exception e) { set = new ConfigSet(); }

	initComponents(set);
	this.add("Center", Utils.centerComp(arrangeComponents()));

	// add our start button
	JButton start = new JButton("start");
	add("South", Utils.makeWrapper(start));
	start.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    start();
		}
	    });
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * read in values from all of our components, and then start the
     * appropriate mode
     */
    private void start() {
	// check to make sure that the variable set exists
	if(prob == null) {
	    JOptionPane.showMessageDialog(this,
		"You have not chosen a dataset yet. Please do that first",
		"Click OK", JOptionPane.INFORMATION_MESSAGE);
	    return;
	}

	// check to see if our logdir exists. If it does, prompt to make 
	// sure it is ok to overwrite anything in it
	if((new File(logdir.getText())).exists()) {
	    int option = JOptionPane.showConfirmDialog(
                this,
                "The log directory you specified already exists, and may\n" +
                "already contain data. Logging here may overwrite your\n" +
                "old data. Are you sure you wish to continue?"
		);
	    if(option != JOptionPane.YES_OPTION)
		return;
	}

	// build up our validation set and partition it out 
	// from our full problem set
	VariableSetProblem validation = null;
	//***********
	// FINISH ME
	//***********

	int num_testsets = ((SpinnerNumberModel)subsets.getModel()).
	    getNumber().intValue();
	double  testpct  = ((SpinnerNumberModel)setsize.getModel()).
	    getNumber().doubleValue();
    
	// build up our test sets
	VariableSetProblem testSets[] = new VariableSetProblem[num_testsets];
	boolean retest  = (testpct != 1.0);
	for(int i = 0; i < testSets.length; i++) {
	    testSets[i] = prob.copy();
	    testSets[i].setTestPct(testpct);
	}

	// parse out our target
	String target = (String)this.target.getSelectedItem();

	// make our fitness functions
	FitnessFunction test_ffunc = null; // used for evolution
	FitnessFunction      ffunc = null; // used for logging/validation
	ffunc = TargetFitnessFactory.make((String)ffuncs.getSelectedItem(),
					  target);

	// now, assign the test ffunc, and all of the appropriate wrappers
	test_ffunc = ffunc;

	// figure out our activation function
	ActivationFunction aFunc = getActivation(activation.getSelectedIndex());

	// figure out values to construct networks from
	int hidden_units = ((SpinnerNumberModel)this.hidden_units.getModel()).
	    getNumber().intValue();
	int layers       = ((SpinnerNumberModel)this.layers.getModel()).
	    getNumber().intValue();
	
	// build our skeleton network
	NeuralNetwork net   = new NeuralNetwork();

	// make input units for each of our variables
	Object[] variables  = varPanel.getSelectedItems();
	NeuralNode inputs[] = new NeuralNode[variables.length];
	for(int i = 0; i < inputs.length; i++) {
	    inputs[i] = new NeuralNode(aFunc);
	    net.add(inputs[i]);
	}

	// make our hidden units
	NeuralNode hunits[][] = new NeuralNode[layers][hidden_units];
	for(int i = 0; i < layers; i++) {
	    for(int j = 0; j < hidden_units; j++) {
		hunits[i][j] = new NeuralNode(aFunc);
		net.add(hunits[i][j]);
	    }
	}

	// make our output node
	NeuralNode out      = new NeuralNode(aFunc);
	net.add(out);

	// assign all of the input units names
	for(int i = 0; i < inputs.length; i++)
	    net.setInput((String)variables[i], inputs[i]);

	// assign all of our hidden units to their layer
	for(int i = 0; i < layers; i++)
	    for(int j = 0; j < hidden_units; j++)
		net.setLayer(hunits[i][j], i+1);

	// assign our output node
	net.setOutput(target, out);
	
	// connect all of our input nodes to the first hidden unit layer
	for(int i = 0; i < inputs.length; i++)
	    for(int j = 0; j < hidden_units; j++)
		net.connect(inputs[i], hunits[0][j]);

	// connect all of our layers together
	for(int i = 0; i < layers-1; i++)
	    for(int j = 0; j < hidden_units; j++)
		for(int k = 0; k < hidden_units; k++)
		    net.connect(hunits[i][j], hunits[i+1][k]);
	
	// connect all of our hidden units in the last layer to the output node
	for(int i = 0; i < hidden_units; i++)
	    net.connect(hunits[layers-1][i], out);

	// figure out range for starting weights
	int minweight  = ((SpinnerNumberModel)this.minweight.getModel()).
	    getNumber().intValue();
	int maxweight  = ((SpinnerNumberModel)this.maxweight.getModel()).
	    getNumber().intValue();

	// build our factory
	NeuralFactory factory = new NeuralFactory(net, target, 
						  minweight, maxweight);

	// make our breeder
	Breeder breeder = null;
	if(this.breeder.getSelectedIndex() == BREEDER_GREEDY_OVERSELECTION)
	    breeder = new GreedyOverselectionBreeder(factory);
	else if(this.breeder.getSelectedIndex() == BREEDER_MEDIAN_PASS)
	    breeder = new MedianPassBreeder(factory);
	else; // oops! error... we'll crash.

	// check to see if we have mutation
	double mutation = ((SpinnerNumberModel)this.mutation.getModel()).
	    getNumber().doubleValue();
	if(mutation != 0d)
	    breeder = new MutationBreeder(breeder, factory, mutation);

	// build up our output window
	JTextArea area = new JTextArea();
	area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
	JScrollPane scroller = new JScrollPane(area);
	scroller.setEnabled(false);

	// make our logger
	Logger logger =
	    new BasicLog(new PrintStream(new GuiOutputStream(area)), 
			 logdir.getText(), ffunc, prob.copy(), 1, 
			 new VariableSetReporter(prob.copy(), validation, 
						 target, ffunc));

	// start up a BasicMode
	int runs = 
	   ((SpinnerNumberModel)this.runs.getModel()).getNumber().intValue();
	int gens = 
	   ((SpinnerNumberModel)this.gens.getModel()).getNumber().intValue();
	int pop  = 
	   ((SpinnerNumberModel)this.popsize.getModel()).getNumber().intValue();
	int noprog =
	    ((SpinnerNumberModel)this.noprog.getModel()).getNumber().intValue();

	// make a vector of our stopping conditions
	MultiStopCondition cond = new MultiStopCondition();
	if(gens > 0)
	    cond.add(new GenerationStopCondition(gens));
	if(noprog > 0)
	    cond.add(new ProgressStopCondition(noprog));

	mode.Mode mode = new mode.BasicMode(breeder, test_ffunc, testSets,
					    retest,  logger, runs, pop, 
					    cond);

	// set all of the new values in our config set
	ConfigSet set = new ConfigSet();
	set.put("dataset",             dataset.getText());
	set.put("log",                 logdir.getText());
	set.put("target",              target);
	set.put("subsets",             new Integer(num_testsets));
	set.put("setsize",             new Double(testpct));
	set.put("population",          new Integer(pop));
	set.put("generations",         new Integer(gens));
	set.put("noprogress",          new Integer(noprog));
	set.put("runs",                new Integer(runs));
	set.put("fitness",             ffuncs.getSelectedItem());
	set.put("min weight",          new Integer(minweight));
	set.put("max weight",          new Integer(maxweight));
	set.put("hidden units",        new Integer(hidden_units));
	set.put("layers",              new Integer(layers));
	// display size here
	set.put("mutation",            new Double(mutation));
	set.put("activation",          activation.getSelectedItem());

	set.write(CONFIG_FILE);
	set.write(logdir.getText() + File.separator + "config.txt");

	// make a new nuance panel and swap it in
	NuancePanel np = new NuancePanel(mode);
	np.setLayout(new GridLayout(1, 0));
	np.add(scroller);

	// swap ourself out for the log window, and start up the nuance
	parent.swap(this, (new File(logdir.getText())).getName(), np);
	mode.start();
    }

    /**
     * Returns the activation function assocciated with the given name
     */
    private ActivationFunction getActivation(String name) {
	if(name.equalsIgnoreCase(activation_funcs[ACTIVATION_SIGMOID]))
	    return new SigmoidActivation();
	else if(name.equalsIgnoreCase(activation_funcs[ACTIVATION_GAUSSIAN]))
	    return new GaussianActivation();
	else
	    return null;
    }

    /**
     * Returns the activation function associated with the given number
     */
    private ActivationFunction getActivation(int val) {
	return getActivation(activation_funcs[val]);
    }

    /**
     * Query for a directory to log to with a JFileChooser
     */
    public void queryLogdir() {
	File logdir = new File("log");
	if(!logdir.exists())
	    logdir.mkdir();
	JFileChooser fchooser =new JFileChooser(logdir.getAbsolutePath());
	fchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fchooser.setDialogType(JFileChooser.OPEN_DIALOG);
	fchooser.setMultiSelectionEnabled(false);
	int option = fchooser.showDialog(this, "Choose");
	if(option == JFileChooser.APPROVE_OPTION)
	    this.logdir.setText(fchooser.getSelectedFile().getAbsolutePath());
    }

    /**
     * Pop up a new JFileChooser and ask for a new VariableSetProblem. Check
     * to see if it has an OK form before we allow the change.
     */
    private void queryDataset() {
	JFileChooser fchooser =
	    new JFileChooser(System.getProperty("user.dir"));
	fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fchooser.setMultiSelectionEnabled(false);

	// we selected something... see if it parses OK
	if(JFileChooser.APPROVE_OPTION == fchooser.showOpenDialog(this)) {
	    try {
		prob = VariableSetProblem.parse(
		     fchooser.getSelectedFile().getAbsolutePath());
		dataset.setText(fchooser.getSelectedFile().getAbsolutePath());
		buildVariableComps(prob);
	    }
	    catch(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage());
	    }
	}
    }

    /**
     * Takes in a VariableSetProblem and builds our variable and target lists
     * out of it
     */
    private void buildVariableComps(VariableSetProblem problem) {
	Object[] predictors = new Object[problem.numVariables()];
	target.removeAllItems();
	// build our target and predictors list
	int i = 0;
	for(java.util.Iterator it = problem.variables(); it.hasNext(); i++) {
	    predictors[i] = it.next();
	    target.addItem(predictors[i]);
	}
	varPanel.setContents(null, predictors);

	// remove our selected item from the target list, 
	// and add it to the unselected portion
	if(predictors.length > 0 && target.getSelectedItem() != null) {
	    varPanel.removeItem(target.getSelectedItem());
	    varPanel.addUnselectedItem(target.getSelectedItem());
	}
    }

    /**
     * Initialize all of our components with the values found in the given
     * ConfigSet
     */
    private void initComponents(ConfigSet set) {
	// make all of the stuff with our real defaults
	varPanel   = new MoveSelectionPanel(null, null, 3);
	target     = new JComboBox();
	breeder    = new JComboBox(breeders);
	ffuncs     = new JComboBox(TargetFitnessFactory.fitnessFuncs);
	activation = new JComboBox(activation_funcs);
	gens       = new JSpinner(new SpinnerNumberModel(1000, 0, 100000,  10));
	noprog     = new JSpinner(new SpinnerNumberModel(15,   0, 100000,   5));
	runs       = new JSpinner(new SpinnerNumberModel(10,   1,  10000,   1));
	popsize    = new JSpinner(new SpinnerNumberModel(1000, 10, 10000, 100));
	mutation   = new JSpinner(new SpinnerNumberModel(0,    0,  1,    0.01));
        mutation.setEditor(new JSpinner.NumberEditor(mutation, "#.##%"));
	minweight  = new JSpinner(new SpinnerNumberModel(-10, -999,  999,   1));
	maxweight  = new JSpinner(new SpinnerNumberModel( 10, -999,  999,   1));
	hidden_units=new JSpinner(new SpinnerNumberModel(  2,    1,99999,   1));
	layers     = new JSpinner(new SpinnerNumberModel(  1,  1,   1000,   1));
	subsets    = new JSpinner(new SpinnerNumberModel(10,   1,  999,     1));
	setsize   =new JSpinner(new SpinnerNumberModel(0.05,0.00000001,1,0.05));
	setsize.setEditor(new JSpinner.NumberEditor(setsize, "#.##%"));
	host       = new JCheckBox("act as server for pNUANCE",          false);
	dataset    = new JTextField("datasets" + File.separator + "set.txt",20);
	dataset.setEditable(false);
	dataset.setEnabled(false);
	logdir     = new JTextField("log" + File.separator + 
				    (new java.util.Date()).
				    toString().replaceAll(":", "."), 20);
	// we'll make them go through the JFileChooser, so we can
	// update the targets list
	logdir.setEditable(false);
	logdir.setEnabled(false);

	// load in our defaults from the config set
	if(set.containsKey("fitness"))
	    ffuncs.setSelectedIndex(
                TargetFitnessFactory.getNum(set.get("fitness")));
	if(set.containsKey("generations"))
	    gens.setValue(new Integer(set.get("generations")));
	if(set.containsKey("noprogress"))
	    noprog.setValue(new Integer(set.get("noprogress")));
	if(set.containsKey("runs"))
	    runs.setValue(new Integer(set.get("runs")));
	if(set.containsKey("population"))
	    popsize.setValue(new Integer(set.get("population")));
	if(set.containsKey("mutation"))
	    mutation.setValue(new Double(set.get("mutation")));
	if(set.containsKey("subsets"))
	    subsets.setValue(new Integer(set.get("subsets")));
	if(set.containsKey("setsize"))
	    setsize.setValue(new Double(set.get("setsize")));
	if(set.containsKey("min weight"))
	    minweight.setValue(new Integer(set.get("min weight")));
	if(set.containsKey("max weight"))
	    maxweight.setValue(new Integer(set.get("max weight")));
	if(set.containsKey("hidden units"))
	    hidden_units.setValue(new Integer(set.get("hidden units")));
	if(set.containsKey("layers"))
	    layers.setValue(new Integer(set.get("layers")));
	if(set.containsKey("log"))
	    logdir.setText(set.get("log"));
	if(set.containsKey("dataset"))
	    dataset.setText(set.get("dataset"));
	if(set.containsKey("activation"))
	    activation.setSelectedItem(set.get("activation"));

	// set up our target JComboBox
	if((new File(dataset.getText())).exists()) {
	    try {
		prob = VariableSetProblem.parse(dataset.getText());
	    }
	    catch(Exception e) {
		prob = new VariableSetProblem();
		dataset.setText("");
	    }
	    buildVariableComps(prob);
	}
    }


    /**
     * Arrange all of our components into a panel
     */
    private JPanel arrangeComponents() {
	// organize our data and operator panels on the right hand size
	JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
	rightPanel.add("North",  makeLogPanel());
	rightPanel.add("Center", makeDataPanel());

	// organize our evolutionary parameters
	JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
	leftPanel.add("North", makeEPPanel());

	// the final panel... organize everything
	JPanel p = new JPanel(new BorderLayout(5, 5));
	p.add("West",  Utils.makeWrapper(leftPanel));
	p.add("East",  Utils.makeWrapper(rightPanel));

	return p;
    }

    /**
     * Makes the evolutionary parameters panel
     */
    private JPanel makeEPPanel() {
	// one panel for our spinners. They'll be beside labels
	JPanel spinners = new JPanel(new GridLayout(0, 1, 3, 3));
	spinners.add(gens);
	spinners.add(noprog);
	spinners.add(runs);
	spinners.add(popsize);
	spinners.add(mutation);
	spinners.add(subsets);
	spinners.add(setsize);
	spinners.add(minweight);
	spinners.add(maxweight);
	spinners.add(hidden_units);
	spinners.add(layers);
	spinners.add(activation);
	spinners.add(ffuncs);
	spinners.add(breeder);

	// our labels
	JPanel labels = new JPanel(new GridLayout(0, 1, 3, 3));
	labels.add(new JLabel("Max Generations", JLabel.RIGHT));
	labels.add(new JLabel("Max Gens w/o Change", JLabel.RIGHT));
	labels.add(new JLabel("Runs", JLabel.RIGHT));
	labels.add(new JLabel("Population Size", JLabel.RIGHT));
	labels.add(new JLabel("Mutation Rate", JLabel.RIGHT));
	labels.add(new JLabel("Number of Subsets", JLabel.RIGHT));
	labels.add(new JLabel("Subset Size", JLabel.RIGHT));
	labels.add(new JLabel("Minimum Weight", JLabel.RIGHT));
	labels.add(new JLabel("Maximum Weight", JLabel.RIGHT));
	labels.add(new JLabel("Hidden Units per Layer", JLabel.RIGHT));
	labels.add(new JLabel("Layers", JLabel.RIGHT));
	labels.add(new JLabel("Activation Function", JLabel.RIGHT));
	labels.add(new JLabel("Fitness Function", JLabel.RIGHT));
	labels.add(new JLabel("Selection Strategy", JLabel.RIGHT));

	// put our labels and spinners beside eachother 
	JPanel slp = new JPanel(new BorderLayout(5, 5));
	slp.add("West", labels);
	slp.add("East", spinners);
	
	// now, put the panels side by side in the 
	// evolutionary parameters panel
	JPanel epp = new JPanel(new BorderLayout(5, 5));
	epp.add("North", new JLabel("Evolution Parameters", JLabel.CENTER));
	epp.add("South", slp);
	
	return epp;
    }

    /**
     * Makes the log panel
     */
    private JPanel makeLogPanel() {
	// build up our panel for choosing the log directory
	JButton logChooser   = new JButton("choose");
	JPanel  lChoosePanel = new JPanel();
	lChoosePanel.add(logdir);
	lChoosePanel.add(logChooser);
	logChooser.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    queryLogdir();
		}
	    });

	JPanel labels = new JPanel(new GridLayout(0, 1, 3, 3));
	labels.add(new JLabel("Log Directory", JLabel.RIGHT));

	// organize our log panel beside its label
	JPanel llp = new JPanel(new BorderLayout(5, 5));
	llp.add("West", labels);
	llp.add("East", lChoosePanel);

	// add a header above our log panel
	JPanel lp = new JPanel(new BorderLayout(5, 5));
	lp.add("North", new JLabel("Log Information", JLabel.CENTER));
	lp.add("South", llp);
	return lp;
    }

    /**
     * Makes the data panel
     */
    private JPanel makeDataPanel() {
	// build up the panel for handling dataset stuff
	JButton dataChooser = new JButton("choose");
	JPanel    dataPanel = new JPanel(new GridLayout(0, 1, 3, 3));
	JPanel dChoosePanel = new JPanel(new FlowLayout());
	dChoosePanel.add(dataset);
	dChoosePanel.add(dataChooser);
	dataPanel.add(dChoosePanel);
	dataPanel.add(Utils.makeWrapper(target, new FlowLayout(FlowLayout.LEFT)));

	// add our datachooser's action listener
	dataChooser.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    queryDataset();
		}
	    });

	// now, make the labels for the dataPanel
	JPanel labels = new JPanel(new GridLayout(0, 1, 3, 3));
	labels.add(new JLabel("Dataset", JLabel.RIGHT));
	labels.add(new JLabel("Target Varable", JLabel.RIGHT));

	// now, put our data panel and labels beside eachother
	JPanel dlp = new JPanel(new BorderLayout(5, 5));
	dlp.add("West", labels);
	dlp.add("East", dataPanel);

	// and put our variables into a panel, with a title
	JPanel vlp = new JPanel(new BorderLayout(5, 5));
	vlp.add("North", new JLabel("Predictors", JLabel.CENTER));
	vlp.add("South", varPanel);

	// and put a header above this section
	JPanel dp = new JPanel(new BorderLayout(5, 5));
	dp.add("North", new JLabel("Dataset Information", JLabel.CENTER));
	dp.add("Center", dlp);
	dp.add("South", vlp);
	return dp;
    }
}
