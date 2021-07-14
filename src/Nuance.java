//*****************************************************************************
//
// Nuance.java
//
// This is the entrypoint into NUANCE (Naturalistic University of Alberta
// Nonlinear Correlation Explorer). It will boot up the user interface.
//
//*****************************************************************************
import ui.NuanceUI;
import ui.tui.TextNuance;
import ui.gui.GuiNuance;
public abstract class Nuance extends Thread {
    /**
     * The entrypoint into the NUANCE program. Figures out if we want to go into
     * pNUANCE mode or regular single mode, and whether we want a GUI or TUI
     */
    public static void main(String[] args) throws Exception {
	boolean graphical = true; // are we running in graphical mode?
	String connection = null; // are we trying to connect somewhere?

	// parse options
	for(int i = 0; i < args.length; i++) {
	    if(args[i].substring(1).equals("textmode"))
		graphical = false;
	    else if(args[i].substring(1).equals("guimode"))
		graphical = true;
	    else if(args[i].substring(1).equals("c") || 
		    args[i].substring(1).equals("connect")) {
		connection = args[++i];
		graphical  = false;
	    }
	    else {
		System.err.println("Invalid option, " + args[i]);
		System.exit(1);
	    }
	}

	// javax.swing.UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());

	// run!
	NuanceUI ui = null;
	if(graphical == true)
	    ui = new GuiNuance();
	else if(connection != null)
	    ui = new TextNuance(connection);
	else
	    ui = new TextNuance();
	ui.display();
    }
}
