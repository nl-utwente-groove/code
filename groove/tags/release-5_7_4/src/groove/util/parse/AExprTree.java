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
package groove.util.parse;

import groove.algebra.Constant;
import groove.grammar.QualName;

/**
 * Abstract expression tree, to be instantiated with an operator type.
 * @param <O> the operator type for the expressions
 * @param <T> should be set to the implementing type itself (this is the closes you can get to
 * a MyType in Java)
 * @author rensink
 * @version $Revision $
 */
public abstract class AExprTree<O extends Op,T extends AExprTree<O,T>> extends ATermTree<O,T> {
    /**
     * Constructs an expression tree with a given top-level operator.
     */
    protected AExprTree(O op) {
        super(op);
    }

    /** Sets a top-level constant for this expression. */
    public void setConstant(Constant constant) {
        assert!isFixed();
        assert this.op.getKind() == OpKind.ATOM;
        assert!hasId();
        this.constant = constant;
    }

    /** Indicates if this expression contains constant content. */
    public boolean hasConstant() {
        return getConstant() != null;
    }

    /** Returns the constant wrapped in this expression, if any. */
    public Constant getConstant() {
        return this.constant;
    }

    private Constant constant;

    /** Sets a top-level identifier for this expression. */
    public void setId(QualName id) {
        assert!isFixed();
        assert this.op.getKind() == OpKind.ATOM || this.op.getKind() == OpKind.CALL;
        assert!hasConstant();
        this.id = id;
    }

    /** Indicates if this expression contains a top-level identifier. */
    public boolean hasId() {
        return getId() != null;
    }

    /** Returns the identifier wrapped in this expression, if any. */
    public QualName getId() {
        return this.id;
    }

    private QualName id;

    /** Returns a string representation of the top-level content of this tree. */
    @Override
    protected String toAtomString() {
        if (hasConstant()) {
            return getConstant().getSymbol();
        } else if (hasId()) {
            return getId().toString();
        } else {
            return "";
        }
    }

    @Override
    public T clone() {
        T result = super.clone();
        AExprTree<O,T> upcast = result;
        upcast.id = this.id;
        upcast.constant = this.constant;
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.constant == null) ? 0 : this.constant.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        AExprTree<?,?> other = (AExprTree<?,?>) obj;
        assert other != null; // guaranteed by !super.equals
        if (this.constant == null) {
            if (other.constant != null) {
                return false;
            }
        } else if (!this.constant.equals(other.constant)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
