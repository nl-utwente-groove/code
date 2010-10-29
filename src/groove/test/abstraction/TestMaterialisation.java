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
package groove.test.abstraction;

import groove.abstraction.Materialisation;
import groove.abstraction.Multiplicity;
import groove.abstraction.PreMatch;
import groove.abstraction.Shape;
import groove.abstraction.Util;
import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestMaterialisation extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Multiplicity.initMultStore();
    }

    public void testMaterialisation0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 1);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertTrue(mats.size() == 4);
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    assertTrue((matShape.nodeSet().size() == 5 && Util.getBinaryEdges(
                        matShape).size() == 4)
                        || (matShape.nodeSet().size() == 6 && Util.getBinaryEdges(
                            matShape).size() == 7));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void testMaterialisation1() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-1").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 1);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertTrue(mats.size() == 3);
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    assertTrue((matShape.nodeSet().size() == 4 && Util.getBinaryEdges(
                        matShape).size() == 3)
                        || (matShape.nodeSet().size() == 3 && Util.getBinaryEdges(
                            matShape).size() == 2));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void testMaterialisation2() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-2").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 1);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertTrue(mats.size() == 8 || mats.size() == 7
                    || mats.size() == 6);
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    assertTrue((matShape.nodeSet().size() == 5 && Util.getBinaryEdges(
                        matShape).size() == 3)
                        || (matShape.nodeSet().size() == 6 && Util.getBinaryEdges(
                            matShape).size() == 4));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void testRuleApplicationAndShapeNormalisation() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("rule-app-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("add");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 1);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertTrue(mats.size() == 3 || mats.size() == 2);
                for (Materialisation mat : mats) {
                    assertTrue(mat.getShape().nodeSet().size() == 5);
                    assertTrue(Util.getBinaryEdges(mat.getShape()).size() == 7);
                    /*Shape result = mat.applyMatch();
                    new ShapeDialog(result, "result");
                    assertTrue(result.nodeSet().size() == 6);
                    assertTrue(Util.getBinaryEdges(result).size() == 8);
                    Shape normalisedShape = result.normalise();
                    new ShapeDialog(normalisedShape, "normalised");
                    assertTrue(normalisedShape.nodeSet().size() == 5);
                    assertTrue(Util.getBinaryEdges(normalisedShape).size() == 7);*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }
}
