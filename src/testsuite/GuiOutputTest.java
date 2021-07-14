//*****************************************************************************
//
// GuiOutputTest.java
//
// This is just a small test to make sure that the GuiOutputStream is working.
//
//*****************************************************************************
package testsuite;
import java.io.PrintStream;
import logger.GuiOutputStream;
import javax.swing.*;
import java.awt.Dimension;
public class GuiOutputTest {
    public static void main(String args[]) {
	// make the JTextArea
	JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

	// and the scrollpane to contain it
	JScrollPane scroller = new JScrollPane(area);
	scroller.setEnabled(false);
	scroller.setPreferredSize(new Dimension(800, 600));

	// a panel...
        JPanel panel = new JPanel();
        panel.add(scroller);
	
	// and, finally, a frame to toss it all in so we can display
	JFrame frame = new JFrame("GuiOutputTest");
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);	

	// display it all
        frame.pack();
        frame.setVisible(true); 

	// make all of the classes we need
	agent.s_expression.SymbolicFactory factory = 
	    new agent.s_expression.SymbolicFactory();
	breeder.MedianPassBreeder breeder          = 
	    new breeder.MedianPassBreeder(factory);
	fitness.PctHitFitnessFunction fitness      = 
	    new fitness.PctHitFitnessFunction("TARGET");
	problem.VariableSetProblem problem         = 
	    new problem.VariableSetProblem();

	// set up our symbolic factory for use
	factory.addSymbol(new agent.s_expression.AddSymbol());
	factory.addSymbol(new agent.s_expression.MultiplySymbol());
	factory.addSymbol(new agent.s_expression.DivisionSymbol());
	factory.addVariable("A");
	factory.addVariable("B");
	factory.addVariable("C");

	// initialize our problem
	for(int i = 0; i < 1000; i++) {
	    // the problem is:
	    //   TARGET = A*B + A*B*C + A*C + C + 15
	    // where:
	    //   A = i + 1
	    //   B = 5
	    //   C = i
	    problem.VariableProblem prob = new problem.VariableProblem();
	    int a = i+1;
	    int b = 5;
	    int c = i;
	    prob.put("TARGET", new Double(a*b + a*b*c + a*c + c + 15));
	    prob.put("A",      new Double(a));
	    prob.put("B",      new Double(b));
	    prob.put("C",      new Double(c));
	    problem.add(prob);
	}

	logger.BasicLog log                        =
	    new logger.BasicLog(new PrintStream(new GuiOutputStream(area)), 
				"tmplogfile", fitness, problem.copy());

	// start up a BasicMode
	mode.Mode mode = new mode.BasicMode(breeder, fitness, problem, log, 
		1, 100,
		new stopcondition.GenerationStopCondition(10000));
	mode.start();
    }
}
