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

/**
 * Default implementation of booleans.
 * @author Arend Rensink
 * @version $Revision $
 */
public class BoolAlgebra extends BoolSignature<Boolean> implements
        Algebra<Boolean> {
    /** Private constructor for the singleton instance. */
    private BoolAlgebra() {
        // empty
    }

    @Override
    public Boolean and(Boolean arg0, Boolean arg1) {
        return arg0 && arg1;
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

    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.DEFAULT;
    }

    public String getSymbol(Object value) {
        return value.toString();
    }

    public Boolean getValueFromString(String constant) {
        return constant.equals("true");
    }

    @Override
    protected Boolean toValue(Boolean constant) {
        return constant;
    }

    /** The name of this algebra. */
    static public final String NAME = "sbool";
    /** Singleton instance of this algebra. */
    public static final BoolAlgebra instance = new BoolAlgebra();
}