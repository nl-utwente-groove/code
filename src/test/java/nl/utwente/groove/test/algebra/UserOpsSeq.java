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

import nl.utwente.groove.annotation.BoolSeq;
import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.RealSeq;
import nl.utwente.groove.annotation.StringSeq;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserSeq;
import nl.utwente.groove.util.AIGenerated;

/**
 * User operations whose last parameter is a sequence, one for each sequence type.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserOpsSeq {
    /** Evaluates the polynomial with given coefficients, in the order of increasing degree. */
    @UserOperation
    static public int horner(int x, IntSeq coeffs) {
        int result = 0;
        for (int i = coeffs.size() - 1; i >= 0; i--) {
            result = result * x + coeffs.get(i);
        }
        return result;
    }

    /** Sums up any number of reals. */
    @UserOperation
    static public double total(RealSeq values) {
        double result = 0;
        for (int i = 0; i < values.size(); i++) {
            result += values.get(i);
        }
        return result;
    }

    /** Tests if all of any number of booleans hold. */
    @UserOperation
    static public boolean all(BoolSeq flags) {
        for (int i = 0; i < flags.size(); i++) {
            if (!flags.get(i)) {
                return false;
            }
        }
        return true;
    }

    /** Concatenates any number of strings, with a separator in between. */
    @UserOperation
    static public String join(String separator, StringSeq parts) {
        return String.join(separator, parts.toArray());
    }

    /** Counts the coefficients of any number of polynomials. */
    @UserOperation
    static public int countCoeffs(UserSeq<UserTypePoly> polys) {
        int result = 0;
        for (int i = 0; i < polys.size(); i++) {
            result += polys.get(i).coeffs().size();
        }
        return result;
    }
}
