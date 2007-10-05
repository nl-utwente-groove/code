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
/**
 * 
 */
package groove.trans;

import groove.control.ControlView;
import groove.graph.Graph;
import groove.util.AbstractNestedIterator;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Rule applier that works for an ordinary rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractRuleApplier implements RuleApplier {
	/**
	 * Creates a rule applier for a given rule system record.
	 * @param record the rules to be used in this deriver
	 */
	public AbstractRuleApplier(SystemRecord record) {
		this.record = record;
	}
	
	public Iterator<RuleApplication> getApplicationIter() {
		Iterator<RuleApplication> result = null;

		reporter.start(GET_DERIVATIONS);
        // find the first batch of rules that has any derivations
        
        //Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
        // optionally return only for current control state
        Iterator<Set<Rule>> ruleSetIter = getRuleSetIter();
        
        while (result == null && ruleSetIter.hasNext()) {
        	Set<Rule> rules = ruleSetIter.next();
        	// if i'm dealing with lambdas i have to use getApplications, cant be done lazy
        	
        	
        	// this would implement the stuff considering an iterator over all applications
        	// but it doesn't matter yet for linear exploration since it would only do the
        	// highest priority anyway. Maybe add later for e.g. barbed?
        	// TODO: should this be inhere?
        	//int priority = rules.iterator().next().getPriority();
        	//if( priority == ControlView.ANY_RULE_PRORITY)
        		//iter = this.getApplications().iterator();
        	// else
            	Iterator<RuleApplication> iter = getDerivationIter(rules);
        	

        	if (iter.hasNext()) {
        		result = iter;
        	}
        	
        	// make sure it continues when the current result yields only lambda transitions
        	// except for else transitions
        }
        reporter.stop();
        return result == null ? Collections.<RuleApplication>emptySet().iterator() : result;
	}
	
	/** 
	 * Returns a lazy iterator over the applications of a given set of rules (with
	 * equal priority) to the underlying graph.
	 * @param rules the set of rules to be applied;
	 * assumed to be non-empty and to have a uniform priority.
	 */
	private Iterator<RuleApplication> getDerivationIter(final Set<Rule> rules) {
        Iterator<RuleApplication> result = new AbstractNestedIterator<RuleApplication>() {
        	@Override
            protected boolean hasNextIterator() {
                while (!atEnd && nextIter == null && ruleIter.hasNext()) {
                    final Rule nextRule = ruleIter.next();
					nextIter = new TransformIterator<RuleMatch, RuleApplication>(nextRule.getMatchIter(getGraph(), null)) {
						@Override
						public RuleApplication toOuter(RuleMatch from) {
							return record.getApplication(from, getGraph());
						}
					};
                }
                atEnd |= nextIter == null;
                return !atEnd;
            }

        	@Override
            protected Iterator<RuleApplication> nextIterator() {
            	if (hasNextIterator()) {
            		Iterator<RuleApplication> result = nextIter;
            		nextIter = null;
            		return result;
            	} else {
            		throw new NoSuchElementException();
            	}
            }

            /** An iterator over the priority rule sets of the rule system. */
            private final Iterator<Rule> ruleIter = rules.iterator();
            /** The next iterator to be returned by {@link #nextIterator()} */
            private Iterator<RuleApplication> nextIter;
            /** A flag indicating if we have reached the end of applicable rules. */
            private boolean atEnd = false;
        };
        return result;
	}

	public Set<RuleApplication> getApplications() {
        reporter.start(GET_DERIVATIONS);
		Set<RuleApplication> result = createApplicationSet();
		//Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
		
		Iterator<Set<Rule>> ruleSetIter = getRuleSetIter();
		
		Set<Rule> rules = null;

		// the commented lines would be a fix for collecting all possible applications 
		// when we have lambda transitions also. not needed for full or linear (where in
		// linear it would only do a lambda transition anyway)
		// TODO: should this be added?
		
		//boolean done = false;
		
		// need done here instead of isEmpty if the commented lines are uncommented
		while (!result.isEmpty() && ruleSetIter.hasNext()) {
			
			//int size = result.size();
			rules = ruleSetIter.next();
			
			//int priority = rules.iterator().next().getPriority();
			
			// dont collect when i have results and i'm dealing with ELSE
			//if( priority > ControlView.ELSE_RULE_PRIORITY || size == 0)
			collectApplications(rules, result);

			// if i didnt collect for LAMBDA and i have results i'm done
			//if( priority < ControlView.ANY_RULE_PRORITY && size > 0 )
			//	done = true;
		}
		reporter.stop();
        return result;
	}

    /**
     * Attempts to apply a given set of rules to a given graph, and collects
     * the applications in a collection passed in as a parameter.
     * The return value indicates if applications were actually found
     * @param rules the set of rules to be applied
     * @param result the collection to add the resulting applications to
     */
	protected void collectApplications(Set<Rule> rules, Set<RuleApplication> result) {
		reporter.start(COLLECT_APPLICATIONS);
        for (Rule rule: rules) {
        	collectApplications(rule, result);
        }
		reporter.stop();
    }

    /**
	 * Attempts to apply a given rule to a given graph, and collects
	 * the applications in a collection passed in as a parameter.
	 * The return value indicates if applications were actually found
	 * @param rule the rule to be applied
	 * @param result the collection to add the resulting applications to
	 */
	protected void collectApplications(Rule rule, Set<RuleApplication> result) {
	    // compute applications of this production rule to graph
	    for (RuleMatch match: rule.getMatches(getGraph(), null)) {
	        result.add(record.getApplication(match, getGraph()));
	    }
	}

	/**
	 *  Returns the currently possible rules.
	 */
	protected Iterator<Set<Rule>> getRuleSetIter()
	{
		// default implementation
		return record.getRuleSystem().getRuleMap().values().iterator();
	}
	
	public boolean doApplications(Action action) {
		reporter.start(GET_DERIVATIONS);
		boolean done = false;
		boolean lambdas = false;
		
		//Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
		Iterator<Set<Rule>> ruleSetIter = getRuleSetIter();
		
		Set<Rule> rules = null;

		int priority;
		
		while (!done && ruleSetIter.hasNext()) {
			rules = ruleSetIter.next();
			
			priority = rules.iterator().next().getPriority();
			if( priority == ControlView.ANY_RULE_PRORITY )
				lambdas = doApplications(rules, action);
			else if( lambdas && priority == ControlView.ELSE_RULE_PRIORITY )
				done = true;
			else if( !lambdas && priority == ControlView.ELSE_RULE_PRIORITY )
				done = doApplications(rules, action);
			else
				done = doApplications(rules, action);
		}
		reporter.stop();
		return done;
	}

	/**
	 * Attempts to apply a given set of rules to the graph, and performs a given action
	 * for all applications found.
	 * 
	 * @param rules
	 *            the set of rules to be applied
	 * @param action
	 *            the action to perform for all rule applications found
	 * @return <code>true</code> if the action was applied at least once 
	 */
	protected boolean doApplications(Set<Rule> rules, Action action) {
		boolean result = false;
		reporter.start(COLLECT_APPLICATIONS);
		for (Rule rule : rules) {
			if (doApplications(rule, action)) {
				result = true;
			}
		}
		reporter.stop();
		return result;
	}

	/**
	 * Attempts to apply a given rule to the graph, and performs a given action
	 * for all applications found.
	 * 
	 * @param rule
	 *            the rule to be applied
	 * @param action
	 *            the action to perform for all rule applications found
	 * @return <code>true</code> if the action was applied at least once 
	 */
	protected boolean doApplications(Rule rule, Action action) {
		boolean result = false;
		for (RuleMatch match : rule.getMatches(getGraph(), null)) {
			RuleApplication application = record.getApplication(match, getGraph());
			reporter.stop();
			reporter.stop();
			action.perform(application);
			reporter.restart(COLLECT_APPLICATIONS);
			reporter.restart(GET_DERIVATIONS);
			result = true;
		}
		
		return result;
	}

	/**
	 * Callback factory method to create the set to collect the applications in.
	 * It is important that equal applications are collapsed, so the result
	 * should really be a set.
	 */
    protected Set<RuleApplication> createApplicationSet() {
        return new LinkedHashSet<RuleApplication>();
    }

    /** Callback method to provide the graph on which the applier works. */
    abstract protected Graph getGraph();
    
    /**
	 * The (fixed) system record used by this deriver.
	 */
	protected final SystemRecord record;
//	/**
//	 * The (fixed) graph of this deriver.
//	 */
//	protected final Graph graph;
	
	/** Reporter instance for profiling this class. */
    static protected final Reporter reporter = Reporter.register(RuleApplier.class);
    /** Handle for profiling {@link #getApplications()}. */
    static protected final int GET_DERIVATIONS = reporter.newMethod("getDerivations(Graph)");
    /** Handle for profiling {@link #collectApplications(Rule, Set)}. */
    static protected final int COLLECT_APPLICATIONS = reporter.newMethod("collectApplications(...)");
}