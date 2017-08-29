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

import groove.control.Call;
import groove.control.Procedure;
import groove.grammar.Callable;
import groove.util.collect.Pool;

/**
 * Term for a call (of a {@link Callable}).
 * @author Arend Rensink
 * @version $Revision $
 */
public class CallTerm extends Term {
    /**
     * Constructs a call term.
     */
    public CallTerm(Pool<Term> pool, Call call) {
        super(pool, Op.CALL);
        assert call != null;
        this.call = call;
    }

    /** Returns the call wrapped in this term. */
    public Call getCall() {
        return this.call;
    }

    private final Call call;

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        Derivation deriv = new Derivation(getCall(), epsilon());
        DerivationAttempt result;
        if (nested && getCall().getUnit() instanceof Procedure) {
            Term inner = ((Procedure) getCall().getUnit()).getTerm();
            assert inner != null : String.format("Procedure %s has not been declared",
                getCall().getUnit()
                    .getQualName());
            result = body(inner, deriv).getAttempt(nested);
        } else {
            result = createAttempt();
            result.add(deriv);
            result.setSuccess(delta());
            result.setFailure(delta());
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return Type.TRIAL;
    }

    @Override
    protected int computeHashCode() {
        int prime = 31;
        return prime * super.computeHashCode() + this.call.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return this.call.equals(((CallTerm) obj).call);
    }

    @Override
    public String toString() {
        return "Call " + this.call;
    }

    @Override
    protected boolean isAtomic() {
        return this.call.getUnit()
            .getKind()
            .isAction();
    }
}
