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
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
public abstract class AspectualViewGps extends Observable implements
        GrammarViewXml<DefaultGrammarView> {
    /**
     * Constructs an instance based on a given graph format reader, and a given
     * production rule, to be used as a rule factory. If no file with the
     * default start graph name exists then the empty graph is taken as a start
     * graph.
     * @param graphFactory the graph format reader
     */
    private AspectualViewGps(GraphFactory graphFactory, boolean layouted) {
        this.gxlGraphMarshaller = createGraphMarshaller(graphFactory, layouted);
        this.autGraphMarshaller = new AspectGxl(new Aut());
    }

    /**
     * Constructs an instance based on the standard graph and rule factories.
     */
    protected AspectualViewGps(boolean layouted) {
        this(GraphFactory.getInstance(), layouted);
    }

    /**
     * Fixed handler method for unmarshalling using an url. If available, a
     * startgraph name and a controlname are extracted from the url. default
     * names are used otherwise. The method is then dispatched to
     * {@link #unmarshal(URL, String, String)}, which are implemented by the
     * subclasses.
     */
    public final DefaultGrammarView unmarshal(URL url) throws IOException {
        String startGraphName = url.getQuery();
        String controlName = url.getRef();
        URL newURL =
            new URL(url.getProtocol(), url.getHost(), url.getPort(),
                url.getPath());
        // extract the path of the grammar from the jar file

        return unmarshal(newURL, startGraphName, controlName);
    }

    /**
     * Loads the grammar from the given url with the given start graph name and
     * control name.
     * @param location the URL to load from (not <code>null</code>)
     * @param startGraphName the name of the start graph; if <code>null</code>, {@link #DEFAULT_START_GRAPH_NAME} is chosen
     * @param controlName the name of the control program; if <code>null</code>, {@link #DEFAULT_CONTROL_NAME} is chosen
     */
    protected abstract DefaultGrammarView unmarshal(URL location,
            String startGraphName, String controlName) throws IOException;

    /**
     * Loads the properties file for a given graph grammar from a given
     * location.
     */
    protected void loadProperties(DefaultGrammarView result, URL propertiesURL)
        throws IOException {
        // search for a properties file

        if (propertiesURL != null) {
            Properties grammarProperties = new Properties();
            InputStream s = propertiesURL.openStream();
            grammarProperties.load(s);
            s.close();
            result.setProperties(grammarProperties);
        }
    }

    /**
     * Loads the control program from the location set in the properties of the
     * grammar. Can only be done after rules and properties are loaded.
     */
    protected void loadControl(DefaultGrammarView result, URL controlURL,
            String controlName) throws IOException {
        
        if( result.getProperties().isUseControl() ) {
            if (controlURL != null) {
                ControlView cv = new ControlView(result, controlURL, controlName);
                result.setControl(cv);
            }
        }
    }

    /**
     * Loads the rules for a given graph grammar from a given location.
     */
    protected void loadRules(DefaultGrammarView result,
            Map<RuleNameLabel,URL> ruleMap) throws IOException {
        setChanged();
        notifyObservers(LOADING_RULES);
        setChanged();
        notifyObservers(ruleMap.size());
        for (Map.Entry<RuleNameLabel,URL> ruleEntry : ruleMap.entrySet()) {
            result.addRule(loadRule(ruleEntry.getValue(), ruleEntry.getKey(),
                result.getProperties()));
            setChanged();
            notifyObservers(result);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Loads in and returns a single rule from a given location, giving it a
     * given name and priority.
     */
    private AspectualRuleView loadRule(URL location, RuleNameLabel ruleName,
            SystemProperties properties) throws IOException {

        AspectGraph unmarshalledRule =
            getGxlGraphMarshaller().unmarshalGraph(location);

        GraphInfo.setRole(unmarshalledRule, Groove.RULE_ROLE);

        AspectualRuleView result =
            createRuleView(unmarshalledRule, ruleName, properties);

        return result;
    }

    /**
     * Loads in a graph and adds a view for that graph to a given grammar view.
     * It is assumed that the grammar properties are already loaded.
     */
    protected void loadStartGraph(DefaultGrammarView result,
            URL startGraphSource) throws IOException {
        // determine the start graph file

        // get the start graph
        if (startGraphSource != null) {
            setChanged();
            notifyObservers(LOADING_START_GRAPH);
            setChanged();
            notifyObservers(1);

            AspectGraph unmarshalledStartGraph =
                unmarshalGraph(startGraphSource);

            AspectualGraphView startGraph =
                new AspectualGraphView(unmarshalledStartGraph,
                    result.getProperties());

            startGraph.getName();

            setChanged();
            notifyObservers(startGraph);
            result.setStartGraph(startGraph);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Loads a graph from a certain url
     * 
     * @param graph
     * @throws IOException
     */
    public AspectGraph unmarshalGraph(URL graph) throws IOException {
        Xml<AspectGraph> marshaller;
        AspectGraph unmarshalledStartGraph;
        if (graph.getFile().endsWith(Groove.AUT_EXTENSION)) {
            marshaller = getAutGraphMarshaller();
        } else {
            marshaller = getGxlGraphMarshaller();
        }
        unmarshalledStartGraph = marshaller.unmarshalGraph(graph);
        GraphInfo.setRole(unmarshalledStartGraph, Groove.GRAPH_ROLE);
        return unmarshalledStartGraph;
    }

    /**
     * Unmarshals a rule from a file in <tt>.gpr</tt> format. The rule gets
     * the filename (without directory path) as name.
     * @param location the file to get the rule from
     * @param properties the properties for the rule to be unmarshalled
     * @return a rule view for the given rule.
     * @throws IOException
     */
    public AspectualRuleView unmarshalRule(URL location,
            SystemProperties properties) throws IOException {
        String filename =
            Groove.createRuleFilter().stripExtension(location.getFile());
        PriorityFileName priorityFileName = new PriorityFileName(filename);
        RuleNameLabel ruleName =
            new RuleNameLabel(priorityFileName.getActualName());

        return loadRule(location, ruleName, properties);
    }

    public void marshal(DefaultGrammarView gg, File target) throws IOException,
        UnsupportedOperationException {
        // not supported by default
    }

    /**
     * Stores a given rule in a given directory.
     * @param location
     */
    @SuppressWarnings("unused")
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
     * Save a rule in the dir argument that is the location of the grammar.
     * @param rule
     * @param dir
     * @throws IOException
     */
    public void marshalRule(AspectualRuleView rule, File dir)
        throws IOException {
        throw new UnsupportedOperationException(
            "Current grammar does not support saving.");
    }

    /**
     * Deletes a rule given a grammar directory
     */
    public void deleteRule(AspectualRuleView rule, File dir) {
        throw new UnsupportedOperationException(
            "Current grammar does not support deleting.");
    }

    /**
     * Retrieves the current graph loader. The graph loader is used to read in
     * all rule and state graphs. Set during construction.
     */
    protected Xml<AspectGraph> getGxlGraphMarshaller() {
        return this.gxlGraphMarshaller;
    }

    /**
     * Retrieves the current graph loader. The graph loader is used to read in
     * all rule and state graphs. Set during construction.
     */
    protected Xml<AspectGraph> getAutGraphMarshaller() {
        return this.autGraphMarshaller;
    }

    /** Callback factory method for creating a graph marshaller. */
    protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory,
            boolean layouted) {
        if (layouted) {
            return new AspectGxl(new LayedOutXml(graphFactory));
        } else {
            return new AspectGxl(new DefaultGxl(graphFactory));
        }
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
    private AspectualRuleView createRuleView(AspectGraph graph,
            RuleNameLabel ruleName, SystemProperties properties) {
        return new AspectualRuleView(graph, ruleName, properties);
    }

    /**
     * Returns whether this loader can also marshal. false by default.
     * @return if this loader is readOnly
     */
    public boolean canWrite() {
        return false;
    }

    /**
     * This method should be implemented when the unmarshaller has an
     * extensionfilter. In this case, a file can be selected that can be
     * unmarshalled. The unmarshaller should create a URL such that the
     * URLLoaderFactory can delegate the URL to this loader.
     * @param file
     * @return URL 
     */
    public abstract URL createURL(File file);

    /**
     * The xml reader used to unmarshal graphs in GXL format.
     */
    private final Xml<AspectGraph> gxlGraphMarshaller;
    /**
     * The reader used to unmarshal graphs in the CADP aut format.
     */
    private final Xml<AspectGraph> autGraphMarshaller;

    /** Notification text for the rule loading phase. */
    static public final String LOADING_RULES = "Loading rules";
    /** Notification text for the start graph loading phase. */
    static public final String LOADING_START_GRAPH = "Loading start graph";
    /** Error message if a grammar cannot be loaded. */
    static protected final String LOAD_ERROR = "Can't load graph grammar";
    
    /** Returns the name of the grammar found at the given URL */
    public abstract String grammarName(URL grammarURL);
    
    /** Returns a String representation of the location where the grammar is found */
    public abstract String grammarLocation(URL grammarURL);
}