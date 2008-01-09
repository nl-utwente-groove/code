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
 * $Id: RuleMatch.java,v 1.6 2007-10-08 12:17:34 rensink Exp $
 */
package groove.trans;

import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Match of an {@link SPORule}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleMatch extends CompositeMatch {
    /** Constructs a match for a given {@link SPORule}. */
    public RuleMatch(SPORule rule, VarNodeEdgeMap elementMap) {
    	super(elementMap);
        this.rule = rule;
    }
    
    /** Returns the rule of which this is a match. */
    public SPORule getRule() {
        return rule;
    }

    /** 
     * Creates an event on the basis of this match. 
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse flag indicating that the events will be reused, so attempts
     * should be made to gain time by sacrifying space
     */
    public RuleEvent newEvent(NodeFactory nodeFactory, boolean reuse) {
    	SortedSet<SPOEvent> eventSet = new TreeSet<SPOEvent>();
    	collectEvents(eventSet, null, nodeFactory, reuse);
    	assert !eventSet.isEmpty();
    	if (eventSet.size() == 1 && !getRule().hasSubRules()) {
    		return eventSet.iterator().next();
    	} else {
    		return new CompositeEvent(rule, eventSet);
    	}
    }
    
    /** 
     * Recursively collects the events of this match and all sub-matches
     * into a given collection.
     * @param events the resulting set of events
     * @param coContextMap mapping from the right hand side root nodes
     * (i.e., the creator nodes of the parent rule) to host nodes; or <code>null</code>
     * if the rule is the top-level rule
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse flag indicating that the events will be reused, so attempts
     * should be made to gain time by sacrifying space
     */
    private void collectEvents(Collection<SPOEvent> events, VarNodeEdgeMap coContextMap, NodeFactory nodeFactory, boolean reuse) {
    	SPOEvent myEvent = createEvent(coContextMap, nodeFactory, reuse);
    	events.add(myEvent);
    	for (Match subMatch: getSubMatches()) {
    		if (subMatch instanceof RuleMatch) {
    			((RuleMatch) subMatch).collectEvents(events, myEvent.getCoanchorMap(), nodeFactory, reuse);
    		}
    	}
    }

    /** 
     * Callback factory method for an event based on this match. 
     * @param coContextMap mapping from the right hand side root nodes
     * (i.e., the creator nodes of the parent rule) to host nodes; or <code>null</code>
     * if the rule is the top-level rule
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse flag indicating that the events will be reused, so attempts
     * should be made to gain time by sacrifying space
     */
    private SPOEvent createEvent(VarNodeEdgeMap coContextMap, NodeFactory nodeFactory, boolean reuse) {
        return new SPOEvent(getRule(), getElementMap(), coContextMap, nodeFactory, reuse);
    }

	@Override
	public Collection<RuleMatch> addSubMatchChoice(Iterable<? extends Match> choices) {
		return (Collection<RuleMatch>) super.addSubMatchChoice(choices);
	}
    
    @Override
	protected RuleMatch createMatch() {
    	return new RuleMatch(rule, getElementMap());
	}

	/** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleMatch
                && ((RuleMatch) obj).getRule().equals(getRule())
                && super.equals(obj);
    }
    
    /** This implementation takes the rule into account. */
    @Override
    protected int computeHashCode() {
        return getRule().hashCode() + super.computeHashCode();
    }

    @Override
    public String toString() {
        return String.format("Match of %s: Nodes %s, edges %s", getRule(), getElementMap().nodeMap(), getElementMap().edgeMap());
    }
    
    /** The fixed rule of which this is a match. */
    private final SPORule rule;
}
