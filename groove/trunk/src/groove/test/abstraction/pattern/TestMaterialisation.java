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
import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.Materialisation;
import groove.abstraction.pattern.trans.PatternRule;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMaterialisation {

    private static final String DIRECTORY = "junit/pattern/mat-test.gps/";
    private static GrammarModel view;
    private static TypeGraph pTGraph;
    private static PatternShape pShape;
    private static PatternRule pRule;

    @BeforeClass
    public static void setUp() {
        PatternAbstraction.initialise();
        File file = new File(DIRECTORY);
        try {
            view = GrammarModel.newInstance(file, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaterialisation0() {
        testSingleResult(0, 9, 10);
    }

    private void testSingleResult(int testNumber, int nodeCount, int edgeCount) {
        loadTest(testNumber);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<Match> matches = matcher.findMatches(pShape);
        assertEquals(1, matches.size());
        PreMatch preMatch = (PreMatch) matches.get(0);
        Collection<Materialisation> mats =
            Materialisation.getMaterialisations(pShape, preMatch);
        assertEquals(1, mats.size());
        Materialisation mat = mats.iterator().next();
        PatternShape matShape = mat.getShape();
        assertEquals(nodeCount, matShape.nodeCount());
        assertEquals(edgeCount, matShape.edgeCount());
    }

    private void loadTest(int testNumber) {
        final String TYPE_GRAPH = DIRECTORY + "ptgraph-" + testNumber + ".gxl";
        final String HOST = "host-" + testNumber;
        final String RULE = "rule-" + testNumber;

        HostGraph sHost = null;
        Rule sRule = null;
        try {
            sHost = view.getHostModel(HOST).toResource();
            sRule = view.getRuleModel(RULE).toResource();
            pTGraph =
                TypeGraphJaxbGxlIO.getInstance().unmarshalTypeGraph(
                    new File(TYPE_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        PatternGraph pHost = pTGraph.lift(sHost);
        pShape = new PatternShape(pHost).normalise();
        pShape.setFixed();
        pRule = pTGraph.lift(sRule);
    }

}
