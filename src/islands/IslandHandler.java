//*****************************************************************************
//
// IslandHandler.java
//
// pNUANCE uses and Island Model approach for distributed computation. Each
// client does its own thing (it is an island), and occasionally agents from 
// different islands will drift around. The IslandHandler uses an IslandModel
// to handle the migration of all the agents drifting around.
//
//*****************************************************************************
package islands;
import  java.io.*;
import  java.net.*;
import  java.util.*;
import  connection.Connection;
import  logger.Logger;
import  mode.ModeGenerator;
public class IslandHandler extends Thread {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private IslandModel       model;
    private ModeGenerator generator;
    private Logger              log;
    private Vector            conns = new Vector();
    private Hashtable    immigrants = new Hashtable();
    private boolean       interrupt = false;
    private Random       randomizer = new Random();
    private boolean          locked = false;

    

    //*************************************************************************
    // constructors
    //*************************************************************************
    public IslandHandler(ModeGenerator generator,Logger log,IslandModel model) {
	this.model     = model;
	this.generator = generator;
	this.log       = log;
	this.log.clear();
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public void run() {
	while(!interrupt) {
	    handleIslands();
	    try {
		sleep(100);
	    } catch(Exception e) { 
		log.println(e.getMessage());
		System.err.println(e.getMessage());
	    }
	}

	while(locked) {
 	    try {
 		wait();
 	    } catch(InterruptedException e) { }
	} locked = true;
	// close all of our connections
	for(Iterator it = conns.iterator(); it.hasNext();) {
	    Connection conn = (Connection)it.next();
	    closeConnection(conn);
	}
	locked = false;
	notifyAll();
    }

    /**
     * Interrupt our processing
     */
    public synchronized void interrupt() {
	while(locked) {
 	    try {
 		wait();
 	    } catch(InterruptedException e) { }
	} locked = true;
	this.interrupt = interrupt;
	log.endAllRuns();
	locked = false;
 	notifyAll();
    }

    /**
     * Try registering a new connection
     */
    public boolean tryRegister(Socket sock) {
 	boolean  retval = false;
	Connection conn = null;
	try { 
	    conn = new IslandConnection(sock);
	} catch(Exception e) { 
	    return false;
	}

 	if(!interrupt && !model.exists(conn.toString())) {
 	    try {
  		ObjectOutputStream oo = 
		    (ObjectOutputStream)conn.getOutputStream();
 		// send the connection its population
  		oo.writeObject("populate");
  		oo.writeObject(generator);
  		addConnection(conn);
 		retval = true;
 	    }
 	    catch(Exception e) {
 		retval = false;
 	    }
 	}

 	return retval;
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Goes through all of the connections with immigrants in queue. Sends them
     * out to the connections.
     */
    private void sendImmigrants() {
	LinkedList to_close = new LinkedList();
	Connection     conn;
	Vector       tosend;
	// loop to send out all of our immigrants
	for(Iterator it = conns.iterator(); it.hasNext();) {
	    conn = (Connection)it.next();
	    try {
		// check to see if we have any immigrants to send
		if( (tosend=(Vector)immigrants.remove(conn.toString()))!=null) {
		    log.println("Sending immigrants to " + conn);
		    ObjectOutputStream oo = 
			(ObjectOutputStream)conn.getOutputStream();
		    oo.writeObject("immigrate");
		    oo.writeObject(tosend);
		}
	    }
	    // problem reading or writing to the connection. close it down.
	    catch(Exception e) {
		to_close.addLast(conn);
	    }
	}

	// close down all the connections that had an error
	for(Iterator it = to_close.iterator(); it.hasNext();)
	    closeConnection((Connection)it.next());
    }


    /**
     * Add a new connection to our island model
     */
    private synchronized void addConnection(Connection conn) {
	while(locked) {
 	    try {
 		wait();
 	    } catch(InterruptedException e) { }
	} locked = true;
	// add the new connection to our list, and its id to the model
	log.println("Receiving connection from " + conn + "\t" + 
		    (conns.size() + 1) + " connections total");
	model.add(conn.toString());
	conns.addElement(conn);
	locked = false;
 	notifyAll();
    }


    /**
     * Takes in a vector of emigrants and ships them out to a random neighbour
     */
    private void doEmigration(Connection conn, Vector emigrants) {
	log.println("Receiving emigrants from " + conn);
	// log what we just received
	Object[]  obj_agents = emigrants.toArray();
	agent.Agent[] agents = new agent.Agent[obj_agents.length];
	for(int i = 0; i < obj_agents.length; i++)
	    agents[i] = (agent.Agent)obj_agents[i];
	log.log(agents);

	Vector neighbours = model.getNeighbours(conn.toString());
	if(neighbours != null && neighbours.size() > 0) {
	    String randNeighbour = (String) 
		neighbours.elementAt(randomizer.nextInt(neighbours.size()));
	    // pull up his list of pending immigrants. If it exists, append
	    // these ones. If it doesn't, then set the immigrants as these
	    Vector immigrants = (Vector)this.immigrants.get(randNeighbour);
	    if(immigrants == null)
		this.immigrants.put(randNeighbour, emigrants);
	    else for(int i = 0; i < emigrants.size(); i++)
		    immigrants.addElement(emigrants.elementAt(i));
	}
    }


    /**
     * See if any of our connections have info to send us. If they do, read it
     * in and execute whatever they tell us to do.
     */
    private void queryConnections() {
	LinkedList to_close = new LinkedList();
	Connection     conn;
	// loop to receive all of our emigrants, and send them out
	for(Iterator it = conns.iterator(); it.hasNext();) {
	    conn = (Connection)it.next();
	    // check to see if we have any stuff to receive
	    try {
		if(!conn.isConnected())
		    to_close.addLast(conn);
		else if(conn.hasInput()) {
		    ObjectInputStream is = 
			(ObjectInputStream)conn.getInputStream();
  		    String cmd = (String)is.readObject();
	    
  		    // do we want to close our connection down?
  		    if(cmd.equalsIgnoreCase("close"))
			to_close.addLast(conn);
  		    // do we want to send emigrants to other islands?
  		    else if(cmd.equalsIgnoreCase("emigrate"))
  			doEmigration(conn, (Vector)is.readObject());
 		}
	    }
	    // problem reading or writing to the connection. close it down.
	    catch(Exception e) {
		to_close.addLast(conn);
	    }
	}

	// close all of our connections that need closing
	for(Iterator it = to_close.iterator(); it.hasNext();)
	    closeConnection((Connection)it.next());
    }

    /**
     * Handles emigrations and immigration for all the islands
     */
    private synchronized void handleIslands() {
  	while(locked) {
   	    try {
   		wait();
   	    } catch(InterruptedException e) { }
  	} locked = true;

  	// try to execute all of the commands we are being sent
  	queryConnections();
	    
 	// send out all of our immigrants in queue
 	sendImmigrants();

  	locked = false;
   	notifyAll();
    }

    /**
     * Close one of our connections, and remove it from the Island Model
     */
    private void closeConnection(Connection conn) {
	log.println("Closing connection to " + conn + "\t" +
		    (conns.size() - 1) + " connections total");
	model.remove(conn.toString());
	immigrants.remove(conn.toString());
	conns.removeElement(conn);
	try {
	    conn.close();
	} catch(Exception e) { }
    }
}
