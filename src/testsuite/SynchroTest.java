//*****************************************************************************
//
// SynchroTest.java
//
// My little experiment with synchronization
//
//
//*****************************************************************************
package testsuite;
public class SynchroTest {
    public static void main(String args[]) {
	Box box = new Box();
	Client foo = new Client("foo", box, 505);
	Client bar = new Client("bar", box, 1000);
	foo.start();
	bar.start();
    }

    public static class Box {
	int    content = 0;
	boolean locked = false;
	public int get() {
	    return content;
	}
	public synchronized void put2(int val) {
	    while(locked) {
		try { wait(); }
		catch(Exception e) { }
	    } locked = true;
	    System.out.println("put2");
	    content = val;
	    locked = false;
	    notifyAll();
	}

	public synchronized void put(int val) {
	    while(locked) {
		try { wait(); }
		catch(Exception e) { }
	    } locked = true;
	    System.out.println("put");
	    content = val;
	    locked = false;
	    notifyAll();
	}
    }

    public static class Client extends Thread {
	String name;
	Box     box;
	int    wait;
	public Client(String name, Box box, int wait) {
	    this.name = name;
	    this.box  = box;
	    this.wait = wait;
	}
	public void run() {
	    while(true) {
		int val = box.get();
		System.out.println(name + " got " + val);
		if(name.equalsIgnoreCase("foo"))
		    box.put(val + 1);
		else
		    box.put2(val + 1);
		try { sleep(wait); }
		catch(Exception e) { }
	    }
	}
    }
}
