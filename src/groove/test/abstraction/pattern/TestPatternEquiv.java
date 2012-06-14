/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.test.abstraction.pattern;

import static org.junit.Assert.assertEquals;
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.shape.PatternEquivRel;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.PatternGraphGrammar;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPatternEquiv {

    private static final int VERBOSITY = 0;

    private String[] getArgs(String grammar, String startGraph, String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", grammar, startGraph,
            typeGraph};
    }

    @Test
    public void testEquivalence() {
        final String GRAMMAR = "junit/pattern/equiv.gps/";
        final String TYPE = "ptgraph.gxl";

        File grammarFile = new File(GRAMMAR);
        File typeGraphFile = new File(GRAMMAR + TYPE);
        TypeGraph typeGraph = null;
        GraphGrammar sGrammar = null;
        try {
            typeGraph =
                TypeGraphJaxbGxlIO.getInstance().unmarshalTypeGraph(
                    typeGraphFile);
            sGrammar = GrammarModel.newInstance(grammarFile, false).toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        PatternGraphGrammar pGrammar =
            new PatternGraphGrammar(sGrammar, typeGraph);
        PatternShape pShape = new PatternShape(pGrammar.getStartGraph());
        PatternEquivRel peq = new PatternEquivRel(pShape);

        assertEquals(9, peq.getNodeEquivRel().size());
        assertEquals(8, peq.getEdgeEquivRel().size());
    }

}
