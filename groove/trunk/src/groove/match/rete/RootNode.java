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
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.RuleElement;
import groove.util.TreeHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class RootNode extends ReteNetworkNode {

    private Map<String,Set<EdgeCheckerNode>> positiveEdgeCheckers =
        new HashMap<String,Set<EdgeCheckerNode>>();

    private Set<EdgeCheckerNode> openEdgeCheckers =
        new TreeHashSet<EdgeCheckerNode>();

    private Set<EdgeCheckerNode> otherEdgeCheckers =
        new TreeHashSet<EdgeCheckerNode>();

    private Map<String,Set<SingleEdgePathChecker>> positivePathCheckers =
        new HashMap<String,Set<SingleEdgePathChecker>>();

    private Set<SingleEdgePathChecker> otherPathCheckers =
        new TreeHashSet<SingleEdgePathChecker>();

    private NodeCheckerNode theNodeChecker = null;

    /**
     * Creates a root n-node for a given RETE network.
     * @param network The given RETE network.
     */
    public RootNode(ReteNetwork network) {
        super(network);
    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
        boolean isValid =
            (nnode instanceof EdgeCheckerNode)
                || ((nnode instanceof NodeCheckerNode) && (this.theNodeChecker == null))
                || (nnode instanceof SingleEdgePathChecker)
                || (nnode instanceof EmptyPathChecker);
        assert isValid;
        /*
         * check to see if n-node is of type g-node-checker or 
         * g-edge-checker. If it is, then if it is not already there 
         * it should be added to the successors collection.         
         * if it's already there, then it should just return true;
         * if the type is no of the above two, it should fail and
         * return false
         */
        if (isValid && !isAlreadySuccessor(nnode)) {
            getSuccessors().add(nnode);
            nnode.addAntecedent(this);
            if (nnode instanceof NodeCheckerNode) {
                this.theNodeChecker = (NodeCheckerNode) nnode;
            } else if (nnode instanceof EdgeCheckerNode) {
                EdgeCheckerNode ec = (EdgeCheckerNode) nnode;
                if (!ec.isWildcardEdge()
                    || (ec.isPositiveWildcard() && ec.isWildcardGuarded())) {
                    addPositiveEdgeChecker(ec);
                } else if (ec.isWildcardEdge() && !ec.isPositiveWildcard()) {
                    this.otherEdgeCheckers.add(ec);
                } else if (ec.isWildcardEdge() && !ec.isWildcardGuarded()) {
                    this.openEdgeCheckers.add(ec);
                }
            } else if (nnode instanceof SingleEdgePathChecker) {
                SingleEdgePathChecker pathChecker =
                    (SingleEdgePathChecker) nnode;
                if (nnode instanceof AtomPathChecker) {
                    this.addPositivePathChecker((AtomPathChecker) pathChecker);
                } else {
                    this.otherPathCheckers.add(pathChecker);
                }
            }
        }

    }

    /**
     * This is the method that is to be called for each single atomic update
     * to the RETE network, i.e. a single node creation/removal.
     * 
     * @param elem The node that is added or deleted from the host graph.
     * @param action Determined if the given node is deleted or added.
     */
    public void receiveNode(HostNode elem, Action action) {
        if (this.theNodeChecker != null) {
            this.theNodeChecker.receiveNode(elem, action);
        }
    }

    /**
     * This is the method that is to be called for each single atomic update
     * to the RETE network, i.e. a single edge creation/removal.
     * 
     * @param elem The edge that is added or deleted from the host graph.
     * @param action Determined if the given edge is deleted or added.
     */
    public void receiveEdge(HostEdge elem, Action action) {
        Set<EdgeCheckerNode> edgeCheckers =
            this.positiveEdgeCheckers.get(elem.label().text());
        if (edgeCheckers != null) {

            for (EdgeCheckerNode ec : edgeCheckers) {
                assert ec.isAcceptingLabel(elem.label());
                ec.receiveEdge(this, elem, action);
            }
        }

        for (EdgeCheckerNode ec : this.openEdgeCheckers) {
            ec.receiveEdge(this, elem, action);
        }
        for (EdgeCheckerNode ec : this.otherEdgeCheckers) {
            if (ec.isAcceptingLabel(elem.label())) {
                ec.receiveEdge(this, elem, action);
            }
        }

        Set<SingleEdgePathChecker> pathCheckers =
            this.positivePathCheckers.get(elem.label().text());
        if (pathCheckers != null) {
            for (SingleEdgePathChecker pc : pathCheckers) {
                pc.receive(this, elem, action);
            }
        }
        for (SingleEdgePathChecker pc : this.otherPathCheckers) {
            pc.receive(this, elem, action);
        }
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        //There should be only one Root node
        return this == node;
    }

    /**
     * For root the value of size is irrelevant. By fiat, we set its size
     * to be -1.
     * 
     * This is a construction-time method only.  
     */
    @Override
    public int size() {
        return -1;
    }

    @Override
    public RuleElement[] getPattern() {
        // this method is not supposed to be called
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean demandUpdate() {
        return true;
    }

    @Override
    public boolean isUpToDate() {
        // this method is not supposed to be called
        throw new UnsupportedOperationException();
    }

    @Override
    public int demandOneMatch() {
        // this method is not supposed to be called
        throw new UnsupportedOperationException();
    }

    @Override
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        throw new UnsupportedOperationException();
    }

    private void addPositiveEdgeChecker(EdgeCheckerNode edgeChecker) {
        if (!edgeChecker.isWildcardEdge()) {
            String atomLabel = edgeChecker.getEdge().label().text();
            Set<EdgeCheckerNode> s = this.positiveEdgeCheckers.get(atomLabel);
            if (s == null) {
                s = new TreeHashSet<EdgeCheckerNode>();
                this.positiveEdgeCheckers.put(atomLabel, s);
            }
            s.add(edgeChecker);
        } else {
            assert edgeChecker.isPositiveWildcard()
                && edgeChecker.isWildcardGuarded();
            Set<TypeLabel> labels =
                edgeChecker.getEdge().label().getMatchExpr().getTypeLabels();
            for (TypeLabel l : labels) {
                String atomLabel = l.text();
                Set<EdgeCheckerNode> s =
                    this.positiveEdgeCheckers.get(atomLabel);
                if (s == null) {
                    s = new TreeHashSet<EdgeCheckerNode>();
                    this.positiveEdgeCheckers.put(atomLabel, s);
                }
                s.add(edgeChecker);
            }
        }
    }

    private void addPositivePathChecker(AtomPathChecker pathChecker) {

        String atomLabel = pathChecker.getExpression().getAtomText();
        assert atomLabel != null;
        Set<SingleEdgePathChecker> s = this.positivePathCheckers.get(atomLabel);
        if (s == null) {
            s = new TreeHashSet<SingleEdgePathChecker>();
            this.positivePathCheckers.put(atomLabel, s);
        }
        s.add(pathChecker);
    }
}
