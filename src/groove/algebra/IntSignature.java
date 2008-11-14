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
 * $Id: IntSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import java.math.BigInteger;


/**
 * Interface for integer algebras.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("hiding")
public abstract class IntSignature<Int,Bool,String> implements Signature {
    /** Addition of two integers. */
    public abstract Int add(Int arg0, Int arg1);

    /** Subtraction of two integers. */
    public abstract Int sub(Int arg0, Int arg1);

    /** Multiplication of two integers. */
    public abstract Int mul(Int arg0, Int arg1);

    /** Division of two integers. */
    public abstract Int div(Int arg0, Int arg1);

    /** Modulo of two integers. */
    public abstract Int mod(Int arg0, Int arg1);

    /** Minimum of two integers. */
    public abstract Int min(Int arg0, Int arg1);

    /** Maximum of two integers. */
    public abstract Int max(Int arg0, Int arg1);

    /** Lesser-than comparison. */
    public abstract Bool lt(Int arg0, Int arg1);

    /** Lesser-or-equal comparison. */
    public abstract Bool le(Int arg0, Int arg1);

    /** Greater-than comparison. */
    public abstract Bool gt(Int arg0, Int arg1);

    /** Greater-or-equal comparison. */
    public abstract Bool ge(Int arg0, Int arg1);

    /** Equality test. */
    public abstract Bool eq(Int arg0, Int arg1);

    /** Inversion. */
    public abstract Int neg(Int arg);
    
    /** 
     * Tests if the number can be parsed as a {@link BigInteger}.
     * This means that a number of any length is accepted.
     */
    final public boolean isValue(java.lang.String value) {
        try {
            new BigInteger(value);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }
}
