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
 * Default implementation of the string algebra.
 * @author Arend Rensink
 * @version $Revision $
 */
public class JavaStringAlgebra extends AbstractStringAlgebra<Integer,Double> {
    /** Empty constructor for the singleton instance. */
    private JavaStringAlgebra() {
        // empty
    }

    @Override
    public Integer toInt(String arg0) {
        try {
            return Integer.valueOf(arg0);
        } catch (NumberFormatException exc) {
            return 0;
        }
    }

    @Override
    public Double toReal(String arg0) {
        try {
            return Double.valueOf(arg0);
        } catch (NumberFormatException exc) {
            return 0.0;
        }
    }

    @Override
    public Integer length(String arg) {
        return arg.length();
    }

    @Override
    public String substring(String arg0, Integer arg1, Integer arg2) {
        try {
            return arg0.substring(arg1, arg2);
        } catch (IndexOutOfBoundsException exc) {
            return "";
        }
    }

    @Override
    public String suffix(String arg0, Integer arg1) {
        try {
            return arg0.substring(arg1);
        } catch (IndexOutOfBoundsException exc) {
            return "";
        }
    }

    @Override
    public Integer lookup(String arg0, String arg1) {
        return arg0.indexOf(arg1);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.DEFAULT;
    }

    /** The name of this algebra. */
    public static final String NAME = "string";
    /** The singleton instance of this class. */
    public static final JavaStringAlgebra instance = new JavaStringAlgebra();
}
