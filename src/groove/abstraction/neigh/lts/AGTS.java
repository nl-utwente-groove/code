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

import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.graph.EdgeRole;
import groove.graph.GraphCache;
import groove.graph.TypeLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.util.FilterIterator;
import groove.util.TreeHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The graph transition system for abstract exploration. All states of this
 * GTS are of type Shape.
 * 
 * @author Eduardo Zambon
 */
public final class AGTS extends GTS {

    private int subsumedCount;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs the GTS object for the given grammar. */
    public AGTS(GraphGrammar grammar) {
        super(grammar);
        this.subsumedCount = 0;
        this.getRecord().setCheckIso(true);
        this.storeAbsLabels();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** The given state must be of type ShapeState. */
    @Override
    public ShapeState addState(GraphState newState) {
        assert newState instanceof ShapeState : "Type error : " + newState
            + " is not of type ShapeState.";
        ShapeState result = (ShapeState) super.addState(newState);
        if (result == null) {
            // There is no state in the transition system that subsumes the
            // new state. Maybe the new state subsumes some states that are
            // already in the GTS.
            this.subsumedCount += ((ShapeState) newState).markSubsumedStates();
        }
        return result;
    }

    /**
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
        unaryLabels.addAll(this.getGrammar().getTypeGraph().getLabels(
            EdgeRole.NODE_TYPE));
        unaryLabels.addAll(this.getGrammar().getTypeGraph().getLabels(
            EdgeRole.FLAG));

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
        return this.subsumedCount;
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

        @Override
        protected boolean areEqual(GraphState myState, GraphState otherState) {
            assert myState instanceof ShapeState;
            assert otherState instanceof ShapeState;
            ShapeState myShapeState = (ShapeState) myState;
            ShapeState otherShapeState = (ShapeState) otherState;
            ShapeIsoChecker checker = ShapeIsoChecker.getInstance(true);
            int comparison =
                checker.compareShapes(myShapeState.getGraph(),
                    otherShapeState.getGraph());
            if (checker.isDomStrictlyLargerThanCod(comparison)) {
                // New state subsumes old one.
                myShapeState.addSubsumedState(otherShapeState);
            }
            return checker.areEqual(comparison);
        }
    }
}
