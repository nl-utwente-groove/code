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
import groove.abstraction.pattern.PatternAbsParam;
import groove.abstraction.pattern.explore.PatternShapeGenerator;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PSTS;

import org.junit.After;
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

    @After
    public void restoreMultiplicitySettings() {
        PatternAbsParam.getInstance().setUseThreeValues(false);
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

        typeGraph = "ptgraph-larger.gxl";
        generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(14, psts.getStateCount());
        assertEquals(29, psts.getTransitionCount());
    }

    @Test
    public void testCircList0() {
        final String GRAMMAR = "junit/pattern/match-test";
        final String START_GRAPH = "start-5";
        final String typeGraph = "ptgraph.gxl";
        PatternShapeGenerator generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS psts = generator.getPGTS();
        assertEquals(40, psts.getStateCount());
        assertEquals(589, psts.getTransitionCount());
    }

    @Test
    public void testCircList1() {
        final String GRAMMAR = "junit/pattern/circ-list-4";
        final String START_GRAPH = "start";
        final String typeGraph = "ptgraph.gxl";
        PatternShapeGenerator generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS psts = generator.getPGTS();
        assertEquals(60, psts.getStateCount());
        assertEquals(393, psts.getTransitionCount());
    }

    @Test
    public void testEuler0() {
        final String GRAMMAR = "junit/pattern/euler-0";
        final String START_GRAPH = "start";
        final String typeGraph = "ptgraph-0.gxl";
        PatternShapeGenerator generator;
        PGTS psts;

        // EDUARDO: Fix the code so the commented tests pass again...

        /*PatternAbsParam.getInstance().setNodeMultBound(1);
        PatternAbsParam.getInstance().setEdgeMultBound(1);
        generator =
            new PatternShapeGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(30, psts.getStateCount());
        assertEquals(63, psts.getTransitionCount());*/

        /*PatternAbsParam.getInstance().setNodeMultBound(2);
        PatternAbsParam.getInstance().setEdgeMultBound(1);
        generator =
            new PatternShapeGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(50, psts.getStateCount());
        assertEquals(98, psts.getTransitionCount());*/

        /*PatternAbsParam.getInstance().setNodeMultBound(1);
        PatternAbsParam.getInstance().setEdgeMultBound(2);
        generator =
            new PatternShapeGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(155, psts.getStateCount());
        assertEquals(428, psts.getTransitionCount());*/

        /*PatternAbsParam.getInstance().setNodeMultBound(2);
        PatternAbsParam.getInstance().setEdgeMultBound(2);
        generator =
            new PatternShapeGenerator(getArgs(GRAMMAR, START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(202, psts.getStateCount());
        assertEquals(525, psts.getTransitionCount());*/

        generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        psts = generator.getPGTS();
        assertEquals(16, psts.getStateCount());
        assertEquals(22, psts.getTransitionCount());
    }

    @Test
    public void testEuler1() {
        final String GRAMMAR = "junit/pattern/euler-1";
        final String START_GRAPH = "start";
        final String typeGraph = "ptgraph.gxl";
        PatternShapeGenerator generator =
            new PatternShapeGenerator(getArgsWithThreeValue(GRAMMAR,
                START_GRAPH, typeGraph));
        generator.processArguments();
        generator.explore();
        PGTS psts = generator.getPGTS();
        assertEquals(11, psts.getStateCount());
        assertEquals(19, psts.getTransitionCount());
    }

}
