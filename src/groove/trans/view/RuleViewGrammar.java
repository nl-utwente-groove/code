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
 * $Id: RuleViewGrammar.java,v 1.1.1.1 2007-03-20 10:05:21 kastenberg Exp $
 */
package groove.trans.view;

import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class RuleViewGrammar extends GraphGrammar {
    /**
     * Constructs a named, empty grammar.
     */
    public RuleViewGrammar(String name) {
        super(name);
    }
//    
//    /**
//     * In addition to delegating to <code>super</code>, creates and adds a view
//     * for the rule.
//     * @see #createRuleView(Rule)
//     */
//    public Rule add(Rule rule) {
//        Rule result = super.add(rule);
//        ruleViewMap.put(rule.getName(), createRuleView(rule));
//        return result;
//    }
//    
//    /**
//     * Adds a rule based on a given rule view.
//     * The priority is constructed as in {@link #add(Rule)}.
//     * Convenience method for <code>add(ruleView, null)</code>.
//     */
//    public Rule add(RuleView ruleView) {
//        return add(ruleView, null);
//    }

    /**
     * Adds a rule based on a given rule view and priority.
     * If the priority is <code>null</code>, it is constructed as in 
     * {@link #add(Rule)}.
     * Calls {@link #add(Rule)} on <code>super</code>,
     * and adds the <code>ruleGraph</code> to the map.
     * @return the added rule, obtained from <code>ruleGraph.toRule()</code>
     */
    public Rule add(RuleView ruleView) {
        Rule result = super.add(ruleView.toRule());
        ruleViewMap.put(ruleView.getName(), ruleView);
        return result;
    }
    
    /**
     * Returns the rule view stored for a given rule name.
     * May be <code>null</code> if the rule cannot be viewed in the available
     * {@link RuleView} format.
     * @see #add(Rule)
     */
    public RuleView getRuleView(NameLabel name) {
        return ruleViewMap.get(name);
    }
//    
//    /**
//     * Creates a fresh rule view for a givn rule, using {@link #getRuleViewFactory()}.
//     * Callback method from {@link #add(Rule)}. 
//     * May return <code>null</code> if <code>rule</code> cannot be viewed in the
//     * chosen {@link RuleView} format (see {@link #getRuleViewFactory()}).
//     */
//    protected RuleView createRuleView(Rule rule) {
//    	return null;
////        try {
////            return getRuleFactory().createRuleView(rule.graph, name, priority);
////        } catch (ViewFormatException exc) {
////            return null;
////        }
//    }
    
    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<NameLabel,RuleView> ruleViewMap = new HashMap<NameLabel,RuleView>();
}