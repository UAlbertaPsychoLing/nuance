//*****************************************************************************
//
// ConnectionSendTest.java
//
// A testsuite for ensuring that connections between NUANCEs are working
// properly. Implements the sending end of things.
//
//*****************************************************************************
package testsuite;
import  java.util.*;
import  java.io.*;
import  java.net.*;
public class ConnectionSendTest {
    public static void main(String args[]) throws Exception {
	int port = 7159;
	if(args.length >= 1)
	    port = Integer.parseInt(args[0]);

	// create the server
	ServerSocket server = new ServerSocket(port);
	System.out.println("Server bound. Awaiting connection");

	// listen for a connection
	Socket sock = server.accept();
	System.out.println("Connection accepted. Sending message");

	// create a vector of strings, and send it
	Vector tosend = new Vector();
	tosend.addElement("hello,");
	tosend.addElement("world!");
	ObjectOutputStream oo = 
	    new ObjectOutputStream(sock.getOutputStream());
	System.out.println("Message sent. Terminating");
	    
	oo.writeObject(tosend);
	System.out.println(sock.toString());
	sock.close();

	server.close();
    }
}
