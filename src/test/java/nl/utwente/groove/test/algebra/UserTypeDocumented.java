// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2026 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.test.algebra;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * Example user type whose operations carry tool tip annotations: parameter
 * documentation with as many lines as the operation has declared parameters,
 * fewer, or more, and texts that are or are not valid format strings.
 * @author Arend Rensink
 * @version $Revision$
 */
@UserType
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-11")
public record UserTypeDocumented(int value) {
    /** Non-static operation with a line for its one declared parameter. */
    @UserOperation
    @ToolTipPars("the factor to scale by")
    public int scaledBy(int factor) {
        return value() * factor;
    }

    /** Static operation with a line for each of its parameters. */
    @UserOperation
    @ToolTipPars({"the first summand", "the second summand"})
    static public int addUp(int first, int second) {
        return first + second;
    }

    /** Static operation with a line for the first of its three parameters only. */
    @UserOperation
    @ToolTipPars("the lower bound")
    static public boolean isBetween(int lower, int mid, int upper) {
        return lower <= mid && mid <= upper;
    }

    /** Static operation with a line although it has no parameters. */
    @UserOperation
    @ToolTipPars("no such parameter")
    static public int nought() {
        return 0;
    }

    /** Non-static operation whose body refers to its operands by position. */
    @UserOperation
    @ToolTipBody("Yields the sum of %1$s and %2$s.")
    public int plus(int increment) {
        return value() + increment;
    }

    /** Static operation none of whose tool tip texts is a valid format string. */
    @UserOperation
    @ToolTipHeader("Share of 100%")
    @ToolTipBody("Yields %2$s, which does not exist.")
    @ToolTipPars("the share, in %")
    static public int whole(int share) {
        return 100 * share;
    }
}
