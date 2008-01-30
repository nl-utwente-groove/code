package groove.test;

import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.io.AspectualViewGps;
import groove.io.GrammarViewXml;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.GrammarView;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class CalculatorTest extends TestCase {
	/** Location of the samples. */
    static public final String INPUT_DIR = "C:/files/work/groove_cvs/samples";
    
    /** Tests the append sample. */
    public void testCalculator() throws FormatException {

    	GrammarView<?,?> view = loadGrammar("ferryman.gps", "start");
    	GraphGrammar gg = view.toGrammar();

    	GraphCalculator calc = new DefaultGraphCalculator(gg);
    	GraphState result = calc.getMax();
    	this.assertNull(result);
    	int numMax = calc.getAllMax().size();
    	
    }
    
    private GrammarView<?,?> loadGrammar(String grammarName, String startGraphName) {
        try {
        	return loader.unmarshal(new File(INPUT_DIR, grammarName), startGraphName);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (FormatException exc) {
            throw new RuntimeException(exc);
        }
    }
   /**
    * Grammar loader used in this test case.
    */
   protected GrammarViewXml<?> loader = new AspectualViewGps();
}
