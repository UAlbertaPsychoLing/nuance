//*****************************************************************************
//
// InvalidRangeException.java
//
// This is an exception people can throw if they ever run into a problem with
// an invalid range.
//
//*****************************************************************************
package numerics;
public class InvalidRangeException extends Exception {
    public InvalidRangeException() { }
    public InvalidRangeException(String message) {
	super(message);
    }
}
