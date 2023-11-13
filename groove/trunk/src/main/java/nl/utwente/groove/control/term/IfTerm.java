/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control.term;

/**
 * If-else term.
 * @author Arend Rensink
 * @version $Revision$
 */
public class IfTerm extends Term {
    /**
     * Constructs an if-else term.
     * @param cond term used as a condition, to be attempted first
     * @param thenPart attempted after the condition has terminated
     * @param alsoPart attempted after the condition has yielded a success verdict
     * @param elsePart attempted after the condition has yielded a failure verdict
     */
    IfTerm(Term cond, Term thenPart, Term alsoPart, Term elsePart) {
        super(Op.IF, cond, thenPart, alsoPart, elsePart);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        checkTrial();
        return switch (arg0().getType()) {
        case TRIAL -> {
            var result = createAttempt();
            DerivationAttempt ders0 = arg0().getAttempt(nested);
            for (Derivation deriv : ders0) {
                result.add(deriv.newInstance(deriv.onFinish().seq(arg1()), false));
            }
            result.setSuccess(ders0.onSuccess().seq(arg1()).or(arg2()));
            result.setFailure(ders0.onFailure().ifAlsoElse(arg1(), arg2(), arg3()));
            yield result;
        }
        case FINAL -> arg1OrArg2().getAttempt(nested);
        case DEAD -> arg3().getAttempt(nested);
        };
    }

    private Term arg1OrArg2() {
        if (this.arg1OrArg2 == null) {
            this.arg1OrArg2 = arg1().or(arg2());
        }
        return this.arg1OrArg2;
    }

    private Term arg1OrArg2;

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return switch (arg0().getType()) {
        case TRIAL -> Type.TRIAL;
        case FINAL -> arg1OrArg2().getType();
        case DEAD -> arg3().getType();
        };
    }

    @Override
    protected boolean isAtomic() {
        return switch (arg0().getType()) {
        case TRIAL -> arg0().isAtomic() && arg0().isFinal();
        case FINAL -> arg1OrArg2().isAtomic();
        case DEAD -> arg3().isAtomic();
        };
    }
}
