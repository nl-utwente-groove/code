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
import groove.grammar.host.HostNode;
import groove.graph.ALabel;
import groove.graph.Label;
import groove.gui.look.Line;

/** Class of labels that can appear on recipe transitions. */
public class RecipeTransitionLabel extends ALabel implements ActionLabel {
    /**
     * Constructs a new label on the basis of a given initial rule transition.
     */
    public RecipeTransitionLabel(RuleTransition initial) {
        this.recipe = initial.getStep().getRecipe();
        this.initial = initial;
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    /** Returns the initial rule transition of the recipe transition. */
    public RuleTransition getInitial() {
        return this.initial;
    }

    @Override
    protected Line computeLine() {
        return Line.atom(text(false));
    }

    @Override
    public HostNode[] getArguments() {
        throw new UnsupportedOperationException();
    }

    /** Returns the label text, with optionally the rule parameters
     * replaced by anchor images.
     * @param anchored if {@code true}, the anchor images are used
     * instead of the rule parameters
     */
    public String text(boolean anchored) {
        StringBuilder result = new StringBuilder();
        result.append(getAction().getFullName());
        //        if (getInitial().source().getGTS().getGrammar().getProperties().isUseParameters()) {
        //            result.append('(');
        //            boolean first = true;
        //            for (HostNode arg : getArguments(addedNodes)) {
        //                if (!first) {
        //                    result.append(',');
        //                }
        //                first = false;
        //                if (arg == null) {
        //                    result.append('_');
        //                } else if (arg instanceof ValueNode) {
        //                    result.append(((ValueNode) arg).getTerm().toDisplayString());
        //                } else {
        //                    result.append(arg);
        //                }
        //            }
        //            result.append(')');
        //        }
        return result.toString();
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
            throw new IllegalArgumentException(String.format("Can't compare %s and %s",
                this.getClass(), obj.getClass()));
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

    private final Recipe recipe;
    private final RuleTransition initial;
}
