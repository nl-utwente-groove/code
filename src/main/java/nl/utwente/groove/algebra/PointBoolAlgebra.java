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
package nl.utwente.groove.algebra;

import java.util.List;

import nl.utwente.groove.algebra.syntax.Expression;

/**
 * Implementation of booleans consisting of a singleton value.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class PointBoolAlgebra extends BoolAlgebra<Boolean> implements PointAlgebra<Boolean> {
    /** Private constructor for the singleton instance. */
    private PointBoolAlgebra() {
        // empty
    }

    @Override
    public Boolean and(Boolean arg0, Boolean arg1) {
        return singleBool;
    }

    @Override
    public Boolean bigand(List<Boolean> arg) {
        return singleBool;
    }

    @Override
    public Boolean bigor(List<Boolean> arg) {
        return singleBool;
    }

    @Override
    public Boolean eq(Boolean arg0, Boolean arg1) {
        return singleBool;
    }

    @Override
    public Boolean neq(Boolean arg0, Boolean arg1) {
        return singleBool;
    }

    @Override
    public Boolean not(Boolean arg) {
        return singleBool;
    }

    @Override
    public Boolean or(Boolean arg0, Boolean arg1) {
        return singleBool;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.POINT;
    }

    @Override
    public Boolean getPointValue() {
        return singleBool;
    }

    @Override
    public boolean isValidValue(Object value) {
        return value == singleBool;
    }

    @Override
    public Expression toValidTerm(Object value) {
        return singleBool
            ? BoolSignature.TRUE
            : BoolSignature.FALSE;
    }

    @Override
    public Object toJavaValue(Object value) {
        return singleBool;
    }

    @Override
    public Boolean toValueFromConstant(Constant constant) {
        return singleBool;
    }

    @Override
    protected Boolean toValueFromJavaBoolean(Boolean value) {
        return singleBool;
    }

    /** Name of this algebra. */
    public static final String NAME = "pbool";
    /** Singleton object of this algebra. */
    public static final Boolean singleBool = Boolean.FALSE;
    /** Singleton instance of this algebra. */
    public static final PointBoolAlgebra instance = new PointBoolAlgebra();
}
