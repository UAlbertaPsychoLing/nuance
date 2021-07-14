//*****************************************************************************
//
// Connection.java
//
// The basics of a two-way connection. Supports reading and writing, and closing
// of the connection.
//
//*****************************************************************************
package connection;
import  java.io.*;
public interface Connection {
    public OutputStream getOutputStream();
    public InputStream  getInputStream();
    public boolean      hasInput();
    public boolean      isConnected();
    public void         close();
}
