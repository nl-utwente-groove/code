/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
import groove.graph.Element;
import groove.graph.Node;
import groove.util.Reporter;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class NodeCheckerNode extends ReteNetworkNode {

    private Element[] pattern = new Element[1];

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
        this.pattern[0] = DefaultNode.createNode();
    }

    /**
     * Each object of type {@link NodeCheckerNode} has an associated {@link Node} object 
     * that will represent and binds to to isolated nodes of rules'
     * LHS during build-time and dynamically to all nodes of a host graph
     * (either injectively or non-injectively).
     * 
     * @return the node object associated with this checker
     */
    public Node getNode() {
        return (Node) this.pattern[0];
    }

    /**
     * This method is called by the ROOT of the RETE network whenever a new {@link Node}
     * is added or removed to/from the host graph.
     * 
     * @param node The node in host graph that has been added or removed.
     * @param action Determines if the given <code>node</code> has been added or removed.
     */
    public void receiveNode(Node node, Action action) {
        receiveNodeReporter.start();
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
        receiveNodeReporter.stop();
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
    public Element[] getPattern() {
        return this.pattern;
    }

}
