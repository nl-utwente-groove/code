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

import groove.graph.Label;
import groove.util.Pair;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class NodeSingEqSystem extends EquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static boolean DEBUG = false;

    private static Set<Multiplicity> zeroOneSet;

    static {
        zeroOneSet = new HashSet<Multiplicity>();
        zeroOneSet.add(Multiplicity.getMultOf(0));
        zeroOneSet.add(Multiplicity.getMultOf(1));
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private ShapeNode node; // Node to singularise: v
    private EquivClass<ShapeNode> origEc; // The original equivalence class: C
    private EquivClass<ShapeNode> singEc; // The singularised equivalence class: C'
    private EquivClass<ShapeNode> remEc; // The remaining equivalence class: C''

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * EDUARDO: Comment this...
     */
    public NodeSingEqSystem(Shape shape, ShapeNode node) {
        super(shape);
        this.node = node;
        this.buildEquationSystem();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "NodeSingEqSystem:\n" + super.toString();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    @Override
    void buildEquationSystem() {
        // The original equivalence class: C
        this.origEc = this.shape.getEquivClassOf(this.node);
        assert this.origEc.size() > 1;

        // The singularised equivalence class: C'
        this.singEc = new EquivClass<ShapeNode>();
        this.singEc.add(this.node);

        // The remaining equivalence class: C''
        this.remEc = this.origEc.clone();
        this.remEc.remove(this.node);

        // For all binary labels.
        for (Label label : Util.binaryLabelSet(this.shape)) {
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
                        // Outgoing MultVar for C''
                        MultVar q = this.newMultVar();
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
                        // Incoming MultVar for C''
                        MultVar s = this.newMultVar();
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

    @Override
    // EDUARDO: Improve this method... Maybe move to super?
    void buildAdmissibilityConstraints() {
        // For all binary labels.
        for (Label label : Util.binaryLabelSet(this.shape)) {
            // For all equivalence classes. (As outgoing)
            for (EquivClass<ShapeNode> ecO : this.shape.getEquivRelation()) {
                // For all equivalence classes. (As incoming)
                for (EquivClass<ShapeNode> ecI : this.shape.getEquivRelation()) {

                    // For each pair of equivalence classes we have three
                    // constraints.
                    // This constraint is about the summation of all flux
                    // between two equivalence classes.
                    AdmissibilityConstraint sumConstr = this.newAdmisConstr();
                    // This constraint is about the summation of the flux
                    // between an arbitrary equivalence class and the singleton
                    // of the equivalence class that is being split.
                    AdmissibilityConstraint singConstr = this.newAdmisConstr();
                    // This constraint is about the summation of the flux
                    // between an arbitrary equivalence class and the remainder
                    // of the equivalence class that is being split.
                    AdmissibilityConstraint remConstr = this.newAdmisConstr();

                    // Create the terms of the sum for the outgoing equivalence
                    // class.
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
                            MultTerm term;
                            term = new MultTerm(nOMult, outMultVars.first());
                            sumConstr.addToOutSum(term);
                            term = new MultTerm(nOMult, outMultVars.second());
                            sumConstr.addToOutSum(term);
                            // Add the first term to the singleton constraint
                            // as well.
                            term = new MultTerm(nOMult, outMultVars.first());
                            singConstr.addToOutSum(term);
                            // Add the second term to the remainder constraint
                            // as well.
                            term = new MultTerm(nOMult, outMultVars.second());
                            remConstr.addToOutSum(term);
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            Multiplicity eOMult =
                                this.shape.getEdgeSigOutMult(nOEs);
                            Multiplicity outMult = nOMult.multiply(eOMult);
                            sumConstr.addToOutSum(outMult);
                            if (!nO.equals(this.node)) {
                                // The sum does not consider the node that is
                                // being singularised.
                                remConstr.addToOutSum(outMult);
                            } else {
                                singConstr.addToOutSum(outMult);
                            }
                        }
                    }

                    // Create the terms of the sum for the incoming equivalence
                    // class.
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
                            MultTerm term;
                            term = new MultTerm(nIMult, inMultVars.first());
                            sumConstr.addToInSum(term);
                            term = new MultTerm(nIMult, inMultVars.second());
                            sumConstr.addToInSum(term);
                            // Add the first term to the singleton constraint
                            // as well.
                            term = new MultTerm(nIMult, inMultVars.first());
                            singConstr.addToInSum(term);
                            // Add the second term to the remainder constraint
                            // as well.
                            term = new MultTerm(nIMult, inMultVars.second());
                            remConstr.addToInSum(term);
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            Multiplicity eIMult =
                                this.shape.getEdgeSigInMult(nIEs);
                            Multiplicity inMult = nIMult.multiply(eIMult);
                            sumConstr.addToInSum(inMult);
                            if (!nI.equals(this.node)) {
                                // The sum does not consider the node that is
                                // being singularised.
                                remConstr.addToInSum(inMult);
                            } else {
                                singConstr.addToInSum(inMult);
                            }
                        }
                    }

                    // We don't want trivially valid constraints.
                    if (!sumConstr.isVacuous()) {
                        this.admisConstrs.add(sumConstr);
                    }
                    if (!singConstr.isVacuous()) {
                        this.admisConstrs.add(singConstr);
                    }
                    if (!remConstr.isVacuous()) {
                        this.admisConstrs.add(remConstr);
                    }
                }
            }
        }
    }

    @Override
    void storeTrivialResultShape() {
        assert this.varCount == 0;
        Shape newShape = this.shape;

        // Split the equivalence class.
        newShape.splitEc(this.origEc, this.singEc, this.remEc, true);

        // Sanity check.
        newShape.checkShapeInvariant();
        this.results.add(newShape);
    }

    @Override
    void storeResultShape() {
        Shape newShape = this.shape.clone();

        // Split the equivalence class.
        newShape.splitEc(this.origEc, this.singEc, this.remEc, false);

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
            MultVar singVar = entry.getValue().first();
            MultVar remVar = entry.getValue().second();
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
            MultVar singVar = entry.getValue().first();
            MultVar remVar = entry.getValue().second();
            newShape.setEdgeInMult(singEs, singVar.mult);
            newShape.setEdgeInMult(remEs, remVar.mult);
        }

        // Sanity check.
        newShape.checkShapeInvariant();
        this.results.add(newShape);
    }

    @Override
    void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
    }

}
