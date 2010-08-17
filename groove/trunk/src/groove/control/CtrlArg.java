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
 * Class representing a control argument. A control argument has two properties:
 * <ul>
 * <li>Its direction: input, output or don't care
 * <li>Its content: <i>variable</i> or <i>constant</i>. A constant can be virtual 
 * (only given by a string representation) or instantiated to a {@link ValueNode}.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlArg {
    /** 
     * Constructs a don't care argument.
     */
    public CtrlArg() {
        this.inArg = false;
        this.outArg = false;
        this.var = null;
        this.constRepr = null;
        this.constNode = null;
        this.type = null;
    }

    /** 
     * Constructs an input or output control argument
     * wrapping a given variable.
     * @param inArg flag indicating if the argument is input or output
     * @param var the (non-{@code null}) variable to be wrapped by this argument.
     */
    public CtrlArg(boolean inArg, CtrlVar var) {
        this.inArg = inArg;
        this.outArg = !inArg;
        assert var != null;
        this.var = var;
        this.constRepr = null;
        this.constNode = null;
        this.type = var.getType();
    }

    /**
     * Constructs a virtual constant input parameter from the string representation
     * of a given value.
     * It is assumed that the value represents a valid constant in some signature. 
     * @param constRepr string representation of the (non-{@code null}) constant value.
     */
    public CtrlArg(String constRepr) {
        this.inArg = true;
        this.outArg = false;
        this.constRepr = constRepr;
        assert AlgebraRegister.getSignatureName(constRepr) != null;
        this.constNode = null;
        this.var = null;
        this.type =
            CtrlType.createDataType(AlgebraRegister.getSignatureName(constRepr));
    }

    /**
     * Constructs an instantiated constant input parameter from a given value node.
     * @param constNode string representation of the (non-{@code null}) constant value.
     */
    public CtrlArg(ValueNode constNode) {
        this.inArg = true;
        this.outArg = false;
        this.constRepr = constNode.getSymbol();
        this.constNode = constNode;
        this.var = null;
        this.type =
            CtrlType.createDataType(AlgebraRegister.getSignatureName(constNode.getAlgebra()));
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result && obj instanceof CtrlArg) {
            CtrlArg other = (CtrlArg) obj;
            result =
                isInArg() == other.isInArg() && isOutArg() == other.isOutArg();
            if (result) {
                result =
                    getVar() == null ? other.getVar() == null
                            : getVar().equals(other.getVar());
            }
            if (result) {
                result =
                    getConstRepr() == null ? other.getConstRepr() == null
                            : getConstRepr().equals(other.getConstRepr());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = isInArg() ? 1 : isOutArg() ? 2 : 3;
        if (getVar() != null) {
            result += getVar().hashCode();
        } else if (getConstRepr() != null) {
            result -= getConstRepr().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result;
        if (isDontCare()) {
            result = "_";
        } else if (getVar() != null) {
            result = isOutArg() ? OUT_PREFIX + " " : "";
            result += getVar().toString();
        } else {
            result = getConstRepr();
        }
        return result;
    }

    /** 
     * Indicates whether this argument is an input argument.
     * An argument is either input, output, or don't care. 
     */
    public boolean isInArg() {
        return this.inArg;
    }

    /** 
     * Indicates whether this argument is an output argument. 
     * If {@code true}, the variable must be set.
     * An argument is either input, output, or don't care. 
     */
    public boolean isOutArg() {
        return this.outArg;
    }

    /** 
     * Indicates if this argument is a don't care; i.e., its value is irrelevant. 
     * An argument is either input, output, or don't care. 
     */
    public boolean isDontCare() {
        return !isInArg() && !isOutArg();
    }

    /** Returns the (possibly {@code null}) variable encapsulated by this argument. */
    public CtrlVar getVar() {
        return this.var;
    }

    /** Returns the (possibly {@code null}) constant encapsulated by this argument. */
    public String getConstRepr() {
        return this.constRepr;
    }

    /** 
     * Returns the (possibly {@code null}) constant encapsulated by this argument.
     * The constant may be {@code null} under two circumstances:
     * <ul>
     * <li> The argument is a don't care or variable argument
     * <li> The argument has not yet been instantiated
     * </ul>
     */
    public ValueNode getConstNode() {
        return this.constNode;
    }

    /** 
     * Returns the control type of this argument.
     * @return {@code null} if the argument is don't care;
     * the type derived from the variable or constant otherwise.
     */
    public CtrlType getType() {
        return this.type;
    }

    /**
     * Instantiates this (virtual) control argument by providing  
     * appropriate value nodes for constants.
     * @param grammar the rule system specifying the appropriate
     * data algebra
     * @return an instantiated control argument
     */
    public CtrlArg instantiate(RuleSystem grammar) {
        if (getConstRepr() == null) {
            return this;
        } else {
            // find the algebra of this constant
            AlgebraRegister register =
                AlgebraRegister.getInstance(grammar.getProperties().getAlgebraFamily());
            Algebra<?> algebra =
                register.getImplementation(getType().getSignature());
            // find the appropriate constant value itself
            Object constant = algebra.getValue(getConstRepr());
            // construct the control argument with the corresponding value node
            return new CtrlArg(ValueNode.createValueNode(algebra, constant));
        }

    }

    /** Flag signalling whether this argument is an input argument. */
    private final boolean inArg;
    /** Flag signalling whether this argument is an output argument. */
    private final boolean outArg;
    /** The variable encapsulated by this parameter, if any. */
    private final CtrlVar var;
    /** String representation of the constant encapsulated by this parameter, if any. */
    private final String constRepr;
    /**
     * The constant encapsulated by this parameter, if any.
     * May be {@code null} if the constant has not yet been instantiated. 
     */
    private final ValueNode constNode;
    /** 
     * The control type of this argument.
     * Is {@code null} if the argument is don't care; otherwise
     * it is derived from the variable or constant type.
     * 
     */
    private final CtrlType type;
    /** String representation of a don't care argument. */
    public static final String DONT_CARE = "_";
    /** Prefix used to indicate output parameters. */
    public static final String OUT_PREFIX = "out";
}
