/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.tree;

import groove.gui.Icons;
import groove.gui.SimulatorModel;
import groove.io.HTMLConverter;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.lts.RuleTransitionLabel;

import javax.swing.Icon;

/**
 * Tree node wrapping a graph transition.
 */
class MatchTreeNode extends DisplayTreeNode {
    /**
     * Creates a new tree node based on a given graph transition. The node cannot have
     * children.
     */
    public MatchTreeNode(SimulatorModel model, GraphState source, MatchResult match, int nr,
            boolean anchored) {
        super(match, false);
        this.source = source;
        this.nr = nr;
        this.model = model;
        this.anchored = anchored;
    }

    /** Indicates if this match corresponds to a transition from the source state. */
    private boolean isTransition() {
        return getMatch().hasTransitionFrom(this.source);
    }

    @Override
    public boolean inRecipe() {
        return getMatch().getStep().inRecipe();
    }

    /**
     * Convenience method to retrieve the user object as a graph transition.
     */
    public MatchResult getMatch() {
        return (MatchResult) getUserObject();
    }

    /**
     * Returns the graph state for which this is a match.
     */
    public GraphState getSource() {
        return this.source;
    }

    @Override
    public Icon getIcon() {
        return Icons.GRAPH_MATCH_ICON;
    }

    @Override
    public boolean isEnabled() {
        return isTransition();
    }

    @Override
    public String getTip() {
        if (isEnabled()) {
            return super.getTip();
        } else {
            return "Doubleclick to apply match";
        }
    }

    @Override
    public String getText() {
        if (this.label == null) {
            this.label = computeText();
        }
        return this.label;
    }

    private String computeText() {
        StringBuilder result = new StringBuilder();
        result.append(this.nr);
        result.append(": ");
        if (isTransition()) {
            RuleTransition trans = getMatch().getTransition();
            result.append(trans.text(this.anchored));
            result.append(RIGHTARROW);
            result.append(HTMLConverter.ITALIC_TAG.on(trans.target().toString()));
            if (this.model.getTrace().contains(trans)) {
                result.append(TRACE_SUFFIX);
            }
            if (trans.target().isAbsent()) {
                HTMLConverter.STRIKETHROUGH_TAG.on(result);
            }
        } else {
            result.append(RuleTransitionLabel.text(this.source, getMatch(), this.anchored));
            result.append(RIGHTARROW);
            result.append("?");
        }
        return result.toString();
    }

    private final SimulatorModel model;
    private final GraphState source;
    private final int nr;
    private final boolean anchored;
    private String label;
    /** HTML representation of the right arrow. */
    private static final String RIGHTARROW = "-->";
    /** The suffix for a match that is in the selected trace. */
    private static final String TRACE_SUFFIX = " " + HTMLConverter.STRONG_TAG.on("(*)");
}