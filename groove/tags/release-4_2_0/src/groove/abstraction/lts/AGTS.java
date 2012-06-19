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
package groove.abstraction.lts;

import groove.abstraction.Shape;
import groove.graph.GraphCache;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.DefaultHostGraph;
import groove.trans.GraphGrammar;
import groove.util.TreeHashSet;

/**
 * The graph transition system for abstract exploration. All states of this
 * GTS are of type Shape.
 * 
 * @author Eduardo Zambon
 */
public final class AGTS extends GTS {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs the GTS object for the given grammar. */
    public AGTS(GraphGrammar grammar) {
        super(grammar);
        this.getRecord().setCheckIso(true);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** The given state must be of type ShapeState. */
    @Override
    public GraphState addState(GraphState newState) {
        assert newState instanceof ShapeState : "Type error : " + newState
            + " is not of type ShapeState.";
        return super.addState(newState);
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
    public double getBytesPerState() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected TreeHashSet<GraphState> createStateSet() {
        return new ShapeStateSet(getCollapse());
    }

    @Override
    protected GraphCache<?,?> createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected DefaultHostGraph createStartGraph(DefaultHostGraph startGraph) {
        Shape result = new Shape(startGraph);
        result.setFixed();
        return result;
    }

    @Override
    protected ShapeState createStartState(DefaultHostGraph startGraph) {
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
    // Inner classes
    // ------------------------------------------------------------------------

    /** Class to store the states of the GTS. */
    private static final class ShapeStateSet extends GTS.StateSet {

        /** Default constructor, delegates to super class. */
        private ShapeStateSet(int collapse) {
            super(collapse);
        }

        /**
         * We override this method to make sure that shapes are properly
         * compared for isomorphism.
         */
        @Override
        protected boolean areEqual(GraphState stateKey, GraphState otherStateKey) {
            Shape one = (Shape) stateKey.getGraph();
            Shape two = (Shape) otherStateKey.getGraph();
            return one.equals(two);
        }

    }
}