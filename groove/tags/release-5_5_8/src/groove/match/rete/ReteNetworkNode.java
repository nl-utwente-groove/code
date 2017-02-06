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

import groove.grammar.rule.RuleElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the basic behavior of an n-node
 * in a RETE network. 
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class ReteNetworkNode {

    /**
     * collection of successors of an n-node. 
     */
    private List<ReteNetworkNode> successors;

    private ReteNetwork owner;
    private ArrayList<ReteNetworkNode> antecedents =
        new ArrayList<>();

    /**
     * A look up table that allows fast querying about the 
     * pattern that an n-node finds matches for.
     */
    private LookupTable patternLookupTable;

    /**
     * Flag indicating if this n-node's run-time state
     * is up to date or has been invalidated due to a
     * lazily deferred update at some higher level in the RETE
     * network in the on-demand mode.
     * 
     * The value of this field is only meaningful if 
     * {@link ReteNetwork#isInOnDemandMode()} returns <code>true</code>.
     */
    private boolean isUpToDate = true;

    /**
     * @return The pattern of elements this n-node finds matches for.
     */
    public abstract RuleElement[] getPattern();

    /**
     * @return The fast lookup table for the pattern.
     */
    public LookupTable getPatternLookupTable() {
        if ((this.patternLookupTable == null) && (this.getPattern() != null)) {
            this.patternLookupTable = new LookupTable(this);
        }
        return this.patternLookupTable;
    }

    /**
     * 
     */
    public ReteNetworkNode(ReteNetwork network) {
        this.owner = network;
        this.successors = new ArrayList<>();
    }

    /**
     * Methods overriding this method call this method back after making sure the
     * parameter <code>suc</code> is of a type allowed to be the successor
     * of this n-node;      
     * @param suc the successor node to be added to the list of the successors of this. 
     */
    public void addSuccessor(ReteNetworkNode suc) {
        getSuccessors().add(suc);
    }

    /**
     * @return the successor nodes of this RETE n-node 
     */
    public List<ReteNetworkNode> getSuccessors() {
        return this.successors;
    }

    /**
     * Descendants should check if the given n-node is
     * semantically the same as this node. This is particularly
     * useful when the RETE network is being built to avoid
     * adding the same checker node twice.
     * 
     * @param node The node with which equality is to be tested.
     * @return <code>true</code> if the nodes are equal, <code>false</code> otherwise.
     */
    public abstract boolean equals(ReteNetworkNode node);

    /**
     * The descendants should implement this method by
     * returning the number of edges in the subgraph represented by 
     * this checker node. 
     * 
     * @return size of the graph component checked by this node or zero otherwise.
     */
    public abstract int size();

    /**
     * Causes an n-node to bring it run-time state up to date by pulling down
     * any unpropagated matches from antecedents. This will also cause
     * this n-node to send these new updates to successors.
     * 
     * @return <code>true</code> if the update request has resulted in any
     * new matches to be created, <code>false</code> otherwise. The result will
     * also be <code>false</code> the RETE network is not in on-demand 
     * update propagation mode. See {@link ReteNetwork#isInOnDemandMode()} 
     */
    public abstract boolean demandUpdate();

    /**
     * When called, this method will try to send down one match
     * to its successors. There is however no guarantee that the
     * actual number of matches sent down is equal to one. 
     * 
     * @return The actual number of matches found and sent down. If the
     * n-node cannot tell exactly how many match have actually been produced 
     * (probably because it does not buffer anything itself) the return value 
     * should be 1 if it has produced any matches and 0 otherwise.
     */
    public abstract int demandOneMatch();

    /**
     * This method is called by an n-node's antecedent telling it
     * to send down all its lazily kept matches to its successor and 
     * force them to propagate their updates as well. 
     */
    public void forceFlush() {
        demandUpdate();
        for (ReteNetworkNode nnode : this.getSuccessors()) {
            nnode.forceFlush();
        }
    }

    /**
     * @param nnode A given n-node
     * @return <code>true</code> if the given n-node is a successor of this
     * n-node.
     */
    protected boolean isAlreadySuccessor(ReteNetworkNode nnode) {
        boolean result = false;
        for (ReteNetworkNode n : this.getSuccessors()) {
            result = n.equals(nnode);
            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * 
     * @return The RETE network to which this n-node belongs.
     */
    public ReteNetwork getOwner() {
        return this.owner;
    }

    /**
     * @return The list of this n-node's antecedents. The order is important
     * to those n-nodes that performing some sort of match merging, such as the
     * subgraph-checker. 
     */
    public List<ReteNetworkNode> getAntecedents() {
        return this.antecedents;
    }

    /**
     * Adds a backward reference from this node to its antecedent 
     * @param nnode The antecedent
     */
    public void addAntecedent(ReteNetworkNode nnode) {
        this.antecedents.add(nnode);
    }

    /**
     * Determines if this n-node's run-time state
     * is up to date or has been invalidated due to a
     * lazily deferred update at some higher level in the RETE
     * network in the on-demand mode.
     * 
     * The return value is meaningful only if the owner RETE
     * network is in on-demand mode. See {@link ReteNetwork#isInOnDemandMode()}.
     */
    public boolean isUpToDate() {
        return this.isUpToDate;
    }

    /**
     * Instructs this n-node that it should consider its run-time state
     * out of date.
     *
     * @return The old value of it's up-to-date flag prior to the call to this
     * method.
     */
    public boolean invalidate() {
        boolean result = this.isUpToDate;
        this.isUpToDate = false;
        if (result) {
            for (ReteNetworkNode suc : this.getSuccessors()) {
                suc.invalidate();
            }
        }
        return result;
    }

    /**
     * Sets the new value of the up-to-date flag.
     *  
     * @param newValue The new value for the flag.
     * @return The old value of the up-to-date flag.
     */
    protected boolean setUpToDate(boolean newValue) {
        boolean result = this.isUpToDate;
        this.isUpToDate = newValue;
        return result;
    }

    /**
     * Receives a match from its antecedent.
     *  
     * @param source The reference to the antecedent n-node
     * @param repeatIndex The repeat index if the n-node represented by <code>source</code> 
     *                    occurs more than once in the list of antecedents. 
     * @param match       The match passed down from the <code>source</code>
     */
    public abstract void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch match);

    /**
     * Passes down a given match to the successors.
     * @param m the given match
     */
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        ReteNetworkNode previous = null;
        int repeatedSuccessorIndex = 0;

        for (ReteNetworkNode n : this.getSuccessors()) {
            repeatedSuccessorIndex =
                (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
            n.receive(this, repeatedSuccessorIndex, m);
            previous = n;
        }

    }

    /**
     * The represents the mode in which a RETE network should operate under
     * in run-time when it is fed with edges of a graph.  
     * @author Arash Jalali
     * @version $Revision $
     */
    public static enum Action {
        /**
         * <code>ADD</code> means edges fed into the RETE network must be
         * added to the conflict set.
         */
        ADD,
        /**
        * <code>REMOVE</code> means edges fed into the RETE network must be
        * removed from the conflict set.
        */
        REMOVE
    }

}
