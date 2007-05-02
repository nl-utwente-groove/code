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
 * $Id: Rule.java,v 1.8 2007-05-02 08:44:33 rensink Exp $
 * $Date: 2007-05-02 08:44:33 $
 */
package groove.trans;

import java.util.Comparator;
import java.util.List;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.rel.VarGraph;
import groove.rel.VarNodeEdgeMap;

/**
 * Interface of a production rule.
 * The rule essentially consists of a left hand side graph,
 * a right hand side graph, a rule morphism and a set of NACs.
 * [AR: In the future the interface might provide less functionality;
 *  instead there will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision: 1.8 $
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

	/**
     * Sets the priority of this rule.
     * Should be called at initialization time, before the first application.
     * @param priority the priority of the rule
     * @deprecated Priority should be set at construction time
     */
	@Deprecated
	public void setPriority(int priority);

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
    public VarGraph lhs();

    /**
     * Returns the right hand side of this Rule.
     * @ensure <tt>result == morphism().cod()</tt>
     */
    public VarGraph rhs();

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

    /**
     * Returns the array of anchor elements of this rule.
     * These are the elements from the left hand side that fully determine
     * the matchings of the rule.
     */
    public Element[] anchor();
    
    /**
     * Returns the array of co-anchor elements of this rule.
     * These are the elements from the right hand side that together with
     * the matching and derivation morphism fully determine the comatchings of the rule.
     * Essentially, they are the creator nodes.
     */
    public Node[] coanchor();
    
    /**
     * Factory method to create an event based on this rule.
     * Parameters are an anchor map and an optional derivation record.
     * @param anchorMap the anchor map of the event, being a mapping from the 
     * anchors to elements presumably in the host graph
     * @param record an object queried for fresh node numbers
     */
    public RuleEvent newEvent(VarNodeEdgeMap anchorMap, SystemRecord record);

    /**
     * Lazily creates and returns a search plan for rule events of this rule,
     * which tries to find the anchor image in a given graph. 
     */
    public List<SearchItem> getAnchorSearchPlan();
    
    /**
     * Factory method to create an application for this rule from a given
     * matching.
     * @see RuleEvent#newApplication(Graph)
     * @deprecated use {@link #newEvent(VarNodeEdgeMap, SystemRecord)} and
     * {@link RuleEvent#newApplication(Graph)} instead
     */
    @Deprecated
    public RuleApplication createApplication(Matching match);

    /** 
     * Adds a negative application condition to this rule.
     * After adding it, the nac may not be modified.
     * @param nac the negative application condition to be added
     * @require <tt>nac.source().equals(lhs())</tt>
     * @ensure <tt>nacSet().contains(nac)</tt>
     * @deprecated use {@link DefaultGraphCondition#setAndNot(GraphTest)} instead
     */
    @Deprecated
    public void addNAC(NAC nac);
}