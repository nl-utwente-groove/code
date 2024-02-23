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

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.Exceptions;

/**
 * Sequential composition.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SeqTerm extends Term {
    /**
     * Constructs the sequential composition of two control terms.
     */
    SeqTerm(Term arg0, Term arg1) {
        super(Term.Op.SEQ, arg0, arg1);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        checkTrial();
        DerivationAttempt result;
        switch (arg0().getType()) {
        case TRIAL -> {
            result = createAttempt();
            var ders0 = arg0().getAttempt(nested);
            for (Derivation deriv : ders0) {
                result.add(deriv.newInstance(deriv.onFinish().seq(arg1()), false));
            }
            result.setSuccess(ders0.onSuccess().seq(arg1()));
            result.setFailure(ders0.onFailure().seq(arg1()));
        }
        case FINAL -> result = arg1().getAttempt(nested);
        default -> throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return arg0().getTransience();
    }

    @Override
    protected Type computeType() {
        return switch (arg0().getType()) {
        case TRIAL, DEAD -> arg0().getType();
        case FINAL -> arg1().getType();
        };
    }

    @Override
    protected boolean isAtomic() {
        return switch (arg0().getType()) {
        case TRIAL -> arg0().isAtomic() && arg1().isFinal();
        case DEAD -> true;
        case FINAL -> arg1().isAtomic();
        };
    }
}
