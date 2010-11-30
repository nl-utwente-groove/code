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
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;

import java.util.Collection;
import java.util.Iterator;
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
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     */
    public MatchSetCollector(GraphState state, SystemRecord record) {
        this.state = state;
        this.ctrlState = state.getCtrlState();
        assert this.ctrlState != null;
        this.record = record;
        GraphState parent = null;
        if (state instanceof GraphNextState) {
            parent = ((GraphNextState) state).source();
        }
        if (parent != null && parent.isClosed()) {
            this.parentOutMap = parent.getTransitionMap();
            Rule lastRule = ((GraphNextState) state).getEvent().getRule();
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        } else {
            this.parentOutMap = null;
        }
    }

    /** Returns a single match for the state passed in through the constructor. */
    public MatchResult getMatch() {
        MatchResult result = null;
        Rule currentRule = firstRule();
        while (result == null && currentRule != null) {
            // first try to find a virtual event
            Collection<GraphTransition> parentOuts = getParentOuts(currentRule);
            if (parentOuts != null) {
                for (GraphTransition parentOut : parentOuts) {
                    if (isStillValid(parentOut)) {
                        result = parentOut;
                        break;
                    }
                }
            }
            if (result == null) {
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
            }
        }
        return result;
    }

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<MatchResult> getMatchSet() {
        Set<MatchResult> result = new MatchResultSet();
        collectMatchSet(result);
        return result;
    }

    /**
     * Collects the set of matching events for the state passed in by the
     * constructor into a collection passed in as a parameter.
     */
    public void collectMatchSet(Collection<MatchResult> result) {
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
    protected boolean collectEvents(Rule rule, Collection<MatchResult> result) {
        boolean hasMatched = addParentOuts(rule, result);
        if (hasNewMatches(rule)) {
            // if this rule used parameters in the control expression, we need
            // to construct a partial morphism out of them
            boolean bindingCorrect = true;
            NodeEdgeMap boundMap = null;
            CtrlTransition ctrlTrans = this.ctrlState.getTransition(rule);
            List<CtrlPar> args = ctrlTrans.getCall().getArgs();
            if (args != null && args.size() > 0) {
                boundMap = new NodeEdgeHashMap();
                bindingCorrect =
                    extractBinding(rule, ctrlTrans, args, boundMap);
            }
            if (bindingCorrect) {
                // the rule was possibly enabled afresh, so we have to add the fresh
                // matches
                for (RuleMatch match : rule.getMatches(this.state.getGraph(),
                    boundMap)) {
                    result.add(this.record.getEvent(match));
                    hasMatched = true;
                }
            }
        }
        return hasMatched;
    }

    private boolean hasNewMatches(Rule rule) {
        if (this.enabledRules == null || this.enabledRules.contains(rule)) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getCtrlTransition().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule was untried in
        // the parent state
        return !state.source().getSchedule().getTriedRules().contains(rule);
    }

    /** Extracts the morphism from rule nodes to input graph nodes
     * corresponding to the transition's input parameters.
     * @param result collects the resulting binding
     * @return if {@code false}, the binding cannot be constructed and
     * so the rule cannot match
     */
    private boolean extractBinding(Rule rule, CtrlTransition ctrlTrans,
            List<CtrlPar> args, NodeEdgeMap result) {
        boolean success = true;
        int[] parBinding = ctrlTrans.getParBinding();
        List<CtrlPar.Var> ruleSig = rule.getSignature();
        Node[] boundNodes = this.state.getBoundNodes();
        for (int i = 0; i < args.size(); i++) {
            CtrlPar arg = args.get(i);
            Node image = null;
            if (arg instanceof CtrlPar.Const) {
                image = ((CtrlPar.Const) arg).getConstNode();
                assert image != null : String.format(
                    "Constant argument %s not initialised properly", arg);
            } else if (arg.isInOnly()) {
                image = boundNodes[parBinding[i]];
                // test if the bound node is not deleted by a previous rule
                if (image == null) {
                    success = false;
                    break;
                }
            } else {
                // non-input arguments are ignored
                continue;
            }
            result.putNode(ruleSig.get(i).getRuleNode(), image);
        }
        return success;
    }

    /**
     * Adds the parent's out-transitions for a given rule into an existing set.
     * @return <code>true</code> if any virtual events were found
     */
    private boolean addParentOuts(Rule rule, Collection<MatchResult> result) {
        boolean hasMatches = false;
        Collection<GraphTransition> parentOuts = getParentOuts(rule);
        if (parentOuts != null) {
            for (GraphTransition parentOut : parentOuts) {
                if (isStillValid(parentOut)) {
                    result.add(parentOut);
                    hasMatches = true;
                }
            }
        }
        return hasMatches;
    }

    /** Returns the parent's out-transitions for a given rule (if any). */
    public Collection<GraphTransition> getParentOuts(Rule rule) {
        return this.parentOutMap == null ? null : this.parentOutMap.get(rule);
    }

    /**
     * Tests if a parent out-transition is still valid in this state.
     */
    private boolean isStillValid(GraphTransition parentTrans) {
        Rule rule = parentTrans.getEvent().getRule();
        boolean result = !this.disabledRules.contains(rule);
        if (!result) {
            assert this.state instanceof GraphNextState;
            // check if the underlying control transition is the same
            CtrlTransition virtualCtrlTrans = parentTrans.getCtrlTransition();
            CtrlState myCtrlState = this.state.getCtrlState();
            CtrlTransition myCtrlTrans =
                myCtrlState == null ? null : myCtrlState.getTransition(rule);
            result =
                virtualCtrlTrans == myCtrlTrans
                    && parentTrans.getEvent().hasMatch(this.state.getGraph());
        }
        return result;
    }

    /**
     * Returns the first rule of the state's control schedule.
     */
    protected Rule firstRule() {
        Rule result;
        CtrlSchedule schedule = this.ctrlState.getSchedule();
        this.state.setSchedule(schedule);
        if (schedule.isFinished()) {
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
        CtrlSchedule schedule = this.state.getSchedule();
        if (schedule.isFinished()) {
            result = null;
        } else {
            this.state.setSchedule(schedule = schedule.next(matchFound));
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
    /** The control state of the graph state, if any. */
    private final CtrlState ctrlState;
    /** The system record is set at construction. */
    protected final SystemRecord record;
    /** Possibly {@code null} mapping from rules to sets of outgoing
     * transitions for the parent of this state.
     */
    private final Map<Rule,Collection<GraphTransition>> parentOutMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;

    /** Returns the total number of reused parent events. */
    public static int getEventReuse() {
        return parentOutReuse;
    }

    /** Counter for the number of reused parent events. */
    private static int parentOutReuse;
}
