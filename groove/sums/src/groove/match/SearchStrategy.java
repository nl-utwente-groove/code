/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.grammar.host.HostGraph;
import groove.grammar.rule.RuleToHostMap;
import groove.match.plan.PlanSearchEngine;
import groove.match.rete.ReteSearchEngine;
import groove.util.Visitor;

/**
 * Strategy to traverse over all tree matches of a condition.
 * Note that this type is not intended to be exposed: the actual
 * condition and rule matching should use {@link Matcher} objects instead,
 * since these provide more functionality and, more importantly, are
 * refreshed in reaction to changes of the search engine (from
 * {@link PlanSearchEngine} to {@link ReteSearchEngine} and back).
 * @see MatcherFactory
 * @see Matcher
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SearchStrategy {
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
    public <T> T traverse(HostGraph host, RuleToHostMap seedMap, Visitor<TreeMatch,T> visitor);

    /**
     * Returns the search engine that was used to create this strategy.
     * @return the search engine; non-{@code null}
     */
    public SearchEngine getEngine();
}