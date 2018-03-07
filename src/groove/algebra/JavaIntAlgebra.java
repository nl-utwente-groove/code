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
 * $Id$
 */
package groove.algebra;

import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JavaIntAlgebra extends IntAlgebra<Integer,Double,Boolean,String> {
    /** Private constructor for the singleton instance. */
    private JavaIntAlgebra() {
        // empty
    }

    @Override
    public Integer abs(Integer arg) {
        return Math.abs(arg);
    }

    @Override
    public Integer add(Integer arg0, Integer arg1) {
        return arg0 + arg1;
    }

    @Override
    public Integer bigmax(List<Integer> arg) {
        return arg.stream()
            .max(Integer::compareTo)
            .get();
    }

    @Override
    public Integer bigmin(List<Integer> arg) {
        return arg.stream()
            .min(Integer::compareTo)
            .get();
    }

    @Override
    public Integer div(Integer arg0, Integer arg1) {
        return arg0 / arg1;
    }

    @Override
    public Boolean eq(Integer arg0, Integer arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean neq(Integer arg0, Integer arg1) {
        return !arg0.equals(arg1);
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
        return Math.max(arg0, arg1);
    }

    @Override
    public Integer min(Integer arg0, Integer arg1) {
        return Math.min(arg0, arg1);
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
    public Integer prod(List<Integer> arg) {
        return arg.stream()
            .reduce(1, (i, j) -> i * j);
    }

    @Override
    public Integer sub(Integer arg0, Integer arg1) {
        return arg0 - arg1;
    }

    @Override
    public Integer sum(List<Integer> arg) {
        return arg.stream()
            .reduce(0, (i, j) -> i + j);
    }

    @Override
    public Double toReal(Integer arg) {
        return Double.valueOf(arg);
    }

    @Override
    public String toString(Integer arg) {
        return arg.toString();
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof Integer;
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance((Integer) value);
    }

    @Override
    public Integer toValueFromConstant(Constant constant) {
        return constant.getIntRepr()
            .intValue();
    }

    /* The value is already of the right type. */
    @Override
    public Integer toJavaValue(Object value) {
        return (Integer) value;
    }

    @Override
    protected Integer toValue(Integer constant) {
        return constant;
    }

    /**
     * Delegates to {@link Integer#toString()}.
     */
    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.DEFAULT;
    }

    /** Name of the algebra. */
    public static final String NAME = "jint";
    /** Singleton instance of this algebra. */
    public static final JavaIntAlgebra instance = new JavaIntAlgebra();
}
