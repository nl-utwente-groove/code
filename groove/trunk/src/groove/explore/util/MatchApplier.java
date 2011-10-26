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
package groove.explore.util;

import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.lts.AbstractGraphState;
import groove.lts.DefaultGraphNextState;
import groove.lts.DefaultGraphTransition;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.lts.MatchResult;
import groove.trans.CompositeEvent;
import groove.trans.HostNode;
import groove.trans.MergeMap;
import groove.trans.Rule;
import groove.trans.RuleEffect;
import groove.trans.RuleEffect.Fragment;
import groove.trans.RuleEvent;
import groove.util.Reporter;

import java.util.Set;

/**
 * Provides functionality to add states and transitions to a GTS, based on known
 * rule events.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MatchApplier implements RuleEventApplier {
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
     * rule event. The event is assumed not to have been explored yet.
     * @return the added (new) transition
     */
    public GraphTransition apply(GraphState source, MatchResult match) {
        addTransitionReporter.start();
        GraphTransition transition = null;
        Rule rule = match.getEvent().getRule();
        CtrlState sourceCtrl = source.getCtrlState();
        CtrlTransition ctrlTrans = sourceCtrl.getTransition(rule);
        if (!ctrlTrans.isModifying()) {
            if (!rule.isModifying()) {
                transition = createTransition(source, match, source, false);
            } else if (match instanceof GraphTransition) {
                // try to find the target state by walking around three previously
                // generated sides of a confluent diamond
                // the parent state is the source of source
                // the sibling is the child reached by the virtual event
                assert source instanceof GraphNextState;
                GraphTransition parentTrans = (GraphTransition) match;
                boolean parentModifiesCtrl =
                    parentTrans.getCtrlTransition().isModifying();
                RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
                if (!parentModifiesCtrl && !parentTrans.isSymmetry()
                    && !parentTrans.getEvent().conflicts(sourceEvent)) {
                    GraphState sibling = parentTrans.target();
                    GraphTransitionStub siblingOut =
                        sibling.getOutStub(sourceEvent);
                    if (siblingOut != null) {
                        transition =
                            createTransition(source, match,
                                siblingOut.getTarget(sibling),
                                siblingOut.isSymmetry());
                        confluentDiamondCount++;
                    }
                }
            }
        }
        if (transition == null) {
            GraphNextState freshTarget = createState(source, ctrlTrans, match);
            addStateReporter.start();
            GraphState isoTarget = getGTS().addState(freshTarget);
            addStateReporter.stop();
            if (isoTarget == null) {
                transition = freshTarget;
            } else {
                transition =
                    new DefaultGraphTransition(source, match.getEvent(),
                        freshTarget.getAddedNodes(), isoTarget, true);
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
    private GraphNextState createState(GraphState source,
            CtrlTransition ctrlTrans, MatchResult match) {
        HostNode[] addedNodes;
        HostNode[] boundNodes;
        RuleEvent event = match.getEvent();
        if (reuseCreatedNodes(source, match)) {
            GraphTransition parentOut = (GraphTransition) match;
            addedNodes = parentOut.getAddedNodes();
        } else if (event.getRule().hasNodeCreators()) {
            RuleEffect record =
                new RuleEffect(source.getGraph(), Fragment.NODE_CREATION);
            event.recordEffect(record);
            addedNodes = record.getCreatedNodeArray();
        } else {
            addedNodes = EMPTY_NODE_ARRAY;
        }
        if (ctrlTrans.target().getBoundVars().isEmpty()) {
            boundNodes = EMPTY_NODE_ARRAY;
        } else {
            RuleEffect record = new RuleEffect(addedNodes, Fragment.NODE_ALL);
            event.recordEffect(record);
            boundNodes = computeBoundNodes(source, event, ctrlTrans, record);
        }
        assert boundNodes.length == ctrlTrans.getTargetVarBinding().length;
        return new DefaultGraphNextState(this.gts.nodeCount(),
            (AbstractGraphState) source, event, addedNodes, boundNodes);
    }

    /**
     * Creates a fresh graph transition, based on a given rule event and source
     * and target state. A final parameter determines if the target state is
     * directly derived from the source, or modulo a symmetry.
     */
    private GraphTransition createTransition(GraphState source,
            MatchResult match, GraphState target, boolean symmetry) {
        HostNode[] addedNodes;
        RuleEvent event = match.getEvent();
        if (reuseCreatedNodes(source, match)) {
            GraphTransition parentOut = (GraphTransition) match;
            addedNodes = parentOut.getAddedNodes();
        } else {
            RuleEffect record =
                new RuleEffect(source.getGraph(), Fragment.NODE_CREATION);
            event.recordEffect(record);
            addedNodes =
                record.hasCreatedNodes() ? record.getCreatedNodeArray()
                        : EMPTY_NODE_ARRAY;
        }
        return new DefaultGraphTransition(source, event, addedNodes, target,
            symmetry);
    }

    /**
     * Indicates if the created nodes in a given match can be reused
     * as created nodes for a new target graph.
     */
    private boolean reuseCreatedNodes(GraphState source, MatchResult match) {
        if (!(match instanceof GraphTransition)) {
            return false;
        }
        if (!(source instanceof GraphNextState)) {
            return false;
        }
        HostNode[] addedNodes = ((GraphTransition) match).getAddedNodes();
        if (addedNodes == null || addedNodes.length == 0) {
            return true;
        }
        RuleEvent sourceEvent = ((GraphNextState) source).getEvent();
        if (sourceEvent instanceof CompositeEvent) {
            return false;
        }
        RuleEvent matchEvent = match.getEvent();
        if (match instanceof CompositeEvent) {
            return false;
        }
        return sourceEvent != matchEvent;
    }

    private HostNode[] computeBoundNodes(GraphState source, RuleEvent event,
            CtrlTransition ctrlTrans, RuleEffect record) {
        HostNode[] result;
        int[] varBinding = ctrlTrans.getTargetVarBinding();
        int valueCount = varBinding.length;
        result = new HostNode[valueCount];
        HostNode[] parentValues = source.getBoundNodes();
        Rule rule = event.getRule();
        int anchorNodeCount = rule.getAnchorNodes().length;
        HostNode[] createdNodes = record.getCreatedNodeArray();
        Set<HostNode> erasedNodes = record.getErasedNodes();
        MergeMap mergeMap = record.getMergeMap();
        for (int i = 0; i < valueCount; i++) {
            int fromI = varBinding[i];
            HostNode value;
            if (fromI >= parentValues.length) {
                // this is an output parameter of the rule
                int binding = rule.getParBinding(fromI - parentValues.length);
                if (binding < anchorNodeCount) {
                    // the parameter is not a creator node
                    HostNode sourceValue =
                        (HostNode) event.getAnchorImage(binding);
                    value = getNodeImage(sourceValue, mergeMap, erasedNodes);
                } else {
                    // the parameter is a creator node
                    value = createdNodes[binding - anchorNodeCount];
                }
            } else {
                // this is an input parameter of the rule
                HostNode sourceValue = parentValues[fromI];
                value = getNodeImage(sourceValue, mergeMap, erasedNodes);
            }
            result[i] = value;
        }
        return result;
    }

    private HostNode getNodeImage(HostNode key, MergeMap mergeMap,
            Set<HostNode> erasedNodes) {
        if (mergeMap == null) {
            if (erasedNodes == null || !erasedNodes.contains(key)) {
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
    static private final Reporter reporter =
        Reporter.register(MatchApplier.class);
    /** Profiling aid for adding states. */
    static public final Reporter addStateReporter =
        reporter.register("addState");
    /** Profiling aid for adding transitions. */
    static public final Reporter addTransitionReporter =
        reporter.register("addTransition");
}
