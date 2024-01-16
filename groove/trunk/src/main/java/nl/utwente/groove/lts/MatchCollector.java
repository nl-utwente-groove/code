/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.lts;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.AnchorValue;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.MatchChecker;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.transform.CompositeEvent;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.transform.Record;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Visitor;
import nl.utwente.groove.util.collect.KeySet;

/**
 * Algorithm to create the set of current match results for a given state.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MatchCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     */
    public MatchCollector(GraphState state) {
        this.state = state;
        this.record = state.getRecord();
        if (state instanceof GraphNextState ns) {
            GraphState parent = ns.source();
            this.parentClosed = parent.isClosed();
            this.parentTransMap = parent.getCache().getTransitionMap();
            Rule lastRule = ns.getEvent().getRule();
            this.enabledRules = this.record.getEnabledRules(lastRule);
            this.disabledRules = this.record.getDisabledRules(lastRule);
        } else {
            this.parentClosed = false;
            this.parentTransMap = null;
            this.enabledRules = null;
            this.disabledRules = null;
        }
    }

    /**
     * Returns the set of matching events for a given control step.
     * @param step the control step for which matches are to be found; non-{@code null}
     */
    public MatchResultSet computeMatches(final Step step) {
        final MatchResultSet result = new MatchResultSet();
        if (DEBUG) {
            System.out.printf("Matches for %s, %s%n  ", this.state, this.state.getGraph());
        }
        assert step != null;
        // there are three reasons to want to use the parent matches: to
        // save matching time, to reuse added nodes, and to find confluent
        // diamonds. The first is only relevant if the rule is not (re)enabled,
        // the third only if the parent match target is already closed
        final boolean isDisabled = isDisabled(step.getInnerCall());
        boolean isModifying = step.isModifying();
        if (!isDisabled) {
            for (GraphTransition trans : this.parentTransMap) {
                if (trans instanceof RuleTransition ruleTrans) {
                    if (ruleTrans.getAction().equals(step.getRule())) {
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
        if (isDisabled || isEnabled(step.getInnerCall())) {
            // the rule was possibly enabled afresh, so we have to add the fresh
            // matches
            RuleToHostMap boundMap = extractBinding(step);
            if (boundMap != null) {
                final Record record = this.record;
                Optional<MatchChecker> matchFilter = step.getRule().getMatchFilter();
                Visitor<Proof,Boolean> eventCollector = new Visitor<>(false) {
                    @Override
                    protected boolean process(Proof proof) {
                        RuleEvent event = record.getEvent(proof);
                        boolean filtered = false;
                        GraphState state = MatchCollector.this.state;
                        HostGraph host = state.getGraph();
                        if (matchFilter.isPresent()) {
                            try {
                                filtered = matchFilter.get().invoke(host, event.getAnchorMap());
                            } catch (InvocationTargetException exc) {
                                state
                                    .getGTS()
                                    .addError("Error at state %s while applying match filter %s: %s",
                                              state, matchFilter.get().getQualName(),
                                              exc.getCause());
                            }
                        }
                        if (!filtered) {
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
                        }
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
        if (event instanceof CompositeEvent ce) {
            ce.getEventSet().forEach(this::checkEvent);
        } else {
            var anchorImages = event.getAnchorImages();
            for (int i = 0; i < anchorImages.length; i++) {
                AnchorValue anchorImage = anchorImages[i];
                HostGraph host = MatchCollector.this.state.getGraph();
                switch (anchorImage.getAnchorKind()) {
                case EDGE:
                    if (!host.containsEdge((HostEdge) anchorImage)) {
                        assert false : String
                            .format("Edge %s does not occur in graph %s", anchorImage, host);
                    }
                    break;
                case NODE:
                    if (!(anchorImage instanceof ValueNode)
                        && !host.containsNode((HostNode) anchorImage)) {
                        assert false : String
                            .format("Node %s does not occur in graph %s", anchorImage, host);
                    }
                    break;
                default:
                    // nothing to be checked
                }
            }
        }
    }

    /**
     * Indicates if new matches of a given control call might have been enabled
     * with respect to the parent state.
     */
    private boolean isEnabled(Call call) {
        if (this.enabledRules == null || !this.parentClosed
            || this.enabledRules.contains(call.getRule())) {
            return true;
        }
        // since enabledRules != null, it is now certain that this is a NextState
        GraphNextState state = (GraphNextState) this.state;
        if (state.getStep().isModifying()) {
            return true;
        }
        // there may be new matches only if the rule call was untried in
        // the parent state
        var triedCalls = state.source().getActualFrame().getPastCalls();
        return !triedCalls.contains(call);
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
    }

    /** Extracts the morphism from rule nodes to input graph nodes
     * corresponding to the transition's input parameters.
     * @return if {@code null}, the binding cannot be constructed and
     * so the rule cannot match
     */
    private @Nullable RuleToHostMap extractBinding(Step step) {
        RuleToHostMap result = this.state.getGraph().getFactory().createRuleToHostMap();
        var valuator = this.state.getGTS().getRecord().getValuator();
        Object[] stack = this.state.getActualStack();
        valuator.setVarInfo(stack);
        for (var bind : step.getParAssign()) {
            if (bind.type() == Source.NONE) {
                // this corresponds to an output or wildcard parameter
                continue;
            }
            var value = valuator.eval(bind);
            RuleNode ruleNode = ((RulePar) bind.par()).getNode();
            if (isCompatible(ruleNode, value)) {
                result.putNode(ruleNode, value);
            } else {
                result = null;
                break;
            }
        }
        return result;
    }

    /** Tests if a given host node can match a given rule node. */
    private boolean isCompatible(RuleNode ruleNode, HostNode hostNode) {
        if (hostNode == null) {
            return false;
        }
        if (!ruleNode.getType().subsumes(hostNode.getType(), ruleNode.isSharp())) {
            return false;
        }
        if (ruleNode instanceof VariableNode && ((VariableNode) ruleNode).hasConstant()) {
            Constant constant = ((VariableNode) ruleNode).getConstant();
            Object value = this.record.getFamily().toValue(constant);
            if (!value.equals(((ValueNode) hostNode).getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the parent state's out-transition for a given event, if any,
     * or otherwise the event itself.
     */
    private MatchResult getParentTrans(MatchResult key) {
        MatchResult result;
        if (this.record.checkDiamonds() && this.parentTransMap != null) {
            RuleTransition trans = (RuleTransition) this.parentTransMap.get(key);
            result = trans == null
                ? key
                : trans.getKey();
        } else {
            result = key;
        }
        return result;
    }

    /** The host graph we are working on. */
    protected final GraphState state;
    /**
     * Flag indicating that the parent state is closed.
     * This means that all outgoing transitions have been added.
     */
    protected final boolean parentClosed;
    /** The system record is set at construction. */
    protected final Record record;
    /** Possibly {@code null} mapping from rules to sets of outgoing
     * transitions for the parent of this state.
     */
    protected final KeySet<GraphTransitionKey,GraphTransition> parentTransMap;
    /** The rules that may be enabled. */
    protected final Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    protected final Set<Rule> disabledRules;

    /** Returns the total number of reused parent events. */
    public static int getEventReuse() {
        return parentOutReuse;
    }

    /** Counter for the number of reused parent events. */
    private static int parentOutReuse;

    /** Debug flag for the match collector. */
    private final static boolean DEBUG = false;
}
