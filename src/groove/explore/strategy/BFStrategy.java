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
 * $Id$
 */
package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchSetCollector;
import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.AbstractGraphState;
import groove.lts.DefaultGraphNextState;
import groove.lts.DefaultGraphTransition;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTSAdapter;
import groove.lts.StateGenerator;
import groove.trans.RuleEvent;
import groove.trans.VirtualEvent;
import groove.util.Pair;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/** A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BFStrategy extends AbstractStrategy {
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(exploreListener);
			return false;
		}
		ExploreCache cache = getCache(false, false);
		Collection<RuleEvent> matchSet = new MatchSetCollector(getAtState(), cache, getRecord(), parentTransitions).getMatchMap();
		Iterator<RuleEvent> matchIter = matchSet.iterator();
		Collection<VirtualEvent.GraphState> outTransitions = new ArrayList<VirtualEvent.GraphState>(matchSet.size());
		while (matchIter.hasNext()) {
			GraphTransition trans = addTransition(getAtState(), matchIter.next());
			outTransitions.add(new VirtualEvent.GraphState(trans));
		}
		for (GraphState newState: newStates) {
			stateQueue.offer(new Pair<GraphState,Collection<VirtualEvent.GraphState>>(newState, outTransitions));	
		}
		setClosed(getAtState());
		newStates.clear();
		updateAtState();
		return true;
	}

	@Override
	public void updateAtState() {
		Pair<GraphState,Collection<VirtualEvent.GraphState>> next = stateQueue.poll();
		this.atState = next == null ? null : next.first();
		this.parentTransitions = next == null ? null : next.second();
	}
	
	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		getGTS().addGraphListener(exploreListener);
	}	
//	
//	/**
//	 * Applies a match and returns the resulting complete set of graph transitions.
//	 */
//	public Set<? extends GraphTransition> applyMatch(GraphState source, RuleEvent event, ExploreCache cache) {
//		RuleApplication appl;
//		if (event instanceof VirtualRuleEvent) {
//		    VirtualRuleEvent<GraphTransitionStub> virtualEvent = (VirtualRuleEvent) event;
//		    appl = new DefaultAliasApplication(virtualEvent.getWrappedEvent(), (GraphNextState) source, virtualEvent.getContent());
//		} else {
//            appl = event.newApplication(source.getGraph());
//		}
//		return this.addTransition(source, appl, cache);
//	}

    private GraphTransition addTransition(GraphState source, RuleEvent event) {
        reporter.start(ADD_TRANSITION);
        GraphTransition transition = null;
        if (!event.getRule().isModifying() ) {
        	transition = createTransition(event, source, source, false);
        } else if (event instanceof VirtualEvent) {
	        assert source instanceof GraphNextState;
		    VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
	        GraphState target = virtual.getConfluentTarget(((GraphNextState) source).getEvent());
	        if (target != null) {
	        	transition = createTransition(event, source, target, false);
	        	confluentDiamondCount++;
	        }
        }
        if (transition == null) {
        	GraphNextState freshTarget = createState(event, source);
        	reporter.start(ADD_STATE);
        	GraphState isoTarget = getGTS().addState(freshTarget);
        	reporter.stop();
        	if (isoTarget == null) {
        		transition = freshTarget;
        	} else {
        		transition = createTransition(event, source, isoTarget, true);
        	}
        }
        // add transition to gts
        getGTS().addTransition(transition);
        reporter.stop();
        return transition;
    }
    
    /**
	 * Creates a fresh graph state, based on a given rule application and source state.
	 */
	private GraphNextState createState(RuleEvent event, GraphState source) {
		Node[] addedNodes;
		if (event instanceof VirtualEvent.GraphState) {
			VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
			event = virtual.getInnerEvent();
			addedNodes = getCreatedNodes(virtual, source);
		} else {
			addedNodes = getCreatedNodes(event, source.getGraph());
		}
		return new DefaultGraphNextState((AbstractGraphState) source, event, addedNodes, null);
	}


    /**
	 * Creates a fresh graph transition, based on a given rule event and source and target state.
	 * A final parameter determines if the target state is directly derived from the source, or modulo a symmetry.
	 */
	private GraphTransition createTransition(RuleEvent event, GraphState source, GraphState target, boolean symmetry) {
		Node[] addedNodes;
		if (event instanceof VirtualEvent.GraphState) {
			VirtualEvent.GraphState virtual = (VirtualEvent.GraphState) event;
			event = virtual.getInnerEvent();
			addedNodes = getCreatedNodes(virtual, source);
		} else {
			addedNodes = getCreatedNodes(event, source.getGraph());
		}
	    return new DefaultGraphTransition(event, addedNodes, source, target, symmetry);
	}

	/** 
	 * Returns the array of nodes created when applying a 
	 * given virtual event to a given source state.
	 * @return the inner added nodes of the virtual event, unless this event
	 * coincides with the source state event; otherwise, the added nodes are computed
	 * from the event.
	 */
	private Node[] getCreatedNodes(VirtualEvent.GraphState event, GraphState source) {
		Node[] result;
		result = event.getInnerAddedNodes();
		// if this application's event is the same as that of the source,
		// test if the added nodes coincide
		if (result.length > 0
				&& ((GraphNextState) source).getEvent() == event.getInnerEvent()) {
			Graph host = source.getGraph();
			Node[] sourceAddedNodes = ((GraphNextState) source).getAddedNodes();
			boolean conflict = false;
			for (int i = 0; !conflict && i < result.length; i++) {
				conflict = result[i] == sourceAddedNodes[i];
				assert conflict || !host.containsElement(result[i]);
			}
			if (conflict) {
				// the nodes coincide, so delegate the method
				result = getCreatedNodes(event, host);
			}
		}
		return result;
	}

	/** Computes the nodes created by applying a given event to a given graph. */
	private Node[] getCreatedNodes(RuleEvent event, Graph graph) {
		return event.getCreatedNodes(graph.nodeSet()).toArray(EMPTY_NODE_ARRAY);
	}

	/** 
	 * Queue of states to be explored.
	 * The set of outgoing transitions of the parent state is included with each state.
	 */
	private final Queue<Pair<GraphState,Collection<VirtualEvent.GraphState>>> stateQueue = new LinkedList<Pair<GraphState,Collection<VirtualEvent.GraphState>>>();
	/** Internal store of newly generated states. */
	private Collection<GraphState> newStates = new ArrayList<GraphState>();
	/** Parent transitions of the currently explored state. */
	private Collection<VirtualEvent.GraphState> parentTransitions;
	/** Listener to keep track of states added to the GTS. */
	private ExploreListener exploreListener = new ExploreListener();

	/**
	 * Returns the number of confluent diamonds found during generation.
	 */
	public static int getConfluentDiamondCount() {
	    return confluentDiamondCount;
	}

	/**
	 * Returns the time spent generating successors.
	 */
	public static long getGenerateTime() {
	    return reporter.getTotalTime(ADD_TRANSITION);
	}

	private static int confluentDiamondCount;

	/** 
	 * Constant empty node array, to be shared among rule applications
	 * that create no nodes. 
	 */
	private static final Node[] EMPTY_NODE_ARRAY = new Node[0];
	
	/** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static private final Reporter reporter = Reporter.register(StateGenerator.class);
    /** Profiling aid for adding states. */
    static public final int ADD_STATE = reporter.newMethod("addState");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION = reporter.newMethod("addTransition");

	/** A queue with states to be explored, used as a FIFO. */
	protected class ExploreListener extends LTSAdapter {
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			newStates.add((GraphState) node);
		}
	}
}
