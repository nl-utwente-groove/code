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
import groove.control.ControlTransition;
import groove.control.Location;
import groove.explore.util.ControlStateCache;
import groove.explore.util.ExploreCache;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;
import groove.util.Reporter;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;
import groove.verify.ModelChecking;

import java.util.HashSet;
import java.util.Set;

/**
 * Class providing functionality to generate new states in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StateGenerator {

    /**
     * Creates a state generator for a given graph transition system.
     * @param gts
     */
    public StateGenerator(GTS gts) {
        super();
        setGTS(gts);
    }

    /**
     * Constructor for a state genenerator for a product gts.
     * @param gts the product gts
     */
    public StateGenerator(ProductGTS gts) {
        super();
        setProductGTS(gts);
    }

    /**
     * Returns the underlying GTS.
     */
    public GTS getGTS() {
        return this.gts;
    }

    /**
     * Sets a new GTS, and resets the system record.
     */
    public void setGTS(GTS gts) {
        this.gts = gts;
        // this.collector.setGTS(gts);
        // this.applier = null;
    }

    /**
     * Returns the product gts.
     * @return the product gts
     */
    public ProductGTS getProductGTS() {
        return this.productGts;
    }

    /**
     * Sets the product gts.
     * @param gts the product gts
     */
    public void setProductGTS(ProductGTS gts) {
        this.productGts = gts;
    }

    /** Convenience method to retrieve the record from the current GTS. */
    protected final SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * To be called only by
     * {@link #addTransition(GraphState, RuleEvent, ExploreCache)}.
     */
    private Set<? extends GraphTransition> addTransition(GraphState source,
            RuleApplication appl, ExploreCache cache) {
        addTransitionReporter.start();

        GraphTransition transition;
        ControlTransition ct = null;
        Location targetLocation;
        if (cache instanceof ControlStateCache) {
            ct = ((ControlStateCache) cache).getTransition(appl.getRule());
            targetLocation = ct.target();
        } else {
            targetLocation = cache.getTarget(appl.getRule());
        }
        if (!appl.getRule().isModifying()) {
            if (source.getLocation() != targetLocation
                || (source.getLocation() != null && ((ControlState) source.getLocation()).getTransition(
                    appl.getRule()).hasOutputParameters())) {
                GraphNextState freshTarget = createState(appl, source);
                freshTarget.setLocation(targetLocation);
                GraphState isoTarget = getGTS().addState(freshTarget);
                if (isoTarget == null) {
                    transition = freshTarget;
                } else {
                    transition =
                        createTransition(appl, source, isoTarget, true);
                }
            } else {
                transition = createTransition(appl, source, source, false);
            }
        } else {
            transition = computeConfluentTransition(source, appl);

            if (transition == null
                || transition.target().getLocation() != targetLocation) {
                // can't have this as add_transition, it may be counted as
                // matching
                GraphNextState freshTarget = createState(appl, source);
                freshTarget.setLocation(targetLocation);
                GraphState isoTarget = getGTS().addState(freshTarget);
                if (isoTarget == null) {
                    transition = freshTarget;
                } else {
                    transition =
                        createTransition(appl, source, isoTarget, true);
                }
            } else {
                confluentDiamondCount++;
            }
        }

        // add transition to gts
        getGTS().addTransition(transition);

        addTransitionReporter.stop();
        Set<GraphTransition> result = new HashSet<GraphTransition>(1);
        result.add(transition);
        return result;
    }

    /**
     * Adds to the GTS the transitions defined from a given rule match.
     * (Multiple transitions may be added only in the case of abstract
     * simulation.) The applications' target graphs are compared to the existing
     * states for symmetry; if a symmetric one is found then that is taken as
     * target state. If no symmetric state is found, then a fresh target state
     * is added. The method returns the set of target states of all transitions
     * defined by the match.
     * @param source the source state of the new transition
     * @param match the rule match defining the derivation
     * @return the set of actually added states
     */
    public Set<? extends GraphState> addTransition(GraphState source,
            RuleEvent match, ExploreCache cache) {

        Set<? extends GraphTransition> gtrs = applyMatch(source, match, cache);
        HashSet<GraphState> states = new HashSet<GraphState>();
        for (GraphTransition trans : gtrs) {
            states.add(trans.target());
        }
        return states;
    }

    /**
     * Applies a match and returns the resulting complete set of graph
     * transitions.
     */
    public Set<? extends GraphTransition> applyMatch(GraphState source,
            RuleEvent event, ExploreCache cache) {
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

    /**
     * Adds a transition to the product gts given a source Buechi graph-state, a
     * graph transition, and a target Buechi location.
     * @param source the source Buechi graph-state
     * @param transition the graph transition
     * @param targetLocation the target Buechi location
     * @return the added product transition
     */
    public Set<ProductTransition> addTransition(BuchiGraphState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        addTransitionReporter.start();
        // we assume that we only add transitions for modifying graph
        // transitions
        BuchiGraphState target =
            createBuchiGraphState(source, transition, targetLocation);
        BuchiGraphState isoTarget = getProductGTS().addState(target);
        ProductTransition productTransition = null;

        if (isoTarget == null) {
            // no isomorphic state found
            productTransition =
                createProductTransition(source, transition, target);
        } else {
            assert (isoTarget.iteration() <= ModelChecking.CURRENT_ITERATION) : "This state belongs to the next iteration and should not be explored now.";
            productTransition =
                createProductTransition(source, transition, isoTarget);
        }
        addTransitionReporter.stop();
        return getProductGTS().addTransition(productTransition);
    }

    private BuchiGraphState createBuchiGraphState(BuchiGraphState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        if (transition == null) {
            // the system-state is a final one for which we assume an artificial
            // self-loop
            // the resulting Buchi graph-state is nevertheless the product of
            // the
            // graph-state component of the source Buchi graph-state and the
            // target
            // Buchi-location
            return new BuchiGraphState(getProductGTS().getRecord(),
                source.getGraphState(), targetLocation, source);
        } else {
            return new BuchiGraphState(getProductGTS().getRecord(),
                transition.target(), targetLocation, source);
        }
    }

    private ProductTransition createProductTransition(BuchiGraphState source,
            GraphTransition transition, BuchiGraphState target) {
        return new ProductTransition(source, transition, target);
    }

    /** The underlying GTS. */
    private GTS gts;
    private ProductGTS productGts;
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
