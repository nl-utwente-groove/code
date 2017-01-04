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

import groove.util.collect.Pool;

/**
 * Successful termination.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EpsilonTerm extends Term {
    /**
     * Constructs an epsilon term.
     */
    public EpsilonTerm(Pool<Term> pool) {
        super(pool, Term.Op.EPSILON);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        return null;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return Type.FINAL;
    }

    @Override
    protected boolean isAtomic() {
        return true;
    }

    @Override
    public Term seq(Term arg1) {
        return arg1;
    }
}
