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
import groove.control.Binding.Source;
import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.instance.Step;
import groove.grammar.Rule;
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
import groove.util.Visitor;
import groove.util.collect.KeySet;

import java.util.List;
import java.util.Set;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NewMatchCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     */
    public NewMatchCollector(GraphState state) {
        this.state = state;
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
     * Returns the set of matching events for a given control switch.
     * @param ct the transition for which matches are to be found; non-{@code null}
     */
    public MatchResultSet computeMatches(final Step ct) {
        final MatchResultSet result = new MatchResultSet();
        if (DEBUG) {
            System.out.printf("Matches for %s, %s%n  ", this.state, this.state.getGraph());
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
                    if (ruleTrans.getEvent().getRule().equals(ct.getCall().getUnit())) {
                        result.add(ruleTrans.getKey());
                        if (DEBUG) {
                            System.out.print(" T" + System.identityHashCode(trans.getEvent()));
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
                final Record record = this.record;
                Visitor<Proof,Boolean> eventCollector = new Visitor<Proof,Boolean>(false) {
                    @Override
                    protected boolean process(Proof object) {
                        RuleEvent event = record.getEvent(object);
                        // only look up the event in the parent map if
                        // the rule was disabled, as otherwise the result
                        // already contains all relevant parent results
                        MatchResult match = null;
                        if (isDisabled) {
                            match = getParentTrans(event, ct);
                        }
                        if (match == null) {
                            match = new MatchResult(event, ct);
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
                ((Rule) ct.getCall().getUnit()).traverseMatches(this.state.getGraph(), boundMap,
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
                HostGraph host = NewMatchCollector.this.state.getGraph();
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
        if (this.enabledRules == null || this.enabledRules.contains(call.getUnit())) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getStep().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule call was untried in
        // the parent state
        Set<Call> triedCalls = state.source().getCurrentFrame().getPastCalls();
        return triedCalls == null || !triedCalls.contains(call);
    }

    /** 
     * Indicates if matches of a given control call might have been disabled
     * since the parent state.
     */
    private boolean isDisabled(Call call) {
        assert call.getUnit() instanceof Rule;
        if (this.disabledRules == null || this.disabledRules.contains(call.getUnit())) {
            return true;
        }
        // since disabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getStep().isModifying()) {
            return true;
        }
        return false;
    }

    /** Extracts the morphism from rule nodes to input graph nodes
     * corresponding to the transition's input parameters.
     * @return if {@code null}, the binding cannot be constructed and
     * so the rule cannot match
     */
    private RuleToHostMap extractBinding(Step step) {
        RuleToHostMap result = this.state.getGraph().getFactory().createRuleToHostMap();
        List<? extends CtrlPar> args = step.getCall().getArgs();
        if (args != null && args.size() > 0) {
            Binding[] parBind = step.getSwitch().getTargetBinding();
            List<CtrlPar.Var> ruleSig = step.getCall().getUnit().getSignature();
            HostNode[] boundNodes = this.state.getBoundNodes();
            for (int i = 0; i < args.size(); i++) {
                CtrlPar arg = args.get(i);
                HostNode image = null;
                if (arg instanceof CtrlPar.Const) {
                    CtrlPar.Const constArg = (CtrlPar.Const) arg;
                    image =
                        this.state.getGraph().getFactory().createNode(constArg.getAlgebra(),
                            constArg.getValue());
                    assert image != null : String.format(
                        "Constant argument %s not initialised properly", arg);
                } else if (arg.isInOnly()) {
                    assert parBind[i].getType() == Source.VAR;
                    image = boundNodes[parBind[i].getIndex()];
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
    private MatchResult getParentTrans(RuleEvent event, Step ct) {
        MatchResult result = null;
        if (this.parentTransMap != null) {
            RuleTransition trans =
                (RuleTransition) this.parentTransMap.get(new MatchResult(event, ct));
            return trans == null ? null : trans.getKey();
        }
        return result;
    }

    /** The host graph we are working on. */
    private final GraphState state;
    /** The system record is set at construction. */
    private final Record record;
    /** Possibly {@code null} mapping from rules to sets of outgoing
     * transitions for the parent of this state.
     */
    private final KeySet<GraphTransitionKey,GraphTransition> parentTransMap;
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
