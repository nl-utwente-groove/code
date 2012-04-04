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
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashSet;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
    /** 
     * Flag to test out the behaviour of the materialisation
     * in case 0..1 multiplicities in stage 1 are not expanded
     * artificially.
     */
    public static boolean ENABLE_ZERO_ONE_BRANCHES = true;

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Creates a new equation system for the given materialisation. */
    public final static EquationSystem newInstance() {
        return new EquationSystem(1);
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

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
    private final int bound;
    /** The number of multiplicity variables of the system. */
    private int varsCount;

    /** List of all variables occurring in the equation system. */
    private final List<Duo<Var>> allVars;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor to avoid object creation.
     * Use {@link #newInstance()}.
     */
    public EquationSystem(int stage) {
        this.stage = stage;
        this.bound = computeBound(stage);
        this.trivialEqs = new MyHashSet<Equation>();
        this.lbEqs = new MyHashSet<Equation>();
        this.ubEqs = new MyHashSet<Equation>();
        this.allVars = new ArrayList<Duo<Var>>();
        this.varsCount = 0;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
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

    /** Returns the maximum bound for the values in a given stage. */
    private int computeBound(int stage) {
        MultKind kind = null;
        switch (stage) {
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
        return Multiplicity.getBound(kind) + 1;
    }

    /**
     * Returns the upper bound of the possible values of all variables.
     */
    public int getBound() {
        return this.bound;
    }

    /** 
     * Returns a fresh duo of variables, consisting of a lower bound
     * and an upper bound variable with the same (fresh) numbers.
     */
    public Duo<Var> createVars() {
        Var lbVar = new Var(this.varsCount, BoundType.LB);
        Var ubVar = new Var(this.varsCount, BoundType.UB);
        this.varsCount++;
        Duo<Var> result = Duo.newDuo(lbVar, ubVar);
        this.allVars.add(result);
        return result;
    }

    /** Returns the number of known (pairs of) variables. */
    public int getVarsCount() {
        return this.varsCount;
    }

    /**
     * Stores the given pair of equations in the appropriate sets. If the
     * equations have no variables or are not useful, they are discarded. An
     * equation is not useful, for example, if its constant is at the extremity
     * of the valid bound range.
     */
    public void addEquations(Duo<Equation> eqs) {
        Equation lbEq = eqs.one();
        Equation ubEq = eqs.two();
        assert lbEq.getVars().size() == ubEq.getVars().size();
        if (lbEq.isEmpty()) {
            // Empty equations. Nothing to do.
            return;
        }
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

    /** Computes and returns the solutions to this equation system. */
    public SolutionSet computeSolutions() {
        // Compute all solutions.
        Solution initialSol =
            new Solution(this.allVars, this.bound, this.lbEqs, this.ubEqs);
        // First iterate once over the trivial solutions.
        for (Equation eq : this.trivialEqs) {
            eq.computeNewValues(initialSol);
        }
        SolutionSet finishedSols = new SolutionSet();
        SolutionSet partialSols = new SolutionSet();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Iterator<Solution> iter = partialSols.iterator();
            Solution sol = iter.next();
            iter.remove();
            this.iterateSolution(sol, partialSols, finishedSols);
        }
        return finishedSols;
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
     * Checks if we can branch on the variables with zero or one values.
     * Used only for the first stage.
     * @return true if we can branch, false otherwise.
     */
    private boolean canBranchOnZeroOneValues(Solution sol) {
        return ENABLE_ZERO_ONE_BRANCHES && this.stage == 1
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
        // Iterate the solutions.
        for (Solution newSol : new ZeroOneBranchList(sol)) {
            newSol.setAllVarsToMax();
            if (isValid(newSol)) {
                finishedSols.add(newSol);
            }
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
            if (isValid(sol)) {
                finishedSols.add(sol);
            }
        } else {
            // Check if we're in first stage and we can stop early.
            if (this.canMaxSolution(sol)) {
                // Yes, we can stop. Store the max'ed solution.
                sol.setAllVarsToMax();
                if (isValid(sol)) {
                    finishedSols.add(sol);
                }
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
                getNewSolutions(branchingEq, sol, partialSols, finishedSols);
            }
        }
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
    private void getNewSolutions(Equation eq, Solution sol,
            SolutionSet partialSols, SolutionSet finishedSols) {
        assert !sol.isFinished();
        assert sol.getEqs(eq.getType()).contains(eq);

        sol.getEqs(eq.getType()).remove(eq);

        if (eq.isValidSolution(sol)) {
            // The equation is already satisfied. Nothing to do.
            if (sol.isFinished()) {
                if (isValid(sol)) {
                    finishedSols.add(sol);
                }
            } else {
                partialSols.add(sol);
            }
        } else {
            // Iterate the solutions.
            for (Solution newSol : new EquationBranchList(eq, sol)) {
                if (newSol.isFinished()) {
                    if (isValid(newSol)) {
                        finishedSols.add(newSol);
                    }
                } else {
                    partialSols.add(newSol);
                }
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
        while (solutionModified) {
            solutionModified = false;
            for (BoundType type : BoundType.values()) {
                Iterator<Equation> iter = sol.getEqs(type).iterator();
                while (iter.hasNext()) {
                    Equation eq = iter.next();
                    boolean removeEq = eq.computeNewValues(sol);
                    if (removeEq) {
                        iter.remove();
                    }
                    solutionModified = solutionModified || removeEq;
                }
            }
        }
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
            if (!eq.isValidSolution(sol)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Simple iterator for a value. Starts with the minimum and
     * sweeps the range until hitting the maximum.
     */
    private static final class ValueIterator {
        /** Maximum value for the count. */
        private final int maxCount;
        /** Start value for the result. */
        private final int start;
        /** Number of invocations of {@link #doNext()}. */
        private int count;

        ValueIterator(BoundType type, Value value) {
            assert type == BoundType.LB || value.j != OMEGA;
            this.maxCount = Math.min(value.j, value.bound) - value.i;
            this.start = value.i;
            reset();
        }

        /** Indicates if a next increment is possible. */
        public boolean hasNext() {
            return this.count < this.maxCount;
        }

        /** Increases the count. */
        public void doNext() {
            assert this.hasNext();
            this.count++;
        }

        /** Returns number of invocations of {@link #doNext()}. */
        public int count() {
            return this.count;
        }

        /** Returns the current value of the iterator. */
        public int current() {
            return this.start + this.count;
        }

        /** Resets the count to zero. */
        public void reset() {
            this.count = 0;
        }
    }

    // -----------------
    // GenericBranchList
    // -----------------

    /**
     * Generic list that compute all possible branches of a given solution. 
     */
    private static abstract class GenericBranchList extends ArrayList<Solution> {
        /** Original solution used for branching. */
        final Solution sol;
        /** List of variables to be iterated. */
        final List<Var> vars;
        /** Iterators for the values of variables. */
        final ValueIterator iters[];

        /** Default constructor. */
        GenericBranchList(Solution sol, List<Var> vars) {
            assert vars.size() > 0;
            this.sol = sol;
            this.vars = vars;
            this.iters = new ValueIterator[vars.size()];
            int i = 0;
            for (Var var : this.vars) {
                this.iters[i] =
                    new ValueIterator(var.getType(), sol.getValue(var));
                i++;
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(this.iters);
        }

        /** Creates and stores a new valid solution. */
        void addNewSolution() {
            Solution newSol = this.sol.clone();
            for (int i = 0; i < this.iters.length; i++) {
                newSol.cut(this.vars.get(i), this.iters[i].current());
            }
            this.add(newSol);
        }
    }

    // ------------------
    // EquationBranchList
    // ------------------

    /**
     * List that collects all possible branches of a given solution and
     * equation. All solutions returned by the iterator are guaranteed to
     * satisfy the given equation but others equations in the system may still
     * be violated. 
     */
    private static final class EquationBranchList extends GenericBranchList {
        /** Default constructor. Pre-computes all solutions. */
        EquationBranchList(Equation eq, Solution sol) {
            super(sol, eq.getOpenVars(sol));
            int sum = sol.getSum(eq.getVars(), false);
            assert sum != OMEGA;
            int diff =
                eq.getType() == BoundType.LB ? eq.getConstant() - sum : sum
                    - eq.getConstant();
            boolean done = diff < 0;
            int length = this.iters.length;
            while (!done) {
                int pos = length - 1;
                while (pos >= 0 && (diff == 0 || !this.iters[pos].hasNext())) {
                    diff += this.iters[pos].count();
                    this.iters[pos].reset();
                    pos--;
                }
                done = pos < 0;
                if (!done) {
                    this.iters[pos].doNext();
                    diff--;
                }
                if (diff == 0) {
                    addNewSolution();
                }
            }
        }
    }

    // -----------------
    // ZeroOneBranchList
    // -----------------

    /**
     * List that compute all possible branches of variables in the zero
     * one range. Used only on first state.
     */
    private static final class ZeroOneBranchList extends GenericBranchList {

        /** Default constructor. Pre-computes all solutions. */
        ZeroOneBranchList(Solution sol) {
            super(sol, sol.getZeroOneVars());
            boolean done = false;
            int length = this.iters.length;
            while (!done) {
                addNewSolution();
                int pos = length - 1;
                while (pos >= 0 && !this.iters[pos].hasNext()) {
                    this.iters[pos].reset();
                    pos--;
                }
                done = pos < 0;
                if (!done) {
                    this.iters[pos].doNext();
                }
            }
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
