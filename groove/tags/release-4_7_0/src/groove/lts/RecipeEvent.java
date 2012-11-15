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
package groove.lts;

import groove.trans.Event;
import groove.trans.Recipe;

/** Event class for recipe transitions. */
public class RecipeEvent implements GraphTransitionStub, Event,
        GraphTransitionKey {
    /** Constructs a stub from a sequence of rule transitions. */
    public RecipeEvent(RecipeTransition trans) {
        this.recipe = trans.getAction();
        this.initial = trans.getInitial().toStub();
        this.target = trans.target();
    }

    /**
     * Constructs an event for a given recipe, initial transition and target state.
     */
    public RecipeEvent(Recipe recipe, RuleTransition initial, GraphState target) {
        this.recipe = recipe;
        this.initial = initial.toStub();
        this.target = target;
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    @Override
    public GraphState getTarget(GraphState source) {
        assert this.initial.getTarget(source) != null;
        return this.target;
    }

    @Override
    public GraphTransitionKey getKey(GraphState source) {
        return this;
    }

    @Override
    public RecipeTransition toTransition(GraphState source) {
        return new RecipeTransition(source, this.initial.toTransition(source),
            this.target);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.recipe.hashCode();
        result = prime * result + this.initial.hashCode();
        result = prime * result + this.target.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RecipeEvent)) {
            return false;
        }
        RecipeEvent other = (RecipeEvent) obj;
        if (!this.recipe.equals(other.recipe)) {
            return false;
        }
        if (!this.initial.equals(other.initial)) {
            return false;
        }
        if (!this.target.equals(other.target)) {
            return false;
        }
        return true;
    }

    /** Source state of the rule transition. */
    private final Recipe recipe;
    /** Initial rule transition of the event. */
    private final RuleTransitionStub initial;
    /** Target state of the event. */
    private final GraphState target;
}
