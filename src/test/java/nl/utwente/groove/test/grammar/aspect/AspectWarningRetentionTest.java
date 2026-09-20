/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.grammar.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests that fixing an {@link AspectNode} or {@link AspectEdge} retains a
 * warnings-only error set, and that the graph aggregates it. Since gh #885
 * an element's {@code hasErrors()} only reports blocking errors, so the
 * fixing code that gated on it replaced such a set by the empty set.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class AspectWarningRetentionTest {
    /** Warnings on a node and an edge survive fixing and reach the graph. */
    @Test
    public void testFixKeepsWarnings() {
        var graph = new AspectGraph("g", GraphRole.HOST, true);
        var node = graph.addNode();
        var edge = graph.addEdge(node, "a", node);
        node.addError(new FormatError(Severity.WARNING, "node warning"));
        edge.addError(new FormatError(Severity.WARNING, "edge warning"));
        graph.setFixed();
        assertFalse(node.hasErrors());
        assertEquals(1, node.getErrors().get().size());
        assertEquals(Severity.WARNING, node.getErrors().getSeverity());
        assertFalse(edge.hasErrors());
        assertEquals(1, edge.getErrors().get().size());
        assertEquals(Severity.WARNING, edge.getErrors().getSeverity());
        assertFalse(graph.hasErrors());
        assertEquals(2, graph.getErrors().get().size());
    }
}
