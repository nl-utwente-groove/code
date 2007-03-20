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
 * $Id: GpsGrammar.java,v 1.1.1.2 2007-03-20 10:42:50 kastenberg Exp $
 */

package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphFormatException;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.AttributedGraph;
import groove.trans.AttributedSPORuleFactory;
import groove.trans.DefaultRuleFactory;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.trans.RuleSystem;
import groove.trans.StructuredRuleName;
import groove.trans.view.RuleGraph;
import groove.trans.view.RuleView;
import groove.trans.view.RuleViewGrammar;
import groove.trans.view.ViewFormatException;
import groove.util.Groove;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A class that takes care of loading in a rule system consisting of a set of individual files
 * containing graph rules, from a given location | presumably the top level directory containing the
 * rule files.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GpsGrammar implements XmlGrammar {
    /**
     * The default (initial) graph loader.
     * Set to {@link UntypedGxl}.
     * Can be overridden in the constructor or by {@link #setGraphLoader(Xml)}.
     * @see #getGraphLoader()
     */
    static public final Xml DEFAULT_GRAPH_READER = new UntypedGxl();

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
     * @param graphLoader the graph format reader
     * @param ruleFactory production rule to be used as a rule factory
     */
    public GpsGrammar(Xml graphLoader, RuleFactory ruleFactory) {
        this.graphLoader = graphLoader;
        this.defaultRuleFactory = ruleFactory;
    }

    /**
     * Constructs an instance based on a given graph format reader, and the default rule factory.
     * @param graphLoader the graph format reader
     */
    public GpsGrammar(Xml graphLoader) {
        this(graphLoader, DefaultRuleFactory.getInstance());
    }

    /**
     * Constructs an instance based on the standard (GXL) graph format reader.
     * @param ruleFactory the {@link groove.trans.RuleFactory} for this grammar
     * @see #DEFAULT_GRAPH_READER
     */
    public GpsGrammar(RuleFactory ruleFactory) {
        this(DEFAULT_GRAPH_READER, ruleFactory);
    }

    /**
     * Constructs an instance based on the standard (GXL) graph format reader
     * @see #DEFAULT_GRAPH_READER
     */
    public GpsGrammar() {
        this(DefaultRuleFactory.getInstance());
    }

    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    public GraphGrammar unmarshal(File location) throws IOException {
        return unmarshal(location, null);
    }
    
    /**
     * Unmarshals a rule from a file in <tt>.gpr</tt> format.
     * The rule gets the filename (without directory path) as name.
     * @param location the file to get the rule from
     * @return a {@link RuleGraph} for the given rule.
     * @throws IOException
     */
    public RuleGraph unmarshalRule(File location) throws IOException {
        try {
            String filename = Groove.createRuleFilter().stripExtension(location.getName());
            PriorityFileName priorityFileName = new PriorityFileName(filename);
            StructuredRuleName ruleName = new StructuredRuleName(priorityFileName.getRuleName());
            return createRuleGraph(getGraphLoader().unmarshal(location), ruleName, priorityFileName.getPriority().intValue());
        } catch (GraphFormatException exc) {
            throw new IOException("Error in graph format: "+exc.getMessage());
        }
    }

    public GraphGrammar unmarshal(File location, String startGraphName) throws IOException {
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

        // search for a properties file
        File propertiesFile = new File(location, PROPERTIES_FILTER.addExtension(result.getName()));
        Properties grammarProperties = null;
        //FIXME: actually, every gps shouls contain a properties-file
        // if not available, set default properties
        if (propertiesFile.exists()) {
            grammarProperties = new Properties();
            grammarProperties.load(new FileInputStream(propertiesFile));
            result.setProperties(grammarProperties);
        }
        // set the rule factory for this unmarshaller,
        // as well as the required factories of the resulting grammar
        setFactories(result);

        // load the rules from location
        Map<StructuredRuleName,RuleGraph> ruleGraphMap = new HashMap<StructuredRuleName,RuleGraph>();
        loadRules(location, null, ruleGraphMap);
        for (RuleGraph ruleGraph: ruleGraphMap.values()) {
            result.add(ruleGraph);
        }

        // determine the start graph file
        File startGraphFile;
        if (startGraphName == null) {
            startGraphFile = new File(location, STATE_FILTER.addExtension(DEFAULT_START_STATE_NAME));
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
                startGraph = getGraphLoader().unmarshal(startGraphFile);
                startGraph.setFixed();
                result.setStartGraph(startGraph);
            } catch (FileNotFoundException exc) {
                throw new IOException(LOAD_ERROR + ": start graph " + startGraphFile + " not found");
            }
        }
//        else {
//        	throw new IOException(LOAD_ERROR + ": start graph " + getStateExtensionFilter().addExtension(DEFAULT_START_STATE_NAME) + " not available");
//        }
        return result;
    }

    public void marshal(GraphGrammar gg, File location) throws IOException {
        marshalRules(gg, location);
        // save start graph
        File startGraphLocation = new File(location, XmlGrammar.DEFAULT_START_STATE_NAME);
        getGraphLoader().marshal(gg.getStartGraph(), startGraphLocation);
        // save propeties
        File propertiesFile = new File(location, PROPERTIES_FILTER.addExtension(gg.getName()));
        Properties grammarProperties = gg.getProperties();
        if (grammarProperties.isEmpty()) {
            propertiesFile.delete();
        } else {
            grammarProperties.store(new FileOutputStream(propertiesFile), "Graph grammar properties for "+gg.getName());
        }
    }

    /**
     * Marshals a rule system to a given location.
     * Creates the necessary subdirectories.
     * @param rules the rule system to be marshalled
     * @param location the location to which the rule is to be marshalled
     * @throws IOException if {@link Xml#marshal(GraphShape, File)} throws an exception
     */
    public void marshalRules(RuleSystem rules, File location) throws IOException {
        // delete existing file, if any
        if (location.exists()) {
            if (!location.delete()) {
                throw new IOException("Existing location " + location + " cannot be deleted");
            }
        }
        // create location as directory
        location.mkdirs();
        try {
            // iterate over rules and save them
        	for (Rule rule: rules.getRules()) {
                NameLabel ruleName = rule.getName();
                // turn the rule into a rule graph
                RuleGraph ruleGraph;
                if (rules instanceof RuleViewGrammar) {
                    // maybe the grammar has a rule graph for the rule
                    RuleView ruleView = ((RuleViewGrammar) rules).getRuleView(ruleName);
                    ruleGraph = ruleView instanceof RuleGraph ? (RuleGraph) ruleView : createRuleGraph(rule);
                } else {
                    ruleGraph = createRuleGraph(rule);
                }
                marshalRule(ruleGraph, location);
            }
        } catch (ViewFormatException exc) {
            throw new IOException("Error in creating rule graph: "+exc.getMessage());
        }
    }

    /**
     * Marshals a single rule, given in graph input format, to a given location.
     * Creates the necessary subdirectories if the rule has a structured rule name.
     * @param ruleGraph the rule to be marshalled
     * @param location the location to which the rule is to be marshalled
     * @throws IOException if {@link Xml#marshal(GraphShape, File)} throws an exception
     */
    public void marshalRule(RuleGraph ruleGraph, File location) throws IOException {
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
        NodeEdgeMap ruleToGraphMap = new NodeEdgeHashMap();
        Graph graphForRule = ruleGraph.toGraph(ruleToGraphMap);
        GraphInfo.setLayoutMap(graphForRule, GraphInfo.getLayoutMap(ruleGraph).afterInverse(ruleToGraphMap));
        getGraphLoader().marshal(graphForRule, ruleLocation);
    }

    /**
     * Changes the graph loader used in this grammar loader.
     * @param graphLoader the new graph loader
     * @require <tt>graphLoader! != null</tt>
     * @ensure <tt>getXml().equals(graphLoader)</tt>
     */
    protected void setGraphLoader(Xml graphLoader) {
        this.graphLoader = graphLoader;
    }

    /**
     * Retrieves the current graph loader.
     * The graph loader is used to read in all rule and state graphs.
     * Set during construction or using {@link #setGraphLoader(Xml)}.
     */
    public Xml getGraphLoader() {
        return graphLoader;
    }

    /**
     * Set the factories for the given graph grammar.
     * @param grammar the graph grammar for which to set the factories
     */
    protected void setFactories(RuleViewGrammar grammar) {
		if (isAttributedGrammar(grammar)) {
			setRuleFactory(AttributedSPORuleFactory.getInstance());
            grammar.setGraphFactory(GraphFactory.newInstance(new AttributedGraph(grammar.getGraphFactory())));
		} else if (defaultRuleFactory != null) {
			setRuleFactory(getDefaultRuleFactory());
		}
	}

    /**
     * Tests if a graph grammar contains attributed graphs.
     * @param grammar the grammar for which to check whether it should be able
     * to transform attributed graphs
     * @return <tt>true</tt> if the given grammar should support the transformation
     * of attributed graphs, <tt>false</tt> otherwise
     */
    protected boolean isAttributedGrammar(GraphGrammar grammar) {
		String attributes = grammar.getProperty(GraphGrammar.ATTRIBUTE_SUPPORT);
		return attributes != null && attributes.equals(GraphGrammar.ATTRIBUTES_YES);
    }

    /**
	 * Changes the rule factory currently used in this grammar loader.
	 * @param ruleFactory the new rule factory
	 * @require <tt>ruleFactory! != null</tt>
	 * @ensure <tt>getRuleFactory().equals(ruleFactory)</tt>
	 */
	protected void setRuleFactory(RuleFactory ruleFactory) {
	    this.ruleFactory = ruleFactory;
	}

    /**
	 * Retrieves the default rule factory to be used in this grammar loader. 
	 * This is the factory that is used if no special circumstances apply,
	 * such as attributed graphs.
     * @return the {@link #defaultRuleFactory}
	 * @see #getRuleFactory()
	 * @see #setFactories(RuleViewGrammar)
	 */
    protected RuleFactory getDefaultRuleFactory() {
        return defaultRuleFactory;
    }

    /**
	 * Returns the rule factory to be used for attributed rules.
	 * This implementation returns an {@link AttributedSPORuleFactory}.
     * @return the singleton instance of {@link groove.trans.AttributedSPORuleFactory}
	 * @see #getRuleFactory()
	 * @see #setFactories(RuleViewGrammar)
	 */
    protected RuleFactory getAttributedRuleFactory() {
        return AttributedSPORuleFactory.getInstance();
    }

    /**
     * Recursively loads in and returns a rule system consisting of all files in a given directory
     * and its sub-directories.
     * @param directory the directory from which the rules are to be read
     * @param rulePath the extended rule name that is to be the parent of the rules found in
     *        <tt>directory</tt>
     * @param ruleGraphMap output parameter: mapping from the names of the rules found to the
     *        corresponding rule graphs 
     * @require <tt>directory.exists() && directory.isDirectory()</tt>
     * @throws IOException if <tt>directory</tt> contains duplicate or malformed production rules
     */
    private void loadRules(File directory, StructuredRuleName rulePath,
            Map<StructuredRuleName,RuleGraph> ruleGraphMap) throws IOException {
        File[] files = directory.listFiles(RULE_FILTER);
        if (files == null) {
            throw new IOException(LOAD_ERROR+": exception when reading rules from location "+directory);
        } else {
            // read in production rules
            for (int i = 0; i < files.length; i++) {
                String fileName = RULE_FILTER.stripExtension(files[i].getName());
                PriorityFileName priorityFileName = new PriorityFileName(fileName);
                StructuredRuleName ruleName = new StructuredRuleName(rulePath,
                        priorityFileName.getRuleName());
                // check for overlapping rule and directory names
                if (ruleGraphMap.containsKey(ruleName))
                    throw new IOException(LOAD_ERROR + ": duplicate rule name \"" + ruleName+"\"");
                if (!files[i].isDirectory()) {
                    try {
                        RuleGraph ruleGraph = createRuleGraph(getGraphLoader().unmarshal(files[i]), ruleName, priorityFileName.getPriority().intValue());
                        ruleGraphMap.put(ruleName, ruleGraph);
                    } catch (GraphFormatException exc) {
                        throw new IOException(LOAD_ERROR + ": rule format error in "
                                + files[i].getName()+": "+exc.getMessage());
                    } catch (XmlException exc) {
                        throw new IOException(LOAD_ERROR + ": xml format error in "
                                + files[i].getName()+": "+exc.getMessage());
                    } catch (FileNotFoundException exc) {
                        // proceed; this should not occur but I'm not sure now and don't want
                        // to turn it into an assert
                    }
                } else {
                    assert files[i].isDirectory();
                    loadRules(files[i], ruleName, ruleGraphMap);
                }
            }
        }
    }
    
    /**
     * Creates a {@link groove.trans.view.RuleViewGrammar} with the given name.
     * @param name the name of the {@link groove.trans.view.RuleViewGrammar} to be created
     * @return a new {@link groove.trans.view.RuleViewGrammar} with the given name
     */
    protected RuleViewGrammar createGrammar(String name) {
        return new RuleViewGrammar(name);
    }

    /**
     * Creates a rule-graph from the given graph by delegating the actual creation
     * to {@link #getRuleFactory()}.
     * @param graph the graph from which to create the rule-graph
     * @param ruleName the name of the rule to be created
     * @param priority the priority of the rule to be created
     * @return the {@link groove.trans.view.RuleGraph} created from the given graph
     * @throws GraphFormatException when the given graph does not conform to
     * the requirements for making a rule-graph out of it
     */
    protected RuleGraph createRuleGraph(Graph graph, StructuredRuleName ruleName, int priority)
            throws GraphFormatException {
    	return (RuleGraph) getRuleFactory().createRuleView(graph, ruleName, priority);
    }
    
    /**
     * Callback method to create a rule graph from a rule.
     * @param rule the rule from which to create a {@link groove.trans.view.RuleGraph}
     * @return the {@link groove.trans.view.RuleGraph} created from the given rule
     * @throws ViewFormatException when the given rule does not conform the
     * requirements for making a {@link groove.trans.view.RuleGraph} from it
     * @see #marshal(GraphGrammar, File)
     */
    protected RuleGraph createRuleGraph(Rule rule) throws ViewFormatException {
        return new RuleGraph(rule);
    }

    /* (non-Javadoc)
     * @see groove.io.XmlGrammar#getRuleFactory()
     */
    public RuleFactory getRuleFactory() {
    	return ruleFactory;
    }

    /**
     * The xml reader used to unmarshal graphs.
     */
    protected Xml graphLoader;

    /**
     * The production rule factory used to unmarshal graphs.
     */
    protected RuleFactory ruleFactory;
    /**
     * The default rule factory, set during construction time.
     */
    private final RuleFactory defaultRuleFactory;
}