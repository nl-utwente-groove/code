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

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShapeGenerator {

    private static final boolean PRINT_RESULT = false;

    @Test
    public void testSingleLinkList() {
        final String GRAMMAR = "junit/abstraction/single-link-list.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, PRINT_RESULT);
        assertEquals(9, generator.getStateCount());
        assertEquals(17, generator.getTransitionCount());

        Parameters.setNodeMultBound(2);
        generator.reset();
        generator.exploreGrammar(PRINT_RESULT);
        assertEquals(15, generator.getStateCount());
        assertEquals(29, generator.getTransitionCount());

        Parameters.setNodeMultBound(3);
        generator.reset();
        generator.exploreGrammar(PRINT_RESULT);
        assertEquals(21, generator.getStateCount());
        assertEquals(41, generator.getTransitionCount());
    }

    @Test
    public void testTriPartGraph() {
        final String GRAMMAR = "junit/abstraction/tri-part-graph.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, PRINT_RESULT);
        assertEquals(3, generator.getStateCount());
        assertEquals(3, generator.getTransitionCount());
    }

    @Test
    public void testCircularBuffer0() {
        final String GRAMMAR = "junit/abstraction/circ-buf-0.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, PRINT_RESULT);
        assertEquals(54, generator.getStateCount());
        assertEquals(130, generator.getTransitionCount());
    }

    @Test
    public void testCircularBuffer1() {
        final String GRAMMAR = "junit/abstraction/circ-buf-1.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, PRINT_RESULT);
        assertEquals(31, generator.getStateCount());
        assertEquals(85, generator.getTransitionCount());
    }

    @Test
    public void testFirewall() {
        final String GRAMMAR = "junit/abstraction/firewall.gps";
        final String START_GRAPH = "start-2";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, PRINT_RESULT);
        assertEquals(48, generator.getStateCount());
        assertEquals(222, generator.getTransitionCount());

        Parameters.setNodeMultBound(2);
        Parameters.setEdgeMultBound(2);

        generator.reset();
        generator.exploreGrammar(PRINT_RESULT);
        assertEquals(124, generator.getStateCount());
        assertEquals(588, generator.getTransitionCount());

        /*Parameters.setNodeMultBound(3);
        Parameters.setEdgeMultBound(3);

        generator.reset();
        generator.exploreGrammar(false);
        assertEquals(255, generator.getStateCount());
        assertEquals(1225, generator.getTransitionCount());*/
    }

    /*@Test
    public void testHopf() {
        final String GRAMMAR = "junit/abstraction/hopf.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(10, generator.getStateCount());
        assertEquals(14, generator.getTransitionCount());

        Parameters.setNodeMultBound(2);
        Parameters.setEdgeMultBound(2);
        generator.reset();
        generator.exploreGrammar(false);
        assertEquals(99, generator.getStateCount());
        assertEquals(326, generator.getTransitionCount());

        Parameters.setNodeMultBound(3);
        Parameters.setEdgeMultBound(3);
        generator.reset();
        generator.exploreGrammar(false);
        assertEquals(149, generator.getStateCount());
        assertEquals(494, generator.getTransitionCount());
    }*/

}
