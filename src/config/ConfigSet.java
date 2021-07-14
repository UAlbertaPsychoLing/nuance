//*****************************************************************************
//
// ConfigSet.java
//
// Basically, this is a glorified hashtable that parses in key:value pairs from
// a file, and keeps them in storage for easy reference. Handy when you don't
// want to write a loop that parses config values from a file. Ignores comments
// (signified with #). Config files take the form:
//
// key:value
//
// spaces are skipped
//
//*****************************************************************************
package config;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Hashtable;
public class ConfigSet extends Hashtable {
    //*************************************************************************
    // Constructors
    //*************************************************************************
    public ConfigSet() {
	super();
    }

    /**
     * Make a new ConfigSet, and partse the config values from the specified
     * file.
     */
    public ConfigSet(String filename) throws 
        MalformedConfigFileException,
        java.io.FileNotFoundException,
        java.io.IOException {

	this();
	read(filename);
    }



    //*************************************************************************
    // public functions
    //*************************************************************************
    /**
     * Same as hashtable.get(), except always returns a string instead of Object
     */
    public String get(String key) {
	Object val = super.get(key);
	return (val == null ? null : val.toString());
    }

    /**
     * Write the contents of a config set to disk
     */ 
    public void write(String filename) {
	// build up an array of all our keys
	String keys[] = new String[size()];
	int longest   = 0;

	java.util.Enumeration en = keys();
	for(int i = 0; en.hasMoreElements(); i++) {
	    keys[i] = (String)en.nextElement();
	    if(keys[i].length() > longest)
		longest = keys[i].length();
	}

	try {
	    java.io.BufferedWriter bw = new java.io.BufferedWriter(
					    new java.io.FileWriter(
				                new java.io.File(filename)));

	    // now, write all of our values
	    for(int i = 0; i < keys.length; i++)
		bw.write(padright(keys[i], longest) + ": " + get(keys[i]) + 
			 System.getProperty("line.separator"));
	    bw.close();
	}
	catch(Exception e) { }
    }

    /**
     * Reads the contents of a file into the config set
     */
    public void read(String filename) throws
        MalformedConfigFileException,
        java.io.FileNotFoundException,
        java.io.IOException {
	
	BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
	String line;
	
	// go through each line in the file
	while( (line = in.readLine()) != null) {
	    // kill our whitespaces
	    line = line.trim();

	    // see if it is a comment or a blank line
	    if(line.charAt(0) == '#' || line.length() == 0)
		continue;
	    
	    // ok, now parse the key:val pair
	    int separator_index = line.indexOf(':');
	    if(separator_index <= 0)
		throw new MalformedConfigFileException(
		   "Uninterperable line, " + line + 
		   System.getProperty("line.separator") + 
		   "in file: " + filename);
	    String key = line.substring(0, separator_index).trim();
	    String val = line.substring(separator_index +1).trim();

	    // make sure both the key and val are OK
	    if(key.length() == 0 || val.length() == 0)
		throw new MalformedConfigFileException(
		   "Uninterperable line, " + line + 
		   System.getProperty("line.separator") + 
		   "in file: " + filename);

	    // everything is fine. Add this config setting to us
	    put(key, val);
	}

	// close down our reader
	in.close();
    }



    //*************************************************************************
    // static private methods
    //*************************************************************************
    /**
     * Parse in values from the specified config file
     */
    public static ConfigSet parse(String filename) throws 
        MalformedConfigFileException,
        java.io.FileNotFoundException,
        java.io.IOException {
	
	ConfigSet set = new ConfigSet();
	set.read(filename);
	return set;
    }



    //*************************************************************************
    // static private methods
    //*************************************************************************
    /**
     * Returns a new string based on the argument string, but make sure it is
     * padded with enough spaces on the right to ensure the new string is of the
     * specified length
     */
    private static String padright(String string, int length) {
	while(string.length() < length)
	    string = string + " ";
	return string;
    }
}
