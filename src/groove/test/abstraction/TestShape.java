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

import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.abstraction.Shape;
import groove.abstraction.ShapeEdge;
import groove.abstraction.ShapeNode;
import groove.abstraction.Util;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestShape extends TestCase {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }

    public void testShapeBuild0() {
        File file = new File(DIRECTORY + "shape-build-test-0.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 5);
            assertTrue(Util.getBinaryEdges(shape).size() == 7);
            Multiplicity oneMult = Multiplicity.getMultOf(1);
            for (Edge edge : Util.getBinaryEdges(shape)) {
                ShapeEdge se = (ShapeEdge) edge;
                assertTrue(shape.getEdgeOutMult(se).equals(oneMult));
                assertTrue(shape.getEdgeInMult(se).equals(oneMult));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testShapeBuild1() {
        File file = new File(DIRECTORY + "shape-build-test-1.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 10);
            assertTrue(Util.getBinaryEdges(shape).size() == 12);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testShapeBuild2() {
        File file = new File(DIRECTORY + "shape-build-test-2.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 1);
            assertTrue(Util.getBinaryEdges(shape).size() == 1);
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-3.gst");
        Parameters.setNodeMultBound(3);
        Multiplicity.initMultStore();
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 1);
            assertTrue(Util.getBinaryEdges(shape).size() == 1);
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.getMultOf(3)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-4.gst");
        Parameters.setNodeMultBound(1);
        Multiplicity.initMultStore();
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 1);
            assertTrue(Util.getBinaryEdges(shape).size() == 1);
            ShapeNode node = shape.nodeSet().iterator().next();
            assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testShapeBuild3() {
        File file = new File(DIRECTORY + "shape-build-test-5.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 2);
            assertTrue(Util.getBinaryEdges(shape).size() == 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-6.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 4);
            assertTrue(Util.getBinaryEdges(shape).size() == 6);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(DIRECTORY + "shape-build-test-7.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 2);
            assertTrue(Util.getBinaryEdges(shape).size() == 4);
            for (ShapeNode node : shape.nodeSet()) {
                assertTrue(shape.getNodeMult(node).equals(Multiplicity.OMEGA));
            }
            for (Edge edge : Util.getBinaryEdges(shape)) {
                ShapeEdge se = (ShapeEdge) edge;
                assertTrue(shape.getEdgeOutMult(se).equals(
                    Multiplicity.getMultOf(1)));
                assertTrue(shape.getEdgeInMult(se).equals(Multiplicity.OMEGA));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testShapeBuild4() {
        File file = new File(DIRECTORY + "shape-build-test-8.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            Shape shape = new Shape(graph);
            assertTrue(shape.nodeSet().size() == 3);
            assertTrue(Util.getBinaryEdges(shape).size() == 2);
            for (Edge edge : Util.getBinaryEdges(shape)) {
                ShapeEdge se = (ShapeEdge) edge;
                assertTrue(shape.getEdgeOutMult(se).equals(Multiplicity.OMEGA));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
