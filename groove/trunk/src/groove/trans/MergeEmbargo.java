// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: MergeEmbargo.java,v 1.10 2008-01-30 09:32:35 iovka Exp $
 */
package groove.trans;

import groove.graph.LabelStore;

/**
 * A specialised NAC that enforces an injectivity constraint. Merge embargoes
 * are treated separately to allow performance optimisation by early enforcement
 * (while searching for matchings of the enclosing graph condition). A merge
 * embargo may itself not have negative conditions.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MergeEmbargo extends NotCondition {
    /**
     * Constructs a merge embargo on a given graph, between two given nodes.
     * @param source the graph on which this embargo works
     * @param node1 the first of the nodes that may not be merged
     * @param node2 the second of the nodes that may not me merged
     * @param labelStore label store specifying the subtype relation
     * @require <tt>source.contains(node1) && source.contains(node2)</tt>
     * @ensure <tt>node1().equals(node1) && node2().equals(node2)</tt>
     */
    public MergeEmbargo(RuleGraph source, RuleNode node1, RuleNode node2,
            LabelStore labelStore, SystemProperties properties) {
        super(source.newGraph(), properties, labelStore);
        this.node1 = node1;
        this.node2 = node2;
        RuleNode codNode = getTarget().addNode();
        getRootMap().putNode(node1, codNode);
        getRootMap().putNode(node2, codNode);
    }

    /**
     * Returns the first of the nodes whose merging this embargo forbids.
     * @ensure <tt>result != null</tt>
     */
    public RuleNode node1() {
        return this.node1;
    }

    /**
     * Returns the second of the nodes whose merging this embargo forbids.
     * @ensure <tt>result != null</tt>
     */
    public RuleNode node2() {
        return this.node2;
    }

    /**
     * Returns the nodes that are to be matched injectively in an array.
     * @see #node1()
     * @see #node2()
     */
    public RuleNode[] getNodes() {
        return new RuleNode[] {this.node1, this.node2};
    }

    /** First node whose merging this embargo forbids. */
    private final RuleNode node1;
    /** Second node whose merging this embargo forbids. */
    private final RuleNode node2;
}