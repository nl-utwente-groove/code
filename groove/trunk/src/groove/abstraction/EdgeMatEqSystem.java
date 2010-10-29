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
import groove.graph.Label;
import groove.util.Pair;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public final class EdgeMatEqSystem extends EquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static boolean DEBUG = false;
    private static boolean USE_GUI = false;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private CountingSet<EdgeSignature> outEsSet;
    private CountingSet<EdgeSignature> inEsSet;
    private Set<ShapeEdge> frozenEdges;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * EDUARDO: Comment this...
     */
    public EdgeMatEqSystem(Shape shape, CountingSet<EdgeSignature> outEsSet,
            CountingSet<EdgeSignature> inEsSet, Set<ShapeEdge> frozenEdges) {
        super(shape);
        this.outEsSet = outEsSet;
        this.inEsSet = inEsSet;
        this.frozenEdges = frozenEdges;
        this.buildEquationSystem();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "EdgeMatEqSystem:\n" + super.toString();
    }

    @Override
    void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
    }

    @Override
    void buildEquationSystem() {
        // For each signature in the counting sets, create a pair of variables.
        // Outgoing.
        for (Entry<EdgeSignature,Integer> entry : this.outEsSet.entrySet()) {
            EdgeSignature es = entry.getKey();
            Multiplicity oM = this.shape.getEdgeSigOutMult(es);

            // Outgoing MultVar for the frozen edges.
            MultVar p = this.newMultVar();
            // Outgoing MultVar for the remaining edges.
            MultVar q = this.newMultVar();
            // Create the equation: q = oM - p
            this.newEquation(p, q, oM);
            // Create the constraint: p \in {pM}
            Multiplicity pM = Multiplicity.getMultOf(entry.getValue());
            Set<Multiplicity> pMSet = new HashSet<Multiplicity>();
            pMSet.add(pM);
            this.newSetConstr(p, pMSet);
            // Create a new entry in the map.
            Pair<MultVar,MultVar> pair = new Pair<MultVar,MultVar>(p, q);
            this.outMap.put(es, pair);
        }
        // Incoming.
        for (Entry<EdgeSignature,Integer> entry : this.inEsSet.entrySet()) {
            EdgeSignature es = entry.getKey();
            Multiplicity iM = this.shape.getEdgeSigInMult(es);

            // Incoming MultVar for the frozen edges.
            MultVar r = this.newMultVar();
            // Incoming MultVar for the remaining edges.
            MultVar s = this.newMultVar();
            // Create the equation: s = iM - r
            this.newEquation(r, s, iM);
            // Create the constraint: r \in {rM}
            Multiplicity rM = Multiplicity.getMultOf(entry.getValue());
            Set<Multiplicity> rMSet = new HashSet<Multiplicity>();
            rMSet.add(rM);
            this.newSetConstr(r, rMSet);
            // Create a new entry in the map.
            Pair<MultVar,MultVar> pair = new Pair<MultVar,MultVar>(r, s);
            this.inMap.put(es, pair);
        }
        this.buildAdmissibilityConstraints();
    }

    @Override
    void buildAdmissibilityConstraints() {
        if (USE_GUI) {
            new ShapeDialog(this.shape, "");
        }
        // For all binary labels.
        for (Label label : Util.binaryLabelSet(this.shape)) {
            // For all equivalence classes. (As outgoing)
            for (EquivClass<ShapeNode> ecO : this.shape.getEquivRelation()) {
                // For all equivalence classes. (As incoming)
                for (EquivClass<ShapeNode> ecI : this.shape.getEquivRelation()) {
                    // For each pair of equivalence classes we have a constraint.
                    AdmissibilityConstraint admisConstr = this.newAdmisConstr();

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
                            // Yes, we do. Create a new MultTerm for the second
                            // element in the pair.
                            MultTerm term;
                            term = new MultTerm(nOMult, outMultVars.second());
                            admisConstr.addToOutSum(term);
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            if (!this.shape.sigContains(nOEs, this.frozenEdges,
                                true)) {
                                Multiplicity eOMult =
                                    this.shape.getEdgeSigOutMult(nOEs);
                                Multiplicity outMult = nOMult.multiply(eOMult);
                                admisConstr.addToOutSum(outMult);
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
                            // Yes, we do. Create a new MultTerm for the second
                            // element in the pair.
                            MultTerm term;
                            term = new MultTerm(nIMult, inMultVars.second());
                            admisConstr.addToInSum(term);
                        } else {
                            // No, we don't. Multiply the two multiplicities
                            // and add the result to the constant in the
                            // constraint.
                            if (!this.shape.sigContains(nIEs, this.frozenEdges,
                                false)) {
                                Multiplicity eIMult =
                                    this.shape.getEdgeSigInMult(nIEs);
                                Multiplicity inMult = nIMult.multiply(eIMult);
                                admisConstr.addToInSum(inMult);
                            }
                        }
                    }

                    // We don't want trivially valid constraints.
                    if (!admisConstr.isVacuous()) {
                        this.addAdmisConstr(admisConstr);
                    }
                }
            }
        }
    }

    @Override
    void storeResultShape() {
        Shape newShape = this.shape.clone();

        // Freeze the edges in the new shape.
        newShape.freezeEdges(this.frozenEdges);

        // Update multiplicities from the variables values.
        // Outgoing multiplicities.
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.outMap.entrySet()) {
            EdgeSignature es = entry.getKey();
            MultVar var = entry.getValue().second();
            newShape.setEdgeOutMult(es, var.mult);
        }
        // Incoming multiplicities.
        for (Entry<EdgeSignature,Pair<MultVar,MultVar>> entry : this.inMap.entrySet()) {
            EdgeSignature es = entry.getKey();
            MultVar var = entry.getValue().second();
            newShape.setEdgeInMult(es, var.mult);
        }

        // Sanity check.
        newShape.checkShapeInvariant();
        this.results.add(newShape);
    }

    @Override
    void storeTrivialResultShape() {
        throw new UnsupportedOperationException();
    }

}
