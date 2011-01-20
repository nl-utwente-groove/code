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

import groove.graph.DefaultNode;
import groove.graph.Node;
import groove.trans.HostNode;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.util.Reporter;
import groove.util.TreeHashSet;

import java.util.List;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class NodeCheckerNode extends ReteNetworkNode implements StateSubscriber {

    private RuleElement[] pattern = new RuleElement[1];
    private TreeHashSet<HostNode> ondemandBuffer = new TreeHashSet<HostNode>();

    /**
     * The reporter object for this class.
     */
    protected static final Reporter reporter =
        Reporter.register(NodeCheckerNode.class);

    /**
     * The reporter collecting statistics for the {@link #receiveNode} method.
     */
    protected static final Reporter receiveNodeReporter =
        reporter.register("receiveNode(node, action)");

    /**
     * @param network The {@link ReteNetwork} object to which this node will belong.
     */
    public NodeCheckerNode(ReteNetwork network) {
        super(network);
        this.pattern[0] =
            RuleFactory.instance().createNode(
                RuleFactory.instance().getMaxNodeNr() + 1);
        this.getOwner().getState().subscribe(this);
    }

    /**
     * Each object of type {@link NodeCheckerNode} has an associated {@link DefaultNode} object 
     * that will represent and binds to isolated nodes of rules'
     * LHS during build-time and dynamically to all nodes of a host graph
     * (either injectively or non-injectively).
     * 
     * @return the node object associated with this checker
     */
    public DefaultNode getNode() {
        return (DefaultNode) this.pattern[0];
    }

    /**
     * This method is called by the ROOT of the RETE network whenever a new {@link Node}
     * is added or removed to/from the host graph.
     * 
     * @param node The node in host graph that has been added or removed.
     * @param action Determines if the given <code>node</code> has been added or removed.
     */
    public void receiveNode(HostNode node, Action action) {
        receiveNodeReporter.start();
        if (!this.getOwner().isInOnDemandMode()) {
            sendDownReceivedNode(node, action);
        } else if ((action == Action.REMOVE)
            && !this.ondemandBuffer.contains(node)) {
            sendDownReceivedNode(node, action);
        } else {
            bufferReceivedNode(node, action);
        }
        receiveNodeReporter.stop();
    }

    private void sendDownReceivedNode(HostNode node, Action action) {
        ReteNetworkNode previous = null;
        int repeatedSuccessorIndex = 0;
        for (ReteNetworkNode n : getSuccessors()) {
            repeatedSuccessorIndex =
                (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
            if (n instanceof ConditionChecker) {
                ((ConditionChecker) n).receive(node, action);
            } else if (n instanceof SubgraphCheckerNode) {
                ((SubgraphCheckerNode) n).receive(this, repeatedSuccessorIndex,
                    node, action);
            } else if (n instanceof DisconnectedSubgraphChecker) {
                ((DisconnectedSubgraphChecker) n).receive(this,
                    repeatedSuccessorIndex, node, action);
            }
            previous = n;
        }
    }

    private void bufferReceivedNode(HostNode node, Action action) {
        if (action == Action.REMOVE) {
            this.ondemandBuffer.remove(node);
        } else {
            this.ondemandBuffer.add(node);
            this.invalidate();
        }
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (node != null) && (node instanceof NodeCheckerNode)
            && (((NodeCheckerNode) node).getNode().equals(this.getNode()));
    }

    /**
     * For node-checkers the value of size is always zero
     * 
     * This is a construction-time method only.  
     */
    @Override
    public int size() {
        return this.pattern.length;
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
                for (HostNode n : this.ondemandBuffer) {
                    sendDownReceivedNode(n, Action.ADD);
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
                HostNode n = this.ondemandBuffer.iterator().next();
                this.ondemandBuffer.remove(n);
                sendDownReceivedNode(n, Action.ADD);
                setUpToDate(this.ondemandBuffer.size() == 0);
                result = 1;
            }
        }
        return result;
    }
}
