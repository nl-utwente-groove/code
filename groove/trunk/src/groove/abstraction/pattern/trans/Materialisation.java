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
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.PatternAbsParam;
import groove.abstraction.pattern.gui.dialog.PatternPreviewDialog;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.PatternShapeMorphism;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeNode;
import groove.trans.HostNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

/**
 * Materialisation of pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public final class Materialisation {

    private static final boolean USE_GUI = false;

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

    private final List<PatternNode> downTraversal;

    private final List<PatternNode> upTraversal;

    private final Map<TypeEdge,Set<PatternNode>> danglingOut;

    private final Map<TypeEdge,Set<PatternNode>> danglingIn;

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
            this.downTraversal = new MySortedList(true);
            this.upTraversal = new MySortedList(false);
            this.danglingOut = new MyHashMap<TypeEdge,Set<PatternNode>>();
            this.danglingIn = new MyHashMap<TypeEdge,Set<PatternNode>>();
        } else { // The rule is not modifying.
            // Nothing to do, we just return immediately.
            this.shape = shape;
            this.match = preMatch;
            this.morph = null;
            this.downTraversal = null;
            this.upTraversal = null;
            this.danglingOut = null;
            this.danglingIn = null;
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
        // Clone auxiliary structures.
        this.downTraversal = cloneTraversalList(mat.downTraversal, true);
        this.upTraversal = cloneTraversalList(mat.upTraversal, false);
        this.danglingOut = cloneDanglingMap(mat.danglingOut);
        this.danglingIn = cloneDanglingMap(mat.danglingIn);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "\nMaterialisation:\n" + this.shape + "Down: "
            + this.downTraversal + "\nUp: " + this.upTraversal;
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    // ---------
    // Main loop
    // ---------

    private void getSolutions(Collection<Materialisation> result) {
        prepareSolutions();
        Stack<Materialisation> toProcess = new Stack<Materialisation>();
        toProcess.push(this);
        while (!toProcess.isEmpty()) {
            Materialisation mat = toProcess.pop();
            if (mat.isFinished()) {
                assert mat.isValid();
                mat.shape.improvePrecision();
                assert mat.isAdmissable(false);
                assert mat.isMorphValid();
                mat.shape.removeUncoveredNodes();
                assert mat.shape.isWellFormed();
                result.add(mat);
            } else {
                mat.computeSolutions(toProcess);
            }
        }
    }

    private void prepareSolutions() {
        materialiseNodeImages();
        materialiseEdgeImages();
        computeTraversals();
    }

    private void materialiseNodeImages() {
        PatternRuleGraph lhs = this.rule.lhs();
        for (int layer = 0; layer <= lhs.depth(); layer++) {
            // Materialise all nodes of the current layer.
            for (RuleNode rNode : lhs.getLayerNodes(layer)) {
                PatternNode origNode = this.preMatch.getNode(rNode);
                materialiseNode(rNode, origNode);
            }
        }
    }

    private void materialiseEdgeImages() {
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
        PatternNode newNode = extractNode(origNode, Multiplicity.ONE_NODE_MULT);
        this.match.putNode(rNode, newNode);
    }

    private void materialiseEdge(RuleEdge rEdge, PatternEdge origEdge) {
        PatternNode newSrc = this.match.getNode(rEdge.source());
        PatternNode newTgt = this.match.getNode(rEdge.target());
        PatternEdge newEdge =
            extractEdge(origEdge, newSrc, newTgt, Multiplicity.ONE_EDGE_MULT,
                true);
        this.match.putEdge(rEdge, newEdge);
    }

    private PatternNode extractNode(PatternNode origNode,
            Multiplicity newNodeMult) {
        // There are two cases:
        // - if the original node has a multiplicity smaller then the wanted
        //   multiplicity then we just return it.
        // - if the original node is a collector then we extract a copy.
        PatternNode newNode;
        Multiplicity origMult = this.shape.getMult(origNode);
        Multiplicity adjustedMult = origMult.sub(newNodeMult);
        if (origMult.isCollector() && !adjustedMult.isZero()) {
            // Extract a copy.
            newNode = createNode(origNode.getType());
            this.shape.setMult(newNode, newNodeMult);
            // Adjust the original node multiplicity.
            this.shape.setMult(origNode, adjustedMult);
            // Adjust the morphism.
            if (!this.originalShape.containsNode(origNode)) {
                origNode = this.morph.getNode(origNode);
            }
            this.morph.putNode(newNode, origNode);
        } else {
            // We can't extract a node because it will make the original one
            // disappear. This is bad because then we would lose the edges as
            // well.
            newNode = origNode;
        }
        return newNode;
    }

    private PatternEdge extractEdge(PatternEdge origEdge, PatternNode newSrc,
            PatternNode newTgt, Multiplicity newEdgeMult, boolean keepOrigEdge) {
        PatternNode origSrc = origEdge.source();
        PatternNode origTgt = origEdge.target();
        boolean sameSrc = origSrc.equals(newSrc);
        boolean sameTgt = origTgt.equals(newTgt);

        Multiplicity origEdgeMult = this.shape.getMult(origEdge);
        TypeEdge edgeType = origEdge.getType();
        PatternEdge newEdge;

        if (sameSrc && sameTgt) {
            // The source and target nodes remained unchanged. Just return the
            // original edge.
            assert origEdgeMult.isOne() && newEdgeMult.isOne();
            newEdge = origEdge;
        } else {
            // In all other cases we need to create a new edge.
            newEdge = createEdge(newSrc, edgeType, newTgt);
            this.shape.setMult(newEdge, newEdgeMult);
            // Check the remaining cases.
            if (sameSrc && !sameTgt) {
                // Same source node with new target. Maybe we need to adjust the
                // multiplicity of the original edge that goes to the original
                // target node to account for the newly materialised edge.
                if (keepOrigEdge) {
                    Multiplicity adjustedOrigEdgeMult =
                        origEdgeMult.sub(newEdgeMult);
                    this.shape.setMult(origEdge, adjustedOrigEdgeMult);
                } else {
                    // We we explicitly told to remove the original edge.
                    this.shape.removeEdge(origEdge);
                }
            }
        }

        adjustMorphism(newEdge, origEdge);

        return newEdge;
    }

    private void adjustMorphism(PatternEdge newEdge, PatternEdge origEdge) {
        PatternEdge oldOrigEdge = origEdge;
        if (!this.originalShape.containsEdge(origEdge)) {
            origEdge = this.morph.getEdge(origEdge);
        }
        if (!this.shape.containsEdge(oldOrigEdge)) {
            // The original edge is gone so we have to remove it from
            // the morphism.
            this.morph.removeEdge(oldOrigEdge);
        }
        this.morph.putEdge(newEdge, origEdge);
    }

    private PatternNode createNode(TypeNode type) {
        PatternNode newNode = this.shape.createNode(type);
        this.shape.addNode(newNode);
        return newNode;
    }

    private PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        PatternEdge newEdge = this.shape.createEdge(source, type, target);
        this.shape.addEdge(newEdge);
        return newEdge;
    }

    private void computeTraversals() {
        for (int layer = 0; layer <= this.shape.depth(); layer++) {
            for (PatternNode newNode : this.shape.getLayerNodes(layer)) {
                computeTraversal(newNode);
            }
        }
    }

    private void computeTraversal(PatternNode newNode) {
        if (!shouldTraverse(newNode)) {
            return;
        }
        computeUpTraversal(newNode);
        computeDownTraversal(newNode);
    }

    private boolean shouldTraverse(PatternNode newNode) {
        return isMaterialised(newNode) || isEnvironment(newNode);
    }

    private boolean isMaterialised(PatternNode newNode) {
        PatternNode origNode = this.morph.getNode(newNode);
        return !newNode.equals(origNode);
    }

    private void computeUpTraversal(PatternNode newNode) {
        // Check if there are incoming edges missing for the new node.
        PatternNode origNode = this.morph.getNode(newNode);
        Set<PatternEdge> origInEdges = this.originalShape.inEdgeSet(origNode);
        for (PatternEdge origInEdge : origInEdges) {
            TypeEdge edgeType = origInEdge.getType();
            if (!this.shape.hasInEdgeWithType(newNode, edgeType)) {
                addToDanglingIn(edgeType, newNode);
                addToUpTraversal(newNode);
            }
        }
    }

    private void computeDownTraversal(PatternNode newNode) {
        // Check if there are outgoing edges missing for the new node.
        PatternNode origNode = this.morph.getNode(newNode);
        Set<PatternEdge> origOutEdges = this.originalShape.outEdgeSet(origNode);
        for (PatternEdge origOutEdge : origOutEdges) {
            TypeEdge edgeType = origOutEdge.getType();
            if (!this.shape.hasOutEdgeWithType(newNode, edgeType)) {
                addToDanglingOut(edgeType, newNode);
                addToDownTraversal(newNode);
            }
        }
        // Check if the existing outgoing edges for the new node lead to
        // non-unique coverage.
        for (PatternEdge newOutEdge : this.shape.outEdgeSet(newNode)) {
            if (!this.shape.isUniquelyCovered(newOutEdge.target())) {
                addToDownTraversal(newNode);
            }
        }
    }

    private void computeSolutions(Stack<Materialisation> toProcess) {
        assert !isFinished();
        if (!this.upTraversal.isEmpty()) {
            traverseUp(toProcess);
        } else if (!this.downTraversal.isEmpty()) {
            traverseDown(toProcess);
        }
    }

    private boolean isFinished() {
        return this.upTraversal.isEmpty() && this.downTraversal.isEmpty();
    }

    private void traverseUp(Stack<Materialisation> toProcess) {
        assert !this.upTraversal.isEmpty();

        final PatternNode newTgt = this.upTraversal.get(0);
        final PatternEdge missingOrigEdge = getMissingInEdge(newTgt);

        if (missingOrigEdge == null) {
            // No edge is missing. We're done.
            this.upTraversal.remove(0);
            toProcess.push(this);
            return;
        }

        if (USE_GUI) {
            PatternPreviewDialog.showPatternGraph(this.shape);
        }

        final Multiplicity newTgtMult = this.shape.getMult(newTgt);
        final TypeEdge edgeType = missingOrigEdge.getType();

        // Find possible sources.
        final Set<PatternNode> possibleSources = new MyHashSet<PatternNode>();
        final Map<PatternNode,PatternEdge> srcToOrigEdgeMap =
            new MyHashMap<PatternNode,PatternEdge>();
        for (PatternNode possibleSource : getDanglingOut(edgeType)) {
            possibleSources.add(possibleSource);
            srcToOrigEdgeMap.put(possibleSource, missingOrigEdge);
        }
        // We may have more than one incoming edge with the same type.
        // This means that we have to consider all possible sources.
        // Note that we can't use the 'newTgt' variable here, since it might
        // be a materialised node that doesn't have all the incoming edges yet.
        for (PatternEdge inEdge : this.shape.getInEdgesWithType(
            missingOrigEdge.target(), edgeType)) {
            PatternNode possibleSource = inEdge.source();
            if (possibleSources.add(possibleSource)) {
                // We didn't see this source yet.
                srcToOrigEdgeMap.put(possibleSource, inEdge);
            }
        }

        // Remove possible sources that will lead to non-commuting shapes.
        filterSourcesByCommutativity(possibleSources, newTgt, edgeType);

        // For each possible source we have to branch the search.
        int srcCount = possibleSources.size();
        boolean clone = srcCount > 1 && newTgtMult.isOne();
        int i = 1;
        boolean last;
        for (PatternNode possibleSource : possibleSources) {
            last = i == srcCount;
            // Check if we need to clone the materialisation.
            Materialisation mat;
            if (clone) {
                mat = this.clone();
            } else {
                mat = this;
            }
            PatternEdge origEdge = srcToOrigEdgeMap.get(possibleSource);
            Multiplicity origEdgeMult = mat.shape.getMult(origEdge);
            PatternNode newSrc = possibleSource;
            // The hard part is done in another method.
            // Sometimes we have to extract another node from the source,
            // hence the returning of a node in the method call.
            newSrc =
                mat.traverseUp(origEdge, origEdgeMult, newSrc, newTgt,
                    newTgtMult);
            // The new edge nodes are no longer dangling w.r.t. this edge type.
            mat.getDanglingOut(edgeType).remove(newSrc);
            if (clone || last) {
                mat.getDanglingIn(edgeType).remove(newTgt);
                // Push the new materialisation object into the stack.
                assert mat.isConcretePartCommuting(true);
                assert mat.isAdmissable(true);
                toProcess.push(mat);
            }
            i++;
        }
    }

    private PatternNode traverseUp(PatternEdge origEdge,
            Multiplicity origEdgeMult, PatternNode newSrc, PatternNode newTgt,
            Multiplicity newTgtMult) {
        Multiplicity newEdgeMult;
        boolean wasSrcMaterialised = false;
        // Check if we need to materialise the source and other special cases.
        if (this.shape.getMult(newSrc).isCollector()) {
            Multiplicity newSrcMult;
            if (newTgtMult.isOne()) {
                newSrcMult = Multiplicity.ONE_NODE_MULT;
                newEdgeMult = Multiplicity.ONE_EDGE_MULT;
            } else {
                newSrcMult = newTgtMult;
                newEdgeMult = Multiplicity.ONE_EDGE_MULT;
            }
            newSrc = extractNode(newSrc, newSrcMult);
            wasSrcMaterialised = true;
        } else { // The source is a concrete node.
            if (newTgtMult.isOne()) {
                newEdgeMult = Multiplicity.ONE_EDGE_MULT;
            } else {
                newEdgeMult = origEdgeMult;
            }
        }
        // Create the new edge.
        extractEdge(origEdge, newSrc, newTgt, newEdgeMult, true);
        Multiplicity adjustedOrigEdgeMult = origEdgeMult.sub(newEdgeMult);
        // Check for special cases.
        if (wasSrcMaterialised) {
            if (!adjustedOrigEdgeMult.isZero()) {
                PatternEdge remainderEdge =
                    createEdge(newSrc, origEdge.getType(), origEdge.target());
                this.shape.setMult(remainderEdge, adjustedOrigEdgeMult);
                adjustMorphism(remainderEdge, origEdge);
            }
            computeTraversal(newSrc);
        }
        return newSrc;
    }

    private void traverseDown(Stack<Materialisation> toProcess) {
        assert this.upTraversal.isEmpty();
        assert !this.downTraversal.isEmpty();

        final PatternNode newSrc = this.downTraversal.get(0);
        final boolean isSrcEnvironment = isEnvironment(newSrc);
        final PatternEdge origEdge =
            getMissingOutEdge(newSrc, isSrcEnvironment);

        if (origEdge == null) {
            // No edge is missing. We're done.
            this.downTraversal.remove(0);
            toProcess.push(this);
            return;
        }

        if (USE_GUI) {
            PatternPreviewDialog.showPatternGraph(this.shape);
        }

        Multiplicity origEdgeMult = this.shape.getMult(origEdge);
        if (origEdgeMult.isZero()) {
            origEdgeMult = this.originalShape.getMult(origEdge);
        }
        final Multiplicity newSrcMult = this.shape.getMult(newSrc);
        final TypeEdge edgeType = origEdge.getType();

        final Set<PatternNode> danglingSet = getDanglingIn(edgeType);
        final List<PatternNode> possibleTargets =
            new ArrayList<PatternNode>(danglingSet.size() + 1);
        possibleTargets.addAll(danglingSet);
        possibleTargets.add(origEdge.target());

        // For each possible target we have to branch the search.
        for (PatternNode possibleTarget : possibleTargets) {
            // Check if we need to clone the materialisation.
            Materialisation mat;
            if (possibleTargets.size() > 1) {
                mat = this.clone();
            } else {
                mat = this;
            }
            PatternNode newTgt = possibleTarget;
            // The hard part is done in another method.
            // Sometimes we have to extract another node from the target,
            // hence the returning of a node in the method call.
            newTgt =
                mat.traverseDown(origEdge, origEdgeMult, newSrc, newSrcMult,
                    newTgt, isSrcEnvironment);
            // The new edge nodes are no longer dangling w.r.t. this edge type.
            mat.getDanglingOut(edgeType).remove(newSrc);
            mat.getDanglingIn(edgeType).remove(newTgt);
            // Push the new materialisation object into the stack.
            assert mat.isConcretePartCommuting(true);
            assert mat.isAdmissable(true);
            toProcess.push(mat);
        }
    }

    private PatternNode traverseDown(PatternEdge origEdge,
            Multiplicity origEdgeMult, PatternNode newSrc,
            Multiplicity newSrcMult, PatternNode newTgt,
            boolean isSrcEnvironment) {
        if (!isSrcEnvironment) {
            // The source is not part of the environment, this means we don't
            // have to worry about unique coverage. Just create a new edge.
            extractEdge(origEdge, newSrc, newTgt, origEdgeMult, true);
            return newTgt;
        }

        // else: the source is part of the environment.
        // The target node has to have unique coverage.
        boolean isTgtUniquelyCovered = this.shape.isUniquelyCovered(newTgt);
        int origEdgeTypeCountInTgt =
            this.shape.getInEdgesWithType(newTgt, origEdge.getType()).size();

        // At this point we are sure that:
        // 1) There already exists at least one incoming edge at the target
        //    with the same type that we want to add. This follows from the
        //    fact that upward traversal has priority over downward traversal.
        //    This means that when we are traversing down the target is already
        //    covered.
        // 2) If the target is uniquely covered then it follows that there is
        //    exactly one incoming edge of the same type.
        assert (isTgtUniquelyCovered && origEdgeTypeCountInTgt == 1)
            || !isTgtUniquelyCovered;

        // We have to test if we have to extract a new copy from the target
        // or if we need to split the target in several parts.
        // We extract a copy from the target if:
        // A) The target is uniquely covered. Since there already exists an
        //    incoming edge of the same type we want to add, we can't add the
        //    edge right away because then the coverage would no longer be unique.
        // B) The target is not uniquely covered and (one of) the cause for
        //    non-uniqueness comes from the edge type we are working on. In this
        //    case we want to ignore all the others incoming edge of the same
        //    type by extracting a new target and routing our new edge there.
        //    If there were other edge type also not unique this is not a
        //    problem: the new extracted node will be have an incomplete
        //    coverage and so we'll have to traverse up and choose the other
        //    sources.
        // C) The target is not uniquely covered and there is only one incoming
        //    edge of the type we want to create and the edge multiplicity is
        //    one. This case is similar to the previous one, we'll extract a
        //    new target to route the new edge and ignore the other existing one.
        boolean extractNewTgt =
            isTgtUniquelyCovered || origEdgeTypeCountInTgt > 1
                || origEdgeMult.isOne();

        if (extractNewTgt) {
            // Extract the new target.
            Multiplicity newTgtMult =
                newSrcMult.times(origEdgeMult).toNodeKind();
            newTgt = extractNode(newTgt, newTgtMult);
            // Create a new edge to the new target.
            extractEdge(origEdge, newSrc, newTgt, origEdgeMult,
                isTgtUniquelyCovered);
            // Make sure the new node will get properly connected later. 
            computeTraversal(newTgt);
            return newTgt;
        }

        // else:    !isTgtUniquelyCovered
        //       && origEdgeTypeCountInTgt == 1
        //       && origEdgeMult != 1 
        return splitTargetToUniqueCovers(origEdge, origEdgeMult, newSrc, newTgt);
    }

    // EDUARDO: Implement this method correctly.
    private PatternNode splitTargetToUniqueCovers(PatternEdge origEdge,
            Multiplicity origEdgeMult, PatternNode newSrc, PatternNode newTgt) {
        assert !origEdgeMult.isOne();
        Set<TypeEdge> edgeTypes = new MyHashSet<TypeEdge>();
        edgeTypes.addAll(this.shape.getTypeGraph().inEdgeSet(newTgt.getType()));
        edgeTypes.remove(origEdge.getType());
        // For now we only try to handle this case.
        assert edgeTypes.size() == 1;
        TypeEdge nonUniqueEdgeType = edgeTypes.iterator().next();
        Set<PatternEdge> inEdges = new MyHashSet<PatternEdge>();
        inEdges.addAll(this.shape.getInEdgesWithType(newTgt, nonUniqueEdgeType));
        int copies = inEdges.size() - 1;
        assert copies >= 1;
        Iterator<PatternEdge> iter = inEdges.iterator();
        for (int i = 0; i <= copies; i++) {
            PatternEdge inEdge = iter.next();
            Multiplicity inEdgeMult = this.shape.getMult(inEdge);
            PatternNode otherSrc = inEdge.source();
            Multiplicity otherSrcMult = this.shape.getMult(otherSrc);
            Multiplicity newTgtMult =
                otherSrcMult.times(inEdgeMult).toNodeKind();
            if (i < copies) {
                PatternNode splitTgt = extractNode(newTgt, newTgtMult);
                extractEdge(inEdge, otherSrc, splitTgt, inEdgeMult, false);
                extractEdge(origEdge, newSrc, splitTgt, origEdgeMult, true);
                computeTraversal(splitTgt);
            } else {
                this.shape.setMult(newTgt, newTgtMult);
                if (otherSrcMult.isOne() && this.shape.containsEdge(origEdge)) {
                    this.shape.setMult(origEdge, Multiplicity.ONE_EDGE_MULT);
                }
            }
        }
        return newTgt;
    }

    private PatternEdge getMissingOutEdge(PatternNode newSrc,
            boolean isSrcEnvironment) {
        PatternNode origSrc = this.morph.getNode(newSrc);
        Set<PatternEdge> origEdges = this.originalShape.outEdgeSet(origSrc);
        for (PatternEdge origEdge : origEdges) {
            if (!this.shape.hasOutEdgeWithType(newSrc, origEdge.getType())) {
                return origEdge;
            }
        }
        // If we reach this point it means the source node has all outgoing
        // edges that it should. But...
        if (isSrcEnvironment) {
            // ...if the source is part of the environment graph then we have
            // to check if all targets are uniquely covered.
            for (PatternEdge newOutEdge : this.shape.outEdgeSet(newSrc)) {
                if (!this.shape.isUniquelyCovered(newOutEdge.target())) {
                    return newOutEdge;
                }
            }
        }
        // There are no missing outgoing edges.
        return null;
    }

    /**
     * Special method used only in assertions. Returns an edge outgoing from the
     * given source with the given type and target node. 
     */
    private PatternEdge getMissingOutEdge(PatternNode newSrc,
            TypeEdge edgeType, PatternNode tgt) {
        PatternNode origSrc = this.morph.getNode(newSrc);
        PatternEdge origEdge =
            this.originalShape.getOutEdgeWithType(origSrc, edgeType);
        if (origEdge.target().equals(tgt)) {
            return origEdge;
        } else {
            return null;
        }
    }

    private PatternEdge getMissingInEdge(PatternNode newTgt) {
        PatternNode origTgt = this.morph.getNode(newTgt);
        Set<PatternEdge> origEdges = this.originalShape.inEdgeSet(origTgt);
        for (PatternEdge origEdge : origEdges) {
            if (!this.shape.hasInEdgeWithType(newTgt, origEdge.getType())) {
                if (newTgt.getLayer() == 1) {
                    return origEdge;
                }// else:
                 // Make sure that the returned edge has an intersection with
                 // some other incoming edge. This in turn ensures that we are
                 // building the coverage for the target from connected parts,
                 // which leads to less non-determinism.
                if (this.shape.hasIntersection(newTgt, origEdge.getType())) {
                    // Note that a materialised node always starts with at least
                    // one incoming edge so eventually this test will hold for
                    // one of the missing incoming edges.
                    return origEdge;
                }
            }
        }
        // The target is fully covered so there is no missing edge.
        return null;
    }

    /** Returns true if the given node is in the environment graph. */
    // EZ says: this is probably a good candidate method for optimization.
    // Instead of traversing the shape all the time we could keep a set of nodes
    // that are part of the environment.
    private boolean isEnvironment(PatternNode pNode) {
        List<PatternNode> toTest = new LinkedList<PatternNode>();
        toTest.add(pNode);
        while (!toTest.isEmpty()) {
            PatternNode node = toTest.remove(0);
            if (this.match.containsNodeValue(node)) {
                return true;
            } else {
                for (PatternEdge inEdge : this.shape.inEdgeSet(node)) {
                    toTest.add(inEdge.source());
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // Auxiliary methods
    // ------------------------------------------------------------------------

    private List<PatternNode> cloneTraversalList(List<PatternNode> other,
            boolean ascending) {
        List<PatternNode> result = new MySortedList(ascending);
        result.addAll(other);
        return result;
    }

    private Map<TypeEdge,Set<PatternNode>> cloneDanglingMap(
            Map<TypeEdge,Set<PatternNode>> other) {
        Map<TypeEdge,Set<PatternNode>> result =
            new MyHashMap<TypeEdge,Set<PatternNode>>();
        for (Entry<TypeEdge,Set<PatternNode>> entry : other.entrySet()) {
            Set<PatternNode> set = new MyHashSet<PatternNode>();
            set.addAll(entry.getValue());
            result.put(entry.getKey(), set);
        }
        return result;
    }

    private void addToDanglingOut(TypeEdge edgeType, PatternNode newNode) {
        addToDanglingSet(this.danglingOut, edgeType, newNode);
    }

    private void addToDanglingIn(TypeEdge edgeType, PatternNode newNode) {
        addToDanglingSet(this.danglingIn, edgeType, newNode);
    }

    private void addToDanglingSet(Map<TypeEdge,Set<PatternNode>> map,
            TypeEdge edgeType, PatternNode newNode) {
        Set<PatternNode> set = map.get(edgeType);
        if (set == null) {
            set = new MyHashSet<PatternNode>();
            map.put(edgeType, set);
        }
        set.add(newNode);
    }

    private Set<PatternNode> getDanglingOut(TypeEdge edgeType) {
        return getDanglingSet(this.danglingOut, edgeType);
    }

    private Set<PatternNode> getDanglingIn(TypeEdge edgeType) {
        return getDanglingSet(this.danglingIn, edgeType);
    }

    private Set<PatternNode> getDanglingSet(Map<TypeEdge,Set<PatternNode>> map,
            TypeEdge edgeType) {
        Set<PatternNode> set = map.get(edgeType);
        if (set == null) {
            set = Collections.emptySet();
        }
        return set;
    }

    private void addToUpTraversal(PatternNode newNode) {
        this.upTraversal.add(newNode);
    }

    private void addToDownTraversal(PatternNode newNode) {
        this.downTraversal.add(newNode);
    }

    // ------------------------------------------------------------------------
    // Basic interfacing methods
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

    // ------------------------------------------------------------------------
    // Consistency check methods
    // ------------------------------------------------------------------------

    /** Returns true if the materialisation is valid. */
    private boolean isValid() {
        assert areMapsEmpty();
        assert isMorphConsistent();
        assert isMatchConcrete();
        assert isEnvironmentCorrect();
        assert isConcretePartCommuting(false);
        return areMapsEmpty()
            && isMorphConsistent() // && isMorphValid()
            && isMatchConcrete() && isEnvironmentCorrect()
            && isConcretePartCommuting(false);
    }

    private boolean areMapsEmpty() {
        if (!this.upTraversal.isEmpty() || !this.downTraversal.isEmpty()) {
            return false;
        }
        for (Set<PatternNode> set : this.danglingOut.values()) {
            if (!set.isEmpty()) {
                return false;
            }
        }
        for (Set<PatternNode> set : this.danglingIn.values()) {
            if (!set.isEmpty()) {
                return false;
            }
        }
        return true;
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

    private boolean isEnvironmentCorrect() {
        // Compute the nodes for the environment graph.
        List<PatternNode> toTraverse = new ArrayList<PatternNode>();
        for (RuleNode rNode : this.rule.lhs().getLayerNodes(0)) {
            toTraverse.add(this.match.getNode(rNode));
        }
        Set<PatternNode> envNodes = this.shape.getDownwardTraversal(toTraverse);
        // Check if each node of the environment graph is uniquely covered.
        for (PatternNode envNode : envNodes) {
            if (!this.shape.isUniquelyCovered(envNode)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConcretePartCommuting(boolean acceptNonWellFormed) {
        return this.shape.isConcretePartCommuting(acceptNonWellFormed);
    }

    private void filterSourcesByCommutativity(Set<PatternNode> sources,
            PatternNode target, TypeEdge edgeType) {
        Iterator<PatternNode> iter = sources.iterator();
        while (iter.hasNext()) {
            PatternNode source = iter.next();
            if (!isNewEdgeCommuting(source, edgeType, target)) {
                iter.remove();
            }
        }
    }

    /** Checks if the addition of a new edge preserves commutativity. */
    private boolean isNewEdgeCommuting(PatternNode source, TypeEdge edgeType,
            PatternNode target) {
        assert target.getLayer() != 0;
        assert !this.shape.hasInEdgeWithType(target, edgeType);

        PatternEdge newEdge = this.shape.createEdge(source, edgeType, target);
        this.shape.addEdge(newEdge);
        boolean commuting = isConcretePartCommuting(true);
        this.shape.removeEdge(newEdge);

        if (commuting || !this.shape.getMult(source).isCollector()) {
            return commuting;
        }

        // else: the shape is not commuting but the source is a collector node
        // so maybe we can materialise a copy in a way that the shape becomes
        // commuting.
        boolean emptyIntersection = true;
        for (PatternEdge inEdge : this.shape.inEdgeSet(target)) {
            // Special case: the source is already covering a pattern in the
            // target.
            if (inEdge.source() == source) {
                return false;
            }
            PatternNode pNode1 = source;
            PatternNode pNode2 = inEdge.source();
            for (HostNode sNode : target.getPattern().nodeSet()) {
                if (edgeType.isCod(sNode) && inEdge.isCod(sNode)) {
                    emptyIntersection = false;
                    // We have an intersection of morphisms on the simple node.
                    HostNode sNode1 = edgeType.getPreImage(sNode);
                    HostNode sNode2 = inEdge.getPreImage(sNode);
                    if (!this.shape.haveCommonAncestor(pNode1, sNode1, pNode2,
                        sNode2)) {
                        return false;
                    }
                }
            }

        }
        return !emptyIntersection;
    }

    /** Checks if the multiplicities in the shape make sense. */
    private boolean isAdmissable(boolean acceptNonWellFormed) {
        final int n = PatternAbsParam.getInstance().getNodeMultBound();
        final int e = PatternAbsParam.getInstance().getEdgeMultBound();
        final boolean nGtE = n > e;
        for (int layer = 1; layer <= this.shape.depth(); layer++) {
            for (PatternNode target : this.shape.getLayerNodes(layer)) {
                if (!isAdmissable(target, acceptNonWellFormed, nGtE)) {
                    return false;
                }
            }
        }
        return true;
    }

    // EDUARDO: Probably refactor this method...
    private boolean isAdmissable(PatternNode tgt, boolean acceptNonWellFormed,
            boolean nGtE) {
        Multiplicity tgtMult = this.shape.getMult(tgt);
        for (TypeEdge typeEdge : this.shape.getTypeGraph().inEdgeSet(
            tgt.getType())) {
            Multiplicity acc =
                Multiplicity.getMultiplicity(0, 0, MultKind.EQSYS_MULT);

            // Sum the multiplicities of the already existing incoming edges.
            for (PatternEdge inEdge : this.shape.getInEdgesWithType(tgt,
                typeEdge)) {
                Multiplicity srcMult = this.shape.getMult(inEdge.source());
                Multiplicity edgeMult = this.shape.getMult(inEdge);
                acc = acc.add(srcMult.times(edgeMult));
            }

            // We also need to consider the dangling edges.
            srcLoop: for (PatternNode src : getDanglingOut(typeEdge)) {
                if (!this.downTraversal.contains(src)) {
                    continue srcLoop;
                }
                PatternEdge inEdge = getMissingOutEdge(src, typeEdge, tgt);
                Multiplicity srcMult = this.shape.getMult(src);
                Multiplicity edgeMult = this.shape.getMult(inEdge);
                acc = acc.add(srcMult.times(edgeMult));
            }

            Multiplicity sum = acc.toNodeKind();
            if (!tgtMult.subsumes(sum)) {
                // Check for the special case when n > e. In this case
                // we might have for example tgtMult = 2 and sum = 2+.
                // This is still correct, there's no way to make these
                // multiplicities more precise since the bounds are
                // different.
                if (nGtE) {
                    if (!tgtMult.ge(sum)) {
                        return false;
                    }
                } else if (!acceptNonWellFormed || !sum.isZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    private static class MySortedList extends LinkedList<PatternNode> {

        final boolean ascending;

        MySortedList(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public boolean add(PatternNode node) {
            int index = 0;
            Iterator<PatternNode> iter = iterator();
            while (iter.hasNext()) {
                PatternNode other = iter.next();
                int result = compare(node, other);
                if (result == 0) {
                    index = -1;
                    break;
                } else if (result > 0) {
                    break;
                } else { // result < 0
                    index++;
                }
            }
            if (index >= 0) {
                super.add(index, node);
                return true;
            } else {
                return false;
            }
        }

        int compare(PatternNode n0, PatternNode n1) {
            // n0 < n1 => -1
            // n0 = n1 =>  0
            // n0 > n1 =>  1
            if (n0.getNumber() == n1.getNumber()) {
                return 0;
            }
            if (this.ascending) {
                if (n0.getLayer() <= n1.getLayer()) {
                    return 1;
                } else {
                    return -1;
                }
            } else { // descending layers.
                if (n0.getLayer() >= n1.getLayer()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

    }

}
