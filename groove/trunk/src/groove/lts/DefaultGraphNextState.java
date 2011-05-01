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
import groove.graph.EdgeRole;
import groove.trans.AbstractEvent;
import groove.trans.RuleApplication;
import groove.trans.DeltaApplier;
import groove.trans.DeltaHostGraph;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.MergeMap;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.Proof;
import groove.view.FormatException;

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
     * @param number the number of the state; required to be positive
     */
    public DefaultGraphNextState(int number, AbstractGraphState source,
            RuleEvent event, HostNode[] addedNodes) {
        super(source.getCacheReference(), number);
        this.source = source;
        this.event = event;
        this.addedNodes = addedNodes;
        CtrlState sourceCtrlState = source.getCtrlState();
        CtrlTransition ctrlTrans =
            sourceCtrlState.getTransition(event.getRule());
        setCtrlState(ctrlTrans.target());
        this.boundNodes = computeBoundNodes(ctrlTrans);
    }

    public RuleEvent getEvent() {
        return this.event;
    }

    @Override
    public String getOutputString() throws FormatException {
        return ((AbstractEvent<?,?>) getEvent()).getOutputString(getAddedNodes());
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
            Rule rule = getEvent().getRule();
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
                            mergeMap.getNode((HostNode) getEvent().getAnchorImage(
                                binding));
                    } else {
                        value = getAddedNodes()[binding - anchorSize];
                    }
                } else {
                    value = mergeMap.getNode(parentValues[fromI]);
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
    public Proof getMatch() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    public HostGraphMorphism getMorphism() {
        RuleApplication appl =
            new RuleApplication(getEvent(), source().getGraph(), getGraph(),
                getAddedNodes());
        return appl.getMorphism();
    }

    /**
     * This implementation returns the rule name.
     */
    public DerivationLabel label() {
        return new DerivationLabel(getEvent(), this.addedNodes);
    }

    @Override
    public EdgeRole getRole() {
        if (getEvent().getRule().isModifying()
            || getCtrlTransition().isModifying()) {
            return EdgeRole.BINARY;
        } else {
            return EdgeRole.FLAG;
        }
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