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
 * $Id: Rule.java,v 1.18 2007-10-02 11:59:14 rensink Exp $
 * $Date: 2007-10-02 11:59:14 $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeMap;

import java.util.Comparator;

/**
 * Interface of a production rule.
 * The rule essentially consists of a left hand side graph,
 * a right hand side graph, a rule morphism and a set of NACs.
 * [AR: In the future the interface might provide less functionality;
 *  instead there will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision: 1.18 $
 */
public interface Rule extends Comparable<Rule>, GraphCondition {
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
     * @ensure <tt>result != null</tt>
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
    
    //
	//    /**
	//     * Returns the array of anchor elements of this rule.
	//     * These are the elements from the left hand side that fully determine
	//     * the matchings of the rule.
	//     */
	//    public Element[] anchor();
//	
//    /**
//     * Returns the array of creator nodes of this rule.
//     * @deprecated Only valid in {@link SPORule}
//     */
//    @Deprecated
//    public Node[] getCreatorNodes();
    
    /**
     * Lazily creates and returns a matcher for rule events of this rule.
     * The matcher will try to extend anchor maps to full matches.
     */
    public MatchStrategy getEventMatcher();

	/**
	 * Factory method to create an event based on this rule.
	 * @param anchorMap the anchor map of the new event; should map at least
	 * the elements of the rule anchor to elements presumably in the host graph
	 * @param nodeFactory an object queried for fresh node numbers; may be
	 * <code>null</code>
	 * @param reuse if <code>true</code>, the created event will store
	 * data structures internally for reuse. This takes space, but saves space
	 * if events are shared among transformations.
	 */
	public RuleEvent newEvent(VarNodeEdgeMap anchorMap, NodeFactory nodeFactory, boolean reuse);
}
