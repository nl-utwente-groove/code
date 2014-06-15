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
import groove.lts.RecipeEvent;
import groove.lts.RecipeTransition;

import javax.swing.Icon;

/**
 * Tree node wrapping a recipe transition.
 */
class RecipeTransitionTreeNode extends DisplayTreeNode {
    /**
     * Creates a new tree node based on a given recipe transition. The node cannot have
     * children.
     * @param source source state of the recipe transition 
     */
    public RecipeTransitionTreeNode(SimulatorModel model, GraphState source, RecipeEvent event,
            int nr) {
        super(event.toTransition(source), false);
        this.nr = nr;
        this.model = model;
    }

    /**
     * Convenience method to retrieve the user object as a recipe event.
     */
    public RecipeTransition getTransition() {
        return (RecipeTransition) getUserObject();
    }

    /**
     * Returns the graph state for which this is a match.
     */
    public GraphState getSource() {
        return getTransition().source();
    }

    @Override
    public Icon getIcon() {
        return Icons.GRAPH_MATCH_ICON;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getText() {
        if (this.text == null) {
            this.text = computeText();
        }
        return this.text;
    }

    private String text;

    private String computeText() {
        StringBuilder result = new StringBuilder();
        result.append(this.nr);
        result.append(": ");
        RecipeTransition trans = getTransition();
        result.append(trans.text());
        result.append(RIGHTARROW);
        result.append(HTMLConverter.ITALIC_TAG.on(trans.target().toString()));
        if (this.model.getTrace().contains(trans)) {
            result.append(TRACE_SUFFIX);
        }
        return result.toString();
    }

    private final SimulatorModel model;
    private final int nr;
    /** HTML representation of the right arrow. */
    private static final String RIGHTARROW = "-->";
    /** The suffix for a match that is in the selected trace. */
    private static final String TRACE_SUFFIX = " " + HTMLConverter.STRONG_TAG.on("(*)");
}