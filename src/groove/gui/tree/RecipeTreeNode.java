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

import groove.grammar.Recipe;
import groove.gui.Icons;
import groove.io.HTMLConverter;

import javax.swing.Icon;

/**
 * Transaction nodes (= level 1 nodes) of the directory
 */
class RecipeTreeNode extends DisplayTreeNode implements ActionTreeNode {
    /**
     * Creates a new transaction node based on a given control automaton.
     */
    public RecipeTreeNode(Recipe recipe) {
        super(recipe, true);
    }

    /**
     * Returns the control automaton of the transaction wrapped in this node.
     */
    public Recipe getRecipe() {
        return (Recipe) getUserObject();
    }

    @Override
    public Icon getIcon() {
        return Icons.RECIPE_LIST_ICON;
    }

    @Override
    public String getName() {
        return getRecipe().getFullName();
    }

    @Override
    public boolean isError() {
        return getRecipe().getTemplate() == null;
    }

    @Override
    public String toString() {
        return getRecipe().getLastName() + " (recipe)";
    }

    /** Indicates if the rule wrapped by this node has been tried on the current state. */
    @Override
    public boolean isEnabled() {
        boolean result = false;
        int count = getChildCount();
        for (int i = 0; !result && i < count; i++) {
            RuleTreeNode child = (RuleTreeNode) getChildAt(i);
            result = child.isEnabled();
        }
        return result;
    }

    @Override
    public String getTip() {
        StringBuilder result = new StringBuilder();
        result.append("Recipe ");
        result.append(HTMLConverter.STRONG_TAG.on(getName()));
        if (!isEnabled()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not active in this state");
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }
}