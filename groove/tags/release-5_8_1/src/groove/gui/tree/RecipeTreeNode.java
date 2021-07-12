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

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.gui.Icons;
import groove.io.HTMLConverter;

/**
 * Recipe nodes of the directory
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
    public boolean isProperty() {
        return getRecipe().isProperty();
    }

    @Override
    public Icon getIcon() {
        return Icons.RECIPE_TREE_ICON;
    }

    @Override
    public QualName getQualName() {
        return getRecipe().getQualName();
    }

    @Override
    public boolean isError() {
        return getRecipe().getTemplate() == null;
    }

    @Override
    public String getText() {
        return getRecipe().getLastName() + RECIPE_SUFFIX;
    }

    /** Indicates if the rule wrapped by this node has been tried on the current state. */
    @Override
    public boolean isEnabled() {
        boolean result = false;
        int count = getChildCount();
        for (int i = 0; !result && i < count; i++) {
            TreeNode child = getChildAt(i);
            if (child instanceof RecipeTransitionTreeNode) {
                result = true;
            } else if (child instanceof RuleTreeNode) {
                result = ((RuleTreeNode) child).isEnabled();
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    public String getTip() {
        StringBuilder result = new StringBuilder();
        result.append("Recipe ");
        result.append(HTMLConverter.ITALIC_TAG.on(getQualName()));
        if (!isEnabled()) {
            result.append(HTMLConverter.HTML_LINEBREAK);
            result.append("Not scheduled in this state");
        }
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns the number of child {@link RecipeTransitionTreeNode} nodes. */
    public int getTransitionCount() {
        int result = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RecipeTransitionTreeNode) {
                result++;
            }
        }
        return result;
    }

    private final static String RECIPE_SUFFIX = " : " + HTMLConverter.STRONG_TAG.on("recipe");
}