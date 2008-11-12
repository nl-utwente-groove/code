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

import groove.io.FileGps;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.trans.Condition;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.util.CommandLineTool;
import groove.util.Generator;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Command-line tool directing the model checking process.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-28 07:03:03 $
 */
public class CTLModelChecker extends CommandLineTool {

    /** Usage message for the generator. */
    static public final String USAGE_MESSAGE =
        "Usage: ModelChecker <grammar-location> [<property>]";
    /** Extension for graph files */
    static public final String GRAPH_FILE_EXTENSION = ".graphs";
    /** QUIT option */
    private final String QUIT_OPTION = "Q";

    /**
     * Symbol used for special states, i.e. final and unexplored states
     */
    static public final String SPECIAL_STATE_PREFIX = "$";

    /**
     * Main method.
     * @param args the list of command-line arguments
     */
    public static void main(String args[]) {
        List<String> argList = new LinkedList<String>(Arrays.asList(args));
        CTLModelChecker verifier = new CTLModelChecker(argList);
        verifier.start();
    }

    /**
     * Constructor.
     * @param args the command line arguments for the generator.
     */
    public CTLModelChecker(List<String> args) {
        super(args);
    }

    /** Constructs a model checker for a given LTS and property. */
    public CTLModelChecker(LTS lts, TemporalFormula property) {
        super(Collections.<String>emptyList());
        this.gts = (GTS) lts;
        this.property = property;
        this.marker = new CTLTransitionMarker();
    }

    /**
     * Starts the marking process.
     */
    public void verify() {
        this.marker.mark(new DefaultMarking(), getProperty(), this.gts, this);
    }

    /**
     * Method managing the actual work to be done.
     */
    public void start() {
        processArguments();
        this.generator = new Generator(getArgs());
        this.generator.start();
        this.gts = this.generator.getGTS();
        this.grammar = this.generator.getGrammar();
        this.marker = new CTLTransitionMarker();
        if (this.checkSingleProperty) {
            this.marker.mark(this.marking, getProperty(), this.gts, this);
            if (this.property.getCounterExamples().contains(
                this.gts.startState())) {
                System.err.println("The model violates the given property.");
            } else {
                System.out.println("The model satisfies the given property.");
            }
        } else {
            // listPredicates();
            while (nextProperty()) {
                this.marker.mark(this.marking, getProperty(), this.gts, this);

                if (this.property.getCounterExamples().contains(
                    this.gts.startState())) {
                    System.err.println("The model violates the given property.");
                } else {
                    System.out.println("The model satisfies the given property.");
                }
            }
        }
        if (REPORT) {
            report();
        }
    }

    /**
     * Goes through the list of command line arguments and tries to find command
     * line options. The options and their parameters are subsequently removed
     * from the argument list. If an option cannot be parsed, the method prints
     * an error message and terminates the program.
     */
    @Override
    public void processArguments() {
        super.processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() == 2) {
            setProperty(argsList.remove(1));
            this.checkSingleProperty = true;
        } else if (argsList.size() == 0) {
            printError("No grammar location specified");
        }
    }

    /*
     * (non-Javadoc)
     * @see groove.util.CommandLineTool#supportsLogOption()
     */
    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see groove.util.CommandLineTool#supportsOutputOption()
     */
    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see groove.util.CommandLineTool#supportsVerbosityOption()
     */
    @Override
    protected boolean supportsVerbosityOption() {
        return false;
    }

    /**
     * Returns the graph condition (read: rule) with the given name
     * @param name the name of the rule to be returned
     * @return the graph condition with the given name
     */
    public Condition getGraphCondition(String name) {
        return this.grammar.getRule(new NameLabel(name));
    }

    /**
     * Returns the predecessor map.
     * @return the predecessor map.
     */
    public Map<GraphState,Collection<GraphState>> getPredecessorMap() {
        if (this.predecessorMap == null) {
            createPredecessorMap();
        }
        return this.predecessorMap;
    }

    /**
     * Returns the CTL-expression to be model-checked.
     * @return the CTL-expression to be model-checked.
     */
    public TemporalFormula getProperty() {
        return this.property;
    }

    /** Sets the property to be checked (as a string). */
    public void setProperty(String property) {
        try {
            setProperty(CTLFormula.parseFormula(property));
        } catch (FormatException efe) {
            print("Format error in property: " + efe.getMessage());
        }
    }

    /**
     * Ordinary set-method.
     * @param property the property to be checked
     */
    public void setProperty(TemporalFormula property) {
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
            System.out.print("Enter CTL-expression (or '" + this.QUIT_OPTION
                + "' to quit):\n> ");
            String expression = in.readLine();
            if (!(expression.equals(this.QUIT_OPTION))) {
                setProperty(CTLFormula.parseFormula(expression));
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException efe) {
            System.err.println("Wrong format. Retry.");
            return nextProperty();
            // efe.printStackTrace();
        }
        return result;
    }

    /**
     * Sets the new marker.
     * @param marker the new marker
     */
    public void setMarkingVisitor(CTLFormulaMarker marker) {
        this.marker = marker;
    }

    /** Sets the name of the location where the grammar may be found. */
    public void setGrammarLocation(String location) {
        this.grammarLocation = location;
    }

    /** Sets the name of the start state of the exploration. */
    public void setStartStateName(String name) {
        this.startStateName = name;
    }

    /**
     * Constructs a mapping from states to a set containing its predecessor
     * states.
     */
    private void createPredecessorMap() {
        if (this.predecessorMap == null) {
            this.predecessorMap =
                new HashMap<GraphState,Collection<GraphState>>();
        }
        // first create an empty set of predecessors for every state
        Iterator<? extends GraphState> stateIter =
            this.gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            this.predecessorMap.put(nextState, new ArrayList<GraphState>());
        }
        // now fill the predecessor-collection for every state
        Iterator<? extends GraphTransition> transitionIter =
            this.gts.edgeSet().iterator();
        while (transitionIter.hasNext()) {
            GraphTransition nextTransition = transitionIter.next();
            if (nextTransition.getEvent().getRule().isModifying()) {
                GraphState sourceState = nextTransition.source();
                GraphState targetState = nextTransition.target();
                // add this source-state to the collection of predecessors
                if (this.predecessorMap.containsKey(targetState)) {
                    Collection<GraphState> predecessors =
                        this.predecessorMap.get(targetState);
                    predecessors.add(sourceState);
                    this.predecessorMap.put(targetState, predecessors);
                }
                // create a new collection of predecessors containing only this
                // source-state
                else {
                    ArrayList<GraphState> predecessors =
                        new ArrayList<GraphState>();
                    predecessors.add(sourceState);
                    this.predecessorMap.put(targetState, predecessors);
                }
            } else {
                // the rule does not change the graph, which means that this
                // graph transition is not really a transition; therefore we
                // disregard this source state as a predecessor of the tar
            }
        }
    }

    /**
     * Loads the graph-grammar.
     */
    protected void loadGrammar() {
        if (this.grammarLocation != null) {
            try {
                this.grammar =
                    this.loader.unmarshal(new File(this.grammarLocation),
                        this.startStateName).toGrammar();
                this.grammar.setFixed();
            } catch (IOException exc) {
                printError("Can't load grammar: " + exc.getMessage());
            } catch (FormatException exc) {
                printError("Grammar format error: " + exc.getMessage());
            }
        } else {
            System.err.println("Grammar-location and start-state unspecified.");
        }
    }

    /**
     * Prints the collection of counter-example states.
     */
    protected void counterExamples() {
        if (this.property.hasCounterExamples()) {
            System.out.println("The following "
                + this.property.getCounterExamples().size() + " (out of "
                + this.gts.nodeCount()
                + ") states do not satisfy the property "
                + this.property.toString());
            System.out.println(this.property.getCounterExamples());
        } else {
            System.out.println("No counter-examples have been found.");
        }
    }

    /**
     * Factory method for the grammar loader to be used by state space
     * generation. This implementation returns a loader whose rules are
     * generated by <tt>RuleFactory</tt>.
     * @return the grammar loader
     */
    protected FileGps createGrammarLoader() {
        return new FileGps(false);
    }

    /**
     * Creates the set of strings of rule names.
     * @return the set of strings of rule names
     */
    protected Set<String> getPotentialPredicates() {
        Set<String> result = new HashSet<String>();
        Iterator<Rule> ruleIter = this.grammar.getRules().iterator();

        // listing only the occurring labels is not sufficient, since rules may
        // not have been applied and
        // therefore may not be represented in the graph transition system
        while (ruleIter.hasNext()) {
            Rule nextRule = ruleIter.next();
            result.add(nextRule.getName().name());
        }
        return result;
    }

    /**
     * Writes results to the files it asks for.
     */
    protected void report() {
        try {
            BufferedReader systemIn =
                new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Log file? ");
            String filename = systemIn.readLine();
            if (filename.length() != 0) {
                groove.util.Reporter.report(new PrintWriter(new FileWriter(
                    filename + ".log", true), true));
            }
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
        groove.util.Reporter.report(new PrintWriter(System.out));
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
    private boolean checkSingleProperty = false;
    /**
     * The generator used for generating the state space.
     */
    private Generator generator;
    /**
     * The state space (with graphs as states) to be model-checked.
     */
    private GTS gts;
    /**
     * Mapping from state to predecessor states.
     */
    private Map<GraphState,Collection<GraphState>> predecessorMap;
    /**
     * The marking of the state space.
     */
    private final Marking marking = new DefaultMarking();
    /**
     * The location of the graph-grammar.
     */
    private String grammarLocation = null;
    /**
     * The name of the start-state.
     */
    private String startStateName = null;
    /**
     * The graph-grammar.
     */
    private GraphGrammar grammar = null;

    /**
     * The CTL-expression to be checked for.
     */
    private TemporalFormula property;

    /**
     * 
     */
    // protected final ExtensionFilter ruleSystemFilter =
    // Groove.createRuleSystemFilter();
    /**
     * 
     */
    // protected final ExtensionFilter gxlFilter = Groove.createGxlFilter();
    /**
     * 
     */
    // protected final ExtensionFilter gstFilter = Groove.createStateFilter();
    /**
     * 
     */
    // protected final ExtensionFilter graphFilter = new
    // ExtensionFilter("Serialized graph files",
    // GRAPH_FILE_EXTENSION);
    /**
     * 
     */
    protected final FileGps loader = createGrammarLoader();

    /**
     * The state marker.
     */
    private CTLFormulaMarker marker;

    /**
     * 
     */
    private static final boolean REPORT = false;
}