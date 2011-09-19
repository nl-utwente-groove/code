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

    /** EDUARDO: Comment this... */
    public final static EquationSystem newEqSys(Materialisation mat) {
        assert mat.getStage() == 1;
        return new EquationSystem(mat);
    }

    private final Materialisation mat;
    private final int stage;
    private final Set<Equation> trivialEqs;
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
    private Map<EdgeSignature,Duo<BoundVar>> esVarsMap;
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
                Duo<BoundVar> varPair = retrieveBoundVars(edge);
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
        Shape origShape = mat.getOriginalShape();
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
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                boolean nodeMultIsOne =
                    shape.getNodeMult(edge.incident(direction)).isOne();
                Multiplicity edgeMult;
                if (nodeMultIsOne) {
                    edgeMult = mult;
                } else {
                    edgeMult =
                        origShape.getEdgeMult(morph.getEdge(edge), direction);
                }
                shape.setEdgeMult(edge, direction, edgeMult);
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

        this.esVarsMap = new THashMap<EdgeSignature,Duo<BoundVar>>();
        this.varEsMap = new ArrayList<Pair<EdgeSignature,EdgeMultDir>>();
        Multiplicity one =
            Multiplicity.getMultiplicity(1, 1, MultKind.EDGE_MULT);

        // For each bundle...
        for (EdgeBundle bundle : this.mat.getSplitBundles()) {
            bundle.computeAdditionalEdges(this.mat);
            EdgeMultDir direction = bundle.direction;
            // ... create one lower bound and one upper bound equation.
            Equation lbEq = null;
            Equation ubEq = null;
            int esCount = bundle.splitEs.size();
            if (esCount > 1) {
                lbEq =
                    new Equation(BoundType.LB, Relation.GE,
                        bundle.origEsMult.getLowerBound(), esCount);
                ubEq =
                    new Equation(BoundType.UB, Relation.LE,
                        bundle.origEsMult.getUpperBound(), esCount);
            }
            // For each edge signature...
            for (EdgeSignature es : bundle.splitEs) {
                // ... create two bound variables.
                Duo<BoundVar> varPair = retrieveBoundVars(es, direction);
                BoundVar lbVar = varPair.one();
                BoundVar ubVar = varPair.two();
                if (esCount > 1) {
                    // We have a non-trivial equation. Add the variables.
                    lbEq.addVar(lbVar);
                    ubEq.addVar(ubVar);
                } else {
                    // We have just a trivial equation.
                    Multiplicity constMult = bundle.origEsMult;
                    for (ShapeEdge edge : bundle.edges) {
                        if (this.mat.isFixed(edge)) {
                            constMult = constMult.sub(one);
                        }
                    }
                    // Create one lower bound and one upper bound trivial equation.
                    Equation trivialLbEq =
                        new Equation(BoundType.LB, Relation.GE,
                            constMult.getLowerBound(), 1);
                    Equation trivialUbEq =
                        new Equation(BoundType.UB, Relation.LE,
                            constMult.getUpperBound(), 1);
                    trivialLbEq.addVar(lbVar);
                    trivialUbEq.addVar(ubVar);
                    this.storeEquations(trivialLbEq, trivialUbEq);
                }
            }
            if (esCount > 1) {
                this.storeEquations(lbEq, ubEq);
            }
        }

        Shape shape = this.mat.getShape();
        for (int i = 0; i < this.varsCount; i++) {
            Pair<EdgeSignature,EdgeMultDir> pair = this.varEsMap.get(i);
            EdgeSignature es = pair.one();
            EdgeMultDir direction = pair.two();
            Set<ShapeEdge> esEdges = shape.getEdgesFromSig(es, direction);
            if (esEdges.size() == 1) {
                ShapeEdge edge = esEdges.iterator().next();
                // Check if the opposite is a concrete node.
                ShapeNode opp = edge.opposite(direction);
                if (shape.getNodeMult(opp).isOne()) {
                    Duo<BoundVar> varPair = this.esVarsMap.get(es);
                    BoundVar lbVar = varPair.one();
                    BoundVar ubVar = varPair.two();
                    // We have another trivial equation.
                    Equation oppLbEq =
                        new Equation(BoundType.LB, Relation.GE, 1, 1);
                    Equation oppUbEq =
                        new Equation(BoundType.UB, Relation.LE, 1, 1);
                    oppLbEq.addVar(lbVar);
                    oppUbEq.addVar(ubVar);
                    this.storeEquations(oppLbEq, oppUbEq);
                }

            }
        }
    }

    private Duo<BoundVar> retrieveBoundVars(EdgeSignature es,
            EdgeMultDir direction) {
        assert this.stage == 2;
        Duo<BoundVar> vars = this.esVarsMap.get(es);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.esVarsMap.put(es, vars);
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
            // ... create one lower bound and one upper bound equation.
            Equation lbEq =
                new Equation(BoundType.LB, Relation.GE,
                    origMult.getLowerBound(), varsCount);
            Equation ubEq =
                new Equation(BoundType.UB, Relation.LE,
                    origMult.getUpperBound(), varsCount);
            // ... create two bound variables.
            Duo<BoundVar> varPair = retrieveBoundVars(origNode);
            BoundVar lbVar = varPair.one();
            BoundVar ubVar = varPair.two();
            lbEq.addVar(lbVar);
            ubEq.addVar(ubVar);
            for (ShapeNode splitNode : splitNodes) {
                // ... create two bound variables.
                varPair = retrieveBoundVars(splitNode);
                lbVar = varPair.one();
                ubVar = varPair.two();
                lbEq.addVar(lbVar);
                ubEq.addVar(ubVar);
            }
            this.storeEquations(lbEq, ubEq);
        }

        // For each concrete node in the shape...
        // EDUARDO : Continue here...
        /*for (ShapeNode node : shape.nodeSet()) {
            if (!shape.getNodeMult(node).isOne()) {
                continue;
            }
            Set<EdgeBundle> splitBundles = this.mat.getSplitBundles(node);
            for (EdgeBundle splitBundle : splitBundles) {
                EdgeMultDir direction = splitBundle.direction;
                for (EdgeSignature splitEs : splitBundle.splitEs) {
                    shape.getEdgesFromSig(splitEs, direction);
                }
                            }
        }*/

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
        Set<ShapeEdge> edgesInShape;
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
            this.edgesInShape = new THashSet<ShapeEdge>();
            this.positivePossibleEdges = new THashSet<ShapeEdge>();
            EquivRelation<ShapeNode> er = new EquivRelation<ShapeNode>();
            if (shape.getNodeMult(this.node).isCollector()) {
                for (ShapeEdge edge : this.edges) {
                    int varNum = eqSys.retrieveBoundVars(edge).one().number;
                    boolean positive = !sol.getMultValue(varNum, kind).isZero();
                    if (positive) {
                        er.add(shape.getEquivClassOf(edge.opposite(this.direction)));
                        if (shape.containsEdge(edge)) {
                            this.edgesInShape.add(edge);
                        } else {
                            this.positivePossibleEdges.add(edge);
                        }
                    }
                }
            }
            if (er.size() > 1 && this.splitEs == null) {
                this.splitEs = new THashSet<EdgeSignature>();
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
    // Relation
    // --------

    private enum Relation {
        LE, GE
    }

    // --------
    // Equation
    // --------

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
