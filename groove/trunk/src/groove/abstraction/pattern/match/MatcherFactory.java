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
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.trans.PatternRule;

import java.util.Map;

/**
 * Factory of matchers for pattern graph rules.
 * 
 * @author Eduardo Zambon
 */
public final class MatcherFactory {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** The unique instance of this class. */
    private static final MatcherFactory instance = new MatcherFactory();

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the unique instance of this class. */
    public static MatcherFactory instance() {
        return instance;
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private final Map<PatternRule,Matcher> matcherMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private constructor to avoid object creating. Use {@link #instance()}.*/
    private MatcherFactory() {
        this.matcherMap = new MyHashMap<PatternRule,Matcher>();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns the matcher associated with the given rule. */
    public Matcher getMatcher(PatternRule pRule) {
        Matcher result = this.matcherMap.get(pRule);
        if (result == null) {
            result = new Matcher(pRule);
            this.matcherMap.put(pRule, result);
        }
        return result;
    }

}
