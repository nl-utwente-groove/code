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
import groove.util.Duo;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    /** EDUARDO: Comment this... */
    public final static EquationSystem newEqSys(Materialisation mat) {
        return new EquationSystem(mat, 1);
    }

    private final Materialisation mat;
    private final int stage;
    private final Set<Equation> trivialEqs;
    private final THashSet<Equation> lbEqs;
    private final THashSet<Equation> ubEqs;
    private final Map<ShapeEdge,Duo<BoundVar>> edgeVarsMap;
    private int lbRange[];
    private int ubRange[];
    private int varsCount;

    private EquationSystem(Materialisation mat, int stage) {
        assert mat != null;
        assert stage >= 1 && stage <= 3;
        this.mat = mat;
        this.stage = stage;
        this.trivialEqs = new THashSet<Equation>();
        this.lbEqs = new THashSet<Equation>();
        this.ubEqs = new THashSet<Equation>();
        if (this.stage == 1) {
            this.edgeVarsMap = new THashMap<ShapeEdge,Duo<BoundVar>>();
        } else {
            this.edgeVarsMap = null;
        }
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
        for (Equation eq : this.lbEqs) {
            sb.append(eq + "\n");
        }
        for (Equation eq : this.ubEqs) {
            sb.append(eq + "\n");
        }
        return sb.toString();
    }

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

    private void createFirstStage() {
        assert this.stage == 1;
        Shape shape = this.mat.getShape();

        // Create the edge bundles.
        Set<EdgeBundle> bundles = new THashSet<EdgeBundle>();
        for (ShapeEdge edge : this.mat.getAffectedEdges()) {
            this.addToEdgeBundle(bundles, edge.source(), edge, OUTGOING);
            this.addToEdgeBundle(bundles, edge.target(), edge, INCOMING);
        }

        // For each bundle...
        for (EdgeBundle bundle : bundles) {
            bundle.computeAdditionalEdges(this.mat);
            int varsCount = bundle.edges.size();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // ... create one lower bound and one upper bound equation.
            Equation lbEq =
                new Equation(BoundType.LB, Relation.GE,
                    constMult.getLowerBound(), varsCount);
            Equation ubEq =
                new Equation(BoundType.UB, Relation.LE,
                    constMult.getUpperBound(), varsCount);

            // For each edge in the bundle...
            for (ShapeEdge edge : bundle.edges) {
                // ... create two bound variables.
                Pair<BoundVar,BoundVar> varPair = retrieveBoundVars(edge);
                BoundVar lbVar = varPair.one();
                BoundVar ubVar = varPair.two();
                lbEq.addVar(lbVar);
                ubEq.addVar(ubVar);

                // Create one additional equations for the fixed edges.
                if (bundle.direction == OUTGOING && this.mat.isFixed(edge)) {
                    Equation trivialLbEq =
                        new Equation(BoundType.LB, Relation.GE, 1, 1);
                    Equation trivialUbEq =
                        new Equation(BoundType.UB, Relation.LE, 1, 1);
                    trivialLbEq.addVar(lbVar);
                    trivialUbEq.addVar(ubVar);
                    this.storeEquations(trivialLbEq, trivialUbEq);
                }
            }
            this.storeEquations(lbEq, ubEq);
        }
    }

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

    private Duo<BoundVar> retrieveBoundVars(ShapeEdge edge) {
        Duo<BoundVar> vars = this.edgeVarsMap.get(edge);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.edgeVarsMap.put(edge, vars);
            this.varsCount++;
        }
        return vars;
    }

    private void storeEquations(Equation lbEq, Equation ubEq) {
        // Fill the duality relation first, before we store the equations.
        lbEq.setDual(ubEq);
        ubEq.setDual(lbEq);
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

    private void createSecondStage() {
        // todo
    }

    private void createThirdStage() {
        // todo
    }

    /** EDUARDO: Comment this... */
    public void solve(Set<Materialisation> result) {
        this.solveFirstStage(result);
    }

    private void solveFirstStage(Set<Materialisation> result) {
        assert this.stage == 1;
        // Compute all solutions.
        Solution initialSol =
            new Solution(this.varsCount, this.lbRange, this.ubRange,
                this.lbEqs, this.ubEqs);
        // First iterate once over the trivial solutions.
        for (Equation eq : this.trivialEqs) {
            eq.nonRecComputeNewValues(initialSol);
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
            if (this.requiresSecondStage(mat, sol)) {
                new EquationSystem(mat, 2).solveSecondStage(result);
            } else {
                result.add(mat);
            }
        }
    }

    private void solveSecondStage(Set<Materialisation> result) {
        // todo
    }

    private void solveThirdStage(Set<Materialisation> result) {
        // todo
    }

    private boolean shouldStopEarly(Solution sol) {
        return this.stage == 1 && sol.ubEqs.isEmpty();
    }

    private void iterateSolution(Solution sol, Set<Solution> partialSols,
            Set<Solution> finishedSols) {
        this.iterateEquations(sol);
        if (sol.isFinished() || this.shouldStopEarly(sol)) {
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
    private void iterateEquations(Solution sol) {
        // For all equations, try to fix as many variables as possible.
        for (BoundType type : BoundType.values()) {
            boolean solutionModified = true;
            while (!sol.getEqs(type).isEmpty() && solutionModified) {
                THashSet<Equation> copyEqs =
                    (THashSet<Equation>) sol.getEqs(type).clone();
                solutionModified = false;
                for (Equation eq : copyEqs) {
                    solutionModified = eq.computeNewValues(sol);
                    if (solutionModified) {
                        sol.getEqs(type).remove(eq);
                    }
                }
            }
        }
    }

    private void updateMatFromSolution(Materialisation mat, Solution sol) {
        MultKind kind = this.finalMultKind();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            // Need the edge bundles here...
        }
    }

    private boolean requiresSecondStage(Materialisation mat, Solution sol) {
        return false;
    }

    private static class EdgeBundle {

        final EdgeSignature origEs;
        final Multiplicity origEsMult;
        final ShapeNode node;
        final TypeLabel label;
        final EdgeMultDir direction;
        final Set<ShapeEdge> edges;

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

        void computeAdditionalEdges(Materialisation mat) {
            Shape shape = mat.getShape();
            for (ShapeEdge edgeS : shape.binaryEdgeSet(this.node,
                this.direction)) {
                if (edgeS.label().equals(this.label)) {
                    EdgeSignature es =
                        shape.getEdgeSignature(edgeS, this.direction);
                    EdgeSignature otherOrigEs =
                        mat.getShapeMorphism().getEdgeSignature(
                            mat.getOriginalShape(), es);
                    if (otherOrigEs.equals(this.origEs)) {
                        this.edges.add(edgeS);
                    }
                }
            }
        }

    }

    private enum BoundType {
        UB, LB
    }

    private static class BoundVar {

        final int number;
        final BoundType type;

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

    private enum Relation {
        LE, GE
    }

    private class Equation {

        final BoundType type;
        final ArrayList<BoundVar> vars;
        final Relation relation;
        final int constant;
        Equation dual;

        Equation(BoundType type, Relation relation, int constant, int varsCount) {
            this.type = type;
            this.vars = new ArrayList<BoundVar>(varsCount);
            this.relation = relation;
            this.constant = constant;
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
            switch (this.relation) {
            case LE:
                result.append(" <= ");
                break;
            case GE:
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
            return result.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.constant;
            result = prime * result + this.relation.hashCode();
            result = prime * result + this.type.hashCode();
            for (BoundVar var : this.vars) {
                result = prime * result + var.number;
            }
            return result;
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
                        && this.relation == eq.relation
                        && this.vars.containsAll(eq.vars)
                        && eq.vars.containsAll(this.vars);
            }
            return result;
        }

        boolean isTrivial() {
            return this.vars.size() == 1;
        }

        void addVar(BoundVar var) {
            assert var.type == this.type;
            this.vars.add(var);
        }

        void setDual(Equation dual) {
            assert (this.type == BoundType.LB && dual.type == BoundType.UB)
                || (this.type == BoundType.UB && dual.type == BoundType.LB);
            if (dual.isUseful()) {
                this.dual = dual;
            }
        }

        boolean hasDual() {
            return this.dual != null;
        }

        boolean isUseful() {
            boolean result = false;
            switch (this.relation) {
            case LE:
                result = this.constant < this.getMaxRangeValue();
                break;
            case GE:
                result = this.constant > this.getMinRangeValue();
                break;
            default:
                assert false;
            }
            return result;
        }

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

        int getMaxRangeValue() {
            int boundRange[] = this.getBoundRange();
            return boundRange[boundRange.length - 1];
        }

        int getMinRangeValue() {
            return this.getBoundRange()[0];
        }

        boolean computeNewValues(Solution sol) {
            boolean result = this.nonRecComputeNewValues(sol);
            if (result && this.hasDual()) {
                this.dual.nonRecComputeNewValues(sol);
            }
            return result;
        }

        boolean nonRecComputeNewValues(Solution sol) {
            // EZ says: sorry for the multiple return points, but otherwise
            // the code becomes less readable...

            if (!this.hasOpenVars(sol)) {
                return true;
            }

            ArrayList<BoundVar> openVars = this.getOpenVars(sol);
            int sum = this.getFixedVarsSum(sol);
            int newConst = Multiplicity.sub(this.constant, sum);

            if (this.type == BoundType.LB && this.relation == Relation.GE) {
                if (openVars.size() == 1) {
                    // Set the lower bound to the new constant.
                    sol.cutLow(openVars.get(0), newConst);
                    return true;
                }
            }

            if (this.type == BoundType.UB && this.relation == Relation.LE) {
                if (newConst == 0) {
                    // All open variables are zero.
                    for (BoundVar openVar : openVars) {
                        sol.cutHigh(openVar, newConst);
                    }
                    return true;
                }
                if (openVars.size() == 1) {
                    // Set the upper bound to the new constant.
                    sol.cutHigh(openVars.get(0), newConst);
                    return true;
                }
                // At this point we can at least trim the upper bounds.
                for (BoundVar openVar : openVars) {
                    sol.cutHigh(openVar, newConst);
                }
                return false;
            }

            return false;
        }

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

        int getFixedVarsSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                if (sol.isSingleton(var)) {
                    result = Multiplicity.add(result, sol.getValue(var));
                }
            }
            return result;
        }

        boolean isSatisfied(Solution sol) {
            assert !this.hasOpenVars(sol);
            int sum = this.getFixedVarsSum(sol);
            boolean result = false;
            switch (this.relation) {
            case GE:
                result = sum >= this.constant;
                break;
            case LE:
                result = sum <= this.constant;
                break;
            default:
                assert false;
            }
            return result;
        }

        boolean isValidSolution(Solution sol) {
            boolean result = this.isSatisfied(sol);
            if (result && this.hasDual()) {
                result = this.dual.isSatisfied(sol);
            }
            return result;
        }

        void getNewSolutions(Solution sol, Set<Solution> partialSols,
                Set<Solution> finishedSols) {
            assert !sol.isFinished();
            assert sol.getEqs(this.type).contains(this);
            sol.getEqs(this.type).remove(this);
            ArrayList<BoundVar> openVars = this.getOpenVars(sol);
            BranchSolsIterator iter = new BranchSolsIterator(sol, openVars);
            while (iter.hasNext()) {
                Solution newSol = iter.next();
                if (this.hasDual()) {
                    this.dual.nonRecComputeNewValues(newSol);
                }
                if (this.isValidSolution(newSol)) {
                    if (newSol.isFinished()) {
                        finishedSols.add(newSol);
                    } else {
                        partialSols.add(newSol);
                    }
                }
            }
        }
    }

    private static class ValueRange implements Iterable<Integer> {

        final int range[];
        int i;
        int j;
        ValueRange dual;

        ValueRange(int range[]) {
            this.range = range;
            this.i = 0;
            this.j = range.length - 1;
        }

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

        boolean isSingleton() {
            return this.i == this.j;
        }

        int getValue() {
            assert this.isSingleton();
            return this.range[this.i];
        }

        int getMin() {
            return this.range[this.i];
        }

        int getMax() {
            return this.range[this.j];
        }

        void setDual(ValueRange dual) {
            this.dual = dual;
        }

        void nonRecCutLow(int limit) {
            while (this.getMin() < limit && this.i < this.j) {
                this.i++;
            }
        }

        void cutLow(int limit) {
            this.nonRecCutLow(limit);
            this.dual.nonRecCutLow(limit);
        }

        void nonRecCutHigh(int limit) {
            while (this.getMax() > limit && this.i < this.j) {
                this.j--;
            }
        }

        void cutHigh(int limit) {
            this.nonRecCutHigh(limit);
            this.dual.nonRecCutHigh(limit);
        }

        void nonRecFix(int i) {
            this.i = i;
            this.j = i;
        }

        void fix(int i) {
            assert i >= 0 && i < this.range.length;
            this.nonRecFix(i);
            this.dual.nonRecFix(i);
        }

        public ValueRangeIterator iterator() {
            return new ValueRangeIterator(this);
        }
    }

    private static class Solution {

        final ValueRange lbValues[];
        final ValueRange ubValues[];
        final THashSet<Equation> lbEqs;
        final THashSet<Equation> ubEqs;

        Solution(int varsCount, int lbRange[], int ubRange[],
                THashSet<Equation> lbEqs, THashSet<Equation> ubEqs) {
            this.lbValues = new ValueRange[varsCount];
            this.ubValues = new ValueRange[varsCount];
            for (int i = 0; i < varsCount; i++) {
                this.lbValues[i] = new ValueRange(lbRange);
                this.ubValues[i] = new ValueRange(ubRange);
                this.lbValues[i].setDual(this.ubValues[i]);
                this.ubValues[i].setDual(this.lbValues[i]);
            }
            this.lbEqs = (THashSet<Equation>) lbEqs.clone();
            this.ubEqs = (THashSet<Equation>) ubEqs.clone();
        }

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
            this.lbEqs = (THashSet<Equation>) original.lbEqs.clone();
            this.ubEqs = (THashSet<Equation>) original.ubEqs.clone();
        }

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

        THashSet<Equation> getEqs(BoundType type) {
            THashSet<Equation> result = null;
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

        int size() {
            return this.lbValues.length;
        }

        ValueRange getValueRange(BoundVar var) {
            return this.getValueRangeArray(var.type)[var.number];
        }

        boolean isSingleton(BoundVar var) {
            return this.getValueRange(var).isSingleton();
        }

        int getValue(BoundVar var) {
            assert this.isSingleton(var);
            return this.getValueRange(var).getValue();
        }

        void cutLow(BoundVar var, int limit) {
            this.getValueRange(var).cutLow(limit);
        }

        void cutHigh(BoundVar var, int limit) {
            this.getValueRange(var).cutHigh(limit);
        }

        void fix(BoundVar var, int i) {
            this.getValueRange(var).fix(i);
        }

        boolean isFinished() {
            return this.lbEqs.isEmpty() && this.ubEqs.isEmpty();
        }

        Equation getBestBranchingEquation() {
            Equation result = null;
            for (Equation eq : this.ubEqs) {
                if (result == null
                    || (eq.getOpenVars(this).size() < result.getOpenVars(this).size())) {
                    result = eq;
                }
            }
            if (result == null) {
                for (Equation eq : this.lbEqs) {
                    if (result == null
                        || (eq.getOpenVars(this).size() < result.getOpenVars(
                            this).size())) {
                        result = eq;
                    }
                }
            }
            return result;
        }

        Multiplicity getMultValue(int varNum, MultKind kind) {
            int i = this.lbValues[varNum].getMin();
            int j = this.ubValues[varNum].getMax();
            return Multiplicity.getMultiplicity(i, j, kind);
        }

    }

    private static class ValueRangeIterator implements Iterator<Integer> {

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

    private static class BranchSolsIterator implements Iterator<Solution> {

        final Solution sol;
        final ArrayList<BoundVar> openVars;
        final ValueRangeIterator iters[];

        BranchSolsIterator(Solution sol, ArrayList<BoundVar> openVars) {
            assert openVars.size() > 0;
            this.sol = sol;
            this.openVars = openVars;
            this.iters = new ValueRangeIterator[openVars.size()];
            int i = 0;
            for (BoundVar var : this.openVars) {
                this.iters[i] = sol.getValueRange(var).iterator();
                i++;
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(this.iters);
        }

        @Override
        public boolean hasNext() {
            return this.iters[0].hasNext();
        }

        @Override
        public Solution next() {
            // Create a new solution.
            Solution newSolution = this.sol.clone();
            int i = 0;
            for (ValueRangeIterator iter : this.iters) {
                newSolution.fix(this.openVars.get(i), iter.current());
                i++;
            }
            // Update iterators and compute next solution.
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
                    for (i = prev + 1; i <= curr; i++) {
                        this.iters[i].reset();
                    }
                }
            }
            return newSolution;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
