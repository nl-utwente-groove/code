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
package groove.test.abstraction.neigh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.neigh.trans.Materialisation;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleNode;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMaterialisation {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";
    static private GrammarModel view;
    static private GraphGrammar grammar;

    @BeforeClass
    public static void setUp() {
        File file = new File(DIRECTORY);
        try {
            view = GrammarModel.newInstance(file, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaterialisation0a() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0a").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0a");

        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 3
                    && binaryEdgeCount == 2);
            }
        }

        Parameters.setNodeMultBound(1);
        Multiplicity.initMultStore();

        shape = Shape.createShape(graph);
        preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 4
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation0b() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0b").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0b");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 4
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation0c() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0c").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0c");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 6
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation1a() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-1a").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-1a");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 6
                    && binaryEdgeCount == 4);
            }
        }
    }

    @Test
    public void testMaterialisation1b() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-1b").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-1b");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(2, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 9
                    && binaryEdgeCount == 15
                    && matShape.getEquivRelation().size() == 5);
            }
        }
    }

    @Test
    public void testMaterialisation1c() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-1c").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-1c");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(8, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 14
                    && binaryEdgeCount == 32
                    && matShape.getEquivRelation().size() == 6);
            }
        }
    }

    @Test
    public void testMaterialisation2() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-2").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-2");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(2, mats.size());
            boolean gotFive = false;
            boolean gotSix = false;
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int nodeCount = matShape.nodeSet().size();
                if (nodeCount == 5) {
                    gotFive = true;
                } else if (nodeCount == 6) {
                    gotSix = true;
                } else {
                    fail();
                }
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(binaryEdgeCount == 4 || binaryEdgeCount == 7);
            }
            assertTrue(gotFive && gotSix);
        }
    }

    @Test
    public void testMaterialisation3() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-3").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-3");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(6, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 6
                    && binaryEdgeCount == 13
                    && matShape.getEquivRelation().size() == 3);
            }
        }
    }

    @Test
    public void testMaterialisation4() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-4").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-4");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());

        // Ugly hack to create a shape that otherwise can't exist until
        // later in a transformation.
        // BEGIN HACK
        Proof preMatch = preMatches.iterator().next();
        RuleNode nodeR = null;
        for (RuleNode node : preMatch.getPatternMap().nodeMap().keySet()) {
            if (node.getNumber() == 1) {
                nodeR = node;
            }
        }
        ShapeNode source =
            (ShapeNode) preMatch.getPatternMap().nodeMap().get(nodeR);
        ShapeNode target = null;
        for (ShapeNode node : shape.getEquivClassOf(source)) {
            if (!node.equals(source) && shape.getNodeMult(node).isOne()) {
                target = node;
            }
        }
        TypeLabel label = null;
        for (ShapeEdge edge : shape.inEdgeSet(target)) {
            if (edge.getRole() != EdgeRole.BINARY) {
                continue;
            }
            label = edge.label();
        }
        ShapeEdge newEdge = shape.createEdge(source, label, target);
        shape.addEdgeWithoutCheck(newEdge);
        // END HACK

        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape, preMatch);
        assertEquals(2, mats.size());
        boolean gotFour = false;
        boolean gotFive = false;
        for (Materialisation mat : mats) {
            Shape matShape = mat.getShape();
            int nodeCount = matShape.nodeSet().size();
            if (nodeCount == 4) {
                gotFour = true;
            } else if (nodeCount == 5) {
                gotFive = true;
            } else {
                fail();
            }
            int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
            assertTrue(binaryEdgeCount == 3 || binaryEdgeCount == 6);
        }
        assertTrue(gotFour && gotFive);
    }

    @Test
    public void testMaterialisation5() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-5").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-5");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(6, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 7
                    && binaryEdgeCount == 20
                    && matShape.getEquivRelation().size() == 4);
            }
        }
    }

    @Test
    public void testMaterialisation6() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-6").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-6");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 18
                    && binaryEdgeCount == 292
                    && matShape.getEquivRelation().size() == 3);
            }
        }
    }

    @Test
    public void testMaterialisation7() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-7").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-7");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(2, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 5
                    && binaryEdgeCount == 3
                    && matShape.getEquivRelation().size() == 3);
            }
        }
    }

    @Test
    public void testRuleAppAndNormalisation() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("rule-app-test-0").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("add");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                assertEquals(5, matShape.nodeSet().size());
                assertEquals(7, Util.getBinaryEdges(matShape).size());
                Shape result = mat.applyMatch(null).one();
                assertEquals(6, result.nodeSet().size());
                assertEquals(8, Util.getBinaryEdges(result).size());
                Shape normalisedShape = result.normalise();
                assertEquals(4, normalisedShape.nodeSet().size());
                assertEquals(5, Util.getBinaryEdges(normalisedShape).size());
            }
        }
    }
}
