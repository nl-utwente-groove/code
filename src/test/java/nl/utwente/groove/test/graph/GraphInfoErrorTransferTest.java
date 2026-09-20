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
package nl.utwente.groove.test.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests that {@link GraphInfo#transferErrors} carries over non-blocking
 * diagnostics as well as blocking errors. Since gh #885 a graph's
 * {@code hasErrors()} only reports blocking errors, so gating the transfer
 * on it silently dropped a warnings-only set.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class GraphInfoErrorTransferTest {
    /** A warnings-only set survives a direct transfer. */
    @Test
    public void testTransferKeepsWarnings() {
        var source = new PlainGraph("source", GraphRole.HOST);
        source.getErrors().add(new FormatError(Severity.WARNING, "some warning"));
        var target = new PlainGraph("target", GraphRole.HOST);
        GraphInfo.transferErrors(source, target, null);
        var errors = target.getErrors();
        assertEquals(1, errors.get().size());
        assertEquals(Severity.WARNING, errors.getSeverity());
        assertFalse(errors.hasErrors());
    }

    /** A warning on a plain graph reaches the aspect graph built from it,
     * with its context mapped to the corresponding aspect node. */
    @Test
    public void testNewInstanceKeepsWarnings() {
        var plain = new PlainGraph("plain", GraphRole.HOST);
        var node = plain.addNode();
        plain.getErrors().add(new FormatError(Severity.WARNING, "warning on %s", node));
        var aspect = AspectGraph.newInstance(plain);
        var errors = aspect.getErrors();
        assertEquals(1, errors.get().size());
        assertFalse(errors.hasErrors());
        var warning = errors.get().iterator().next();
        assertEquals(Severity.WARNING, warning.getSeverity());
        var context = warning.getContext(AspectNode.class);
        assertEquals(1, context.size());
        assertEquals(node.getNumber(), context.get(0).getNumber());
    }
}
