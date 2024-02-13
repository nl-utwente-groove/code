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
package nl.utwente.groove.gui.tree;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.lts.RuleTransitionLabel;

/**
 * Tree node wrapping a graph transition.
 */
@NonNullByDefault
class RuleMatchTreeNode extends MatchTreeNode {
    /**
     * Creates a new tree node based on a given graph transition.
     */
    public RuleMatchTreeNode(SimulatorModel simulator, GraphState source, MatchResult match, int nr,
                             boolean anchored) {
        super(simulator, source, nr);
        this.match = match;
        this.anchored = anchored;
    }

    /**
     * Returns the match result of this node.
     */
    MatchResult getMatch() {
        return this.match;
    }

    private final MatchResult match;

    @Override
    public boolean isInRecipe() {
        return getMatch().getStep().isInternal();
    }

    /** Indicates if this match corresponds to a transition from the source state. */
    private boolean isTransition() {
        return getMatch().hasTransitionFrom(getSource());
    }

    /** Indicates if this is a match of a property. */
    private boolean isProperty() {
        return getMatch().getAction().isProperty();
    }

    @Override
    Status getStatus() {
        return isTransition()
            ? Status.ACTIVE
            : Status.STANDBY;
    }

    @Override
    public String getTip() {
        StringBuilder result = new StringBuilder();
        QualName actionName = getMatch().getAction().getQualName();
        if (isProperty()) {
            result.append(String.format("Property '%s' is satisfied", actionName));
        } else if (isTransition()) {
            result.append(String.format("Explored transition of '%s'", actionName));
            GraphState target = getMatch().getTransition().target();
            if (target.isAbsent()) {
                result.append(HTMLConverter.HTML_LINEBREAK);
                result.append(String.format("Target state %s is not real", target));
            }
        } else {
            result.append(String.format("Currently unexplored match of '%s'", actionName));
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Doubleclick to apply");
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
    }

    @Override
    String computeText() {
        StringBuilder result = new StringBuilder();
        result.append(getNumber());
        result.append(": ");
        boolean showArrow
            = !getMatch().getAction().isProperty() || getMatch().getStep().isModifying();
        if (isTransition()) {
            RuleTransition trans = getMatch().getTransition();
            result.append(trans.text(this.anchored));
            if (showArrow) {
                result.append(RIGHTARROW);
                result.append(HTMLConverter.ITALIC_TAG.on(trans.target().toString()));
            }
            if (getSimulator().getTrace().contains(trans)) {
                result.append(TRACE_SUFFIX);
            }
            if (trans.target().isAbsent()) {
                HTMLConverter.STRIKETHROUGH_TAG.on(result);
            }
        } else {
            result.append(RuleTransitionLabel.text(getSource(), getMatch(), this.anchored));
            if (showArrow) {
                result.append(RIGHTARROW);
                result.append("?");
            }
        }
        return result.toString();
    }

    private final boolean anchored;
    /** The suffix for a match that is in the selected trace. */
    private static final String TRACE_SUFFIX = " " + HTMLConverter.STRONG_TAG.on("(*)");
}