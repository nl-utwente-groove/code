/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.encode;

import java.util.HashSet;
import java.util.Set;

import groove.explore.strategy.Boundary;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.RuleSetBoundary;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.model.GrammarModel;
import groove.util.parse.FormatException;

/**
 * Encoding of a model checking boundary.
 * The boundary is either a number (the graph size) or an identifier
 * (the
 * <p>
 * @see EncodedType
 * @author Arend Rensink
 */
public class EncodedBoundary implements EncodedType<Boundary,String> {
    /**
     * Default constructor. Creates local store only.
     */
    public EncodedBoundary() {
        // empty
    }

    @Override
    public EncodedTypeEditor<Boundary,String> createEditor(GrammarModel grammar) {
        return null;
    }

    @Override
    public Boundary parse(Grammar rules, String source) throws FormatException {
        // Split the source String (assumed to be a comma separated list).
        String[] units = source.split(",");
        assert units.length > 0;
        if (Character.isLetter(units[0].charAt(0))) {
            // this is a list of names making up a rule set boundary
            Set<Rule> ruleSet = new HashSet<>();
            for (int i = 0; i < units.length; i++) {
                Rule rule = rules.getRule(QualName.parse(units[i]));
                if (rule == null) {
                    throw new FormatException(
                        "Error in rule set boundary specification '%s': no rule '%s' in grammar",
                        source, units[i]);
                }
                ruleSet.add(rule);
            }
            return new RuleSetBoundary(ruleSet);
        } else {
            // this is a pair of numbers making up a graph size boundary
            if (units.length != 2) {
                throw new FormatException(
                    "Wrong graph size boundary specification '%s': wrong number of arguments",
                    source);
            }
            try {
                int start = Integer.parseInt(units[0]);
                if (start < 0) {
                    throw new FormatException(
                        "Wrong graph size boundary specification '%s': negative start size %d",
                        source, start);
                }
                int step = Integer.parseInt(units[1]);
                if (step <= 0) {
                    throw new FormatException(
                        "Wrong graph size boundary specification '%s': non-positive step size %d",
                        source, step);
                }
                return new GraphNodeSizeBoundary(start, step);
            } catch (NumberFormatException e) {
                throw new FormatException(
                    "Wrong graph size boundary specification '%s': arguments are not numbers",
                    source);
            }
        }
    }
}
