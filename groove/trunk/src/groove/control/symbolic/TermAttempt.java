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
package groove.control.symbolic;

import groove.control.Attempt;
import groove.control.Call;
import groove.util.Pair;

/**
 * Symbolic transition.
 * This is a pair of the control call and the target symbolic location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermAttempt extends Pair<Call,Term> implements Attempt<Term> {
    /**
     * Constructs a symbolic edge out of a call and a target location.
     */
    public TermAttempt(Call call, Term target) {
        super(call, target);
    }

    /** Returns the call wrapped into this edge.
     */
    public Call getCall() {
        return one();
    }

    /**
     * Returns the target symbolic location wrapped into this edge.
     */
    public Term target() {
        return two();
    }

    /** Creates a new edge, with the call of this edge but another target. */
    public TermAttempt newAttempt(Term target) {
        return new TermAttempt(getCall(), target);
    }
}
