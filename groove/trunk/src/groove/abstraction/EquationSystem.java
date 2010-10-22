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
public abstract class EquationSystem {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    Shape shape;

    int varCount;
    List<MultVar> vars;
    List<Equation> equations;
    List<SetConstraint> setConstrs;
    List<AdmissibilityConstraint> admisConstrs;

    Map<EdgeSignature,Pair<MultVar,MultVar>> outMap;
    Map<EdgeSignature,Pair<MultVar,MultVar>> inMap;

    int eqI;
    int eqJ;
    int setCI;
    int setCJ;

    Set<Shape> results;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * EDUARDO: Comment this...
     */
    public EquationSystem(Shape shape) {
        this.shape = shape;
        this.varCount = 0;
        this.vars = new ArrayList<MultVar>();
        this.equations = new ArrayList<Equation>();
        this.setConstrs = new ArrayList<SetConstraint>();
        this.admisConstrs = new ArrayList<AdmissibilityConstraint>();
        this.outMap = new HashMap<EdgeSignature,Pair<MultVar,MultVar>>();
        this.inMap = new HashMap<EdgeSignature,Pair<MultVar,MultVar>>();
        this.results = new HashSet<Shape>();
    }

    // ------------------------------------------------------------------------
    // Abstract methods
    // ------------------------------------------------------------------------

    abstract void buildEquationSystem();

    abstract void buildAdmissibilityConstraints();

    abstract void storeResultShape();

    abstract void storeTrivialResultShape();

    abstract void print(String s);

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Equations: " + this.equations + "\nSet Constraints: "
            + this.setConstrs + "\nAdmissibility Constraints: "
            + this.admisConstrs;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    MultVar newMultVar() {
        MultVar result = new MultVar(this.varCount);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    Equation newEquation(MultVar singVar, MultVar remVar, Multiplicity origMult) {
        assert origMult.isPositive();
        Equation eq = new Equation(singVar, remVar, origMult);
        this.equations.add(eq);
        return eq;
    }

    SetConstraint newSetConstr(MultVar singVar, Set<Multiplicity> multSet) {
        SetConstraint setConst = new SetConstraint(singVar, multSet);
        this.setConstrs.add(setConst);
        return setConst;
    }

    AdmissibilityConstraint newAdmisConstr() {
        return new AdmissibilityConstraint();
    }

    /**
     * EDUARDO: Comment this...
     */
    public void solve() {
        this.println("------------------------------------------");
        this.println("Solving " + this.toString() + "\n");

        if (this.varCount == 0) {
            // Trivial equation system. The node the be singularised has no
            // shared multiplicities. Just split the equivalence classes.
            this.println("Valid!");
            this.storeTrivialResultShape();
        } else {
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

        this.println("Solving finished.");
    }

    void initSolve() {
        this.setCI = this.setConstrs.size() - 1;
        this.setCJ = this.setCI;
        this.computeEquations();
    }

    void computeEquations() {
        this.eqI = this.equations.size() - 1;
        this.eqJ = this.eqI;
        for (Equation eq : this.equations) {
            eq.compute();
        }
    }

    void printVars() {
        this.print("Possible solution: ");
        for (MultVar var : this.vars) {
            this.print(var.toString() + " = " + var.mult + " ");
        }
    }

    boolean isSolvingFinished() {
        return this.setCI == -1 && this.setCJ == -1;
    }

    boolean areAdmisConstrsSatisfied() {
        boolean result = true;
        for (AdmissibilityConstraint admisConstr : this.admisConstrs) {
            if (!admisConstr.isSatisfied()) {
                result = false;
                break;
            }
        }
        return result;
    }

    void updateSolutionValues() {
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

    /**
     * Returns clones of the original shape that satisfy the equation system.
     * If the system has no solutions, this set in empty.
     */
    public Set<Shape> getResultShapes() {
        return this.results;
    }

    void println(String s) {
        this.print(s + "\n");
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ------
    // MatVar
    // ------

    static class MultVar {

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
    static class Equation {

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
    static class SetConstraint {

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

    static class MultTerm {

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

    static class AdmissibilityConstraint {

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
