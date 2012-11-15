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

import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.util.Duo;
import groove.util.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class wrapping the functionality to resolve a materialisation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Materialiser {
    // Debug variables.
    private static final boolean WARN_BLOWUP = false;
    private static final int MAX_SOLUTION_COUNT = 4;

    /** Creates a new equation system for the given materialisation. */
    public final static Materialiser newInstance(Materialisation mat) {
        assert mat.getStage() == 1;
        return new Materialiser(mat);
    }

    private final Materialisation mat;
    private final int stage;
    private final EquationSystem eqSys;
    // ------------------------------------------------------------------------
    // Used in first stage.
    // ------------------------------------------------------------------------
    private Map<ShapeEdge,Duo<Var>> edgeVarsMap;
    private ArrayList<ShapeEdge> varEdgeMap;
    // ------------------------------------------------------------------------
    // Used in second stage.
    // ------------------------------------------------------------------------
    private Map<EdgeSignature,Duo<Var>> outEsVarsMap;
    private Map<EdgeSignature,Duo<Var>> inEsVarsMap;
    private ArrayList<EdgeSignature> varEsMap;
    // ------------------------------------------------------------------------
    // Used in third stage.
    // ------------------------------------------------------------------------
    private Map<ShapeNode,Duo<Var>> nodeVarsMap;
    private ArrayList<ShapeNode> varNodeMap;

    /**
     * Private constructor to avoid object creation.
     */
    private Materialiser(Materialisation mat) {
        assert mat != null;
        this.mat = mat;
        this.stage = mat.getStage();
        this.eqSys = new EquationSystem(this.stage);
        this.create();
    }

    /**
     * Finds all solutions of this equation system and return all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public void solve(Set<Materialisation> result) {
        Set<Solution> finishedSols = computeSolutions();
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
                new Materialiser(mat).solve(result);
            } else {
                result.add(mat);
            }
        }
    }

    /**
     * Finds all solutions of this equation system and visits all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public void visitSolutions(Visitor<Materialisation,?> visitor) {
        Set<Solution> finishedSols = computeSolutions();
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
                new Materialiser(mat).visitSolutions(visitor);
            } else if (mat.postProcess()) {
                visitor.visit(mat);
            }
        }
    }

    private Set<Solution> computeSolutions() {
        Set<Solution> result = this.eqSys.computeSolutions();
        int finishedSolsSize = result.size();
        assert this.stage == 2 ? finishedSolsSize == 1 : true;
        if (WARN_BLOWUP && finishedSolsSize > MAX_SOLUTION_COUNT) {
            System.out.println("Warning! Blowup while solving equation system: "
                + finishedSolsSize + " solutions.");
            System.out.println(this.eqSys);
            System.out.println(this.mat);
        }
        return result;
    }

    /**
     * Creates the equation system. Calls the appropriated creation method
     * depending on the stage.
     */
    private void create() {
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

    /**
     * Creates a first stage equation system. In this stage, each edge bundle
     * gives rise to an equation and each edge of the bundle to a variable.
     */
    private void createFirstStage() {
        assert this.stage == 1;
        this.edgeVarsMap = new MyHashMap<ShapeEdge,Duo<Var>>();
        this.varEdgeMap = new ArrayList<ShapeEdge>();

        boolean mayHaveGarbageNodes =
            NeighAbsParam.getInstance().getNodeMultBound() < NeighAbsParam.getInstance().getEdgeMultBound();
        Shape shape = this.mat.getShape();

        // General case:
        // For each bundle...
        for (EdgeBundle bundle : this.mat.getBundles()) {
            int varsCount = bundle.getEdgesCount();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // Collect variables...
            List<Duo<Var>> varList = new ArrayList<Duo<Var>>(varsCount);
            // For each split edge signature...
            for (EdgeSignature splitEs : bundle.getSplitEsSet()) {
                // ...for each edge...
                for (ShapeEdge edge : bundle.getSplitEsEdges(splitEs)) {
                    // ... create two bound variables.
                    Duo<Var> vars = retrieveBoundVars(edge);
                    varList.add(vars);
                    // Create additional trivial equations.
                    Duo<Equation> trivialEqs = null;
                    if (this.mat.isFixed(edge)) {
                        // Optimization 1:
                        // Create additional equations for the fixed edges.
                        trivialEqs = this.createEquations(vars, 1, 1);
                    } else if (shape.areNodesConcrete(edge)) {
                        // Optimization 2:
                        // Create additional equations for edges with concrete nodes.
                        trivialEqs = this.createEquations(vars, 0, 1);
                    }
                    if (trivialEqs != null) {
                        this.eqSys.addEquations(trivialEqs);
                    }
                }
            }
            // ... create a pair of equations.
            Duo<Equation> eqs;
            if (mayHaveGarbageNodes && nodeMult.isZeroPlus()) {
                eqs =
                    this.createEquations(varList, constMult.getLowerBound(),
                        constMult.getUpperBound(), bundle.node);
            } else {
                eqs =
                    this.createEquations(varList, constMult.getLowerBound(),
                        constMult.getUpperBound());
            }
            this.eqSys.addEquations(eqs);
        }
    }

    /**
     * Creates a second stage equation system. In this stage, each edge bundle
     * gives rise to an equation and each edge signature of the bundle is
     * associated with a pair of variables.
     * The equation system created by this method is deterministic, i.e., the
     * second stage always produces a single solution.
     */
    private void createSecondStage() {
        assert this.stage == 2;

        this.outEsVarsMap = new MyHashMap<EdgeSignature,Duo<Var>>();
        this.inEsVarsMap = new MyHashMap<EdgeSignature,Duo<Var>>();
        this.varEsMap = new ArrayList<EdgeSignature>();
        Shape shape = this.mat.getShape();

        // General case:
        // For each affected node...
        for (ShapeNode affectedNode : this.mat.getAffectedNodes()) {
            // For each split bundle...
            for (EdgeBundle bundle : this.mat.getBundles(affectedNode)) {
                // ... create one pair of equations.
                int esCount = bundle.getSplitEsSet().size();
                List<Duo<Var>> varList = new ArrayList<Duo<Var>>(esCount);
                // For each edge signature...
                for (EdgeSignature es : bundle.getSplitEsSet()) {
                    // ... create a pair of variables.
                    Duo<Var> vars = retrieveBoundVars(es);
                    varList.add(vars);
                    Set<ShapeEdge> edges = bundle.getSplitEsEdges(es);
                    if (edges.size() == 1) {
                        // Special case. Fixed edge signatures.
                        ShapeEdge edge = edges.iterator().next();
                        if (this.mat.isFixed(edge)
                            || bundle.isFixed(edge, bundle.direction, shape)) {
                            Duo<Equation> trivialEqs =
                                this.createEquations(vars, 1, 1);
                            this.eqSys.addEquations(trivialEqs);
                        }
                    }
                }
                Duo<Equation> eqs =
                    this.createEquations(varList,
                        bundle.origEsMult.getLowerBound(),
                        bundle.origEsMult.getUpperBound());
                this.eqSys.addEquations(eqs);
            }
        }
    }

    /**
     * Creates a third stage equation system. In this stage, each node that was
     * split gives rise to an equation and each new split node corresponds to
     * a variable.
     */
    private void createThirdStage() {
        assert this.stage == 3;

        this.nodeVarsMap = new MyHashMap<ShapeNode,Duo<Var>>();
        this.varNodeMap = new ArrayList<ShapeNode>();

        Shape shape = this.mat.getShape();
        Map<ShapeNode,Set<ShapeNode>> nodeSplitMap = this.mat.getNodeSplitMap();

        // General case:
        // For each split node...
        for (ShapeNode origNode : nodeSplitMap.keySet()) {
            Multiplicity origMult = this.mat.getOrigNodeMult(origNode);
            Set<ShapeNode> splitNodes = nodeSplitMap.get(origNode);
            int varsCount = splitNodes.size() + 1;
            List<Duo<Var>> varList = new ArrayList<Duo<Var>>(varsCount);
            // ... create one pair of variables for the original node.
            if (shape.containsNode(origNode)) {
                Duo<Var> vars = retrieveBoundVars(origNode);
                varList.add(vars);
            }
            for (ShapeNode splitNode : splitNodes) {
                // ... create one pair of variables for each of the split nodes.
                if (shape.containsNode(splitNode)) {
                    Duo<Var> vars = retrieveBoundVars(splitNode);
                    varList.add(vars);
                }
            }
            // ... create one pair of equations.
            Duo<Equation> eqs =
                this.createEquations(varList, origMult.getLowerBound(),
                    origMult.getUpperBound());
            this.eqSys.addEquations(eqs);
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
                    // Go over the edges of the signature and check if the
                    // opposite nodes have variables.
                    List<Duo<Var>> varList =
                        new ArrayList<Duo<Var>>(sigEdges.size());
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
                        Duo<Var> vars = this.nodeVarsMap.get(opposite);
                        if (vars == null) {
                            // The opposite node has no variable, so its
                            // multiplicity is already known. Subtract this
                            // value from the equation constant.
                            Multiplicity oppMult = shape.getNodeMult(opposite);
                            int lb = oppMult.getLowerBound();
                            constMult = constMult.sub(lb);
                        } else { // vars != null.
                            varList.add(vars);
                            // Check for a special case.
                            if (esMult.getUpperBound() == sigEdges.size()) {
                                // This case implies that all opposite nodes must
                                // be concrete.
                                Duo<Equation> trivialEqs =
                                    this.createEquations(
                                        Collections.singletonList(vars), 1, 1);
                                this.eqSys.addEquations(trivialEqs);
                            }
                        }
                    }
                    // Create a new equation.
                    Duo<Equation> eqs =
                        this.createEquations(varList,
                            constMult.getLowerBound(),
                            constMult.getUpperBound());
                    this.eqSys.addEquations(eqs);
                }
            }
        }

    }

    /**
     * Updates the given materialisation object using the given solution. Calls
     * the appropriate update method according to the stage.
     * 
    a     * @return true if a next stage is necessary, false otherwise.
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

    /**
     * Updates the given materialisation object with the given solution.
     * Always returns true since we always need to go to second stage to
     * compute the multiplicities for each edge signature. 
     */
    private boolean updateMatFirstStage(Materialisation mat, Solution sol) {
        assert this.stage == 1;

        Shape shape = mat.getShape();
        MultKind kind = finalMultKind();

        // First, get the zero and positive edges from the solution.
        Set<ShapeEdge> zeroEdges = new MyHashSet<ShapeEdge>();
        Set<ShapeEdge> positiveEdges = new MyHashSet<ShapeEdge>();
        for (int i = 0; i < this.eqSys.getVarsCount(); i++) {
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
        Set<ShapeNode> garbageNodes = sol.getGarbageNodes();
        if (garbageNodes == null) {
            return;
        }
        Shape shape = mat.getShape();
        for (ShapeNode node : garbageNodes) {
            assert shape.getNodeMult(node).isZeroPlus();
            assert shape.isUnconnected(node);
            mat.removeUnconnectedNode(node);
        }
    }

    /**
     * Updates the given materialisation object with the given solution.
     * Returns true if we had node splits in the second stage, meaning that we
     * have to compute the multiplicities for the split nodes.
     */
    private boolean updateMatSecondStage(Materialisation mat, Solution sol) {
        assert this.stage == 2;
        Shape shape = mat.getShape();
        MultKind kind = finalMultKind();
        for (int i = 0; i < this.eqSys.getVarsCount(); i++) {
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

    /**
     * Updates the given materialisation object with the given solution.
     * Always returns false, since this is the last stage.
     */
    private boolean updateMatThirdStage(Materialisation mat, Solution sol) {
        assert this.stage == 3;
        Shape shape = mat.getShape();
        MultKind kind = finalMultKind();
        for (int i = 0; i < this.eqSys.getVarsCount(); i++) {
            Multiplicity mult = sol.getMultValue(i, kind);
            shape.setNodeMult(this.varNodeMap.get(i), mult);
        }
        return false;
    }

    /** Creates and returns a pair of equations with the given constants. */
    private Duo<Equation> createEquations(Duo<Var> vars, int lbConst,
            int ubConst) {
        Equation lbEq =
            new Equation(Collections.singletonList(vars.one()), BoundType.LB,
                lbConst, null);
        Equation ubEq =
            new Equation(Collections.singletonList(vars.two()), BoundType.UB,
                ubConst, null);
        return new Duo<Equation>(lbEq, ubEq);
    }

    /** Creates and returns a pair of equations with the given constants. */
    private Duo<Equation> createEquations(List<Duo<Var>> vars, int lbConst,
            int ubConst) {
        return createEquations(vars, lbConst, ubConst, null);
    }

    /**
     * Creates and returns a pair of equations with the given constants and
     * a reference to a collector node. The node is used only in the
     * first stage.
     */
    private Duo<Equation> createEquations(List<Duo<Var>> varList, int lbConst,
            int ubConst, ShapeNode node) {
        List<Var> lbVars = new ArrayList<Var>(varList.size());
        List<Var> ubVars = new ArrayList<Var>(varList.size());
        for (Duo<Var> varDuo : varList) {
            lbVars.add(varDuo.one());
            ubVars.add(varDuo.two());
        }
        Equation lbEq = new Equation(lbVars, BoundType.LB, lbConst, node);
        Equation ubEq = new Equation(ubVars, BoundType.UB, ubConst, node);
        return new Duo<Equation>(lbEq, ubEq);
    }

    /**
     * Returns a pair of variables associated with the given node. If no
     * variable pair is found, a new one is created and associated with the
     * node. 
     */
    private Duo<Var> retrieveBoundVars(ShapeNode node) {
        assert this.stage == 3;
        Duo<Var> vars = this.nodeVarsMap.get(node);
        if (vars == null) {
            vars = this.eqSys.createVars();
            this.nodeVarsMap.put(node, vars);
            this.varNodeMap.add(vars.one().getNumber(), node);
        }
        return vars;
    }

    /**
     * Returns a pair of variables associated with the given edge. If no
     * variable pair is found, a new one is created and associated with the
     * edge. 
     */
    private Duo<Var> retrieveBoundVars(ShapeEdge edge) {
        assert this.stage == 1;
        Duo<Var> vars = this.edgeVarsMap.get(edge);
        if (vars == null) {
            vars = this.eqSys.createVars();
            this.edgeVarsMap.put(edge, vars);
            this.varEdgeMap.add(vars.one().getNumber(), edge);
        }
        return vars;
    }

    /**
     * Returns a pair of variables associated with the given signature. If no
     * variable pair is found, a new one is created and associated with the
     * edge signature. 
     */
    private Duo<Var> retrieveBoundVars(EdgeSignature es) {
        assert this.stage == 2;
        EdgeMultDir direction = es.getDirection();
        Duo<Var> vars = this.getEsMap(direction).get(es);
        if (vars == null) {
            vars = this.eqSys.createVars();
            this.getEsMap(direction).put(es, vars);
            this.varEsMap.add(vars.one().getNumber(), es);
        }
        return vars;
    }

    /** Returns the signature map for the given direction. */
    private Map<EdgeSignature,Duo<Var>> getEsMap(EdgeMultDir direction) {
        assert this.stage == 2;
        Map<EdgeSignature,Duo<Var>> result = null;
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
}
