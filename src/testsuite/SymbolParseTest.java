//*****************************************************************************
//
// SymbolParseTest.java
//
// This is a small testsuite to ensure that the parse() function in
// SymbolicFactory works properly
//
//*****************************************************************************
package testsuite;
import  agent.s_expression.*;
import  agent.s_expression.SymbolicFactory.MalformedSExpressionException;
public class SymbolParseTest {
    public static void main(String args[]) {
	String[] wellformed = {
	    "single_var",
	    "(+ var1 var2)",
	    "(+ var1 1.0)",
	    "(+ (- var1 -1.0) var2)",
	};
	String[] malformed = {
	    "(+ var1)",
	    "(+ var1 var2 var3)",
	    "(+ var1 var2",
	    "(+ (- var1 var2 var3)",
	    "(+ var1 var2))",
	};

	int errors = 0;
	System.out.println("--- WELLFORMED EXPRESSIONS ---");
	// go through all of our good ones and make sure they parse OK
	for(int i = 0; i < wellformed.length; i++) {
	    try {
		Symbol s = SymbolicFactory.parse(wellformed[i]);
		System.out.println("Correctly parsed: " + wellformed[i] +"\r\n"+
				   "              to: " + s);
	    } catch(MalformedSExpressionException e) {
		System.out.println("Incorrectly threw: "+e.getMessage()+"\r\n" +
				   "For expression   : "+wellformed[i]);
		errors++;
	    }
	}
	
	System.out.println("--- MALFORMED EXPRESSIONS ---");
	// go through all of our bad ones and make sure they DON'T parse OK
	for(int i = 0; i < malformed.length; i++) {
	    try {
		Symbol s = SymbolicFactory.parse(malformed[i]);
		errors++;
		System.out.println("Incorrectly passed: " + malformed[i]);
	    } catch(MalformedSExpressionException e) {
		System.out.println("Correctly threw: "+e.getMessage()+"\r\n" +
				   "For expression : "+malformed[i]);
	    }
	}

	System.out.println("--- REPORT ---");
	System.out.println("errors: " + errors);
    }
}
