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
package groove.abstraction.neigh.lts;

import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.grammar.Grammar;
import groove.grammar.host.HostGraph;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.graph.GraphCache;
import groove.lts.ActionLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchApplier;
import groove.lts.RuleTransition;
import groove.transform.RuleEvent.Reuse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The graph transition system for abstract exploration. All states of this
 * GTS are of type ShapeState or ShapeNextState.
 * 
 * @author Eduardo Zambon
 */
public final class AGTS extends GTS {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Number of states marked as subsumed. */
    private int subsumedStatesCount;
    /** Number of transitions marked as subsumed. */
    private int subsumedTransitionsCount;
    /** Flag that indicates if we are only interested in reachability. */
    private final boolean reachability;
    /** Set of states to be removed from the GTS. Used only in reachability mode. */
    private final Collection<ShapeState> toRemove;
    /** Next state number. */
    private int nextStateNr;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs the GTS object for the given grammar. */
    public AGTS(Grammar grammar, boolean reachability) {
        super(grammar);
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
        this.reachability = reachability;
        if (this.reachability) {
            this.toRemove = new MyHashSet<ShapeState>();
        } else {
            this.toRemove = null;
        }
        this.nextStateNr = 0;
        this.getRecord().setReuseEvents(Reuse.NONE);
        this.getRecord().setCheckIso(true);
        this.storeAbsLabels();
    }

    /** Private constructor used to build the reduced state space. */
    private AGTS(AGTS agts) {
        super(agts.getGrammar());
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
        this.reachability = agts.reachability;
        this.toRemove = null;
        this.nextStateNr = 0;
        this.getRecord().setReuseEvents(Reuse.NONE);
        this.getRecord().setCheckIso(false);
        //        this.getRecord().setCollapse(false);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /**
     * Adds the given state to the abstract GTS. 
     * The given state must be of type ShapeState or ShapeNextState.
     * While trying to add the new state, subsumption is computed in both
     * directions. If the state is fresh, this method goes over the subsumed
     * states already stored and tries to update the subsumption relation.
     */
    @Override
    public ShapeState addState(GraphState newGState) {
        assert newGState instanceof ShapeState : "Type error : " + newGState
            + " is not of type ShapeState.";
        if (this.reachability) {
            this.toRemove.clear();
        }
        ShapeState newState = (ShapeState) newGState;
        ShapeState result = (ShapeState) super.addState(newState);
        if (result == null) {
            // There is no state in the transition system that subsumes the
            // new state. Maybe the new state subsumes some states that are
            // already in the GTS.
            this.subsumedStatesCount +=
                newState.markSubsumedStates(this.toRemove);
            if (this.reachability) {
                for (ShapeState stateToRemove : this.toRemove) {
                    getStateSet().remove(stateToRemove);
                    stateToRemove.disconnectState();
                }
            }
            // Adjust the counter for the next state number.
            this.nextStateNr = newState.getNumber() + 1;
        } else if (newState.isSubsumed()) {
            // The state will produce only a transition.
            this.subsumedTransitionsCount++;
        }
        return result;
    }

    /**
     * Delegates to super.
     * The given transition must be of type ShapeTransition or ShapeNextState.
     */
    @Override
    public void addTransition(GraphTransition transition) {
        assert (transition instanceof ShapeTransition)
            || (transition instanceof ShapeNextState) : "Type error : "
            + transition + " is not of type ShapeTransition or ShapeNextState.";
        if (!this.reachability) {
            super.addTransition(transition);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends ShapeState> nodeSet() {
        return (Set<? extends ShapeState>) super.nodeSet();
    }

    @Override
    protected ShapeStateSet createStateSet() {
        return new ShapeStateSet(getCollapse(), isReachability());
    }

    /** Throws an UnsupportedOperationException. */
    @Override
    protected GraphCache<GraphState,RuleTransition> createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Shape createStartGraph(HostGraph startGraph) {
        Shape result = Shape.createShape(startGraph);
        return result;
    }

    @Override
    protected ShapeState createStartState(HostGraph startGraph) {
        ShapeState result =
            new ShapeState(this, (Shape) startGraph,
                getGrammar().getCtrlAut().getStart(), 0);
        return result;
    }

    @Override
    public boolean checkDiamonds() {
        return false;
    }

    @Override
    public ShapeState startState() {
        return (ShapeState) super.startState();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Store the abstraction labels in the parameters, if any. */
    private void storeAbsLabels() {
        TypeGraph typeGraph = getGrammar().getTypeGraph();
        Set<TypeLabel> unaryLabels = new MyHashSet<TypeLabel>();
        for (TypeEdge typeEdge : typeGraph.edgeSet()) {
            if (typeEdge.getRole() != EdgeRole.BINARY) {
                unaryLabels.add(typeEdge.label());
            }
        }
        if (!typeGraph.isImplicit()) {
            // Node types are not stored as edges.
            for (TypeNode typeNode : typeGraph.nodeSet()) {
                if (!typeNode.isTopType()) {
                    unaryLabels.add(typeNode.label());
                }
            }
        }

        List<String> absLabelsStr =
            this.getGrammar().getProperties().getAbstractionLabels();
        Set<TypeLabel> absLabels = new MyHashSet<TypeLabel>();

        for (TypeLabel unaryLabel : unaryLabels) {
            if (absLabelsStr.contains(unaryLabel.text())) {
                absLabels.add(unaryLabel);
            }
        }

        NeighAbsParam.getInstance().setAbsLabels(absLabels);
    }

    /** Returns the number of states marked as subsumed. */
    public int getSubsumedStatesCount() {
        return this.subsumedStatesCount;
    }

    /** Returns the number of transitions marked as subsumed. */
    public int getSubsumedTransitionsCount() {
        return this.subsumedTransitionsCount;
    }

    /** Basic getter method. */
    public int getStateCount() {
        return this.nodeCount();
    }

    /** Basic inspection method. */
    public boolean isReachability() {
        return this.reachability;
    }

    /** Basic inspection method. */
    public int getNextStateNr() {
        return this.nextStateNr;
    }

    /**
     * Constructs and returns the reduced state space with only non-subsumed
     * states.
     */
    public AGTS reduceGTS() {
        AGTS result = new AGTS(this);
        Map<ShapeState,ShapeState> closureMap =
            new MyHashMap<ShapeState,ShapeState>();
        ArrayList<ShapeState> ancestors = new ArrayList<ShapeState>();

        // Go over all states and compute the closure for the subsumption
        // relation.
        for (GraphState graphState : getStateSet()) {
            ShapeState state = (ShapeState) graphState;
            if (!state.isSubsumed() && !closureMap.containsKey(state)) {
                // This state will be in the reduced GTS.
                closureMap.put(state, state);
            } else { // The state is subsumed.
                // Check if we already have a closure.
                ShapeState closure = closureMap.get(state);
                if (closure == null) {
                    // No, we don't.
                    ShapeState subsumptor = state.getSubsumptor();
                    closure = closureMap.get(subsumptor);
                    ancestors.clear();
                    ancestors.add(state);
                    while (closure == null) {
                        // We need to search.
                        if (subsumptor.isSubsumed()) {
                            ancestors.add(subsumptor);
                            subsumptor = subsumptor.getSubsumptor();
                            closure = closureMap.get(subsumptor);
                        } else {
                            // The current subsumptor is the closure.
                            closure = subsumptor;
                            ancestors.add(closure);
                        }
                    }
                    // Now we have a closure, go over all ancestors that we
                    // visited and update the closure map.
                    for (ShapeState ancestor : ancestors) {
                        assert !closureMap.containsKey(ancestor);
                        closureMap.put(ancestor, closure);
                    }
                } // else closure != null: Nothing to do.
            }
        }

        // Add the shapes to the new GTS.
        List<ShapeState> toProcess = new LinkedList<ShapeState>();
        toProcess.add(this.startState());
        Map<ShapeState,ShapeState> stateMap =
            new MyHashMap<ShapeState,ShapeState>();
        while (!toProcess.isEmpty()) {
            ShapeState origSrc = toProcess.remove(0);
            ShapeState origSrcClosure = closureMap.get(origSrc);
            assert origSrcClosure != null;
            ShapeState reducedSrc = stateMap.get(origSrcClosure);
            if (reducedSrc == null) {
                // This can only happen on the first state.
                assert !(origSrc instanceof ShapeNextState);
                reducedSrc =
                    new ShapeState(AGTS.this, origSrcClosure.getGraph(),
                        origSrcClosure.getCtrlState(), 0);
                addReducedState(result, origSrcClosure, reducedSrc);
                stateMap.put(origSrcClosure, reducedSrc);
                result.startState = reducedSrc;
            }
            // Go over all next states.
            for (RuleTransition origTrans : origSrcClosure.getRuleTransitions()) {
                ShapeState origTgt = (ShapeState) origTrans.target();
                ShapeState origTgtClosure = closureMap.get(origTgt);
                assert origTgtClosure != null;
                ShapeState reducedTgt = stateMap.get(origTgtClosure);
                if (reducedTgt == null) {
                    // The target state doesn't exist yet.
                    reducedTgt =
                        new ShapeNextState(result.nodeCount(),
                            origTgtClosure.getGraph(), reducedSrc,
                            origTrans.getKey());
                    toProcess.add(origTgtClosure);
                    addReducedState(result, origTgtClosure, reducedTgt);
                    stateMap.put(origTgtClosure, reducedTgt);
                } else {
                    // Check if we have a new transition.
                    ActionLabel transLabel = origTrans.label();
                    // Create a new transition.
                    if (!reducedSrc.containsTransition(transLabel, reducedTgt)) {
                        ShapeTransition reducedTrans =
                            new ShapeTransition(reducedSrc, origTrans.getKey(),
                                reducedTgt);
                        result.addTransition(reducedTrans);
                    }
                }
            }
        }

        return result;
    }

    private void addReducedState(AGTS reducedGTS, ShapeState origState,
            ShapeState reducedState) {
        reducedGTS.addStateWithoutCheck(reducedState);
        if (reducedState instanceof ShapeNextState) {
            reducedGTS.addTransition((RuleTransition) reducedState);
        }
        reducedState.setClosed(true);
        if (this.isFinal(origState)) {
            reducedGTS.setFinal(reducedState);
        }
    }

    private ShapeState addStateWithoutCheck(ShapeState state) {
        return (ShapeState) super.addState(state);
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    @Override
    protected MatchApplier createMatchApplier() {
        return new ShapeMatchApplier(this);
    }

    /** Class to store the states of the GTS. */
    private static final class ShapeStateSet extends GTS.StateSet {

        private final boolean reachability;

        /** Default constructor, delegates to super class. */
        ShapeStateSet(CollapseMode collapse, boolean reachability) {
            super(collapse, ShapeIsoChecker.getInstance(true));
            this.reachability = reachability;
        }

        /**
         * Compares the given states both for (in)equality and subsumption.
         * Bear in mind that this method has side-effects. 
         */
        @Override
        protected boolean areEqual(GraphState myState, GraphState otherState) {
            if (CHECK_CONTROL_LOCATION
                && myState.getCtrlState() != otherState.getCtrlState()) {
                return false;
            }

            assert myState instanceof ShapeState;
            assert otherState instanceof ShapeState;
            ShapeState myShapeState = (ShapeState) myState;
            ShapeState otherShapeState = (ShapeState) otherState;

            if (this.reachability) {
                if (myShapeState.isSubsumed() || otherShapeState.isSubsumed()) {
                    // We are in reachability mode and one state already has a
                    // subsumptor. This means that this method invocation
                    // is in the context of a state removal from the state set.
                    // At this point there is no need to check for iso, object
                    // equality is sufficient.
                    return myShapeState == otherShapeState;
                }
            } else {
                if (otherShapeState.isSubsumed()) {
                    // We are not in reachability mode and the other state is
                    // subsumed. This means we can leave the comparison to the
                    // the subsumptor state.
                    return false;
                }
            }

            // Now let's check for iso...
            ShapeIsoChecker checker = ShapeIsoChecker.getInstance(true);
            int comparison =
                checker.compareShapes(myShapeState.getGraph(),
                    otherShapeState.getGraph()).one();
            if (checker.isDomStrictlyLargerThanCod(comparison)) {
                // New state subsumes old one.
                myShapeState.addSubsumedState(otherShapeState);
            } else if (checker.isCodSubsumesDom(comparison)) {
                // Old state subsumes new state.
                myShapeState.setSubsumptor(otherShapeState);
            }

            return checker.areEqual(comparison);
        }
    }
}
