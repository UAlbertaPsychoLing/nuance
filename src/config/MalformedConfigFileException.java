//*****************************************************************************
//
// MalformedConfigFileExcepetion.java
//
// A little error that is thrown when a config file is being parsed, but it has
// improper syntax.
//
//*****************************************************************************
package config;
public class MalformedConfigFileException extends Exception {
    public MalformedConfigFileException(String s) {
	super(s);
    }
}
