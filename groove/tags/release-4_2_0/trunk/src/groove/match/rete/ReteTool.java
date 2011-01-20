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

import groove.util.CommandLineOption;
import groove.util.CommandLineTool;
import groove.view.StoredGrammarView;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
public class ReteTool extends CommandLineTool {

    private SaveNetworkOption saveNetworkOption = new SaveNetworkOption();
    private StoredGrammarView grammarView;

    /**
     * 
     * @param args The command-line arguments.
     */
    public ReteTool(List<String> args) {
        super(args);
        addOption(this.saveNetworkOption);
    }

    @Override
    public void processArguments() {
        super.processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            this.grammarView = loadGrammar(argsList.get(0));
        } else {
            printError("No grammar location specified", true);
        }
    }

    private void start() {
        processArguments();
        doSaveReteNetwork();
        print("RETE network shape was successuflly saved to "
            + this.saveNetworkOption.outputFilePath);
    }

    private void doSaveReteNetwork() {
        String name = "RETE-" + getGrammarView().getName();
        String filePath =
            (this.saveNetworkOption.outputFilePath != null)
                    ? this.saveNetworkOption.outputFilePath : name;
        new ReteNetwork(getGrammarView(), false).save(filePath, name);
    }

    private StoredGrammarView loadGrammar(String path) {
        StoredGrammarView result = null;
        try {
            result = StoredGrammarView.newInstance(path);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        return result;
    }

    /**
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new ReteTool(new LinkedList<String>(Arrays.asList(args))).start();

    }

    @Override
    /**
     * Returns a usage message for the command line tool.
     */
    protected String getUsageMessage() {
        return "Usage: ReteTool [options] grammar-location";
    }

    @Override
    protected boolean supportsVerbosityOption() {
        return false;
    }

    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    private StoredGrammarView getGrammarView() {
        return this.grammarView;
    }

    static class SaveNetworkOption implements CommandLineOption {

        protected String outputFilePath;
        /** Abbreviation of the format option. */
        static public final String NAME = "f";
        /** Short description of the format option. */
        static public final String DESCRIPTION =
            "Save a the shape of the RETE network for the given grammar.";

        @Override
        public String[] getDescription() {
            return new String[] {DESCRIPTION};
        }

        @Override
        public String getName() {
            return "s";
        }

        @Override
        public String getParameterName() {
            return "graph-file-path";
        }

        @Override
        public boolean hasParameter() {
            return true;
        }

        @Override
        public void parse(String parameter) throws IllegalArgumentException {
            if (parameter.trim().length() == 0) {
                throw new IllegalArgumentException(
                    "An output graph file path should be specified.");
            }
            this.outputFilePath = parameter;
        }

        public String getOutputFilePath() {
            return this.outputFilePath;
        }
    }
}
