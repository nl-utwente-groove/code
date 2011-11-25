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

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.graph.GraphCache;
import groove.graph.Morphism;
import groove.graph.TypeEdge;
import groove.graph.TypeLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.util.FilterIterator;
import groove.util.Pair;
import groove.util.TreeHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
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
    /** Flag to indicate if we should do ancestor collapsing. */
    private boolean checkAncestorCollapsing;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs the GTS object for the given grammar. */
    public AGTS(GraphGrammar grammar) {
        super(grammar);
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
        this.checkAncestorCollapsing = false;
        this.getRecord().setReuseEvents(false);
        this.getRecord().setCheckIso(true);
        this.storeAbsLabels();
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
        ShapeState newState = (ShapeState) newGState;
        ShapeState result = (ShapeState) super.addState(newState);
        if (result == null) {
            // There is no state in the transition system that subsumes the
            // new state. Maybe the new state subsumes some states that are
            // already in the GTS.
            this.subsumedStatesCount += newState.markSubsumedStates();
            this.doAncestorCollapsing(newState);
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
        super.addTransition(transition);
    }

    @Override
    protected TreeHashSet<GraphState> createStateSet() {
        return new ShapeStateSet(getCollapse());
    }

    @Override
    public Iterator<GraphState> getOpenStateIter() {
        return new FilterIterator<GraphState>(nodeSet().iterator()) {
            @Override
            protected boolean approves(Object obj) {
                ShapeState state = (ShapeState) obj;
                return !state.isClosed() && !state.isSubsumed();
            }
        };
    }

    /** Throws an UnsupportedOperationException. */
    @Override
    protected GraphCache<GraphState,GraphTransition> createCache() {
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
            new ShapeState((Shape) startGraph,
                getGrammar().getCtrlAut().getStart(), 0);
        return result;
    }

    @Override
    public boolean checkDiamonds() {
        return false;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Store the abstraction labels in the parameters, if any. */
    private void storeAbsLabels() {
        Set<TypeLabel> unaryLabels = new MyHashSet<TypeLabel>();
        for (TypeEdge typeEdge : getGrammar().getTypeGraph().edgeSet()) {
            if (!typeEdge.label().isBinary()) {
                unaryLabels.add(typeEdge.label());
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

        Parameters.setAbsLabels(absLabels);
    }

    /** Returns the number of states marked as subsumed. */
    public int getSubsumedStatesCount() {
        return this.subsumedStatesCount;
    }

    /** Returns the number of transitions marked as subsumed. */
    public int getSubsumedTransitionsCount() {
        return this.subsumedTransitionsCount;
    }

    private boolean doAncestorCollapsing(ShapeState newState) {
        if (!this.checkAncestorCollapsing
            || !(newState instanceof ShapeNextState)) {
            return false;
        }
        boolean changed = false;
        ShapeState ancestor = ((ShapeNextState) newState).source();
        Shape oldShape = ancestor.getGraph();
        Shape newShape = newState.getGraph();
        // Since state is a new fresh state, its direct ancestor is was not yet
        // subsumed (otherwise the ancestor would not have been explored). So,
        // all we need to do is check if the ancestor is subsumed by the new
        // state. (Remember that subsumption implies isomorphism).
        ShapeIsoChecker checker = ShapeIsoChecker.getInstance(true);
        Pair<Integer,Morphism<ShapeNode,ShapeEdge>> result =
            checker.compareShapes(newShape, oldShape);
        int comparison = result.one();
        if (checker.isDomStrictlyLargerThanCod(comparison)) {
            // The new state subsumes the ancestor.
            Morphism<ShapeNode,ShapeEdge> morphism = result.two();
            Multiplicity zeroPlus =
                Multiplicity.getMultiplicity(0, Multiplicity.OMEGA,
                    MultKind.NODE_MULT);
            // Check for nodes in the new shape that can be collapsed to 0+.
            for (Entry<ShapeNode,ShapeNode> entry : morphism.nodeMap().entrySet()) {
                ShapeNode newNode = entry.getValue();
                Multiplicity newMult = newShape.getNodeMult(newNode);
                if (!newMult.isZeroPlus() && newMult.isUnbounded()) {
                    ShapeNode oldNode = entry.getKey();
                    Multiplicity oldMult = oldShape.getNodeMult(oldNode);
                    if (newMult.le(oldMult)) {
                        // The multiplicity decreased in the new shape. We can
                        // set it to zeroPlus.
                        newShape.setNodeMult(newNode, zeroPlus);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Class to store the states of the GTS. */
    private static final class ShapeStateSet extends GTS.StateSet {

        /** Default constructor, delegates to super class. */
        private ShapeStateSet(int collapse) {
            super(collapse, ShapeIsoChecker.getInstance(true).downcast());
            assert collapse == COLLAPSE_ISO_STRONG;
        }

        /** Compares the given states both for (in)equality and subsumption. */
        @Override
        protected boolean areEqual(GraphState myState, GraphState otherState) {
            assert myState instanceof ShapeState;
            assert otherState instanceof ShapeState;
            ShapeState myShapeState = (ShapeState) myState;
            ShapeState otherShapeState = (ShapeState) otherState;
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
