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
import nl.utwente.groove.lts.GraphNextState;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RuleTransition;

/**
 * Tree node wrapping a potential recipe transition.
 */
class RecipeOngoingTreeNode extends MatchTreeNode {
    /**
     * Creates a new tree node based on a given recipe transition. The node cannot have
     * children.
     * @param source source state of the recipe transition
     */
    public RecipeOngoingTreeNode(SimulatorModel model, GraphState source, MatchResult innerMatch,
                                 int nr) {
        super(model, source, nr);
        this.innerMatch = innerMatch;
    }

    @Override
    MatchResult getKey() {
        return getInnerMatch();
    }

    MatchResult getInnerMatch() {
        return this.innerMatch;
    }

    private final MatchResult innerMatch;

    /**
     * Convenience method to retrieve the user object as a recipe event.
     */
    @Override
    @NonNull
    Recipe getRecipe() {
        return getSource().getActualFrame().getRecipe().get();
    }

    @Override
    MatchResult getInitMatch() {
        return getInitStep().getKey();
    }

    /** Returns initial source state of the ongoing recipe transition. */
    private GraphState getInitSource() {
        return getInitStep().source();
    }

    /** Returns the initial step of the ongoing recipe transition. */
    private RuleTransition getInitStep() {
        var result = (GraphNextState) getSource();
        while (!result.source().isRealState()) {
            result = (GraphNextState) result.source();
        }
        return result;
    }

    @Override
    Status getStatus() {
        return getSource().isDone()
            ? Status.ACTIVE
            : Status.STANDBY;
    }

    @Override
    String computeText() {
        StringBuilder result = new StringBuilder();
        var realSource = getInitSource();
        result.append(HTMLConverter.ITALIC_TAG.on(realSource.toString()));
        result.append(ARROW_TAIL);
        result.append(getRecipe().getTransitionLabel());
        var args = getRecipeArgs(getSimulator(), getInitSource(), getInitStep().getStep());
        result.append(toArgsString(args));
        result.append(RIGHTARROW);
        result.append('?');
        return result.toString();
    }

    @Override
    String getTip() {
        StringBuilder result = new StringBuilder();
        result.append("Ongoing transition of recipe " + getRecipe().getLastName());
        if (!getSource().isDone()) {
            result.append(" (as yet not fully explored)");
        }
        result.append(",");
        result.append(HTMLConverter.HTML_LINEBREAK);
        result.append("starting in predecessor state ");
        var initSource = getInitSource();
        result.append(HTMLConverter.ITALIC_TAG.on(initSource.toString()));
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    @Override
    public String toString() {
        return "Ongoing recipe transition of " + getRecipe() + ", initial match " + getInitMatch();
    }
}