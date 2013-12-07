/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.transform;

import groove.explore.Exploration;
import groove.explore.ExplorationListener;
import groove.explore.encode.Serialized;
import groove.explore.result.Result;
import groove.grammar.Grammar;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.host.HostGraph;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.ResourceKind;
import groove.io.FileType;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Groove;
import groove.util.collect.TransformCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Encapsulates a grammar and offers functionality to transform 
 * arbitrary graphs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Transformer {
    /**
     * Constructs a transformer based on the grammar found at a given location.
     * @throws IOException if the grammar cannot be loaded from the given location
     */
    public Transformer(String grammarFileName) throws IOException {
        this(GrammarModel.newInstance(grammarFileName));
    }

    /**
     * Constructs a transformer based on the grammar found at a given location.
     * @throws IOException if the grammar cannot be loaded from the given location
     */
    public Transformer(File grammarLocation) throws IOException {
        this(GrammarModel.newInstance(grammarLocation));
    }

    /**
     * Constructs a transformer based on a given grammar model.
     */
    public Transformer(GrammarModel grammarModel) {
        this.grammarModel = grammarModel;
    }

    /** Returns the grammar model wrapped in this transformer. */
    public GrammarModel getGrammarModel() {
        return this.grammarModel;
    }

    private final GrammarModel grammarModel;

    /**
     * Runs the exploration, and returns the exploration result.
     * @return The set of graph states comprising the exploration result
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Collection<GraphState> explore() throws FormatException {
        Grammar grammar = getGrammarModel().toGrammar();
        GTS gts = getFreshGTS(grammar);
        getExploration().play(gts, null);
        Result exploreResult = getExploration().getResult();
        gts.setResult(exploreResult);
        return getExploration().getResult().getValue();
    }

    /**
     * Runs the exploration on a given start graph, and returns the exploration result.
     * @param start the start graph for the transformation; if {@code null},
     * the default start graph will be used.
     * @return The set of graph states comprising the exploration result
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Collection<GraphState> explore(AspectGraph start)
        throws FormatException {
        if (start != null) {
            getGrammarModel().setStartGraph(start);
        }
        return explore();
    }

    /**
     * Runs the exploration on a given start model, and returns the exploration result.
     * @param start the start model for the transformation; if {@code null},
     * the default start graph will be used.
     * @return The set of graph states comprising the exploration result
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Collection<GraphState> explore(Model start) throws FormatException {
        return explore(start == null ? null : start.toAspectGraph());
    }

    /**
     * Runs the exploration on a given named start graph, and returns the exploration result.
     * @param startGraphName name of the start graph to be loaded; either the name of a graph
     * in the grammar, or a filename within the grammar, or a standalone file
     * @return The set of graph states comprising the exploration result.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     * @throws IOException if the named start graph cannot be loaded
     */
    public Collection<GraphState> explore(String startGraphName)
        throws FormatException, IOException {
        return explore(computeStartGraph(startGraphName));
    }

    /**
     * Runs the exploration on a start model consisting of a union of
     * named start graphs, and returns the exploration result.
     * @param startGraphNames list of start graph names, each of which is
     * interpreted as for {@link #explore(String)}
     * @return The set of named graph states comprising the exploration result.
     * The names are either local to the grammar directory, or file names.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     * @throws IOException if any of the named start graphs cannot be loaded
     */
    public Collection<GraphState> explore(List<String> startGraphNames)
        throws IOException, FormatException {
        return explore(computeStartGraph(startGraphNames));
    }

    /** Loads a named start graph. */
    private AspectGraph computeStartGraph(String startGraphName)
        throws IOException {
        AspectGraph result = null;
        if (startGraphName != null) {
            // first see if the name refers to a local host graph
            GraphBasedModel<?> hostModel =
                getGrammarModel().getGraphResource(ResourceKind.HOST,
                    startGraphName);
            if (hostModel == null) {
                // try to load the graph as a standalone file
                startGraphName =
                    FileType.STATE_FILTER.addExtension(startGraphName);
                File startGraphFile = new File(startGraphName);
                if (!startGraphFile.exists()) {
                    // look for the name within the grammar location
                    Object grammarLocation =
                        getGrammarModel().getStore().getLocation();
                    if (grammarLocation instanceof File) {
                        startGraphFile =
                            new File((File) grammarLocation, startGraphName);
                    }
                }
                if (!startGraphFile.exists()) {
                    throw new IOException("Can't find start graph "
                        + startGraphName);
                }
                result =
                    GraphConverter.toAspect(Groove.loadGraph(startGraphFile));
            } else {
                result = hostModel.getSource();
            }
        }
        return result;
    }

    private AspectGraph computeStartGraph(List<String> startGraphNames)
        throws IOException {
        AspectGraph result = null;
        if (startGraphNames != null && !startGraphNames.isEmpty()) {
            List<AspectGraph> graphs = new ArrayList<AspectGraph>();
            for (String startGraphName : startGraphNames) {
                graphs.add(computeStartGraph(startGraphName));
            }
            result = AspectGraph.mergeGraphs(graphs);
        }
        return result;
    }

    /**
     * Returns the (first) result of transforming the 
     * grammar's default start graph, or {@code null} if
     * there is no result.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Model getResult() throws FormatException {
        Model result = null;
        int oldResultCount = getResultCount();
        setResultCount(1);
        Collection<GraphState> exploreResult = explore();
        if (!exploreResult.isEmpty()) {
            result = createModel(exploreResult.iterator().next().getGraph());
        }
        setResultCount(oldResultCount);
        return result;
    }

    /**
     * Returns the list of results of transforming the 
     * grammar's default start graph.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Collection<Model> getResults() throws FormatException {
        Collection<GraphState> exploreResult = explore();
        return new TransformCollection<GraphState,Model>(exploreResult) {
            @Override
            protected Model toOuter(GraphState key) {
                return createModel(key.getGraph());
            }
        };
    }

    /**
     * Returns the (first) result of transforming a given model, or {@code null} if
     * there is no result.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Model getResult(Model start) throws FormatException {
        getGrammarModel().setStartGraph(start.toAspectGraph());
        return getResult();
    }

    /**
     * Returns the models resulting from transforming a given model.
     * @throws FormatException if either the grammar could not be built
     * or the exploration is not compatible with the grammar
     */
    public Collection<Model> getResults(Model start) throws FormatException {
        getGrammarModel().setStartGraph(start.toAspectGraph());
        return getResults();
    }

    /** Returns the GTS of the most recent exploration. */
    public GTS getGTS() {
        return this.gts;
    }

    /** 
     * Creates and returns a fresh GTS.
     */
    private GTS getFreshGTS(Grammar grammar) throws FormatException {
        GTS result = createGTS(grammar);
        this.gts = result;
        return result;
    }

    /**
     * Callback factory method for a GTS.
     * The GTS gets all listeners set prior to a transformation.
     * @throws FormatException if the grammar cannot be transformed. This
     * is used in subclasses to check that transformation is only invoked
     * for appropriate grammars.
     */
    protected GTS createGTS(Grammar grammar) throws FormatException {
        return new GTS(grammar);
    }

    private GTS gts;

    /** Callback factory method for models. */
    private Model createModel(HostGraph host) {
        return new Model(getGrammarModel(), host);
    }

    /** Returns the exploration currently set for this transformer. */
    public Exploration getExploration() {
        if (this.exploration == null) {
            this.exploration = computeExploration();
        }
        return this.exploration;
    }

    private Exploration computeExploration() {
        Exploration result = getGrammarModel().getDefaultExploration();
        if (result == null) {
            result = new Exploration();
        }
        boolean rebuild = hasStrategy() || hasAcceptor() || hasResultCount();
        if (rebuild) {
            Serialized strategy =
                hasStrategy() ? getStrategy() : result.getStrategy();
            Serialized acceptor =
                hasAcceptor() ? getAcceptor() : result.getAcceptor();
            int resultCount =
                hasResultCount() ? getResultCount() : result.getNrResults();
            result = new Exploration(strategy, acceptor, resultCount);
        }
        for (ExplorationListener listener : getListeners()) {
            result.addListener(listener);
        }
        return result;
    }

    private Exploration exploration;

    /** 
     * Sets the strategy to be used in the next exploration.
     * @param strategy the strategy to be used; if {@code null}, the
     * default strategy of the grammar will be used 
     */
    public void setStrategy(Serialized strategy) {
        this.strategy = strategy;
        // reset the exploration, so that it will be regenerated
        this.exploration = null;
    }

    /** Returns the user-set strategy for the next exploration. */
    private Serialized getStrategy() {
        return this.strategy;
    }

    /** Indicates if there is a user-set strategy for the next exploration. */
    private boolean hasStrategy() {
        return getStrategy() != null;
    }

    /** 
     * Sets the acceptor to be used in the next exploration.
     * @param acceptor the acceptor to be used; if {@code null}, the
     * default acceptor of the grammar will be used 
     */
    public void setAcceptor(Serialized acceptor) {
        this.acceptor = acceptor;
        // reset the exploration, so that it will be regenerated
        this.exploration = null;
    }

    /** Returns the user-set acceptor for the next exploration. */
    private Serialized getAcceptor() {
        return this.acceptor;
    }

    /** Indicates if there is a user-set acceptor for the next exploration. */
    private boolean hasAcceptor() {
        return getAcceptor() != null;
    }

    /** 
     * Sets the result count to be used in the next exploration.
     * @param count the result count to be used; if {@code null}, the
     * default count of the grammar will be used 
     */
    public void setResultCount(int count) {
        this.resultCount = count;
        // reset the exploration, so that it will be regenerated
        this.exploration = null;
    }

    /** Returns the user-set result count for the next exploration. */
    private int getResultCount() {
        return this.resultCount;
    }

    /** Indicates if there is a user-set result count for the next exploration. */
    private boolean hasResultCount() {
        return getResultCount() != 0;
    }

    /** Adds a listener for the subsequent explorations. */
    public void addListener(ExplorationListener listener) {
        this.gtsListeners.add(listener);
        // do not use getExploration()
        if (this.exploration != null) {
            this.exploration.addListener(listener);
        }
    }

    /** Removes an exploration listener. */
    public void removeListener(ExplorationListener listener) {
        this.gtsListeners.remove(listener);
        // do not use getExploration()
        if (this.exploration != null) {
            this.exploration.removeListener(listener);
        }
    }

    /** Returns the set of GTS listeners. */
    private List<ExplorationListener> getListeners() {
        return this.gtsListeners;
    }

    private final List<ExplorationListener> gtsListeners =
        new ArrayList<ExplorationListener>();
    private Serialized strategy;
    private Serialized acceptor;
    private int resultCount;
}
