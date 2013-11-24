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
package groove.algebra.syntax;

import groove.algebra.Precedence;
import groove.algebra.SignatureKind;

import java.util.Collections;
import java.util.Map;

/**
 * Parameter expression.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Parameter extends Expression {
    /** Constructs a new parameter. */
    public Parameter(int nr, SignatureKind type) {
        assert nr >= 0;
        this.nr = nr;
        this.type = type;
    }

    @Override
    public SignatureKind getSignature() {
        return this.type;
    }

    @Override
    protected void buildDisplayString(StringBuilder result, Precedence context) {
        result.append("$");
        result.append(getNumber());
    }

    /** Returns the parameter number. */
    public int getNumber() {
        return this.nr;
    }

    @Override
    public boolean isTerm() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    protected Map<String,SignatureKind> computeVarMap() {
        return Collections.emptyMap();
    }

    @Override
    public int hashCode() {
        return this.nr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Parameter)) {
            return false;
        }
        Parameter other = (Parameter) obj;
        return this.nr == other.nr;
    }

    @Override
    public String toString() {
        return getSignature() + ":" + toDisplayString();
    }

    private final int nr;
    private final SignatureKind type;
}
