package groove.test;

import static org.junit.Assert.assertNull;
import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.GrammarView;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

@SuppressWarnings("all")
public class CalculatorTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/samples/";

    /** Tests the append sample. */
    @Test
    public void testCalculator() throws FormatException {
        GrammarView view = loadGrammar("ferryman.gps", "start");
        GraphGrammar gg = view.toGrammar();
        GraphCalculator calc = new DefaultGraphCalculator(gg);
        GraphState result = calc.getFinal();
        assertNull(result);
    }

    private GrammarView loadGrammar(String grammarName, String startGraphName) {
        try {
            return StoredGrammarView.newInstance(new File(INPUT_DIR,
                grammarName), false);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
