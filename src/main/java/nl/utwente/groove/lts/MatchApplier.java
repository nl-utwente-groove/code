/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.lts;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.control.Valuator;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.transform.CompositeEvent;
import nl.utwente.groove.transform.RuleEffect;
import nl.utwente.groove.transform.RuleEffect.Fragment;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Reporter;

/**
 * Provides functionality to add states and transitions to a GTS, based on known
 * rule events.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MatchApplier {
    /**
     * Creates an applier for a given graph transition system.
     */
    public MatchApplier(GTS gts) {
        this.gts = gts;
        this.valuator = gts.getRecord().getValuator();
    }

    /**
     * Returns the underlying GTS.
     */
    protected GTS getGTS() {
        return this.gts;
    }

    /** The underlying GTS. */
    private final GTS gts;

    /** The valuator of the underlying GTS. */
    private final Valuator valuator;

    /**
     * Adds a transition to the GTS, from a given source state and for a given
     * rule match. The match is assumed not to have been explored yet.
     * @return the added (new) transition; non-{@code null}
     * @throws InterruptedException if an oracle input was cancelled
     */
    public RuleTransition apply(GraphState source, MatchResult match) throws InterruptedException {
        addTransitionReporter.start();
        RuleTransition transition = null;
        Rule rule = match.getAction();
        if (!match.getStep().isModifying()) {
            if (!rule.isModifying()) {
                transition = createTransition(source, match, source, false);
            } else if (match.hasTransition()) {
                // try to find the target state by walking around three previously
                // generated sides of a confluent diamond
                // the parent state is the source of source
                // the sibling is the child reached by the virtual event
                assert source instanceof GraphNextState;
                var parentOut = match.getTransition();
                assert parentOut != null && source != parentOut.source();
                boolean sourceModifiesCtrl = ((GraphNextState) source).getStep().isModifying();
                MatchResult sourceKey = ((GraphNextState) source).getKey();
                if (!sourceModifiesCtrl && !parentOut.isSymmetry()
                    && !match.getEvent().conflicts(sourceKey.getEvent())) {
                    GraphState sibling = parentOut.target();
                    RuleTransitionStub siblingOut = sibling.getOutStub(sourceKey);
                    if (siblingOut != null) {
                        transition = createTransition(source, match, siblingOut.getTarget(sibling),
                                                      siblingOut.isSymmetry());
                        confluentDiamondCount++;
                    }
                }
            }
        }
        if (transition == null) {
            GraphNextState freshTarget = createState(source, match);
            addStateReporter.start();
            GraphState isoTarget = getGTS().addState(freshTarget);
            addStateReporter.stop();
            if (isoTarget == null) {
                transition = freshTarget;
            } else {
                // we don't actually know for sure if the old state graph is equal or merely isomorphic
                transition = new DefaultRuleTransition(source, match, freshTarget.getAddedNodes(),
                    isoTarget, getGTS().getRecord().isCheckIso());
            }
        }
        // add transition to gts
        getGTS().addTransition(transition);
        addTransitionReporter.stop();
        return transition;
    }

    /**
     * Creates a fresh graph state, based on a given rule application and source
     * state.
     * @throws InterruptedException if an oracle input was cancelled
     */
    private GraphNextState createState(GraphState source,
                                       MatchResult match) throws InterruptedException {
        @NonNull
        HostNode[] addedNodes;
        Object[] stack;
        RuleEvent event = match.getEvent();
        Step ctrlStep = match.getStep();
        boolean hasVars = ctrlStep.onFinish().hasVars();
        RuleEffect effectRecord = null;
        if (reuseCreatedNodes(source, match)) {
            RuleTransition parentOut = match.getTransition();
            assert parentOut != null;
            addedNodes = parentOut.getAddedNodes();
        } else if (event.getRule().hasNodeCreators()) {
            // compute the frame values at the same time, if there are any
            Fragment fragment = hasVars
                ? Fragment.NODE_ALL
                : Fragment.NODE_CREATION;
            effectRecord = new RuleEffect(source.getGraph(), fragment, this.gts.getOracle());
            event.recordEffect(effectRecord);
            effectRecord.setFixed();
            addedNodes = effectRecord.getCreatedNodeArray();
        } else {
            addedNodes = EMPTY_NODE_ARRAY;
        }
        if (hasVars || ctrlStep.onFinish().getNestingDepth() > 0) {
            // only compute the effect if it has not yet been done
            if (effectRecord == null) {
                effectRecord = new RuleEffect(source.getGraph(), addedNodes, Fragment.NODE_ALL);
                event.recordEffect(effectRecord);
                effectRecord.setFixed();
            }
            stack = computeTargetStack(ctrlStep, source, event, effectRecord);
        } else {
            stack = EMPTY_NODE_ARRAY;
        }
        return new DefaultGraphNextState(this.gts.nodeCount(), (AbstractGraphState) source, match,
            addedNodes, stack);
    }

    /**
     * Creates a fresh graph transition, based on a given rule event and source
     * and target state. A final parameter determines if the target state is
     * directly derived from the source, or modulo a symmetry.
     * @throws InterruptedException if an oracle input was cancelled
     */
    private RuleTransition createTransition(GraphState source, MatchResult match, GraphState target,
                                            boolean symmetry) throws InterruptedException {
        HostNode[] addedNodes;
        RuleEvent event = match.getEvent();
        if (reuseCreatedNodes(source, match)) {
            var parentOut = match.getTransition();
            assert parentOut != null;
            addedNodes = parentOut.getAddedNodes();
        } else if (match.getAction().hasNodeCreators()) {
            RuleEffect effect
                = new RuleEffect(source.getGraph(), Fragment.NODE_CREATION, this.gts.getOracle());
            event.recordEffect(effect);
            effect.setFixed();
            addedNodes = effect.getCreatedNodeArray();
        } else {
            addedNodes = EMPTY_NODE_ARRAY;
        }
        return new DefaultRuleTransition(source, match, addedNodes, target, symmetry);
    }

    /**
     * Indicates if the created nodes in a given match can be reused
     * as created nodes for a new target graph.
     */
    private boolean reuseCreatedNodes(GraphState source, MatchResult match) {
        if (!match.hasTransition()) {
            return false;
        }
        if (!(source instanceof GraphNextState)) {
            return false;
        }
        var parentOut = match.getTransition();
        assert parentOut != null;
        HostNode[] addedNodes = parentOut.getAddedNodes();
        if (addedNodes.length == 0) {
            return true;
        }
        RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
        if (sourceEvent instanceof CompositeEvent) {
            return false;
        }
        RuleEvent matchEvent = match.getEvent();
        if (matchEvent instanceof CompositeEvent) {
            return false;
        }
        return sourceEvent != matchEvent;
    }

    /** Computes the prime call stack for the target state of a given rule transition. */
    private Object[] computeTargetStack(Step step, GraphState source, RuleEvent event,
                                        RuleEffect record) {
        Object[] result = source.getFrameStack(step.getSource());
        if (!record.isNodeId()) {
            // map the entire valuation stack through the node mapping of the record
            result = CallStack.map(result, record::mapNode);
        }
        var valuator = this.valuator;
        HostNode[] createdNodes = record.getCreatedNodeArray();
        valuator.setCreatorInfo(i -> createdNodes[i]);
        var anchorImages = event.getAnchorImages();
        if (record.isNodeId()) {
            valuator.setAnchorInfo(i -> (HostNode) anchorImages[i]);
        } else {
            valuator.setAnchorInfo(i -> record.mapNode((HostNode) anchorImages[i]));
        }
        result = step.getApplyChange().apply(result, valuator);
        return result;
    }

    /**
     * The number of confluent diamonds found.
     */
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

    /**
     * Constant empty node array, to be shared among rule applications that
     * create no nodes.
     */
    private static final HostNode[] EMPTY_NODE_ARRAY = {};

    /** Reporter for profiling information. */
    static private final Reporter reporter = Reporter.register(MatchApplier.class);
    /** Profiling aid for adding states. */
    static public final Reporter addStateReporter = reporter.register("addState");
    /** Profiling aid for adding transitions. */
    static public final Reporter addTransitionReporter = reporter.register("addTransition");
}
