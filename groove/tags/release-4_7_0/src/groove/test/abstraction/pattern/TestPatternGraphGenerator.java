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
package groove.test.abstraction.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.abstraction.pattern.explore.PatternGraphGenerator;
import groove.abstraction.pattern.lts.PGTS;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPatternGraphGenerator {

    private static final int VERBOSITY = 0;

    private String[] getArgs(String grammar, String startGraph, String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", grammar, startGraph,
            typeGraph};
    }

    @Test
    public void testCircList() {
        final String GRAMMAR = "junit/pattern/circ-list-4";
        final String START_GRAPH = "start";
        String typeGraph = "ptgraph.gxl";
        PatternGraphGenerator generator;

        generator =
            new PatternGraphGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS pgts = generator.getPGTS();
        assertEquals(6, pgts.getStateCount());
        assertEquals(12, pgts.getTransitionCount());
        assertTrue(generator.compareGTSs());

        typeGraph = "ptgraph-min.gxl";
        generator =
            new PatternGraphGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        pgts = generator.getPGTS();
        assertEquals(6, pgts.getStateCount());
        assertEquals(12, pgts.getTransitionCount());
        assertTrue(generator.compareGTSs());
    }

    @Test
    public void testTrains() {
        final String GRAMMAR = "junit/pattern/trains";
        final String START_GRAPH = "start";
        String typeGraph = "ptgraph.gxl";
        PatternGraphGenerator generator;

        generator =
            new PatternGraphGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS pgts = generator.getPGTS();
        assertEquals(11, pgts.getStateCount());
        assertEquals(12, pgts.getTransitionCount());
        assertTrue(generator.compareGTSs());
    }

}
