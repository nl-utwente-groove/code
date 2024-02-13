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

import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.RecipeEvent;
import nl.utwente.groove.lts.RecipeTransition;

/**
 * Tree node wrapping a recipe transition.
 */
class RecipeTransitionTreeNode extends MatchTreeNode {
    /**
     * Creates a new tree node based on a given recipe transition. The node cannot have
     * children.
     * @param source source state of the recipe transition
     */
    RecipeTransitionTreeNode(SimulatorModel model, GraphState source, RecipeEvent event, int nr) {
        super(model, source, nr);
        this.trans = event.toTransition(source);
    }

    /**
     * Convenience method to retrieve the user object as a recipe event.
     */
    RecipeTransition getTransition() {
        return this.trans;
    }

    private final RecipeTransition trans;

    @Override
    Status getStatus() {
        return Status.ACTIVE;
    }

    @Override
    String computeText() {
        StringBuilder result = new StringBuilder();
        result.append(getNumber());
        result.append(": ");
        RecipeTransition trans = getTransition();
        result.append(trans.text());
        result.append(RIGHTARROW);
        result.append(HTMLConverter.ITALIC_TAG.on(trans.target().toString()));
        if (getSimulator().getTrace().contains(trans)) {
            result.append(TRACE_SUFFIX);
        }
        return result.toString();
    }

    /** The suffix for a match that is in the selected trace. */
    private static final String TRACE_SUFFIX = " " + HTMLConverter.STRONG_TAG.on("(*)");
}