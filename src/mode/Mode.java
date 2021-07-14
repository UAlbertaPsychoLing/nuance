//*****************************************************************************
//
// Mode.java
//
// NUANCE can run in various modes. It can run in a solo mode, a networked
// client mode, and a networked server mode, to name a few. All NUANCE modes
// must extend this class.
//
//*****************************************************************************
package mode;
public abstract class Mode extends Thread {
    /**
     * When the mode needs to be stopped running by the application, it should
     * be able to set this to true, and the thread will stop.
     */
    public volatile boolean interrupt = false;
}
