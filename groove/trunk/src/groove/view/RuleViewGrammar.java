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
 * $Id: RuleViewGrammar.java,v 1.1 2007-04-29 09:22:36 rensink Exp $
 */
package groove.view;

import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class RuleViewGrammar extends GraphGrammar implements GrammarView<AspectualRuleView> {
    /**
     * Constructs a (non-fixed) copy of an existing rule view grammar.
     */
    public RuleViewGrammar(RuleViewGrammar oldGrammar) {
        this(oldGrammar.getName());
        getProperties().putAll(oldGrammar.getProperties());
        try {
			for (AspectualRuleView ruleView: oldGrammar.ruleViewMap.values()) {
				add(ruleView);
			}
		} catch (FormatException exc) {
			throw new IllegalStateException("Exception in copying grammar", exc);
		}
        setStartGraph(oldGrammar.getStartGraph());
    }

    /**
     * Constructs a named, empty grammar based on a given rule factory.
     */
    public RuleViewGrammar(String name) {
        super(name);
    }
    
    
    public GraphGrammar toGrammar() throws FormatException {
		return this;
	}

	public Map<NameLabel, AspectualRuleView> getRuleViewMap() {
		return ruleViewMap;
	}

	/**
     * Adds a rule based on a given rule view.
     * Returns the rule with the same name previously stored, if any.
     * @throws FormatException if the rule view does not translate correctly to a rule
     * @see #getRuleView(NameLabel)
     */
    public AspectualRuleView add(AspectualRuleView ruleView) throws FormatException {
    	super.add(ruleView.toRule());
        return ruleViewMap.put(ruleView.getName(), ruleView);
    }
    
    /**
     * Returns the rule view stored for a given rule name.
     * May be <code>null</code> if the rule cannot be viewed in the available
     * {@link RuleView} format.
     * @see #add(Rule)
     */
    public AspectualRuleView getRuleView(NameLabel name) {
        return ruleViewMap.get(name);
    }
    
    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<NameLabel,AspectualRuleView> ruleViewMap = new HashMap<NameLabel,AspectualRuleView>();
}