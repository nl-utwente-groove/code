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

import groove.io.FileType;

import java.io.File;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Setter;

/**
 * Option handler that adds a {@code .gps} extension to a directory name,
 * if the directory has no extension and does not exist.
 */
public class GrammarHandler extends DirectoryHandler {
    /** Required constructor. */
    public GrammarHandler(CmdLineParser parser, OptionDef option,
            Setter<? super File> setter) {
        super(parser, option, setter, FileType.GRAMMAR);
    }

    /** Meta-variable for the grammar argument. */
    public static final String META_VAR = "grammar";
    /** Usage message of the grammar argument. */
    public static final String USAGE =
        "Grammar location (default extension .gps)";
}