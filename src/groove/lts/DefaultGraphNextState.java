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
 */
package groove.lts;

import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.graph.DeltaApplier;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.DeltaHostGraph;
import groove.trans.HostNode;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPORule;

/**
 * 
 * @author Arend
 * @version $Revision$
 */
public class DefaultGraphNextState extends AbstractGraphState implements
        GraphNextState, GraphTransitionStub {
    /**
     * Constructs a successor state on the basis of a given parent state and
     * rule application, and a given control location.
     */
    public DefaultGraphNextState(AbstractGraphState source, RuleEvent event,
            HostNode[] addedNodes) {
        super(source.getCacheReference());
        this.source = source;
        this.event = event;
        this.addedNodes = addedNodes;
        CtrlState sourceCtrlState = source.getCtrlState();
        if (sourceCtrlState == null) {
            this.boundNodes = null;
        } else {
            CtrlTransition ctrlTrans =
                sourceCtrlState.getTransition(event.getRule());
            setCtrlState(ctrlTrans.target());
            this.boundNodes = computeBoundNodes(ctrlTrans);
        }
    }

    public RuleEvent getEvent() {
        return this.event;
    }

    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    @Override
    public HostNode[] getBoundNodes() {
        return this.boundNodes;
    }

    private HostNode[] computeBoundNodes(CtrlTransition ctrlTrans) {
        HostNode[] result;
        if (getCtrlState() == null) {
            result = EMPTY_NODE_LIST;
        } else {
            int valueCount = getCtrlState().getBoundVars().size();
            result = new HostNode[valueCount];
            HostNode[] parentValues = this.source.getBoundNodes();
            int[] varBinding = ctrlTrans.getTargetVarBinding();
            SPORule rule = ((SPORule) getEvent().getRule());
            int anchorSize = getEvent().getAnchorSize();
            MergeMap mergeMap = getEvent().getMergeMap();
            for (int i = 0; i < valueCount; i++) {
                int fromI = varBinding[i];
                HostNode value;
                if (fromI >= parentValues.length) {
                    int binding =
                        rule.getParBinding(fromI - parentValues.length);
                    if (binding < anchorSize) {
                        value =
                            (HostNode) mergeMap.getNode((Node) getEvent().getAnchorImage(
                                binding));
                    } else {
                        value = getAddedNodes()[binding - anchorSize];
                    }
                } else {
                    value = (HostNode) mergeMap.getNode(parentValues[fromI]);
                }
                result[i] = value;
            }
        }
        return result;
    }

    /**
     * This implementation reconstructs the matching using the rule, the anchor
     * images, and the basis graph.
     */
    public RuleMatch getMatch() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    public NodeEdgeMap getMorphism() {
        RuleApplication appl =
            new DefaultApplication(getEvent(), source().getGraph(), getGraph(),
                getAddedNodes());
        //        Graph derivedTarget = appl.getTarget();
        //        Graph realTarget = target().getGraph();
        //        if (derivedTarget.edgeSet().equals(realTarget.edgeSet())
        //            && derivedTarget.nodeSet().equals(realTarget.nodeSet())) {
        return appl.getMorphism();
        //        } else {
        //            Morphism iso = derivedTarget.getIsomorphismTo(realTarget);
        //            assert iso != null : "Can't reconstruct derivation from graph transition "
        //                + this
        //                + ": \n"
        //                + AbstractGraphShape.toString(derivedTarget)
        //                + " and \n"
        //                + AbstractGraphShape.toString(realTarget)
        //                + " \nnot isomorphic";
        //            return appl.getMorphism().then(iso);
        //        }
    }

    /**
     * This implementation returns the rule name.
     */
    public Label label() {
        return new DerivationLabel(getEvent(), this.addedNodes);
    }

    @Override
    public boolean isNodeType() {
        return false;
    }

    @Override
    public boolean isFlag() {
        return false;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    /**
     * Returns the basis graph of the delta graph (which is guaranteed to be a
     * {@link GraphState}).
     */
    public AbstractGraphState source() {
        return this.source;
    }

    /**
     * This implementation returns <code>this</code>.
     */
    public DefaultGraphNextState target() {
        return this;
    }

    public RuleEvent getEvent(GraphState source) {
        if (source == source()) {
            return getEvent();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            return getSourceEvent();
        }
    }

    public HostNode[] getAddedNodes(GraphState source) {
        if (source == source()) {
            return getAddedNodes();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            return getSourceAddedNodes();
        }
    }

    public boolean isSymmetry() {
        return false;
    }

    public GraphTransitionStub toStub() {
        return this;
    }

    /**
     * This implementation returns a {@link DefaultGraphTransition} with
     * {@link #getSourceEvent()} if <code>source</code> does not equal
     * {@link #source()}, otherwise it returns <code>this</code>.
     */
    public GraphTransition toTransition(GraphState source) {
        if (source != source()) {
            return new DefaultGraphTransition(getSourceEvent(),
                getSourceAddedNodes(), source, this, isSymmetry());
        } else {
            return this;
        }
    }

    /**
     * When a {@link DefaultGraphNextState} is used as a graph transition
     * stub, the state itself is always the target state.
     */
    public GraphState getTarget(GraphState source) {
        return this;
    }

    @Override
    public DeltaHostGraph getGraph() {
        return getCache().getGraph();
    }

    /**
     * Returns the delta applier associated with the rule application leading up
     * to this state.
     */
    public DeltaApplier getDelta() {
        return getCache().getDelta();
    }

    @Override
    protected void updateClosed() {
        //        clearCache();
    }

    /**
     * Returns the event from the source of this transition, if that is itself a
     * {@link groove.lts.GraphTransitionStub}.
     */
    protected RuleEvent getSourceEvent() {
        if (source() instanceof GraphNextState) {
            return ((GraphNextState) source()).getEvent();
        } else {
            return null;
        }
    }

    /**
     * Returns the event from the source of this transition, if that is itself a
     * {@link groove.lts.GraphTransitionStub}.
     */
    protected HostNode[] getSourceAddedNodes() {
        if (source() instanceof GraphNextState) {
            return ((GraphNextState) source()).getAddedNodes();
        } else {
            return null;
        }
    }

    /**
     * This implementation compares the state on the basis of its qualities as a
     * {@link GraphTransition}. That is, two objects are considered equal if
     * they have the same source and event.
     * @see #equalsTransition(GraphTransition)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return (obj instanceof GraphTransition)
                && equalsTransition((GraphTransition) obj);
        }
    }

    /**
     * This implementation compares the source and event of another
     * {@link GraphTransition} to those of this object.
     */
    protected boolean equalsTransition(GraphTransition other) {
        return source() == other.source() && getEvent() == other.getEvent();
    }

    /**
     * This implementation combines the identities of source and event.
     */
    @Override
    public int hashCode() {
        return source().getNumber() + getEvent().identityHashCode();
    }

    /**
     * This implementation returns <code>this</code> if the derivation's event
     * is identical to the event stored in this state. Otherwise it invokes
     * <code>super</code>.
     */
    @Override
    protected GraphTransitionStub createInTransitionStub(GraphState source,
            RuleEvent event, HostNode[] addedNodes) {
        if (source == source() && event == getEvent()) {
            return this;
        } else if (source != source() && event == getSourceEvent()) {
            return this;
        } else {
            return super.createInTransitionStub(source, event, addedNodes);
        }
    }

    public CtrlTransition getCtrlTransition() {
        CtrlState sourceCtrlState = source().getCtrlState();
        return sourceCtrlState == null ? null
                : sourceCtrlState.getTransition(getEvent().getRule());
    }

    /** Keeps track of bound variables */
    private HostNode[] boundNodes;
    /**
     * The rule of the incoming transition with which this state was created.
     */
    private final AbstractGraphState source;
    /**
     * The rule event of the incoming transition with which this state was
     * created.
     */
    private final RuleEvent event;
    /**
     * The identities of the nodes added with respect to the source state.
     */
    private final HostNode[] addedNodes;
}