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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
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
    private final Set<NodeVar> nodeVars;
    private final Set<EdgeVar> edgeVars;
    private final Set<Equation> trivialEqs;
    private final THashSet<Equation> nonTrivialEqs;
    private final Map<ShapeNode,NodeVar> nodeVarMap;
    private final Map<ShapeEdge,EdgeVar> edgeVarMap;
    private int varsCount;

    /** 
     * Constructs the equation system based on the given materialisation.
     * After the constructor finishes, a call to solve() yields all possible
     * resulting materialisations, with the proper shapes.
     */
    public EquationSystem(Materialisation mat) {
        this.mat = mat;
        this.nodeVars = new THashSet<NodeVar>();
        this.edgeVars = new THashSet<EdgeVar>();
        this.trivialEqs = new THashSet<Equation>();
        this.nonTrivialEqs = new THashSet<Equation>();
        this.nodeVarMap = new THashMap<ShapeNode,NodeVar>();
        this.edgeVarMap = new THashMap<ShapeEdge,EdgeVar>();
        this.varsCount = 0;
        this.create();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equation System:\n");
        sb.append("Trivial Equations:\n");
        for (Equation eq : this.trivialEqs) {
            sb.append(eq + "\n");
        }
        sb.append("Non-trivial Equations:\n");
        for (Equation eq : this.nonTrivialEqs) {
            sb.append(eq + "\n");
        }
        return sb.toString();
    }

    /**
     * Creates the equation system.
     * See the comments in the code for more details.
     */
    private void create() {
        Shape shape = this.mat.getShape();

        // Create one node variable for each node involved in the materialisation.
        for (ShapeNode node : this.mat.getAffectedNodes()) {
            NodeVar nodeVar = this.newNodeVar(node);
            // Create one constraint for each node variable.
            Constraint constraint =
                new Constraint(nodeVar, shape.getNodeMult(node));
            this.storeEquation(constraint);
        }

        // Create the edge bundles.
        Set<EdgeBundle> bundles = new THashSet<EdgeBundle>();
        for (ShapeEdge edge : this.mat.getAffectedEdges()) {
            this.addToEdgeBundle(bundles, edge.source(), edge, OUTGOING);
            this.addToEdgeBundle(bundles, edge.target(), edge, INCOMING);
        }

        // Create one edge variable for each edge in the bundle.
        Multiplicity one =
            Multiplicity.getMultiplicity(1, 1, MultKind.EDGE_MULT);
        for (EdgeBundle bundle : bundles) {
            bundle.computeAdditionalEdges();
            Equality equality =
                new Equality(bundle.origEsMult, this.getNodeVar(bundle.node),
                    bundle.edges.size());
            for (ShapeEdge edge : bundle.edges) {
                EdgeVar edgeVar = this.getOrNewEdgeVar(edge);
                equality.addVar(edgeVar);
                // Create one additional equation for the fixed edges.
                if (bundle.direction == OUTGOING && this.mat.isFixed(edge)) {
                    Equality trivialEq = new Equality(one, null, 1);
                    trivialEq.addVar(edgeVar);
                    this.storeEquation(trivialEq);
                }
            }
            this.storeEquation(equality);
        }

    }

    private NodeVar newNodeVar(ShapeNode node) {
        NodeVar nodeVar = new NodeVar(this.varsCount, node);
        this.nodeVars.add(nodeVar);
        this.varsCount++;
        this.nodeVarMap.put(node, nodeVar);
        return nodeVar;
    }

    private NodeVar getNodeVar(ShapeNode node) {
        NodeVar nodeVar = this.nodeVarMap.get(node);
        assert nodeVar != null;
        return nodeVar;
    }

    private EdgeVar newEdgeVar(ShapeEdge edge) {
        EdgeVar edgeVar = new EdgeVar(this.varsCount, edge);
        this.edgeVars.add(edgeVar);
        this.varsCount++;
        this.edgeVarMap.put(edge, edgeVar);
        return edgeVar;
    }

    private EdgeVar getEdgeVar(ShapeEdge edge) {
        EdgeVar edgeVar = this.edgeVarMap.get(edge);
        assert edgeVar != null;
        return edgeVar;
    }

    private EdgeVar getOrNewEdgeVar(ShapeEdge edge) {
        EdgeVar edgeVar = this.edgeVarMap.get(edge);
        if (edgeVar == null) {
            edgeVar = newEdgeVar(edge);
        }
        return edgeVar;
    }

    private void storeEquation(Equation eq) {
        if (eq.isTrivial()) {
            this.trivialEqs.add(eq);
        } else {
            this.nonTrivialEqs.add(eq);
        }
    }

    /**
     * Searches in the given bundle set for a bundle with corresponding node,
     * label and direction. If a proper bundle is not found, a new one is
     * created and added to the set.  
     */
    private void addToEdgeBundle(Set<EdgeBundle> bundles, ShapeNode node,
            ShapeEdge edge, EdgeMultDir direction) {
        EdgeBundle result = null;
        TypeLabel label = edge.label();

        Shape origShape = this.mat.getOriginalShape();
        EdgeSignature origEs =
            this.mat.getShapeMorphism().getEdgeSignature(origShape,
                this.mat.getShape().getEdgeSignature(edge, direction));

        for (EdgeBundle bundle : bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction
                && bundle.origEs.equals(origEs)) {
                result = bundle;
                break;
            }
        }

        if (result == null) {
            Multiplicity origEsMult =
                origShape.getEdgeSigMult(origEs, direction);
            result = new EdgeBundle(origEs, origEsMult, node, label, direction);
            bundles.add(result);
        }

        result.edges.add(edge);
    }

    /**
     * Finds all solutions of this equation system and return all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public void solve(Set<Materialisation> result) {
        // Compute all solutions.
        Solution initialSol =
            new Solution(this.varsCount,
                (THashSet<Equation>) this.nonTrivialEqs.clone());
        // First iterate once over the trivial solutions.
        for (Equation eq : this.trivialEqs) {
            boolean solChanged = eq.computeNewValues(initialSol);
            assert solChanged;
        }
        Set<Solution> finishedSols = new THashSet<Solution>();
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
            result.add(mat);
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
        boolean solutionModified = true;
        while (solutionModified) {
            Set<Equation> currEqsToUse = (Set<Equation>) sol.eqsToUse.clone();
            solutionModified = false;
            for (Equation eq : currEqsToUse) {
                solutionModified = eq.computeNewValues(sol);
            }
        }
        return sol.isComplete();
    }

    /**
     * Adjust the given materialisation object using the given solution.
     */
    private void updateMatFromSolution(Materialisation mat, Solution sol) {
        assert sol.isComplete();
    }

    /**
     * Abstract class to represent all types of multiplicity variables. 
     */
    private static abstract class MultVar {

        /** The unique variable number. */
        final int number;

        /** Basic constructor. */
        MultVar(int number) {
            this.number = number;
        }

        @Override
        public abstract String toString();

        abstract boolean isValueOfRightKind(Multiplicity value);
    }

    private static class NodeVar extends MultVar {

        final ShapeNode node;

        /** Basic constructor. */
        NodeVar(int number, ShapeNode node) {
            super(number);
            this.node = node;
        }

        @Override
        public String toString() {
            return "y" + this.number;
        }

        @Override
        boolean isValueOfRightKind(Multiplicity value) {
            return value.isNodeKind();
        }
    }

    private static class EdgeVar extends MultVar {

        final ShapeEdge edge;

        /** Basic constructor. */
        EdgeVar(int number, ShapeEdge edge) {
            super(number);
            this.edge = edge;
        }

        @Override
        public String toString() {
            return "x" + this.number;
        }

        @Override
        boolean isValueOfRightKind(Multiplicity value) {
            return value.isEqSysKind();
        }
    }

    private static abstract class Equation {

        abstract boolean isTrivial();

        abstract boolean computeNewValues(Solution partialSol);

        abstract void getNewSolutions(Solution partialSol,
                Set<Solution> partialSols, Set<Solution> finishedSols);

        abstract boolean canBranch();

        abstract boolean isCollector();

        abstract int openVarsCount(Solution partialSol);
    }

    private static class Equality extends Equation {

        private final Multiplicity constant;
        private final NodeVar nodeVar;
        private final ArrayList<EdgeVar> edgeVars;

        Equality(Multiplicity constant, NodeVar nodeVar, int edgeVarsCount) {
            this.constant = constant;
            this.nodeVar = nodeVar;
            this.edgeVars = new ArrayList<EdgeVar>(edgeVarsCount);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<EdgeVar> iter = this.edgeVars.iterator();
            while (iter.hasNext()) {
                result.append(iter.next().toString());
                if (iter.hasNext()) {
                    result.append(" + ");
                }
            }
            result.append(" = " + this.constant);
            if (!this.isTrivial()) {
                result.append("*" + this.nodeVar);
            }
            return result.toString();
        }

        @Override
        boolean isTrivial() {
            return this.nodeVar == null;
        }

        @Override
        boolean computeNewValues(Solution partialSol) {
            boolean result;
            if (this.isTrivial()) {
                this.solveTrivial(partialSol);
                result = true;
            } else {
                result = this.tryToSolve(partialSol);
            }
            return result;
        }

        @Override
        void getNewSolutions(Solution partialSol, Set<Solution> partialSols,
                Set<Solution> finishedSols) {

        }

        @Override
        boolean canBranch() {
            return true;
        }

        @Override
        boolean isCollector() {
            return this.constant.isCollector();
        }

        @Override
        int openVarsCount(Solution partialSol) {
            int openVars = this.edgeVars.size();
            for (MultVar var : this.edgeVars) {
                if (partialSol.hasValue(var)) {
                    openVars--;
                }
            }
            return openVars;
        }

        void addVar(EdgeVar edgeVar) {
            assert edgeVar != null;
            this.edgeVars.add(edgeVar);
        }

        void solveTrivial(Solution partialSol) {
            assert this.edgeVars.size() == 1;
            EdgeVar edgeVar = this.edgeVars.iterator().next();
            Multiplicity one =
                Multiplicity.getMultiplicity(1, 1, MultKind.NODE_MULT);
            partialSol.setValue(edgeVar, one.times(this.constant));
        }

        /**
         * Given a partial solution, tries to assign additional values to the
         * variables when possible. (See comments on code for additional
         * information). If no new values can be obtained from this equation
         * the given solution remains unchanged. 
         */
        boolean tryToSolve(Solution partialSol) {
            assert partialSol.eqsToUse.contains(this);
            // EZ says: sorry for the multiple return points, but otherwise
            // the code becomes less readable...

            int openVarsCount = this.openVarsCount(partialSol);
            boolean nodeVarHasValue = partialSol.hasValue(this.nodeVar);
            Multiplicity nodeVarValue = null;
            if (nodeVarHasValue) {
                nodeVarValue = partialSol.getValue(this.nodeVar);
            }

            if (openVarsCount == 1 && nodeVarHasValue) {
                // We have only one variable without a value so we assign the
                // equation constant minus the sum of all other variables values
                // to the open variable.
                Multiplicity newConst = nodeVarValue.times(this.constant);
                for (Multiplicity mult : this.getFixedValues(partialSol,
                    openVarsCount)) {
                    newConst = newConst.sub(mult);
                }
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, newConst);
                }
                partialSol.eqsToUse.remove(this);
                return true;
            }

            return false;
        }

        /**
         * Returns the variables of this equation that have a value
         * in the given solution.
         */
        Multiplicity[] getFixedValues(Solution partialSol, int openVarsCount) {
            Multiplicity result[] =
                new Multiplicity[this.edgeVars.size() - openVarsCount];
            int i = 0;
            for (MultVar var : this.edgeVars) {
                if (partialSol.hasValue(var)) {
                    result[i] = partialSol.getValue(var);
                    i++;
                }
            }
            return result;
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
            for (MultVar var : this.edgeVars) {
                if (!partialSol.hasValue(var)) {
                    result[i] = var;
                    i++;
                }
            }
            assert i == openVarsCount;
            return result;
        }
    }

    private static class Constraint extends Equation {

        private final NodeVar nodeVar;
        private final Multiplicity constant;

        Constraint(NodeVar nodeVar, Multiplicity constant) {
            this.nodeVar = nodeVar;
            this.constant = constant;
        }

        @Override
        public String toString() {
            return this.nodeVar + " <= " + this.constant;
        }

        @Override
        boolean isTrivial() {
            return this.constant.isSingleton();
        }

        @Override
        boolean computeNewValues(Solution partialSol) {
            boolean result;
            if (this.isTrivial()) {
                partialSol.setValue(this.nodeVar, this.constant);
                result = true;
            } else {
                result = false;
            }
            return result;
        }

        @Override
        void getNewSolutions(Solution partialSol, Set<Solution> partialSols,
                Set<Solution> finishedSols) {
            assert false;
        }

        @Override
        boolean canBranch() {
            return false;
        }

        @Override
        boolean isCollector() {
            assert false;
            return false;
        }

        @Override
        int openVarsCount(Solution partialSol) {
            assert false;
            return Integer.MAX_VALUE;
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
        final Multiplicity origEsMult;
        final ShapeNode node;
        final TypeLabel label;
        final EdgeMultDir direction;
        final Set<ShapeEdge> edges;

        /** Basic constructor. */
        EdgeBundle(EdgeSignature origEs, Multiplicity origEsMult,
                ShapeNode node, TypeLabel label, EdgeMultDir direction) {
            this.origEs = origEs;
            this.origEsMult = origEsMult;
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.edges = new THashSet<ShapeEdge>();
        }

        @Override
        public String toString() {
            return this.direction + ":" + this.node + "-" + this.label + "-"
                + this.edges;
        }

        void computeAdditionalEdges() {

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

        Solution(int varsCount, THashSet<Equation> eqsToUse) {
            this.values = new Multiplicity[varsCount];
            this.valuesCount = 0;
            this.eqsToUse = eqsToUse;
        }

        /** Returns true if all values are non-null. */
        boolean isComplete() {
            return this.valuesCount == this.values.length;
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
         */
        void setValue(MultVar var, Multiplicity mult) {
            assert mult != null;
            assert var.isValueOfRightKind(mult);
            if (this.hasValue(var)) {
                assert this.getValue(var).subsumes(mult);
            } else {
                this.valuesCount++;
            }
            this.values[var.number] = mult;
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
                if (eq.canBranch()
                    && (result == null
                        || (result.isCollector() && !eq.isCollector()) || (eq.openVarsCount(this) < result.openVarsCount(this)))) {
                    result = eq;
                }
            }
            return result;
        }
    }

}
