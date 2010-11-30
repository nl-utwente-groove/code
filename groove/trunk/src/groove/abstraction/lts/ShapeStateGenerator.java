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
import groove.explore.util.RuleEventApplier;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.trans.RuleEvent;

import java.util.Set;

/**
 * A version of a {@link RuleEventApplier} for abstract exploration.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeStateGenerator implements RuleEventApplier {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Debug flag. If set to true, text will be printed in stdout. */
    private static final boolean DEBUG = false;
    /** Debug flag. If set to true, the shapes will be shown in a dialog. */
    private static final boolean USE_GUI = false;

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
    public GraphTransition apply(GraphState source, MatchResult match) {
        assert source instanceof ShapeState;
        GraphTransition result = null;
        RuleEvent event = match.getEvent();
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
                this.println("New transition: " + trans);
            } else {
                // The state was added as a next-state.
                trans = newState;
                this.println("New state: " + source + "--" + match + "-->"
                    + newState);
                if (USE_GUI) {
                    new ShapeDialog((Shape) newState.getGraph(),
                        Integer.toString(newState.getNumber()));
                }
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

    private void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
    }

    private void println(String s) {
        this.print(s + "\n");
    }

}
