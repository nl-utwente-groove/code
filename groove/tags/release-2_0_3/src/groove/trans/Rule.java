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
 * $Id: Rule.java,v 1.25 2007-11-29 12:52:09 rensink Exp $
 * $Date: 2007-11-29 12:52:09 $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeMap;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Interface of a production rule.
 * The rule essentially consists of a left hand side graph,
 * a right hand side graph, a rule morphism and a set of NACs.
 * [AR: In the future the interface might provide less functionality;
 *  instead there will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision: 1.25 $
 */
public interface Rule extends Comparable<Rule>, Condition {
	/** Returns the name of this rule. */
	public RuleNameLabel getName();
	
	/**
	 * Returns the priority of this object.
	 * A higher number means higher priority, with {@link #DEFAULT_PRIORITY} the lowest.
	 */
	public int getPriority();

    /**
     * Returns the left hand side of this Rule.
     * @ensure <tt>result == morphism().source()</tt>
     */
    public Graph lhs();

    /**
     * Returns the right hand side of this Rule.
     * @ensure <tt>result == morphism().cod()</tt>
     */
    public Graph rhs();

    /**
     * Returns the rule morphism, which is the partial morphism from LHS
     * to RHS.
     * @see #lhs()
     * @see #rhs()
     */
    public Morphism getMorphism();

    /**
     * Indicates if application of this rule actually changes the host graph.
     * If <code>false</code>, this means the rule is essentially a graph condition.
     */
    public boolean isModifying();
    
    /** Indicates if the rule has (node or edge) creators. */
    public boolean hasCreators();
	    
    /** Indicates if the rule has node mergers. */
    public boolean hasMergers();
    
    /** 
     * Returns an iterator over the matches for a given host graph, given
     * a matching of the root context.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may
     * be <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>contextMap</code> is <code>null</code>
     * and the condition is not ground, or if <code>contextMap</code> is not compatible
     * with the root map
     */
    public Iterator<RuleMatch> getMatchIter(GraphShape host, NodeEdgeMap contextMap);
    
    /** 
     * Returns the collection of all matches for a given host graph, given
     * a matching of the root context.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may
     * be <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>contextMap</code> is <code>null</code>
     * and the condition is not ground, or if <code>contextMap</code> is not compatible
     * with the root map
     */
    public Iterable<RuleMatch> getMatches(GraphShape host, NodeEdgeMap contextMap);

    /**
     * Lazily creates and returns a matcher for rule events of this rule.
     * The matcher will try to extend anchor maps to full matches.
     */
    public MatchStrategy<VarNodeEdgeMap> getEventMatcher();

	/**
	 * Factory method to create an event based on this rule.
	 * @param anchorMap the anchor map of the new event; should map at least
	 * the elements of the rule anchor to elements presumably in the host graph
	 * @param nodeFactory an object queried for fresh node numbers; may be
	 * <code>null</code>
	 * @param reuse if <code>true</code>, the created event will store
	 * data structures internally for reuse. This takes space, but saves space
	 * if events are shared among transformations.
	 * @deprecated use {@link #getMatches(GraphShape, NodeEdgeMap)} and {@link RuleMatch#newEvent(NodeFactory, boolean)}
	 */
    @Deprecated
	public RuleEvent newEvent(VarNodeEdgeMap anchorMap, NodeFactory nodeFactory, boolean reuse);
	
	/**
	 * The lowest rule priority, which is also the default value if no
	 * explicit priority is given.
	 */
	static public final int DEFAULT_PRIORITY = 0;
	/**
	 * A comparator for priorities, encoded as {@link Integer} objects.
	 * This implementation orders priorities from high to low.
	 */
	static final public Comparator<Integer> PRIORITY_COMPARATOR = new Comparator<Integer>() {
	    public int compare(Integer o1, Integer o2) {
	        return o2.intValue() - o1.intValue();
	    }
	    
	};
}