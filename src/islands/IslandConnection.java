//*****************************************************************************
//
// IslandConnection.java
//
// This is a wrapper around a plain old socket, which has an object input and
// output stream which can be shared by everyone. Nothing fancy here.
//
//*****************************************************************************
package islands;
import java.io.*;
import java.net.*;
import connection.Connection;
public class IslandConnection implements Connection {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Socket sock;
    private ObjectOutputStream oo;
    private ObjectInputStream  oi;



    //*************************************************************************
    // constructors
    //*************************************************************************
    public IslandConnection(Socket sock) throws
	java.io.IOException {
	this.sock = sock;
	this.oo   = new ObjectOutputStream(sock.getOutputStream());
	this.oi   = new ObjectInputStream(sock.getInputStream());
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    public OutputStream getOutputStream() {
	return oo;
    }

    public InputStream getInputStream() {
	return oi;
    }

    public void close() {
	try {
	    sock.close();
	} catch(Exception e) {}
    }

    public boolean hasInput() {
	try {
	    int amnt = sock.getInputStream().available();
	    return (amnt > 4);
	} catch(Exception e) {
	    return false;
	}
    }

    public boolean isConnected() {
	return sock.isConnected();
    }

    public String toString() {
	return sock.toString();
    }
}
