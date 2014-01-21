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
 * If-else term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class IfTerm extends Term {
    /**
     * Constructs an if-else term.
     * @param cond term used as a condition, to be attempted first
     * @param thenPart attempted after the condition has terminated
     * @param alsoPart attempted after the condition has yielded a success verdict
     * @param elsePart attempted after the condition has yielded a failure verdict
     */
    IfTerm(Term cond, Term thenPart, Term alsoPart, Term elsePart) {
        super(Op.IF, cond, thenPart, alsoPart, elsePart);
        assert cond.isTopLevel();
        assert thenPart.isTopLevel();
        assert alsoPart.isTopLevel();
        assert elsePart.isTopLevel();
    }

    @Override
    protected DerivationList computeAttempt() {
        DerivationList result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = createAttempt();
            for (Derivation attempt : arg0().getAttempt()) {
                result.add(attempt.newAttempt(attempt.target().seq(arg1())));
            }
            break;
        case FINAL:
            result = arg1OrArg2().getAttempt();
            break;
        case DEAD:
            result = arg3().getAttempt();
            break;
        default:
            assert false;
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = arg0().onSuccess().seq(arg1()).or(arg2());
            break;
        case FINAL:
            result = arg1OrArg2().onSuccess();
            break;
        case DEAD:
            result = arg3().onSuccess();
            break;
        default:
            assert false;
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = arg0().onFailure().ifAlsoElse(arg1(), arg2(), arg3());
            break;
        case FINAL:
            result = arg1OrArg2().onFailure();
            break;
        case DEAD:
            result = arg3().onFailure();
            break;
        default:
            assert false;
        }
        return result;
    }

    private Term arg1OrArg2() {
        if (this.arg1OrArg2 == null) {
            this.arg1OrArg2 = arg1().or(arg2());
        }
        return this.arg1OrArg2;
    }

    private Term arg1OrArg2;

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        Type result;
        switch (arg0().getType()) {
        case TRIAL:
            result = Type.TRIAL;
            break;
        case FINAL:
            result = arg1().getType();
            break;
        case DEAD:
            result = arg2().getType();
            break;
        default:
            assert false;
            result = null;
        }
        return result;
    }
}
