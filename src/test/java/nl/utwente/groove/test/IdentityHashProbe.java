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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Cross-JVM determinism probe (gh #894). Advances the JVM's identity hash
 * code sequence by a given number of draws, then loads a grammar and prints a
 * signature to standard output: the raw (unsorted) anchor of every rule,
 * followed by the exploration event stream and an enumeration of the final
 * GTS including transition hash codes. Running this main in separate JVMs
 * with different draw counts and comparing the outputs exposes identity hash
 * codes that leak into anchor order or transition order — including
 * {@link Class} and {@link Enum} identity hashes, which are constant within
 * one JVM and therefore invisible to {@link DeterminismTest}.
 * <p>
 * Usage: {@code IdentityHashProbe [draws [grammar [startGraph]]]}, with
 * defaults {@code 0}, {@code junit/samples/leader-election} and
 * {@code start-2}; the grammar path is relative to the working directory.
 * {@link CrossJvmDeterminismTest} automates the comparison.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class IdentityHashProbe {
    /** Runs the probe and prints the signature to standard output. */
    public static void main(String[] args) throws IOException, FormatException {
        int draws = args.length > 0
            ? Integer.parseInt(args[0])
            : 0;
        String grammarName = args.length > 1
            ? args[1]
            : "junit/samples/leader-election";
        String startGraph = args.length > 2
            ? args[2]
            : "start-2";
        System.out.print(run(draws, grammarName, startGraph));
    }

    /**
     * Computes the probe signature of a given grammar, after advancing the
     * identity hash sequence by the given number of draws.
     */
    static String run(int draws, String grammarName,
                      String startGraph) throws IOException, FormatException {
        advanceIdentityHashes(draws);
        // fix the master seed so that any randomness drawn on the exploration
        // path is reproducible (the plan-based default draws none)
        Randomness.setMasterSeed(42);
        GrammarModel grammarModel = Groove.loadGrammar(grammarName);
        grammarModel.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraph));
        Grammar grammar = grammarModel.toGrammar();
        StringBuilder result = new StringBuilder();
        // enumerate the rules in name order, but dump each anchor in its raw
        // internal order - that order, not the anchor content, is the
        // hash-sensitive signal (cf. the canonicalisation in RuleCompilationTest)
        List<Rule> rules = new ArrayList<>(grammar.getAllRules());
        rules.sort(Comparator.comparing(r -> r.getQualName().toString()));
        for (Rule rule : rules) {
            result.append("anchor ").append(rule.getQualName()).append(": ")
                .append(rule.getAnchor()).append('\n');
        }
        // record the exploration event stream as it occurs, followed by an
        // enumeration of the final GTS (mirroring DeterminismTest.explore)
        GTS gts = new GTS(grammar);
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
                }
            }
        });
        ExploreType exploreType = ExploreTypeConverter.toExploreType(ExploreConfig.parse(""));
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
            // the transition hash covers the (content-based) event and
            // state-number hashes, which must also be reproducible
            result.append(" #").append(e.hashCode());
            result.append('\n');
        });
        return result.toString();
    }

    /**
     * Advances the JVM's identity hash code sequence by a given number of
     * draws, so that all objects subsequently created receive different
     * identity hashes than they would in a JVM without the draws. Identity
     * hashes are computed lazily, so the throwaway objects must actually be
     * hashed for the sequence to advance.
     */
    private static void advanceIdentityHashes(int draws) {
        int sink = 0;
        for (int i = 0; i < draws; i++) {
            sink += System.identityHashCode(new Object());
        }
        // use the sink so the loop cannot be optimised away
        if (sink == Integer.MIN_VALUE) {
            throw new IllegalStateException("unreachable");
        }
    }
}
