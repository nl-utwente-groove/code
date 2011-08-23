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

    @Test
    public void testShapeGenerator0() {
        final String GRAMMAR = "junit/samples/abs-single-link-list.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(9, generator.getStateCount());
        assertEquals(17, generator.getTransitionCount());

        Parameters.setNodeMultBound(2);
        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(15, generator.getStateCount());
        assertEquals(29, generator.getTransitionCount());

        Parameters.setNodeMultBound(3);
        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(21, generator.getStateCount());
        assertEquals(41, generator.getTransitionCount());

    }

    @Test
    public void testShapeGenerator1() {
        final String GRAMMAR = "junit/samples/abs-tri-part-graph.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(3, generator.getStateCount());
        assertEquals(3, generator.getTransitionCount());
    }

    @Test
    public void testShapeGenerator2() {
        final String GRAMMAR = "junit/samples/abs-circ-buf-0.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(73, generator.getStateCount());
        assertEquals(204, generator.getTransitionCount());
    }

    /*@Test
    public void testShapeGenerator3() {
        final String GRAMMAR = "junit/samples/abs-circ-buf-1.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(37, generator.getStateCount());
        assertEquals(98, generator.getTransitionCount());
    }*/

}
