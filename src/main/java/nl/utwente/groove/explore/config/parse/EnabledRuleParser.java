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
package nl.utwente.groove.explore.config.parse;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parser for a rule name, resolving to an enabled rule of a grammar.
 * Successor of the parsing in the legacy {@code EncodedEnabledRule}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class EnabledRuleParser {
    private EnabledRuleParser() {
        // static utility class
    }

    /**
     * Resolves a rule name to the corresponding enabled rule of a grammar.
     * @throws FormatException if the name does not parse as a qualified
     * name, or does not denote an enabled rule of the grammar
     */
    public static Rule parse(Grammar grammar, String name) throws FormatException {
        QualName qualName = QualName.parse(name);
        Rule rule = grammar.getRule(qualName);
        if (rule == null) {
            throw new FormatException("'%s' is not an enabled rule in the loaded grammar",
                qualName);
        }
        return rule;
    }
}
