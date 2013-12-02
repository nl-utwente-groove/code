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
 * Algebraic variable.
 * @author Rensink
 * @version $Revision $
 */
public class Variable extends Expression {
    /** Constructs a new variable with a given name and signature. */
    public Variable(String name, SignatureKind signature) {
        this.signature = signature;
        this.name = name;
    }

    /** Returns the name of this variable. */
    public String getName() {
        return this.name;
    }

    @Override
    protected void buildDisplayString(StringBuilder result, Precedence context) {
        result.append(getName());
    }

    @Override
    public SignatureKind getSignature() {
        return this.signature;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean isTerm() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Map<String,SignatureKind> computeVarMap() {
        return Collections.singletonMap(getName(), getSignature());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) obj;
        if (!getName().equals(other.getName())) {
            return false;
        }
        assert getSignature() == other.getSignature();
        return true;
    }

    @Override
    public String toString() {
        return getSignature().getName() + ":" + getName();
    }

    /** The name of this variable. */
    private final String name;
    /** The signature of this variable. */
    private final SignatureKind signature;
}
