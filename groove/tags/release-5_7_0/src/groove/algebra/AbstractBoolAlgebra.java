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
package groove.algebra;

import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Abstract implementation of boolean algebra,
 * in which the values are represented by Java {@link Boolean}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractBoolAlgebra extends BoolAlgebra<Boolean> {
    /** Private constructor for the singleton instance. */
    AbstractBoolAlgebra() {
        // empty
    }

    @Override
    public Boolean and(Boolean arg0, Boolean arg1) {
        return arg0 && arg1;
    }

    @Override
    public Boolean bigand(List<Boolean> arg) {
        return arg.stream()
            .reduce(Boolean.TRUE, (b, c) -> Boolean.logicalAnd(b, c));
    }

    @Override
    public Boolean bigor(List<Boolean> arg) {
        return arg.stream()
            .reduce(Boolean.FALSE, (b, c) -> Boolean.logicalOr(b, c));
    }

    @Override
    public Boolean not(Boolean arg) {
        return !arg;
    }

    @Override
    public Boolean eq(Boolean arg0, Boolean arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean neq(Boolean arg0, Boolean arg1) {
        return !arg0.equals(arg1);
    }

    @Override
    public Boolean or(Boolean arg0, Boolean arg1) {
        return arg0 || arg1;
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance((Boolean) value);
    }

    @Override
    public Boolean toValueFromConstant(Constant constant) {
        return constant.getBoolRepr();
    }

    @Override
    public Boolean toJavaValue(Object value) {
        return (Boolean) value;
    }

    @Override
    protected Boolean toValueFromJavaBoolean(Boolean value) {
        return value;
    }
}
