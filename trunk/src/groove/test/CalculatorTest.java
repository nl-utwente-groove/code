package groove.test;

import static org.junit.Assert.assertNull;
import groove.lts.GraphState;
import groove.samples.calc.DefaultGraphCalculator;
import groove.samples.calc.GraphCalculator;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.GrammarModel;

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
        GrammarModel view = loadGrammar("ferryman.gps", "start");
        GraphGrammar gg = view.toGrammar();
        GraphCalculator calc = new DefaultGraphCalculator(gg);
        GraphState result = calc.getFinal();
        assertNull(result);
    }

    private GrammarModel loadGrammar(String grammarName,
            String startGraphName) {
        try {
            return GrammarModel.newInstance(new File(INPUT_DIR,
                grammarName), false);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
