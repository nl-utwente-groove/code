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
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.lts.RuleTransition;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleToHostMap;
import groove.trans.SystemRecord;
import groove.util.Visitor;

import java.util.Collection;
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
     * @param record factory to turn {@link Proof}s in to
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
            this.enabledRules = null;
            this.disabledRules = null;
        }
    }

    /** Returns a single match for the state passed in through the constructor. */
    public MatchResult getMatch() {
        MatchResult result = null;
        for (CtrlTransition ctrlTrans = firstCall(); result == null
            && ctrlTrans != null; ctrlTrans = nextCall(false)) {
            result = getMatch(ctrlTrans);
        }
        return result;
    }

    /** Returns a match result for a given control transition. */
    protected MatchResult getMatch(CtrlTransition ctrlTrans) {
        RuleToHostMap boundMap = extractBinding(ctrlTrans);
        MatchResult result = null;
        if (boundMap != null) {
            Proof match =
                ctrlTrans.getRule().getMatch(this.state.getGraph(), boundMap);
            if (match != null) {
                // convert the match to an event
                result = this.record.getEvent(match);
            }
        }
        return result;
    }

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<MatchResult> getMatchSet() {
        MatchResultSet result = new MatchResultSet();
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
            final Collection<MatchResult> result) {
        boolean hasMatched = false;
        // there are three reasons to want to use the parent matches: to
        // save matching time, to reuse added nodes, and to find confluent 
        // diamonds. The first is only relevant if the rule is not (re)enabled,
        // the third only if the parent match target is already closed
        final boolean isDisabled = isDisabled(ctrlTrans.getCall());
        if (!isDisabled) {
            for (RuleTransition trans : this.parentOutMap.values()) {
                if (trans.getEvent().getRule().equals(ctrlTrans.getRule())) {
                    result.add(trans);
                    hasMatched = true;
                }
            }
        }
        if (isDisabled || isEnabled(ctrlTrans.getCall())) {
            // the rule was possibly enabled afresh, so we have to add the fresh
            // matches
            RuleToHostMap boundMap = extractBinding(ctrlTrans);
            if (boundMap != null) {
                final SystemRecord record = MatchSetCollector.this.record;
                Visitor<Proof,Boolean> eventCollector =
                    new Visitor<Proof,Boolean>(false) {
                        @Override
                        protected boolean process(Proof object) {
                            RuleEvent event = record.getEvent(object);
                            // only look up the event in the parent map if
                            // the rule was disabled, as otherwise the result
                            // already contains all relevant parent results
                            result.add(isDisabled ? getParentOut(event) : event);
                            setResult(true);
                            return true;
                        }
                    };
                hasMatched =
                    ctrlTrans.getRule().traverseMatches(this.state.getGraph(),
                        boundMap, eventCollector);
            }
        }
        return hasMatched;
    }

    /**
     * Indicates if new matches of a given control call might have been enabled
     * with respect to the parent state.
     */
    private boolean isEnabled(CtrlCall call) {
        if (this.enabledRules == null
            || this.enabledRules.contains(call.getRule())) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getCtrlTransition().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule call was untried in
        // the parent state
        Set<CtrlCall> triedCalls = state.source().getSchedule().getTriedCalls();
        return triedCalls == null || !triedCalls.contains(call);
    }

    /** 
     * Indicates if matches of a given control call might have been disabled
     * since the parent state.
     */
    private boolean isDisabled(CtrlCall call) {
        if (this.disabledRules == null
            || this.disabledRules.contains(call.getRule())) {
            return true;
        }
        // since disabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getCtrlTransition().isModifying()) {
            return true;
        }
        return false;
    }

    /** Indicates if match results are compared with the parent state. */
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
                    CtrlPar.Const constArg = (CtrlPar.Const) arg;
                    image =
                        this.state.getGraph().getFactory().createValueNode(
                            constArg.getAlgebra(), constArg.getValue());
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
     * Returns the parent state's out-transition for a given event, if any,
     * or otherwise the event itself.
     */
    private MatchResult getParentOut(RuleEvent event) {
        MatchResult result = null;
        if (this.parentOutMap != null) {
            result = this.parentOutMap.get(event);
        }
        if (result == null) {
            result = event;
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
    private final Map<RuleEvent,RuleTransition> parentOutMap;
    /** The rules that may be enabled. */
    private final Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private final Set<Rule> disabledRules;

    /** Returns the total number of reused parent events. */
    public static int getEventReuse() {
        return parentOutReuse;
    }

    /** Counter for the number of reused parent events. */
    private static int parentOutReuse;

    /** Flag to indicate if confluent diamonds should be considered. */
    private final boolean checkDiamonds;
}
