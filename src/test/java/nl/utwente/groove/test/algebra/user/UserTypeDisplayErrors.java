/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.algebra.user;

import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;

/**
 * User type with display methods that are all unfit, in different ways.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@UserType
public record UserTypeDisplayErrors(int value) {
    /** A display method may not be static. */
    @UserDisplay
    public static String showStatic() {
        return "static";
    }

    /** A display method may not have parameters. */
    @UserDisplay
    public String showPar(int width) {
        return "width " + width;
    }

    /** A display method has to return a string. */
    @UserDisplay
    public int showInt() {
        return value();
    }

    /** A display method has to be public. */
    @UserDisplay
    String showHidden() {
        return "hidden";
    }
}
