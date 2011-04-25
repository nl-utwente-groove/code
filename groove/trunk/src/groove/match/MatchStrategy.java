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
 * $Id $
 */

package groove.match;

import groove.trans.HostGraph;
import groove.trans.RuleToHostMap;
import groove.util.Property;
import groove.util.Visitor;
import groove.util.Visitor.Collector;
import groove.util.Visitor.Finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface wrapping a match strategy. A class implementing this interface will
 * generate element maps given a target graph, together with and a partial
 * (initial) map to that target graph.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class MatchStrategy<R> {
    /** 
     * Returns the first match that satisfies a given property. 
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param property a property used to filter the match. The result
     *        of the method is guaranteed to satisfy the property.
     */
    public R find(HostGraph host, RuleToHostMap seedMap, Property<R> property) {
        Finder<R> finder = this.finder.newInstance(property);
        R result = traverse(host, seedMap, finder);
        finder.dispose();
        return result;
    }

    /** 
     * Returns the list of all matches that satisfy a given property. 
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param property a property used to filter the matches. All matches
     *        in the returned list are guaranteed to satisfy the property.
     */
    public List<R> findAll(HostGraph host, RuleToHostMap seedMap,
            Property<R> property) {
        List<R> result = new ArrayList<R>();
        Collector<R,List<R>> collector =
            this.collector.newInstance(result, property);
        traverse(host, seedMap, collector);
        collector.dispose();
        return result;
    }

    /** 
     * Traverses the matches, and calls a visit method on them.
     * The traversal stops when the visit method returns {@code false}.
     * The visitor is disposed afterwards.
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param visitor the object whose visit method is invoked for all matches.
     * The visitor is reset after usage.
     * @return the result of the visitor after the traversal
     * @see Visitor#visit(Object)
     * @see Visitor#getResult()
     * @see Visitor#dispose()
     */
    abstract public <T> T traverse(HostGraph host, RuleToHostMap seedMap,
            Visitor<R,T> visitor);

    /** Reusable finder for {@link #find(HostGraph, RuleToHostMap, Property)}. */
    private final Finder<R> finder = Visitor.newFinder(null);
    /** Reusable collector for {@link #findAll(HostGraph, RuleToHostMap, Property)}. */
    private final Collector<R,List<R>> collector = Visitor.newCollector(null);
}
