//*****************************************************************************
//
// SymbolReplaceTest.java
//
// Tests to make sure the SymbolicFactory.replace function is working properly
//
//*****************************************************************************
package testsuite;
import  agent.s_expression.*;
import  agent.s_expression.SymbolicFactory.MalformedSExpressionException;
public class SymbolReplaceTest {
    public static void main(String args[]) {
	String[] s_expression = {
	    "single_var",
	    "(+ A B)",
	    "(+ A A)",
	    "(+ A A)",
	    "(+ (+ A A) (+ A A))",
	};

	String[] subsymbol = {
	    "single_var",
	    "A",
	    "A",
	    "(+ A A)",
	    "(+ A A)",
	};

	String[] replacement = {
	    "1.0",
	    "B",
	    "(+ A A)",
	    "A",
	    "A",
	};

	// check each one
	for(int i = 0; i < s_expression.length; i++) {
	    try {
		Symbol symbol = SymbolicFactory.replace(
				    SymbolicFactory.parse(s_expression[i]),
				    SymbolicFactory.parse(subsymbol[i]),
				    SymbolicFactory.parse(replacement[i]));
		System.out.println("s-expression: " + s_expression[i] + "\n" +
				   "subsymbol   : " + subsymbol[i]    + "\n" +
				   "replacement : " + replacement[i]  + "\n" +
				   "result      : " + symbol);
		System.out.println("--------------------------------------------------------------------------------");
	    }
	    catch(Exception e) {
		System.err.println("Unexpected Error: " + e.getMessage());
		System.exit(1);
	    }
	}
    }
}
