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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Event class for recipe transitions. */
public class RecipeEvent implements GraphTransitionStub, Event {
    /** Constructs a stub from a sequence of rule transitions. */
    public RecipeEvent(RecipeTransition trans) {
        List<RuleTransitionStub> steps = new ArrayList<RuleTransitionStub>();
        this.recipe = trans.getAction();
        this.steps = steps.toArray(new RuleTransitionStub[steps.size()]);
    }

    @Override
    public Recipe getAction() {
        return this.recipe;
    }

    @Override
    public GraphState getTarget(GraphState source) {
        return toTransition(source).target();
    }

    @Override
    public RecipeTransition toTransition(GraphState source) {
        List<RuleTransition> steps = new ArrayList<RuleTransition>();
        GraphState intermediate = source;
        for (int i = 0; i < this.steps.length; i++) {
            RuleTransitionStub stub = this.steps[i];
            RuleTransition step = stub.toTransition(intermediate);
            steps.add(step);
            intermediate = step.target();
        }
        return new RecipeTransition(source, this.recipe, steps, intermediate);
    }

    @Override
    public String toString() {
        return "RecipeTransitionStub [recipe=" + this.recipe + ", steps="
            + Arrays.toString(this.steps) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.recipe.hashCode();
        result = prime * result + Arrays.hashCode(this.steps);
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
        if (!Arrays.equals(this.steps, other.steps)) {
            return false;
        }
        return true;
    }

    /** Source state of the rule transition. */
    private final Recipe recipe;
    /** Sequence of rule transitions leading to this recipe transition. */
    private final RuleTransitionStub[] steps;
}
