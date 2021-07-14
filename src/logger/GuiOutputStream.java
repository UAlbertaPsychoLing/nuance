//*****************************************************************************
//
// GuiOutputStream.java
//
// We'll often be using NUANCE in a GUI environment, and we'll need to be able
// to output text to the screen. That's what this class covers; it lets us
// treat a JTextArea as if it were an OutputStream.
//
//*****************************************************************************
package logger;
import  java.awt.event.MouseEvent;
import  javax.swing.event.MouseInputAdapter;
public class GuiOutputStream extends java.io.OutputStream {
    //*************************************************************************
    // private variables
    //*************************************************************************
    private javax.swing.JTextArea out; // where we send text
    private boolean        autoscroll; // does output scroll when it's added?

    // how much text will our output area hold?
    private static int MAX_CAPACITY = 1000000;



    //*************************************************************************
    // constructors
    //*************************************************************************
    /**
     * Create a new output stream to a JTextArea
     */
    public GuiOutputStream(javax.swing.JTextArea out) {
	this.out        = out;
	this.autoscroll = true;
	out.addMouseListener(new MouseInputAdapter() {
		public void mouseClicked(MouseEvent e) {
		    toggleAutoscroll();
		}
	    });
	this.out.setToolTipText("click to freeze/unfreeze window");
    }



    //*************************************************************************
    // public functions
    //*************************************************************************
    /**
     * sets the value of autoscroll
     */
    public void toggleAutoscroll() {
	autoscroll = (autoscroll ? false : true);
    }



    //*************************************************************************
    // implementation of abstract and interface functions
    //*************************************************************************
    /**
     * Write a single byte to the output area
     */ 
    public void write(int b) {
	byte[] bArray = new byte[1];
	bArray[0] = (byte)b;
	write(bArray, 0, 1);
    }

    public void write(byte[] b) {
	write(b, 0, b.length);
    }
    
    public void write(byte[] b, int off, int len) {
	String str = new String(b, off, len);

	// it starts to become harder for the text area to scroll as we get
	// more and more data in the text area. When it gets to some arbitrarily
	// full point, cut off old portions of it
	if(autoscroll && out.getText().length() >= MAX_CAPACITY)
	    out.setText(out.getText().substring((int)(MAX_CAPACITY * 0.95)));

	out.append(str);
	    
	// if we haven't selected anything, then make sure we scroll by putting
	// the caret at the end of the JTextArea
	if(autoscroll)
	    out.setCaretPosition(out.getText().length());
    }
}
