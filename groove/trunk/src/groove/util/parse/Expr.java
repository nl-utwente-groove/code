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
import groove.util.line.Line;
import groove.util.parse.OpKind.Direction;
import groove.util.parse.OpKind.Placement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * General expression type.
 * @param <O> the type for the operators
 * @author Arend Rensink
 * @version $Id$
 */
public class Expr<O extends Op> implements Fallible {
    /**
     * Constructs an initially argument- and content-free expression
     * with a given top-level operator.
     */
    protected Expr(O op) {
        assert op != null;
        this.op = op;
        this.args = new ArrayList<Expr<O>>();
        this.errors = new FormatErrorSet();
    }

    /** Returns the top-level operator of this expression. */
    public O getOp() {
        return this.op;
    }

    private final O op;

    /** Sets a top-level constant for this expression. */
    public void setConstant(Constant constant) {
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

    /** Adds an argument to this expression. */
    public void addArg(Expr<O> arg) {
        this.args.add(arg);
        addErrors(arg.getErrors());
    }

    /** Returns an unmodifiable view on the list of arguments of this expression. */
    public List<Expr<O>> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    private final List<Expr<O>> args;

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
        this.errors.add(error);
    }

    @Override
    public void addErrors(Set<FormatError> errors) {
        this.errors.addAll(errors);
    }

    @Override
    public void addErrors(FormatException exc) {
        addErrors(exc.getErrors());
    }

    private final FormatErrorSet errors;

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
        return toLine(OpKind.NONE, spaces);
    }

    /**
     * Builds the display string for this expression in the
     * result parameter.
     * @param spaces if {@code true}, spaces are introduced for readability
     */
    private Line toLine(OpKind context, boolean spaces) {
        if (getOp().getKind() == OpKind.CALL) {
            return toCallLine(spaces);
        } else if (getOp().getKind() == OpKind.ATOM) {
            if (hasId()) {
                return getId().toLine();
            } else {
                return Line.atom(getConstant().toDisplayString());
            }
        } else {
            return toFixLine(context, spaces);
        }
    }

    /** Builds a display string for an operator without symbol.
     * @param spaces if {@code true}, spaces are introduced for readability */
    private Line toCallLine(boolean spaces) {
        List<Line> result = new ArrayList<Line>();
        result.add(getId().toLine());
        result.add(Line.atom("("));
        boolean firstArg = true;
        for (Expr<?> arg : getArgs()) {
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
    public String toParsableString() {
        if (this.parseString == null) {
            return toLine().toFlatString();
        } else {
            return this.parseString;
        }
    }

    /**
     * Sets the parse string for this expression.
     * @param parseString the complete parse string
     * @see #toParsableString()
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
        if (!(obj instanceof Expr)) {
            return false;
        }
        Expr<?> other = (Expr<?>) obj;
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
