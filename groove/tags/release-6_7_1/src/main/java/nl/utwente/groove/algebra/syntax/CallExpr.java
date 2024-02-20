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
package nl.utwente.groove.algebra.syntax;

import static nl.utwente.groove.graph.EdgeRole.BINARY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.OpKind;
import nl.utwente.groove.util.parse.OpKind.Direction;
import nl.utwente.groove.util.parse.OpKind.Placement;

/**
 * "Proper" term, consisting of an operator applied to other terms.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class CallExpr extends Expression {
    /** Constructs a term from a given operator and list of arguments. */
    public CallExpr(boolean prefixed, Operator op, List<Expression> args) {
        super(prefixed);
        this.op = op;
        this.args = new ArrayList<>(args);
        assert isTypeCorrect() : String.format("%s is not a type correct term", toString());
    }

    /**
     * Constructs a term from a given operator and sequence of arguments.
     * The term will be considered type prefixed if the operator is ambiguous.
     * @see #isPrefixed()
     * @see Operator#isAmbiguous()
     */
    public CallExpr(Operator op, Expression... args) {
        this(op.isAmbiguous(), op, Arrays.asList(args));
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
    protected SortMap computeTyping() {
        SortMap result = new SortMap();
        getArgs().stream().forEach(a -> result.add(a.getTyping()));
        return result;
    }

    @Override
    public Sort getSort() {
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
            List<Expression> newArgs = new ArrayList<>();
            boolean isNew = false;
            for (int i = 0; i < getArgs().size(); i++) {
                Expression oldArg = getArgs().get(i);
                Expression newArg = oldArg.relabel(oldLabel, newLabel);
                newArgs.add(newArg);
                isNew |= newArg != oldArg;
            }
            if (isNew) {
                result = new CallExpr(isPrefixed(), getOperator(), newArgs);
            }
        }
        return result;
    }

    @Override
    public @NonNull Expression bind(Function<Variable,Object> bindMap) {
        List<Expression> newArgs = new ArrayList<>();
        getArgs().stream().map(a -> a.bind(bindMap)).forEach(newArgs::add);
        return new CallExpr(isPrefixed(), getOperator(), newArgs);
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
        if (!(obj instanceof CallExpr other)) {
            return false;
        } else {
            if (!this.op.equals(other.op)) {
                return false;
            }
            if (!this.args.equals(other.args)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Line toLine(OpKind context) {
        if (getOperator().getSymbol() == null) {
            return toCallLine();
        } else {
            return toFixLine(context);
        }
    }

    /** Builds a display string for an operator without symbol. */
    private @NonNull Line toCallLine() {
        List<Line> result = new ArrayList<>();
        result.add(Line.atom(this.op.getName() + '('));
        boolean firstArg = true;
        for (Expression arg : getArgs()) {
            if (!firstArg) {
                result.add(Line.atom(", "));
            } else {
                firstArg = false;
            }
            result.add(arg.toLine(OpKind.NONE));
        }
        result.add(Line.atom(")"));
        return Line.composed(result);
    }

    /** Builds a display string for an operator with an infix or prefix symbol. */
    private @NonNull Line toFixLine(OpKind context) {
        List<Line> result = new ArrayList<>();
        OpKind me = getOperator().getKind();
        boolean addPars = me.compareTo(context) < 0;
        boolean addSpaces = me.compareTo(OpKind.MULT) < 0;
        int nextArgIx = 0;
        if (addPars) {
            result.add(Line.atom("("));
        }
        if (me.getPlace() != Placement.PREFIX) {
            // add left argument
            result
                .add(this.args
                    .get(nextArgIx)
                    .toLine(me.getDirection() == Direction.LEFT
                        ? me
                        : me.increase()));
            nextArgIx++;
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
        }
        result.add(Line.atom(getOperator().getSymbol()));
        if (me.getPlace() != Placement.POSTFIX) {
            // add left argument
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
            result
                .add(this.args
                    .get(nextArgIx)
                    .toLine(me.getDirection() == Direction.RIGHT
                        ? me
                        : me.increase()));
            nextArgIx++;
        }
        if (addPars) {
            result.add(Line.atom(")"));
        }
        return Line.composed(result);
    }

    @Override
    protected String createParseString() {
        StringBuilder result = new StringBuilder();
        if (isPrefixed()) {
            result.append(getOperator().getFullName());
        } else {
            result.append(getOperator().getName());
        }
        result.append('(');
        boolean firstArg = true;
        for (Expression arg : getArgs()) {
            if (!firstArg) {
                result.append(", ");
            } else {
                firstArg = false;
            }
            result.append(arg.toParseString());
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
        var args = getArgs();
        var argTypes = this.op.getParamTypes();
        if (!this.op.allowsArgCount(args.size())) {
            return false;
        }
        if (this.op.isVarArgs()) {
            for (var arg : args) {
                if (arg.getSort() != argTypes.get(0)) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i).getSort() != argTypes.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private final Operator op;
    private final List<Expression> args;
}
