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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.abstraction.Materialisation;
import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.abstraction.PreMatch;
import groove.abstraction.Shape;
import groove.abstraction.ShapeEdge;
import groove.abstraction.ShapeNode;
import groove.trans.DefaultHostGraph;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.util.Groove;
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
public class TestShape {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    @BeforeClass
    public static void setUp() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }

    @Test
    public void testShapeBuild0() {
        File file = new File(DIRECTORY + "shape-build-test-0.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(5, shape.nodeSet().size());
            assertEquals(7, getBinaryEdges(shape).size());
            Multiplicity oneMult = Multiplicity.getMultOf(1);
            for (ShapeEdge se : getBinaryEdges(shape)) {
                assertTrue(shape.getEdgeOutMult(se).equals(oneMult));
                assertTrue(shape.getEdgeInMult(se).equals(oneMult));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShapeBuild1() {
        File file = new File(DIRECTORY + "shape-build-test-1.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(10, shape.nodeSet().size());
            assertEquals(12, getBinaryEdges(shape).size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShapeBuild2() {
        File file = new File(DIRECTORY + "shape-build-test-2.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(1, shape.nodeSet().size());
            assertEquals(1, getBinaryEdges(shape).size());
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-3.gst");
        Parameters.setNodeMultBound(3);
        Multiplicity.initMultStore();
        try {
            Shape shape = createShape(file);
            assertEquals(1, shape.nodeSet().size());
            assertEquals(1, getBinaryEdges(shape).size());
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.getMultOf(3)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-4.gst");
        Parameters.setNodeMultBound(1);
        Multiplicity.initMultStore();
        try {
            Shape shape = createShape(file);
            assertEquals(1, shape.nodeSet().size());
            assertEquals(1, getBinaryEdges(shape).size());
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShapeBuild3() {
        File file = new File(DIRECTORY + "shape-build-test-5.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(2, shape.nodeSet().size());
            assertEquals(2, getBinaryEdges(shape).size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-6.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(4, shape.nodeSet().size());
            assertEquals(6, getBinaryEdges(shape).size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-7.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(2, shape.nodeSet().size());
            assertEquals(4, getBinaryEdges(shape).size());
            for (ShapeNode node : shape.nodeSet()) {
                assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
            }
            for (ShapeEdge se : getBinaryEdges(shape)) {
                assertTrue(shape.getEdgeOutMult(se).equals(
                    Multiplicity.getMultOf(1)));
                assertTrue(shape.getEdgeInMult(se).equals(Multiplicity.OMEGA));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShapeBuild4() {
        File file = new File(DIRECTORY + "shape-build-test-8.gst");
        try {
            Shape shape = createShape(file);
            assertEquals(3, shape.nodeSet().size());
            assertEquals(2, getBinaryEdges(shape).size());
            for (ShapeEdge se : getBinaryEdges(shape)) {
                assertTrue(shape.getEdgeOutMult(se).equals(Multiplicity.OMEGA));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShapeIso() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            GraphGrammar grammar = view.toGrammar();

            HostGraph graph0 =
                view.getGraphView("materialisation-test-0").toModel();
            Shape shape0 = new Shape(graph0);
            HostGraph graph1 =
                view.getGraphView("materialisation-test-1").toModel();
            Shape shape1 = new Shape(graph1);
            HostGraph graph2 =
                view.getGraphView("materialisation-test-2").toModel();
            Shape shape2 = new Shape(graph2);

            // Basic tests.
            // A shape must be isomorphic to itself.
            assertEquals(shape0, shape0);
            // Compare to a clone.
            assertEquals(shape0, shape0.clone());
            // Two completely different shapes.
            assertFalse(shape0.equals(shape1));
            // Shapes with same graph structure but different multiplicities.
            assertFalse(shape1.equals(shape2));

            // More elaborated tests.
            Rule rule0 = grammar.getRule("add");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape0, rule0);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape0, preMatch);
                for (Materialisation mat : mats) {
                    Shape result = mat.applyMatch();
                    // The shape after rule application is different.
                    assertFalse(shape0.equals(result));
                    Shape normalisedShape = result.normalise();
                    // The shape after normalisation is isomorphic to the
                    // original one.
                    assertTrue(shape0.equals(normalisedShape));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (FormatException e) {
            e.printStackTrace();
            fail();
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

    private Shape createShape(File file) throws IOException {
        HostGraph graph = new DefaultHostGraph(Groove.loadGraph(file));
        return new Shape(graph);
    }
}
