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
package groove.abstraction.neigh.explore.util;

import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.neigh.lts.ShapeNextState;
import groove.abstraction.neigh.lts.ShapeState;
import groove.abstraction.neigh.lts.ShapeTransition;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.trans.Materialisation;
import groove.explore.util.MatchApplier;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.trans.RuleEvent;
import groove.util.Pair;

import java.util.Set;

/**
 * A version of a {@link MatchApplier} for abstract exploration.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeMatchApplier extends MatchApplier {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Debug flag. If set to true, text will be printed in stdout. */
    private static final boolean DEBUG = false;
    /** Debug flag. If set to true, the shapes will be shown in a dialog. */
    private static final boolean USE_GUI = false;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public ShapeMatchApplier(AGTS gts) {
        super(gts);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public AGTS getGTS() {
        return (AGTS) super.getGTS();
    }

    @Override
    public GraphTransition apply(GraphState sourceGS, MatchResult match) {
        assert sourceGS instanceof ShapeState;
        ShapeState source = (ShapeState) sourceGS;
        GraphTransition result = null;
        RuleEvent origEvent = match.getEvent();
        Shape host = source.getGraph();
        AGTS agts = this.getGTS();

        if (USE_GUI && source.getNumber() == 0) {
            ShapePreviewDialog.showShape(host);
        }

        // Transform the source state.
        assert PreMatch.isValidPreMatch(host, origEvent);
        // Find all materialisations.
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(host, origEvent.getMatch(host));
        // For all materialisations.
        for (Materialisation mat : mats) {
            // Transform and normalise the shape.
            Pair<Shape,RuleEvent> pair = mat.applyMatch(agts.getRecord());
            Shape transformedShape = pair.one();
            RuleEvent realEvent = pair.two();
            Shape target = transformedShape.normalise();

            GraphTransition trans;
            ShapeNextState newState =
                new ShapeNextState(agts.nodeCount(), target, source, realEvent);
            ShapeState oldState = agts.addState(newState);
            if (oldState != null) {
                // The state was not added as an equivalent state existed.
                trans = new ShapeTransition(source, realEvent, oldState);
                this.println("New transition: " + trans);
            } else {
                // The state was added as a next-state.
                trans = newState;
                this.println("New state: " + source + "--" + match + "-->"
                    + newState);
                if (USE_GUI) {
                    ShapePreviewDialog.showShape(newState.getGraph());
                }
            }
            agts.addTransition(trans);
            result = trans;
        }

        return result;
    }

    private void println(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

}