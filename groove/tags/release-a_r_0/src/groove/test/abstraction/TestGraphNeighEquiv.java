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

import groove.abstraction.GraphNeighEquiv;
import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.graph.Graph;
import groove.graph.Node;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestGraphNeighEquiv extends TestCase {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }

    public void testLevelZeroEquiv() {
        File file = new File(DIRECTORY + "equiv-test-0.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertTrue(gne.getRadius() == 0 && gne.size() == 4);
            Node n0 = null, n1 = null, n4 = null;
            Iterator<? extends Node> iterator = graph.nodeSet().iterator();
            while (iterator.hasNext()) {
                Node n = iterator.next();
                if (n.getNumber() == 0) {
                    n0 = n;
                } else if (n.getNumber() == 1) {
                    n1 = n;
                } else if (n.getNumber() == 4) {
                    n4 = n;
                }
            }
            // Equivalence comparison.
            assertFalse(gne.areEquivalent(n0, n1));
            assertTrue(gne.areEquivalent(n0, n4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testLevelOneEquiv() {
        File file = new File(DIRECTORY + "equiv-test-1.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertTrue(gne.getRadius() == 0 && gne.size() == 2);
            gne.refineEquivRelation();
            assertTrue(gne.getRadius() == 1 && gne.size() == 4);
            gne.refineEquivRelation();
            assertTrue(gne.getRadius() == 2 && gne.size() == 6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testLevelTwoEquiv() {
        File file = new File(DIRECTORY + "equiv-test-2.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertTrue(gne.getRadius() == 0 && gne.size() == 2);
            gne.refineEquivRelation();
            assertTrue(gne.getRadius() == 1 && gne.size() == 4);
            gne.refineEquivRelation();
            assertTrue(gne.getRadius() == 2 && gne.size() == 6);
            gne.refineEquivRelation();
            assertTrue(gne.getRadius() == 3 && gne.size() == 7);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
