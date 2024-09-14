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
package nl.utwente.groove.lts;

import java.util.Arrays;

import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.HostNodeComparator;
import nl.utwente.groove.transform.Event;

/** Event class for recipe transitions. */
public class RecipeEvent
    implements GraphTransitionStub, Event, GraphTransitionKey, Comparable<RecipeEvent> {
    /** Constructs an instance from a recipe transition. */
    public RecipeEvent(RecipeTransition trans) {
        this.recipe = (Recipe) trans.getSwitch().getUnit();
        this.initial = trans.getLaunch().toStub();
        this.target = trans.target();
        this.arguments = trans.getArguments();
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    private final Recipe recipe;

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
        return new RecipeTransition(source, this);
    }

    /** Returns the initial transition for this event. */
    public RuleTransitionStub getInitial() {
        return this.initial;
    }

    /** Initial rule transition of the event. */
    private final RuleTransitionStub initial;

    /**
     * Returns the arguments of the transition.
     */
    public HostNode[] getArguments() {
        return this.arguments;
    }

    /** The arguments of the transition. */
    private final HostNode[] arguments;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.initial.hashCode();
        result = prime * result + this.target.hashCode();
        result = prime * result + Arrays.hashCode(this.arguments);
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
        if (!(obj instanceof RecipeEvent other)) {
            return false;
        }
        if (!this.initial.equals(other.initial)) {
            return false;
        }
        if (!this.target.equals(other.target)) {
            return false;
        }
        if (!Arrays.equals(this.arguments, other.arguments)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RecipeEvent [target=" + this.target + ", initial=" + this.initial + ", arguments="
            + Arrays.toString(this.arguments) + "]";
    }

    @Override
    public int compareTo(RecipeEvent o) {
        int result = getAction().compareTo(o.getAction());
        if (result != 0) {
            return result;
        }
        result = getInitial().getEvent().compareTo(o.getInitial().getEvent());
        if (result != 0) {
            return result;
        }
        var comparator = HostNodeComparator.instance();
        var args = this.arguments;
        var oArgs = o.arguments;
        for (int i = 0; i < args.length && result == 0; i++) {
            result = comparator.compare(args[i], oArgs[i]);
        }
        return result;
    }

}
