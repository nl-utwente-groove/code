/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.algebra;

/** A constant symbol for a particular signature. */
public class Constant implements Term {
    /**
     * Constructs a new constant from a given signature and 
     * constant symbol.
     * The parameters are required to satisfy {@link Algebras#isConstant(SignatureKind, String)}.
     */
    Constant(SignatureKind signature, String symbol) {
        this.signature = signature;
        this.symbol = symbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.signature.hashCode();
        result = prime * result + this.symbol.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Constant)) {
            return false;
        }
        Constant other = (Constant) obj;
        if (!this.signature.equals(other.signature)) {
            return false;
        }
        if (!this.symbol.equals(other.symbol)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getSignature() + ":" + getSymbol();
    }

    @Override
    public final SignatureKind getSignature() {
        return this.signature;
    }

    /** Returns the constant symbol. */
    public final String getSymbol() {
        return this.symbol;
    }

    private final SignatureKind signature;
    private final String symbol;
}
