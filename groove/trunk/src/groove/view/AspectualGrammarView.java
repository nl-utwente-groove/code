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
 * $Id: AspectualGrammarView.java,v 1.2 2007-04-30 19:53:31 rensink Exp $
 */
package groove.view;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class AspectualGrammarView implements GrammarView<AspectualRuleView>, View<GraphGrammar> {
    /**
     * Constructs a (non-fixed) copy of an existing rule view grammar.
     */
    public AspectualGrammarView(GrammarView<AspectualRuleView> oldGrammar) {
        this(oldGrammar.getName());
        getProperties().putAll(oldGrammar.getProperties());
        for (AspectualRuleView ruleView: oldGrammar.getRuleMap().values()) {
        	addRule(ruleView);
        }
        setStartGraph(oldGrammar.getStartGraph());
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
    		this.properties.setFixed();
    	}
    	return properties;
    }

	/**
	 * Sets and fixes the properties of this view, by copying a given 
	 * properties object.
	 */
	public final void setProperties(Properties properties) {
		this.properties = new SystemProperties();
		this.properties.putAll(properties);
		invalidateGrammar();
	}

    public Map<RuleNameLabel, AspectualRuleView> getRuleMap() {
		return Collections.unmodifiableMap(ruleMap);
	}

	/**
     * Adds a rule based on a given rule view.
     * @see #getRule(RuleNameLabel)
     */
    public AspectualRuleView addRule(AspectualRuleView ruleView) throws IllegalStateException {
		AspectualRuleView result = removeRule(ruleView.getName());
		ruleMap.put(ruleView.getName(), ruleView);
		int priority = ruleView.getPriority();
		Set<RuleView> priorityRules = priorityMap.get(priority);
		if (priorityRules == null) {
			priorityMap.put(priority, priorityRules = new HashSet<RuleView>());
		}
		priorityRules.add(ruleView);
		return result;
	}

	/**
	 * Removes a rule view with a given name.
	 * Also removes the rule from the graph grammar.
	 * @return the view previously stored with name <code>name</code>, or <code>null</code>
	 */
	public AspectualRuleView removeRule(NameLabel name) {
		AspectualRuleView result = ruleMap.remove(name);
		if (result != null) {
			int priority = result.getPriority();
			Set<RuleView> priorityRules = priorityMap.get(priority);
			priorityRules.remove(result);
			if (priorityRules.isEmpty()) {
				priorityMap.remove(priority);
			}
		}
		invalidateGrammar();
		return result;
	}
    
    /**
     * Returns the rule view stored for a given rule name.
     * May be <code>null</code> if the rule cannot be viewed in the available
     * {@link RuleView} format.
     */
    public AspectualRuleView getRule(RuleNameLabel name) {
        return ruleMap.get(name);
    }
    
    public Map<Integer, Set<RuleView>> getPriorityMap() {
		return Collections.unmodifiableMap(priorityMap);
	}

    public Graph getStartGraph() {
		return startGraph;
	}

    /** Sets the start graph to a given graph. */
    public void setStartGraph(Graph startGraph) {
		this.startGraph = startGraph;
		invalidateGrammar();
	}

    /** Collects and returns the permanent errors of the rule views. */
	public void addErrors(List<String> errors) {
		this.errors.addAll(errors);
	}

    /** Collects and returns the permanent errors of the rule views. */
	public List<String> getErrors() {
		List<String> result = new ArrayList<String>();
		for (RuleView rule: ruleMap.values()) {
			result.addAll(rule.getErrors());
		}
		return Collections.unmodifiableList(result);
	}

	/** Delegates to {@link #toGrammar()}. */
	public GraphGrammar toModel() throws FormatException {
		return toGrammar();
	}

	/** Converts the grammar view to a real grammar. */
    public GraphGrammar toGrammar() throws FormatException {
    	List<String> errors = getErrors();
    	if (! errors.isEmpty()) {
    		throw new FormatException("Format errors in graph grammar", errors);
    	} else {
    		return computeGrammar();
    	}
    }
    
    /** 
     * Computes a graph grammar from this view. 
     * @throws FormatException if there are syntax errors in the view
     */
    private GraphGrammar computeGrammar() throws FormatException {
    	GraphGrammar result = new GraphGrammar(getName());
    	List<String> errors = new ArrayList<String>();
    	for (RuleView ruleView: getRuleMap().values()) {
    		try {
    			result.add(ruleView.toRule());
    		} catch (FormatException exc) {
    			for (String error: exc.getErrors()) {
    				errors.add(String.format("Format error in %s: %s", ruleView.getName(), error));
    			}
    		}
    	}
    	SystemProperties properties = getProperties();
    	result.setProperties(properties);
    	Graph startGraph = getStartGraph();
    	result.setStartGraph(startGraph);
    	try {
			result.setFixed();
		} catch (FormatException exc) {
			for (String error: exc.getErrors()) {
				errors.add(String.format("Global format error: %s", error));
			}
    	}
		if (errors.isEmpty()) {
			return result;
		} else {
			throw new FormatException("Format errors in %s", getName(), errors);
		}
    }
    
    /** 
     * Resets the internally stored graph grammar to <code>null</code>, so
     * the next call to {@link #toGrammar()} will have to recompute it.
     * This is done in reaction to a change in the rules, start graph or properties. 
     */
    private void invalidateGrammar() {
    	// empty
    }
    
    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<RuleNameLabel,AspectualRuleView> ruleMap = new TreeMap<RuleNameLabel,AspectualRuleView>();
	/** Mapping from priorities to sets of rule names. */
    private final Map<Integer,Set<RuleView>> priorityMap = new HashMap<Integer,Set<RuleView>>();
    /** The name of this grammar view. */
    private final String name;
    /** The start gramg of the grammar. */
    private Graph startGraph;
    /** The rule system properties of this grammar view. */
    private SystemProperties properties;
    private List<String> errors;
//	/** The graph grammar derived from the rule views. */
//    private GraphGrammar grammar;
}