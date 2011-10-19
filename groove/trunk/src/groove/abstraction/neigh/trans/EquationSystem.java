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
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    private static final boolean WARN_BLOWUP = false;
    private static final int MAX_SOLUTION_COUNT = 4;

    /** Creates a new equation system for the given materialisation. */
    public final static EquationSystem newEqSys(Materialisation mat) {
        assert mat.getStage() == 1;
        return new EquationSystem(mat);
    }

    private final Materialisation mat;
    private final int stage;
    private final MyHashSet<Equation> trivialEqs;
    private final MyHashSet<Equation> lbEqs;
    private final MyHashSet<Equation> ubEqs;
    private int lbRange[];
    private int ubRange[];
    private int varsCount;
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
    // Common methods to all stages.
    // ------------------------------------------------------------------------

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

    /** Solves the equation system and stores the results in the given set. */
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
            if (!this.isValid(sol)) {
                continue;
            }
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

    private Solution tryToMaxSolution(Solution sol) {
        Solution result = sol;
        if (this.stage == 1 && sol.ubEqs.isEmpty()) {
            result = sol.clone();
            result.setAllVarsToMax(this.edgeVarsMap.values());
        }
        return result;
    }

    private void iterateSolution(Solution sol, SolutionSet partialSols,
            SolutionSet finishedSols) {
        this.iterateEquations(sol);
        if (sol.isFinished()) {
            finishedSols.add(sol);
        } else {
            // Check if we're in first stage and we can stop early.
            Solution maxSol = this.tryToMaxSolution(sol);
            if (maxSol != sol) { // Reference inequality is sufficient.
                // Yes, we can stop. Store the max'ed solution.
                finishedSols.add(maxSol);
            } else {
                // No escape, we have to branch.
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

    private Duo<Equation> createEquations(int varsCount, int lbConst,
            int ubConst) {
        Equation lbEq = new Equation(BoundType.LB, varsCount, lbConst);
        Equation ubEq = new Equation(BoundType.UB, varsCount, ubConst);
        return new Duo<Equation>(lbEq, ubEq);
    }

    private static void addVars(Duo<Equation> eqs, Duo<BoundVar> vars) {
        eqs.one().addVar(vars.one());
        eqs.two().addVar(vars.two());
    }

    private boolean isValid(Solution sol) {
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

    private void createFirstStage() {
        assert this.stage == 1;

        this.edgeVarsMap = new MyHashMap<ShapeEdge,Duo<BoundVar>>();
        this.varEdgeMap = new ArrayList<ShapeEdge>();
        Shape shape = this.mat.getShape();

        // General case:
        // For each bundle...
        for (EdgeBundle bundle : this.mat.getBundles()) {
            int varsCount = bundle.getEdgesCount();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // ... create a pair of equations.
            Duo<Equation> eqs =
                this.createEquations(varsCount, constMult.getLowerBound(),
                    constMult.getUpperBound());
            // For each split edge signature...
            for (EdgeSignature splitEs : bundle.getSplitEsSet()) {
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

        // Update the bundles and check for non-singular ones.
        MyHashSet<EdgeBundle> nonSingBundles = new MyHashSet<EdgeBundle>();
        Set<ShapeEdge> nonSingEdges = new MyHashSet<ShapeEdge>();
        for (EdgeBundle bundle : mat.getBundles()) {
            bundle.updateFromSolution(shape, zeroEdges, positiveEdges);
            if (bundle.isNonSingular()
                && shape.getNodeMult(bundle.node).isCollector()) {
                nonSingBundles.add(bundle);
                nonSingEdges.addAll(bundle.possibleEdges);
            }
            /*if (bundle.isEmpty()) {
                
            }*/
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

    // ------------------------------------------------------------------------
    // Methods for second stage.
    // ------------------------------------------------------------------------

    private void createSecondStage() {
        assert this.stage == 2;

        this.outEsVarsMap = new MyHashMap<EdgeSignature,Duo<BoundVar>>();
        this.inEsVarsMap = new MyHashMap<EdgeSignature,Duo<BoundVar>>();
        this.varEsMap = new ArrayList<EdgeSignature>();

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
                    Duo<BoundVar> vars =
                        retrieveBoundVars(es, bundle.direction);
                    addVars(eqs, vars);
                }
                this.storeEquations(eqs);
            }
        }

        Shape shape = this.mat.getShape();

        // Optimization 1:
        // Opposite nodes are concrete.
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            Map<EdgeSignature,Duo<BoundVar>> esMap = this.getEsMap(direction);
            for (EdgeSignature es : esMap.keySet()) {
                if (shape.isEdgeSigUnique(es)) {
                    ShapeEdge edge =
                        shape.getEdgesFromSig(es).iterator().next();
                    ShapeNode opposite = edge.opposite(direction);
                    if (shape.getNodeMult(opposite).isOne()) {
                        Duo<Equation> trivialEqs =
                            this.createEquations(1, 1, 1);
                        Duo<BoundVar> vars = esMap.get(es);
                        addVars(trivialEqs, vars);
                        this.storeEquations(trivialEqs);
                    }
                }
            }
        }
    }

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

    private Duo<BoundVar> retrieveBoundVars(EdgeSignature es,
            EdgeMultDir direction) {
        assert this.stage == 2;
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

    private boolean updateMatSecondStage(Materialisation mat, Solution sol) {
        assert this.stage == 2;
        Shape shape = mat.getShape();
        MultKind kind = this.finalMultKind();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            assert !mult.isZero() && (mult.isSingleton() || mult.isCollector());
            EdgeSignature es = this.varEsMap.get(i);
            shape.setEdgeSigMult(es, mult);
        }
        mat.markGarbageNodes();
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

    private void createThirdStage() {
        assert this.stage == 3;

        this.nodeVarsMap = new MyHashMap<ShapeNode,Duo<BoundVar>>();
        this.varNodeMap = new ArrayList<ShapeNode>();

        Shape shape = this.mat.getShape();
        Map<ShapeNode,Set<ShapeNode>> nodeSplitMap = this.mat.getNodeSplitMap();

        // General case:
        // For each split node...
        for (ShapeNode origNode : nodeSplitMap.keySet()) {
            Multiplicity origMult = shape.getNodeMult(origNode);
            Set<ShapeNode> splitNodes = nodeSplitMap.get(origNode);
            int varsCount = splitNodes.size() + 1;
            // ... create one pair of equations.
            Duo<Equation> eqs =
                this.createEquations(varsCount, origMult.getLowerBound(),
                    origMult.getUpperBound());
            // ... create one pair of variables for the original node.
            Duo<BoundVar> vars;
            if (!this.mat.isGarbage(origNode)) {
                vars = retrieveBoundVars(origNode);
                addVars(eqs, vars);
            }
            for (ShapeNode splitNode : splitNodes) {
                // ... create one pair of variables for each of the split nodes.
                if (!this.mat.isGarbage(splitNode)) {
                    vars = retrieveBoundVars(splitNode);
                    addVars(eqs, vars);
                }
            }
            this.storeEquations(eqs);
        }

        // Optimization 1:
        // Sum of opposite nodes for concrete nodes.
        outerLoop: for (ShapeNode node : shape.nodeSet()) {
            if (!shape.getNodeMult(node).isOne()) {
                continue outerLoop;
            }
            for (EdgeBundle bundle : this.mat.getBundles(node)) {
                EdgeMultDir direction = bundle.direction;
                for (EdgeSignature splitEs : bundle.getSplitEsSet()) {
                    // We may have another equation.
                    Multiplicity constMult =
                        shape.getEdgeSigMult(splitEs).toNodeKind();
                    int maxEdgesCount = bundle.getEdges().size();
                    Duo<Equation> oppEqs =
                        this.createEquations(maxEdgesCount,
                            constMult.getLowerBound(),
                            constMult.getUpperBound());
                    innerLoop: for (ShapeEdge edge : shape.getEdgesFromSig(splitEs)) {
                        ShapeNode opposite = edge.opposite(direction);
                        Duo<BoundVar> vars = this.nodeVarsMap.get(opposite);
                        if (vars == null) {
                            continue innerLoop;
                        } // else vars != null.
                        EdgeSignature oppEs =
                            shape.getEdgeSignature(edge, direction.reverse());
                        if (shape.isEdgeSigConcrete(oppEs)) {
                            addVars(oppEqs, vars);
                        }
                    }
                    this.storeEquations(oppEqs);
                }
            }
        }

    }

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

    private enum BoundType {
        UB, LB
    }

    // --------
    // BoundVar
    // --------

    static class BoundVar {

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

    // --------
    // Equation
    // --------

    private class Equation {

        final BoundType type;
        final ArrayList<BoundVar> vars;
        final int constant;
        Equation dual;

        Equation(BoundType type, int varsCount, int constant) {
            this.type = type;
            this.vars = new ArrayList<BoundVar>(varsCount);
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
            return result.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.constant;
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
            switch (this.type) {
            case UB:
                result = this.constant < this.getMaxRangeValue();
                break;
            case LB:
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
            int fixedVarsSum = this.getFixedVarsSum(sol);
            int newConst = Multiplicity.sub(this.constant, fixedVarsSum);

            if (this.type == BoundType.LB) {
                if (openVars.size() == 1) {
                    // Set the lower bound to the new constant.
                    sol.cutLow(openVars.get(0), newConst);
                    return true;
                }
                // Check if the max sum equals the constant.
                int openVarsMaxSum = this.getOpenVarsMaxSum(sol);
                if (openVarsMaxSum == newConst) {
                    // We can set all the open variables to their maximum.
                    for (BoundVar openVar : openVars) {
                        sol.cutLow(openVar, sol.getMaxValue(openVar));
                    }
                }
                return false;
            }

            if (this.type == BoundType.UB) {
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

        int getVarsSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                result = Multiplicity.add(result, sol.getValue(var));
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

        int getOpenVarsMaxSum(Solution sol) {
            int result = 0;
            for (BoundVar var : this.vars) {
                if (!sol.isSingleton(var)) {
                    result += sol.getMaxValue(var);
                }
            }
            return result;
        }

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

        boolean isValidSolution(Solution sol) {
            boolean result = this.isSatisfied(sol);
            if (result && this.hasDual()) {
                result = this.dual.isSatisfied(sol);
            }
            return result;
        }

        void getNewSolutions(Solution sol, SolutionSet partialSols,
                SolutionSet finishedSols) {
            assert !sol.isFinished();
            assert sol.getEqs(this.type).contains(this);

            sol.getEqs(this.type).remove(this);

            if (this.isSatisfied(sol)) {
                // Nothing to do.
                if (sol.isFinished()) {
                    finishedSols.add(sol);
                } else {
                    partialSols.add(sol);
                }
            } else {
                // Iterate the solutions.
                ArrayList<BoundVar> openVars = this.getOpenVars(sol);
                BranchSolsIterator iter =
                    new BranchSolsIterator(this, sol, openVars);
                while (iter.hasNext()) {
                    Solution newSol = iter.next();
                    if (newSol.isFinished()) {
                        finishedSols.add(newSol);
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

        public ValueRangeIterator iterator() {
            return new ValueRangeIterator(this);
        }

        boolean isZeroOne() {
            return this.getMin() == 0 && this.getMax() == 1;
        }
    }

    // --------
    // Solution
    // --------

    static class Solution {

        final ValueRange lbValues[];
        final ValueRange ubValues[];
        final MyHashSet<Equation> lbEqs;
        final MyHashSet<Equation> ubEqs;

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

        int getMaxValue(BoundVar var) {
            return this.getValueRange(var).getMax();
        }

        void cutLow(BoundVar var, int limit) {
            this.getValueRange(var).cutLow(limit);
        }

        void cutHigh(BoundVar var, int limit) {
            this.getValueRange(var).cutHigh(limit);
        }

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
            return Multiplicity.approx(i, j, kind);
        }

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

        void projectToZeroWhenNeeded(BoundVar var) {
            ValueRange range = this.getValueRange(var);
            if (range.isZeroOne()) {
                range.cutHigh(0);
            }
        }

        void setAllVarsToMax(Collection<Duo<BoundVar>> vars) {
            for (Duo<BoundVar> pair : vars) {
                BoundVar lbVar = pair.one();
                int max = this.getMaxValue(lbVar);
                this.cutLow(lbVar, max);
            }
        }
    }

    // ------------------
    // ValueRangeIterator
    // ------------------

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

    // ------------------
    // BranchSolsIterator
    // ------------------

    private static class BranchSolsIterator implements Iterator<Solution> {

        final Equation eq;
        final Solution sol;
        final ArrayList<BoundVar> openVars;
        final ValueRangeIterator iters[];
        final ArrayList<Solution> validSolutions;
        int next;

        BranchSolsIterator(Equation eq, Solution sol,
                ArrayList<BoundVar> openVars) {
            assert openVars.size() > 0;
            this.eq = eq;
            this.sol = sol;
            this.openVars = openVars;
            this.iters = new ValueRangeIterator[openVars.size()];
            int i = 0;
            for (BoundVar var : this.openVars) {
                this.iters[i] = sol.getValueRange(var).iterator();
                i++;
            }
            this.validSolutions = new ArrayList<Solution>();
            this.next = 0;
            this.computeAllSolutions();
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

        void computeAllSolutions() {
            while (this.iters[0].hasNext()) {
                // Create a new solution.
                Solution newSol = this.sol.clone();
                int i = 0;
                for (ValueRangeIterator iter : this.iters) {
                    newSol.cut(this.openVars.get(i), iter.current());
                    i++;
                }
                if (this.eq.hasDual()) {
                    this.eq.dual.nonRecComputeNewValues(newSol);
                } else if (this.eq.type == BoundType.LB) {
                    // Variables with zero or one values have to be handled
                    // in a special way.
                    for (BoundVar openVar : this.openVars) {
                        newSol.projectToZeroWhenNeeded(openVar);
                    }
                }
                if (this.eq.isValidSolution(newSol)) {
                    this.validSolutions.add(newSol);
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
            }
        }
    }

    // -----------
    // SolutionSet
    // -----------

    private static class SolutionSet extends MyHashSet<Solution> {
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
