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
package nl.utwente.groove.test.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the role classification of a rule whose only effect is an
 * interactive ({@code ask:}) parameter. The value node of such a parameter
 * is created at application time, so the rule is a transformer, not a
 * condition; before the fix, {@code RuleModel.testAsProperty} overlooked
 * the parameter, classified the rule as a condition, and the compiled rule
 * then failed the property-like assertion in {@code Rule.getRole}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-10")
public class AskParameterRoleTest {
    /** The ask-only rule is a transformer and compiles into a usable grammar. */
    @Test
    public void testAskOnlyRuleIsTransformer() throws IOException, FormatException {
        GrammarModel grammarModel = Groove.loadGrammar("junit/rules/askOnly");
        RuleModel model = grammarModel.getRuleModel(QualName.parse("askInt"));
        assertNotNull(model);
        assertFalse("Unexpected errors: " + model.getErrors(), model.hasErrors());
        assertEquals(Role.TRANSFORMER, model.getRole());
        // compiling the grammar used to trip the assertion in Rule.getRole
        Grammar grammar = grammarModel.toGrammar();
        Rule rule = grammar.getRule(QualName.parse("askInt"));
        assertNotNull(rule);
        assertFalse(rule.isProperty());
        assertTrue(rule.isModifying());
    }
}
