/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import groove.algebra.Algebra;
import groove.grammar.QualName;
import groove.grammar.UnitPar.Direction;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleNode;

/**
 * Class representing a control argument in an action call.
 * A control parameter has two properties:
 * <ul>
 * <li>Its direction: input-only, output-only or don't care
 * <li>Its content: <i>variable</i>, <i>constant</i> or <i>wildcard</i>.
 * A constant can be virtual
 * (only given by a string representation) or instantiated to a {@link ValueNode}.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class CtrlPar {

    /**
     * Indicates whether this parameter is input-only.
     * A parameter is either input-only, output-only, or don't care.
     */
    public abstract boolean isInOnly();

    /**
     * Indicates whether this parameter is output-only.
     * A parameter is either input-only, output-only, or don't care.
     */
    public abstract boolean isOutOnly();

    /**
     * Indicates if this parameter is a don't care; i.e., its direction is irrelevant.
     * A parameter is either input-only, output-only, or don't care.
     */
    public abstract boolean isDontCare();

    /**
     * Returns the control type of this parameter.
     * @return {@code null} if the parameter is a wildcard, and
     * the type derived from the variable or constant otherwise.
     */
    public abstract CtrlType getType();

    /** Computes and inserts the host nodes to be used for constant value arguments. */
    public void initialise(HostFactory factory) {
        // empty
    }

    /** String representation of a don't care parameter. */
    public static final String DONT_CARE = "_";
    /** Prefix used to indicate output parameters. */
    public static final String OUT_PREFIX = Direction.OUT.getPrefix();

    /**
     * Convenience method to construct a parameter with a given name, type and direction.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static Var var(QualName scope, String name, CtrlType type, boolean inOnly) {
        return new Var(new CtrlVar(scope, name, type), inOnly);
    }

    /** Convenience method to construct an input parameter with a given name and type.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static Var inVar(QualName scope, String name, String type) {
        return var(scope, name, CtrlType.getType(type), true);
    }

    /** Convenience method to construct an output parameter with a given name and type.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static Var outVar(QualName scope, String name, String type) {
        return var(scope, name, CtrlType.getType(type), false);
    }

    /** Returns a wildcard parameter with a given type and number. */
    public static Var wild(CtrlType type, int nr) {
        List<Var> typeVars = wildMap.get(type);
        if (typeVars == null) {
            wildMap.put(type, typeVars = new ArrayList<>());
        }
        for (int i = typeVars.size(); i <= nr; i++) {
            typeVars.add(new Var(CtrlVar.wild(type, i), false));
        }
        return typeVars.get(nr);
    }

    /** Store of wildcard variables. */
    private static Map<CtrlType,List<Var>> wildMap = new EnumMap<>(CtrlType.class);

    /** Returns the single untyped wildcard argument. */
    public static Wild wild() {
        return WILD;
    }

    /** The singleton instance of the untyped wildcard argument. */
    private static Wild WILD = new Wild();

    /**
     * Variable control parameter.
     * A variable parameter has a name and type,
     * and an optional direction.
     * Can be used as formal parameter or argument.
     */
    public static class Var extends CtrlPar {
        /**
         * Constructs a new, non-directional variable control parameter.
         * @param var the control variable of this parameter
         */
        public Var(CtrlVar var) {
            this.var = var;
            this.inOnly = false;
            this.outOnly = false;
        }

        /**
         * Constructs a new, directional variable control argument.
         * @param var the control variable of this parameter
         * @param inOnly if {@code true}, the parameter is input-only,
         * otherwise it is output-only
         */
        public Var(CtrlVar var, boolean inOnly) {
            assert var != null;
            this.var = var;
            this.inOnly = inOnly;
            this.outOnly = !inOnly;
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
        public CtrlType getType() {
            return getVar().getType();
        }

        /** Returns the control variable wrapped in this variable parameter. */
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
        public boolean isDontCare() {
            return !(this.inOnly || this.outOnly);
        }

        @Override
        public boolean isInOnly() {
            return this.inOnly;
        }

        @Override
        public boolean isOutOnly() {
            return this.outOnly;
        }

        @Override
        public String toString() {
            String result = isOutOnly() ? OUT_PREFIX + " " : "";
            result += getVar().toString();
            return result;
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
            if (isDontCare()) {
                return true;
            } else if (isInOnly()) {
                return arg.isInOnly();
            } else {
                assert isOutOnly();
                return !arg.isInOnly();
            }
        }

        /** Returns the (possibly {@code null} rule node in this parameter. */
        public RuleNode getRuleNode() {
            return this.ruleNode;
        }

        /**
         * Sets the rule node of this parameter.
         * Also indicates if it is a creator node.
         */
        public void setRuleNode(RuleNode ruleNode, boolean creator) {
            this.ruleNode = ruleNode;
            this.creator = creator;
        }

        /** Indicates if this is a rule parameter corresponding to a creator node. */
        public final boolean isCreator() {
            return this.creator;
        }

        /** Associated node if this is a rule parameter. */
        private RuleNode ruleNode;
        /** Flag indicating if this is a rule parameter referring to a creator node. */
        private boolean creator;
        /** The control variable wrapped in this variable parameter. */
        private final CtrlVar var;
        /** Flag indicating if this is an input-only parameter. */
        private final boolean inOnly;
        /** Flag indicating if this is an output-only parameter. */
        private final boolean outOnly;
    }

    /**
     * Constant control argument.
     */
    public static class Const extends CtrlPar {
        /**
         * Constructs a constant argument from an algebra value
         * @param algebra the algebra from which the value is taken
         * @param value the algebra value
         */
        public Const(Algebra<?> algebra, Object value) {
            this.algebra = algebra;
            this.value = value;
            this.type = CtrlType.getType(algebra.getSort());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof Const)) {
                return false;
            }
            Const other = (Const) obj;
            return getValue().equals(other.getValue());
        }

        /** Returns the value of this constant. */
        public Algebra<?> getAlgebra() {
            return this.algebra;
        }

        /** Returns the value of this constant. */
        public Object getValue() {
            return this.value;
        }

        @Override
        public CtrlType getType() {
            return this.type;
        }

        @Override
        public int hashCode() {
            return getValue().hashCode();
        }

        @Override
        public boolean isDontCare() {
            return false;
        }

        @Override
        public boolean isInOnly() {
            return true;
        }

        @Override
        public boolean isOutOnly() {
            return false;
        }

        /** Returns the host node containing the value of this constant. */
        public HostNode getNode() {
            assert this.node != null;
            return this.node;
        }

        @Override
        public void initialise(HostFactory factory) {
            this.node = factory.createNode(getAlgebra(), getValue());
        }

        @Override
        public String toString() {
            return this.algebra.getSymbol(this.value);
        }

        private final Algebra<?> algebra;
        /** The value of this constant. */
        private final Object value;
        /** The type of the constant. */
        private final CtrlType type;
        /** The host node to be used as image. */
        private HostNode node;
    }

    /**
     * Wildcard parameter.
     */
    public static class Wild extends CtrlPar {
        /** Constructor for the singleton instance. */
        private Wild() {
            // empty
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Wild;
        }

        @Override
        public CtrlType getType() {
            return null;
        }

        @Override
        public int hashCode() {
            return Wild.class.hashCode();
        }

        @Override
        public boolean isDontCare() {
            return true;
        }

        @Override
        public boolean isInOnly() {
            return false;
        }

        @Override
        public boolean isOutOnly() {
            return false;
        }

        @Override
        public String toString() {
            return DONT_CARE;
        }
    }
}
