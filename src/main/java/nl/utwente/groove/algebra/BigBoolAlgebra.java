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

/**
 * Boolean implementation for the {@link AlgebraFamily#BIG} family.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class BigBoolAlgebra extends AbstractBoolAlgebra {
    /** Private constructor for the singleton instance. */
    private BigBoolAlgebra() {
        // empty
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.BIG;
    }

    /** The name of this algebra. */
    public static final String NAME = "bbool";
    /** Singleton instance of this algebra. */
    public static final BigBoolAlgebra instance = new BigBoolAlgebra();
}
