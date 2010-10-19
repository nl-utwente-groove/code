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
import java.util.Map.Entry;
import java.util.Set;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class NewEquationSystem {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static boolean DEBUG = false;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private Shape shape;
    private CountingSet<EdgeSignature> outEsSet;
    private CountingSet<EdgeSignature> inEsSet;
    private Set<ShapeEdge> frozenEdges;

    private int varCount;
    private List<MultVar> vars;
    private List<Equation> equations;
    private List<SetConstraint> setConstrs;
    private List<AdmissibilityConstraint> admisConstrs;

    private Map<EdgeSignature,Pair<MultVar,MultVar>> outMap;
    private Map<EdgeSignature,Pair<MultVar,MultVar>> inMap;

    private int eqI;
    private int eqJ;
    private int setCI;
    private int setCJ;

    private Set<Shape> results;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * EDUARDO: Comment this...
     */
    public NewEquationSystem(Shape shape, CountingSet<EdgeSignature> outEsSet,
            CountingSet<EdgeSignature> inEsSet, Set<ShapeEdge> frozenEdges) {
        this.shape = shape;
        this.outEsSet = outEsSet;
        this.inEsSet = inEsSet;
        this.frozenEdges = frozenEdges;
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
        return "NewEquationSystem:\nEquations: " + this.equations
            + "\nSet Constraints: " + this.setConstrs
            + "\nAdmissibility Constraints: " + this.admisConstrs;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void buildEquationSystem() {
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

    private void buildAdmissibilityConstraints() {
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
                            // Yes, we do. Create a new MultTerm for each
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
                            // Yes, we do. Create a new MultTerm for each
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
                        this.admisConstrs.add(admisConstr);
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
        this.println("------------------------------------------");
        this.println("Solving " + this.toString() + "\n");

        //new ShapeDialog(this.shape, "");

        // We have a real equation system to solve.
        this.initSolve();
        while (!this.isSolvingFinished()) {
            this.printVars();
            if (this.areAdmisConstrsSatisfied()) {
                this.println("Valid!");
                this.storeResultShape();
            } else {
                this.println("Invalid!");
            }
            this.updateSolutionValues();
        }
    }

    private void initSolve() {
        this.setCI = this.setConstrs.size() - 1;
        this.setCJ = this.setCI;
        this.computeEquations();
    }

    private void computeEquations() {
        this.eqI = this.equations.size() - 1;
        this.eqJ = this.eqI;
        for (Equation eq : this.equations) {
            eq.compute();
        }
    }

    private void printVars() {
        this.print("Possible solution: ");
        for (MultVar var : this.vars) {
            this.print(var.toString() + " = " + var.mult + " ");
        }
    }

    private boolean isSolvingFinished() {
        return this.setCI == -1 && this.setCJ == -1;
    }

    private boolean areAdmisConstrsSatisfied() {
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
        // First check for the equations.

        // Update eqJ.
        while (this.eqJ >= 0 && this.eqJ >= this.eqI
            && !this.equations.get(this.eqJ).hasNext()) {
            this.eqJ--;
        }

        // Check to see if we need to update eqI.
        if (this.eqJ < this.eqI) {
            // Yes, we do. Try to decrease eqI.
            while (this.eqI >= 0 && !this.equations.get(this.eqI).hasNext()) {
                // The constraint pointed by eqI has no next value.
                this.eqI--;
            }
            if (this.eqI >= 0) {
                // Get the next element of eqI.
                this.equations.get(this.eqI).next();
                // Reset all iterators from the elements below eqI.
                for (int i = this.eqI + 1; i < this.equations.size(); i++) {
                    this.equations.get(i).resetIterator();
                }
                // Reset eqJ.
                this.eqJ = this.equations.size() - 1;
                // We are done for now.
                return;
            }
        } else {
            // We don't need to update eqI. This means that eqJ is pointing
            // to an element that has a next value.
            // Get the next element of eqJ.
            this.equations.get(this.eqJ).next();
            // Reset all iterators from the elements below eqJ.
            for (int i = this.eqJ + 1; i < this.equations.size(); i++) {
                this.equations.get(i).resetIterator();
            }
            // Reset eqJ.
            this.eqJ = this.equations.size() - 1;
            // We are done for now.
            return;
        }

        // Now update the set constraints.

        // Update setCJ.
        while (this.setCJ >= 0 && this.setCJ >= this.setCI
            && !this.setConstrs.get(this.setCJ).hasNext()) {
            // The constraint pointed by setCJ has no next value.
            this.setCJ--;
        }

        // Check to see if we need to update setCI.
        if (this.setCJ < this.setCI) {
            // Yes, we do. Try to decrease setCI.
            while (this.setCI >= 0
                && !this.setConstrs.get(this.setCI).hasNext()) {
                // The constraint pointed by setCI has no next value.
                this.setCI--;
            }
            if (this.setCI >= 0) {
                // Get the next element of setCI.
                this.setConstrs.get(this.setCI).next();
                // Reset all iterators from the elements below setCI.
                for (int i = this.setCI + 1; i < this.setConstrs.size(); i++) {
                    this.setConstrs.get(i).resetIterator();
                }
                // Recompute the equations.
                this.computeEquations();
                // Reset setCJ.
                this.setCJ = this.setConstrs.size() - 1;
                // We are done for now.
                return;
            } else {
                this.setCJ = this.setCI;
            }
        } else {
            // We don't need to update setCI. This means that setCJ is pointing
            // to an element that has a next value.
            // Get the next element of setCJ.
            this.setConstrs.get(this.setCJ).next();
            // Reset all iterators from the elements below setCJ.
            for (int i = this.setCJ + 1; i < this.setConstrs.size(); i++) {
                this.setConstrs.get(i).resetIterator();
            }
            // Recompute the equations.
            this.computeEquations();
            // Reset setCJ.
            this.setCJ = this.setConstrs.size() - 1;
            // We are done for now.
            return;
        }
    }

    private void storeResultShape() {
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

    /**
     * Returns clones of the original shape that satisfy the equation system.
     * If the system has no solutions, this set in empty.
     */
    public Set<Shape> getResultShapes() {
        return this.results;
    }

    private void println(String s) {
        this.print(s + "\n");
    }

    private void print(String s) {
        if (DEBUG) {
            System.out.print(s);
        }
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
                && this.outSumConst.overlaps(this.inSumConst);
        }

        public boolean isSatisfied() {
            Multiplicity outM = this.doSum(this.outSumTerms, this.outSumConst);
            Multiplicity inM = this.doSum(this.inSumTerms, this.inSumConst);
            return outM.overlaps(inM);
        }

    }
}
