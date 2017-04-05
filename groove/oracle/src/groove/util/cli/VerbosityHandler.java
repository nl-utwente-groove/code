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
package groove.util.cli;

import groove.explore.Verbosity;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * Option handler that checks whether a verbosity value is in the correct range.
 */
public class VerbosityHandler extends OneArgumentOptionHandler<Verbosity> {
    /** Required constructor. */
    public VerbosityHandler(CmdLineParser parser, OptionDef option,
            Setter<? super Verbosity> setter) {
        super(parser, option, setter);
    }

    @Override
    public Verbosity parse(String value) throws NumberFormatException {
        int result = Integer.parseInt(value);
        if (result < Verbosity.LOWEST || result >= Verbosity.HIGHEST) {
            throw new NumberFormatException();
        }
        return Verbosity.getVerbosity(result);
    }

    /** Name of the verbosity option. */
    public final static String NAME = "-v";
    /** Meta-variable of the verbosity option. */
    public final static String VAR = "level";
    /** Usage message for the verbosity option. */
    public final static String USAGE =
        "Set verbosity level (range = 0 to 2, default = 1)";
}