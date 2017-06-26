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
package groove.match;

import groove.grammar.Condition;
import groove.grammar.rule.Anchor;
import groove.match.plan.PlanSearchEngine;

/**
 * A factory for matchers.
 * The factory keeps an inner {@link SearchEngine} factory for search strategies;
 * updating this inner factory will cause the match strategies to
 * refresh themselves to the corresponding new search strategy.
 * Currently this is a singleton class, but it would be quite possible to
 * have multiple instances, at the cost of linking a shared instance to
 * all the objects that may do matching.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatcherFactory {
    private MatcherFactory(boolean simple) {
        this.engine = this.defaultEngine = PlanSearchEngine.instance(simple);
    }

    /**
     * Changes the search engine set in this factory.
     * This will cause all matchers created by this factory to refresh
     * their inner search strategies.
     */
    public void setEngine(SearchEngine engine) {
        this.engine = engine;
    }

    /** Sets the search engine to the default. */
    public void setDefaultEngine() {
        setEngine(this.defaultEngine);
    }

    /** Returns the currently set search engine. */
    public SearchEngine getEngine() {
        return this.engine;
    }

    /** Creates a matcher for a given condition and default seeds. */
    public Matcher createMatcher(Condition condition) {
        return createMatcher(condition, null);
    }

    /** Creates a matcher for a given condition and explicitly specified seeds. */
    public Matcher createMatcher(Condition condition, Anchor seed) {
        return new Matcher(this, condition, seed);
    }

    /** The currently set search engine. */
    private SearchEngine engine;

    /** The default engine of this matcher factory. */
    private final SearchEngine defaultEngine;

    /** Returns the instance of the factory matching
     * simple or multi-graphs. */
    public static MatcherFactory instance(boolean simple) {
        MatcherFactory result = simple ? simpleInstance : multiInstance;
        if (result == null) {
            result = new MatcherFactory(simple);
            if (simple) {
                simpleInstance = result;
            } else {
                multiInstance = result;
            }
        }
        return result;
    }

    /** Matcher factory instance for simple graphs. */
    private static MatcherFactory simpleInstance;
    /** Matcher factory instance for multi-graphs. */
    private static MatcherFactory multiInstance;
}
