// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2026 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.LevelFixture.names;
import static nl.utwente.groove.grammar.model.LevelFixture.strings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.model.LevelDistribution.Level;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Direct tests of {@link LevelDistribution} (gh #893): the assignment of the
 * nodes and edges of a normalised rule graph to its quantification levels,
 * the propagation of edge end nodes and match count nodes to enclosing
 * levels, and the binding of label variables across levels.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class LevelDistributionTest {
    /** Returns the levels of a fixture's distribution as a list, in tree order. */
    private static List<Level> levels(LevelFixture f) throws Exception {
        Map<Index,Level> levelMap = f.distribution().getLevelMap();
        assertEquals(strings(f.tree().getIndices()), strings(levelMap.keySet()));
        return List.copyOf(levelMap.values());
    }

    /** Asserts the sorted node and edge names of a level. */
    private static void assertLevel(Level level, List<String> nodes, List<String> edges) {
        assertEquals("Nodes of level " + level.index, nodes, names(level.modelNodes));
        assertEquals("Edges of level " + level.index, edges, names(level.modelEdges));
    }

    /** Two nested universal levels: elements go to the level of their
     * nesting node, the source node of an edge into the inner level is
     * added to that level as well, and the implicit level stays empty. */
    @Test
    public void testNestedUniversals() throws Exception {
        List<Level> levels = levels(LevelFixture.loadValid("regression", "nestedForall"));
        assertEquals(4, levels.size());
        assertLevel(levels.get(0), List.of(), List.of());
        assertLevel(levels.get(1), List.of("n2"), List.of("n2--type:A-->n2"));
        assertLevel(levels.get(2), List.of("n2", "n3"), List.of("n2--a-->n3", "n3--type:B-->n3"));
        assertLevel(levels.get(3), List.of(), List.of());
        // parent links mirror the index tree
        assertNull(levels.get(0).parent);
        for (int i = 1; i < levels.size(); i++) {
            assertSame(levels.get(i - 1), levels.get(i).parent);
            assertSame(levels.get(i).index, levels.get(i).getIndex());
        }
    }

    /** Two sibling optional levels: each holds its own elements, the
     * top level holds nothing. */
    @Test
    public void testOptionalSiblings() throws Exception {
        List<Level> levels = levels(LevelFixture.loadValid("existsOptional", "optionals"));
        assertLevel(levels.get(0), List.of(), List.of());
        assertLevel(levels.get(1), List.of("n3"),
                    List.of("n3--new:flag:mark-->n3", "n3--type:B-->n3"));
        assertLevel(levels.get(2), List.of("n5"),
                    List.of("n5--new:flag:mark-->n5", "n5--type:A-->n5"));
    }

    /** An edge from a top-level node into a universal level lives on the
     * universal level and pulls its source node down with it, while the
     * top level keeps the node and its own edges. The count node of the
     * universal level is defined at the top level and registered on the
     * universal level. */
    @Test
    public void testEdgeEndNodesAndCount() throws Exception {
        LevelFixture f = LevelFixture.loadValid("forallCount", "countOpen");
        List<Level> levels = levels(f);
        assertLevel(levels.get(0), List.of("n0", "x3"),
                    List.of("n0--new:x-->x3", "n0--type:A-->n0"));
        assertLevel(levels.get(1), List.of("n0", "n2"), List.of("n0--a-->n2", "n2--type:B-->n2"));
        assertLevel(levels.get(2), List.of(), List.of());
        assertNull(levels.get(0).countNode);
        assertSame(f.node("x3"), levels.get(1).countNode);
        assertNull(levels.get(2).countNode);
    }

    /** A count node defined at the top level and used three levels down is
     * added to the intermediate levels, but not to the level using it. */
    @Test
    public void testCountOnIntermediateLevels() throws Exception {
        LevelFixture f = LevelFixture.loadValid("regression", "forallExistsForallCount");
        List<Level> levels = levels(f);
        assertEquals(5, levels.size());
        assertTrue(levels.get(0).modelNodes.contains(f.node("x5")));
        assertTrue(levels.get(1).modelNodes.contains(f.node("x5")));
        assertTrue(levels.get(2).modelNodes.contains(f.node("x5")));
        assertLevel(levels.get(3), List.of("n1", "n2"),
                    List.of("n2--new:mark-->n2", "n2--parent-->n1"));
        assertSame(f.node("x5"), levels.get(3).countNode);
    }

    /** A label variable bound at the top level and used two levels down is
     * recorded, without binders, on the intermediate level. */
    @Test
    public void testVariableParentBinding() throws Exception {
        LevelFixture f = LevelFixture.loadValid("regexpr", "setSublevelVar");
        List<Level> levels = levels(f);
        assertEquals(3, levels.size());
        Map<LabelVar,Set<AspectEdge>> topVars = levels.get(0).modelVars;
        assertEquals(1, topVars.size());
        LabelVar x = topVars.keySet().iterator().next();
        assertEquals("?x", x.toString());
        assertEquals(List.of("n0--flag:?x-->n0"), names(topVars.get(x)));
        assertEquals(Map.of(x, Set.of()), levels.get(1).modelVars);
        assertEquals(List.of("n3--new:flag:?x-->n3"), names(levels.get(2).modelVars.get(x)));
        // the unnamed wildcards bind no variables
        assertLevel(levels.get(1), List.of("n0", "n1"), List.of("n0--?-->n1", "n1--type:B-->n1"));
    }

    /** Asserts that building the distribution of a rule fails with an error
     * containing a given text, and that the error carries a given edge of the
     * normalised graph as context. */
    private static void assertDistributionError(String ruleName, String text, String edge) {
        LevelFixture f = LevelFixture.load("quantLevelErrors", ruleName);
        FormatException exc = assertThrows(FormatException.class, f::distribution);
        assertEquals(1, exc.getErrors().get().size());
        FormatError error = exc.getErrors().iterator().next();
        assertTrue(error.toString(), error.toString().contains(text));
        assertTrue("Error " + error + " does not mention " + edge,
                   names(error.getElements()).contains(edge));
    }

    /** A level name on an ordinary rule edge that names no quantifier is
     * rejected when the edge is distributed. (On {@code let} and {@code test}
     * edges the same error arises earlier, during normalisation.) */
    @Test
    public void testUndefinedLevelName() {
        assertDistributionError("undefEdgeLevel", "Undefined nesting level 'zz'", "n0--use=zz:a-->n0");
    }

    /** An edge between nodes on two sibling universal levels has no level to go to. */
    @Test
    public void testIncompatibleEndNodes() {
        assertDistributionError("siblingEdge", "incompatible nesting", "n2--a-->n3");
    }

    /** A match count node must be defined on a level above the one it counts. */
    @Test
    public void testCountAtOwnLevel() {
        assertDistributionError("countAtOwnLevel", "Match count not defined at appropriate level",
                                "x2"); // the normalised form of the int: node n2
    }

    /** The level tree exposed by the rule model (used by the GUI to colour
     * the levels) has one entry per index, holding that level's nodes and
     * edges; it is absent if compilation fails before the distribution is
     * built, and present if compilation fails in a later phase. */
    @Test
    public void testLevelTree() throws Exception {
        LevelFixture f = LevelFixture.loadValid("regression", "nestedForall");
        Map<Index,Set<AspectElement>> levelTree = f.model().getLevelTree();
        assertNotNull(levelTree);
        assertEquals(strings(f.tree().getIndices()), strings(levelTree.keySet()));
        for (Level level : f.distribution().getLevelMap().values()) {
            Set<AspectElement> expected = new HashSet<>(level.modelNodes);
            expected.addAll(level.modelEdges);
            assertEquals("Elements of level " + level.index, expected,
                         levelTree.get(level.index));
        }
        // normalisation error: no compiler at all
        assertNull(LevelFixture.load("quantLevelErrors", "undefLevel").model().getLevelTree());
        // distribution error: compiler without distribution
        assertNull(LevelFixture.load("quantLevelErrors", "undefEdgeLevel").model().getLevelTree());
        // typing error: the distribution survives
        levelTree = LevelFixture.load("varTypes", "creatorVarNarrowed").model().getLevelTree();
        assertNotNull(levelTree);
        assertEquals(1, levelTree.size());
    }
}
