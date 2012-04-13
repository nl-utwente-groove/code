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
 * $Id: DefaultGraphCalculator.java,v 1.16 2008-02-12 15:29:54 fladder Exp $
 */
package groove.calc;

import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.result.Acceptor;
import groove.explore.result.EmptyResult;
import groove.explore.result.PropertyAcceptor;
import groove.explore.result.Result;
import groove.explore.result.SizedResult;
import groove.explore.strategy.BreadthFirstStrategy;
import groove.explore.strategy.DepthFirstStrategy1;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.Strategy;
import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Condition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.util.Property;
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
    public DefaultGraphCalculator(RuleSystem rules, Graph start) {
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
     * Creates a (possibly prototype) calculator on the basis of a given,
     * fixed graph grammar.
     * @param grammar the pre-existing graph grammar
     * @param prototype flag to indicate whether the constructed calculator is to be used
     * as a prototype
     */
    protected DefaultGraphCalculator(GraphGrammar grammar, boolean prototype) {
    	grammar.testFixed(true);
        this.grammar = grammar;
        this.gts = new GTS(grammar);
        this.prototype = prototype;
    }

    private Scenario<GraphState> createScenario(Strategy strategy, Acceptor<GraphState> acceptor, Result<GraphState> result) {
    	DefaultScenario<GraphState> scenario = new DefaultScenario<GraphState>();
    	scenario.setResult(result);
    	scenario.setAcceptor(acceptor);
    	scenario.setStrategy(strategy);
    	acceptor.setResult(result);
    	scenario.setGTS(getGTS());
    	return scenario;
    }
    
    /**
     * Beware, maximal != final, maximal can have self-transitions
     */
    public GraphState getMax() {
        testPrototype();

        GraphState result = null;
        // any final state is maximal; try that first
        if ( gts.getFinalStates().size() > 0 ) {
            result = gts.getFinalStates().iterator().next();
        } else {
        	// try linear
        	Scenario<GraphState> sc = createScenario(new LinearStrategy(), new PropertyAcceptor(new MaximalStateProperty()), new SizedResult<GraphState>(1));
        	sc.setState(getGTS().startState());
        	Result<GraphState> results = null;
        	try {
        		results = sc.play();
        	} catch (InterruptedException e) {
        		results = sc.getComputedResult();
        	}
        	if( results.getResult().size() == 1 ) {
        		result = results.getResult().iterator().next();
        	} else {
        		// try depth first
        		sc = createScenario(new DepthFirstStrategy1(), new PropertyAcceptor(new MaximalStateProperty()), new SizedResult<GraphState>(1));
        		try {
        			results = sc.play();
        		} catch (InterruptedException e) {
        			results = sc.getComputedResult();
        		}
        		if( results.getResult().size() == 1 ) {
        			result = results.getResult().iterator().next();
        		}
        	}
        }
        return result;
    }
    
    /**
	 * Returns the first "maximal" graph, i.e., that cannot evolve further,
	 * where <i>first</i> means that it has the shortest path from the original
	 * graph (as given by {@link #getBasis()}). The method will fail to
	 * terminate if there is no graph meeting the requirements.
	 * 
	 * @return A result wrapping a state whose only outgoing transitions are to
	 *         itself, or <code>null</code> if there is no such state. The
	 *         resulting state is guaranteed to be the one closest to the
	 *         original graph.
	 * @see #getMax()
	 * @see #getAllMax()
	 * @throws IllegalStateException
	 *             if the basis has not been initialised
	 */
//	public GraphState getFirstMax() {
//        testPrototype();
//        return getGenerator().get(isMaximal, toResult);
//    }

    public Collection<GraphState> getAllMax() {
        testPrototype();
        
        Scenario<GraphState> sc = createScenario(new BreadthFirstStrategy(), new PropertyAcceptor(new MaximalStateProperty()), new EmptyResult<GraphState>());
        sc.setState(getGTS().startState());
        Result<GraphState> result = null;
        try {
        	result = sc.play();
        } catch (InterruptedException e) {
        	result = sc.getComputedResult();
        }
        return result.getResult();
    }

    public Collection<GraphState> getAll(String conditionName) {
        //return getAll(getRule(conditionName));
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
            throw new IllegalArgumentException("No rule \""+name+"\" in grammar");
        }
        return result;
    }

    public GraphState getFirst(Condition condition) {
        testPrototype();
        
        return null;
    }

    public Collection<GraphState> getAll(final Condition condition) {
        testPrototype();
        
        
//        return getGenerator().getAll(getMatcher(condition), toResult);
//        return new Result<GraphState>().getResult();
        return null;
    }

    public Graph getBasis() {
        if (isPrototype()) {
            return null;
        } else {
            return gts.startState().getGraph();
        }
    }

    public GraphCalculator newInstance(Graph start) throws IllegalArgumentException {
    	try {
			GraphGrammar newGrammar = new GraphGrammar(grammar, start);
			newGrammar.setFixed();
			return new DefaultGraphCalculator(newGrammar);
		} catch (FormatException exc) {
			throw new IllegalArgumentException(exc.getMessage(), exc);
		}
    }
    
    public GTS getGTS() {
        return gts;
    }
    
	public GraphGrammar getGrammar() {
        return grammar;
    }

    /**
     * Tests if a certain state is maximal in the LTS,
     * in the sense that there are no outgoing transitions to any other state except itself.
     */
    

    /**
     * Indicates if this calculator is a prototype.
     * If <code>true</code>, then it is only to be used to create new instances.
     */
    protected boolean isPrototype() {
        return prototype;
    }

    /** Returns a property that tests for the matching of a graph to a test. */
    private final Property<GraphState> getMatcher(final Condition test) {
    	
    	return new Property<GraphState>() {
        	@Override
			public boolean isSatisfied(GraphState state) {
				return test.hasMatch(state.getGraph());
			}
        };
    }
    
    /**
	 * Method to test if the calculator is a prototype.
	 * 
	 * @throws IllegalStateException
	 *             if the calculator is a prototype
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