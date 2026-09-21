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
package nl.utwente.groove.test.rule;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.match.Proof;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Checks that a {@link RuleApplication} is not retained by its {@link Rule} once
 * the application itself is unreachable (gh #919): computing the application's
 * lazily created values reads grammar-lifetime factories of the rule, which must
 * not keep the application alive.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class RuleApplicationRetentionTest {
    /** Applies a rule, drops the application, and checks that it is collected. */
    @Test
    public void testApplicationNotRetainedByRule() throws Exception {
        GrammarModel model = Groove.loadGrammar(RuleApplicationTest.INPUT_DIR + "/creators");
        model.setLocalActiveNames(ResourceKind.HOST, QualName.name("createNode-0"));
        Rule rule = model.toGrammar().getRule(QualName.name("createNode"));
        assertNotNull(rule);
        HostGraph start = model.getHostModel(QualName.name("createNode-0")).toResource();
        Proof proof = rule.getProver().getAllMatches(start).iterator().next();
        RuleEvent event = RuleEvent.createEvent(proof, null);
        WeakReference<@Nullable RuleApplication> ref = new WeakReference<>(apply(event, start));
        for (int i = 0; i < 10 && ref.get() != null; i++) {
            System.gc();
            Thread.sleep(10);
        }
        assertNull(ref.get(), "rule application retained after becoming unreachable");
        // the grammar and rule must stay alive during the check, otherwise it is vacuous
        Reference.reachabilityFence(rule);
        Reference.reachabilityFence(model);
    }

    /**
     * Applies an event to a source graph, computing the match, target and morphism
     * (the comatch needs the added-node array that only the LTS path records),
     * and returns the application. Kept out of the test method so that no local
     * variable of the test frame keeps the application reachable.
     */
    private static @Nullable RuleApplication apply(RuleEvent event, HostGraph source) {
        RuleApplication result = new RuleApplication(event, source);
        result.getMatch();
        result.getTarget();
        result.getMorphism();
        return result;
    }
}
