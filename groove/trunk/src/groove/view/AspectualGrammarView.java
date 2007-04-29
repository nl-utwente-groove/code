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
 * $Id: AspectualGrammarView.java,v 1.1 2007-04-29 09:22:35 rensink Exp $
 */
package groove.view;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.SystemProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class AspectualGrammarView implements GrammarView<AspectualRuleView> {
    /**
     * Constructs a (non-fixed) copy of an existing rule view grammar.
     */
    public AspectualGrammarView(AspectualGrammarView oldGrammar) {
        this(oldGrammar.getName());
        getProperties().putAll(oldGrammar.getProperties());
        for (AspectualRuleView ruleView: oldGrammar.ruleViewMap.values()) {
        	add(ruleView);
        }
    }

    /**
     * Constructs a named, empty grammar based on a given rule factory.
     */
    public AspectualGrammarView(String name) {
        this.name = name;
    }
    
    /** Returns the name of this grammar view. */
    public String getName() {
    	return name;
    }
    
    /** Returns the system properties of this grammar view. */
    public SystemProperties getProperties() {
    	if (properties == null) {
    		properties = new SystemProperties();
    	}
    	return properties;
    }
    
    public Map<NameLabel, AspectualRuleView> getRuleViewMap() {
		return ruleViewMap;
	}

	/**
     * Adds a rule based on a given rule view.
     * @see #getRuleView(NameLabel)
     */
    public AspectualRuleView add(AspectualRuleView ruleView) throws IllegalStateException {
        return ruleViewMap.put(ruleView.getName(), ruleView);
    }
    
    /**
     * Returns the rule view stored for a given rule name.
     * May be <code>null</code> if the rule cannot be viewed in the available
     * {@link RuleView} format.
     */
    public AspectualRuleView getRuleView(NameLabel name) {
        return ruleViewMap.get(name);
    }

    public Graph getStartGraph() {
		return startGraph;
	}

    /** Sets the start graph to a given graph. */
    public void setStartGraph(Graph startGraph) {
		this.startGraph = startGraph;
	}

	/** Converts the grammar view to a real grammar. */
    public GraphGrammar toGrammar() throws FormatException {
    	if (grammar == null) {
    		grammar = computeGrammar();
    	}
    	return grammar;
    }
    
    /** 
     * Computes a graph grammar from this view. 
     * @throws FormatException if there are syntax errors in the view
     */
    private GraphGrammar computeGrammar() throws FormatException {
    	GraphGrammar result = new GraphGrammar(getName());
    	for (RuleView ruleView: getRuleViewMap().values()) {
    		result.add(ruleView.toRule());
    	}
    	result.setProperties(getProperties());
    	result.setStartGraph(getStartGraph());
    	return result;
    }
    
    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<NameLabel,AspectualRuleView> ruleViewMap = new HashMap<NameLabel,AspectualRuleView>();
    /** The name of this grammar view. */
    private final String name;
    private Graph startGraph;
    /** The rule system properties of this grammar view. */
    private SystemProperties properties;
	/** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;
}