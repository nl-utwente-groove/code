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
        assert arg0.isTopLevel();
    }

    @Override
    protected DerivationList computeAttempt() {
        DerivationList result = null;
        if (arg0().isTrial()) {
            result = createAttempt();
            for (Derivation attempt : arg0().getAttempt()) {
                result.add(attempt.newAttempt(attempt.target().seq(this)));
            }
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onSuccess().seq(this).or(epsilon());
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onFailure().seq(this).or(epsilon());
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
}
