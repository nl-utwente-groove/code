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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.store.SystemStore;

/**
 * Tests the invariant that a fixed aspect graph has at most one remark edge
 * per node pair, with the merged text preserving line order and multiplicity.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RemarkMergeTest {
    @Test
    public void testMerge() throws Exception {
        GrammarModel grammar = SystemStore.newGrammar(new File("junit/rules/creators.gps"));
        AspectGraph graph = grammar.getHostModel(QualName.parse("createEdge-0")).getSource();
        // the file contains two remark self-edges on n6 ("rem:" and "rem:Edges will ...")
        // and two on n7; after the merge sweep each node pair has at most one
        List<? extends AspectEdge> remarkLoops = graph
            .edgeSet()
            .stream()
            .filter(e -> e.has(AspectKind.REMARK))
            .filter(e -> e.source() == e.target())
            .toList();
        assertEquals(2, remarkLoops.size(), "one merged remark loop each on n6 and n7");
        for (AspectEdge loop : remarkLoops) {
            String text = loop.label().getInnerText();
            assertTrue(text.contains("\n"), "merged remark text should be multi-line: " + text);
            assertTrue(text.startsWith("\n"), "empty first remark line should be preserved: " + text);
        }
        // the three binary remark edges from n6 and one from n7 stay separate
        long remarkBinary = graph
            .edgeSet()
            .stream()
            .filter(e -> e.has(AspectKind.REMARK))
            .filter(e -> e.source() != e.target())
            .count();
        assertEquals(4, remarkBinary);
        // round trip through the plain-graph (save) representation
        AspectGraph reloaded = AspectGraph.newInstance(graph.toPlainGraph());
        assertEquals(graph.edgeSet().size(), reloaded.edgeSet().size());
        long reloadedRemarkLoops = reloaded
            .edgeSet()
            .stream()
            .filter(e -> e.has(AspectKind.REMARK))
            .filter(e -> e.source() == e.target())
            .count();
        assertEquals(2, reloadedRemarkLoops);
    }
}
