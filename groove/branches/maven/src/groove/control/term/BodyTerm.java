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
 * Term wrapping the body of a procedure.
 * @author Arend Rensink
 * @version $Revision $
 */
public class BodyTerm extends Term {
    /**
     * @param arg the body of the procedure
     * @param caller the derivation from which the procedure was called.
     */
    public BodyTerm(Term arg, Derivation caller) {
        super(Op.BODY, arg);
        this.caller = caller;
    }

    private final Derivation caller;

    @Override
    protected Type computeType() {
        return arg0().getType();
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        DerivationAttempt result = createAttempt();
        DerivationAttempt argAttempt = arg0().getAttempt(nested);
        for (Derivation deriv : argAttempt) {
            result.add(this.caller.newInstance(deriv));
        }
        result.setSuccess(body(argAttempt.onSuccess(), this.caller));
        result.setFailure(body(argAttempt.onFailure(), this.caller));
        return result;
    }

    @Override
    protected int computeDepth() {
        return arg0().getTransience();
    }

    @Override
    protected boolean isAtomic() {
        return arg0().isAtomic();
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = super.computeHashCode();
        result = prime * result + ((this.caller == null) ? 0 : this.caller.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BodyTerm)) {
            return false;
        }
        BodyTerm other = (BodyTerm) obj;
        if (!this.caller.equals(other.caller)) {
            return false;
        }
        return true;
    }
}
