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
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeNode;
import groove.util.Duo;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
     * in the materialisation phase.
     */
    public static Collection<PatternShape> getMaterialisations(
            PatternShape pShape, PreMatch preMatch) {
        Collection<PatternShape> result = new ArrayList<PatternShape>();
        if (preMatch.getRule().isModifying()) {
            Materialisation initialMat = new Materialisation(pShape, preMatch);
            initialMat.getSolutions(result);
        } else {
            result.add(pShape);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The original shape we are trying to materialise. Remains unchanged.
     */
    private final PatternShape origShape;
    /**
     * Auxiliar copy.
     */
    private final PatternShape pShape;
    /**
     * The pre-match that triggered this materialisation.
     */
    private final PreMatch preMatch;
    /**
     * The matched rule.
     */
    private final PatternRule rule;
    /**
     * The quasi-shape we are trying to materialise.
     */
    private QuasiShape qShape;
    /**
     * The concrete match of the rule into the (partially) materialised
     * quasi-shape.
     */
    private Match match;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
    * Constructs the initial materialisation object given a shape and a
    * pre-match of a rule into the shape. The pre-match given must be valid.
    */
    private Materialisation(PatternShape pShape, PreMatch preMatch) {
        assert pShape.isFixed();
        assert preMatch.isFixed();
        this.origShape = pShape;
        this.pShape = pShape.clone();
        this.preMatch = preMatch;
        this.rule = preMatch.getRule();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void getSolutions(Collection<PatternShape> result) {
        // Sequence of operations as defined in the canonical pattern shape
        // transformation.
        pullMatchAndDevolveShape();
        disambiguate();
        deletePatterns();
        addPatterns();
        close();
        split();
        branch(result);
    }

    private PatternNode createNode(TypeNode type) {
        PatternNode newNode = this.pShape.createNode(type);
        this.pShape.addNode(newNode);
        return newNode;
    }

    private PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        PatternEdge newEdge = this.pShape.createEdge(source, type, target);
        this.pShape.addEdgeContext(newEdge);
        return newEdge;
    }

    // ------------------------------------------------------------------------
    // Operations pull and devol
    // ------------------------------------------------------------------------

    private void pullMatchAndDevolveShape() {
        this.match = new Match(this.rule, this.pShape);
        PatternRuleGraph lhs = this.rule.lhs();

        // Materialise node images.
        for (RuleNode rNode : lhs.nodeSet()) {
            PatternNode origNode = this.preMatch.getNode(rNode);
            materialiseNode(rNode, origNode);
        }
        // Materialise edge images.
        for (RuleEdge rEdge : lhs.edgeSet()) {
            PatternEdge origEdge = this.preMatch.getEdge(rEdge);
            materialiseEdge(rEdge, origEdge);
        }

        // Create extra outgoing edges.
        for (RuleNode rNode : lhs.nodeSet()) {
            PatternNode origNode = this.preMatch.getNode(rNode);
            PatternNode matNode = this.match.getNode(rNode);
            for (PatternEdge origEdge : this.origShape.outEdgeSet(origNode)) {
                copyEdge(origEdge, matNode);
            }
        }

        assert this.pShape.isWellDefined();
        this.qShape = QuasiShape.devolve(this.pShape);
        Match tempMatch = this.match;
        this.match = new Match(this.rule, this.qShape);
        this.match.putAll(tempMatch);
        this.match.setFixed();
    }

    private void materialiseNode(RuleNode rNode, PatternNode origNode) {
        PatternNode newNode;
        // There are two cases:
        // - if the original node is concrete we just use it.
        // - if the original node is a collector then we extract a copy.
        Multiplicity origMult = this.pShape.getMult(origNode);
        if (origMult.isOne()) {
            newNode = origNode;
        } else {
            // Extract a copy.
            newNode = createNode(origNode.getType());
            this.pShape.setMult(newNode, Multiplicity.ONE_NODE_MULT);
            // Adjust the original node multiplicity.
            this.pShape.setMult(origNode, origMult.sub(1));
        }
        this.match.putNode(rNode, newNode);
    }

    private void materialiseEdge(RuleEdge rEdge, PatternEdge origEdge) {
        PatternNode origSrc = origEdge.source();
        PatternNode origTgt = origEdge.target();
        PatternNode newSrc = this.match.getNode(rEdge.source());
        PatternNode newTgt = this.match.getNode(rEdge.target());
        boolean sameSrc = origSrc.equals(newSrc);
        boolean sameTgt = origTgt.equals(newTgt);

        Multiplicity origMult = this.pShape.getMult(origEdge);
        TypeEdge edgeType = origEdge.getType();
        PatternEdge newEdge;

        if (sameSrc && sameTgt) {
            // The source and target nodes remained unchanged. Just return the
            // original edge.
            assert origMult.isOne();
            newEdge = origEdge;
        } else {
            // In all other cases we need to create a new edge.
            newEdge = createEdge(newSrc, edgeType, newTgt);
            this.pShape.setMult(newEdge, Multiplicity.ONE_EDGE_MULT);
            if (sameSrc && !sameTgt) {
                // Same source node with new target. We need to adjust the
                // multiplicity of the original edge that goes to the original
                // target node to account for the newly materialised edge.
                this.pShape.setMult(origEdge, origMult.sub(1));
            }
        }

        this.match.putEdge(rEdge, newEdge);
    }

    private void copyEdge(PatternEdge origEdge, PatternNode newSrc) {
        Multiplicity origMult = this.origShape.getMult(origEdge);
        int preImageCount = this.preMatch.getPreImages(origEdge).size();
        Multiplicity adjustedMult = origMult.sub(preImageCount);
        if (adjustedMult.isZero()) {
            // Nothing to create.
            return;
        }
        PatternNode newTgt = origEdge.target();
        PatternEdge newEdge = createEdge(newSrc, origEdge.getType(), newTgt);
        this.pShape.setMult(newEdge, adjustedMult);
    }

    // ------------------------------------------------------------------------
    // Operation di
    // ------------------------------------------------------------------------

    private void disambiguate() {
        // Make sure that everything in the deletion cone is unambiguous.
        List<PatternNode> toTraverse = new LinkedList<PatternNode>();
        for (RuleNode rNode : this.rule.getEraserNodes()) {
            toTraverse.add(this.match.getNode(rNode));
        }
        for (PatternNode delNode : this.qShape.getDownwardTraversal(toTraverse)) {
            if (delNode.getLayer() > 0) {
                this.qShape.disambiguate(delNode);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Operation del
    // ------------------------------------------------------------------------

    private void deletePatterns() {
        for (RuleNode rNode : this.rule.getEraserNodes()) {
            this.qShape.deletePattern(this.match.getNode(rNode));
        }
    }

    // ------------------------------------------------------------------------
    // Operation add
    // ------------------------------------------------------------------------

    private void addPatterns() {
        // First add layer 0 patterns.
        for (RuleNode rNode : this.rule.getCreatorNodes()) {
            if (!rNode.isNodePattern()) {
                continue;
            }
            addNodePattern(rNode);
        }

        // Then add layer 1 patterns.
        for (RuleNode rNode : this.rule.getCreatorNodes()) {
            if (!rNode.isEdgePattern()) {
                continue;
            }
            addEdgePattern(rNode);
        }
    }

    private void addNodePattern(RuleNode rNode) {
        PatternNode newNode = this.qShape.addNodePattern(rNode.getType());
        this.match.putNode(rNode, newNode);
    }

    private void addEdgePattern(RuleNode rNode) {
        Duo<RuleEdge> inEdges = this.rule.rhs().getIncomingEdges(rNode);

        RuleEdge r1 = inEdges.one();
        RuleEdge r2 = inEdges.two();
        TypeEdge m1 = r1.getType();
        TypeEdge m2 = r2.getType();
        PatternNode p1 = this.match.getNode(r1.source());
        PatternNode p2 = this.match.getNode(r2.source());

        Pair<PatternNode,Duo<PatternEdge>> pair =
            this.qShape.addEdgePattern(m1, m2, p1, p2);
        PatternNode newNode = pair.one();
        PatternEdge d1 = pair.two().one();
        PatternEdge d2 = pair.two().two();
        this.match.putNode(rNode, newNode);
        this.match.putEdge(r1, d1);
        this.match.putEdge(r2, d2);
    }

    // ------------------------------------------------------------------------
    // Operation close
    // ------------------------------------------------------------------------

    private void close() {
        this.origShape.getTypeGraph().close(this.qShape);
    }

    // ------------------------------------------------------------------------
    // Operation sp
    // ------------------------------------------------------------------------

    private void split() {
        // EDUARDO: Implement this...
    }

    // ------------------------------------------------------------------------
    // Operation br
    // ------------------------------------------------------------------------

    private void branch(Collection<PatternShape> result) {
        this.qShape.getMaterialisations(result);
    }

}
