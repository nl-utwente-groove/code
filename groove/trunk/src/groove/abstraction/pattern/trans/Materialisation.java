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
import groove.abstraction.pattern.shape.TypeEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
    /**
     * The queue of materialisation steps to be performed.
     */
    private final List<MatStep> queue;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
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
            this.queue = new LinkedList<MatStep>();
        } else { // The rule is not modifying.
            // Nothing to do, we just return immediately.
            this.shape = shape;
            this.match = preMatch;
            this.morph = null;
            this.queue = null;
        }
    }

    /**
     * Copying constructor. Clones the structures of the given materialisation
     * object that can be modified. 
     */
    private Materialisation(Materialisation mat) {
        // No need to clone the original objects since they are fixed.
        this.originalShape = mat.originalShape;
        this.preMatch = mat.preMatch;
        this.rule = mat.rule;
        // No need to clone the match either because the materialisation of the
        // match is deterministic.
        this.match = mat.match;
        // Clone the shape and the morphism.
        this.shape = mat.shape.clone();
        this.morph = mat.morph.clone();
        // Clone auxiliary structures when needed.
        this.queue = new LinkedList<MatStep>();
        for (MatStep matStep : mat.queue) {
            this.queue.add(matStep.clone());
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "\nMaterialisation:\n" + this.shape + "Queue: " + this.queue;
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
                //PatternPreviewDialog.showPatternGraph(mat.shape);
                assert mat.isValid();
                result.add(mat);
            } else {
                mat.computeSolutions(toProcess);
            }
        }
    }

    private boolean isFinished() {
        return this.queue.isEmpty();
    }

    private void computeSolutions(Stack<Materialisation> toProcess) {
        assert !isFinished();
        MatStep matStep = this.queue.remove(0);
        executeStep(matStep, toProcess);
    }

    private void prepareSolutions() {
        materialiseInitialNodes();
        materialiseInitialEdges();
        computeSteps();
    }

    private void materialiseInitialNodes() {
        PatternRuleGraph lhs = this.rule.lhs();
        for (int layer = 0; layer <= lhs.depth(); layer++) {
            // Materialise all nodes of the current layer.
            for (RuleNode rNode : lhs.getLayerNodes(layer)) {
                PatternNode origNode = this.preMatch.getNode(rNode);
                materialiseNode(rNode, origNode);
            }
        }
    }

    private void materialiseInitialEdges() {
        PatternRuleGraph lhs = this.rule.lhs();
        for (int layer = 0; layer < lhs.depth(); layer++) {
            // Materialise all outgoing edges of the current layer.
            for (RuleEdge rEdge : lhs.getLayerOutEdges(layer)) {
                PatternEdge origEdge = this.preMatch.getEdge(rEdge);
                materialiseEdge(rEdge, origEdge);
            }
        }
    }

    private void materialiseNode(RuleNode rNode, PatternNode origNode) {
        Multiplicity origMult = this.originalShape.getMult(origNode);
        PatternNode newNode;
        if (origMult.isCollector()) {
            // Extract a copy.
            newNode = extractNode(origNode);
        } else {
            // The original node is already concrete.
            newNode = origNode;
        }
        this.match.putNode(rNode, newNode);
    }

    /**
     * Pre-condition: source and target nodes of original edge are already
     * materialised.
     */
    private void materialiseEdge(RuleEdge rEdge, PatternEdge origEdge) {
        PatternNode origSrc = origEdge.source();
        PatternNode newSrc = this.match.getNode(rEdge.source());
        PatternNode origTgt = origEdge.target();
        PatternNode newTgt = this.match.getNode(rEdge.target());

        // The new end nodes must be concrete.
        assert this.shape.getMult(newSrc).isOne();
        assert this.shape.getMult(newTgt).isOne();

        Multiplicity origMult = this.shape.getMult(origEdge);
        PatternEdge newEdge;

        // Possibly create a new edge.
        if (newSrc.equals(origSrc) && newTgt.equals(origTgt)) {
            // The source and target nodes remained unchanged. This means that
            // the original edge must be concrete.
            assert origMult.isOne();
            newEdge = origEdge;
        } else {
            // We have to create a new edge outgoing from the new source.
            newEdge = this.shape.createEdge(newSrc, origEdge.getType(), newTgt);
            // Add this new edge with multiplicity one.
            this.shape.addEdge(newEdge);
            // Compute the adjusted multiplicity.
            Multiplicity adjustedMult =
                origMult.sub(Multiplicity.ONE_EDGE_MULT);
            // Check if we need to duplicate some edges.
            if (newSrc.equals(origSrc)) { // newTgt != origTgt
                // Same source node with new target. Since the source is
                // concrete we need to adjust the multiplicity of the original
                // edge that goes to the original target node to account for
                // the newly materialised edge.
                this.shape.setMult(origEdge, adjustedMult);
            } else { // newSrc != origSrc
                assert !newTgt.equals(origTgt);
                // If the adjusted multiplicity is not zero, we need to create
                // a new edge from the new source to the original target.
                if (!adjustedMult.isZero()) {
                    PatternEdge remainderEdge =
                        this.shape.createEdge(newSrc, origEdge.getType(),
                            origTgt);
                    this.shape.addEdge(remainderEdge);
                    this.shape.setMult(remainderEdge, adjustedMult);
                    this.morph.putEdge(remainderEdge, origEdge);
                }
            }
        }

        // Update the morphisms.
        this.match.putEdge(rEdge, newEdge);
        this.morph.putEdge(newEdge, origEdge);
    }

    private boolean isMaterialised(PatternNode newNode) {
        PatternNode origNode = this.morph.getNode(newNode);
        return !newNode.equals(origNode)
            || this.match.containsNodeValue(newNode);
    }

    private void computeSteps() {
        for (int layer = 0; layer <= this.shape.depth(); layer++) {
            for (PatternNode newNode : this.shape.getLayerNodes(layer)) {
                computeSteps(newNode);
            }
        }
    }

    private void computeSteps(PatternNode newNode) {
        if (!isMaterialised(newNode)) {
            return;
        }
        createRouteOutEdgeSteps(newNode);
        createMaterialiseTargetSteps(newNode);
    }

    private void createRouteOutEdgeSteps(PatternNode newNode) {
        // Check if there are outgoing edges missing for the new node.
        PatternNode origNode = this.morph.getNode(newNode);
        Set<PatternEdge> origOutEdges = this.originalShape.outEdgeSet(origNode);
        for (PatternEdge origOutEdge : origOutEdges) {
            TypeEdge edgeType = origOutEdge.getType();
            if (!this.shape.hasOutEdgeWithType(newNode, edgeType)) {
                MatStep matStep =
                    new MatStep(StepKind.ROUTE_OUT_EDGE, newNode, origOutEdge,
                        origOutEdge.target());
                this.queue.add(matStep);
            }
        }
    }

    private void createMaterialiseTargetSteps(PatternNode newNode) {
        // Check if the existing outgoing edges for the new node lead to
        // non-unique coverage.
        for (PatternEdge newOutEdge : this.shape.outEdgeSet(newNode)) {
            if (!this.shape.isUniquelyCovered(newOutEdge.target())) {
                MatStep matStep =
                    new MatStep(StepKind.MAT_TARGET, newNode, newOutEdge,
                        newOutEdge.target());
                //this.queue.add(matStep);
                // These steps go into the front of the queue.
                this.queue.add(0, matStep);
            }
        }
    }

    private void executeStep(MatStep matStep, Stack<Materialisation> toProcess) {
        switch (matStep.kind) {
        case ROUTE_OUT_EDGE:
            routeOutgoingEdge(matStep, toProcess);
            break;
        case ROUTE_IN_EDGE:
            routeIncomingEdge(matStep, toProcess);
            break;
        case MAT_TARGET:
            materialiseTarget(matStep, toProcess);
            break;
        default:
            assert false;
        }
    }

    private Set<PatternNode> computePossibleTargets(PatternNode origTgt,
            TypeEdge edgeType) {
        Set<PatternNode> result = new MyHashSet<PatternNode>();
        // Iterate over the morphism to see the elements that we materialised.
        // We can't look at the reverse map because it fixes the morphism.
        for (Entry<PatternNode,PatternNode> entry : this.morph.nodeMap().entrySet()) {
            PatternNode newNode = entry.getKey();
            PatternNode origNode = entry.getValue();
            if (origNode.equals(origTgt)) {
                int size =
                    this.shape.getInEdgesWithType(newNode, edgeType).size();
                // The new node is a possible target if it doesn't have another
                // incoming edge of the same type.
                if (size == 0 || this.shape.getMult(newNode).isCollector()) {
                    result.add(newNode);
                }
            }
        }
        return result;
    }

    private void routeOutgoingEdge(MatStep matStep,
            Stack<Materialisation> toProcess) {
        assert matStep.kind == StepKind.ROUTE_OUT_EDGE;

        PatternNode newSrc = matStep.source;
        PatternNode origTgt = matStep.target;
        PatternEdge origEdge = matStep.edge;
        TypeEdge type = matStep.type;

        // Create new materialisation steps.
        Set<PatternNode> possibleTargets =
            computePossibleTargets(origTgt, type);
        for (PatternNode newTgt : possibleTargets) {
            Materialisation mat;
            if (possibleTargets.size() == 1) {
                mat = this;
            } else {
                mat = this.clone();
            }
            // Create a new edge.
            PatternEdge newEdge = mat.shape.createEdge(newSrc, type, newTgt);
            mat.shape.addEdge(newEdge);
            mat.morph.putEdge(newEdge, origEdge);
            if (mat.shape.getMult(newTgt).isCollector()
                && mat.isEnvironment(newSrc)) {
                // The new target is a collector node. Either we are fine and
                // can just route the edge or we need to materialise the target.
                mat.createMaterialiseTargetSteps(newSrc);
            }
            mat.removeRouteInEdgeStep(newTgt, type);

            // Push the new materialisation object into the stack.
            toProcess.push(mat);
        }
    }

    private void removeRouteInEdgeStep(PatternNode target, TypeEdge type) {
        Iterator<MatStep> iter = this.queue.iterator();
        while (iter.hasNext()) {
            MatStep matStep = iter.next();
            if (matStep.kind == StepKind.ROUTE_IN_EDGE
                && matStep.target.equals(target) && matStep.type == type) {
                iter.remove();
            }
        }
    }

    private void routeIncomingEdge(MatStep matStep,
            Stack<Materialisation> toProcess) {
        assert matStep.kind == StepKind.ROUTE_IN_EDGE;

        PatternEdge origEdge = matStep.edge;
        PatternNode newTgt = matStep.target;
        assert this.shape.getMult(newTgt).isOne();

        PatternNode origSrc = origEdge.source();
        if (this.shape.getMult(origSrc).isOne()) {
            // No materialisation needed, just route the edge.
            reRouteEdge(origEdge, origSrc, newTgt);
        } else {
            // We need to materialise the source.
            PatternNode newSrc = extractNode(origSrc);
            reRouteEdge(origEdge, newSrc, newTgt);
            createRouteOutEdgeSteps(newSrc);
        }
        // This step is deterministic.
        toProcess.push(this);
    }

    private void materialiseTarget(MatStep matStep,
            Stack<Materialisation> toProcess) {
        assert matStep.kind == StepKind.MAT_TARGET;

        PatternNode newSrc = matStep.source;
        PatternEdge origEdge = matStep.edge;
        TypeEdge type = matStep.type;
        PatternNode origTgt = matStep.target;
        assert origEdge.source().equals(newSrc);
        assert origEdge.target().equals(origTgt);

        Multiplicity origMult = this.shape.getMult(origTgt);
        assert origMult.isCollector();
        assert this.shape.getMult(newSrc).isOne();

        PatternNode newTgt = extractNode(origTgt);
        reRouteEdge(origEdge, newSrc, newTgt);

        // Create new materialisation steps.
        Set<Set<PatternEdge>> coverageSet =
            this.shape.getCoveragePossibilities(origTgt, type);
        for (Set<PatternEdge> coverage : coverageSet) {
            Materialisation mat;
            if (coverageSet.size() == 1) {
                mat = this;
            } else {
                mat = this.clone();
            }
            for (PatternEdge inEdge : coverage) {
                MatStep newStep =
                    new MatStep(StepKind.ROUTE_IN_EDGE, null, inEdge, newTgt);
                mat.queue.add(newStep);
            }
            // Push the new materialisation object into the stack.
            toProcess.push(mat);
        }
    }

    private PatternNode extractNode(PatternNode origNode) {
        Multiplicity origMult = this.shape.getMult(origNode);
        assert origMult.isCollector();
        PatternNode newNode = this.shape.createNode(origNode.getType());
        this.shape.addNode(newNode);
        // Adjust the original node multiplicity.
        Multiplicity adjustedMult = origMult.sub(Multiplicity.ONE_NODE_MULT);
        this.shape.setMult(origNode, adjustedMult);
        this.morph.putNode(newNode, origNode);
        return newNode;
    }

    private void reRouteEdge(PatternEdge origEdge, PatternNode source,
            PatternNode target) {
        PatternEdge newEdge =
            this.shape.createEdge(source, origEdge.getType(), target);
        this.shape.addEdge(newEdge);
        this.morph.putEdge(newEdge, this.morph.getEdge(origEdge));
        if (origEdge.source().equals(source)) {
            assert this.shape.getMult(source).isOne();
            // Remove the original edge.
            this.shape.removeEdge(origEdge);
            this.morph.removeEdge(origEdge);
        }
    }

    /** Returns true if the given node is in the environment graph. */
    private boolean isEnvironment(PatternNode pNode) {
        List<PatternNode> toTest = new LinkedList<PatternNode>();
        toTest.add(pNode);
        while (!toTest.isEmpty()) {
            PatternNode node = toTest.remove(0);
            if (this.match.containsNodeValue(node)) {
                return true;
            } else {
                for (PatternEdge inEdge : this.shape.inEdgeSet(node)) {
                    toTest.add(inEdge.target());
                }
            }
        }
        return false;
    }

    /** Returns true if the materialisation is valid. */
    private boolean isValid() {
        return isMorphConsistent() && isMorphValid() && isMatchConcrete()
            && isEnvironmentUniquelyCovered();
    }

    private boolean isMorphConsistent() {
        return this.morph.isConsistent(this.shape, this.originalShape);
    }

    private boolean isMorphValid() {
        return this.morph.isValid(this.shape, this.originalShape);
    }

    private boolean isMatchConcrete() {
        PatternRuleGraph lhs = this.rule.lhs();
        for (RuleNode rNode : lhs.nodeSet()) {
            PatternNode pNode = this.match.getNode(rNode);
            assert pNode != null;
            if (!this.shape.getMult(pNode).isOne()) {
                return false;
            }
        }
        for (RuleEdge rEdge : lhs.edgeSet()) {
            PatternEdge pEdge = this.match.getEdge(rEdge);
            assert pEdge != null;
            if (!this.shape.getMult(pEdge).isOne()) {
                return false;
            }
        }
        return true;
    }

    private boolean isEnvironmentUniquelyCovered() {
        Set<PatternNode> nextLayer = new MyHashSet<PatternNode>();
        for (RuleNode rNode : this.rule.lhs().getLayerNodes(0)) {
            PatternNode pNode = this.match.getNode(rNode);
            assert pNode != null;
            nextLayer.addAll(this.shape.getSuccessors(pNode));
        }
        while (!nextLayer.isEmpty()) {
            Set<PatternNode> toTest = nextLayer;
            nextLayer = new MyHashSet<PatternNode>();
            for (PatternNode pNode : toTest) {
                if (this.shape.isUniquelyCovered(pNode)) {
                    nextLayer.addAll(this.shape.getSuccessors(pNode));
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    private enum StepKind {
        ROUTE_OUT_EDGE, ROUTE_IN_EDGE, MAT_TARGET
    }

    private static class MatStep {

        final StepKind kind;
        final PatternEdge edge;
        final PatternNode source;
        final TypeEdge type;
        final PatternNode target;

        MatStep(StepKind kind, PatternNode source, PatternEdge edge,
                PatternNode target) {
            this.kind = kind;
            this.edge = edge;
            this.source = source;
            this.type = edge.getType();
            this.target = target;
        }

        MatStep(MatStep step) {
            this.kind = step.kind;
            this.edge = step.edge;
            this.source = step.source;
            this.type = step.type;
            this.target = step.target;
        }

        @Override
        public MatStep clone() {
            return new MatStep(this);
        }

        @Override
        public String toString() {
            return this.kind + ": (" + this.edge + ", " + this.source + ", "
                + this.target + ")";
        }

    }

}
