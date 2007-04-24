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
package groove.lts;

import groove.graph.AbstractGraph;
import groove.graph.DeltaTarget;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;

/**
 * 
 * @author Arend
 * @version $Revision: 1.1 $
 */
public class DefaultGraphNextState extends AbstractGraphState implements GraphNextState, GraphTransitionStub {
    /**
	 * Constructs a state on the basis of a given source and application.
	 */
	public DefaultGraphNextState(GraphState source, RuleEvent event, Node[] coAnchorImage) {
		this.source = source;
		this.event = event;
		this.addedNodes = coAnchorImage;
	}
	
	/**
	 * Return the rule of the incoming transition with which this state
	 * was created.
	 */
	public Rule getRule() {
		return getEvent().getRule(); 
	}
	
	public RuleEvent getEvent() {
		return event;
	}
	
	/**
	 * This implementation reconstructs the matching using the
	 * rule, the anchor images, and the basis graph.
	 * @see #getRule()
	 * @see RuleEvent#getMatching(Graph)
	 */
	public Morphism matching() {
    	return getEvent().getMatching(source().getGraph());
	}
    
    /**
     * Constructs an underlying morphism for the transition from the stored footprint.
     */
    public Morphism morphism() {
        RuleApplication appl = getEvent().newApplication(source().getGraph());
        Graph derivedTarget = appl.getTarget();
        Graph realTarget = target().getGraph();
        if (derivedTarget.edgeSet().equals(realTarget.edgeSet())
                && derivedTarget.nodeSet().equals(realTarget.nodeSet())) {
            return appl.getMorphism();
        } else {
            Morphism iso = derivedTarget.getIsomorphismTo(realTarget);
            assert iso != null : "Can't reconstruct derivation from graph transition " + this
                    + ": \n" + AbstractGraph.toString(derivedTarget) + " and \n"
                    + AbstractGraph.toString(realTarget) + " \nnot isomorphic";
            return appl.getMorphism().then(iso);
        }
    }

	/**
	 * This implementation returns the rule name.
	 */
	public Label label() {
        return getEvent().getLabel();
	}

	/**
	 * This implementation returns <code>this</code>.
	 */
	public GraphState target() {
		return this;
	}

	/**
	 * Returns <code>getBasis()</code> or <code>this</code>, depending on the index.
	 */
	public Node end(int i) {
		switch (i) {
		case SOURCE_INDEX : return source();
		case TARGET_INDEX : return target();
		default : throw new IllegalArgumentException("End index "+i+" not valid");
		}
	}

	/**
	 * @return {@link #END_COUNT}.
	 */
	public int endCount() {
		return END_COUNT;
	}

	public int endIndex(Node node) {
		if (source().equals(node)) {
			return SOURCE_INDEX;
		} else if (target().equals(node)) {
			return TARGET_INDEX;
		} else {
			throw new IllegalArgumentException("Node "+node+" is not an end state of this transition");
		}
	}

	public Node[] ends() {
		return new Node[] { source(), target() };
	}

	public boolean hasEnd(Node node) {
		return source().equals(node) || target().equals(node);
	}

	public Node opposite() {
		return target();
	}

	/**
	 * Returns the basis graph of the delta graph (which is guaranteed to be 
	 * a {@link GraphState}).
	 */
	public GraphState source() {
		return source;
	}
	
    /**
     * This implementation retrieves the coanchor image from the delta array.
     */
    public Node[] getCoanchorImage() {
    	return addedNodes;
    }
    
	public RuleEvent getEvent(GraphState source) {
		if (source == source()) {
			return getEvent();
		} else {
			return getSourceEvent();
		}
	}

	public boolean isSymmetry() {
		return false;
	}

	public GraphTransitionStub toStub() {
		return new IdentityTransitionStub(getEvent(), target());
	}

	/**
     * This implementation returns a {@link DefaultGraphTransition} with 
     * {@link #getSourceEvent()} if <code>source</code> does not equal {@link #source()},
     * otherwise it returns <code>this</code>.
	 */
	public GraphTransition toTransition(GraphState source) {
		if (source != source()) {
			return new DefaultGraphTransition(getSourceEvent(), source, this, isSymmetry());
		} else {
			return this;
		}
	}
	
	@Override
	public Graph getGraph() {
		return getCache().getGraph();
	}

	/** Constructs the graph for this state from the stored information. */
	Graph computeGraph() {
		return getEvent().newApplication(source().getGraph()).getTarget();
	}
	
	@Override
	protected void updateClosed() {
		// empty
	}

	/**
	 * Returns the event from the source of this transition,
	 * if that is itself a {@link groove.lts.GraphTransitionStub}.
	 */
	protected RuleEvent getSourceEvent() {
		if (source() instanceof GraphNextState) {
			return ((GraphNextState) source()).getEvent();
		} else {
			return null;
		}
	}
//	
//    /**
//     * This implementation compares the event identities.
//     * Callback method from {@link #equals(Object)}.
//     */
//    protected boolean equalsEvent(GraphTransitionStub other) {
//        return getEvent() == other.getEvent();
//    }
//
//    /**
//     * This implementation compares the source graph identities.
//     * Callback method from {@link #equals(Object)}.
//     */
//    protected boolean equalsSource(GraphTransitionStub other) {
//        return !(other instanceof DefaultGraphNextState) || source() == ((DefaultGraphNextState) other).source();
//    }
//    
    /**
     * This implementation compares the state on the basis of its qualities as
     * a {@link GraphNextState}.
     * That is, two objects are considered equal if they have the same basis,
     * rule and anchor images.
     */
    @Override
    public boolean equals(Object obj) {
    	if (obj == this) {
    		return true;
    	} else {
    		return (obj instanceof GraphNextState) && equalsNextState((GraphNextState) obj);
    	}
    }

	/**
	 * This implementation compares the source and event of another {@link GraphNextState}
	 * to those of this object.
	 */
	protected boolean equalsNextState(GraphNextState other) {
		return source() == other.source() && getEvent() == other.getEvent();
	}
 

    /**
     * This implementation combines the identities of source and event.
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(source()) + System.identityHashCode(getEvent());
    }

    @Override
	public NextState imageFor(NodeEdgeMap elementMap) {
		throw new UnsupportedOperationException();
	}
//
//	/**
//     * This implementation transforms the outgoing transitions from
//     * their raw format to the proper representation as a {@link groove.lts.GraphTransitionStub}
//     * from the current state.
//     */
//    @Override
//    public Iterator<GraphTransitionStub> getOutTransitionIter() {
//		return new TransformIterator<GraphTransitionStub,GraphTransitionStub>(getTransitionStubIter()) {
//			@Override
//			protected GraphTransitionStub toOuter(GraphTransitionStub trans) {
//				if (trans instanceof DefaultGraphNextState) {
//					DefaultGraphNextState derivedOut = (DefaultGraphNextState) trans;
//					if (derivedOut.source() != DefaultGraphNextState.this) {
//						return createOutTransitionFromThis(derivedOut.getSourceEvent(), derivedOut);
//					}
////					return ((DerivedGraphState) inner).createOutTransitionToThis(DerivedGraphState.this);
//				}
//				return trans;
//			}
//		};
//	}
//    
//    /**
//     * Creates an outgoing transition starting in a given source state,
//     * based on the transformation information of this state.
//     */
//    protected GraphOutTransition createOutTransitionTo(DefaultNextGraphState source) {
//    	if (source != source()) {
//    		return createOutTransitionToThis(getSourceEvent());
//    	} else {
//    		return this;
//    	}
//    }
    
    /**
	 * This implementation returns <code>this</code> if the derivation's event
	 * is identical to the event stored in this state.
	 * Otherwise it invokes <code>super</code>.
	 */
    @Override
	protected GraphTransitionStub createInTransitionStub(GraphState source, RuleEvent event) {
	    if (source == source() && event == getEvent()) {
	        return this;
	    } else if (source != source() && event == getSourceEvent()) {
			return this;
		} else {
			return super.createInTransitionStub(source, event);
		}
	}
//    
//    protected GraphTransitionStub createOutTransitionFromThis(RuleEvent event, GraphState target) {
//    	return new DefaultGraphTransitionStub(event, target);
//    }
//
//	/**
//	 * This implementation takes into account that an outgoing transition may
//	 * actually be an alias to some other transition that forms the outer end of
//	 * a confluent diamond with this one.
//	 */
//    @Override
//	protected RuleEvent getEvent(GraphOutTransition trans) {
//		if (trans instanceof DefaultGraphNextState && ((DefaultGraphNextState)trans).source() != this) {
//			return ((DefaultGraphNextState) trans).getSourceEvent();
//		} else {
//			return super.getEvent(trans);
//		}
//	}

	/**
	 * Applies the underlying rule of this derived state to a given target.
	 */
	protected void applyRule(DeltaTarget target) {
		// if the basis graph cache is cleared before rule application, 
		// clear it again afterwards
		boolean sourceCacheCleared = source instanceof AbstractGraphState && ((AbstractGraphState) source).isCacheCleared();
		// do the actual rule application
		RuleApplication applier = getEvent().newApplication(source.getGraph());
		applier.setCoanchorImage(getCoanchorImage());
	    applier.applyDelta(target);
	    // clear the basis cache
	    if (sourceCacheCleared) {
	    	((AbstractGraphState) source).clearCache();
	    }
	}
	
	/**
	 * The rule of the incoming transition with which this state was created.
	 */
	private final GraphState source;
	/**
	 * The rule event of the incoming transition with which this state was created.
	 */
	private final RuleEvent event;
	/**
	 * The identities of the nodes added with respect to the source state.
	 */
	private final Node[] addedNodes;
}