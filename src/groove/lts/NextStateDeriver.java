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
 * $Id 
 */
package groove.lts;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import groove.graph.Graph;
import groove.trans.DefaultDeriver;
import groove.trans.DerivationData;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleDependencies;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;

/**
 * Deriver that uses information about the underlying transition of a {@link NextState}
 * to compute its results.
 * @author Arend Rensink
 * @version $Revision
 */
public class NextStateDeriver extends DefaultDeriver {
    /** Flag to indicate if dependencies should be used to optimize derivations. */
    static private boolean useDependencies = true;
    
    /**
     * Indicates if rule dependencies are used for optimizing derivations.
     * The default value is <code>true</code>.
     * @see #setUseDependencies(boolean) 
     */
	public static boolean isUseDependencies() {
        return useDependencies;
    }

    /**
     * Sets the policy whether rule dependencies are used for optimizing derivations.
     * The default value is <code>true</code>.
     * @see #isUseDependencies() 
     */
    public static void setUseDependencies(boolean useDependencies) {
        NextStateDeriver.useDependencies = useDependencies;
    }

    /**
	 * Constructs a new deriver, on the basis of a given set of rules.
	 * @param record the rules for this deriver
	 */
	public NextStateDeriver(DerivationData record) {
		super(record);
	}

	@Override
	public Set<RuleApplication> getDerivations(Graph graph) {
		if (graph instanceof GraphNextState && ((GraphNextState) graph).source().isClosed()) {
	        reporter.start(GET_DERIVATIONS);
			Set<RuleApplication> result = createApplicationSet();
			GraphNextState state = (GraphNextState) graph;
			Rule prevRule = state.getRule();
			int prevRulePriority = prevRule.getPriority();
			Collection<Rule> enabledRules = getEnabledRules(prevRule);
			// first investigate the higher-priority rules
			int currentPriority = Integer.MIN_VALUE;
			boolean sufficientPriority = true;
            boolean aliasesCollected = false;
            for (Rule rule: getRules()) {
				// try next production rule
                int rulePriority = rule.getPriority();
                assert rulePriority >= prevRulePriority || enabledRules.contains(rule);
				sufficientPriority = (rulePriority >= currentPriority);
				if (sufficientPriority) {
                    // aliases of previous-state transitions need to be collected only once
				    if (rulePriority == prevRulePriority && !aliasesCollected) {
				        if (collectAliases(state, result)) {
				            currentPriority = rulePriority;
				        }
                        aliasesCollected = true;
                    }
                    // if the rule is enabled by the previous-state rule, it
                    // is possible that there are new applications
                    if (isUseDependencies() && enabledRules.contains(rule) && collectApplications(rule, graph, result)) {
                        currentPriority = rulePriority;
                    }
                }
			}
			reporter.stop();
	        return result;
		} else {
			return super.getDerivations(graph);
		}
	}

    /**
     * Constructs the aliased applications from the outgoing transitions of the previous state.
     * The return value indicates if any applications were found.
     * @param state the derived state for which we want to alias the outgoing transitions 
     * @param result the collection to add the resulting applications to
     * @return <code>true</code> if any applications were found
     */
    protected boolean collectAliases(GraphNextState state, Set<RuleApplication> result) {
    	reporter.start(COLLECT_ALIASES);
        boolean added = false;
        Rule prevRule = state.getRule();
        Collection<Rule> disabledRules = getDisabledRules(prevRule);
        // if the state rule has high enough priority, go through the previous state's transitions
        GraphState prevSource = state.source();
        Iterator<GraphOutTransition> prevTransitionIter = prevSource.getOutTransitionIter();
        while (prevTransitionIter.hasNext()) {
            GraphOutTransition prevTransition = prevTransitionIter.next();
            Rule rule = prevTransition.getRule();
            assert rule.getPriority() == prevRule.getPriority() : "Inconsistent priorities "+rule.getPriority()+" and "+prevRule.getPriority();
            RuleEvent event = prevTransition.getEvent();
//            if (!disabledRules.contains(rule) || !((SPOEvent) state.getEvent()).disables(event) && event.hasMatching(state)) {
            if (isUseDependencies() && !disabledRules.contains(rule) || event.hasMatching(state.getGraph())) {
                RuleApplication appl = createApplication((SPOEvent) event, state.getGraph(), prevTransition);
                added |= result.add(appl);
        	}
        }
        reporter.stop();
        return added;
    }
    
    /** Callback factory method to create an {@link AliasSPOApplication}. */
    private RuleApplication createApplication(SPOEvent event, Graph host, GraphOutTransition prior) {
    	return new AliasSPOApplication(event, host, prior); 
    }

	/**
     * Returns the set of rules that may be enabled by a given rule,
     * according to the currently calculated dependencies.
     * @param enabler the (potential) enabler rule
     * @return the set of rules that may be enabled by <code>enabler</code>
     */
    protected Set<Rule> getEnabledRules(Rule enabler) {
        if (dependencies == null) {
            initDependencies();
        }
        return dependencies.getEnableds(enabler);
    }
    
    /**
     * Returns the set of rules that may be disabled by a given rule,
     * according to the currently calculated dependencies.
     * @param disabler the (potential) disabler rule
     * @return the set of rules that may be disabled by <code>disabler</code>
     */
    protected Set<Rule> getDisabledRules(Rule disabler) {
        if (dependencies == null) {
            initDependencies();
        }
        Set<Rule> result = dependencies.getDisableds(disabler);
        assert result != null : String.format("Null rule dependencies for %s", disabler.getName());
        return result;
    }
    
    /**
     * Initializes the rule dependencies.
     */
    protected void initDependencies() {
        dependencies = new RuleDependencies(this.getRules());
    }

    /** Rule dependencies, used to determine possible aliases. */
    private RuleDependencies dependencies;
    /** Profiling handle for the alias collection phase. */
    static protected final int COLLECT_ALIASES = reporter.newMethod("collectAliases(...)");
}