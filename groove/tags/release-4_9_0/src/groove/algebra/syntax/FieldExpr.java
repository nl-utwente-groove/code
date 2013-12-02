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

import static groove.graph.EdgeRole.BINARY;
import groove.algebra.Precedence;
import groove.algebra.SignatureKind;
import groove.grammar.type.TypeLabel;

import java.util.Collections;
import java.util.Map;

/**
 * Expression consisting of a target (a node ID) and a field name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FieldExpr extends Expression {
    /** Constructs a new field expression. */
    public FieldExpr(String target, String field, SignatureKind type) {
        assert field != null && target != null && type != null;
        this.target = target;
        this.field = field;
        this.type = type;
    }

    @Override
    public SignatureKind getSignature() {
        return this.type;
    }

    /** Returns the target of this field expression. */
    public String getTarget() {
        return this.target;
    }

    /** Returns the name of this field expression. */
    public String getField() {
        return this.field;
    }

    @Override
    public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        if (oldLabel.getRole() == BINARY && oldLabel.text().equals(this.field)) {
            return new FieldExpr(getTarget(), newLabel.text(), getSignature());
        } else {
            return this;
        }
    }

    @Override
    protected void buildDisplayString(StringBuilder result, Precedence context) {
        result.append(getTarget());
        result.append(".");
        result.append(getField());
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FieldExpr)) {
            return false;
        }
        FieldExpr other = (FieldExpr) obj;
        if (this.type != other.type) {
            return false;
        }
        if (!this.target.equals(other.target)) {
            return false;
        }
        return this.field.equals(other.field);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.field.hashCode();
        result = prime * result + this.target.hashCode();
        result = prime * result + this.type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getSignature() + ":" + toDisplayString();
    }

    private final String target;
    private final String field;
    private final SignatureKind type;
}
