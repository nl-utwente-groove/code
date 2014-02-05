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
 * Sequential composition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SeqTerm extends Term {
    /**
     * Constructs the sequential composition of two control terms.
     */
    SeqTerm(Term arg0, Term arg1) {
        super(Term.Op.SEQ, arg0, arg1);
    }

    @Override
    protected DerivationList computeAttempt() {
        DerivationList result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = createAttempt();
            DerivationList ders0 = arg0().getAttempt();
            for (Derivation deriv : ders0) {
                result.add(deriv.newAttempt(deriv.onFinish().seq(arg1())));
            }
            result.setSuccess(ders0.onSuccess().seq(arg1()));
            result.setFailure(ders0.onFailure().seq(arg1()));
            break;
        case FINAL:
            result = arg1().isTrial() ? arg1().getAttempt() : null;
            break;
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return arg0().getDepth();
    }

    @Override
    protected Type computeType() {
        switch (arg0().getType()) {
        case TRIAL:
        case DEAD:
            return arg0().getType();
        case FINAL:
            return arg1().getType();
        default:
            assert false;
            return null;
        }
    }
}
