/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.rel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.plain.PlainEdge;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.match.automaton.NodeRelation;
import nl.utwente.groove.match.automaton.NodeRelation.Entry;
import nl.utwente.groove.util.AIGenerated;

/**
 * Regression tests for {@link NodeRelation}: entry equality is based on the
 * related pair of nodes, so that duplicate pairs merge their support and
 * {@link NodeRelation#doTransitiveClosure()} terminates (under the former
 * identity-based entry equality, it looped forever on any composable relation).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class NodeRelationTest {
    private final PlainGraph graph = new PlainGraph("test", GraphRole.NONE);
    private final PlainNode a = this.graph.addNode();
    private final PlainNode b = this.graph.addNode();
    private final PlainNode c = this.graph.addNode();

    /** Duplicate pairs collapse to a single entry with merged support. */
    @Test
    public void testDuplicatePairsMerge() {
        PlainEdge e1 = this.graph.addEdge(this.a, "x", this.b);
        PlainEdge e2 = this.graph.addEdge(this.a, "y", this.b);
        NodeRelation rel = new NodeRelation();
        assertTrue(rel.addRelated(e1));
        // same pair: not a new entry, but its support is extended
        assertTrue(rel.addRelated(e2));
        // now nothing changes any more
        assertFalse(rel.addRelated(e1));
        assertEquals(1, rel.getAllRelated().size());
        Entry entry = rel.getAllRelated().iterator().next();
        assertTrue(entry.support().contains(e1));
        assertTrue(entry.support().contains(e2));
    }

    /** Transitive closure of a two-edge chain terminates and adds the composed pair. */
    @Test
    public void testTransitiveClosure() {
        PlainEdge ab = this.graph.addEdge(this.a, "x", this.b);
        PlainEdge bc = this.graph.addEdge(this.b, "x", this.c);
        NodeRelation rel = new NodeRelation();
        rel.addRelated(ab);
        rel.addRelated(bc);
        assertTrue(rel.doTransitiveClosure());
        assertEquals(3, rel.getAllRelated().size());
        // the composed pair is supported by both composing edges
        Entry ac = findEntry(rel, this.a, this.c);
        assert ac != null : "closure should have added the composed (a,c) pair";
        assertTrue(ac.support().contains(ab));
        assertTrue(ac.support().contains(bc));
        // the relation is now closed
        assertFalse(rel.doTransitiveClosure());
    }

    /** Transitive closure of a self-loop terminates without adding entries. */
    @Test
    public void testSelfLoopClosure() {
        PlainEdge loop = this.graph.addEdge(this.a, "x", this.a);
        NodeRelation rel = new NodeRelation();
        rel.addRelated(loop);
        assertFalse(rel.doTransitiveClosure());
        assertEquals(1, rel.getAllRelated().size());
    }

    /** Returns the entry relating the given nodes, if any. */
    private @Nullable Entry findEntry(NodeRelation rel, Node source, Node target) {
        for (Entry entry : rel.getAllRelated()) {
            if (entry.source().equals(source) && entry.target().equals(target)) {
                return entry;
            }
        }
        return null;
    }
}
