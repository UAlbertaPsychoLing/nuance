//*****************************************************************************
//
// NuanceUI.java
//
// Just a simple interface so that startup can handle multiple UI types. All
// interfaces (i.e. graphical and textual) should implement a display() 
// function that displays the interface and (eventually) starts evolution
// running.
//
//*****************************************************************************
package ui;
public interface NuanceUI {
    /**
     * Display UI for the first time.
     */
    public abstract void display();
}
