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
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
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
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleNode;
import groove.trans.SystemRecord;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
    /**
     * Auxiliary set that contains all newly materialised nodes.
     */
    private final THashSet<ShapeNode> matNodes;
    /**
     * Auxiliary set that contains all newly materialised edges.
     */
    private final THashSet<ShapeEdge> matEdges;
    /**
     * Auxiliary set of possible new edges that should be included in the 
     * shape that is being materialised.
     */
    private final THashSet<ShapeEdge> possibleEdges;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation(Shape shape, Proof preMatch) {
        this.originalShape = shape;
        this.shape = this.originalShape.clone();
        this.morph =
            ShapeMorphism.createIdentityMorphism(this.shape, this.originalShape);
        this.matchedRule = preMatch.getRule();
        this.originalMatch = (RuleToShapeMap) preMatch.getPatternMap();
        // Fix the original match to prevent modification.
        this.originalMatch.setFixed();
        this.match = this.originalMatch.clone();
        // Create new auxiliary structures.
        this.matNodes = new THashSet<ShapeNode>();
        this.matEdges = new THashSet<ShapeEdge>();
        this.possibleEdges = new THashSet<ShapeEdge>();
    }

    /**
     * Copying constructor. Clones the structures of the given materialisation
     * object that can be modified. 
     */
    private Materialisation(Materialisation mat) {
        // No need to clone the original objects since they are fixed.
        this.originalShape = mat.originalShape;
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
        // No need for the auxiliary structures at this point.
        this.matNodes = null;
        this.matEdges = null;
        this.possibleEdges = null;
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
        Materialisation initialMat = new Materialisation(shape, preMatch);
        return initialMat.compute();
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
    public ShapeMorphism getShapeMorphism() {
        return this.morph;
    }

    /** Returns the set of nodes involved in the materialisation. */
    public Set<ShapeNode> getAffectedNodes() {
        Set<ShapeNode> result = new THashSet<ShapeNode>();
        result.addAll(this.matNodes);
        for (ShapeEdge matEdge : this.matEdges) {
            result.add(matEdge.source());
            result.add(matEdge.target());
        }
        for (ShapeEdge possibleEdge : this.possibleEdges) {
            result.add(possibleEdge.source());
            result.add(possibleEdge.target());
        }
        return Collections.unmodifiableSet(result);
    }

    /** Returns the set of edges involved in the materialisation. */
    public Set<ShapeEdge> getAffectedEdges() {
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        result.addAll(this.matEdges);
        result.addAll(this.possibleEdges);
        return Collections.unmodifiableSet(result);
    }

    /** EDUARDO: Comment this... */
    public void addMatNode(ShapeNode newNode, ShapeNode collectorNode,
            RuleNode nodeR) {
        assert this.shape.containsNode(newNode);
        this.match.putNode(nodeR, newNode);
        this.morph.putNode(newNode, collectorNode);
        this.matNodes.add(newNode);
    }

    /** EDUARDO: Comment this... */
    public void addMatEdge(ShapeEdge newEdge, ShapeEdge inconsistentEdge,
            RuleEdge edgeR) {
        assert this.shape.containsEdge(newEdge);
        this.match.putEdge(edgeR, newEdge);
        this.morph.putEdge(newEdge, inconsistentEdge);
        this.matEdges.add(newEdge);
    }

    /** EDUARDO: Comment this... */
    public void handleCollectorNode(ShapeNode collectorNode) {
        if (!this.shape.containsNode(collectorNode)) {
            // The collector node was removed from the shape and we have a
            // dangling reference in the shape morphism.
            this.morph.removeNode(collectorNode);
        } else {
            this.matNodes.add(collectorNode);
        }
    }

    /** EDUARDO: Comment this... */
    public void handleInconsistentEdge(ShapeEdge inconsistentEdge) {
        if (!this.shape.containsEdge(inconsistentEdge)) {
            // The edge was removed from the shape and we have a
            // dangling reference in the shape morphism.
            this.morph.removeEdge(inconsistentEdge);
        } else {
            this.matEdges.add(inconsistentEdge);
        }
    }

    /** EDUARDO: Comment this... */
    public void addPossibleEdge(ShapeEdge possibleEdge, ShapeEdge origEdge) {
        assert !this.shape.containsEdge(possibleEdge);
        this.possibleEdges.add(possibleEdge);
        // Use the shape morphism to store additional info.
        this.morph.putEdge(possibleEdge, origEdge);
    }

    /** EDUARDO: Comment this... */
    public boolean isFixed(ShapeEdge edge) {
        return !this.match.getPreImages(edge).isEmpty();
    }

    /**
     * Applies the rule match defined by this materialisation and returns the
     * transformed shape, which is not yet normalised.
     */
    public Shape applyMatch(SystemRecord record) {
        assert this.hasConcreteMatch();
        RuleEvent event = new BasicEvent(this.matchedRule, this.match, true);
        if (record != null) {
            event = record.normaliseEvent(event);
        }
        RuleApplication app = new RuleApplication(event, this.shape);
        Shape result = (Shape) app.getTarget();
        return result;
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
                complyToEquivClass = false;
                break;
            }
        }

        // Item 5: check that for any two nodes in the image of the LHS, their
        // outgoing and incoming multiplicities are equal and correspond to
        // the number of edges in the underlying graph structure of the shape.
        boolean complyToEdgeMult = true;
        if (complyToNodeMult && complyToEquivClass) {
            // For all binary labels.
            for (TypeLabel label : Util.getBinaryLabels(this.shape)) {
                // For all nodes v in the image of the LHS.
                for (ShapeNode v : this.match.nodeMapValueSet()) {
                    // For all nodes w in the image of the LHS.
                    for (ShapeNode w : this.match.nodeMapValueSet()) {
                        EquivClass<ShapeNode> ecW =
                            this.shape.getEquivClassOf(w);
                        EdgeSignature es =
                            this.shape.getEdgeSignature(v, label, ecW);
                        Multiplicity outMult =
                            this.shape.getEdgeSigMult(es, EdgeMultDir.OUTGOING);
                        Multiplicity inMult =
                            this.shape.getEdgeSigMult(es, EdgeMultDir.INCOMING);
                        Set<HostEdge> vInterW =
                            Util.getIntersectEdges(this.shape, v, w, label);
                        Multiplicity vInterWMult =
                            Multiplicity.getEdgeSetMult(vInterW);
                        Set<HostEdge> wInterV =
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

    private Set<Materialisation> compute() {
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
        this.createPossibleEdges();

        // Make sure that all shape nodes in the match image are in a
        // singleton equivalence class.
        for (ShapeNode nodeS : this.match.nodeMapValueSet()) {
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
        ResultSet result = new ResultSet();
        EquationSystem.newEqSys(this).solve(result);

        return result;
    }

    /**
     * Returns true if the morphism from the materialised shape to the original
     * one is consistent.
     */
    public boolean isShapeMorphConsistent() {
        return this.morph.isConsistent(this.shape, this.originalShape);
    }

    /** Returns true if the materialisation is complete. */
    public boolean isFinished() {
        return this.possibleEdges.isEmpty();
    }

    /**
     * Duplicates all incoming and outgoing edges for the materialised nodes.
     * These edges are not added to the shape since they may not be present
     * in the final configuration. For now they are put in a set of possible
     * edges and later used by the equation system to decide on the final
     * configuration of the shape.
     */
    private void createPossibleEdges() {
        if (this.matNodes.isEmpty()) {
            return;
        }
        // Clone the morphism because we need pre-images and this fixes
        // the object.
        ShapeMorphism clonedMorph = this.morph.clone();
        clonedMorph.setFixed();
        // For all nodes involved in the materialisation.
        for (ShapeNode matNode : this.matNodes) {
            // Get the original node from the shape morphism.
            ShapeNode origNode = clonedMorph.getNode(matNode);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Look for all edges connected to the original node in the
                // original shape.     
                for (ShapeEdge origEdge : this.originalShape.binaryEdgeSet(
                    origNode, direction)) {
                    ShapeNode origOppNode = origEdge.opposite(direction);
                    for (ShapeNode oppNode : clonedMorph.getPreImages(origOppNode)) {
                        ShapeEdge possibleEdge =
                            this.shape.createEdge(matNode, oppNode,
                                origEdge.label(), direction);
                        if (!this.shape.containsEdge(possibleEdge)) {
                            this.addPossibleEdge(possibleEdge, origEdge);
                        }
                    }
                }
            }
        }
    }

    private static class ResultSet extends THashSet<Materialisation> {
        @Override
        public boolean add(Materialisation mat) {
            assert mat.isShapeMorphConsistent();
            assert mat.getShape().isInvariantOK();
            return super.add(mat);
        }
    }

    /** Used for tests. */
    public static void main(String args[]) {
        String DIRECTORY = "junit/samples/abs-test.gps/";
        Multiplicity.initMultStore();
        File file = new File(DIRECTORY);
        try {
            GrammarModel view = GrammarModel.newInstance(file, false);
            HostGraph graph =
                view.getHostModel("materialisation-test-5").toResource();
            Shape shape = Shape.createShape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-5");
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

}
