//*****************************************************************************
//
// PNuanceBreeder.java
//
// NUANCE implements distributed genetic programming via the "island model" of
// GP. What happens is that a bunch of independent populations are spawned and
// connected to eachother like "islands". Agents are occassionally allowed to
// migrate between islands. This is a method for keeping diversity amongst all
// your agents high and (hopefully) giving yourself a better search. Plus,
// computation can be distributed accross multiple computers! This breeder will
// handle transfer of all population members.
//
//*****************************************************************************
package breeder;
import  java.util.Vector;
import  java.io.*;
import  connection.Connection;
import  mode.Mode;
public class PNuanceBreeder implements Breeder {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Breeder       breeder; // the real breeder we wrap around
    private Connection       conn; // our connection to our server
    private Mode      mode = null; // the mode we interrupt if our socket ever
                                   // gets broken



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Set up the pNuanceBreeder. After it is set up, a Mode must be attached
     * to it. This is so that the mode can be closed down if the connection to
     * our server is ever broken.
     */
    public PNuanceBreeder(Breeder breeder, connection.Connection conn) {
	this.breeder = breeder;
	this.conn    = conn;
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Set our mode, so we can interrupt it if our connection ever gets closed
     */
    public void setMode(Mode mode) {
	this.mode = mode;
    }



    //*************************************************************************
    // implementation of abstract and inherited methods
    //*************************************************************************
    public agent.Agent[] populate(int n) {
	return breeder.populate(n);
    }

    public agent.Agent[] breed(agent.Agent[] agents) {
	try {
	    agents = breeder.breed(agents);

	    // check if we have agents being sent to us
	    if(conn.hasInput()) {
		ObjectInputStream oi = (ObjectInputStream)conn.getInputStream();
		String           cmd = (String)oi.readObject();

		if(cmd.equalsIgnoreCase("close"))
		    mode.interrupt = true;
		// if so, receive them, kick out the bottom n agents, and 
		// replace them	with the ones we are receiving.
		else if(cmd.equalsIgnoreCase("immigrate")) {
		    Vector new_agents = (Vector)oi.readObject();
		    for(int i = 0; i < new_agents.size(); i++) {
			agents[agents.length - 1 - i] = 
			    (agent.Agent)new_agents.elementAt(i);
		    }
		}
	    }

	    // check to see if we need to sent any agents out - 5% chance
	    if(System.currentTimeMillis() % 20 == 0) {
		// send off the top two population members
		Vector to_send = new Vector();
		to_send.addElement(agents[0]);
		to_send.addElement(agents[1]);
		ObjectOutputStream oo =
		    (ObjectOutputStream)conn.getOutputStream();
		oo.writeObject("emigrate");
		oo.writeObject(to_send);

		// kill off the two members we just sent out
		//***********
		// FINISH ME
		//***********	
	    }
	}
	catch(Exception e) {
	    // we encountered a problem. Terminate our mode
	    mode.interrupt = true;
	}

	// return whatever we did
	return agents;
    }
}
