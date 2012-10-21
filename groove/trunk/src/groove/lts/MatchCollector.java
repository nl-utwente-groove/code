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
package groove.lts;

import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.graph.algebra.ValueNode;
import groove.trans.Event;
import groove.trans.AnchorValue;
import groove.trans.CompositeEvent;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleToHostMap;
import groove.trans.SystemRecord;
import groove.util.KeySet;
import groove.util.Visitor;

import java.util.List;
import java.util.Set;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     */
    public MatchCollector(GraphState state) {
        this.state = state;
        this.ctrlState = state.getCtrlState();
        assert this.ctrlState != null;
        this.record = state.getGTS().getRecord();
        boolean checkDiamonds = state.getGTS().checkDiamonds();
        GraphState parent = null;
        if (state instanceof GraphNextState) {
            parent = ((GraphNextState) state).source();
        }
        if (parent != null && parent.isClosed() && checkDiamonds) {
            this.parentTransMap = parent.getCache().getTransitionMap();
            Rule lastRule = ((GraphNextState) state).getEvent().getRule();
            this.enabledRules = this.record.getEnabledRules(lastRule);
            this.disabledRules = this.record.getDisabledRules(lastRule);
        } else {
            this.parentTransMap = null;
            this.enabledRules = null;
            this.disabledRules = null;
        }
    }

    /**
     * Returns the set of matching events for a given control transition.
     * @param ct the transition for which matches are to be found; non-{@code null}
     */
    public MatchResultSet computeMatches(CtrlTransition ct) {
        final MatchResultSet result = new MatchResultSet();
        if (DEBUG) {
            System.out.printf("Matches for %s, %s%n  ", this.state,
                this.state.getGraph());
        }
        assert ct != null;
        // there are three reasons to want to use the parent matches: to
        // save matching time, to reuse added nodes, and to find confluent 
        // diamonds. The first is only relevant if the rule is not (re)enabled,
        // the third only if the parent match target is already closed
        final boolean isDisabled = isDisabled(ct.getCall());
        if (!isDisabled) {
            for (GraphTransition trans : this.parentTransMap) {
                if (trans instanceof RuleTransition) {
                    RuleTransition ruleTrans = (RuleTransition) trans;
                    if (ruleTrans.getEvent().getRule().equals(ct.getRule())) {
                        result.add(ruleTrans);
                        if (DEBUG) {
                            System.out.print(" T"
                                + System.identityHashCode(trans.getEvent()));
                        }
                    }
                }
            }
        }
        if (isDisabled || isEnabled(ct.getCall())) {
            // the rule was possibly enabled afresh, so we have to add the fresh
            // matches
            RuleToHostMap boundMap = extractBinding(ct);
            if (boundMap != null) {
                final SystemRecord record = this.record;
                Visitor<Proof,Boolean> eventCollector =
                    new Visitor<Proof,Boolean>(false) {
                        @Override
                        protected boolean process(Proof object) {
                            RuleEvent event = record.getEvent(object);
                            // only look up the event in the parent map if
                            // the rule was disabled, as otherwise the result
                            // already contains all relevant parent results
                            MatchResult match =
                                isDisabled ? getParentTrans(event) : event;
                            result.add(match);
                            if (DEBUG) {
                                System.out.print(" E"
                                    + System.identityHashCode(match.getEvent()));
                                checkEvent(match.getEvent());
                            }
                            setResult(true);
                            return true;
                        }
                    };
                ct.getRule().traverseMatches(this.state.getGraph(), boundMap,
                    eventCollector);
            }
        }
        if (DEBUG) {
            System.out.println();
        }
        return result;
    }

    /** Tests if all anchor images in a given event actually occur in the graph. */
    private void checkEvent(RuleEvent event) {
        if (event instanceof CompositeEvent) {
            for (RuleEvent subEvent : ((CompositeEvent) event).getEventSet()) {
                checkEvent(subEvent);
            }
        } else {
            for (int i = 0; i < event.getRule().getAnchor().size(); i++) {
                AnchorValue anchorImage = event.getAnchorImage(i);
                HostGraph host = MatchCollector.this.state.getGraph();
                switch (anchorImage.getAnchorKind()) {
                case EDGE:
                    if (!host.containsEdge((HostEdge) anchorImage)) {
                        assert false : String.format(
                            "Edge %s does not occur in graph %s", anchorImage,
                            host);
                    }
                    break;
                case NODE:
                    if (!(anchorImage instanceof ValueNode)
                        && !host.containsNode((HostNode) anchorImage)) {
                        assert false : String.format(
                            "Node %s does not occur in graph %s", anchorImage,
                            host);
                    }
                }
            }
        }
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
        Set<CtrlCall> triedCalls =
            state.source().getSchedule().getPreviousCalls();
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
    private MatchResult getParentTrans(RuleEvent event) {
        MatchResult result = null;
        if (this.parentTransMap != null) {
            result = (RuleTransition) this.parentTransMap.get(event);
        }
        if (result == null) {
            result = event;
        }
        return result;
    }

    /** The host graph we are working on. */
    private final GraphState state;
    /** The control state of the graph state, if any. */
    private final CtrlState ctrlState;
    /** The system record is set at construction. */
    private final SystemRecord record;
    /** Possibly {@code null} mapping from rules to sets of outgoing
     * transitions for the parent of this state.
     */
    private final KeySet<Event,GraphTransition> parentTransMap;
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

    /** Debug flag for the match collector. */
    private final static boolean DEBUG = false;
}
