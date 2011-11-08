/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.MultKind.NODE_MULT;
import groove.abstraction.neigh.Abstraction;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.BasicEvent;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleNode;
import groove.trans.SystemRecord;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public final class Materialisation {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private int stage;

    /**
     * The shape we are trying to materialise.
     * The field is final but the shape is modified by the materialisation.
     */
    private final Shape shape;
    /**
     * The original shape that started the materialisation process.
     * This is left unchanged during the materialisation.
     */
    private final Shape originalShape;
    /**
     * The morphism from the (partially) materialised shape into the original
     * shape.
     * The field is final but the morphism is modified by the materialisation.
     */
    private final ShapeMorphism morph;
    /**
     * The pre-match that triggered this materialisation.
     */
    private final Proof preMatch;
    /**
     * The matched rule.
     */
    private final Rule matchedRule;
    /**
     * A copy of the original match of the rule into the original shape.
     * This is left unchanged during the materialisation.
     */
    private final RuleToShapeMap originalMatch;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     * The field is final but the match is modified by the materialisation.
     */
    private final RuleToShapeMap match;

    // ------------------------------------------------------------------------
    // Used in first stage.
    // ------------------------------------------------------------------------

    /**
     * Set that contains all newly materialised nodes.
     */
    private Set<ShapeNode> matNodes;
    /**
     * Set that contains all newly materialised edges.
     */
    private Set<ShapeEdge> matEdges;
    /**
     * Set of possible new edges that should be included in the 
     * shape that is being materialised.
     */
    private Set<ShapeEdge> possibleEdges;
    /**
     * Map from nodes to their set of bundles.
     */
    private Map<ShapeNode,Set<EdgeBundle>> bundleMap;
    /**
     * Set of all bundles involved in the materialisation.
     */
    private Set<EdgeBundle> allBundles;

    // ------------------------------------------------------------------------
    // Used in second stage.
    // ------------------------------------------------------------------------

    /**
     * Map from nodes to their split copies.
     */
    private Map<ShapeNode,Set<ShapeNode>> nodeSplitMap;
    /**
     * Map from nodes to their multiplicities. Nodes can disappear from the
     * shape due to garbage collection, so in case the original node is gone
     * we still have its multiplicity stored.  
     */
    private Map<ShapeNode,Multiplicity> nodeSplitMultMap;
    /**
     * Auxiliary set that contains nodes that must be garbage collected at
     * the end of materialisation.
     */
    private Set<ShapeNode> garbageNodes;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation(Shape shape, Proof preMatch) {
        this.stage = 1;
        this.originalShape = shape;
        this.preMatch = preMatch;
        this.originalMatch = (RuleToShapeMap) preMatch.getPatternMap();
        // Fix the original match to prevent modification.
        this.originalMatch.setFixed();
        this.matchedRule = preMatch.getRule();
        if (this.isRuleModifying()) {
            this.shape = this.originalShape.clone();
            this.morph =
                ShapeMorphism.createIdentityMorphism(this.shape,
                    this.originalShape);
            this.match = this.originalMatch.clone();
            // Create new auxiliary structures.
            this.matNodes = new MyHashSet<ShapeNode>();
            this.matEdges = new MyHashSet<ShapeEdge>();
            this.possibleEdges = new MyHashSet<ShapeEdge>();
            this.bundleMap = new MyHashMap<ShapeNode,Set<EdgeBundle>>();
            this.allBundles = new MyHashSet<EdgeBundle>();
        } else {
            // Nothing to do, we just return immediately.
            this.shape = null;
            this.morph = null;
            this.match = null;
        }
    }

    /**
     * Copying constructor. Clones the structures of the given materialisation
     * object that can be modified. 
     */
    private Materialisation(Materialisation mat) {
        this.stage = mat.stage;
        // No need to clone the original objects since they are fixed.
        this.originalShape = mat.originalShape;
        this.preMatch = mat.preMatch;
        this.originalMatch = mat.originalMatch;
        this.matchedRule = mat.matchedRule;
        // The match should also be fixed.
        assert mat.match.isFixed();
        this.match = mat.match;
        // Clone the shape.
        this.shape = mat.shape.clone();
        // Since the shape can still be modified, we also need to clone the
        // morphism into the original shape.
        this.morph = mat.morph.clone();
        // Clone the rest of structures according to the stage.
        if (this.stage == 1) {
            this.bundleMap = new MyHashMap<ShapeNode,Set<EdgeBundle>>();
            this.allBundles = new MyHashSet<EdgeBundle>();
            for (ShapeNode node : mat.bundleMap.keySet()) {
                Set<EdgeBundle> bundles = new MyHashSet<EdgeBundle>();
                this.bundleMap.put(node, bundles);
                for (EdgeBundle matBundle : mat.getBundles(node)) {
                    EdgeBundle bundle = matBundle.clone();
                    bundles.add(bundle);
                    this.allBundles.add(bundle);
                }
            }
        } else if (this.stage == 3) {
            // At this stage we don't have to clone these structures.
            // Just update the references.
            this.nodeSplitMap = mat.nodeSplitMap;
            this.nodeSplitMultMap = mat.nodeSplitMultMap;
            this.garbageNodes = null;
        } else {
            // this.stage == 2. We should not clone the materialisation object
            // during second stage.
            assert false;
        }
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
            Proof preMatch) {
        Set<Materialisation> result;
        Materialisation initialMat = new Materialisation(shape, preMatch);
        if (initialMat.isRuleModifying()) {
            result = initialMat.compute();
        } else {
            result = new MyHashSet<Materialisation>();
            result.add(initialMat);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape + "Match: "
            + this.match + "\n";
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Common methods to all stages.
    // ------------------------------------------------------------------------

    private boolean isRuleModifying() {
        return this.matchedRule.isModifying();
    }

    /** Basic getter method. */
    public int getStage() {
        return this.stage;
    }

    /** Basic getter method. */
    public Shape getShape() {
        return this.shape;
    }

    /** Basic getter method. */
    public Shape getOriginalShape() {
        return this.originalShape;
    }

    /** Basic getter method. */
    public RuleToShapeMap getMatch() {
        return this.match;
    }

    /** Basic getter method. */
    public RuleToShapeMap getOriginalMatch() {
        return this.originalMatch;
    }

    /** Basic getter method. */
    ShapeMorphism getShapeMorphism() {
        return this.morph;
    }

    List<RuleNode> getSingularRuleNodes() {
        return Arrays.asList(this.matchedRule.getAnchorNodes());
    }

    Set<EdgeBundle> getBundles() {
        return this.allBundles;
    }

    /** Returns the bundles from the given node. */
    Set<EdgeBundle> getBundles(ShapeNode node) {
        Set<EdgeBundle> result = this.bundleMap.get(node);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    EdgeBundle getBundle(ShapeNode node, EdgeSignature origEs) {
        EdgeBundle result = null;
        for (EdgeBundle bundle : this.getBundles(node)) {
            if (bundle.isEqual(node, origEs)) {
                result = bundle;
                break;
            }
        }
        return result;
    }

    /**
     * Looks at the keys of the bundle map for an edge bundle compatible
     * with the given edge. If no suitable edge bundle is found, returns
     * null.
     */
    EdgeBundle maybeGetBundle(ShapeEdge edge, EdgeMultDir direction) {
        EdgeSignature origEs = this.getOrigEs(edge, direction);
        return this.getBundle(edge.incident(direction), origEs);
    }

    /**
     * Looks at the keys of the bundle map for an edge bundle compatible
     * with the given edge. If no suitable edge signature is found, a new one
     * is created and added to the proper structures.
     */
    EdgeBundle getBundle(ShapeEdge edge, EdgeMultDir direction) {
        return this.createBundle(edge, direction, true);
    }

    EdgeBundle createBundle(EdgeSignature origEs, ShapeNode node) {
        Multiplicity origEsMult = this.getOrigEsMult(origEs);
        return new EdgeBundle(origEs, origEsMult, node);
    }

    EdgeBundle createBundle(ShapeEdge edge, EdgeMultDir direction, boolean store) {
        ShapeNode node = edge.incident(direction);
        EdgeSignature origEs = this.getOrigEs(edge, direction);
        return this.createBundle(node, origEs, store);
    }

    EdgeBundle createBundle(ShapeNode node, EdgeSignature origEs, boolean store) {
        EdgeBundle result = this.getBundle(node, origEs);
        if (result == null) {
            result = this.createBundle(origEs, node);
            if (store) {
                this.addBundle(result);
            }
        }
        return result;
    }

    void addBundle(EdgeBundle bundle) {
        Set<EdgeBundle> bundles = this.bundleMap.get(bundle.node);
        if (bundles == null) {
            bundles = new MyHashSet<EdgeBundle>();
            this.bundleMap.put(bundle.node, bundles);
        }
        bundles.add(bundle);
        this.allBundles.add(bundle);
    }

    void removeNodeFromBundleMap(ShapeNode node) {
        this.allBundles.removeAll(this.getBundles(node));
        this.bundleMap.remove(node);
    }

    EdgeSignature getOrigEs(ShapeEdge edge, EdgeMultDir direction) {
        return this.getShapeMorphism().getEdgeSignature(
            this.getOriginalShape(),
            this.getShape().getEdgeSignature(edge, direction));
    }

    Multiplicity getOrigEsMult(EdgeSignature origEs) {
        return this.getOriginalShape().getEdgeSigMult(origEs);
    }

    /** Returns true if this edge is in the co-domain of the match. */
    boolean isFixed(ShapeEdge edge) {
        return !this.match.getPreImages(edge).isEmpty();
    }

    /**
     * Removes elements from the morphism that are no longer present in the
     * shape.
     */
    void updateShapeMorphism() {
        Set<HostNode> nodesToRemove = new MyHashSet<HostNode>();
        for (Entry<HostNode,HostNode> entry : this.morph.nodeMap().entrySet()) {
            HostNode key = entry.getKey();
            HostNode value = entry.getValue();
            if (!this.shape.containsNode(key)
                || !this.originalShape.containsNode(value)) {
                nodesToRemove.add(key);
            }
        }
        for (HostNode nodeToRemove : nodesToRemove) {
            this.morph.removeNode(nodeToRemove);
        }
        Set<HostEdge> edgesToRemove = new MyHashSet<HostEdge>();
        for (Entry<HostEdge,HostEdge> entry : this.morph.edgeMap().entrySet()) {
            HostEdge key = entry.getKey();
            HostEdge value = entry.getValue();
            if (!this.shape.containsEdge(key)
                || !this.originalShape.containsEdge(value)) {
                edgesToRemove.add(key);
            }
        }
        for (HostEdge edgeToRemove : edgesToRemove) {
            this.morph.removeEdge(edgeToRemove);
        }
    }

    /**
     * Returns true if the morphism from the materialised shape to the original
     * one is consistent.
     */
    boolean isShapeMorphConsistent() {
        return this.morph.isValid(this.shape, this.originalShape)
            && this.morph.isConsistent(this.shape, this.originalShape);
    }

    /**
     * Applies the rule match defined by this materialisation and returns the
     * transformed shape, which is not yet normalised.
     */
    public Pair<Shape,RuleEvent> applyMatch(SystemRecord record) {
        RuleEvent event;
        Shape result;
        if (this.isRuleModifying()) {
            assert this.hasConcreteMatch();
            event = new BasicEvent(this.matchedRule, this.match, true);
            ((BasicEvent) event).setAggressiveNodeReuse();
            if (record != null) {
                event = record.normaliseEvent(event);
            }
            ShapeRuleApplication app =
                new ShapeRuleApplication(event, this.shape);
            result = app.getTarget();
        } else {
            assert record != null;
            event = record.getEvent(this.preMatch);
            result = this.originalShape;
        }
        return new Pair<Shape,RuleEvent>(result, event);
    }

    /**
     * Checks if the match in the materialisation is concrete. See items 3, 4,
     * and 5 of Def. 35 in pg. 21 of the technical report. 
     * @return true if all three items are satisfied; false, otherwise.
     */
    private boolean hasConcreteMatch() {
        assert this.match.isConsistent();
        // Check for items 3 and 4.
        boolean complyToNodeMult = true;
        boolean complyToEquivClass = true;
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);
        // For all nodes in the image of the LHS.
        for (ShapeNode nodeS : this.match.nodeMapValueSet()) {
            // Item 3: check that all nodes in the image of the LHS have
            // multiplicity one.
            if (!this.shape.getNodeMult(nodeS).equals(one)) {
                complyToNodeMult = false;
                break;
            }
            // Item 4: check that for all nodes in the image of the LHS, their
            // equivalence class is a singleton set.
            if (!this.shape.getEquivClassOf(nodeS).isSingleton()) {
                RuleNode nodeR =
                    this.match.getPreImages(nodeS).iterator().next();
                if (this.getSingularRuleNodes().contains(nodeR)) {
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
            Set<HostEdge> intersectEdges = new MyHashSet<HostEdge>();
            // For all binary labels.
            for (TypeLabel label : Util.getBinaryLabels(this.shape)) {
                // For all nodes v in the image of the LHS.
                for (ShapeNode v : this.match.nodeMapValueSet()) {
                    // For all nodes w in the image of the LHS.
                    for (ShapeNode w : this.match.nodeMapValueSet()) {
                        EquivClass<ShapeNode> ecW =
                            this.shape.getEquivClassOf(w);
                        EdgeSignature outEs =
                            this.shape.getEdgeSignature(EdgeMultDir.OUTGOING,
                                v, label, ecW);
                        Multiplicity outMult = this.shape.getEdgeSigMult(outEs);
                        EdgeSignature inEs =
                            this.shape.getEdgeSignature(EdgeMultDir.INCOMING,
                                v, label, ecW);
                        Multiplicity inMult = this.shape.getEdgeSigMult(inEs);
                        Util.getIntersectEdges(this.shape, v, w, label,
                            intersectEdges);
                        Multiplicity vInterWMult =
                            Multiplicity.getEdgeSetMult(intersectEdges);
                        Util.getIntersectEdges(this.shape, w, v, label,
                            intersectEdges);
                        Multiplicity wInterVMult =
                            Multiplicity.getEdgeSetMult(intersectEdges);
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

    // ------------------------------------------------------------------------
    // Methods for first stage.
    // ------------------------------------------------------------------------

    /** Returns the set of edges involved in the materialisation. */
    Set<ShapeEdge> getAffectedEdges() {
        assert this.stage == 1;
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        result.addAll(this.matEdges);
        result.addAll(this.possibleEdges);
        return result;
    }

    /** Records the materialisation of the new node from the collector node. */
    public void addMatNode(ShapeNode newNode, ShapeNode collectorNode,
            RuleNode nodeR) {
        assert this.stage == 1;
        assert this.shape.containsNode(newNode);
        this.match.putNode(nodeR, newNode);
        this.morph.putNode(newNode, collectorNode);
        this.matNodes.add(newNode);
    }

    /** Records the materialisation of the new edge from the inconsistent edge. */
    public void addMatEdge(ShapeEdge newEdge, ShapeEdge inconsistentEdge,
            RuleEdge edgeR) {
        assert this.stage == 1;
        assert this.shape.containsEdge(newEdge);
        this.match.putEdge(edgeR, newEdge);
        this.morph.putEdge(newEdge, inconsistentEdge);
        this.matEdges.add(newEdge);
    }

    /** Updates the proper sets with the given collector node. */
    public void handleCollectorNode(ShapeNode collectorNode) {
        assert this.stage == 1;
        assert this.shape.containsNode(collectorNode);
        this.matNodes.add(collectorNode);
    }

    /** Updates the proper sets with the given inconsistent edge. */
    public void handleInconsistentEdge(ShapeEdge inconsistentEdge) {
        assert this.stage == 1;
        if (!this.shape.containsEdge(inconsistentEdge)) {
            // The edge was removed from the shape and we have a
            // dangling reference in the shape morphism.
            this.morph.removeEdge(inconsistentEdge);
        } else {
            this.matEdges.add(inconsistentEdge);
        }
    }

    /**
     * Adds the given edge to the list of possible edges and updates the
     * morphism to the original edge.
     */
    public void addPossibleEdge(ShapeEdge possibleEdge, ShapeEdge origEdge) {
        assert this.stage == 1 || this.stage == 2;
        assert !this.shape.containsEdge(possibleEdge);
        this.possibleEdges.add(possibleEdge);
        this.morph.putEdge(possibleEdge, origEdge);
    }

    private Set<Materialisation> compute() {
        assert this.stage == 1;
        // Search for nodes in the original match image that have to be
        // materialised. 
        for (ShapeNode nodeS : this.originalMatch.nodeMapValueSet()) {
            if (this.originalShape.getNodeMult(nodeS).isCollector()) {
                // We have a rule node that was matched to a collector
                // node. We need to materialise this collector node.
                this.shape.materialiseNode(this, nodeS);
            }
        }

        // Search for edges in the match image that have to be materialised. 
        for (ShapeEdge edgeS : this.match.getInconsistentEdges()) {
            // This edge was affected by a node materialisation.
            // We have to materialise the edge as well.
            this.shape.materialiseEdge(this, edgeS);
        }

        assert this.match.isConsistent();
        this.match.setFixed();

        // Create the set of possible edges that can occur on the final shape.
        this.createPossibleEdges(this.morph.clone(), this.shape,
            this.originalShape, this.matNodes);

        // Make sure that all shape nodes in the match image are in a
        // singleton equivalence class.
        for (RuleNode singularNodeR : this.getSingularRuleNodes()) {
            ShapeNode nodeS = this.match.getNode(singularNodeR);
            if (!this.shape.getEquivClassOf(nodeS).isSingleton()) {
                // We have a rule node that was mapped to a shape node that is
                // not in its own equivalence class. We need to singularise the
                // shape node.
                this.shape.singulariseNode(this, nodeS);
            }
        }

        // The deterministic steps of the materialisation are done.
        // Create a new equation system for this materialisation and return
        // the resulting materialisations from the solution.
        this.computeBundles(this.getAffectedEdges());
        ResultSet result = new ResultSet();
        EquationSystem.newEqSys(this).solve(result);

        return result;
    }

    /**
     * Duplicates all incoming and outgoing edges for the materialised nodes.
     * These edges are not added to the shape since they may not be present
     * in the final configuration. For now they are put in a set of possible
     * edges and later used by the equation system to decide on the final
     * configuration of the shape.
     */
    private void createPossibleEdges(ShapeMorphism morph, Shape from, Shape to,
            Set<ShapeNode> nodes) {
        assert this.stage == 1 || this.stage == 2;
        if (nodes.isEmpty()) {
            return;
        }
        // Fix the morphism because we need pre-images.
        morph.setFixed();
        // For all nodes involved in the materialisation.
        for (ShapeNode node : nodes) {
            // Get the original node from the shape morphism.
            ShapeNode origNode = morph.getNode(node);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Look for all edges connected to the original node in the
                // original shape.     
                for (ShapeEdge origEdge : to.binaryEdgeSet(origNode, direction)) {
                    ShapeNode origOppNode = origEdge.opposite(direction);
                    for (ShapeNode oppNode : morph.getPreImages(origOppNode)) {
                        ShapeEdge possibleEdge =
                            from.createEdge(node, oppNode, origEdge.label(),
                                direction);
                        if (!from.containsEdge(possibleEdge)) {
                            this.addPossibleEdge(possibleEdge, origEdge);
                        }
                    }
                }
            }
        }
    }

    private void computeBundles(Set<ShapeEdge> toProcess) {
        assert this.stage == 1;
        // Keep adding edges and bundles until we reach a fix point.
        Set<ShapeEdge> handledEdges = new MyHashSet<ShapeEdge>();
        Set<EdgeBundle> handledBundles = new MyHashSet<EdgeBundle>();

        while (!toProcess.isEmpty()) {
            for (ShapeEdge edge : toProcess) {
                for (EdgeMultDir direction : EdgeMultDir.values()) {
                    EdgeBundle bundle = this.getBundle(edge, direction);
                    bundle.addEdge(this.shape, edge, direction);
                }
                handledEdges.add(edge);
            }
            toProcess.clear();
            bundleLoop: for (EdgeBundle bundle : this.getBundles()) {
                if (handledBundles.contains(bundle)) {
                    continue bundleLoop;
                }
                bundle.computeAdditionalEdges(this);
                for (ShapeEdge edge : bundle.getEdges()) {
                    if (!handledEdges.contains(edge)) {
                        toProcess.add(edge);
                    }
                }
                handledBundles.add(bundle);
            }
        }
    }

    void removeUnconnectedNode(ShapeNode nodeToRemove) {
        assert this.stage == 1;
        this.morph.removeNode(nodeToRemove);
        // EZ says: can't remove the node here because we may have conflicts
        // in node identities when we move to second stage and try split nodes.
        // this.shape.removeNode(nodeToRemove);
        this.getGarbageNodeSet().add(nodeToRemove);
        this.removeNodeFromBundleMap(nodeToRemove);
    }

    // ------------------------------------------------------------------------
    // Methods for second stage.
    // ------------------------------------------------------------------------

    void moveToSecondStage(Set<EdgeBundle> nonSingBundles) {
        assert this.stage == 1;

        this.stage++;
        this.matEdges = null;

        if (nonSingBundles.isEmpty()) {
            // We don't need to split nodes. But maybe we have to collect
            // garbage nodes.
            this.simpleGarbageCollectNodes();
            return;
        }

        this.matNodes = new MyHashSet<ShapeNode>();
        this.possibleEdges = new MyHashSet<ShapeEdge>();
        this.nodeSplitMap = new MyHashMap<ShapeNode,Set<ShapeNode>>();
        this.nodeSplitMultMap = new MyHashMap<ShapeNode,Multiplicity>();

        // Compute the set of nodes that require splitting.
        Set<ShapeNode> origNodesToSplit = new MyHashSet<ShapeNode>();
        Map<ShapeNode,Set<EdgeBundle>> auxBundleMap =
            new MyHashMap<ShapeNode,Set<EdgeBundle>>();
        for (EdgeBundle nonSingBundle : nonSingBundles) {
            ShapeNode node = nonSingBundle.node;
            Set<EdgeBundle> bundles;
            if (origNodesToSplit.add(node)) {
                bundles = new MyHashSet<EdgeBundle>();
                auxBundleMap.put(node, bundles);
            } else {
                bundles = auxBundleMap.get(node);
            }
            bundles.add(nonSingBundle);
        }

        // Auxiliary structures.
        Shape shape = this.getShape();
        ShapeMorphism auxMorph =
            ShapeMorphism.createIdentityMorphism(shape, shape);
        Map<ShapeNode,PowerSetIterator> iterMap =
            new MyHashMap<ShapeNode,PowerSetIterator>();

        // Fill in the node split map and try to create new nodes. 
        for (ShapeNode origNode : origNodesToSplit) {
            // For sure we'll have at least one split node, so we can
            // initialise the map already.
            this.nodeSplitMap.put(origNode, new MyHashSet<ShapeNode>());
            // Store the multiplicity of the original node in case it is
            // garbage collected.
            this.nodeSplitMultMap.put(origNode, shape.getNodeMult(origNode));
            // Get the non-singular bundles of the original node.
            Set<EdgeBundle> bundles = auxBundleMap.get(origNode);
            // Iterate over possible configurations and split the nodes.
            PowerSetIterator iter = new PowerSetIterator(bundles, true);
            // Split the nodes in one go to improve performance.
            shape.splitNode(this, origNode, iter.resultCount());
            // Store the iterator in the map.
            iterMap.put(origNode, iter);
        }

        // Now go over the split nodes and create the edges.
        for (ShapeNode origNode : origNodesToSplit) {
            PowerSetIterator iter = iterMap.get(origNode);
            for (ShapeNode splitNode : this.nodeSplitMap.get(origNode)) {
                // First update the auxiliary morphism that is used later to
                // create the remaining edges.
                auxMorph.putNode(splitNode, origNode);
                // Add the new edges to the split node.
                this.addNewEdges(origNode, iter.next(), splitNode);
            }
            assert !iter.hasNext();
        }

        // Now it's safe to remove the nodes that were marked as garbage during
        // the first stage.
        this.simpleGarbageCollectNodes();

        // Now collect the remaining edges created by the node split that still
        // give rise to admissible configurations.
        this.createPossibleEdges(auxMorph, shape, shape, this.matNodes);
        for (ShapeEdge edgeToAdd : this.possibleEdges) {
            shape.addEdgeWithoutCheck(edgeToAdd);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                EdgeBundle bundle = this.getBundle(edgeToAdd, direction);
                bundle.addEdge(this.shape, edgeToAdd, direction);
            }
        }

        // And then update all bundles, since the edge signatures changed.
        for (EdgeBundle bundle : this.getBundles()) {
            bundle.update(this);
        }
    }

    /** Records the split of the new node from the collector node. */
    public void addSplitNode(ShapeNode newNode, ShapeNode origNode) {
        assert this.stage == 2;
        assert this.shape.containsNode(origNode);
        assert this.shape.containsNode(newNode);
        this.morph.putNode(newNode, origNode);
        Set<ShapeNode> copies = this.nodeSplitMap.get(origNode);
        assert copies != null;
        copies.add(newNode);
        // Add the new node to the set of affected nodes.
        this.matNodes.add(newNode);
    }

    private void addNewEdges(ShapeNode origNode,
            Map<EdgeBundle,Set<ShapeEdge>> origMap, ShapeNode newNode) {
        assert this.stage == 2;
        Set<ShapeEdge> newEdges = new MyHashSet<ShapeEdge>();
        for (EdgeBundle origBundle : origMap.keySet()) {
            EdgeMultDir direction = origBundle.direction;
            EdgeMultDir reverse = direction.reverse();
            // Create a new bundle for the new node.
            EdgeBundle newBundle =
                this.createBundle(origBundle.origEs, newNode);
            for (ShapeEdge origEdge : origMap.get(origBundle)) {
                this.routeNewEdges(origNode, origEdge, newNode, direction,
                    newEdges);
                for (ShapeEdge newEdge : newEdges) {
                    // Add the new edge to the new bundle.
                    newBundle.addEdge(this.shape, newEdge, direction);
                    newBundle.setEdgeAsFixed(newEdge);
                    // Add the new edge to the shape.
                    this.shape.addEdgeWithoutCheck(newEdge);
                    // Update the shape morphism.
                    this.morph.putEdge(newEdge, this.morph.getEdge(origEdge));
                    // Now handle the opposite bundle.
                    EdgeBundle oppBundle = this.getBundle(newEdge, reverse);
                    oppBundle.addEdge(this.shape, newEdge, reverse);
                    oppBundle.setEdgeAsFixed(newEdge);
                }
                newEdges.clear();
            }
            this.addBundle(newBundle);
        }
    }

    private void routeNewEdges(ShapeNode origNode, ShapeEdge origEdge,
            ShapeNode newNode, EdgeMultDir direction, Set<ShapeEdge> result) {
        assert this.stage == 2;
        TypeLabel label = origEdge.label();
        ShapeNode incident = newNode;
        ShapeNode opposite = origEdge.opposite(direction);
        ShapeEdge newEdge =
            this.shape.createEdge(incident, opposite, label, direction);
        result.add(newEdge);
        Set<ShapeNode> splitNodes = this.nodeSplitMap.get(opposite);
        if (splitNodes != null) {
            for (ShapeNode newOpposite : splitNodes) {
                newEdge =
                    this.shape.createEdge(incident, newOpposite, label,
                        direction);
                result.add(newEdge);
            }
        }
    }

    /** Returns the set of nodes involved in the materialisation. */
    Set<ShapeNode> getAffectedNodes() {
        assert this.stage == 2;
        return this.bundleMap.keySet();
    }

    boolean requiresThirdStage() {
        return this.nodeSplitMap != null && !this.nodeSplitMap.isEmpty();
    }

    /** Mark nodes that cannot exist. */
    void markGarbageNodes(Set<ShapeNode> garbageNodes) {
        assert this.stage == 2 || this.stage == 3;
        assert garbageNodes != null;
        garbageNodes.clear();
        Shape shape = this.shape;
        Shape origShape = this.originalShape;
        this.updateShapeMorphism();
        ShapeMorphism morph = this.morph.clone();
        Set<EdgeSignature> preImgEs = new MyHashSet<EdgeSignature>();
        // We need to check for nodes that got disconnected...
        for (ShapeNode origNode : origShape.nodeSet()) {
            for (ShapeNode node : morph.getPreImages(origNode)) {
                for (EdgeSignature origEs : origShape.getEdgeSignatures(origNode)) {
                    // We know that the original signature has edges.
                    // Check if the pre-images also do.
                    morph.getPreImages(shape, node, origEs, false, preImgEs);
                    if (preImgEs.isEmpty()) {
                        // The node got disconnected and therefore cannot exist.
                        garbageNodes.add(node);
                    }
                }
            }
        }
    }

    Set<ShapeNode> getGarbageNodeSet() {
        if (this.garbageNodes == null) {
            this.garbageNodes = new MyHashSet<ShapeNode>();
        }
        return this.garbageNodes;
    }

    /** Remove the nodes from the shape that were marked as garbage. */
    void recursiveGarbageCollectNodes() {
        assert this.stage == 2 || this.stage == 3;
        MyHashSet<ShapeNode> garbageNodes = new MyHashSet<ShapeNode>();
        this.markGarbageNodes(garbageNodes);
        while (!garbageNodes.isEmpty()) {
            for (ShapeNode garbageNode : garbageNodes) {
                this.shape.removeNode(garbageNode);
            }
            // Need to search for garbage nodes again because of possible
            // new disconnections.
            this.markGarbageNodes(garbageNodes);
        }
    }

    void simpleGarbageCollectNodes() {
        assert this.stage == 2;
        if (this.garbageNodes != null) {
            for (ShapeNode garbageNode : this.garbageNodes) {
                // No need to check anything here, the node is guaranteed to
                // be unconnected.
                this.shape.removeNode(garbageNode);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Methods for third stage.
    // ------------------------------------------------------------------------

    /** Moves the materialisation object to the third and last stage. */
    void moveToThirdStage() {
        assert this.stage == 2;
        this.stage++;
    }

    /** Basic getter method. */
    Map<ShapeNode,Set<ShapeNode>> getNodeSplitMap() {
        assert this.stage == 3;
        return this.nodeSplitMap;
    }

    Multiplicity getOrigNodeMult(ShapeNode origNode) {
        assert this.stage == 3;
        return this.nodeSplitMultMap.get(origNode);
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ---------
    // ResultSet
    // ---------

    private static class ResultSet extends MyHashSet<Materialisation> {
        @Override
        public boolean add(Materialisation mat) {
            mat.recursiveGarbageCollectNodes();
            mat.updateShapeMorphism();
            assert mat.isShapeMorphConsistent();
            assert mat.getShape().isInvariantOK();
            return super.add(mat);
        }
    }

    /** Used for tests. */
    public static void main(String args[]) {
        String DIRECTORY = "junit/abstraction/basic-tests.gps/";
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Abstraction.initialise();
        File file = new File(DIRECTORY);
        try {
            String number = "9";
            GrammarModel view = GrammarModel.newInstance(file, false);
            HostGraph graph =
                view.getHostModel("materialisation-test-" + number).toResource();
            Shape shape = Shape.createShape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-" + number);
            Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
            for (Proof preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    ShapePreviewDialog.showShape(mat.shape);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String args[]) {
         String DIRECTORY = "junit/abstraction/basic-tests.gps/";
         Parameters.setNodeMultBound(1);
         Parameters.setEdgeMultBound(2);
         Abstraction.initialise();
         File file = new File(DIRECTORY + "materialisation-test-13.gxl");
         File grammarFile = new File(DIRECTORY);
         try {
             String number = "13";
             GrammarModel view = GrammarModel.newInstance(grammarFile, false);
             GraphGrammar grammar = view.toGrammar();
             Rule rule = grammar.getRule("test-mat-" + number);
             Shape shape = ShapeGxl.getInstance().unmarshalShape(file);
             Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
             for (Proof preMatch : preMatches) {
                 Set<Materialisation> mats =
                     Materialisation.getMaterialisations(shape, preMatch);
                 for (Materialisation mat : mats) {
                     ShapePreviewDialog.showShape(mat.shape);
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         } catch (FormatException e) {
             e.printStackTrace();
         }
     }*/
}
