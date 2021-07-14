//*****************************************************************************
//
// SetupChooser.java
//
// Implements a JPanel that allows users to choose which mode they'd like to
// enter when setting up NUANCE.
//
//*****************************************************************************
package ui.gui;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
public class SetupChooser extends JPanel implements ActionListener {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private GuiNuance     parent;
    private JRadioButton  basic    = new JRadioButton("basic setup");
    private JRadioButton  advanced = new JRadioButton("advanced setup");
    //    private JRadioButton  neural   = new JRadioButton("neural networks");
    private JRadioButton  connect  = new JRadioButton("connect to host");



    //*************************************************************************
    // constructors
    //*************************************************************************
    public SetupChooser(GuiNuance parent) {
	super(new BorderLayout());
	this.parent = parent;
	basic.addActionListener(this);
	advanced.addActionListener(this);
	//	neural.addActionListener(this);
	connect.addActionListener(this);

	// a panel to hold the buttons
	JPanel bPanel = new JPanel(new GridLayout(0, 1));
	bPanel.add(basic);
	bPanel.add(advanced);
	//	bPanel.add(neural);
	bPanel.add(connect);

	// another panel to keep the label above the buttons
	JPanel content = new JPanel(new BorderLayout());
	content.add("North", Utils.makeWrapper(new JLabel("How would you like to proceed?")));
	content.add("South", Utils.makeWrapper(bPanel));

	// toss everything into us, centered
	add("Center", Utils.centerComp(content));
    }



    //*************************************************************************
    // ActionListener methods
    //************************************************************************* 
    /**
     * Figures out which button was pressed, and enters into the appropriate
     *  mode
     */
    public void actionPerformed(ActionEvent e) {
	if(e.getSource() == connect)
	    parent.swap(this, "connect", new ConnectSetup(parent));
	else if(e.getSource() == basic)
	    parent.swap(this, "basic setup", new BasicSetup(parent));
	else if(e.getSource() == advanced) {
	    // advanced setup takes awhile to load... put up a loading message
	    JPanel p = new JPanel(new BorderLayout());
	    Component mssg = Utils.centerComp(new JLabel("Loading..."));
	    p.add("Center", mssg);
	    parent.swap(this, "advanced setup", p);
	    
	    class AdvancedLoader extends Thread {
		private Component toreplace;
		public AdvancedLoader(Component toreplace) {
		    this.toreplace = toreplace;
		}
		public void run() {
		    parent.swap(toreplace, "advanced setup", 
				new AdvancedSetup(parent));
		}
	    }

	    Thread t = new AdvancedLoader(p);
	    t.start();
	}
	//	else if(e.getSource() == neural)
	//	    parent.swap(this, "neurovolve", new NeuralNetSetup(parent));
	else if(e.getSource() == connect)
	    ;
    }
}
