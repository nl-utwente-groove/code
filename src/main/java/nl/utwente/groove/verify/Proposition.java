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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.verify.Proposition.Call;
import nl.utwente.groove.verify.Proposition.Derived;
import nl.utwente.groove.verify.Proposition.Literal;

/** Proposition, wrapped inside a formula of type {@link LogicOp#CALL_PROP}, {@link LogicOp#LITERAL_PROP} or {@link LogicOp#DERIVED_PROP}. */
@NonNullByDefault
public abstract sealed class Proposition permits Literal, Derived, Call {
    /** Returns the proposition operator corresponding to the actual concrete proposition type. */
    abstract public LogicOp getOp();

    /**
     * Tests if this proposition matches another.
     * This is the case if the two are equal, or if this is a parameterless {@link Call}
     * and the other a call of that action, or this is a call with wildcards and the other
     * a call that only differs in providing values for the wildcards.
     */
    abstract public boolean matches(Proposition other);

    @Override
    abstract public int hashCode();

    @Override
    abstract public boolean equals(@Nullable Object obj);

    @Override
    abstract public String toString();

    /** Constructs a display line for this proposition.
     * @param spaces flag indicating if spaces should be used for layout.
     */
    abstract public Line toLine(boolean spaces);

    /** Checks if this proposition are compatible with the given GTS,
     * and returns a (possibly empty) set of errors found in this check. */
    public FormatErrorSet check(GTS gts) {
        return new FormatErrorSet();
    }

    /** Returns an {@link Literal} or {@link Derived} consisting of a given label text.
     */
    public static Proposition literal(String label) {
        return new Literal(label);
    }

    /** Returns a parameterless {@link Call}. */
    public static Proposition call(QualName id) {
        return new Call(id);
    }

    /** Returns a {@link Call} proposition consisting of a given identifier and list of arguments. */
    public static Proposition call(QualName id, List<Arg> args) {
        return new Call(id, args);
    }

    /** Returns a {@link Derived} proposition for a given special flag. */
    public static Proposition derived(Flag flag) {
        return derived(flag.getDefault());
    }

    /** Returns a {@link Derived} proposition for a given name. */
    public static Proposition derived(String name) {
        var result = derivedPropMap.get(name);
        if (result == null) {
            derivedPropMap.put(name, result = new Derived(name));
        }
        return result;
    }

    /** Collection of previously generated derived properties. */
    static private final Map<String,@Nullable Derived> derivedPropMap = new HashMap<>();

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

    /** Proposition wrapping a literal label. */
    static public final class Literal extends Proposition {
        private Literal(String label) {
            this.label = label;
        }

        /** Returns the label in this proposition.
         */
        public String getLabel() {
            return this.label;
        }

        private final String label;

        @Override
        public @NonNull LogicOp getOp() {
            return LogicOp.LITERAL_PROP;
        }

        @Override
        public boolean matches(Proposition other) {
            return getLabel().equals(other.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass(), getLabel());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Literal other)) {
                return false;
            }
            return getLabel().equals(other.getLabel());
        }

        @Override
        public String toString() {
            return getLabel();
        }

        @Override
        public Line toLine(boolean spaces) {
            return Line.atom(getLabel());
        }
    }

    /** Derived proposition. */
    static public final class Derived extends Proposition {
        private Derived(String name) {
            this.name = name;
            this.flag = LTSLabels.flag(name);
        }

        /** Returns the name of this proposition. */
        public String getName() {
            return this.name;
        }

        private final String name;

        /**
         * Returns the special flag of this proposition, if any.
         */
        public @Nullable Flag getFlag() {
            return this.flag;
        }

        /** Indicates that this is a system property. */
        public boolean isSpecial() {
            return getFlag() != null;
        }

        private final @Nullable Flag flag;

        @Override
        public @NonNull LogicOp getOp() {
            return LogicOp.DERIVED_PROP;
        }

        @Override
        public boolean matches(Proposition other) {
            return equals(other);
        }

        @Override
        public @NonNull FormatErrorSet check(GTS gts) {
            var result = super.check(gts);
            if (!isSpecial() && !gts.hasStateProperty(getName())) {
                result.add("Derived property '%s' is not defined in this system", getName());
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass(), getName());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Derived other)) {
                return false;
            }
            return Objects.equals(getName(), other.getName());
        }

        @Override
        public String toString() {
            return getName();
        }

        /** Constructs a display line for this proposition.
         * @param spaces flag indicating if spaces should be used for layout.
         */
        @Override
        public Line toLine(boolean spaces) {
            return Line.atom(toString());
        }

    }

    /** Proposition wrapping a rule call. */
    static public final class Call extends Proposition {
        private Call(QualName id) {
            this.id = id;
            this.args = null;
        }

        private Call(QualName id, List<Arg> args) {
            this.id = id;
            this.args = args;
        }

        /** Returns the identifier in this proposition.
         */
        public QualName getId() {
            return this.id;
        }

        private final QualName id;

        /** Returns the optional array of call arguments.
         */
        public @Nullable List<Arg> getArgs() {
            return this.args;
        }

        /** Indicates if this call proposition has arguments. */
        public boolean hasArgs() {
            return getArgs() != null;
        }

        /** Returns the argument count of the call,
         * or {@code -1} if the proposition is parameterless. */
        public int arity() {
            var args = getArgs();
            return args == null
                ? -1
                : args.size();
        }

        private final @Nullable List<Arg> args;

        @Override
        public @NonNull LogicOp getOp() {
            return LogicOp.CALL_PROP;
        }

        @Override
        public boolean matches(Proposition other) {
            boolean result = false;
            if (other instanceof Call call) {
                result = getId().equals(call.getId());
                var args = getArgs();
                if (result && args != null) {
                    var otherArgs = call.getArgs();
                    if (otherArgs == null) {
                        result = false;
                    } else {
                        result = call.arity() == arity();
                        for (int i = 0; result && i < arity(); i++) {
                            result = args.get(i).matches(otherArgs.get(i));
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public @NonNull FormatErrorSet check(GTS gts) {
            var result = super.check(gts);
            var args = getArgs();
            var action = gts.getGrammar().getAction(getId());
            if (action == null) {
                result.add("Action '%s' is not defined in this grammar", getId());
            } else if (args != null && args.size() != action.getSignature().size()) {
                result
                    .add("Action '%s' has arity %s, not %s", getId(), action.getSignature().size(),
                         args.size());
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass(), getId(), getArgs());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Call other)) {
                return false;
            }
            return getId().equals(other.getId()) && Objects.equals(getArgs(), other.getArgs());
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(getId().toString());
            var args = getArgs();
            if (args != null) {
                result.append('(');
                boolean first = true;
                for (Arg arg : args) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(',');
                    }
                    result.append(arg);
                }
                result.append(')');
            }
            return result.toString();
        }

        /** Constructs a display line for this proposition.
         * @param spaces flag indicating if spaces should be used for layout.
         */
        @Override
        public Line toLine(boolean spaces) {
            List<Line> lines = new ArrayList<>();
            lines.add(getId().toLine());
            lines.add(Line.atom("("));
            var args = getArgs();
            if (args != null) {
                boolean firstArg = true;
                for (Arg arg : args) {
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
            }
            return Line.composed(lines);
        }
    }
}