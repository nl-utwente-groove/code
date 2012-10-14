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

import groove.graph.AbstractLabel;
import groove.trans.Recipe;

/** Class of labels that can appear on rule transitions. */
public class RecipeTransitionLabel extends AbstractLabel implements ActionLabel {
    /** 
     * Constructs a new label on the basis of a given rule event and list
     * of created nodes.
     */
    public RecipeTransitionLabel(Recipe recipe, Iterable<RuleTransition> steps) {
        this.recipe = recipe;
        this.steps = steps;
        this.text = getLabelText();
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    /** Returns the list of rule transitions comprising this label. */
    public Iterable<RuleTransition> getSteps() {
        return this.steps;
    }

    /** Constructs the label text. */
    private String getLabelText() {
        return this.recipe.getFullName();
    }

    @Override
    public String text() {
        return this.text;
    }

    private final String text;
    private final Recipe recipe;
    private final Iterable<RuleTransition> steps;
}
