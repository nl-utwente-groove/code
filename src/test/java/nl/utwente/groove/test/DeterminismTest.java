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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests that successive explorations of the same rule system, within one JVM,
 * enumerate exactly the same states and transitions in the same order.
 * This guards against run-to-run nondeterminism creeping in through
 * identity-based hash codes in iterated hash collections, through
 * unordered collections on the exploration path, or through
 * garbage-collection timing: when a state cache is collected, the state graph
 * is reconstructed along a different basis chain, so the iteration order of
 * its (insertion-ordered) element sets may change, and enumeration order on
 * the exploration path must not depend on it.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DeterminismTest {
    /** Restores the master-seed state that the tests in this class modify. */
    @ClassRule
    public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();

    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests determinism of the default (plan-based) exploration. */
    @Test
    public void testPlanEngineDeterminism() {
        test("ferryman", "");
        test("loose-nodes", "");
    }

    /**
     * Tests determinism of the non-default engine configurations under the
     * same harness: the frontier orders, a restrictive beam (whose drop
     * decisions must also be reproducible), and unstored exploration. The
     * unstored configurations must terminate by grammar shape or depth
     * bound: without storing there is no state collapse, so an unbounded
     * unstored exploration of a cyclic state space diverges.
     */
    @Test
    @AIGenerated("Claude Fable 5, 2026-08")
    public void testEngineConfigDeterminism() {
        test("ferryman", "next=newest");
        test("ferryman", "next=random");
        test("ferryman", "frontier=beam:2");
        test("ferryman", "next=random frontier=beam:2");
        // counting's rules create nodes, whose anchor hashes are not
        // replay-stable (see the hashesReplayStable parameter of test)
        test("counting", "persistence=none", false);
        test("ferryman", "persistence=none cost=uniform bound=cost:4");
    }

    /** Same as {@link #test(String, String, boolean)}, with replay-stable hashes. */
    private void test(String grammarName, String config) {
        test(grammarName, config, true);
    }

    /**
     * Explores a named grammar repeatedly with a given exploration
     * configuration — perturbing the identity hash code sequence in between,
     * and simulating a garbage-collection sweep of the state caches at
     * various points during the later explorations — and asserts that all
     * explorations enumerate identical states and transitions in identical
     * order.
     * @param hashesReplayStable if {@code true}, the signature includes the
     * transition hash codes, which cover the (content-based) event and
     * state-number hashes. This does not hold for grammars whose rule
     * applications create nodes: fresh node numbers are drawn from the host
     * factory on the grammar's start graph, which is shared across GTSs, so
     * a re-exploration numbers its created nodes differently and the anchor
     * hashes of later events shift with them. That leaves the exploration
     * behaviour unaffected (the event-stream part of the signature must
     * still be identical), but such grammars must skip the hash check.
     */
    private void test(String grammarName, String config, boolean hashesReplayStable) {
        try {
            GrammarModel grammarModel = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            ExploreType exploreType = ExploreTypeConverter.toExploreType(ExploreConfig.parse(config));
            String first = explore(grammarModel, exploreType, NO_COLLAPSE, hashesReplayStable);
            int closures = this.closureCount;
            perturbIdentityHashes();
            String second = explore(grammarModel, exploreType, NO_COLLAPSE, hashesReplayStable);
            assertEquals(String
                .format("Non-deterministic '%s' exploration of grammar %s", config, grammarName),
                         first, second);
            for (int collapseAt : new int[] {COLLAPSE_ALWAYS, 1, closures / 4, closures / 2,
                3 * closures / 4}) {
                if (collapseAt == 0) {
                    continue;
                }
                perturbIdentityHashes();
                String third = explore(grammarModel, exploreType, collapseAt, hashesReplayStable);
                assertEquals(String
                    .format("Non-deterministic '%s' exploration of grammar %s"
                        + " under cache collapse at %s", config, grammarName,
                            collapseAt == COLLAPSE_ALWAYS
                                ? "every closure"
                                : "closure " + collapseAt),
                             first, third);
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /** Value for the collapse parameter of {@link #explore} that disables cache collapse. */
    private static final int NO_COLLAPSE = -1;
    /** Value for the collapse parameter of {@link #explore} that collapses at every closure. */
    private static final int COLLAPSE_ALWAYS = -2;

    /**
     * Explores a grammar and returns the exploration signature: the event
     * stream of the run followed by an enumeration of the resulting GTS.
     * @param collapseAt if {@code collapseAt > 0}, then at the moment the
     * so-manieth state closure happens, the caches of all closed states are
     * cleared at once; if {@link #COLLAPSE_ALWAYS}, this happens at every
     * closure. This simulates a garbage collection sweep clearing the (softly
     * referenced) caches under memory pressure, which forces graphs and
     * transition data to be reconstructed on next use, along basis chains
     * that differ from the original construction — the enumeration must be
     * insensitive to such reconstructions, wherever they occur.
     */
    private String explore(GrammarModel grammarModel, ExploreType exploreType, int collapseAt,
                           boolean withHashes) throws FormatException {
        // reset the master seed so that every exploration draws identical
        // random streams; the randomised configurations are then expected to
        // enumerate identically across runs (the plan-based default draws no
        // randomness and is unaffected)
        Randomness.setMasterSeed(42);
        GTS gts = new GTS(grammarModel.toGrammar());
        this.closureCount = 0;
        StringBuilder result = new StringBuilder();
        // the signature records the exploration event stream as it occurs,
        // followed by an enumeration of the final GTS. The event stream also
        // covers unstored explorations (persistence=none), whose states and
        // transitions are announced but not entered into the state set, so
        // that their final enumeration is nearly empty
        gts.addLTSListener(new GTSListener() {
            @Override
            public void addUpdate(GTS observed, GraphState state) {
                result.append("add ").append(state).append('\n');
            }

            @Override
            public void addUpdate(GTS observed, GraphTransition transition) {
                result
                    .append(transition.source())
                    .append("--")
                    .append(transition.label())
                    .append("->")
                    .append(transition.target())
                    .append('\n');
            }

            @Override
            public void statusUpdate(GTS observed, GraphState state, int change) {
                if (Status.Flag.CLOSED.test(change) && state.isClosed()) {
                    result.append("close ").append(state).append('\n');
                    DeterminismTest.this.closureCount++;
                    if (collapseAt == COLLAPSE_ALWAYS
                        || DeterminismTest.this.closureCount == collapseAt) {
                        for (GraphState s : observed.nodeSet().toArray(new GraphState[0])) {
                            if (s.isClosed() && s instanceof AbstractGraphState closed) {
                                closed.clearCache();
                            }
                        }
                    }
                }
            }
        });
        exploreType.newExploration(gts, null).play();
        result.append("-- final GTS --\n");
        gts.nodeSet().forEach(n -> result.append(n).append('\n'));
        gts.edgeSet().forEach(e -> {
            result
                .append(e.source())
                .append("--")
                .append(e.label())
                .append("->")
                .append(e.target());
            if (withHashes) {
                // the transition hash covers the (content-based) event and
                // state-number hashes, which must also be reproducible
                result.append(" #").append(e.hashCode());
            }
            result.append('\n');
        });
        return result.toString();
    }

    /** Number of state closures observed during the last call to {@link #explore}. */
    private int closureCount;

    /**
     * Advances the JVM's identity hash code sequence, so that objects created
     * by a subsequent exploration receive different identity hashes than in a
     * previous one. This is what exposes iteration over identity-hash-keyed
     * collections. The allocations also perturb garbage-collection timing,
     * which exposes order dependence on cache-driven state graph
     * reconstruction.
     */
    private void perturbIdentityHashes() {
        int sink = 0;
        for (int i = 0; i < 100_000; i++) {
            sink += System.identityHashCode(new Object());
        }
        // use the sink so the loop cannot be optimised away
        if (sink == Integer.MIN_VALUE) {
            fail("unreachable");
        }
    }
}
