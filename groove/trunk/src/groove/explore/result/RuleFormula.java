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
package groove.explore.result;

import groove.explore.result.StateCondition;
import groove.lts.GraphState;
import groove.trans.Rule;

/**
 * A <code>RuleFormulae</code> is a formula that can be evaluated for new states
 * that are added to the LTS. The formula is built out of rules with the logical
 * operators <b>not</b>, <b>and</b>, <b>or</b>, and <b>implies</b>.
 * 
 * @author Maarten de Mol
 */
public class RuleFormula implements StateCondition {
    private final boolean isBasic;
    private final boolean isNot;
    private final boolean isAnd;
    private final boolean isOr;
    private final boolean isImplies;

    private final RuleFormula operand1;
    private final RuleFormula operand2;

    private final Rule rule;

    /**
     * Default constructor which initializes all local fields.
     * Do not use for creating a formula; use the static create methods instead.
     */
    public RuleFormula(boolean isBasic, boolean isNot, boolean isAnd,
            boolean isOr, boolean isImplies, RuleFormula operand1,
            RuleFormula operand2, Rule rule) {
        this.isBasic = isBasic;
        this.isNot = isNot;
        this.isAnd = isAnd;
        this.isOr = isOr;
        this.isImplies = isImplies;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.rule = rule;
    }

    /**
     * Create a basic <code>RuleFormula</code>, which checks the applicability
     * of a rule in a newly created state.
     */
    public static RuleFormula createBasic(Rule rule) {
        return new RuleFormula(true, false, false, false, false, null, null,
            rule);
    }

    /**
      * Create a negated <code>RuleFormula</code>.
      */
    public static RuleFormula createNot(RuleFormula P) {
        return new RuleFormula(false, true, false, false, false, P, null, null);
    }

    /**
      * Create a conjunctive <code>RuleFormula</code>.
      */
    public static RuleFormula createAnd(RuleFormula P, RuleFormula Q) {
        return new RuleFormula(false, false, true, false, false, P, Q, null);
    }

    /**
      * Create a disjunctive <code>RuleFormula</code>.
      */
    public static RuleFormula createOr(RuleFormula P, RuleFormula Q) {
        return new RuleFormula(false, false, false, true, false, P, Q, null);
    }

    /**
      * Create an implied <code>RuleFormula</code>.
      */
    public static RuleFormula createImplies(RuleFormula P, RuleFormula Q) {
        return new RuleFormula(false, false, false, false, true, P, Q, null);
    }

    @Override
    public boolean evalNewState(GraphState newState) {
        if (this.isBasic) {
            return this.rule.hasMatch(newState.getGraph());
        } else if (this.isNot) {
            return !this.operand1.evalNewState(newState);
        } else if (this.isAnd) {
            return this.operand1.evalNewState(newState)
                && this.operand2.evalNewState(newState);
        } else if (this.isOr) {
            return this.operand1.evalNewState(newState)
                || this.operand2.evalNewState(newState);
        } else if (this.isImplies) {
            if (this.operand1.evalNewState(newState)) {
                return this.operand2.evalNewState(newState);
            } else {
                return true;
            }
        }
        // unreachable
        return false;
    }

    /**
     * For testing and debugging.
     */
    public void print() {
        if (this.isBasic) {
            System.out.print(this.rule.getName());
        } else if (this.isNot) {
            System.out.print("!");
            this.operand1.print();
        } else if (this.isAnd) {
            System.out.print("(");
            this.operand1.print();
            System.out.print("&&");
            this.operand2.print();
            System.out.print(")");
        } else if (this.isOr) {
            System.out.print("(");
            this.operand1.print();
            System.out.print("||");
            this.operand2.print();
            System.out.print(")");
        } else if (this.isImplies) {
            System.out.print("(");
            this.operand1.print();
            System.out.print("->");
            this.operand2.print();
            System.out.print(")");
        }
    }
}
