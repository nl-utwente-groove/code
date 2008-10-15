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
 * $Id: AspectualViewGps.java,v 1.25 2008-03-18 12:18:24 fladder Exp $
 */

package groove.io;

import groove.control.ControlView;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

/**
 * A class that takes care of loading in a rule system consisting of a set of
 * individual files containing graph rules, from a given location --- presumably
 * the top level directory containing the rule files. The class is an observable
 * to enable the use of progress information. Updates consist of a
 * <code>String</code> update indicating the type of object loaded, followed
 * by an <code>Integer</code> indicating the number of number of objects of
 * this type, followed by a null update to indicate the end of this type of
 * load.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectualViewGps extends Observable implements
        GrammarViewXml<DefaultGrammarView> {
    /**
     * Constructs an instance based on a given graph format reader, and a given
     * production rule, to be used as a rule factory. If no file with the
     * default start graph name exists then the empty graph is taken as a start
     * graph.
     * @param graphFactory the graph format reader
     */
    private AspectualViewGps(GraphFactory graphFactory) {
        this.graphMarshaller = createGraphMarshaller(graphFactory);
    }

    /**
     * Constructs an instance based on the standard graph and rule factories.
     */
    public AspectualViewGps() {
        this(GraphFactory.getInstance());
    }

    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    public DefaultGrammarView unmarshal(File location) throws IOException {
        return unmarshal(location, null);
    }

    public DefaultGrammarView unmarshal(File location, String startGraphName)
        throws IOException {
        return unmarshal(location, startGraphName, null);
    }

    public DefaultGrammarView unmarshal(File location, String startGraphName,
            String controlName) throws IOException {
        if (!location.exists()) {
            throw new FileNotFoundException(LOAD_ERROR
                + ": rule rystem location \"" + location.getAbsolutePath()
                + "\" does not exist");
        }
        if (!location.isDirectory()) {
            throw new IOException(LOAD_ERROR + ": rule system location \""
                + location + "\" is not a directory");
        }

        String grammarName =
            getExtensionFilter().stripExtension(location.getName());
        DefaultGrammarView result = createGrammar(grammarName);

        loadProperties(result, location);
        loadRules(result, location);
        loadStartGraph(result, startGraphName, location);
        loadControl(result, location, controlName);
        return result;
    }

    /**
     * Loads the properties file for a given graph grammar from a given
     * location.
     */
    private void loadProperties(DefaultGrammarView result, File location)
        throws IOException, FileNotFoundException {
        // search for a properties file

        File propertiesFile =
            new File(location,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        if (!propertiesFile.exists()) {
            propertiesFile =
                new File(location,
                    PROPERTIES_FILTER.addExtension(result.getName()));
        }
        Properties grammarProperties = null;
        if (propertiesFile.exists()) {
            grammarProperties = new Properties();
            grammarProperties.load(new FileInputStream(propertiesFile));
            result.setProperties(grammarProperties);
        }
    }

    /**
     * Loads the control program from the location set in the properties of the
     * grammar. Can only be done after rules and properties are loaded. TODO:
     * put this somewhere else?
     */
    private void loadControl(DefaultGrammarView result, File location,
            String controlName) {
        
        if (controlName == null) {
            controlName = "control";
        }
        File controlProgramFile =
            new File(location, controlName + Groove.CONTROL_EXTENSION);
        // backwards compatibilty, trying <grammarname>.gcp also
        if (!controlProgramFile.exists()) {
            controlProgramFile =
                new File(location, result.getName() + Groove.CONTROL_EXTENSION);
        }
        if (controlProgramFile.exists()) {
            ControlView cv = new ControlView(result, controlProgramFile, controlName);
            result.setControl(cv);
        }
    }

    /**
     * Loads the rules for a given graph grammar from a given location.
     */
    private void loadRules(DefaultGrammarView result, File location)
        throws IOException {
        Map<RuleNameLabel,File> ruleMap = new HashMap<RuleNameLabel,File>();
        collectRuleNames(ruleMap, location, null);
        setChanged();
        notifyObservers(LOADING_RULES);
        setChanged();
        notifyObservers(ruleMap.size());
        for (Map.Entry<RuleNameLabel,File> ruleEntry : ruleMap.entrySet()) {
            try {
                result.addRule(loadRule(ruleEntry.getValue(),
                    ruleEntry.getKey(), result.getProperties()));
                setChanged();
                notifyObservers(result);
            } catch (IOException exc) {
                throw new IOException(exc.getMessage());
            }
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Constructs a map from rule names to files, from the rules in a given
     * directory and its sub-directories.
     * @param result the result map; initially empty output parameter
     * @param directory the directory from which to load the rules
     * @param rulePath the parent rule name; may be <code>null</code> if this
     *        is the top level
     * @return A map from rule names to files where the rules can be loaded from
     */
    private Map<RuleNameLabel,File> collectRuleNames(
            Map<RuleNameLabel,File> result, File directory,
            RuleNameLabel rulePath) throws IOException {
        File[] files = directory.listFiles(RULE_FILTER);
        if (files == null) {
            throw new IOException(LOAD_ERROR + ": no files found at "
                + directory);
        } else {
            // read in production rules
            for (File file : files) {
                String fileName = RULE_FILTER.stripExtension(file.getName());
                PriorityFileName priorityFileName =
                    new PriorityFileName(fileName);
                RuleNameLabel ruleName =
                    new RuleNameLabel(rulePath,
                        priorityFileName.getActualName());
                // check for overlapping rule and directory names
                if (file.isDirectory()) {
                    collectRuleNames(result, file, ruleName);
                } else if (result.put(ruleName, file) != null) {
                    throw new IOException(LOAD_ERROR
                        + ": duplicate rule name \"" + ruleName + "\"");
                }
            }
        }
        return result;
    }

    /**
     * Loads in and returns a single rule from a given location, giving it a
     * given name and priority.
     */
    private AspectualRuleView loadRule(File location, RuleNameLabel ruleName,
            SystemProperties properties) throws IOException {
        AspectGraph unmarshalledRule =
            getGraphMarshaller().unmarshalGraph(location);
        GraphInfo.setRole(unmarshalledRule, Groove.RULE_ROLE);
        AspectualRuleView result =
            createRuleView(unmarshalledRule, ruleName, properties);
        return result;
    }

    /**
     * @param result
     * @param startGraphName
     * @param location
     * @throws IOException
     */
    private void loadStartGraph(DefaultGrammarView result,
            String startGraphName, File location) throws IOException {
        // determine the start graph file
        File startGraphFile;
        if (startGraphName == null) {
            startGraphFile =
                new File(location,
                    STATE_FILTER.addExtension(DEFAULT_START_GRAPH_NAME));
            if (!startGraphFile.exists()) {
                startGraphFile = null;
            }
        } else {
            startGraphFile = new File(startGraphName);
            if (!startGraphFile.exists()) {
                startGraphFile =
                    new File(location,
                        STATE_FILTER.addExtension(startGraphName));
            }
        }
        // get the start graph
        if (startGraphFile != null) {
            try {
                setChanged();
                notifyObservers(LOADING_START_GRAPH);
                setChanged();
                notifyObservers(1);
                AspectGraph unmarshalledStartGraph =
                    getGraphMarshaller().unmarshalGraph(startGraphFile);
                GraphInfo.setRole(unmarshalledStartGraph, Groove.GRAPH_ROLE);
                AspectualGraphView startGraph =
                    new AspectualGraphView(unmarshalledStartGraph);
                setChanged();
                notifyObservers(startGraph);
                result.setStartGraph(startGraph);
            } catch (FileNotFoundException exc) {
                throw new IOException(LOAD_ERROR + ": start graph "
                    + startGraphFile + " not found");
            } finally {
                setChanged();
                notifyObservers();
            }
        }
    }

    /**
     * Unmarshals a rule from a file in <tt>.gpr</tt> format. The rule gets
     * the filename (without directory path) as name.
     * @param location the file to get the rule from
     * @param properties the properties for the rule to be unmarshalled
     * @return a rule view for the given rule.
     * @throws IOException
     */
    public AspectualRuleView unmarshalRule(File location,
            SystemProperties properties) throws IOException {
        String filename =
            Groove.createRuleFilter().stripExtension(location.getName());
        PriorityFileName priorityFileName = new PriorityFileName(filename);
        RuleNameLabel ruleName =
            new RuleNameLabel(priorityFileName.getActualName());
        return createRuleView(getGraphMarshaller().unmarshalGraph(location),
            ruleName, properties);
    }

    public void marshal(DefaultGrammarView gg, File location)
        throws IOException {
        createLocation(location);
        String grammarName = GRAMMAR_FILTER.stripExtension(location.getName());
        gg.setName(grammarName);
        // iterate over rules and save them
        for (RuleNameLabel ruleName : gg.getRuleMap().keySet()) {
            // turn the rule into a rule graph
            marshalRule(gg.getRule(ruleName), location);
        }
        saveStartGraph(gg.getStartGraph(), location);
        saveProperties(gg, location);
    }

    /**
     * Prepares a location for marshalling a graph grammar.
     */
    private void createLocation(File location) throws IOException {
        // delete existing file, if any
        if (location.exists()) {
            if (!deleteRecursive(location)) {
                throw new IOException("Existing location " + location
                    + " cannot be deleted");
            }
        }
        // create location as directory
        location.mkdirs();
    }

    /**
     * Recursively traverses all subdirectories and deletes all files and
     * directories.
     */
    private boolean deleteRecursive(File location) {
        if (location.isDirectory()) {
            for (File file : location.listFiles()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            }
        }
        return location.delete();
    }

    /**
     * Saves a graph as start graph (using the default name) in a given
     * location, assumed to be a directory.
     * @see #DEFAULT_START_GRAPH_NAME
     */
    private void saveStartGraph(AspectualGraphView startGraph, File location)
        throws IOException {
        if (startGraph != null) {
            // save start graph
            File startGraphLocation =
                new File(location,
                    STATE_FILTER.addExtension(startGraph.getName()));
            getGraphMarshaller().marshalGraph(startGraph.getAspectGraph(),
                startGraphLocation);
        }
    }

    /**
     * Saves the properties of a graph grammar in a given location, assumed to
     * be a directory.
     * 
     */
    private void saveProperties(DefaultGrammarView gg, File location)
        throws IOException, FileNotFoundException {
        // save properties
        String grammarName = gg.getName();
        File propertiesFile =
            new File(location, PROPERTIES_FILTER.addExtension(grammarName));
        Properties grammarProperties = gg.getProperties();
        grammarProperties.store(new FileOutputStream(propertiesFile),
            "Graph grammar properties for " + gg.getName());
    }

    /**
     * Marshals a single rule, given in graph input format, to a given location.
     * Creates the necessary sub-directories if the rule has a structured rule
     * name.
     * @param ruleGraph the rule to be marshaled
     * @param location the location to which the rule is to be marshaled
     * @throws IOException if {@link Xml#marshalGraph(GraphShape, File)} throws
     *         an exception
     */
    public void marshalRule(AspectualRuleView ruleGraph, File location)
        throws IOException {
        this.graphMarshaller.marshalGraph(ruleGraph.getAspectGraph(), getFile(
            location, ruleGraph, true));
    }

    /**
     * Removes a rule file from the file system
     * @param ruleGraph the rule to be removed
     * @param location the basis location of the GPS
     */
    public void deleteRule(AspectualRuleView ruleGraph, File location) {
        File deletedFile = getFile(location, ruleGraph, false);
        if (deletedFile != null) {
            getGraphMarshaller().deleteGraph(deletedFile);
        }
    }

    /**
     * Constructs a rule file from the combination of a basis location (the
     * directory where the GPS is stored), a rule name, and the
     * {@link #RULE_FILTER} extension. The method looks for an existing file; if
     * there is none, a further parameter determines if it should be created
     * (including any enclosing directories)
     * @param create if <code>true</code>, a file is created if none is found
     */
    private File getFile(File location, AspectualRuleView ruleGraph,
            boolean create) {
        File result = null;
        // if the rule name is structured, go to the relevant sub-directory
        String remainingName = ruleGraph.getName();
        int priority = ruleGraph.getPriority();
        boolean searching = true;
        while (searching) {
            File candidate =
                new File(location, RULE_FILTER.addExtension(remainingName));
            if (!candidate.exists()) {
                PriorityFileName priorityName =
                    new PriorityFileName(remainingName, priority, true);
                candidate =
                    new File(location,
                        RULE_FILTER.addExtension(priorityName.toString()));
            }
            if (candidate.exists()) {
                result = candidate;
            }
            int separator = remainingName.indexOf(RuleNameLabel.SEPARATOR);
            searching = result == null && separator >= 0;
            if (searching) {
                // descend into sub-directory if no file is found and the name
                // specifies a further hierarchy
                String subDir = remainingName.substring(0, separator);
                remainingName = remainingName.substring(separator + 1);
                location = new File(location, subDir);
                // if the sub-directory does not exist, create it or stop the
                // search
                if (!location.exists()) {
                    if (create) {
                        location.mkdir();
                    } else {
                        searching = false;
                    }
                }
            }
        }
        // create a file if none is found and the create parameter says so
        if (result == null && create) {
            result =
                new File(location, RULE_FILTER.addExtension(remainingName));
        }
        return result;
    }

    /**
     * Retrieves the current graph loader. The graph loader is used to read in
     * all rule and state graphs. Set during construction.
     */
    protected Xml<AspectGraph> getGraphMarshaller() {
        return this.graphMarshaller;
    }

    /** Callback factory method for creating a graph marshaller. */
    protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory) {
        return new AspectGxl(new DefaultGxl(graphFactory));
    }

    /**
     * Creates a {@link groove.view.DefaultGrammarView} with the given name.
     * @param name the name of the {@link groove.view.DefaultGrammarView} to be
     *        created
     * @return a new {@link groove.view.DefaultGrammarView} with the given name
     */
    protected DefaultGrammarView createGrammar(String name) {
        return new DefaultGrammarView(name);
    }

    /**
     * Creates an {@link AspectualRuleView}.
     * @param graph the graph from which to create the rule-graph
     * @param ruleName the name of the rule to be created
     * @return the {@link AspectualRuleView} created from the given graph
     */
    protected AspectualRuleView createRuleView(AspectGraph graph,
            RuleNameLabel ruleName, SystemProperties properties) {
        return new AspectualRuleView(graph, ruleName, properties);
    }

    /**
     * The xml reader used to unmarshal graphs.
     */
    private final Xml<AspectGraph> graphMarshaller;

    /** Notification text for the rule loading phase. */
    static public final String LOADING_RULES = "Loading rules";
    /** Notification text for the start graph loading phase. */
    static public final String LOADING_START_GRAPH = "Loading start graph";
    /** Error message if a grammar cannot be loaded. */
    static private final String LOAD_ERROR = "Can't load graph grammar";

    /** File filter for graph grammars in the GPS format. */
    static private final ExtensionFilter GRAMMAR_FILTER =
        Groove.createRuleSystemFilter();
    /** File filter for transformation rules in the GPR format. */
    static private final ExtensionFilter RULE_FILTER =
        Groove.createRuleFilter();
    /** File filter for state graphs in the GST format. */
    static private final ExtensionFilter STATE_FILTER =
        Groove.createStateFilter();
    /** File filter for property files. */
    static private final ExtensionFilter PROPERTIES_FILTER =
        Groove.createPropertyFilter();
}