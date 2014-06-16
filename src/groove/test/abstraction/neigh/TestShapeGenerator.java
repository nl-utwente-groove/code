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
import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.neigh.lts.AGTS;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShapeGenerator {

    private static final int VERBOSITY = 0;
    private static final String STRATEGY = "shapedfs";

    private String[] getArgs(String grammar, String startGraph) {
        return new String[] {"-v", VERBOSITY + "", "-s", STRATEGY, grammar, startGraph};
    }

    @Test
    public void testSingleLinkList() {
        final String GRAMMAR = "junit/abstraction/single-link-list.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
            AGTS gts = generator.start().reduceGTS();
            assertEquals(6, gts.nodeCount());
            assertEquals(11, gts.edgeCount());

            NeighAbsParam.getInstance().setNodeMultBound(2);
            gts = generator.start().reduceGTS();
            assertEquals(6, gts.nodeCount());
            assertEquals(11, gts.edgeCount());

            NeighAbsParam.getInstance().setNodeMultBound(3);
            gts = generator.start().reduceGTS();
            assertEquals(6, gts.nodeCount());
            assertEquals(11, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTriPartGraph() {
        final String GRAMMAR = "junit/abstraction/tri-part-graph.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
            AGTS gts = ShapeGenerator.execute(getArgs(GRAMMAR, START_GRAPH)).reduceGTS();
            assertEquals(3, gts.nodeCount());
            assertEquals(3, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCircularBuffer0() {
        final String GRAMMAR = "junit/abstraction/circ-buf-0.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            AGTS gts = ShapeGenerator.execute(getArgs(GRAMMAR, START_GRAPH)).reduceGTS();
            assertEquals(26, gts.nodeCount());
            assertEquals(59, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCircularBuffer1() {
        final String GRAMMAR = "junit/abstraction/circ-buf-1.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
            AGTS gts = generator.start().reduceGTS();
            assertEquals(17, gts.nodeCount());
            assertEquals(40, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFirewall() {
        final String GRAMMAR = "junit/abstraction/new-firewall-with-labels.gps";
        final String START_GRAPH = "start-2";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
            AGTS gts = generator.start().reduceGTS();
            assertEquals(8, gts.nodeCount());
            assertEquals(42, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEuler() {
        final String GRAMMAR = "junit/abstraction/euler-counting.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        NeighAbsParam.getInstance().setNodeMultBound(2);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        try {
            generator = new ShapeGenerator(getArgs(GRAMMAR, START_GRAPH));
            AGTS gts = generator.start().reduceGTS();
            assertEquals(36, gts.nodeCount());
            assertEquals(64, gts.edgeCount());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
