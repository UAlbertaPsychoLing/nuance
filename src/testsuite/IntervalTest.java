//*****************************************************************************
//
// IntervalTest.java
//
// Just a basic test to make sure interval arithmetic is working properly
//
//*****************************************************************************
package testsuite;
import  problem.Problem;
import  problem.VariableSetProblem;
import  agent.s_expression.SymbolicFactory;
import  agent.s_expression.Symbol;
import  numerics.Range;
public class IntervalTest {
    public static void main(String args[]) throws Exception {
	// open up our dataset
	Problem p = VariableSetProblem.parse("../datasets/pairwise.txt");
	
	// build up a couple expressions and test them all
	checkExpression("ON", p);
	checkExpression("PN", p);
	checkExpression("(+ ON PN)", p);
	checkExpression("(- ON PN)", p);
	checkExpression("(* ON PN)", p);
	checkExpression("(/ ON PN)", p);
	checkExpression("(sqrt ON)", p);
	checkExpression("(cbrt ON)", p);
	checkExpression("(tan ON)",  p);
	checkExpression("(sin ON)",  p);
	checkExpression("(* ON (- ON PN))", p);
	checkExpression("(/ (tan ON) 4)", p);
	checkExpression("4", p);
	checkExpression("(/ 4 4)", p);
    }

    /**
     * checks on s-expression for its range
     */
    private static void checkExpression(String exp, Problem p) {
	try {
	    Symbol sym = SymbolicFactory.parse(exp);
	    Range  rng = sym.range(p);
	    System.out.println(rng.min() + "\t" + rng.max() + "\t" + sym);
	} catch(Exception e) {
	    System.err.println(exp + " : " + e.getMessage());
	}
    }
}
