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

import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.graph.algebra.ValueNode;
import groove.trans.RuleSystem;

/**
 * Class representing a control parameter. 
 * Control parameters are used as arguments and formal parameters
 * in rules and functions.
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

    /**
     * Instantiates this (virtual) control parameter by providing  
     * appropriate value nodes for constants.
     * @param grammar the rule system specifying the appropriate
     * data algebra
     * @return an instantiated control parameter
     */
    public abstract CtrlPar instantiate(RuleSystem grammar);

    /** String representation of a don't care parameter. */
    public static final String DONT_CARE = "_";
    /** Prefix used to indicate output parameters. */
    public static final String OUT_PREFIX = "out";

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
         * Constructs a new, directional variable control parameter.
         * @param var the control variable of this parameter
         * @param inOnly if {@code true}, the parameter is input-only,
         * otherwise it is output-only
         */
        public Var(CtrlVar var, boolean inOnly) {
            this.var = var;
            this.inOnly = inOnly;
            this.outOnly = !inOnly;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Var)) {
                return false;
            }
            Var other = (Var) obj;
            return isInOnly() == other.isInOnly()
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
            result += getVar().hashCode();
            return result;
        }

        @Override
        public CtrlPar instantiate(RuleSystem grammar) {
            return this;
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

        /** The control variable wrapped in this variable parameter. */
        private final CtrlVar var;
        /** Flag indicating if this is an input-only parameter. */
        private final boolean inOnly;
        /** Flag indicating if this is an output-only parameter. */
        private final boolean outOnly;
    }

    /** 
     * Constant control parameter.
     * A constant parameter van be virtual or instantiated;
     * in the first case it is represented as a string.
     * Can only be used as an argument
     */
    public static class Const extends CtrlPar {
        /**
         * Constructs a constant argument from a string representation of the 
         * constant value
         * @param repr String representation of this constant
         */
        public Const(String repr) {
            this.repr = repr;
            this.node = null;
            assert AlgebraRegister.getSignatureName(repr) != null;
            this.type =
                CtrlType.createDataType(AlgebraRegister.getSignatureName(repr));
        }

        /**
         * Constructs a constant argument 
         */
        public Const(ValueNode node) {
            this.node = node;
            this.repr = node.getSymbol();
            this.type =
                CtrlType.createDataType(AlgebraRegister.getSignatureName(node.getAlgebra()));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof Const)) {
                return false;
            }
            Const other = (Const) obj;
            return getConstRepr().equals(other.getConstRepr());
        }

        /** 
         * Returns the value node for this constant.
         * Is {@code null} for a virtual argument.
         */
        public ValueNode getConstNode() {
            return this.node;
        }

        /** Returns the string representation of this constant. */
        public String getConstRepr() {
            return this.repr;
        }

        @Override
        public CtrlType getType() {
            return this.type;
        }

        @Override
        public int hashCode() {
            return getConstRepr().hashCode();
        }

        @Override
        public CtrlPar instantiate(RuleSystem grammar) {
            // find the algebra of this constant
            AlgebraRegister register =
                AlgebraRegister.getInstance(grammar.getProperties().getAlgebraFamily());
            Algebra<?> algebra =
                register.getImplementation(getType().getSignature());
            // find the appropriate constant value itself
            Object constant = algebra.getValue(getConstRepr());
            // construct the control argument with the corresponding value node
            return new Const(ValueNode.createValueNode(algebra, constant));
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

        @Override
        public String toString() {
            return this.repr;
        }

        /** The string representation of this constant. */
        private final String repr;
        /** The value node representing the constant; may be {@code null}. */
        private final ValueNode node;
        /** The type of the constant. */
        private final CtrlType type;
    }

    /**
     * Wildcard parameter.
     */
    public static class Wild extends CtrlPar {
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
        public CtrlPar instantiate(RuleSystem grammar) {
            return this;
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
