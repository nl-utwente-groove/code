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
 * Until-do term.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class UntilTerm extends Term {
    /**
     * Constructs an until-do term.
     */
    UntilTerm(Term arg0, Term arg1) {
        super(Op.UNTIL, arg0, arg1);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        checkTrial();
        var result = createAttempt();
        switch (arg0().getType()) {
        case TRIAL:
            DerivationAttempt ders0 = arg0().getAttempt(nested);
            result.addAll(ders0);
            result.setSuccess(ders0.onSuccess());
            result.setFailure(ders0.onFailure().ifElse(epsilon(), arg1().seq(this)));
            break;
        case DEAD:
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
    protected Type computeType() {
        return switch (arg0().getType()) {
        case TRIAL -> Type.TRIAL;
        case DEAD -> arg1().isTrial()
            ? Type.TRIAL
            : Type.DEAD;
        case FINAL -> Type.FINAL;
        };
    }

    @Override
    protected boolean isAtomic() {
        return !isTrial();
    }
}
