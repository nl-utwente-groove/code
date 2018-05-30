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
package groove.lts;

import java.util.Set;

import groove.control.Binding;
import groove.control.Valuator;
import groove.control.instance.Assignment;
import groove.control.instance.Step;
import groove.grammar.Rule;
import groove.grammar.host.HostNode;
import groove.transform.CompositeEvent;
import groove.transform.MergeMap;
import groove.transform.RuleEffect;
import groove.transform.RuleEffect.Fragment;
import groove.transform.RuleEvent;
import groove.util.Reporter;

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
    }

    /**
     * Returns the underlying GTS.
     */
    protected GTS getGTS() {
        return this.gts;
    }

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
        if (!match.getStep()
            .isModifying()) {
            if (!rule.isModifying()) {
                transition = createTransition(source, match, source, false);
            } else if (match.hasTransition()) {
                // try to find the target state by walking around three previously
                // generated sides of a confluent diamond
                // the parent state is the source of source
                // the sibling is the child reached by the virtual event
                assert source instanceof GraphNextState;
                RuleTransition parentTrans = match.getTransition();
                assert source != parentTrans.source();
                boolean sourceModifiesCtrl = ((GraphNextState) source).getStep()
                    .isModifying();
                MatchResult sourceKey = ((GraphNextState) source).getKey();
                if (!sourceModifiesCtrl && !parentTrans.isSymmetry() && !match.getEvent()
                    .conflicts(sourceKey.getEvent())) {
                    GraphState sibling = parentTrans.target();
                    RuleTransitionStub siblingOut = sibling.getOutStub(sourceKey);
                    if (siblingOut != null) {
                        transition = createTransition(source,
                            match,
                            siblingOut.getTarget(sibling),
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
                transition = new DefaultRuleTransition(source, match, freshTarget.getAddedNodes(),
                    isoTarget, true);
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
    private GraphNextState createState(GraphState source, MatchResult match)
        throws InterruptedException {
        HostNode[] addedNodes;
        Object[] frameValues;
        RuleEvent event = match.getEvent();
        Step ctrlStep = match.getStep();
        boolean hasFrameValues = ctrlStep.onFinish()
            .hasVars();
        RuleEffect effectRecord = null;
        if (reuseCreatedNodes(source, match)) {
            RuleTransition parentOut = match.getTransition();
            addedNodes = parentOut.getAddedNodes();
        } else if (event.getRule()
            .hasNodeCreators()) {
            // compute the frame values at the same time, if there are any
            Fragment fragment = hasFrameValues ? Fragment.NODE_ALL : Fragment.NODE_CREATION;
            effectRecord = new RuleEffect(source.getGraph(), fragment, this.gts.getOracle());
            event.recordEffect(effectRecord);
            effectRecord.setFixed();
            addedNodes = effectRecord.getCreatedNodeArray();
        } else {
            addedNodes = EMPTY_NODE_ARRAY;
        }
        if (hasFrameValues || ctrlStep.onFinish()
            .getNestingDepth() > 0) {
            // only compute the effect if it has not yet been done
            if (effectRecord == null) {
                effectRecord = new RuleEffect(source.getGraph(), addedNodes, Fragment.NODE_ALL);
                event.recordEffect(effectRecord);
                effectRecord.setFixed();
            }
            frameValues = computeFrameValues(ctrlStep, source, event, effectRecord);
        } else {
            frameValues = EMPTY_NODE_ARRAY;
        }
        return new DefaultGraphNextState(this.gts.nodeCount(), (AbstractGraphState) source, match,
            addedNodes, frameValues);
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
            RuleTransition parentOut = match.getTransition();
            addedNodes = parentOut.getAddedNodes();
        } else if (match.getAction()
            .hasNodeCreators()) {
            RuleEffect effect =
                new RuleEffect(source.getGraph(), Fragment.NODE_CREATION, this.gts.getOracle());
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
        HostNode[] addedNodes = match.getTransition()
            .getAddedNodes();
        if (addedNodes == null || addedNodes.length == 0) {
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

    /** Computes the value stack for the target state of a given rule transition. */
    private Object[] computeFrameValues(Step step, GraphState source, RuleEvent event,
        RuleEffect record) {
        Object[] result = source.getActualValues();
        for (Assignment assign : step.getApplyAssignments()) {
            switch (assign.getKind()) {
            case MODIFY:
                Object[] values = apply(assign, result, event, record);
                result = Valuator.replace(result, values);
                break;
            case POP:
            case PUSH:
                result = assign.apply(result);
                break;
            default:
                assert false;
            }
        }
        return result;
    }

    /** Computes the frame values for the target of a rule application. */
    private HostNode[] apply(Assignment assign, Object[] sourceValues, RuleEvent event,
        RuleEffect record) {
        Binding[] bindings = assign.getBindings();
        int valueCount = bindings.length;
        HostNode[] result = new HostNode[valueCount];
        HostNode[] createdNodes = record.getCreatedNodeArray();
        Set<HostNode> removedNodes = record.getRemovedNodes();
        MergeMap mergeMap = record.getMergeMap();
        for (int i = 0; i < valueCount; i++) {
            Binding bind = bindings[i];
            HostNode value = null;
            switch (bind.getSource()) {
            case VAR:
                // this is an input parameter of the rule
                HostNode sourceValue = Valuator.get(sourceValues, bind);
                value = getNodeImage(sourceValue, mergeMap, removedNodes);
                break;
            case ANCHOR:
                // the parameter is not a creator node
                sourceValue = (HostNode) event.getAnchorImage(bind.getIndex());
                value = getNodeImage(sourceValue, mergeMap, removedNodes);
                break;
            case CREATOR:
                // the parameter is a creator node
                value = createdNodes[bind.getIndex()];
                break;
            default:
                assert false;
            }
            result[i] = value;
        }
        return result;
    }

    private HostNode getNodeImage(HostNode key, MergeMap mergeMap, Set<HostNode> removedNodes) {
        if (mergeMap == null) {
            if (removedNodes == null || !removedNodes.contains(key)) {
                return key;
            } else {
                return null;
            }
        } else {
            return mergeMap.getNode(key);
        }
    }

    /** The underlying GTS. */
    private final GTS gts;
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
    private static final HostNode[] EMPTY_NODE_ARRAY = new HostNode[0];

    /** Reporter for profiling information. */
    static private final Reporter reporter = Reporter.register(MatchApplier.class);
    /** Profiling aid for adding states. */
    static public final Reporter addStateReporter = reporter.register("addState");
    /** Profiling aid for adding transitions. */
    static public final Reporter addTransitionReporter = reporter.register("addTransition");
}
