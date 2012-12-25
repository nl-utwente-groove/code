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
 * $Id: DefaultGraphCalculator.java,v 1.16 2008-02-12 15:29:54 fladder Exp $
 */
package groove.samples.calc;

import groove.explore.Exploration;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Action;
import groove.trans.Condition;
import groove.trans.DefaultHostGraph;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.view.FormatException;

import java.util.Collection;

/**
 * Default implementation of a graph calculator.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultGraphCalculator implements GraphCalculator {
    /**
     * Creates a graph calculator for a given rule set and start graph.
     * @param rules the rule system for the calculator
     * @param start the start graph for the calculator
     */
    public DefaultGraphCalculator(GraphGrammar rules, DefaultHostGraph start) {
        this(new GraphGrammar(rules, start), false);
    }

    /**
     * Creates a graph calculator for a given, fixed graph grammar.
     * @param grammar the graph grammar for the calculator
     */
    public DefaultGraphCalculator(GraphGrammar grammar) {
        this(grammar, false);
    }

    /**
     * Creates a (possibly prototype) calculator on the basis of a given, fixed
     * graph grammar.
     * @param grammar the pre-existing graph grammar
     * @param prototype flag to indicate whether the constructed calculator is
     *        to be used as a prototype
     */
    protected DefaultGraphCalculator(GraphGrammar grammar, boolean prototype) {
        grammar.testFixed(true);
        this.grammar = grammar;
        this.gts = new GTS(grammar);
        this.prototype = prototype;
    }

    /**
     * Final states are all states with only transitions of unmodifying rules.
     */
    public GraphState getFinal() {
        testPrototype();

        GraphState result = null;
        // any final state is maximal; try that first
        if (this.gts.getFinalStates().size() > 0) {
            result = this.gts.getFinalStates().iterator().next();
        } else {
            // try linear
            Result scenarioResult = play("linear", "final", 1);
            if (scenarioResult.done()) {
                result = scenarioResult.getValue().iterator().next();
            } else {
                // try depth first
                scenarioResult = play("dfs", "final", 1);
                if (scenarioResult.done()) {
                    result = scenarioResult.getValue().iterator().next();
                }
            }
        }
        return result;
    }

    /**
     * Getting a maximal state given the passed strategy. Beware, maximal !=
     * final, maximal can have self-transitions
     */
    public GraphState getMax(Strategy strategy) {
        testPrototype();
        GraphState result = null;
        // any final state is maximal; try that first
        if (this.gts.getFinalStates().size() > 0) {
            result = this.gts.getFinalStates().iterator().next();
        } else {
            // try linear
            Result results = play("bfs", "final", 1);
            if (results.done()) {
                result = results.getValue().iterator().next();
            }
        }
        return result;
    }

    public Collection<GraphState> getAllFinal() {
        testPrototype();
        return play("bfs", "final", 1).getValue();
    }

    /** Plays a given exploration on the GTS, and returns the result. */
    private Result play(String strategy, String acceptor, int count) {
        Exploration exploration = new Exploration(strategy, acceptor, count);
        try {
            exploration.play(getGTS(), getGTS().startState());
        } catch (FormatException e) {
            // there can be no incompatibility
            assert false;
        }
        return exploration.getLastResult();
    }

    public Collection<GraphState> getAll(String conditionName) {
        return null;
    }

    public GraphState getFirst(String conditionName) {
        return null;
    }

    /**
     * Returns the rule in the underlying grammar with a certain name.
     * @throws IllegalArgumentException if no such condition exists
     */
    protected Action getRule(String name) {
        Action result = getGrammar().getRule(name);
        if (result == null) {
            throw new IllegalArgumentException("No rule \"" + name
                + "\" in grammar");
        }
        return result;
    }

    public GraphState getFirst(Condition condition) {
        testPrototype();

        return null;
    }

    public Collection<GraphState> getAll(final Condition condition) {
        testPrototype();
        return null;
    }

    public HostGraph getBasis() {
        if (isPrototype()) {
            return null;
        } else {
            return this.gts.startState().getGraph();
        }
    }

    public GraphCalculator newInstance(DefaultHostGraph start)
        throws IllegalArgumentException {
        try {
            GraphGrammar newGrammar = new GraphGrammar(this.grammar, start);
            newGrammar.setFixed();
            return new DefaultGraphCalculator(newGrammar);
        } catch (FormatException exc) {
            throw new IllegalArgumentException(exc.getMessage(), exc);
        }
    }

    public GTS getGTS() {
        return this.gts;
    }

    public GraphGrammar getGrammar() {
        return this.grammar;
    }

    /**
     * Indicates if this calculator is a prototype. If <code>true</code>,
     * then it is only to be used to create new instances.
     */
    protected boolean isPrototype() {
        return this.prototype;
    }

    /**
     * Method to test if the calculator is a prototype.
     * 
     * @throws IllegalStateException if the calculator is a prototype
     */
    private void testPrototype() {
        if (isPrototype()) {
            throw new IllegalStateException();
        }
    }

    /**
     * The grammar underlying this calculator.
     */
    private final GraphGrammar grammar;
    /**
     * The transition system underlying this calculator.
     */
    final GTS gts;

    /**
     * Switch indicating if the calculator is a prototype only.
     */
    private final boolean prototype;
}
