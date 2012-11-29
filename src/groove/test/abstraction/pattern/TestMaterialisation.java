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

import groove.abstraction.pattern.PatternAbsParam;
import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.io.xml.PatternShapeGxl;
import groove.abstraction.pattern.io.xml.TypeGraphGxl;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.PatternRule;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;

import org.junit.After;
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

    @After
    public void restoreMultiplicitySettings() {
        PatternAbsParam.getInstance().setUseThreeValues(false);
        PatternAbsParam.getInstance().setNodeMultBound(1);
        PatternAbsParam.getInstance().setEdgeMultBound(1);
    }

    @Test
    public void testMaterialisation0() {
        // EDUARDO: Restore tests.
    }

    /*private void testSingleResult(int testNumber, int nodeCount, int edgeCount,
            boolean hostIsShape) {
        loadTest(testNumber, hostIsShape);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<MatchResult> matches = matcher.findMatches(pShape, null);
        assertEquals(1, matches.size());
        PreMatch preMatch = (PreMatch) matches.get(0).getMatch();
        Collection<Materialisation> mats =
            Materialisation.getMaterialisations(pShape, preMatch);
        assertEquals(1, mats.size());
        Materialisation mat = mats.iterator().next();
        PatternShape matShape = mat.getShape();
        assertEquals(nodeCount, matShape.nodeCount());
        assertEquals(edgeCount, matShape.edgeCount());
    }*/

    /*private void testMultipleResults(int testNumber, int matchCount,
            int nodeCount[], int edgeCount[], boolean hostIsShape) {
        assert nodeCount.length == edgeCount.length;
        int size = nodeCount.length;
        loadTest(testNumber, hostIsShape);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<MatchResult> matches = matcher.findMatches(pShape, null);
        assertEquals(matchCount, matches.size());
        int i = 0;
        for (MatchResult preMatch : matches) {
            Collection<Materialisation> mats =
                Materialisation.getMaterialisations(pShape,
                    (PreMatch) preMatch.getMatch());
            for (Materialisation mat : mats) {
                PatternShape matShape = mat.getShape();
                assertEquals(nodeCount[i], matShape.nodeCount());
                assertEquals(edgeCount[i], matShape.edgeCount());
                i++;
            }
        }
    }*/

    private void loadTest(int testNumber, boolean hostIsShape) {
        final String TYPE_GRAPH = DIRECTORY + "ptgraph-" + testNumber + ".gst";
        final String HOST = "host-" + testNumber + (hostIsShape ? ".gxl" : "");
        final String RULE = "rule-" + testNumber;

        HostGraph sHost = null;
        Rule sRule = null;
        try {
            if (!hostIsShape) {
                sHost = view.getHostModel(HOST).toResource();
            }
            sRule = view.getRuleModel(RULE).toResource();
            pTGraph =
                TypeGraphGxl.getInstance().unmarshalTypeGraph(
                    new File(TYPE_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        pRule = pTGraph.lift(sRule);

        if (!hostIsShape) {
            PatternGraph pHost = pTGraph.lift(sHost);
            pShape = new PatternShape(pHost).normalise();
        } else {
            PatternShapeGxl gxl = new PatternShapeGxl(pTGraph);
            File file = new File(DIRECTORY + HOST);
            pShape = gxl.loadPatternShape(file);
        }
        pShape.setFixed();
    }

}
