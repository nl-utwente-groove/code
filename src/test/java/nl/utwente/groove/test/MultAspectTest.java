// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.aspect.GraphConverter.HostToAspectMap;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the static checks on the parallel-edge multiplicity aspect
 * ({@code mult=k:}): it is only allowed in host graphs (its use in rules is
 * deferred until a real use case comes by), the value must be a positive
 * constant, the edge must be binary, and any use requires the parallelEdges
 * grammar property to be SPO or DPO. Also tests that a host graph edge with
 * a multiplicity compiles into that many parallel host edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MultAspectTest {
    /** Returns a named test grammar. */
    private GrammarModel getGrammar(String grammarName) {
        try {
            return Groove.loadGrammar("junit/rules/" + grammarName);
        } catch (IOException exc) {
            Assert.fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    /** Returns the model of a named host graph in a named test grammar. */
    private ResourceModel<?> getHostModel(String grammarName, String hostName) {
        return getGrammar(grammarName).getResource(ResourceKind.HOST, QualName.parse(hostName));
    }

    /** Asserts that a resource model has an error containing a given text. */
    private void assertError(ResourceModel<?> model, String text) {
        assertTrue(model.hasErrors());
        assertTrue("Unexpected errors: " + model.getErrors(),
                   model.getErrors().toString().contains(text));
    }

    /** A multiplicity is not allowed on rule edges: its use in rules is
     * deferred until a real use case comes by. */
    @Test
    public void testRuleMult() {
        assertError(getGrammar("multErrors").getRuleModel(QualName.parse("ruleMult")),
                    "not allowed");
    }

    /** A multiplicity must be a single number, not a range. */
    @Test
    public void testHostRange() {
        assertError(getHostModel("multErrors", "hostRange"), "single");
    }

    /** A multiplicity must be positive. */
    @Test
    public void testHostZero() {
        assertError(getHostModel("multErrors", "hostZero"), "Multiplicity 0");
    }

    /** A multiplicity is not allowed on node type edges: a node's typing is
     * not a host graph edge. */
    @Test
    public void testHostType() {
        assertError(getHostModel("multErrors", "hostType"), "node type");
    }

    /** A flag with multiplicity 2 compiles into 2 parallel flag edges. */
    @Test
    public void testHostFlagMult() throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse("hostFlag"));
        assertFalse("Unexpected errors: " + hostModel.getErrors(), hostModel.hasErrors());
        assertEquals(2, hostModel.toResource().edgeSet().size());
    }

    /** A field initialiser with multiplicity 2 compiles into 2 parallel
     * value edges (to the same value node). */
    @Test
    public void testHostLetMult() throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse("hostLet"));
        assertFalse("Unexpected errors: " + hostModel.getErrors(), hostModel.hasErrors());
        assertEquals(2, hostModel.toResource().edgeSet().size());
    }

    /** A host graph multiplicity above 1 requires the parallelEdges property. */
    @Test
    public void testNoParallelHost() {
        assertError(getHostModel("multNoParallel", "hostMult"), "parallelEdges grammar property");
    }

    /** The missing-parallelEdges error is attributed to an edge of the host
     * model's source graph, also when the host graph is normalised before
     * compilation (here because of a {@code let} edge) so that the error is
     * raised on an edge of the normalised graph. */
    @Test
    @AIGenerated("Claude Fable 5, 2026-08")
    public void testNoParallelHostErrorContext() {
        for (String hostName : new String[] {"hostMult", "hostLet"}) {
            var hostModel = getGrammar("multNoParallel").getHostModel(QualName.parse(hostName));
            assertError(hostModel, "parallelEdges grammar property");
            AspectGraph source = hostModel.getSource();
            for (FormatError error : hostModel.getErrors()) {
                assertTrue("Error not attributed to the source graph of " + hostName + ": " + error,
                           error
                               .getElements()
                               .stream()
                               .anyMatch(e -> e instanceof AspectEdge edge
                                   && source.containsEdge(edge)));
            }
        }
    }

    /** A host graph edge with multiplicity 2 compiles into 2 parallel edges. */
    @Test
    public void testHostMult() throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse("hostMult"));
        assertFalse("Unexpected errors: " + hostModel.getErrors(), hostModel.hasErrors());
        assertEquals(2, hostModel.toResource().edgeSet().size());
    }

    /** Converting a multigraph host graph back to an aspect graph aggregates
     * parallel copies into a single mult= edge (so a saved graph reloads to
     * the same multigraph), with every copy mapping to the aggregated image
     * (so element-keyed GUI state, such as match highlighting, reaches every
     * copy). */
    @Test
    public void testAspectAggregation() throws FormatException {
        testAggregation("hostMult");
    }

    /** Parallel flags aggregate like parallel binary edges. */
    @Test
    public void testFlagAggregation() throws FormatException {
        testAggregation("hostFlag");
    }

    /** Parallel value edges aggregate like parallel binary edges. */
    @Test
    public void testLetAggregation() throws FormatException {
        testAggregation("hostLet");
    }

    /** Converts the (2-copy multigraph) host graph of a named multErrors
     * host model back to an aspect graph, and asserts that the copies are
     * aggregated into a single mult=2 aspect edge to which both copies map.
     */
    private void testAggregation(String hostName) throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse(hostName));
        HostGraph host = hostModel.toResource();
        assertEquals(2, host.edgeSet().size());
        HostToAspectMap map = GraphConverter.toAspectMap(host);
        AspectGraph aspect = map.getAspectGraph();
        assertTrue(aspect.isSimple());
        assertEquals(1, aspect.edgeSet().size());
        AspectEdge image = aspect.edgeSet().iterator().next();
        var mult = image.getMult();
        assertNotNull(mult);
        assertEquals(2, mult.lower());
        for (HostEdge edge : host.edgeSet()) {
            assertEquals(image, map.getEdge(edge));
        }
    }
}
