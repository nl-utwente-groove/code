/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.explore.util;

import groove.control.CtrlPar;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchSetCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     * @param virtualEventSet outgoing transitions from the parent state;
     * may be {@code null}
     */
    public MatchSetCollector(GraphState state, ExploreCache cache,
            SystemRecord record,
            Collection<VirtualEvent.GraphState> virtualEventSet) {
        this.state = state;
        this.cache = cache;
        this.record = record;
        this.virtualEventSet = virtualEventSet;
        if (virtualEventSet != null) {
            Rule lastRule = ((GraphNextState) state).getEvent().getRule();
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        }
        if (state.getCtrlState() != null) {
            this.schedule = state.getCtrlState().getSchedule();
        }
    }

    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     */
    public MatchSetCollector(GraphState state, ExploreCache cache,
            SystemRecord record) {
        this(state, cache, record, null);
    }

    /** Returns a single match for the state passed in through the constructor. */
    public RuleEvent getMatch() {
        RuleEvent result = null;
        Rule currentRule = firstRule();
        while (result == null && currentRule != null) {
            // first try to find a virtual event
            Collection<RuleEvent> virtualEvents = getVirtualEvents(currentRule);
            if (virtualEvents == null) {
                // if this fails, try to find a match
                Iterator<RuleMatch> matchIter =
                    currentRule.getMatchIter(this.state.getGraph(), null);
                if (matchIter.hasNext()) {
                    // convert the match to an event
                    result = this.record.getEvent(matchIter.next());
                } else {
                    // no luck; try the next rule
                    currentRule = nextRule(false);
                }
            } else {
                result = virtualEvents.iterator().next();
            }
        }
        return result;
    }

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<RuleEvent> getMatchSet() {
        Set<RuleEvent> result = new LinkedHashSet<RuleEvent>();
        collectMatchSet(result);
        return result;
    }

    /**
     * Collects the set of matching events for the state passed in by the
     * constructor into a collection passed in as a parameter.
     */
    public void collectMatchSet(Collection<RuleEvent> result) {
        Rule currentRule = firstRule();
        while (currentRule != null) {
            boolean hasMatches = collectEvents(currentRule, result);
            currentRule = nextRule(hasMatches);
        }
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     * @param rule the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were
     *         added to <code>result</code>
     */
    protected boolean collectEvents(Rule rule, Collection<RuleEvent> result) {
        boolean hasMatched = collectVirtualEvents(rule, result);
        CtrlState ctrlState = this.state.getCtrlState();
        // AREND: I added here a check so that new matches are also added when the cache is a location cache
        // because the parent state may (regardless enabledRules) have no matches in the parent due to control.
        if (this.enabledRules == null || this.enabledRules.contains(rule)
            || ctrlState != null) {
            // if this rule used parameters in the control expression, we need
            // to construct a partial morphism out of them
            NodeEdgeMap m = null;
            boolean morphismError = false;
            if (ctrlState != null) {
                CtrlTransition ctrlTrans = ctrlState.getTransition(rule);
                List<CtrlPar> args = ctrlTrans.getCall().getArgs();
                if (args != null && args.size() > 0) {
                    int[] parBinding = ctrlTrans.getParBinding();
                    List<CtrlPar.Var> ruleSig = rule.getSignature();
                    Node[] boundNodes = this.state.getBoundNodes();
                    m = new NodeEdgeHashMap();
                    for (int i = 0; i < args.size(); i++) {
                        CtrlPar arg = args.get(i);
                        Node image = null;
                        if (arg instanceof CtrlPar.Const) {
                            image = ((CtrlPar.Const) arg).getConstNode();
                            assert image != null : String.format(
                                "Constant argument %s not initialised properly",
                                arg);
                        } else if (arg.isInOnly()) {
                            image = boundNodes[parBinding[i]];
                            if (image == null) {
                                morphismError = true;
                                break;
                            }
                        } else {
                            continue;
                        }
                        m.putNode(ruleSig.get(i).getRuleNode(), image);
                    }
                }
            }

            if (!morphismError) {
                // the rule was possibly enabled afresh, so we have to add the fresh
                // matches
                for (RuleMatch match : rule.getMatches(this.state.getGraph(), m)) {
                    result.add(this.record.getEvent(match));
                    hasMatched = true;
                }
            }
        }
        return hasMatched;
    }

    /**
     * Adds the virtual events for a given rule into an existing set.
     * @return <code>true</code> if any virtual events were found
     */
    private boolean collectVirtualEvents(Rule rule, Collection<RuleEvent> result) {
        // add the virtual events for this rule if any
        Collection<RuleEvent> virtualEvents = getVirtualEvents(rule);
        if (virtualEvents == null) {
            return false;
        } else {
            result.addAll(virtualEvents);
            return true;
        }
    }

    /** Returns the virtual events for a given rule. */
    private Collection<RuleEvent> getVirtualEvents(Rule rule) {
        // Create the virtual event map if it is not yet there.
        if (this.virtualEventMap == null && this.virtualEventSet != null) {
            this.virtualEventMap = computeVirtualEventMap();
        }
        if (this.virtualEventMap == null) {
            return null;
        } else {
            return this.virtualEventMap.get(rule);
        }
    }

    /**
     * Computes a map with all matches from the previous state that still match
     * in the current state.
     */
    private Map<Rule,Collection<RuleEvent>> computeVirtualEventMap() {
        Map<Rule,Collection<RuleEvent>> result =
            new HashMap<Rule,Collection<RuleEvent>>();
        if (this.virtualEventSet != null) {
            for (VirtualEvent.GraphState virtual : this.virtualEventSet) {
                Rule rule = virtual.getRule();
                if (isStillValid(virtual)) {
                    Collection<RuleEvent> matches = result.get(rule);
                    if (matches == null) {
                        matches = new ArrayList<RuleEvent>();
                        result.put(rule, matches);
                    }
                    matches.add(virtual);
                    virtualEventReuse++;
                }
            }
        }
        return result;
    }

    /**
     * Tests if a virtual event is still valid in this state.
     */
    private boolean isStillValid(VirtualEvent.GraphState virtual) {
        Rule rule = virtual.getRule();
        boolean result = !this.disabledRules.contains(rule);
        if (!result) {
            assert this.state instanceof GraphNextState;
            // check if the underlying control transition is the same
            CtrlTransition virtualCtrlTrans = virtual.getInnerCtrlTransition();
            CtrlTransition myCtrlTransition = null;
            CtrlState myCtrlState = this.state.getCtrlState();
            if (myCtrlState != null) {
                myCtrlTransition = myCtrlState.getTransition(rule);
            }
            result =
                virtualCtrlTrans == myCtrlTransition
                    && virtual.hasMatch(this.state.getGraph());
        }
        return result;
    }

    /**
     * Returns either the last (previously returned) rule from the
     * {@link ExploreCache}, or the first new rule if there is no last.
     */
    protected Rule firstRule() {
        Rule result;
        CtrlSchedule schedule = this.schedule;
        if (schedule == null) {
            result = this.cache.last();
            if (result == null && this.cache.hasNext()) {
                // this means that the cache was freshly created and has never been
                // incremented before
                result = this.cache.next();
            }
        } else if (schedule.isFinished()) {
            result = null;
        } else {
            result = schedule.getTransition().getRule();
        }
        return result;
    }

    /**
     * Increments the rule iterator, and returns the next rule.
     */
    protected Rule nextRule(boolean matchFound) {
        Rule result;
        CtrlSchedule schedule = this.schedule;
        if (schedule == null) {
            if (matchFound) {
                this.cache.updateMatches(this.cache.last());
            }
            this.cache.updateExplored(this.cache.last());
            result = this.cache.hasNext() ? this.cache.next() : null;
        } else if (schedule.isFinished()) {
            result = null;
        } else {
            this.schedule = schedule = schedule.next(matchFound);
            if (schedule.isFinished()) {
                result = null;
            } else {
                result = schedule.getTransition().getRule();
            }
        }
        return result;
    }

    /** The host graph we are working on. */
    protected final GraphState state;
    /** The schedule of rules to try out. */
    private CtrlSchedule schedule;
    /** Matches cache. */
    protected final ExploreCache cache;
    /** The system record is set at construction. */
    protected final SystemRecord record;
    private final Collection<VirtualEvent.GraphState> virtualEventSet;
    private Map<Rule,Collection<RuleEvent>> virtualEventMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;

    /** Returns the total number of reused parent events. */
    public static int getEventReuse() {
        return virtualEventReuse;
    }

    /** Counter for the number of reused parent events. */
    private static int virtualEventReuse;
}
