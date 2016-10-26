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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Option handler for the (no-argument) help option.
 * Stops option parsing when invoked.
 */
public class HelpHandler extends OptionHandler<Boolean> {
    /** Required constructor. */
    public HelpHandler(CmdLineParser parser, OptionDef option,
            Setter<? super Boolean> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        ((GrooveCmdLineParser) this.owner).setHelp();
        this.setter.addValue(true);
        return params.size();
    }

    @Override
    public String getDefaultMetaVariable() {
        return null;
    }

    /** Name of the help option. */
    public final static String NAME = "-h";
    /** Usage message for the help option. */
    public final static String USAGE = "Print this help message and exit";
}