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
 * $Id$
 */
package groove.view;

import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class DefaultGrammarView implements GrammarView<AspectualGraphView,AspectualRuleView>, View<GraphGrammar> {
    /**
     * Constructs a (non-fixed) copy of an existing rule view grammar.
     */
    public DefaultGrammarView(GrammarView<AspectualGraphView,AspectualRuleView> oldGrammar) {
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
    public DefaultGrammarView(String name) {
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
		AspectualRuleView result = removeRule(ruleView.getNameLabel());
		ruleMap.put(ruleView.getNameLabel(), ruleView);
		int priority = ruleView.getPriority();
		Set<AspectualRuleView> priorityRules = priorityMap.get(priority);
		if (priorityRules == null) {
			priorityMap.put(priority, priorityRules = new TreeSet<AspectualRuleView>());
		}
		priorityRules.add(ruleView);
		invalidateGrammar();
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
			Set<AspectualRuleView> priorityRules = priorityMap.get(priority);
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
    
    public Map<Integer, Set<AspectualRuleView>> getPriorityMap() {
		return Collections.unmodifiableMap(priorityMap);
	}

    public AspectualGraphView getStartGraph() {
		return startGraph;
	}

    /** Sets the start graph to a given graph. */
    public void setStartGraph(AspectualGraphView startGraph) {
		this.startGraph = startGraph;
		invalidateGrammar();
	}

    /** Collects and returns the permanent errors of the rule views. */
	public void addErrors(List<String> errors) {
		this.errors.addAll(errors);
	}

    /** Collects and returns the permanent errors of the rule views. */
	public List<String> getErrors() {
    	if (errors == null) {
    		initGrammar();
    	}
    	return errors;
	}

	/** Delegates to {@link #toGrammar()}. */
	public GraphGrammar toModel() throws FormatException {
		return toGrammar();
	}

	/** Converts the grammar view to a real grammar. */
    public GraphGrammar toGrammar() throws FormatException {
    	if (errors == null) {
    		initGrammar();
    	}
    	if (errors.isEmpty()) {
    		return grammar;
    	} else {
    		throw new FormatException(errors);
    	}
    }
    
    /** Initialises the {@link #grammar} and {@link #errors} fields. */
    private void initGrammar() {
    	try {
    		grammar = computeGrammar();
    		errors = Collections.emptyList();
    	} catch (FormatException exc) {
    		errors = new ArrayList<String>(exc.getErrors());
    		Collections.sort(errors);
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
    			// only add the enabled rules
    			if (ruleView.isEnabled()) {
    				result.add(ruleView.toRule());
    			}
    		} catch (FormatException exc) {
    			for (String error: exc.getErrors()) {
    				errors.add(String.format("Format error in %s: %s", ruleView.getNameLabel(), error));
    			}
    		}
    	}
    	result.setProperties(getProperties());
    	if (getStartGraph() == null) {
			errors.add("Grammar has no start graph");
		} else {
			try {
				result.setStartGraph(getStartGraph().toModel());
				result.setFixed();
			} catch (FormatException exc) {
    			for (String error: exc.getErrors()) {
    				errors.add(String.format("Format error in start graph: %s", error));
    			}
			}
		}
		if (errors.isEmpty()) {
			return result;
		} else {
			throw new FormatException(errors);
		}
    }
    
    /** 
     * Resets the internally stored graph grammar to <code>null</code>, so
     * the next call to {@link #toGrammar()} will have to recompute it.
     * This is done in reaction to a change in the rules, start graph or properties. 
     */
    private void invalidateGrammar() {
    	grammar = null;
    	errors = null;
    }
    
    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<RuleNameLabel,AspectualRuleView> ruleMap = new TreeMap<RuleNameLabel,AspectualRuleView>();
	/** Mapping from priorities to sets of rule names. */
    private final Map<Integer,Set<AspectualRuleView>> priorityMap = new TreeMap<Integer,Set<AspectualRuleView>>(Rule.PRIORITY_COMPARATOR);
    /** The name of this grammar view. */
    private final String name;
    /** The start gramg of the grammar. */
    private AspectualGraphView startGraph;
    /** The rule system properties of this grammar view. */
    private SystemProperties properties;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<String> errors;
	/** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;
}