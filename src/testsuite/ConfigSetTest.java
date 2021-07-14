//*****************************************************************************
//
// ConfigSetTest.java
//
// A basic test to make sure ConfigSets are working A-OK
//
//*****************************************************************************
package testsuite;
import config.*;
public class ConfigSetTest {
    public static void main(String args[]) throws Exception {
	ConfigSet set = new ConfigSet("testsuite/config.txt");
	System.out.println(set.get("key1"));
	System.out.println(set.get("key2"));
	System.out.println(set.get("key3"));
	System.out.println(set.get("key4"));
	System.out.println(set.get("key5"));

	set = new ConfigSet("testsuite/bad_config.txt");
    }
}
