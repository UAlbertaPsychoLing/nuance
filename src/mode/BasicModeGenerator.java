//*****************************************************************************
//
// BasicModeGenerator.java
//
// If we are doing parallel GP, it is rather cumbersome to send an entire mode
// to a requesting socket. For one, sending that many agents is wasteful when
// they can be generated client-side. Also, sending a logger makes not sense,
// as the client might be GUI or TUI - we don't know which on the server side.
// So, this is a class that will just send the basics of what we need from the
// server, and generate a Mode from it on the client's side.
//
// BasicModeGenerator is a generator for the BasicMode
//
//*****************************************************************************
package mode;
import breeder.Breeder;
import breeder.PNuanceBreeder;
import fitness.FitnessFunction;
import problem.Problem;
import stopcondition.StopCondition;
public class BasicModeGenerator implements ModeGenerator {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Breeder         breeder;
    private FitnessFunction   ffunc;
    private Problem[]      problems;
    private boolean          retest;
    private int                runs;
    private int             popsize;
    private StopCondition condition;



    //*************************************************************************
    // constructors
    //*************************************************************************
    public BasicModeGenerator(Breeder breeder,     FitnessFunction fitness, 
			      Problem[] problems,  boolean retest, int runs, 
			      int popsize,         StopCondition condition) {
	this.breeder   = breeder;
	this.ffunc     = fitness;
	this.problems  = problems;
	this.retest    = retest;
	this.runs      = runs;
	this.popsize   = popsize;
	this.condition = condition;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Mode generate(logger.Logger log, connection.Connection conn) {
	try {
	    // create our PNuanceBreeder
	    PNuanceBreeder pbreeder = new PNuanceBreeder(breeder, conn);
	    Mode mode = new BasicMode(pbreeder, ffunc, problems,retest,log,runs,
				      popsize, condition);
	    pbreeder.setMode(mode);
	    return mode;
	} catch(Exception e) {
	    return null;
	}
    }
}
