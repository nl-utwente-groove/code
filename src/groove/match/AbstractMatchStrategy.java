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
 * $Id: AbstractMatchStrategy.java,v 1.1 2007-10-02 16:15:15 rensink Exp $
 */
package groove.match;

import groove.calc.Property;
import groove.graph.Graph;
import groove.graph.NodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class providing basic functionality for match strategies.
 * The only method left to be implemented is {@link #getMatches(Graph host, NodeEdgeMap preMatch)}.
 * @param <R> the result type of the match
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractMatchStrategy<R> implements MatchStrategy<R> {
    public void setFilter(Property<R> filter) {
        this.filter = filter;
    }
    
    /**
     * Returns the filter currently set for the matches found by this strategy.
     * @return a property which may be <code>null</code>, but if not,
     * is guaranteed to hold for all matches returned by any of the 
     * search methods.
     * @see #setFilter(Property)
     */
    protected Property<R> getFilter() {
        return filter;
    }

    public R getMatch(Graph host, NodeEdgeMap preMatch) {
        Iterator<R> iter = getMatchIter(host, preMatch);
        return iter.hasNext() ? iter.next(): null;
    }

    public Iterable<R> getMatches(final Graph host, final NodeEdgeMap preMatch) {
        return new Iterable<R>() {
            public Iterator<R> iterator() {
                return getMatchIter(host, preMatch);
            }
        };
    }

    public Collection<R> getMatchSet(Graph host, NodeEdgeMap preMatch) {
        Collection<R> result = new ArrayList<R>();
        for (R match: getMatches(host, preMatch)) {
            result.add(match);
        }
        return result;
    }

    /** 
     * Additional property that has to be satisfied by all matches returned
     * by the matcher.
     */
    private Property<R> filter;
}
