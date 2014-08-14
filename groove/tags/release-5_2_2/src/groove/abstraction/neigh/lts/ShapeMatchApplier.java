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
 * $Id$
 */
package groove.abstraction.neigh.lts;

import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.trans.Materialisation;
import groove.abstraction.neigh.trans.NeighAnchorFactory;
import groove.lts.GraphState;
import groove.lts.MatchApplier;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.transform.RuleEvent;
import groove.util.Pair;
import groove.util.Visitor;

/**
 * A version of a {@link MatchApplier} for abstract exploration.
 * In order for this class to receive the proper number of matches on the
 * {@link #apply(GraphState, MatchResult)} method, a dedicated anchor factory
 * must be installed. For the neighbourhood abstraction we used the
 * {@link NeighAnchorFactory}.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeMatchApplier extends MatchApplier {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Debug flag. If set to true, text will be printed in stdout. */
    private static boolean DEBUG = false;
    /** Debug flag. If set to true, the shapes will be shown in a dialog. */
    private static final boolean USE_GUI = false;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Delegates to super. */
    public ShapeMatchApplier(AGTS gts) {
        super(gts);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Specialises the return type. */
    @Override
    protected AGTS getGTS() {
        return (AGTS) super.getGTS();
    }

    /**
     * Applies the given pre-match to the given state.
     * 
     * From the given pre-match a (possible empty) set of materialisations is
     * computed. For each materialisation the rule is applied, the resulting
     * shape is normalised and the normalised shape is added to the state space.
     * Subsumption collapsing is used and thus the normalised shape can
     * produce either a new transition, if the shape is subsumed by an existing
     * state, or a new state.
     * 
     * Due to non-determinism in the materialisation, a single pre-match may
     * produce more than one transition. This means that the return value of
     * this method is garbage: callers should not use it.
     */
    @Override
    public RuleTransition apply(GraphState sourceGS, final MatchResult match) {
        assert sourceGS instanceof ShapeState;
        addTransitionReporter.start();
        final ShapeState source = (ShapeState) sourceGS;
        RuleTransition result = null;
        RuleEvent origEvent = match.getEvent();
        Shape host = source.getGraph();
        AGTS agts = this.getGTS();

        if (host == null) {
            assert agts.isReachability() && source.isDisconnected();
            return result;
        }

        if (USE_GUI && source.getNumber() == 0) {
            ShapePreviewDialog.showShape(host);
        }

        //        try {
        // Transform the source state.
        assert PreMatch.isValidPreMatch(host, origEvent);
        // Visit all materialisations and apply them to the source graph
        Visitor<Materialisation,RuleTransition> visitor =
            new Visitor<Materialisation,RuleTransition>() {
                @Override
                protected boolean process(Materialisation mat) {
                    RuleTransition trans = applyMaterialisation(source, match, mat);
                    if (trans != null) {
                        setResult(trans);
                    }
                    return true;
                }
            };
        Materialisation.visitMaterialisations(host, origEvent.getMatch(host), visitor);
        result = visitor.getResult();
        //        } catch (Throwable e) {
        //            // Additional code for bug hunting.
        //            System.err.println("\nFound a bug in the abstraction code!!!");
        //            File file = new File(source.toString() + ".gxl");
        //            ShapeGxl.getInstance().saveShape(host, file);
        //            System.err.println(String.format(
        //                "Dumped shape from state %s to help debugging.",
        //                source.toString()));
        //            System.err.println(origEvent.getMatch(host));
        //            System.err.println();
        //            // Raise the error again so it reaches the top level.
        //            // throw e;
        //            e.printStackTrace();
        //            System.exit(1);
        //        }

        addTransitionReporter.stop();
        // Returns the last produced transition.
        return result;
    }

    /**
     * Transforms and normalises a source graph according to a given materialisation,
     * and adds it to the GTS, together with a transition to it if required.
     */
    private RuleTransition applyMaterialisation(ShapeState source, MatchResult match,
            Materialisation mat) {
        AGTS agts = this.getGTS();
        // Transform and normalise the shape.
        Pair<Shape,RuleEvent> pair = mat.applyMatch(agts.getRecord());
        Shape transformedShape = pair.one();
        RuleEvent realEvent = pair.two();
        MatchResult realMatch = new MatchResult(realEvent, match.getStep());
        Shape target = transformedShape.normalise();

        RuleTransition trans = null;
        ShapeNextState newState =
            new ShapeNextState(agts.getNextStateNr(), target, source, realMatch);
        addStateReporter.start();
        ShapeState oldState = agts.addState(newState);
        addStateReporter.stop();
        if (oldState != null) {
            // The state was not added as an equivalent state existed.
            if (!agts.isReachability()) {
                trans = new ShapeTransition(source, realMatch, oldState);
                this.println("New transition: " + trans);
            }
        } else {
            // The state was added as a next-state.
            trans = newState;
            this.println("New state: " + source + "--" + match + "-->" + newState);
            if (USE_GUI) {
                ShapePreviewDialog.showShape(newState.getGraph());
            }
        }
        if (trans != null) {
            agts.addTransition(trans);
        }
        return trans;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void println(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

}
