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

import groove.control.ControlState;
import groove.control.ControlView;
import groove.control.Location;
import groove.graph.Graph;
import groove.trans.AbstractRuleApplier;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Deriver that uses information about the underlying transition of a {@link NextState}
 * to compute its results.
 * @author Arend Rensink
 * @version $Revision
 */
public class AliasRuleApplier extends AbstractRuleApplier {
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
        AliasRuleApplier.useDependencies = useDependencies;
    }

    /**
	 * Constructs a new deriver, on the basis of a given rule system instance and graph state.
	 * @param record the rules for this deriver
     * @param state the state to derive application for
	 */
	public AliasRuleApplier(SystemRecord record, GraphState state) {
		super(record);
		setState(state);
	}

    /**
	 * Constructs a new deriver, on the basis of a given rule system instance.
	 * The state is initially not set.
	 * @param record the rules for this deriver
	 */
	public AliasRuleApplier(SystemRecord record) {
		super(record);
	}
	
	/** Sets this applier to work on a given state. */
	public final void setState(GraphState state) {
		this.graph = state.getGraph();
		this.control = state.getLocation();
		if (state instanceof GraphNextState && ((GraphNextState) state).source().isClosed()) {
			this.state = (GraphNextState) state;
			this.rule = this.state.getEvent().getRule();
			this.priority = this.rule.getPriority();
			this.enabledRules = record.getEnabledRules(this.rule);
		} else {
			this.state = null;
			this.rule = null;
			this.priority = 0;
			this.enabledRules = null;
		}
	}
	
    @Override
	protected void collectApplications(Set<Rule> rules, Set<RuleApplication> result) {
    	if (state != null) {
    		int rulesPriority = rules.iterator().next().getPriority();
			if (rulesPriority == priority || rulesPriority == ControlView.ANY_RULE_PRORITY) {
				collectAliases(result);
			}
    	}
    	super.collectApplications(rules, result);
	}

	@Override
	protected void collectApplications(Rule rule, Set<RuleApplication> result) {
		if (doApplyRule(rule)) {
			super.collectApplications(rule, result);
		}
	}

	/**
     * Constructs the aliased applications from the outgoing transitions of the previous state.
     * The return value indicates if any applications were found.
     * TODO this method should probably not rely on the stored transitions;
     * rather, a separate map could be used to keep outgoing transitions as long
     * as they may be required.
	 * @param result the collection to add the resulting applications to
     */
    protected void collectAliases(Set<RuleApplication> result) {
    	reporter.start(COLLECT_ALIASES);
        Collection<Rule> disabledRules = record.getDisabledRules(rule);
        Iterator<GraphTransitionStub> iter =  ((AbstractGraphState) state.source()).getTransitionStubIter();
        //        for (GraphTransition otherTransition: state.source().getTransitionSet()) {
        while (iter.hasNext()) {
        	GraphTransitionStub stub = iter.next();
            RuleEvent event = stub.getEvent(state.source());
            if (isUseDependencies() && !disabledRules.contains(event.getRule()) || event.hasMatch(getGraph())) {
                result.add(createAlias(event, state, stub));
        	}
        }
        reporter.stop();
    }
    
	@Override
	protected boolean doApplications(Set<Rule> rules, final Action action) {
		boolean result = false;
		Action myAction = action;
    	if (state != null && control == null) {
    		int rulesPriority = rules.iterator().next().getPriority();
			if (rulesPriority == priority ) {
				final Set<RuleApplication> aliases = createApplicationSet();
				collectAliases(aliases);
				for (RuleApplication alias : aliases) {
					reporter.stop();
					action.perform(alias);
					reporter.restart(GET_DERIVATIONS);
					result = true;
				}
				if (result) {
					myAction = new Action() {
						public void perform(RuleApplication application) {
							if (!aliases.contains(application)) {
								action.perform(application);
							}
						}
					};
				}
    		}
    	}
    	return super.doApplications(rules, myAction) || result;
	}

	@Override
	protected boolean doApplications(Rule rule, Action action) {
		if (doApplyRule(rule)) {
			return super.doApplications(rule, action);
		} else {
			return false;
		}
	}
	
	/** Indicates is the applications if a given rule should be developed. */
	private boolean doApplyRule(Rule rule) {
	    return state == null || !isUseDependencies() || (rule instanceof SPORule) && ((SPORule) rule).hasSubRules() || enabledRules.contains(rule);
	}

	/** Callback factory method to create an {@link AliasSPOApplication}. */
    private RuleApplication createAlias(RuleEvent event, GraphNextState source, GraphTransitionStub prior) {
    	return new DefaultAliasApplication(event, source, prior); 
    }

    /** 
     * Attempts to obtain the applicable rules from the control state, if any.
     * Calls <code>super</code> if there is no control state.
     */
    @Override
    protected Iterator<Set<Rule>> getRuleSetIter()
    {
    	if( control != null  ) {
    		return control.ruleMap().values().iterator();
    	} else {
    		return super.getRuleSetIter();
    	}
    }
    
    
    @Override
	protected Graph getGraph() {
		return graph;
	}
    
    /** The graph on which this applier currently works. */
    private Graph graph;
	/** The (fixed) state of this deriver. */
    private GraphNextState state;
    /** Control location of the current the state on which this applier works **/
    private Location control;
    /** The rule leading up to <code>state</code>. */
    private Rule rule;
    /** The priority of the rule leading to <code>state</code>. */
    private int priority;
    /** The rules that are enabled by the rule leading to <code>state</code>. */
	private Collection<Rule> enabledRules;

    /** Profiling handle for the alias collection phase. */
    static protected final int COLLECT_ALIASES = reporter.newMethod("collectAliases(...)");
}