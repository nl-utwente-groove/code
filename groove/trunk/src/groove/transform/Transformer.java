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
import groove.explore.encode.Serialized;
import groove.grammar.Grammar;
import groove.grammar.host.HostGraph;
import groove.grammar.model.GrammarModel;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
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
     * Returns the (first) result of transforming the 
     * grammar's default start graph, or {@code null} if
     * there is no result.
     */
    public Model getResult() throws Exception {
        Model result = null;
        Grammar grammar = getGrammarModel().toGrammar();
        GTS gts = createGTS(grammar);
        int oldResultCount = getResultCount();
        setResultCount(1);
        getExploration().play(gts, null);
        Collection<GraphState> exploreResult =
            getExploration().getResult().getValue();
        if (!exploreResult.isEmpty()) {
            result = createModel(exploreResult.iterator().next().getGraph());
        }
        setResultCount(oldResultCount);
        return result;
    }

    /**
     * Returns the list of results of transforming the 
     * grammar's default start graph.
     */
    public Collection<Model> getResults() throws Exception {
        Grammar grammar = getGrammarModel().toGrammar();
        GTS gts = createGTS(grammar);
        for (GTSListener listener : getGTSListeners()) {
            gts.addLTSListener(listener);
        }
        getGTSListeners().clear();
        getExploration().play(gts, null);
        Collection<GraphState> exploreResult =
            getExploration().getResult().getValue();
        return new TransformCollection<GraphState,Model>(exploreResult) {
            @Override
            protected Model toOuter(GraphState key) {
                return createModel(key.getGraph());
            }
        };
    }

    /**
     * Returns the (first) result of transforming the 
     * grammar's default start graph, or {@code null} if
     * there is no result.
     */
    public Model getResult(Model start) throws Exception {
        getGrammarModel().setStartGraph(start.toAspectGraph());
        return getResult();
    }

    /**
     * Returns the list of results of transforming the 
     * grammar's default start graph.
     */
    public Collection<Model> getResults(Model start) throws Exception {
        getGrammarModel().setStartGraph(start.toAspectGraph());
        return getResults();
    }

    /** Callback factory method for a GTS.
     * The GTS gets all listeners set prior to a transformation.
     */
    private GTS createGTS(Grammar grammar) {
        GTS result = new GTS(grammar);
        for (GTSListener listener : getGTSListeners()) {
            result.addLTSListener(listener);
        }
        getGTSListeners().clear();
        return result;
    }

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

    /** Adds a listener for the next transformation. */
    public void addGTSListener(GTSListener listener) {
        this.gtsListeners.add(listener);
    }

    /** Returns the set of GTS listeners. */
    private List<GTSListener> getGTSListeners() {
        return this.gtsListeners;
    }

    private final List<GTSListener> gtsListeners = new ArrayList<GTSListener>();
    private Serialized strategy;
    private Serialized acceptor;
    private int resultCount;
}
