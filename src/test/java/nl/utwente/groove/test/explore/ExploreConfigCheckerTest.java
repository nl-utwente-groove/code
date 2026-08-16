/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.explore;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreConfigChecker;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.Groove;

/**
 * Tests that the grammar-dependent contents of an exploration configuration
 * are validated by {@link ExploreConfigChecker}, so that a broken condition
 * formula, an unknown rule name or an unknown edge label surfaces as an
 * error on the {@code explore} settings resource (via its schema) rather
 * than only failing an attempted exploration.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreConfigCheckerTest {
    /** Location of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** Configuration values whose content is broken for the ferryman grammar. */
    static private final String[] BROKEN = {
        // malformed condition formulas
        "goal=condition:",
        "goal=condition:load&&",
        "goal=condition:(load",
        // unknown or disabled rules
        "goal=condition:nonexistent",
        "goal=fires:nonexistent",
        "bound=upto:nonexistent",
        "bound=include:!nonexistent",
        // malformed or unknown edge bounds
        "bound=edges:nolabel>2",
        "bound=edges:load",};

    /** Configuration values that are valid for the ferryman grammar. */
    static private final String[] VALID = {
        "",
        "goal=condition:load",
        "goal=condition:!load",
        "goal=\"condition:load || eat\"",
        "goal=fires:load",
        "bound=upto:!load",
        "bound=include:load",};

    /** Tests that broken contents are reported by the property checker. */
    @Test
    public void testBrokenContent() throws Exception {
        GrammarModel grammar = Groove.loadGrammar(INPUT_DIR + "/ferryman");
        for (String value : BROKEN) {
            var errors = ExploreConfigChecker.check(grammar, ExploreConfig.parse(value));
            assertFalse(errors.isEmpty(), "Value '%s' should be reported as erroneous"
                .formatted(value));
        }
    }

    /** Tests that valid contents pass the property checker. */
    @Test
    public void testValidContent() throws Exception {
        GrammarModel grammar = Groove.loadGrammar(INPUT_DIR + "/ferryman");
        for (String value : VALID) {
            var errors = ExploreConfigChecker.check(grammar, ExploreConfig.parse(value));
            assertTrue(errors.isEmpty(), "Value '%s' should be accepted, but got %s"
                .formatted(value, errors));
        }
    }
}
