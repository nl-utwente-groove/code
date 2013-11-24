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
import groove.algebra.Operator;
import groove.algebra.SignatureKind;
import groove.grammar.type.TypeLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Proper" term, consisting of an operator applied to other terms.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CallExpr extends Expression {
    /** Constructs a term from a given operator and list of arguments. */
    public CallExpr(Operator op, List<Expression> args) {
        this.op = op;
        this.args = new ArrayList<Expression>(args);
        assert isTypeCorrect() : String.format("%s is not a type correct term",
            toString());
    }

    /** Constructs a term from a given operator and sequence of arguments. */
    public CallExpr(Operator op, Expression... args) {
        this(op, Arrays.asList(args));
    }

    /* A call expression is a term if all its arguments are terms. */
    @Override
    public boolean isTerm() {
        boolean result = true;
        for (Expression arg : getArgs()) {
            if (!arg.isTerm()) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    protected Map<String,SignatureKind> computeVarMap() {
        Map<String,SignatureKind> result = new HashMap<String,SignatureKind>();
        for (Expression arg : getArgs()) {
            result.putAll(arg.getVariables());
        }
        return result;
    }

    @Override
    public SignatureKind getSignature() {
        return this.op.getResultType();
    }

    /** Returns the operator of this term. */
    public Operator getOperator() {
        return this.op;
    }

    /** Returns an unmodifiable view on the list of arguments of this term. */
    public List<Expression> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    @Override
    public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        CallExpr result = this;
        if (oldLabel.getRole() == BINARY) {
            List<Expression> newArgs = new ArrayList<Expression>();
            boolean isNew = false;
            for (int i = 0; i < getArgs().size(); i++) {
                Expression oldArg = getArgs().get(i);
                Expression newArg = oldArg.relabel(oldLabel, newLabel);
                newArgs.add(newArg);
                isNew |= newArg != oldArg;
            }
            if (isNew) {
                result = new CallExpr(getOperator(), newArgs);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.args.hashCode();
        result = prime * result + this.op.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CallExpr)) {
            return false;
        }
        CallExpr other = (CallExpr) obj;
        if (!this.op.equals(other.op)) {
            return false;
        }
        if (!this.args.equals(other.args)) {
            return false;
        }
        return true;
    }

    @Override
    public String toDisplayString() {
        StringBuilder result = new StringBuilder(this.op.getName());
        result.append('(');
        boolean firstArg = true;
        for (Expression arg : getArgs()) {
            if (!firstArg) {
                result.append(", ");
            } else {
                firstArg = false;
            }
            result.append(arg.toDisplayString());
        }
        result.append(')');
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.op.getFullName());
        result.append('(');
        boolean firstArg = true;
        for (Expression arg : getArgs()) {
            if (!firstArg) {
                result.append(", ");
            } else {
                firstArg = false;
            }
            result.append(arg.toString());
        }
        result.append(')');
        return result.toString();
    }

    /** Method to check the type correctness of the operator/arguments combination. */
    private boolean isTypeCorrect() {
        int arity = this.op.getArity();
        List<SignatureKind> argTypes = this.op.getParamTypes();
        if (arity != getArgs().size()) {
            return false;
        }
        for (int i = 0; i < arity; i++) {
            if (getArgs().get(i).getSignature() != argTypes.get(i)) {
                return false;
            }
        }
        return true;
    }

    private final Operator op;
    private final List<Expression> args;
}
