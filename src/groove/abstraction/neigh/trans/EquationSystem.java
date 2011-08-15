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
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.RuleEdge;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Constructs the equation system based on the given materialisation.
     * After the constructor finishes, a call to solve() yields all possible
     * resulting materialisations, with the proper shapes.
     * 
     * Passing a materialisation that has an empty set of possible new edges
     * gives an assertion error.
     */
    public EquationSystem(Materialisation mat) {
        assert !mat.getPossibleNewEdgeSet().isEmpty();
        this.mat = mat;
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
            EdgeBundle outBundle =
                this.getEdgeBundle(bundles, edge.source(), label, OUTGOING);
            outBundle.addVar(outMultVar);
            EdgeBundle inBundle =
                this.getEdgeBundle(bundles, edge.target(), label, INCOMING);
            inBundle.addVar(inMultVar);
        } // END - Create opposition relations and edge bundles.

        // For each edge bundle, create an equality.
        // BEGIN - Create equalities.
        for (EdgeBundle bundle : bundles) {
            EdgeMultDir direction = bundle.direction;
            // Multiplicity of the bundle node.
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            // Counter of bundle edges that were matched by the rule.
            Multiplicity matchedEdgesMult =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            // Edge signature of the bundle.
            EdgeSignature es = null;

            boolean hasUnmatchedEdges = false;
            // For all edges in the bundle.
            // BEGIN - Bundle edges loop.
            Set<ShapeEdge> bundleEdges = this.getEdgesForBundle(bundle);
            for (ShapeEdge edge : bundleEdges) {
                if (!this.hasVariable(edge, direction)) {
                    // There is no variable for the edge. This means that the
                    // edge is already in the shape.

                    // First check if we already have the signature.
                    if (es == null) {
                        // All edges of the bundle that are in the shape have
                        // the same edge signature. Just used the current edge
                        // of the loop for getting the edge signature.
                        // This code is only executed once.
                        es = shape.getEdgeSignature(edge, direction);
                    } // else do nothing, we may already have the signature.

                    // Now, check if the shape edge is an image of a rule edge.
                    Set<RuleEdge> edgesR =
                        this.mat.getMatch().getPreImages(edge);
                    if (edgesR.size() > 0) {
                        assert edgesR.size() == 1;
                        // Yes, we have a rule edge. Increment the counter.
                        matchedEdgesMult = matchedEdgesMult.increment();
                    } else {
                        // No. There is no rule edge. Adjust the flag.
                        hasUnmatchedEdges = true;
                    }
                }
            } // END - Bundle edges loop.

            // Multiplicity of the edge signature.
            Multiplicity esMult;
            if (es == null) {
                // All edges in the bundle are possible edges and there are no
                // fixed edges in the shape. We need to retrieve the edge
                // signature from the original shape so we can get the proper
                // multiplicity.
                // Take any edge from the bundle.
                ShapeEdge bundleEdge = bundleEdges.iterator().next();
                // Find the original edge from the shaping morphism.
                ShapeEdge origEdge =
                    this.mat.getShapeMorphism().getEdge(bundleEdge);
                esMult =
                    this.mat.getOriginalShape().getEdgeMult(origEdge, direction);
            } else {
                esMult = shape.getEdgeSigMult(es, direction);
            }

            // Check if we need to create an extra variable for the signature.
            if (hasUnmatchedEdges) {
                // There are some edges of the shape in the bundle that were
                // not matched by rule edges. Create another multiplicity
                // variable to represent the value of the remainder
                // multiplicity for the edge signature.
                MultVar esVar = this.newEdgeSigMultVar(es, direction);
                bundle.vars.add(esVar);
            }

            // Create the equation.
            Equation eq = this.newEquation();
            // The constant = (nodeMult * esMult) - matchedEdgesMult .
            eq.setConstant(nodeMult.times(esMult).sub(matchedEdgesMult));
            // Add all variables in the bundle to the equation.
            for (MultVar var : bundle.vars) {
                eq.addVar(var);
            }
        } // END - Create equalities.
    }

    /**
     * Creates and returns a new edge multiplicity variable for the given
     * edge and direction.
     * The map from edges to variables is properly adjusted along with the
     * variable set and count. 
     */
    private EdgeMultVar newEdgeMultVar(ShapeEdge edge, EdgeMultDir direction) {
        EdgeMultVar result = new EdgeMultVar(this.varCount, direction, edge);
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
    private EdgeSigMultVar newEdgeSigMultVar(EdgeSignature es,
            EdgeMultDir direction) {
        EdgeSigMultVar result =
            new EdgeSigMultVar(this.varCount, direction, es);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    /**
     * Creates and returns a new equation,
     * while storing it in the equation set.
     */
    private Equation newEquation() {
        Equation result = new Equation();
        this.eqs.add(result);
        return result;
    }

    /**
     * Searches in the given bundle set for a bundle with corresponding node,
     * label and direction. If a proper bundle is not found, a new one is
     * created and added to the set.  
     */
    private EdgeBundle getEdgeBundle(Set<EdgeBundle> bundles, ShapeNode node,
            TypeLabel label, EdgeMultDir direction) {
        EdgeBundle result = null;
        for (EdgeBundle bundle : bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction) {
                result = bundle;
                break;
            }
        }
        if (result == null) {
            result = new EdgeBundle(node, label, direction);
            bundles.add(result);
        }
        return result;
    }

    /**
     * Returns the set of edges that are associated with given bundle. This
     * includes the possible new edges of the materialisation that are 
     * associated with bundle variables and also all edges that are already in
     * the shape and have the same edge signature that is associated with the
     * bundle.
     */
    private Set<ShapeEdge> getEdgesForBundle(EdgeBundle bundle) {
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        // Include the possible edges.
        for (ShapeEdge possibleEdge : this.mat.getPossibleNewEdgeSet()) {
            MultVar var = this.getEdgeVariable(possibleEdge, bundle.direction);
            if (bundle.vars.contains(var)) {
                result.add(possibleEdge);
            }
        }
        for (ShapeEdge edgeS : this.mat.getShape().binaryEdgeSet(bundle.node,
            bundle.direction)) {
            if (edgeS.label().equals(bundle.label)) {
                // EDUARDO: Fix this. This is wrong. If a node is in more than
                // one bundle, all edges are mixed...
                result.add(edgeS);
            }
        }
        return result;
    }

    /**
     * Returns true is the given edge has a variable associated in the given
     * direction. 
     */
    private boolean hasVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeVariable(edge, direction) != null;
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

    /** EDUARDO: Comment this... */
    public Set<Materialisation> solve() {
        // Compute all solutions.
        Set<Solution> solutions = new THashSet<Solution>();
        Solution initialSol =
            new Solution(this.varCount, (THashSet<Equation>) this.eqs.clone());
        Set<Solution> partialSols = new THashSet<Solution>();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Solution sol = partialSols.iterator().next();
            partialSols.remove(sol);
            for (Solution newSol : this.iterateSolution(sol)) {
                if (newSol.isComplete()) {
                    solutions.add(newSol);
                } else {
                    partialSols.add(newSol);
                }
            }
        }
        // Create the return objects.
        Set<Materialisation> result = new THashSet<Materialisation>();
        if (solutions.size() == 1) {
            // Only one solution, no need to clone the materialisation object.
            this.updateMatFromSolution(this.mat, solutions.iterator().next());
            result.add(this.mat);
        } else {
            // OK, we need to clone.
            for (Solution sol : solutions) {
                Materialisation mat = this.mat.clone();
                this.updateMatFromSolution(mat, sol);
                result.add(mat);
            }
        }
        return result;
    }

    private Set<Solution> iterateSolution(Solution sol) {
        // For all equations, try to fix as many variables as possible.
        for (Equation eq : this.eqs) {
            eq.computeNewValues(sol);
        }
        Set<Solution> result;
        Equation branchingEq = sol.getBestBranchingEquation();
        if (branchingEq == null) {
            // We don't need to branch in the solution search.
            result = new THashSet<Solution>();
            result.add(sol);
        } else {
            result = branchingEq.getNewSolutions(sol);
        }
        return result;
    }

    private void updateMatFromSolution(Materialisation mat, Solution sol) {
        assert mat != null;
        assert sol != null;
        assert sol.isComplete();
        Shape shape = mat.getShape();
        // Each variable is associated with either an edge or a signature.
        // Iterate over the variables and modify the shape accordingly.
        for (MultVar var : this.vars) {
            if (var instanceof EdgeMultVar) {
                // We have a variable associated with a possible edge.
                EdgeMultVar edgeMultVar = (EdgeMultVar) var;
                Multiplicity value = sol.getValue(edgeMultVar);
                if (!value.isZero()) {
                    // We have to include the edge in the shape.
                    // But first, check if it's not included, because of an
                    // opposite variable.
                    ShapeEdge edgeS = edgeMultVar.edge;
                    if (!shape.edgeSet().contains(edgeS)) {
                        shape.addEdgeWithoutCheck(edgeS);
                        // Set the multiplicity accordingly.
                        shape.setEdgeMult(edgeS, edgeMultVar.direction, value);
                    }
                }
            } else if (var instanceof EdgeSigMultVar) {
                // We have a variable associated with the remainder of an edge
                // signature multiplicity;
                EdgeSigMultVar edgeSigMultVar = (EdgeSigMultVar) var;
                Multiplicity value = sol.getValue(edgeSigMultVar);
                if (value.isZero()) {
                    // Setting the edge signature multiplicity to zero removes
                    // all associated edges from the shape.
                    shape.setEdgeSigMult(edgeSigMultVar.es,
                        edgeSigMultVar.direction, value);
                }
            }
        }
        // Adjust the shape morphism accordingly.
        mat.getShapeMorphism().removeInvalidEdgeKeys(shape);
        assert mat.isShapeMorphConsistent();
        assert shape.isInvariantOK();
    }

    /**
     * Abstract class to represent all types of multiplicity variables. 
     */
    private static abstract class MultVar {

        /** The unique variable number. */
        final int number;
        /** The direction this variable is associated with. */
        final EdgeMultDir direction;

        /** Basic constructor. */
        MultVar(int number, EdgeMultDir direction) {
            this.number = number;
            this.direction = direction;
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
     * EDUARDO: This is actually not true in the current implementation. Fix?
     */
    private static class EdgeMultVar extends MultVar {

        /** The edge associated with this variable. */
        private final ShapeEdge edge;
        /** The opposite variable. */
        private EdgeMultVar opposite;

        /** Basic constructor. */
        private EdgeMultVar(int number, EdgeMultDir direction, ShapeEdge edge) {
            super(number, direction);
            this.edge = edge;
        }

        /** Variables of this type a prefixed with an 'x'. */
        @Override
        public String toString() {
            return "x" + this.number;
        }
    }

    /**
     * Multiplicity variable associated with an edge signature, representing
     * the remaining value of the signature multiplicity after the
     * materialisation is finished.
     */
    private static class EdgeSigMultVar extends MultVar {

        /** The edge signature associated with this variable. */
        private final EdgeSignature es;

        /** Basic constructor. */
        private EdgeSigMultVar(int number, EdgeMultDir direction,
                EdgeSignature es) {
            super(number, direction);
            this.es = es;
        }

        /** Variables of this type a prefixed with an 'y'. */
        @Override
        public String toString() {
            return "y" + this.number;
        }
    }

    private static class Equation {

        private final Set<MultVar> vars;
        private Multiplicity constant;

        private Equation() {
            this.vars = new THashSet<MultVar>();
        }

        private void setConstant(Multiplicity constant) {
            this.constant = constant;
        }

        private void addVar(MultVar var) {
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
            result.append("=" + this.constant);
            return result.toString();
        }

        private void computeNewValues(Solution partialSol) {
            if (!partialSol.eqsToUse.contains(this)) {
                return;
            }
            int openVarsCount = this.openVarsCount(partialSol);
            if (openVarsCount == 0) {
                // All variables in this equation have a value assigned.
                // Nothing to do.
                return;
            }
            // Check which variables can have a value set.
            if (openVarsCount == 1 || this.constant.isZero()) {
                // There are two cases:
                // 1) We have only one variable without a value so we assign the
                // equation constant to this variable.
                // 2) The equation constant is zero.
                // Since multiplicities can't be negative, this means that all
                // variables in the equation have to be zero.
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, this.constant);

                }
                // This equation is no longer useful for finding the remaining
                // values of this solution.
                partialSol.eqsToUse.remove(this);
            }

        }

        private int openVarsCount(Solution partialSol) {
            int openVars = this.vars.size();
            for (MultVar var : this.vars) {
                if (partialSol.hasValue(var)) {
                    openVars--;
                }
            }
            return openVars;
        }

        private boolean isFullyAssigned(Solution sol) {
            return this.openVarsCount(sol) == 0;
        }

        private MultVar[] getOpenVars(Solution partialSol, int openVarsCount) {
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

        private MultVar[] getOpenVars(Solution partialSol) {
            return this.getOpenVars(partialSol, this.openVarsCount(partialSol));
        }

        private Set<Solution> getNewSolutions(Solution sol) {
            assert !sol.isComplete();
            assert sol.eqsToUse.contains(this);

            Set<Solution> result = new THashSet<Solution>();

            // Array of possible multiplicity values.
            Multiplicity mults[] = Multiplicity.getAllEdgeMultiplicities();
            int multsSize = mults.length;
            // Array of variables that need values.
            MultVar vars[] = this.getOpenVars(sol);
            int varsSize = vars.length;
            // Array of current values for the variables.
            // Hold indexes of positions in the mults array.
            int currVals[] = new int[varsSize];

            // Start iterating.
            int curr = varsSize - 1;
            int prev;
            while (currVals[0] != multsSize) {
                this.testAndStoreValidSolution(sol, vars, currVals, mults,
                    result);

                /*System.out.print("Solution: ");
                int i = 0;
                for (MultVar var : vars) {
                    System.out.print(var + "=" + mults[currVals[i]] + ", ");
                    i++;
                }
                System.out.println();*/

                // Update pointers and compute next solution.
                currVals[curr]++;
                if (currVals[curr] == multsSize) {
                    prev = curr - 1;
                    while (prev >= 0 && currVals[prev]++ == multsSize) {
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
            sol.eqsToUse.remove(this);

            return result;
        }

        private void testAndStoreValidSolution(Solution sol,
                MultVar openVars[], int currVals[], Multiplicity mults[],
                Set<Solution> result) {
            Solution newSol = sol.clone();
            // Store the current values in the new solution.
            for (int i = 0; i < openVars.length; i++) {
                newSol.setValue(openVars[i], mults[currVals[i]]);
            }
            if (this.isValidSolution(newSol)) {
                result.add(newSol);
            }
        }

        private boolean isValidSolution(Solution sol) {
            Multiplicity sum =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            for (MultVar var : this.vars) {
                sum = sum.add(sol.getValue(var));
            }
            return this.constant.equals(sum);
        }
    }

    private static class EdgeBundle {

        private final ShapeNode node;
        private final TypeLabel label;
        private final EdgeMultDir direction;
        private final Set<MultVar> vars;

        private EdgeBundle(ShapeNode node, TypeLabel label,
                EdgeMultDir direction) {
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.vars = new THashSet<MultVar>();
        }

        @Override
        public String toString() {
            return this.direction + ":" + this.node + "-" + this.label + "-"
                + this.vars;
        }

        private void addVar(MultVar var) {
            assert var.direction == this.direction;
            this.vars.add(var);
        }
    }

    private static class Solution {

        private final Multiplicity values[];
        private int valuesCount;
        private THashSet<Equation> eqsToUse;

        private Solution(int varCount, THashSet<Equation> eqsToUse) {
            this.values = new Multiplicity[varCount];
            this.valuesCount = 0;
            this.eqsToUse = eqsToUse;
        }

        private boolean isComplete() {
            return this.valuesCount == this.values.length;
        }

        private boolean hasValue(MultVar var) {
            return this.getValue(var) != null;
        }

        private Multiplicity getValue(MultVar var) {
            return this.values[var.number];
        }

        private void setValue(MultVar var, Multiplicity mult) {
            assert !this.hasValue(var);
            assert mult != null;
            this.values[var.number] = mult;
            this.valuesCount++;
            // Check if the variable has an opposite one. And if yes, also
            // set its value.
            if (var instanceof EdgeMultVar) {
                MultVar opposite = ((EdgeMultVar) var).opposite;
                assert !this.hasValue(opposite);
                this.values[opposite.number] = mult;
                this.valuesCount++;
            }
            // Check if any equation has become fully assigned.
            Iterator<Equation> iter = this.eqsToUse.iterator();
            while (iter.hasNext()) {
                Equation eq = iter.next();
                if (eq.isFullyAssigned(this)) {
                    // Remove the equation.
                    iter.remove();
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
            return result;
        }

        private Equation getBestBranchingEquation() {
            Equation result = null;
            for (Equation eq : this.eqsToUse) {
                if (result == null
                    || (result.constant.isCollector() && !eq.constant.isCollector())
                    || (eq.openVarsCount(this) < result.openVarsCount(this))) {
                    result = eq;
                }
            }
            return result;
        }
    }
}
