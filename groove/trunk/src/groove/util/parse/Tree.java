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
import groove.util.DefaultFixable;
import groove.util.Pair;
import groove.util.line.Line;
import groove.util.parse.OpKind.Direction;
import groove.util.parse.OpKind.Placement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * General expression type.
 * @param <O> the type for the operators
 * @author Arend Rensink
 * @version $Id$
 */
public class Tree<O extends Op,T extends Tree<O,T>> extends DefaultFixable implements Fallible {
    /**
     * Constructs an initially argument- and content-free expression
     * with a given top-level operator.
     */
    protected Tree(O op) {
        assert op != null;
        this.op = op;
        this.args = new ArrayList<T>();
        this.errors = new FormatErrorSet();
    }

    /** Returns the top-level operator of this expression. */
    public O getOp() {
        return this.op;
    }

    private final O op;

    /** Sets a top-level constant for this expression. */
    public void setConstant(Constant constant) {
        assert !isFixed();
        assert this.op.getKind() == OpKind.ATOM;
        assert !hasId();
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
    public void setId(Id id) {
        assert !isFixed();
        assert this.op.getKind() == OpKind.ATOM || this.op.getKind() == OpKind.CALL;
        assert !hasConstant();
        this.id = id;
    }

    /** Indicates if this expression contains a top-level identifier. */
    public boolean hasId() {
        return getId() != null;
    }

    /** Returns the identifier wrapped in this expression, if any. */
    public Id getId() {
        return this.id;
    }

    private Id id;

    /** Returns a string representation of the top-level content of this tree. */
    public String getContentString() {
        if (hasConstant()) {
            return getConstant().getSymbol();
        } else if (hasId()) {
            return getId().getName();
        } else {
            return "";
        }
    }

    /** Adds an argument to this expression. */
    public void addArg(T arg) {
        assert !isFixed();
        this.args.add(arg);
    }

    /** Retrieves the argument at a given position. */
    public T getArg(int index) {
        return this.args.get(index);
    }

    /** Returns an unmodifiable view on the list of arguments of this expression. */
    public List<T> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    private final List<T> args;

    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    @Override
    public void addError(FormatError error) {
        assert !isFixed();
        this.errors.add(error);
    }

    @Override
    public void addErrors(Set<FormatError> errors) {
        assert !isFixed();
        this.errors.addAll(errors);
    }

    @Override
    public void addErrors(FormatException exc) {
        assert !isFixed();
        addErrors(exc.getErrors());
    }

    private final FormatErrorSet errors;

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            if (!hasErrors() && getOp().getArity() >= 0 && getOp().getArity() != getArgs().size()) {
                addError(new FormatError("Operator '%s' expects %s but has %s operands in %s",
                    getOp().getSymbol(), getOp().getArity(), getArgs().size(), getParseString()));
            }
            for (T arg : getArgs()) {
                arg.setFixed();
                addErrors(arg.getErrors());
            }
            super.setFixed();
        }
        return result;
    }

    /** Returns a string representation of the syntax tree of the formula. */
    public final String toTreeString() {
        assert isFixed();
        StringBuilder result = new StringBuilder();
        toTree(new Stack<Pair<Integer,Boolean>>(), result);
        result.append('\n');
        return result.toString();
    }

    private final void toTree(Stack<Pair<Integer,Boolean>> indent, StringBuilder result) {
        if (getArgs().size() > 0) {
            String symbol = getOp().hasSymbol() ? getOp().getSymbol() : getId().getName();
            result.append(symbol);
            result.append(getArgs().size() == 1 ? " --- " : " +-- ");
            int i;
            for (i = 0; i < getArgs().size() - 1; i++) {
                indent.push(Pair.newPair(symbol.length(), true));
                getArg(i).toTree(indent, result);
                result.append('\n');
                addIndent(indent, result);
                indent.pop();
            }
            indent.push(Pair.newPair(symbol.length(), false));
            getArg(i).toTree(indent, result);
            indent.pop();
        } else if (getOp().getKind() == OpKind.ATOM) {
            result.append(getContentString());
        }
    }

    private static final void addIndent(Stack<Pair<Integer,Boolean>> indent, StringBuilder result) {
        for (int i = 0; i < indent.size(); i++) {
            Pair<Integer,Boolean> p = indent.get(i);
            for (int s = 0; s < p.one(); s++) {
                result.append(" ");
            }
            result.append(p.two() ? (i == indent.size() - 1 ? " +-- " : " |   ") : "     ");
        }
    }

    /** Returns a formatted line representation of this expression,
     * without spaces for readability.
     */
    public Line toLine() {
        return toLine(false);
    }

    /** Returns a formatted line representation of this expression,
     * with optional spaces for readability.
     * @param spaces if {@code true}, spaces are introduced for readability
     */
    public Line toLine(boolean spaces) {
        assert isFixed();
        return toLine(OpKind.NONE, spaces);
    }

    /**
     * Builds the display string for this expression in the
     * result parameter.
     * @param spaces if {@code true}, spaces are introduced for readability
     */
    private Line toLine(OpKind context, boolean spaces) {
        Line result;
        if (getOp().getKind() == OpKind.CALL) {
            result = toCallLine(spaces);
        } else if (getOp().getKind() == OpKind.ATOM) {
            if (hasId()) {
                result = getId().toLine();
            } else {
                result = Line.atom(getConstant().toDisplayString());
            }
        } else {
            result = toFixLine(context, spaces);
        }
        return result;
    }

    /** Builds a display string for an operator without symbol.
     * @param spaces if {@code true}, spaces are introduced for readability */
    private Line toCallLine(boolean spaces) {
        List<Line> result = new ArrayList<Line>();
        result.add(hasId() ? getId().toLine() : Line.atom(getOp().getSymbol()));
        result.add(Line.atom("("));
        boolean firstArg = true;
        for (T arg : getArgs()) {
            if (!firstArg) {
                result.add(Line.atom(spaces ? ", " : ","));

            } else {
                firstArg = false;
            }
            result.add(arg.toLine(OpKind.NONE, spaces));
        }
        result.add(Line.atom(")"));
        return Line.composed(result);
    }

    /** Builds a display string for an operator with an infix or prefix symbol.
     * @param spaces if {@code true}, spaces are introduced for readability */
    private Line toFixLine(OpKind context, boolean spaces) {
        List<Line> result = new ArrayList<Line>();
        OpKind me = getOp().getKind();
        boolean addPars = me.compareTo(context) < 0;
        boolean addSpaces = spaces && me.compareTo(OpKind.MULT) < 0;
        int nextArgIx = 0;
        if (addPars) {
            result.add(Line.atom("("));
        }
        if (me.getPlace() != Placement.PREFIX) {
            // add left argument
            result.add(this.args.get(nextArgIx).toLine(
                me.getDirection() == Direction.LEFT ? me : me.increase(), spaces));
            nextArgIx++;
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
        }
        result.add(Line.atom(getOp().getSymbol()));
        if (me.getPlace() != Placement.POSTFIX) {
            // add left argument
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
            result.add(this.args.get(nextArgIx).toLine(
                me.getDirection() == Direction.RIGHT ? me : me.increase(), spaces));
            nextArgIx++;
        }
        if (addPars) {
            result.add(Line.atom(")"));
        }
        return Line.composed(result);
    }

    /** Returns the string from which this expression was parsed, if any. */
    public String getParseString() {
        if (this.parseString == null) {
            return toLine().toFlatString();
        } else {
            return this.parseString;
        }
    }

    /**
     * Sets the parse string for this expression.
     * @param parseString the complete parse string
     * @see #getParseString()
     */
    public void setParseString(String parseString) {
        this.parseString = parseString;
    }

    private String parseString;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.op.hashCode();
        result = prime * result + this.args.hashCode();
        result = prime * result + this.errors.hashCode();
        result = prime * result + ((this.constant == null) ? 0 : this.constant.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tree)) {
            return false;
        }
        Tree<?,?> other = (Tree<?,?>) obj;
        if (!this.op.equals(other.op)) {
            return false;
        }
        if (!this.args.equals(other.args)) {
            return false;
        }
        if (!this.errors.equals(other.errors)) {
            return false;
        }
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

    @Override
    public String toString() {
        String result = this.op.toString();
        if (hasId()) {
            result += getId();
        } else if (hasConstant()) {
            result += "<" + getConstant() + ">";
        }
        return result + getArgs();
    }
}
