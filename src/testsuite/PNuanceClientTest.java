//*****************************************************************************
//
// PNuanceClientTest.java
//
// Used in conjunction with PNuanceServerTest. Tries to connect to the server,
// generate a new population, register it, and go from there.
//
//*****************************************************************************
package testsuite;
import  java.net.*;
import  java.io.*;
import  mode.*;
import  logger.*;
import  problem.*;
import  fitness.*;
import  islands.IslandConnection;
public class PNuanceClientTest {
    public static void main(String args[]) throws Exception {
	String host = "localhost";
	int    port = 7159;
	if(args.length >= 1)
	    host = args[0];
	if(args.length >= 2)
	    port = Integer.parseInt(args[1]);

	// Create our connection
	IslandConnection conn = new IslandConnection(new Socket(host, port));

	// get our streams
	ObjectInputStream  oi = (ObjectInputStream)conn.getInputStream();

	// read our first command
	String cmd = (String)oi.readObject();
	if(cmd.equalsIgnoreCase("close"))
	    conn.close();
	else if(cmd.equalsIgnoreCase("populate")) {
	    ModeGenerator gen = (ModeGenerator)oi.readObject();

	    // spawn the mode
	    Mode mode = 
		gen.generate(new BasicLog(System.out, "../log/pnuanceclient",
					  new RSquaredFitnessFunction("TARGET"),
					  new VariableSetProblem()), conn);
	    mode.start();
	}
    }
}
