//*****************************************************************************
//
// BasicSetup.java
//
// This is a window that only asks for basic questions when setting up NUANCE;
// problem mode, dataset, etc... all of the parameters are left to the program
// to figure out for itself.
//
//*****************************************************************************
package ui.gui;

// ui stuff
import  java.util.Date;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  java.io.File;

// gp stuff
import  problem.VariableSetProblem;
import  java.io.PrintStream;
import  fitness.*;
import  logger.*;
import  stopcondition.*;
import  agent.s_expression.SymbolicFactory;

public class BasicSetup extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private GuiNuance       parent = null;  // the gui we're adding ourself to
    private String          target = null;  // the target variable
    private VariableSetProblem set = null;  // our dataset
    private String          logdir = null;  // where are we logging info?
    private int               runs = 10;    // # runs we are doing
    private boolean       parallel = false; // parallel version?
    private boolean    categorical = false; // predicting categorical data?
    private int            islands = 0;     // if so, how many islands

    // some constants
    private static final int MIN_SIZE_FOR_VALIDATION = 200;



    //*************************************************************************
    // abstract classes we use here
    //*************************************************************************
    /**
     * This class uses JPanels that have ActionListeners for lots of things.
     * Most of the steps in the setup wizard implement this. The Action
     * listened for is a click on the "next" button.
     */
    abstract class ActionPanel extends JPanel implements ActionListener { }



    //*************************************************************************
    // constructors
    //*************************************************************************
    public BasicSetup(GuiNuance parent) {
	super(new BorderLayout());
	this.parent = parent;
	// add in our first panel
	addDatasetPanel();
    }



    //*************************************************************************
    // wizard functions
    //*************************************************************************
    /**
     * Sets the current contents to a panel that queries for the dataset.
     */
    private void addDatasetPanel() {
	setContent(new DatasetPanel());
    }

    /**
     * Adds a panel that queries for our target in the dataset
     */
    private void addTargetPanel() {
	setContent(new TargetPanel());
    }

    /**
     * Adds a panel that queries for our fitness function
     */
    private void addFitnessPanel() {
	setContent(new FitnessPanel());
    }

    /**
     * Adds in a panel that queries for the log directory we want to use
     */
    private void addLogPanel() {
	setContent(new LogPanel(), "start");
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Changes the content of our main window and refreshes the GUI
     */ 
    private void setContent(ActionPanel p) {
	setContent(p, "next");
    }

    /**
     * Changes the content of our main window and refreshes the GUI. Also
     * changes the name of our "next" button
     */ 
    private void setContent(ActionPanel p, String nextName) {
	// remove what we currently have
	removeAll();

	// add the new component
	add("Center", Utils.centerComp(p));

	// make our next button, and attach the action listener
	JButton next = new JButton(nextName);
	next.addActionListener(p);

	// put our "next" button in
	add("South", Utils.makeWrapper(next, new FlowLayout(FlowLayout.RIGHT)));

	// and refresh everything
	invalidate();
	validate();
    }

    /**
     * Takes in a VariableSet and applies some heuristics to figure out how
     * many sets we should use for averaged multitest fitness, and how big
     * each one should be during test
     */
    private VariableSetProblem[] buildTestsets(VariableSetProblem prob) {
	VariableSetProblem[] probs = new VariableSetProblem[1];
	probs[0]                   = prob.copy();
	// if we have a small dataset, use it all
	if(prob.size() < 200)
	    probs[0].setTestPct(1.0);
	// otherwise, only use ~100 elements
	else
	    probs[0].setTestPct(100.0 / prob.size());
	return probs;
    }

    /**
     * Take in all of our current values, and use them to start evolution
     */
    private void start() {
	// make our text window, add the scrollpane, and swap us for it
	// in the GuiNuance
	JTextArea area = new JTextArea();
	area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
	JScrollPane scroller = new JScrollPane(area);
	scroller.setEnabled(false);

	// build our factory
	SymbolicFactory factory = new SymbolicFactory();
	factory.addSymbol(new agent.s_expression.AddSymbol());
	factory.addSymbol(new agent.s_expression.MultiplySymbol());
	factory.addSymbol(new agent.s_expression.DivisionSymbol());
	factory.addSymbol(new agent.s_expression.SqrtSymbol());

	// if we have a categorical target, add some logical operators too
	if(categorical == true) {
	    factory.addSymbol(new agent.s_expression.LessSymbol());
	    factory.addSymbol(new agent.s_expression.EqualsSymbol());
	}

	// add in all of our variables to the factory
	for(java.util.Iterator it = set.variables(); it.hasNext();) {
	    String next = (String)it.next();
	    if(!next.equals(target))
		factory.addVariable(next);
	}

	// break our VariableSet into a couple sets depending on its size, and
	// select out a few values for a validation set if we have enough
	VariableSetProblem validation = null;
	if(set.size() > MIN_SIZE_FOR_VALIDATION) {
	    //***********
	    // FINISH ME 
	    //***********
	    // 1) select out 1/2 the variables
	    // 2) take the antiset and assign it to our current set
	}

	// make a full copy of our set for the logger
	VariableSetProblem logSet     = set.copy();
	VariableSetProblem testSets[] = buildTestsets(set);
	boolean retest = (testSets[0].getTestPct() != 1.0);

	// make our breeder
	breeder.Breeder breeder = 
	    new breeder.GreedyOverselectionBreeder(factory);

	// which fitness function are we using?
	FitnessFunction ffunc = null;
	if(categorical == true)
	    ffunc = new PctHitFitnessFunction(target);
	else
	    ffunc = new RSquaredFitnessFunction(target);

	// and give it a parsimony wrapper
	FitnessFunction real_ffunc = new ParsimonyFitnessFunction(ffunc, 0.002);

	// and always do interval arithmetic
	real_ffunc = new IntervalFitnessFunction(real_ffunc, set.copy());

	// make our logger and set its directory
	Logger logger =
	    new BasicLog(new PrintStream(new GuiOutputStream(area)), logdir,
			 ffunc, set.copy(), 1,
			 new VariableSetReporter(logSet, validation, target, 
						 ffunc));

	MultiStopCondition conds = new MultiStopCondition();
	conds.add(new ProgressStopCondition(15));

	// start up a BasicMode
	mode.Mode mode = new mode.BasicMode(breeder, real_ffunc, testSets, 
					    retest, logger, runs, 1000,
					    conds);

	// make a new nuance panel and swap it in
	NuancePanel np = new NuancePanel(mode);
	np.setLayout(new GridLayout(1, 0));
	np.add(scroller);

	// swap in our new panel
	parent.swap(this, (new File(logdir)).getName(), np);

	// start our nuance thread
	mode.start();
    }



    //*************************************************************************
    // class LogPanel extends ActionPanel
    //*************************************************************************
    /**
     * Asks the user for the directory we wish to log to
     */
    class LogPanel extends ActionPanel {
	private JTextField field  = new JTextField(
            "log" + java.io.File.separator + 
	    (new java.util.Date()).toString().replaceAll(":", "."));
	public LogPanel() {
	    setLayout(new BorderLayout());
	    JButton change = new JButton("change");
	    change.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			    queryLogdir();
		    }
		});
	    
	    // group the label, field, and change button together
	    JPanel p = new JPanel();
	    p.add(new JLabel("log directory", JLabel.RIGHT));
	    p.add(field);
	    p.add(change);
	    
	    // put a question above the panel, in us
	    JLabel q  = new JLabel("Where do you want to log results to?");
	    add("North", Utils.makeWrapper(q));
	    add("South", p);
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
		field.setText(fchooser.getSelectedFile().getAbsolutePath());
	}
	
	/**
	 * Make sure our log dir doesn't already exist, or make sure it is
	 * ok if we overwrite contents. Then, get out of the wizard and start
	 * actually running the program.
	 */
	public void actionPerformed(ActionEvent e) {
	    // check to see if our logdir exists. If it does, prompt to make 
	    // sure it is ok to overwrite anything in it
	    if((new File(field.getText())).exists()) {
		int option = JOptionPane.showConfirmDialog(
                this,
                "The log directory you specified already exists, and may\n" +
                "already contain data. Logging here may overwrite your\n" +
                "old data. Are you sure you wish to continue?"
		);
		if(option != JOptionPane.YES_OPTION)
		    return;            
	    }
	    // set the logdir
	    logdir = field.getText();
	    start();
	}
    }



    //*************************************************************************
    // class TargetPanel extends ActionPanel
    //*************************************************************************
    /**
     * Looks at all of the variables in our VariableSet, and queries the
     * user for one that we will be trying to predict.
     */
    class TargetPanel extends ActionPanel {
	private JComboBox list = new JComboBox();
	public TargetPanel() {
	    setLayout(new BorderLayout());
	    // add in all of our variables
	    for(java.util.Iterator it = set.variables(); it.hasNext(); )
		list.addItem(it.next());
	    add("North", Utils.makeWrapper(
		new JLabel("Which variable are you trying to predict?")));
	    add("South", Utils.makeWrapper(list));
	}

	/**
	 * Set the target to our currently selected list item
	 */
	public void actionPerformed(ActionEvent e) {
	    target = list.getSelectedItem().toString();
	    // everything is OK. Switch to our next panel
	    addFitnessPanel();
	}
    }



    //*************************************************************************
    // class FitnessPanel extends ActionPanel
    //*************************************************************************
    /**
     * Queries the user for the type of fitness function s/he wishes to use
     */
    class FitnessPanel extends ActionPanel {
	private String[] options = {"Yes", "No"};
	private JComboBox   list = new JComboBox(options);
	public FitnessPanel() {
	    setLayout(new BorderLayout());
	    add("North", Utils.makeWrapper(
	        new JLabel("Are you predicting a categorical variable?")));
	    add("South", Utils.makeWrapper(list));
	}

	/**
	 * see which item is selected, and set it as your fitness function
	 */
	public void actionPerformed(ActionEvent e) {
	    categorical = (list.getSelectedItem().equals("Yes") ? true : false);
	    // go onto our next panel
	    addLogPanel();
	}
    }



    //*************************************************************************
    // class DatasetPanel extends ActionPanel
    //*************************************************************************
    /**
     * A panel that queries for a dataset we will be using
     */
    class DatasetPanel extends ActionPanel {
	private JTextField dataset = new JTextField("datasets/set.txt", 30);
	public DatasetPanel() {
	    setLayout(new BorderLayout());
	    JButton change = new JButton("change");
	    change.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			    queryDataset();
		    }
		});
	    
	    // group the label, field, and change button together
	    JPanel p = new JPanel();
	    p.add(new JLabel("dataset", JLabel.RIGHT));
	    p.add(dataset);
	    p.add(change);
	    
	    // put a question above the panel, in us
	    JLabel q  = new JLabel("Which dataset do you want to load?");
	    add("North", Utils.makeWrapper(q));
	    add("South", p);
	}
	
	/**
	 * Query for a dataset to open with a JFileChooser
	 */
	public void queryDataset() {
	    JFileChooser fchooser =
		new JFileChooser(System.getProperty("user.dir"));
	    fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fchooser.setMultiSelectionEnabled(false);
	    if(JFileChooser.APPROVE_OPTION == fchooser.showOpenDialog(this))
		dataset.setText(fchooser.getSelectedFile().getAbsolutePath());
	}
	
	/**
	 * Make sure our dataset exists and parses OK. Then, swap to
	 * the panel for choosing our target
	 */
	public void actionPerformed(ActionEvent e) {
	    // first things first. Make sure our datafile exists
	    if(!(new File(dataset.getText())).exists()) {
		JOptionPane.showMessageDialog(
		     this, "The dataset you specified does not exist.");
		return;
	    }
	    try {
		set = VariableSetProblem.parse(dataset.getText());
	    }
	    catch(Exception ex) {
		JOptionPane.showMessageDialog(this, ex.getMessage());
		return;
	    }

	    // everything is OK. Switch to our next panel
	    addTargetPanel();
	}
    }
}
