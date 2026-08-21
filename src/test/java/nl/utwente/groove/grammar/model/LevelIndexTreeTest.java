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

import static nl.utwente.groove.grammar.model.LevelFixture.strings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import nl.utwente.groove.grammar.Condition.Op;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.util.AIGenerated;

/**
 * Direct tests of {@link LevelIndexTree} (gh #893): the tree of
 * quantification level indices derived from the nesting aspects of a rule,
 * with its lookup maps from nesting nodes and quantifier names to indices and
 * from universal indices to match count nodes.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class LevelIndexTreeTest {
    /** Returns the indices of a tree as a list, in tree order. */
    private static List<Index> indices(LevelIndexTree tree) {
        return List.copyOf(tree.getIndices());
    }

    /** Two directly nested universal levels: the tree is a chain, with an
     * implicit existential level inserted under the childless inner universal. */
    @Test
    public void testNestedUniversals() {
        LevelFixture f = LevelFixture.loadValid("regression", "nestedForall");
        LevelIndexTree tree = f.tree();
        List<Index> indices = indices(tree);
        assertEquals(List.of("[]", "[0]", "[0, 0]", "[0, 0, 0]"), strings(indices));
        Index top = indices.get(0);
        Index outer = indices.get(1);
        Index inner = indices.get(2);
        Index implicit = indices.get(3);
        // tree structure
        assertSame(top, tree.getTopLevelIndex());
        assertTrue(top.isTopLevel());
        assertNull(top.getParent());
        assertSame(top, outer.getParent());
        assertSame(outer, inner.getParent());
        assertSame(inner, implicit.getParent());
        // operators
        assertEquals(Op.EXISTS, top.getOperator());
        assertEquals(Op.FORALL, outer.getOperator());
        assertEquals(Op.FORALL, inner.getOperator());
        assertEquals(Op.EXISTS, implicit.getOperator());
        // the implicit level has no nesting node and is positive; a plain
        // forall: is not
        assertNull(implicit.getLevelNode());
        assertTrue(implicit.isPositive());
        assertFalse(outer.isPositive());
        assertFalse(inner.isPositive());
        // universality is inherited
        assertFalse(top.isUniversal());
        assertTrue(outer.isUniversal());
        assertTrue(implicit.isUniversal());
        // names
        assertEquals("nestedForall", top.getName());
        assertEquals("nestedForall[0]", outer.getName());
        assertEquals("nestedForall[0,0,0]", implicit.getName());
        // nesting node lookup
        assertSame(f.node("n0"), outer.getLevelNode());
        assertSame(f.node("n1"), inner.getLevelNode());
        assertSame(outer, tree.getIndex(f.node("n0")));
        assertSame(inner, tree.getIndex(f.node("n1")));
        assertNull(tree.getIndex(f.node("n2")));
        assertTrue(tree.getMatchCountMap().isEmpty());
    }

    /** A named universal level is found by its quantifier name; its match
     * count node (here created by normalising a {@code test:} on the count)
     * is registered in the match count map. */
    @Test
    public void testNamedLevelWithCount() {
        LevelFixture f = LevelFixture.loadValid("regression", "nestedCount");
        LevelIndexTree tree = f.tree();
        AspectNode flower = f.node("n1");
        Index flowerIndex = tree.getIndex("flower");
        assertNotNull(flowerIndex);
        assertSame(tree.getIndex(flower), flowerIndex);
        assertEquals("[0, 0]", flowerIndex.toString());
        assertNull(tree.getIndex("plant"));
        Map<Index,AspectNode> countMap = tree.getMatchCountMap();
        assertEquals(1, countMap.size());
        assertSame(flower.getMatchCount(), countMap.get(flowerIndex));
    }

    /** A quantifier name on an {@code id:} aspect is a lookup key too. */
    @Test
    public void testNamedLevelById() {
        LevelFixture f = LevelFixture.loadValid("quantLevel", "markTest");
        assertSame(f.tree().getIndex(f.node("n0")), f.tree().getIndex("q"));
    }

    /** Two optional existential levels directly under the top level: both
     * are non-positive, childless and get no implicit sublevel. */
    @Test
    public void testOptionalSiblings() {
        LevelFixture f = LevelFixture.loadValid("existsOptional", "optionals");
        List<Index> indices = indices(f.tree());
        assertEquals(List.of("[]", "[0]", "[1]"), strings(indices));
        for (Index sibling : indices.subList(1, 3)) {
            assertSame(indices.get(0), sibling.getParent());
            assertEquals(Op.EXISTS, sibling.getOperator());
            assertFalse(sibling.isPositive());
            assertFalse(sibling.isUniversal());
        }
        assertSame(f.node("n0"), indices.get(1).getLevelNode());
        assertSame(f.node("n4"), indices.get(2).getLevelNode());
    }

    /** A forall-exists-forall chain of positive quantifiers: the positive
     * flag follows the {@code forallx:}/{@code exists:} aspects, the
     * implicit level goes under the innermost universal, and the count node
     * of that universal is registered. */
    @Test
    public void testPositiveChainWithCount() {
        LevelFixture f = LevelFixture.loadValid("regression", "forallExistsForallCount");
        LevelIndexTree tree = f.tree();
        List<Index> indices = indices(tree);
        assertEquals(List.of("[]", "[0]", "[0, 0]", "[0, 0, 0]", "[0, 0, 0, 0]"), strings(indices));
        assertEquals(List.of(Op.EXISTS, Op.FORALL, Op.EXISTS, Op.FORALL, Op.EXISTS),
                     indices.stream().map(Index::getOperator).toList());
        assertEquals(List.of(false, true, true, true, true),
                     indices.stream().map(Index::isPositive).toList());
        assertSame(f.node("n3"), indices.get(1).getLevelNode());
        assertSame(f.node("n6"), indices.get(2).getLevelNode());
        assertSame(f.node("n4"), indices.get(3).getLevelNode());
        assertNull(indices.get(4).getLevelNode());
        assertEquals(Map.of(indices.get(3), f.node("x5")), tree.getMatchCountMap());
    }
}
