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
 * $Id: AttributedSPORuleFactory.java,v 1.3 2007-04-01 12:49:54 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;

/**
 * Factory class for attributed SPO rules.
 * It yields the following types:
 * <ul>
 * <li> {@link groove.trans.AttributedSPORule} for the rule type
 * <li> {@link groove.trans.DefaultMatching} for the matching type
 * <li> {@link groove.trans.SPOApplication} for the applier type
 * </ul>
 * This is a singleton class; use {@link #getInstance()} to retrieve its only instance.
 * @author Harmen Kastenberg
 * @version $Revision: 1.3 $ $Date: 2007-04-01 12:49:54 $
 * @deprecated all functionality now in the {@link DefaultRuleFactory} classes
 */
@Deprecated
public class AttributedSPORuleFactory extends DefaultRuleFactory {
	/** The singleton instance of {@link AttributedSPORuleFactory}. */
	private static final AttributedSPORuleFactory singleton = new AttributedSPORuleFactory();

	/**
	 * Returns the singleton instance of {@link AttributedSPORuleFactory}.
	 */
	static public AttributedSPORuleFactory getInstance() {
		return singleton;
	}

	/**
	 * Constructor used to create the only (singleton) instance of this class.
	 * Its visibility is chosen so as to allow subclassing within the package. 
	 */
	protected AttributedSPORuleFactory() {
		// empty constructor
	}
//
//	/**
//	 * This implementation returns an {@link AttributedSPORule}.
//	 */
//	@Override
//	public Rule createRule(Morphism morphism, NameLabel name, int priority) {
//		return new AttributedSPORule(morphism, name, priority, this);
//	}

	/**
	 * This implementation returns an {@link AttributedSPOApplication}.
	 */
	@Override
	public RuleApplication createRuleApplication(RuleEvent event, Graph source) {
        return new AttributedSPOApplication((SPOEvent) event, source, this);
	}
//
//	/**
//	 * This implementation returns an {@link AttributedRuleGraph}.
//	 */
//	@Override
//	public RuleGraph createRuleView(Graph graph, NameLabel name, int priority) throws GraphFormatException {
//		return new AttributedRuleGraph(graph, name, priority, this);
//	}
//
//	/**
//	 * This implementation returns an {@link AttributeSimulation}.
//	 */
//	@Override
//	public MatchingSimulation createSimulation(Matching morphism) {
//		return new AttributeSimulation(morphism, this);
//	}
}