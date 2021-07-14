//*****************************************************************************
//
// GuiNuance.java
//
// GuiNuance provides a graphical interface into NUANCE. Buttons, sliders,
// spinners, ohh my! GuiNuance also provides additional features for analyzing
// functions, and such.
//
//*****************************************************************************
package ui.gui;
import  javax.swing.*;
import  java.awt.*;
import  java.awt.event.*;
import  java.io.PrintStream;
import  logger.*;
import  config.*;
import  ui.NuanceUI;
public class GuiNuance extends JFrame implements NuanceUI {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private JTabbedPane   tabs; // where we store all of our windows

    // the set where we keep info about how we start
    private static final String CONFIG_FILE = "config/preferences.txt";



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Make a new GuiNuance. Parse default options from the specifedi 
     * ConfigSet.
     */
    public GuiNuance() {
	//*********************************************************************
	// initialize ourself
	//*********************************************************************
	setTitle("NUANCE v3.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	//*********************************************************************
	// init all of our pieces
	//*********************************************************************
	initComponents();
	initMenu();
    }



    //*************************************************************************
    // implementation of interface methods
    //*************************************************************************
    public void display() {
	pack();
	setVisible(true);
    }



    //*************************************************************************
    // public methods
    //*************************************************************************
    /**
     * Add a new component to the tabbed pane
     */
    public void addTab(String name, Component comp) {
	tabs.addTab(name, comp);
    }

    /**
     * Remove a component from the tabbed pane
     */
    public void removeTab(Component comp) {
	tabs.remove(comp);
	// if we're out of tabs, add a new basic setup window
	if(tabs.getTabCount() == 0)
	    makeSetup();
    }

    /**
     * Swaps the old component for a new one and title
     */
    public void swap(Component old, String name, Component comp) {
	int index = tabs.indexOfComponent(old);
	if(index >= 0) {
	    tabs.setComponentAt(index, comp);
	    tabs.setTitleAt(index, name);
	    tabs.invalidate();
	    tabs.validate();
	}
    }



    //*************************************************************************
    // private methods
    //*************************************************************************
    /**
     * Make a new setup chooser pane
     */
    private void makeSetup() {
	Component c = new SetupChooser(this);
	tabs.addTab("setup", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Makes a new Advanced Setup pane
     */
    private void makeAdvancedSetup() {
	Component c = new AdvancedSetup(this);
	tabs.addTab("advanced setup", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Makes a new Basic Setup pane
     */
    private void makeBasicSetup() {
	Component c = new BasicSetup(this);
	tabs.addTab("basic setup", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Makes a new Connection Setup pane
     */
    private void makeConnectSetup() {
	Component c = new JLabel("Connect", JLabel.CENTER);
	tabs.addTab("connect to host", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Makes a new Setup for evoling neural networks
     */
    private void makeNeuralSetup() {
	Component c = new NeuralNetSetup(this);
	tabs.addTab("neural network setup", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Makes a new output generator pane
     */
    private void makeFunctionEvaluator() {
	Component c = new SExpressionInfoPanel();
	tabs.addTab("function evaluator", c);
	tabs.setSelectedComponent(c);
    }

    /**
     * Closes the tabe that is currently selected
     */
    private void closeCurrentTab() {
	if(tabs.getSelectedIndex() >= 0)
	    removeTab(tabs.getSelectedComponent());
    }

    /**
     * Create all of our components, and add them into us
     */
    private void initComponents() {
	tabs  = new ClosableTabbedPane();//IconClosableTabbedPane();
	tabs.setPreferredSize(new Dimension(1000, 800));
	getContentPane().add(tabs);
	try {
	    ConfigSet set = new ConfigSet(CONFIG_FILE);
	    if(set.containsKey("setup")) {
		if("advanced".equalsIgnoreCase(set.get("setup"))) {
		    makeAdvancedSetup();
		}
	    }
	}
	catch(Exception e) {
	    makeSetup();
	}
    }

    /**
     * Take our frame, and add all of our menu options to it
     */
    private void initMenu() {
	// set up our menu bar
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	// add the "NUANCE" menu option and all of its items
	JMenu menu = new JMenu("Nuance");
	menuBar.add(menu);

	// add a command to the menu that allows a person to
	// start up a new setup pane
	JMenuItem mi = new JMenuItem("New Tab");
	mi.setAccelerator(KeyStroke.getKeyStroke('N',
	  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
	menu.add(mi);
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    makeSetup();
		}
	    });

	// add a command that allows a person to close the current tab
	mi = new JMenuItem("Close Tab");
	mi.setAccelerator(KeyStroke.getKeyStroke('W',
	  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
	menu.add(mi);
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    closeCurrentTab();
		}
	    });

	// add a command for bringing up the output generator
	mi = new JMenuItem("Function Evaluator");
	mi.setAccelerator(KeyStroke.getKeyStroke('E',
	  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
	menu.add(mi);
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    makeFunctionEvaluator();
		}
	    });
    }
}
