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

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * Checks if a file option value is an existing file.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExistingFileHandler extends FileOptionHandler {
    /**
     * required constructor.
     */
    public ExistingFileHandler(CmdLineParser parser, OptionDef option,
            Setter<? super File> setter) {
        super(parser, option, setter);
    }

    @Override
    protected File parse(String argument) throws CmdLineException {
        File result = super.parse(argument);
        if (!result.exists()) {
            throw new CmdLineException(this.owner, String.format(
                "Argument '%s' is not an existing file", argument));
        }
        return result;
    }

}
