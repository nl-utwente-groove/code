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
package nl.utwente.groove.util.cli;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import nl.utwente.groove.io.FileType;

/**
 * Option handler that checks whether a value is an existing directory.
 */
public class DirectoryHandler extends FileOptionHandler {
    /** Constructor that allows an optional extension filter to be specified. */
    protected DirectoryHandler(CmdLineParser parser, OptionDef option,
            Setter<? super File> setter, FileType filter) {
        super(parser, option, setter);
        this.filter = filter;
    }

    /** Required constructor. */
    public DirectoryHandler(CmdLineParser parser, OptionDef option,
            Setter<? super File> setter) {
        this(parser, option, setter, null);
    }

    @Override
    protected File parse(String argument) throws CmdLineException {
        File result = super.parse(argument);
        if (!result.isDirectory()) {
            // see if we can set an extension
            if (this.filter != null && !this.filter.hasExtension(result)) {
                result = new File(this.filter.addExtension(result.getPath()));
            }
            if (!result.isDirectory()) {
                throw new CmdLineException(this.owner, String.format(
                    "File name \"%s\" must be an existing directory", result));
            }
        }
        return result;
    }

    private final FileType filter;
}