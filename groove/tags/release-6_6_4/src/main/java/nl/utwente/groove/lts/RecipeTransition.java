// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.lts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.CtrlPar;
import nl.utwente.groove.control.CtrlPar.Const;
import nl.utwente.groove.control.CtrlPar.Var;
import nl.utwente.groove.control.CtrlPar.Wild;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Valuator;
import nl.utwente.groove.control.instance.Assignment;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.control.template.SwitchStack;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.ALabelEdge;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;

/**
 * Models a transition corresponding to the complete execution of
 * a recipe. This comprises a sequence of ordinary graph transitions.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
@NonNullByDefault
public class RecipeTransition extends ALabelEdge<GraphState>
    implements GraphTransition, ActionLabel {
    /**
     * Constructs a recipe transition between
     * a given source and target state, on the basis of
     * an initial underlying rule transition.
     */
    public RecipeTransition(RuleTransition initial, GraphState target) {
        super(initial.source(), target);
        this.initial = initial;
        SwitchStack initialStack = initial.getStep().getSwitchStack();
        int depth = 0;
        while (initialStack.get(depth).getKind() != Kind.RECIPE) {
            depth++;
        }
        this.recipeCallDepth = depth;
        this.recipeSwitch = initialStack.get(depth);
    }

    @Override
    public RecipeTransition label() {
        return this;
    }

    @Override
    public GTS getGTS() {
        return source().getGTS();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public String text(boolean anchored) {
        return text();
    }

    @Override
    public Recipe getAction() {
        return (Recipe) getSwitch().getUnit();
    }

    /** Returns the control switch instantiated by this transition. */
    @Override
    public Switch getSwitch() {
        return this.recipeSwitch;
    }

    private final Switch recipeSwitch;
    /** Depth of the recipe switch in the initial call of this transition. */
    private final int recipeCallDepth;

    @Override
    public RecipeEvent getEvent() {
        var result = this.event;
        if (result == null) {
            this.event = result = new RecipeEvent(this);
        }
        return result;
    }

    private @Nullable RecipeEvent event;

    @Override
    public final boolean isInternalStep() {
        return false;
    }

    @Override
    public final boolean isRealStep() {
        return source().isRealState() && target().isRealState();
    }

    @Override
    public boolean isPartial() {
        return source().getActualFrame().isTransient() || target().getActualFrame().isTransient();
    }

    /** Returns the initial rule transition of the recipe transition. */
    @Override
    public RuleTransition getInitial() {
        return this.initial;
    }

    private final RuleTransition initial;

    /** Returns the collection of rule transitions comprising this label. */
    @Override
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

    private @Nullable Set<RuleTransition> steps;

    private Set<RuleTransition> computeSteps() {
        // mapping from states to sets of incoming transitions
        Map<GraphState,@Nullable Set<RuleTransition>> inMap = new HashMap<>();
        // build the incoming transition map
        Stack<GraphState> pool = new Stack<>();
        pool.add(getInitial().target());
        while (!pool.isEmpty()) {
            GraphState next = pool.pop();
            for (RuleTransition trans : next.getRuleTransitions()) {
                GraphState target = trans.target();
                if (target.isInternalState() || target == target()) {
                    var inSet = inMap.get(target);
                    boolean fresh = inSet == null;
                    if (fresh) {
                        inMap.put(target, inSet = new HashSet<>());
                    }
                    assert inSet != null; // just set in case it was not set
                    inSet.add(trans);
                    if (fresh && target != target()) {
                        pool.add(target);
                    }
                }
            }
        }
        assert getInitial().target().equals(target()) || inMap.containsKey(target());
        // backward reachability to build up the result set
        Set<RuleTransition> result = new HashSet<>();
        result.add(getInitial());
        pool.add(target());
        while (!pool.isEmpty()) {
            GraphState next = pool.pop();
            var inSet = inMap.remove(next);
            if (inSet != null) {
                for (RuleTransition in : inSet) {
                    result.add(in);
                    pool.add(in.source());
                }
            }
        }
        return result;
    }

    /** Returns a shortest rule transition sequence from source to target.
     * This is guaranteed to be a non-empty list.
     */
    public List<RuleTransition> getPath() {
        List<RuleTransition> result = null;
        // all paths of the current length
        List<List<RuleTransition>> paths = new ArrayList<>();
        paths.add(Arrays.asList(getInitial()));
        // do the following for paths of increasing length
        while (result == null) {
            List<List<RuleTransition>> newPaths = new ArrayList<>();
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
                            List<RuleTransition> newPath = new ArrayList<>(path);
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

    @Override
    public HostNode[] getArguments() {
        List<? extends CtrlPar> args = getSwitch().getArgs();
        HostNode[] result = new HostNode[args.size()];
        for (int i = 0; i < args.size(); i++) {
            CtrlPar arg = args.get(i);
            HostNode node;
            if (arg instanceof Const c) {
                node = c.getNode();
            } else if (arg instanceof Wild) {
                node = null;
            } else if (arg instanceof Var v) {
                CtrlVar var = v.var();
                if (arg.inOnly()) {
                    int varIndex = getSwitch().getSource().getVars().indexOf(var);
                    node = Valuator.get(source().getPrimeValues(), varIndex);
                } else {
                    assert arg.outOnly();
                    Map<CtrlVar,Integer> varIxMap = getSwitch().onFinish().getVarIxMap();
                    int varIndex = varIxMap.get(var);
                    Object[] values = getFrameValues();
                    node = Valuator.get(values, varIndex);
                }
            } else {
                throw Exceptions.UNREACHABLE;
            }
            result[i] = node;
        }
        return result;
    }

    /** Retrieves the frame values of the target state
     * popped to the level corresponding to the switch' target location.
     */
    private Object[] getFrameValues() {
        Object[] result = target().getPrimeValues();
        List<Assignment> pops = target().getActualFrame().getPops();
        int popCount = target().getActualFrame().getNestingDepth() - this.recipeCallDepth;
        assert popCount <= pops.size();
        for (int i = 0; i < popCount; i++) {
            result = pops.get(i).apply(result);
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
    @Override
    public HostGraphMorphism getMorphism() {
        var result = this.morphism;
        if (result == null) {
            this.morphism = result = computeMorphism();
        }
        return result;
    }

    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private @Nullable HostGraphMorphism morphism;

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected HostGraphMorphism computeMorphism() {
        HostGraphMorphism result = null;
        for (RuleTransition step : getPath()) {
            result = result == null
                ? step.getMorphism()
                : result.then(step.getMorphism());
        }
        assert result != null : String.format("Path %s should not be empty", getPath());
        return result;
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
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
     */
    public RecipeTransition toTransition(GraphState source) {
        if (source != source()) {
            throw Exceptions
                .illegalArg("Source state %s should coincide with argument %s", source(), source);
        } else {
            return this;
        }
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof ActionLabel)) {
            throw Exceptions.illegalArg("Can't compare %s and %s", this.getClass(), obj.getClass());
        }
        if (obj instanceof RuleTransitionLabel) {
            return -obj.compareTo(this);
        }
        int result = super.compareTo(obj);
        if (result != 0) {
            return result;
        }
        RecipeTransition other = (RecipeTransition) obj;
        result = getAction().compareTo(other.getAction());
        if (result != 0) {
            return result;
        }
        return getInitial().label().compareTo(other.getInitial().label());
    }

    @Override
    protected boolean isTypeEqual(@Nullable Object obj) {
        return obj instanceof RecipeTransition;
    }

    @Override
    protected Line computeLine() {
        StringBuilder text = new StringBuilder(getAction().getTransitionLabel());
        text.append(RuleTransitionLabel.computeParameters(this));
        return Line.atom(text.toString());
    }

    @Override
    protected int computeLabelHash() {
        return this.initial.hashCode();
    }

    @Override
    protected boolean isLabelEqual(Edge other) {
        return other instanceof RecipeTransition
            && ((RecipeTransition) other).initial.equals(this.initial);
    }
}