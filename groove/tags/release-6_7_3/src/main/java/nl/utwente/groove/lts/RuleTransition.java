// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 *
 */
package nl.utwente.groove.lts;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.CtrlArg;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Exceptions;

/**
 *
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface RuleTransition extends GraphTransition, RuleTransitionStub {
    @Override
    default @NonNull Rule getAction() {
        return getEvent().getRule();
    }

    /* Overrides the method to specialise the result type. */
    @Override
    GraphState source();

    /* Overrides the method to specialise the result type. */
    @Override
    GraphState target();

    /* Overrides the method to specialise the result type. */
    @Override
    RuleTransitionLabel label();

    @Override
    public default String text(boolean anchored) {
        return label().text(anchored);
    }

    @Override
    default public EdgeRole getRole() {
        if (getAction().getRole() == Role.TRANSFORMER || getStep().isModifying()) {
            return EdgeRole.BINARY;
        } else {
            return EdgeRole.FLAG;
        }
    }

    @Override
    public RuleEvent getEvent();

    /** Callback method to construct a rule application from this
     * graph transition.
     */
    public RuleApplication createRuleApplication();

    /** Returns the control step associated with this transition. */
    @Override
    Step getStep();

    @Override
    public default Switch getSwitch() {
        return getStep().getInnerSwitch();
    }

    @Override
    public default boolean isPartial() {
        return getStep().isPartial();
    }

    @Override
    public default boolean isInternalStep() {
        return getStep().isInternal();
    }

    @Override
    public default boolean isRealStep() {
        return !isInternalStep() && source().isRealState() && target().isRealState();
    }

    @Override
    public default RuleTransition getInitial() {
        return this;
    }

    @Override
    public default MatchResult getKey() {
        return new MatchResult(this);
    }

    @Override
    public default HostNode[] getArguments() {
        HostNode[] result;
        List<? extends CtrlArg> args = getSwitch().getArgs();
        if (args.isEmpty()) {
            result = EMPTY_ARGS;
        } else {
            var anchorImages = getEvent().getAnchorImages();
            result = new HostNode[args.size()];
            for (int i = 0; i < args.size(); i++) {
                var bind = getAction().getParBinding(i);
                result[i] = switch (bind.type()) {
                case ANCHOR -> (HostNode) anchorImages[bind.index()];
                case CREATOR -> getAddedNodes()[bind.index()];
                default -> throw Exceptions.UNREACHABLE;
                };
            }
        }
        return result;
    }

    /**
     * Returns the nodes added by this transition, in coanchor order.
     */
    public HostNode[] getAddedNodes();

    /**
     * Returns the proof of the matching of the LHS into the source graph.
     */
    public default Proof getProof() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * Returns the (partial) morphism from the source to the target graph.
     */
    @Override
    public HostGraphMorphism getMorphism();

    /**
     * Indicates if the transition involves a non-trivial symmetry. This is the
     * case if and only if there is a non-trivial isomorphism from the directly
     * derived target of the event applied to the source, to the actual (stored)
     * target.
     * @return <code>true</code> if the transition involves a non-trivial
     *         symmetry
     * @see #getMorphism()
     */
    @Override
    public boolean isSymmetry();

    /**
     * Converts this transition to a more memory-efficient representation, from
     * which the original transition can be retrieved by
     * {@link GraphTransitionStub#toTransition(GraphState)}.
     */
    @Override
    public RuleTransitionStub toStub();

    /** Static empty list of rule transition arguments. */
    public static final HostNode[] EMPTY_ARGS = new HostNode[0];
}