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
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.trans.GraphGrammar;
import groove.trans.SystemRecord;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class AGTS extends GTS {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public AGTS(GraphGrammar grammar) {
        super(grammar);
        this.getRecord().setCheckIso(true);
        // This adds the start state.
        this.startState();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphState addState(GraphState newState) {
        assert newState instanceof ShapeState : "Type error : " + newState
            + " is not of type ShapeState.";
        return super.addState(newState);
    }

    @Override
    public void addTransition(GraphTransition transition) {
        assert (transition instanceof ShapeTransition)
            || (transition instanceof ShapeNextState) : "Type error : "
            + transition + " is not of type ShapeTransition or ShapeNextState.";
        super.addTransition(transition);
    }

    @Override
    public ShapeState startState() {
        return (ShapeState) super.startState();
    }

    @Override
    protected SystemRecord createRecord() {
        SystemRecord record = new SystemRecord(getGrammar(), true);
        return record;
    }

    @Override
    public double getBytesPerState() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GraphShapeCache createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void notifyLTSListenersOfClose(State closed) {
        super.notifyLTSListenersOfClose(closed);
    }

    @Override
    protected ShapeState createStartState(Graph startGraph) {
        Shape shape = new Shape(startGraph);
        ShapeState result = new ShapeState(shape);
        return result;
    }

}
