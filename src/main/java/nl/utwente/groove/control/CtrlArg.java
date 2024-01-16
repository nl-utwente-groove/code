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
package nl.utwente.groove.control;

import java.util.function.Function;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.UnitPar.Direction;

/**
 * Class representing a control argument in an action call.
 * A control argument has two properties:
 * <ul>
 * <li>Its direction: input-only, output-only or don't care
 * <li>Its content: <i>variable</i>, <i>constant</i> or <i>wildcard</i>.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
public interface CtrlArg {

    /**
     * Indicates whether this parameter is input-only.
     * A parameter is either input-only, output-only, or don't care.
     */
    public abstract boolean inOnly();

    /**
     * Indicates whether this parameter is output-only.
     * A parameter is either input-only, output-only, or don't care.
     */
    public default boolean outOnly() {
        return !inOnly();
    }

    /**
     * Indicates if this parameter is a don't care; i.e., its direction is irrelevant.
     * A parameter is either input-only, output-only, or don't care.
     */
    public abstract boolean dontCare();

    /**
     * Returns the control type of this parameter.
     * @return {@code null} if the parameter is a wildcard, and
     * the type derived from the variable or constant otherwise.
     */
    public abstract CtrlType getType();

    /** Returns a copy of this control argument in which all variables
     * have been enriched with binding information.
     * @param bindMap Mapping from variables to corresponding binding information.
     */
    public default CtrlArg bind(java.util.function.Function<Variable,Object> bindMap) {
        return this;
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

    /** Returns a new {@link Expr} argument based on a given expression. */
    public static Expr expr(Expression expr) {
        return new Expr(expr);
    }

    /** Returns the single untyped wildcard argument. */
    public static Wild wild() {
        return Wild.WILD;
    }

    /**
     * Variable control argument.
     * A variable argument has a name, type and direction.
     */
    public static record Var(CtrlVar var, boolean inOnly) implements CtrlArg {
        @Override
        public CtrlType getType() {
            return var().type();
        }

        @Override
        public boolean dontCare() {
            return false;
        }

        @Override
        public String toString() {
            String result = outOnly()
                ? OUT_PREFIX + " "
                : "";
            result += var().toString();
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
        public boolean compatibleWith(CtrlArg arg) {
            CtrlType argType = arg.getType();
            if (argType != null && !getType().equals(argType)) {
                return false;
            }
            if (dontCare()) {
                return true;
            } else if (inOnly()) {
                return arg.inOnly();
            } else {
                assert outOnly();
                return !arg.inOnly();
            }
        }
    }

    /**
     * Wildcard parameter.
     */
    public record Wild() implements CtrlArg {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Wild;
        }

        @Override
        public int hashCode() {
            return Wild.class.hashCode();
        }

        @Override
        public CtrlType getType() {
            return null;
        }

        @Override
        public boolean dontCare() {
            return true;
        }

        @Override
        public boolean inOnly() {
            return false;
        }

        @Override
        public boolean outOnly() {
            return false;
        }

        @Override
        public String toString() {
            return DONT_CARE;
        }

        /** The singleton instance of the untyped wildcard argument. */
        private static Wild WILD = new Wild();
    }

    /**
     * Expression control argument, wrapping an {@link Expression}.
     */
    static public record Expr(Expression expr) implements CtrlArg {
        @Override
        public boolean inOnly() {
            return true;
        }

        @Override
        public boolean dontCare() {
            return false;
        }

        @Override
        public CtrlType getType() {
            return CtrlType.getType(expr().getSort());
        }

        @Override
        public CtrlArg bind(Function<Variable,Object> bindMap) {
            return new Expr(expr().bind(bindMap));
        }
    }
}
