//*****************************************************************************
//
// Utils.java
//
// Just a collection of routines I find myself calling often, across many
// different components of the GUI.
//
//*****************************************************************************\
package ui.gui;
import  java.awt.*;
import  javax.swing.*;
public class Utils {
    /**
     * This will take a component and wrap a new Container around it that will
     * ensure the wrapped component is always centered in whatever space it is
     * put into.
     */
    public static Component centerComp(Component comp) {
	Box box = Box.createVerticalBox();
	box.add(Box.createVerticalGlue());
	box.add(makeWrapper(comp));
	box.add(Box.createVerticalGlue());
	return box;
    }

    /**
     * Creates a label for the component, and puts the two into a panel side
     * by side, with the label being on the left, and right-justified
     */
    public static Component labelComp(Component comp, String label) {
	JPanel p = new JPanel();
	p.add(new JLabel(label, JLabel.RIGHT));
	p.add(comp);
	return p;
    }

    /**
     * It's rather cumbersome to always be wrapping panels around components,
     * so here's a little function to do most of the dirty work for us
     */ 
    public static JPanel makeWrapper(Component comp) {
	return makeWrapper(comp, new FlowLayout());
    }

    /**
     * Same as above, but a layout manager can be specified
     */ 
    public static JPanel makeWrapper(Component comp, LayoutManager layout) {
	return makeWrapper(comp, layout, null);
    }

    /**
     * Same as above, but also takes an argument for the add
     */
    public static JPanel makeWrapper(Component comp, LayoutManager layout,
				      String arg) {
	JPanel p = new JPanel(layout);
	if(arg == null)  p.add(comp);
	else             p.add(arg, comp);
	return p;
    }
}
