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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.lts.GraphTransition.Claz;

/**
 * Information required for the proper exploration of transient states.
 * @author Arend Rensink
 * @version $Revision$
 */
class ExploreData {
    /**
     * Creates a record for a given state.
     */
    ExploreData(StateCache cache) {
        this.cache = cache;
        GraphState state = this.state = cache.getState();
        boolean stateIsFull = state.isFull();
        assert stateIsFull
            || state.getActualFrame().getTransience() == state.getPrimeFrame().getTransience();
        var knownInner = this.knownInner = state.isInner();
        if (!knownInner || stateIsFull) {
            this.backInner = this.forwInner = EMPTY_SET;
            this.backOuter = EMPTY_TRANS_SET;
        } else {
            this.backInner = new HashSet<>();
            this.backInner.add(this);
            this.forwInner = new HashSet<>();
            this.forwInner.add(this);
            this.backOuter = new HashSet<>();
        }
        if (!state.getPrimeFrame().isInner()) {
            this.forwOuter = EMPTY_TARGET_SET;
        } else if (stateIsFull) {
            this.forwOuter = computeForwOuter();
        } else {
            this.forwOuter = new HashSet<>();
            if (!knownInner) {
                this.forwOuter.add(new RecipeTarget(state));
            }
        }
        this.backTransient = new HashSet<>();
        this.knownAbsence = stateIsFull
            ? state.getAbsence()
            : state.getActualFrame().getTransience();
        boolean knownTransient = this.knownTransient = state.isTransient();
        if (!stateIsFull) {
            this.forwTransient = new HashSet<>();
            if (knownTransient) {
                this.backTransient.add(this);
                this.forwTransient.add(this);
            }
            this.forwTransientOpen = new HashSet<>();
            if (!state.isClosed() || knownTransient) {
                this.forwTransientOpen.add(this);
            }
        } else {
            this.forwTransient = EMPTY_SET;
            this.forwTransientOpen = EMPTY_SET;
        }
    }

    private Set<RecipeTarget> computeForwOuter() {
        assert getState().isFull() && getState().getPrimeFrame().isInner();
        Set<RecipeTarget> result = new LinkedHashSet<>();
        Set<GraphState> known = new HashSet<>();
        Queue<ExploreData> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            ExploreData source = queue.poll();
            var state = source.getState();
            assert state.getPrimeFrame().isInner();
            if (state.isInner()) {
                for (var trans : source.getState().getTransitions(Claz.NON_ABSENT)) {
                    assert trans.isInnerStep();
                    var target = trans.target();
                    if (!known.add(target)) {
                        if (target.getPrimeFrame().isInner()) {
                            queue.add(target.getCache().getExploreData());
                        } else {
                            result.add(new RecipeTarget((RuleTransition) trans));
                        }
                    }
                }
            } else {
                result.add(new RecipeTarget(state));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "D-" + getState().getNumber();
    }

    /** The backward reachable inner states. */
    private Set<ExploreData> backInner;

    /** The forward reachable inner states. */
    private Set<ExploreData> forwInner;

    /** The backward reachable initial recipe transitions to an inner state. */
    private Set<RuleTransition> backOuter;

    /** The forward reachable outer states. */
    private Set<RecipeTarget> forwOuter;

    /** The backward reachable transient states, up to and including the first steady state. */
    private Set<ExploreData> backTransient;

    /** The forward reachable transient states. */
    private Set<ExploreData> forwTransient;

    /** The forward reachable transient open states. */
    private Set<ExploreData> forwTransientOpen;

    /** Returns the state for which this record contains the exploration data. */
    GraphState getState() {
        return this.state;
    }

    /** The state for which this record contains the exploration data. */
    private final AbstractGraphState state;

    /** Returns the cache for which this record contains the exploration data. */
    StateCache getCache() {
        return this.cache;
    }

    /** The cache for which this record contains the exploration data. */
    private final StateCache cache;

    /**
     * Notifies the cache of the addition of an outgoing partial transition.
     * @param partial new outgoing partial rule transition from this state
     */
    void notifyOutPartial(RuleTransition partial) {
        if (DEBUG) {
            System.out
                .printf("Rule transition added: %s--%s-->%s%n", partial.source(), partial.label(),
                        partial.target());
        }
        assert partial.isPartialStep();
        assert partial.source() == getState();
        GraphState target = partial.target();
        if (target.getActualFrame().isRemoved()) {
            return;
        }
        ExploreData targetData = target.getCache().getExploreData();
        // add recipe transitions
        if (this.knownInner) {
            var gts = getState().getGTS();
            for (var back : this.backOuter) {
                if (target.getPrimeFrame().isInner()) {
                    targetData.forwOuter
                        .stream()
                        .map(t -> createRecipeTransition(back, t))
                        .forEach(gts::addTransition);
                } else {
                    gts.addTransition(createRecipeTransition(back, new RecipeTarget(partial)));
                }
            }
        } else if (partial.getStep().isInitial() && !target.getPrimeFrame().isInner()) {
            // it's a single-step recipe transition
            addRecipeTransition(partial, new RecipeTarget(partial));
        }
        // modify the reachable inner and outer sets
        if (target.getPrimeFrame().isInner()) {
            var backInner = new HashSet<>(this.backInner);
            if (!this.state.isInner()) {
                for (var forw : targetData.forwInner) {
                    forw.backOuter.add(partial);
                }
            } else if (!this.forwInner.contains(targetData)) {
                // if targetData is already known, then the assignments below
                // will have no effect
                for (var forw : targetData.forwInner) {
                    forw.backInner.addAll(backInner);
                    forw.backOuter.addAll(this.backOuter);
                }
                for (var back : backInner) {
                    back.forwInner.addAll(targetData.forwInner);
                    back.forwOuter.addAll(targetData.forwOuter);
                }
            }
        }
        // modify the absence level
        setAbsence(target.getAbsence());
        // modify the reachable transient sets
        if (target.isTransient()) {
            var targetForwTransient = new HashSet<>(targetData.forwTransient);
            if (getState().isTransient()) {
                for (var back : this.backTransient) {
                    back.forwTransient.addAll(targetForwTransient);
                    back.forwTransientOpen.addAll(targetData.forwTransientOpen);
                }
                for (var forw : targetForwTransient) {
                    forw.backTransient.addAll(this.backTransient);
                }
            } else {
                this.forwTransient.addAll(targetForwTransient);
                this.forwTransientOpen.addAll(targetData.forwTransientOpen);
                for (var forw : targetForwTransient) {
                    forw.backTransient.add(this);
                }
            }
        }
    }

    /**
     * Callback method invoked when the state has been closed.
     * This may or may not involve a simultaneous change in transience.
     */
    void notifyClosure() {
        if (DEBUG) {
            System.out.printf("State closed: %s%n", getState());
        }
        removeFromForwTransientOpen();
    }

    /** Removes this state from the {@link #forwTransientOpen} of all backward transients. */
    private void removeFromForwTransientOpen() {
        this.backTransient.forEach(this::removeFromForwTransientOpenOf);
        removeFromForwTransientOpenOf(this);
    }

    /** Remove this record from the {@link #forwTransientOpen} of a given state data. */
    private void removeFromForwTransientOpenOf(ExploreData data) {
        var state = data.getState();
        var transientOpen = data.forwTransientOpen;
        if (transientOpen.remove(this) && transientOpen.isEmpty() && state.isClosed()) {
            state.setFull(this.knownAbsence);
            // reset the auxiliary sets, they are no longer needed
            data.backInner.forEach(d -> d.forwInner.remove(data));
            data.backInner = EMPTY_SET;
            data.forwInner = EMPTY_SET;
            data.backOuter = EMPTY_TRANS_SET;
            data.backTransient.forEach(d -> d.forwTransient.remove(data));
            data.backTransient = EMPTY_SET;
            data.forwTransient = EMPTY_SET;
        }
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void notifyTransience(int transience) {
        if (DEBUG) {
            System.out.printf("Transient depth of %s set to %s%n", getState(), transience);
        }
        var state = getState();
        if (this.knownInner && !state.isInner()) {
            this.knownInner = false;
            // add incoming recipe transitions
            var gts = state.getGTS();
            var target = new RecipeTarget(state);
            for (var back : this.backOuter) {
                var trans = createRecipeTransition(back, target);
                gts.addTransition(trans);
            }
            // update reachable recipe targets
            for (var back : this.backInner) {
                back.forwOuter.add(target);
            }
        }
        setAbsence(state.getActualFrame().getTransience());
        if (this.knownTransient && !state.isTransient()) {
            this.knownTransient = false;
            removeFromForwTransientOpen();
        }
    }

    private RecipeTransition createRecipeTransition(RuleTransition partial, RecipeTarget target) {
        return new RecipeTransition(partial, target.outValues, target.state());
    }

    private void addRecipeTransition(RuleTransition partial, RecipeTarget target) {
        var trans = createRecipeTransition(partial, target);
        getState().getGTS().addTransition(trans);
        if (DEBUG) {
            System.out
                .printf("Recipe transition added: %s--%s-->%s%n", trans.source(), trans.label(),
                        target);
        }
    }

    /** Sets the known absence to a given level, if it is lower than the current level. */
    private void setAbsence(int newAbsence) {
        if (newAbsence < this.knownAbsence) {
            this.knownAbsence = newAbsence;
        }
    }

    /**
     * Returns the (known) absence level of the state.
     * This is {@link Status#MAX_ABSENCE} if the state is erroneous,
     * otherwise it is the minimum absence level of the reachable states.
     */
    final int getAbsence() {
        return this.knownAbsence;
    }

    /** Flag indicating if this state is known to be inner. */
    private boolean knownInner;

    /** Flag indicating if this state is known to be transient. */
    private boolean knownTransient;

    /** Known absence level. */
    private int knownAbsence;

    /** Shared unmodifiable empty set of states. */
    static private final Set<ExploreData> EMPTY_SET = Collections.emptySet();
    /** Shared unmodifiable empty set of transitions. */
    static private final Set<RuleTransition> EMPTY_TRANS_SET = Collections.emptySet();
    /** Shared unmodifiable empty set of recipe targets. */
    static private final Set<RecipeTarget> EMPTY_TARGET_SET = Collections.emptySet();

    private final static boolean DEBUG = false;

    /** Combination of target state and out-parameter values. */
    private record RecipeTarget(Recipe recipe, HostNode[] outValues, GraphState state) {
        /** Creates a recipe target from a graph state whose prime call stack contains the
         * out-parameter values.
         */
        RecipeTarget(GraphState target) {
            this(target.getPrimeFrame().getRecipe().get(), getOutValuesFromTarget(target), target);
        }

        /** Creates a recipe target from the last partial transition in the recipe.
         */
        RecipeTarget(RuleTransition partial) {
            this(partial.getStep().getRecipe().get(), getOutValuesFromFinalTrans(partial),
                 partial.target());
        }

        /** Computes the recipe out-parameter values by reconstructing the final transition. */
        static private HostNode[] getOutValuesFromFinalTrans(RuleTransition partial) {
            assert partial.isInnerStep() && !partial.target().getPrimeFrame().isInner();
            var step = partial.getStep();
            var valuator = partial.getGTS().getRecord().getValuator();
            // apply the transition's push change; for this we need the rule arguments
            var anchorImages = partial.getEvent().getAnchorImages();
            valuator.setAnchorInfo(i -> (HostNode) anchorImages[i]);
            var addedNodes = partial.getAddedNodes();
            valuator.setCreatorInfo(i -> addedNodes[i]);
            Object[] stack = partial.source().getFrameStack(step.getSource());
            stack = step.getPush().apply(stack, valuator);
            // pop until the (final) switch within the outer recipe body
            var recipeFinal = step
                .getSwitch()
                .stream()
                .filter(s -> s.getTemplate().filter(t -> t.hasOwner(Kind.RECIPE)).isPresent())
                .findFirst()
                .get();
            stack = step.getPopUntil(s -> s == recipeFinal).apply(stack);
            // now obtain the parameter values
            var result = recipeFinal.onFinish().assignFinal2Par().lookup(stack);
            // apply the transition's permutation, if it is not the identity
            if (!partial.getMorphism().isIdentity()) {
                var nodeMap = partial.getMorphism().nodeMap();
                result = Assignment.map(result, n -> nodeMap.get(n));
            }
            return result;
        }

        /** Computes the recipe out-parameter values from the prime frame of the target state. */
        static private HostNode[] getOutValuesFromTarget(GraphState target) {
            assert target.getPrimeFrame().isInner() && !target.isInner();
            // look for the last frame between the state's prime and actual frames
            // that was still internal; the corresponding stack contains the out-parameter values
            var frame = target.getActualFrame();
            while (!frame.isInner()) {
                var pred = frame.getPred();
                assert pred != null;
                frame = pred;
            }
            // get the stack at that frame
            Object[] stack = target.getFrameStack(frame);
            // get the out-parameter assignment
            return frame.getLocation().assignFinal2Par().lookup(stack);
        }
    }
}
