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
 * $Id: JavaIntAlgebra.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import java.math.BigInteger;


/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JavaIntAlgebra extends IntSignature<Integer,Boolean,String> implements Algebra<Integer> {
    @Override
    public Integer add(Integer arg0, Integer arg1) {
        return arg0 + arg1;
    }

    @Override
    public Integer div(Integer arg0, Integer arg1) {
        return arg0/arg1;
    }

    @Override
    public Boolean eq(Integer arg0, Integer arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean ge(Integer arg0, Integer arg1) {
        return arg0 >= arg1;
    }

    @Override
    public Boolean gt(Integer arg0, Integer arg1) {
        return arg0 > arg1;
    }

    @Override
    public Boolean le(Integer arg0, Integer arg1) {
        return arg0 <= arg1;
    }

    @Override
    public Boolean lt(Integer arg0, Integer arg1) {
        return arg0 < arg1;
    }

    @Override
    public Integer max(Integer arg0, Integer arg1) {
        return Math.max(arg0,arg1);
    }

    @Override
    public Integer min(Integer arg0, Integer arg1) {
        return Math.min(arg0,arg1);
    }

    @Override
    public Integer mod(Integer arg0, Integer arg1) {
        return arg0 % arg1;
    }

    @Override
    public Integer mul(Integer arg0, Integer arg1) {
        return arg0 * arg1;
    }

    @Override
    public Integer neg(Integer arg) {
        return -arg;
    }

    @Override
    public Integer sub(Integer arg0, Integer arg1) {
        return arg0-arg1;
    }

    @Override
    public String toString(Integer arg) {
        return arg.toString();
    }

    /**
     * Delegates to {@link BigInteger#intValue()}.
     */
    public Integer getValue(String symbol) {
        return new BigInteger(symbol).intValue();
    }
    
    /**
     * Delegates to {@link Integer#toString()}.
     */
    public String getSymbol(Object value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    public String getName() {
        return NAME;
    }
    
    /** Name of the algebra. */
    public static final String NAME = "jint";
}
