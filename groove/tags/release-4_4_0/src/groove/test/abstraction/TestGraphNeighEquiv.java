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
import groove.abstraction.GraphNeighEquiv;
import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestGraphNeighEquiv {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    @BeforeClass
    public static void setUp() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }

    @Test
    public void testLevelZeroEquiv() {
        File file = new File(DIRECTORY + "equiv-test-0.gst");
        try {
            HostGraph graph = createHostGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertEquals(0, gne.getRadius());
            assertEquals(4, gne.size());
            HostNode n0 = null, n1 = null, n4 = null;
            Iterator<? extends HostNode> iterator = graph.nodeSet().iterator();
            while (iterator.hasNext()) {
                HostNode n = iterator.next();
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

    @Test
    public void testLevelOneEquiv() {
        File file = new File(DIRECTORY + "equiv-test-1.gst");
        try {
            HostGraph graph = createHostGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertEquals(0, gne.getRadius());
            assertEquals(2, gne.size());
            gne.refineEquivRelation();
            assertEquals(1, gne.getRadius());
            assertEquals(4, gne.size());
            gne.refineEquivRelation();
            assertEquals(2, gne.getRadius());
            assertEquals(6, gne.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLevelTwoEquiv() {
        File file = new File(DIRECTORY + "equiv-test-2.gst");
        try {
            HostGraph graph = createHostGraph(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            assertEquals(0, gne.getRadius());
            assertEquals(2, gne.size());
            gne.refineEquivRelation();
            assertEquals(1, gne.getRadius());
            assertEquals(4, gne.size());
            gne.refineEquivRelation();
            assertEquals(2, gne.getRadius());
            assertEquals(6, gne.size());
            gne.refineEquivRelation();
            assertEquals(3, gne.getRadius());
            assertEquals(7, gne.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HostGraph createHostGraph(File file) throws IOException {
        return new DefaultHostGraph(Groove.loadGraph(file));
    }
}