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
package groove.explore.util;

import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.Location;
import groove.lts.GraphState;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Olaf Keijsers
 * @version $Revision $
 */
public class ControlStateCache implements ExploreCache {
    /**
     * Creates a new ControlStateCache
     * @param location the location this ControlStateCache will operate on
     * @param state the GraphState this ControlStateCache will operate on
     */
    public ControlStateCache(Location location, GraphState state) {
        this.location = location;
        this.failed = new HashSet<Rule>();
        this.matched = new HashSet<Rule>();

        this.iterator = createIterator(this.location);
    }

    private Iterator<Rule> createIterator(Location location) {
        Set<Rule> enabledRules =
            location.getEnabledRules(this.matched, this.failed);
        return enabledRules.iterator();
    }

    @Override
    public Location getTarget(Rule rule) {
        return this.getTransition(rule).target();
    }

    /**
     * Gets the transition used to exit this state given a Rule
     * @param rule the Rule to use on the required transition
     * @return a ControlTransition which can be traversed by applying rule
     */
    public ControlTransition getTransition(Rule rule) {
        return ((ControlState) this.location).getTransition(rule);
    }

    @Override
    public void updateExplored(Rule rule) {
        if (!this.matched.contains(rule)) {
            this.failed.add(rule);
        }
    }

    @Override
    public void updateMatches(Rule rule) {
        this.matched.add(rule);
    }

    @Override
    public Rule last() {
        return this.last;
    }

    @Override
    public boolean hasNext() {
        if (this.iterator == null) {
            return false;
        } else if (this.iterator.hasNext()) {
            return true;
        } else {
            this.iterator = createIterator(this.location);
            if (this.iterator.hasNext()) {
                return true;
            } else {
                this.iterator = null;
                return false;
            }
        }
    }

    @Override
    public Rule next() {
        // TODO: FIX THIS for interruptible
        if (this.iterator == null) {
            return null;
        }
        if (!this.iterator.hasNext()) {
            this.iterator = createIterator(this.location);
        }
        if (!this.iterator.hasNext()) {
            this.iterator = null;
            return null;
        } else {
            this.last = this.iterator.next();
            return this.last;
        }
    }

    /**
     * Unsupported method
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /** The set of rules that are known not to match in this cache's state. */
    private final Set<Rule> failed;
    /** The set of rules that are known to match in this cache's state. */
    private final Set<Rule> matched;
    /** The current rule iterator. */
    private Iterator<Rule> iterator;
    /** Control location on which this cache works. */
    private final Location location;
    /** Most recently returned rule. */
    private Rule last;
}
