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

import groove.control.Binding;
import groove.control.CtrlTransition;
import groove.grammar.Rule;
import groove.grammar.host.HostNode;
import groove.transform.CompositeEvent;
import groove.transform.MergeMap;
import groove.transform.RuleEffect;
import groove.transform.RuleEffect.Fragment;
import groove.transform.RuleEvent;
import groove.util.Reporter;

import java.util.Set;

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
    public GTS getGTS() {
        return this.gts;
    }

    /**
     * Adds a transition to the GTS, from a given source state and for a given
     * rule match. The match is assumed not to have been explored yet.
     * @return the added (new) transition; non-{@code null}
     */
    public RuleTransition apply(GraphState source, MatchResult match) {
        addTransitionReporter.start();
        RuleTransition transition = null;
        Rule rule = match.getRule();
        CtrlTransition ctrlTrans = match.getCtrlTransition();
        if (!ctrlTrans.isModifying()) {
            if (!rule.isModifying()) {
                transition = createTransition(source, match, source, false);
            } else if (match.hasRuleTransition()) {
                // try to find the target state by walking around three previously
                // generated sides of a confluent diamond
                // the parent state is the source of source
                // the sibling is the child reached by the virtual event
                assert source instanceof GraphNextState;
                RuleTransition parentTrans = match.getRuleTransition();
                assert source != parentTrans.source();
                boolean sourceModifiesCtrl =
                    ((GraphNextState) source).getCtrlTransition().isModifying();
                MatchResult sourceKey = ((GraphNextState) source).getKey();
                if (!sourceModifiesCtrl && !parentTrans.isSymmetry()
                    && !match.getEvent().conflicts(sourceKey.getEvent())) {
                    GraphState sibling = parentTrans.target();
                    RuleTransitionStub siblingOut = sibling.getOutStub(sourceKey);
                    if (siblingOut != null) {
                        transition =
                            createTransition(source, match, siblingOut.getTarget(sibling),
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
                transition =
                    new DefaultRuleTransition(source, match, freshTarget.getAddedNodes(),
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
     */
    private GraphNextState createState(GraphState source, MatchResult match) {
        HostNode[] addedNodes;
        HostNode[] boundNodes;
        RuleEvent event = match.getEvent();
        if (reuseCreatedNodes(source, match)) {
            RuleTransition parentOut = match.getRuleTransition();
            addedNodes = parentOut.getAddedNodes();
        } else if (event.getRule().hasNodeCreators()) {
            RuleEffect record = new RuleEffect(source.getGraph(), Fragment.NODE_CREATION);
            event.recordEffect(record);
            record.setFixed();
            addedNodes = record.getCreatedNodeArray();
        } else {
            addedNodes = EMPTY_NODE_ARRAY;
        }
        CtrlTransition ctrlTrans = match.getCtrlTransition();
        if (ctrlTrans.target().getBoundVars().isEmpty()) {
            boundNodes = EMPTY_NODE_ARRAY;
        } else {
            RuleEffect record = new RuleEffect(source.getGraph(), addedNodes, Fragment.NODE_ALL);
            event.recordEffect(record);
            record.setFixed();
            boundNodes = computeBoundNodes(source, event, ctrlTrans, record);
        }
        assert boundNodes.length == ctrlTrans.getTargetVarBinding().length;
        return new DefaultGraphNextState(this.gts.nodeCount(), (AbstractGraphState) source, match,
            addedNodes, boundNodes);
    }

    /**
     * Creates a fresh graph transition, based on a given rule event and source
     * and target state. A final parameter determines if the target state is
     * directly derived from the source, or modulo a symmetry.
     */
    private RuleTransition createTransition(GraphState source, MatchResult match,
            GraphState target, boolean symmetry) {
        HostNode[] addedNodes;
        RuleEvent event = match.getEvent();
        if (reuseCreatedNodes(source, match)) {
            RuleTransition parentOut = match.getRuleTransition();
            addedNodes = parentOut.getAddedNodes();
        } else if (match.getRule().hasNodeCreators()) {
            RuleEffect record = new RuleEffect(source.getGraph(), Fragment.NODE_CREATION);
            event.recordEffect(record);
            record.setFixed();
            addedNodes = record.getCreatedNodeArray();
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
        if (!match.hasRuleTransition()) {
            return false;
        }
        if (!(source instanceof GraphNextState)) {
            return false;
        }
        HostNode[] addedNodes = match.getRuleTransition().getAddedNodes();
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

    private HostNode[] computeBoundNodes(GraphState source, RuleEvent event,
            CtrlTransition ctrlTrans, RuleEffect record) {
        HostNode[] result;
        Binding[] varBind = ctrlTrans.getTargetVarBinding();
        int valueCount = varBind.length;
        result = new HostNode[valueCount];
        HostNode[] parentValues = source.getBoundNodes();
        Rule rule = event.getRule();
        HostNode[] createdNodes = record.getCreatedNodeArray();
        Set<HostNode> removedNodes = record.getRemovedNodes();
        MergeMap mergeMap = record.getMergeMap();
        for (int i = 0; i < valueCount; i++) {
            Binding bind = varBind[i];
            HostNode value = null;
            switch (bind.getType()) {
            case VAR:
                // this is an input parameter of the rule
                HostNode sourceValue = parentValues[bind.getIndex()];
                value = getNodeImage(sourceValue, mergeMap, removedNodes);
                break;
            case OUT:
                // this is an output parameter of the rule
                bind = rule.getParBinding(bind.getIndex());
                switch (bind.getType()) {
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
