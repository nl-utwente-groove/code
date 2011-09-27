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
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
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

    private static final boolean WARN_BLOWUP = false;
    private static final int MAX_SOLUTION_COUNT = 4;

    /** EDUARDO: Comment this... */
    public final static EquationSystem newEqSys(Materialisation mat) {
        assert mat.getStage() == 1;
        return new EquationSystem(mat);
    }

    private final Materialisation mat;
    private final int stage;
    private final THashSet<Equation> trivialEqs;
    private final THashSet<Equation> lbEqs;
    private final THashSet<Equation> ubEqs;
    private int lbRange[];
    private int ubRange[];
    private int varsCount;
    // ------------------------------------------------------------------------
    // Used in first stage.
    // ------------------------------------------------------------------------
    private Map<ShapeEdge,Duo<BoundVar>> edgeVarsMap;
    private ArrayList<ShapeEdge> varEdgeMap;
    private Set<EdgeBundle> bundles;
    // ------------------------------------------------------------------------
    // Used in second stage.
    // ------------------------------------------------------------------------
    private Map<EdgeSignature,Duo<BoundVar>> outEsVarsMap;
    private Map<EdgeSignature,Duo<BoundVar>> inEsVarsMap;
    private ArrayList<Pair<EdgeSignature,EdgeMultDir>> varEsMap;
    // ------------------------------------------------------------------------
    // Used in third stage.
    // ------------------------------------------------------------------------
    private Map<ShapeNode,Duo<BoundVar>> nodeVarsMap;
    private ArrayList<ShapeNode> varNodeMap;

    private EquationSystem(Materialisation mat) {
        assert mat != null;
        this.mat = mat;
        this.stage = mat.getStage();
        this.trivialEqs = new THashSet<Equation>();
        this.lbEqs = new THashSet<Equation>();
        this.ubEqs = new THashSet<Equation>();
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

    /** EDUARDO: Comment this... */
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
        if (WARN_BLOWUP && finishedSolsSize > MAX_SOLUTION_COUNT) {
            System.out.println("Warning! Blowup while solving equation system: "
                + finishedSolsSize + " solutions.");
            System.out.println(this);
            System.out.println(this.mat);
        }
        // Create the return objects.
        for (Solution sol : finishedSols) {
            Materialisation mat;
            if (finishedSols.size() == 1) {
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

    private boolean shouldStopEarly(Solution sol) {
        return this.stage == 1 && sol.ubEqs.isEmpty();
    }

    private void iterateSolution(Solution sol, SolutionSet partialSols,
            SolutionSet finishedSols) {
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
        boolean solutionModified = true;
        boolean removeEq;
        while (solutionModified) {
            solutionModified = false;
            for (BoundType type : BoundType.values()) {
                THashSet<Equation> copyEqs =
                    (THashSet<Equation>) sol.getEqs(type).clone();
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

    private Duo<Equation> createEquations(int varsCount) {
        Equation lbEq = new Equation(BoundType.LB, varsCount);
        Equation ubEq = new Equation(BoundType.UB, varsCount);
        return new Duo<Equation>(lbEq, ubEq);
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

    private boolean hasTrivialEqs() {
        return this.trivialEqs.size() > 0;
    }

    private boolean hasNonTrivialEqs() {
        return this.lbEqs.size() > 0 || this.ubEqs.size() > 0;
    }

    // ------------------------------------------------------------------------
    // Methods for first stage.
    // ------------------------------------------------------------------------

    private void createFirstStage() {
        assert this.stage == 1;

        this.edgeVarsMap = new THashMap<ShapeEdge,Duo<BoundVar>>();
        this.varEdgeMap = new ArrayList<ShapeEdge>();
        this.bundles = new THashSet<EdgeBundle>();
        Shape shape = this.mat.getShape();

        // Create the edge bundles.
        for (ShapeEdge edge : this.mat.getAffectedEdges()) {
            this.addToEdgeBundle(edge.source(), edge, OUTGOING);
            this.addToEdgeBundle(edge.target(), edge, INCOMING);
        }

        // For each bundle...
        for (EdgeBundle bundle : this.bundles) {
            bundle.computeAdditionalEdges(this.mat);
            int varsCount = bundle.edges.size();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // ... create a pair of equations.
            Duo<Equation> eqs =
                this.createEquations(varsCount, constMult.getLowerBound(),
                    constMult.getUpperBound());
            // For each edge in the bundle...
            for (ShapeEdge edge : bundle.edges) {
                // ... create two bound variables.
                Duo<BoundVar> vars = retrieveBoundVars(edge);
                addVars(eqs, vars);
                if (bundle.direction == OUTGOING) {
                    Duo<Equation> trivialEqs = null;
                    if (this.mat.isFixed(edge)) {
                        // Create additional equations for the fixed edges.
                        trivialEqs = this.createEquations(1, 1, 1);
                    } else if (shape.areNodesConcrete(edge)) {
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

    private void addToEdgeBundle(ShapeNode node, ShapeEdge edge,
            EdgeMultDir direction) {
        assert this.stage == 1;
        EdgeBundle result = null;
        TypeLabel label = edge.label();

        Shape origShape = this.mat.getOriginalShape();
        EdgeSignature origEs =
            this.mat.getShapeMorphism().getEdgeSignature(origShape,
                this.mat.getShape().getEdgeSignature(edge, direction));

        for (EdgeBundle bundle : this.bundles) {
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
            result =
                new EdgeBundle(origEs, origEsMult, node, label, direction,
                    false);
            this.bundles.add(result);
        }

        result.edges.add(edge);
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

        boolean requiresSecondStage = false;
        Shape shape = mat.getShape();
        MultKind kind = this.finalMultKind();

        // First, check if we need to split nodes.
        THashSet<EdgeBundle> nonSingBundles = new THashSet<EdgeBundle>();
        Set<ShapeEdge> edgesNotToInclude = new THashSet<ShapeEdge>();
        for (EdgeBundle bundle : this.bundles) {
            if (bundle.isNonSingular(this, shape, kind, sol)) {
                nonSingBundles.add(bundle);
                edgesNotToInclude.addAll(bundle.positivePossibleEdges);
                requiresSecondStage = true;
            }
        }

        // Then, update the shape as much as possible.
        ShapeMorphism morph = mat.getShapeMorphism();
        for (int i = 0; i < this.varsCount; i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            ShapeEdge edge = this.varEdgeMap.get(i);

            if (mult.isZero()) {
                if (shape.containsEdge(edge)) {
                    shape.removeEdge(edge);
                }
                morph.removeEdge(edge);
                continue;
            }

            if (edgesNotToInclude.contains(edge)) {
                continue;
            }

            // Multiplicity is not zero.
            // Make sure the edge is in the shape.
            if (!shape.containsEdge(edge)) {
                shape.addEdge(edge);
            }
            boolean fixed = true;
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                if (shape.getNodeMult(edge.incident(direction)).isOne()) {
                    shape.setEdgeMult(edge, direction, mult);
                } else {
                    fixed = false;
                }
            }
            if (fixed && mult.isOne()) {
                this.mat.setFixedOnFirstStage(edge);
            }
        }

        if (requiresSecondStage) {
            mat.setNonSingBundles(nonSingBundles);
            mat.moveToSecondStage();
        }

        return requiresSecondStage;
    }

    // ------------------------------------------------------------------------
    // Methods for second stage.
    // ------------------------------------------------------------------------

    private void createSecondStage() {
        assert this.stage == 2;

        this.outEsVarsMap = new THashMap<EdgeSignature,Duo<BoundVar>>();
        this.inEsVarsMap = new THashMap<EdgeSignature,Duo<BoundVar>>();
        this.varEsMap = new ArrayList<Pair<EdgeSignature,EdgeMultDir>>();
        Multiplicity one =
            Multiplicity.getMultiplicity(1, 1, MultKind.EDGE_MULT);

        // For each split bundle...
        for (EdgeBundle bundle : this.mat.getSplitBundles()) {
            bundle.computeAdditionalEdges(this.mat);
            EdgeMultDir direction = bundle.direction;
            // ... create one pair of equations.
            int esCount = bundle.splitEs.size();
            Duo<Equation> eqs = this.createEquations(esCount);
            // For each edge signature...
            for (EdgeSignature es : bundle.splitEs) {
                // ... create a pair of variables.
                Duo<BoundVar> vars = retrieveBoundVars(es, direction);
                addVars(eqs, vars);
                Multiplicity constMult = bundle.origEsMult;
                for (ShapeEdge edge : bundle.edges) {
                    // Adjust the constant according to the fixed edges.
                    if (this.mat.isFixedOnFirstStage(edge)) {
                        constMult = constMult.sub(one);
                    }
                }
                eqs.one().setConstant(constMult.getLowerBound());
                eqs.two().setConstant(constMult.getUpperBound());
            }
            this.storeEquations(eqs);
        }

        Shape shape = this.mat.getShape();

        // Optimization: sum of opposite variables.
        for (EdgeBundle bundle : this.mat.getSplitBundles()) {
            if (bundle.splitEs.size() > 1
                || !shape.getNodeMult(bundle.node).isOne()) {
                continue;
            }
            EdgeMultDir direction = bundle.direction;
            EdgeMultDir reverse = direction.reverse();
            EdgeSignature es = bundle.splitEs.iterator().next();
            Set<ShapeEdge> esEdges = shape.getEdgesFromSig(es, direction);
            if (!esEdges.containsAll(bundle.edges)) {
                continue;
            }
            Duo<Equation> oppVarsSum =
                this.createEquations(esEdges.size(),
                    bundle.origEsMult.getLowerBound(),
                    bundle.origEsMult.getUpperBound());
            for (ShapeEdge edge : esEdges) {
                EdgeSignature oppEs = shape.getEdgeSignature(edge, reverse);
                if (shape.isEdgeSigUnique(oppEs, reverse)) {
                    Duo<BoundVar> oppVars = this.getEsMap(reverse).get(oppEs);
                    addVars(oppVarsSum, oppVars);
                }
            }
            this.storeEquations(oppVarsSum);
        }

        // Optimization: opposite nodes are concrete.
        for (int i = 0; i < this.varsCount; i++) {
            Pair<EdgeSignature,EdgeMultDir> pair = this.varEsMap.get(i);
            EdgeSignature es = pair.one();
            EdgeMultDir direction = pair.two();
            EdgeMultDir reverse = direction.reverse();
            Set<ShapeEdge> esEdges = shape.getEdgesFromSig(es, direction);
            if (esEdges.size() == 1) {
                ShapeEdge edge = esEdges.iterator().next();
                // Check if the opposite is a concrete node.
                ShapeNode opp = edge.opposite(direction);
                if (shape.getNodeMult(opp).isOne()) {
                    // We have trivial equations.
                    int lb;
                    EdgeSignature oppEs = shape.getEdgeSignature(edge, reverse);
                    BoundVar ubOppVar = this.getEsMap(reverse).get(oppEs).two();
                    if (!this.occursInTrivialUbEq(ubOppVar)) {
                        lb = 1;
                    } else {
                        lb = 0;
                    }
                    Duo<Equation> trivialEqs = this.createEquations(1, lb, 1);
                    Duo<BoundVar> vars = this.getEsMap(direction).get(es);
                    addVars(trivialEqs, vars);
                    this.storeEquations(trivialEqs);
                }
            }
        }
    }

    private boolean occursInTrivialUbEq(BoundVar ubVar) {
        assert this.stage == 2;
        assert ubVar.type == BoundType.UB;
        boolean result = false;
        for (Equation eq : this.trivialEqs) {
            if (eq.vars.contains(ubVar)) {
                result = true;
                break;
            }
        }
        return result;
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
            this.varEsMap.add(this.varsCount,
                new Pair<EdgeSignature,EdgeMultDir>(es, direction));
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
            Pair<EdgeSignature,EdgeMultDir> pair = this.varEsMap.get(i);
            EdgeSignature es = pair.one();
            EdgeMultDir direction = pair.two();
            if (shape.getEdgeMultMapKeys(direction).contains(es)) {
                shape.setEdgeSigMult(es, direction, mult);
            } // else, the signature was removed from the shape because the
              // opposite variable is zero. Don't set the multiplicity here
              // otherwise we would end up with a spurious entry in the
              // multiplicity maps of the shape.
        }
        mat.moveToThirdStage();
        return true;
    }

    // ------------------------------------------------------------------------
    // Methods for third stage.
    // ------------------------------------------------------------------------

    private void createThirdStage() {
        assert this.stage == 3;

        this.nodeVarsMap = new THashMap<ShapeNode,Duo<BoundVar>>();
        this.varNodeMap = new ArrayList<ShapeNode>();

        Shape shape = this.mat.getShape();

        Map<ShapeNode,Set<ShapeNode>> nodeSplitMap = this.mat.getNodeSplitMap();
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
            Duo<BoundVar> vars = retrieveBoundVars(origNode);
            addVars(eqs, vars);
            for (ShapeNode splitNode : splitNodes) {
                // ... create one pair of variables for each of the split nodes.
                vars = retrieveBoundVars(splitNode);
                addVars(eqs, vars);
            }
            this.storeEquations(eqs);
        }

        assert !this.hasTrivialEqs();

        // Optimization: sum of concrete opposite nodes and edges.
        for (ShapeNode splitNode : this.nodeVarsMap.keySet()) {
            bundleLoop: for (EdgeBundle splitBundle : this.mat.getSplitBundles(splitNode)) {
                EdgeMultDir direction = splitBundle.direction;
                int concreteEdgeCount = 0;
                edgeLoop: for (ShapeEdge edge : splitBundle.edges) {
                    if (!shape.containsEdge(edge)) {
                        continue edgeLoop;
                    }
                    ShapeNode opp = edge.opposite(direction);
                    if (shape.getNodeMult(opp).isOne()
                        && shape.isEdgeConcrete(edge)) {
                        concreteEdgeCount++;
                    }
                }
                if (concreteEdgeCount == 1) {
                    Duo<Equation> trivialEqs = this.createEquations(1, 1, 1);
                    Duo<BoundVar> vars = this.nodeVarsMap.get(splitNode);
                    addVars(trivialEqs, vars);
                    this.storeEquations(trivialEqs);
                    // We're done with this node.
                    break bundleLoop;
                }
            }
        }

        if (!this.hasNonTrivialEqs()) {
            // There are no non-trivial equations. There's nothing left to do.
            // All variables will receive the most general multiplicity value.
            // The extra steps below are optimizations that only make sense
            // when we already have equations. Otherwise, the optimizations
            // only cause unnecessary branching in the equation system.
            return;
        }

        // Optimization: sum of opposite nodes for concrete nodes.
        // For each concrete node in the shape...
        outerLoop: for (ShapeNode node : shape.nodeSet()) {
            if (!shape.getNodeMult(node).isOne()) {
                continue outerLoop;
            }
            for (EdgeBundle splitBundle : this.mat.getSplitBundles(node)) {
                EdgeMultDir direction = splitBundle.direction;
                for (EdgeSignature splitEs : splitBundle.splitEs) {
                    // We may have another equation.
                    Multiplicity constMult =
                        shape.getEdgeSigMult(splitEs, direction);
                    int maxEdgesCount = splitBundle.edges.size();
                    Duo<Equation> oppEqs =
                        this.createEquations(maxEdgesCount,
                            constMult.getLowerBound(),
                            constMult.getUpperBound());
                    innerLoop: for (ShapeEdge edge : shape.getEdgesFromSig(
                        splitEs, direction)) {
                        ShapeNode opposite = edge.opposite(direction);
                        Duo<BoundVar> vars = this.nodeVarsMap.get(opposite);
                        if (vars == null) {
                            continue innerLoop;
                        } // else vars != null.
                        EdgeSignature oppEs =
                            shape.getEdgeSignature(opposite, edge.label(),
                                shape.getEquivClassOf(node));
                        if (shape.isEdgeSigConcrete(oppEs, direction.reverse())) {
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

    // ----------
    // EdgeBundle
    // ----------

    /** EDUARDO: Comment this... */
    public static class EdgeBundle {

        final EdgeSignature origEs;
        final Multiplicity origEsMult;
        final ShapeNode node;
        final TypeLabel label;
        final EdgeMultDir direction;
        final Set<ShapeEdge> edges;
        Set<ShapeEdge> positivePossibleEdges;
        Set<EdgeSignature> splitEs;

        EdgeBundle(EdgeSignature origEs, Multiplicity origEsMult,
                ShapeNode node, TypeLabel label, EdgeMultDir direction,
                boolean forSecondStage) {
            this.origEs = origEs;
            this.origEsMult = origEsMult;
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.edges = new THashSet<ShapeEdge>();
            if (forSecondStage) {
                this.splitEs = new THashSet<EdgeSignature>();
            }
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

        boolean isNonSingular(EquationSystem eqSys, Shape shape, MultKind kind,
                Solution sol) {
            this.positivePossibleEdges = new THashSet<ShapeEdge>();
            EquivRelation<ShapeNode> er = new EquivRelation<ShapeNode>();
            if (shape.getNodeMult(this.node).isCollector()) {
                for (ShapeEdge edge : this.edges) {
                    int varNum = eqSys.retrieveBoundVars(edge).one().number;
                    boolean positive = !sol.getMultValue(varNum, kind).isZero();
                    if (positive) {
                        er.add(shape.getEquivClassOf(edge.opposite(this.direction)));
                        if (!shape.containsEdge(edge)) {
                            this.positivePossibleEdges.add(edge);
                        }
                    }
                }
            }
            return er.size() > 1;
        }
    }

    // ---------
    // BoundType
    // ---------

    private enum BoundType {
        UB, LB
    }

    // --------
    // BoundVar
    // --------

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

    // --------
    // Equation
    // --------

    private class Equation {

        final BoundType type;
        final ArrayList<BoundVar> vars;
        int constant;
        Equation dual;

        Equation(BoundType type, int varsCount) {
            this.type = type;
            this.vars = new ArrayList<BoundVar>(varsCount);
        }

        Equation(BoundType type, int varsCount, int constant) {
            this(type, varsCount);
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

        void setConstant(int constant) {
            this.constant = constant;
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
                if (openVarsMaxSum == this.constant) {
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
    }

    // --------
    // Solution
    // --------

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

    private static class SolutionSet extends THashSet<Solution> {
        @Override
        public boolean add(Solution newSol) {
            boolean storeNew = true;
            THashSet<Solution> toRemove = null;
            for (Solution oldSol : this) {
                if (oldSol.subsumes(newSol)) {
                    storeNew = false;
                    break;
                } else if (newSol.subsumes(oldSol)) {
                    if (toRemove == null) {
                        toRemove = new THashSet<Solution>(this.size());
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
