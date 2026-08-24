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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.Semantics;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Pins the semantics of the implicit creator NACs generated for the
 * {@code checkCreatorEdges} and {@code rhsIsNAC} grammar properties
 * (see gh #901): under non-injective matching the NAC tests for the absence
 * of <i>any</i> host copy of the created edge, whereas under injective
 * matching of a multigraph pattern it tests for the absence of a copy
 * <i>not already used by the match</i> (the NAC edge is bound in the same
 * search as the pattern edges, so the edge-injectivity constraint spans
 * both). This interpretation is uniform across the three semantics; the
 * only asymmetry is that edge-injectivity cannot activate on a simple
 * pattern, so in SPO-simple the NAC blocks whenever the copy exists —
 * which is the desired outcome there, since the creation would be a no-op.
 * <p>
 * Consequences pinned here: a pure creator with either guard implements
 * the "create only if absent" idiom in every semantics and under either
 * matching regime; a reader+creator rule with a guard never fires under
 * non-injective matching (the reader itself supplies the copy the NAC
 * finds), and under injective matching fires exactly if the host holds no
 * copy beyond those matched.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class CreatorNacTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR = "junit/rules/creatorNac";

    /** The guard property under test. */
    private enum Guard {
        /** No implicit NACs. */
        NONE,
        /** The {@code checkCreatorEdges} property. */
        CREATOR_EDGES,
        /** The {@code rhsIsNAC} property. */
        RHS_AS_NAC;
    }

    /** All three semantics. */
    private static final Semantics[] ALL_SEMANTICS = Semantics.values();
    /** The two multigraph semantics. */
    private static final Semantics[] MULTI_SEMANTICS
        = {Semantics.SPO_MULTI, Semantics.DPO};

    /** A reader+creator rule on a host with one copy: without a guard it
     * matches once in every configuration; with a guard it never fires
     * under non-injective matching, and under injective matching only in
     * the multigraph semantics, where the read copy does not count. */
    @Test
    public void testReaderCreatorOneCopy() throws Exception {
        for (Semantics semantics : ALL_SEMANTICS) {
            test(semantics, Guard.NONE, false, "readerCreator", "host1", 1);
            test(semantics, Guard.NONE, true, "readerCreator", "host1", 1);
            for (Guard guard : new Guard[] {Guard.CREATOR_EDGES, Guard.RHS_AS_NAC}) {
                test(semantics, guard, false, "readerCreator", "host1", 0);
                int injectiveCount = semantics.isMulti()
                    ? 1
                    : 0;
                test(semantics, guard, true, "readerCreator", "host1", injectiveCount);
            }
        }
    }

    /** A reader+creator rule on a host with two parallel copies (multigraph
     * semantics only): the guards block in every matching regime, since even
     * injective matching leaves an unused copy for the NAC to find. */
    @Test
    public void testReaderCreatorTwoCopies() throws Exception {
        for (Semantics semantics : MULTI_SEMANTICS) {
            test(semantics, Guard.NONE, false, "readerCreator", "host2", 2);
            test(semantics, Guard.NONE, true, "readerCreator", "host2", 2);
            for (Guard guard : new Guard[] {Guard.CREATOR_EDGES, Guard.RHS_AS_NAC}) {
                test(semantics, guard, false, "readerCreator", "host2", 0);
                test(semantics, guard, true, "readerCreator", "host2", 0);
            }
        }
    }

    /** A pure creator rule: with either guard, the "create only if absent"
     * idiom works uniformly — the rule fires on the copy-free host and is
     * blocked as soon as any copy exists, in every semantics and under
     * either matching regime. */
    @Test
    public void testFreshCreator() throws Exception {
        for (Semantics semantics : ALL_SEMANTICS) {
            for (Guard guard : Guard.values()) {
                for (boolean injective : new boolean[] {false, true}) {
                    test(semantics, guard, injective, "freshCreator", "host0", 1);
                    int oneCopyCount = guard == Guard.NONE
                        ? 1
                        : 0;
                    test(semantics, guard, injective, "freshCreator", "host1", oneCopyCount);
                }
            }
        }
        for (Semantics semantics : MULTI_SEMANTICS) {
            for (Guard guard : new Guard[] {Guard.CREATOR_EDGES, Guard.RHS_AS_NAC}) {
                test(semantics, guard, false, "freshCreator", "host2", 0);
                test(semantics, guard, true, "freshCreator", "host2", 0);
            }
        }
    }

    /**
     * Counts the matches of a named rule on a named host graph of the
     * fixture grammar, under given semantics, guard and injectivity
     * settings, and asserts the expected count.
     */
    private void test(Semantics semantics, Guard guard, boolean injective, String ruleName,
                      String hostName, int expectedCount) throws Exception {
        var grammarModel = Groove.loadGrammar(GRAMMAR);
        var properties = grammarModel.getProperties().clone();
        properties.setSemantics(semantics);
        properties.setInjective(injective);
        if (guard == Guard.CREATOR_EDGES) {
            properties.setCheckCreatorEdges(true);
        } else if (guard == Guard.RHS_AS_NAC) {
            properties.setRhsAsNac(true);
        }
        grammarModel.setProperties(properties);
        Rule rule = grammarModel.toGrammar().getRule(QualName.name(ruleName));
        assertNotNull(rule);
        var host = grammarModel.getHostModel(QualName.name(hostName)).toResource();
        int count = rule.getProver().getAllMatches(host).size();
        assertEquals(expectedCount, count,
                     String
                         .format("Rule %s on %s under %s, guard %s, %sinjective", ruleName,
                                 hostName, semantics, guard, injective
                                     ? ""
                                     : "non-"));
    }
}
