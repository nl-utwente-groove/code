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

import groove.graph.TypeLabel;
import groove.match.rete.ReteNetwork.ReteState.ReteUpdateMode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.RegExpr.Wildcard.LabelConstraint;
import groove.trans.HostEdge;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleLabel;
import groove.util.Reporter;
import groove.util.TreeHashSet;

import java.util.List;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class EdgeCheckerNode extends ReteNetworkNode implements StateSubscriber {

    private RuleElement[] pattern = new RuleElement[1];

    /**
     * This is where incoming edges are buffered lazily
     * when the RETE network is working in on-demand mode.
     */
    private TreeHashSet<HostEdge> ondemandBuffer = new TreeHashSet<HostEdge>();

    /**
     * The reporter.
     */
    protected static final Reporter reporter =
        Reporter.register(EdgeCheckerNode.class);

    /**
     * For collecting reports on the number of time the 
     * {@link #receiveEdge(ReteNetworkNode, HostEdge, Action)} method is called.
     */
    protected static final Reporter receiveEdgeReporter =
        reporter.register("receiveEdge(source, gEdge, action)");

    /**
     * Creates an new edge-checker n-node that matches a certain kind of edge.
     * 
     * @param e The edge that is to be used as a sample edge that this edge-checker 
     *          must accept matches for.
     */
    public EdgeCheckerNode(ReteNetwork network, RuleEdge e) {
        super(network);
        this.pattern[0] = e;
        //This is just to fill up the lookup table
        getPatternLookupTable();
        this.getOwner().getState().subscribe(this);
    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
        boolean isValid =
            (nnode instanceof SubgraphCheckerNode)
                || (nnode instanceof ConditionChecker)
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
        RuleEdge e1 = this.getEdge();
        //condition 1: labels must match <-- commented out because we check this in the root
        //condition 2: if this is an edge checker for a loop then e should also be a loop
        assert (this.isWildcardEdge() && e1.label().getMatchExpr().getWildcardGuard().isSatisfied(
            e.label()))
            || (e1.label().text().equals(e.label().text()));
        return (!e1.source().equals(e1.target()) || (e.source().equals(e.target())));
    }

    /**
     * @return <code>true</code> if this edge-checker is checking for
     * wild-card edges.
     */
    public boolean isWildcardEdge() {
        return this.getEdge().label().isWildcard();
    }

    /**
     * @return <code>true</code> if this edge checker is a wild-card edge checker
     * and if the wildcard is positive.
     */
    public boolean isPositiveWildcard() {
        LabelConstraint lc =
            ((RegExpr.Wildcard) this.getEdge().label().getMatchExpr()).getGuard();
        return this.isWildcardEdge() && (lc == null || !lc.isNegated());
    }

    /**
     * @return <code>true</code> if this edge checker is a guarded wild-card edge checker
     *
     */
    public boolean isWildcardGuarded() {
        return this.isWildcardEdge()
            && (((RegExpr.Wildcard) this.getEdge().label().getMatchExpr()).getGuard() != null)
            && (((RegExpr.Wildcard) this.getEdge().label().getMatchExpr()).getGuard().getLabels() != null);
    }

    /**
     * @return <code>true</code> if this edge-checker accepts 
     * the given label (either by exact matching or through wild-card matching)
     */
    public boolean isAcceptingLabel(TypeLabel l) {
        RuleLabel rl = this.getEdge().label();
        return (this.isWildcardEdge() && rl.getWildcardGuard().isSatisfied(l))
            || rl.text().equals(l.text());
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
        RuleEdge e1 = this.getEdge();
        //condition 1: labels must match
        //condition 2: if this is an edge checker for a loop then e should also be a loop and vice versa
        return e1.label().equals(e.label())
            && (e1.source().equals(e1.target()) == (e.source().equals(e.target())));
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
    public void receiveEdge(ReteNetworkNode source, HostEdge gEdge,
            Action action) {
        receiveEdgeReporter.start();
        if (this.canBeMappedToEdge(gEdge)) {
            if (!this.getOwner().isInOnDemandMode()) {
                sendDownReceivedEdge(gEdge, action);
            } else if ((action == Action.REMOVE)
                && !this.ondemandBuffer.contains(gEdge)) {
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

    @SuppressWarnings("rawtypes")
    private void sendDownReceivedEdge(HostEdge gEdge, Action action) {

        ReteNetworkNode previous = null;
        int repeatedSuccessorIndex = 0;
        LabelVar variable;
        if (this.isWildcardEdge()
            && ((variable = this.getEdge().label().getWildcardId()) != null)) {
            for (ReteNetworkNode n : this.getSuccessors()) {
                repeatedSuccessorIndex =
                    (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
                if (n instanceof SubgraphCheckerNode) {
                    ((SubgraphCheckerNode) n).receiveBoundEdge(this,
                        repeatedSuccessorIndex, gEdge, variable, action);
                } else if (n instanceof ConditionChecker) {
                    ((ConditionChecker) n).receiveBoundEdge(gEdge, variable,
                        action);
                } else if (n instanceof DisconnectedSubgraphChecker) {
                    ((DisconnectedSubgraphChecker) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                }
                previous = n;
            }
        } else {
            for (ReteNetworkNode n : this.getSuccessors()) {
                repeatedSuccessorIndex =
                    (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
                if (n instanceof SubgraphCheckerNode) {
                    ((SubgraphCheckerNode) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                } else if (n instanceof ConditionChecker) {
                    ((ConditionChecker) n).receive(gEdge, action);
                } else if (n instanceof DisconnectedSubgraphChecker) {
                    ((DisconnectedSubgraphChecker) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                }
                previous = n;
            }
        }
    }

    /**
     * @return the edge associated with this edge-checker
     */
    public RuleEdge getEdge() {
        return (RuleEdge) this.pattern[0];
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
        return "Checking edge: " + this.getEdge().toString();
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    public boolean demandUpdate() {
        boolean result = this.ondemandBuffer.size() > 0;
        if (!this.isUpToDate()) {
            if (this.getOwner().isInOnDemandMode()) {
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

    }

    @Override
    public List<? extends Object> initialize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int demandOneMatch() {
        int result = this.ondemandBuffer.size();
        if (this.getOwner().isInOnDemandMode()) {
            if (!this.isUpToDate() && (result > 0)) {
                HostEdge e = this.ondemandBuffer.iterator().next();
                this.ondemandBuffer.remove(e);
                sendDownReceivedEdge(e, Action.ADD);
                setUpToDate(this.ondemandBuffer.size() == 0);
                result = 1;
            }
        }
        return result;
    }

    @Override
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        throw new UnsupportedOperationException();
    }
}
