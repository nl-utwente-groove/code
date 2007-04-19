/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: AspectualGpsGrammar.java,v 1.7 2007-04-19 09:21:32 rensink Exp $
 */

package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.aspect.AspectGraph;
import groove.graph.aspect.AspectualGraphView;
import groove.trans.DefaultRuleFactory;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.trans.SystemProperties;
import groove.trans.StructuredRuleName;
import groove.trans.view.AspectualRuleView;
import groove.trans.view.RuleView;
import groove.trans.view.RuleViewGrammar;
import groove.util.FormatException;
import groove.util.Groove;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A class that takes care of loading in a rule system consisting of a set of individual files
 * containing graph rules, from a given location | presumably the top level directory containing the
 * rule files.
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public class AspectualGpsGrammar implements XmlGrammar<RuleViewGrammar> {
    /** Error message if a grammar cannot be loaded. */
    static private final String LOAD_ERROR = "Can't load graph grammar";

    /** File filter for graph grammars in the GPS format. */
    static private final ExtensionFilter GRAMMAR_FILTER = Groove.createRuleSystemFilter();
    /** File filter for transformation rules in the GPR format. */
    static private final ExtensionFilter RULE_FILTER = Groove.createRuleFilter();
    /** File filter for state graphs in the GST format. */
    static private final ExtensionFilter STATE_FILTER = Groove.createStateFilter();
    /** File filter for property files. */
    static private final ExtensionFilter PROPERTIES_FILTER = Groove.createPropertyFilter();

    
    /**
     * Constructs an instance based on a given graph format reader, and a given production rule, to
     * be used as a rule factory. If no file with the default start graph name exists then the empty
     * graph is taken as a start graph.
     * @param graphFactory the graph format reader
     * @param ruleFactory production rule to be used as a rule factory
     */
    private AspectualGpsGrammar(GraphFactory graphFactory, RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
        this.graphMarshaller = createGraphMarshaller(graphFactory);
    }

    /**
     * Constructs an instance based on the standard graph factory.
     * @param ruleFactory the {@link groove.trans.RuleFactory} for this grammar
     */
    public AspectualGpsGrammar(RuleFactory ruleFactory) {
        this(GraphFactory.getInstance(), ruleFactory);
    }

    /**
     * Constructs an instance based on the standard 
     * graph and rule factories.
     */
    public AspectualGpsGrammar() {
        this(DefaultRuleFactory.getInstance());
    }

    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    public RuleViewGrammar unmarshalGrammar(File location) throws IOException, FormatException {
        return unmarshalGrammar(location, null);
    }
    
    public RuleViewGrammar unmarshalGrammar(File location, String startGraphName) throws IOException, FormatException {
        if (!location.exists()) {
            throw new FileNotFoundException(LOAD_ERROR + ": rule rystem location \"" + location
                    + "\" does not exist");
        }
        if (!location.isDirectory()) {
            throw new IOException(LOAD_ERROR + ": rule system location \"" + location
                    + "\" is not a directory");
        }

        RuleViewGrammar result = createGrammar(getExtensionFilter().stripExtension(location
                .getName()));

        loadProperties(result, location);
        FormatException formatExc = null;
        try {
			loadRules(result, location);
		} catch (FormatException exc) {
			exc.insert(formatExc);
			formatExc = exc;
		}
        try {
			loadStartGraph(result, startGraphName, location);
		} catch (FormatException exc) {
			exc.insert(formatExc);
			formatExc = exc;
		}
		if (formatExc != null) {
			throw formatExc;
		} else {
			return result;
		}
    }

	/**
	 * Loads the properties file for a given graph grammar
	 * from a given location.
	 */
	private void loadProperties(RuleViewGrammar result, File location) throws IOException, FileNotFoundException {
		// search for a properties file
        File propertiesFile = new File(location, PROPERTIES_FILTER.addExtension(result.getName()));
        Properties grammarProperties = null;
        if (propertiesFile.exists()) {
            grammarProperties = new Properties();
            grammarProperties.load(new FileInputStream(propertiesFile));
            result.setProperties(grammarProperties);
        }
	}

	/**
	 * Loads the rules for a given graph grammar from a given location.
	 */
	private void loadRules(RuleViewGrammar result, File location) throws IOException, FormatException {
        // load the rules from location
        loadRules(result, location, null);
	}

    /**
	 * Recursively loads in and returns a rule system consisting of all files in a given directory
	 * and its sub-directories.
     * @param result the graph grammar to store the rules
     * @param directory the directory from which the rules are to be read
     * @param rulePath the extended rule name that is to be the parent of the rules found in
	 *        <tt>directory</tt>
     * @require <tt>directory.exists() && directory.isDirectory()</tt>
	 * @throws IOException if <tt>directory</tt> contains duplicate or malformed production rules
	 */
	private void loadRules(RuleViewGrammar result, File directory,
	        StructuredRuleName rulePath) throws IOException, FormatException {
		FormatException formatExc = null;
	    File[] files = directory.listFiles(RULE_FILTER);
	    if (files == null) {
	        throw new IOException(LOAD_ERROR+": exception when reading rules from location "+directory);
	    } else {
	        // read in production rules
	        for (File file: files) {
	        	String fileName = RULE_FILTER.stripExtension(file.getName());
	            PriorityFileName priorityFileName = new PriorityFileName(fileName);
	            StructuredRuleName ruleName = new StructuredRuleName(rulePath,
	                    priorityFileName.getRuleName());
	            // check for overlapping rule and directory names
	            if (result.getRule(ruleName) != null) {
	                throw new IOException(LOAD_ERROR + ": duplicate rule name \"" + ruleName+"\"");
	            }
	            try {
	            if (file.isDirectory()) {
	                loadRules(result, file, ruleName);
	            } else {
	            	result.add(loadRule(file, priorityFileName.getPriority(), ruleName, result.getProperties()));
	            }
	            } catch (FormatException exc) {
	            	exc.insert(formatExc);
	            	formatExc = exc;
	            }
	        }
	    }
	    if (formatExc != null) {
	    	throw formatExc;
	    }
	}

	/**
	 * Loads in and returns a single rule from a given location, 
	 * giving it a given name and priority, and using a given rule factory.
	 */
	private AspectualRuleView loadRule(File location, int priority, StructuredRuleName ruleName, SystemProperties properties) throws IOException, FormatException {
//		try {
			AspectGraph unmarshalledRule = getGraphMarshaller().unmarshalGraph(location);
		    return createRuleGraph(unmarshalledRule, ruleName, priority, properties);
//		} catch (FormatException exc) {
//		    throw new IOException(LOAD_ERROR + ": rule format error in "
//		            + location.getName()+": "+exc.getMessage());
//		} 
	}

	/**
	 * @param result
	 * @param startGraphName
	 * @param location
	 * @throws FormatException
	 * @throws IOException
	 */
	private void loadStartGraph(RuleViewGrammar result, String startGraphName, File location) throws FormatException, IOException {
		// determine the start graph file
	    File startGraphFile;
	    if (startGraphName == null) {
	        startGraphFile = new File(location, STATE_FILTER.addExtension(DEFAULT_START_GRAPH_NAME));
	        if (! startGraphFile.exists()) {
	            startGraphFile = null;
	        }
	    } else {
	        startGraphFile = new File(startGraphName);
	        if (! startGraphFile.exists()) {
	            startGraphFile = new File(location, STATE_FILTER.addExtension(startGraphName));                
	        }
	    }
	    // get the start graph
	    Graph startGraph = null;
	    if (startGraphFile != null) {
	        try {
	            AspectGraph unmarshalledStartGraph = getGraphMarshaller().unmarshalGraph(startGraphFile);
	            startGraph = new AspectualGraphView(unmarshalledStartGraph).getModel();
	            startGraph.setFixed();
	            result.setStartGraph(startGraph);
	        } catch (FileNotFoundException exc) {
	            throw new IOException(LOAD_ERROR + ": start graph " + startGraphFile + " not found");
	        } catch (FormatException exc) {
	        	throw new FormatException(String.format("Format error in start graph %s: %s", startGraphFile, exc.getMessage()));
	        }
	    }
	}

	/**
     * Unmarshals a rule from a file in <tt>.gpr</tt> format.
     * The rule gets the filename (without directory path) as name.
     * @param location the file to get the rule from
	 * @param properties the properties for the rule to be unmarshalled
     * @return a rule view for the given rule.
     * @throws IOException
     */
    public AspectualRuleView unmarshalRule(File location, SystemProperties properties) throws IOException {
        try {
            String filename = Groove.createRuleFilter().stripExtension(location.getName());
            PriorityFileName priorityFileName = new PriorityFileName(filename);
            StructuredRuleName ruleName = new StructuredRuleName(priorityFileName.getRuleName());
            return createRuleGraph(getGraphMarshaller().unmarshalGraph(location), ruleName, priorityFileName.getPriority(), properties);
        } catch (FormatException exc) {
            throw new IOException("Error in graph format: "+exc.getMessage());
        }
    }

    public void marshalGrammar(RuleViewGrammar gg, File location) throws IOException, FormatException {
        createLocation(location);
		// iterate over rules and save them
		for (NameLabel ruleName : gg.getRuleNames()) {
			// turn the rule into a rule graph
			saveRule(gg.getRuleView(ruleName), location);
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
	        if (!location.delete()) {
	            throw new IOException("Existing location " + location + " cannot be deleted");
	        }
	    }
	    // create location as directory
	    location.mkdirs();
	}

	/**
	 * Saves a rule in a given location.
	 * The rule is given as a rule view; if it is an {@link AspectualRuleView}
	 * then use it directly, otherwise construct an {@link AspectualRuleView}
	 * from the underlying rule.
	 */
	private void saveRule(RuleView ruleView, File location) throws IOException {
		try {
			AspectualRuleView ruleGraph = ruleView instanceof AspectualRuleView ? (AspectualRuleView) ruleView
					: createRuleGraph(ruleView.toRule());
			graphMarshaller.marshalGraph(ruleGraph.getView(), location);
		} catch (FormatException exc) {
			throw new IOException("Error in creating rule graph: "
					+ exc.getMessage());
		}
	}

	/**
	 * Saves a graph as start graph (using the default name) in
	 * a given location, assumed to be a directory.
	 * @see #DEFAULT_START_GRAPH_NAME
	 */
	private void saveStartGraph(Graph startGraph, File location) throws FormatException, IOException {
		// save start graph
        File startGraphLocation = new File(location, DEFAULT_START_GRAPH_NAME);
        AspectualGraphView startGraphView = new AspectualGraphView(startGraph);
        getGraphMarshaller().marshalGraph(startGraphView.getView(), startGraphLocation);
	}

	/**
	 * Saves the properties of a graph grammar in a given location, 
	 * assumed to be a directory.
	 * 
	 */
	private void saveProperties(RuleViewGrammar gg, File location) throws IOException, FileNotFoundException {
		// save propeties
	    File propertiesFile = new File(location, PROPERTIES_FILTER.addExtension(gg.getName()));
	    Properties grammarProperties = gg.getProperties();
	    grammarProperties.store(new FileOutputStream(propertiesFile), "Graph grammar properties for "+gg.getName());
	}

	/**
     * Marshals a single rule, given in graph input format, to a given location.
     * Creates the necessary subdirectories if the rule has a structured rule name.
     * @param ruleGraph the rule to be marshalled
     * @param location the location to which the rule is to be marshalled
     * @throws IOException if {@link Xml#marshalGraph(Graph, File)} throws an exception
     */
    public void marshalRule(RuleView ruleGraph, File location) throws IOException {
        File ruleLocation = location;
        NameLabel ruleName = ruleGraph.getName();
        int priority = ruleGraph.getPriority();
        String coreRuleName;
        // if the rule name is structured, go to the relevant subdirectory
        if (ruleName instanceof StructuredRuleName) {
            String[] tokens = ((StructuredRuleName) ruleName).tokens();
            for (int i = 0; i < tokens.length-1; i++) {
                ruleLocation.mkdir();
                ruleLocation = new File(ruleLocation, tokens[i]);
            }
            coreRuleName = tokens[tokens.length-1];
        } else {
            coreRuleName = ruleName.text();
        }
        String prioritizedRuleName = new PriorityFileName(coreRuleName, priority).toString();
        ruleLocation = new File(ruleLocation, RULE_FILTER.addExtension(prioritizedRuleName));
        // now save the rule
        saveRule(ruleGraph, ruleLocation);
    }

    /**
     * Retrieves the current graph loader.
     * The graph loader is used to read in all rule and state graphs.
     * Set during construction.
     */
    protected Xml<AspectGraph> getGraphMarshaller() {
        return graphMarshaller;
    }

    /** Callback factory method for creating a graph marshaller. */
    protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory) {
    	return new AspectualGxl(new UntypedGxl(graphFactory));
    }
    
    /**
     * Creates a {@link groove.trans.view.RuleViewGrammar} with the given name.
     * @param name the name of the {@link groove.trans.view.RuleViewGrammar} to be created
     * @return a new {@link groove.trans.view.RuleViewGrammar} with the given name
     */
    protected RuleViewGrammar createGrammar(String name) {
        return new RuleViewGrammar(getRuleFactory(), name);
    }

    /**
     * Creates a rule-graph from the given graph by delegating the actual creation
     * to {@link #getRuleFactory()}.
     * @param graph the graph from which to create the rule-graph
     * @param ruleName the name of the rule to be created
     * @param priority the priority of the rule to be created
     * @return the {@link groove.trans.view.RuleGraph} created from the given graph
     * @throws FormatException when the given graph does not conform to
     * the requirements for making a rule-graph out of it
     */
    protected AspectualRuleView createRuleGraph(AspectGraph graph, NameLabel ruleName, int priority, SystemProperties properties)
            throws FormatException {
    	return new AspectualRuleView(graph, ruleName, priority, properties);
//    	return (AspectualRuleView) getRuleFactory().createRuleView(graph, ruleName, priority);
    }
    
    /**
     * Callback method to create a rule graph from a rule.
     * @param rule the rule from which to create a {@link groove.trans.view.RuleGraph}
     * @return the {@link groove.trans.view.RuleGraph} created from the given rule
     * @throws FormatException when the given rule does not conform the
     * requirements for making a {@link groove.trans.view.RuleGraph} from it
     */
    protected AspectualRuleView createRuleGraph(Rule rule) throws FormatException {
        return new AspectualRuleView(rule);
    }

    /** Returns the rule factory passed in at construction time. */
    public RuleFactory getRuleFactory() {
    	return ruleFactory;
    }
//    
//    /**
//	 * Tests if a graph grammar contains attributed graphs.
//	 * @param grammar the grammar for which to check whether it should be able
//	 * to transform attributed graphs
//	 * @return <tt>true</tt> if the given grammar should support the transformation
//	 * of attributed graphs, <tt>false</tt> otherwise
//	 */
//	protected boolean isAttributed(Properties grammarProperties) {
//		String attributes = grammarProperties.getProperty(SystemProperties.ATTRIBUTE_SUPPORT);
//		return attributes != null && attributes.equals(SystemProperties.ATTRIBUTES_YES);
//	}

	/**
     * The xml reader used to unmarshal graphs.
     */
    private final Xml<AspectGraph> graphMarshaller;

    /**
     * The production rule factory used to unmarshal graphs.
     */
    private final RuleFactory ruleFactory;
}