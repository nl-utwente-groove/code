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
import groove.abstraction.pattern.explore.PatternShapeGenerator;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PSTS;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPatternShapeGenerator {

    private static final int VERBOSITY = 0;

    private String[] getArgs(String grammar, String startGraph, String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", grammar, startGraph,
            typeGraph};
    }

    private String[] getArgsWithThreeValue(String grammar, String startGraph,
            String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", "-t", grammar, startGraph,
            typeGraph};
    }

    @Test
    public void testLinkedList() {
        final String GRAMMAR = "junit/pattern/pattern-list";
        final String START_GRAPH = "start";
        String typeGraph = "ptgraph.gxl";
        PatternShapeGenerator generator;

        generator =
            new PatternShapeGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PSTS psts = generator.getPGTS();
        assertEquals(12, psts.getStateCount());
        assertEquals(24, psts.getTransitionCount());

        generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(8, psts.getStateCount());
        assertEquals(16, psts.getTransitionCount());
    }

    @Test
    public void testCircList0() {
        final String GRAMMAR = "junit/pattern/match-test";
        final String START_GRAPH = "start-5";
        String typeGraph = "ptgraph.gxl";
        PatternShapeGenerator generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS pgts = generator.getPGTS();
        assertEquals(40, pgts.getStateCount());
        assertEquals(589, pgts.getTransitionCount());
    }

}
