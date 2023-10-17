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
 * While-do term.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class WhileTerm extends Term {
    /**
     * Constructs a while-do term.
     * @param condPart the condition of the while
     * @param bodyPart the body of the while
     */
    public WhileTerm(Term condPart, Term bodyPart) {
        super(Op.WHILE, condPart, bodyPart);
    }

    @Override
    protected Type computeType() {
        return switch (arg0().getType()) {
        case TRIAL -> Type.TRIAL;
        case FINAL -> arg1().getType() == Type.TRIAL
            ? Type.TRIAL
            : Type.DEAD;
        case DEAD -> Type.FINAL;
        };
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        checkTrial();
        var result = createAttempt();
        switch (arg0().getType()) {
        case TRIAL:
            DerivationAttempt ders0 = arg0().getAttempt(nested);
            for (Derivation deriv : ders0) {
                result.add(deriv.newInstance(deriv.onFinish().seq(arg1()).seq(this), false));
            }
            result.setSuccess(ders0.onSuccess().seq(arg1()).seq(this));
            result.setFailure(ders0.onFailure().ifOnly(arg1().seq(this)));
            break;
        case FINAL:
            DerivationAttempt ders1 = arg1().getAttempt(nested);
            for (Derivation deriv : ders1) {
                result.add(deriv.newInstance(deriv.onFinish().seq(this), false));
            }
            result.setSuccess(ders1.onSuccess().seq(this));
            result.setFailure(ders1.onFailure().seq(this));
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected boolean isAtomic() {
        return !isTrial();
    }
}
