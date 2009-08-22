package groove.test;

import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.io.FileGps;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.GenericGrammarView;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

@SuppressWarnings("all")
public class CalculatorTest extends TestCase {
    /** Location of the samples. */
    static public final String INPUT_DIR = "C:/files/work/groove_cvs/samples";

    /** Tests the append sample. */
    public void testCalculator() throws FormatException {

        GenericGrammarView<?,?> view = loadGrammar("ferryman.gps", "start");
        GraphGrammar gg = view.toGrammar();

        GraphCalculator calc = new DefaultGraphCalculator(gg);
        GraphState result = calc.getFinal();
        assertNull(result);
    }

    private GenericGrammarView<?,?> loadGrammar(String grammarName,
            String startGraphName) {
        try {
            return this.loader.unmarshal(new File(INPUT_DIR, grammarName),
                startGraphName);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
//        catch (FormatException exc) {
//            throw new RuntimeException(exc);
//        }
    }

    /**
     * Grammar loader used in this test case.
     */
    protected FileGps loader = new FileGps(false);
}
