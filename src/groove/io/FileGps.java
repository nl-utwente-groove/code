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
 * $Id$
 */
package groove.io;

import groove.control.ControlView;
import groove.graph.GraphShape;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class FileGps extends AspectualViewGps {

    /**
     * Creates a grammar (un)marshaller for .gps directories.
     * @param layouted whether or not layout iformation must be loaded.
     */
    public FileGps(boolean layouted) {
        super(layouted);
    }

    /**
     * 
     */
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
        saveControl(gg.getControl(), location);
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
        this.getGraphMarshaller().marshalGraph(ruleGraph.getAspectGraph(),
            getFile(location, ruleGraph, true));
    }

    /**
     * Deletes the rule given a grammar location.
     */
    public void deleteRule(AspectualRuleView ruleGraph, File location) {
        this.getGraphMarshaller().deleteGraph(
            getFile(location, ruleGraph, false));
    }

    /**
     * Saves the properties of a graph grammar in a given location, assumed to
     * be a directory.
     * 
     */
    private void saveProperties(DefaultGrammarView grammar, File location)
        throws IOException, FileNotFoundException {
        // save properties
        SystemProperties properties = grammar.getProperties();
        File propertiesFile =
            new File(location,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        properties.store(new FileOutputStream(propertiesFile),
            "Graph grammar properties for " + grammar.getName());
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
     * Saves a graph as start graph (using the default name) in a given
     * location, assumed to be a directory.
     * @see #DEFAULT_START_GRAPH_NAME
     */
    private void saveControl(ControlView control, File location)
        throws IOException {
        if (control != null) {
            // save start graph
            File controlLocation =
                new File(location, STATE_FILTER.addExtension(control.getName()));
            ControlView.store(control.getProgram(), new FileOutputStream(
                controlLocation));
        }
    }

    @Override
    public DefaultGrammarView unmarshal(URL location, String startGraphName,
            String controlName) throws IOException {
        return unmarshal(new File(location.getFile()), startGraphName,
            controlName);
    }

    /**
     * For backwards compatibility, this creates a grammarsource first
     * @param location
     * @return
     */
    public DefaultGrammarView unmarshal(File location) throws IOException {
        return unmarshal(location, DEFAULT_START_GRAPH_NAME,
            DEFAULT_CONTROL_NAME);
    }

    /**
     * Utility method for easy use.
     */
    public DefaultGrammarView unmarshal(File location, String startGraphName)
        throws IOException {
        if (startGraphName == null) {
            startGraphName = DEFAULT_START_GRAPH_NAME;
        }
        return unmarshal(location, startGraphName, DEFAULT_CONTROL_NAME);
    }

    /**
     * unmarshals a grammar for a gps directory with a specific start graph name
     * and a specific control name.
     */
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
        DefaultGrammarView result =
            createGrammar(getExtensionFilter().stripExtension(
                location.getName()));

        // PROPERTIES
        File propertiesFile =
            new File(location,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile =
                new File(location,
                    PROPERTIES_FILTER.addExtension(result.getName()));
        }
        if (propertiesFile.exists()) {
            loadProperties(result, toURL(propertiesFile));
        }

        Map<RuleNameLabel,URL> ruleMap = new HashMap<RuleNameLabel,URL>();
        collectRuleNames(ruleMap, location, null);

        // RULES
        loadRules(result, ruleMap);

        // START GRAPH
        File startGraphFile;
        startGraphFile =
            new File(location, STATE_FILTER.addExtension(startGraphName));
        if (startGraphFile.exists()) {
            loadStartGraph(result, toURL(startGraphFile));
        }

        // CONTROL
        File controlFile =
            new File(location, CONTROL_FILTER.addExtension(controlName));
        // backwards compatibility: <grammar name>.gcp
        if (!controlFile.exists()) {
            controlFile =
                new File(location,
                    CONTROL_FILTER.addExtension(result.getName()));

        }
        if (controlFile.exists()) {
            loadControl(result, controlFile);
        }
        return result;
    }

    public void loadControl(DefaultGrammarView grammar, File controlFile)
        throws IOException {
        String controlName =
            CONTROL_FILTER.stripExtension(controlFile.getName());
        loadControl(grammar, toURL(controlFile), controlName);

    }

    /** returns the extension filter for directory grammars */
    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    /**
     * Convenience method for backwards compatibility. Converts a File to an
     * URL, discarding any exceptions since it is well-formed
     */
    public static URL toURL(File file) {
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (Exception e) {
            //
        }
        return url;
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

    private void collectRuleNames(Map<RuleNameLabel,URL> result,
            File directory, RuleNameLabel rulePath) throws IOException {
        File[] files = directory.listFiles(RULE_FILTER);
        if (files == null) {
            // TOM: empty grammar is hardly an error
            // throw new IOException(LOAD_ERROR + ": no files found at "
            // + directory);
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
                    if (!file.getName().equals(".svn")) {
                        collectRuleNames(result, file, ruleName);
                    }
                } else if (result.put(ruleName, file.toURI().toURL()) != null) {
                    throw new IOException(LOAD_ERROR
                        + ": duplicate rule name \"" + ruleName + "\"");
                }
            }
        }
    }

    private static File getFile(File location, AspectualRuleView ruleGraph,
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

    /** File filter for graph grammars in the GPS format. */
    static protected final ExtensionFilter GRAMMAR_FILTER =
        Groove.createRuleSystemFilter();

    /** File filter for transformation rules in the GPR format. */
    static protected final ExtensionFilter RULE_FILTER =
        Groove.createRuleFilter();

    /** File filter for state files. */
    static protected final ExtensionFilter STATE_FILTER =
        Groove.createStateFilter();

    /** File filter for property files. */
    static protected final ExtensionFilter PROPERTIES_FILTER =
        Groove.createPropertyFilter();

    /** File filter for control files. */
    static protected final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

}
