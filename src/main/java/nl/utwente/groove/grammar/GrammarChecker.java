/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package nl.utwente.groove.grammar;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.util.parse.FormatErrorSet;

/** Functionality to check the consistency of a grammar property with a grammar. */
public interface GrammarChecker {
    /**
     * Checks the consistency of a property with a given grammar model.
     * @return the (possibly empty) set of errors in the value
     */
    public FormatErrorSet check(GrammarModel grammar, GrammarProperties.Entry value);
}
