//*****************************************************************************
//
// RingIslandModel.java
//
// PNUANCE uses and Island Model approach for distributed computation. Each
// client does its own thing (it is an island), and occasionally agents from 
// different islands will drift around. The IslandModel handles the insertion
// and organization of all the different islands.
//
// This IslandModel organizes all of its inhabitants into a ring.
//
//*****************************************************************************
package islands;
import  java.util.Vector;
public class RingIslandModel implements IslandModel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Vector islands; // the islands we have registered



    //*************************************************************************
    // constructors
    //*************************************************************************
    public RingIslandModel() {
	islands = new Vector();
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public boolean exists(Object id) { 
	return islands.contains(id);
    }

    public void add(Object id) { 
	// if the ID already exists, do nothing
	if(!exists(id))
	    islands.addElement(id);
    }

    public void remove(Object id) {
	islands.removeElement(id);
    }

    public Vector getNeighbours(Object id) { 
	int pos = islands.indexOf(id);
	if(pos == -1)
	    return null;

	// if we're the only island, we have no neighbours
	if(islands.size() == 1)
	    return new Vector();

	Vector neighbours = new Vector();

	// add the one below us, unless we're the first island. 
	// Then add the top element
	if(pos > 1)
	    neighbours.add(islands.elementAt(pos-1));
	else
	    neighbours.add(islands.elementAt(islands.size()-1));

	// if there are only two islands, don't try to re-add single neighbour
	if(islands.size() > 2) {
	    // add the one above us. Unless we're the last island.
	    // Then add the bottom element
	    if(pos == islands.size() - 1)
		neighbours.add(islands.elementAt(0));
	    else
		neighbours.add(islands.elementAt(pos+1));
	}

	// and return our neighbours
	return neighbours;
    }
}
