/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: CTLModelChecker.java,v 1.14 2008-03-28 07:03:03 kastenberg Exp $
 */
package groove.verify;

import groove.explore.Generator;
import groove.lts.GTS;
import groove.util.CommandLineTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Command-line tool directing the model checking process.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-28 07:03:03 $
 */
public class CTLModelChecker extends CommandLineTool {

    /** Usage message for the generator. */
    static public final String USAGE_MESSAGE =
        "Usage: ModelChecker <grammar-location> [property] [-g [generator-options]]";
    /** QUIT option */
    private static final String QUIT_OPTION = "Q";

    /**
     * Main method.
     * @param args the list of command-line arguments
     */
    public static void main(String args[]) {
        List<String> argList = new LinkedList<String>(Arrays.asList(args));
        List<String> checkerArgs;
        List<String> genArgs;
        if (argList.contains("-g")) {
            int splitPoint = argList.indexOf("-g");
            checkerArgs =
                new LinkedList<String>(argList.subList(0, splitPoint));
            genArgs =
                new LinkedList<String>(argList.subList(splitPoint + 1,
                    argList.size()));
        } else {
            checkerArgs = argList;
            genArgs = null;
        }
        CTLModelChecker verifier = new CTLModelChecker(checkerArgs, genArgs);
        verifier.start();
    }

    /**
     * Constructor.
     * @param checkerArgs the command line arguments for the model checker.
     * @param genArgs the command line arguments for the generator.
     */
    public CTLModelChecker(List<String> checkerArgs, List<String> genArgs) {
        super(checkerArgs.toArray(new String[0]));
        this.genArgs = genArgs.toArray(new String[0]);
        this.properties = new LinkedList<Formula>();
    }

    /**
     * Method managing the actual work to be done.
     */
    public void start() {
        processArguments();
        this.generator = new Generator(this.genArgs);
        this.generator.start();
        this.gts = this.generator.getGTS();
        this.marker = new DefaultMarker(this.property, this.gts);
        long startTime = System.currentTimeMillis();

        while (this.properties.size() > 0) {
            this.setProperty(this.properties.remove(0));
            System.out.println("Checking CTL formula: " + this.property);
            this.marker.verify();
            if (this.marker.hasValue(false)) {
                System.out.println("The model violates the given property.");
            } else {
                System.out.println("The model satisfies the given property.");
            }
        }

        long endTime = System.currentTimeMillis();
        long mcTime = endTime - startTime;

        println("** Model Checking Time (ms):\t" + mcTime);
        println("** Total Running Time (ms):\t"
            + (this.generator.getRunningTime() + mcTime));

    }

    /**
     * Goes through the list of command line arguments and tries to find command
     * line options. The options and their parameters are subsequently removed
     * from the argument list. If an option cannot be parsed, the method prints
     * an error message and terminates the program.
     */
    @Override
    public void processArguments() {
        List<String> argsList = getArgs();
        while (argsList.size() > 1) {
            this.addProperty(argsList.remove(1));
        }
        if (argsList.size() == 0) {
            this.printError("No grammar location specified", true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see groove.util.CommandLineTool#supportsLogOption()
     */
    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see groove.util.CommandLineTool#supportsOutputOption()
     */
    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see groove.util.CommandLineTool#supportsVerbosityOption()
     */
    @Override
    protected boolean supportsVerbosityOption() {
        return false;
    }

    /** Adds the string property to the list of properties to be checked. */
    public void addProperty(String property) {
        try {
            this.properties.add(FormulaParser.parse(property).toCtlFormula());
        } catch (ParseException efe) {
            print("Format error in property: " + efe.getMessage());
        }
    }

    /**
     * Ordinary set-method.
     * @param property the property to be checked
     */
    public void setProperty(Formula property) {
        this.property = property;
    }

    /**
     * Asks the user for the next property to be verified.
     * @return <tt>true</tt> if the user wants to verify another property,
     *         <tt>false</tt> otherwise
     */
    public boolean nextProperty() {
        boolean result = false;
        try {
            BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter CTL-expression (or '" + QUIT_OPTION
                + "' to quit):\n> ");
            String expression = in.readLine();
            if (!(expression.equals(QUIT_OPTION))) {
                setProperty(FormulaParser.parse(expression).toCtlFormula());
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException efe) {
            System.err.println("Wrong format. Retry.");
            return nextProperty();
            // efe.printStackTrace();
        }
        return result;
    }

    /**
     * Returns a usage message for the command line tool.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    /**
     * Flag to indicate whether to check a single property or to ask for
     * properties interactively.
     */
    // private boolean checkSingleProperty = false;
    /**
     * The generator used for generating the state space.
     */
    private Generator generator;
    /** The list of options to the generator. */
    private String[] genArgs = null;
    /**
     * The state space (with graphs as states) to be model-checked.
     */
    private GTS gts;

    /**
     * The CTL-expression to be checked for.
     */
    private Formula property;

    /**
     * The list of CTL formulas to be checked.
     */
    private List<Formula> properties;

    /**
     * The state marker.
     */
    private DefaultMarker marker;
}