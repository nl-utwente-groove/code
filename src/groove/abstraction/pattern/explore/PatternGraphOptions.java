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
package groove.abstraction.pattern.explore;

import groove.util.cli.CommandLineOptions;
import groove.util.cli.GrammarHandler;

import java.io.File;

import org.kohsuke.args4j.Argument;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class PatternGraphOptions extends CommandLineOptions {
    /**
     * Returns the grammar location.
     * The location is guaranteed to be an existing directory.
     * @return the (non-{@code null}) grammar location
     */
    public File getGrammar() {
        return this.grammar;
    }

    /**
     * Returns the optional start graph, if set.
     * @return the start graph name; may be {@code null} 
     */
    public String getStartGraph() {
        return this.startGraph;
    }

    /**
     * Returns the optional type graph, if set.
     * @return the type graph name; may be {@code null} 
     */
    public String getTypeGraph() {
        return this.typeGraph;
    }

    @Argument(metaVar = "grammar", required = true,
            usage = "grammar location (default extension .gps)",
            handler = GrammarHandler.class)
    private File grammar;

    @Argument(index = 1, metaVar = "start",
            usage = "Start graph name (defined in grammar, no extension)")
    private String startGraph;

    @Argument(index = 1, metaVar = "type",
            usage = "Type graph name (defined in grammar, no extension)")
    private String typeGraph;
}
