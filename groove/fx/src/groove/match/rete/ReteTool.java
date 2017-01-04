/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.rete;

import groove.grammar.model.GrammarModel;
import groove.io.FileType;
import groove.util.cli.GrammarHandler;
import groove.util.cli.GrooveCmdLineTool;
import groove.util.parse.FormatException;

import java.io.File;
import java.io.IOException;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Tool for acquiring engine information about the RETE network
 * that is not ordinarily visible to the GROOVE user.
 * 
 * This is basically meant for debugging and studying the RETE engine's
 * behavior.
 * 
 * @author Arash Jalali
 * @version $1$
 */
public class ReteTool extends GrooveCmdLineTool<Object> {

    /**
     * 
     * @param args The command-line arguments.
     */
    public ReteTool(String... args) {
        super("ReteTool", args);
    }

    /** Runs the tool. */
    @Override
    protected Object run() throws Exception {
        String outFileName = doSaveReteNetwork();
        emit("RETE network shape for %s was successfully saved to %s ",
            getGrammarDir(), outFileName);
        return null;
    }

    private String doSaveReteNetwork() throws IOException, FormatException {
        GrammarModel grammar = GrammarModel.newInstance(getGrammarDir());
        String name = grammar.getName() + ".rete";
        String filePath = hasOutFileName() ? getOutFileName() : name;
        new ReteSearchEngine(grammar.toGrammar()).getNetwork().save(filePath,
            name);
        return FileType.GXL.addExtension(filePath);
    }

    private File getGrammarDir() {
        return this.grammarDir;
    }

    private boolean hasOutFileName() {
        return getOutFileName() != null;
    }

    private String getOutFileName() {
        return this.outFileName;
    }

    @Argument(metaVar = GrammarHandler.META_VAR, required = true,
            usage = GrammarHandler.USAGE, handler = GrammarHandler.class)
    private File grammarDir;
    @Option(name = "-s", metaVar = "file",
            usage = "Save the shape of the RETE network in <file>")
    private String outFileName;

    /**
     * Constructs and invokes the tool.
     * Always ends with {@link System#exit(int)};
     * prefer {@link #execute(String[])} for programmatic use.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        tryExecute(ReteTool.class, args);
    }

    /**
     * Constructs and invokes the tool programmatically.
     * @param args The command-line arguments.
     */
    public static void execute(String[] args) throws Exception {
        new ReteTool(args).start();
    }
}
