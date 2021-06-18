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

import groove.control.template.Switch;
import groove.grammar.Callable.Kind;
import groove.grammar.Recipe;
import groove.transform.Event;

/** Event class for recipe transitions. */
public class RecipeEvent implements GraphTransitionStub, Event, GraphTransitionKey {
    /** Constructs an instance from a recipe transition. */
    public RecipeEvent(RecipeTransition trans) {
        this.recipeSwitch = trans.getSwitch();
        assert this.recipeSwitch.getKind() == Kind.RECIPE;
        this.initial = trans.getInitial()
            .toStub();
        this.target = trans.target();
    }

    /**
     * Constructs an event for a given recipe, initial transition and target state.
     */
    public RecipeEvent(Switch recipeSwitch, RuleTransition initial, GraphState target) {
        this.recipeSwitch = recipeSwitch;
        this.initial = initial.toStub();
        this.target = target;
    }

    @Override
    public Recipe getAction() {
        return (Recipe) this.recipeSwitch.getUnit();
    }

    @Override
    public RecipeEvent getEvent() {
        return this;
    }

    @Override
    public GraphTransitionKey getKey(GraphState source) {
        return this;
    }

    @Override
    public GraphState getTarget(GraphState source) {
        assert this.initial.getTarget(source) != null;
        return this.target;
    }

    /** Returns the target state of this event. */
    public GraphState getTarget() {
        return this.target;
    }

    /** Target state of the event. */
    private final GraphState target;

    @Override
    public RecipeTransition toTransition(GraphState source) {
        return new RecipeTransition(source, this.target, this.initial.toTransition(source));
    }

    /** Returns the switch corresponding to the invocation wrapped in this event. */
    public Switch getSwitch() {
        return this.recipeSwitch;
    }

    /** Source state of the rule transition. */
    private final Switch recipeSwitch;

    /** Returns the initial transition for this event. */
    public RuleTransitionStub getInitial() {
        return this.initial;
    }

    /** Initial rule transition of the event. */
    private final RuleTransitionStub initial;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.recipeSwitch.hashCode();
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
        if (!this.recipeSwitch.equals(other.recipeSwitch)) {
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

    @Override
    public String toString() {
        return "RecipeEvent [target=" + this.target + ", recipeSwitch=" + this.recipeSwitch
            + ", initial=" + this.initial + "]";
    }
}
