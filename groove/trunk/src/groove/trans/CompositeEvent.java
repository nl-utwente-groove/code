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
 * $Id: CompositeEvent.java,v 1.3 2007-10-08 00:59:19 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeEvent extends AbstractEvent<Rule> {
    /** Creates a new event on the basis of a given event set. */
    public CompositeEvent(Rule rule, SortedSet<SPOEvent> eventSet) {
    	super(rule);
        this.eventSet = eventSet;
    }

    public boolean conflicts(RuleEvent other) {
        for (RuleEvent event: eventSet) {
            if (event.conflicts(other)) {
                return true;
            }
        }
        return false;
    }

    public String getAnchorImageString() {
        return "";
    }

    public List<Node> getCreatedNodes(Set<? extends Node> hostNodes) {
    	List<Node> result = new ArrayList<Node>();
    	for (RuleEvent event: getEventSet()) {
    		result.addAll(event.getCreatedNodes(hostNodes));
    	}
    	return result;
    }

    public Set<Node> getErasedNodes() {
        Set<Node> result = createNodeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getErasedNodes());
        }
        return result;
    }

    @Deprecated
    public Morphism getMatching(Graph source) {
        throw new UnsupportedOperationException();
    }

    public RuleMatch getMatch(Graph source) {
    	// the events are ordered according to rule level
    	// so we can build a stack of corresponding matches
    	Stack<RuleMatch> matchStack = new Stack<RuleMatch>();
    	for (SPOEvent event: getEventSet()) {
    		RuleMatch match = new RuleMatch(event.getRule(), event.getMatch(source).getElementMap());
    		int[] eventLevel = event.getRule().getLevel();
    		int eventDepth = eventLevel.length;
    		assert eventDepth/2 <= matchStack.size();
    		// pop the stack until the right nesting depth
    		while (eventDepth/2 < matchStack.size()) {
    			matchStack.pop();
    		}
    		// add this match to the match of the parent event
    		// (which is now on the top of the stack)
    		if (eventDepth > 0) {
    			RuleMatch parentMatch = matchStack.peek();
    			assert eventDepth <= 2 || parentMatch.getRule().getLevel()[eventDepth-3] == eventLevel[eventDepth-3];
    			parentMatch.addSubMatch(match);
    		}
    		// add this match to the stack, to receive its sub-matches
    		matchStack.push(match);
    	}
		return matchStack.get(0);
	}

	public MergeMap getMergeMap() {
        MergeMap result = new MergeMap();
        for (RuleEvent event: eventSet) {
            result.putAll(event.getMergeMap());
        }
        return result;
    }

    @Deprecated
    public VarNodeEdgeMap getSimpleCoanchorMap() {
        return null;
    }

    public Set<Edge> getSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getSimpleCreatedEdges());
        }
        return result;
    }

	public Set<Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
        Set<Edge> result = createEdgeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getComplexCreatedEdges(createdNodes));
        }
        return result;
	}

	public Set<Edge> getSimpleErasedEdges() {
        Set<Edge> result = createEdgeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getSimpleErasedEdges());
        }
        return result;
    }

    /**
	 * @deprecated Use {@link #hasMatch(Graph)} instead
	 */
	@Deprecated
	public boolean hasMatching(Graph source) {
		return hasMatch(source);
	}

	public boolean hasMatch(Graph source) {
        for (RuleEvent event: getEventSet()) {
            if (!event.hasMatch(source)) {
                return false;
            }
        }
        return true;
    }

    public int compareTo(RuleEvent other) {
    	int result = getRule().compareTo(other.getRule());
    	if (result == 0) {
    		// the same rule, so the other is also a composite event
			Set<? extends RuleEvent> myEvents = getEventSet();
			Set<? extends RuleEvent> otherEvents = ((CompositeEvent) other).getEventSet();
			// more events = larger
			result = myEvents.size() - otherEvents.size();
			if (result == 0) {
				// compare the individual events lexicographically
				Iterator<? extends RuleEvent> myEventIter = myEvents.iterator();
				Iterator<? extends RuleEvent> otherEventIter = otherEvents.iterator();
				while (result == 0 && myEventIter.hasNext()) {
					result = myEventIter.next().compareTo(otherEventIter.next());
				}
			}
		}
    	return result;
    }

    /** Returns the set of constituent events of this set event. */
    public Set<SPOEvent> getEventSet() {
    	return eventSet;
    }
    

    /**
     * The hash code is based on that of the rule and an initial fragment of the
     * anchor images.
     */
	@Override
    public int hashCode() {
		if (hashCode == 0) {
			hashCode = computeHashCode();
			if (hashCode == 0) {
				hashCode = 1;
			}
		}
		return hashCode;
    }
	
	private int computeHashCode() {
		return getEventSet().hashCode();
	}
    
    /**
     * Two composite events are equal if they contain the same primitive events.
     */
	@Override
    public boolean equals(Object obj) {
    	if (this == obj) {
    		return true;
    	}
    	if (!(obj instanceof CompositeEvent)) {
    		return false;
    	}
    	if (getEventSet().size() != ((CompositeEvent) obj).getEventSet().size()) {
    		return false;
    	}
    	Iterator<SPOEvent> myEventIter = getEventSet().iterator();
    	Iterator<SPOEvent> otherEventIter = ((CompositeEvent) obj).getEventSet().iterator();
    	while (myEventIter.hasNext()) {
    		if (!myEventIter.next().equals(otherEventIter.next())) {
    			return false;
    		}
    	}
    	return true;
    }
    
	@Override
	public String toString() {
	    return eventSet.toString();
	}
	
    /** The set of events constituting this event. */
    private final SortedSet<SPOEvent> eventSet;
    /** The hash code of this event. */
    private int hashCode;
}
