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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.Test;

import nl.utwente.groove.explore.config.parse.RuleFormulaParser;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the rule formula parser, in particular the conventional operator
 * precedence that distinguishes it from the legacy {@code EncodedRuleFormula}
 * (which parsed all operators at a single right-associative level).
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleFormulaParserTest {
    /** Constant predicate, standing in for rule applicability. */
    private static class Constant extends Predicate<GraphState> {
        Constant(boolean value) {
            super(true);
            this.value = value;
        }

        private final boolean value;

        @Override
        public boolean test(GraphState state) {
            return this.value;
        }
    }

    /** Parses a formula over constant-valued atoms and evaluates it. */
    private boolean eval(String formula, Map<String,Boolean> atoms) throws FormatException {
        Predicate<GraphState> predicate = RuleFormulaParser
            .parse(name -> new Constant(atoms.get(name.toString())), formula);
        return predicate.test(null);
    }

    /** Tests the basic operators and parenthesised grouping. */
    @Test
    public void testBasicOperators() throws FormatException {
        var atoms = Map.of("a", true, "b", false);
        assertEquals(true, eval("a", atoms));
        assertEquals(false, eval("!a", atoms));
        assertEquals(false, eval("a && b", atoms));
        assertEquals(true, eval("a || b", atoms));
        assertEquals(false, eval("a -> b", atoms));
        assertEquals(true, eval("b -> a", atoms));
        assertEquals(false, eval("(a || b) && b", atoms));
        // the single-character legacy forms remain accepted
        assertEquals(false, eval("a & b", atoms));
        assertEquals(true, eval("a | b", atoms));
    }

    /**
     * Tests that {@code ->} can be written without surrounding spaces, while
     * rule names keep their internal hyphens.
     */
    @Test
    public void testSpacelessImplication() throws FormatException {
        var atoms = Map.of("a", true, "b", false, "a-b", false);
        assertEquals(false, eval("a->b", atoms));
        assertEquals(true, eval("b->a", atoms));
        // a hyphen not followed by '>' belongs to the rule name
        assertEquals(false, eval("a-b", atoms));
        assertEquals(true, eval("a-b->b", atoms));
    }

    /**
     * Tests the conventional precedence: {@code !} binds strongest, then
     * {@code &&}, then {@code ||}, then {@code ->}. The chosen valuations
     * distinguish this from the legacy single-level parsing.
     */
    @Test
    public void testPrecedence() throws FormatException {
        // !a && b reads (!a) && b, not the legacy !(a && b)
        var atoms = Map.of("a", true, "b", false, "c", true);
        assertEquals(false, eval("!a && b", atoms));
        // a && b || c reads (a && b) || c, not the legacy a && (b || c)
        assertEquals(true, eval("a && b || c", Map.of("a", false, "b", true, "c", true)));
        // || binds stronger than ->: a || b -> c reads (a || b) -> c, which is
        // false here, while a || (b -> c) would be true
        assertEquals(false, eval("a || b -> c", Map.of("a", true, "b", false, "c", false)));
        // -> is right-associative: a -> b -> c reads a -> (b -> c), true by the
        // false antecedent, while the left-associative reading would be false
        assertEquals(true, eval("a -> b -> c", Map.of("a", false, "b", true, "c", false)));
    }

    /** Tests that malformed formulas are rejected. */
    @Test
    public void testErrors() {
        var atoms = Map.of("a", true);
        assertThrows(FormatException.class, () -> eval("", atoms));
        assertThrows(FormatException.class, () -> eval("a &&", atoms));
        assertThrows(FormatException.class, () -> eval("(a", atoms));
        assertThrows(FormatException.class, () -> eval("a b", atoms));
        assertThrows(FormatException.class, () -> eval("&& a", atoms));
    }
}
