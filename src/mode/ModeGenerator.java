//*****************************************************************************
//
// ModeGenerator.java
//
// If we are doing parallel GP, it is rather cumbersome to send an entire mode
// to a requesting socket. For one, sending that many agents is wasteful when
// they can be generated client-side. Also, sending a logger makes not sense,
// as the client might be GUI or TUI - we don't know which on the server side.
// So, this is a class that will just send the basics of what we need from the
// server, and generate a Mode from it on the client's side.
//
//*****************************************************************************
package mode;
public interface ModeGenerator extends java.io.Serializable {
    /**
     * generate a new mode. Each mode should have its own generator. Uses the
     * given connection for sending/receiving data from whatever sent the
     * generator.
     */
    public Mode generate(logger.Logger log, connection.Connection conn);
}
