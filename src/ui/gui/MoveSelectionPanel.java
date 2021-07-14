//*****************************************************************************
//
// MoveSelectionPanel.java
//
// This panel has two JLists; one for selected items and one for unselected
// items. People can move items between the two lists, but each item can only
// belong to one of the two lists.
//
//*****************************************************************************
package ui.gui;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.border.*;

public class MoveSelectionPanel extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    JList       unselected = new JList(new DefaultListModel());
    JList         selected = new JList(new DefaultListModel());
    String   selectedLabel;
    String unselectedLabel;



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Creates a new MoveSelectionPanel. All of items[] are in the unselected
     * list be default.
     */
    public MoveSelectionPanel(Object[] items) {
	this(items, null);
    }

    /**
     * Creates a new MoveSelectionPanel, with the specified lists[] in their
     * corresponding JLists
     */
    public MoveSelectionPanel(Object[] unselected, Object[] selected) {
	this(unselected, selected, 6);
    }

    /**
     * Creates a new MoveSelectionPanel as above, with the added constraint
     * that the lists never display more than the specified number of elements
     */
    public MoveSelectionPanel(Object[] unselected, Object[] selected,
			      int maxheight) {
	this(unselected, selected, "Unused", "Used", 6);
    }

    /**
     * Constructs the panel, and also sets panel labels
     */
    public MoveSelectionPanel(Object[]    unselected, Object[]    selected,
			      String unselectedLabel, String selectedLabel) {
	this(unselected, selected, unselectedLabel, selectedLabel, 6);
    }

    /**
     * piece everything together
     */
    public MoveSelectionPanel(Object[]    unselected, Object[]    selected,
			      String unselectedLabel, String selectedLabel,
			      int maxheight) {
	this.unselected.setBorder(new EmptyBorder(1, 1, 1, 1));
	this.selected.setBorder(new EmptyBorder(1, 1, 1, 1));
	this.unselected.setVisibleRowCount(maxheight);
	this.selected.setVisibleRowCount(maxheight);
	this.unselectedLabel = unselectedLabel;
	this.selectedLabel = selectedLabel;
	setContents(unselected, selected);
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Returns a list of all the items in the selected list
     */
    public Object[] getSelectedItems() {
	return ((DefaultListModel)selected.getModel()).toArray();
    }

    /**
     * Returns a list of all the unselected items
     */
    public Object[] getUnselectedItems() {
	return ((DefaultListModel)unselected.getModel()).toArray();
    }

    /**
     * Adds an element to our selected list
     */
    public void addSelectedItem(Object elem) {
	((DefaultListModel)this.selected.getModel()).addElement(elem);
	arrangeComps();
    }

    /**
     * Adds an element to our unselected list
     */
    public void addUnselectedItem(Object elem) {
	((DefaultListModel)this.unselected.getModel()).addElement(elem);
	arrangeComps();
    }

    /**
     * Removes an item from us; checks both the selected and unselected lists
     */
    public void removeItem(Object elem) {
	((DefaultListModel)this.selected.getModel()).removeElement(elem);
	((DefaultListModel)this.unselected.getModel()).removeElement(elem);
	arrangeComps();
    }

    /**
     * Remove all existing elements
     */
    public void removeAllItems() {
    	((DefaultListModel)this.selected.getModel()).removeAllElements();
    	((DefaultListModel)this.unselected.getModel()).removeAllElements();
    }
    
    /**
     * Sets the contents of the two lists. Either list can be null, which
     * will clear the list.
     */
    public void setContents(Object[] unselected, Object[] selected) {
	// clear all of our contents and add the new ones
	((DefaultListModel)this.selected.getModel()).removeAllElements();
	((DefaultListModel)this.unselected.getModel()).removeAllElements();
	addContents(unselected, selected);
    }

    /**
     * Adds contents to the two lists. Either of the lists can be null, in
     * which case no elements are added to that list.
     */
    public void addContents(Object[] unselected, Object[] selected) {
	if(unselected != null)
	    for(int i = 0; i < unselected.length; i++)
		addUnselectedItem(unselected[i]);
	if(selected != null)
	    for(int i = 0; i < selected.length; i++)
		addSelectedItem(selected[i]);
	arrangeComps();
    }

    public void loadContents(Object[] unselected_elems, Object[] selected_elems) {
    	// clear all of our contents and add the new ones
    	((DefaultListModel)this.selected.getModel()).removeAllElements();
    	((DefaultListModel)this.unselected.getModel()).removeAllElements();
    	if(unselected_elems != null)
    	    for(int i = 0; i < unselected_elems.length; i++)
    	    	((DefaultListModel)this.unselected.getModel()).addElement(unselected_elems[i]);
    	if(selected_elems != null)
    	    for(int i = 0; i < selected_elems.length; i++)
    	    	((DefaultListModel)this.selected.getModel()).addElement(selected_elems[i]);
    	
    	//System.out.println(((DefaultListModel)this.unselected.getModel()).getSize());
    	//System.out.println(((DefaultListModel)this.selected.getModel()).getSize());
    	
    	setContents(unselected_elems, selected_elems);
    	
    	selected.ensureIndexIsVisible(((DefaultListModel)this.selected.getModel()).getSize());
    	unselected.ensureIndexIsVisible(((DefaultListModel)this.unselected.getModel()).getSize());
    	
    	unselected.setModel(((DefaultListModel)this.unselected.getModel()));
    	selected.setModel(((DefaultListModel)this.selected.getModel()));
    }
    
    
    //*************************************************************************
    // private methods
    //*************************************************************************
    
    /**
     * Arrange all of our components to look nice and pretty. Refresh what
     * we look like, afterwards.
     */
    private void arrangeComps() {
	// clear out all of our old contents
	removeAll();
	
	// make the panel to hold our buttons
	JButton add      = new JButton("add");
	JButton remove   = new JButton("remove");
	JPanel bPanel = new JPanel(new BorderLayout());
	bPanel.add("North", add);
	bPanel.add("South", remove);

	// and panels to hold our lists
	JPanel   selectedPanel = new JPanel(new BorderLayout(5, 5));
	JPanel unselectedPanel = new JPanel(new BorderLayout(5, 5));
	selectedPanel.add("North",   new JLabel(selectedLabel, JLabel.CENTER));
	unselectedPanel.add("North", new JLabel(unselectedLabel,JLabel.CENTER));

	// for moving items between the two lists
	add.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    select();
		}
	    });
	remove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    unselect();
		}
	    });

	//put in all of our components
	setLayout(new GridLayout(1, 0, 5, 5));

	// to avoid weird size problems, we only put a List into a scroller
	// if it actually has contents
	if(((DefaultListModel)this.unselected.getModel()).getSize() == 0)
	    unselectedPanel.add("Center", this.unselected);
	else
	    unselectedPanel.add("Center",
				new JScrollPane(this.unselected,
				    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	add(unselectedPanel);
 	add(Utils.centerComp(bPanel));


	if(((DefaultListModel)this.selected.getModel()).getSize() == 0)
	    selectedPanel.add("Center", this.selected);
	else
	    selectedPanel.add("Center",
			      new JScrollPane(this.selected,
				  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	add(selectedPanel);

	// refresh what we look like
	invalidate();
	validate();
    }

    /**
     * Moves the highlighted items in the unselected list to the selected list
     */
    private void select() {
	if(unselected.getSelectedIndex() != -1)
	    transferSelected(unselected, selected);
    }

    /**
     * Moves the highlighted items in the selected list to the unselected list
     */
    private void unselect() {
	if(selected.getSelectedIndex() != -1)
	    transferSelected(selected, unselected);
    }

    /**
     * Transfers the selected items in one list to the other list
     */
    private void transferSelected(JList from, JList to) {
	// the values we're transferring over
	Object[] new_vals = from.getSelectedValues();
	for(int i = 0; i < new_vals.length; i++) {
	    ((DefaultListModel)to.getModel()).addElement(new_vals[i]);
	    ((DefaultListModel)from.getModel()).removeElement(new_vals[i]);
	}

	// set the newly moved items as the selected items
	int[] index = new int[new_vals.length];
	for(int i = 0; i < index.length; i++)
	    index[i] = ((DefaultListModel)to.getModel()).indexOf(new_vals[i]);
	to.setSelectedIndices(index);

	// redisplay everything
	arrangeComps();
    }
}
