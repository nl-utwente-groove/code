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

import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.Location;
import groove.graph.DeltaApplier;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.trans.DefaultApplication;
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
            Node[] addedNodes, Location control) {
        super(source.getCacheReference(), control);
        this.source = source;
        this.event = event;
        this.addedNodes = addedNodes;
        if (source.getLocation() != null) {
            initializeVariables();
        }
    }

    /**
     * Initializes the variables for this state based on the previous state and
     * the transition taken to get here.
     */
    private void initializeVariables() {
        MergeMap nodeMap = this.event.getMergeMap();
        ControlTransition transition = this.getControlTransition();

        if (this.getLocation() == null) {
            this.setLocation(transition.target());
        }

        AbstractGraphState src = this.source;
        // if src has parameters, we need to apply the morphism to them
        if (src.hasParameters()) {
            Node[] parameters = src.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Node targetNode = nodeMap.getNode(parameters[i]);
                // the node could be deleted by this rule
                if (targetNode != null
                    && transition.target().isInitialized(
                        transition.source().getVariableName(i))) {
                    // we need to find the position this variable has in the current state
                    String name =
                        ((ControlState) src.getLocation()).getVariableName(i);
                    int newPosition =
                        ((ControlState) this.getLocation()).getVariablePosition(name);
                    this.setParameter(newPosition, targetNode);
                }
            }
        }

        // if transition has output parameters, we need to apply them
        if (transition.hasOutputParameters()) {
            RuleMatch match = getMatch();
            String[] output = transition.getOutputParameters();
            SPORule rule = (SPORule) getEvent().getRule();
            for (int i = 0; i < output.length; i++) {
                if (output[i] != null && !output[i].equals("_")) {
                    int creator = -1;
                    int position =
                        ((ControlState) this.getLocation()).getVariablePosition(output[i]);
                    Node targetValue;
                    if ((creator = rule.isCreatorParameter(i + 1)) != -1) {
                        targetValue = this.addedNodes[creator];
                    } else {
                        Node sourceValue =
                            match.getElementMap().getNode(
                                rule.getParameter(i + 1));
                        targetValue = nodeMap.getNode(sourceValue);
                    }
                    this.setParameter(position, targetValue);
                }
            }
        }
    }

    public RuleEvent getEvent() {
        return this.event;
    }

    public Node[] getAddedNodes() {
        return this.addedNodes;
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
    public Morphism getMorphism() {
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
        return getEvent().getLabel();
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

    public Node opposite() {
        return target();
    }

    /**
     * Returns <code>getBasis()</code> or <code>this</code>, depending on
     * the index.
     */
    public AbstractGraphState end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return source();
        case TARGET_INDEX:
            return target();
        default:
            throw new IllegalArgumentException("End index " + i + " not valid");
        }
    }

    /**
     * @return {@link #END_COUNT}.
     */
    public int endCount() {
        return END_COUNT;
    }

    public int endIndex(Node node) {
        if (source().equals(node)) {
            return SOURCE_INDEX;
        } else if (target().equals(node)) {
            return TARGET_INDEX;
        } else {
            throw new IllegalArgumentException("Node " + node
                + " is not an end state of this transition");
        }
    }

    public Node[] ends() {
        return new Node[] {source(), target()};
    }

    public boolean hasEnd(Node node) {
        return source().equals(node) || target().equals(node);
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

    public Node[] getAddedNodes(GraphState source) {
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
    public Graph getGraph() {
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
    protected Node[] getSourceAddedNodes() {
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
            RuleEvent event, Node[] addedNodes) {
        if (source == source() && event == getEvent()) {
            return this;
        } else if (source != source() && event == getSourceEvent()) {
            return this;
        } else {
            return super.createInTransitionStub(source, event, addedNodes);
        }
    }

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
    private final Node[] addedNodes;

    /**
     * Returns the ControlTransition with which this transition is associated
     * @return the ControlTransition with which this transition is associated, 
     * or null if no control is present
     */
    public ControlTransition getControlTransition() {
        if (this.source.getLocation() != null) {
            return ((ControlState) this.source.getLocation()).getTransition(this.event.getRule());
        } else {
            return null;
        }
    }

    /**
     * Returns whether this transition is a self transition (graph does not change,
     * ControlLocation does not change, no output parameters)
     * @return whether this transition is a self transition
     */
    public boolean isSelfTransition() {
        boolean retval = !this.event.getRule().isModifying();
        if (retval && this.source.getLocation() != null) {
            retval &=
                this.source.getLocation() == this.getControlTransition().target();
            retval &= this.getControlTransition().hasOutputParameters();
        }
        return retval;
    }
}