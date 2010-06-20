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
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class Materialisation {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private Shape shape;
    private RuleMatch preMatch;
    private NodeEdgeMap absElems;
    private NodeEdgeMap match;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Materialisation(Shape shape, RuleMatch preMatch) {
        this.shape = shape;
        this.preMatch = preMatch;
        this.absElems = getAbstractMatchedElems(shape, preMatch);
        this.match = preMatch.getElementMap().clone();
    }

    /** Copying constructor. */
    private Materialisation(Materialisation mat) {
        this.shape = mat.shape.clone();
        this.preMatch = mat.preMatch;
        this.absElems = mat.absElems;
        this.match = mat.match.clone();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.shape.toString() + this.preMatch.toString() + "\n"
            + this.absElems.toString();
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    private static NodeEdgeMap getAbstractMatchedElems(Shape shape,
            RuleMatch preMatch) {
        NodeEdgeMap elemsToMat = new NodeEdgeHashMap();
        NodeEdgeMap originalMap = preMatch.getElementMap();

        // Check the node images.
        for (Entry<Node,Node> nodeEntry : originalMap.nodeMap().entrySet()) {
            ShapeNode nodeS = (ShapeNode) nodeEntry.getValue();
            if (shape.getNodeMult(nodeS).isAbstract()) {
                // We have a node in the rule that was matched to an abstract node.
                elemsToMat.putNode(nodeEntry.getKey(), nodeS);
            }
        }

        // Check the edge images.
        for (Entry<Edge,Edge> edgeEntry : originalMap.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            ShapeEdge edgeS = (ShapeEdge) edgeEntry.getValue();
            if (elemsToMat.nodeMap().containsKey(edgeR.source())
                || elemsToMat.nodeMap().containsKey(edgeR.opposite())) {
                if (!Util.isUnary(edgeR)) {
                    elemsToMat.putEdge(edgeR, edgeS);
                }
            }
        }

        return elemsToMat;
    }

    /** EDUARDO */
    public static Set<Materialisation> getMaterialisations(Shape shape,
            RuleMatch preMatch) {
        Set<Materialisation> result = new HashSet<Materialisation>();

        // Clone the given shape to avoid aliasing and unwanted modifications.
        Shape shapeClone = shape.clone();
        // We are going to materialise elements in the cloned shape, so set
        // the original shape as the graph from which the clone was created
        // and create an identity morphism between the elements of the clone
        // and of the original. This morphism will be later updated and when
        // the materialisation is done it will be the shaping morphism.
        shapeClone.setShapeAndCreateIdentityMorphism(shape);

        // Initial materialisation object.
        Materialisation initialMat = new Materialisation(shapeClone, preMatch);

        Set<Materialisation> possibleMats =
            initialMat.materialiseNodesAndExtendPreMatch();

        for (Materialisation possibleMat : possibleMats) {
            result.addAll(possibleMat.finishMaterialisation());
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private Set<Materialisation> materialiseNodesAndExtendPreMatch() {
        Set<Materialisation> result = new HashSet<Materialisation>();

        // Compute how many copies of each of the abstract nodes we need to
        // materialise.
        Set<Pair<ShapeNode,Integer>> nodesToMat =
            new HashSet<Pair<ShapeNode,Integer>>();
        for (Node nodeS : this.absElems.nodeMap().values()) {
            Set<Node> nodesR = Util.getReverseNodeMap(this.absElems, nodeS);
            Integer nodesSize = nodesR.size();
            nodesToMat.add(new Pair<ShapeNode,Integer>((ShapeNode) nodeS,
                nodesSize));
        }

        // Materialise all nodes and get the new multiplicity set back.
        Set<Pair<ShapeNode,Set<Multiplicity>>> origNodesAndMults =
            this.shape.materialiseNodes(nodesToMat);

        // By materialising nodes we actually want a set of materialisations.
        // Construct this set now.
        Set<Materialisation> newMats = new HashSet<Materialisation>();
        // For all original node that materialised one or more copies.
        for (Pair<ShapeNode,Set<Multiplicity>> pair : origNodesAndMults) {
            ShapeNode origNode = pair.first();
            Set<Multiplicity> mults = pair.second();
            // For all multiplicities that the original node needs to have.
            for (Multiplicity mult : mults) {
                // Create a new materialisation...
                Materialisation newMat = this.clone();
                // ...and properly adjust the multiplicity of the original node.
                newMat.shape.setNodeMult(origNode, mult);
                newMats.add(newMat);
            }
        }

        // Now we need to expand the pre-match that is shared by all newMats.
        for (Materialisation newMat : newMats) {
            for (Node nodeR : newMat.absElems.nodeMap().keySet()) {
                // nodeR was matched in the pre-match to an abstract node.
                ShapeNode origNode =
                    (ShapeNode) newMat.absElems.nodeMap().get(nodeR);
                // Through the shaping morphism in the shape of the
                // materialisation we can get the materialised nodes.
                Set<Node> newNodes =
                    Util.getReverseNodeMap(newMat.shape.getNodeShaping(),
                        origNode);
                // Remove the original node since it will not help extend
                // the match.
                newNodes.remove(origNode);
                if (newNodes.size() == 1) {
                    ShapeNode nodeS = (ShapeNode) newNodes.iterator().next();
                    newMat.extendMatch(nodeR, nodeS);
                    result.add(newMat);
                } else {
                    // For each possibility to match the rule node, we
                    // create a new materialisation object.
                    for (Node newNode : newNodes) {
                        ShapeNode nodeS = (ShapeNode) newNode;
                        // Sorry, running out of meaningful names... :P 
                        Materialisation spankingNewMat = newMat.clone();
                        spankingNewMat.extendMatch(nodeR, nodeS);
                        result.add(spankingNewMat);
                    }
                }
            }
        }

        return result;
    }

    private void extendMatch(Node nodeR, ShapeNode nodeS) {
        this.match.putNode(nodeR, nodeS);
        // Look for all edges where nodeR occurs and update the edge map.
        for (Entry<Edge,Edge> edgeEntry : this.match.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            ShapeEdge origEdgeS = (ShapeEdge) edgeEntry.getValue();
            // Start with the nodes from the original edge.
            ShapeNode srcS = origEdgeS.source();
            ShapeNode tgtS = origEdgeS.opposite();

            boolean modifyMatch = false;
            if (edgeR.source().equals(nodeR)) {
                srcS = nodeS;
                modifyMatch = true;
            }
            if (edgeR.opposite().equals(nodeR)) {
                tgtS = nodeS;
                modifyMatch = true;
            }

            if (modifyMatch) {
                // Get the new edge from the shape.
                // Variables srcS and tgtS were already properly updated.
                ShapeEdge newEdgeS =
                    this.shape.getShapeEdge(srcS, origEdgeS.label(), tgtS);
                this.match.putEdge(edgeR, newEdgeS);
            }
        }
    }

    private Set<Materialisation> finishMaterialisation() {
        Set<Materialisation> result = new HashSet<Materialisation>();

        // At this point we assume that all variations on the nodes were
        // resolved, and that the rule match is complete and fixed.
        // Now, we go through the matched edges of the rule and adjust the 
        // shared edge multiplicities.

        for (Edge origEdge : this.absElems.edgeMap().values()) {
            ShapeEdge origEdgeS = (ShapeEdge) origEdge;
            // Through the shaping morphism in the shape of the
            // materialisation we can get the materialised edges.
            Set<Edge> newEdges =
                Util.getReverseEdgeMap(this.shape.getEdgeShaping(), origEdge);
            // Remove the original edge since it will not help extend
            // the match.
            newEdges.remove(origEdgeS);

            Multiplicity toSub = Multiplicity.getMultOf(newEdges.size());

            Set<Materialisation> newMats = new HashSet<Materialisation>();

            // Outgoing edges.
            EdgeSignature outEs = this.shape.getEdgeOutSignature(origEdgeS);
            Multiplicity origEdgeOutMult = this.shape.getEdgeSigOutMult(outEs);
            Set<Multiplicity> outMults = origEdgeOutMult.subEdgeMult(toSub);
            // For all multiplicities that the original edge needs to have.
            for (Multiplicity outMult : outMults) {
                // Create a new materialisation...
                Materialisation newMat;
                if (outMults.size() == 1) {
                    newMat = this;
                } else {
                    newMat = this.clone();
                }
                if (outMult.isPositive()) {
                    // ...and properly adjust the multiplicity of the edge signature.
                    newMat.shape.setEdgeOutMult(outEs, outMult);
                } else {
                    // ...remove the impossible edges from the shape.
                    newMat.shape.removeImpossibleOutEdges(outEs, newEdges);
                }
                newMats.add(newMat);
            }

            // Incoming edges.
            for (Materialisation newMat : newMats) {
                EdgeSignature inEs = this.shape.getEdgeInSignature(origEdgeS);
                Multiplicity origEdgeInMult = this.shape.getEdgeSigInMult(inEs);
                Set<Multiplicity> inMults = origEdgeInMult.subEdgeMult(toSub);
                // For all multiplicities that the original edge needs to have.
                for (Multiplicity inMult : inMults) {
                    // Create a new materialisation...
                    // Sorry, running out of meaningful names... :P 
                    Materialisation spankingNewMat;
                    if (outMults.size() == 1) {
                        spankingNewMat = newMat;
                    } else {
                        spankingNewMat = newMat.clone();
                    }
                    if (inMult.isPositive()) {
                        // ...and properly adjust the multiplicity of the edge signature.
                        spankingNewMat.shape.setEdgeInMult(inEs, inMult);
                    } else {
                        // ...remove the impossible edges from the shape.
                        spankingNewMat.shape.removeImpossibleInEdges(inEs,
                            newEdges);
                    }
                    result.add(spankingNewMat);
                }
            }
        }

        return result;
    }

    /** EDUARDO */
    public Shape applyMatch() {
        // EDUARDO : Implement this.
        return null;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testMaterialisation0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    System.out.println(mat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Unit test. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testMaterialisation0();
    }

}
