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
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPOEvent;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class represents an attempt to materialise a certain shape, driven by
 * a certain pre-match of a rule into the shape. We assume that the reader is
 * familiar with the concepts of shape abstraction, in particular Sect. 6
 * (pg 27) of the Technical Report "Graph Abstraction and Abstract Graph
 * Transformation".
 * 
 * WARNING: Beware of the code in this class. It's rather tricky.
 * 
 * @author Eduardo Zambon
 */
@Deprecated
public class Materialisation_Old {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The shape we are trying to materialise. Note that this shape is
     * modified along the construction of the materialisation. This basically
     * implies that the materialisation object needs to be cloned every time
     * we perform some modifying operation on the shape. 
     */
    private Shape shape;
    /**
     * The pre-match of the rule into the shape. This is the starting point for
     * the materialisation. We assume that the pre-match is a valid one.
     */
    private RuleMatch preMatch;
    /**
     * Temporary storage for the elements of the shape that are abstract and
     * need to be materialised.
     */
    private NodeEdgeMap absElems;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     */
    private NodeEdgeMap match;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation_Old(Shape shape, RuleMatch preMatch) {
        this.shape = shape;
        this.preMatch = preMatch;
        this.absElems = getAbstractMatchedElems(shape, preMatch);
        this.match = preMatch.getElementMap().clone();
    }

    /**
     * Copying constructor. Clones the materialisation object given to avoid
     * aliasing and undesired modifications. 
     */
    private Materialisation_Old(Materialisation_Old mat) {
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
    public Materialisation_Old clone() {
        return new Materialisation_Old(this);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Goes over the images of the pre-match and collects the elements of the
     * shape that need to be materialised.
     */
    private static NodeEdgeMap getAbstractMatchedElems(Shape shape,
            RuleMatch preMatch) {
        NodeEdgeMap elemsToMat = new NodeEdgeHashMap();
        NodeEdgeMap originalMap = preMatch.getElementMap();

        // Check the node images.
        for (Entry<Node,Node> nodeEntry : originalMap.nodeMap().entrySet()) {
            ShapeNode nodeS = (ShapeNode) nodeEntry.getValue();
            if (shape.getNodeMult(nodeS).isAbstract()) {
                // We have a node in the rule that was matched to an abstract
                // node. We need to materialise this abstract node.
                elemsToMat.putNode(nodeEntry.getKey(), nodeS);
            }
        }

        // Check the edge images.
        for (Entry<Edge,Edge> edgeEntry : originalMap.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            if (!Util.isUnary(edgeR)) {
                ShapeEdge edgeS = (ShapeEdge) edgeEntry.getValue();
                EdgeSignature outEs = shape.getEdgeOutSignature(edgeS);
                EdgeSignature inEs = shape.getEdgeInSignature(edgeS);
                if (!shape.isOutEdgeSigUnique(outEs)
                    || !shape.isInEdgeSigUnique(inEs)) {
                    // We have an edge in the rule that was matched to an edge
                    // in the shape with a shared multiplicity. We need to
                    // materialise this shared edge.
                    elemsToMat.putEdge(edgeR, edgeS);
                }
            }
        }

        return elemsToMat;
    }

    /**
     * Constructs and returns the set of all possible materialisations of the
     * given shape and pre-match. This method resolves all non-determinism
     * in the materialisation phase, so the shapes in the returned
     * materialisations are ready to be transformed by conventional rule
     * application.
     * Note that the returned set may be empty, even if the pre-match is valid.
     * This is the case because the shape may not admit any valid
     * materialisation.
     */
    public static Set<Materialisation_Old> getMaterialisations(Shape shape,
            RuleMatch preMatch) {
        Set<Materialisation_Old> result = new HashSet<Materialisation_Old>();

        // Clone the given shape to avoid aliasing and unwanted modifications.
        Shape shapeClone = shape.clone();
        // We are going to materialise elements in the cloned shape, so set
        // the original shape as the graph from which the clone was created
        // and create an identity morphism between the elements of the clone
        // and of the original. This morphism will be later updated and when
        // the materialisation is done it will be the shaping morphism.
        shapeClone.setShapeAndCreateIdentityMorphism(shape);

        // Initial materialisation object.
        Materialisation_Old initialMat =
            new Materialisation_Old(shapeClone, preMatch);

        Set<Materialisation_Old> partialMats;
        if (!initialMat.isExtensionFinished()) {
            partialMats = initialMat.materialiseNodesAndExtendPreMatch();
        } else {
            // We don't need to materialise any node.
            partialMats = new HashSet<Materialisation_Old>();
            partialMats.add(initialMat);
        }

        for (Materialisation_Old partialMat : partialMats) {
            partialMat.finishMaterialisation();
            result.add(partialMat);
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * This method duplicates all nodes marked as abstract that need to be
     * materialised. This produces several different shapes. For each of those
     * shapes, a new materialisation object is created and the match of the
     * rule is adjusted, i.e., the pre-match is extended over the new nodes.
     * In the materialisation objects returned all nodes of the rule are
     * properly matched on the nodes of the shapes. We call these 'partial'
     * materialisations. After calling this method, the partial materialisations
     * need to be finished, i.e., the non-determinism on the edges must be
     * resolved. 
     */
    private Set<Materialisation_Old> materialiseNodesAndExtendPreMatch() {
        Set<Materialisation_Old> result = new HashSet<Materialisation_Old>();

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
        Set<Pair<ShapeNode,Set<Multiplicity>>> origNodesAndMults = null;// =
        //this.shape.materialiseNodes(nodesToMat);

        // By materialising nodes we actually want a set of materialisations.
        // Construct this set now.
        Set<Materialisation_Old> newMats = new HashSet<Materialisation_Old>();
        Iterator<Set<Pair<ShapeNode,Multiplicity>>> iter =
            new PairSetIterator<ShapeNode,Multiplicity>(origNodesAndMults);
        while (iter.hasNext()) {
            // This set represents a variation of a materialisation.
            Set<Pair<ShapeNode,Multiplicity>> nodesAndMultsPairs = iter.next();
            // Create a new materialisation object.
            Materialisation_Old newMat = this.clone();
            for (Pair<ShapeNode,Multiplicity> pair : nodesAndMultsPairs) {
                // For all original nodes that materialised one or more copies.
                ShapeNode origNode = pair.first();
                Multiplicity mult = pair.second();
                // Properly adjust the multiplicity of the original node.
                newMat.shape.setNodeMult(origNode, mult);
            }
            // Store this new materialisation.
            newMats.add(newMat);
        }

        // Extend the pre-match of all the newly constructed materialisations.
        for (Materialisation_Old newMat : newMats) {
            result.addAll(newMat.extendPreMatch());
        }

        return result;
    }

    /**
     * Extends the pre-match of a partially constructed materialisation object.
     * In this method, the nodes of the pre-match that were mapped to abstract
     * nodes in the shape are re-mapped to the newly materialised nodes. Note
     * that this step is also non-deterministic, since we may have more
     * than one node in the rule that was mapped to the same abstract node. In
     * this case we need to try out all possible combinations. Not all of those
     * are actually valid configurations but this will only be checked later. 
     */
    private Set<Materialisation_Old> extendPreMatch() {
        Set<Materialisation_Old> result = new HashSet<Materialisation_Old>();
        // Process the nodes pre-matched to abstract nodes using a queue.
        List<Materialisation_Old> todoMats =
            new ArrayList<Materialisation_Old>();
        // Add initial element to end.
        todoMats.add(this);
        while (!todoMats.isEmpty()) {
            // Remove from head.
            Materialisation_Old mat = todoMats.remove(0);
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
                        Materialisation_Old newMat = mat.clone();
                        newMat.extendMatch(nodeR, nodeS);
                        todoMats.add(newMat);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Looks at the partially extended match and remove occurring nodes given.
     */
    private void removePreviouslyMatchedNodes(Set<Node> nodes) {
        for (Node node : this.match.nodeMap().values()) {
            nodes.remove(node);
        }
    }

    /**
     * Extends the rule match to the given shape node. The rule edges adjacent
     * to the rule node also have their mapping updated.
     * @param nodeR - the node in the rule.
     * @param nodeS - the newly materialised node in the shape.
     */
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

    /** Returns true if all abstract nodes were already materialised. */
    private boolean isExtensionFinished() {
        return this.absElems.nodeMap().isEmpty();
    }

    /** 
     * Finishes the materialisation by removing impossible edge configurations.
     */
    private void finishMaterialisation() {
        // At this point we assume that all variations on the nodes were
        // resolved, and that the rule match is complete and fixed.
        // Now, we go through the matched edges of the rule and adjust the 
        // shared edge multiplicities.
        for (Edge edgeR : this.absElems.edgeMap().keySet()) {
            ShapeEdge mappedEdge = (ShapeEdge) this.match.getEdge(edgeR);
            this.removeImpossibleEdges(mappedEdge);
        }
        this.absElems.edgeMap().clear();
    }

    /**
     * Given an edge matched by the rule, clear all other shared edges in the
     * shape that can no longer exist. 
     */
    private void removeImpossibleEdges(ShapeEdge mappedEdge) {
        Multiplicity oneMult = Multiplicity.getMultOf(1);
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

    /**
     * Applies the rule match defined by this materialisation and returns the
     * transformed shape. This shape is not yet normalised.
     */
    public Shape applyMatch() {
        RuleEvent event =
            new SPOEvent(this.preMatch.getRule(), (VarNodeEdgeMap) this.match,
                ShapeNodeFactory.FACTORY, false);
        DefaultApplication app = new DefaultApplication(event, this.shape);
        Shape result = (Shape) app.getTarget();
        return result;
    }

    /** Basic getter method. */
    public Shape getShape() {
        return this.shape;
    }

}
