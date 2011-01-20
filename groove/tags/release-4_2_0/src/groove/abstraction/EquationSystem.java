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

import groove.util.Duo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements the common functionality of an equation system.
 * 
 * An equation system is formed by multiplicity variables (MultVars). These
 * variables may appear in three sub-parts of the equation system (x, y are
 * MultVars):
 * - Set constraints - constraints of the form: x \in Set_of_Multiplicities.
 * - Equations - equality constraints that express the dependency between
 *   variables. E.g., y = Multiplicity - x .
 * - Admissibility constraints - the constraints that talk about the overall
 *   admissibility of a shape configuration. 
 * 
 * An equation system that has no variables is said to be trivial. 
 * 
 * The only methods that need to be implemented by sub-classes are the
 * construction of the equation system and the storage of shapes from a
 * given solution. The code that solves an equation system is given in this
 * class.
 * 
 * @author Eduardo Zambon
 */
public abstract class EquationSystem {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The shape that will produce the equation system. */
    final Shape shape;

    /** The number of multiplicity variables of the system. */
    int varCount;
    /** An ordered list of variables of the system. */
    final List<MultVar> vars;
    /** The collection of equations (y = Multiplicity - x) of the system. */
    final List<Equation> equations;
    /**
     * The collection of set constraints (x \in Set_of_Multiplicities) of the
     * system.
     */
    final List<SetConstraint> setConstrs;
    /** The collection of admissibility constraints of the system. */
    final List<AdmissibilityConstraint> admisConstrs;

    /** Maps used in the construction of the system. */
    final Map<EdgeSignature,Duo<MultVar>> outMap;
    final Map<EdgeSignature,Duo<MultVar>> inMap;

    /**
     * Indices used when solving the equation system.
     * IMPORTANT: this indices always start at the maximum value and are
     * decreased until they reach zero.
     */
    int eqI;
    int eqJ;
    int setCI;
    int setCJ;

    /** The set of shapes that are valid according to the equation system. */
    final Set<Shape> results;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------    

    /**
     * Basic constructor. Initialises all collections of the system but does
     * not actually builds the equation system. For that, call
     * buildEquationSystem() .
     */
    public EquationSystem(Shape shape) {
        this.shape = shape;
        this.varCount = 0;
        this.vars = new ArrayList<MultVar>();
        this.equations = new ArrayList<Equation>();
        this.setConstrs = new ArrayList<SetConstraint>();
        this.admisConstrs = new ArrayList<AdmissibilityConstraint>();
        this.outMap = new HashMap<EdgeSignature,Duo<MultVar>>();
        this.inMap = new HashMap<EdgeSignature,Duo<MultVar>>();
        this.results = new HashSet<Shape>();
    }

    // ------------------------------------------------------------------------
    // Abstract methods
    // ------------------------------------------------------------------------

    /**
     * This method analyses the shape and the operation to be performed and
     * creates the MultVars needed. It also creates the equations and the
     * set constraints. In the end, it should also call the 
     * buildAdmissibilityConstraints() method, to finish the construction of
     * the equation system.
     */
    abstract void buildEquationSystem();

    /**
     * Defines the admissibility constraints for this equation system based
     * on the operation that is being performed.
     */
    abstract void buildAdmissibilityConstraints();

    /** Creates a new shape from the current valid result of the system. */
    abstract void storeResultShape();

    /** Creates a new shape from the current result of the trivial system. */
    abstract void storeTrivialResultShape();

    /** Debug method. */
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

    /**
     * Creates and returns a new MultVar, that is also stored in the variables
     * set.
     */
    MultVar newMultVar() {
        MultVar result = new MultVar(this.varCount);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    /**
     * Creates and returns a new equation, that is also stored in the equation
     * set. The equation is of the form: remVar = origMult - singVar .
     * The given multiplicity must be positive.
     */
    Equation newEquation(MultVar singVar, MultVar remVar, Multiplicity origMult) {
        assert origMult.isPositive();
        Equation eq = new Equation(singVar, remVar, origMult);
        this.equations.add(eq);
        return eq;
    }

    /**
     * Creates and returns a new set constraint, that is also stored in the
     * proper set. The constraint is of the form: singVar \in multSet .
     */
    SetConstraint newSetConstr(MultVar singVar, Set<Multiplicity> multSet) {
        SetConstraint setConst = new SetConstraint(singVar, multSet);
        this.setConstrs.add(setConst);
        return setConst;
    }

    /**
     * Creates and returns a new admissibility constraint, which is NOT stored.
     * Make sure to call the method addAdmisConstr when the constraint is
     * build. The constraint is not added automatically because in some systems
     * we may have vacuously true constraints, which don't have to be added
     * to the equation system.  
     */
    AdmissibilityConstraint newAdmisConstr() {
        return new AdmissibilityConstraint();
    }

    /**
     * Adds the given admissibility constraint to the proper collection in the
     * equation system if the constraint is not already present. 
     */
    void addAdmisConstr(AdmissibilityConstraint admisConstr) {
        if (!this.admisConstrs.contains(admisConstr)) {
            this.admisConstrs.add(admisConstr);
        }
    }

    /**
     * Solves the equation system.
     * The method is very simple. It just goes over all possible valid values
     * for the variables and checks if all admissibility constraints are
     * satisfied. If yes, the shape corresponding to the current set of values
     * is stored in the result set.
     */
    public void solve() {
        this.println("------------------------------------------");
        this.println("Solving " + this.toString() + "\n");

        if (this.varCount == 0) {
            // Trivial equation system.
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

    /**
     * Initialises the indices for the variables that occur in the set
     * constraints, and compute the possible values for the derived 
     * variables in the equations.
     */
    void initSolve() {
        this.setCI = this.setConstrs.size() - 1;
        this.setCJ = this.setCI;
        this.computeEquations();
    }

    /**
     * Initialises the indices for the derived variables that occur in the
     * equations, and compute the possible values for the derived 
     * variables in the equations.
     */
    void computeEquations() {
        this.eqI = this.equations.size() - 1;
        this.eqJ = this.eqI;
        for (Equation eq : this.equations) {
            eq.compute();
        }
    }

    /** Debug method. */
    void printVars() {
        this.print("Possible solution: ");
        for (MultVar var : this.vars) {
            this.print(var.toString() + " = " + var.mult + " ");
        }
    }

    /**
     * Returns true if all values of all the variables in the set constraints
     * were tried.
     */
    boolean isSolvingFinished() {
        return this.setCI == -1 && this.setCJ == -1;
    }

    /**
     * Returns true if all admissibility constraints of the equation system are
     * satisfied by the current values of the variables.
     */
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

    /**
     * Updates the working indices of the equation system and sets all the
     * variables to a proper value.
     * We first check if there are unexplored values in the equations. If yes,
     * then we just update the indices for the equations. If no, then we need
     * to update the indices for the variables in the set constraints. Every
     * time that we modify such an index, all the equations need to be computed
     * again, and we reset all equation indices.
     * The method always keeps the invariant: j >= i, for all j's and i's.
     * The j indices are the working indices.
     * The i indices store the last element that was modified. Every time that
     * i changes, all iterators from i+1 to the maximum value are reset.
     */
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

    /** Debug method. */
    void println(String s) {
        this.print(s + "\n");
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // -------
    // MultVar
    // -------

    /** A multiplicity variable. It has a number and (possibly) a value. */
    static final class MultVar {

        /** The variable unique number. */
        public final int number;
        /** The current multiplicity value. May be null. */
        public Multiplicity mult;

        /**
         * Basic constructor.
         * Should not be called directly. See newMultVar() method.
         */
        public MultVar(int number) {
            this.number = number;
            this.mult = null;
        }

        @Override
        public String toString() {
            return "x" + this.number;
        }

        /** Two multiplicity variables are equal if they have the same number. */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof MultVar)) {
                result = false;
            } else {
                MultVar other = (MultVar) o;
                result = this.number == other.number;
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * this.number * this.number;
        }

    }

    // --------
    // Equation
    // --------

    /**
     * Class that represents equations of the form: y = c - x, where
     * x and y are MultVars that are complementary; and c is a constant
     * multiplicity.
     */
    static final class Equation {

        public final MultVar singVar; // x
        public final MultVar remVar; // y
        public final Multiplicity origMult; // c
        /** The possible values of y when x is assigned a value. */
        public Set<Multiplicity> resultSet;
        /** The iterator over the result set. */
        public Iterator<Multiplicity> iter;

        /**
         * Basic constructor.
         * Should not be called directly. See newEquation() method.
         */
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

        /**
         * Computes the possible values for y.
         * Assumes that x has a proper value set.
         */
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

        /** Sets the next value of y. */
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
    static final class SetConstraint {

        public final MultVar singVar; // x
        public final Set<Multiplicity> multSet;
        /** The iterator over the multiplicity set. */
        public Iterator<Multiplicity> iter;

        /**
         * Basic constructor.
         * Should not be called directly. See newSetConstr() method.
         */
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

        /** Sets the next value of x. */
        public void next() {
            this.singVar.mult = this.iter.next();
        }
    }

    // --------
    // MultTerm
    // --------

    /**
     * Class representing a multiplication term, formed by a constant
     * multiplicity 'c' and a multiplicity variable 'x'. The term corresponds
     * then to: c * x .
     */
    static final class MultTerm {

        public final Multiplicity constMult; // c
        public final MultVar multVar; // x

        public MultTerm(Multiplicity constMult, MultVar multVar) {
            this.constMult = constMult;
            this.multVar = multVar;
        }

        @Override
        public String toString() {
            return this.constMult + "*" + this.multVar;
        }

        /** 
         * Two multiplicity terms are equal if they have the same constant and
         * variable.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof MultTerm)) {
                result = false;
            } else {
                MultTerm other = (MultTerm) o;
                result =
                    this.constMult.equals(other.constMult)
                        && this.multVar.equals(other.multVar);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.constMult.hashCode();
            result = prime * result + this.multVar.hashCode();
            return result;
        }

        /** Multiplies c with the current value of x and returns the result. */
        public Multiplicity multiply() {
            assert this.multVar.mult != null;
            return this.constMult.multiply(this.multVar.mult);
        }

    }

    // -----------------------
    // AdmissibilityConstraint
    // -----------------------

    /**
     * Class representing an admissibility constraint.
     * Such constraint is formed by terms and constants, grouped in outgoing
     * and incoming sums. The constraint is satisfied if the resulting
     * multiplicities of both sums are overlapping, i.e., have a non-empty
     * intersection.
     */
    static final class AdmissibilityConstraint {

        public final List<MultTerm> outSumTerms;
        public Multiplicity outSumConst;
        public final List<MultTerm> inSumTerms;
        public Multiplicity inSumConst;

        /**
         * Basic constructor.
         * Should not be called directly. See newAdmisConstr() method.
         */
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

        /** 
         * Two admissibility constraints are equal if all terms and constants
         * are equal.
         */
        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof AdmissibilityConstraint)) {
                result = false;
            } else {
                AdmissibilityConstraint other = (AdmissibilityConstraint) o;
                result =
                    this.outSumTerms.equals(other.outSumTerms)
                        && this.outSumConst.equals(other.outSumConst)
                        && this.inSumTerms.equals(other.inSumTerms)
                        && this.inSumConst.equals(other.inSumConst);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.outSumTerms.hashCode();
            result = prime * result + this.outSumConst.hashCode();
            result = prime * result + this.inSumTerms.hashCode();
            result = prime * result + this.inSumConst.hashCode();
            return result;
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

        /** Performs an unbounded sum for one side of the constraint. */
        public Multiplicity doSum(List<MultTerm> sumTerms, Multiplicity sumConst) {
            Multiplicity result = sumConst;
            for (MultTerm term : sumTerms) {
                result = result.uadd(term.multiply());
            }
            return result;
        }

        /**
         * Returns true if there are no outgoing or incoming terms and the
         * constants are overlapping.
         */
        public boolean isVacuous() {
            return this.outSumTerms.isEmpty() && this.inSumTerms.isEmpty()
                && this.outSumConst.overlaps(this.inSumConst);
        }

        /**
         * Returns true if there are no outgoing or incoming terms and the
         * constants are not overlapping.
         */
        public boolean isInvalid() {
            return this.outSumTerms.isEmpty() && this.inSumTerms.isEmpty()
                && !this.outSumConst.overlaps(this.inSumConst);
        }

        /**
         * The constraint is satisfied if the resulting multiplicities of both
         * sums are overlapping, i.e., have a non-empty intersection.
         */
        public boolean isSatisfied() {
            Multiplicity outM = this.doSum(this.outSumTerms, this.outSumConst);
            Multiplicity inM = this.doSum(this.inSumTerms, this.inSumConst);
            return outM.overlaps(inM);
        }

    }
}
