//*****************************************************************************
//
// NuancePanel.java
//
// A panel that holds a NUANCE runmode and an output window. When the 
// NuancePanel is closed, it ensures that the runmode is stopped.
//
//*****************************************************************************
package ui.gui;
import  javax.swing.*;
import  mode.Mode;
import  logger.GuiOutputStream;
public class NuancePanel extends JPanel implements Closable {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private Mode mode; // the nuance mode we're running



    //*************************************************************************
    // constructors
    //*************************************************************************
    public NuancePanel(Mode mode) {
	super();
	this.mode = mode;
    }



    //*************************************************************************
    // implementation of abstract methods
    //*************************************************************************
    public void close() {
	mode.interrupt = true;
    }
}
