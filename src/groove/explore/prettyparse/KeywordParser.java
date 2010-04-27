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
package groove.explore.prettyparse;

import groove.explore.encode.Serialized;

/**
 * A <code>KeywordParser</code> parses a text for a given keyword or one of its
 * alternative representations. If parsing succeeds, a new
 * <code>Serialized</code> is returned with the given keyword.
 * 
 * @see Serialized
 * @author Maarten de Mol
 */
public class KeywordParser {

    // The keyword to search for. 
    private final String keyword;

    // Alternative representations of the keyword.
    private final String[] alternatives;

    /**
     * Constructor. Builds a <code>KeywordParser</code> out of a given keyword
     * and a list of alternative representations.
     * Assumes that the keyword and its alternatives are not <code>null</code>.
     */
    public KeywordParser(String keyword, String... alternatives) {
        this.keyword = keyword;
        this.alternatives = alternatives;
    }

    /**
     * The parse method, which simply compares a given text to all available
     * alternatives. Returns a <code>Serialized</code> with the keyword when
     * parsing succeeds, and <code>null</code> otherwise.
     */
    public Serialized parse(String text) {
        if (text.equals(this.keyword)) {
            return new Serialized(this.keyword);
        }
        for (int i = 0; i < this.alternatives.length; i++) {
            if (text.equals(this.alternatives[i])) {
                return new Serialized(this.keyword);
            }
        }
        return null;
    }

    /**
     * Builds a (pretty printed) regular expression that corresponds to the
     * language that is accepted by the parser.
     */
    public String asRegularExpression() {
        if (this.alternatives.length == 0) {
            return this.keyword;
        }
        StringBuffer regExp = new StringBuffer();
        regExp.append("(");
        regExp.append(this.keyword);
        for (int i = 0; i < this.alternatives.length; i++) {
            regExp.append("|");
            regExp.append(this.alternatives[i]);
        }
        regExp.append(")");
        return regExp.toString();
    }
}
