// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: Validator.java,v 1.2 2007-03-27 14:18:37 rensink Exp $
 */
package groove.io;

import groove.graph.GraphFormatException;
import groove.graph.GraphShape;
import groove.graph.aspect.AspectGraph;
import groove.trans.NameLabel;
import groove.trans.view.AspectRuleView;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Application to check graph and rule file formats.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class Validator {
    static public final String OPTION_PREFIX = "-";
    // options
    static public final String HELP_OPTION = "h";
    static public final String VERBOSE_OPTION = "v";
    static public final String QUIET_OPTION = "q";
    // option descriptions
    static public final String HELP_DESCRIPTION = "Prints this message";
    static public final String VERBOSE_DESCRIPTION = "Run in verbose mode";
    static public final String QUIET_DESCRIPTION = "Run in quiet mode";
    //
    //    static public final int QUIET_MODE = 0;
    //    static public final int NORMAL_MODE = 1;
    //    static public final int VERBOSE_MODE = 2;

    /**
     * Main method. Parameters are options and file names.
     * 
     * @param args an array of strings, interpreted as options or file names
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(OPTION_PREFIX))
                processOption(args[i].substring(OPTION_PREFIX.length()));
            else
                files.add(new File(args[i]));
        }
        if (!helpGiven) {
            GraphFileHandler fileValidator = new FileValidator(verbosity);
            if (verbosity == GraphFileHandler.VERBOSE_MODE)
                System.out.println("Running validator in verbose mode");
            if (files.isEmpty()) {
                // no files were specified; take the working directory
                fileValidator.handle(new File("."));
            } else {
                fileValidator.handle(files);
            }
            if (verbosity > GraphFileHandler.QUIET_MODE) {
                System.out.println("Number of graphs validated: " + (graphsValidated - rulesValidated));
                System.out.println("Number of rules validated: " + rulesValidated);
            }
            System.out.print(errorsFound == 0 ? "No" : "" + errorsFound);
            System.out.print(errorsFound == 1 ? " error" : " errors");
            System.out.println(" found");
            System.exit(errorsFound > 0 ? 1 : 0);
        }
    }

    static private void processOption(String option) {
        if (option.equals(HELP_OPTION)) {
            printHelpText();
        } else if (option.equals(VERBOSE_OPTION)) {
            setVerbosity(GraphFileHandler.VERBOSE_MODE);
        } else if (option.equals(QUIET_OPTION)) {
            setVerbosity(GraphFileHandler.QUIET_MODE);
        }
    }

    static private void printHelpText() {
        helpGiven = true;
        System.out.print("Usage: Validator ");
        for (int i = 0; i < OPTIONS.length; i++) {
            System.out.print("[" + OPTION_PREFIX + OPTIONS[i] + "] ");
        }
        System.out.println("[files]");
        System.out.println("Options: ");
        for (int i = 0; i < OPTIONS.length; i++) {
            System.out.println("  " + OPTION_PREFIX + OPTIONS[i] + "  " + OPTION_DESCRIPTIONS[i]);
        }
        System.out.println("\nFiles can be either graph or rule files or directories;");
        System.out.println("Directories are recursively validated for files with known extensions.");
        System.out.println("If no file is provided, the current working directory is validated.");

        System.out.println("\nCurrently known extensions:");
        for (int i = 0; i < FILTERS.length; i++) {
            System.out.println("  " + FILTERS[i].getDescription());
        }
    }

    /** Sets the verbosity of the validation. */
    static private void setVerbosity(int verbosity) {
        Validator.verbosity = verbosity;
    }

    static private class FileValidator extends GraphFileHandler {
        public FileValidator(int verbosity) {
            super(verbosity);
        }

        @Override
        public void handleGraph(File file) {
            super.handleGraph(file);
            validateGraph(file);
        }

        @Override
        public void handleRule(File file) {
            super.handleRule(file);
            validateRule(file);
        }
    }

    /** Validates a file supposedly containing a graph. */
    static private GraphShape validateGraph(File file) {
        try {
            graphsValidated++;
            String name = verbosity == GraphFileHandler.VERBOSE_MODE ? file.getName() : file.toString();
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.print("* Validating " + name + " as a graph: ");
            GraphShape result = gxl.unmarshal(file);
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.println("OK");
            return result;
        } catch (IOException exc) {
            errorsFound++;
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.println("ERROR: " + exc.getMessage());
            else
                System.out.println("Graph format error in " + file + ": " + exc.getMessage());
            return null;
        }
    }

    /** Validates a file supposedly containing a rule. */
    static private void validateRule(File file) {
        rulesValidated++;
        GraphShape graph = validateGraph(file);
        if (graph == null)
            // there was already an error in the graph format 
            return;
        try {
            String name = verbosity == GraphFileHandler.VERBOSE_MODE ? file.getName() : file.toString();
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.print("* Validating " + name + " as a production rule: ");
            new AspectRuleView(AspectGraph.getFactory().fromPlainGraph(graph), new NameLabel(file.getName()));
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.println("OK");
        } catch (GraphFormatException exc) {
            errorsFound++;
            if (verbosity > GraphFileHandler.QUIET_MODE)
                System.out.println("ERROR: " + exc.getMessage());
            else
                System.out.println("Rule format error in " + file + ": " + exc.getMessage());
        }
    }

    /** Array of all options. */
    static private final String[] OPTIONS = { HELP_OPTION, VERBOSE_OPTION };
    /** Array op all option descriptions. */
    static private final String[] OPTION_DESCRIPTIONS = { HELP_DESCRIPTION, VERBOSE_DESCRIPTION };
    static private final ExtensionFilter[] FILTERS =
        { Groove.createGxlFilter(false), Groove.createRuleFilter(false), Groove.createStateFilter(false)};

    /** Array of files to be checked. */
    static private final List<File> files = new ArrayList<File>();
    /** Verbosity of validation: 0 = quiet, 1 = normal, 2 = verbose */
    static private int verbosity = 1;
    /** Signals that the help option was included. */
    static private boolean helpGiven = false;
    /** The number of graphs validated. */
    static private int graphsValidated;
    /** The number of rules validated. */
    static private int rulesValidated;
    /** The number of validation errors found. */
    static private int errorsFound;
    /** The GXL transformer used in validation. */
    static private Xml gxl = new UntypedGxl();
}
