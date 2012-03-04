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

import static groove.abstraction.neigh.Multiplicity.OMEGA;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.util.Duo;
import groove.util.Fixable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An equation system is the mechanism used to resolve non-determinism during
 * the materialisation phase.
 * The system is composed of multiplicity variables, that are associated with
 * elements of the shape under materialisation.
 * The variables are used to form equations over multiplicities values. The
 * process of solving the equation system entails finding all possible values
 * for the multiplicity variables that satisfy all equations.
 * When a solution is found the materialisation object is adjusted accordingly.
 * 
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    // Debug variables.
    private static final boolean WARN_BLOWUP = false;
    private static final int MAX_SOLUTION_COUNT = 4;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Creates a new equation system for the given materialisation. */
    public final static EquationSystem newEqSys(Materialisation mat) {
        assert mat.getStage() == 1;
        return new EquationSystem(mat);
    }

    /** Adds the given pair of variables to the given pair of equations. */
    private static void addVars(Duo<Equation> eqs, Duo<BoundVar> vars) {
        eqs.one().addVar(vars.one());
        eqs.two().addVar(vars.two());
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * The initial materialisation object for which the equation system should
     * be built. If the system has only one solution this object is modified
     * directly. If there are multiple solutions, this object is cloned.
     */
    private final Materialisation mat;
    /**
     * Stage of this equation system. Should match the state of the
     * materialisation object.
     */
    private final int stage;
    /**
     * Set of trivial equations (equations with just one variable) that are
     * used first while solving the system.
     */
    private final MyHashSet<Equation> trivialEqs;
    /**
     * Sets of all equations that compose the system. The objects are final but 
     * the sets are modified during the creation of the system. After that they
     * no longer change. 
     */
    private final MyHashSet<Equation> lbEqs;
    private final MyHashSet<Equation> ubEqs;
    /**
     * Range of values for the lower and upper bound variables. The ranges are
     * stored in the equation system and referenced in the solutions.
     */
    private int lbRange[];
    private int ubRange[];
    /** The number of multiplicity variables of the system. */
    private int varsCount;

    /**
     * Maps and reverse maps from shape elements to variables. Each element
     * (the kind of element depends on the stage) is associated with a pair
     * of lower and upper bound variables. The reverse map is an array indexed
     * by the variable numbers.
     */
    // ------------------------------------------------------------------------
    // Used in first stage.
    // ------------------------------------------------------------------------
    private Map<ShapeEdge,Duo<BoundVar>> edgeVarsMap;
    private ArrayList<ShapeEdge> varEdgeMap;
    // ------------------------------------------------------------------------
    // Used in second stage.
    // ------------------------------------------------------------------------
    private Map<EdgeSignature,Duo<BoundVar>> outEsVarsMap;
    private Map<EdgeSignature,Duo<BoundVar>> inEsVarsMap;
    private ArrayList<EdgeSignature> varEsMap;
    // ------------------------------------------------------------------------
    // Used in third stage.
    // ------------------------------------------------------------------------
    private Map<ShapeNode,Duo<BoundVar>> nodeVarsMap;
    private ArrayList<ShapeNode> varNodeMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor to avoid object creation.
     * Use {@link #newEqSys(Materialisation)}.
     */
    private EquationSystem(Materialisation mat) {
        assert mat != null;
        this.mat = mat;
        this.stage = mat.getStage();
        this.trivialEqs = new MyHashSet<Equation>();
        this.lbEqs = new MyHashSet<Equation>();
        this.ubEqs = new MyHashSet<Equation>();
        this.varsCount = 0;
        this.create();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equation System - Stage " + this.stage + " - "
            + this.varsCount + " variables\n");
        sb.append("Trivial Equations:\n");
        for (Equation eq : this.trivialEqs) {
            sb.append(eq + "\n");
        }
        sb.append("Non-trivial Equations:\n");
        for (Equation eq : this.lbEqs) {
            sb.append(eq + "\n");
        }
        for (Equation eq : this.ubEqs) {
            sb.append(eq + "\n");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Common methods to all stages.
    // ------------------------------------------------------------------------

    /**
     * Creates the equation system. Calls the appropriated creation method
     * depending on the stage.
     */
    private void create() {
        this.fillBoundRanges();
        switch (this.stage) {
        case 1:
            this.createFirstStage();
            break;
        case 2:
            this.createSecondStage();
            break;
        case 3:
            this.createThirdStage();
            break;
        default:
            assert false;
        }
    }

    /** Fills the range fields accordingly to stage. */
    private void fillBoundRanges() {
        MultKind kind = null;
        switch (this.stage) {
        case 1:
            kind = MultKind.EQSYS_MULT;
            break;
        case 2:
            kind = MultKind.EDGE_MULT;
            break;
        case 3:
            kind = MultKind.NODE_MULT;
            break;
        default:
            assert false;
        }
        int b = Multiplicity.getBound(kind);
        this.lbRange = new int[b + 2];
        this.ubRange = new int[b + 2];
        for (int i = 0; i <= b + 1; i++) {
            this.lbRange[i] = i;
            if (i == b + 1) {
                this.ubRange[i] = OMEGA;
            } else {
                this.ubRange[i] = i;
            }
        }
    }

    /**
     * Returns the kind of multiplicity that is to be used when updating
     * the materialisation from a solution.
     */
    private MultKind finalMultKind() {
        MultKind kind = null;
        switch (this.stage) {
        case 1:
        case 2:
            kind = MultKind.EDGE_MULT;
            break;
        case 3:
            kind = MultKind.NODE_MULT;
            break;
        default:
            assert false;
        }
        return kind;
    }

    /**
     * Stores the given pair of equations in the appropriate sets. If the
     * equations have no variables or are not useful, they are discarded. An
     * equation is not useful, for example, if its constant is at the extremity
     * of the valid bound range.
     */
    private void storeEquations(Duo<Equation> eqs) {
        Equation lbEq = eqs.one();
        Equation ubEq = eqs.two();
        assert lbEq.vars.size() == ubEq.vars.size();
        if (lbEq.vars.size() == 0) {
            // Empty equations. Nothing to do.
            return;
        }
        // Fill the duality relation first, before we store the equations.
        lbEq.setDual(ubEq);
        ubEq.setDual(lbEq);
        lbEq.setFixed();
        ubEq.setFixed();
        // Now store.
        if (lbEq.isUseful()) {
            if (lbEq.isTrivial()) {
                this.trivialEqs.add(lbEq);
            } else {
                this.lbEqs.add(lbEq);
            }
        }
        if (ubEq.isUseful()) {
            if (ubEq.isTrivial()) {
                this.trivialEqs.add(ubEq);
            } else {
                this.ubEqs.add(ubEq);
            }
        }
    }

    /**
     * Finds all solutions of this equation system and return all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public void solve(Set<Materialisation> result) {
        // Compute all solutions.
        Solution initialSol =
            new Solution(this.varsCount, this.lbRange, this.ubRange,
                this.lbEqs, this.ubEqs);
        // First iterate once over the trivial solutions.
        for (Equation eq : this.trivialEqs) {
            eq.nonRecComputeNewValues(initialSol);
        }
        SolutionSet finishedSols = new SolutionSet();
        SolutionSet partialSols = new SolutionSet();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Solution sol = partialSols.iterator().next();
            partialSols.remove(sol);
            this.iterateSolution(sol, partialSols, finishedSols);
        }
        int finishedSolsSize = finishedSols.size();
        assert this.stage == 2 ? finishedSolsSize == 1 : true;
        if (WARN_BLOWUP && finishedSolsSize > MAX_SOLUTION_COUNT) {
            System.out.println("Warning! Blowup while solving equation system: "
                + finishedSolsSize + " solutions.");
            System.out.println(this);
            System.out.println(this.mat);
        }
        // Create the return objects.
        for (Solution sol : finishedSols) {
            assert this.isValid(sol);
            Materialisation mat;
            if (finishedSolsSize == 1) {
                mat = this.mat;
            } else {
                mat = this.mat.clone();
            }
            boolean requiresNextStage = this.updateMat(mat, sol);
            if (requiresNextStage) {
                assert this.stage < 3;
                new EquationSystem(mat).solve(result);
            } else {
                result.add(mat);
            }
        }
    }

    /**
     * Tries to add the given solution to the set of finished solutions.
     * @return true if the solution was valid and it was added, false otherwise. 
     */
    private boolean addFinishedSolution(Solution sol, Set<Solution> finishedSols) {
        boolean added = false;
        if (this.isValid(sol)) {
            finishedSols.add(sol);
            added = true;
        }
        return added;
    }

    /**
     * Checks if we can set the values of open variables of the given solution to
     * their maximum. This is useful in the first stage, when we don't have to
     * solve the equation system completely and we just want to know if the
     * variables are zero or positive. This is important to avoid unnecessary
     * branching in the first stage.
     */
    private boolean canMaxSolution(Solution sol) {
        return this.stage == 1
            && (sol.ubEqs.isEmpty() || sol.allEqsHaveNodes(sol.ubEqs))
            && !sol.hasZeroOneVars();
    }

    /**
     * Set the values of the open variables of the given solution to
     * their maximum. This is useful in the first stage, when we don't have to
     * solve the equation system completely and we just want to know if the
     * variables are zero or positive. This is important to avoid unnecessary
     * branching in the first stage.
     * 
     * @return a new solution object with maximized variables. 
     */
    private Solution maxSolution(Solution sol) {
        Solution result = sol.clone();
        result.setAllVarsToMax(this.edgeVarsMap.values());
        return result;
    }

    /** 
     * Checks if we can branch on the variables with zero or one values.
     * Used only for the first stage.
     * @return true if we can branch, false otherwise.
     */
    private boolean canBranchOnZeroOneValues(Solution sol) {
        return this.stage == 1
            && (sol.ubEqs.isEmpty() || sol.allEqsHaveNodes(sol.ubEqs))
            && sol.hasZeroOneVars();
    }

    /** 
     * Branches on the variables with zero or one values. After the new solutions
     * are created they are max'ed out and stored as finished.
     * Used only for the first stage.
     */
    private void branchOnZeroOneValues(Solution sol, SolutionSet finishedSols) {
        assert this.stage == 1;
        Collection<Duo<BoundVar>> allVars = this.edgeVarsMap.values();
        ArrayList<BoundVar> zeroOneVars = sol.getZeroOneVars(allVars);
        // Iterate the solutions.
        ZeroOneBranchIterator iter =
            new ZeroOneBranchIterator(sol, zeroOneVars);
        while (iter.hasNext()) {
            Solution newSol = iter.next();
            newSol.setAllVarsToMax(allVars);
            this.addFinishedSolution(newSol, finishedSols);
        }
    }

    /**
     * Iterates the given solution over all equations of the system and tries to
     * fix more variables. This may lead to branching. In this case, an
     * heuristic is used to decide on which equation to use that is likely
     * to produce less branching. Returns the set of new (partial) solutions.
     */
    private void iterateSolution(Solution sol, SolutionSet partialSols,
            SolutionSet finishedSols) {
        this.iterateEquations(sol);
        if (sol.isFinished()) {
            this.addFinishedSolution(sol, finishedSols);
        } else {
            // Check if we're in first stage and we can stop early.
            if (this.canMaxSolution(sol)) {
                // Yes, we can stop. Store the max'ed solution.
                this.addFinishedSolution(this.maxSolution(sol), finishedSols);
            } else
            // No escape, we have to branch.
            if (this.canBranchOnZeroOneValues(sol)) {
                // Special case for first stage where we can branch on variables
                // with possible zero or one values, and then max out the
                // remainder open variables.
                this.branchOnZeroOneValues(sol, finishedSols);
            } else {
                // Default case for branching, pick an equation and branch.
                Equation branchingEq = sol.getBestBranchingEquation();
                branchingEq.getNewSolutions(sol, partialSols, finishedSols);
            }
        }
    }

    /**
     * Iterate the given solution over all equations of the system and try to
     * fix more variables. 
     */
    private void iterateEquations(Solution sol) {
        // For all equations, try to fix as many variables as possible.
        boolean solutionModified = true;
        boolean removeEq;
        while (solutionModified) {
            solutionModified = false;
            for (BoundType type : BoundType.values()) {
                MyHashSet<Equation> copyEqs = sol.getEqs(type).clone();
                for (Equation eq : copyEqs) {
                    removeEq = eq.computeNewValues(sol);
                    if (removeEq) {
                        sol.getEqs(type).remove(eq);
                    }
                    solutionModified = solutionModified || removeEq;
                }
            }
        }
    }

    /**
     * Updates the given materialisation object using the given solution. Calls
     * the appropriate update method according to the stage.
     * 
     * @return true if a next stage is necessary, false otherwise.
     */
    private boolean updateMat(Materialisation mat, Solution sol) {
        boolean result = false;
        switch (this.stage) {
        case 1:
            result = this.updateMatFirstStage(mat, sol);
            break;
        case 2:
            result = this.updateMatSecondStage(mat, sol);
            break;
        case 3:
            result = this.updateMatThirdStage(mat, sol);
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Creates and returns a pair of equations with the given constants. */
    private Duo<Equation> createEquations(int varsCount, int lbConst,
            int ubConst) {
        return createEquations(varsCount, lbConst, ubConst, null);
    }

    /**
     * Creates and returns a pair of equations with the given constants and
     * a reference to a collector node. The node is used only in the
     * first stage.
     */
    private Duo<Equation> createEquations(int varsCount, int lbConst,
            int ubConst, ShapeNode node) {
        Equation lbEq = new Equation(BoundType.LB, varsCount, lbConst, node);
        Equation ubEq = new Equation(BoundType.UB, varsCount, ubConst, node);
        return new Duo<Equation>(lbEq, ubEq);
    }

    /** Updates the constants in the given equations to the given values. */
    private void updateConstant(Duo<Equation> eqs, int lbConst, int ubConst) {
        Equation lbEq = eqs.one();
        Equation ubEq = eqs.two();
        lbEq.constant = lbConst;
        ubEq.constant = ubConst;
    }

    /** Checks if the given solution satisfies all equations of the system. */
    private boolean isValid(Solution sol) {
        if (sol.invalid) {
            return false;
        }
        boolean result = true;
        Set<Equation> allEqs = new MyHashSet<Equation>();
        allEqs.addAll(this.trivialEqs);
        allEqs.addAll(this.lbEqs);
        allEqs.addAll(this.ubEqs);
        for (Equation eq : allEqs) {
            // Don't use eq.isValidSolution(sol) because this checks for the
            // dual as well. This means extra unnecessary work.
            if (!eq.isSatisfied(sol)) {
                result = false;
                break;
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Methods for first stage.
    // ------------------------------------------------------------------------

    /**
     * Creates a first stage equation system. In this stage, each edge bundle
     * gives rise to an equation and each edge of the bundle to a variable.
     */
    private void createFirstStage() {
        assert this.stage == 1;

        this.edgeVarsMap = new MyHashMap<ShapeEdge,Duo<BoundVar>>();
        this.varEdgeMap = new ArrayList<ShapeEdge>();
        boolean mayHaveGarbageNodes =
            Parameters.getNodeMultBound() < Parameters.getEdgeMultBound();
        Shape shape = this.mat.getShape();

        // General case:
        // For each bundle...
        for (EdgeBundle bundle : this.mat.getBundles()) {
            int varsCount = bundle.getEdgesCount();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // ... create a pair of equations.
            Duo<Equation> eqs;
            if (mayHaveGarbageNodes && nodeMult.isZeroPlus()) {
                eqs =
                    this.createEquations(varsCount, constMult.getLowerBound(),
                        constMult.getUpperBound(), bundle.node);
            } else {
                eqs =
                    this.createEquations(varsCount, constMult.getLowerBound(),
                        constMult.getUpperBound());
            }
            // For each split edge signature...
            for (EdgeSignature splitEs : bundle.getSplitEsSet()) {
                // ...for each edge...
                for (ShapeEdge edge : bundle.getSplitEsEdges(splitEs)) {
                    // ... create two bound variables.
                    Duo<BoundVar> vars = retrieveBoundVars(edge);
                    addVars(eqs, vars);
                    // Create additional trivial equations.
                    Duo<Equation> trivialEqs = null;
                    if (this.mat.isFixed(edge)) {
                        // Optimization 1:
                        // Create additional equations for the fixed edges.
                        trivialEqs = this.createEquations(1, 1, 1);
                    } else if (shape.areNodesConcrete(edge)) {
                        // Optimization 2:
                        // Create additional equations for edges with concrete nodes.
                        trivialEqs = this.createEquations(1, 0, 1);
                    }
                    if (trivialEqs != null) {
                        addVars(trivialEqs, vars);
                        this.storeEquations(trivialEqs);
                    }
                }
            }
            this.storeEquations(eqs);
        }
    }

    /**
     * Returns a pair of variables associated with the given edge. If no
     * variable pair is found, a new one is created and associated with the
     * edge. 
     */
    private Duo<BoundVar> retrieveBoundVars(ShapeEdge edge) {
        assert this.stage == 1;
        Duo<BoundVar> vars = this.edgeVarsMap.get(edge);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.edgeVarsMap.put(edge, vars);
            this.varEdgeMap.add(this.varsCount, edge);
            this.varsCount++;
        }
        return vars;
    }

    /**
     * Updates the given materialisation object with the given solution.
     * Always returns true since we always need to go to second stage to
     * compute the multiplicities for each edge signature. 
     */
    private boolean updateMatFirstStage(Materialisation mat, Solution sol) {
        assert this.stage == 1;

        Shape shape = mat.getShape();
        MultKind kind = this.finalMultKind();

        // First, get the zero and positive edges from the solution.
        Set<ShapeEdge> zeroEdges = new MyHashSet<ShapeEdge>();
        Set<ShapeEdge> positiveEdges = new MyHashSet<ShapeEdge>();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            ShapeEdge edge = this.varEdgeMap.get(i);
            if (mult.isZero()) {
                zeroEdges.add(edge);
            } else {
                positiveEdges.add(edge);
            }
        }

        // Remove all zero edges from the shape.
        ShapeMorphism morph = mat.getShapeMorphism();
        for (ShapeEdge zeroEdge : zeroEdges) {
            if (shape.containsEdge(zeroEdge)) {
                shape.removeEdge(zeroEdge);
            }
            morph.removeEdge(zeroEdge);
        }

        // Remove nodes that cannot exist.
        this.collectGarbageNodes(mat, sol);

        // Update the bundles and check for non-singular ones.
        Set<EdgeBundle> nonSingBundles = new MyHashSet<EdgeBundle>();
        Set<ShapeEdge> nonSingEdges = new MyHashSet<ShapeEdge>();
        for (EdgeBundle bundle : mat.getBundles()) {
            bundle.updateFromSolution(shape, zeroEdges, positiveEdges);
            if (bundle.isNonSingular()
                && shape.getNodeMult(bundle.node).isCollector()) {
                nonSingBundles.add(bundle);
                nonSingEdges.addAll(bundle.possibleEdges);
            }
        }

        // Add the positive edges to the shape.
        for (ShapeEdge positiveEdge : positiveEdges) {
            if (!shape.containsEdge(positiveEdge)
                && !nonSingEdges.contains(positiveEdge)) {
                // The multiplicity is positive and this edge won't be
                // duplicated in the next stage. We have to add the edge to
                // the shape here.
                shape.addEdge(positiveEdge);
            }
        }

        mat.moveToSecondStage(nonSingBundles);

        return true;
    }

    /**
     * Goes over the collector nodes marked as garbage in the first stage and
     * removes them from the materialisation. Note that the nodes are not
     * removed from the shape at this point. This is done later at the end
     * of second stage. This avoids unnecessary updates on the bundles. 
     */
    // See materialisation test case 11.
    private void collectGarbageNodes(Materialisation mat, Solution sol) {
        assert this.stage == 1;
        if (sol.garbageNodes == null) {
            return;
        }
        Shape shape = mat.getShape();
        for (ShapeNode node : sol.garbageNodes) {
            assert shape.getNodeMult(node).isZeroPlus();
            assert shape.isUnconnected(node);
            mat.removeUnconnectedNode(node);
        }
    }

    // ------------------------------------------------------------------------
    // Methods for second stage.
    // ------------------------------------------------------------------------

    /**
     * Creates a second stage equation system. In this stage, each edge bundle
     * gives rise to an equation and each edge signature of the bundle is
     * associated with a pair of variables.
     * The equation system created by this method is deterministic, i.e., the
     * second stage always produces a single solution.
     */
    private void createSecondStage() {
        assert this.stage == 2;

        this.outEsVarsMap = new MyHashMap<EdgeSignature,Duo<BoundVar>>();
        this.inEsVarsMap = new MyHashMap<EdgeSignature,Duo<BoundVar>>();
        this.varEsMap = new ArrayList<EdgeSignature>();
        Shape shape = this.mat.getShape();

        // General case:
        // For each affected node...
        for (ShapeNode affectedNode : this.mat.getAffectedNodes()) {
            // For each split bundle...
            for (EdgeBundle bundle : this.mat.getBundles(affectedNode)) {
                // ... create one pair of equations.
                int esCount = bundle.getSplitEsSet().size();
                Duo<Equation> eqs =
                    this.createEquations(esCount,
                        bundle.origEsMult.getLowerBound(),
                        bundle.origEsMult.getUpperBound());
                // For each edge signature...
                for (EdgeSignature es : bundle.getSplitEsSet()) {
                    // ... create a pair of variables.
                    Duo<BoundVar> vars = retrieveBoundVars(es);
                    addVars(eqs, vars);
                    Set<ShapeEdge> edges = bundle.getSplitEsEdges(es);
                    if (edges.size() == 1) {
                        // Special case. Fixed edge signatures.
                        ShapeEdge edge = edges.iterator().next();
                        if (this.mat.isFixed(edge)
                            || bundle.isFixed(edge, bundle.direction, shape)) {
                            Duo<Equation> trivialEqs =
                                this.createEquations(1, 1, 1);
                            addVars(trivialEqs, vars);
                            this.storeEquations(trivialEqs);
                        }
                    }
                }
                this.storeEquations(eqs);
            }
        }
    }

    /** Returns the signature map for the given direction. */
    private Map<EdgeSignature,Duo<BoundVar>> getEsMap(EdgeMultDir direction) {
        assert this.stage == 2;
        Map<EdgeSignature,Duo<BoundVar>> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outEsVarsMap;
            break;
        case INCOMING:
            result = this.inEsVarsMap;
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Returns a pair of variables associated with the given signature. If no
     * variable pair is found, a new one is created and associated with the
     * edge signature. 
     */
    private Duo<BoundVar> retrieveBoundVars(EdgeSignature es) {
        assert this.stage == 2;
        EdgeMultDir direction = es.getDirection();
        Duo<BoundVar> vars = this.getEsMap(direction).get(es);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.getEsMap(direction).put(es, vars);
            this.varEsMap.add(this.varsCount, es);
            this.varsCount++;
        }
        return vars;
    }

    /**
     * Updates the given materialisation object with the given solution.
     * Returns true if we had node splits in the second stage, meaning that we
     * have to compute the multiplicities for the split nodes.
     */
    private boolean updateMatSecondStage(Materialisation mat, Solution sol) {
        assert this.stage == 2;
        Shape shape = mat.getShape();
        MultKind kind = this.finalMultKind();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            assert mult.isSingleton() || mult.isCollector();
            EdgeSignature es = this.varEsMap.get(i);
            shape.setEdgeSigMult(es, mult);
        }
        mat.recursiveGarbageCollectNodes();
        if (mat.requiresThirdStage()) {
            mat.moveToThirdStage();
            return true;
        } else {
            return false;
        }
    }

    // ------------------------------------------------------------------------
    // Methods for third stage.
    // ------------------------------------------------------------------------

    /**
     * Creates a third stage equation system. In this stage, each node that was
     * split gives rise to an equation and each new split node corresponds to
     * a variable.
     */
    private void createThirdStage() {
        assert this.stage == 3;

        this.nodeVarsMap = new MyHashMap<ShapeNode,Duo<BoundVar>>();
        this.varNodeMap = new ArrayList<ShapeNode>();

        Shape shape = this.mat.getShape();
        Map<ShapeNode,Set<ShapeNode>> nodeSplitMap = this.mat.getNodeSplitMap();

        // General case:
        // For each split node...
        for (ShapeNode origNode : nodeSplitMap.keySet()) {
            Multiplicity origMult = this.mat.getOrigNodeMult(origNode);
            Set<ShapeNode> splitNodes = nodeSplitMap.get(origNode);
            int varsCount = splitNodes.size() + 1;
            // ... create one pair of equations.
            Duo<Equation> eqs =
                this.createEquations(varsCount, origMult.getLowerBound(),
                    origMult.getUpperBound());
            // ... create one pair of variables for the original node.
            Duo<BoundVar> vars;
            if (shape.containsNode(origNode)) {
                vars = retrieveBoundVars(origNode);
                addVars(eqs, vars);
            }
            for (ShapeNode splitNode : splitNodes) {
                // ... create one pair of variables for each of the split nodes.
                if (shape.containsNode(splitNode)) {
                    vars = retrieveBoundVars(splitNode);
                    addVars(eqs, vars);
                }
            }
            this.storeEquations(eqs);
        }

        // Optimization 1:
        // Sum of opposite nodes for concrete nodes.
        nodeLoop: for (ShapeNode node : shape.nodeSet()) {
            if (!shape.getNodeMult(node).isOne()) {
                // This node is a collector or it was split. Nothing to do.
                continue nodeLoop;
            } // else: the node is concrete.
            for (EdgeBundle bundle : this.mat.getBundles(node)) {
                bundle.update(this.mat);
                EdgeMultDir direction = bundle.direction;
                sigLoop: for (EdgeSignature splitEs : bundle.getSplitEsSet()) {
                    Set<ShapeEdge> sigEdges = bundle.getSplitEsEdges(splitEs);
                    if (!bundle.possibleEdges.containsAll(sigEdges)) {
                        // We don't have a signature with possible edges.
                        // Nothing to do.
                        continue sigLoop;
                    } // else: all edges of the signature are possible edges.
                    Multiplicity esMult = shape.getEdgeSigMult(splitEs);
                    // If the edge bound is larger than the node bound then
                    // the signature multiplicity is more precise and cannot
                    // be used directly.
                    Multiplicity constMult = esMult.toNodeKind();
                    // Create a new equation.
                    Duo<Equation> eqs =
                        this.createEquations(sigEdges.size(), 0, 0);
                    // Go over the edges of the signature and check if the
                    // opposite nodes have variables.
                    edgeLoop: for (ShapeEdge edge : sigEdges) {
                        EdgeSignature oppEs =
                            shape.getEdgeSignature(edge, direction.reverse());
                        if (!shape.isEdgeSigConcrete(oppEs)) {
                            // We have an opposite edge signature that is not
                            // concrete. This means that we can't create a
                            // proper equation for this case. Abort.
                            break edgeLoop;
                        }
                        ShapeNode opposite = direction.opposite(edge);
                        Duo<BoundVar> vars = this.nodeVarsMap.get(opposite);
                        if (vars == null) {
                            // The opposite node has no variable, so its
                            // multiplicity is already known. Subtract this
                            // value from the equation constant.
                            Multiplicity oppMult = shape.getNodeMult(opposite);
                            constMult = constMult.sub(oppMult);
                        } else { // vars != null.
                            addVars(eqs, vars);
                            // Check for a special case.
                            if (esMult.getUpperBound() == sigEdges.size()) {
                                // This case implies that all opposite nodes must
                                // be concrete.
                                Duo<Equation> trivialEqs =
                                    this.createEquations(1, 1, 1);
                                addVars(trivialEqs, vars);
                                this.storeEquations(trivialEqs);
                            }
                        }
                    }
                    this.updateConstant(eqs, constMult.getLowerBound(),
                        constMult.getUpperBound());
                    this.storeEquations(eqs);
                }
            }
        }

    }

    /**
     * Returns a pair of variables associated with the given node. If no
     * variable pair is found, a new one is created and associated with the
     * node. 
     */
    private Duo<BoundVar> retrieveBoundVars(ShapeNode node) {
        assert this.stage == 3;
        Duo<BoundVar> vars = this.nodeVarsMap.get(node);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.nodeVarsMap.put(node, vars);
            this.varNodeMap.add(this.varsCount, node);
            this.varsCount++;
        }
        return vars;
    }

    /**
     * Updates the given materialisation object with the given solution.
     * Always returns false, since this is the last stage.
     */
    private boolean updateMatThirdStage(Materialisation mat, Solution sol) {
        assert this.stage == 3;
        Shape shape = mat.getShape();
        MultKind kind = this.finalMultKind();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            shape.setNodeMult(this.varNodeMap.get(i), mult);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ---------
    // BoundType
    // ---------

    /** Types of equations and variables. */
    private enum BoundType {
        UB, LB
    }

    // --------
    // BoundVar
    // --------

    /**
     * Multiplicity variable class. Each variable has an unique pair of 
     * number and type (upper or lower bound). Variables can only be present
     * in equations with compatible types. The variables themselves have no
     * further information (i.e., they don't have a current value). This allows
     * for a static representation of the equation system.  
     */
    private static final class BoundVar {

        /** Natural number that identifies this variable. */
        final int number;
        /** Upper or lower bound type. */
        final BoundType type;

        /** Basic constructor. */
        BoundVar(int number, BoundType type) {
            this.number = number;
            this.type = type;
        }

        @Override
        public String toString() {
            String prefix = "";
            switch (this.type) {
            case LB:
                prefix = "x_";
                break;
            case UB:
                prefix = "x^";
                break;
            default:
                assert false;
            }
            return prefix + this.number;
        }
    }

    // --------
    // Equation
    // --------

    /**
     * A class to represent equations of the system. An equation has a type:
     * upper or lower bound; a list of variables (type compatible with the
     * equation) and a constant (c).
     * 
     * The relation (inequality) used in the equation depends on its type:
     * - Lower bound equations are taken as: x_0 + x_1 + ... + x_n >= c
     * - Upper bound equations are taken as: x^0 + x^1 + ... + x^n <= c
     *   
     * An equation may have a dual, which is a counter-part with the opposite
     * type and same variables. If a dual equation is not useful for solving the
     * system (for example, a lower bound equation with constant zero is not
     * useful), then the dual field is null.
     */
    private final class Equation implements Fixable {

        /** Type of this equation. */
        final BoundType type;
        /** List of variables (type compatible with the equation). */
        final ArrayList<BoundVar> vars;
        /** Constant value for this equation. */
        int constant;
        /**
         * Special case: reference to a node that may become garbage in the
         * first stage, and thus affects the solution of the system.
         */
        final ShapeNode node;
        /** Reference to a dual equation (may be null). */
        Equation dual;
        /**
         * The hash code of this equation. Once computed it cannot be 0.
         * Once it's different than 0, the equation is fixed and no
         * elements can be added or removed. This avoids nasty hashing problems.
         */
        private int hashCode;

        /** Basic constructor. */
        Equation(BoundType type, int varsCount, int constant, ShapeNode node) {
            this.type = type;
            this.vars = new ArrayList<BoundVar>(varsCount);
            this.constant = constant;
            this.node = node;
            this.hashCode = 0;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<BoundVar> iter = this.vars.iterator();
            while (iter.hasNext()) {
                result.append(iter.next().toString());
                if (iter.hasNext()) {
                    result.append(" + ");
                }
            }
            switch (this.type) {
            case UB:
                result.append(" <= ");
                break;
            case LB:
                result.append(" >= ");
                break;
            default:
                assert false;
            }
            if (this.constant == OMEGA) {
                result.append("w");
            } else {
                result.append(this.constant);
            }
            if (this.hasNode()) {
                result.append(" (" + this.getNode() + ")");
            }
            return result.toString();
        }

        /** The hash code is computed by {@link #computeHashCode()}. */
        @Override
        final public int hashCode() {
            // Lazy computation because the equation may not have been populated yet.
            if (this.hashCode == 0) {
                this.hashCode = this.computeHashCode();
                if (this.hashCode == 0) {
                    this.hashCode = -1;
                }
            }
            return this.hashCode;
        }

        private int computeHashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.constant;
            result = prime * result + this.type.hashCode();
            for (BoundVar var : this.vars) {
                // We can't multiply the result by prime here because this would
                // make the hash dependent on the ordering of elements.
                result += var.number;
            }
            // Multiply here. This probably least to a worst hash function, but
            // nothing to do...
            return result * prime;
        }

        @Override
        public boolean equals(Object o) {
            boolean result;
            if (!(o instanceof Equation)) {
                result = false;
            } else {
                Equation eq = (Equation) o;
                result =
                    this.constant == eq.constant && this.type == eq.type
                        && this.vars.containsAll(eq.vars)
                        && eq.vars.containsAll(this.vars);
            }
            return result;
        }

        @Override
        public void setFixed() {
            this.hashCode();
        }

        @Override
        public boolean isFixed() {
            return this.hashCode != 0;
        }

        @Override
        public void testFixed(boolean fixed) {
            if (this.isFixed() != fixed) {
                throw new IllegalStateException();
            }
        }

        /** Returns true if this equation has a special node reference. */
        boolean hasNode() {
            return this.node != null;
        }

        /** Returns the node reference of this equation. */
        ShapeNode getNode() {
            assert this.hasNode();
            return this.node;
        }

        /**
         * Returns true if this equation has one variable and no node reference.
         */
        boolean isTrivial() {
            return this.vars.size() == 1 && !this.hasNode();
        }

        /**
         * Adds the given variable to this equation. Fails in an assertion if
         * the variable type is not equal to the equation type. 
         */
        void addVar(BoundVar var) {
            assert !this.isFixed();
            assert var.type == this.type;
            this.vars.add(var);
        }

        /**
         * Sets the dual of this equation to the parameter passed, if it is
         * an useful equation.
         */
        void setDual(Equation dual) {
            assert (this.type == BoundType.LB && dual.type == BoundType.UB)
                || (this.type == BoundType.UB && dual.type == BoundType.LB);
            if (dual.isUseful()) {
                this.dual = dual;
            }
        }

        /** Basic inspection method. */
        boolean hasDual() {
            return this.dual != null;
        }

        /**
         * Checks if this equation is useful and thus should be added to
         * the equation system.
         */
        boolean isUseful() {
            boolean result = false;
            switch (this.type) {
            case UB:
                result =
                    this.hasNode() || this.constant < this.getMaxRangeValue();
                break;
            case LB:
                result =
                    this.hasNode() || this.constant > this.getMinRangeValue();
                break;
            default:
                assert false;
            }
            return result;
        }

        /**
         * Returns an array of possible values for the variables in the
         * equation.
         */
        int[] getBoundRange() {
            int result[] = null;
            switch (this.type) {
            case LB:
                result = EquationSystem.this.lbRange;
                break;
            case UB:
                result = EquationSystem.this.ubRange;
                break;
            default:
                assert false;
            }
            return result;
        }

        /** Returns the maximum value for the variables in the equation. */
        int getMaxRangeValue() {
            int boundRange[] = this.getBoundRange();
            return boundRange[boundRange.length - 1];
        }

        /** Returns the minimum value for the variables in the equation. */
        int getMinRangeValue() {
            return this.getBoundRange()[0];
        }

        /**
         * Computes new values for the variables in this equation based on the
         * given solution. Updates these new values, thus modifying the
         * solution. If the equation has a dual, the solution is also updated
         * with new values from the dual equation.
         * Returns true if this equation can no longer contribute in solving
         * the system and should be removed from the list of equations of the
         * solution.
         */
        boolean computeNewValues(Solution sol) {
            boolean result = this.nonRecComputeNewValues(sol);
            if (result && this.hasDual()) {
                this.dual.nonRecComputeNewValues(sol);
            }
            return result;
        }

        /**
         * Main method to computes new values for the variables in this 
         * equation based on the given solution. This method does not consider
         * the dual equation.
         * 
         * Returns true if this equation can no longer contribute in solving
         * the system and should be removed from the list of equations of the
         * solution.
         * If the solution cannot be improved at the moment, the method returns
         * false, and so this equation will be considered again in future
         * iterations.
         * 
         * We split the equation variables in two types:
         * - Fixed: variables that have a singleton interval in the given
         *          solution. Their value cannot change.
         * - Open: variables that have a non-singleton interval in the given
         *         solution. We try to improve the solution by making these
         *         intervals smaller.
         */
        boolean nonRecComputeNewValues(Solution sol) {
            // EZ says: sorry for the multiple return points, but otherwise
            // the code becomes less readable...

            int fixedVarsSum = this.getFixedVarsSum(sol);

            if (!this.hasOpenVars(sol)) {
                if (this.hasNode() && fixedVarsSum == 0) {
                    // Special case. We have a node in the equation that became
                    // garbage. Mark this node as such in the solution so
                    // when we reach other equations with the same node we
                    // can set all variables to zero.
                    sol.addGarbageNode(this.getNode());
                }
                // All variables are fixed, so this equation is no longer
                // useful.
                return true;
            }

            if (this.hasNode() && sol.isGarbage(this.getNode())) {
                // Special case. The node in the equation is garbage.
                // All variables must be zero. This may make the solution
                // invalid but we can properly identify this later.
                for (BoundVar var : this.vars) {
                    sol.setToZero(var);
                }
                // We have to discard this equation otherwise the solving
                // procedure won't terminate.
                return true;
            }

            // List of variables still open.
            ArrayList<BoundVar> openVars = this.getOpenVars(sol);
            // New constant value.
            int newConst = Multiplicity.sub(this.constant, fixedVarsSum);

            if (this.type == BoundType.LB) {
                if (openVars.size() == 1) {
                    // We have only one open variable that has to be at least
                    // the new constant. Set the lower bound to the new constant.
                    sol.cutLow(openVars.get(0), newConst);
                    if (this.hasNode()) {
                        // We need to keep this special equation around.
                        return false;
                    } else {
                        // We are done with this equation.
                        return true;
                    }
                }
                // else: two or more open variables.
                // Check if the max sum equals the constant.
                int openVarsMaxSum = this.getOpenVarsMaxSum(sol);
                if (openVarsMaxSum == newConst) {
                    // We can set all the open variables to their maximum.
                    for (BoundVar openVar : openVars) {
                        sol.cutLow(openVar, sol.getMaxValue(openVar));
                    }
                }
                // Maybe we improved the solution but we still need this
                // equation.
                return false;
            } // End: lower bound equation.

            if (this.type == BoundType.UB) {
                if (newConst == 0) {
                    // New constant is zero: all open variables must be zero.
                    for (BoundVar openVar : openVars) {
                        sol.cutHigh(openVar, newConst);
                    }
                    // We are done with this equation.
                    return true;
                }
                if (openVars.size() == 1) {
                    // We have only one open variable that has to be at most
                    // the new constant. Set the upper bound to the new constant.
                    sol.cutHigh(openVars.get(0), newConst);
                    if (this.hasNode()) {
                        // We need to keep this special equation around.
                        return false;
                    } else {
                        // We are done with this equation.
                        return true;
                    }
                }
                // If we reach this point we know that:
                // - This is an upper bound equation;
                // - The new constant is positive;
                // - There are two or more open variables.
                // Nothing to do except try to trim the upper bounds.
                for (BoundVar openVar : openVars) {
                    sol.cutHigh(openVar, newConst);
                }
                // Maybe we improved the solution but we still need this
                // equation.
                return false;
            } // End: upper bound equation.

            // This return point is never reached but the compiler cannot tell
            // this.
            assert false;
            return false;
        }

        /**
         * Returns true if the equation has open variables for the given
         * solution.
         */
        boolean hasOpenVars(Solution sol) {
            boolean result = false;
            for (BoundVar var : this.vars) {
                if (!sol.isSingleton(var)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        /**
         * Returns a list of equation variables that are open for the given
         * solution.
         */
        ArrayList<BoundVar> getOpenVars(Solution sol) {
            ArrayList<BoundVar> result =
                new ArrayList<BoundVar>(this.vars.size());
            for (BoundVar var : this.vars) {
                if (!sol.isSingleton(var)) {
                    result.add(var);
                }
            }
            return result;
        }

        /**
         * Returns the sum of all equation variables in the given solution.
         */
        int getVarsSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                result = Multiplicity.add(result, sol.getValue(var));
            }
            return result;
        }

        /**
         * Returns the sum of fixed equation variables in the given solution.
         */
        int getFixedVarsSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                if (sol.isSingleton(var)) {
                    result = Multiplicity.add(result, sol.getValue(var));
                }
            }
            return result;
        }

        /**
         * Returns the sum of the maximum values of the open equation variables
         * in the given solution.
         */
        int getOpenVarsMaxSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                if (!sol.isSingleton(var)) {
                    result += sol.getMaxValue(var);
                }
            }
            return result;
        }

        /**
         * Returns true if the given solution satisfies this equation.
         * Does not check the dual equation.
         */
        boolean isSatisfied(Solution sol) {
            int sum = this.getVarsSum(sol);
            boolean result = false;
            switch (this.type) {
            case LB:
                result = sum >= this.constant;
                break;
            case UB:
                result = sum <= this.constant;
                break;
            default:
                assert false;
            }
            return result;
        }

        /**
         * Returns true if the given solution satisfies this equation and its
         * dual. Note that this is a local check. Even if the given solution
         * satisfies this equation, some other equation may still be violated.
         */
        boolean isValidSolution(Solution sol) {
            boolean result = this.isSatisfied(sol);
            if (result && this.hasDual()) {
                result = this.dual.isSatisfied(sol);
            }
            return result;
        }

        /**
         * Branches the search space for new valid solutions. Branching is
         * based on the open variables of this equation so, the less open
         * variables we have, the better (less branching).
         *  
         * @param sol the solution to start with.
         * @param partialSols set of all partial solutions computed so far,
         *                    newly computed solutions that are not yet
         *                    finished are put in this set. 
         * @param finishedSols set of all finished solutions computed so far,
         *                    newly computed solutions that are finished are
         *                    put in this set. 
         */
        void getNewSolutions(Solution sol, SolutionSet partialSols,
                SolutionSet finishedSols) {
            assert !sol.isFinished();
            assert sol.getEqs(this.type).contains(this);

            sol.getEqs(this.type).remove(this);

            if (this.isSatisfied(sol)) {
                // The equation is already satisfied. Nothing to do.
                if (sol.isFinished()) {
                    EquationSystem.this.addFinishedSolution(sol, finishedSols);
                } else {
                    partialSols.add(sol);
                }
            } else {
                // Iterate the solutions.
                ArrayList<BoundVar> openVars = this.getOpenVars(sol);
                EquationBranchIterator iter =
                    new EquationBranchIterator(this, sol, openVars);
                while (iter.hasNext()) {
                    Solution newSol = iter.next();
                    if (newSol.isFinished()) {
                        EquationSystem.this.addFinishedSolution(newSol,
                            finishedSols);
                    } else {
                        partialSols.add(newSol);
                    }
                }
            }
        }
    }

    // ----------
    // ValueRange
    // ----------

    /** Range of possible values for the variables. Used in the solutions. */
    private static final class ValueRange implements Iterable<Integer> {

        /** Total range of values. */
        final int range[];
        /** Current minimum index in the range array. */
        int i;
        /** Current maximum index in the range array. */
        int j;
        /** Dual range. */
        ValueRange dual;

        /** Basic constructor. Start the indices to cover the given range. */
        ValueRange(int range[]) {
            this.range = range;
            this.i = 0;
            this.j = range.length - 1;
        }

        /** Copying constructor. */
        ValueRange(ValueRange original) {
            this.range = original.range;
            this.i = original.i;
            this.j = original.j;
            this.dual = original.dual;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int k = this.i; k <= this.j; k++) {
                int value = this.range[k];
                if (value == OMEGA) {
                    sb.append("w");
                } else {
                    sb.append(value);
                }
            }
            return sb.toString();
        }

        @Override
        public ValueRange clone() {
            return new ValueRange(this);
        }

        /** Returns true if this range indices are equal. */
        boolean isSingleton() {
            return this.i == this.j;
        }

        /** Returns the current minimal value of the range. */
        int getMin() {
            return this.range[this.i];
        }

        /** Returns the current maximal value of the range. */
        int getMax() {
            return this.range[this.j];
        }

        /** Sets the dual range. */
        void setDual(ValueRange dual) {
            this.dual = dual;
        }

        /**
         * Increases the minimum index until we hit the given limit or
         * the maximum index. Doesn't operate on the dual range.
         */
        void nonRecCutLow(int limit) {
            while (this.getMin() < limit && this.i < this.j) {
                this.i++;
            }
        }

        /**
         * Increases the minimum index until we hit the given limit or
         * the maximum index. Does operate on the dual range.
         */
        void cutLow(int limit) {
            this.nonRecCutLow(limit);
            this.dual.nonRecCutLow(limit);
        }

        /**
         * Decreases the maximum index until we hit the given limit or
         * the minimum index. Doesn't operate on the dual range.
         */
        void nonRecCutHigh(int limit) {
            while (this.getMax() > limit && this.i < this.j) {
                this.j--;
            }
        }

        /**
         * Decreases the maximum index until we hit the given limit or
         * the minimum index. Does operate on the dual range.
         */
        void cutHigh(int limit) {
            this.nonRecCutHigh(limit);
            this.dual.nonRecCutHigh(limit);
        }

        public ValueRangeIterator iterator() {
            return new ValueRangeIterator(this);
        }

        /** Returns true if this current range is 0 .. 1 .*/
        boolean isZeroOne() {
            return this.getMin() == 0 && this.getMax() == 1;
        }

        /** Sets this range and its dual to zero. */
        void setToZero() {
            this.nonRecSetToZero();
            this.dual.nonRecSetToZero();
        }

        /** Sets this range to zero. */
        void nonRecSetToZero() {
            this.i = 0;
            this.j = 0;
        }
    }

    // --------
    // Solution
    // --------

    /**
     * Class representing a possible solution for the equation system.
     * This is the only dynamic part of the system, the remaining structures
     * are all fixed when trying to solve the system. Objects of this class
     * are cloned during the solution search, so we try to keep the objects
     * as small as possible. 
     */
    private static final class Solution {

        /** Ranges for lower and upper bound variables. */
        final ValueRange lbValues[];
        final ValueRange ubValues[];
        /** Sets of equations to use for improving this solution. */
        final MyHashSet<Equation> lbEqs;
        final MyHashSet<Equation> ubEqs;
        /** Set of nodes marked as garbage. (Only used in first stage). */
        MyHashSet<ShapeNode> garbageNodes;
        /** Flag to indicate that this solution should be discarded. */
        boolean invalid;

        /** Basic constructor. */
        Solution(int varsCount, int lbRange[], int ubRange[],
                MyHashSet<Equation> lbEqs, MyHashSet<Equation> ubEqs) {
            this.lbValues = new ValueRange[varsCount];
            this.ubValues = new ValueRange[varsCount];
            for (int i = 0; i < varsCount; i++) {
                this.lbValues[i] = new ValueRange(lbRange);
                this.ubValues[i] = new ValueRange(ubRange);
                this.lbValues[i].setDual(this.ubValues[i]);
                this.ubValues[i].setDual(this.lbValues[i]);
            }
            this.lbEqs = lbEqs.clone();
            this.ubEqs = ubEqs.clone();
        }

        /** Copying constructor. */
        Solution(Solution original) {
            int varsCount = original.size();
            this.lbValues = new ValueRange[varsCount];
            this.ubValues = new ValueRange[varsCount];
            for (int i = 0; i < varsCount; i++) {
                this.lbValues[i] = original.lbValues[i].clone();
                this.ubValues[i] = original.ubValues[i].clone();
                this.lbValues[i].setDual(this.ubValues[i]);
                this.ubValues[i].setDual(this.lbValues[i]);
            }
            this.lbEqs = original.lbEqs.clone();
            this.ubEqs = original.ubEqs.clone();
            if (original.garbageNodes != null) {
                this.garbageNodes = original.garbageNodes.clone();
            }
        }

        /** Returns the proper range array for the given bound type. */
        ValueRange[] getValueRangeArray(BoundType type) {
            ValueRange result[] = null;
            switch (type) {
            case LB:
                result = this.lbValues;
                break;
            case UB:
                result = this.ubValues;
                break;
            default:
                assert false;
            }
            return result;
        }

        /** Returns the proper set of equations for the given bound type. */
        MyHashSet<Equation> getEqs(BoundType type) {
            MyHashSet<Equation> result = null;
            switch (type) {
            case LB:
                result = this.lbEqs;
                break;
            case UB:
                result = this.ubEqs;
                break;
            default:
                assert false;
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < this.size(); i++) {
                sb.append("(" + this.lbValues[i].toString() + ","
                    + this.ubValues[i].toString() + "), ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            return sb.toString();
        }

        @Override
        public Solution clone() {
            return new Solution(this);
        }

        /**
         * Returns the number of values of the solution. Equals the variable
         * count of the equation system.
         */
        int size() {
            return this.lbValues.length;
        }

        /** Returns the value range of the given variable. */
        ValueRange getValueRange(BoundVar var) {
            return this.getValueRangeArray(var.type)[var.number];
        }

        /** Returns true if the given variable has a singleton range. */
        boolean isSingleton(BoundVar var) {
            return this.getValueRange(var).isSingleton();
        }

        /**
         * Returns the minimum or maximum value of the given variable,
         * depending on the variable type.
         */
        int getValue(BoundVar var) {
            int result = 0;
            switch (var.type) {
            case UB:
                result = this.getValueRange(var).getMax();
                break;
            case LB:
                result = this.getValueRange(var).getMin();
                break;
            default:
                assert false;
            }
            return result;
        }

        /** Returns the maximum value of the given variable. */
        int getMaxValue(BoundVar var) {
            return this.getValueRange(var).getMax();
        }

        /** Cuts the minimum value of the given variable by the given limit. */
        void cutLow(BoundVar var, int limit) {
            this.getValueRange(var).cutLow(limit);
        }

        /** Cuts the maximum value of the given variable by the given limit. */
        void cutHigh(BoundVar var, int limit) {
            this.getValueRange(var).cutHigh(limit);
        }

        /** Cuts a bound of the given variable by the given limit. */
        void cut(BoundVar var, int limit) {
            switch (var.type) {
            case UB:
                this.cutHigh(var, limit);
                break;
            case LB:
                this.cutLow(var, limit);
                break;
            default:
                assert false;
            }
        }

        /** Sets the value of the given variable to zero. */
        void setToZero(BoundVar var) {
            if (this.isSingleton(var) && this.getValue(var) != 0) {
                // We are trying to set the variable to zero but it already
                // has a non-zero value. This means that the solution is
                // invalid, so we mark it as such.
                this.invalid = true;
            } else {
                this.getValueRange(var).setToZero();
            }
        }

        /**
         * Returns true if this solution is finished. This is the case if
         * both equation sets are empty or this solution is marked as invalid.
         * Special case for first stage: if the solution only has equations
         * with nodes then it is also considered finished. 
         */
        boolean isFinished() {
            boolean result =
                this.lbEqs.isEmpty() && this.ubEqs.isEmpty() || this.invalid;
            if (!result) {
                result =
                    allEqsHaveNodes(this.ubEqs) && allEqsHaveNodes(this.lbEqs);
            }
            return result;
        }

        /** Returns true if all equations in the given set have nodes. */
        boolean allEqsHaveNodes(Set<Equation> eqs) {
            boolean result = !eqs.isEmpty();
            for (Equation eq : eqs) {
                if (!eq.hasNode()) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        /**
         * Uses some simple heuristics to find an equation that will yield
         * the least amount of branching in the search. Upper bound equations
         * always have preference.
         */
        Equation getBestBranchingEquation() {
            Equation result = null;
            for (Equation eq : this.ubEqs) {
                if (isNewEqBetter(result, eq)) {
                    result = eq;
                }
            }
            if (result == null) {
                for (Equation eq : this.lbEqs) {
                    if (isNewEqBetter(result, eq)) {
                        result = eq;
                    }
                }
            }
            assert !result.hasNode();
            return result;
        }

        /**
         * Returns true if the new equation will produce less branching than
         * the old one.
         */
        boolean isNewEqBetter(Equation oldEq, Equation newEq) {
            if (newEq.hasNode()) {
                // It's pointless to use equations with nodes in branching.
                return false;
            }
            if (oldEq == null) {
                return true;
            }
            // else: oldEq != null && !newEq.hasNode()
            int newOpenVarsCount = newEq.getOpenVars(this).size();
            int oldOpenVarsCount = oldEq.getOpenVars(this).size();
            if (newOpenVarsCount < oldOpenVarsCount) {
                // The less open variables the better.
                return true;
            } else if (newOpenVarsCount > oldOpenVarsCount) {
                return false;
            } else {
                // Same number of open variables. Give preference to
                // to the equation with best constant.
                if (oldEq.type == BoundType.UB) {
                    return newEq.constant < oldEq.constant;
                } else { // oldEq.type == BoundType.LB
                    return newEq.constant > oldEq.constant;
                }
            }
        }

        /** Returns a multiplicity value for the given variable number. */
        Multiplicity getMultValue(int varNum, MultKind kind) {
            int i = this.lbValues[varNum].getMin();
            int j = this.ubValues[varNum].getMax();
            return Multiplicity.approx(i, j, kind);
        }

        /** Returns true if this solution subsumes the other. */
        boolean subsumes(Solution other) {
            boolean result = true;
            for (int varNum = 0; varNum < this.size(); varNum++) {
                if (this.lbValues[varNum].getMin() > other.lbValues[varNum].getMin()
                    || this.ubValues[varNum].getMax() < other.ubValues[varNum].getMax()) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        /**
         * Special method used when generating new solutions. Only affects
         * variables that are currently in a range of 0 .. 1 .
         */
        void projectToZeroWhenNeeded(BoundVar var) {
            ValueRange range = this.getValueRange(var);
            if (range.isZeroOne()) {
                range.cutHigh(0);
            }
        }

        /**
         * Returns true if the solution has at least one variable within the
         * 0 .. 1 range.
         */
        boolean hasZeroOneVars() {
            boolean result = false;
            for (int varNum = 0; varNum < this.size(); varNum++) {
                if (this.lbValues[varNum].isZeroOne()
                    && this.ubValues[varNum].isZeroOne()) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        /**
         * Returns a list of the given collection formed by the variables in
         * the zero .. one range.
         */
        ArrayList<BoundVar> getZeroOneVars(Collection<Duo<BoundVar>> vars) {
            ArrayList<BoundVar> result = new ArrayList<BoundVar>(vars.size());
            for (Duo<BoundVar> varDuo : vars) {
                BoundVar lbVar = varDuo.one();
                BoundVar ubVar = varDuo.two();
                ValueRange lbRange = this.getValueRange(lbVar);
                ValueRange ubRange = this.getValueRange(ubVar);
                if (lbRange.isZeroOne() && ubRange.isZeroOne()) {
                    result.add(ubVar);
                }
            }
            return result;
        }

        /**
         * Raises all variables in the given set to their maximum.
         * Used on first stage.
         */
        void setAllVarsToMax(Collection<Duo<BoundVar>> vars) {
            for (Duo<BoundVar> pair : vars) {
                BoundVar lbVar = pair.one();
                int max = this.getMaxValue(lbVar);
                this.cutLow(lbVar, max);
            }
        }

        /**
         * Adds the given node to the list of garbage nodes.
         * Used on first stage.
         */
        void addGarbageNode(ShapeNode node) {
            if (this.garbageNodes == null) {
                this.garbageNodes = new MyHashSet<ShapeNode>();
            }
            this.garbageNodes.add(node);
        }

        /** Checks if the given node is marked as garbage. */
        boolean isGarbage(ShapeNode node) {
            if (this.garbageNodes == null) {
                return false;
            } else {
                return this.garbageNodes.contains(node);
            }
        }
    }

    // ------------------
    // ValueRangeIterator
    // ------------------

    /**
     * Simple iterator for a value range. Starts with the minimum and
     * sweeps the range until hitting the maximum.
     */
    private static final class ValueRangeIterator implements Iterator<Integer> {

        ValueRange value;
        int i;

        ValueRangeIterator(ValueRange value) {
            this.value = value;
            this.i = value.i;
        }

        @Override
        public boolean hasNext() {
            return this.i <= this.value.j;
        }

        @Override
        public Integer next() {
            assert this.hasNext();
            return ++this.i;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.value.range[this.i] + "";
        }

        public Integer current() {
            assert this.hasNext();
            return this.i;
        }

        void reset() {
            this.i = this.value.i;
        }

    }

    // ---------------------
    // GenericBranchIterator
    // ---------------------

    /**
     * Generic iterator that compute all possible branches of a given solution. 
     */
    private static abstract class GenericBranchIterator implements
            Iterator<Solution> {

        /** Original solution used for branching. */
        final Solution sol;
        /** List of variables to be iterated. */
        final ArrayList<BoundVar> vars;
        /** Iterators for the values of variables. */
        final ValueRangeIterator iters[];
        /** List of all valid solutions. */
        final ArrayList<Solution> validSolutions;
        /** Index in the list of valid solutions to be returned next. */
        int next;

        /** Default constructor. */
        GenericBranchIterator(Solution sol, ArrayList<BoundVar> vars) {
            assert vars.size() > 0;
            this.sol = sol;
            this.vars = vars;
            this.iters = new ValueRangeIterator[vars.size()];
            int i = 0;
            for (BoundVar var : this.vars) {
                this.iters[i] = sol.getValueRange(var).iterator();
                i++;
            }
            this.validSolutions = new ArrayList<Solution>();
            this.next = 0;
        }

        @Override
        public String toString() {
            return Arrays.toString(this.iters);
        }

        @Override
        public boolean hasNext() {
            return this.next < this.validSolutions.size();
        }

        @Override
        public Solution next() {
            assert this.hasNext();
            return this.validSolutions.get(this.next++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Creates and stores a new valid solution. */
        abstract void createNewSolution();

        /**
         * Iterates over all possible values for all open variables and
         * stores all valid results. No subsumption checks are done here,
         * they are handled by the solution set (see below).
         */
        void computeAllSolutions() {
            while (this.iters[0].hasNext()) {
                createNewSolution();
                // Update iterators.
                int curr = this.iters.length - 1;
                this.iters[curr].next();
                if (!this.iters[curr].hasNext()) {
                    int prev = curr - 1;
                    while (prev >= 0) {
                        this.iters[prev].next();
                        if (this.iters[prev].hasNext()) {
                            break;
                        }
                        prev--;
                    }
                    if (prev >= 0) {
                        // Reset all iterators between prev + 1 and curr .
                        for (int i = prev + 1; i <= curr; i++) {
                            this.iters[i].reset();
                        }
                    }
                }
            }
        }
    }

    // ----------------------
    // EquationBranchIterator
    // ----------------------

    /**
     * Iterator that compute all possible branches of a given solution and
     * equation. All solutions returned by the iterator are guaranteed to
     * satisfy the given equation but others equations in the system may still
     * be violated. 
     */
    private static final class EquationBranchIterator extends
            GenericBranchIterator {

        /** Equation used for branching. */
        final Equation eq;

        /** Default constructor. Pre-computes all solutions. */
        EquationBranchIterator(Equation eq, Solution sol,
                ArrayList<BoundVar> openVars) {
            super(sol, openVars);
            this.eq = eq;
            this.computeAllSolutions();
        }

        /**
         * Creates and stores a new valid solution.
         */
        @Override
        void createNewSolution() {
            Solution newSol = this.sol.clone();
            int i = 0;
            for (ValueRangeIterator iter : this.iters) {
                newSol.cut(this.vars.get(i), iter.current());
                i++;
            }
            if (this.eq.hasDual()) {
                this.eq.dual.nonRecComputeNewValues(newSol);
            } else if (this.eq.type == BoundType.LB) {
                // Variables with zero or one values have to be handled
                // in a special way.
                for (BoundVar openVar : this.vars) {
                    newSol.projectToZeroWhenNeeded(openVar);
                }
            }
            if (this.eq.isValidSolution(newSol)) {
                this.validSolutions.add(newSol);
            }
        }
    }

    // ----------------------
    // ZeroOneBranchIterator
    // ----------------------

    /**
     * Iterator that compute all possible branches of variables in the zero
     * one range. The solutions returned are NOT max'ed out.
     * Used only on first state.
     */
    private static final class ZeroOneBranchIterator extends
            GenericBranchIterator {

        /** Default constructor. Pre-computes all solutions. */
        ZeroOneBranchIterator(Solution sol, ArrayList<BoundVar> zeroOneVars) {
            super(sol, zeroOneVars);
            this.computeAllSolutions();
        }

        /**
         * Creates and stores a new valid solution.
         */
        @Override
        void createNewSolution() {
            Solution newSol = this.sol.clone();
            int i = 0;
            for (ValueRangeIterator iter : this.iters) {
                newSol.cutHigh(this.vars.get(i), iter.current());
                i++;
            }
            this.validSolutions.add(newSol);
        }
    }

    // -----------
    // SolutionSet
    // -----------

    /**
     * Dedicated hash set to stores solutions. Checks for subsumption in
     * both directions when adding a new solution to the set. This guarantees
     * a minimum number of solutions and thus less materialisation objects.
     */
    private static final class SolutionSet extends MyHashSet<Solution> {
        @Override
        public boolean add(Solution newSol) {
            boolean storeNew = true;
            MyHashSet<Solution> toRemove = null;
            for (Solution oldSol : this) {
                if (oldSol.subsumes(newSol)) {
                    storeNew = false;
                    break;
                } else if (newSol.subsumes(oldSol)) {
                    if (toRemove == null) {
                        toRemove = new MyHashSet<Solution>(this.size());
                    }
                    toRemove.add(oldSol);
                }
            }
            if (toRemove != null) {
                this.removeAll(toRemove);
            }
            if (storeNew) {
                super.add(newSol);
            }
            return storeNew;
        }
    }

}
