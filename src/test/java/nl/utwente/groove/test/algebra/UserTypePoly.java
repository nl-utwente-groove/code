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
package nl.utwente.groove.test.algebra;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * User type with a sequence-valued last field: a named polynomial,
 * given by its coefficients in the order of increasing degree.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public record UserTypePoly(String name, IntSeq coeffs) {
    /** Returns the degree of this polynomial, taking its coefficients as they are. */
    @UserOperation
    public int degree() {
        return coeffs().size() - 1;
    }

    /** Returns this polynomial extended by a coefficient for the next higher degree. */
    @UserOperation
    public UserTypePoly append(int coeff) {
        return new UserTypePoly(name(), coeffs().append(coeff));
    }

    /** Returns the sum of the values of this polynomial at any number of points. */
    @UserOperation
    public int sumAt(IntSeq points) {
        int result = 0;
        for (int i = 0; i < points.size(); i++) {
            result += UserOpsSeq.horner(points.get(i), coeffs());
        }
        return result;
    }
}
