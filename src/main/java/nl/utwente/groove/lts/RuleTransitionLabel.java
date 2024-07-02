/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.lts;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.CtrlArg;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.ALabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.transform.Record;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;

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
        return getStep().getInnerSwitch();
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
        List<? extends CtrlArg> callArgs = getStep().getInnerCall().getArgs();
        if (callArgs.isEmpty()) {
            result = EMPTY_NODE_ARRAY;
        } else {
            result = new HostNode[callArgs.size()];
            var addedNodes = getAddedNodes();
            var anchorImages = getEvent().getAnchorImages();
            for (int i = 0; i < callArgs.size(); i++) {
                var bind = getAction().getParBinding(i);
                result[i] = switch (bind.type()) {
                case ANCHOR -> (HostNode) anchorImages[bind.index()];
                case CREATOR -> addedNodes == null
                    ? null
                    : addedNodes[bind.index()];
                default -> throw Exceptions.illegalState("Binding %s is of a wrong type", bind);
                };
            }
        }
        return result;
    }

    @Override
    protected Line computeLine() {
        Line result = Line.atom(text(false));
        if (getRole() == EdgeRole.FLAG) {
            result = result.style(Style.ITALIC);
            if (getAction().getRole().hasColor()) {
                result = result.color(getAction().getRole().getColor());
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
        for (var swt : getStep().getSwitch()) {
            if (swt.getKind() == Kind.RULE) {
                result.append(getAction().toLabelString(getArguments()));
            } else {
                result.append(swt.getQualName());
                result.append('/');
            }
        }
        if (anchored) {
            result.append(getEvent().getAnchorImageString());
        }
        return result.toString();
    }

    @Override
    public EdgeRole getRole() {
        if (getAction().getRole().isProperty() && !getStep().isModifying()) {
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
            throw Exceptions.illegalArg("Can't compare %s and %s", this.getClass(), obj.getClass());
        }
        int result = super.compareTo(obj);
        if (result != 0) {
            return result;
        }
        // Rule transitions are always smaller than recipe transitions
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
                                                                 MatchResult match,
                                                                 HostNode[] addedNodes) {
        @NonNull
        RuleTransitionLabel result = new RuleTransitionLabel(source, match, addedNodes);
        if (REUSE_LABELS) {
            Record record = source.getGTS().getRecord();
            RuleTransitionLabel newResult = record.normaliseLabel(result);
            result = newResult;
        }
        return result;
    }

    /** Flag controlling whether transition labels are normalised. */
    public static boolean REUSE_LABELS = true;
    /** Global empty set of nodes. */
    static private final HostNode[] EMPTY_NODE_ARRAY = {};
}
