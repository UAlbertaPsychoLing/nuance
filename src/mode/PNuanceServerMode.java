//*****************************************************************************
//
// PNuanceServerMode.java
//
// Creates a new server for handling the coordination of multiple islands of
// evolving agents.
//
//*****************************************************************************
package mode;
import  java.io.*;
import  java.net.*;
import  logger.Logger;
import  islands.IslandHandler;
public class PNuanceServerMode extends Mode {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private int              port; // the port we are serving on
    private IslandHandler handler; // the thing that handles all our clients
    private Logger            log; // where we log messages 

    // static variables
    public static final int DFLT_PORT = 7159;



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Create a new server with the given generator. Run on the default port.
     */
    public PNuanceServerMode(Logger log, IslandHandler handler) {
	this(log, handler, DFLT_PORT);
    }

    /**
     * Create a new server with the given handler. Run on the specified port.
     */
    public PNuanceServerMode(Logger log, IslandHandler handler, int port) {
	this.log       = log;
	this.port      = port;
	this.handler   = handler;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public void run() {
	ServerSocket   server = null;
	handler.start();

	try {
	    // set up our ServerSocket
	    server = new ServerSocket(port);
	// we couldn't bind our server
	} catch(Exception e) {
	    // send out a message
	    log.println("Could not bind socket on port " + port);
	    log.println("Error message is: ");
	    log.println(e.getMessage());
	    return;
	}

	log.println("Server bound. Awaiting connections.");

	// start serving
	while(!interrupt) {
	    Socket socket = null;
	    try {
		// wait for a connection
		socket = server.accept();
		handler.tryRegister(socket);
	    }
	    // error reading from the socket. We should do something here
	    catch(Exception e) { 
		//***********
		// FINISH ME
		//***********
	    }
	}

	// let us know what happened
	log.println("Server interrupted. Terminating.");

	// interrupt our handler, like we have been
	handler.interrupt();

	try {
	    // close down our server socket
	    server.close();
	// error closing... ignore
	} catch(Exception e) { }
    }
}
