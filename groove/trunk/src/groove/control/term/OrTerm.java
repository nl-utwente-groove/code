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
        assert arg0.isTopLevel();
        assert arg1.isTopLevel();
    }

    @Override
    protected DerivationList computeAttempt() {
        DerivationList result = null;
        if (isTrial()) {
            DerivationList ders0 = arg0().getAttempt();
            DerivationList ders1 = arg1().getAttempt();
            result = createAttempt();
            if (useArg0Only()) {
                result.addAll(ders0);
                result.setSuccess(ders0.onSuccess().or(arg1()));
                result.setFailure(ders0.onFailure().or(arg1()));
            } else if (useArg1Only()) {
                result.addAll(ders1);
                result.setSuccess(arg0().or(ders1.onSuccess()));
                result.setFailure(arg0().or(ders1.onFailure()));
            } else {
                // optimise: combine the attempts of both args
                result.addAll(ders0);
                result.addAll(ders1);
                result.setSuccess(ders0.onSuccess().or(ders1.onSuccess()));
                result.setFailure(ders0.onFailure().or(ders1.onFailure()));
            }
        }
        return result;
    }

    /** 
     * Yields true if arg0 is a trial position for which the verdicts are distinct,
     * or arg1 is not a trial position.
     */
    private boolean useArg0Only() {
        return arg0().isTrial() && !arg0().getAttempt().sameVerdict() || !arg1().isTrial();
    }

    /** 
     * Yields true if arg0 is not a trial position, or it has equal verdicts
     * and arg1 is a trial position with distinct verdicts.
     */
    private boolean useArg1Only() {
        return !arg0().isTrial() || !useArg0Only() && !arg1().getAttempt().sameVerdict();
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
}
