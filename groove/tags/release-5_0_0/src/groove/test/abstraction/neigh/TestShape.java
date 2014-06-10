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

import static groove.abstraction.Multiplicity.OMEGA;
import static groove.abstraction.Multiplicity.MultKind.EDGE_MULT;
import static groove.abstraction.Multiplicity.MultKind.NODE_MULT;
import static groove.abstraction.neigh.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.EdgeMultDir.OUTGOING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.abstraction.Multiplicity;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShape {

    static private final String DIRECTORY = "junit/abstraction/basic-tests.gps/";

    @BeforeClass
    public static void setUp() {
        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);
    }

    @Test
    public void testShapeBuild0() {
        File file = new File(DIRECTORY + "shape-build-test-0.gst");
        Shape shape = createShape(file);
        assertEquals(5, shape.nodeSet().size());
        assertEquals(7, Util.getBinaryEdges(shape).size());
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, EDGE_MULT);
        for (ShapeEdge edgeS : Util.getBinaryEdges(shape)) {
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                assertTrue(shape.getEdgeMult(edgeS, direction).equals(one));
            }
        }
    }

    @Test
    public void testShapeBuild1() {
        File file = new File(DIRECTORY + "shape-build-test-1.gst");
        Shape shape = createShape(file);
        assertEquals(10, shape.nodeSet().size());
        assertEquals(12, Util.getBinaryEdges(shape).size());
    }

    @Test
    public void testShapeBuild2() {
        File file = new File(DIRECTORY + "shape-build-test-2.gst");
        Shape shape = createShape(file);
        assertEquals(1, shape.nodeSet().size());
        assertEquals(1, Util.getBinaryEdges(shape).size());
        ShapeNode node = shape.nodeSet().iterator().next();
        Multiplicity twoPlus = Multiplicity.getMultiplicity(2, OMEGA, NODE_MULT);
        assertTrue(shape.getNodeMult(node).equals(twoPlus));

        file = new File(DIRECTORY + "shape-build-test-3.gst");
        NeighAbsParam.getInstance().setNodeMultBound(3);
        shape = createShape(file);
        assertEquals(1, shape.nodeSet().size());
        assertEquals(1, Util.getBinaryEdges(shape).size());
        node = shape.nodeSet().iterator().next();
        Multiplicity three = Multiplicity.getMultiplicity(3, 3, NODE_MULT);
        assertTrue(shape.getNodeMult(node).equals(three));

        file = new File(DIRECTORY + "shape-build-test-4.gst");
        NeighAbsParam.getInstance().setNodeMultBound(1);
        shape = createShape(file);
        assertEquals(1, shape.nodeSet().size());
        assertEquals(1, Util.getBinaryEdges(shape).size());
        node = shape.nodeSet().iterator().next();
        twoPlus = Multiplicity.getMultiplicity(2, OMEGA, NODE_MULT);
        assertTrue(shape.getNodeMult(node).equals(twoPlus));
    }

    @Test
    public void testShapeBuild3() {
        File file = new File(DIRECTORY + "shape-build-test-5.gst");
        Shape shape = createShape(file);
        assertEquals(2, shape.nodeSet().size());
        assertEquals(2, Util.getBinaryEdges(shape).size());

        file = new File(DIRECTORY + "shape-build-test-6.gst");
        shape = createShape(file);
        assertEquals(4, shape.nodeSet().size());
        assertEquals(6, Util.getBinaryEdges(shape).size());

        file = new File(DIRECTORY + "shape-build-test-7.gst");
        shape = createShape(file);
        assertEquals(2, shape.nodeSet().size());
        assertEquals(4, Util.getBinaryEdges(shape).size());
        Multiplicity twoPlus = Multiplicity.getMultiplicity(2, OMEGA, NODE_MULT);
        for (ShapeNode node : shape.nodeSet()) {
            assertTrue(shape.getNodeMult(node).equals(twoPlus));
        }
        twoPlus = Multiplicity.getMultiplicity(2, OMEGA, EDGE_MULT);
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, EDGE_MULT);
        for (ShapeEdge edgeS : Util.getBinaryEdges(shape)) {
            assertTrue(shape.getEdgeMult(edgeS, OUTGOING).equals(one));
            assertTrue(shape.getEdgeMult(edgeS, INCOMING).equals(twoPlus));
        }
    }

    @Test
    public void testShapeBuild4() {
        File file = new File(DIRECTORY + "shape-build-test-8.gst");
        Shape shape = createShape(file);
        assertEquals(3, shape.nodeSet().size());
        assertEquals(2, Util.getBinaryEdges(shape).size());
        Multiplicity twoPlus = Multiplicity.getMultiplicity(2, OMEGA, EDGE_MULT);
        for (ShapeEdge edgeS : Util.getBinaryEdges(shape)) {
            assertTrue(shape.getEdgeMult(edgeS, OUTGOING).equals(twoPlus));
        }
    }

    private Shape createShape(File file) {
        HostGraph graph = createHostGraph(file);
        return Shape.createShape(graph);
    }

    private HostGraph createHostGraph(File file) {
        HostGraph result = null;
        try {
            result = new DefaultHostGraph(Groove.loadGraph(file));
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
        return result;
    }
}
