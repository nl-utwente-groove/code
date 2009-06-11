/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: RealSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import java.math.BigDecimal;


/**
 * Interface for real number algebras.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
@SuppressWarnings("hiding")
public abstract class RealSignature<Real,Bool,String> implements Signature {
    /** Addition of two real numbers. */
    public abstract Real add(Real arg0, Real arg1);

    /** Subtraction of two real numbers. */
    public abstract Real sub(Real arg0, Real arg1);

    /** Multiplication of two real numbers. */
    public abstract Real mul(Real arg0, Real arg1);

    /** Division of two real numbers. */
    public abstract Real div(Real arg0, Real arg1);

    /** Minimum of two real numbers. */
    public abstract Real min(Real arg0, Real arg1);

    /** Maximum of two real numbers. */
    public abstract Real max(Real arg0, Real arg1);

    /** Lesser-than comparison. */
    public abstract Bool lt(Real arg0, Real arg1);

    /** Lesser-or-equal comparison. */
    public abstract Bool le(Real arg0, Real arg1);

    /** Greater-than comparison. */
    public abstract Bool gt(Real arg0, Real arg1);

    /** Greater-or-equal comparison. */
    public abstract Bool ge(Real arg0, Real arg1);

    /** Equality test. */
    public abstract Bool eq(Real arg0, Real arg1);

    /** Inversion. */
    public abstract Real neg(Real arg);
    
    /** String representation. */
    public abstract String toString(Real arg);
    
    /** 
     * Tests if the number can be parsed as a {@link BigDecimal}.
     * This means that a number of any length is accepted.
     */
    final public boolean isValue(java.lang.String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }
}
