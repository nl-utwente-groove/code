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
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPOEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
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
public class Materialisation {

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
     * The concrete match of the rule into the (partially) materialised shape.
     */
    private NodeEdgeMap match;
    /**
     * The queue of operations that need to be performed on the materialisation
     * object. When this queue is empty, the materialisation is complete. 
     */
    private Queue<MatOp> tasks;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation(Shape shape, RuleMatch preMatch) {
        this.shape = shape;
        this.preMatch = preMatch;
        this.match = preMatch.getElementMap().clone();
        this.tasks = new LinkedList<MatOp>();
        this.planTasks();
    }

    /**
     * Copying constructor. Clones the materialisation object given to avoid
     * aliasing and undesired modifications. 
     */
    private Materialisation(Materialisation mat) {
        this.shape = mat.shape.clone();
        this.preMatch = mat.preMatch;
        this.match = mat.match.clone();
        this.tasks = new LinkedList<MatOp>();
        // Update the materialisation reference in the tasks.
        for (MatOp origOp : mat.tasks) {
            MatOp cloneOp = origOp.clone();
            cloneOp.setMat(this);
            this.tasks.add(cloneOp);
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape + "Match: "
            + this.match + "\nTasks: " + this.tasks + "\n";
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

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

        // Some operations that need to be performed during the materialisation
        // phase are non-deterministic. This implies that, when performing a
        // certain operation, we may get back a collection of new
        // materialisations. We use a queue to store these temporary
        // materialisation objects and move them to the result set when the
        // materialisation is complete.
        Queue<Materialisation> queue = new LinkedList<Materialisation>();
        queue.add(initialMat);
        while (!queue.isEmpty()) {
            // We have materialisations on the queue with pending operations.
            // Remove the first materialisation from the queue.
            Materialisation mat = queue.remove();
            if (mat.isFinished()) {
                // This one is done.
                result.add(mat);
            } else { // Process the next operation on the materialisation.
                MatOp op = mat.getNextOp();
                op.perform();
                if (op.isSuccesful()) {
                    op.collectResults(queue);
                }
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

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

    /** Returns true if the materialisation is finished. */
    private boolean isFinished() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns the next operation that should be performed in the
     * materialisation.
     */
    private MatOp getNextOp() {
        assert !this.isFinished() : "Nothing to do!";
        return this.tasks.remove();
    }

    private void planTasks() {
        NodeEdgeMap originalMap = this.preMatch.getElementMap();

        // Check the node images.
        Set<ShapeNode> processedNodes = new HashSet<ShapeNode>();
        for (Entry<Node,Node> nodeEntry : originalMap.nodeMap().entrySet()) {
            ShapeNode nodeS = (ShapeNode) nodeEntry.getValue();
            if (!processedNodes.contains(nodeS)
                && this.shape.getNodeMult(nodeS).isAbstract()) {
                // We have a node in the rule that was matched to an abstract
                // node. We need to materialise this abstract node.
                // Check the nodes on the rule that were mapped to nodeS.
                Set<Node> nodesR = Util.getReverseNodeMap(originalMap, nodeS);
                this.tasks.add(new MaterialiseNode(this, nodeS, nodesR));
                processedNodes.add(nodeS);
            }
        }

        // EDUARDO: Construct the edge images...
        // EDUARDO: Check that all nodes of the LHS are in the same equivalence class...

    }

    /**
     * Extends the rule match to the given shape node. The rule edges adjacent
     * to the rule node also have their mapping updated.
     * @param nodeR - the node in the rule.
     * @param nodeS - the newly materialised node in the shape.
     */
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
                if (newEdgeS != null) {
                    this.match.putEdge(edgeR, newEdgeS);
                }
            }
        }
    }

    /**
     * Given an edge matched by the rule, clear all other shared edges in the
     * shape that can no longer exist. 
     */
    private void removeImpossibleEdges(ShapeEdge mappedEdge) {
        // EDUARDO: fix this method...
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

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // -----------
    // Class MatOp
    // -----------

    private abstract class MatOp {

        public static final int MAX_PRIORITY = 3;

        protected Materialisation mat;
        protected Set<Materialisation> result;

        protected MatOp() {
            this.mat = null;
            this.result = new HashSet<Materialisation>();
        }

        public MatOp(Materialisation mat) {
            this.mat = mat;
            this.result = new HashSet<Materialisation>();
        }

        @Override
        abstract public MatOp clone();

        abstract public void perform();

        abstract public int getPriority();

        public void setMat(Materialisation mat) {
            this.mat = mat;
        }

        public boolean isSuccesful() {
            return !this.result.isEmpty();
        }

        public void collectResults(Collection<Materialisation> collector) {
            assert this.isSuccesful() : "Invalid call!";
            collector.addAll(this.result);
        }

    }

    // ---------------------
    // Class MaterialiseNode
    // ---------------------

    private class MaterialiseNode extends MatOp {

        private ShapeNode nodeS;
        private Set<Node> nodesR;

        public MaterialiseNode(Materialisation mat, ShapeNode nodeS,
                Set<Node> nodesR) {
            super(mat);
            this.nodeS = nodeS;
            this.nodesR = nodesR;
        }

        private MaterialiseNode(MaterialiseNode matNode) {
            super();
            this.setMat(matNode.mat);
            this.nodeS = matNode.nodeS;
            this.nodesR = matNode.nodesR;
        }

        @Override
        public MatOp clone() {
            return new MaterialiseNode(this);
        }

        @Override
        public String toString() {
            return "MaterialiseNode: " + this.nodeS + ", " + this.nodesR;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void perform() {
            // Compute how many copies of the abstract node we need to
            // materialise.
            int copies = this.nodesR.size();
            // Materialise the node and get the new multiplicity set back.
            Set<Multiplicity> mults =
                this.mat.shape.materialiseNode(this.nodeS, copies);
            // Look in the shaping morphism to get the new nodes that were
            // materialised from the original node.
            Set<ShapeNode> newNodes =
                this.mat.shape.getReverseNodeMap(this.nodeS);
            // Remove the original node from the set of new nodes because
            // the original node will not help to extend the match.
            newNodes.remove(this.nodeS);

            // Create the new materialisation objects.
            for (Multiplicity mult : mults) {
                Materialisation newMat;
                // Check if we need to clone the materialisation object.
                if (mults.size() == 1) {
                    // No, we don't need to clone.
                    newMat = this.mat;
                } else {
                    // Yes, we do need to clone.
                    newMat = this.mat.clone();
                }
                // Update the multiplicity of the original node.
                newMat.shape.setNodeMult(this.nodeS, mult);

                // Create the new tasks that will be performed after this one.
                // First, extend the pre-match into the new nodes.
                ExtendPreMatch extendPreMatch =
                    new ExtendPreMatch(newMat, this.nodesR, newNodes);
                newMat.tasks.add(extendPreMatch);
                // Second, make sure that all materialised nodes will be in a
                // singleton equivalence class.
                for (ShapeNode newNode : newNodes) {
                    SingulariseNode singulariseNode =
                        new SingulariseNode(newMat, newNode);
                    newMat.tasks.add(singulariseNode);
                }
                // Add this new materialisation to the result set of this
                // operation.
                this.result.add(newMat);
            }
        }

    }

    // --------------------
    // Class ExtendPreMatch
    // --------------------

    private class ExtendPreMatch extends MatOp {

        private Set<Node> nodesR;
        private Set<ShapeNode> newNodes;

        public ExtendPreMatch(Materialisation mat, Set<Node> nodesR,
                Set<ShapeNode> newNodes) {
            super(mat);
            this.nodesR = nodesR;
            this.newNodes = newNodes;
        }

        private ExtendPreMatch(ExtendPreMatch extPm) {
            super();
            this.setMat(extPm.mat);
            this.nodesR = extPm.nodesR;
            this.newNodes = extPm.newNodes;
        }

        @Override
        public MatOp clone() {
            return new ExtendPreMatch(this);
        }

        @Override
        public String toString() {
            return "ExtendPreMatch: " + this.nodesR + ", " + this.newNodes;
        }

        @Override
        public int getPriority() {
            return 1;
        }

        /**
         * Extends the pre-match of a partially constructed materialisation
         * object. In this method, the nodes of the pre-match that were mapped
         * to abstract nodes in the shape are re-mapped to the newly
         * materialised nodes. Note that this step is also non-deterministic,
         * since we may have more than one node in the rule that was mapped to
         * the same abstract node. In this case we need to try out all possible
         * combinations. Not all of those are actually valid configurations but
         * this will only be checked later. 
         */
        @Override
        public void perform() {
            // Compute all possible matches of nodesR into newNodes.
            Set<NodeEdgeMap> matches =
                SetPermutation.getPermutationSet(this.nodesR, this.newNodes);

            for (NodeEdgeMap match : matches) {
                Materialisation newMat;
                // Check if we need to clone the materialisation object.
                if (matches.size() == 1) {
                    // No, we don't need to clone.
                    newMat = this.mat;
                } else {
                    // Yes, we do need to clone.
                    newMat = this.mat.clone();
                }

                // For all nodes in the match permutation, adjust the match of
                // the materialisation.
                for (Entry<Node,Node> entry : match.nodeMap().entrySet()) {
                    Node nodeR = entry.getKey();
                    ShapeNode nodeS = (ShapeNode) entry.getValue();
                    newMat.extendMatch(nodeR, nodeS);
                }

                // Create the new tasks that will be performed after this one.
                // Cleanup impossible edges. 
                CleanupImpossibleEdges cleanupImpossibleEdges =
                    new CleanupImpossibleEdges(newMat);
                newMat.tasks.add(cleanupImpossibleEdges);

                // Add this new materialisation to the result set of this
                // operation.
                this.result.add(newMat);
            }
        }
    }

    // ----------------------------
    // Class CleanupImpossibleEdges
    // ----------------------------

    private class CleanupImpossibleEdges extends MatOp {

        public CleanupImpossibleEdges(Materialisation mat) {
            super(mat);
        }

        private CleanupImpossibleEdges(CleanupImpossibleEdges cleanIe) {
            super();
            this.setMat(cleanIe.mat);
        }

        @Override
        public MatOp clone() {
            return new CleanupImpossibleEdges(this);
        }

        @Override
        public String toString() {
            return "CleanupImpossibleEdges";
        }

        @Override
        public int getPriority() {
            return 2;
        }

        /** 
         * Finishes the materialisation by removing impossible edge
         * configurations.
         */
        @Override
        public void perform() {
            // At this point we assume that all variations on the nodes were
            // resolved, and that the rule match is complete and fixed.
            // Now, we go through the matched edges of the rule and adjust the 
            // shared edge multiplicities.
            for (Edge edgeR : this.mat.match.edgeMap().keySet()) {
                ShapeEdge edgeS = (ShapeEdge) this.mat.match.getEdge(edgeR);
                this.mat.removeImpossibleEdges(edgeS);
            }
            // This operation is deterministic. No need to clone.
            this.result.add(this.mat);
        }

    }

    // ---------------------
    // Class SingulariseNode
    // ---------------------

    private class SingulariseNode extends MatOp {

        private ShapeNode nodeS;

        public SingulariseNode(Materialisation mat, ShapeNode nodeS) {
            super(mat);
            this.nodeS = nodeS;
        }

        private SingulariseNode(SingulariseNode singNode) {
            super();
            this.setMat(singNode.mat);
            this.nodeS = singNode.nodeS;
        }

        @Override
        public MatOp clone() {
            return new SingulariseNode(this);
        }

        @Override
        public String toString() {
            return "SingulariseNode: " + this.nodeS;
        }

        @Override
        public int getPriority() {
            return 3;
        }

        @Override
        public void perform() {
            this.result.add(this.mat);
        }

    }

}
