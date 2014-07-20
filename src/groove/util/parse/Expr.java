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

import groove.algebra.SignatureKind;
import groove.util.Triple;
import groove.util.line.Line;
import groove.util.parse.OpKind.Direction;
import groove.util.parse.OpKind.Placement;

import java.math.BigDecimal;
import java.math.BigInteger;
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
     * Constructs an initially argument-free expression with a given top-level operator
     * and {@code null} content.
     */
    public Expr(O op) {
        this(op, null);
    }

    /**
     * Constructs an initially argument-free expression with a given top-level operator
     * and given (possibly {@code null}) content.
     */
    public Expr(O op, Content<?> content) {
        this.op = op;
        this.args = new ArrayList<Expr<O>>();
        this.errors = new FormatErrorSet();
        this.content = content;
    }

    /** Returns the top-level operator of this expression. */
    public O getOp() {
        return this.op;
    }

    private final O op;

    /** Indicates if this expression has non-{@code null} top-level content. */
    public boolean hasContent() {
        return getContent() != null;
    }

    /** Returns the top-level content of this expression, if any. */
    public Content<?> getContent() {
        return this.content;
    }

    private final Content<?> content;

    /** Adds an argument to this expression. */
    public void addArg(Expr<O> arg) {
        assert this.args.size() < getOp().getArity();
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
        errors.addAll(errors);
    }

    @Override
    public void addErrors(FormatException exc) {
        addErrors(exc.getErrors());
    }

    private final FormatErrorSet errors;

    /** Returns a formatted line representation of this expression. */
    public Line toLine() {
        return toLine(OpKind.NONE);
    }

    /**
     * Builds the display string for this expression in the
     * result parameter.
     */
    private Line toLine(OpKind context) {
        if (getOp().getKind() == OpKind.CALL) {
            return toCallLine();
        } else {
            return toFixLine(context);
        }
    }

    /** Builds a display string for an operator without symbol. */
    private Line toCallLine() {
        List<Line> result = new ArrayList<Line>();
        result.add(Line.atom(this.op.getSymbol() + '('));
        boolean firstArg = true;
        for (Expr<?> arg : getArgs()) {
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
    private Line toFixLine(OpKind context) {
        List<Line> result = new ArrayList<Line>();
        OpKind me = getOp().getKind();
        boolean addPars = me.compareTo(context) < 0;
        boolean addSpaces = me.compareTo(OpKind.MULT) < 0;
        int nextArgIx = 0;
        if (addPars) {
            result.add(Line.atom("("));
        }
        if (me.getPlace() != Placement.PREFIX) {
            // add left argument
            result.add(this.args.get(nextArgIx).toLine(
                me.getDirection() == Direction.LEFT ? me : me.increase()));
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
                me.getDirection() == Direction.RIGHT ? me : me.increase()));
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
        result = prime * result + (hasContent() ? getContent().hashCode() : 0);
        result = prime * result + this.args.hashCode();
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
        if (hasContent()) {
            if (!other.hasContent()) {
                return false;
            }
            if (!other.getContent().equals(getContent())) {
                return false;
            }
        } else if (!other.hasContent()) {
            return false;
        }
        if (!this.args.equals(other.args)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.op.toString() + (hasContent() ? "(" + getContent() + ")" : "") + this.args;
    }

    /** The singleton null content instance. */
    public static final NullContent NULL = new NullContent();

    /** Creates expression content for an algebraic constant. */
    public static final Content<?> createContent(SignatureKind sig, String payload) {
        switch (sig) {
        case BOOL:
            return new BoolContent(payload);
        case INT:
            return new IntContent(payload);
        case REAL:
            return new RealContent(payload);
        case STRING:
            return new StringContent(payload);
        }
        assert false;
        return null;
    }

    /**
     * Top-level content of an expression.
     * @author Arend Rensink
     * @version $Id$
     */
    abstract public static class Content<P> extends Triple<ContentKind,String,P> {
        /** Constructs content of a given kind. */
        protected Content(ContentKind kind, String parseString, P payload) {
            super(kind, parseString, payload);
        }

        /** Returns the kind of content. */
        public ContentKind getKind() {
            return one();
        }

        /** Returns the payload of this content object. */
        public P getPayload() {
            return three();
        }

        /** Indicates if this content has a data payload. */
        public boolean hasSig() {
            return getSig() != null;
        }

        /** Returns the signature of the data payload. */
        public SignatureKind getSig() {
            return getKind().getSig();
        }
    }

    /** Null content. */
    public static class NullContent extends Content<Object> {
        /** Constructs the singleton instance of this content class. */
        private NullContent() {
            super(ContentKind.NULL, "", null);
        }
    }

    /** Identifier content. */
    public static class IdContent extends Content<Id> {
        /** Constructs a content object wrapping a given identifier. */
        public IdContent(String parseString, Id id) {
            super(ContentKind.ID, parseString, id);
        }

        /** Adds a name to the identifier wrapped in this content. */
        public void addName(String name) {
            getPayload().two().add(name);
        }
    }

    /** String content. */
    public static class StringContent extends Content<String> {
        /** Constructs a content object wrapping a given string. */
        public StringContent(String value) {
            super(ContentKind.STRING, value, toUnquoted(value));
        }

        private static String toUnquoted(String value) {
            try {
                return StringHandler.toUnquoted(value);
            } catch (FormatException exc) {
                throw new IllegalArgumentException();
            }
        }
    }

    /** Boolean content. */
    public static class BoolContent extends Content<Boolean> {
        /** Constructs a content object wrapping a given boolean value represented as a string. */
        public BoolContent(String value) {
            super(ContentKind.BOOL, value, new Boolean(value));
        }
    }

    /** Real-number content. */
    public static class RealContent extends Content<BigDecimal> {
        /** Constructs a content object wrapping a given real value represented as a string. */
        public RealContent(String value) {
            super(ContentKind.REAL, value, new BigDecimal(value));
        }
    }

    /** Integer content. */
    public static class IntContent extends Content<BigInteger> {
        /** Constructs a content object wrapping a given integer value represented as a string. */
        public IntContent(String value) {
            super(ContentKind.INT, value, new BigInteger(value));
        }
    }

    /**
     * Kind of top-level expression content.
     * @author Arend Rensink
     * @version $Id$
     */
    public static enum ContentKind {
        /** Identifier. */
        ID,
        /** Identifier. */
        NULL,
        /** Boolean content. */
        BOOL(SignatureKind.STRING),
        /** Integer content. */
        INT(SignatureKind.STRING),
        /** Real-number content. */
        REAL(SignatureKind.STRING),
        /** String content. */
        STRING(SignatureKind.STRING), ;

        private ContentKind() {
            this(null);
        }

        private ContentKind(SignatureKind sig) {
            this.sig = sig;
        }

        /** Returns the signature kind of this content, if any. */
        public SignatureKind getSig() {
            return this.sig;
        }

        private final SignatureKind sig;
    }
}
