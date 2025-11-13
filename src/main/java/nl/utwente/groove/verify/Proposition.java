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
package nl.utwente.groove.verify;

import static nl.utwente.groove.verify.Proposition.Kind.CALL;
import static nl.utwente.groove.verify.Proposition.Kind.FLAG;
import static nl.utwente.groove.verify.Proposition.Kind.ID;
import static nl.utwente.groove.verify.Proposition.Kind.LABEL;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatErrorSet;

/** Proposition, wrapped inside a formula of type {@link LogicOp#PROP}. */
@NonNullByDefault
public class Proposition {
    /** Creates an identifier proposition.
     * @see Kind#ID
     */
    private Proposition(QualName id) {
        assert id != null;
        this.kind = ID;
        this.id = id;
        this.label = null;
        this.flag = null;
        this.args = null;
    }

    /** Creates call proposition.
     * @see Kind#CALL
     */
    private Proposition(QualName id, List<Arg> args) {
        assert id != null;
        assert args != null;
        this.kind = CALL;
        this.id = id;
        this.label = null;
        this.flag = null;
        this.args = args;
    }

    /** Creates a label proposition.
     * @see Kind#LABEL
     */
    private Proposition(String label) {
        assert label != null;
        this.id = null;
        this.args = null;
        if (label.startsWith(FLAG_PREFIX)) {
            this.kind = FLAG;
            this.label = null;
            var flag = this.flag = LTSLabels.flag(label);
            if (flag == null) {
                throw Exceptions.illegalArg("Label '%s' is not a flag", label);
            }
        } else {
            this.kind = LABEL;
            this.label = label;
            this.flag = null;
        }
    }

    /** Creates a flag proposition.
     * @see Kind#FLAG
     */
    private Proposition(Flag flag) {
        assert flag != null;
        this.kind = FLAG;
        this.id = null;
        this.label = null;
        this.flag = flag;
        this.args = null;
    }

    /** Returns the identifier in this proposition if the proposition is an {@link #ID} or {@link #CALL}
     * @throws UnsupportedOperationException if this proposition is not an {@link #ID} or {@link #CALL}
     */
    public QualName getId() {
        var result = this.id;
        if (result == null) {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    private final @Nullable QualName id;

    /** Returns the kind of this proposition. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /** Returns the constant in this proposition if the proposition is a {@link #LABEL}.
     * @throws UnsupportedOperationException if this proposition is not a {@link #LABEL}
     */
    public String getLabel() {
        var result = this.label;
        if (result == null) {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    private final @Nullable String label;

    /** Returns the constant in this proposition if the proposition is a {@link #FLAG}.
     * @throws UnsupportedOperationException if this proposition is not a {@link #FLAG}
     */
    public Flag getFlag() {
        var result = this.flag;
        if (result == null) {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    private final @Nullable Flag flag;

    /** Returns the array of call arguments.
     * @throws UnsupportedOperationException if this proposition is not a {@link #CALL}
     */
    public List<Arg> getArgs() {
        var result = this.args;
        if (result == null) {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    /** Returns the argument count of the call. */
    public int arity() {
        return getArgs().size();
    }

    private final @Nullable List<Arg> args;

    /**
     * Tests if this proposition matches another.
     * This is the case of the two are equal, or if this is a {@link #LABEL} or parameterless {@link #ID}
     * and the other a call of that action, or this is a call with wildcards and the other
     * a call that only differs in providing values for the wildcards.
     */
    public boolean matches(Proposition other) {
        boolean result;
        switch (getKind()) {
        case CALL:
            if (other.getKind() == CALL) {
                result = getId().equals(other.getId()) && other.arity() == arity();
                for (int i = 0; result && i < arity(); i++) {
                    result = getArgs().get(i).matches(other.getArgs().get(i));
                }
            } else {
                result = false;
            }
            break;
        case ID:
            result = equals(other);
            // An Id proposition without arguments matches all calls of that Id
            if (!result && other.getKind() == CALL) {
                result = getId().equals(other.getId());
            }
            break;
        case LABEL:
            result = getLabel().equals(other.toString());
            // A Label proposition without arguments matches all calls of that Id
            if (!result && other.getKind() == CALL) {
                result = getLabel().equals(other.getId().toString());
            }
            break;
        case FLAG:
            result = equals(other);
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.kind.hashCode();
        result = switch (this.kind) {
        case CALL -> {
            var tmp = prime * result + getId().hashCode();
            yield prime * tmp + getArgs().hashCode();
        }
        case ID -> prime * result + getId().hashCode();
        case LABEL -> prime * result + getLabel().hashCode();
        case FLAG -> prime * result + getFlag().hashCode();
        };
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Proposition other)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return switch (this.kind) {
        case CALL -> getId().equals(other.getId()) && getArgs().equals(other.getArgs());
        case ID -> getId().equals(other.getId());
        case LABEL -> getLabel().equals(other.getLabel());
        case FLAG -> getFlag().equals(other.getFlag());
        };
    }

    @Override
    public String toString() {
        return switch (getKind()) {
        case CALL -> {
            StringBuilder result = new StringBuilder(getId().toString());
            result.append('(');
            boolean first = true;
            for (Arg arg : getArgs()) {
                if (first) {
                    first = false;
                } else {
                    result.append(',');
                }
                result.append(arg);
            }
            result.append(')');
            yield result.toString();
        }
        case ID -> getId().toString();
        case LABEL -> getLabel();
        case FLAG -> getFlag().toString();
        };
    }

    /** Constructs a display line for this proposition.
     * @param spaces flag indicating if spaces should be used for layout.
     */
    public Line toLine(boolean spaces) {
        Line result;
        switch (getKind()) {
        case CALL:
            List<Line> lines = new ArrayList<>();
            lines.add(getId().toLine());
            lines.add(Line.atom("("));
            boolean firstArg = true;
            for (Arg arg : getArgs()) {
                if (!firstArg) {
                    lines
                        .add(Line
                            .atom(spaces
                                ? ", "
                                : ","));
                } else {
                    firstArg = false;
                }
                lines.add(arg.toLine());
            }
            lines.add(Line.atom(")"));
            result = Line.composed(lines);
            break;
        case LABEL:
            return Line.atom(getLabel());
        case ID:
            return getId().toLine();
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /** Checks if this proposition can be generated by the given grammar,
     * and returns a (possibly empty) set of errors found in this check. */
    FormatErrorSet computeErrors(Grammar grammar) {
        var result = new FormatErrorSet();
        // TODO implement
        return result;
    }

    /** Returns a {@link Kind#LABEL} proposition consisting of a given label text. */
    public static Proposition prop(String label) {
        return new Proposition(label);
    }

    /** Returns a {@link Kind#ID} proposition consisting of a given identifier. */
    public static Proposition prop(QualName id) {
        return new Proposition(id);
    }

    /** Returns a {@link Kind#CALL} proposition consisting of a given identifier and list of arguments. */
    public static Proposition prop(QualName id, List<Arg> args) {
        return new Proposition(id, args);
    }

    /** Returns a {@link Kind#FLAG} proposition for a given flag. */
    public static Proposition prop(Flag flag) {
        var result = flagPropMap.get(flag);
        if (result == null) {
            flagPropMap.put(flag, result = new Proposition(flag));
        }
        return result;
    }

    static private final EnumMap<Flag,@Nullable Proposition> flagPropMap
        = new EnumMap<>(Flag.class);

    /** Prefix for {@link #FLAG}-type propositions. */
    static public final String FLAG_PREFIX = "$";

    /**
     * Call argument.
     * Types of call arguments are given by {@link Arg#kind}.
     */
    public static class Arg {
        /** Constructs a new {@link ArgKind#NAME} argument. */
        private Arg(String name) {
            assert name != null;
            this.name = name;
            this.expr = null;
            this.kind = ArgKind.NAME;
        }

        /** Constructs a new {@link ArgKind#CONST} argument. */
        private Arg(Expression expr) {
            assert expr != null;
            this.name = null;
            this.expr = expr;
            this.kind = ArgKind.CONST;
        }

        /** Constructs a new {@link ArgKind#WILD} argument. */
        private Arg() {
            this.name = null;
            this.expr = null;
            this.kind = ArgKind.WILD;
        }

        /** Returns the identifier in this argument if the argument is a {@link ArgKind#NAME}.
        * @throws UnsupportedOperationException if this proposition is not a {@link ArgKind#NAME}
         */
        public String getName() {
            var result = this.name;
            if (result == null) {
                throw Exceptions.unsupportedOp();
            }
            return result;
        }

        /** Returns the constant in this argument if the argument is a {@link ArgKind#CONST}.
        * @throws UnsupportedOperationException if this proposition is not a {@link ArgKind#CONST}
         */
        public Expression getExpr() {
            var result = this.expr;
            if (result == null) {
                throw Exceptions.unsupportedOp();
            }
            return result;
        }

        /** Returns the kind of this argument. */
        public ArgKind getKind() {
            return this.kind;
        }

        private final ArgKind kind;
        private final @Nullable String name;
        private final @Nullable Expression expr;

        /** Tests if this argument matches another. */
        public boolean matches(Arg other) {
            switch (getKind()) {
            case CONST:
            case NAME:
                return equals(other);
            case WILD:
                return true;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.kind.hashCode();
            result = prime * result + ((this.expr == null)
                ? 0
                : this.expr.hashCode());
            result = prime * result + ((this.name == null)
                ? 0
                : this.name.hashCode());
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Arg other)) {
                return false;
            }
            if (this.kind != other.kind) {
                return false;
            }
            return switch (this.kind) {
            case CONST -> getExpr().equals(other.expr);
            case NAME -> getName().equals(other.name);
            case WILD -> true;
            };
        }

        @Override
        public String toString() {
            return switch (this.kind) {
            case NAME -> getName();
            case CONST -> getExpr().toDisplayString();
            case WILD -> WILD_TEXT;
            };
        }

        Line toLine() {
            return switch (getKind()) {
            case CONST -> getExpr().toLine();
            case NAME -> Line.atom(getName());
            case WILD -> Line.atom(WILD_TEXT);
            };
        }

        /** Constructs and returns a {@link ArgKind#NAME}-argument. */
        public static Arg arg(String name) {
            return new Arg(name);
        }

        /** Constructs and returns a {@link ArgKind#CONST}-argument. */
        public static Arg arg(Expression expr) {
            return new Arg(expr);
        }

        /** Textual representation of a wildcard argument. */
        public static final String WILD_TEXT = "_";

        /** Singleton wildcard argument. */
        public static final Arg WILD_ARG = new Arg();

        /** Call argument kind. */
        public static enum ArgKind {
            /** Identifier argument. */
            NAME,
            /** Constant argument. */
            CONST,
            /** Wildcard argument. */
            WILD;
        }
    }

    /** Atomic proposition kind. */
    public static enum Kind {
        /** Identifier proposition. */
        ID,
        /** Constant proposition. */
        LABEL,
        /** Call proposition. */
        CALL,
        /** Flag proposition. */
        FLAG,
    }
}