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
 * $Id: AbstrStateGenerator.java,v 1.4 2008-02-05 13:28:29 rensink Exp $
 */
package groove.abstraction.lts;

import groove.abstraction.Shape;
import groove.abstraction.Transform;
import groove.abstraction.gui.ShapeDialog;
import groove.control.Location;
import groove.explore.util.RuleEventApplier;
import groove.gui.Options;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;

import java.util.Set;

/**
 * A version of a {@link RuleEventApplier} for abstract exploration.
 * 
 * @author Eduardo Zambon
 */
public class ShapeStateGenerator implements RuleEventApplier {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    private static Options options = new Options();
    static {
        options.setValue(Options.SHOW_NODE_IDS_OPTION, 1);
        options.setValue(Options.SHOW_VERTEX_LABELS_OPTION, 0);
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final AGTS gts;
    private int transitions = 0;
    private int states = 1;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public ShapeStateGenerator(AGTS gts) {
        this.gts = gts;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public AGTS getGTS() {
        return this.gts;
    }

    @Override
    public GraphTransition apply(GraphState source, RuleEvent event,
            Location targetLocation) {
        GraphTransition result = null;

        Shape host = (Shape) source.getGraph();
        Set<Shape> targets = Transform.transform(host, event);

        for (Shape target : targets) {
            GraphTransition trans;
            ShapeNextState newState =
                new ShapeNextState(target, (ShapeState) source, event);
            ShapeState oldState = (ShapeState) getGTS().addState(newState);
            if (oldState != null) {
                // The state was not added as an equivalent state existed.
                trans =
                    new ShapeTransition((ShapeState) source, event, oldState);
                System.out.println("New transition: " + trans);
            } else {
                // The state was added as a next-state.
                trans = newState;
                // BEGIN DEBUG CODE.
                System.out.println("New state: " + source + "--" + event
                    + "-->" + newState);
                new ShapeDialog((Shape) newState.getGraph(), options,
                    Integer.toString(newState.getNumber()));
                // END DEBUG CODE.
                this.states++;
            }
            getGTS().addTransition(trans);
            this.transitions++;
            result = trans;
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public int getStateCount() {
        return this.states;
    }

    /** Basic getter method. */
    public int getTransitionCount() {
        return this.transitions;
    }

}