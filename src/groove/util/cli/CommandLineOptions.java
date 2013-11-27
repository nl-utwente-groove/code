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

import java.io.File;

import org.kohsuke.args4j.Option;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class CommandLineOptions {
    /** 
     * Indicates if the help option has been invoked.
     * If this is the case, then none of the other options have been processed. 
     * @return {@code true} if the help option has been invoked
     */
    public boolean isHelp() {
        return this.help;
    }

    /**
     * Indicates if the log option has been set.
     * @return {@code true} if {@link #getLogDir()} does not return {@code null}
     */
    public boolean isLogging() {
        return getLogDir() != null;
    }

    /**
     * Sets the directory for the log file.
     * If {@code null}, no logging will take place.
     */
    public File getLogDir() {
        return this.logdir;
    }

    /**
     * Returns the verbosity level, as a value between 0 and 2 (inclusive).
     */
    public Verbosity getVerbosity() {
        return this.verbosity;
    }

    @Option(name = HelpHandler.NAME, usage = HelpHandler.USAGE,
            handler = HelpHandler.class)
    private boolean help;

    @Option(name = "-l", metaVar = "dir",
            usage = "Log the generation process in the directory <dir>",
            handler = DirectoryHandler.class)
    private File logdir;

    @Option(name = VerbosityHandler.NAME, metaVar = VerbosityHandler.VAR,
            usage = VerbosityHandler.USAGE, handler = VerbosityHandler.class)
    private Verbosity verbosity = Verbosity.MEDIUM;
}
