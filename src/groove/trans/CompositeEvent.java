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
 * $Id: CompositeEvent.java,v 1.9 2008-02-06 17:04:38 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.util.CacheReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeEvent extends AbstractEvent<Rule,CompositeEvent.CompositeEventCache> {
    /** Creates a new event on the basis of a given event set. */
    public CompositeEvent(Rule rule, SortedSet<SPOEvent> eventSet) {
    	super(reference, rule);
    	this.eventArray = new SPOEvent[eventSet.size()];
        eventSet.toArray(this.eventArray);
    }

    public boolean conflicts(RuleEvent other) {
        for (RuleEvent event: eventArray) {
            if (event.conflicts(other)) {
                return true;
            }
        }
        return false;
    }

    public String getAnchorImageString() {
        return Arrays.toString(eventArray);
    }

    public List<Node> getCreatedNodes(Set<? extends Node> hostNodes) {
    	List<Node> result = new ArrayList<Node>(eventArray.length);
    	for (SPOEvent event: eventArray) {
    		event.collectCreatedNodes(hostNodes, result);
    	}
    	return result;
    }

    @Override
    Set<Node> computeErasedNodes() {
        Set<Node> result = createNodeSet(eventArray.length);
        for (SPOEvent event: eventArray) {
            event.collectErasedNodes(result);
        }
        return result;
    }

    public RuleMatch getMatch(Graph source) {
    	// the events are ordered according to rule level
    	// so we can build a stack of corresponding matches
    	Stack<RuleMatch> matchStack = new Stack<RuleMatch>();
    	for (SPOEvent event: eventArray) {
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
        for (RuleEvent event: eventArray) {
            result.putAll(event.getMergeMap());
        }
        return result;
    }

    public Set<Edge> getSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet(eventArray.length * 2);
        for (SPOEvent event: eventArray) {
            event.collectSimpleCreatedEdges(getErasedNodes(), result);
        }
        return result;
    }

	public Set<Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
        Set<Edge> result = createEdgeSet(eventArray.length * 2);
        for (SPOEvent event: eventArray) {
            event.collectComplexCreatedEdges(getErasedNodes(), createdNodes, result);
        }
        return result;
	}

	public Set<Edge> getSimpleErasedEdges() {
        Set<Edge> result = createEdgeSet(eventArray.length * 2);
        for (SPOEvent event: eventArray) {
            event.collectSimpleErasedEdges(result);
        }
        return result;
    }

	/** 
	 * This method always returns <code>false</code> because it is quite hard 
	 * to check universally matched sub-events against a new graph, especially
	 * since the universal information was lost in the conversion from rule match to rule event.
	 */
	public boolean hasMatch(Graph source) {
        return false;
    }

	/** 
	 * Checks if the sub-events have matches in the given graph.
	 * This is <code>not</code> sufficient to make sure that the event as a whole matches!
	 */
	boolean hasSubMatches(Graph source) {
        for (RuleEvent event: eventArray) {
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
			SPOEvent[] myEventArray = eventArray;
			SPOEvent[] otherEventArray;
			if (other instanceof CompositeEvent) {
				otherEventArray = ((CompositeEvent) other).eventArray;
			} else {
				otherEventArray = new SPOEvent[] { (SPOEvent) other };
			}
			// more events = larger
			result = myEventArray.length - otherEventArray.length;
			if (result == 0) {
				// compare the individual events lexicographically
				for (int i = 0; result == 0 && i < myEventArray.length; i++) {
					result = myEventArray[i].compareTo(otherEventArray[i]);
				}
			}
		}
    	return result;
    }

    /** Returns the set of constituent events of this set event. */
    public Set<SPOEvent> getEventSet() {
    	return getCache().getEventSet();
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
		return Arrays.hashCode(eventArray);
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
    	return Arrays.equals(eventArray, ((CompositeEvent) obj).eventArray);
    }
    
	@Override
	public String toString() {
	    return Arrays.toString(eventArray);
	}
	
    @Override
	protected CompositeEventCache createCache() {
		return new CompositeEventCache();
	}

	/** The set of events constituting this event. */
    final SPOEvent[] eventArray;
    /** The hash code of this event. */
    private int hashCode;
    /** Cache reference instance for initialisation. */
    static private final CacheReference<CompositeEventCache> reference = CacheReference.<CompositeEventCache>newInstance(false);
    
    class CompositeEventCache extends AbstractEvent<Rule,CompositeEventCache>.AbstractEventCache {
    	/** Reconstructs a set of events from the array stored in the composite event. */
    	SortedSet<SPOEvent> getEventSet() {
    		if (eventSet == null) {
    			eventSet = new TreeSet<SPOEvent>(Arrays.asList(CompositeEvent.this.eventArray));
    		}
    		return eventSet;
    	}
    	
    	private SortedSet<SPOEvent> eventSet;
    }
}
