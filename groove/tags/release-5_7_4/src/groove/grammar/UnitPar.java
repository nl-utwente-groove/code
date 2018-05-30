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
package groove.grammar;

import static groove.grammar.aspect.AspectKind.PARAM_ASK;
import static groove.grammar.aspect.AspectKind.PARAM_BI;
import static groove.grammar.aspect.AspectKind.PARAM_IN;
import static groove.grammar.aspect.AspectKind.PARAM_OUT;

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.aspect.AspectKind;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.util.Exceptions;
import groove.util.parse.FormatException;
import groove.util.parse.IdValidator;

/** Class encoding a formal unit parameter. */
@NonNullByDefault
public abstract class UnitPar {
    /** Constructor for subclassing, setting the parameter direction. */
    protected UnitPar(CtrlType type, String name, Direction direction) {
        this.type = type;
        this.name = name;
        this.direction = direction;
    }

    /** Returns the direction of this parameter. */
    public Direction getDirection() {
        return this.direction;
    }

    private final Direction direction;

    /**
     * Returns the control type of this parameter.
     */
    public CtrlType getType() {
        return this.type;
    }

    private final CtrlType type;

    /**
     * Returns the name of this parameter.
     */
    public String getName() {
        return this.name;
    }

    private final String name;

    /**
     * Indicates whether this parameter is input-only.
     * A parameter is either input-only, output-only, user-provided, or bidirectional.
     */
    public boolean isInOnly() {
        return getDirection() == Direction.IN;
    }

    /**
     * Indicates whether this parameter is output-only.
     * A parameter is either input-only, output-only, user-provided, or bidirectional.
     */
    public boolean isOutOnly() {
        return getDirection() == Direction.OUT;
    }

    /**
     * Indicates whether this parameter is bidirectional.
     * A parameter is either input-only, output-only, user-provided, or bidirectional.
     */
    public boolean isBidirectional() {
        return getDirection() == Direction.BI;
    }

    /**
     * Indicates whether this parameter is user-provided.
     * A parameter is either input-only, output-only, user-provided, or bidirectional.
     */
    public boolean isAsk() {
        return getDirection() == Direction.ASK;
    }

    /**
     * Tests whether this variable parameter,
     * when used as a formal parameter, is compatible with a given
     * control argument.
     * Compatibility refers to direction and type
     * @param arg the control argument to test against; non-{@code null}
     * @return if <code>true</code>, this variable is compatible with {@code arg}
     */
    public boolean compatibleWith(CtrlPar arg) {
        CtrlType argType = arg.getType();
        if (argType != null && !getType().equals(argType)) {
            return false;
        }
        if (isInOnly()) {
            return arg.isInOnly();
        }
        if (isOutOnly() || isAsk()) {
            return !arg.isInOnly();
        }
        assert isBidirectional();
        return true;
    }

    @Override
    public int hashCode() {
        int result = getDirection().hashCode();
        result = result * 31 + getType().hashCode();
        result = result * 31 + getName().hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UnitPar)) {
            return false;
        }
        UnitPar other = (UnitPar) obj;
        if (!getDirection().equals(other.getDirection())) {
            return false;
        }
        if (!getType().equals(other.getType())) {
            return false;
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getDirection().getPrefix());
        if (result.length() != 0) {
            result.append(' ');
        }
        result.append(getType());
        result.append(' ');
        result.append(getName());
        return result.toString();
    }

    /** Attempts to parse a string as a procedure parameter,
     * consisting of optional direction, mandatory node type,
     * and mandatory name (an identifier). */
    static public ProcedurePar parse(String input) throws FormatException {
        String text = input.trim();
        Direction dir = Arrays.stream(Direction.values())
            .filter(d -> !d.getPrefix()
                .isEmpty())
            .filter(d -> text.startsWith(d.getPrefix() + " "))
            .findAny()
            .orElse(Direction.IN);
        String typeText = text.substring(dir.getPrefix()
            .length())
            .trim();
        CtrlType type = Arrays.stream(CtrlType.values())
            .filter(t -> typeText.startsWith(t.getName() + " "))
            .findAny()
            .orElseThrow(() -> new FormatException(
                "Error in parameter declaration '%s': Could not determine parameter type", input));
        String name = typeText.substring(type.getName()
            .length())
            .trim();
        if (!IdValidator.JAVA_ID.isValid(name)) {
            throw new FormatException(
                "Error in parameter declaration '%s': name '%s' is not a valid identifier", input,
                name);
        }
        return par(name, type, dir);
    }

    /**
     * Convenience method to construct a parameter with a given scope, name, type and direction.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static ProcedurePar par(@Nullable QualName scope, String name, CtrlType type,
        Direction dir) {
        return new ProcedurePar(new CtrlVar(scope, name, type), dir);
    }

    /**
     * Convenience method to construct a parameter with a given name, type and direction.
     */
    public static ProcedurePar par(String name, CtrlType type, Direction dir) {
        return par(null, name, type, dir);
    }

    /** Class encoding a formal action parameter. */
    public static class ProcedurePar extends UnitPar {
        /**
         * Constructs a new formal action parameter.
         * @param var the control variable declared by this parameter
         * @param dir value-passing direction of the parameter
         */
        public ProcedurePar(CtrlVar var, Direction dir) {
            super(var.getType(), var.getName(), dir);
            this.var = var;
        }

        /** Returns the control variable declared by this procedure parameter. */
        public CtrlVar getVar() {
            return this.var;
        }

        @Override
        public int hashCode() {
            int result = isInOnly() ? 0 : isOutOnly() ? 1 : 2;
            result = result * 31 + getVar().hashCode();
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            assert obj != null;
            ProcedurePar other = (ProcedurePar) obj;
            return getVar().equals(other.getVar());
        }

        /** The control variable declared by this procedure parameter. */
        private final CtrlVar var;
    }

    /** Class encoding a formal rule parameter. */
    public static class RulePar extends UnitPar {
        /**
         * Constructs a new formal rule parameter.
         * @param kind the kind of parameter; determines the directionality
         * @param node the associated rule node
         */
        public RulePar(AspectKind kind, RuleNode node, boolean creator) {
            super(getType(node), node.getId(), creator ? Direction.OUT : toDirection(kind));
            assert kind.isParam();
            assert !creator || toDirection(kind) != Direction.IN;
            this.node = node;
            this.creator = creator;
            // do this only now, after all instance variables are set
            node.setPar(this);
        }

        /** Returns the (non-{@code null}) rule node in this parameter. */
        public RuleNode getNode() {
            return this.node;
        }

        /** Associated node if this is a rule parameter. */
        private final RuleNode node;

        /** Indicates if this is a rule parameter corresponding to a creator node. */
        public final boolean isCreator() {
            return this.creator;
        }

        /** Flag indicating if this is a rule parameter referring to a creator node. */
        private final boolean creator;

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = result * 31 + getNode().hashCode();
            result = result * 31 + (isCreator() ? 0xFF : 0);
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            assert obj != null;
            RulePar other = (RulePar) obj;
            if (!getNode().equals(other.getNode())) {
                return false;
            }
            return isCreator() == other.isCreator();
        }

        @Override
        public String toString() {
            String result = isOutOnly() ? "!" : isInOnly() ? "?" : "";
            result += getNode().getId();
            return result;
        }

        /**
         * Extracts the control type of a rule node.
         */
        static public CtrlType getType(RuleNode node) {
            if (node instanceof VariableNode) {
                return CtrlType.getType(((VariableNode) node).getSort());
            } else {
                return CtrlType.NODE;
            }
        }
    }

    /** The value-passing direction of a parameter. */
    public static enum Direction {
        /** Input-only. */
        IN,
        /** Bidirectional. */
        BI,
        /** Output-only. */
        OUT,
        /** User-provided. */
        ASK,;

        /** Returns the parameter prefix that indicates a parameter of this direction. */
        public String getPrefix() {
            switch (this) {
            case IN:
                return "";
            case OUT:
                return OUT_PREFIX;
            case BI:
                return BI_PREFIX;
            case ASK:
                return ASK_PREFIX;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        /** Inserts the prefix of this direction in front of a given parameter. */
        public String prefix(String par) {
            String prefix = getPrefix();
            return prefix.isEmpty() ? par : prefix + " " + par;
        }

        /** Converts this direction to a parameter aspect kind. */
        public AspectKind toAspectKind() {
            switch (this) {
            case IN:
                return PARAM_IN;
            case OUT:
                return PARAM_OUT;
            case BI:
                return PARAM_BI;
            case ASK:
                return PARAM_ASK;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }
    }

    /** Converts a parameter aspect kind to a parameter direction. */
    static public Direction toDirection(AspectKind paramKind) {
        switch (paramKind) {
        case PARAM_IN:
            return Direction.IN;
        case PARAM_OUT:
            return Direction.OUT;
        case PARAM_BI:
            return Direction.BI;
        case PARAM_ASK:
            return Direction.ASK;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Prefix used to indicate output parameters. */
    public static final String OUT_PREFIX = "out";
    /** Prefix used to indicate bidirectional parameters. */
    public static final String BI_PREFIX = "bi";
    /** Prefix used to indicate user-provided parameters. */
    public static final String ASK_PREFIX = "ask";
}