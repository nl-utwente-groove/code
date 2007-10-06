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
 * $Id: AspectualViewGps.java,v 1.17 2007-10-06 11:27:51 rensink Exp $
 */

package groove.io;

import groove.control.ControlView;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
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
import java.util.Properties;

/**
 * A class that takes care of loading in a rule system consisting of a set of individual files
 * containing graph rules, from a given location | presumably the top level directory containing the
 * rule files.
 * @author Arend Rensink
 * @version $Revision: 1.17 $
 */
public class AspectualViewGps implements GrammarViewXml<DefaultGrammarView> {
    /**
     * Constructs an instance based on a given graph format reader, and a given production rule, to
     * be used as a rule factory. If no file with the default start graph name exists then the empty
     * graph is taken as a start graph.
     * @param graphFactory the graph format reader
     */
    private AspectualViewGps(GraphFactory graphFactory) {
        this.graphMarshaller = createGraphMarshaller(graphFactory);
    }

    /**
     * Constructs an instance based on the standard 
     * graph and rule factories.
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
    
    public DefaultGrammarView unmarshal(File location, String startGraphName) throws IOException {
        if (!location.exists()) {
            throw new FileNotFoundException(LOAD_ERROR + ": rule rystem location \"" + location
                    + "\" does not exist");
        }
        if (!location.isDirectory()) {
            throw new IOException(LOAD_ERROR + ": rule system location \"" + location
                    + "\" is not a directory");
        }

        String grammarName = getExtensionFilter().stripExtension(location.getName());
        DefaultGrammarView result = createGrammar(grammarName);

        loadProperties(result, location);
        loadRules(result, location);
        loadStartGraph(result, startGraphName, location);
        loadControl(result, location);
        return result;
    }

	/**
	 * Loads the properties file for a given graph grammar
	 * from a given location.
	 */
	private void loadProperties(DefaultGrammarView result, File location) throws IOException, FileNotFoundException {
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
	 * Loads the control program from the location set in the properties of the grammar.
	 * Can only be done after rules and properties are loaded.
	 * TODO: put this somewhere else?
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadControl(DefaultGrammarView result, File location) throws IOException, FileNotFoundException {
		String controlFileName = result.getProperties().getProperty(SystemProperties.CONTROL_PROGRAM_KEY);
		if( controlFileName == null)
			return;
		File controlProgramFile = new File(location, result.getProperties().getProperty(SystemProperties.CONTROL_PROGRAM_KEY));
		//System.out.println(controlProgram.getAbsolutePath());
		if( controlProgramFile.exists() ) {
			try
			{

				ControlView cv = new ControlView();
				
				cv.initScope(result);
				cv.loadFile(controlProgramFile);
				cv.loadProgram();

				result.setControl(cv);
				
			}
			catch(IOException e) {
				System.err.println("Error: unable to open control program " + controlProgramFile.getName());
			}
		}
	}
	
	/**
	 * Loads the rules for a given graph grammar from a given location.
	 */
	private void loadRules(DefaultGrammarView result, File location) throws IOException {
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
	private void loadRules(DefaultGrammarView result, File directory,
	        RuleNameLabel rulePath) throws IOException {
	    File[] files = directory.listFiles(RULE_FILTER);
	    if (files == null) {
	        throw new IOException(LOAD_ERROR+": no files found at "+directory);
	    } else {
	        // read in production rules
	        for (File file: files) {
	        	String fileName = RULE_FILTER.stripExtension(file.getName());
	            PriorityFileName priorityFileName = new PriorityFileName(fileName);
	            RuleNameLabel ruleName = new RuleNameLabel(rulePath,
	                    priorityFileName.getActualName());
	            // check for overlapping rule and directory names
	            if (result.getRule(ruleName) != null) {
	    	        throw new IOException(LOAD_ERROR + ": duplicate rule name \"" + ruleName+"\"");
	            }
	            if (file.isDirectory()) {
	                loadRules(result, file, ruleName);
	            } else {
					try {
						result.addRule(loadRule(file, ruleName, result.getProperties()));
					} catch (IOException exc) {
						throw new IOException(exc.getMessage());
					}
				}
	        }
	    }
	}

	/**
	 * Loads in and returns a single rule from a given location, 
	 * giving it a given name and priority.
	 */
	private AspectualRuleView loadRule(File location, RuleNameLabel ruleName,
			SystemProperties properties) throws IOException {
		AspectGraph unmarshalledRule = getGraphMarshaller().unmarshalGraph(location);
        GraphInfo.setRole(unmarshalledRule, Groove.RULE_ROLE);
		return createRuleView(unmarshalledRule, ruleName, properties);
	}

	/**
	 * @param result
	 * @param startGraphName
	 * @param location
	 * @throws IOException
	 */
	private void loadStartGraph(DefaultGrammarView result, String startGraphName, File location) throws IOException {
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
	    if (startGraphFile != null) {
	        try {
	            AspectGraph unmarshalledStartGraph = getGraphMarshaller().unmarshalGraph(startGraphFile);
                GraphInfo.setRole(unmarshalledStartGraph, Groove.GRAPH_ROLE);
	            AspectualGraphView startGraph = new AspectualGraphView(unmarshalledStartGraph);
	            result.setStartGraph(startGraph);
	        } catch (FileNotFoundException exc) {
	            throw new IOException(LOAD_ERROR + ": start graph " + startGraphFile + " not found");
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
    	String filename = Groove.createRuleFilter().stripExtension(location.getName());
    	PriorityFileName priorityFileName = new PriorityFileName(filename);
    	RuleNameLabel ruleName = new RuleNameLabel(priorityFileName.getActualName());
    	return createRuleView(getGraphMarshaller().unmarshalGraph(location), ruleName, properties);
    }

    public void marshal(DefaultGrammarView gg, File location) throws IOException {
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
	            throw new IOException("Existing location " + location + " cannot be deleted");
	        }
	    }
	    // create location as directory
	    location.mkdirs();
	}
	
	/** Recursively traverses all subdirectories and deletes all files and directories. */
	private boolean deleteRecursive(File location) {
		if (location.isDirectory()) {
			for (File file: location.listFiles()) {
				if (!deleteRecursive(file)) {
					return false;
				}
			}
		}
		return location.delete();
	}
//
//	/**
//	 * Saves a rule in a given location.
//	 * The rule is given as a rule view; if it is an {@link AspectualRuleView}
//	 * then use it directly, otherwise construct an {@link AspectualRuleView}
//	 * from the underlying rule.
//	 */
//	private void saveRule(AspectualRuleView ruleView, File location) throws IOException {
//		graphMarshaller.marshalGraph(ruleView.getAspectGraph(), getFile(location, ruleView.getNameLabel()));
//	}

	/**
	 * Saves a graph as start graph (using the default name) in
	 * a given location, assumed to be a directory.
	 * @see #DEFAULT_START_GRAPH_NAME
	 */
	private void saveStartGraph(AspectualGraphView startGraph, File location) throws IOException {
		if (startGraph != null) {
			// save start graph
			File startGraphLocation = new File(location, STATE_FILTER.addExtension(startGraph.getName()));
			getGraphMarshaller().marshalGraph(startGraph.getAspectGraph(), startGraphLocation);
		}
	}

	/**
	 * Saves the properties of a graph grammar in a given location, 
	 * assumed to be a directory.
	 * 
	 */
	private void saveProperties(DefaultGrammarView gg, File location) throws IOException, FileNotFoundException {
		// save properties
		String grammarName = gg.getName();
	    File propertiesFile = new File(location, PROPERTIES_FILTER.addExtension(grammarName));
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
    public void marshalRule(AspectualRuleView ruleGraph, File location) throws IOException {
		graphMarshaller.marshalGraph(ruleGraph.getAspectGraph(), getFile(location, ruleGraph.getNameLabel()));
    }

	/**
     * Removes a rule file from the file system
     * @param ruleName the name of the rule to be removed
     * @param location the basis location of the GPS 
     */
    public void deleteRule(RuleNameLabel ruleName, File location) {
        getGraphMarshaller().deleteGraph(getFile(location, ruleName));
    }

    /** 
     * Constructs a rule file from the combination of a basis location (the
     * directory where the GPS is stored), a rule name, and the {@link #RULE_FILTER} extension.
     */
    public File getFile(File location, RuleNameLabel name) {
        File ruleLocation = location;
        // if the rule name is structured, go to the relevant subdirectory
		String[] tokens = name.tokens();
		for (int i = 0; i < tokens.length - 1; i++) {
			ruleLocation.mkdir();
			ruleLocation = new File(ruleLocation, tokens[i]);
		}
		String coreRuleName = tokens[tokens.length - 1];
        return new File(ruleLocation, RULE_FILTER.addExtension(coreRuleName));
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
    	return new AspectGxl(new DefaultGxl(graphFactory));
    }
    
    /**
     * Creates a {@link groove.view.DefaultGrammarView} with the given name.
     * @param name the name of the {@link groove.view.DefaultGrammarView} to be created
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
    protected AspectualRuleView createRuleView(AspectGraph graph, RuleNameLabel ruleName, SystemProperties properties) {
    	return new AspectualRuleView(graph, ruleName, properties);
    }
	/**
     * The xml reader used to unmarshal graphs.
     */
    private final Xml<AspectGraph> graphMarshaller;
    
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
}