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

import groove.graph.Graph;
import groove.nesting.rule.NestedRule;
import groove.rel.VarMorphism;
import groove.util.AbstractNestedIterator;
import groove.util.NestedIterator;
import groove.util.Pair;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

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
        Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
        int i =0;
        while (result == null && ruleSetIter.hasNext()) {
        	Set<Rule> rules = ruleSetIter.next();
        	Iterator<RuleApplication> iter = getDerivationIter(rules);
        	if (iter.hasNext()) {
        		result = iter;
        	}
        	i++;
        }
        //System.out.println("Number of applications iterated: " + i);
        reporter.stop();
        return result == null ? Collections.<RuleApplication>emptySet().iterator() : result;
	}
	
	/** 
	 * Returns a lazy iterator over the applications of a given set of rules
	 * to a given graph.
	 * The set of rules is guaranteed to be non-empty and to have a uniform priority.
	 */
	Iterator<RuleApplication> getDerivationIter(final Set<Rule> rules) {
        Iterator<Iterator<RuleApplication>> result = new AbstractNestedIterator<Iterator<RuleApplication>>() {
        	@Override
            protected boolean hasNextIterator() {
                while (!atEnd && nextIter == null && ruleIter.hasNext()) {
                    final Rule nextRule = ruleIter.next();
                    // JHK: Outcome hier in geklust
                    final GraphConditionOutcome outcome = nextRule.getOutcome(getGraph());
					nextIter = new TransformIterator<Matching, Iterator<RuleApplication>> (outcome.getSuccessKeys().iterator()) {
						@Override
						public Iterator<RuleApplication> toOuter(Matching from) {
							return record.getApplications(nextRule, from, outcome.get(from)).iterator();
						}
					};
                }
                atEnd |= nextIter == null;
                return !atEnd;
            }

        	@Override
            protected Iterator<Iterator<RuleApplication>> nextIterator() {
            	if (hasNextIterator()) {
            		Iterator<Iterator<RuleApplication>> result = nextIter;
            		nextIter = null;
            		return result;
            	} else {
            		throw new NoSuchElementException();
            	}
            }

            /** An iterator over the priority rule sets of the rule system. */
            private final Iterator<Rule> ruleIter = rules.iterator();
            /** The next iterator to be returned by {@link #nextIterator()} */
            private Iterator<Iterator<RuleApplication>> nextIter;
            /** A flag indicating if we have reached the end of applicable rules. */
            private boolean atEnd = false;
        };
        
        return new NestedIterator<RuleApplication> (result);
	}

	public Set<RuleApplication> getApplications() {
        reporter.start(GET_DERIVATIONS);
		Set<RuleApplication> result = createApplicationSet();
		Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
		while (result.isEmpty() && ruleSetIter.hasNext()) {
			collectApplications(ruleSetIter.next(), result);
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
	    //for (Matching match: rule.getMatchingSet(getGraph())) {
		// TODO:
		//System.out.println("Collection Applications...");
		GraphConditionOutcome outcome = rule.getOutcome(getGraph()); 
		for( Matching match : outcome.getSuccessKeys() ) {
			//System.out.println("Collected Applications");
	        result.addAll(record.getApplications(rule, match, outcome.get(match)));
	        //System.out.println("# Applications: " + result.size());
	    }
	}

	public void doApplications(Action action) {
		reporter.start(GET_DERIVATIONS);
		boolean done = false;
		Iterator<Set<Rule>> ruleSetIter = record.getRuleSystem().getRuleMap().values().iterator();
		while (!done && ruleSetIter.hasNext()) {
			done = doApplications(ruleSetIter.next(), action);
		}
		reporter.stop();
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

		GraphConditionOutcome outcome = rule.getOutcome(getGraph());
		//System.out.println("ARA:Graph used:" + getGraph().hashCode() + ", Rule used:" + rule.hashCode() + ", Successkeys:"+ outcome.getSuccessKeys().size());
		//System.exit(0);

		//System.out.println("_____OUTCOME______\n" + outcome + "\n_____END_OUTCOME_____");
		//System.exit(0);
		for( Matching m : outcome.getSuccessKeys() ) {
			//System.out.println("Successkey!");
			//Matching match = new DefaultMatching(outcome.get(m));
			//NestedRule.buildTotalMatching(match, outcome.get(m));
			//System.out.println("Domain of matching: \n" + match.cod() + "--- End of Domain");
			//System.out.println("Hey... een matching: " + match);
			Set<RuleApplication> applications = record.getApplications(rule, m, outcome.get(m));
			//System.out.println("# of applications: " + applications.size());
			for( RuleApplication application : applications ) {
				reporter.stop();
				reporter.stop();
				action.perform(application);
				reporter.restart(COLLECT_APPLICATIONS);
				reporter.restart(GET_DERIVATIONS);
			}
			result = true;
		}
		
		/**
		for (Matching match : rule.getMatchingSet(getGraph())) {
			RuleApplication application = record.getApplication(rule, match);
			reporter.stop();
			reporter.stop();
			action.perform(application);
			reporter.restart(COLLECT_APPLICATIONS);
			reporter.restart(GET_DERIVATIONS);
			result = true;
		}**/
		return result;
	}

	/**
	 * Callback factory method to create the set to collect the applications in.
	 * It is important that equal applications are collapsed, so the result
	 * should really be a set.
	 */
    protected Set<RuleApplication> createApplicationSet() {
        return new HashSet<RuleApplication>();
    }

    /** Callback method to provide the graph on which the applier works. */
    abstract protected Graph getGraph();
    
    /**
	 * The (fixed) derivation data used by this deriver.
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