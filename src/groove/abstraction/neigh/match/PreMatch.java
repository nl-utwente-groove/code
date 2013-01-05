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
package groove.abstraction.neigh.match;

import groove.abstraction.Multiplicity;
import groove.abstraction.MyHashSet;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.EdgeSignatureStore;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.neigh.trans.RuleToShapeMap;
import groove.grammar.Rule;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.match.MatcherFactory;
import groove.match.SearchEngine.SearchMode;
import groove.transform.Proof;
import groove.transform.RuleEvent;
import groove.util.Property;
import groove.util.Visitor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A pre-match is a match of the left-hand side of a rule into the graph
 * structure of a shape. See Def. 35 on page 21 of the Technical Report for
 * more information. 
 *  
 * This class is only a collection of static methods and therefore should not
 * be instantiated.
 * 
 * @author Eduardo Zambon
 */
public final class PreMatch {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private PreMatch() {
        // We make the constructor private to prevent creation of objects
        // of this class.
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Computes and returns the valid pre-matches of a rule into a shape.
     * The given host must be a shape.
     */
    public static Set<Proof> getPreMatches(final Shape shape, Rule rule) {
        assert shape.getTypeGraph() == rule.getTypeGraph();

        // Make sure that the search engine is set to minimal mode. This is
        // needed when we have rules with NACs.
        MatcherFactory.instance().setEngine(SearchMode.MINIMAL);

        Set<Proof> preMatches = new MyHashSet<Proof>();
        // We use the normal matching algorithms for finding matches.
        rule.traverseMatches(shape, null,
            Visitor.newCollector(preMatches, new Property<Proof>() {
                @Override
                public boolean isSatisfied(Proof value) {
                    return isValidPreMatch(shape, value);
                }
            }));

        return preMatches;
    }

    /**
     * Returns true if the given match in the host is a valid pre-match.
     * A pre-match is valid if the non-injective matching of the LHS
     * respects node multiplicities.
     */
    public static boolean isValidPreMatch(Shape host, RuleEvent event) {
        return isValidPreMatch(host, event.getMatch(host));
    }

    /**
     * Returns true if the given match in the host is a valid pre-match.
     * A pre-match is valid if the non-injective matching of the LHS
     * respects node multiplicities.
     */
    // EZ says: This method can certainly be optimised, however from the CPU
    // profiling we saw that this is far from being a bottle neck in the
    // execution time, so we leave it as it is for now.
    public static boolean isValidPreMatch(Shape shape, Proof match) {
        RuleToShapeMap map = (RuleToShapeMap) match.getPatternMap();

        // Since we have non-injective matching of the LHS of the rule
        // we need to check if the multiplicities are respected. 

        // Check node multiplicities.
        boolean complyToNodeMult = true;
        // For all nodes in the image of the LHS.
        Map<ShapeNode,Multiplicity> nodeMultMap = shape.getNodeMultMap();
        for (ShapeNode nodeS : map.nodeMapValueSet()) {
            Multiplicity nSMult = nodeMultMap.get(nodeS);
            Set<RuleNode> nodesG = map.getPreImages(nodeS);
            if (!Multiplicity.getNodeSetMult(nodesG).le(nSMult)) {
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
        // two items: 2 and 3. See the updated version of the report.

        if (complyToNodeMult) {
            RuleGraph lhs = match.getRule().lhs();
            Set<RuleEdge> intersectEdges = new MyHashSet<RuleEdge>();
            // For all binary labels.
            EdgeSignatureStore shapeStore = shape.getEdgeSigStore();
            // For all nodes of the LHS.
            outerLoop: for (Entry<RuleNode,ShapeNode> entry : map.nodeMap().entrySet()) {
                RuleNode v = entry.getKey();
                ShapeNode pV = entry.getValue();

                // for all incoming and outgoing signatures of the node
                for (EdgeMultDir dir : EdgeMultDir.values()) {
                    Set<EdgeSignature> ess = shapeStore.getSigs(pV, dir);
                    if (ess == null) {
                        continue;
                    }
                    for (EdgeSignature es : ess) {
                        Multiplicity rightMult = shapeStore.getMult(es);
                        for (ShapeEdge e : shapeStore.getEdges(es)) {
                            ShapeNode w = dir.opposite(e);
                            Set<RuleNode> pInvW = map.getPreImages(w);
                            RuleLabel ruleLabel = new RuleLabel(e.label());
                            switch (dir) {
                            case OUTGOING:
                                Util.getIntersectEdges(lhs, v, pInvW,
                                    ruleLabel, intersectEdges);
                                break;
                            case INCOMING:
                                Util.getIntersectEdges(lhs, pInvW, v,
                                    ruleLabel, intersectEdges);
                            }
                            Multiplicity leftMult =
                                Multiplicity.getEdgeSetMult(intersectEdges);
                            if (!leftMult.le(rightMult)) {
                                complyToEdgeMult = false;
                                break outerLoop;
                            }
                        }
                    }
                }
                // AR says: the test below has been replaced by the one above 
                /*// For all outgoing edges from the image of v. Item 2.
                for (ShapeEdge e : shape.outEdgeSet(pV)) {
                    TypeLabel label = e.label();
                    if (!label.isBinary()) {
                        continue;
                    }
                    ShapeNode w = e.target();
                    Set<RuleNode> pInvW = map.getPreImages(w);
                    RuleLabel ruleLabel = new RuleLabel(label);
                    Util.getIntersectEdges(lhs, v, pInvW, ruleLabel,
                        intersectEdges);
                    Multiplicity leftMult =
                        Multiplicity.getEdgeSetMult(intersectEdges);
                
                    Multiplicity rightMult =
                        shapeStore.getMult(e, EdgeMultDir.OUTGOING);
                
                    if (!leftMult.le(rightMult)) {
                        complyToEdgeMult = false;
                        break outerLoop;
                    }
                }
                
                // For all incoming edges from the image of v. Item 3.
                for (ShapeEdge e : shape.inEdgeSet(pV)) {
                    TypeLabel label = e.label();
                    if (!label.isBinary()) {
                        continue;
                    }
                    ShapeNode w = e.source();
                    Set<RuleNode> pInvW = map.getPreImages(w);
                    RuleLabel ruleLabel = new RuleLabel(label);
                    Util.getIntersectEdges(lhs, pInvW, v, ruleLabel,
                        intersectEdges);
                    Multiplicity leftMult =
                        Multiplicity.getEdgeSetMult(intersectEdges);
                
                    Multiplicity rightMult =
                        shapeStore.getMult(e, EdgeMultDir.INCOMING);
                
                    if (!leftMult.le(rightMult)) {
                        complyToEdgeMult = false;
                        break outerLoop;
                    }
                }*/
            }
        }

        return complyToNodeMult && complyToEdgeMult;
    }

}
