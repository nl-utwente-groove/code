package groove.explore.util;

import groove.lts.AbstractGraphState;
import groove.lts.GraphNextState;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An iterator over the matches in a certain state that takes aliases into
 * account. TODO: This class may be better integrated into its super class. For
 * instance, the aliasMatchIter field is not needed, super.matchIter may be used
 * instead
 */
public class AliasMatchesIterator extends MatchesIterator {
    /**
     * Creates an aliased matches iterator for a state and a rule.
     * @param state
     * @param rules
     * @param record the event factory
     */
    public AliasMatchesIterator(GraphNextState state, ExploreCache rules,
            SystemRecord record) {
        super(state, rules, true, record);
        reporter.start(CONSTRUCT);
        Rule lastRule = state.getEvent().getRule();
        this.enabledRules = record.getEnabledRules(lastRule);
        this.disabledRules = record.getDisabledRules(lastRule);
        firstRule();
        goToNext();
        reporter.stop();
    }

    @Override
    protected Iterator<RuleEvent> createEventIter(Rule rule) {
        Collection<RuleEvent> aliasedMatches = getAliasedMatches(rule);
        if (aliasedMatches != null) {
            if (this.enabledRules.contains(rule)) {
                // the rule was possible enabled afresh, so we have to add the
                // fresh matches
                Iterator<RuleEvent> freshMatches = super.createEventIter(rule);
                while (freshMatches.hasNext()) {
                    aliasedMatches.add(freshMatches.next());
                }
            }
            return aliasedMatches.iterator();
        }
        // if (currentRule.getPriority() > priority &&
        // !enabledRules.contains(currentRule)) {
        // return new EmptyMatchIter();
        // }
        // if (currentRule.getPriority() <= priority
        // && !((currentRule instanceof SPORule && ((SPORule)
        // currentRule).hasSubRules()) || enabledRules
        // .contains(currentRule))) {
        // // it didn't match in the previous state or no matches left after
        // rematching
        // return new EmptyMatchIter();
        // }
        return super.createEventIter(rule);
    }

    /**
     * Returns the set of matches from the previous state, if any.
     * @param rule The rule to be matched
     * @return All matches for <code>rule</code>; <code>null</code> if the
     *         matches could not be computed on the basis of the previous state.
     */
    private Collection<RuleEvent> getAliasedMatches(Rule rule) {
        Collection<RuleEvent> result = null;
        AbstractGraphState parent =
            (AbstractGraphState) ((GraphNextState) this.state).source();
        if (parent.isClosed() && isUseDependencies()) {
            if (this.aliasedRuleMatches == null) {
                this.aliasedRuleMatches = computeAliasedMatches();
            }
            result = this.aliasedRuleMatches.get(rule);
            // rules that did not match at all are not included in the
            // aliasedRuleMatches
            if (result == null && !this.enabledRules.contains(rule)) {
                result = Collections.emptyList();
            }
        }
        return result;
    }

    /**
     * Computes a map with all matches from the previous state that still match
     * in the current state.
     */
    private Map<Rule,Collection<RuleEvent>> computeAliasedMatches() {
        reporter.start(COMPUTE_ALIAS_MAP);
        Map<Rule,Collection<RuleEvent>> result =
            new TreeMap<Rule,Collection<RuleEvent>>();
        for (GraphTransitionStub stub : ((AbstractGraphState) ((GraphNextState) this.state).source()).getStoredTransitionStubs()) {
            RuleEvent event =
                stub.getEvent(((GraphNextState) this.state).source());
            Rule rule = event.getRule();
            if (!this.disabledRules.contains(rule)
                || event.hasMatch(this.state.getGraph())) {
                Collection<RuleEvent> matches = result.get(rule);
                if (matches == null) {
                    // if the rule is enabled, we will also add the fresh
                    // matches so we need a set;
                    // otherwise, a list is more efficient
                    if (this.enabledRules.contains(rule)) {
                        matches = new LinkedHashSet<RuleEvent>();
                    } else {
                        matches = new ArrayList<RuleEvent>();
                    }
                    result.put(rule, matches);
                }
                matches.add(new VirtualEvent<GraphTransitionStub>(event, stub));
            }
        }
        reporter.stop();
        return result;
    }

    /**
     * TODO: fixme, currently always enabled if this class is used... (?)
     */
    private boolean isUseDependencies() {
        return true;
    }

    /** Set with matched rules and the corresponding matches * */
    // TODO which exactly matches are there ?
    private Map<Rule,Collection<RuleEvent>> aliasedRuleMatches;

    /** The rules that may be enabled. */
    private final Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private final Set<Rule> disabledRules;
}
