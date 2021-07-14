//*****************************************************************************
//
// IslandModel.java
//
// PNUANCE uses and Island Model approach for distributed computation. Each
// client does its own thing (it is an island), and occasionally agents from 
// different islands will drift around. The IslandModel handles the insertion
// and organization of all the different islands.
//
//*****************************************************************************
package islands;
import  java.util.Vector;
public interface IslandModel {
    /**
     * Returns whether or not someone with the given ID exists already
     */
    public boolean exists(Object id);

    /**
     * Add a new island to the model
     */
    public void add(Object id);

    /**
     * Removes an island from the model
     */
    public void remove(Object id);

    /**
     * Returns a vector of our neighbours
     */
    public Vector getNeighbours(Object id);
}
