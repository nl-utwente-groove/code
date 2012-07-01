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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class EquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static Set<Multiplicity> zeroOneSet;

    static {
        zeroOneSet = new HashSet<Multiplicity>();
        zeroOneSet.add(Multiplicity.getMultOf(0));
        zeroOneSet.add(Multiplicity.getMultOf(1));
    }

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private Shape shape;
    private ShapeNode node; // Node to singularise: v
    private EquivClass<ShapeNode> origEc; // The original equivalence class: C
    private EquivClass<ShapeNode> singEc; // The singularised equivalence class: C'
    private EquivClass<ShapeNode> remEc; // The remaining equivalence class: C''

    private int varCount;
    private List<MultVar> vars;
    private List<Equation> equations;
    private List<SetConstraint> setConstrs;
    private List<AdmissibilityConstraint> admisConstrs;

    private Map<EdgeSignature,Pair<MultVar,MultVar>> outMap;
    private Map<EdgeSignature,Pair<MultVar,MultVar>> inMap;

    private int setCIdx;
    private int eqIdx;

    private Set<Shape> results;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * EDUARDO: Comment this...
     */
    public EquationSystem(Shape shape, ShapeNode node) {
        this.shape = shape;
        this.node = node;
        this.varCount = 0;
        this.vars = new ArrayList<MultVar>();
        this.equations = new ArrayList<Equation>();
        this.setConstrs = new ArrayList<SetConstraint>();
        this.admisConstrs = new ArrayList<AdmissibilityConstraint>();
        this.outMap = new HashMap<EdgeSignature,Pair<MultVar,MultVar>>();
        this.inMap = new HashMap<EdgeSignature,Pair<MultVar,MultVar>>();
        this.results = new HashSet<Shape>();
        this.buildEquationSystem();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "EquationSystem:\nEquations: " + this.equations
            + "\nSet Constraints: " + this.setConstrs
            + "\nAdmissibility Constraints: " + this.admisConstrs;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void buildEquationSystem() {
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
                    if (oM.isPositive()) {
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
                    if (iM.isPositive()) {
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

        this.buildAdmissibilityConstraints();
    }

    private void buildAdmissibilityConstraints() {
        // For all binary labels.
        for (Label label : Util.binaryLabelSet(this.shape)) {
            // For all equivalence classes. (As outgoing)
            for (EquivClass<ShapeNode> ecO : this.shape.getEquivRelation()) {
                // For all equivalence classes. (As incoming)
                for (EquivClass<ShapeNode> ecI : this.shape.getEquivRelation()) {

                    // For each pair of equivalence classes we have two
                    // constraints.
                    // This constraint is about the summation of all flux
                    // between two equivalence classes.
                    AdmissibilityConstraint sumConstr = this.newAdmisConstr();
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
                            }
                        }
                    }

                    // We don't want trivially valid constraints.
                    if (!sumConstr.isVacuous()) {
                        this.admisConstrs.add(sumConstr);
                    }
                    if (!remConstr.isVacuous()) {
                        this.admisConstrs.add(remConstr);
                    }
                }
            }
        }
    }

    private MultVar newMultVar() {
        MultVar result = new MultVar(this.varCount);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    private Equation newEquation(MultVar singVar, MultVar remVar,
            Multiplicity origMult) {
        assert origMult.isPositive();
        Equation eq = new Equation(singVar, remVar, origMult);
        this.equations.add(eq);
        return eq;
    }

    private SetConstraint newSetConstr(MultVar singVar,
            Set<Multiplicity> multSet) {
        SetConstraint setConst = new SetConstraint(singVar, multSet);
        this.setConstrs.add(setConst);
        return setConst;
    }

    private AdmissibilityConstraint newAdmisConstr() {
        return new AdmissibilityConstraint();
    }

    /**
     * EDUARDO: Comment this...
     */
    public void solve() {
        System.out.println("------------------------------------------");
        System.out.println("Solving " + this.toString() + "\n");

        this.setCIdx = this.setConstrs.size() - 1;
        this.eqIdx = this.equations.size() - 1;
        for (Equation eq : this.equations) {
            eq.compute();
        }
        while (!this.isSolvingFinished()) {
            this.printVars();
            if (this.areAdmisContrsSatisfied()) {
                System.out.println("Valid!");
                this.storeResultShape();
            } else {
                System.out.println("Invalid!");
            }
            this.updateSolutionValues();
        }
    }

    private void printVars() {
        System.out.print("Possible solution: ");
        for (MultVar var : this.vars) {
            System.out.print(var.toString() + " = " + var.mult + " ");
        }
    }

    private boolean isSolvingFinished() {
        return this.setCIdx == -1 && this.eqIdx == -1;
    }

    private boolean areAdmisContrsSatisfied() {
        boolean result = true;
        for (AdmissibilityConstraint admisConstr : this.admisConstrs) {
            if (!admisConstr.isSatisfied()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void updateSolutionValues() {
        while (this.eqIdx >= 0 && !this.equations.get(this.eqIdx).hasNext()) {
            this.eqIdx--;
        }
        if (this.eqIdx >= 0) {
            this.equations.get(this.eqIdx).next();
            for (int i = this.eqIdx + 1; i < this.equations.size(); i++) {
                this.equations.get(i).resetIterator();
            }
        } else {
            // We finished iterating the equations. Change the set constraints.
            while (this.setCIdx >= 0
                && !this.setConstrs.get(this.setCIdx).hasNext()) {
                this.setCIdx--;
            }
            if (this.setCIdx >= 0) {
                this.setConstrs.get(this.setCIdx).next();
                for (int i = this.setCIdx + 1; i < this.setConstrs.size(); i++) {
                    this.setConstrs.get(i).resetIterator();
                }
                for (Equation eq : this.equations) {
                    eq.compute();
                }
                this.eqIdx = this.equations.size() - 1;
            }
        }
    }

    private void storeResultShape() {
        Shape newShape = this.shape.clone();
        //newShape
        this.results.add(newShape);
    }

    /**
     * Returns clones of the original shape that satisfy the equation system.
     * If the system has no solutions, this set in empty.
     */
    public Set<Shape> getResultShapes() {
        return this.results;
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ------
    // MatVar
    // ------

    private static class MultVar {

        public int number;
        public Multiplicity mult;

        public MultVar(int number) {
            this.number = number;
            this.mult = null;
        }

        @Override
        public String toString() {
            return "x" + this.number;
        }

    }

    // --------
    // Equation
    // --------

    /**
     * Class that represents equations of the form: y = c - x, where:
     * x - is the MultVar of the singularised equivalence class;
     * y - is the MultVar for the remainder of the equivalence class; and
     * c - is the original constant multiplicity.
     */
    private static class Equation {

        public MultVar singVar; // x
        public MultVar remVar; // y
        public Multiplicity origMult; // c
        public Set<Multiplicity> resultSet;
        public Iterator<Multiplicity> iter;

        public Equation(MultVar singVar, MultVar remVar, Multiplicity origMult) {
            this.singVar = singVar;
            this.remVar = remVar;
            this.origMult = origMult;
            this.resultSet = null;
            this.iter = null;
        }

        @Override
        public String toString() {
            return this.remVar + " = " + this.origMult + " - " + this.singVar;
        }

        public void compute() {
            assert this.singVar.mult != null;
            this.resultSet = this.origMult.subEdgeMult(this.singVar.mult);
            this.resetIterator();
        }

        public void resetIterator() {
            this.iter = this.resultSet.iterator();
            this.next();
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public void next() {
            this.remVar.mult = this.iter.next();
        }

    }

    // -------------
    // SetConstraint
    // -------------

    /**
     * Class that represents a set inclusion constraint of the form:
     * x \in {0, 1, ..., \omega}, where:
     * x - is the MultVar of the singularised equivalence class.
     */
    private static class SetConstraint {

        public MultVar singVar; // x
        public Set<Multiplicity> multSet;
        public Iterator<Multiplicity> iter;

        public SetConstraint(MultVar singVar, Set<Multiplicity> multSet) {
            this.singVar = singVar;
            this.multSet = multSet;
            this.resetIterator();
        }

        @Override
        public String toString() {
            return this.singVar + " in " + this.multSet;
        }

        public void resetIterator() {
            this.iter = this.multSet.iterator();
            this.next();
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public void next() {
            this.singVar.mult = this.iter.next();
        }
    }

    // --------
    // MultTerm
    // --------

    private static class MultTerm {

        public Multiplicity nodeMult;
        public MultVar multVar;

        public MultTerm(Multiplicity nodeMult, MultVar multVar) {
            this.nodeMult = nodeMult;
            this.multVar = multVar;
        }

        @Override
        public String toString() {
            return this.nodeMult + "*" + this.multVar;
        }

        public Multiplicity multiply() {
            assert this.multVar.mult != null;
            return this.nodeMult.multiply(this.multVar.mult);
        }
    }

    // -----------------------
    // AdmissibilityConstraint
    // -----------------------

    private static class AdmissibilityConstraint {

        public List<MultTerm> outSumTerms;
        public Multiplicity outSumConst;
        public List<MultTerm> inSumTerms;
        public Multiplicity inSumConst;

        public AdmissibilityConstraint() {
            this.outSumTerms = new ArrayList<MultTerm>();
            this.outSumConst = Multiplicity.getMultOf(0);
            this.inSumTerms = new ArrayList<MultTerm>();
            this.inSumConst = Multiplicity.getMultOf(0);
        }

        @Override
        public String toString() {
            return "[Out sum: " + this.outSumTerms + " + " + this.outSumConst
                + ", In sum: " + this.inSumTerms + " + " + this.inSumConst
                + "]";
        }

        public void addToOutSum(MultTerm term) {
            this.outSumTerms.add(term);
        }

        public void addToOutSum(Multiplicity mult) {
            this.outSumConst = this.outSumConst.uadd(mult);
        }

        public void addToInSum(MultTerm term) {
            this.inSumTerms.add(term);
        }

        public void addToInSum(Multiplicity mult) {
            this.inSumConst = this.inSumConst.uadd(mult);
        }

        public Multiplicity doSum(List<MultTerm> sumTerms, Multiplicity sumConst) {
            Multiplicity result = sumConst;
            for (MultTerm term : sumTerms) {
                result = result.uadd(term.multiply());
            }
            return result;
        }

        public boolean isVacuous() {
            return this.outSumTerms.isEmpty() && this.inSumTerms.isEmpty()
                && !this.outSumConst.isPositive()
                && !this.inSumConst.isPositive();
        }

        public boolean isSatisfied() {
            Multiplicity outM = this.doSum(this.outSumTerms, this.outSumConst);
            Multiplicity inM = this.doSum(this.inSumTerms, this.inSumConst);
            return outM.overlaps(inM);
        }

    }

}