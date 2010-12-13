/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

import groove.abstraction.gui.ShapeDialog;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class that implements an equation system for the SingulariseNode operation.
 * 
 * In this equation system the variables represent the multiplicities of
 * edge signatures that will be affected by the operation. For each such
 * signatures, we create a pair of variables. One variable of the pair (x)
 * represents the multiplicity related to the singular equivalence class that
 * will be created and the other variable (y) is for the multiplicity related
 * to the remainder equivalence class after the split.
 * Variable x is put in a set constraint, ranging on the set {0,1}. This is
 * because all nodes to be singularised need to be concrete.
 * Variable y is a derived variable put in an equation.
 * 
 * @author Eduardo Zambon
 */
public final class NodeSingEqSystem extends EquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Debug flag. If set to true, text will be printed in stdout. */
    private static final boolean DEBUG = false;
    /** Debug flag. If set to true, the shapes will be shown in a dialog. */
    private static final boolean USE_GUI = false;

    private static final Set<Multiplicity> zeroOneSet;

    static {
        zeroOneSet = new HashSet<Multiplicity>();
        zeroOneSet.add(Multiplicity.getMultOf(0));
        zeroOneSet.add(Multiplicity.getMultOf(1));
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Node to singularise. */
    private final ShapeNode node; // v
    /** The original equivalence class of v . */
    private final EquivClass<ShapeNode> origEc; // C
    /** The singularised equivalence class. */
    private final EquivClass<ShapeNode> singEc; // C'
    /** The remaining equivalence class. */
    private final EquivClass<ShapeNode> remEc; // C''
    /** Map from variables to associated equivalence classes. */
    private final HashMap<MultVar,EquivClass<ShapeNode>> varMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * Basic constructor.
     * @param shape - the shape for which the equation system is to be built.
     * @param node - the node that will be singularised.
     */
    public NodeSingEqSystem(Shape shape, ShapeNode node) {
        super(shape);
        this.node = node;

        // The original equivalence class: C
        this.origEc = this.shape.getEquivClassOf(this.node);
        assert this.origEc.size() > 1;

        // The singularised equivalence class: C'
        this.singEc = new EquivClass<ShapeNode>();
        this.singEc.add(this.node);

        // The remaining equivalence class: C''
        this.remEc = this.origEc.clone();
        this.remEc.remove(this.node);

        this.varMap = new HashMap<MultVar,EquivClass<ShapeNode>>();

        this.buildEquationSystem();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "NodeSingEqSystem:\n" + super.toString();
    }

    @Override
    void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
    }

    /**
     * Creates a pair of multiplicity variables for each outgoing or incoming
     * edge signature that is affected by the SingulariseNode operation.
     * See the detailed description on the class header.
     */
    @Override
    void buildEquationSystem() {
        // For all binary labels.
        for (TypeLabel label : Util.binaryLabelSet(this.shape)) {
            // For all equivalence classes of the shape: D \in N_S/~
            for (EquivClass<ShapeNode> D : this.shape.getEquivRelation()) {
                // For all nodes in the equivalence class: w \in D
                for (ShapeNode w : D) {
                    EdgeSignature es =
                        this.shape.getEdgeSignature(w, label, this.origEc);

                    // Outgoing multiplicity.
                    Multiplicity oM = this.shape.getEdgeSigOutMult(es);
                    if (oM.isPositive()
                        && this.shape.isNonFrozenEdgeFromSigInvolved(es,
                            this.node, true)
                        && !this.shape.isOutEdgeSigUnique(es)) {
                        // Outgoing MultVar for C'
                        MultVar p = this.newMultVar();
                        this.varMap.put(p, this.singEc);
                        // Outgoing MultVar for C''
                        MultVar q = this.newMultVar();
                        this.varMap.put(q, this.remEc);
                        // Create the equation: q = oM - p
                        this.newEquation(p, q, oM);
                        // Create the constraint: p \in {0, 1}
                        this.newSetConstr(p, zeroOneSet);
                        // Create a new entry in the map.
                        Pair<MultVar,MultVar> pair =
                            new Pair<MultVar,MultVar>(p, q);
                        this.outMap.put(es, pair);
                    }

                    // Incoming multiplicity.
                    Multiplicity iM = this.shape.getEdgeSigInMult(es);
                    if (iM.isPositive()
                        && this.shape.isNonFrozenEdgeFromSigInvolved(es,
                            this.node, false)
                        && !this.shape.isInEdgeSigUnique(es)) {
                        // Incoming MultVar for C'
                        MultVar r = this.newMultVar();
                        this.varMap.put(r, this.singEc);
                        // Incoming MultVar for C''
                        MultVar s = this.newMultVar();
                        this.varMap.put(s, this.remEc);
                        // Create the equation: s = iM - r
                        this.newEquation(r, s, iM);
                        // Create the constraint: r \in {0, 1}
                        this.newSetConstr(r, zeroOneSet);
                        // Create a new entry in the map.
                        Pair<MultVar,MultVar> pair =
                            new Pair<MultVar,MultVar>(r, s);
                        this.inMap.put(es, pair);
                    }
                }
            }
        }

        if (this.varCount > 0) {
            this.buildAdmissibilityConstraints();
        }
    }

    /**
     * Builds the admissibility constraints for this equation system. This
     * method should only be called after all the variables of the system are
     * created.
     * The constraints make sure that no invalid values are assigned to the
     * variables. For example, we cannot set the outgoing multiplicity of an
     * edge signature to zero if there is a corresponding positive incoming
     * multiplicity.
     */
    @Override
    void buildAdmissibilityConstraints() {
        if (USE_GUI) {
            new ShapeDialog(this.shape, "");
        }

        final int OUTGOING = 0;
        final int INCOMING = 1;

        // Variable used to handle a particular corner case.
        boolean handledCrossCutting = false;

        // For all binary labels.
        for (TypeLabel label : Util.binaryLabelSet(this.shape)) {
            // For outgoing and incoming directions.
            for (int direction = OUTGOING; direction <= INCOMING; direction++) {
                EquivClass<ShapeNode> ecO = null;
                EquivClass<ShapeNode> ecI = null;

                if (direction == OUTGOING) {
                    // Take the original equivalence class as incoming.            
                    ecI = this.origEc;
                } else if (direction == INCOMING) {
                    // Take the original equivalence class as outgoing.            
                    ecO = this.origEc;
                }

                innerLoop: for (EquivClass<ShapeNode> ec : this.shape.getEquivRelation()) {
                    if (direction == OUTGOING) {
                        // For all equivalence classes. (As outgoing)
                        ecO = ec;
                    } else if (direction == INCOMING) {
                        // For all equivalence classes. (As incoming)
                        ecI = ec;
                    }

                    if (!this.haveVars(ecO, ecI, label, direction == OUTGOING)) {
                        continue innerLoop;
                    }

                    // For each pair of equivalence classes we have two constraints.

                    // This constraint is about the flow between an arbitrary
                    // equivalence class and the node that will be made singular.
                    AdmissibilityConstraint singConstr = this.newAdmisConstr();
                    // This constraint is about the flow between an arbitrary
                    // equivalence class and the remainder equivalence class.
                    AdmissibilityConstraint remConstr = this.newAdmisConstr();

                    // Outgoing terms.
                    for (ShapeNode nO : ecO) {
                        Multiplicity nOMult = this.shape.getNodeMult(nO);
                        EdgeSignature nOEs =
                            this.shape.getEdgeSignature(nO, label, ecI);
                        // Check if we have MultVars associated with this
                        // signature.
                        Pair<MultVar,MultVar> outMultVars =
                            this.outMap.get(nOEs);
                        if (outMultVars != null) {
                            // Yes, we do. Create a new MultTerm for each
                            // element in the pair.
                            MultVar p = outMultVars.one();
                            MultTerm pTerm = new MultTerm(nOMult, p);
                            if (this.varMap.get(p).equals(this.singEc)) {
                                singConstr.addToOutSum(pTerm);
                            } else if (this.varMap.get(p).equals(this.remEc)) {
                                remConstr.addToOutSum(pTerm);
                            } else {
                                assert false : "Error in building the EqSys!";
                            }

                            MultVar q = outMultVars.two();
                            MultTerm qTerm = new MultTerm(nOMult, q);
                            if (this.varMap.get(q).equals(this.singEc)) {
                                singConstr.addToOutSum(qTerm);
                            } else if (this.varMap.get(q).equals(this.remEc)) {
                                remConstr.addToOutSum(qTerm);
                            } else {
                                assert false : "Error in building the EqSys!";
                            }
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            Multiplicity eOMult =
                                this.shape.getEdgeSigOutMult(nOEs);
                            Multiplicity outMult = nOMult.multiply(eOMult);
                            if (this.shape.isNonFrozenEdgeFromSigInvolved(nOEs,
                                true)) {
                                if (nO.equals(this.node)) {
                                    singConstr.addToOutSum(outMult);
                                } else {
                                    remConstr.addToOutSum(outMult);
                                }
                            }
                        }
                    }

                    // Incoming terms.
                    for (ShapeNode nI : ecI) {
                        Multiplicity nIMult = this.shape.getNodeMult(nI);
                        EdgeSignature nIEs =
                            this.shape.getEdgeSignature(nI, label, ecO);
                        // Check if we have MultVars associated with this
                        // signature.
                        Pair<MultVar,MultVar> inMultVars = this.inMap.get(nIEs);
                        if (inMultVars != null) {
                            // Yes, we do. Create a new MultTerm for each
                            // element in the pair.
                            MultVar r = inMultVars.one();
                            MultTerm rTerm = new MultTerm(nIMult, r);
                            if (this.varMap.get(r).equals(this.singEc)) {
                                singConstr.addToInSum(rTerm);
                            } else if (this.varMap.get(r).equals(this.remEc)) {
                                remConstr.addToInSum(rTerm);
                            } else {
                                assert false : "Error in building the EqSys!";
                            }

                            MultVar s = inMultVars.two();
                            MultTerm sTerm = new MultTerm(nIMult, s);
                            if (this.varMap.get(s).equals(this.singEc)) {
                                singConstr.addToInSum(sTerm);
                            } else if (this.varMap.get(s).equals(this.remEc)) {
                                remConstr.addToInSum(sTerm);
                            } else {
                                assert false : "Error in building the EqSys!";
                            }
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            Multiplicity eIMult =
                                this.shape.getEdgeSigInMult(nIEs);
                            Multiplicity inMult = nIMult.multiply(eIMult);
                            if (this.shape.isNonFrozenEdgeFromSigInvolved(nIEs,
                                false)) {
                                if (nI.equals(this.node)) {
                                    singConstr.addToInSum(inMult);
                                } else {
                                    remConstr.addToInSum(inMult);
                                }
                            }
                        }
                    }

                    this.addAdmisConstr(singConstr);
                    this.addAdmisConstr(remConstr);

                    // This is a corner case on which we need to add additional
                    // constraints for the opposite edges in the same EC.
                    if (!handledCrossCutting && ecO.equals(ecI)) {
                        HashMap<HostEdge,Pair<MultVar,MultVar>> edgeMap =
                            new HashMap<HostEdge,Pair<MultVar,MultVar>>();
                        this.buildEdgeToVarsMap(edgeMap);
                        Multiplicity one = Multiplicity.getMultOf(1);
                        for (Pair<MultVar,MultVar> vars : edgeMap.values()) {
                            AdmissibilityConstraint constr =
                                new AdmissibilityConstraint();
                            MultTerm outTerm = new MultTerm(one, vars.one());
                            MultTerm inTerm = new MultTerm(one, vars.two());
                            constr.addToOutSum(outTerm);
                            constr.addToInSum(inTerm);
                            this.addAdmisConstr(constr);
                        }
                        handledCrossCutting = true;
                    }
                    // End corner case.
                }
            }
        }
    }

    /**
     * Stores the result of a trivial equation system. Since there are no
     * variables, we only have to split the equivalence class in the shape.
     * No cloning is necessary.
     */
    @Override
    void storeTrivialResultShape() {
        assert this.varCount == 0;
        Shape newShape = this.shape;

        // Split the equivalence class.
        newShape.splitEc(this.origEc, this.singEc, this.remEc);

        // Sanity check.
        newShape.checkShapeInvariant();
        this.results.add(newShape);
    }

    /**
     * Clones the shape associated with this equation system and modifies the
     * clone accordingly to the current values of the variables of the equation
     * system. It is assumed that these values correspond to a valid shape
     * configuration. The cloned shape is stored in the result set.
     */
    @Override
    void storeResultShape() {
        Shape newShape = this.shape.clone();

        // Split the equivalence class.
        newShape.splitEc(this.origEc, this.singEc, this.remEc);

        // Update multiplicities from the variables values.
        // Outgoing multiplicities.
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.outMap.entrySet()) {
            EdgeSignature origEs = entry.getKey();
            EdgeSignature singEs =
                newShape.getEdgeSignature(origEs.getNode(), origEs.getLabel(),
                    this.singEc);
            EdgeSignature remEs =
                newShape.getEdgeSignature(origEs.getNode(), origEs.getLabel(),
                    this.remEc);
            MultVar singVar = entry.getValue().one();
            MultVar remVar = entry.getValue().two();
            newShape.setEdgeOutMult(singEs, singVar.mult);
            newShape.setEdgeOutMult(remEs, remVar.mult);
        }
        // Incoming multiplicities.
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.inMap.entrySet()) {
            EdgeSignature origEs = entry.getKey();
            EdgeSignature singEs =
                newShape.getEdgeSignature(origEs.getNode(), origEs.getLabel(),
                    this.singEc);
            EdgeSignature remEs =
                newShape.getEdgeSignature(origEs.getNode(), origEs.getLabel(),
                    this.remEc);
            MultVar singVar = entry.getValue().one();
            MultVar remVar = entry.getValue().two();
            newShape.setEdgeInMult(singEs, singVar.mult);
            newShape.setEdgeInMult(remEs, remVar.mult);
        }

        // Sanity check.
        newShape.checkShapeInvariant();
        this.results.add(newShape);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns true if there are any variables in the equation system associated
     * with an edge signature that can be build from elements of the given
     * equivalence classes. The boolean parameter defines which two equivalence
     * classes should be iterated.
     */
    private boolean haveVars(EquivClass<ShapeNode> ec0,
            EquivClass<ShapeNode> ec1, TypeLabel label, boolean outgoing) {
        boolean result = false;
        Map<EdgeSignature,Pair<MultVar,MultVar>> map;
        EquivClass<ShapeNode> ec;
        EquivClass<ShapeNode> iterEc;
        if (outgoing) {
            map = this.outMap;
            ec = ec1;
            iterEc = ec0;
        } else {
            map = this.inMap;
            ec = ec0;
            iterEc = ec1;
        }
        for (ShapeNode n : iterEc) {
            EdgeSignature es = this.shape.getEdgeSignature(n, label, ec);
            if (map.containsKey(es)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void buildEdgeToVarsMap(Map<HostEdge,Pair<MultVar,MultVar>> edgeMap) {
        for (HostEdge edge : Util.getBinaryEdges(this.shape)) {
            edgeMap.put(edge, new Pair<MultVar,MultVar>(null, null));
        }
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.outMap.entrySet()) {
            EdgeSignature es = entry.getKey();
            Pair<MultVar,MultVar> vars = entry.getValue();
            for (ShapeEdge edge : this.shape.getEdgesFrom(es, true)) {
                if (this.remEc.contains(edge.target())) {
                    edgeMap.get(edge).setOne(vars.two());
                } else {
                    edgeMap.get(edge).setOne(vars.one());
                }
            }
        }
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.inMap.entrySet()) {
            EdgeSignature es = entry.getKey();
            Pair<MultVar,MultVar> vars = entry.getValue();
            assert vars != null;
            for (ShapeEdge edge : this.shape.getEdgesFrom(es, false)) {
                Pair<MultVar,MultVar> edgeVars = edgeMap.get(edge);
                assert edgeVars != null;
                if (this.remEc.contains(edge.source())) {
                    edgeVars.setTwo(vars.two());
                } else {
                    edgeVars.setTwo(vars.one());
                }
            }
        }
        Set<HostEdge> edgesToRemove = new HashSet<HostEdge>();
        for (Entry<HostEdge,Pair<MultVar,MultVar>> entry : edgeMap.entrySet()) {
            HostEdge edge = entry.getKey();
            Pair<MultVar,MultVar> pair = entry.getValue();
            if (pair.one() == null || pair.two() == null) {
                edgesToRemove.add(edge);
            }
        }
        for (HostEdge edge : edgesToRemove) {
            edgeMap.remove(edge);
        }
    }

}
