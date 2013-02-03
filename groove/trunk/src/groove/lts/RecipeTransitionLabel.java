/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.lts;

import groove.grammar.Recipe;
import groove.graph.AbstractLabel;
import groove.graph.Label;

/** Class of labels that can appear on rule transitions. */
public class RecipeTransitionLabel extends AbstractLabel implements ActionLabel {
    /** 
     * Constructs a new label on the basis of a given recipe and initial rule transition.
     * The subsequent rule transitions are optionally passed in; if {@code null},
     * they can be computed.
     */
    public RecipeTransitionLabel(RuleTransition initial) {
        this.recipe = initial.getRecipe();
        this.initial = initial;
        this.text = getLabelText();
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    /** Returns the initial rule transition of the recipe transition. */
    public RuleTransition getInitial() {
        return this.initial;
    }

    /** Constructs the label text. */
    private String getLabelText() {
        return this.recipe.getFullName();
    }

    @Override
    public String text() {
        return this.text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RecipeTransitionLabel)) {
            return false;
        }
        RecipeTransitionLabel other = (RecipeTransitionLabel) obj;
        if (!this.recipe.equals(other.recipe)) {
            return false;
        }
        if (!this.initial.equals(other.initial)) {
            return false;
        }
        return true;
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = this.recipe.hashCode();
        result = prime * result + this.initial.hashCode();
        return result;
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof ActionLabel)) {
            throw new IllegalArgumentException(String.format(
                "Can't compare %s and %s", this.getClass(), obj.getClass()));
        }
        if (obj instanceof RuleTransitionLabel) {
            return -obj.compareTo(this);
        }
        int result = super.compareTo(obj);
        if (result != 0) {
            return result;
        }
        RecipeTransitionLabel other = (RecipeTransitionLabel) obj;
        result = this.recipe.compareTo(other.recipe);
        if (result != 0) {
            return result;
        }
        return getInitial().label().compareTo(other.getInitial().label());
    }

    private final String text;
    private final Recipe recipe;
    private final RuleTransition initial;
}
