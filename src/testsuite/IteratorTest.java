//*****************************************************************************
//
// IteratorTest.java
//
// Compares the speed of iterating over a list vs. referencing vector positions
//
//*****************************************************************************
package testsuite;
import java.util.*;
public class IteratorTest {
    public static void main(String args[]) {
	if(args.length != 1) {
	    System.out.println("How long of a list are we iterating over?\r\n");
	    return;
	}

	int size = new Integer(args[0]).intValue();

	// create the vectors and lists
	Iterator     it = null;
	LinkedList list = new LinkedList();
	Vector   vector = new Vector(size);
	Object      val = null;

	// fill 'em all up
	for(int i = 0; i < size; i++) {
	    list.addLast(new Integer(i));
	    vector.add(i, new Integer(i));
	}

	// go over the list a bunch of times, and time it
	long start = System.currentTimeMillis();
	for(int i = 0; i < 1000; i++) {
	    it = list.iterator();
	    while(it.hasNext())
		val = it.next();
	}
	double list_time = (System.currentTimeMillis() - start);
	
	// now go over the vector
	start = System.currentTimeMillis();
	for(int i = 0; i < 1000; i++) {
	    for(int j = 0; j < size; j++) {
		val = vector.elementAt(j);
	    }
	}
	double vector_time = (System.currentTimeMillis() - start);

	System.out.println("List   = " + list_time);
	System.out.println("vector = " + vector_time);
    }
}
