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
 * Term with increased transient depth.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TransitTerm extends Term {
    /**
     * Creates a term with increased transient depth.
     */
    public TransitTerm(Term arg0) {
        super(Op.TRANSIT, arg0);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        DerivationAttempt result = null;
        if (isTrial()) {
            result = createAttempt();
            DerivationAttempt ders = arg0().getAttempt(nested);
            for (Derivation deriv : ders) {
                result.add(deriv.newInstance(deriv.onFinish().transit(), false));
            }
            result.setSuccess(ders.onSuccess().transit());
            result.setFailure(ders.onFailure().transit());
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        if (arg0().isFinal()) {
            return 0;
        } else {
            return arg0().getTransience() + 1;
        }
    }

    @Override
    protected Type computeType() {
        return arg0().getType();
    }

    @Override
    protected boolean isAtomic() {
        return arg0().isAtomic();
    }
}
