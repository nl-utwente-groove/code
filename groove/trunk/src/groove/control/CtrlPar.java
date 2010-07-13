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
public class CtrlPar {
    /** 
     * Constructs an input or output don't care parameter.
     * @param inPar flag indicating if the parameter is input or output
     */
    public CtrlPar(boolean inPar) {
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
    public CtrlPar(boolean inPar, String var) {
        this.inPar = inPar;
        assert var != null;
        this.var = var;
        this.constant = null;
    }

    /**
     * Constructs a constant input parameter.
     * @param constant the (non-{@code null}) constant value.
     */
    public CtrlPar(Object constant) {
        this.inPar = true;
        this.constant = constant;
        this.var = null;
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
