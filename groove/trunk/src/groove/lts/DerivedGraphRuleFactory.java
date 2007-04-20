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
package groove.lts;

import groove.graph.Graph;
import groove.trans.DefaultRuleFactory;
import groove.trans.RuleApplication;

/**
 * Factory whose rule applications generate {@link DerivedGraphState}s.
 * @deprecated use DefaultRuleFactory instead
 */
@Deprecated
public class DerivedGraphRuleFactory extends DefaultRuleFactory {
	/** The singleton instance of {@link DefaultRuleFactory}. */
	static private final DerivedGraphRuleFactory singleton = new DerivedGraphRuleFactory();

	/**
	 * Returns the singleton instance of {@link DefaultRuleFactory}.
	 */
	public static DerivedGraphRuleFactory getInstance() {
		return singleton;
	}
	
	/** Empty constructor with restricted visibility, only for subclassing. */
	protected DerivedGraphRuleFactory() {
		// empty constructor
	}
//
//	/**
//	 * This implementation returns an {@link AliasSPOApplication}.
//	 */
//	@Override
//	public RuleApplication createRuleApplication(RuleEvent event, Graph host) {
//        return new AliasSPOApplication((SPOEvent) event, host);
//	}

	/**
	 * This implementation returns a {@link DerivedGraphState}.
	 */
	@Override
	@Deprecated
	public Graph createTarget(RuleApplication ruleApplication) {
        return new DerivedGraphState(ruleApplication);
	}
}