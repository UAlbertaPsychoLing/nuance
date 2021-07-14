//*****************************************************************************
//
// Logger.java
//
// Nope, no lumberjacks here! NUANCE can run in many different modes, and with
// both graphical and textual interfaces. All of these things will require
// slightly different methods of logging data. That is what a Logger handles.
//
// All loggers must implement these basic methods.
//
//*****************************************************************************
package logger;
import  agent.Agent;
public interface Logger {
    /**
     * Does all of the final logging that needs to be done for the end of run;
     * increment up to a new run.
     */
    public void endRun();

    /**
     * Does all of the final logging that needs to be done for a set of runs.
     */
    public void endAllRuns();

    /**
     * Resets everything to default
     */
    public void clear();

    /**
     * Returns the best agent found to date
     */
    public Agent best();

    /**
     * Sends out log information based on the population passed in. It is
     * assumed the population is sorted in order of fitness (best to worst)
     */
    public void log(Agent[] population);

    /**
     * Change the log directory. Changes relative to the root directory of the
     * logger.
     */
    public void changeDirectory(String directory);

    /**
     * Returns the root directory of this logger
     */
    public String root();

    /**
     * Write the data to a file with the given name. Name is relative to the
     * current directory of the logger. Overwrites old files by the same name
     */
    public void write(String filename, String data);

    /**
     * Appends the data to the end of the file with the given name. Name is
     * relative to the current directory of the logger.
     */
    public void append(String filename, String data);

    /**
     * Sends the message to whatever output our logger has.
     */
    public void print(String s);

    /**
     * Sends a newline-delimited message to whatever output our logger has.
     */
    public void println(String s);
}
