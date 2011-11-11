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
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShapeGenerator {

    private static final int VERBOSITY = 0;

    private List<String> getArgs(String grammar, String startGraph) {
        String args[] = {"-v", VERBOSITY + "", grammar, startGraph};
        return new LinkedList<String>(Arrays.asList(args));
    }

    @Test
    public void testSingleLinkList() {
        final String GRAMMAR = "junit/abstraction/single-link-list.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);

        generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
        generator.start();
        assertEquals(9, generator.getStateCount());
        assertEquals(17, generator.getTransitionCount());

        Parameters.setNodeMultBound(2);
        generator.explore();
        assertEquals(15, generator.getStateCount());
        assertEquals(29, generator.getTransitionCount());

        Parameters.setNodeMultBound(3);
        generator.explore();
        assertEquals(21, generator.getStateCount());
        assertEquals(41, generator.getTransitionCount());
    }

    @Test
    public void testTriPartGraph() {
        final String GRAMMAR = "junit/abstraction/tri-part-graph.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);

        generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
        generator.start();
        assertEquals(3, generator.getStateCount());
        assertEquals(3, generator.getTransitionCount());
    }

    @Test
    public void testCircularBuffer0() {
        final String GRAMMAR = "junit/abstraction/circ-buf-0.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);

        generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
        generator.start();
        if (ShapeIsoChecker.CHECK_SUBSUMPTION) {
            assertEquals(31, generator.getStateCount());
            assertEquals(65, generator.getTransitionCount());
        } else {
            assertEquals(54, generator.getStateCount());
            assertEquals(130, generator.getTransitionCount());
        }
    }

    @Test
    public void testCircularBuffer1() {
        final String GRAMMAR = "junit/abstraction/circ-buf-1.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);

        generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
        generator.start();
        if (ShapeIsoChecker.CHECK_SUBSUMPTION) {
            assertEquals(31, generator.getStateCount());
            assertEquals(92, generator.getTransitionCount());
        } else {
            assertEquals(50, generator.getStateCount());
            assertEquals(154, generator.getTransitionCount());
        }
    }

    @Test
    public void testFirewall() {
        final String GRAMMAR = "junit/abstraction/firewall.gps";
        final String START_GRAPH = "start-2";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);

        generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
        generator.start();
        if (ShapeIsoChecker.CHECK_SUBSUMPTION) {
            assertEquals(33, generator.getStateCount());
            assertEquals(132, generator.getTransitionCount());
        } else {
            assertEquals(48, generator.getStateCount());
            assertEquals(222, generator.getTransitionCount());
        }

        Parameters.setNodeMultBound(2);
        Parameters.setEdgeMultBound(2);
        generator.explore();
        if (ShapeIsoChecker.CHECK_SUBSUMPTION) {
            assertEquals(80, generator.getStateCount());
            assertEquals(308, generator.getTransitionCount());
        } else {
            assertEquals(124, generator.getStateCount());
            assertEquals(588, generator.getTransitionCount());
        }
    }

}
