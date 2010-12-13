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
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleLabel;
import groove.trans.RuleMatch;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A pre-match is a match of the left-hand side of a rule into the graph
 * structure of a shape. See Def. 35 on page 21 of the Technical Report for
 * more information. 
 *  
 * This class is only a collection of static methods and therefore should not
 * be instantiated. Stupid packaging system of Java... >:(
 * 
 * @author Eduardo Zambon
 */
public final class PreMatch {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private PreMatch() {
        // We make the constructor private to prevent the creation of objects
        // of this class.
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Computes and returns the valid pre-matches of a rule into a shape.
     * The given host must be a shape.
     */
    public static Set<RuleMatch> getPreMatches(GraphShape host, Rule rule) {
        assert host instanceof Shape : "Cannot use abstract methods on non-abstract graphs.";
        Shape shape = (Shape) host;
        Set<RuleMatch> preMatches = new HashSet<RuleMatch>();
        // We use the normal matching algorithms for finding matches.
        for (RuleMatch match : rule.getMatches(shape, null)) {
            if (isValidPreMatch(host, match)) {
                // We have a pre-match.
                preMatches.add(match);
            }
        }
        return preMatches;
    }

    /**
     * Returns true if the given match in the host is a valid pre-match.
     * A pre-match is valid if the non-injective matching of the LHS
     * respects node multiplicities.
     */
    public static boolean isValidPreMatch(HostGraph host, RuleEvent event) {
        return isValidPreMatch(host, event.getMatch(host));
    }

    /**
     * Returns true if the given match in the host is a valid pre-match.
     * A pre-match is valid if the non-injective matching of the LHS
     * respects node multiplicities.
     */
    public static boolean isValidPreMatch(GraphShape host, RuleMatch match) {
        assert host instanceof Shape : "Cannot use abstract methods on non-abstract graphs.";

        Shape shape = (Shape) host;
        RuleToHostMap map = match.getElementMap();

        // Since we have non-injective matching of the LHS of the rule
        // we need to check if the multiplicities are respected. 

        // Check node multiplicities.
        boolean complyToNodeMult = true;
        // For all nodes in the image of the LHS.
        // FIXME this will repeat needlessly for all multiple images
        for (Node node : map.nodeMap().values()) {
            ShapeNode nodeS = (ShapeNode) node;
            Multiplicity nSMult = shape.getNodeMult(nodeS);
            Set<RuleNode> nodesG = Util.getReverseNodeMap(map, nodeS);
            if (!Multiplicity.getNodeSetMult(nodesG).isAtMost(nSMult)) {
                // Violation of node multiplicity.
                complyToNodeMult = false;
                break;
            }
        }

        // Check edge multiplicities.
        boolean complyToEdgeMult = true;

        // EZ says: the snippet of code commented below comes from the
        // definition of pre-matching (see the technical report: 
        // "Graph Abstraction and Abstract Graph Transformation", page 21,
        // definition 35). However, item 2 of the definition is wrong since
        // it excludes valid pre-matches.

        /*if (complyToNodeMult) {
            // For all edges in the image of the LHS.
            for (Edge edge : map.edgeMap().values()) {
                ShapeEdge edgeS = (ShapeEdge) edge;
                Set<Edge> edgesG = Util.getReverseEdgeMap(map, edgeS);
                Multiplicity eGMult =
                    Multiplicity.getMult(edgesG.size(),
                        Parameters.getEdgeMultBound());

                // Outgoing multiplicities.
                if (!eGMult.isAtMost(shape.getEdgeOutMult(edgeS))) {
                    // Violation of edge out multiplicity.
                    complyToEdgeMult = false;
                    break;
                }

                // Incoming multiplicities.
                if (!eGMult.isAtMost(shape.getEdgeInMult(edgeS))) {
                    // Violation of edge out multiplicity.
                    complyToEdgeMult = false;
                    break;
                }
            }
        }*/

        // EZ says: here is the correct version of item 2. It is split in
        // two items: 2' and 2''. See the updated version of the report.

        if (complyToNodeMult) {
            Graph lhs = match.getRule().lhs();
            // For all binary labels.
            outerLoop: for (TypeLabel label : Util.binaryLabelSet(shape)) {
                // For all nodes of the LHS.
                for (Entry<RuleNode,HostNode> entry : map.nodeMap().entrySet()) {
                    RuleNode v = entry.getKey();
                    ShapeNode pV = (ShapeNode) entry.getValue();

                    // For all outgoing edges from the image of v. Item 2'.
                    for (Edge edge : Util.getOutEdges(shape, pV, label)) {
                        ShapeEdge e = (ShapeEdge) edge;
                        ShapeNode w = e.target();
                        Set<RuleNode> pInvW = Util.getReverseNodeMap(map, w);
                        // FIXME label is of the wrong type:
                        // it is a DefaultLabel whereas in the rule you
                        // have RuleLabels
                        RuleLabel ruleLabel = new RuleLabel(label);
                        Set<RuleEdge> vInterPInvW =
                            Util.<RuleNode,RuleEdge>getIntersectEdges(lhs, v,
                                pInvW, ruleLabel);
                        Multiplicity leftMult =
                            Multiplicity.getEdgeSetMult(vInterPInvW);

                        EquivClass<ShapeNode> ecW = shape.getEquivClassOf(w);
                        EdgeSignature es =
                            shape.getEdgeSignature(pV, label, ecW);
                        Multiplicity rightMult = shape.getEdgeSigOutMult(es);

                        if (!leftMult.isAtMost(rightMult)) {
                            complyToEdgeMult = false;
                            break outerLoop;
                        }
                    }

                    // For all incoming edges from the image of v. Item 2''.
                    for (Edge edge : Util.getInEdges(shape, pV, label)) {
                        ShapeEdge e = (ShapeEdge) edge;
                        ShapeNode w = e.source();
                        Set<RuleNode> pInvW = Util.getReverseNodeMap(map, w);
                        // FIXME label is of the wrong type:
                        // it is a DefaultLabel whereas in the rule you
                        // have RuleLabels
                        RuleLabel ruleLabel = new RuleLabel(label);
                        Set<RuleEdge> pInvWInterV =
                            Util.getIntersectEdges(lhs, pInvW, v, ruleLabel);
                        Multiplicity leftMult =
                            Multiplicity.getEdgeSetMult(pInvWInterV);

                        EquivClass<ShapeNode> ecW = shape.getEquivClassOf(w);
                        EdgeSignature es =
                            shape.getEdgeSignature(pV, label, ecW);
                        Multiplicity rightMult = shape.getEdgeSigInMult(es);

                        if (!leftMult.isAtMost(rightMult)) {
                            complyToEdgeMult = false;
                            break outerLoop;
                        }
                    }
                }
            }
        }

        return complyToNodeMult && complyToEdgeMult;
    }

}
