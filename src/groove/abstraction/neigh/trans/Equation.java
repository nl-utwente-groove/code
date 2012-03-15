package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.OMEGA;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.neigh.trans.EquationSystem.BoundType;
import groove.abstraction.neigh.trans.EquationSystem.EquationBranchIterator;
import groove.abstraction.neigh.trans.EquationSystem.Solution;
import groove.abstraction.neigh.trans.EquationSystem.SolutionSet;
import groove.abstraction.neigh.trans.EquationSystem.Var;
import groove.util.Fixable;

import java.util.ArrayList;
import java.util.Iterator;

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
final class Equation implements Fixable {

    /**
     * Comment for <code>newEquationSystem</code>
     */
    private final EquationSystem eqSys;
    /** Type of this equation. */
    final BoundType type;
    /** The upper bound of the allowed values. */
    final int bound;
    /** List of variables (type compatible with the equation). */
    final ArrayList<Var> vars;
    /** Constant value for this equation. */
    int constant;
    /**
     * Special case: reference to a node that may become garbage in the
     * first stage, and thus affects the solution of the system.
     */
    final ShapeNode node;
    /**
     * The hash code of this equation. Once computed it cannot be 0.
     * Once it's different than 0, the equation is fixed and no
     * elements can be added or removed. This avoids nasty hashing problems.
     */
    private int hashCode;

    /** Basic constructor. */
    Equation(EquationSystem newEquationSystem, BoundType type,
            int varsCount, int constant, ShapeNode node) {
        this.eqSys = newEquationSystem;
        this.bound = this.eqSys.bound;
        this.type = type;
        this.vars = new ArrayList<Var>(varsCount);
        this.constant = constant;
        this.node = node;
        this.hashCode = 0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator<Var> iter = this.vars.iterator();
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
        for (Var var : this.vars) {
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
    void addVar(Var var) {
        assert !this.isFixed();
        assert var.type == this.type;
        this.vars.add(var);
    }

    /**
     * Checks if this equation is useful and thus should be added to
     * the equation system.
     */
    boolean isUseful() {
        boolean result = false;
        switch (this.type) {
        case UB:
            result = this.hasNode() || this.constant < this.getMaxRangeValue();
            break;
        case LB:
            result = this.hasNode() || this.constant > this.getMinRangeValue();
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Returns the maximum value for the variables in the equation. */
    int getMaxRangeValue() {
        int result = -1;
        switch (this.type) {
        case UB:
            result = OMEGA;
            break;
        case LB:
            result = this.bound;
            break;
        default:
            assert false;
        }
        assert result != -1;
        return result;
    }

    /** Returns the minimum value for the variables in the equation. */
    int getMinRangeValue() {
        return 0;
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
    boolean computeNewValues(Solution sol) {
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
            for (Var var : this.vars) {
                sol.setToZero(var);
            }
            // We have to discard this equation otherwise the solving
            // procedure won't terminate.
            return true;
        }

        // List of variables still open.
        ArrayList<Var> openVars = this.getOpenVars(sol);
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
                for (Var openVar : openVars) {
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
                for (Var openVar : openVars) {
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
            for (Var openVar : openVars) {
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
        for (Var var : this.vars) {
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
    ArrayList<Var> getOpenVars(Solution sol) {
        ArrayList<Var> result = new ArrayList<Var>(this.vars.size());
        for (Var var : this.vars) {
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
        for (Var var : this.vars) {
            result = Multiplicity.add(result, sol.getBoundValue(var));
        }
        return result;
    }

    /**
     * Returns the sum of fixed equation variables in the given solution.
     */
    int getFixedVarsSum(Solution sol) {
        int result = 0;
        for (Var var : this.vars) {
            if (sol.isSingleton(var)) {
                result = Multiplicity.add(result, sol.getBoundValue(var));
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
        for (Var var : this.vars) {
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
    boolean isValidSolution(Solution sol) {
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

        if (this.isValidSolution(sol)) {
            // The equation is already satisfied. Nothing to do.
            if (sol.isFinished()) {
                if (this.eqSys.isValid(sol)) {
                    finishedSols.add(sol);
                }
            } else {
                partialSols.add(sol);
            }
        } else {
            // Iterate the solutions.
            ArrayList<Var> openVars = this.getOpenVars(sol);
            EquationBranchIterator iter =
                new EquationSystem.EquationBranchIterator(this, sol,
                    openVars);
            while (iter.hasNext()) {
                Solution newSol = iter.next();
                if (newSol.isFinished()) {
                    if (this.eqSys.isValid(newSol)) {
                        finishedSols.add(newSol);
                    }
                } else {
                    partialSols.add(newSol);
                }
            }
        }
    }
}