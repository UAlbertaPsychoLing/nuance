//*****************************************************************************
//
// PNuanceClientMode.java
//
// Creates a new client mode for connecting to a pNUANCE server.
//
//*****************************************************************************
package mode;
import  java.io.*;
import  java.net.*;
import  logger.Logger;
import  islands.IslandConnection;
public class PNuanceClientMode extends Mode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private ModeGenerator generator; // the generator we downloaded
    private Mode               mode; // the mode running inside us
    private Logger              log; // our logger
    private String             host; // the host we're connected to
    private int                port; // the port we are connected on
    private Socket             sock; // our connection



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Create a new client with the given connection address
     */
    public PNuanceClientMode(Logger log, String host) {
	this(log, host, PNuanceServerMode.DFLT_PORT);
    }

    /**
     * Create a new server with the given handler. Run on the specified port.
     */
    public PNuanceClientMode(Logger log, String host, int port) {
	this.log  = log;
	this.host = host;
	this.port = port;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public void run() {
	try {
	    IslandConnection conn = new IslandConnection(new Socket(host,port));

	    // get our streams
	    ObjectInputStream  oi = (ObjectInputStream)conn.getInputStream();
	    ObjectOutputStream oo = (ObjectOutputStream)conn.getOutputStream();
	    String            cmd = (String)oi.readObject();

	    if(cmd.equalsIgnoreCase("close")) {
		log.println("Connection to " + host + ":" + port + " refused");
		conn.close();
	    }
	    else if(cmd.equalsIgnoreCase("populate")) {
		generator = (ModeGenerator)oi.readObject();
		mode      = generator.generate(log, conn);
		mode.start();

		while(!interrupt && mode.isAlive()) {
		    try {
			wait(1000);
		    } catch(Exception e) {}
		}

		mode.interrupt = true;
 		oo.writeObject("close");
 		conn.close();
	    }
	}
	catch(Exception e) {
	    log.println("Could not establish connection to "+host+":"+port);
	    log.println("Error message is:");
	    log.println(e.getMessage());
	}
    }
}
