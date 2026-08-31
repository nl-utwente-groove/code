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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.type.TypeFactory;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.iso.Bisimulator;
import nl.utwente.groove.graph.iso.CertificateStrategy;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.graph.iso.PartitionRefiner;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.HashBag;

/**
 * Cross-checks the two certificate strategies, {@link PartitionRefiner}
 * (the production strategy) and {@link Bisimulator} (the independent oracle
 * behind {@code IsoChecker.ISO_ASSERT}), against the isomorphism checker's
 * verdicts: isomorphic graphs must receive equal graph certificates and equal
 * multisets of node and edge certificates under either strategy. This keeps
 * the otherwise dormant oracle from rotting; it would in particular have
 * caught the inverted label comparison that lurked in
 * {@code Bisimulator.MyEdge1Cert.equals} until 2026-08.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class CertificateStrategyTest {
    /** Location of the isomorphic graph fixtures. */
    static private final String INPUT_DIR = "junit/graphs/iso";
    static private final IsoChecker checker = IsoChecker.getInstance(true);

    /** The isomorphic bridges fixture pair (differently laid-out plain graphs)
     * must be certificate-equivalent under both strategies. */
    @Test
    public void testBridgesFixture() throws IOException {
        Graph one = Groove.loadGraph(new File(INPUT_DIR, "bridges-1.gst"));
        Graph two = Groove.loadGraph(new File(INPUT_DIR, "bridges-2.gst"));
        assertEquivalent(one, two);
    }

    /** The same structure built twice from one factory carries differently
     * numbered nodes; certificates must not depend on the numbering. */
    @Test
    public void testNodeRenumbering() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), true);
        DefaultHostGraph one = new DefaultHostGraph("one", factory);
        HostNode n0 = one.addNode();
        HostNode n1 = one.addNode();
        HostNode n2 = one.addNode();
        one.addEdge(n0, "a", n1);
        one.addEdge(n1, "b", n2);
        one.addEdge(n2, "c", n0);
        one.addEdge(n0, "d", n0);
        // the same structure, built in reverse creation order
        DefaultHostGraph two = new DefaultHostGraph("two", factory);
        HostNode m2 = two.addNode();
        HostNode m1 = two.addNode();
        HostNode m0 = two.addNode();
        two.addEdge(m2, "c", m0);
        two.addEdge(m1, "b", m2);
        two.addEdge(m0, "d", m0);
        two.addEdge(m0, "a", m1);
        assertEquivalent(one, two);
    }

    /** Loop certificates must compare their labels: equal labels give
     * equivalent graphs, distinct labels distinct certificates. Regression
     * test for the inverted label comparison in the {@link Bisimulator}
     * unary-edge certificate. */
    @Test
    public void testLoopLabels() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), true);
        assertEquivalent(createLoopGraph(factory, "a"), createLoopGraph(factory, "a"));
        assertDistinct(createLoopGraph(factory, "a"), createLoopGraph(factory, "b"));
    }

    /** Bundles of parallel edges share one certificate carrying their
     * multiplicity (gh #906); the multiplicity must enter both the unary and
     * the binary certificate comparison. */
    @Test
    public void testParallelBundles() {
        HostFactory factory = HostFactory.newInstance(TypeFactory.newInstance(), false);
        assertEquivalent(createParallelGraph(factory, 3, 2), createParallelGraph(factory, 3, 2));
        // differing loop multiplicity (unary certificates)
        assertDistinct(createParallelGraph(factory, 3, 2), createParallelGraph(factory, 2, 2));
        // differing binary-edge multiplicity
        assertDistinct(createParallelGraph(factory, 3, 2), createParallelGraph(factory, 3, 3));
    }

    /** Creates a single-node graph with one labelled loop. */
    private DefaultHostGraph createLoopGraph(HostFactory factory, String label) {
        DefaultHostGraph result = new DefaultHostGraph("loop-" + label, factory);
        HostNode node = result.addNode();
        result.addEdge(node, label, node);
        return result;
    }

    /** Creates a two-node multigraph with a bundle of parallel a-loops and a
     * bundle of parallel b-edges, of given multiplicities. */
    private DefaultHostGraph createParallelGraph(HostFactory factory, int loops, int edges) {
        DefaultHostGraph result = new DefaultHostGraph("parallel", factory);
        HostNode source = result.addNode();
        HostNode target = result.addNode();
        for (int i = 0; i < loops; i++) {
            result.addEdge(source, "a", source);
        }
        for (int i = 0; i < edges; i++) {
            result.addEdge(source, "b", target);
        }
        return result;
    }

    /** Asserts that two graphs are isomorphic, and that both certificate
     * strategies agree: equal graph certificates and equal certificate
     * multisets. */
    private void assertEquivalent(Graph dom, Graph cod) {
        assertTrue(checker.areIsomorphic(dom, cod));
        assertCertsEqual(new PartitionRefiner(dom, true), new PartitionRefiner(cod, true));
        assertCertsEqual(new Bisimulator(dom), new Bisimulator(cod));
    }

    /** Asserts that two graphs are not isomorphic, and that both certificate
     * strategies discriminate them. The strategies' contract only requires
     * equal certificates for isomorphic graphs, but for the hand-built
     * fixtures of this test the differences feed directly into the
     * (deterministic) certificate values, so inequality is stable. */
    private void assertDistinct(Graph dom, Graph cod) {
        assertFalse(checker.areIsomorphic(dom, cod));
        assertNotEquals(new PartitionRefiner(dom, true).getGraphCertificate(),
                        new PartitionRefiner(cod, true).getGraphCertificate());
        assertNotEquals(new Bisimulator(dom).getGraphCertificate(),
                        new Bisimulator(cod).getGraphCertificate());
    }

    /** Asserts equal graph certificates and equal node and edge certificate
     * multisets, in the manner of {@code IsoChecker.checkBisimulator}. */
    private void assertCertsEqual(CertificateStrategy dom, CertificateStrategy cod) {
        assertEquals(dom.getGraphCertificate(), cod.getGraphCertificate());
        assertEquals(new HashBag<>(Arrays.asList(dom.getNodeCertificates())),
                     new HashBag<>(Arrays.asList(cod.getNodeCertificates())));
        assertEquals(new HashBag<>(Arrays.asList(dom.getEdgeCertificates())),
                     new HashBag<>(Arrays.asList(cod.getEdgeCertificates())));
    }
}
