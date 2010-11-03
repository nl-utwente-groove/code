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
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPOEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
 * The constructors of this class are all private. This means that it is not
 * possible to freely create objects of this class. The only way to do so is
 * by calling the static method getMaterialisations().
 * 
 * WARNING: Beware of the code in this class. It's rather tricky.
 * 
 * @author Eduardo Zambon
 */
public final class Materialisation implements Cloneable {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /**
     * Debug flag. If set to true each materialisation object will store the
     * sequence of operations performed.
     */
    private static final boolean LOG = false;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The shape we are trying to materialise. Note that this shape is
     * modified along the construction of the materialisation. This basically
     * implies that the materialisation object needs to be cloned every time
     * we perform some modifying operation on the shape. 
     */
    Shape shape;
    /**
     * The pre-match of the rule into the shape. This is the starting point for
     * the materialisation. We assume that the pre-match is a valid one.
     */
    private final RuleMatch preMatch;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     */
    final NodeEdgeMap match;
    /**
     * The queue of operations that need to be performed on the materialisation
     * object. When this queue is empty, the materialisation is complete. 
     */
    final PriorityQueue<MatOp> tasks;
    /**
     * The sequence of operations applied in this materialisation.
     */
    private final List<String> log;

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
        if (LOG) {
            this.log = new ArrayList<String>();
        } else {
            this.log = null;
        }
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
        if (LOG) {
            this.log = new ArrayList<String>(mat.log);
        } else {
            this.log = null;
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape + "Match: "
            + this.match + "\nTasks: " + this.tasks + "\nLog: " + this.log
            + "\n";
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    /**
     * Two materialisation objects are equal if the shapes and the tasks to
     * be performed are equal.
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof Materialisation)) {
            result = false;
        } else {
            Materialisation other = (Materialisation) o;
            result =
                this.shape.equals(other.shape)
                    && this.tasks.equals(other.tasks);
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.shape.hashCode();
        result = prime * result + this.tasks.hashCode();
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

        // The operations that need to be performed during the materialisation
        // phase are non-deterministic. This implies that, when performing a
        // certain operation, we get back a collection of new
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
                storeResult(mat, result);
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

    /**
     * Stores the given materialisation in the given result set. The shape
     * in the given materialisation is checked for isomorphism against the
     * ones already in the result set. If there is already an isomorphic shape
     * in the result set, the new shape is not added. 
     */
    private static void storeResult(Materialisation mat,
            Set<Materialisation> results) {
        assert mat.hasConcreteMatch();
        mat.shape.unfreezeEdges();
        results.add(mat);
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

    /** Logs the given operation string. */
    private void logOp(String op) {
        if (LOG) {
            this.log.add(op);
        }
    }

    /**
     * Checks if the match in the materialisation is concrete. See items 3, 4,
     * and 5 of Def. 35 in pg. 21 of the technical report. 
     * @return true if all three items are satisfied; false, otherwise.
     */
    private boolean hasConcreteMatch() {
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
        boolean isMatNodeOpEmpty = true;
        boolean isMatEdgeOpEmpty = true;

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
                isMatNodeOpEmpty = false;
            }
        }

        // Search for edges in the match image that have abstract
        // multiplicities.
        Set<ShapeEdge> processedEdges = new HashSet<ShapeEdge>();
        Set<ShapeEdge> edgesToFreeze = new HashSet<ShapeEdge>();
        for (Entry<Edge,Edge> edgeEntry : originalMap.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            ShapeEdge edgeS = (ShapeEdge) edgeEntry.getValue();
            if (!Util.isUnary(edgeR) && !processedEdges.contains(edgeS)) {
                // Check if the image edge in the shape has abstract
                // multiplicities.
                EdgeSignature outEs = this.shape.getEdgeOutSignature(edgeS);
                EdgeSignature inEs = this.shape.getEdgeInSignature(edgeS);
                if (!this.shape.isOutEdgeSigConcrete(outEs)
                    || !this.shape.isOutEdgeSigUnique(outEs)
                    || !this.shape.isInEdgeSigConcrete(inEs)
                    || !this.shape.isInEdgeSigUnique(inEs)) {
                    // We have an edge in the rule that was matched to an edge
                    // in the shape with an abstract multiplicity. We need to
                    // materialise this edge.
                    // Check the edges on the rule that were mapped to edgeS.
                    Set<Edge> edgesR =
                        Util.getReverseEdgeMap(originalMap, edgeS);
                    this.tasks.add(new MaterialiseEdge(this, edgeS, edgesR));
                    processedEdges.add(edgeS);
                    isMatEdgeOpEmpty = false;
                } else {
                    // We have a concrete match of an edge. Freeze it.
                    edgesToFreeze.add(edgeS);
                }
            }
        }
        this.shape.freezeEdges(edgesToFreeze);

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
        // Check if we need to pull out more nodes in the shape before
        // starting to singularise nodes.
        if (isMatNodeOpEmpty && isMatEdgeOpEmpty) {
            this.addPullNodeOps();
        }
    }

    /**
     * Extends the rule match to the given shape node. The rule edges adjacent
     * to the rule node also have their mapping updated.
     * @param nodeR - the node in the rule.
     * @param nodeS - the newly materialised node in the shape.
     */
    private void extendMatch(Node nodeR, ShapeNode nodeS,
            Set<ShapeEdge> edgesToFreeze) {
        this.match.putNode(nodeR, nodeS);
        // Look for all edges where nodeR occurs and update the edge map.
        for (Entry<Edge,Edge> edgeEntry : this.match.edgeMap().entrySet()) {
            Edge edgeR = edgeEntry.getKey();
            ShapeEdge origEdgeS = (ShapeEdge) edgeEntry.getValue();
            // Start with the nodes from the original edge.
            ShapeNode srcS = origEdgeS.source();
            ShapeNode tgtS = origEdgeS.target();

            boolean modifyMatch = false;
            if (edgeR.source().equals(nodeR)) {
                srcS = nodeS;
                modifyMatch = true;
            }
            if (edgeR.target().equals(nodeR)) {
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
                        && this.shape.isOutEdgeSigUnique(outEs)
                        && this.shape.isInEdgeSigConcrete(inEs)
                        && this.shape.isInEdgeSigUnique(inEs)) {
                        this.match.putEdge(edgeR, newEdgeS);
                        edgesToFreeze.add(newEdgeS);
                    } // else, we have an edge that needs to be materialised.
                      // Wait for the MaterialiseEdge operation to take care
                      // of this edge.
                }
            }
        }
    }

    /**
     * Creates PullNode operations for this materialisation object when needed.
     * This method checks all nodes marked to be singularised. For each such
     * nodes, we look in the neighbourhood, and if there are any shared
     * multiplicities, then we create new PullNode operations.
     */
    void addPullNodeOps() {
        PriorityQueue<PullNode> pullNodeOps = new PriorityQueue<PullNode>();
        // Check all nodes marked to be singularised.
        for (MatOp op : this.tasks) {
            if (!(op instanceof SingulariseNode)) {
                // Ignore this operation.
                continue;
            }

            // Outgoing edges.
            ShapeNode srcS = ((SingulariseNode) op).nodeS;
            for (ShapeEdge edgeS : this.shape.outBinaryEdgeSet(srcS)) {
                if (!this.shape.isFrozen(edgeS)) {
                    EquivClass<ShapeNode> srcEc =
                        this.shape.getEquivClassOf(srcS);
                    Label label = edgeS.label();
                    ShapeNode tgtS = edgeS.target();
                    EdgeSignature inEs =
                        this.shape.getEdgeSignature(tgtS, label, srcEc);
                    if (!this.shape.isInEdgeSigUnique(inEs)) {
                        // We need to pull some nodes.
                        // First, check if tgtS is abstract.
                        Multiplicity tgtMult = this.shape.getNodeMult(tgtS);
                        if (tgtMult.isAbstract()) {
                            // Get the multiplicity from the source signature.
                            EquivClass<ShapeNode> tgtEc =
                                this.shape.getEquivClassOf(tgtS);
                            EdgeSignature outEs =
                                this.shape.getEdgeSignature(srcS, label, tgtEc);
                            Multiplicity mult =
                                this.shape.getEdgeSigOutMult(outEs);
                            PullNode pullNode =
                                new PullNode(this, edgeS, tgtS, mult);
                            pullNodeOps.add(pullNode);
                        }
                    }
                }
            }

            // Incoming edges.
            ShapeNode tgtS = ((SingulariseNode) op).nodeS;
            for (ShapeEdge edgeS : this.shape.inBinaryEdgeSet(tgtS)) {
                if (!this.shape.isFrozen(edgeS)) {
                    EquivClass<ShapeNode> tgtEc =
                        this.shape.getEquivClassOf(tgtS);
                    Label label = edgeS.label();
                    srcS = edgeS.source();
                    EdgeSignature outEs =
                        this.shape.getEdgeSignature(srcS, label, tgtEc);
                    if (!this.shape.isOutEdgeSigUnique(outEs)) {
                        // We need to pull some nodes.
                        // First, check if srcS is abstract.
                        Multiplicity srcMult = this.shape.getNodeMult(srcS);
                        if (srcMult.isAbstract()) {
                            // Get the multiplicity from the source signature.
                            EquivClass<ShapeNode> srcEc =
                                this.shape.getEquivClassOf(srcS);
                            EdgeSignature inEs =
                                this.shape.getEdgeSignature(tgtS, label, srcEc);
                            Multiplicity mult =
                                this.shape.getEdgeSigInMult(inEs);
                            PullNode pullNode =
                                new PullNode(this, edgeS, srcS, mult);
                            pullNodeOps.add(pullNode);
                        }
                    }
                }
            }
        }

        this.tasks.addAll(pullNodeOps);
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // -----------
    // Class MatOp
    // -----------

    /**
     * Abstract class for the materialisation operations. The rationale on
     * creating the sub-classes of this class is:
     * - Each materialisation operation should be somewhat independent and
     *   understandable on its own.
     * - Each materialisation operation must introduce only one level of
     *   non-determinism.
     * Each operation has a priority. Zero is highest priority. Operations are
     * processed in the priority order.
     * Not all operations can be determined when the materialisation process
     * starts, so an operation can create other ones. It is expected that those
     * newly created operations have a lower priority than the one that is
     * being performed.  
     */
    private static abstract class MatOp implements Comparable<MatOp>, Cloneable {

        /** The materialisation object handled by the operation. */
        Materialisation mat;
        /** The result of performing the operation. */
        final Set<Materialisation> result;

        /** Used in the copying constructor. */
        private MatOp() {
            this.mat = null;
            this.result = new HashSet<Materialisation>();
        }

        /** Default constructor. */
        private MatOp(Materialisation mat) {
            this.mat = mat;
            this.result = new HashSet<Materialisation>();
        }

        /**
         * Compares two operations based on their priorities.
         * @return 0, if both operations have the same priorities.
         *        -1, if priority(this) < priority(o) .
         *         1, if priority(this) > priority(o) .
         */
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

        /**
         * Executes the operation in this object. This may produce zero or more
         * materialisation objects, which are stored in the result set of
         * this operation. If the execution of the operation yields zero
         * results, then it is said that the operation failed, i.e., performing
         * the operation on the materialisation object does not produce a
         * valid shape. 
         */
        abstract void perform();

        /**
         * Returns the priority of this operation. Zero is the highest
         * priority.
         */
        abstract int getPriority();

        /** Basic setter method. */
        void setMat(Materialisation mat) {
            this.mat = mat;
        }

        /** Returns true if the result set is non-empty, false otherwise. */
        private boolean isSuccesful() {
            return !this.result.isEmpty();
        }

        /** Adds the results of this operation to the collection given. */
        private void collectResults(Collection<Materialisation> collector) {
            assert this.isSuccesful() : "Invalid call!";
            collector.addAll(this.result);
        }

    }

    // ---------------------
    // Class MaterialiseNode
    // ---------------------

    /**
     * Class that represents the operation of materialising one or more nodes
     * from a collector node, i.e., a node with multiplicity greater than one.
     * The decision on which nodes have to be materialised comes from the
     * image of the pre-match of the rule.
     * The non-determinism on this operation comes from the choice on the 
     * remaining multiplicity of the collector node, once the new nodes are
     * materialised.
     * This operation creates a SingulariseNode operation for each of the
     * newly materialised nodes.
     */
    private static final class MaterialiseNode extends MatOp {

        /**
         * The collector node, from which the new nodes will be materialised.
         */
        private final ShapeNode nodeS;
        /**
         * The nodes in the LHS of the rule that were mapped to the collector
         * node by the pre-match.
         */
        private final Set<Node> nodesR;

        /** Default constructor. */
        private MaterialiseNode(Materialisation mat, ShapeNode nodeS,
                Set<Node> nodesR) {
            super(mat);
            this.nodeS = nodeS;
            this.nodesR = nodesR;
        }

        /** Copying constructor. */
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

        /**
         * Two materialise node operations are equal if the collector node and
         * the rule nodes are equal.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof MaterialiseNode)) {
                result = false;
            } else {
                MaterialiseNode other = (MaterialiseNode) o;
                result =
                    this.nodeS.equals(other.nodeS)
                        && this.nodesR.equals(other.nodesR);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.nodeS.hashCode();
            result = prime * result + this.nodesR.hashCode();
            return result;
        }

        @Override
        int getPriority() {
            return 0;
        }

        /**
         * Executes the materialise node operation.
         * The number of new copies of the collector node is determined by the
         * number of nodes of the LHS of the rule. All new materialised nodes
         * are created with multiplicity one and the mapping of the pre-match
         * is adjusted to the new nodes.
         * Keep in mind that when materialising a node, all adjacent edges
         * are duplicated.
         * The non-determinism on this operation comes from the choice on the 
         * remaining multiplicity of the collector node, when the new nodes are
         * materialised.
         * This operation creates a SingulariseNode operation for each of the
         * newly materialised nodes.  
         */
        @Override
        void perform() { // MaterialiseNode
            this.mat.logOp(this.toString());

            // Compute how many copies of the abstract node we need to
            // materialise.
            int copies = this.nodesR.size();
            // Materialise the nodes and get the new multiplicity set back.
            Set<Multiplicity> mults =
                this.mat.shape.materialiseNode(this.nodeS,
                    Multiplicity.getMultOf(1), copies);
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

                // This used to be a separate operation. It was merged here.
                // ------------------------------------------------------------
                // Begin Extend Pre-Match
                // ------------------------------------------------------------
                Set<ShapeEdge> edgesToFreeze = new HashSet<ShapeEdge>();

                // Both sets have the same size. Go over both of them at the
                // same time, and take the returned values of the iterator as
                // the match.
                Iterator<Node> nodesRIter = this.nodesR.iterator();
                Iterator<ShapeNode> newNodesIter = newNodes.iterator();
                for (int i = 0; i < copies; i++) {
                    Node nodeR = nodesRIter.next();
                    ShapeNode nodeS = newNodesIter.next();
                    // Adjust the match of the materialisation.
                    newMat.extendMatch(nodeR, nodeS, edgesToFreeze);
                }

                // Freeze all extended edges.
                newMat.shape.freezeEdges(edgesToFreeze);
                // ------------------------------------------------------------
                // End Extend Pre-Match
                // ------------------------------------------------------------

                // Make sure that all materialised nodes will be in a
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

    // ---------------------
    // Class MaterialiseEdge
    // ---------------------

    /**
     * Class that represents the operation of materialising one or more edges
     * from a collector edge, i.e., an edge with multiplicity greater than one.
     * The decision on which edges have to be materialised comes from the
     * image of the pre-match of the rule.
     * The non-determinism on this operation comes from the choice on the 
     * remaining multiplicity of the collector edge, once the new edges are
     * materialised.
     * This operation may create new PullNode operations.
     */
    private static final class MaterialiseEdge extends MatOp {

        /**
         * The collector edge, from which the new edges will be materialised.
         */
        private final ShapeEdge edgeS;
        /**
         * The edges in the LHS of the rule that were mapped to the collector
         * edge by the pre-match.
         */
        private final Set<Edge> edgesR;

        /** Default constructor. */
        private MaterialiseEdge(Materialisation mat, ShapeEdge edgeS,
                Set<Edge> edgesR) {
            super(mat);
            this.edgeS = edgeS;
            this.edgesR = edgesR;
        }

        /** Copying constructor. */
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

        /**
         * Two materialise edges operations are equal if the collector edge and
         * the rule edges are equal.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof MaterialiseEdge)) {
                result = false;
            } else {
                MaterialiseEdge other = (MaterialiseEdge) o;
                result =
                    this.edgeS.equals(other.edgeS)
                        && this.edgesR.equals(other.edgesR);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.edgeS.hashCode();
            result = prime * result + this.edgesR.hashCode();
            return result;
        }

        @Override
        int getPriority() {
            return 1;
        }

        /**
         * Executes the materialise edge operation.
         * Pre-condition: all MaterialiseNode operations have been performed,
         * and the node images in the match are final.
         * This operation goes over the images of the edges in the rule and
         * freezes them.
         * After this, an equation system is created and solved, and the valid
         * solutions correspond to the result of the operation.
         * The non-determinism on this operation comes from the choice on the 
         * remaining outgoing and incoming multiplicities of the collector edge,
         * once the new edges are materialised (frozen).
         * This operation may create new PullNode operations.
         */
        @Override
        void perform() { // MaterialiseEdge
            this.mat.logOp(this.toString());

            NodeEdgeMap match = this.mat.match;
            Shape shape = this.mat.shape;

            // Collect all signatures that will be affected by this operation.
            CountingSet<EdgeSignature> outEsSet =
                new CountingSet<EdgeSignature>();
            CountingSet<EdgeSignature> inEsSet =
                new CountingSet<EdgeSignature>();
            Set<ShapeEdge> frozenEdges = new HashSet<ShapeEdge>();
            // For each edge involved edge in the rule.
            for (Edge edgeR : this.edgesR) {
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
                    frozenEdges.add(edge);
                }
            }

            // Build the equation system and solve it.
            EdgeMatEqSystem eqSys =
                new EdgeMatEqSystem(this.mat.shape, outEsSet, inEsSet,
                    frozenEdges);
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
                // Check if we need to pull out more nodes in the shape before
                // starting to singularise nodes.
                newMat.addPullNodeOps();
                // Add this new materialisation to the result set of this
                // operation.
                this.result.add(newMat);
            }
        }

    }

    // --------------
    // Class PullNode
    // --------------

    /**
     * Class that represents the operation of pulling a node out from a
     * collector node, i.e., a node with multiplicity greater than one.
     * This operation is very similar to MaterialiseNode, with the exception
     * that only one new node is created, which can have an arbitrary positive
     * multiplicity. This new node will not be singularised later.
     * This operation does not create any new operations.
     * The source of non-determinism is the same as in the MaterialiseNode
     * operation, i.e., the choices on the remaining multiplicity of the
     * collector node.
     */
    private static final class PullNode extends MatOp {

        /** The edge that is pulling a new node from the collector node. */
        private final ShapeEdge pullingEdge;
        /** The collector node that is being pulled by the edge. */
        private final ShapeNode pulledNode;
        /** The multiplicity for the new node that will be created. */
        private final Multiplicity mult;

        /** Default constructor. */
        private PullNode(Materialisation mat, ShapeEdge pullingEdge,
                ShapeNode pulledNode, Multiplicity mult) {
            super(mat);
            this.pullingEdge = pullingEdge;
            this.pulledNode = pulledNode;
            this.mult = mult;
        }

        /** Copying constructor. */
        private PullNode(PullNode pullNode) {
            super();
            this.setMat(pullNode.mat);
            this.pullingEdge = pullNode.pullingEdge;
            this.pulledNode = pullNode.pulledNode;
            this.mult = pullNode.mult;
        }

        @Override
        public MatOp clone() {
            return new PullNode(this);
        }

        @Override
        public String toString() {
            return "PullNode: " + this.pulledNode + "(" + this.mult + "), "
                + this.pullingEdge;
        }

        /**
         * Two pull node operations are equal if the pulled node, the pulling
         * edge and the new node multiplicity are equal.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof PullNode)) {
                result = false;
            } else {
                PullNode other = (PullNode) o;
                result =
                    this.pullingEdge.equals(other.pullingEdge)
                        && this.pulledNode.equals(other.pulledNode)
                        && this.mult.equals(other.mult);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.pullingEdge.hashCode();
            result = prime * result + this.pulledNode.hashCode();
            result = prime * result + this.mult.hashCode();
            return result;
        }

        @Override
        int getPriority() {
            return 2;
        }

        /**
         * Executes the pull node operation.
         * Pre-condition: all MaterialiseNode and MaterialiseEdge operations
         * have been performed. This implies that the match is final.
         * This operation is very similar to MaterialiseNode with the exception
         * that here only one node is materialised in the shape. Node that, also
         * here, all adjacent edges are duplicated.
         * The newly created node also produces a pulled edge. When possible,
         * this pulled edge is frozen to ensure that it remains in the shape.
         * However, there cases where it is not possible at this point to freeze
         * the pulled edge. We leave to the SingulariseNode operation to decide
         * on the valid configurations that contain the pulled edge. This may
         * seem strange but actually leads to less non-determinism in the end,
         * because otherwise we would have to perform another MaterialiseEdge
         * operation on the pulled edge.
         */
        @Override
        void perform() { // PullNode
            this.mat.logOp(this.toString());

            // Look in the shaping morphism to get all the nodes that were
            // materialised from the original node.
            Set<ShapeNode> origNodes =
                this.mat.shape.getReverseNodeMap(this.pulledNode);
            // Materialise the node and get the new multiplicity set back.
            Set<Multiplicity> mults =
                this.mat.shape.materialiseNode(this.pulledNode, this.mult, 1);
            // Look in the shaping morphism to get the new node that was
            // materialised from the original node.
            Set<ShapeNode> newNodes =
                this.mat.shape.getReverseNodeMap(this.pulledNode);
            // Remove the original nodes from the set of new nodes.
            newNodes.removeAll(origNodes);
            assert newNodes.size() == 1;
            ShapeNode newNode = newNodes.iterator().next();
            Label label = this.pullingEdge.label();
            // Find the new pulled edge.
            ShapeEdge pulledEdge;
            if (this.pullingEdge.source().equals(this.pulledNode)) {
                pulledEdge =
                    this.mat.shape.getShapeEdge(newNode, label,
                        this.pullingEdge.target());
            } else {
                pulledEdge =
                    this.mat.shape.getShapeEdge(this.pullingEdge.source(),
                        label, newNode);
            }

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
                newMat.shape.setNodeMult(this.pulledNode, mult);
                // Simplify the shape, when possible, to avoid some
                // non-determinism.
                newMat.shape.removeImpossibleEdges(pulledEdge);

                // Add this new materialisation to the result set of this
                // operation.
                this.result.add(newMat);
            }
        }

    }

    // ---------------------
    // Class SingulariseNode
    // ---------------------

    /**
     * Class that represents the operation of putting a node in a singleton
     * equivalence class.
     * The non-determinism of this operation comes from the choices on the
     * edge multiplicities that are affected by the splitting. These choices
     * are made on the basis of another equation system, where the valid
     * results indicate the shape configurations.
     * This operation has the lowest priority and therefore is always executed
     * last.
     * This operation does not create new operations. 
     */
    private static final class SingulariseNode extends MatOp {

        /** The node to be singularised. */
        private final ShapeNode nodeS;

        /** Default constructor. */
        private SingulariseNode(Materialisation mat, ShapeNode nodeS) {
            super(mat);
            this.nodeS = nodeS;
        }

        /** Copying constructor. */
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

        /**
         * Two singularise node operations are equal if the nodes to be made
         * singular are equal.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof SingulariseNode)) {
                result = false;
            } else {
                SingulariseNode other = (SingulariseNode) o;
                result = this.nodeS.equals(other.nodeS);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int hash = this.nodeS.hashCode();
            return prime * hash * hash;
        }

        /**
         * Experimentation seems to show that it is better to singularise first
         * the images of the LHS of the rule. This usually leads to less
         * non-determinism. We make this choice here, on the basis of the node
         * identities. Nodes with smaller numbers are singularised first. 
         */
        @Override
        public int compareTo(MatOp op) {
            int result = super.compareTo(op);
            if (result == 0) {
                SingulariseNode other = (SingulariseNode) op;
                int thisId = this.nodeS.getNumber();
                int otherId = other.nodeS.getNumber();
                if (thisId == otherId) {
                    result = 0;
                } else if (thisId < otherId) {
                    result = -1;
                } else {
                    result = 1;
                }
            }
            return result;
        }

        @Override
        int getPriority() {
            return 3;
        }

        /**
         * Executes the singularise node operation.
         * Pre-condition: all MaterialiseNode, MaterialiseEdge and PullNode
         * operations have been performed. This implies that the match is final
         * and that the number of nodes in the shape will no longer change.
         * What is left to decide are the outgoing and incoming multiplicities
         * of the edge signatures that will be affected by this operation.
         * This decision is based on another equation system. The valid
         * solutions of this equation system give rise to valid shape
         * configurations.
         * This operation does not create new operations.
         */
        @Override
        void perform() { // SingulariseNode
            this.mat.logOp(this.toString());

            if (this.mat.shape.getEquivClassOf(this.nodeS).size() == 1) {
                // Nothing to do, the node is already in a singleton
                // equivalence class. This may happen as a side-effect of
                // another SingulariseNode operation.
                this.result.add(this.mat);
            } else {
                // Create an equation system and solve it.
                EquationSystem eqSys =
                    new NodeSingEqSystem(this.mat.shape, this.nodeS);
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

}
