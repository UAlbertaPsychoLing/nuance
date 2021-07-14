//*****************************************************************************
//
// AdvancedSetup.java
//
// This is a window that allows the user to customize everything before running
// Nuance.
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
import  agent.s_expression.*;
import  agent.s_expression.SymbolicFactory.*;
import  islands.*;
import  mode.*;



public class AdvancedSetup extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private GuiNuance     parent; // the gui we're embedded in
    private JComboBox     ffuncs; // the types of fitness functions available
    private JComboBox     target; // the variable we're trying to predict
    private JComboBox    breeder; // which kind of selection strat do we use?
    private JComboBox const_type; // real numbers or integers?
    private JComboBox   interval; // interval arithmetic?
    private JComboBox       mode; // the mode we're running in
    private JSpinner        gens; // # of generations
    private JSpinner      noprog; // how long can we go w/o making progress?
    private JSpinner        runs; // # of runs
    private JSpinner     popsize; // # of population members
    private JSpinner   parsimony; // strength of parsimony pressure
    private JSpinner   ageweight; // ageweighting value
    private JSpinner    mutation; // mutation rate
    private JSpinner    minconst; // the minimum constant value allowed
    private JSpinner    maxconst; // the maximum constant value allowed
    private JSpinner     setsize; // how big is each subset?
    private JTextField   dataset; // the dataset we're using
    private JTextField    logdir; // the dir we're logging to
    private MoveSelectionPanel  opPanel; // a panel saying which ops we use
    private MoveSelectionPanel varPanel; // a panel for our variables
    private VariableSetProblem     prob; // the problem we're being run on
    private JCheckBox host = new JCheckBox("host service", false);
    
    // ver 3.2
    //private JTextField configFile; // the config file

    // some private constants we need
    private static final String[] interval_options = {
	"true",
	"false" };
    private static final int INTERVAL_FALSE = 0;
    private static final int INTERVAL_TRUE  = 1;

    private static final String[] consts = {
	"real numbers",
	"integers" };

    // their corresponding integer numbers
    private static final int CONST_REAL = 0;
    private static final int CONST_INT  = 1;

    private static final String[] breeders = {
	"greedy overselection",
	"median pass" };
    private static final int BREEDER_GREEDY_OVERSELECTION = 0;
    private static final int BREEDER_MEDIAN_PASS          = 1;

    private static final String[] modes = {
	"normal",
	"singleton",
	"pairwise" };
    private static final int MODE_NORMAL    = 0;
    private static final int MODE_SINGLETON = 1;
    private static final int MODE_PAIRWISE  = 2;

    // where we save/load our config set
    private static final String CONFIG_FILE = "config/advanced_config.txt";
    private static final String PREF_FILE   = "config/misc.txt";



    //*************************************************************************
    // constructors
    //*************************************************************************
    public AdvancedSetup(GuiNuance parent) {
	super(new BorderLayout());
	this.parent = parent;
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch(Exception e) { set = new ConfigSet(); }

	initComponents(set);
	this.add("Center", Utils.centerComp(arrangeComponents()));

	// make a panel for our start button and hosting option..
	JPanel sPanel = new JPanel(new BorderLayout());

	// add our start button
	JButton  start = new JButton("start");
	add("South", Utils.makeWrapper(start));
	start.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    run();
		}
	    });
	sPanel.add("Center", Utils.makeWrapper(start));
 	sPanel.add("East", Utils.makeWrapper(host, 
 					     new FlowLayout(FlowLayout.RIGHT)));
 	// add a button for save and load old configs
 	//JButton load_cfg = new JButton("load config");
 	//add("South", Utils.makeWrapper(load_cfg));
 	//sPanel.add("West", Utils.makeWrapper(load_cfg));
 	
	add("South", sPanel);
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    
    private ConfigSet populateConfigSet() {
    	// parse out our target
    	String target = (String)this.target.getSelectedItem();
    	
    	int minconst  = ((SpinnerNumberModel)this.minconst.getModel()).
    	    getNumber().intValue();
    	int maxconst  =	((SpinnerNumberModel)this.maxconst.getModel()).
    	    getNumber().intValue();
    	boolean reals = (consts[CONST_REAL].equals(const_type.getSelectedItem())
    			 ? true : false);


    	// build our factory
    	SymbolicFactory factory = new SymbolicFactory(3, minconst, maxconst, 
    						      reals);
    				
    	// add our operators
    	Object[] symbolNamePairs = opPanel.getSelectedItems();
    	for(int i = 0; i < symbolNamePairs.length; i++)
    	    factory.addSymbol(((SymbolNamePair)symbolNamePairs[i]).getSymbol());

    	// and variables
    	Object[] variables = varPanel.getSelectedItems();
    	for(int i = 0; i < variables.length; i++)
    	    factory.addVariable((String)variables[i]);

    	// build up our validation set and partition it out 
    	// from our full problem set
    	VariableSetProblem validation = null;
    	//***********
    	// FINISH ME
    	//***********

    	double  testpct  = ((SpinnerNumberModel)setsize.getModel()).
    	    getNumber().doubleValue();
        
    	// build up our test sets
    	boolean              retest  = (testpct != 1.0);
    	VariableSetProblem[] testSet = new VariableSetProblem[1];
    	testSet[0] = prob.copy();
    	testSet[0].setTestPct(testpct);

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

    	// make our fitness functions
    	FitnessFunction test_ffunc = null; // used for evolution
    	FitnessFunction      ffunc = null; // used for logging/validation
    	ffunc = TargetFitnessFactory.make((String)ffuncs.getSelectedItem(),
    					  target);

    	// now, assign the test ffunc, and all of the appropriate wrappers
    	test_ffunc = ffunc;
    	double parsimony = ((SpinnerNumberModel)this.parsimony.getModel()).
    	    getNumber().doubleValue();
    	double ageweight = ((SpinnerNumberModel)this.ageweight.getModel()).
    	    getNumber().doubleValue();
    	boolean interval = (interval_options[INTERVAL_TRUE].equalsIgnoreCase(
    	        (String)this.interval.getSelectedItem()) ? true : false);
    	if(parsimony != 0d)
    	    test_ffunc = new ParsimonyFitnessFunction(test_ffunc, parsimony);
    	if(ageweight != 0d)
    	    test_ffunc = new AgeWeightedFitnessFunction(test_ffunc, ageweight);
    	if(interval == true)
    	    test_ffunc = new IntervalFitnessFunction(test_ffunc, prob.copy());
    	
    	// stop conditions and population size
    	int runs = 
    	   ((SpinnerNumberModel)this.runs.getModel()).getNumber().intValue();
    	int gens = 
    	   ((SpinnerNumberModel)this.gens.getModel()).getNumber().intValue();
    	int pop  = 
    	   ((SpinnerNumberModel)this.popsize.getModel()).getNumber().intValue();
    	int noprog =
    	    ((SpinnerNumberModel)this.noprog.getModel()).getNumber().intValue();
    	
    	ConfigSet  set;
    	try {
    	    set = new ConfigSet();
    	} catch(Exception e) { set = new ConfigSet(); }
    	set.put("dataset",             dataset.getText());
    	set.put("log",                 logdir.getText());
    	set.put("target",              target);
    	set.put("setsize",             new Double(testpct));
    	set.put("population",          new Integer(pop));
    	set.put("generations",         new Integer(gens));
    	set.put("noprogress",          new Integer(noprog));
    	set.put("runs",                new Integer(runs));
    	set.put("ageweight",           new Double(ageweight));
    	set.put("parsimony",           new Double(parsimony));
    	set.put("fitness",             ffuncs.getSelectedItem());
    	set.put("min const",           new Integer(minconst));
    	set.put("max const",           new Integer(maxconst));
    	set.put("dbl const",           new Boolean(reals));
    	set.put("mutation",            new Double(mutation));
    	set.put("interval arithmetic", new Boolean(interval));
    	set.put("mode",                this.mode.getSelectedItem());
    	set.put("host",                new Boolean(this.host.isSelected()));
    		
    	
    	String operators = "";
    	Object selectedOps[] = opPanel.getSelectedItems();
    	System.out.println(selectedOps.length);
    	for(int i = 0; i < selectedOps.length; i++)
    	    operators += ((SymbolNamePair)selectedOps[i]).getSymbol().type()
    		+ (i < selectedOps.length - 1 ? " " : "");
    	set.put("operators", operators);

    	String varstrings = "";
    	for(int i = 0; i < variables.length; i++)
    	    varstrings += variables[i].toString() + 
    		(i < variables.length - 1 ? " " : "");
    	set.put("variables", varstrings);
    	
    	return set;
    }
    
    /**
     * read in values from all of our components, and then start the
     * appropriate mode
     */
    private void run() {
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

	// parse out our target
	
	String target = (String)this.target.getSelectedItem();
	
	int minconst  = ((SpinnerNumberModel)this.minconst.getModel()).
	    getNumber().intValue();
	int maxconst  =	((SpinnerNumberModel)this.maxconst.getModel()).
	    getNumber().intValue();
	boolean reals = (consts[CONST_REAL].equals(const_type.getSelectedItem())
			 ? true : false);


	// build our factory
	SymbolicFactory factory = new SymbolicFactory(3, minconst, maxconst, 
						      reals);
				
	// add our operators
	Object[] symbolNamePairs = opPanel.getSelectedItems();
	for(int i = 0; i < symbolNamePairs.length; i++)
	    factory.addSymbol(((SymbolNamePair)symbolNamePairs[i]).getSymbol());

	// and variables
	Object[] variables = varPanel.getSelectedItems();
	for(int i = 0; i < variables.length; i++)
	    factory.addVariable((String)variables[i]);

	// build up our validation set and partition it out 
	// from our full problem set
	VariableSetProblem validation = null;
	//***********
	// FINISH ME
	//***********

	double  testpct  = ((SpinnerNumberModel)setsize.getModel()).
	    getNumber().doubleValue();
    
	// build up our test sets
	boolean              retest  = (testpct != 1.0);
	VariableSetProblem[] testSet = new VariableSetProblem[1];
	testSet[0] = prob.copy();
	testSet[0].setTestPct(testpct);

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

	// make our fitness functions
	FitnessFunction test_ffunc = null; // used for evolution
	FitnessFunction      ffunc = null; // used for logging/validation
	ffunc = TargetFitnessFactory.make((String)ffuncs.getSelectedItem(),
					  target);

	// now, assign the test ffunc, and all of the appropriate wrappers
	test_ffunc = ffunc;
	double parsimony = ((SpinnerNumberModel)this.parsimony.getModel()).
	    getNumber().doubleValue();
	double ageweight = ((SpinnerNumberModel)this.ageweight.getModel()).
	    getNumber().doubleValue();
	boolean interval = (interval_options[INTERVAL_TRUE].equalsIgnoreCase(
	        (String)this.interval.getSelectedItem()) ? true : false);
	if(parsimony != 0d)
	    test_ffunc = new ParsimonyFitnessFunction(test_ffunc, parsimony);
	if(ageweight != 0d)
	    test_ffunc = new AgeWeightedFitnessFunction(test_ffunc, ageweight);
	if(interval == true)
	    test_ffunc = new IntervalFitnessFunction(test_ffunc, prob.copy());

	// build up our output window
	JTextArea area = new JTextArea();
	area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
	JScrollPane scroller = new JScrollPane(area);
	scroller.setEnabled(false);

	// stop conditions and population size
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

	// set all of the new values in our config set
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch(Exception e) { set = new ConfigSet(); }
	set.put("dataset",             dataset.getText());
	set.put("log",                 logdir.getText());
	set.put("target",              target);
	set.put("setsize",             new Double(testpct));
	set.put("population",          new Integer(pop));
	set.put("generations",         new Integer(gens));
	set.put("noprogress",          new Integer(noprog));
	set.put("runs",                new Integer(runs));
	set.put("ageweight",           new Double(ageweight));
	set.put("parsimony",           new Double(parsimony));
	set.put("fitness",             ffuncs.getSelectedItem());
	set.put("min const",           new Integer(minconst));
	set.put("max const",           new Integer(maxconst));
	set.put("dbl const",           new Boolean(reals));
	set.put("mutation",            new Double(mutation));
	set.put("interval arithmetic", new Boolean(interval));
	set.put("mode",                this.mode.getSelectedItem());
	set.put("host",                new Boolean(this.host.isSelected()));

	String operators = "";
	Object selectedOps[] = opPanel.getSelectedItems();
	for(int i = 0; i < selectedOps.length; i++)
	    operators += ((SymbolNamePair)selectedOps[i]).getSymbol().type()
		+ (i < selectedOps.length - 1 ? " " : "");
	set.put("operators", operators);

	String varstrings = "";
	for(int i = 0; i < variables.length; i++)
	    varstrings += variables[i].toString() + 
		(i < variables.length - 1 ? " " : "");
	set.put("variables", varstrings);
	
	// figure out the mode we're running in
	mode.Mode mode = null;
	
	// are we running in pNUANCE mode, or another mode?
	if(!host.isSelected()) {
	    // make our logger
	    Logger logger =
		new BasicLog(new PrintStream(new GuiOutputStream(area)), 
			     logdir.getText(), ffunc, prob.copy(), 1, 
			     new VariableSetReporter(prob.copy(), validation, 
						     set.get("target"), ffunc));

	    mode = new mode.BasicMode(breeder, test_ffunc, testSet,
				      retest,  logger, runs, pop, 
				      cond);

	    // check to see if we've selected a special mode
	    if(modes[MODE_PAIRWISE].equals(this.mode.getSelectedItem()))
		mode = new mode.PairwiseMode(factory, logger, mode);
	    else if(modes[MODE_SINGLETON].equals(this.mode.getSelectedItem()))
		mode = new mode.SingletonMode(factory, logger, mode);
	}
	// we're running in server mode...
	else {
	    // make our logger
	    PNuanceServerLog logger =
		new PNuanceServerLog(new PrintStream(new GuiOutputStream(area)),
			     logdir.getText(), ffunc, prob.copy(), 1, 
			     new VariableSetReporter(prob.copy(), validation, 
						     target, ffunc));

	    ModeGenerator generator = 
		new BasicModeGenerator(breeder, test_ffunc, testSet, retest,
				       runs, pop, cond);

	    // check to see if we've selected special modes...
	    if(modes[MODE_PAIRWISE].equals(this.mode.getSelectedItem()))
		generator = 
		    new PairwiseModeGenerator(factory, 
					      (BasicModeGenerator)generator);
	    else if(modes[MODE_SINGLETON].equals(this.mode.getSelectedItem()))
		generator = 
		    new SingletonModeGenerator(factory, 
					       (BasicModeGenerator)generator);

	    // make our island handler, and plop it into a pNUANCE mode
	    mode = new PNuanceServerMode(logger,
	        new IslandHandler(generator, logger, new RingIslandModel()));
	}

	// make a new nuance panel and swap it in
	NuancePanel np = new NuancePanel(mode);
	np.setLayout(new GridLayout(1, 0));
	np.add(scroller);

	// swap ourself out for the log window, and start up the nuance
	parent.swap(this, (new File(logdir.getText())).getName(), np);
	mode.start();

	// save our new config file
	set.write(CONFIG_FILE);
	set.write(logdir.getText() + File.separator + "config.txt");
    }

    /**
     * Query for a directory to log to with a JFileChooser
     */
    public void queryLogdir() {
	File logdir = 
	    new File(new File(this.logdir.getText()).getParentFile().exists() ?
		     new File(this.logdir.getText()).getParent() :
		     "log");
	if(!logdir.exists())
	    logdir.mkdir();
	JFileChooser fchooser =new JFileChooser(logdir.getAbsolutePath());
	fchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fchooser.setDialogType(JFileChooser.OPEN_DIALOG);
	fchooser.setMultiSelectionEnabled(false);
	int option = fchooser.showDialog(this, "Choose");
	if(option == JFileChooser.APPROVE_OPTION) {
	    this.logdir.setText(fchooser.getSelectedFile().getAbsolutePath());
	}
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
		buildVariableComps(prob);
	    }
	    catch(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage());
	    }
	}
    }

    
    /**
     * Pop up a new JFileChooser an ask for new file
     */
    private void loadConfig() {
    	/**
    	JFileChooser fchooser = new JFileChooser((new File(configFile.getText()).exists() ? 
    								new File(configFile.getText()).getParent() : 
    								System.getProperty("user.dir")));
    	**/
    	JFileChooser fchooser = new JFileChooser();
    	fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fchooser.setMultiSelectionEnabled(false);
    	
    	int option = fchooser.showDialog(this, "Choose");
    	if(option == JFileChooser.APPROVE_OPTION) {
    		File fileToLoad = fchooser.getSelectedFile();
    		ConfigSet set;
    		try {
    			set = new ConfigSet();
    			set.read(fileToLoad.getAbsolutePath());
    			System.out.println(set);
    			//System.out.println(set.get("generations"));
    			gens.setValue(Integer. parseInt(set.get("generations")));
    			noprog.setValue(Integer.parseInt(set.get("noprogress")));
    			runs.setValue(Integer.parseInt(set.get("runs")));
    			popsize.setValue(Integer.parseInt(set.get("population")));
    			parsimony.setValue(Double.parseDouble(set.get("parsimony")));
    			ageweight.setValue(Double.parseDouble(set.get("ageweight")));
    			mutation.setValue(Double.parseDouble(set.get("mutation")));
    			minconst.setValue(Integer.parseInt(set.get("min const")));
    			maxconst.setValue(Integer.parseInt(set.get("max const")));
    			setsize.setValue(Double.parseDouble(set.get("setsize")));
    			ffuncs.setSelectedItem(set.get("fitness"));
    			interval.setSelectedItem(set.get("interval arithmetic"));
    			mode.setSelectedItem(set.get("mode"));
    			target.setSelectedItem(set.get("target"));
    			dataset.setText(set.get("dataset"));
    			logdir.setText(set.get("log"));
    			
    			// our panel of selected symbols...
    			String operators = set.get("operators");
    			//System.out.println(operators);
    			if(operators == null) {
    			    operators = "+ - * / ln sqrt";
    			    opPanel = new MoveSelectionPanel(
    				    SymbolicFactory.getUnselectedSymbolNamePairs(operators),
    				    SymbolicFactory.getSymbolNamePairs(operators));
    			}
    			else
    				opPanel.loadContents(SymbolicFactory.getUnselectedSymbolNamePairs(operators),
    						SymbolicFactory.getSymbolNamePairs(operators));
    				
    			// reload variables
    			if(set.containsKey("variables")) {
    			    Object[] selected = varPanel.getSelectedItems();
    			    for(int i = 0; i < selected.length; i++) {
    			    	varPanel.removeItem(selected[i]);
    			    	varPanel.addUnselectedItem(selected[i]);
    			    }

    			    // now, add in only the variables we want selected
    			    String varlist = set.get("variables");
    			    for(int i = varlist.indexOf(' '); i != -1; i=varlist.indexOf(' ')) {
    				String one_var = varlist.substring(0, i);
    				varlist        = varlist.substring(i +1);
    					varPanel.removeItem(one_var);
    					varPanel.addSelectedItem(one_var);
    			    }
    			    // add our last one
    			    varPanel.removeItem(varlist);
    			    varPanel.addSelectedItem(varlist);
    			}
    			
    		}catch(Exception e) { e.printStackTrace(); }
    		
    	    //this.configFile.setText(fchooser.getSelectedFile().getAbsolutePath());
    	}
    }
    
    /**
     * Popup a new JFileChooser as the save process
     */
    private void saveConfig() {
		// TODO Auto-generated method stub
    	JFileChooser fchooser = new JFileChooser();
    	fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fchooser.setMultiSelectionEnabled(false);
    	
    	int option = fchooser.showDialog(this, "Save");
    	if (option == JFileChooser.APPROVE_OPTION) {
    		File fileToSave = fchooser.getSelectedFile();
    		ConfigSet set = populateConfigSet();
    		set.write(fileToSave.getAbsolutePath());
          
    	    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
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
	const_type = new JComboBox(consts);
	interval   = new JComboBox(interval_options);
	mode       = new JComboBox(modes);
	gens       = new JSpinner(new SpinnerNumberModel(1000, 0, 100000,  10));
	noprog     = new JSpinner(new SpinnerNumberModel(15,   0, 100000,   5));
	runs       = new JSpinner(new SpinnerNumberModel(10,   1,  10000,   1));
	popsize    = new JSpinner(new SpinnerNumberModel(1000, 10, 10000, 100));
	parsimony  = new JSpinner(new SpinnerNumberModel(0.002,0,  1,   0.001));
        parsimony.setEditor(new JSpinner.NumberEditor(parsimony, "#.##%"));
	ageweight  = new JSpinner(new SpinnerNumberModel(0,    0,  1,     0.1));
	ageweight.setEditor(new JSpinner.NumberEditor(ageweight, "#.##"));
	mutation   = new JSpinner(new SpinnerNumberModel(0,    0,  1,    0.01));
        mutation.setEditor(new JSpinner.NumberEditor(mutation, "#.##%"));
	minconst   = new JSpinner(new SpinnerNumberModel(0, -999,  999,     1));
	maxconst   = new JSpinner(new SpinnerNumberModel(1, -999,  999,     1));
	setsize    = new JSpinner(new SpinnerNumberModel(1.0,0.00000001,1,0.05));
	setsize.setEditor(new JSpinner.NumberEditor(setsize, "#.##%"));
	dataset    = new JTextField("datasets" + File.separator + "set.txt",20);
	dataset.setEditable(false);
	dataset.setEnabled(false);
	
	// ver 3.2
	//configFile = new JTextField("configs" + File.separator + "config.txt", 20);
	//configFile.setEditable(true);
	//configFile.setEnabled(true);
	
	logdir     = new JTextField("log" + File.separator + 
				    (new java.util.Date()).
				    toString().replaceAll(":", "."), 20);
	// we'll make them go through the JFileChooser, so we can
	// update the targets list
	logdir.setEditable(false);
	logdir.setEnabled(false);

	// a list of the ops we specifically want to use. null is all
	String operators = null;

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
	if(set.containsKey("parsimony"))
	    parsimony.setValue(new Double(set.get("parsimony")));
	if(set.containsKey("ageweight"))
	    ageweight.setValue(new Double(set.get("ageweight")));
	if(set.containsKey("mutation"))
	    mutation.setValue(new Double(set.get("mutation")));
	if(set.containsKey("setsize"))
	    setsize.setValue(new Double(set.get("setsize")));
	if(set.containsKey("min const"))
	    minconst.setValue(new Integer(set.get("min const")));
	if(set.containsKey("max const"))
	    maxconst.setValue(new Integer(set.get("max const")));
	if(set.containsKey("log"))
	    logdir.setText(set.get("log"));
	if(set.containsKey("dataset"))
	    dataset.setText(set.get("dataset"));
	if(set.containsKey("interval arithmetic"))
	    interval.setSelectedItem(set.get("interval arithmetic"));
	if(set.containsKey("dbl const"))
	    if(set.get("dbl const").equalsIgnoreCase("true"))
		const_type.setSelectedIndex(CONST_REAL);
	if(set.containsKey("mode"))
	    mode.setSelectedItem(set.get("mode"));
	if(set.containsKey("operators"))
	    operators = set.get("operators");
	if(set.containsKey("host"))
	    host.setSelected(Boolean.valueOf(set.get("host")).booleanValue());

	// our panel of selected symbols...
	if(operators == null) {
	    operators = "+ - * / ln sqrt";
	    opPanel = new MoveSelectionPanel(
		    SymbolicFactory.getUnselectedSymbolNamePairs(operators),
		    SymbolicFactory.getSymbolNamePairs(operators));
	}
	else
	    opPanel = new MoveSelectionPanel(
		    SymbolicFactory.getUnselectedSymbolNamePairs(operators),
		    SymbolicFactory.getSymbolNamePairs(operators));

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

	// do we have a specified list of variables we want to have selected?
	if(set.containsKey("variables")) {
	    Object[] selected = varPanel.getSelectedItems();
	    for(int i = 0; i < selected.length; i++) {
		varPanel.removeItem(selected[i]);
		varPanel.addUnselectedItem(selected[i]);
	    }

	    // now, add in only the variables we want selected
	    String varlist = set.get("variables");
	    for(int i = varlist.indexOf(' '); i != -1; i=varlist.indexOf(' ')) {
		String one_var = varlist.substring(0, i);
		varlist        = varlist.substring(i +1);
		varPanel.removeItem(one_var);
		varPanel.addSelectedItem(one_var);
	    }
	    // add our last one
	    varPanel.removeItem(varlist);
	    varPanel.addSelectedItem(varlist);
	}
    }

    /**
     * Arrange all of our components into a panel
     */
    private JPanel arrangeComponents() {
	// organize our data and operator panels on the right hand size
	JPanel rightPanel = new JPanel(new BorderLayout(5, 20));
	rightPanel.add("North",  makeLogPanel());
	rightPanel.add("Center", makeDataPanel());
	rightPanel.add("South",  makeOperatorPanel());

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
	spinners.add(parsimony);
	spinners.add(ageweight);
	spinners.add(mutation);
	spinners.add(setsize);
	spinners.add(minconst);
	spinners.add(maxconst);
	spinners.add(const_type);
	spinners.add(ffuncs);
	spinners.add(interval);
	spinners.add(breeder);
	spinners.add(mode);

	// our labels
	JPanel labels = new JPanel(new GridLayout(0, 1, 3, 3));
	labels.add(new JLabel("Max Generations", JLabel.RIGHT));
	labels.add(new JLabel("Max Gens w/o Change", JLabel.RIGHT));
	labels.add(new JLabel("Runs", JLabel.RIGHT));
	labels.add(new JLabel("Population Size", JLabel.RIGHT));
	labels.add(new JLabel("Parsimony Pressure", JLabel.RIGHT));
	labels.add(new JLabel("Age Weight", JLabel.RIGHT));
	labels.add(new JLabel("Mutation Rate", JLabel.RIGHT));
	labels.add(new JLabel("Subset Size", JLabel.RIGHT));
	labels.add(new JLabel("Minimum Constant", JLabel.RIGHT));
	labels.add(new JLabel("Maximum Constant", JLabel.RIGHT));
	labels.add(new JLabel("Constant Type", JLabel.RIGHT));
	labels.add(new JLabel("Fitness Function", JLabel.RIGHT));
	labels.add(new JLabel("Interval Arithmetic", JLabel.RIGHT));  
	labels.add(new JLabel("Selection Strategy", JLabel.RIGHT));
	labels.add(new JLabel("Running Mode", JLabel.RIGHT));

	// put our labels and spinners beside eachother 
	JPanel slp = new JPanel(new BorderLayout(5, 5));
	slp.add("West", labels);
	slp.add("East", spinners);
	
	// 3.2, add a jpanel for loading and saving configs
	JPanel cfg_panel = new JPanel(new BorderLayout(5,5));
	cfg_panel.add("North", new JLabel("Save or Load Evolution Parameters", JLabel.CENTER));
	//cfg_panel.add("North", configFile);
	
	// add a button for save and load old configs
	JButton load_cfg = new JButton("Load Eval Params");
	cfg_panel.add("West", Utils.makeWrapper(load_cfg));
	load_cfg.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    // File Chooser
			loadConfig();
		}
	    });
	
	JButton save_cfg = new JButton("Save Eval Params");
	cfg_panel.add("East", Utils.makeWrapper(save_cfg));
	save_cfg.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//Save file
			saveConfig();
	}
    });
	
	// now, put the panels side by side in the 
	// evolutionary parameters panel
	JPanel epp = new JPanel(new BorderLayout(5, 5));
	epp.add("North", new JLabel("Evolution Parameters", JLabel.CENTER));
	epp.add("South", slp);
	
	JPanel epp2 = new JPanel(new BorderLayout(5,5));
	epp2.add("North", epp);
	epp2.add("South", cfg_panel);
	
	return epp2;
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
	JPanel vlp = new JPanel(new BorderLayout(5, 0));
	vlp.add("North", new JLabel("Predictors and Categories",JLabel.CENTER));
	vlp.add("South", varPanel);

	// and put a header above this section
	JPanel dp = new JPanel(new BorderLayout(5, 5));
	dp.add("North", new JLabel("Dataset Information", JLabel.CENTER));
	dp.add("Center", dlp);
	dp.add("South", vlp);
	return dp;
    }

    /**
     * Makes the operator panel
     */
    private JPanel makeOperatorPanel() {
	JPanel op      = new JPanel(new BorderLayout(5, 0));
	op.add("North", new JLabel("Symbols", JLabel.CENTER));
	op.add("South", opPanel);
	return op;
    }
}
