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
package nl.utwente.groove.verify;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.EnabledRuleParser;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parser for a bounded model checking boundary: either a comma-separated
 * list of rule names (a rule set boundary) or a pair of numbers giving the
 * initial graph size and increment (a graph size boundary).
 * Successor of the parsing in the legacy {@code EncodedBoundary}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class BoundaryParser {
    private BoundaryParser() {
        // static utility class
    }

    /**
     * Parses a boundary specification, resolving rule names against a grammar.
     * @throws FormatException if the specification is malformed, or names a
     * rule that is not an enabled rule of the grammar
     */
    public static Boundary parse(Grammar grammar, String text) throws FormatException {
        String[] units = text.split(",");
        if (text.isEmpty() || units.length == 0) {
            throw new FormatException("Empty boundary specification");
        }
        if (units[0].isEmpty()) {
            // guard the discriminating charAt below against a leading comma
            throw new FormatException("Malformed boundary specification '%s': empty first element",
                text);
        }
        if (Character.isLetter(units[0].charAt(0))) {
            // this is a list of names making up a rule set boundary
            Set<Rule> ruleSet = new LinkedHashSet<>();
            for (String unit : units) {
                ruleSet.add(EnabledRuleParser.parse(grammar, unit));
            }
            return new RuleSetBoundary(ruleSet);
        } else {
            // this is a pair of numbers making up a graph size boundary
            if (units.length != 2) {
                throw new FormatException(
                    "Wrong graph size boundary specification '%s': wrong number of arguments",
                    text);
            }
            try {
                int start = Integer.parseInt(units[0]);
                if (start < 0) {
                    throw new FormatException(
                        "Wrong graph size boundary specification '%s': negative start size %d",
                        text, start);
                }
                int step = Integer.parseInt(units[1]);
                if (step <= 0) {
                    throw new FormatException(
                        "Wrong graph size boundary specification '%s': non-positive step size %d",
                        text, step);
                }
                return new GraphNodeSizeBoundary(start, step);
            } catch (NumberFormatException e) {
                throw new FormatException(
                    "Wrong graph size boundary specification '%s': arguments are not numbers",
                    text);
            }
        }
    }
}
