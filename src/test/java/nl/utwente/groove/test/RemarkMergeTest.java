/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Tests the invariant that a fixed aspect graph has at most one remark edge
 * per node pair: parallel remark edges are merged into a single edge whose
 * text joins the original texts with newlines, in insertion order.
 * <p>
 * Note that a bare {@code rem:} self-loop is not an empty remark line but the
 * remark-<i>node</i> marker: it is node-only and absorbed into the node as a
 * node label before the merge sweep runs.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class RemarkMergeTest {
    /** Parallel remark self-loops merge into one edge, in insertion order. */
    @Test
    public void testLoopsMerge() {
        PlainGraph plain = new PlainGraph("loops", GraphRole.HOST);
        PlainNode n = plain.addNode();
        plain.addEdge(n, "rem:first", n);
        plain.addEdge(n, "rem:second", n);
        AspectGraph aspect = AspectGraph.newInstance(plain);
        assertFalse(aspect.hasErrors());
        List<? extends AspectEdge> remarks = remarkEdges(aspect);
        assertEquals(1, remarks.size());
        assertEquals("first\nsecond", remarks.get(0).label().getInnerText());
    }

    /** Parallel binary remark edges merge per (source,target) pair;
     * opposite directions stay separate. */
    @Test
    public void testBinaryEdgesMergePerNodePair() {
        PlainGraph plain = new PlainGraph("binary", GraphRole.HOST);
        PlainNode n1 = plain.addNode();
        PlainNode n2 = plain.addNode();
        plain.addEdge(n1, "rem:down1", n2);
        plain.addEdge(n1, "rem:down2", n2);
        plain.addEdge(n2, "rem:up", n1);
        AspectGraph aspect = AspectGraph.newInstance(plain);
        assertFalse(aspect.hasErrors());
        List<? extends AspectEdge> remarks = remarkEdges(aspect);
        assertEquals(2, remarks.size());
        for (AspectEdge remark : remarks) {
            String expected = remark.source().getNumber() == n1.getNumber()
                ? "down1\ndown2"
                : "up";
            assertEquals(expected, remark.label().getInnerText());
        }
    }

    /** The merged edge and multi-line remark texts (including duplicate
     * lines, i.e., multiplicity) survive a save/load round trip. */
    @Test
    public void testRoundTrip() {
        PlainGraph plain = new PlainGraph("roundtrip", GraphRole.HOST);
        PlainNode n = plain.addNode();
        plain.addEdge(n, "rem:first", n);
        plain.addEdge(n, "rem:second", n);
        PlainNode m = plain.addNode();
        plain.addEdge(m, "rem:line\nline", m);
        AspectGraph aspect = AspectGraph.newInstance(plain);
        AspectGraph reloaded = AspectGraph.newInstance(aspect.toPlainGraph());
        assertFalse(reloaded.hasErrors());
        assertEquals(aspect.edgeSet().size(), reloaded.edgeSet().size());
        List<? extends AspectEdge> remarks = remarkEdges(reloaded);
        assertEquals(2, remarks.size());
        for (AspectEdge remark : remarks) {
            String text = remark.label().getInnerText();
            assertTrue(text.equals("first\nsecond") || text.equals("line\nline"),
                       "unexpected remark text: " + text);
        }
    }

    /** A bare {@code rem:} self-loop is the remark-node marker: it becomes a
     * node label rather than an (empty) line of the merged remark edge. */
    @Test
    public void testBareRemarkLoopIsNodeMarker() {
        PlainGraph plain = new PlainGraph("marker", GraphRole.HOST);
        PlainNode n = plain.addNode();
        plain.addEdge(n, "rem:", n);
        plain.addEdge(n, "rem:text", n);
        AspectGraph aspect = AspectGraph.newInstance(plain);
        assertFalse(aspect.hasErrors());
        List<? extends AspectEdge> remarks = remarkEdges(aspect);
        assertEquals(1, remarks.size());
        assertEquals("text", remarks.get(0).label().getInnerText());
        assertTrue(aspect
            .nodeSet()
            .stream()
            .anyMatch(node -> node
                .getNodeLabels()
                .stream()
                .anyMatch(l -> l.has(AspectKind.REMARK))), "node should carry the rem: marker");
    }

    /** Regression on a real grammar: createEdge-0 in the creators grammar has
     * remark nodes n6 and n7, each with a bare {@code rem:} marker loop (which
     * is absorbed into the node) and a single-line remark text loop (which has
     * nothing to merge with), plus four binary remark edges. */
    @Test
    public void testFixtureGraph() throws Exception {
        GrammarModel grammar = SystemStore.newGrammar(new File("junit/rules/creators.gps"));
        AspectGraph graph = grammar.getHostModel(QualName.parse("createEdge-0")).getSource();
        List<? extends AspectEdge> loops = remarkEdges(graph)
            .stream()
            .filter(e -> e.source() == e.target())
            .toList();
        assertEquals(2, loops.size(), "one remark text loop each on n6 and n7");
        for (AspectEdge loop : loops) {
            String text = loop.label().getInnerText();
            assertFalse(text.contains("\n"), "expected single-line remark text: " + text);
            assertTrue(loop
                .source()
                .getNodeLabels()
                .stream()
                .anyMatch(l -> l.has(AspectKind.REMARK)), "loop node should be a remark node");
        }
        long remarkBinary = remarkEdges(graph)
            .stream()
            .filter(e -> e.source() != e.target())
            .count();
        assertEquals(4, remarkBinary);
        // round trip through the plain-graph (save) representation
        AspectGraph reloaded = AspectGraph.newInstance(graph.toPlainGraph());
        assertEquals(graph.edgeSet().size(), reloaded.edgeSet().size());
        assertEquals(2, remarkEdges(reloaded).stream().filter(e -> e.source() == e.target()).count());
    }

    /** Collects the remark edges of a given aspect graph. */
    private List<? extends AspectEdge> remarkEdges(AspectGraph graph) {
        return graph.edgeSet().stream().filter(e -> e.has(AspectKind.REMARK)).toList();
    }
}
