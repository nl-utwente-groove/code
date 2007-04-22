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
 * $Id: GTS.java,v 1.10 2007-04-22 23:32:14 rensink Exp $
 */
package groove.lts;

import groove.graph.AbstractGraphShape;
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.trans.GraphGrammar;
import groove.util.FilterIterator;
import groove.util.NestedIterator;
import groove.util.SetView;
import groove.util.TransformIterator;
import groove.util.TreeHashSet;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements an LTS of which the states are {@link GraphState}s 
 * and the transitions {@link GraphTransition}s.
 * A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision: 1.10 $ $Date: 2007-04-22 23:32:14 $
 */
public class GTS extends AbstractGraphShape<GraphShapeCache> implements LTS {
	/**
	 * Tree resolution of the state set (which is a {@link TreeHashSet}).
	 * A smaller value means memory savings; a larger value means speedup.
	 */
    private final static int STATE_SET_RESOLUTION = 2;
	/**
	 * Tree root resolution of the state set (which is a {@link TreeHashSet}).
	 * A larger number means speedup, but
	 * the memory initially reserved for the set grows exponentially with this number.
	 */
    private final static int STATE_SET_ROOT_RESOLUTION = 10;
    /**
     * Number of states for which the state set should have room initially.
     */
    private final static int INITIAL_STATE_SET_SIZE = 10000;
    
    /**
     * The number of transitions generated but not added (due to overlapping existing transitions)
     */
    private static int spuriousTransitionCount;
    
    /**
     * Returns the number of confluent diamonds found during generation.
     */
    public static int getSpuriousTransitionCount() {
        return spuriousTransitionCount;
    }
    
    /**
     * Returns an estimate of the number of bytes used to store each state.
     */
    public double getBytesPerState() {
    	return stateSet.getBytesPerElement();
    }
    
    /**
     * Constructs a GTS from a (fixed) graph grammar.
     * @ensure <tt>startState().isEmpty()</tt> and
     * <tt>nodeSet().contains(startState())</tt> and
     * <tt>getExploreStarategy() instanceof FullStrategy</tt>
     */
    public GTS(GraphGrammar grammar) {
        this(grammar, true);
    }

    /**
     * Constructs a GTS with an option to avoid storing transitions.
     * @require startGraph != null
     * @ensure startState().equals(startGraph)</tt> and
     * <tt>nodeSet().contains(startState())</tt> 
     */
    private GTS(GraphGrammar grammar, boolean storeTransitions) {
    	grammar.testFixed(true);
        this.ruleSystem = grammar;
        this.storeTransitions = storeTransitions;
        this.startState = computeStartState(grammar.getStartGraph());
        addState(startState);
//        this.strategy = new FullStrategy();
//        this.strategy.setLTS(this);
    }

    /**
     * Callback factory method to create and initialise the start graph of the GTS,
     * on the basis of a given graph.
     * Creation is done using {@link #createStartState(Graph)}.
     */
    protected GraphState computeStartState(Graph startGraph) {
        GraphState result = createStartState(startGraph);
        result.getGraph().setFixed();
        return result;
    }

    /**
     * Callback factory method to create the start graph of the GTS,
     * on the basis of a given graph.
     */
    protected GraphState createStartState(Graph startGraph) {
        return new DefaultGraphState(startGraph);
    }

    /** This implementation specialises the return type to {@link GraphState}. */
    public GraphState startState() {
        return startState;
    }
//
//    /** @deprecated */
//    @Deprecated
//    public Collection<? extends GraphState> nextStates(State state) {
//        freshNextStates(state);
//        return ((GraphState) state).getNextStateSet();
//    }
//    
//    /** @deprecated */
//    @Deprecated
//    public Iterator<? extends GraphState> nextStateIter(final State state) {
//        if (state.isClosed()) {
//            // get the next states from the outgoing edges
//            return ((GraphState) state).getNextStateIter();
//        } else {
//            final Iterator<RuleApplication> derivationIter = getDeriver().getDerivationIter(((GraphState) state).getGraph());
//            if (!derivationIter.hasNext()) {
//                finalStates.add((GraphState) state);
//            }
//            // get the next states by adding transitions for the derivations
//            return new TransformIterator<RuleApplication,GraphState>(derivationIter) {
//            	@Override
//                public boolean hasNext() {
//                    if (hasNext) {
//                        hasNext = super.hasNext();
//                        if (!hasNext) {
//                            setClosed(state);
//                        }
//                    }
//                    return hasNext;
//                }
//
//            	@Override
//                protected GraphState toOuter(RuleApplication from) {
//                    return addTransition(from);
//                }
//                
//                private boolean hasNext = true;
//            };
//        }
//    }
//    
    
    /**
     * Returns the rule system underlying this GTS.
     */
    public GraphGrammar ruleSystem() {
        return ruleSystem;
    }

    public Collection<? extends GraphState> getFinalStates() {
        return finalStates;
    }

    public boolean hasFinalStates() {
        return !getFinalStates().isEmpty();
    }

    public boolean isFinal(State state) {
        return getFinalStates().contains(state);
    }

    /** Adds a given state to the final states of theis GTS. */
    public void setFinal(State state) {
        finalStates.add((GraphState) state);
    }

    public boolean isOpen(State state) {
        return !state.isClosed();
    }

    /**
     * Indicates if the GTS currently has open states.
     * Equivalent to (but more efficient than) <code>getOpenStateIter().hasNext()</code>
     * or <code>!getOpenStates().isEmpty()</code>.
     * @return <code>true</code> if the GTS currently has open states
     * @see #getOpenStateIter()
     * @see #getOpenStates()
     */
    public boolean hasOpenStates() {
        return openStateCount() > 0;
    }
    
    /**
     * Returns a view on the set of currently open states.
     * @see #hasOpenStates()
     * @see #getOpenStateIter()
     */
    public Collection<GraphState> getOpenStates() {
        return new SetView<GraphState>(stateSet) {
        	@Override
            public boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    /**
     * Returns an iterator over the set of currently open states.
     * Equivalent to <code>getOpenStates().iterator()</code>.
     * @see #hasOpenStates()
     * @see #getOpenStates()
     */
    public Iterator<GraphState> getOpenStateIter() {
        return new FilterIterator<GraphState>(nodeSet().iterator()) {
        	@Override
            protected boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    /**
     * Removes a state from the set of open states, and notifies the graph listeners.
     * Only call this after all outgoing transitions of the state have been generated!
     * @param state the state to be removed from the set of open states
     * @require <tt>state instanceof GraphState</tt>
     */
    public void setClosed(State state) {
        GraphState graphState = (GraphState) state;
        if (graphState.setClosed()) {
            notifyLTSListenersOfClose(state);
            closedCount++;
        }
    }
//    
//    /** @deprecated */
//    @Deprecated
//    public synchronized Collection<? extends GraphState> freshNextStates(State state) {
//        reporter.start(CLOSE);
//        final Collection<GraphState> result = new ArrayList<GraphState>();
//        // check if the transitions have not yet been generated
//        if (!state.isClosed()) {
//            Set<RuleApplication> derivations = getDeriver().getDerivations(((GraphState) state).getGraph());
//            // if there are no rule applications, the state is final
//            if (derivations.isEmpty()) {
//                finalStates.add((GraphState) state);
//            } else {
//                Iterator<RuleApplication> derivationIter = derivations.iterator();
//                do {
//                    RuleApplication appl = derivationIter.next();
//                    // to test if the eventual target is fresh, compare it 
//                    // with the original derivation's target
//                    GraphState realTarget = addTransition(appl);
//                    if (appl.isTargetSet() && appl.getTarget() == realTarget && realTarget != state) {
//                    	result.add(realTarget);
//                    }
//                } while (derivationIter.hasNext());
//            }
//            setClosed(state);
//        }
//        reporter.stop();
//        return result;
//    }
//    
    /** @deprecated */
    @Deprecated
    public synchronized void explore(State atState) throws InterruptedException {
//        strategy.setAtState(atState);
//        strategy.explore();
    }

    /** @deprecated */
    @Deprecated
    public synchronized void explore() throws InterruptedException {
//        strategy.setAtState(startState());
//        strategy.explore();
    }

    /** Returns the number of not fully expored states. */
    public int openStateCount() {
        return nodeCount() - closedCount;
    }

    /** @deprecated */
    @Deprecated
    public ExploreStrategy getExploreStrategy() {
    	return null;
//        return strategy;
    }

    /** @deprecated */
    @Deprecated
    public void setExploreStrategy(ExploreStrategy strategy) {
//    	if (this.strategy instanceof GraphListener) {
//    		removeGraphListener((GraphListener) this.strategy);
//    	}
//        this.strategy = strategy;
//        strategy.setLTS(this);
    }

	@Override
    public int nodeCount() {
        return stateSet.size();
    }

	@Override
    public int edgeCount() {
        return transitionCount;
    }

	/**
	 * This implementation calls {@link GraphState#getTransitionSet()} on <tt>node</tt>.
	 * @require <tt>node instanceof GraphState</tt>
	 */
	@Override
	public Set<GraphTransition> outEdgeSet(Node node) {
		return ((GraphState) node).getTransitionSet();
	}
	
    // ----------------------- OBJECT OVERRIDES ------------------------

    public Set<? extends GraphState> nodeSet() {
        return Collections.unmodifiableSet(stateSet);
    }

    public Set<? extends GraphTransition> edgeSet() {
        if (isStoreTransitions()) {
            return new TransitionSet();
        } else {
            return Collections.emptySet();
        }
    }

	@Override
    protected GraphShapeCache createCache() {
        return new GraphShapeCache(this, false);
    }
//    
//    /**
//	 * Returns the (fixed) derivation record for this GTS.
//	 */
//	public final SystemRecord getDerivationData() {
//		if (record == null) {
//			record = createDerivationData();
//		}
//		return record;
//	}
//
//	/** Callback method to create a derivation data store for this GTS. */
//	protected SystemRecord createDerivationData() {
//		return new SystemRecord(ruleSystem());
//	}
//	
//	/**
//     * Factory method for a graph deriver from a given rule system.
//     * Use to initialize this GTS's deriver if it is not set explicitly.
//     * @param ruleSystem the rule system to create the deriver for
//     * @deprecated
//     */
//    @Deprecated
//    protected Deriver createDeriver(GraphGrammar ruleSystem) {
////    	return new DefaultDeriver(ruleSystem.getRules());
//    	return new NextStateDeriver(getDerivationData());
//    }
//    
//    /**
//     * Returns the deriver.
//     * Lazily creates the deriver (using {@link #createDeriver(GraphGrammar)}).
//     * @return a deriver for the current rule system; never <code>null</code>
//     * @deprecated
//     */
//    @Deprecated
//    protected Deriver getDeriver() {
//    	if (deriver == null) {
//    		deriver = createDeriver(ruleSystem);
//    	}
//    	return deriver;
//    }
//    
//    /**
//     * Sets the deriver for this GTS.
//     * Only legal if no deriver has been set, explicitly or implicitle.
//     * @param deriver the deriver to be set; should not be <code>null</code>
//     * @throws IllegalStateException if the deriver has already been set at the time of invocation
//     */
//    protected void setDeriver(Deriver deriver) {
//    	if (this.deriver != null) {
//    		throw new IllegalStateException("Graph deriver set twice");
//    	}
//    	this.deriver = deriver;
//    }
//    
//    /**
//     * Adds a transition to the GTS, from a given source state and with a
//     * given underlying derivation.
//     * The derivation's target graph is compared to the existing states for isomorphism;
//     * if an isomorphic one is found then that is taken as target state, and
//     * the derivation is adjusted accordingly. If no isomorphic state is found,
//     * then a fresh target state is added.
//     * The actual target state is returned as the result of the method.
//     * @param appl the derivation underlying the transition to be added
//     * @return the target state of the resulting transition
//     * @require <tt>devon.dom() == state.getGraph()</tt>
//     * @ensure <tt>state.containsOutTransition(new GraphTransition(devon.rule(), devon.match(), result))</tt>
//     * @deprecated
//     */
//    @Deprecated
//    public GraphState addTransition(RuleApplication appl) {
////        reporter.start(ADD_TRANSITION);
////        reporter.start(ADD_TRANSITION_START);
//        GraphState sourceState = (GraphState) appl.getSource();
//        // check for confluent diamond
//        GraphState targetState = getConfluentTarget(appl);
////        reporter.stop();
//        if (targetState == null) {
//            // determine target state of this transition
//            targetState = (GraphState) appl.getTarget();
//            // see if isomorphic graph is already in the LTS
//            // special case: source = target
//            if (sourceState != targetState) {
//                GraphState isoState = addState(targetState);
//                if (isoState != null) {
//                    // the following line is to ensure the cache is cleared
//                    // even if the state is still used as the basis of another
//                    targetState.dispose();
//                    targetState = isoState;
//                }
//            }
//        }
//        addTransition(sourceState, appl.getEvent(), targetState);
////        reporter.stop();
//        return targetState;
//    }

	/**
	 * Adds a transition to the GTS, under the assumption that the source
	 * and target states are already present.
	 * @param transition the source state of the transition to be added
	 */
	public void addTransition(GraphTransition transition) {
		if (isStoreTransitions()) {
            reporter.start(ADD_TRANSITION_STOP);
            // add (possibly isomorphically modified) edge to LTS
            if (transition.source().addTransition(transition)) {
                transitionCount++;
                fireAddEdge(transition);
            } else {
                spuriousTransitionCount++;
            }
            reporter.stop();
        }
	}
//
//    /**
//     * Returns the target of a given rule application, by trying to walk 
//     * around three sides of a confluent diamond instead of computing the
//     * target directly.
//     */
//	@Deprecated
//    protected GraphState getConfluentTarget(RuleApplication appl) {
//        if (!NextStateDeriver.isUseDependencies() || !(appl instanceof AliasRuleApplication)) {
//            return null;
//        }
//        GraphOutTransition prior = ((AliasRuleApplication) appl).getPrior();
//        if (prior == null) {
//            return null;
//        }
//        GraphState priorTarget = prior.target();
//        if (!priorTarget.isClosed()) {
//            return null;
//        }
//        GraphTransition prevTransition = (DerivedGraphState) appl.getSource();
//        GraphState result = priorTarget.getNextState(prevTransition.getEvent());
//        return result;
//    }

    /**
     * Adds a state to the GTS, if it is not isomorphic to an existing state.
     * Returns the isomorphic state if one was found, or <tt>null</tt> if the state was actually added.
     * @param newState the state to be added
     * @return a state isomorphic to <tt>state</tt>;
     * or <tt>null</tt> if there was no existing isomorphic state (in which  case, and only then,
     * <tt>state</tt> was added and the listeners notified).
     */
    public GraphState addState(GraphState newState) {
        reporter.start(ADD_STATE);
        // see if isomorphic graph is already in the LTS
        ((DefaultGraphState) newState).setStateNumber(nodeCount());
        GraphState result = (GraphState) stateSet.put(newState);
        if (result == null) {
            fireAddNode(newState);
        }
        reporter.stop();
        return result;
    }

    /**
	 * Indicates if transitions are to be stored in the GTS.
	 * This is a property set at construction time.
	 * If they are not stored, the transition set will yield the empty set.
	 */
	protected final boolean isStoreTransitions() {
		return this.storeTransitions;
	}

	/**
     * Iterates over the graph listeners and notifies those which
     * are also LTS listeners of the fact that a state has been closed.
     */
    protected void notifyLTSListenersOfClose(State closed) {
        Iterator<GraphShapeListener> listenerIter = getGraphListeners();
        while (listenerIter.hasNext()) {
        	GraphShapeListener listener = listenerIter.next();
            if (listener instanceof LTSListener) {
                ((LTSListener) listener).closeUpdate(this, closed);
            }
        }
    }

    /**
     * Returns the number of times an isomorphism was suspected on the basis
     * of the "early warning system", viz. the graph certificate.
     */
    static public int getIntCertOverlap() {
        return intCertOverlap;
    }
    
    /**
     * The start state of this LTS.
     * @invariant <tt>nodeSet().contains(startState)</tt>
     */
    protected final GraphState startState;
    
    /**
     * The rule system generating this LTS.
     * @invariant <tt>ruleSystem != null</tt>
     */
    protected final GraphGrammar ruleSystem;
    /** The set of states of the GTS. */
    protected final TreeHashSet<GraphState> stateSet = new TreeHashStateSet();
    
    /**
     * Set of states that have not yet been extended.
     * @invariant <tt>freshStates \subseteq nodes</tt>
     */
    protected final Set<GraphState> finalStates = new HashSet<GraphState>();
//    /**
//     * The current graph deriver.
//     */
//    private Deriver deriver;
//    /** The derivation record for this GTS. */
//    private SystemRecord record;
    /**
     * The number of closed states in the GTS.
     */
    private int closedCount = 0;
    /**
     * The number of transitions in the GTS.
     */
    private int transitionCount = 0;
    /** Flag to indicate whether transitions are to be stored in the GTS. */
    private final boolean storeTransitions;
    /**
     * The number of isomorphism warnings given while exploring the GTS.
     */
    static private int intCertOverlap = 0; 
    
    /** Specialised set implementation for storing states. */
    private class TreeHashStateSet extends TreeHashSet<GraphState> {
    	/** Constructs a new, empty state set. */
        TreeHashStateSet() {
            super(STATE_SET_RESOLUTION, STATE_SET_ROOT_RESOLUTION, INITIAL_STATE_SET_SIZE);
        }
        
        /**
         * Calls {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         */
    	@Override
        protected boolean areEqual(Object key, Object otherKey) {
            Graph one = (Graph) key;
            Graph two = (Graph) otherKey;
            if (!one.getCertificate().equals(two.getCertificate())) {
            	intCertOverlap++;
                return false;
            } else {
                return checker.areIsomorphic(one, two);
            }
        }

        /**
         * Returns the hash code of the isomorphism certificate.
         */
    	@Override
        protected int getCode(Object key) {
            return ((Graph) key).getCertificate().hashCode();
        }
        
        /** The isomorphism checker of the state set. */
        private IsoChecker checker = new DefaultIsoChecker();
    }
    
    /**
     * An unmodifiable view on the transitions of this GTS.
     * The transitions are (re)constructed from the outgoing
     * transitions as stored in the states.
     */
    private class TransitionSet extends AbstractSet<GraphTransition> {
        /**
         * To determine whether a transition is in the set,
         * we look if the source state is known and if the 
         * transition is registered as outgoing transition with the source state.
         * @require <tt>o instanceof GraphTransition</tt> 
         */
    	@Override
        public boolean contains(Object o) {
        	if (o instanceof GraphTransition) {
        		GraphTransition transition = (GraphTransition) o;
        		GraphState source = transition.source();
        		return (containsElement(source) && source.containsTransition(transition));
        	} else {
        		return false;
        	}
        }

        /**
         * Iterates over the state and for each state over
         * that state's outgoing transitions.
         */
    	@Override
        public Iterator<GraphTransition> iterator() {
            Iterator<Iterator<GraphTransition>> stateOutTransitionIter = new TransformIterator<GraphState,Iterator<GraphTransition>>(nodeSet().iterator()) {
            	@Override
                public Iterator<GraphTransition> toOuter(GraphState state) {
                    return state.getTransitionIter();
                }
            };
            return new NestedIterator<GraphTransition>(stateOutTransitionIter);
        }

    	@Override
        public int size() {
            return transitionCount;
        }
    }
    /** Profiling aid for adding states. */
    static public final int ADD_STATE = reporter.newMethod("addState");
    /** Profiling aid for adding transitions. */
    static public final int ADD_TRANSITION_STOP = reporter.newMethod("addTransition  - stop");
//    /** Profiling aid for closing states. */
//    static private final int CLOSE = reporter.newMethod("close(State)");
}