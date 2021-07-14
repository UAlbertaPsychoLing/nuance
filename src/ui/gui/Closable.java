//*****************************************************************************
//
// Closable.java
//
// The interface for anything "closable". This could be a tab in a special
// tabbed pane that can handle closable windows, or maybe even a stream or
// something.
//
//*****************************************************************************
package ui.gui;
public interface Closable {
    /**
     * The method that is called whenever this object is closed
     */
    public void close();
}
