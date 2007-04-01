// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: DefaultRuleFactory.java,v 1.5 2007-04-01 12:49:55 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Simulation;
import groove.graph.aspect.AspectGraph;
import groove.rel.VarNodeEdgeMap;
import groove.trans.match.MatchingMatcher;
import groove.trans.view.AspectualRuleView;
//import groove.trans.view.RuleGraph;
import groove.util.FormatException;

/**
 * A rule factory for SPO rules.
 * It yields the following types:
 * <ul>
 * <li> {@link SPORule} for the rule type
 * <li> {@link groove.trans.DefaultMatching} for the matching type
 * <li> {@link SPOApplication} for the applier type
 * </ul>
 * This is a singleton class; use {@link #getInstance()} to retrieve its only instance.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class DefaultRuleFactory implements RuleFactory {
	/** The singleton instance of {@link DefaultRuleFactory}. */
	static private final DefaultRuleFactory singleton = new DefaultRuleFactory();

	/**
	 * Returns the singleton instance of {@link DefaultRuleFactory}.
	 */
	static public DefaultRuleFactory getInstance() {
		return singleton;
	}
	
	/** Empty constructor with restricted visibility, only for subclassing. */
	protected DefaultRuleFactory() {
		// empty constructor
	}

	/**
	 * This implementation returns a {@link DefaultMatching}.
	 */
	public Matching createMatching(GraphCondition rule, Graph graph) {
		return new DefaultMatching((DefaultGraphCondition) rule, graph, this);
	}

	/**
	 * This implementation returns a {@link DefaultMatching} with
	 * the matching's element map pointing to the given map.
	 */
	public Matching createMatching(GraphCondition rule, final VarNodeEdgeMap partialMap, Graph graph) {
		DefaultMatching result = new DefaultMatching((DefaultGraphCondition) rule, graph, this) {
			@Override
			protected VarNodeEdgeMap createElementMap() {
				return partialMap;
			}
		};
		result.setFixed();
		return result;
	}

	/**
	 * This implementation returns a {@link DefaultMatching}.
	 */
	public Matching createMatching(final Simulation sim) {
		return createMatching((SPORule) sim.dom(), (VarNodeEdgeMap) sim.getSingularMap(), sim.cod());
	}

	/**
	 * This implementation returns an {@link SPORule}.
	 */
	public Rule createRule(Morphism morphism, NameLabel name, int priority) {
		return new SPORule(morphism, name, priority, this);
	}

	/**
	 * This implementation returns an {@link SPOApplication}.
	 */
	public RuleApplication createRuleApplication(RuleEvent event, Graph source) {
        return new SPOApplication((SPOEvent) event, source, this);
	}

	/**
	 * This implementation returns an {@link SPOEvent}.
	 */
	public RuleEvent createRuleEvent(Rule rule, VarNodeEdgeMap anchorMap) {
		return new SPOEvent((SPORule) rule, anchorMap, this);
	}

	/**
	 * This implementation returns an {@link AspectualRuleView}.
	 */
	public AspectualRuleView createRuleView(Graph graph, NameLabel name, int priority) throws FormatException {
		return new AspectualRuleView(AspectGraph.getFactory().fromPlainGraph(graph), name, priority, this);
	}

	/**
	 * This implementation returns a {@link MatchingSimulation}.
	 */
	public Simulation createSimulation(Matching morphism) {
      return new MatchingMatcher(morphism);
	}

	/**
	 * This implementation throws an {@link UnsupportedOperationException}.
	 */
	public Graph createTarget(RuleApplication ruleApplication) {
		throw new UnsupportedOperationException();
	}
}