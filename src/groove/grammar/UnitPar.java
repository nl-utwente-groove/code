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

import static groove.grammar.aspect.AspectKind.PARAM_BI;
import static groove.grammar.aspect.AspectKind.PARAM_IN;
import static groove.grammar.aspect.AspectKind.PARAM_OUT;

import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.aspect.AspectKind;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.util.Exceptions;

/** Class encoding a formal unit parameter. */
public abstract class UnitPar {
    /** Constructor for subclassing, setting the parameter direction. */
    protected UnitPar(Direction direction) {
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
    public abstract CtrlType getType();

    /**
     * Indicates whether this parameter is input-only.
     * A parameter is either input-only, output-only, or bidirectional.
     */
    public boolean isInOnly() {
        return getDirection() == Direction.IN;
    }

    /**
     * Indicates whether this parameter is output-only.
     * A parameter is either input-only, output-only, or bidirectional.
     */
    public boolean isOutOnly() {
        return getDirection() == Direction.OUT;
    }

    /**
     * Indicates whether this parameter is bidirectional.
     * A parameter is either input-only, output-only, or bidirectional.
     */
    public boolean isBidirectional() {
        return getDirection() == Direction.BI;
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
        if (isOutOnly()) {
            return !arg.isInOnly();
        }
        assert isBidirectional();
        return true;
    }

    /**
     * Convenience method to construct a parameter with a given name, type and direction.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static ProcedurePar par(QualName scope, String name, CtrlType type, boolean isOut) {
        return new ProcedurePar(new CtrlVar(scope, name, type), isOut);
    }

    /** Class encoding a formal action parameter. */
    public static class ProcedurePar extends UnitPar {
        /**
         * Constructs a new formal action parameter.
         * @param var the control variable declared by this parameter
         * @param isOut flag indicating whether this is an output parameter
         */
        public ProcedurePar(CtrlVar var, boolean isOut) {
            super(isOut ? Direction.OUT : Direction.IN);
            this.var = var;
        }

        @Override
        public CtrlType getType() {
            return getVar().getType();
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
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof CtrlVar) {
                return this.var.equals(obj);
            }
            if (!(obj instanceof Var)) {
                return false;
            }
            Var other = (Var) obj;
            return isOutOnly() == other.isOutOnly() && isInOnly() == other.isInOnly()
                && getVar().equals(other.getVar());
        }

        @Override
        public String toString() {
            String result = isOutOnly() ? "out " : "";
            result += getVar().toString();
            return result;
        }

        /** The control variable declared by this procedure parameter. */
        private final CtrlVar var;
    }

    /** Class encoding a formal action parameter. */
    public static class RulePar extends UnitPar {
        /**
         * Constructs a new formal action parameter.
         * @param kind the kind of parameter; determines the directionality
         * @param node the associated rule node
         */
        public RulePar(AspectKind kind, RuleNode node, boolean creator) {
            super(creator ? Direction.OUT : toDirection(kind));
            assert kind.isParam();
            this.kind = kind;
            this.ruleNode = node;
            this.creator = creator;
        }

        /**
         * Returns the control type of this parameter.
         */
        @Override
        public CtrlType getType() {
            if (this.ruleNode instanceof VariableNode) {
                return CtrlType.getType(((VariableNode) this.ruleNode).getSort());
            } else {
                return CtrlType.NODE;
            }
        }

        /** Returns the directionality of this parameter.
         * @return one of {@link #PARAM_IN}, {@link #PARAM_OUT} or {@link #PARAM_BI}
         */
        public AspectKind getKind() {
            return this.kind;
        }

        private final AspectKind kind;

        @Override
        public String toString() {
            String result = isOutOnly() ? "!" : isInOnly() ? "?" : "";
            result += getNode().getId()
                .toString();
            return result;
        }

        /** Returns the (possibly {@code null} rule node in this parameter. */
        public RuleNode getNode() {
            return this.ruleNode;
        }

        /** Indicates if this is a rule parameter corresponding to a creator node. */
        public final boolean isCreator() {
            return this.creator;
        }

        /** Associated node if this is a rule parameter. */
        private final RuleNode ruleNode;
        /** Flag indicating if this is a rule parameter referring to a creator node. */
        private final boolean creator;

        @Override
        public int hashCode() {
            int result = getDirection().hashCode();
            result = result * 31 + getNode().hashCode();
            result = result * 31 + (isCreator() ? 0xFF : 0);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RulePar)) {
                return false;
            }
            RulePar other = (RulePar) obj;
            if (!getDirection().equals(other.getDirection())) {
                return false;
            }
            if (!getNode().equals(other.getNode())) {
                return false;
            }
            return isCreator() == other.isCreator();
        }
    }

    /** The value-passing direction of a parameter. */
    public static enum Direction {
        /** Input-only. */
        IN,
        /** Bidirectional. */
        BI,
        /** Output-only. */
        OUT,;

        /** Returns the parameter prefix that indicates a parameter of this direction. */
        public String getPrefix() {
            switch (this) {
            case IN:
                return "";
            case OUT:
                return OUT_PREFIX;
            case BI:
                return BI_PREFIX;
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
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Prefix used to indicate output parameters. */
    public static final String OUT_PREFIX = "out";
    /** Prefix used to indicate bidirectional parameters. */
    public static final String BI_PREFIX = "inout";
}