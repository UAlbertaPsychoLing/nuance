//*****************************************************************************
//
// ClosableTabbedPane.java
//
// Extends the JTabbedPane. Whenever a component is removed from the pane, we
// see if it instantiates Closable. If it does, call the close function.
//
//*****************************************************************************
package ui.gui;
import  javax.swing.*;
import  java.awt.Component;
public class ClosableTabbedPane extends JTabbedPane {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public ClosableTabbedPane() {
	this(TOP);
    }

    public ClosableTabbedPane(int tabPlacement) {
	this(tabPlacement, TOP);
    }
    
    public ClosableTabbedPane(int tabPlacement, int tabLayoutPolicy) {
	super(tabPlacement, tabLayoutPolicy);
    }



    //*************************************************************************
    // methods we override
    //*************************************************************************
    public void remove(Component comp) {
	remove(indexOfComponent(comp));
    }

    public void removeAll() {
	while(getTabCount() > 0)
	    remove(0);
    }

    public void remove(int index) {
	removeTabAt(index);
    }

    public void removeTabAt(int index) {
	Component comp = getComponentAt(index);
	if(comp instanceof Closable)
	    ((Closable)comp).close();
	super.removeTabAt(index);
    }

    public void setComponentAt(int index, Component comp) {
	Component old = getComponentAt(index);
	if(old instanceof Closable)
	    ((Closable)old).close();
	super.setComponentAt(index, comp);
    }
}
