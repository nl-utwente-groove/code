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
 * $Id: DefaultGraphResult.java,v 1.4 2007-04-19 06:39:12 rensink Exp $
 */
package groove.calc;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.lts.DerivedGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.DerivationData;
import groove.trans.GraphGrammar;
import groove.trans.GraphTest;
import groove.trans.Matching;
import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.util.FormatException;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of a {@link GraphResult}, using
 * the {@link DefaultGraphCalculator} for its calculations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultGraphResult implements GraphResult {
	/** 
	 * Constructs a result using a given calculator, and
	 * starting in a given state.
	 */
    public DefaultGraphResult(DefaultGraphCalculator calculator, GraphState state) {
        this.calculator = calculator;
        this.state = state;
    }

	/** 
	 * Constructs a result using a given calculator, and
	 * starting in the calculator's GTS start state.
	 */
    public DefaultGraphResult(DefaultGraphCalculator calculator) {
        this(calculator, calculator.getGTS().startState());
    }

    public Graph getGraph() {
        return state.getGraph();
    }

    public List<Graph> getTrace() {
        List<Graph> result = new LinkedList<Graph>();
        result.add(state.getGraph());
        GraphState intermediate = state;
        while (intermediate != calculator.getBasis()) {
            intermediate = ((DerivedGraphState) intermediate).source();
            result.add(0, intermediate.getGraph());
        }
        return result;
    }

    public Morphism getMorphism() {
        // we iterate over the steps in the trace
        Iterator<Graph> graphIter = getTrace().iterator();
        // first create an isomorphism from the basis to itself
        Graph first = graphIter.next();
        Morphism result = first.getIsomorphismTo(first);
        // now concatenate the morphisms underlying the next transitions
        while (graphIter.hasNext()) {
            GraphTransition next = (DerivedGraphState) graphIter.next();
            result = next.morphism().after(result);
        }
        return result;
    }

    public GraphCalculator getCalculator() {
        return calculator;
    }

	public GraphResult getFirstAfter(String ruleName) {
        Rule rule = calculator.getRule(ruleName);
        Matching match = rule.getMatching(state.getGraph());
        if (match == null) {
        	return null;
        } else {
			DerivationData data = calculator.getGTS().getDerivationData();
			GraphState nextState = (GraphState) data.getApplication(rule, match).getTarget();
			return calculator.createResult(nextState);
		}
    }

	public Collection<GraphResult> getAllAfter(String ruleName) {
		Collection<GraphResult> result = new ArrayList<GraphResult>();
        Rule rule = calculator.getRule(ruleName);
        Iterator<? extends Matching> matchIter = rule.getMatchingIter(state.getGraph());
        while (matchIter.hasNext()) {
			Matching match = matchIter.next();
			DerivationData data = calculator.getGTS().getDerivationData();
			GraphState nextState = (GraphState) data.getApplication(rule, match).getTarget();
			result.add(calculator.createResult(nextState));
		}
        return result;
    }

    public GraphResult getMax(RuleSystem rules) {
		return newCalculator(rules).getMax();
	}

	public Collection<GraphResult> getAllMax(RuleSystem rules) {
		return newCalculator(rules).getAllMax();
	}

	public GraphResult getFirst(RuleSystem rules, GraphTest condition) {
		return newCalculator(rules).getFirst(condition);
	}

	public Collection<GraphResult> getAll(RuleSystem rules, GraphTest condition) {
		return newCalculator(rules).getAll(condition);
	}

	public GraphCalculator newCalculator() {
        return getCalculator().newInstance(getGraph());
    }
    
    public GraphCalculator newCalculator(RuleSystem rules) {
        return createCalculator(rules, getGraph());
    }
    
    @Override
    public String toString() {
        return getTrace().toString();
    }

    /**
     * Factory method for a graph calculator.
     * @param rules the rule system for the calculator
     * @param start the start graph
     */
    protected GraphCalculator createCalculator(RuleSystem rules, Graph start) throws IllegalArgumentException {
    	try {
			GraphGrammar newGrammar = new GraphGrammar(rules, start);
			newGrammar.setFixed();
			return new DefaultGraphCalculator(newGrammar);
		} catch (FormatException exc) {
			throw new IllegalArgumentException(exc.getMessage(), exc);
		}
	}
    
    /**
     * The state underlying this result.
     */
    private final GraphState state;
    /**
     * The calculator that gave rise to this result.
     */
    private final DefaultGraphCalculator calculator;
}
