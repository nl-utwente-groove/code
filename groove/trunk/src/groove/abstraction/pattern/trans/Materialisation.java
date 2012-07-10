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
package groove.abstraction.pattern.trans;

import groove.abstraction.Multiplicity;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.PatternShapeMorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 * Materialisation of pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public final class Materialisation {

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
    public static Collection<Materialisation> getMaterialisations(
            PatternShape pShape, PreMatch preMatch) {
        Collection<Materialisation> result = new ArrayList<Materialisation>();
        Materialisation initialMat = new Materialisation(pShape, preMatch);
        if (initialMat.isRuleModifying()) {
            initialMat.getSolutions(result);
        } else {
            result.add(initialMat);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The shape we are trying to materialise.
     * The field is final but the shape is modified by the materialisation.
     */
    private final PatternShape shape;
    /**
     * The original shape that started the materialisation process.
     * This is left unchanged during the materialisation.
     */
    private final PatternShape originalShape;
    /**
     * The pre-match that triggered this materialisation.
     */
    private final PreMatch preMatch;
    /**
     * The matched rule.
     */
    private final PatternRule rule;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     * The field is final but the match is modified by the materialisation.
     */
    private final Match match;
    /**
     * The morphism from the (partially) materialised shape into the original
     * shape.
     * The field is final but the morphism is modified by the materialisation.
     */
    private final PatternShapeMorphism morph;

    private final MyHashSet<PatternNode> matLayers[];

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    @SuppressWarnings("unchecked")
    private Materialisation(PatternShape shape, PreMatch preMatch) {
        assert shape.isFixed();
        assert preMatch.isFixed();
        this.originalShape = shape;
        this.preMatch = preMatch;
        this.rule = preMatch.getRule();
        if (isRuleModifying()) {
            this.shape = this.originalShape.clone();
            this.match = new Match(this.rule, this.shape);
            this.morph =
                PatternShapeMorphism.createIdentityMorphism(this.shape,
                    this.originalShape);
            this.matLayers = new MyHashSet[this.shape.depth() + 1];
        } else { // The rule is not modifying.
            // Nothing to do, we just return immediately.
            this.shape = shape;
            this.match = preMatch;
            this.morph = null;
            this.matLayers = null;
        }
    }

    /**
     * Copying constructor. Clones the structures of the given materialisation
     * object that can be modified. 
     */
    @SuppressWarnings("unchecked")
    private Materialisation(Materialisation mat) {
        // No need to clone the original objects since they are fixed.
        this.originalShape = mat.originalShape;
        this.preMatch = mat.preMatch;
        this.rule = mat.rule;
        // Clone the shape and the match.
        this.shape = mat.shape.clone();
        this.match = mat.match.clone();
        // Clone auxiliary structures when needed.
        this.morph = mat.morph.clone();
        this.matLayers = new MyHashSet[this.shape.depth() + 1];
        for (int layer = 0; layer <= this.shape.depth(); layer++) {
            if (mat.matLayers[layer] != null) {
                this.matLayers[layer] = mat.matLayers[layer].clone();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\n" + this.shape + "Match: " + this.match
            + "\n";
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic inspection method. */
    private boolean isRuleModifying() {
        return this.rule.isModifying();
    }

    /** Basic getter method. */
    public PatternShape getShape() {
        return this.shape;
    }

    /** Basic getter method. */
    public Match getMatch() {
        return this.match;
    }

    private void getSolutions(Collection<Materialisation> result) {
        prepareSolutions();
        Stack<Materialisation> toProcess = new Stack<Materialisation>();
        toProcess.push(this);
        while (!toProcess.isEmpty()) {
            Materialisation mat = toProcess.pop();
            if (mat.isFinished()) {
                // Check if we didn't do something wrong...
                assert checkMorphism();
                result.add(mat);
            } else {
                mat.computeSolutions(toProcess);
            }
        }
    }

    private boolean isFinished() {
        // EDUARDO: Implement this...
        return false;
    }

    private void computeSolutions(Stack<Materialisation> toProcess) {
        // EDUARDO: Implement this...
    }

    private void prepareSolutions() {
        PatternRuleGraph lhs = this.rule.lhs();
        for (int layer = 0; layer <= lhs.depth(); layer++) {
            // Materialise all nodes of the current layer.
            for (RuleNode rNode : lhs.getLayerNodes(layer)) {
                PatternNode origNode = this.preMatch.getNode(rNode);
                materialiseNode(rNode, origNode);
            }
            // Materialise all incoming edges of the previous layer.
            for (RuleEdge rEdge : lhs.getLayerEdges(layer)) {
                PatternEdge origEdge = this.preMatch.getEdge(rEdge);
                materialiseEdge(rEdge, origEdge);
            }
        }
    }

    private void materialiseNode(RuleNode rNode, PatternNode origNode) {
        Multiplicity origMult = this.shape.getMult(origNode);
        PatternNode newNode;
        if (origMult.isCollector()) {
            // Extract a copy.
            newNode = this.shape.createNode(origNode.getType());
            this.shape.addNode(newNode);
            addToMatLayer(newNode);
            // Adjust the original node multiplicity.
            Multiplicity adjustedMult =
                origMult.sub(Multiplicity.ONE_NODE_MULT);
            this.shape.setMult(origNode, adjustedMult);
        } else {
            // The original node is already concrete.
            newNode = origNode;
        }
        this.match.putNode(rNode, newNode);
        this.morph.putNode(newNode, origNode);
    }

    /**
     * Pre-condition: source and target nodes of original edge are already
     * materialised.
     */
    private void materialiseEdge(RuleEdge rEdge, PatternEdge origEdge) {
        PatternNode newSrc = this.match.getNode(rEdge.source());
        PatternNode newTgt = this.match.getNode(rEdge.target());
        PatternEdge newEdge;
        if (!newSrc.equals(origEdge.source())
            && !newTgt.equals(origEdge.target())) {
            newEdge = this.shape.createEdge(newSrc, origEdge.getType(), newTgt);
            this.shape.addEdge(newEdge);
        } else {
            newEdge = origEdge;
        }
        this.match.putEdge(rEdge, newEdge);
        this.morph.putEdge(newEdge, origEdge);
    }

    private void addToMatLayer(PatternNode newNode) {
        int layer = newNode.getLayer();
        if (this.matLayers[layer] == null) {
            this.matLayers[layer] = new MyHashSet<PatternNode>();
        }
        this.matLayers[layer].add(newNode);
    }

    /**
     * Returns true if the morphism from the materialised shape to the original
     * one is consistent and valid.
     */
    private boolean checkMorphism() {
        return this.morph.isConsistent(this.shape, this.originalShape)
            && this.morph.isValid(this.shape, this.originalShape);
    }

}
