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

import groove.grammar.Recipe;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.graph.AEdge;
import groove.graph.EdgeRole;
import groove.transform.RuleApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Models a transition corresponding to the complete execution of
 * a recipe. This comprises a sequence of ordinary graph transitions.
 * @author Arend Rensink
 * @version $Revision: 3638 $ $Date: 2008-03-05 16:50:10 $
 */
public class RecipeTransition extends
        AEdge<GraphState,RecipeTransitionLabel> implements
        GraphTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public RecipeTransition(GraphState source, RuleTransition initial,
            GraphState target) {
        super(source, new RecipeTransitionLabel(initial), target);
    }

    @Override
    public String text(boolean anchored) {
        return label().text();
    }

    @Override
    public Recipe getAction() {
        return label().getAction();
    }

    @Override
    public RecipeEvent getEvent() {
        if (this.event == null) {
            this.event = new RecipeEvent(this);
        }
        return this.event;
    }

    @Override
    public boolean isPartial() {
        return false;
    }

    /** Returns the initial rule transition of the recipe transition. */
    public RuleTransition getInitial() {
        return label().getInitial();
    }

    /** Returns the collection of rule transitions comprising this label. */
    public Set<RuleTransition> getSteps() {
        Set<RuleTransition> result = this.steps;
        if (result == null) {
            result = computeSteps();
            if (source().isDone()) {
                this.steps = result;
            }
        }
        return result;
    }

    /** Returns a shortest rule transition sequence from source to target. */
    public List<RuleTransition> getPath() {
        List<RuleTransition> result = null;
        // all paths of the current length
        List<List<RuleTransition>> paths =
            new ArrayList<List<RuleTransition>>();
        paths.add(Arrays.asList(getInitial()));
        // do the following for paths of increasing length
        while (result == null) {
            List<List<RuleTransition>> newPaths =
                new ArrayList<List<RuleTransition>>();
            for (List<RuleTransition> path : paths) {
                GraphState target = path.get(path.size() - 1).target();
                // check if any of the paths reaches the target
                if (target == target()) {
                    result = path;
                    break;
                } else {
                    // otherwise, extend the path in all possible ways
                    for (RuleTransition next : target.getRuleTransitions()) {
                        if (getSteps().contains(next)) {
                            List<RuleTransition> newPath =
                                new ArrayList<RuleTransition>(path);
                            newPath.add(next);
                            newPaths.add(newPath);
                        }
                    }
                }
            }
            paths = newPaths;
        }
        return result;
    }

    private Set<RuleTransition> computeSteps() {
        // mapping from states to sets of incoming transitions
        Map<GraphState,Set<RuleTransition>> inMap =
            new HashMap<GraphState,Set<RuleTransition>>();
        // build the incoming transition map
        Stack<GraphState> pool = new Stack<GraphState>();
        pool.add(getInitial().target());
        while (!pool.isEmpty()) {
            GraphState next = pool.pop();
            for (RuleTransition trans : next.getRuleTransitions()) {
                GraphState target = trans.target();
                if (target.isTransient() || target == target()) {
                    Set<RuleTransition> inSet = inMap.get(target);
                    boolean fresh = inSet == null;
                    if (fresh) {
                        inMap.put(target, inSet = new HashSet<RuleTransition>());
                    }
                    inSet.add(trans);
                    if (fresh && target != target()) {
                        pool.add(target);
                    }
                }
            }
        }
        assert getInitial().target().equals(target())
            || inMap.containsKey(target());
        // backward reachability to build up the result set
        Set<RuleTransition> result = new HashSet<RuleTransition>();
        result.add(getInitial());
        pool.add(target());
        while (!pool.isEmpty()) {
            GraphState next = pool.pop();
            Set<RuleTransition> inSet = inMap.remove(next);
            if (inSet != null) {
                for (RuleTransition in : inSet) {
                    result.add(in);
                    pool.add(in.source());
                }
            }
        }
        return result;
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
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

    @Override
    public RecipeEvent getKey() {
        return getEvent();
    }

    @Override
    public RecipeEvent toStub() {
        return getEvent();
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected HostGraphMorphism computeMorphism() {
        HostGraphMorphism result = null;
        HostGraph host = source().getGraph();
        for (RuleTransition step : getSteps()) {
            RuleApplication appl = step.getEvent().newApplication(host);
            result =
                result == null ? appl.getMorphism()
                        : result.then(appl.getMorphism());
            host = appl.getTarget();
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

    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private HostGraphMorphism morphism;
    private RecipeEvent event;
    private Set<RuleTransition> steps;
}