/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.explore.util;

import groove.control.Location;
import groove.trans.Rule;

import java.util.Collection;
import java.util.Iterator;

/**
 * A cache that iterates over all rules, regardless of any priorities or control
 * information.
 */
public class SimpleCache implements ExploreCache {

    /**
     * Creates a simple cache over a set of rules.
     * @param rules
     */
    public SimpleCache(Collection<Rule> rules, boolean isRandomized) {
        this.last = null;
        this.rules = rules;
        if (isRandomized) {
            this.internalIterator = new RandomizedIterator<Rule>(rules);
        } else {
            this.internalIterator = rules.iterator();
        }
    }

    public void updateExplored(Rule rule) {
        if (this.internalIterator instanceof RandomizedIterator<?>) {
            ((RandomizedIterator<Rule>) this.internalIterator).removeFromIterator(rule);
        } else {
            // we can advance the iterator to rule, if that rule was not
            // returned before
            // this however requires the additional field rules, which otherwise
            // is not needed
            Iterator<Rule> it = this.rules.iterator();
            boolean met = false;
            while (it.hasNext() && (last() != null) && !met) {
                if (it.next().equals(rule)) {
                    met = true;
                }
            }
            if (!met) {
                while (this.internalIterator.hasNext()
                    && !this.internalIterator.next().equals(rule)) {
                    // empty
                }
            }
        }
    }

    public void updateMatches(Rule rule) {
        // does nothing
    }

    public boolean hasNext() {
        return this.internalIterator.hasNext();
    }

    public Rule next() {
        this.last = this.internalIterator.next();
        return this.last;
    }

    public void remove() {
        this.internalIterator.remove();
    }

    public Location getTarget(Rule rule) {
        return null;
    }

    public Rule last() {
        return this.last;
    }

    private Rule last;
    private final Collection<Rule> rules;
    private Iterator<Rule> internalIterator;
}
