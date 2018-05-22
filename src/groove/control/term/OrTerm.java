/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control.term;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class OrTerm extends Term {
    /**
     * Constructs the choice of two control terms.
     */
    OrTerm(Term arg0, Term arg1) {
        super(Term.Op.OR, arg0, arg1);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        DerivationAttempt result = null;
        if (isTrial()) {
            DerivationAttempt ders0 = arg0().getAttempt(nested);
            DerivationAttempt ders1 = arg1().getAttempt(nested);
            result = createAttempt();
            // first deal with the operand that does not branch
            // so as to avoid exponential blowup
            boolean sameVerdict0 = arg0().isTrial() && ders0.sameVerdict();
            boolean sameVerdict1 = arg1().isTrial() && ders1.sameVerdict();
            Term success, failure;
            if (sameVerdict0 && sameVerdict1) {
                // optimise: combine the attempts of both args
                result.addAll(ders0);
                result.addAll(ders1);
                success = failure = ders0.onSuccess()
                    .or(ders1.onSuccess());
            } else if (sameVerdict1 || !arg0().isTrial()) {
                // first process arg1
                result.addAll(ders1);
                success = arg0().or(ders1.onSuccess());
                failure = sameVerdict1 ? success : arg0().or(ders1.onFailure());
            } else {
                // first process arg0
                result.addAll(ders0);
                success = ders0.onSuccess()
                    .or(arg1());
                failure = sameVerdict0 ? success : ders0.onFailure()
                    .or(arg1());
            }
            result.setSuccess(success);
            result.setFailure(failure);
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        Type result;
        if (arg0().isTrial() || arg1().isTrial()) {
            result = Type.TRIAL;
        } else if (arg0().isFinal() || arg1().isFinal()) {
            result = Type.FINAL;
        } else {
            result = Type.DEAD;
        }
        return result;
    }

    @Override
    protected boolean isAtomic() {
        return arg0().isAtomic() && arg1().isAtomic();
    }
}
