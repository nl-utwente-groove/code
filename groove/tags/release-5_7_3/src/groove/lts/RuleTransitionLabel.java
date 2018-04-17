/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.lts;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import groove.control.Binding;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Wild;
import groove.control.instance.Step;
import groove.control.template.Switch;
import groove.grammar.GrammarKey;
import groove.grammar.Rule;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.graph.ALabel;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.transform.Record;
import groove.transform.RuleEvent;
import groove.util.ThreeValued;
import groove.util.line.Line;
import groove.util.line.Line.Style;

/** Class of labels that can appear on rule transitions. */
public class RuleTransitionLabel extends ALabel implements ActionLabel {
    /**
     * Constructs a new label on the basis of a given match and list
     * of created nodes.
     * @param source the source graph state of the transition
     * @param match the rule match on which the transition is based
     * @param addedNodes the nodes added by the transition; possibly {@code null} if
     * the added nodes are not specified
     */
    private RuleTransitionLabel(GraphState source, MatchResult match, HostNode[] addedNodes) {
        this.event = match.getEvent();
        this.step = match.getStep();
        this.addedNodes = addedNodes;
    }

    @Override
    public Rule getAction() {
        return this.event.getRule();
    }

    /** Returns the event wrapped in this label. */
    public RuleEvent getEvent() {
        return this.event;
    }

    private final RuleEvent event;

    /** Returns the control step wrapped in this label. */
    public Step getStep() {
        return this.step;
    }

    private final Step step;

    @Override
    public Switch getSwitch() {
        return getStep().getRuleSwitch();
    }

    /** Returns the nodes added by the transition to the target state.
     * @return the added nodes, or {@code null} if the added nodes are not specified
     */
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    private final HostNode[] addedNodes;

    @Override
    public HostNode[] getArguments() {
        HostNode[] result;
        List<? extends CtrlPar> callArgs = getStep().getRuleCall()
            .getArgs();
        if (callArgs.isEmpty()) {
            result = EMPTY_NODE_ARRAY;
        } else {
            result = new HostNode[callArgs.size()];
            HostNode[] added = getAddedNodes();
            for (int i = 0; i < callArgs.size(); i++) {
                HostNode arg;
                Binding binding = getAction().getParBinding(i);
                switch (binding.getSource()) {
                case ANCHOR:
                    arg = (HostNode) getEvent().getAnchorImage(binding.getIndex());
                    break;
                case CREATOR:
                    arg = added == null ? null : added[binding.getIndex()];
                    break;
                default:
                    assert false;
                    arg = null;
                }
                result[i] = arg;
            }
        }
        return result;
    }

    @Override
    protected Line computeLine() {
        Line result = Line.atom(text(false));
        if (getRole() == EdgeRole.FLAG) {
            result = result.style(Style.ITALIC);
            if (getAction().getRole()
                .hasColor()) {
                result = result.color(getAction().getRole()
                    .getColor());
            }
        }
        return result;
    }

    /** Returns the label text, with optionally the rule parameters
     * replaced by anchor images.
     * @param anchored if {@code true}, the anchor images are used
     * instead of the rule parameters
     */
    public String text(boolean anchored) {
        StringBuilder result = new StringBuilder();
        for (int i = getStep().getSource()
            .getSwitchStack()
            .size(); i < getStep().getSwitchStack()
                .size() - 1; i++) {
            Switch sw = getStep().getSwitchStack()
                .get(i);
            result.append(sw.getQualName());
            result.append('/');
        }
        result.append(getAction().getTransitionLabel());
        if (anchored) {
            result.append(getEvent().getAnchorImageString());
        } else {
            result.append(computeParameters(this));
        }
        return result.toString();
    }

    @Override
    public EdgeRole getRole() {
        if (!getAction().isModifying() && !getStep().isModifying()) {
            return EdgeRole.FLAG;
        }
        return super.getRole();
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.addedNodes);
        result = prime * result + this.event.hashCode();
        result = prime * result + this.step.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleTransitionLabel other = (RuleTransitionLabel) obj;
        if (!Arrays.equals(this.addedNodes, other.addedNodes)) {
            return false;
        }
        if (!this.event.equals(other.event)) {
            return false;
        }
        if (!this.step.equals(other.step)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof ActionLabel)) {
            throw new IllegalArgumentException(
                String.format("Can't compare %s and %s", this.getClass(), obj.getClass()));
        }
        int result = super.compareTo(obj);
        if (result != 0) {
            return result;
        }
        if (obj instanceof RecipeTransition) {
            return -1;
        }
        RuleTransitionLabel other = (RuleTransitionLabel) obj;
        result = getStep().compareTo(other.getStep());
        if (result != 0) {
            return result;
        }
        result = getEvent().compareTo(other.getEvent());
        return result;
    }

    static StringBuilder computeParameters(ActionLabel label) {
        StringBuilder result = new StringBuilder();
        ThreeValued useParameters = label.getAction()
            .getGrammarProperties()
            .isUseParameters();
        // also show parameters for properties, unless global property is FALSE
        if (useParameters.isSome() && label.getAction()
            .isProperty()
            && !label.getAction()
                .getSignature()
                .isEmpty()) {
            useParameters = ThreeValued.TRUE;
        }
        if (!useParameters.isFalse()) {
            List<? extends CtrlPar> args = label.getSwitch()
                .getCall()
                .getArgs();
            // test if there is a showable argument
            boolean showArgs = false;
            StringBuilder params = new StringBuilder();
            params.append('(');
            boolean first = true;
            for (int i = 0; i < args.size(); i++) {
                HostNode arg = label.getArguments()[i];
                if (!first) {
                    params.append(',');
                }
                first = false;
                if (arg == null) {
                    params.append('_');
                } else if (arg instanceof ValueNode) {
                    params.append(((ValueNode) arg).getTerm()
                        .toDisplayString());
                } else {
                    params.append(arg);
                }
                showArgs |= SHOW_NONEMPTY_ARGS || !(args.get(i) instanceof Wild);
            }
            params.append(')');
            if (showArgs || useParameters.isTrue()) {
                result.append(params);
            }
        }
        return result;
    }

    /**
     * Returns the label text for the rule label based on a given source state
     * and event. Optionally, the rule parameters are replaced by anchor images.
     */
    public static final String text(GraphState source, MatchResult match, boolean anchored) {
        return createLabel(source, match, null).text(anchored);
    }

    /**
     * Creates a normalised rule label.
     * @see Record#normaliseLabel(RuleTransitionLabel)
     * @param source the source graph state of the transition
     * @param match the rule match on which the transition is based
     * @param addedNodes the nodes added by the transition; possibly {@code null} if
     * the added nodes are not specified
     */
    public static final @NonNull RuleTransitionLabel createLabel(GraphState source,
        MatchResult match, HostNode[] addedNodes) {
        @NonNull RuleTransitionLabel result = new RuleTransitionLabel(source, match, addedNodes);
        if (REUSE_LABELS) {
            Record record = source.getGTS()
                .getRecord();
            RuleTransitionLabel newResult = record.normaliseLabel(result);
            result = newResult;
        }
        return result;
    }

    /** Flag controlling whether transition labels are normalised. */
    public static boolean REUSE_LABELS = true;
    /** Global empty set of nodes. */
    static private final HostNode[] EMPTY_NODE_ARRAY = new HostNode[0];
    /** Flag controlling the behaviour in case the {@link GrammarKey#TRANSITION_PARAMETERS}
     * key is set to {@link ThreeValued#SOME}.
     * If this flag is set to <code>true</code>, then parameters are shown for
     * all rules that have any; otherwise, only if there is a control program that
     * specifies at least one non-wildcard argument.
     */
    static private final boolean SHOW_NONEMPTY_ARGS = true;
}
