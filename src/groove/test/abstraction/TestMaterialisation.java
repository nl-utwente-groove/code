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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.abstraction.Materialisation;
import groove.abstraction.Multiplicity;
import groove.abstraction.PreMatch;
import groove.abstraction.Shape;
import groove.abstraction.ShapeEdge;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMaterialisation {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    @BeforeClass
    public static void setUp() {
        Multiplicity.initMultStore();
    }

    @Test
    public void testMaterialisation0() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            HostGraph graph =
                view.getGraphView("materialisation-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertEquals(6, mats.size());
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    int binaryEdgeCount = getBinaryEdges(matShape).size();
                    assertTrue((matShape.nodeSet().size() == 5 && binaryEdgeCount == 4)
                        || (matShape.nodeSet().size() == 6 && binaryEdgeCount == 7));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaterialisation1() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            HostGraph graph =
                view.getGraphView("materialisation-test-1").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertEquals(3, mats.size());
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    int binaryEdgeCount = getBinaryEdges(matShape).size();
                    assertTrue((matShape.nodeSet().size() == 4 && binaryEdgeCount == 3)
                        || (matShape.nodeSet().size() == 3 && binaryEdgeCount == 2));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaterialisation2() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            HostGraph graph =
                view.getGraphView("materialisation-test-2").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertEquals(6, mats.size());
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    int binaryEdgeCount = getBinaryEdges(matShape).size();
                    assertTrue((matShape.nodeSet().size() == 5 && binaryEdgeCount == 3)
                        || (matShape.nodeSet().size() == 6 && binaryEdgeCount == 4));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRuleApplicationAndShapeNormalisation() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            HostGraph graph = view.getGraphView("rule-app-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("add");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                assertEquals(2, mats.size());
                for (Materialisation mat : mats) {
                    assertEquals(5, mat.getShape().nodeSet().size());
                    assertEquals(7, getBinaryEdges(mat.getShape()).size());
                    Shape result = mat.applyMatch();
                    assertEquals(6, result.nodeSet().size());
                    assertEquals(8, getBinaryEdges(result).size());
                    Shape normalisedShape = result.normalise();
                    assertEquals(4, normalisedShape.nodeSet().size());
                    assertEquals(5, getBinaryEdges(normalisedShape).size());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private Set<ShapeEdge> getBinaryEdges(Shape shape) {
        Set<ShapeEdge> result = new HashSet<ShapeEdge>();
        for (ShapeEdge edge : shape.edgeSet()) {
            if (edge.isBinary()) {
                result.add(edge);
            }
        }
        return result;
    }
}
