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

import groove.abstraction.gui.ShapeDialog;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
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
public class Materialisation implements Cloneable {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The shape we are trying to materialise. Note that this shape is
     * modified along the construction of the materialisation. This basically
     * implies that the materialisation object needs to be cloned every time
     * we perform some modifying operation on the shape. 
     */
    protected Shape shape;
    /**
     * The pre-match of the rule into the shape. This is the starting point for
     * the materialisation. We assume that the pre-match is a valid one.
     */
    private RuleMatch preMatch;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     */
    protected NodeEdgeMap match;
    /**
     * The queue of operations that need to be performed on the materialisation
     * object. When this queue is empty, the materialisation is complete. 
     */
    protected PriorityQueue<MatOp> tasks;

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
        this.tasks = new PriorityQueue<MatOp>();
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
        this.tasks = new PriorityQueue<MatOp>();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((this.shape == null) ? 0 : this.shape.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (!(obj instanceof Materialisation)) {
            result = false;
        } else {
            Materialisation other = (Materialisation) obj;
            result = this.shape.equals(other.shape);
        }
        return result;
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
                assert mat.hasConcreteMatch();
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

    /**
     * Checks if the match in the materialisation is concrete. See items 3, 4,
     * and 5 of Def. 35 in pg. 21 of the technical report. 
     * @return true if all three items are satisfied; false, otherwise.
     */
    public boolean hasConcreteMatch() {
        // Item 3: check that all nodes in the image of the LHS have
        // multiplicity one.
        boolean complyToNodeMult = true;
        Multiplicity oneMult = Multiplicity.getMultOf(1);
        // For all nodes in the image of the LHS.
        for (Node node : this.match.nodeMap().values()) {
            ShapeNode nodeS = (ShapeNode) node;
            if (!this.shape.getNodeMult(nodeS).equals(oneMult)) {
                complyToNodeMult = false;
                break;
            }
        }

        // Item 4: check that for all nodes in the image of the LHS, their
        // equivalence class is a singleton set.
        boolean complyToEquivClass = true;
        if (complyToNodeMult) {
            // For all nodes in the image of the LHS.
            for (Node node : this.match.nodeMap().values()) {
                ShapeNode nodeS = (ShapeNode) node;
                if (this.shape.getEquivClassOf(nodeS).size() != 1) {
                    complyToEquivClass = false;
                    break;
                }
            }
        }

        // Item 5: check that for any two nodes in the image of the LHS, their
        // outgoing and incoming multiplicities are equal and correspond to
        // the number of edges in the underlying graph structure of the shape.
        boolean complyToEdgeMult = true;
        if (complyToNodeMult && complyToEquivClass) {
            // For all binary labels.
            for (Label label : Util.binaryLabelSet(this.shape)) {
                // For all nodes v in the image of the LHS.
                for (Node n0 : this.match.nodeMap().values()) {
                    ShapeNode v = (ShapeNode) n0;
                    // For all nodes w in the image of the LHS.
                    for (Node n1 : this.match.nodeMap().values()) {
                        ShapeNode w = (ShapeNode) n1;
                        EquivClass<ShapeNode> ecW =
                            this.shape.getEquivClassOf(w);
                        EdgeSignature es =
                            this.shape.getEdgeSignature(v, label, ecW);
                        Multiplicity outMult = this.shape.getEdgeSigOutMult(es);
                        Multiplicity inMult = this.shape.getEdgeSigInMult(es);
                        Set<Edge> vInterW =
                            Util.getIntersectEdges(this.shape, v, w, label);
                        Multiplicity vInterWMult =
                            Multiplicity.getEdgeSetMult(vInterW);
                        Set<Edge> wInterV =
                            Util.getIntersectEdges(this.shape, w, v, label);
                        Multiplicity wInterVMult =
                            Multiplicity.getEdgeSetMult(wInterV);
                        if (!outMult.equals(vInterWMult)
                            || !inMult.equals(wInterVMult)) {
                            complyToEdgeMult = false;
                            break;
                        }
                    }
                }
            }
        }

        return complyToNodeMult && complyToEquivClass && complyToEdgeMult;
    }

    private void planTasks() {
        NodeEdgeMap originalMap = this.preMatch.getElementMap();

        // Search for nodes in the match image that have abstract
        // multiplicities. 
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

        // Search for edges in the match image that have abstract
        // multiplicities.
        Set<ShapeEdge> processedEdges = new HashSet<ShapeEdge>();
        for (Entry<Edge,Edge> edgeEntry : originalMap.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            ShapeEdge edgeS = (ShapeEdge) edgeEntry.getValue();
            if (!Util.isUnary(edgeR) && !processedEdges.contains(edgeS)) {
                // Check if the image edge in the shape has abstract
                // multiplicities.
                EdgeSignature outEs = this.shape.getEdgeOutSignature(edgeS);
                EdgeSignature inEs = this.shape.getEdgeInSignature(edgeS);
                if (!this.shape.isOutEdgeSigConcrete(outEs)
                    || !this.shape.isInEdgeSigConcrete(inEs)) {
                    // We have an edge in the rule that was matched to an edge
                    // in the shape with an abstract multiplicity. We need to
                    // materialise this edge.
                    // Check the edges on the rule that were mapped to edgeS.
                    Set<Edge> edgesR =
                        Util.getReverseEdgeMap(originalMap, edgeS);
                    this.tasks.add(new MaterialiseEdge(this, edgeS, edgesR));
                    processedEdges.add(edgeS);
                }
            }
        }

        // Check that all nodes of the LHS are in the same equivalence class.
        for (Entry<Node,Node> nodeEntry : originalMap.nodeMap().entrySet()) {
            ShapeNode nodeS = (ShapeNode) nodeEntry.getValue();
            boolean isSingletonEc =
                this.shape.getEquivClassOf(nodeS).size() == 1;
            if (!processedNodes.contains(nodeS)
                && !this.shape.getNodeMult(nodeS).isAbstract()
                && !isSingletonEc) {
                // We have a node in the rule that was matched to a concrete
                // node but the equivalence class of this concrete node (nodeS)
                // is not a singleton class. We need to put this nodeS in its 
                // own equivalence class. This operation needs to be created
                // here because the nodeS is already concrete so there will be
                // no calls to MaterialiseNode with nodeS as a parameter.
                this.tasks.add(new SingulariseNode(this, nodeS));
                processedNodes.add(nodeS);
            }
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
                    EdgeSignature outEs =
                        this.shape.getEdgeOutSignature(newEdgeS);
                    EdgeSignature inEs =
                        this.shape.getEdgeInSignature(newEdgeS);
                    if (this.shape.isOutEdgeSigConcrete(outEs)
                        && this.shape.isInEdgeSigConcrete(inEs)) {
                        this.match.putEdge(edgeR, newEdgeS);
                    } // else, we have an edge that needs to be materialised.
                      // Wait for the MaterialiseEdge operation to take care
                      // of this edge.
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // -----------
    // Class MatOp
    // -----------

    private static abstract class MatOp implements Comparable<MatOp>, Cloneable {

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
        public int compareTo(MatOp o) {
            int thisOp = this.getPriority();
            int otherOp = o.getPriority();
            int result;
            if (thisOp == otherOp) {
                result = 0;
            } else if (thisOp < otherOp) {
                result = -1;
            } else {
                result = 1;
            }
            return result;
        }

        @Override
        abstract public boolean equals(Object o);

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

    private static class MaterialiseNode extends MatOp {

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
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof MaterialiseNode) {
                MaterialiseNode other = (MaterialiseNode) o;
                result =
                    this.nodeS.equals(other.nodeS)
                        && this.nodesR.equals(other.nodesR);
            }
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result
                    + ((this.nodeS == null) ? 0 : this.nodeS.hashCode());
            result =
                prime * result
                    + ((this.nodesR == null) ? 0 : this.nodesR.hashCode());
            return result;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void perform() { // MaterialiseNode
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

    private static class ExtendPreMatch extends MatOp {

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
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof ExtendPreMatch) {
                ExtendPreMatch other = (ExtendPreMatch) o;
                result =
                    this.nodesR.equals(other.nodesR)
                        && this.newNodes.equals(other.newNodes);
            }
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result
                    + ((this.newNodes == null) ? 0 : this.newNodes.hashCode());
            result =
                prime * result
                    + ((this.nodesR == null) ? 0 : this.nodesR.hashCode());
            return result;
        }

        @Override
        public int getPriority() {
            return 1;
        }

        /**
         * Extends the pre-match of a partially constructed materialisation
         * object. In this method, the nodes of the pre-match that were mapped
         * to abstract nodes in the shape are re-mapped to the newly
         * materialised nodes.
         * This method used to be non-deterministic. Now we only take an
         * arbitrary match because the transformation always leads to an
         * isomorphic shape.
         */
        @Override
        public void perform() { // ExtendPreMatch
            assert (this.nodesR.size() == this.newNodes.size()) : "Sets should have the same size!";

            // Both sets have the same size. Go over both of them at the same
            // time, and take the returned values of the iterator as the match.
            int nodeSetsSize = this.nodesR.size();
            Iterator<Node> nodesRIter = this.nodesR.iterator();
            Iterator<ShapeNode> newNodesIter = this.newNodes.iterator();
            for (int i = 0; i < nodeSetsSize; i++) {
                Node nodeR = nodesRIter.next();
                ShapeNode nodeS = newNodesIter.next();
                // Adjust the match of the materialisation.
                this.mat.extendMatch(nodeR, nodeS);
            }

            // Add this materialisation to the result set of this
            // operation.
            this.result.add(this.mat);
        }

    }

    // ---------------------
    // Class MaterialiseEdge
    // ---------------------

    private static class MaterialiseEdge extends MatOp {

        private ShapeEdge edgeS;
        private Set<Edge> edgesR;

        public MaterialiseEdge(Materialisation mat, ShapeEdge edgeS,
                Set<Edge> edgesR) {
            super(mat);
            this.edgeS = edgeS;
            this.edgesR = edgesR;
        }

        private MaterialiseEdge(MaterialiseEdge matEdge) {
            super();
            this.setMat(matEdge.mat);
            this.edgeS = matEdge.edgeS;
            this.edgesR = matEdge.edgesR;
        }

        @Override
        public MatOp clone() {
            return new MaterialiseEdge(this);
        }

        @Override
        public String toString() {
            return "MaterialiseEdge: " + this.edgeS + ", " + this.edgesR;
        }

        @Override
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof MaterialiseEdge) {
                MaterialiseEdge other = (MaterialiseEdge) o;
                result =
                    this.edgeS.equals(other.edgeS)
                        && this.edgesR.equals(other.edgesR);
            }
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result
                    + ((this.edgeS == null) ? 0 : this.edgeS.hashCode());
            result =
                prime * result
                    + ((this.edgesR == null) ? 0 : this.edgesR.hashCode());
            return result;
        }

        @Override
        public int getPriority() {
            return 2;
        }

        /**
         * Pre-condition: all MaterialiseNode operations have been performed,
         * and the node images in the match are final. 
         */
        @Override
        public void perform() { // MaterialiseEdge
            NodeEdgeMap match = this.mat.match;
            Shape shape = this.mat.shape;

            // Collect all signatures that will be affected by this operation.
            CountingSet<EdgeSignature> outEsSet =
                new CountingSet<EdgeSignature>();
            CountingSet<EdgeSignature> inEsSet =
                new CountingSet<EdgeSignature>();
            for (Edge edgeR : this.edgesR) {
                // For each edge in the rule.
                Label label = edgeR.label();
                // Get the image of source and target from the match.
                ShapeNode srcS = (ShapeNode) match.getNode(edgeR.source());
                ShapeNode tgtS = (ShapeNode) match.getNode(edgeR.target());
                // Outgoing signatures.
                EdgeSignature outEs =
                    shape.getEdgeSignature(srcS, label,
                        shape.getEquivClassOf(tgtS));
                if (!shape.isOutEdgeSigUnique(outEs)
                    || !shape.isOutEdgeSigConcrete(outEs)) {
                    outEsSet.add(outEs);
                }
                // Incoming signatures.
                EdgeSignature inEs =
                    shape.getEdgeSignature(tgtS, label,
                        shape.getEquivClassOf(srcS));
                if (!shape.isInEdgeSigUnique(inEs)
                    || !shape.isInEdgeSigConcrete(inEs)) {
                    inEsSet.add(inEs);
                }

                // We can already set the match here.
                ShapeEdge edge = shape.getShapeEdge(srcS, label, tgtS);
                if (edge != null) {
                    match.putEdge(edgeR, edge);
                }
            }

            // Materialise the edge in the shape and get the multiplicities
            // back.
            Set<Pair<EdgeSigWrapper,Set<Multiplicity>>> mults =
                shape.materialiseEdgeInit(outEsSet, inEsSet);
            // Construct an iterator for all possible multiplicities
            // combinations. 
            PairSetIterator<EdgeSigWrapper,Multiplicity> iter =
                new PairSetIterator<EdgeSigWrapper,Multiplicity>(mults);
            while (iter.hasNext()) {
                Set<Pair<EdgeSigWrapper,Multiplicity>> pairs = iter.next();
                // These pairs correspond to a new materialisation object.
                Materialisation newMat = this.mat.clone();
                // Adjust the shape of the new materialisation with the
                // new multiplicities.
                newMat.shape.materialiseEdgeFinish(pairs, this.edgeS);
                // Add this new materialisation to the result set of this
                // operation if the shape is admissible.
                if (newMat.shape.isAdmissible()) {
                    this.result.add(newMat);
                }
            }
        }

    }

    // ---------------------
    // Class SingulariseNode
    // ---------------------

    private static class SingulariseNode extends MatOp {

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
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof SingulariseNode) {
                SingulariseNode other = (SingulariseNode) o;
                result = this.nodeS.equals(other.nodeS);
            }
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result
                    + ((this.nodeS == null) ? 0 : this.nodeS.hashCode());
            return result;
        }

        @Override
        public int getPriority() {
            return 3;
        }

        @Override
        public void perform() { // SingulariseNode
            if (this.mat.shape.getEquivClassOf(this.nodeS).size() == 1) {
                // Nothing to do, the node is already in a singleton
                // equivalence class.
                this.result.add(this.mat);
            } else {
                EquationSystem eqSys =
                    new EquationSystem(this.mat.shape, this.nodeS);
                eqSys.solve();
                Set<Shape> validShapes = eqSys.getResultShapes();

                // Create the new materialisation objects.
                for (Shape newShape : validShapes) {
                    Materialisation newMat;
                    // Check if we need to clone the materialisation object.
                    if (validShapes.size() == 1) {
                        // No, we don't need to clone.
                        newMat = this.mat;
                    } else {
                        // Yes, we do need to clone.
                        newMat = this.mat.clone();
                    }
                    // Set the new shape to the materialisation object.
                    newMat.shape = newShape;
                    // Add this new materialisation to the result set of this
                    // operation.
                    this.result.add(newMat);
                }

            }
        }

    }

    // ------------------------------------------------------------------------
    // Test methods.
    // ------------------------------------------------------------------------

    /** Test method. */
    public static void test0() {
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
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    String test;
                    if (mat.hasConcreteMatch()) {
                        test = "concrete";
                    } else {
                        test = "abstract";
                    }
                    Shape matShape = mat.getShape();
                    new ShapeDialog(matShape, test);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Test method. */
    public static void test1() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-1").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    String test;
                    if (mat.hasConcreteMatch()) {
                        test = "concrete";
                    } else {
                        test = "abstract";
                    }
                    Shape matShape = mat.getShape();
                    new ShapeDialog(matShape, test);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Test method. */
    public static void test2() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-2").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    String test;
                    if (mat.hasConcreteMatch()) {
                        test = "concrete";
                    } else {
                        test = "abstract";
                    }
                    Shape matShape = mat.getShape();
                    new ShapeDialog(matShape, test);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Test method. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        test1();
    }

}
