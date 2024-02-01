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

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Abstract implementation of the string algebra.
 * The only non-implemented features are the methods concerning integers and reals.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract sealed class AbstractStringAlgebra<INT,REAL>
    extends StringAlgebra<INT,REAL,Boolean,String> permits BigStringAlgebra, JavaStringAlgebra {
    /** Empty constructor for the singleton instance. */
    AbstractStringAlgebra() {
        // empty
    }

    @Override
    public String concat(String arg0, String arg1) {
        return arg0.concat(arg1);
    }

    @Override
    public Boolean isBool(String arg0) {
        return arg0.equals("true") || arg0.equals("false");
    }

    @Override
    public Boolean isInt(String arg0) {
        try {
            Integer.parseInt(arg0);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    @Override
    public Boolean isReal(String arg0) {
        try {
            Double.parseDouble(arg0);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    @Override
    public Boolean toBool(String arg0) {
        return arg0.equals("true");
    }

    @Override
    public Boolean eq(String arg0, String arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean neq(String arg0, String arg1) {
        return !arg0.equals(arg1);
    }

    @Override
    public Boolean ge(String arg0, String arg1) {
        return arg0.compareTo(arg1) >= 0;
    }

    @Override
    public Boolean gt(String arg0, String arg1) {
        return arg0.compareTo(arg1) > 0;
    }

    @Override
    public String ite(Boolean arg0, String arg1, String arg2) {
        return arg0
            ? arg1
            : arg2;
    }

    @Override
    public Boolean le(String arg0, String arg1) {
        return arg0.compareTo(arg1) <= 0;
    }

    @Override
    public Boolean lt(String arg0, String arg1) {
        return arg0.compareTo(arg1) < 0;
    }

    @Override
    public boolean isValidValue(Object value) {
        return value instanceof String;
    }

    @Override
    public String toValidSymbol(Object value) {
        return StringHandler.toQuoted((String) value, StringHandler.DOUBLE_QUOTE_CHAR);
    }

    @Override
    public Expression toValidTerm(Object value) {
        return Constant.instance((String) value);
    }

    @Override
    public String toValueFromConstant(Constant constant) {
        return constant.getStringRepr();
    }

    @Override
    protected String toValueFromJavaString(String value) {
        return value;
    }
}
