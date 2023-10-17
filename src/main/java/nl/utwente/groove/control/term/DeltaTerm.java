/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control.term;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.collect.Pool;

/**
 * Deadlock.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class DeltaTerm extends Term {
    /**
     * Constructs a delta term.
     */
    public DeltaTerm(Pool<Term> pool, int depth) {
        super(pool, Term.Op.DELTA);
        this.depth = depth;
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        throw noTrial();
    }

    @Override
    protected int computeDepth() {
        return this.depth;
    }

    private final int depth;

    @Override
    protected Type computeType() {
        return Type.DEAD;
    }

    @Override
    protected boolean isAtomic() {
        return true;
    }

    @Override
    protected int computeHashCode() {
        int prime = 31;
        return prime * super.computeHashCode() + getTransience();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        assert obj instanceof DeltaTerm;
        return ((DeltaTerm) obj).getTransience() == getTransience();
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (getTransience() > 0) {
            result += "(" + getTransience() + ")";
        }
        return result;
    }
}
