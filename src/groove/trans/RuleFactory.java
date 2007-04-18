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
 * $Id: RuleFactory.java,v 1.4 2007-04-18 08:36:10 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.match.Matcher;
import groove.rel.VarNodeEdgeMap;
import groove.trans.view.RuleView;
import groove.util.FormatException;

/**
 * Factory interface for creating rules and related classes.
 * @author Arend Rensink
 * @version $Revision: 1.4 $ $Date: 2007-04-18 08:36:10 $
 */
public interface RuleFactory {
	/**
	 * Creates a rule view of the correct kind for this rule factory.
	 * @param graph the graph encoding the rule
	 * @param properties TODO
	 * @return a rule view over the rule encoded in the given graph
	 */
	public RuleView createRuleView(Graph graph, NameLabel name, int priority, SystemProperties properties) throws FormatException;

	/**
	 * Creates a named rule from a morphism.
	 * The rule gets default rule properties and priority.
	 * @see #createRule(Morphism, NameLabel, int, SystemProperties)
	 * @see SystemProperties#DEFAULT_PROPERTIES
	 * @see Rule#DEFAULT_PRIORITY
	 * @throws FormatException if a rule cannot be created due to incompatibility
	 * of the morphism and the default rule properties
	 */
	public Rule createRule(Morphism morphism, NameLabel name) throws FormatException;

	/**
	 * Creates a named rule with a given priority and rule properties.
	 * @throws FormatException if a rule cannot be created due to incompatibility
	 * of the morphism and the declared rule properties
	 */
	public Rule createRule(Morphism morphism, NameLabel name, int priority, SystemProperties properties) throws FormatException;

    /**
     * Creates and returns a fresh rule application object, of the kind required by the
     * rules of this factory.
     * @param event the rule instance for which an applier is to be created; is required
     * to be of the type of this factory.
     * @param host the host graph to which the rule is to be applied
	 * @return a RuleApplication of the given rule if there is a matching
	 */
	public RuleApplication createRuleApplication(RuleEvent event, Graph host);

    /**
     * Creates and returns a fresh rule event.
     * @param rule the rule for which an event is to be created; is required
     * to be of the type of this factory.
     * @param anchorMap matching of the rule's left hand side in the host graph
     */
	public RuleEvent createRuleEvent(Rule rule, VarNodeEdgeMap anchorMap);

    /**
     * Creates and returns a fresh rule event.
     * @param rule the rule for which an event is to be created; is required
     * to be of the type of this factory.
     * @param anchorMap matching of the rule's left hand side in the host graph
     */
	public RuleEvent createRuleEvent(Rule rule, VarNodeEdgeMap anchorMap, DerivationData record);

	/**
	 * Creates a matcher for a given matching.
	 */
	public Matcher createMatcher(Matching match);

	/**
	 * Creates and returns a matching for a given rule to a given graph, 
	 * of the type required by the rules of this factory.
	 * @param rule the rule of which the LHS is to be matched; is required to be of the 
	 * type of this factory.
	 * @param graph the graph to which the rule's LHS is to be matched
	 */
	public Matching createMatching(GraphCondition rule, Graph graph);

	/**
	 * Creates and returns a matching for a given rule to a given graph, 
	 * on the basis of a given (partial) element map.
	 * @param rule the rule of which the LHS is to be matched; is required to be of the 
	 * type of this factory.
	 * @param graph the graph to which the rule's LHS is to be matched
	 */
	public Matching createMatching(GraphCondition rule, VarNodeEdgeMap partialMap, Graph graph);
//
//	/**
//	 * Creates and returns a matching based on a given simulation.
//	 * The domain and codomain, as well as the element map, are derived from 
//	 * the simulation.
//	 * @param sim the simulation on which the matching is based
//	 */
//	public Matching createMatching(Simulation sim);

	/**
	 * Creates a target graph of the kind corresponding to the specific
	 * implementation
	 * of this class. 
	 * @param ruleApplication the rule application providing the necessary
	 * information to create the specific target graph
	 * @return the target graph
	 */
	public Graph createTarget(RuleApplication ruleApplication);

	/**
	 * Creates and returns a new derivation rule, on the basis of a given 
	 * morphism between LHS and RHS, a given rule name (which may be <code>null</code>),
	 * and a given priority.
	 * @param morphism the underlying rule morphism
	 * @param name the name of the rule; may be <code>null</code> for an anonymous rule
	 * @param priority the priority of the new rule
	 * @return A new instance of a rule, according to this factory, based on the
	 * parameters.
	 */
//	public Rule newRule(Morphism morphism, NameLabel name, int priority);

	/**
	 * Creates and returns a new derivation rule, on the basis of a given 
	 * morphism between LHS and RHS, a given rule name (which may be <code>null</code>),
	 * and default priority.
	 * @param morphism the underlying rule morphism
	 * @param name the name of the rule; may be <code>null</code> for an anonymous rule
	 * @return A new instance of a rule, according to this factory, based on the
	 * parameters.
	 * @see #newRule(Morphism, NameLabel, int)
	 * @see Rule#DEFAULT_PRIORITY
	 */
//	public Rule newRule(Morphism morphism, NameLabel name);

	/**
     * Creates and returns a fresh rule application object, of the kind required by the
     * rules of this factory.
     * @param rule the rule for which an applier is to be created; is required
     * to be of the type of this factory.
     * @param anchorMap matching of the rule's left hand side in the host graph
     */
//    public RuleEvent newEvent(Rule rule, VarElementMap anchorMap);

    /**
     * Creates and returns a fresh rule application object, of the kind required by the
     * rules of this factory.
     * @param event the rule for which an applier is to be created; is required
     * to be of the type of this factory.
     * @param source the host graph to which the rule is to be applied
     */
//    public RuleApplication newApplication(RuleEvent event, Graph source);
	
	/**
	 * Creates and returns a matching for a given rule to a given graph, 
	 * of the type required by the rules of this factory.
	 * @param rule the rule of which the LHS is to be matched; is required to be of the 
	 * type of this factory.
	 * @param graph the graph to which the rule's LHS is to be matched
	 */
//	public Matching newMatching(Rule rule, Graph graph);

	/**
	 * Creates a factory for creating {@link groove.trans.view.RuleGraph}s of the right type.
	 * @return a factory for creating RuleGraphs of the right type
	 */
//	public RuleGraph getRuleGraphFactory();
}