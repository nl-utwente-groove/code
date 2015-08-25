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
 * Kleene-starred term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StarTerm extends Term {
    /**
     * Constructs a Kleene-starred term.
     */
    public StarTerm(Term arg0) {
        super(Op.STAR, arg0);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        DerivationAttempt result = null;
        if (arg0().isTrial()) {
            result = createAttempt();
            DerivationAttempt ders = arg0().getAttempt(nested);
            for (Derivation deriv : ders) {
                result.add(deriv.newInstance(deriv.onFinish().seq(this), false));
            }
            result.setSuccess(ders.onSuccess().seq(this).or(epsilon()));
            result.setFailure(ders.onFailure().seq(this).or(epsilon()));
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return arg0().isTrial() ? Type.TRIAL : Type.FINAL;
    }

    @Override
    protected boolean isAtomic() {
        return !arg0().isTrial();
    }
}
