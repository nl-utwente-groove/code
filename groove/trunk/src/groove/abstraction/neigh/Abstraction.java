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
package groove.abstraction.neigh;

import groove.abstraction.neigh.match.ReverseMatcherStore;
import groove.abstraction.neigh.trans.NeighAnchorFactory;
import groove.match.MatcherFactory;
import groove.match.SearchEngine.SearchMode;
import groove.match.plan.PlanSearchEngine;
import groove.trans.DefaultAnchorFactory;
import groove.trans.Rule;

/**
 * Basic collection of methods for initialising/terminating the abstraction
 * mechanism.
 * 
 * @author Eduardo Zambon
 */
public final class Abstraction {

    /** Enters abstraction mode. */
    public static void initialise() {
        Rule.setAnchorFactory(NeighAnchorFactory.getInstance());
        // Make sure that the search engine is set to minimal mode. This is
        // needed when we have rules with NACs.
        MatcherFactory.instance().setEngine(
            PlanSearchEngine.getInstance(SearchMode.MINIMAL));
        ReverseMatcherStore.initialise();
    }

    /** Leaves abstraction mode. */
    public static void terminate() {
        Rule.setAnchorFactory(DefaultAnchorFactory.getInstance());
        MatcherFactory.instance().setEngine(
            PlanSearchEngine.getInstance(SearchMode.NORMAL));
        ReverseMatcherStore.terminate();
    }
}