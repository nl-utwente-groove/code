/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.transform.oracle;

import groove.util.DocumentedEnum;
import groove.util.Exceptions;

/** Kind of oracle. */
public enum ValueOracleKind implements DocumentedEnum {
    /** No oracle. */
    NONE,
    /** Default value oracle. */
    DEFAULT,
    /** Random value oracle. */
    RANDOM,
    /** User dialog input. */
    DIALOG,
    /** (File) reader input. */
    READER,;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public String getExplanation() {
        switch (this) {
        case DEFAULT:
            return "Returns the default value of the type";
        case DIALOG:
            return "Asks the user to input a value of the type";
        case NONE:
            return "No oracle";
        case RANDOM:
            return "Returns a random value of the type";
        case READER:
            return "Reads values from a file";
        default:
            throw Exceptions.UNREACHABLE;
        }
    }
}