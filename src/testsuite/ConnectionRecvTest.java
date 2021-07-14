//*****************************************************************************
//
// ConnectionRecvTest.java
//
// A testsuite for ensuring that connections between NUANCEs are working
// properly. Implements the receiving end of things.
//
//*****************************************************************************
package testsuite;
import  java.util.*;
import  java.io.*;
import  java.net.*;
public class ConnectionRecvTest {
    public static void main(String args[]) throws Exception {
	String host = "localhost";
	int    port = 7159;
	if(args.length >= 1)
	    host = args[0];
	if(args.length >= 2)
	    port = Integer.parseInt(args[1]);

	// Create our connection
	Socket sock = new Socket(host, port);

	// get our input stream
	ObjectInputStream oi = new ObjectInputStream(sock.getInputStream());

	// get the vector we've been sent
	Vector recv = (Vector)oi.readObject();

	// write out the message
	for(int i = 0; i < recv.size(); i++)
	    System.out.print(recv.elementAt(i) + " ");
	System.out.println();

	// close the socket down
	sock.close();
    }
}
