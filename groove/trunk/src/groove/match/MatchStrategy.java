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

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface wrapping a match strategy. A class implementing this interface will
 * generate element maps given a target graph, together with and a partial
 * (initial) map to that target graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface MatchStrategy<Result> {
    /**
     * Returns an iterator over all matches to a given graph that extend a given
     * partial match. The partial match should be defined precisely for the
     * pre-matched elements indicated when constructing the match strategy. This
     * method allows the matches to be computed lazily.
     * @param host the graph into which the matching is to go
     * @param anchorMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @return an iterator over all mappings to the elements of
     *         <code>host</code> that augment <code>preMatch</code> and
     *         fulfil the requirements to be total matches
     */
    public Iterator<Result> getMatchIter(HostGraph host, RuleToHostMap anchorMap);

    /** 
     * Returns the first match that satisfies a given property. 
     * @param host the host graph into which the matching is to go
     * @param anchorMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param property a property used to filter the match. The result
     *        of the method is guaranteed to satisfy the property.
     */
    public Result find(HostGraph host, RuleToHostMap anchorMap,
            Property<Result> property);

    /** 
     * Returns the list of all matches that satisfy a given property. 
     * @param host the host graph into which the matching is to go
     * @param anchorMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param property a property used to filter the matches. All matches
     *        in the returned list are guaranteed to satisfy the property.
     */
    public Collection<Result> findAll(HostGraph host, RuleToHostMap anchorMap,
            Property<Result> property);

    /** 
     * Traverses the matches, and calls a visit method on them.
     * The traversal stops when the visit method returns {@code false}.
     * @param host the host graph into which the matching is to go
     * @param anchorMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param visitor the object whose visit method is invoked for all matches
     * @return the result of the visitor after the traversal
     * @see Visitor#visit(Object)
     * @see Visitor#getResult()
     */
    public <T> T visitAll(HostGraph host, RuleToHostMap anchorMap,
            Visitor<Result,T> visitor);
}
