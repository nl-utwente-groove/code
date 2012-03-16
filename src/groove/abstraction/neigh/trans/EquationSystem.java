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
import groove.abstraction.neigh.shape.ShapeNode;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Creates a new equation system for the given materialisation. */
    public final static EquationSystem newEqSys(Materialisation mat) {
        assert mat.getStage() == 1;
        return new EquationSystem(mat.getStage());
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
     * Use {@link #newEqSys(Materialisation)}.
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
     * Stores the given pair of equations in the appropriate sets. If the
     * equations have no variables or are not useful, they are discarded. An
     * equation is not useful, for example, if its constant is at the extremity
     * of the valid bound range.
     */
    public void storeEquations(Duo<Equation> eqs) {
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
            new Solution(this.varsCount, this.bound, this.lbEqs, this.ubEqs);
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
        result.setAllVarsToMax(this.allVars);
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
        ArrayList<Var> zeroOneVars = sol.getZeroOneVars(this.allVars);
        // Iterate the solutions.
        ZeroOneBranchIterator iter =
            new ZeroOneBranchIterator(sol, zeroOneVars);
        while (iter.hasNext()) {
            Solution newSol = iter.next();
            newSol.setAllVarsToMax(this.allVars);
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
                Solution maxSol = maxSolution(sol);
                if (isValid(maxSol)) {
                    finishedSols.add(maxSol);
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
    void getNewSolutions(Equation eq, Solution sol, SolutionSet partialSols,
            SolutionSet finishedSols) {
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
            EquationBranchIterator iter = new EquationBranchIterator(eq, sol);
            while (iter.hasNext()) {
                Solution newSol = iter.next();
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
    public boolean isValid(Solution sol) {
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

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ---------
    // BoundType
    // ---------

    /** Types of equations and variables. */
    enum BoundType {
        UB, LB
    }

    // ---
    // Var
    // ---

    /**
     * Multiplicity variable class. Each variable has an unique pair of 
     * number and type (upper or lower bound). Variables can only be present
     * in equations with compatible types. The variables themselves have no
     * further information (i.e., they don't have a current value). This allows
     * for a static representation of the equation system.  
     */
    public static final class Var {

        /** Natural number that identifies this variable. */
        final int number;
        /** Upper or lower bound type. */
        final BoundType type;

        /** Basic constructor. */
        Var(int number, BoundType type) {
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

        /** Returns the number of this variable. */
        public int getNumber() {
            return this.number;
        }
    }

    // --------
    // Equation
    // --------

    /** Range of possible values for the variables. Used in the solutions. */
    private static final class Value implements Iterable<Integer> {

        /** Total range of values. */
        final int bound;
        /** Current minimum index in the range. */
        int i;
        /** Current maximum index in the range. */
        int j;

        /** Basic constructor. Start the indices to cover the given range. */
        Value(int bound) {
            this.bound = bound;
            this.i = 0;
            this.j = OMEGA;
        }

        /** Copying constructor. */
        Value(Value original) {
            this.bound = original.bound;
            this.i = original.i;
            this.j = original.j;
        }

        @Override
        public String toString() {
            return "(" + this.i + "," + (this.j == OMEGA ? "w" : this.j) + ")";
        }

        @Override
        public Value clone() {
            return new Value(this);
        }

        /** Returns true if this range indices are equal. */
        boolean isSingleton() {
            return this.i == this.j;
        }

        /**
         * Increases the minimum index until we hit the given limit or
         * the maximum index. Doesn't operate on the dual range.
         */
        void cutLow(int limit) {
            if (this.i < limit) {
                this.i = Math.min(this.j, limit);
            }
        }

        /**
         * Decreases the maximum index until we hit the given limit or
         * the minimum index. Doesn't operate on the dual range.
         */
        void cutHigh(int limit) {
            if (this.j > limit) {
                this.j = Math.max(this.i, limit);
            }
        }

        public ValueIterator iterator() {
            return new ValueIterator(this);
        }

        /** Returns true if this current range is 0 .. 1 .*/
        boolean isZeroOne() {
            return this.i == 0 && this.j == 1;
        }

        /** Sets this value to zero. */
        void setToZero() {
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
    static final class Solution {

        /** Values for lower and upper bound variables. */
        final Value values[];
        /** Sets of equations to use for improving this solution. */
        final MyHashSet<Equation> lbEqs;
        final MyHashSet<Equation> ubEqs;
        /** Set of nodes marked as garbage. (Only used in first stage). */
        MyHashSet<ShapeNode> garbageNodes;
        /** Flag to indicate that this solution should be discarded. */
        boolean invalid;

        /** Basic constructor. */
        Solution(int varsCount, int bound, MyHashSet<Equation> lbEqs,
                MyHashSet<Equation> ubEqs) {
            this.values = new Value[varsCount];
            for (int i = 0; i < varsCount; i++) {
                this.values[i] = new Value(bound);
            }
            this.lbEqs = lbEqs.clone();
            this.ubEqs = ubEqs.clone();
        }

        /** Copying constructor. */
        Solution(Solution original) {
            int varsCount = original.size();
            this.values = new Value[varsCount];
            for (int i = 0; i < varsCount; i++) {
                this.values[i] = original.values[i].clone();
            }
            this.lbEqs = original.lbEqs.clone();
            this.ubEqs = original.ubEqs.clone();
            if (original.garbageNodes != null) {
                this.garbageNodes = original.garbageNodes.clone();
            }
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
                sb.append(this.values[i].toString() + ", ");
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
            return this.values.length;
        }

        /** Returns the value of the given variable. */
        Value getValue(Var var) {
            return this.values[var.number];
        }

        /** Returns true if the given variable has a singleton range. */
        boolean isSingleton(Var var) {
            return this.getValue(var).isSingleton();
        }

        /**
         * Returns the minimum or maximum value of the given variable,
         * depending on the variable type.
         */
        int getBoundValue(Var var) {
            int result = 0;
            switch (var.type) {
            case UB:
                result = this.getValue(var).j;
                break;
            case LB:
                result = this.getValue(var).i;
                break;
            default:
                assert false;
            }
            return result;
        }

        /** Returns the maximum value of the given variable. */
        int getMaxValue(Var var) {
            return this.getValue(var).j;
        }

        /** Cuts the minimum value of the given variable by the given limit. */
        void cutLow(Var var, int limit) {
            this.getValue(var).cutLow(limit);
        }

        /** Cuts the maximum value of the given variable by the given limit. */
        void cutHigh(Var var, int limit) {
            this.getValue(var).cutHigh(limit);
        }

        /** Cuts a bound of the given variable by the given limit. */
        void cut(Var var, int limit) {
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
        void setToZero(Var var) {
            if (this.isSingleton(var) && this.getBoundValue(var) != 0) {
                // We are trying to set the variable to zero but it already
                // has a non-zero value. This means that the solution is
                // invalid, so we mark it as such.
                this.invalid = true;
            } else {
                this.getValue(var).setToZero();
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
                if (oldEq.getType() == BoundType.UB) {
                    return newEq.getConstant() < oldEq.getConstant();
                } else { // oldEq.type == BoundType.LB
                    return newEq.getConstant() > oldEq.getConstant();
                }
            }
        }

        /** Returns a multiplicity value for the given variable number. */
        Multiplicity getMultValue(int varNum, MultKind kind) {
            int i = this.values[varNum].i;
            int j = this.values[varNum].j;
            return Multiplicity.approx(i, j, kind);
        }

        /** Returns true if this solution subsumes the other. */
        boolean subsumes(Solution other) {
            boolean result = true;
            for (int varNum = 0; varNum < this.size(); varNum++) {
                if (this.values[varNum].i > other.values[varNum].i
                    || this.values[varNum].j < other.values[varNum].j) {
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
        void projectToZeroWhenNeeded(Var var) {
            Value range = this.getValue(var);
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
                if (this.values[varNum].isZeroOne()) {
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
        ArrayList<Var> getZeroOneVars(Collection<Duo<Var>> vars) {
            ArrayList<Var> result = new ArrayList<Var>(vars.size());
            for (Duo<Var> varDuo : vars) {
                Var lbVar = varDuo.one();
                Var ubVar = varDuo.two();
                Value value = this.getValue(lbVar);
                if (value.isZeroOne()) {
                    result.add(ubVar);
                }
            }
            return result;
        }

        /**
         * Raises all variables in the given set to their maximum.
         * Used on first stage.
         */
        void setAllVarsToMax(Collection<Duo<Var>> vars) {
            for (Duo<Var> pair : vars) {
                Var lbVar = pair.one();
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
     * Simple iterator for a value. Starts with the minimum and
     * sweeps the range until hitting the maximum.
     */
    private static final class ValueIterator implements Iterator<Integer> {

        Value value;
        int i;

        ValueIterator(Value value) {
            this.value = value;
            this.i = value.i;
        }

        @Override
        public boolean hasNext() {
            return this.i <= Math.min(this.value.j, this.value.bound);
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
    static abstract class GenericBranchIterator implements Iterator<Solution> {

        /** Original solution used for branching. */
        final Solution sol;
        /** List of variables to be iterated. */
        final ArrayList<Var> vars;
        /** Iterators for the values of variables. */
        final ValueIterator iters[];
        /** List of all valid solutions. */
        final ArrayList<Solution> validSolutions;
        /** Index in the list of valid solutions to be returned next. */
        int next;

        /** Default constructor. */
        GenericBranchIterator(Solution sol, ArrayList<Var> vars) {
            assert vars.size() > 0;
            this.sol = sol;
            this.vars = vars;
            this.iters = new ValueIterator[vars.size()];
            int i = 0;
            for (Var var : this.vars) {
                this.iters[i] = sol.getValue(var).iterator();
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
    static final class EquationBranchIterator extends GenericBranchIterator {

        /** Equation used for branching. */
        final Equation eq;

        /** Default constructor. Pre-computes all solutions. */
        EquationBranchIterator(Equation eq, Solution sol) {
            super(sol, eq.getOpenVars(sol));
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
            for (ValueIterator iter : this.iters) {
                newSol.cut(this.vars.get(i), iter.current());
                i++;
            }
            if (this.eq.getType() == BoundType.LB) {
                // Variables with zero or one values have to be handled
                // in a special way.
                for (Var openVar : this.vars) {
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
     * one range. Used only on first state.
     */
    private static final class ZeroOneBranchIterator extends
            GenericBranchIterator {

        /** Default constructor. Pre-computes all solutions. */
        ZeroOneBranchIterator(Solution sol, ArrayList<Var> zeroOneVars) {
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
            for (ValueIterator iter : this.iters) {
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
    static final class SolutionSet extends MyHashSet<Solution> {
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
