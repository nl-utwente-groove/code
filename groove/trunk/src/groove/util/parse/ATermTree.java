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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.annotation.NonNull;

import groove.util.DefaultFixable;
import groove.util.Pair;
import groove.util.line.Line;
import groove.util.parse.OpKind.Direction;
import groove.util.parse.OpKind.Placement;

/**
 * General abstract term tree type, parameterised by the operator type.
 * The type does not offer support for structured atoms; this should
 * be dealt with by subtypes.
 * @param <O> the type for the operators
 * @param <T> the tree type itself
 * @author Arend Rensink
 * @version $Id$
 */
abstract public class ATermTree<O extends Op,T extends ATermTree<O,T>> extends DefaultFixable
    implements Fallible, Cloneable {
    /**
     * Constructs an initially argument- and content-free expression
     * with a given top-level operator.
     */
    protected ATermTree(O op) {
        assert op != null && op.getKind() != OpKind.NONE;
        this.op = op;
        this.args = new ArrayList<>();
        this.errors = new FormatErrorSet();
    }

    /** Returns the top-level operator of this expression. */
    public O getOp() {
        return this.op;
    }

    /** Operator of this term tree node. */
    protected final O op;

    /** Adds an argument to this expression. */
    public void addArg(T arg) {
        assert !isFixed();
        this.args.add(arg);
    }

    /** Retrieves the argument at a given position. */
    public T getArg(int index) {
        return this.args.get(index);
    }

    /** Returns the argument at a given position, upcast to the {@code TermTree} supertype. */
    private ATermTree<O,T> getUpArg(int index) {
        return getArg(index);
    }

    /** Returns an unmodifiable view on the list of arguments of this expression. */
    public List<T> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    private final List<T> args;

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors;

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            if (!hasErrors() && getOp().getArity() >= 0 && getOp().getArity() != getArgs().size()) {
                getErrors().add("Operator '%s' expects %s but has %s operands in %s",
                    getOp().getSymbol(),
                    getOp().getArity(),
                    getArgs().size(),
                    getParseString());
            }
            for (T arg : getArgs()) {
                arg.setFixed();
                getErrors().addAll(arg.getErrors());
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
            String symbol = getOp().getSymbol();
            result.append(symbol);
            result.append(getArgs().size() == 1 ? " --- " : " +-- ");
            int i;
            for (i = 0; i < getArgs().size() - 1; i++) {
                indent.push(Pair.newPair(symbol.length(), true));
                getUpArg(i).toTree(indent, result);
                result.append('\n');
                addIndent(indent, result);
                indent.pop();
            }
            indent.push(Pair.newPair(symbol.length(), false));
            getUpArg(i).toTree(indent, result);
            indent.pop();
        } else if (getOp().getKind() == OpKind.ATOM) {
            result.append(toAtomString());
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
        return hasErrors() ? Line.atom(toString()) : toLine(OpKind.NONE, spaces);
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
            result = toAtomLine(spaces);
        } else {
            result = toFixLine(context, spaces);
        }
        return result;
    }

    /** Callback method to build the display string for this atomic term.
      * @param spaces flag indicating if spaces should be used for layout.
      */
    protected Line toAtomLine(boolean spaces) {
        assert getOp().getKind() == OpKind.ATOM;
        Line result;
        if (getOp().hasSymbol()) {
            result = Line.atom(getOp().getSymbol());
        } else {
            result = Line.atom(toAtomString());
        }
        return result;
    }

    /** Builds a display string for an operator without symbol.
     * @param spaces if {@code true}, spaces are introduced for readability */
    private Line toCallLine(boolean spaces) {
        assert getOp().getKind() == OpKind.CALL;
        List<Line> result = new ArrayList<>();
        result.add(Line.atom(getOp().getSymbol()));
        result.add(Line.atom("("));
        boolean firstArg = true;
        for (ATermTree<O,T> arg : getArgs()) {
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
        List<Line> result = new ArrayList<>();
        OpKind me = getOp().getKind();
        boolean addPars = me.compareTo(context) < 0;
        boolean addSpaces = spaces && me.compareTo(OpKind.MULT) < 0;
        int nextArgIx = 0;
        if (addPars) {
            result.add(Line.atom("("));
        }
        if (me.getPlace() != Placement.PREFIX) {
            // add left argument
            result.add(getUpArg(nextArgIx)
                .toLine(me.getDirection() == Direction.LEFT ? me : me.increase(), spaces));
            nextArgIx++;
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
        }
        result.add(toOpLine(addSpaces));
        if (me.getPlace() != Placement.POSTFIX) {
            // add left argument
            if (addSpaces) {
                result.add(Line.atom(" "));
            }
            result.add(getUpArg(nextArgIx)
                .toLine(me.getDirection() == Direction.RIGHT ? me : me.increase(), spaces));
            nextArgIx++;
        }
        if (addPars) {
            result.add(Line.atom(")"));
        }
        return Line.composed(result);
    }

    /** Returns the display line for the top-level operator of this tree.
     * @param spaces if {@code true}, additional space may already have been
     * added to the left and/or right of the operator.
     */
    protected Line toOpLine(boolean spaces) {
        return Line.atom(getOp().getSymbol());
    }

    /** Returns the string from which this expression was parsed, if any. */
    public @NonNull String getParseString() {
        String result = this.parseString;
        if (result == null) {
            result = toLine().toFlatString();
        }
        return result;
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
    public T clone() {
        T result = createTree(getOp());
        ATermTree<O,T> upcast = result;
        upcast.args.addAll(this.args);
        upcast.errors.addAll(this.errors);
        return result;
    }

    /** Callback factory method for an otherwise empty tree with a given top-level operator. */
    public abstract T createTree(O op);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.op.hashCode();
        result = prime * result + this.args.hashCode();
        result = prime * result + this.errors.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ATermTree)) {
            return false;
        }
        ATermTree<?,?> other = (ATermTree<?,?>) obj;
        if (!this.op.equals(other.op)) {
            return false;
        }
        if (!this.args.equals(other.args)) {
            return false;
        }
        if (!this.errors.equals(other.errors)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result;
        if (hasErrors()) {
            result =
                String.format("Parse errors in '%s': %s", getParseString(), getErrors().toString());
        } else if (getOp().getKind() == OpKind.ATOM) {
            result = getOp().hasSymbol() ? getOp().getSymbol() : toAtomString();
        } else {
            result = this.op.toString();
            List<T> args = getArgs();
            return result + (args.isEmpty() ? "" : args);
        }
        return result;
    }

    /** Returns a string representation of this tree, assuming it is an atom without symbol. */
    protected String toAtomString() {
        assert getOp().getKind() == OpKind.ATOM && !getOp().hasSymbol();
        throw new UnsupportedOperationException(
            "This tree type does not support atoms without symbol");
    }
}
