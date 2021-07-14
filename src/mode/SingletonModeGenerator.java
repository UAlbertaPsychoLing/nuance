//*****************************************************************************
//
// SingletonModeGenerator.java
//
// If we are doing parallel GP, it is rather cumbersome to send an entire mode
// to a requesting socket. For one, sending that many agents is wasteful when
// they can be generated client-side. Also, sending a logger makes not sense,
// as the client might be GUI or TUI - we don't know which on the server side.
// So, this is a class that will just send the basics of what we need from the
// server, and generate a Mode from it on the client's side.
//
// SingletonModeGenerator is a generator for the SingletonMode
//
//*****************************************************************************
package mode;
import  agent.s_expression.SymbolicFactory;
public class SingletonModeGenerator implements ModeGenerator {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private BasicModeGenerator generator;
    private SymbolicFactory    factory;



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SingletonModeGenerator(SymbolicFactory factory, 
				  BasicModeGenerator generator) {
	this.factory   = factory;
	this.generator = generator;
    }



    //*************************************************************************
    // implementation of abstract and interface methods
    //*************************************************************************
    public Mode generate(logger.Logger log, connection.Connection conn) {
	return new SingletonMode(factory, log, generator.generate(log, conn));
    }
}
