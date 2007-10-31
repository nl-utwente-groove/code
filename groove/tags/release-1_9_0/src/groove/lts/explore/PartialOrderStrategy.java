/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: PartialOrderStrategy.java,v 1.3 2007-06-01 12:31:08 kastenberg Exp $
 */

package groove.lts.explore;

import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleApplier;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

/**
 * LTS exploration strategy based on the principle of depth search, but without 
 * backtracking.
 * The search is continued until the maximum depth is reached, or a 
 * <i>maximal</i> state is found; that is, a state that only loops back to already explored states.
 * The maximum depth of the search can be set; a depth of 0 means unbounded depth.
 * @author Harmen Kastenberg
 * @version $Revision: 1.3 $
 */
public class PartialOrderStrategy extends AbstractStrategy {
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "Partial Order";
	/** Short description of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Explores only transitions that form persistent sets";

    /**
     * @param grammar
     */
    public PartialOrderStrategy(Simulator simulator) {
    	this.simulator = simulator;
    }

    /** The result of this strategy is the set of all states traversed during exploration. */
    public Collection<? extends State> explore() throws InterruptedException {
    	initializeDependencies();
    	List<GraphState> todo = new ArrayList<GraphState>();
        todo.add(getAtState());
        Collection<GraphState> result = createStateSet();
        while (!todo.isEmpty()) {
        	final GraphState atState = todo.remove(0);
        	result.add(atState);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            RuleApplier applier = getApplier(atState);
            Set<RuleApplication> applications = applier.getApplications();
            Set<RuleApplication> persistentApplications = calculatePersistentSet(applications);
            for (RuleApplication application: persistentApplications) {
            	GraphTransition transition = createTransition(application, atState);
            	GraphState isoState = getGTS().addState(transition.target());
            	if (isoState == null) {
            		todo.add(transition.target());
            	}
            	addTransition(atState, application);
            }
        }
        return result;
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    /**
     * 
     */
    private boolean initializeDependencies() {
    	if (rules == null) {
    		try {
        		rules = simulator.getCurrentGrammar().toGrammar().getRules(); 
    		} catch (FormatException exc) {
    			exc.printStackTrace();
    		}
    	}
    	boolean initialized = false;
    	JFileChooser chooser = new JFileChooser();
    	chooser.showOpenDialog(null);
    	File file = chooser.getSelectedFile();

    	BufferedReader in;
    	String line;
    	try {
    		in = new BufferedReader(new FileReader(file));
    		line = in.readLine();
    		while (line != null && line.length() > 0) {
    			int end = line.indexOf(':');
    			String keyRuleName = line.substring(0,end);
    			line = line.substring(end+1);
    			line = line.trim();
    			int nextSeparator;
    			int endMark;
    			String ruleName;
    			do {
    				nextSeparator = line.indexOf(',');
    				endMark = line.indexOf(';');
    				if (nextSeparator != -1) {
    					ruleName = line.substring(0,nextSeparator);
        				line = line.substring(nextSeparator+1);
        				line = line.trim();
    				} else {
    					ruleName = line.substring(0,endMark);
    				}
    				addDependency(keyRuleName, ruleName);
    			} while (nextSeparator != -1);
    			line = in.readLine();
    		}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException ie) {
    		ie.printStackTrace();
    	}
    	return initialized;
    }

    private boolean addDependency(String rule1, String rule2) {
    	boolean result = false;
    	if (dependenciesRuleNames == null) {
    		dependenciesRuleNames = new HashMap<String,Set<String>>();
    	}

    	if (!dependenciesRuleNames.containsKey(rule1)) {
    		Set<String> dependentRules = new HashSet<String>();
    		result = dependentRules.add(rule2);
    		dependenciesRuleNames.put(rule1, dependentRules);
    	} else {
    		result = dependenciesRuleNames.get(rule1).add(rule2);
    	}
    	return result;
    }

    /**
     * @param rule1 the rule to be used as the key in the map
     * @param rule2 the rule being dependent with <code>rule1</code>
     * @return <tt>true</tt> if the set does not already contain <code>rule2</code>
     */
    private boolean addDependency(Rule rule1, Rule rule2) {
    	boolean result = false;
    	if (dependenciesRules == null) {
    		dependenciesRules = new HashMap<Rule, Set<Rule>>();
    	}
    	if (!dependenciesRules.containsKey(rule1)) {
    		Set<Rule> dependentRules = new HashSet<Rule>();
    		result = dependentRules.add(rule2);
    		dependenciesRules.put(rule1, dependentRules);
    	} else {
    		result = dependenciesRules.get(rule1).add(rule2);
    	}
    	return result;
    }

    private Collection<? extends GraphState> calculatePersistentSet(Collection<? extends GraphState> successors) {
    	Set<GraphState> result = new HashSet<GraphState>();
    	// pick a rule application non-deterministically
    	GraphState graphState = successors.iterator().next();
    	// now construct a persistent set containing this rule application
    	if (graphState instanceof GraphTransition) {
    		result.add(graphState);
    		GraphTransition transition = (GraphTransition) graphState;
    		Rule currentRule = transition.getEvent().getRule();
    		// for every rule on which this rule depends we check whether there it is
    		// applicable to the same graph
    		if (dependenciesRuleNames.containsKey(currentRule.getName().name().toString())) {
    			Set<String> dependentRuleNames = dependenciesRuleNames.get(currentRule.getName().name().toString());
    			for (String ruleName: dependentRuleNames) {
    				// put all the rule applications of the dependent rules in the result
    				boolean applied = false;
    				for (GraphState nextSuccessor: successors) {
    					if (nextSuccessor instanceof GraphTransition) {
    						GraphTransition nextTransition = (GraphTransition) nextSuccessor;
    						if (nextTransition.getEvent().getRule().getName().name().toString().equals(ruleName)) {
    							result.add(nextSuccessor);
    							applied = true;
    						}
    					}
    				}
    				if (!applied) {
    					// if one of the dependent rules has no applications to the current graph
    					// we need to return the whole set of successors.
    					return successors;
    				}
    			}
    		}
    	}
    	return result;
    }

    private Set<RuleApplication> calculatePersistentSet(Set<RuleApplication> applications) {
    	Map<RuleApplication, Set<RuleApplication>> persistentSets = new HashMap<RuleApplication, Set<RuleApplication>>();
    	Set<RuleApplication> result = new HashSet<RuleApplication>();
    	if (applications.isEmpty())
    		return result;

    	// pick a rule application non-deterministically
    	for (RuleApplication application: applications) {
    		Set<RuleApplication> persistentSet = calculatePersistentSet(application, applications);
    		persistentSets.put(application, persistentSet);
    	}
    	// return the smallest persistent set
    	int min = Integer.MAX_VALUE;
    	for (RuleApplication application: persistentSets.keySet()) {
    		Set<RuleApplication> persistentSet = persistentSets.get(application);
    		if (persistentSet.size() < min) {
    			min = persistentSet.size();
    			result = persistentSet;
    		}
    	}
    	return result;
    }

    private Set<RuleApplication> calculatePersistentSet(RuleApplication application, Set<RuleApplication> applications) {
    	Set<RuleApplication> result = new HashSet<RuleApplication>();
    	// pick a rule application non-deterministically
    	// now construct a persistent set containing this rule application
    	result.add(application);
    	Rule currentRule = application.getRule();
    	// for every rule on which this rule depends we check whether there it is
    	// applicable to the same graph
    	if (dependenciesRuleNames.containsKey(currentRule.getName().name().toString())) {
    		Set<String> dependentRuleNames = dependenciesRuleNames.get(currentRule.getName().name().toString());
    		for (String ruleName: dependentRuleNames) {
    			// put all the rule applications of the dependent rules in the result
    			boolean applied = false;
    			for (RuleApplication nextApplication: applications) {
    				if (nextApplication.getRule().getName().name().toString().equals(ruleName)) {
    					result.add(nextApplication);
    					applied = true;
    				}
    			}
    			if (!applied) {
    				// if one of the dependent rules has no applications to the current graph
    				// we need to return the whole set of successors.
    				return applications;
    			}
    		}
    	}
    	return result;
    }

    /**
     * Asymmetric mapping from rules to sets of potential dependent rules.
     */
    private Map<Rule, Set<Rule>> dependenciesRules;
    private Map<String, Set<String>> dependenciesRuleNames;

    protected Simulator simulator;
    protected Collection<Rule> rules;
}