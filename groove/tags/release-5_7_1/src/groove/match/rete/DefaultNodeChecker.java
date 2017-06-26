/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleNode;
import groove.grammar.type.TypeNode;
import groove.graph.Node;
import groove.util.Reporter;
import groove.util.collect.TreeHashSet;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class DefaultNodeChecker extends NodeChecker implements ReteStateSubscriber {

    private HostNodeSet ondemandBuffer = new HostNodeSet();

    /**
     * Node-checker nodes now have a memory of
     * matches produced. This is necessary to
     * bring the triggering of domino-deletion
     * inside the node-checker rather than
     * relegating it to subgraph checkers.
     */
    private TreeHashSet<ReteSimpleMatch> memory = new TreeHashSet<>();

    /** Flag indicating if the node type has non-trivial subtypes. */
    final boolean sharpType;
    /** The type label to be matched. */
    final TypeNode type;

    /**
     * The reporter object for this class.
     */
    protected static final Reporter reporter = Reporter.register(NodeChecker.class);

    /**
     * The reporter collecting statistics for the {@link #receiveNode} method.
     */
    protected static final Reporter receiveNodeReporter =
        reporter.register("receiveNode(node, action)");

    /**
     * @param network The {@link ReteNetwork} object to which this node will belong.
     */
    public DefaultNodeChecker(ReteNetwork network, RuleNode node) {
        super(network);
        this.pattern[0] = node;
        this.type = node.getType();
        this.sharpType = node.isSharp() || this.type.getSubtypes()
            .size() == 1;
        this.getOwner()
            .getState()
            .subscribe(this);
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
        if (checkType(node)) {
            if (!this.getOwner()
                .isInOnDemandMode()) {
                sendDownReceivedNode(node, action);
            } else if ((action == Action.REMOVE) && !this.ondemandBuffer.contains(node)) {
                sendDownReceivedNode(node, action);
            } else {
                bufferReceivedNode(node, action);
            }
        }
        receiveNodeReporter.stop();
    }

    /** Tests if the type of a given node matches the type of
     * the node checker. */
    private boolean checkType(HostNode image) {
        return this.type.subsumes(image.getType(), this.sharpType);
    }

    private void sendDownReceivedNode(HostNode node, Action action) {

        ReteSimpleMatch m = new ReteSimpleMatch(this, node, this.getOwner()
            .isInjective());

        if (action == Action.ADD) {
            assert!this.memory.contains(m);
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
        return (node != null) && (node instanceof DefaultNodeChecker)
            && (((DefaultNodeChecker) node).getType()
                .equals(this.getType()));
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
            if (this.getOwner()
                .isInOnDemandMode()) {
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
                HostNode n = this.ondemandBuffer.iterator()
                    .next();
                this.ondemandBuffer.remove(n);
                sendDownReceivedNode(n, Action.ADD);
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

    @Override
    public boolean canBeStaticallyMappedTo(RuleNode node) {
        return this.getNode()
            .getType() == node.getType();
    }

    /**
     * Returns the type of node that this checker will check against
     */
    public TypeNode getType() {
        return this.type;
    }

}
