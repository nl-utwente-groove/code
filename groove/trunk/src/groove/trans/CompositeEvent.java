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
 * $Id: CompositeEvent.java,v 1.10 2008-03-03 21:27:40 rensink Exp $
 */
package groove.trans;

import groove.graph.Element;
import groove.graph.Node;
import groove.util.CacheReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeEvent extends
        AbstractEvent<Rule,CompositeEvent.CompositeEventCache> {
    /**
     * Creates a composite event on the basis of a given constituent event set.
     * @param rule the rule for which this is an event
     * @param eventSet ordered non-empty collection of constituent events. The
     *        order is assumed to be the prefix traversal order of the
     *        dependency tree of the events, meaning the the first element is
     *        the event corresponding to the top level of <code>rule</code>.
     */
    public CompositeEvent(Rule rule, Collection<SPOEvent> eventSet,
            boolean reuse) {
        super(reference, rule, reuse);
        this.eventArray = new SPOEvent[eventSet.size()];
        eventSet.toArray(this.eventArray);
    }

    public boolean conflicts(RuleEvent other) {
        for (RuleEvent event : this.eventArray) {
            if (event.conflicts(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Element getAnchorImage(int i) {
        return this.eventArray[0].getAnchorImage(i);
    }

    @Override
    public int getAnchorSize() {
        return this.eventArray[0].getAnchorSize();
    }

    public String getAnchorImageString() {
        List<String> eventLabels = new ArrayList<String>();
        for (SPOEvent event : this.eventArray) {
            eventLabels.add(event.getRule().getName()
                + event.getAnchorImageString());
        }
        return Arrays.toString(eventLabels.toArray());
    }

    public Set<HostNode> getCreatedNodes(Set<? extends HostNode> hostNodes) {
        Set<HostNode> result =
            new LinkedHashSet<HostNode>(this.eventArray.length);
        for (SPOEvent event : this.eventArray) {
            event.collectCreatedNodes(hostNodes, result);
        }
        return result;
    }

    @Override
    Set<HostNode> computeErasedNodes() {
        Set<HostNode> result = createNodeSet(this.eventArray.length);
        for (SPOEvent event : this.eventArray) {
            event.collectErasedNodes(result);
        }
        return result;
    }

    public RuleMatch getMatch(HostGraph source) {
        if (false) {
            // the events are ordered according to rule level
            // so we can build a stack of corresponding matches
            Stack<RuleMatch> matchStack = new Stack<RuleMatch>();
            for (SPOEvent event : this.eventArray) {
                RuleMatch match =
                    new RuleMatch(event.getRule(),
                        event.getMatch(source).getElementMap());
                int[] eventLevel = event.getRule().getLevel();
                int eventDepth = eventLevel.length;
                assert eventDepth / 2 <= matchStack.size();
                // pop the stack until the right nesting depth
                while (eventDepth / 2 < matchStack.size()) {
                    matchStack.pop();
                }
                // add this match to the match of the parent event
                // (which is now on the top of the stack)
                if (eventDepth > 0) {
                    RuleMatch parentMatch = matchStack.peek();
                    assert eventDepth <= 2
                        || parentMatch.getRule().getLevel()[eventDepth - 3] == eventLevel[eventDepth - 3];
                    parentMatch.addSubMatch(match);
                }
                // add this match to the stack, to receive its sub-matches
                matchStack.push(match);
            }
            return matchStack.get(0);
        } else {
            for (RuleMatch result : getRule().getMatches(source, null)) {
                if (result.newEvent(null).equals(this)) {
                    return result;
                }
            }
            // if we're here, we failed to reconstruct this event from
            // any of the matches.
            throw new IllegalArgumentException(String.format(
                "Can't find match for event %s", this));
        }
    }

    public MergeMap getMergeMap() {
        MergeMap result = new MergeMap();
        for (RuleEvent event : this.eventArray) {
            for (Map.Entry<HostNode,? extends HostNode> mergeEntry : event.getMergeMap().nodeMap().entrySet()) {
                result.putNode(mergeEntry.getKey(), mergeEntry.getValue());
            }
        }
        return result;
    }

    public Set<HostEdge> getSimpleCreatedEdges() {
        Set<HostEdge> result = createEdgeSet(this.eventArray.length * 2);
        for (SPOEvent event : this.eventArray) {
            event.collectSimpleCreatedEdges(getErasedNodes(), result);
        }
        return result;
    }

    public Collection<HostEdge> getComplexCreatedEdges(
            Iterator<HostNode> createdNodes) {
        Set<HostEdge> result = createEdgeSet(this.eventArray.length * 2);
        Map<RuleNode,HostNode> coRootImages = new HashMap<RuleNode,HostNode>();
        for (SPOEvent event : this.eventArray) {
            event.collectComplexCreatedEdges(getErasedNodes(), createdNodes,
                coRootImages, result);
        }
        return result;
    }

    public Set<HostEdge> getSimpleErasedEdges() {
        Set<HostEdge> result = createEdgeSet(this.eventArray.length * 2);
        for (SPOEvent event : this.eventArray) {
            event.collectSimpleErasedEdges(result);
        }
        return result;
    }

    /**
     * This method always returns <code>false</code> because it is quite hard to
     * check universally matched sub-events against a new graph, especially
     * since the universal information was lost in the conversion from rule
     * match to rule event.
     */
    public boolean hasMatch(HostGraph source) {
        return false;
    }

    /**
     * Checks if the sub-events have matches in the given graph. This is
     * <code>not</code> sufficient to make sure that the event as a whole
     * matches! TODO has to be adapted now that the anchor no longer includes
     * the root map images
     */
    boolean hasSubMatches(HostGraph source) {
        for (RuleEvent event : this.eventArray) {
            // the isGround test is necessary as long as we are not able to
            // include the
            // match of a parent event to provide images for the roots
            if (event.getRule().isGround() && !event.hasMatch(source)) {
                return false;
            }
        }
        return true;
    }

    public int compareTo(RuleEvent other) {
        int result = getRule().compareTo(other.getRule());
        if (result == 0) {
            // the same rule, so the other is also a composite event
            SPOEvent[] myEventArray = this.eventArray;
            SPOEvent[] otherEventArray;
            if (other instanceof CompositeEvent) {
                otherEventArray = ((CompositeEvent) other).eventArray;
            } else {
                otherEventArray = new SPOEvent[] {(SPOEvent) other};
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

    @Override
    int computeEventHashCode() {
        return Arrays.hashCode(this.eventArray);
    }

    /**
     * Two composite events are equal if they contain the same primitive events.
     */
    @Override
    boolean equalsEvent(RuleEvent obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CompositeEvent)) {
            return false;
        }
        return Arrays.equals(this.eventArray, ((CompositeEvent) obj).eventArray);
    }

    @Override
    public String toString() {
        return this.eventArray[0].toString();
    }

    @Override
    public Node[] getArguments(Node[] addedNodes) {
        return this.eventArray[0].getArguments(addedNodes);
    }

    @Override
    protected CompositeEventCache createCache() {
        return new CompositeEventCache();
    }

    /** Also clears the caches of the constituent events. */
    @Override
    public void clearCache() {
        super.clearCache();
        for (SPOEvent event : this.eventArray) {
            event.clearCache();
        }
    }

    /** The (non-empty) array of sub-events constituting this event. */
    final SPOEvent[] eventArray;
    /** Cache reference instance for initialisation. */
    static private final CacheReference<CompositeEventCache> reference =
        CacheReference.<CompositeEventCache>newInstance(false);

    class CompositeEventCache extends
            AbstractEvent<Rule,CompositeEventCache>.AbstractEventCache {
        /**
         * Reconstructs a set of events from the array stored in the composite
         * event.
         */
        SortedSet<SPOEvent> getEventSet() {
            if (this.eventSet == null) {
                this.eventSet =
                    new TreeSet<SPOEvent>(
                        Arrays.asList(CompositeEvent.this.eventArray));
            }
            return this.eventSet;
        }

        private SortedSet<SPOEvent> eventSet;
    }
}
