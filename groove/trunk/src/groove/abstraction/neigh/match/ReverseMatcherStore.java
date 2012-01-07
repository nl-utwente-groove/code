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
package groove.abstraction.neigh.match;

import groove.abstraction.neigh.MyHashMap;
import groove.match.Matcher;
import groove.match.MatcherFactory;
import groove.match.SearchEngine.SearchMode;
import groove.match.plan.PlanSearchEngine;
import groove.trans.Anchor;
import groove.trans.Condition;
import groove.trans.Rule;

import java.util.Map;

/**
 * Store of rule matchers created with REVERSE search mode. Abstraction uses
 * MINIMAL search mode for the pre-matches but if the rule has NACs we need
 * to check for them in REVERSE mode. Since the MINIMAL matcher is already 
 * installed in the rule object, we have to keep an external store for the
 * normal matchers.
 * 
 * @author Eduardo Zambon
 */
public final class ReverseMatcherStore {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** The store. */
    private static Map<Rule,Matcher> store;

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** Returns true if the store has been properly initialised. */
    private static boolean isInitialised() {
        return store != null;
    }

    /**
     * Initialises the store. This method is not reentrant. Before calling it
     * again, make sure to call terminate() first.
     */
    public static void initialise() {
        store = new MyHashMap<Rule,Matcher>();
    }

    /** Terminates the store. */
    public static void terminate() {
        store = null;
    }

    /**
     * Returns the normal matcher associated with the given rule, lazily
     * creating a matcher if none is found in the store. The matcher is created
     * with the main condition of the rule as the seed. 
     */
    public static Matcher getMatcher(Rule rule) {
        assert isInitialised();
        Matcher result = store.get(rule);
        if (result == null) {
            PlanSearchEngine engine =
                PlanSearchEngine.getInstance(SearchMode.REVERSE);
            MatcherFactory factory = MatcherFactory.instance();
            factory.setEngine(engine);
            Condition condition = rule.getCondition();
            result = factory.createMatcher(condition, new Anchor(rule.lhs()));
            store.put(rule, result);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor to avoid object creation.
     */
    private ReverseMatcherStore() {
        // Empty by design.
    }
}
