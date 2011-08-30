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

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.RuleEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An equation system is the mechanism used to resolve non-determinism during
 * the materialisation phase.
 * The system is composed of multiplicity variables, that are associated with
 * edges or edge signatures of the shape under materialisation.
 * The variables are used to form equations over multiplicities values. The
 * process of solving the equation system entails finding all possible values
 * for the multiplicity variables that satisfy all equations.
 * When a solution is found the materialisation object is adjusted accordingly.
 * 
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    /**
     * The initial materialisation object for which the equation system should
     * be built. If the system has only one solution this object is modified
     * directly. If there are multiple solutions, this object is cloned.
     */
    private final Materialisation mat;

    /**
     * List of all variables of the system. The object is final but the list
     * is modified during the creation of the system. After that it no longer
     * changes. 
     */
    private final List<MultVar> vars;

    /** The number of multiplicity variables of the system. */
    private int varCount;

    /**
     * Set of all equations that compose the system. The object is final but 
     * the set is modified during the creation of the system. After that it
     * no longer changes. 
     */
    private final THashSet<Equation> eqs;

    /**
     * Maps from possible new edges that can be in the materialised shape to
     * their corresponding multiplicity variables.  The object are final but 
     * are modified during the creation of the system. After that they no
     * longer change. 
     */
    private final Map<ShapeEdge,EdgeMultVar> outEdgeMultVarMap;
    private final Map<ShapeEdge,EdgeMultVar> inEdgeMultVarMap;

    /**
     * Special field used to indicate if this equation system was created
     * to solve a node pull operation, which makes the system simpler.
     * In the normal case, is set to null.
     */
    private final Set<ShapeNode> pulledNodes;
    private final Set<ShapeEdge> ignoreEdges;

    /** 
     * Constructs the equation system based on the given materialisation.
     * After the constructor finishes, a call to solve() yields all possible
     * resulting materialisations, with the proper shapes.
     * 
     * Passing a materialisation that has an empty set of possible new edges
     * gives an assertion error.
     */
    public EquationSystem(Materialisation mat, Set<ShapeNode> pulledNodes,
            Set<ShapeEdge> ignoreEdges) {
        assert !mat.getPossibleNewEdgeSet().isEmpty();
        this.mat = mat;
        this.pulledNodes =
            pulledNodes != null ? pulledNodes
                    : Collections.<ShapeNode>emptySet();
        this.ignoreEdges =
            ignoreEdges != null ? ignoreEdges
                    : Collections.<ShapeEdge>emptySet();
        this.vars = new ArrayList<MultVar>();
        this.varCount = 0;
        this.eqs = new THashSet<Equation>();
        this.outEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.inEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.create();
    }

    @Override
    public String toString() {
        return "Equalities: " + this.eqs;
    }

    /**
     * Creates the equation system.
     * See the comments in the code for more details.
     */
    private void create() {
        Shape shape = this.mat.getShape();

        // BEGIN - Create opposition relations and edge bundles.
        Set<EdgeBundle> bundles = new THashSet<EdgeBundle>();
        // For all possible new edges.
        for (ShapeEdge edge : this.mat.getPossibleNewEdgeSet()) {
            // Create one outgoing and one incoming multiplicity variable.
            EdgeMultVar outMultVar = this.newEdgeMultVar(edge, OUTGOING);
            EdgeMultVar inMultVar = this.newEdgeMultVar(edge, INCOMING);
            // The variables are opposite of one another.
            outMultVar.opposite = inMultVar;
            inMultVar.opposite = outMultVar;
            // Edge bundles.
            TypeLabel label = edge.label();
            // Outgoing.
            EdgeBundle outBundle =
                this.getEdgeBundle(bundles, edge.source(), label, OUTGOING,
                    edge);
            outBundle.addVar(outMultVar);
            // Incoming.
            EdgeBundle inBundle =
                this.getEdgeBundle(bundles, edge.target(), label, INCOMING,
                    edge);
            inBundle.addVar(inMultVar);
        } // END - Create opposition relations and edge bundles.

        this.filterEdgeBundles(bundles);

        // For each edge bundle, create an equality.
        // BEGIN - Create equalities.
        for (EdgeBundle bundle : bundles) {
            EdgeMultDir direction = bundle.direction;

            // Counter of bundle edges that were matched by the rule.
            Multiplicity matchedEdgesMult =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            boolean isBundleNodeCollector =
                shape.getNodeMult(bundle.node).isCollector();
            // For all edges in the bundle.
            // BEGIN - Bundle edges loop.
            Set<ShapeEdge> bundleEdges = bundle.edges;
            for (ShapeEdge edge : bundleEdges) {
                MultVar var = this.getEdgeVariable(edge, direction);
                if (var == null) {
                    // There is no variable for the edge. This means that the
                    // edge is already in the shape.
                    // Check if the shape edge is an image of a rule edge.
                    Set<RuleEdge> edgesR =
                        this.mat.getMatch().getPreImages(edge);
                    if (edgesR.size() > 0 || this.isForNodePull()) {
                        assert edgesR.size() == 1 || this.isForNodePull();
                        // Yes, we have a rule edge, or all edges in the shape
                        // are considered to be fixed. Increment the counter.
                        matchedEdgesMult = matchedEdgesMult.increment();
                    } else {
                        // There are some edges of the shape in the bundle that were
                        // not matched by rule edges. Create another multiplicity
                        // variable to represent the value of the remainder
                        // multiplicity for the edge signature.
                        EdgeSignature remEs =
                            shape.getEdgeSignature(edge, direction);
                        this.newEdgeSigMultVar(bundle, remEs);
                    }
                } else if (!isBundleNodeCollector) {
                    // The edge has a variable so we can compute an upper bound.
                    ShapeNode opposite = edge.opposite(direction);
                    Multiplicity upperBound =
                        Multiplicity.convertToEdgeMult(shape.getNodeSetMultSum(shape.getEquivClassOf(opposite)));
                    if (!upperBound.isUnbounded()) {
                        Equation eq =
                            this.newEquation(1, upperBound, false, true);
                        eq.addVar(var);
                    }
                }
            } // END - Bundle edges loop.

            Multiplicity origEsMult =
                this.mat.getOriginalShape().getEdgeSigMult(bundle.origEs,
                    direction);
            // Create the equation with constant = origEsMult - matchedEdgesMult .
            Equation eq =
                this.newEquation(bundle.vars.size(),
                    origEsMult.sub(matchedEdgesMult), isBundleNodeCollector,
                    false);
            // Add all variables in the bundle to the equation.
            for (MultVar var : bundle.vars) {
                eq.addVar(var);
            }
            // Connect the bundle and its equation.
            bundle.setEquation(eq);
        } // END - Create equalities.
    }

    /**
     * Creates and returns a new edge multiplicity variable for the given
     * edge and direction.
     * The map from edges to variables is properly adjusted along with the
     * variable set and count. 
     */
    private EdgeMultVar newEdgeMultVar(ShapeEdge edge, EdgeMultDir direction) {
        EdgeMultVar result = new EdgeMultVar(this.varCount, edge);
        this.getEdgeMultVarMap(direction).put(edge, result);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    /**
     * Creates and returns a new edge signature multiplicity variable for
     * the given signature and direction.
     * The variable set and count is properly adjusted.
     */
    private void newEdgeSigMultVar(EdgeBundle bundle, EdgeSignature es) {
        for (MultVar var : bundle.vars) {
            if (var instanceof EdgeSigMultVar
                && ((EdgeSigMultVar) var).es.equals(es)) {
                return;
            }
        }
        EdgeSigMultVar result = new EdgeSigMultVar(this.varCount, es);
        this.vars.add(result);
        this.varCount++;
        bundle.addVar(result);
    }

    /**
     * Creates and returns a new equation,
     * while storing it in the equation set.
     */
    private Equation newEquation(int varsCount, Multiplicity constant,
            boolean isCollector, boolean isUpperBound) {
        Equation result =
            new Equation(varsCount, constant, isCollector, isUpperBound);
        this.eqs.add(result);
        return result;
    }

    /**
     * Searches in the given bundle set for a bundle with corresponding node,
     * label and direction. If a proper bundle is not found, a new one is
     * created and added to the set.  
     */
    private EdgeBundle getEdgeBundle(Set<EdgeBundle> bundles, ShapeNode node,
            TypeLabel label, EdgeMultDir direction, ShapeEdge edge) {
        EdgeBundle result = null;
        Shape shape = this.mat.getShape();
        Shape origShape = this.mat.getOriginalShape();
        ShapeMorphism morph = this.mat.getShapeMorphism();
        EdgeSignature origEs =
            morph.getEdgeSignature(origShape,
                shape.getEdgeSignature(edge, direction));

        for (EdgeBundle bundle : bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction
                && bundle.origEs.equals(origEs)) {
                result = bundle;
                break;
            }
        }

        if (result == null) {
            Set<ShapeEdge> edges = new THashSet<ShapeEdge>();
            for (ShapeEdge edgeS : shape.binaryEdgeSet(node, direction)) {
                if (edgeS.label().equals(label)) {
                    EdgeSignature es = shape.getEdgeSignature(edgeS, direction);
                    EdgeSignature otherOrigEs =
                        morph.getEdgeSignature(origShape, es);
                    if (otherOrigEs.equals(origEs)
                        && !this.ignoreEdges.contains(edgeS)) {
                        edges.add(edgeS);
                    }
                }
            }
            result = new EdgeBundle(origEs, node, label, direction, edges);
            bundles.add(result);
        }
        return result;
    }

    private void filterEdgeBundles(Set<EdgeBundle> bundles) {
        if (this.isForNodePull()) {
            Iterator<EdgeBundle> iter = bundles.iterator();
            while (iter.hasNext()) {
                EdgeBundle bundle = iter.next();
                if (!this.shouldKeepBundle(bundle)) {
                    iter.remove();
                }
            }
        }
    }

    private static boolean haveSingleOppositeEc(Shape shape,
            Set<ShapeEdge> edges, EdgeMultDir direction) {
        boolean result = true;
        EquivClass<ShapeNode> currEc;
        EquivClass<ShapeNode> prevEc = null;
        for (ShapeEdge edge : edges) {
            currEc = shape.getEdgeSignature(edge, direction).getEquivClass();
            if (prevEc == null) {
                prevEc = currEc;
            }
            if (!currEc.equals(prevEc)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean shouldKeepBundle(EdgeBundle bundle) {
        assert this.isForNodePull();
        if (this.pulledNodes.contains(bundle.node)) {
            // Pulled nodes always retain they bundles.
            return true;
        }
        if (!haveSingleOppositeEc(this.mat.getShape(), bundle.edges,
            bundle.direction)) {
            // This bundle is important because it crosses equivalence classes.
            return true;
        }
        Shape origShape = this.mat.getPullNodeOrigShape();
        for (ShapeEdge edge : bundle.edges) {
            if (origShape.containsEdge(edge)) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if the pulledNode field is non-null. */
    private boolean isForNodePull() {
        return !this.pulledNodes.isEmpty();
    }

    /**
     * Returns the (possible null) variable associated with the given edge
     * and direction.
     */
    private EdgeMultVar getEdgeVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeMultVarMap(direction).get(edge);
    }

    /** Returns the proper (non-null) map accordingly to the given direction. */
    private Map<ShapeEdge,EdgeMultVar> getEdgeMultVarMap(EdgeMultDir direction) {
        Map<ShapeEdge,EdgeMultVar> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outEdgeMultVarMap;
            break;
        case INCOMING:
            result = this.inEdgeMultVarMap;
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Finds all solutions of this equation system and return all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public void solve(Set<Materialisation> result) {
        // Compute all solutions.
        Set<Solution> finishedSols = new THashSet<Solution>();
        Solution initialSol =
            new Solution(this.varCount, (THashSet<Equation>) this.eqs.clone());
        Set<Solution> partialSols = new THashSet<Solution>();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Solution sol = partialSols.iterator().next();
            partialSols.remove(sol);
            this.iterateSolution(sol, partialSols, finishedSols);
        }
        // Create the return objects.
        for (Solution sol : finishedSols) {
            Materialisation mat;
            if (finishedSols.size() == 1) {
                mat = this.mat;
            } else {
                mat = this.mat.clone();
            }
            this.updateMatFromSolution(mat, sol);
            if (sol.requiresNodePull()) {
                assert !this.isForNodePull();
                this.performNodePulls(mat, sol, result);
            } else {
                result.add(mat);
            }
        }
    }

    /**
     * Iterate the given solution over all equations of the system and try to
     * fix more variables. This may lead to branching. In this case, an
     * heuristic is used to decide on which equation to use that is likely
     * to produce less branching. Returns the set of new (partial) solutions.
     */
    private void iterateSolution(Solution sol, Set<Solution> partialSols,
            Set<Solution> finishedSols) {
        boolean isFinished = this.iterateEquations(sol);
        if (isFinished) {
            finishedSols.add(sol);
        } else {
            Equation branchingEq = sol.getBestBranchingEquation();
            branchingEq.getNewSolutions(sol, partialSols, finishedSols);
        }
    }

    /**
     * Iterate the given solution over all equations of the system and try to
     * fix more variables. 
     */
    @SuppressWarnings("unchecked")
    private boolean iterateEquations(Solution sol) {
        // For all equations, try to fix as many variables as possible.
        Set<Equation> currEqsToUse = (Set<Equation>) sol.eqsToUse.clone();
        int currEqsToUseSize = currEqsToUse.size();
        int prevEqsToUseSize = 0;
        while (currEqsToUseSize != prevEqsToUseSize && currEqsToUseSize > 0) {
            for (Equation eq : currEqsToUse) {
                eq.computeNewValues(sol);
            }
            prevEqsToUseSize = currEqsToUseSize;
            currEqsToUse = (Set<Equation>) sol.eqsToUse.clone();
            currEqsToUseSize = currEqsToUse.size();
        }
        return this.checkIfSolutionIsFinished(sol, currEqsToUse);
    }

    private boolean checkIfSolutionIsFinished(Solution sol,
            Set<Equation> currEqsToUse) {
        if (sol.isComplete()) {
            return true;
        }
        if (!this.isForNodePull()) {
            return false;
        }// else
         // This is for node pull and the solution is not complete.
        boolean result = true;
        Shape shape = this.mat.getShape();
        for (Equation eq : currEqsToUse) {
            if (eq.isStillUseful(shape, sol, eq.getOpenVars(sol))) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void updateMatFromSolution(Materialisation mat, Solution sol) {
        if (sol.isComplete() && !this.isForNodePull()) {
            this.updateMatFromCompleteSolution(mat, sol);
        } else {
            this.updateMatFromIncompleteSolution(mat, sol);
        }
    }

    /**
     * Adjust the given materialisation object using the given solution.
     */
    private void updateMatFromCompleteSolution(Materialisation mat, Solution sol) {
        assert sol.isComplete();
        Shape shape = mat.getShape();
        ShapeMorphism morph = mat.getShapeMorphism();
        // Each variable is associated with either an edge or a signature.
        // Iterate over the variables and modify the shape accordingly.
        for (MultVar var : this.vars) {
            if (sol.impreciseVars.contains(var)) {
                // We'll deal with the imprecise variables in the end.
                assert !this.isForNodePull();
                continue;
            }
            if (var instanceof EdgeMultVar) {
                // We have a variable associated with a possible edge.
                EdgeMultVar edgeMultVar = (EdgeMultVar) var;
                Multiplicity value = sol.getValue(edgeMultVar);
                ShapeEdge edgeS = edgeMultVar.edge;
                if (!value.isZero()) {
                    // We have to include the edge in the shape.
                    // But first, check if it's not included, because of an
                    // opposite variable.
                    if (!shape.edgeSet().contains(edgeS)) {
                        shape.addEdgeWithoutCheck(edgeS);
                    }
                    // Set the multiplicity accordingly.
                    shape.setEdgeMult(edgeS, edgeMultVar.getDirection(), value);
                } else {
                    // The possible edge is not inserted in the shape.
                    // Remove it from the shape morphism.
                    morph.removeEdge(edgeS);
                }
            } else if (var instanceof EdgeSigMultVar) {
                // We have a variable associated with the remainder of an edge
                // signature multiplicity;
                EdgeSigMultVar edgeSigMultVar = (EdgeSigMultVar) var;
                Multiplicity value = sol.getValue(edgeSigMultVar);
                // Setting the edge signature multiplicity.
                shape.setEdgeSigMult(edgeSigMultVar.es,
                    edgeSigMultVar.getDirection(), value);
            }
        }
    }

    /**
     * Updates the given materialisation with the (possibly incomplete)
     * solution. This method can only be used if the system was created for
     * a node pull. Variables without values have their edges inserted in the
     * shape and the multiplicities are inherited from the original edge.
     */
    private void updateMatFromIncompleteSolution(Materialisation mat,
            Solution sol) {
        assert this.isForNodePull();

        Shape shape = mat.getShape();
        Shape origShape = mat.getOriginalShape();
        ShapeMorphism morph = mat.getShapeMorphism();

        for (MultVar var : this.vars) {
            assert var instanceof EdgeMultVar;
            // We have a variable associated with a possible edge.
            EdgeMultVar edgeMultVar = (EdgeMultVar) var;
            ShapeEdge edgeS = edgeMultVar.edge;
            Multiplicity value;
            if (sol.hasValue(edgeMultVar)) {
                value = sol.getValue(edgeMultVar);
            } else {
                // We don't have a value for this variable.
                // Get the value from the original edge.
                ShapeEdge origEdge = morph.getEdge(edgeS);
                value =
                    origShape.getEdgeMult(origEdge, edgeMultVar.getDirection());
            }
            if (!value.isZero()) {
                // We have to include the edge in the shape.
                // But first, check if it's not included, because of an
                // opposite variable.
                if (!shape.edgeSet().contains(edgeS)) {
                    shape.addEdgeWithoutCheck(edgeS);
                    // Set the multiplicity accordingly.
                    shape.setEdgeMult(edgeS, edgeMultVar.getDirection(), value);
                }
            } else {
                // The possible edge is not inserted in the shape.
                // Remove it from the shape morphism.
                morph.removeEdge(edgeS);
            }
        }
        // Make sure we don't trigger another equation system.
        sol.impreciseVars.clear();
    }

    private void performNodePulls(Materialisation mat, Solution sol,
            Set<Materialisation> result) {
        mat.beginNodePull();
        Shape shape = mat.getShape();
        Set<ShapeNode> newNodes = new THashSet<ShapeNode>();
        for (MultVar var : sol.impreciseVars) {
            if (var instanceof EdgeMultVar) {
                // We have an imprecise variable associated with a possible edge.
                ShapeNode newNode =
                    shape.pullNode(mat, ((EdgeMultVar) var).edge,
                        var.getDirection());
                newNodes.add(newNode);
            }
        }
        mat.endNodePull();
        // Check if we need to resolve non-determinism.
        if (mat.isFinished()) {
            // No, we don't, just return the materialisation object.
            result.add(mat);
        } else {
            // Yes, we do. Create a new equation system.
            Set<ShapeEdge> ignoreEdges = new THashSet<ShapeEdge>();
            for (ShapeEdge edge : this.outEdgeMultVarMap.keySet()) {
                MultVar outVar = this.outEdgeMultVarMap.get(edge);
                if (sol.getValue(outVar).isOne()) {
                    MultVar inVar = ((EdgeMultVar) outVar).opposite;
                    if (sol.getValue(inVar).isOne()) {
                        ignoreEdges.add(edge);
                    }
                }
            }
            new EquationSystem(mat, newNodes, ignoreEdges).solve(result);
        }
    }

    /**
     * Abstract class to represent all types of multiplicity variables. 
     */
    private static abstract class MultVar {

        /** The unique variable number. */
        final int number;
        /** The bundle this variable is associated with. */
        EdgeBundle bundle;

        /** Basic constructor. */
        MultVar(int number) {
            this.number = number;
        }

        /** Basic setter method. */
        void setBundle(EdgeBundle bundle) {
            assert bundle != null && this.bundle == null;
            this.bundle = bundle;
        }

        /** Basic getter method. */
        EdgeMultDir getDirection() {
            return this.bundle.direction;
        }

        /** Basic inspection method. */
        boolean isUsed() {
            return this.bundle.hasEquation();
        }

        @Override
        public abstract String toString();
    }

    /**
     * Multiplicity variable associated with a possible edge.
     * All such variables have an opposite one, representing the other end
     * of the edge. These two opposite variables have an existential relation,
     * i.e., if a variable is zero then its opposite must also be zero, and if
     * a variable is positive its opposite must also be positive (but not
     * necessarily must have the same value).
     */
    private static class EdgeMultVar extends MultVar {

        /** The edge associated with this variable. */
        final ShapeEdge edge;
        /** The opposite variable. */
        EdgeMultVar opposite;

        /** Basic constructor. */
        EdgeMultVar(int number, ShapeEdge edge) {
            super(number);
            this.edge = edge;
        }

        /** Variables of this type a prefixed with an 'x'. */
        @Override
        public String toString() {
            return "x" + this.number;
        }

        /** Basic inspection method. */
        boolean isOppositeUsed() {
            return this.opposite.isUsed();
        }
    }

    /**
     * Multiplicity variable associated with an edge signature, representing
     * the remaining value of the signature multiplicity after the
     * materialisation is finished.
     */
    private static class EdgeSigMultVar extends MultVar {

        /** The edge signature associated with this variable. */
        final EdgeSignature es;

        /** Basic constructor. */
        EdgeSigMultVar(int number, EdgeSignature es) {
            super(number);
            this.es = es;
        }

        /** Variables of this type a prefixed with an 'y'. */
        @Override
        public String toString() {
            return "y" + this.number;
        }
    }

    /**
     * Class representing equations of the form: x0 + x1 + ... xn = const.
     * The variables can be of either type and there is no ordering of variables
     * numbers. The constant is an edge multiplicity. 
     */
    private static class Equation {

        /** The set of variables composing this equation. */
        final Set<MultVar> vars;
        /** The multiplicity constant. */
        final Multiplicity constant;
        /** Flag to indicate if the node from the edge bundle is a collector. */
        final boolean isCollector;

        final boolean isUpperBound;

        /** Basic constructor. */
        Equation(int varsCount, Multiplicity constant, boolean isCollector,
                boolean isUpperBound) {
            this.vars = new THashSet<MultVar>(varsCount);
            this.constant = constant;
            this.isCollector = isCollector;
            this.isUpperBound = isUpperBound;
        }

        /** Basic setter method. */
        void addVar(MultVar var) {
            assert this.isUpperBound ? this.vars.size() == 0 : true;
            this.vars.add(var);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<MultVar> iter = this.vars.iterator();
            while (iter.hasNext()) {
                result.append(iter.next().toString());
                if (iter.hasNext()) {
                    result.append("+");
                }
            }
            result.append((this.isUpperBound ? "<" : "") + "=" + this.constant);
            if (this.isCollector) {
                result.append("C");
            }
            return result.toString();
        }

        /**
         * Given a partial solution, tries to assign additional values to the
         * variables when possible. (See comments on code for additional
         * information). If no new values can be obtained from this equation
         * the given solution remains unchanged. 
         */
        void computeNewValues(Solution partialSol) {
            // EZ says: sorry for the multiple return points, but otherwise
            // the code becomes less readable...

            if (!partialSol.eqsToUse.contains(this)) {
                // This equation can't contribute in finding new values for
                // the solution. Nothing to do.
                return;
            }

            int openVarsCount = this.openVarsCount(partialSol);
            if (openVarsCount == 0) {
                // All variables in this equation have a value assigned.
                // Nothing to do.
                partialSol.eqsToUse.remove(this);
                return;
            }

            if (this.constant.isZero()) {
                assert !this.isUpperBound;
                // The equation constant is zero.
                // Since multiplicities can't be negative, this means that all
                // variables in the equation have to be zero.
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, this.constant);
                }
                // This equation is no longer useful for finding the remaining
                // values of this solution.
                partialSol.eqsToUse.remove(this);
                return;
            }

            if (!this.isUpperBound && openVarsCount == 1) {
                // We have only one variable without a value so we assign the
                // equation constant minus the sum of all other variables values
                // to the open variable.
                Multiplicity newConst = this.constant;
                for (Multiplicity mult : this.getFixedValues(partialSol,
                    openVarsCount)) {
                    newConst = newConst.sub(mult);
                }
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, newConst);
                }
                partialSol.eqsToUse.remove(this);
                return;
            }

            MultVar impreciseVars[] = this.getImpreciseVars(partialSol);
            if (impreciseVars.length == 1) {
                if (this.isUpperBound && this.constant.isOne()) {
                    // Let x = impreciseVars[0].
                    // We know that x > 0, since it is imprecise.
                    // We also know that x <= 1. Thus, x = 1. 
                    partialSol.setValue(impreciseVars[0], this.constant);
                    partialSol.eqsToUse.remove(this);
                    return;
                }

                if (this.constant.isOne() && !this.isCollector) {
                    // In this case we know that the value for the imprecise
                    // variable must be one and that all other open variables must
                    // be zero.
                    Multiplicity zero =
                        Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
                    for (MultVar var : this.getOpenVars(partialSol,
                        openVarsCount)) {
                        if (var.equals(impreciseVars[0])) {
                            partialSol.setValue(var, this.constant);
                        } else {
                            partialSol.setValue(var, zero);
                        }
                    }
                    partialSol.eqsToUse.remove(this);
                    return;
                }

                if (this.isCollector) {
                    // At this point we can't determine anything about the open
                    // variables except that they are positive. This means that
                    // all open variables become imprecise variables.
                    // Since all variables need a value for the solution to become
                    // complete, we set these imprecise variables to the equation
                    // constant. This value is not used anywhere, a proper value
                    // is produced when we pull out nodes at the last stage of
                    // materialisation.
                    // Also, we don't want this equation laying around because it
                    // will just cause unnecessary branching.
                    for (MultVar var : this.getOpenVars(partialSol,
                        openVarsCount)) {
                        partialSol.setValue(var, this.constant);
                        partialSol.impreciseVars.add(var);
                    }
                    partialSol.eqsToUse.remove(this);
                    return;
                }

                // If we reached this point it means that the constant is
                // a positive collector multiplicity. Nothing to do.
                assert this.constant.isCollector();
            }
        }

        /**
         * Counts the number of variables of this equation that open under
         * the given partial solution.
         */
        int openVarsCount(Solution partialSol) {
            int openVars = this.vars.size();
            for (MultVar var : this.vars) {
                if (partialSol.hasValue(var)) {
                    openVars--;
                }
            }
            return openVars;
        }

        /**
         * Returns a newly created array of open variables for the given
         * partial solution. The second parameter is used to avoid counting
         * the number of variables again, when the number is already known.
         */
        MultVar[] getOpenVars(Solution partialSol, int openVarsCount) {
            assert openVarsCount > 0;
            MultVar result[] = new MultVar[openVarsCount];
            int i = 0;
            for (MultVar var : this.vars) {
                if (!partialSol.hasValue(var)) {
                    result[i] = var;
                    i++;
                }
            }
            assert i == openVarsCount;
            return result;
        }

        /**
         * Returns a newly created array of open variables for the given
         * partial solution.
         */
        MultVar[] getOpenVars(Solution partialSol) {
            return this.getOpenVars(partialSol, this.openVarsCount(partialSol));
        }

        /**
         * Returns the variables of this equation that are marked as imprecise
         * in the given solution.
         */
        MultVar[] getImpreciseVars(Solution partialSol) {
            ArrayList<MultVar> result =
                new ArrayList<MultVar>(partialSol.impreciseVars.size());
            for (MultVar var : partialSol.impreciseVars) {
                if (this.vars.contains(var)) {
                    result.add(var);
                }
            }
            return result.toArray(new MultVar[result.size()]);
        }

        /**
         * Returns the variables of this equation that have a value
         * in the given solution.
         */
        Multiplicity[] getFixedValues(Solution partialSol, int openVarsCount) {
            Multiplicity result[] =
                new Multiplicity[this.vars.size() - openVarsCount];
            int i = 0;
            for (MultVar var : this.vars) {
                if (partialSol.hasValue(var)) {
                    result[i] = partialSol.getValue(var);
                    i++;
                }
            }
            return result;
        }

        /**
         * Branches the search for valid solutions by assigning all possible
         * valid values to the open variables of this equation.
         * The blow-up on the materialisation phase is now due to this method,
         * so we should try to avoid it like the plague...
         */
        void getNewSolutions(Solution partialSol, Set<Solution> partialSols,
                Set<Solution> finishedSols) {
            // EDUARDO: to be done: ensure that the most general solution
            // is computed when possible, to avoid redundant solutions...

            assert !partialSol.isComplete();
            assert partialSol.eqsToUse.contains(this);

            // Array of possible multiplicity values.
            Multiplicity mults[] = Multiplicity.getAllEdgeMultiplicities();
            int multsSize = mults.length;
            // Array of variables that need values.
            MultVar vars[] = this.getOpenVars(partialSol);
            int varsSize = vars.length;
            @SuppressWarnings("unchecked")
            Set<MultVar> impreciseVars =
                (Set<MultVar>) partialSol.impreciseVars.clone();
            // Array of current values for the variables.
            // Hold indexes of positions in the mults array.
            int currVals[] = new int[varsSize];

            // Start iterating.
            int curr = varsSize - 1;
            int prev;
            while (currVals[0] != multsSize) {
                // Test and store valid solution.
                Solution newSol = partialSol.clone();
                boolean aborted = false;
                // Store the current values in the new solution.
                for (int i = 0; i < vars.length; i++) {
                    MultVar var = vars[i];
                    Multiplicity mult = mults[currVals[i]];
                    if (mult.startsInZero() && impreciseVars.contains(var)) {
                        aborted = true;
                        break;
                    }
                    newSol.setValue(var, mult);
                }
                if (!aborted && this.isValidSolution(newSol)) {
                    if (newSol.isComplete()) {
                        finishedSols.add(newSol);
                    } else {
                        partialSols.add(newSol);
                    }
                }

                // Update pointers and compute next solution.
                currVals[curr]++;
                if (currVals[curr] == multsSize) {
                    prev = curr - 1;
                    while (prev >= 0 && ++currVals[prev] == multsSize) {
                        prev--;
                    }
                    if (prev >= 0) {
                        // Zero all indices between prev + 1 and curr .
                        for (int i = prev + 1; i <= curr; i++) {
                            currVals[i] = 0;
                        }
                    }
                }
            }

            // This equation is no longer useful for finding the remaining
            // values of this solution.
            partialSol.eqsToUse.remove(this);
        }

        /**
         * Calculates the bounded sum of the values provided by the given
         * solution for the equation values and compares the sum with the
         * equation constant.
         * @return true if the sum is equal to the constant, false otherwise. 
         */
        boolean isValidSolution(Solution sol) {
            Multiplicity sum =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            for (MultVar var : this.vars) {
                Multiplicity mult = sol.getValue(var);
                if (mult.isZero() && sol.impreciseVars.contains(var)) {
                    return false;
                }
                sum = sum.add(mult);
            }
            return this.constant.equals(sum);
        }

        boolean isStillUseful(Shape shape, Solution sol, MultVar openVars[]) {
            EdgeBundle bundle = null;
            Set<ShapeEdge> edges = new THashSet<ShapeEdge>();
            for (MultVar var : openVars) {
                if (var instanceof EdgeMultVar) {
                    EdgeMultVar edgeMultVar = (EdgeMultVar) var;
                    if (!edgeMultVar.isOppositeUsed()) {
                        bundle = edgeMultVar.bundle;
                        edges.add(edgeMultVar.edge);
                    }
                } else {
                    return true;
                }
            }
            if (bundle != null) {
                return !haveSingleOppositeEc(shape, edges, bundle.direction);
            } else {
                return true;
            }
        }
    }

    /**
     * Class that represent a collection of edges that are bundled by a single
     * edge multiplicity. This comprises the new possible edges of the
     * materialisation and the edge signatures already in the shape. We can't
     * use a simple edge signature here because nodes on the opposite direction
     * of the edges are already in distinct equivalence classes. 
     */
    private static class EdgeBundle {

        final EdgeSignature origEs;
        /**
         * The node associated with this bundle. It can be either a source or
         * target node, depending on the direction.
         */
        final ShapeNode node;
        /** The label for all edges of the bundle. */
        final TypeLabel label;
        /** Defines the direction of the edges. */
        final EdgeMultDir direction;
        /** The set of edges that form this bundle. */
        final Set<ShapeEdge> edges;
        /** The variables for the possible edges that are part of the bundle. */
        final Set<MultVar> vars;
        /** The equation associated with this bundle. */
        Equation eq;

        /** Basic constructor. */
        EdgeBundle(EdgeSignature origEs, ShapeNode node, TypeLabel label,
                EdgeMultDir direction, Set<ShapeEdge> edges) {
            this.origEs = origEs;
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.edges = edges;
            this.vars = new THashSet<MultVar>();
        }

        @Override
        public String toString() {
            return this.direction + ":" + this.node + "-" + this.label + "-"
                + this.vars;
        }

        /** Basic setter method. */
        void addVar(MultVar var) {
            var.setBundle(this);
            this.vars.add(var);
            if (var instanceof EdgeMultVar) {
                ShapeEdge edge = ((EdgeMultVar) var).edge;
                this.edges.add(edge);
            }
        }

        /** Basic setter method. */
        void setEquation(Equation eq) {
            assert eq != null;
            this.eq = eq;
        }

        /** Basic inspection method. */
        boolean hasEquation() {
            return this.eq != null;
        }
    }

    /**
     * Class representing a possible solution for the equation system.
     * This is the only dynamic part of the system, the remaining structures
     * are all fixed when trying to solve the system. Objects of this class
     * are cloned during the solution search, hence we try to keep the objects
     * as small as possible. 
     */
    private static class Solution {

        /**
         * The array of values corresponding to an assignment to the variables
         * in the system. The array is indexed by the variable number, thus,
         * for example, the value for variable 'x3' is in value[3]. If a
         * position in the array is null then the corresponding variable is
         * still open in the solution.
         */
        final Multiplicity values[];

        /** Number of non-null values of this solution. */
        int valuesCount;

        /**
         * Set of equations that can still provide new values for variables.
         * When an equation has no open variables it can no longer contribute
         * to the solution search and therefore the equation is removed from
         * this set.
         */
        final THashSet<Equation> eqsToUse;

        /**
         * Set of variables that we know are positive but don't have a
         * precise value yet. Sometimes an imprecise variable can become
         * precise, if it does not, then we have to pull nodes in the shape.
         * */
        final THashSet<MultVar> impreciseVars;

        /** Basic constructor. */
        Solution(int varCount, THashSet<Equation> eqsToUse) {
            this.values = new Multiplicity[varCount];
            this.valuesCount = 0;
            this.eqsToUse = eqsToUse;
            this.impreciseVars = new THashSet<MultVar>();
        }

        /** Returns true if all values are non-null. */
        boolean isComplete() {
            return this.valuesCount == this.values.length
                || this.eqsToUse.isEmpty();
        }

        /** Returns true if the given variable has a value in this solution. */
        boolean hasValue(MultVar var) {
            return this.values[var.number] != null;
        }

        /**
         * Returns the non-null value associated with the given variable in
         * this solution. Fails in an assertion if the variable has no value.
         */
        Multiplicity getValue(MultVar var) {
            assert this.hasValue(var);
            return this.values[var.number];
        }

        /**
         * Sets the value of the given variable to the given multiplicity.
         * Fails in an assertion if the variable already has a value.
         * This method may have two side-effects:
         * 1) If the variable being assigned a value is of type EdgeMultVar
         * then its opposite value also receives a value.
         * 2) Equations that become fully assigned are removed from the set
         * of equations to be used by this solution.
         */
        void setValue(MultVar var, Multiplicity mult) {
            assert !this.hasValue(var);
            assert mult != null;
            this.values[var.number] = mult;
            this.valuesCount++;
            this.impreciseVars.remove(var);

            // Check if the variable has an opposite one. And if yes, also
            // set its value.
            if (var instanceof EdgeMultVar) {
                MultVar opposite = ((EdgeMultVar) var).opposite;
                if (mult.isZero()) {
                    // We are setting this variable to zero so its opposite
                    // must also be zero.
                    assert !this.hasValue(opposite);
                    assert !this.impreciseVars.contains(opposite);
                    this.values[opposite.number] = mult;
                    this.valuesCount++;
                } else {
                    // The multiplicity is positive, we know that the opposite
                    // must be positive as well, but we don't know its precise
                    // value for now.
                    if (!this.hasValue(opposite)) {
                        this.impreciseVars.add(opposite);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(this.values);
        }

        @Override
        public Solution clone() {
            Solution result =
                new Solution(this.values.length,
                    (THashSet<Equation>) this.eqsToUse.clone());
            for (int i = 0; i < this.values.length; i++) {
                result.values[i] = this.values[i];
            }
            result.valuesCount = this.valuesCount;
            result.impreciseVars.addAll(this.impreciseVars);
            return result;
        }

        /**
         * Returns an equation from the set of equations to be used that is
         * likely to cause the least branching during the solution search. The
         * method uses the following heuristic:
         * - Equations with a concrete constant are preferred.
         * - Try to pick the equation with the smaller number of open variables.
         */
        Equation getBestBranchingEquation() {
            Equation result = null;
            for (Equation eq : this.eqsToUse) {
                if (!eq.isUpperBound
                    && (result == null
                        || (result.isCollector && !eq.isCollector)
                        || (result.constant.isCollector() && !eq.constant.isCollector()) || (eq.openVarsCount(this) < result.openVarsCount(this)))) {
                    result = eq;
                }
            }
            return result;
        }

        boolean requiresNodePull() {
            boolean result = false;
            for (MultVar var : this.impreciseVars) {
                if (var instanceof EdgeMultVar) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

}
