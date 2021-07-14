//*****************************************************************************
//
// ConnectSetup.java
//
// This sets up a client to connect to a pNUANCE server.
//
//*****************************************************************************
package ui.gui;
import  java.io.PrintStream;
import  javax.swing.*;
import  java.awt.*;
import  java.awt.event.*;
import  logger.*;
import  mode.PNuanceClientMode;
public class ConnectSetup extends JPanel {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private GuiNuance  parent;
    private JTextField   host = new JTextField("nuance.psych.ualberta.ca", 20);



    //*************************************************************************
    // constructors
    //*************************************************************************
    public ConnectSetup(GuiNuance parent) {
	super(new BorderLayout());
	this.parent = parent;

	JPanel p = new JPanel();

	JButton connect = new JButton("connect");
	p.add(new JLabel("I would like to connect to "));
	p.add(host);
	p.add(Utils.makeWrapper(connect, new FlowLayout(FlowLayout.LEFT)));
	add("Center", Utils.centerComp(p));

	connect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    run();
		}
	    });
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    private void run() {
	// make our text window, add the scrollpane, and swap us for it
	// in the GuiNuance
	JTextArea area = new JTextArea();
	area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
	JScrollPane scroller = new JScrollPane(area);
	scroller.setEnabled(false);

	// construct our logger and mode
	Logger logger = 
	    new PNuanceClientLogger(new PrintStream(new GuiOutputStream(area)));
	PNuanceClientMode mode = new PNuanceClientMode(logger, host.getText());

	// make a new nuance panel and swap it in
	NuancePanel np = new NuancePanel(mode);
	np.setLayout(new GridLayout(1, 0));
	np.add(scroller);

	// swap in our new panel
	parent.swap(this, host.getText(), np);

	// start our nuance thread
	mode.start();
    }
}
