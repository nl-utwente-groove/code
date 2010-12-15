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

import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.RuleToHostMap;
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
    public MatchSetCollector(GraphState state, SystemRecord record,
            boolean checkDiamonds) {
        this.state = state;
        this.ctrlState = state.getCtrlState();
        assert this.ctrlState != null;
        this.record = record;
        this.checkDiamonds = checkDiamonds;
        GraphState parent = null;
        if (state instanceof GraphNextState) {
            parent = ((GraphNextState) state).source();
        }
        if (parent != null && parent.isClosed() && this.checkDiamonds()) {
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
        findResult: for (CtrlTransition ctrlTrans = firstCall(); result == null
            && ctrlTrans != null; ctrlTrans = nextCall(false)) {
            // first try to find a parent transition
            Collection<GraphTransition> parentOuts = getParentOuts(ctrlTrans);
            if (parentOuts != null) {
                for (GraphTransition parentOut : parentOuts) {
                    if (isStillValid(parentOut)) {
                        result = parentOut;
                        break findResult;
                    }
                }
            }
            // if this fails, try to find a match
            // taking the variable binding into account
            result = getMatch(ctrlTrans);
        }
        return result;
    }

    /** Returns a match result for a given control transition. */
    protected MatchResult getMatch(CtrlTransition ctrlTrans) {
        RuleToHostMap boundMap = extractBinding(ctrlTrans);
        MatchResult result = null;
        Iterator<RuleMatch> matchIter = null;
        if (boundMap != null) {
            matchIter =
                ctrlTrans.getRule().getMatchIter(this.state.getGraph(),
                    boundMap);
        }
        if (matchIter != null && matchIter.hasNext()) {
            // convert the match to an event
            result = this.record.getEvent(matchIter.next());
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
        CtrlTransition ctrlTrans = firstCall();
        while (ctrlTrans != null) {
            boolean hasMatches = collectEvents(ctrlTrans, result);
            ctrlTrans = nextCall(hasMatches);
        }
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     * @param ctrlTrans the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were
     *         added to <code>result</code>
     */
    protected boolean collectEvents(CtrlTransition ctrlTrans,
            Collection<MatchResult> result) {
        boolean isEnabled = isEnabled(ctrlTrans.getCall());
        // there are three reasons to want to use the parent matches: to
        // save matching time, to reuse added nodes, and to find confluent 
        // diamonds. The first is only relevant if the rule is not (re)enabled,
        // the third only if the parent match target is already closed
        boolean hasMatched = addParentOuts(ctrlTrans, result);
        if (isEnabled) {
            // the rule was possibly enabled afresh, so we have to add the fresh
            // matches
            RuleToHostMap boundMap = extractBinding(ctrlTrans);
            if (boundMap != null) {
                Iterable<RuleMatch> matches =
                    ctrlTrans.getRule().getMatches(this.state.getGraph(),
                        boundMap);
                for (RuleMatch match : matches) {
                    RuleEvent event = this.record.getEvent(match);
                    result.add(event);
                    hasMatched = true;
                }
            }
        }
        return hasMatched;
    }

    private boolean isEnabled(CtrlCall call) {
        if (this.enabledRules == null || this.enabledRules.contains(call)) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getCtrlTransition().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule was untried in
        // the parent state
        return !state.source().getSchedule().getTriedRules().contains(call);
    }

    private boolean checkDiamonds() {
        return this.checkDiamonds;
    }

    /** Extracts the morphism from rule nodes to input graph nodes
     * corresponding to the transition's input parameters.
     * @return if {@code null}, the binding cannot be constructed and
     * so the rule cannot match
     */
    private RuleToHostMap extractBinding(CtrlTransition ctrlTrans) {
        RuleToHostMap result =
            this.state.getGraph().getFactory().createRuleToHostMap();
        List<CtrlPar> args = ctrlTrans.getCall().getArgs();
        if (args != null && args.size() > 0) {
            int[] parBinding = ctrlTrans.getParBinding();
            List<CtrlPar.Var> ruleSig = ctrlTrans.getRule().getSignature();
            HostNode[] boundNodes = this.state.getBoundNodes();
            for (int i = 0; i < args.size(); i++) {
                CtrlPar arg = args.get(i);
                HostNode image = null;
                if (arg instanceof CtrlPar.Const) {
                    image = ((CtrlPar.Const) arg).getConstNode();
                    assert image != null : String.format(
                        "Constant argument %s not initialised properly", arg);
                } else if (arg.isInOnly()) {
                    image = boundNodes[parBinding[i]];
                    // test if the bound node is not deleted by a previous rule
                    if (image == null) {
                        result = null;
                        break;
                    }
                } else {
                    // non-input arguments are ignored
                    continue;
                }
                result.putNode(ruleSig.get(i).getRuleNode(), image);
            }
        }
        return result;
    }

    /**
     * Adds the parent's out-transitions for a given rule into an existing set.
     * @return <code>true</code> if any out-transitions were found
     */
    private boolean addParentOuts(CtrlTransition call,
            Collection<MatchResult> result) {
        boolean hasMatches = false;
        Collection<GraphTransition> parentOuts = getParentOuts(call);
        if (parentOuts != null) {
            for (GraphTransition parentOut : parentOuts) {
                if (isStillValid(parentOut)) {
                    result.add(parentOut);
                    hasMatches = true;
                    parentOutReuse++;
                }
            }
        }
        return hasMatches;
    }

    /** Returns the parent's out-transitions for a given rule (if any). */
    private Collection<GraphTransition> getParentOuts(CtrlTransition ctrlTrans) {
        Collection<GraphTransition> result = null;
        if (this.parentOutMap != null) {
            result = this.parentOutMap.get(ctrlTrans.getCall());
        }
        return result;
    }

    /**
     * Tests if a parent out-transition is still valid in this state.
     */
    private boolean isStillValid(GraphTransition parentOut) {
        Rule rule = parentOut.getEvent().getRule();
        boolean result = !this.disabledRules.contains(rule);
        if (!result) {
            assert this.state instanceof GraphNextState;
            // check if the underlying control transition is the same
            CtrlTransition parentCtrlTrans = parentOut.getCtrlTransition();
            CtrlState myCtrlState = this.state.getCtrlState();
            CtrlTransition myCtrlTrans =
                myCtrlState == null ? null : myCtrlState.getTransition(rule);
            result =
                parentCtrlTrans == myCtrlTrans
                    && parentOut.getEvent().hasMatch(this.state.getGraph());
        }
        return result;
    }

    /**
     * Returns the first rule of the state's control schedule.
     */
    protected CtrlTransition firstCall() {
        CtrlTransition result;
        CtrlSchedule schedule = this.ctrlState.getSchedule();
        this.state.setSchedule(schedule);
        if (schedule.isFinished()) {
            result = null;
        } else {
            result = schedule.getTransition();
        }
        return result;
    }

    /**
     * Increments the rule iterator, and returns the next rule.
     */
    protected CtrlTransition nextCall(boolean matchFound) {
        CtrlTransition result;
        CtrlSchedule schedule = this.state.getSchedule();
        if (schedule.isFinished()) {
            result = null;
        } else {
            this.state.setSchedule(schedule = schedule.next(matchFound));
            if (schedule.isFinished()) {
                result = null;
            } else {
                result = schedule.getTransition();
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
    private final Map<CtrlCall,Collection<GraphTransition>> parentOutMap;
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

    /** Flag to indicate if confluent diamonds should be considered. */
    private final boolean checkDiamonds;
}
