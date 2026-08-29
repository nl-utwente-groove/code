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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.Semantics;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests the compile-time warning for always-violated implicit creator NACs
 * (gh #904): a rule whose created edge has a content-equal LHS twin, combined
 * with the {@code checkCreatorEdges} or {@code rhsIsNAC} property, can never
 * be applied unless matching is both injective and on multigraphs. The rule
 * is not ill-formed, so it must still compile, but it gets a
 * {@link Severity#WARNING} diagnostic. The run-time match counts of the same
 * fixture are pinned in {@link CreatorNacTest}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-08")
@NonNullByDefault
public class CreatorNacWarningTest {
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

    /** The reader+creator rule warns exactly when matching is not both
     * injective and on multigraphs. */
    @Test
    public void testReaderCreatorWarns() throws Exception {
        for (Semantics semantics : Semantics.values()) {
            for (Guard guard : new Guard[] {Guard.CREATOR_EDGES, Guard.RHS_AS_NAC}) {
                for (boolean injective : new boolean[] {false, true}) {
                    boolean expectWarning = !(semantics.isMulti() && injective);
                    test(semantics, guard, injective, "readerCreator", expectWarning);
                }
            }
        }
    }

    /** Without a guard property there is no implicit NAC, hence no warning. */
    @Test
    public void testNoGuardNoWarning() throws Exception {
        for (Semantics semantics : Semantics.values()) {
            for (boolean injective : new boolean[] {false, true}) {
                test(semantics, Guard.NONE, injective, "readerCreator", false);
            }
        }
    }

    /** A pure creator rule has no LHS twin, hence no warning: the implicit
     * NAC implements the intended "create only if absent" idiom. */
    @Test
    public void testFreshCreatorClean() throws Exception {
        for (Semantics semantics : Semantics.values()) {
            for (Guard guard : Guard.values()) {
                for (boolean injective : new boolean[] {false, true}) {
                    test(semantics, guard, injective, "freshCreator", false);
                }
            }
        }
    }

    /** The warning also surfaces at grammar level, wrapped and non-blocking. */
    @Test
    public void testGrammarLevelWarning() throws Exception {
        var grammarModel = Groove.loadGrammar(GRAMMAR);
        var properties = grammarModel.getProperties().clone();
        properties.setSemantics(Semantics.SPO_SIMPLE);
        properties.setCheckCreatorEdges(true);
        grammarModel.setProperties(properties);
        // the grammar builds despite the warning
        assertNotNull(grammarModel.toGrammar().getRule(QualName.name("readerCreator")));
        var errors = grammarModel.getErrors();
        assertFalse(errors.hasErrors());
        assertTrue(errors
            .stream()
            .anyMatch(e -> e.getSeverity() == Severity.WARNING
                && e.toString().startsWith("Warning in rule 'readerCreator'")));
    }

    /**
     * Compiles a named rule of the fixture grammar under given semantics,
     * guard and injectivity settings, and asserts the presence or absence
     * of the creator-NAC warning. In all cases the rule itself must compile.
     */
    private void test(Semantics semantics, Guard guard, boolean injective, String ruleName,
                      boolean expectWarning) throws Exception {
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
        var ruleModel = grammarModel.getRuleModel(QualName.name(ruleName));
        assertNotNull(ruleModel);
        // the rule compiles regardless of the warning
        assertNotNull(ruleModel.toResource());
        var warnings = ruleModel.getErrors().filter(Severity.WARNING);
        String context = String
            .format("Rule %s under %s, guard %s, %sinjective", ruleName, semantics, guard,
                    injective
                        ? ""
                        : "non-");
        assertEquals(expectWarning
            ? 1
            : 0, warnings.get().size(), context);
        assertFalse(ruleModel.hasErrors(), context);
    }
}
