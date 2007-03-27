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
import groove.util.AbstractNestedIterator;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Deriver that uses straightforward application of a set of rules,
 * while taking rule priorities into account.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class DefaultDeriver implements Deriver {
	/**
	 * Creates a deriver for a given set of rules.
	 * The rules must be ordered (for an iterator created over the set) by descending priority.
	 * @param rules the rules to be used in this deriver
	 */
	public DefaultDeriver(Collection<Rule> rules) {
		this.rules = rules;
	}
	
	/** This implementation just returns <code>freshNextStates().iterator()</code>. */
	public Iterator<RuleApplication> getDerivationIter(final Graph graph) {
        reporter.start(GET_DERIVATIONS);
        Iterator<RuleApplication> result = new AbstractNestedIterator<RuleApplication>() {
        	@Override
            protected boolean hasNextIterator() {
                while (!atEnd && nextIter == null && ruleIter.hasNext()) {
                    final Rule nextRule = ruleIter.next();
                    // check if we have not already handled higher-priority rules
                    int nextRulePriority = nextRule.getPriority();
                    if (currentPriority <= nextRulePriority) {
                        nextIter = new TransformIterator<Matching,RuleApplication>(nextRule.getMatchingIter(graph)) {
                        	@Override
                            public RuleApplication toOuter(Matching from) {
                                return nextRule.createApplication(from);
                            }
                        };

                        // if there are actually derivations, record the priority of the rule
                        if (nextIter.hasNext()) {
                            currentPriority = nextRulePriority;
                        }
                    } else {
                        atEnd = true;
                    }
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
            /** Priority of the rules appleid so far; <code>Integer.MIN_VALUE</code> if no rules have been found. */
            private int currentPriority = Integer.MIN_VALUE;
        };
        reporter.stop();
        return result;
	}

	public Set<RuleApplication> getDerivations(Graph graph) {
        reporter.start(GET_DERIVATIONS);
		Set<RuleApplication> result = createApplicationSet();
		int currentPriority = Integer.MIN_VALUE;
		boolean sufficientPriority = true;
		for (Rule rule: rules) {
            int rulePriority = rule.getPriority();
			sufficientPriority = (rulePriority >= currentPriority);
			if (sufficientPriority && collectApplications(rule, graph, result)) {
			    currentPriority = rulePriority;
			}
		}
		reporter.stop();
        return result;
	}

	/**
	 * Returns the set of rules of this deriver.
	 */
	protected Collection<Rule> getRules() {
		return rules;
	}
	
    /**
     * Attempts to apply a given rule to a given graph, and collects
     * the applications in a collection passed in as a parameter.
     * The return value indicates if applications were actually found
     * @param rule the rule to be applied
     * @param graph the grph to which the rule is to be applied
     * @param result the collection to add the resulting applications to
     * @return <code>true</code> if the rule was applicable (so applications have been added)
     */
	protected boolean collectApplications(Rule rule, Graph graph, Set<RuleApplication> result) {
		reporter.start(COLLECT_APPLICATIONS);
        boolean added = false;
        // compute applications of this production rule to graph
        for (Matching match: rule.getMatchingSet(graph)) {
            added |= result.add(rule.createApplication(match));
        }
		reporter.stop();
        return added;
    }

    /**
     * Callback factory method to create the set to collect the applications in.
     * It is important that equal applications are collapsed, so the result
     * should really be a set.
     */
    protected Set<RuleApplication> createApplicationSet() {
        return new HashSet<RuleApplication>();
    }

    /**
	 * The set of rules for this deriver.
	 */
	private final Collection<Rule> rules;
	
	/** Reporter instance for profiling this class. */
    static protected final Reporter reporter = Reporter.register(Deriver.class);
    /** Handle for profiling {@link #getDerivations(Graph)}. */
    static protected final int GET_DERIVATIONS = reporter.newMethod("getDerivations(Graph)");
    /** Handle for profiling {@link #collectApplications(Rule, Graph, Set)}. */
    static protected final int COLLECT_APPLICATIONS = reporter.newMethod("collectApplications(...)");
}