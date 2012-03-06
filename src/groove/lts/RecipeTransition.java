// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: DefaultGraphTransition.java,v 1.19 2008-03-05 16:50:10 rensink Exp $
 */
package groove.lts;

import groove.graph.AbstractEdge;
import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.trans.HostGraphMorphism;
import groove.trans.Recipe;
import groove.trans.RuleApplication;

import java.util.Iterator;

/**
 * Models a transition corresponding to the complete execution of
 * a recipe. This comprises a sequence of ordinary graph transitions.
 * @author Arend Rensink
 * @version $Revision: 3638 $ $Date: 2008-03-05 16:50:10 $
 */
public class RecipeTransition extends AbstractEdge<GraphState,RecipeTransitionLabel>
        implements GraphTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public RecipeTransition(GraphState source, Recipe recipe,
            Iterable<RuleTransition> steps, GraphState target) {
        super(source, new RecipeTransitionLabel(recipe, steps), target);
    }

    @Override
    public Recipe getAction() {
        return label().getAction();
    }

    @Override
    public boolean isPartial() {
        return false;
    }

    /** Returns the list of rule transitions comprising this recipe transition. */
    public Iterable<RuleTransition> getSteps() {
        return label().getSteps();
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>target()</code>.
     */
    public GraphState getTarget(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return target();
        }
    }

    /**
     * This implementation reconstructs the rule application from the stored
     * footprint, and appends an isomorphism to the actual target if necessary.
     */
    public HostGraphMorphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism();
        }
        return this.morphism;
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected HostGraphMorphism computeMorphism() {
        HostGraphMorphism result = null;
        for (RuleTransition step : getSteps()) {
            RuleApplication appl =
                step.getEvent().newApplication(source().getGraph());
            result =
                result == null ? appl.getMorphism()
                        : result.then(appl.getMorphism());
        }
        return result;
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
     */
    public RecipeTransition toTransition(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return this;
        }
    }

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsSource(RuleTransition other) {
        return source() == other.source();
    }

    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof RecipeTransition;
    }

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof GraphTransition) {
            GraphTransition other = (GraphTransition) obj;
            int result = source().compareTo(other.source());
            if (result == 0) {
                result =
                    getAction().getFullName().compareTo(
                        other.getAction().getFullName());
            }
            if (result == 0) {
                // the other must be a transition for the same recipe;
                // compare the steps lexicographically
                Iterator<RuleTransition> mySteps = getSteps().iterator();
                Iterator<RuleTransition> hisSteps =
                    ((RecipeTransition) other).getSteps().iterator();
                while (result == 0 && mySteps.hasNext() && hisSteps.hasNext()) {
                    result = mySteps.next().compareTo(hisSteps.next());
                }
                if (result == 0) {
                    result =
                        mySteps.hasNext() ? -1 : hisSteps.hasNext() ? +1 : 0;
                }
            }
            if (result == 0) {
                result = target().compareTo(other.target());
            }
            return result;
        } else {
            assert obj instanceof GraphState : String.format(
                "Can't compare graph transition %s to element %s", this, obj);
            int result = source().compareTo(obj);
            if (result == 0) {
                result = +1;
            }
            return result;
        }
    }

    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private HostGraphMorphism morphism;
}