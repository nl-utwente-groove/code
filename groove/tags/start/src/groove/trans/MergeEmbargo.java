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
 * $Id: MergeEmbargo.java,v 1.1.1.2 2007-03-20 10:42:56 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Node;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;

/**
 * A specialised NAC that enforces an injectivity constraint.
 * Merge embargoes are treated separately to allow performance optimisation
 * by early enforcement (while searching for matchings of the enclosing graph condition).
 * A merge embargo may itself not have negative conditions.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class MergeEmbargo extends DefaultGraphCondition implements NAC, GraphCondition {
    /**
     * Constructs a merge embargo on a given graph, between two given nodes.
     * @param source the graph on which this embargo works
     * @param node1 the first of the nodes that may not be merged
     * @param node2 the second of the nodes that may not me merged
     * @require <tt>source.contains(node1) && source.contains(node2)</tt>
     * @ensure <tt>node1().equals(node1) && node2().equals(node2)</tt>
     */
    public MergeEmbargo(VarGraph source, Node node1, Node node2, RuleFactory ruleFactory) {
        super(source, (VarGraph) source.newGraph(), ruleFactory);
        this.node1 = node1;
        this.node2 = node2;
        Node codNode = cod().addNode();
        putNode(node1, codNode);
        putNode(node2, codNode);
    }
    
    /**
     * Constructs a merge embargo on a given graph, between the endpoints in a given
     * array of length 2.
     * @param source the context for this embargo 
     * @param embargoNodes the nodes that should be matched injectively
     * @require <code>embargoEdge.endCount() == 2</code>
     */
    public MergeEmbargo(VarGraph source, Node[] embargoNodes, RuleFactory ruleFactory) {
        this(source, embargoNodes[0], embargoNodes[1], ruleFactory);
        if (embargoNodes.length != 2) {
            throw new IllegalArgumentException("Merge embargo must be binary");
        }
    }

    /**
     * Overwrites the method for efficienty: just an injectivity test is required.
     */
    public boolean forbids(VarMorphism match) {
        reporter.start(MERGE_FORBID);
        boolean result = match.getNode(node1).equals(match.getNode(node2));
        reporter.stop();
        return result;
    }

    /**
     * Returns the first of the nodes whose merging this embargo forbids.
     * @ensure <tt>result != null</tt>
     */
    public Node node1() {
        return node1;
    }

    /**
     * Returns the second of the nodes whose merging this embargo forbids.
     * @ensure <tt>result != null</tt> 
     */
    public Node node2() {
        return node2;
    }

    /**
     * Returns the nodes that are to be matched injectively in an array.
     * @see #node1()
     * @see #node2()
     */
    public Node[] getNodes() {
        return new Node[] { node1, node2 };
    }

    /** First node whose merging this embargo forbids. */
    private final Node node1;
    /** Second node whose merging this embargo forbids. */
    private final Node node2;
    
    private final static int MERGE_FORBID = reporter.newMethod("MergeEmbargo.forbids(Morphism");
}