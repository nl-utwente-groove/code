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

import javax.swing.Icon;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.io.HTMLConverter;

/**
 * Recipe nodes of the directory
 */
@NonNullByDefault
class RecipeTreeNode extends ActionTreeNode {
    /**
     * Creates a new transaction node based on a given control automaton.
     */
    RecipeTreeNode(ResourceDisplay display, Recipe recipe) {
        super(display, recipe.getQualName());
        this.recipe = recipe;
    }

    /**
     * Returns the recipe wrapped in this node.
     */
    Recipe getRecipe() {
        return this.recipe;
    }

    private final Recipe recipe;

    @Override
    Icon getIcon() {
        return Icons.RECIPE_TREE_ICON;
    }

    @Override
    boolean isError() {
        return getRecipe().getTemplate() == null;
    }

    @Override
    String getText() {
        String suffix = "";
        Signature<?> sig = getRecipe().getSignature();
        if (!sig.isEmpty()) {
            suffix = sig.toString();
        }
        suffix += RECIPE_SUFFIX;
        return getRecipe().getLastName() + suffix;
    }

    @Override
    String getTip() {
        StringBuilder result = new StringBuilder();
        result.append("Recipe ");
        result.append(HTMLConverter.ITALIC_TAG.on(getQualName()));
        if (getStatus() == Status.STANDBY) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not scheduled in this state");
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    private final static String RECIPE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("recipe");
}