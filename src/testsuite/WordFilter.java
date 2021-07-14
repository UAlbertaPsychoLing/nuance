//*****************************************************************************
//
// WordFilter.java
//
// Takes in a list of words, and filters out any lines on the input stream that
// do not start with a word from our filter list.
//
//*****************************************************************************
package testsuite;
import  java.io.*;
import  java.util.*;
public class WordFilter {
    public static void main(String args[]) throws Exception {
	if(args.length < 1) {
	    System.out.println("What file contains the filter list?\r\n");
	    System.exit(1);
	}

	BufferedReader br=new BufferedReader(new FileReader(new File(args[0])));
	TreeSet       set = new TreeSet();

	// read in all of our words
	String line;
	while( (line = br.readLine()) != null)
	    set.add(line);

	// read from our input
	br = new BufferedReader(new InputStreamReader(System.in));
	while( (line = br.readLine()) != null) {
	    // pick out our word...
	    String word = line.substring(0, line.indexOf('\t'));
	    if(set.contains(word))
		System.out.println(line);
	}
    }
}
