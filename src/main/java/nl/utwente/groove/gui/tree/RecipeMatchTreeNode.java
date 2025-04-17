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
 * $Id: RecipeTransitionTreeNode.java 6377 2024-02-10 13:14:16Z rensink $
 */
package nl.utwente.groove.gui.tree;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;

/**
 * Tree node wrapping a potential recipe transition.
 */
class RecipeMatchTreeNode extends MatchTreeNode {
    /**
     * Creates a new tree node based on a given initial rule match from a given state.
     * @param initMatch rule match on this this recipe match is based
     * @param nr transition number
     */
    public RecipeMatchTreeNode(SimulatorModel model, GraphState source, MatchResult initMatch,
                               int nr, boolean anchored) {
        super(model, source, nr);
        this.initMatch = initMatch;
        this.anchored = anchored;
    }

    @Override
    MatchResult getKey() {
        return getInitMatch();
    }

    /**
     * Convenience method to retrieve the user object as the initial transition of this recipe.
     */
    @Override
    MatchResult getInitMatch() {
        return this.initMatch;
    }

    private final MatchResult initMatch;

    /**
     * Convenience method to retrieve the user object as a recipe event.
     */
    @Override
    @NonNull
    Recipe getRecipe() {
        return getInitMatch().getStep().getRecipe().get();
    }

    @Override
    Status getStatus() {
        return getSource().isFull()
            ? Status.ACTIVE
            : Status.STANDBY;
    }

    @Override
    String computeText() {
        StringBuilder result = new StringBuilder();
        result.append(getNumber());
        result.append(": ");
        var args = getRecipeArgs(getSimulator(), getSource(), getInitMatch().getStep());
        result.append(getRecipe().toLabelString(args, true));
        if (this.anchored) {
            result.append(getKey().getEvent().getAnchorImageString());
        }
        result.append(RIGHTARROW);
        result.append('?');
        return result.toString();
    }

    @Override
    String getTip() {
        StringBuilder result = new StringBuilder();
        result
            .append(getStatus() == Status.STANDBY
                ? "Potential transition"
                : "Transition");
        result.append(" of recipe " + getRecipe().getLastName());
        if (getStatus() == Status.STANDBY) {
            result.append(" (as yet not fully explored)");
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    private final boolean anchored;

    @Override
    public String toString() {
        return "Recipe transition match of " + getRecipe() + ", initial match " + getInitMatch();
    }
}