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
        this.tasks = new LinkedList<MatOp>(mat.tasks);
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
                this.tasks.add(new MaterialiseNode(nodeS, nodesR));
                processedNodes.add(nodeS);
            }
        }

        // EDUARDO: Construct the edge images...
        // EDUARDO: Check that all nodes of the LHS are in the same equivalence class...

    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // -----------
    // Class MatOp
    // -----------

    private abstract class MatOp {

        protected Set<Materialisation> result = new HashSet<Materialisation>();

        abstract public void perform();

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

        public MaterialiseNode(ShapeNode nodeS, Set<Node> nodesR) {
            this.nodeS = nodeS;
            this.nodesR = nodesR;
        }

        @Override
        public void perform() {
            // Compute how many copies of the abstract node we need to
            // materialise.
            int copies = this.nodesR.size();
            // Materialise the node and get the new multiplicity set back.
            Set<Multiplicity> mults =
                Materialisation.this.shape.materialiseNode(this.nodeS, copies);
            // Look in the shaping morphism to get the new nodes that were
            // materialised from the original node.
            Set<ShapeNode> newNodes =
                Materialisation.this.shape.getReverseNodeMap(this.nodeS);
            // Remove the original node from the set of new nodes because
            // the original node will not help to extend the match.
            newNodes.remove(this.nodeS);

            // Create the new tasks that need to be performed after this one.
            // First, extend the pre-match into the new nodes.
            ExtendPreMatch extendPreMatch =
                new ExtendPreMatch(this.nodesR, newNodes);
            Materialisation.this.tasks.add(extendPreMatch);
            // Second, make sure that all materialised nodes will be in a
            // singleton equivalence class.
            for (ShapeNode newNode : newNodes) {
                SingulariseNode singulariseNode = new SingulariseNode(newNode);
                Materialisation.this.tasks.add(singulariseNode);
            }

            // Check if the need to clone the materialisation object.
            if (mults.size() == 1) {
                // No, we don't need to clone.
                // Properly adjust the multiplicity of the original node.
                Multiplicity mult = mults.iterator().next();
                Materialisation.this.shape.setNodeMult(this.nodeS, mult);
                this.result.add(Materialisation.this);
            } else {
                // Yes, we do need to clone.
                for (Multiplicity mult : mults) {
                    Materialisation newMat = Materialisation.this.clone();
                    newMat.shape.setNodeMult(this.nodeS, mult);
                    this.result.add(newMat);
                }
            }
        }

    }

    // --------------------
    // Class ExtendPreMatch
    // --------------------

    private class ExtendPreMatch extends MatOp {

        private Set<Node> nodesR;
        private Set<ShapeNode> newNodes;

        public ExtendPreMatch(Set<Node> nodesR, Set<ShapeNode> newNodes) {
            this.nodesR = nodesR;
            this.newNodes = newNodes;
        }

        @Override
        public void perform() {
            // 
        }

    }

    // ---------------------
    // Class SingulariseNode
    // ---------------------

    private class SingulariseNode extends MatOp {

        private ShapeNode nodeS;

        public SingulariseNode(ShapeNode nodeS) {
            this.nodeS = nodeS;
        }

        @Override
        public void perform() {
            //
        }

    }

}
