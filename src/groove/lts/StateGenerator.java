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
 * $Id: StateGenerator.java,v 1.32 2008/03/04 14:48:00 kastenberg Exp $
 */
package groove.lts;

import groove.control.ControlState;
import groove.control.Location;
import groove.explore.util.ExploreCache;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;
import groove.util.Reporter;

/**
 * Class providing functionality to generate new states in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
@Deprecated
public class StateGenerator {

    /**
     * Creates a state generator for a given graph transition system.
     * @param gts
     */
    public StateGenerator(GTS gts) {
        this.gts = gts;
    }

    /**
     * Returns the underlying GTS.
     */
    public GTS getGTS() {
        return this.gts;
    }

    /** Convenience method to retrieve the record from the current GTS. */
    protected final SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Applies a match and returns the resulting complete set of graph
     * transitions.
     */
    public GraphTransition applyMatch(GraphState source, RuleEvent event,
            ExploreCache cache) {
        RuleApplication appl;
        if (event instanceof VirtualEvent<?>) {
            @SuppressWarnings("unchecked")
            VirtualEvent<GraphTransitionStub> virtualEvent =
                (VirtualEvent) event;
            appl =
                new DefaultAliasApplication(virtualEvent.getInnerEvent(),
                    (GraphNextState) source, virtualEvent.getInnerTarget());
        } else {
            appl = event.newApplication(source.getGraph());
        }
        return this.addTransition(source, appl, cache);
    }

    /**
     * To be called only by
     * {@link #applyMatch(GraphState, RuleEvent, ExploreCache)}.
     */
    private GraphTransition addTransition(GraphState source,
            RuleApplication appl, ExploreCache cache) {
        addTransitionReporter.start();

        GraphTransition result;
        Location targetLocation = cache.getTarget(appl.getRule());
        if (!appl.getRule().isModifying()) {
            if (source.getLocation() != targetLocation
                || (source.getLocation() != null && ((ControlState) source.getLocation()).getTransition(
                    appl.getRule()).hasOutputParameters())) {
                GraphNextState freshTarget = createState(appl, source);
                freshTarget.setLocation(targetLocation);
                GraphState isoTarget = getGTS().addState(freshTarget);
                if (isoTarget == null) {
                    result = freshTarget;
                } else {
                    result = createTransition(appl, source, isoTarget, true);
                }
            } else {
                result = createTransition(appl, source, source, false);
            }
        } else {
            result = computeConfluentTransition(source, appl);

            if (result == null
                || result.target().getLocation() != targetLocation) {
                // can't have this as add_transition, it may be counted as
                // matching
                GraphNextState freshTarget = createState(appl, source);
                freshTarget.setLocation(targetLocation);
                GraphState isoTarget = getGTS().addState(freshTarget);
                if (isoTarget == null) {
                    result = freshTarget;
                } else {
                    result = createTransition(appl, source, isoTarget, true);
                }
            } else {
                confluentDiamondCount++;
            }
        }

        // add transition to gts
        getGTS().addTransition(result);

        addTransitionReporter.stop();
        return result;
    }

    /**
     * Returns the target of a given rule application, by trying to walk around
     * three sides of a confluent diamond instead of computing the target
     * directly.
     * @param source the source state of the fourth side of the (prospective)
     *        diamond
     * @param appl the rule application (applied to
     *        <code>source.getGraph()</code>)
     * @return the target state; <code>null</code> if no confluent diamond was
     *         found
     */
    private GraphTransition computeConfluentTransition(GraphState source,
            RuleApplication appl) {
        if (!(appl instanceof AliasRuleApplication)) {
            return null;
        }
        assert source instanceof GraphNextState;
        AliasRuleApplication aliasAppl = (AliasRuleApplication) appl;
        GraphTransitionStub prior = aliasAppl.getPrior();
        if (prior.isSymmetry()) {
            return null;
        }
        RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
        if (aliasAppl.getEvent().conflicts(sourceEvent)) {
            // alternating the events does not imply confluence
            return null;
        }
        GraphState parent = ((GraphNextState) source).source();
        GraphState priorTarget = prior.getTarget(parent);
        GraphTransitionStub priorOutStub = priorTarget.getOutStub(sourceEvent);
        if (priorOutStub != null) {
            return createTransition(appl, source,
                priorOutStub.getTarget(priorTarget), priorOutStub.isSymmetry());
        } else {
            return null;
        }
    }

    /**
     * Creates a fresh graph state, based on a given rule application and source
     * state.
     */
    private GraphNextState createState(RuleApplication appl, GraphState source) {
        return new DefaultGraphNextState((AbstractGraphState) source, appl,
            null);
    }

    /**
     * Creates a fresh graph transition, based on a given rule application and
     * source and target state. A final parameter determines if the target state
     * is directly derived from the source, or modulo a symmetry.
     */
    private GraphTransition createTransition(RuleApplication appl,
            GraphState source, GraphState target, boolean symmetry) {
        return new DefaultGraphTransition(appl.getEvent(),
            appl.getCreatedNodes(), source, target, symmetry);
    }

    /** The underlying GTS. */
    private final GTS gts;
    // /**
    // * The number of confluent diamonds found.
    // */
    private static int confluentDiamondCount;

    /**
     * Returns the number of confluent diamonds found during generation.
     */
    public static int getConfluentDiamondCount() {
        return confluentDiamondCount;
    }

    /**
     * Returns the time spent generating successors.
     */
    public static long getGenerateTime() {
        return addTransitionReporter.getTotalTime();
    }

    /** Reporter for profiling information. */
    static private final Reporter reporter =
        Reporter.register(StateGenerator.class);
    /** Profiling aid for adding transitions. */
    static public final Reporter addTransitionReporter =
        reporter.register("addTransition");
    /** Profiling aid for adding transitions. */
}
