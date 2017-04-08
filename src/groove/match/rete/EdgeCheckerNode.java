/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.match.rete;

import java.util.List;

import groove.algebra.Constant;
import groove.automaton.RegExpr;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeNode;
import groove.match.rete.ReteNetwork.ReteState.ReteUpdateMode;
import groove.util.Reporter;
import groove.util.collect.TreeHashSet;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class EdgeCheckerNode extends ReteNetworkNode implements ReteStateSubscriber {

    private RuleElement[] pattern = new RuleElement[1];

    /**
     * This is where incoming edges are buffered lazily
     * when the RETE network is working in on-demand mode.
     */
    private HostEdgeSet ondemandBuffer = new HostEdgeSet();

    /**
     * Edge-checker nodes now have a memory of
     * matches produced. This is necessary to
     * bring the triggering of domino-deletion
     * inside the edge-checker rather than
     * relegating it to subgraph checkers.
     */
    private TreeHashSet<ReteSimpleMatch> memory = new TreeHashSet<>();

    /**
     * The reporter.
     */
    protected static final Reporter reporter = Reporter.register(EdgeCheckerNode.class);

    /**
     * For collecting reports on the number of time the
     * {@link #receiveEdge(ReteNetworkNode, HostEdge, Action)} method is called.
     */
    protected static final Reporter receiveEdgeReporter =
        reporter.register("receiveEdge(source, gEdge, action)");

    /**
     * Determines if this edge checker strictly checks against loop edges.
     */
    protected boolean selfEdge = false;

    /**
     * The type of the edge this checker will enforce during checking.
     */
    protected TypeEdge type;

    /**
     * The type of the source node that will be enforced on incoming edge
     * matches. If it is null then it means this checker will not
     * enforce any typing on the source of the incoming edges.
     */
    protected TypeNode sourceType;

    /**
     * The type of the target node that will be enforced on incoming edge
     * matches. If it is null then it means this checker will not
     * enforce any typing on the target of the incoming edges.
     */
    protected TypeNode targetType;

    /**
     * Direct reference to the edge representing the pattern of this
     * edge-checker. This is equivalent to (RuleEdge)pattern[0] and is
     * redundantly stored like this for performance reasons.
     */
    protected RuleEdge edge;

    /**
     * Direct reference to the source of the edge for performance reasons.
     * Equivalent to {@link #getEdge()}.{@link RuleEdge#source()}.
     */
    protected RuleNode sourceNode;

    /**
     * Direct reference to the target of the edge for performance reasons.
     * Equivalent to {@link #getEdge()}.{@link RuleEdge#target()}.
     */
    protected RuleNode targetNode;

    /**
     * Creates an new edge-checker n-node that matches a certain kind of edge.
     *
     * @param e The edge that is to be used as a sample edge that this edge-checker
     *          must accept matches for.
     */
    public EdgeCheckerNode(ReteNetwork network, RuleEdge e) {
        super(network);
        this.edge = e;
        this.pattern[0] = e;

        this.type = e.getType();
        this.sourceType = e.source()
            .isSharp() || this.type == null
            || e.source()
                .getType() != this.type.source()
                    ? e.source()
                        .getType()
                    : null;

        this.targetType = e.target()
            .isSharp() || this.type == null
            || e.target()
                .getType() != this.type.target()
                    ? e.target()
                        .getType()
                    : null;
        this.selfEdge = e.source()
            .equals(e.target());
        this.sourceNode = e.source();
        this.targetNode = e.target();

        //This is just to fill up the lookup table
        getPatternLookupTable();
        this.getOwner()
            .getState()
            .subscribe(this);

    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
        boolean isValid =
            (nnode instanceof SubgraphCheckerNode) || (nnode instanceof ConditionChecker)
                || (nnode instanceof DisconnectedSubgraphChecker);

        assert isValid;
        if (isValid) {
            super.addSuccessor(nnode);
        }
    }

    /**
     * Determines if this n-edge-checker node could potentially be mapped to a given graph edge.     *
     * This routine no longer checks the edge-labels for compatibility and assumes that
     * the given edge in the parameter <code>e</code> has the same label as the pattern of
     * this edge-checker. This is because the ROOT is made responsible for sending only
     * those edges that have the same label as this edge-checker's associated pattern.
     *
     * @param e the given graph edge
     * @return <code>true</code> if the given edge show be handed over to this
     *          edge-checker by the root, <code>false</code> otherwise.
     */
    public boolean canBeMappedToEdge(HostEdge e) {

        //condition 1: node types for end-points of the patter and host edge must be compatible
        //condition 2: labels must match <-- commented out because we check this in the root
        //condition 3: if this is an edge checker for a loop then e should also be a loop
        assert isWildcardEdge() ? this.edge.label()
            .getMatchExpr()
            .getWildcardGuard()
            .isSatisfied(e.getType())
            : e.getType()
                .equals(this.edge.getType());

        return (this.type == null || this.type.subsumes(e.getType())) && checkSourceType(e.source())
            && checkTargetType(e.target()) && checkValues(this.sourceNode, e.source())
            && checkValues(this.targetNode, e.target()) && (!this.selfEdge || (e.source()
                .equals(e.target())));
    }

    /** Tests if a given host edge source type matches the source type of
     * the pattern for the edge checker. */
    boolean checkSourceType(HostNode imageSource) {
        return this.sourceType == null
            || this.sourceType.subsumes(imageSource.getType(), this.sourceNode.isSharp());
    }

    /** Tests if a given host edge target type matches the target type of
     * the pattern for the edge checker. */
    boolean checkTargetType(HostNode imageTarget) {
        return this.targetType == null
            || this.targetType.subsumes(imageTarget.getType(), this.targetNode.isSharp());
    }

    private boolean checkValues(RuleNode n1, HostNode n2) {
        return !(n1 instanceof VariableNode)
            || (((n2 instanceof ValueNode) && (((ValueNode) n2).getSort()
                .equals(((VariableNode) n1).getSort())))
                && valuesMatch((VariableNode) n1, (ValueNode) n2));

    }

    private boolean valuesMatch(VariableNode n1, ValueNode n2) {
        assert n2.getSort()
            .equals((n2.getSort()));
        Constant c = n1.getConstant();
        return (c == null) || (c.equals(n2.getTerm()));
    }

    /**
     * @return <code>true</code> if this edge-checker is checking for
     * wild-card edges.
     */
    public boolean isWildcardEdge() {
        return this.edge.label()
            .isWildcard();
    }

    /**
     * @return <code>true</code> if this edge checker is a wild-card edge checker
     * and if the wildcard is positive.
     */
    public boolean isPositiveWildcard() {
        TypeGuard lc = ((RegExpr.Wildcard) this.edge.label()
            .getMatchExpr()).getGuard();
        return this.isWildcardEdge() && (lc == null || !lc.isNegated());
    }

    /**
     * @return <code>true</code> if this edge checker is a guarded wild-card edge checker
     *
     */
    public boolean isWildcardGuarded() {
        return this.isWildcardEdge() && (((RegExpr.Wildcard) this.edge.label()
            .getMatchExpr()).getGuard() != null)
            && (((RegExpr.Wildcard) this.edge.label()
                .getMatchExpr()).getGuard()
                    .getLabels() != null);
    }

    /**
     * @return <code>true</code> if this edge-checker accepts
     * the given label (either by exact matching or through wild-card matching)
     */
    public boolean isAcceptingLabel(TypeElement e) {
        RuleLabel rl = this.edge.label();
        return isWildcardEdge() ? rl.getWildcardGuard()
            .isSatisfied(e) : e.equals(this.edge.getType());
    }

    /**
     * Decides if this edge checker object can be put in charge of matching edges that
     * look like the given edge in the parameter <code>e</code>.
     *
     * @param e The LHS edge for which this edge-checker might be put in charge.
     *
     * @return <code>true</code> if this edge checker can match the LHS edge given
     *         in the parameter <code>e</code>. That is, if the labels match and
     *         they have the same shape, i.e. both the pattern of this edge-checker
     *         and <code>e</code> are loops or both are non-loops.
     */
    public boolean canBeStaticallyMappedToEdge(RuleEdge e) {
        //condition 1: labels must match
        //condition 2: if this is an edge checker for a loop then e should also be a loop and vice versa
        //condition 3: the end-points of this n-node's pattern and the given rule node are of the same type
        //condition 4: if any of the end points in the rule are constant then the values must match
        return this.edge.label()
            .equals(e.label()) //condition 1
            && (!this.selfEdge || (e.source() == e.target())) //condition 2
            && (this.sourceNode.getType() == e.source()
                .getType()) //condition 3
            && (this.targetNode.getType() == e.target()
                .getType())
            && endPointValuesStaticallyCompatible(e); //condition 4
    }

    private boolean endPointValuesStaticallyCompatible(RuleEdge e) {
        return nodeValuesStaticallyCompatible(this.sourceNode, e.source())
            && nodeValuesStaticallyCompatible(this.targetNode, e.target());
    }

    private boolean nodeValuesStaticallyCompatible(RuleNode n1, RuleNode n2) {
        boolean result = (n1 instanceof VariableNode) == (n2 instanceof VariableNode);
        if (result && (n1 instanceof VariableNode)) {
            VariableNode vn1 = (VariableNode) n1;
            VariableNode vn2 = (VariableNode) n2;
            result = vn1.getSort()
                .equals(vn2.getSort());
            if (result) {
                result = (vn1.getConstant() == null) == (vn2.getConstant() == null);
                if (result && (vn1.getConstant() != null)) {
                    result = vn1.getConstant()
                        .equals(vn2.getConstant());
                }
            }
        }
        return result;
    }

    /**
     * Receives an edge that is sent from the root and
     * sends it down the RETE network or lazily buffers it
     * depending on what update mode the RETE network is in.
     *
     * For more info on the update mode see {@link ReteUpdateMode}
     *
     * @param source the RETE node that is actually calling this method.
     * @param gEdge the edge that has been added/removed
     * @param action whether the action is ADD or remove.
     */
    public void receiveEdge(ReteNetworkNode source, HostEdge gEdge, Action action) {
        receiveEdgeReporter.start();
        if (this.canBeMappedToEdge(gEdge)) {
            if (!this.getOwner()
                .isInOnDemandMode()) {
                sendDownReceivedEdge(gEdge, action);
            } else if ((action == Action.REMOVE) && !this.ondemandBuffer.contains(gEdge)) {
                sendDownReceivedEdge(gEdge, action);
            } else {
                bufferReceivedEdge(gEdge, action);
            }
        }
        receiveEdgeReporter.stop();
    }

    private void bufferReceivedEdge(HostEdge edge, Action action) {
        if (action == Action.REMOVE) {
            this.ondemandBuffer.remove(edge);
        } else {
            this.ondemandBuffer.add(edge);
            this.invalidate();
        }
    }

    private void sendDownReceivedEdge(HostEdge gEdge, Action action) {

        LabelVar variable = this.isWildcardEdge() ? this.edge.label()
            .getWildcardGuard()
            .getVar() : null;

        ReteSimpleMatch m;
        if (variable != null) {
            m = new ReteSimpleMatch(this, gEdge, variable, this.getOwner()
                .isInjective());
        } else {
            m = new ReteSimpleMatch(this, gEdge, this.getOwner()
                .isInjective());
        }

        if (action == Action.ADD) {
            assert !this.memory.contains(m);
            this.memory.add(m);
            passDownMatchToSuccessors(m);
        } else { // action == Action.REMOVE
            if (this.memory.contains(m)) {
                ReteSimpleMatch m1 = m;
                m = this.memory.put(m);
                this.memory.remove(m1);
                if (m != null) {
                    m.dominoDelete(null);
                }
            }
        }
    }

    /**
     * @return the edge associated with this edge-checker
     */
    public RuleEdge getEdge() {
        return this.edge;
    }

    @Override
    /**
     * used by the currently processed production rules during construction time
     * and zero otherwise.
     *
     * This is a construction-time method only.
     */
    public int size() {
        return this.pattern.length;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (node != null) && (this == node);
    }

    @Override
    public String toString() {
        return "Checking edge: " + this.edge.toString();
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    public boolean demandUpdate() {
        boolean result = this.ondemandBuffer.size() > 0;
        if (!this.isUpToDate()) {
            if (this.getOwner()
                .isInOnDemandMode()) {
                for (HostEdge e : this.ondemandBuffer) {
                    sendDownReceivedEdge(e, Action.ADD);
                }
                this.ondemandBuffer.clear();
            }
            setUpToDate(true);
        }
        return result;
    }

    @Override
    public void clear() {
        this.ondemandBuffer.clear();
        this.memory.clear();

    }

    @Override
    public List<? extends Object> initialize() {
        // TODO ARASH:implement on-demand
        return null;
    }

    @Override
    public int demandOneMatch() {
        int result = this.ondemandBuffer.size();
        if (this.getOwner()
            .isInOnDemandMode()) {
            if (!this.isUpToDate() && (result > 0)) {
                HostEdge e = this.ondemandBuffer.iterator()
                    .next();
                this.ondemandBuffer.remove(e);
                sendDownReceivedEdge(e, Action.ADD);
                setUpToDate(this.ondemandBuffer.size() == 0);
                result = 1;
            }
        }
        return result;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex, AbstractReteMatch subgraph) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void updateBegin() {
        //Do nothing

    }

    @Override
    public void updateEnd() {
        //Do nothing
    }
}
