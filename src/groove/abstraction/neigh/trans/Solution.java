package groove.abstraction.neigh.trans;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class representing a possible solution for the equation system.
 * This is the only dynamic part of the system, the remaining structures
 * are all fixed when trying to solve the system. Objects of this class
 * are cloned during the solution search, so we try to keep the objects
 * as small as possible. 
 */
final class Solution {
    private final List<Duo<Var>> vars;
    /** Values for lower and upper bound variables. */
    final Value values[];
    /** Sets of equations to use for improving this solution. */
    private final MyHashSet<Equation> lbEqs;
    final MyHashSet<Equation> ubEqs;
    /** Set of nodes marked as garbage. (Only used in first stage). */
    private MyHashSet<ShapeNode> garbageNodes;
    /** Flag to indicate that this solution should be discarded. */
    boolean invalid;

    /** Basic constructor. */
    Solution(List<Duo<Var>> vars, int bound, MyHashSet<Equation> lbEqs,
            MyHashSet<Equation> ubEqs) {
        this.vars = vars;
        int varsCount = vars.size();
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
        this.vars = original.vars;
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

    /**
     * Returns the sum of the bound values for a given
     * collection of variables, optionally restricted
     * to those for which the value is fixed.
     */
    public int getSum(List<Var> vars, boolean singletons) {
        int result = 0;
        for (Var var : vars) {
            if (!singletons || isSingleton(var)) {
                result = Multiplicity.add(result, getBoundValue(var));
            }
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
            result = allEqsHaveNodes(this.ubEqs) && allEqsHaveNodes(this.lbEqs);
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
    private boolean isNewEqBetter(Equation oldEq, Equation newEq) {
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
     * Returns a list of all upper bound variables in
     * the zero .. one range.
     */
    List<Var> getZeroOneVars() {
        List<Var> result = new ArrayList<Var>(this.vars.size());
        for (int v = 0; v < this.values.length; v++) {
            Value value = this.values[v];
            if (value.isZeroOne()) {
                result.add(this.vars.get(v).two());
            }
        }
        return result;
    }

    /**
     * Raises all variables in the given set to their maximum.
     * Used on first stage.
     */
    void setAllVarsToMax() {
        for (Value value : this.values) {
            value.i = Math.min(value.j, value.bound);
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

    /**
     * Returns the set of garbage nodes.
     */
    public Set<ShapeNode> getGarbageNodes() {
        return this.garbageNodes;
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