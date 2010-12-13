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
package groove.calc;

import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.Strategy;
import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Condition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.trans.RuleSystem;
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
    public DefaultGraphCalculator(RuleSystem rules, HostGraph start) {
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

    private Scenario createScenario(Strategy strategy, Acceptor acceptor) {
        DefaultScenario scenario = new DefaultScenario(strategy, acceptor);
        return scenario;
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
            Scenario sc =
                createScenario(new LinearStrategy(), new FinalStateAcceptor(
                    new Result(1)));
            sc.prepare(getGTS());
            Result scenarioResult = sc.play();
            if (scenarioResult.done()) {
                result = scenarioResult.getValue().iterator().next();
            } else {
                // try depth first
                sc =
                    createScenario(new DFSStrategy(), new FinalStateAcceptor(
                        new Result(1)));
                sc.prepare(getGTS());
                scenarioResult = sc.play();
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
            Scenario scenatioResult =
                createScenario(strategy, new FinalStateAcceptor(new Result(1)));
            scenatioResult.prepare(getGTS());
            Result results = scenatioResult.play();
            if (results.done()) {
                result = results.getValue().iterator().next();
            }
        }
        return result;
    }

    public Collection<GraphState> getAllFinal() {
        testPrototype();
        Scenario scenario =
            createScenario(new BFSStrategy(), new FinalStateAcceptor());
        scenario.prepare(getGTS());
        return scenario.play().getValue();

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
    protected Rule getRule(String name) {
        Rule result = getGrammar().getRule(name);
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

    public Graph getBasis() {
        if (isPrototype()) {
            return null;
        } else {
            return this.gts.startState().getGraph();
        }
    }

    /**
     * Returns the result from running the passed scenario.
     */
    public Result getResult(Scenario scenario) {
        testPrototype();
        scenario.prepare(getGTS());
        return scenario.play();
    }

    public GraphCalculator newInstance(HostGraph start)
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
