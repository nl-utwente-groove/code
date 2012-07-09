/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.explore.util;

import groove.abstraction.pattern.gui.dialog.PatternPreviewDialog;
import groove.abstraction.pattern.lts.PSTS;
import groove.abstraction.pattern.lts.PatternGraphNextState;
import groove.abstraction.pattern.lts.PatternGraphState;
import groove.abstraction.pattern.lts.PatternGraphTransition;
import groove.abstraction.pattern.lts.PatternNextState;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.lts.PatternTransition;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.trans.MaterialisingRuleApplication;
import groove.abstraction.pattern.trans.NonBranchingRuleApplication;
import groove.abstraction.pattern.trans.PatternShapeRuleApplication;
import groove.explore.util.MatchApplier;

/**
 * Match applier for pattern shape transformation.
 * 
 * See {@link MatchApplier}. 
 */
public class PatternShapeMatchApplier implements PatternRuleEventApplier {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Debug flag. If set to true, text will be printed in stdout. */
    private static boolean DEBUG = true;
    /** Debug flag. If set to true, the shapes will be shown in a dialog. */
    private static final boolean USE_GUI = true;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The underlying PSTS. */
    private final PSTS psts;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    /** Creates an applier for a given pattern transition system. */
    public PatternShapeMatchApplier(PSTS pgts) {
        this.psts = pgts;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void apply(PatternState source, Match match) {
        assert match instanceof PreMatch;
        PreMatch preMatch = (PreMatch) match;

        if (USE_GUI && source.getNumber() == 0) {
            PatternPreviewDialog.showPatternGraph(source.getGraph());
        }

        PatternShapeRuleApplication app =
            createApplication((PatternShape) source.getGraph(), preMatch);

        for (PatternShape result : app.transform()) {
            result.normalise();
            PatternNextState newState =
                new PatternGraphNextState(result, (PatternGraphState) source,
                    this.psts.getNextStateNr(), this.psts, preMatch);
            PatternState oldState = this.psts.addState(newState);
            PatternTransition trans = null;
            if (oldState != null) {
                // The state was not added as an equivalent state existed.
                trans = new PatternGraphTransition(source, preMatch, oldState);
                println("New transition: " + trans);
            } else {
                // The state was added as a next-state.
                trans = newState;
                println("New state: " + source + "--"
                    + match.getRule().getName() + "-->" + newState);
                if (USE_GUI) {
                    PatternPreviewDialog.showPatternGraph(newState.getGraph());
                }
            }
            this.psts.addTransition(trans);
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private PatternShapeRuleApplication createApplication(PatternShape pShape,
            PreMatch preMatch) {
        switch (this.psts.getApplicationMethod()) {
        case MATERIALISATION:
            return new MaterialisingRuleApplication(pShape, preMatch);
        case NON_BRANCHING:
            return new NonBranchingRuleApplication(pShape, preMatch);
        default:
            assert false;
            return null;
        }
    }

    private void println(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    /** Different kinds of rule application. */
    public enum ApplicationMethod {
        /** Computes the shape materialisation, branches. */
        MATERIALISATION,
        /** Less precise over-approximation that only modifies multiplicities. */
        NON_BRANCHING
    }

}
