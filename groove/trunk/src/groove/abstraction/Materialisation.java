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
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPOEvent;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
        this.absElems = mat.absElems.clone();
        this.match = mat.match.clone();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape.toString() + "Match: "
            + this.match.toString() + "\n";
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

        if (!initialMat.isExtensionFinished()) {
            // This is the normal case.
            result = initialMat.materialiseNodesAndExtendPreMatch();

            for (Materialisation mat : result) {
                mat.finishMaterialisation();
            }
        } else {
            // This is a corner case on which the pre-match is already concrete.
            // Nothing to do.
            result.add(initialMat);
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
        Iterator<Set<Pair<ShapeNode,Multiplicity>>> iter =
            new PairSetIterator<ShapeNode,Multiplicity>(origNodesAndMults);
        while (iter.hasNext()) {
            // This set represents a variation of a materialisation.
            Set<Pair<ShapeNode,Multiplicity>> nodesAndMultsPairs = iter.next();
            // Create a new materialisation object.
            Materialisation newMat = this.clone();
            for (Pair<ShapeNode,Multiplicity> pair : nodesAndMultsPairs) {
                // For all original node that materialised one or more copies.
                ShapeNode origNode = pair.first();
                Multiplicity mult = pair.second();
                // Properly adjust the multiplicity of the original node.
                newMat.shape.setNodeMult(origNode, mult);
            }
            // Store this new materialisation.
            newMats.add(newMat);
        }

        // Extend the pre-match of all the newly constructed materialisations.
        for (Materialisation newMat : newMats) {
            result.addAll(newMat.extendPreMatch());
        }

        return result;
    }

    private Set<Materialisation> extendPreMatch() {
        Set<Materialisation> result = new HashSet<Materialisation>();
        // Process the nodes pre-matched to abstract nodes using a queue.
        List<Materialisation> todoMats = new ArrayList<Materialisation>();
        // Add initial element to end.
        todoMats.add(this);
        while (!todoMats.isEmpty()) {
            // Remove from head.
            Materialisation mat = todoMats.remove(0);
            if (mat.isExtensionFinished()) {
                // We are done with this one.
                result.add(mat);
            } else {
                // Take the next node in the pre-match that need to be extended.
                Entry<Node,Node> entry =
                    mat.absElems.nodeMap().entrySet().iterator().next();
                Node nodeR = entry.getKey();
                ShapeNode origNodeS = (ShapeNode) entry.getValue();
                // Look in the shaping morphism to get the new nodes that were
                // materialised from the original node.
                Set<Node> newNodes =
                    Util.getReverseNodeMap(mat.shape.getNodeShaping(),
                        origNodeS);
                // Remove the original node from the set of new nodes because
                // the original node will not help to extend the match.
                newNodes.remove(origNodeS);
                // The match should be injective so remove previously matched
                // nodes.
                mat.removePreviouslyMatchedNodes(newNodes);
                // Iterate over the new nodes and duplicate the materialisation
                // object when necessary.
                for (Node newNode : newNodes) {
                    ShapeNode nodeS = (ShapeNode) newNode;
                    if (newNodes.size() == 1) {
                        // We only have one new node, so no need for cloning.
                        mat.extendMatch(nodeR, nodeS);
                        todoMats.add(mat);
                    } else {
                        // Clone our current materialisation.
                        Materialisation newMat = mat.clone();
                        newMat.extendMatch(nodeR, nodeS);
                        todoMats.add(newMat);
                    }
                }
            }
        }

        return result;
    }

    private void removePreviouslyMatchedNodes(Set<Node> nodes) {
        // Look at the partially extended match and remove occurring nodes.
        for (Node node : this.match.nodeMap().values()) {
            nodes.remove(node);
        }
    }

    private void extendMatch(Node nodeR, ShapeNode nodeS) {
        this.match.putNode(nodeR, nodeS);
        this.absElems.removeNode(nodeR);
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
                if (newEdgeS != null) {
                    this.match.putEdge(edgeR, newEdgeS);
                }
            }
        }
    }

    private boolean isExtensionFinished() {
        return this.absElems.nodeMap().isEmpty();
    }

    private void finishMaterialisation() {
        // At this point we assume that all variations on the nodes were
        // resolved, and that the rule match is complete and fixed.
        // Now, we go through the matched edges of the rule and adjust the 
        // shared edge multiplicities.

        Multiplicity oneMult = Multiplicity.getMultOf(1);
        for (Edge edgeR : this.absElems.edgeMap().keySet()) {
            ShapeEdge mappedEdge = (ShapeEdge) this.match.getEdge(edgeR);

            // Check outgoing multiplicities.
            EdgeSignature outEs = this.shape.getEdgeOutSignature(mappedEdge);
            Multiplicity outMult = this.shape.getEdgeSigOutMult(outEs);
            if (outMult.equals(oneMult)) {
                this.shape.removeImpossibleOutEdges(outEs, mappedEdge);
            }

            // Check incoming multiplicities.
            EdgeSignature inEs = this.shape.getEdgeInSignature(mappedEdge);
            Multiplicity inMult = this.shape.getEdgeSigInMult(inEs);
            if (inMult.equals(oneMult)) {
                this.shape.removeImpossibleInEdges(inEs, mappedEdge);
            }
        }
    }

    /** EDUARDO */
    public Shape applyMatch() {
        RuleEvent event =
            new SPOEvent(this.preMatch.getRule(), (VarNodeEdgeMap) this.match,
                ShapeNodeFactory.FACTORY, false);
        DefaultApplication app = new DefaultApplication(event, this.shape);
        Shape result = (Shape) app.getTarget();
        return result;
    }

    /** EDUARDO */
    public Shape getShape() {
        return this.shape;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testMaterialisation0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("rule-app-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("add");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    mat.applyMatch();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Unit testing. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testMaterialisation0();
    }
}
