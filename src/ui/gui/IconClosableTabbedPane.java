//*****************************************************************************
//
// IconClosableTabbedPane.java
//
// This is an extension to JTabbedPane that allows users to close tabs within
// a tabbed pane. This just removes the tab from the tab pane.
//
//*****************************************************************************
package ui.gui;
import  java.awt.event.MouseEvent;
import  javax.swing.*;
import  javax.swing.event.MouseInputAdapter;
import  java.awt.Component;
import  java.awt.Graphics;
public class IconClosableTabbedPane extends ClosableTabbedPane {
    //*************************************************************************
    // constructors
    //*************************************************************************
    public IconClosableTabbedPane() {
	this(TOP);
    }

    public IconClosableTabbedPane(int tabPlacement) {
	this(tabPlacement, TOP);
    }
    
    public IconClosableTabbedPane(int tabPlacement, int tabLayoutPolicy) {
	super(tabPlacement, tabLayoutPolicy);
	addMouseListener(new ClosingListener());
    }



    //*************************************************************************
    // overriden public methods
    //*************************************************************************
    public void addTab(String title, Component comp) {
	addTab(title, comp, null);
    }

    public void addTab(String title, Icon tabIcon, Component comp) {
	addTab(title, tabIcon, comp, null);
    }

    public void addTab(String title, Component comp, String tip) {
	addTab(title, UIManager.getIcon("InternalFrame.closeIcon"), comp,tip);
    }

    public void addTab(String title, Icon tabIcon, Component comp, String tip) {
	super.addTab(title, new EventIcon(tabIcon), comp, tip);	
    }



    //**************************************************************************
    // internal classes
    //**************************************************************************
    /***
     * Listens to mouse click events on the tab titles. 
     */
     protected class ClosingListener extends MouseInputAdapter {
	 public void mouseClicked(MouseEvent e) {
	     int i = getSelectedIndex();
	     if (i == -1)
		 return;
	     // close tab, if icon was clicked
	     EventIcon icon = (EventIcon)getIconAt(i);
	     if (icon != null) {
		 if (icon.contains(e.getX(), e.getY()))
		     removeTabAt(i);
	     }
	 }
     }

    /***
     * Acts as a proxy class for the closing icon. 
     */
    protected class EventIcon implements Icon {
	private Icon icon;    
	private int x      = 0;
	private int y      = 0;
	private int height = 10;
	private int width  = 10;
	
	public EventIcon(Icon icon) {
	    this.icon = icon;
	    if(icon != null) {
		height = icon.getIconHeight();
		width = icon.getIconWidth();
	    }
	}
 
	public int getIconHeight() {
	    return height;
	}
 
	public int getIconWidth() {
	    return width;
	}
 
	/***
	 * Overwrites paintIcon to get hold of the coordinates of the icon,
	 * this is a rather rude approach just to find out if the icon
	 * was pressed.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
	    this.x = x;
	    this.y = y;

	    if(icon != null)
		icon.paintIcon(c, g, x, y);
	    // draw an X
	    else {
		// top left -> bottom right
		g.drawLine(x, y, x + width, y + height);
		// bottom left -> top right
		g.drawLine(x, y + height, x + width, y);
	    }
	}
 	
	/***
	 * Verifies if x and y are within the icon's borders.
	 */
	public boolean contains(int xEvent, int yEvent) {
	    if(!(xEvent >= x) || !(xEvent <= x + width))
		return false;
	    if(!(yEvent >= y) || !(yEvent <= y + height))
		return false;
	    
	    return true;
	}
    }
}
