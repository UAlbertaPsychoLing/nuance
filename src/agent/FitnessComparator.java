//*****************************************************************************
//
// FitnessComparator.java
//
// There are times where we'll want to sort an array of agents by their 
// fitness. This comparator will allow us to compare any two agents by their
// fitness so sorting can be done. 
//
//*****************************************************************************
package agent;
import java.util.Comparator;
public class FitnessComparator implements Comparator {
    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public int compare(Object o1, Object o2) {
	if(((Agent)o1).getFitness() == Double.NaN)
	    return 1;
	double diff  = ((Agent)o1).getFitness() - ((Agent)o2).getFitness();
	if(diff > 0) return -1;
	if(diff < 0) return 1;
	return 0;
    }

    public boolean equals(Object o1, Object o2) {
	return (((Agent)o1).getFitness() == ((Agent)o2).getFitness());
    }
}
