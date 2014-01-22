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
 * Deadlock.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DeltaTerm extends Term {
    /**
     * Constructs a delta term.
     */
    public DeltaTerm(TermPool pool, int depth) {
        super(pool, Term.Op.DELTA);
        this.depth = depth;
    }

    @Override
    protected DerivationList computeAttempt() {
        return null;
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
    public int hashCode() {
        int prime = 31;
        return prime * super.hashCode() + getDepth();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((DeltaTerm) obj).depth == this.depth;
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (getDepth() > 0) {
            result += "(" + getDepth() + ")";
        }
        return result;
    }
}
