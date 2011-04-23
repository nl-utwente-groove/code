// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Condition.java,v 1.4 2007-11-29 12:52:08 rensink Exp $
 */
package groove.trans;

import groove.graph.LabelStore;
import groove.rel.LabelVar;
import groove.util.Fixable;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Interface for conditions over graphs.
 * A condition is a hierarchical structure, the levels of which are
 * alternating between existentially and universally matched patterns.
 * The patterns on different levels are connected by morphisms, which
 * may merge or rename nodes.
 * <p>
 * A condition consists of the following elements:
 * <ul>
 * <li> The <i>root</i>: the parent graph in the condition hierarchy.
 * A condition can only be matched relative to a match of its root. A condition
 * is called <i>ground</i> if its root is the empty graph.
 * <li> The <i>pattern</i>: the graph describing the structure that is to
 * be matched in a host graph. The (implicit) morphism between the root and
 * the pattern is based on node and edge identity, and is not explicitly
 * stored.
 * <li> The <i>seed</i>: the intersection of the root and the pattern.
 * The seed is thus the subgraph of the pattern that is pre-matched
 * before the condition itself is matched.
 * <li> The <i>anchor</i>: the subgraph of the pattern whose exact image in the
 * host graph is relevant. This includes at least the seed and the elements mapped
 * to the next levels in the condition tree.
 * <li> The <i>subconditions</i>: the next levels in the condition tree. Each
 * subcondition has the pattern of this condition as its root.
 * </ul>
 * The following concepts play a role when matching a condition:
 * <ul>
 * <li> A <i>context map</i>: Mapping from the root to a host graph
 * <li> A <i>seed map</i>: Mapping from the seed to a host graph. This is
 * derived from a context map using the condition's root map
 * <li> A <i>pattern map</i>: Mapping from the pattern to the host graph. This
 * is determined by searching for an extension to a seed map.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Condition extends Fixable {
    /**
     * Returns the name of this predicate. A return value of <code>null</code>
     * indicates that the predicate is unnamed.
     */
    public RuleName getName();

    /**
     * Indicates if this condition is closed, which is to say that it has
     * an empty root.
     * @return <code>true</code> if this condition has an empty root.
     */
    public boolean isGround();

    /**
     * Morphism from the root of this condition to the condition pattern.
     * The root map identifies the seed, i.e., the elements of the pattern that 
     * are matched before the condition is tested.
     */
    public RuleGraphMorphism getRootMap();

    /**
     * Set of variables in the pattern of this condition that also occur in root
     * elements.
     */
    public Set<LabelVar> getRootVars();

    /**
     * The codomain of the root morphism.
     */
    public RuleGraph getPattern();

    /** Returns the secondary properties of this graph condition. */
    public SystemProperties getSystemProperties();

    /**
     * Sets the label store of this graph condition.
     */
    public void setLabelStore(LabelStore labelStore);

    /**
     * Returns the label store of this graph condition.
     * The label store must be set before the graph is fixed.
     */
    public LabelStore getLabelStore();

    /**
     * Tests if this graph condition is internally consistent. Inconsistencies
     * may arise for instance due to incompatibility of the actual condition and
     * the secondary properties, as returned by {@link #getSystemProperties()}. The
     * method does nothing if this graph condition is consistent, and throws an
     * exception if it is not.
     * @throws FormatException if this graph condition is inconsistent. The
     *         exception contains a list of errors.
     */
    public void testConsistent() throws FormatException;

    /**
     * Returns the collection of sub-conditions of this graph condition. The
     * intended interpretation of the sub-conditions (as conjuncts or disjuncts)
     * depends on this condition.
     */
    public Collection<? extends Condition> getSubConditions();

    /**
     * Adds a sub-condition to this graph condition.
     * @param condition the condition to be added
     * @see #getSubConditions()
     */
    public void addSubCondition(Condition condition);

    /**
     * Tests if this condition is ground and has a match to a given host graph.
     * Convenience method for <code>getMatchIter(host, null).hasNext()</code>
     */
    public boolean hasMatch(HostGraph host);

    /**
     * Returns a match of this condition into a given host graph, given a
     * matching of the root graph.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the root of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     */
    public Match getMatch(HostGraph host, RuleToHostMap contextMap);

    /**
     * Returns the collection of all matches into a given host graph, given a
     * matching of the root graph.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the root of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     */
    public Collection<? extends Match> getAllMatches(HostGraph host,
            RuleToHostMap contextMap);

    /**
     * Returns an iterator over all matches for a given host graph, given a
     * matching of the pattern graph.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     */
    @Deprecated
    public Iterator<? extends Match> getMatchIter(HostGraph host,
            RuleToHostMap contextMap);
}