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
import groove.control.Callable;

/**
 * Term for a call (of a {@link Callable}).
 * @author Arend Rensink
 * @version $Revision $
 */
public class CallTerm extends Term {
    /**
     * Constructs a call term.
     */
    public CallTerm(TermPool pool, Call call) {
        super(pool, Op.CALL);
        this.call = call;
    }

    /** Returns the call wrapped in this term. */
    public Call getCall() {
        return this.call;
    }

    private final Call call;

    @Override
    protected DerivationList computeAttempt() {
        DerivationList result = createAttempt();
        result.add(new Derivation(this.call, epsilon()));
        return result;
    }

    @Override
    protected Term computeSuccess() {
        return delta();
    }

    @Override
    protected Term computeFailure() {
        return delta();
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
    public int hashCode() {
        int prime = 31;
        return prime * super.hashCode() + this.call.hashCode();
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
    public Term atom() {
        if (this.call.getUnit().getKind().isAction()) {
            // actions are executed atomically anyway
            return this;
        } else {
            return super.atom();
        }
    }
}
