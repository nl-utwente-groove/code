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

/**
 * Class representing a control parameter. These have two properties:
 * <ul>
 * <li>Their input/output nature
 * <li>Their content which can be <i>variable</i>, <i>constant</i> or
 * <i>don't care</i>
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlArg {
    /** 
     * Constructs an input or output don't care parameter.
     * @param inPar flag indicating if the parameter is input or output
     */
    public CtrlArg(boolean inPar) {
        this.inPar = inPar;
        this.var = null;
        this.constant = null;
    }

    /** 
     * Constructs an input or output control parameter
     * wrapping a given variable.
     * @param inPar flag indicating if the parameter is input or output
     * @param var the (non-{@code null}) variable to be wrapped by this parameter.
     */
    public CtrlArg(boolean inPar, String var) {
        this.inPar = inPar;
        assert var != null;
        this.var = var;
        this.constant = null;
    }

    /**
     * Constructs a constant input parameter.
     * @param constant the (non-{@code null}) constant value.
     */
    public CtrlArg(Object constant) {
        this.inPar = true;
        this.constant = constant;
        this.var = null;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof CtrlArg) {
            CtrlArg other = (CtrlArg) obj;
            result = isInPar() == other.isInPar();
            if (result) {
                if (isDontCare()) {
                    result = other.isDontCare();
                } else if (getVar() != null) {
                    result = getVar().equals(other.getVar());
                } else {
                    assert getConstant() != null;
                    result = getConstant().equals(other.getConstant());
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = isInPar() ? 1 : 2;
        if (!isDontCare()) {
            if (getVar() != null) {
                result += getVar().hashCode();
            } else {
                result -= getConstant().hashCode();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result;
        if (isDontCare()) {
            result = "_";
        } else if (getVar() != null) {
            result = isInPar() ? "" : "out ";
            result += getVar().toString();
        } else {
            result = getConstant().toString();
        }
        return result;
    }

    /** Indicates whether this parameter is an input parameter. */
    public boolean isInPar() {
        return this.inPar;
    }

    /** Returns the (possibly {@code null}) variable encapsulated by this parameter. */
    public String getVar() {
        return this.var;
    }

    /** Returns the (possibly {@code null}) constant encapsulated by this parameter. */
    public Object getConstant() {
        return this.constant;
    }

    /** Indicates if this parameter is a wildcard; i.e., its value is irrelevant. */
    public boolean isDontCare() {
        return this.var == null && this.constant == null;
    }

    /** Flag signalling whether this parameter is an input parameter. */
    private final boolean inPar;
    /** The variable encapsulated by this parameter, if any. */
    private final String var;
    /** The constant encapsulated by this parameter, if any. */
    private final Object constant;
}
