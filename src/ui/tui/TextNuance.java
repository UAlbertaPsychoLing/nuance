//*****************************************************************************
//
// TextNuance.java
//
// TextNuance is a version of NUANCE that runs in the console window. The other
// user interface is GuiNuance, for a graphical version.
//
//*****************************************************************************
package ui.tui;
import  mode.*;
import  fitness.*;
import  logger.*;
import  breeder.*;
import  mode.*;
import  stopcondition.*;
import  islands.*;
import  ui.NuanceUI;
import  config.ConfigSet;
import  problem.Problem;
import  problem.VariableSetProblem;
import  agent.s_expression.SymbolicFactory;
import  java.util.Vector;
public class TextNuance implements NuanceUI {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Mode mode; // our runmode

    private static String CONFIG_FILE = "config/advanced_config.txt";



    //*************************************************************************
    // constructors
    //*************************************************************************
    public TextNuance(String connection) {
	// construct our logger and mode
	Logger logger = new PNuanceClientLogger(System.out);
	mode = new PNuanceClientMode(logger, connection);
    }

    public TextNuance() {
	//*********************************************************************
	// first, set our defaults
	//*********************************************************************
	VariableSetProblem dataset    = null;
	String logdir  = "log" + java.io.File.separator + 
	    (new java.util.Date()).toString().replaceAll(":", ".");
	String fname      = "dataset.txt";
	String target     = null;
	String fitness    = "hitrate";
	String selection  = "greedy overselection";
	String modename   = "normal";
	int subsets       = 1;
	double setsize    = 1;
	boolean retest    = true;
	boolean interval  = false;
	int popsize       = 1000;
	int gens          = 1000;
	int noprog        = 15;
	int runs          = 10;
	int depth         = 3;
	double min_const  = 0;
	double max_const  = 1;
	boolean dbl_const = true;
	double ageweight  = 0;
	double parsimony  = 0.002;
	double mutation   = 0;
	int displaysize   = 1;
	String operators  = null;
	boolean host      = false;

	//*********************************************************************
	// now parse our config values
	//*********************************************************************
	ConfigSet set;
	try {
	    set = new ConfigSet(CONFIG_FILE);
	} catch( Exception e) { set = new ConfigSet(); }

	if(set.containsKey("dataset"))
	    fname  = set.get("dataset");
	if(set.containsKey("log"))     
	    logdir = set.get("log");
	if(set.containsKey("target"))  
	    target = set.get("target");
	if(set.containsKey("subsets")) 
	    subsets = Integer.parseInt(set.get("subsets"));
	if(set.containsKey("setsize")) 
	    setsize = Double.parseDouble(set.get("setsize"));
	if(set.containsKey("population")) 
	    popsize = Integer.parseInt(set.get("population"));
	if(set.containsKey("generations"))
	    gens = Integer.parseInt(set.get("generations"));
	if(set.containsKey("noprogress"))
	    noprog = Integer.parseInt(set.get("noprogress"));
	if(set.containsKey("runs"))
	    runs = Integer.parseInt(set.get("runs"));
	if(set.containsKey("ageweight"))
	    ageweight = Double.parseDouble(set.get("ageweight"));
	if(set.containsKey("parsimony"))
	    parsimony = Double.parseDouble(set.get("parsimony"));
	if(set.containsKey("fitness"))
	    fitness = set.get("fitness");
	if(set.containsKey("depth"))
	    depth = Integer.parseInt(set.get("depth"));
	if(set.containsKey("min const"))
	    min_const = Double.parseDouble(set.get("min const"));
	if(set.containsKey("max const"))
	    max_const = Double.parseDouble(set.get("max const"));
	if(set.containsKey("dbl const"))
	    dbl_const = Boolean.valueOf(set.get("dbl const")).booleanValue();
	if(set.containsKey("display size"))
	    displaysize = Integer.parseInt(set.get("display size"));
	if(set.containsKey("selection"))
	    selection = set.get("selection");
	if(set.containsKey("mutation"))
	    mutation = Double.parseDouble(set.get("mutation"));
	if(set.containsKey("mode"))
	    modename = set.get("mode");
	if(set.containsKey("interval arithmetic"))
	    interval = Boolean.valueOf(set.get("interval arithmetic")).
		booleanValue();
	if(set.containsKey("operators"))
	    operators = set.get("operators");
	if(set.containsKey("host"))
	    host = Boolean.valueOf(set.get("host")).booleanValue();

	

	//*********************************************************************
	// get our dataset and break it up into subsets
	//*********************************************************************
	try { dataset = VariableSetProblem.parse(fname); }
	catch(Exception e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
	dataset.setTestPct(setsize);
	Problem[] problems = new Problem[subsets];
	for(int i = 0; i < problems.length; i++)
	    problems[i] = dataset.copy();

	if(setsize == 1)
	    retest = false;

	//*********************************************************************
	// build our factory
	//*********************************************************************
	SymbolicFactory factory = new SymbolicFactory(depth, min_const, 
						      max_const, dbl_const);
	SymbolicFactory.SymbolNamePair usedSymbols[] = factory.all_ops;
	// there's some specific ones we want to use...
	if(operators != null) 
	    usedSymbols = factory.getSymbolNamePairs(operators);
	for(int i = 0; i < usedSymbols.length; i++)
	    factory.addSymbol(usedSymbols[i].getSymbol());


	// we've picked variables explicitly
	if(set.containsKey("variables")) {
	    Vector    vars = new Vector();
	    String varlist = set.get("variables");
	    for(int i = varlist.indexOf(' '); i != -1; i=varlist.indexOf(' ')) {
		String one_var = varlist.substring(0, i);
		varlist        = varlist.substring(i +1);
		if(!vars.contains(one_var))
		    vars.addElement(one_var);		
	    }
	    // add our last one
	    if(!vars.contains(varlist))
		vars.addElement(varlist);

	    // add them all to our factory
	    for(int i = 0; i < vars.size(); i++)
		factory.addVariable((String)vars.elementAt(i));
	}
	// use all variables
	else {
	    for(java.util.Iterator it = dataset.variables(); it.hasNext();) {
		String val = (String) it.next();
		if(!target.equalsIgnoreCase(val))
		    factory.addVariable(val);
	    }
	}


	//*********************************************************************
	// get our fitness function
	//*********************************************************************
	FitnessFunction rawFitness = null;
	rawFitness = TargetFitnessFactory.make(fitness, target);
	
	// if we are age-weighting or using parsimony pressure, set it
	FitnessFunction ffunc = rawFitness;
	if(ageweight > 0 && ageweight < 1)
	    ffunc = new AgeWeightedFitnessFunction(ffunc, ageweight);
	if(parsimony > 0)
	    ffunc = new ParsimonyFitnessFunction(ffunc, parsimony);
	if(interval == true)
	    ffunc = new IntervalFitnessFunction(ffunc, dataset.copy());

	//*********************************************************************
	// set up our logger
	//*********************************************************************
	VariableSetProblem reporterSet = dataset.copy();
	reporterSet.setTestPct(1d);
	reporterSet.prepare();
	Reporter rep = new VariableSetReporter(reporterSet, target, rawFitness);

	//*********************************************************************
	// set up our breeder
	//*********************************************************************
	Breeder breeder = null;
	if(selection.equalsIgnoreCase("median pass"))
	    breeder = new MedianPassBreeder(factory);
	else if(selection.equalsIgnoreCase("greedy overselection"))
	    breeder = new GreedyOverselectionBreeder(factory, 0.2, 0.8, 0.2);

	if(mutation > 0)
	    breeder = new MutationBreeder(breeder, factory, mutation);

	//*********************************************************************
	// create our stopping conditions
	//*********************************************************************
	MultiStopCondition conds = new MultiStopCondition();
	if(gens > 0)
	    conds.add(new GenerationStopCondition(gens));
	if(noprog > 0)
	    conds.add(new ProgressStopCondition(noprog));

	//*********************************************************************
	// create our mode and logger
	//*********************************************************************
	if(host == false) {
	    Logger log = new BasicLog(System.out, logdir, rawFitness, 
				      dataset.copy(), displaysize, rep);

	    mode = new BasicMode(breeder, ffunc, problems, retest, log, 
				 runs, popsize, conds);
	    if(modename.equalsIgnoreCase("pairwise"))
		mode = new PairwiseMode(factory, log, mode);
	    else if(modename.equalsIgnoreCase("singleton"))
		mode = new SingletonMode(factory, log, mode);
	}
	else {
	    // make our logger
	    PNuanceServerLog logger = 
		new PNuanceServerLog(System.out, logdir, rawFitness,
				     dataset.copy(), displaysize, rep);

	    // now our mode generator
	    ModeGenerator generator = 
		new BasicModeGenerator(breeder, ffunc, problems, retest,
				       runs, popsize, conds);

	    // check to see if we've selected special modes...
	    if(modename.equalsIgnoreCase("pairwise"))
		generator = 
		    new PairwiseModeGenerator(factory, 
					      (BasicModeGenerator)generator);
	    else if(modename.equalsIgnoreCase("singleton"))
		generator = 
		    new SingletonModeGenerator(factory, 
					       (BasicModeGenerator)generator);

	    // make our island handler, and plop it into a pNUANCE mode
	    mode = new PNuanceServerMode(logger,
	        new IslandHandler(generator, logger, new RingIslandModel()));
	}

	// copy our config set to the log dir
	set.write(logdir + java.io.File.separator + "config/config.txt");
    }



    //*************************************************************************
    // implementation of abstract methods
    //*************************************************************************
    public void display() {
	if(mode != null) 
	    mode.start();
	else
	    System.out.println("An error occured while configuring NUANCE.");
    }
}
