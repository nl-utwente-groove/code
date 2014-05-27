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

import groove.control.Binding;
import groove.control.Call;
import groove.control.CtrlPar.Var;
import groove.control.CtrlStep;
import groove.control.Valuator;
import groove.control.instance.Assignment;
import groove.control.instance.Frame;
import groove.control.instance.Step;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleToHostMap;
import groove.transform.CompositeEvent;
import groove.transform.Proof;
import groove.transform.Record;
import groove.transform.RuleEvent;
import groove.util.Pair;
import groove.util.Visitor;

import java.util.List;
import java.util.Set;

/**
 * Algorithm to create the set of current match results for a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StepMatchCollector extends MatchCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     */
    public StepMatchCollector(GraphState state) {
        super(state);
    }

    /**
     * Returns the set of matching events for a given control switch.
     * @param ctrlStep the transition for which matches are to be found; non-{@code null}
     */
    @Override
    public MatchResultSet computeMatches(CtrlStep ctrlStep) {
        final Step step = (Step) ctrlStep;
        final MatchResultSet result = new MatchResultSet();
        if (DEBUG) {
            System.out.printf("Matches for %s, %s%n  ", this.state, this.state.getGraph());
        }
        assert step != null;
        // there are three reasons to want to use the parent matches: to
        // save matching time, to reuse added nodes, and to find confluent 
        // diamonds. The first is only relevant if the rule is not (re)enabled,
        // the third only if the parent match target is already closed
        final boolean isDisabled = isDisabled(step.getRuleCall());
        boolean isModifying = step.isModifying();
        if (!isDisabled) {
            for (GraphTransition trans : this.parentTransMap) {
                if (trans instanceof RuleTransition) {
                    RuleTransition ruleTrans = (RuleTransition) trans;
                    if (ruleTrans.getEvent().getRule().equals(step.getRule())) {
                        MatchResult match = ruleTrans.getKey();
                        if (isModifying) {
                            // we can reuse the event but not the control step
                            match = new MatchResult(match.getEvent(), step);
                        }
                        result.add(match);
                        if (DEBUG) {
                            System.out.print(" T" + System.identityHashCode(trans.getEvent()));
                        }
                    }
                }
            }
        }
        if (isDisabled || isEnabled(step.getRuleCall())) {
            // the rule was possibly enabled afresh, so we have to add the fresh
            // matches
            RuleToHostMap boundMap = extractBinding(step);
            if (boundMap != null) {
                final Record record = this.record;
                Visitor<Proof,Boolean> eventCollector = new Visitor<Proof,Boolean>(false) {
                    @Override
                    protected boolean process(Proof object) {
                        RuleEvent event = record.getEvent(object);
                        // only look up the event in the parent map if
                        // the rule was disabled, as otherwise the result
                        // already contains all relevant parent results
                        MatchResult match = new MatchResult(event, step);
                        if (isDisabled) {
                            match = getParentTrans(match);
                        }
                        result.add(match);
                        if (DEBUG) {
                            System.out.print(" E" + System.identityHashCode(match.getEvent()));
                            checkEvent(match.getEvent());
                        }
                        setResult(true);
                        return true;
                    }
                };
                step.getRule().traverseMatches(this.state.getGraph(), boundMap, eventCollector);
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
                HostGraph host = StepMatchCollector.this.state.getGraph();
                switch (anchorImage.getAnchorKind()) {
                case EDGE:
                    if (!host.containsEdge((HostEdge) anchorImage)) {
                        assert false : String.format("Edge %s does not occur in graph %s",
                            anchorImage, host);
                    }
                    break;
                case NODE:
                    if (!(anchorImage instanceof ValueNode)
                        && !host.containsNode((HostNode) anchorImage)) {
                        assert false : String.format("Node %s does not occur in graph %s",
                            anchorImage, host);
                    }
                }
            }
        }
    }

    /**
     * Indicates if new matches of a given control call might have been enabled
     * with respect to the parent state.
     */
    private boolean isEnabled(Call call) {
        if (this.enabledRules == null || this.enabledRules.contains(call.getRule())) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getStep().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule call was untried in
        // the parent state
        Set<Call> triedCalls = ((Frame) state.source().getActualFrame()).getPastCalls();
        return triedCalls == null || !triedCalls.contains(call);
    }

    /** 
     * Indicates if matches of a given control call might have been disabled
     * since the parent state.
     */
    private boolean isDisabled(Call call) {
        if (this.disabledRules == null || this.disabledRules.contains(call.getRule())) {
            return true;
        }
        // since disabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getStep().isModifying()) {
            return true;
        }
        return false;
        //        for (Pair<Var,Binding> inArg : swit.getCallBinding()) {
        //            if (inArg.two() == null || inArg.two().getSource() == Source.CONST) {
        //                continue;
        //            }
        //            // node values may be deleted 
        //            if (inArg.one().getType() == CtrlType.NODE && swit.getRule().hasNodeErasers()) {
        //                return true;
        //            }
        //            // the incoming control step may have affected the variable's value
        //            if (((Step) state.getStep()).mayAssign(inArg.two().getIndex())) {
        //                return true;
        //            }
        //        }
        //        return false;
    }

    /** Extracts the morphism from rule nodes to input graph nodes
     * corresponding to the transition's input parameters.
     * @return if {@code null}, the binding cannot be constructed and
     * so the rule cannot match
     */
    private RuleToHostMap extractBinding(Step step) {
        RuleToHostMap result = this.state.getGraph().getFactory().createRuleToHostMap();
        Object[] sourceValues = this.state.getFrameValues();
        for (Assignment action : computePops()) {
            sourceValues = action.apply(sourceValues);
        }
        for (Assignment action : step.getFramePushes()) {
            sourceValues = action.apply(sourceValues);
        }
        loop: for (Pair<Var,Binding> entry : step.getRuleSwitch().getCallBinding()) {
            Binding bind = entry.two();
            HostNode value;
            if (bind == null) {
                // this corresponds to an output parameter of the call
                continue;
            }
            switch (bind.getSource()) {
            case CONST:
                value = bind.getValue().getNode();
                break;
            case VAR:
                value = Valuator.get(sourceValues, bind);
                // test if the value is not deleted by a previous rule
                if (value == null) {
                    result = null;
                    break loop;
                }
                break;
            default:
                assert false;
                value = null;
            }
            result.putNode(entry.one().getRuleNode(), value);
        }
        return result;
    }

    /**
     * Computes the pop actions triggered by the procedures
     * exited between the prime and actual control frame.
     */
    private List<Assignment> computePops() {
        return Assignment.computePops((Frame) this.state.getPrimeFrame(),
            (Frame) this.state.getActualFrame());
    }

    /** 
     * Returns the parent state's out-transition for a given event, if any,
     * or otherwise the event itself.
     */
    private MatchResult getParentTrans(MatchResult key) {
        MatchResult result;
        if (this.parentTransMap != null) {
            RuleTransition trans = (RuleTransition) this.parentTransMap.get(key);
            result = trans == null ? key : trans.getKey();
        } else {
            result = key;
        }
        return result;
    }

    /** Returns the total number of reused parent events. */
    public static int getEventReuse() {
        return parentOutReuse;
    }

    /** Counter for the number of reused parent events. */
    private static int parentOutReuse;

    /** Debug flag for the match collector. */
    private final static boolean DEBUG = false;
}
