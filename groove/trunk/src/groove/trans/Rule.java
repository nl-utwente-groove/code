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
 * $Id: Rule.java,v 1.3 2007-04-04 07:04:20 rensink Exp $
 * $Date: 2007-04-04 07:04:20 $
 */
package groove.trans;

import java.util.List;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.rel.VarGraph;

/**
 * Interface of a production rule.
 * The rule essentially consists of a left hand side graph,
 * a right hand side graph, a rule morphism and a set of NACs.
 * [AR: In the future the interface might provide less functionality;
 *  instead there will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public interface Rule extends Comparable<Rule>, GraphCondition {
	/**
	 * The lowest rule priority, which is also the default value if no
	 * explicit priority is given.
	 */
	static public final int DEFAULT_PRIORITY = 0;

	/**
     * Sets the priority of this rule.
     * Should be called at initialization time, before the first application.
     * @param priority the priority of the rule
     */
	public void setPriority(int priority);

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
     * Returns the properties of this rule.
     * May be <code>null</code> if there is no associated grammar.
     */
    public RuleProperties getProperties();

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
    
//    /**
//     * Factory method to create an event based on this rule and a given anchor map.
//     * @ensure <code>result.getRule() == this</code>
//     */
//    public RuleEvent getEvent(VarNodeEdgeMap anchorMap);
//
    /**
     * Lazily creates and returns a search plan for rule events of this rule,
     * which tries to find the anchor image in a given graph. 
     */
    public List<SearchItem> getEventSearchPlan();
    
    /**
     * Factory method to create an application for this rule from a given
     * matching.
     * @see RuleEvent#createApplication(Graph)
     */
    public RuleApplication createApplication(Matching match);

    /** 
     * Adds a negative application condition to this rule.
     * After adding it, the nac may not be modified.
     * @param nac the negative application condition to be added
     * @require <tt>nac.source().equals(lhs())</tt>
     * @ensure <tt>nacSet().contains(nac)</tt>
     */
    public void addNAC(NAC nac);
}