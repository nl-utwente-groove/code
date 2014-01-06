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
package groove.control;

import groove.algebra.AlgebraFamily;
import groove.control.parse.CtrlNameSpace;
import groove.control.parse.Namespace;
import groove.grammar.Action;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.FormatException;
import groove.graph.GraphInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlBuilder {
    /** Private constructor for the singleton instance. */
    private CtrlBuilder(CtrlNameSpace namespace) {
        this.namespace = namespace;
    }

    private final CtrlNameSpace namespace;

    /** 
     * Closes a given control automaton under the <i>as long as possible</i>
     * operator.
     */
    public NewCtrlAut buildAlap(NewCtrlAut aut) {
        return buildLoop(aut, aut.getInitGuard());
    }

    /** 
     * Closes a given control automaton under arbitrary repetition
     */
    public NewCtrlAut buildStar(NewCtrlAut aut) {
        return buildLoop(aut, EMPTY_GUARD);
    }

    /** Factory method for a call. */
    public NewCtrlAut buildCall(Callable unit, List<CtrlPar> args) {
        NewCtrlAut result = createCtrlAut(unit.getFullName());
        result.addEdge(new CtrlEdge(result.getStart(), result.getEnd(), unit,
            args));
        return result;
    }

    /**
     * Builds an automaton for a <i>do-until</i> construct.
     * The result is constructed by modifying the first parameter.
     * The second parameter is also modified.
     * @param first the body of the loop; contains the result upon return
     * @param second the condition of the loop; modified in the course 
     * of the construction
     */
    public NewCtrlAut buildDoUntil(NewCtrlAut first, NewCtrlAut second) {
        buildUntilDo(second, first);
        return buildSeq(first, second);
    }

    /**
     * Builds an automaton for a <i>do-while</i> construct.
     * The result is constructed by modifying the first parameter.
     * The second parameter is also modified.
     * @param first the body of the loop; contains the result upon return
     * @param second the condition of the loop; modified in the course 
     * of the construction
     */
    public NewCtrlAut buildDoWhile(NewCtrlAut first, NewCtrlAut second) {
        buildWhileDo(second, first);
        return buildSeq(first, second);
    }

    /** 
     * Builds an <i>if-then-else</i> construct out of three automata,
     * by modifying the first of the three.
     */
    public NewCtrlAut buildIfThenElse(NewCtrlAut first, NewCtrlAut second,
            NewCtrlAut third) {
        CtrlGuard guard = first.getInitGuard();
        buildSeq(first, second);
        if (third == null) {
            third = buildTrue();
        }
        return buildOr(first, third, guard);
    }

    /** Builds an automation for an {@code any}-call. */
    public NewCtrlAut buildAny() {
        return buildGroupCall(this.namespace.getTopNames());
    }

    /** Builds an automation for an {@code other}-call. */
    public NewCtrlAut buildOther() {
        Set<String> unusedRules =
            new HashSet<String>(this.namespace.getTopNames());
        unusedRules.removeAll(this.namespace.getUsedNames());
        return buildGroupCall(unusedRules);
    }

    /** Builds an automation for a choice among a set of rules. */
    private NewCtrlAut buildGroupCall(Set<String> names) {
        NewCtrlAut result = null;
        for (String name : names) {
            NewCtrlAut callAut = buildCall(this.namespace.getUnit(name), null);
            if (result == null) {
                result = callAut;
            } else {
                result = buildOr(result, callAut);
            }
        }
        if (result == null) {
            result = buildTrue();
        }
        return result;
    }

    /** Adds a second control automaton sequentially after a given automaton. 
     * The result is constructed by modifying the first parameter.
     * @param first the automaton to be executed first; contains the result upon return
     * @param second the automaton to be executed second
     */
    public NewCtrlAut buildSeq(NewCtrlAut first, NewCtrlAut second) {
        CtrlMorphism secondToFirstMap = copyStates(second, first, false);
        // remove omega-transitions from first
        Set<CtrlTransition> firstOmega = removeOmegas(first);
        // copy transitions from second to first
        for (CtrlState state : second.nodeSet()) {
            if (state.equals(second.getStart())) {
                // merge initial transitions of second with all
                // omega transitions of first
                Map<CtrlState,CtrlState> stateMap =
                    new HashMap<CtrlState,CtrlState>(secondToFirstMap.nodeMap());
                for (CtrlTransition omega : firstOmega) {
                    stateMap.put(state, omega.source());
                    state.copyTransitions(stateMap, omega.getGuard());
                }
            } else {
                state.copyTransitions(secondToFirstMap.nodeMap(), null);
            }
        }
        first.addErrors(second);
        return first;
    }

    /** Adds a second control automaton as alternative to a given one. */
    public NewCtrlAut buildOr(NewCtrlAut first, NewCtrlAut second) {
        return buildOr(first, second, EMPTY_GUARD);
    }

    /** Factory method for immediate, unconditional success. */
    public NewCtrlAut buildTrue() {
        NewCtrlAut result = createCtrlAut("true");
        return addOmega(result);
    }

    /** Adds an unconditional terminating transition between start and final state. */
    public NewCtrlAut addOmega(NewCtrlAut result) {
        result.getStart().addTransition(createOmegaLabel(), result.getFinal());
        return result;
    }

    /** 
     * Adds a second control automaton as <i>else</i> parameter in
     * a <i>try</i> construct with the first automaton as try block.
     */
    public NewCtrlAut buildTryElse(NewCtrlAut first, NewCtrlAut second) {
        if (second == null) {
            second = buildTrue();
        }
        return buildOr(first, second, first.getInitGuard());
    }

    /**
     * Constructs an until automaton using a given automaton as condition 
     * and adding a second automaton as until body.
     * @param first the condition automaton; contains the result upon return
     * @param second the until body automaton
     */
    public NewCtrlAut buildUntilDo(NewCtrlAut first, NewCtrlAut second) {
        // get the automaton guard before the omegas are removed
        CtrlGuard firstGuard = first.getInitGuard();
        if (firstGuard != null) {
            // remove omega-transitions from first
            Set<CtrlTransition> firstOmega = removeOmegas(first);
            // build the until loop
            buildOr(first, second, firstGuard);
            buildLoop(first, null);
            // re-attach the omega-transitions
            for (CtrlTransition omega : firstOmega) {
                omega.source().addTransition(omega.label(), omega.target());
            }
        }
        return first;
    }

    /**
     * Constructs a while automaton using a given automaton as condition 
     * and adding a second automaton as while body.
     * The result is constructed by modifying the first parameter.
     * @param first the condition automaton; contains the result upon return
     * @param second the while body automaton
     */
    public NewCtrlAut buildWhileDo(NewCtrlAut first, NewCtrlAut second) {
        // get the automaton guard before the omegas are removed
        CtrlGuard firstGuard = first.getInitGuard();
        // sequentially compose first and second
        buildSeq(first, second);
        return buildLoop(first, firstGuard);
    }

    /** 
     * Loops a given control automaton, while terminating under a 
     * predefined guard.
     * The result is constructed by modifying the parameter.
     */
    private NewCtrlAut buildLoop(NewCtrlAut aut, CtrlGuard guard) {
        Set<CtrlTransition> omegas = removeOmegas(aut);
        // create an identity state map for the automaton
        Map<CtrlState,CtrlState> id = new HashMap<CtrlState,CtrlState>();
        for (CtrlState state : aut.nodeSet()) {
            id.put(state, state);
        }
        // create new omega transitions if the automaton guard is non-degenerate
        if (guard != null) {
            CtrlLabel newLabel = createLabel(CtrlCall.OMEGA_CALL, guard);
            aut.getStart().addTransition(newLabel, aut.getFinal());
        }
        // loop back from final to post-initial states
        for (CtrlTransition omega : omegas) {
            // create cycles for all original omega transitions
            id.put(aut.getStart(), omega.source());
            aut.getStart().copyTransitions(id, omega.getGuard());
        }
        return aut;
    }

    /** 
     * Adds a second control automaton as alternative, reachable 
     * under a given guard. The guard may be {@code null}, meaning that
     * the second automaton is unreachable.
     */
    private NewCtrlAut buildOr(NewCtrlAut first, NewCtrlAut second,
            CtrlGuard guard) {
        // if the guard is degenerate, the second automaton is unreachable
        if (guard != null) {
            CtrlMorphism secondToFirstMap = copyStates(second, first, true);
            Map<CtrlState,CtrlState> stateMap = secondToFirstMap.nodeMap();
            // copy transitions from second to first
            for (CtrlLocation state : second.nodeSet()) {
                // only copy transitions if the source and target are not omega-only
                if (!state.isOmegaOnly() || !stateMap.get(state).isOmegaOnly()) {
                    boolean isInit = state.equals(second.getStart());
                    state.copyTransitions(stateMap, isInit ? guard : null);
                }
            }
        }
        first.addErrors(second);
        return first;
    }

    /** 
     * Copies all non-start and non-final states of a given automaton to
     * another, and returns the mapping from original to new states.
     * @param fromAut the automaton from which states are copied
     * @param toAut the automaton to which states are copied
     * @param shareOmegaOnlyState if {@code true}, an attempt is made to
     * have the omega-only states of the source automaton map to the omega-only
     * states of the target automaton
     * @return a map from states in {@code fromAut} to new states in {@code toAut}
     */
    private CtrlMorphism copyStates(CtrlAut fromAut, CtrlAut toAut,
            boolean shareOmegaOnlyState) {
        CtrlMorphism secondToFirstMap = new CtrlMorphism();
        CtrlState toAutOmegaOnlyState = toAut.getOmegaOnlyState();
        for (CtrlState state : fromAut.nodeSet()) {
            CtrlState image;
            if (state.equals(fromAut.getStart())) {
                image = toAut.getStart();
            } else if (state.equals(fromAut.getFinal())) {
                image = toAut.getFinal();
            } else if (shareOmegaOnlyState && state.isOmegaOnly()
                && toAutOmegaOnlyState != null) {
                image = toAutOmegaOnlyState;
            } else {
                image = toAut.addState(state, null);
            }
            secondToFirstMap.putNode(state, image);
        }
        return secondToFirstMap;
    }

    /** Removes and returns the set of omega transitions from a given automaton. */
    private Set<CtrlTransition> removeOmegas(CtrlAut aut) {
        Set<CtrlTransition> omegas =
            new HashSet<CtrlTransition>(aut.getOmegas());
        for (CtrlTransition o : omegas) {
            o.source().removeOmega(o);
        }
        return omegas;
    }

    /** Factory method for control labels with an empty guard. */
    private CtrlLabel createLabel(CtrlCall call) {
        return createLabel(call, EMPTY_GUARD);
    }

    /** Factory method for control labels. */
    private CtrlLabel createLabel(CtrlCall call, CtrlGuard guard) {
        return new CtrlLabel(call, guard, true);
    }

    /** Factory method for omega control labels, with an empty guard. */
    private CtrlLabel createOmegaLabel() {
        return createLabel(CtrlCall.OMEGA_CALL);
    }

    /** 
     * Builds the default control automaton for a set of actions. 
     * @param symbolic if {@code true}, the automaton will be used for symbolic exploration,
     *  which means that input parameters can be matched even in the default automaton
     */
    public NewCtrlAut buildDefault(Collection<? extends Action> actions,
            boolean symbolic) throws FormatException {
        NewCtrlAut result = new NewCtrlAut("none (arbitrary rule application)");
        FormatErrorSet errors = new FormatErrorSet();
        SortedMap<Integer,Set<Action>> priorityMap =
            new TreeMap<Integer,Set<Action>>();
        // first add the names and signatures to the namespace
        for (Action action : actions) {
            boolean needsInput = false;
            for (CtrlPar.Var var : action.getSignature()) {
                if (var.isInOnly()
                    && (var.getType() == CtrlType.NODE || !symbolic)) {
                    needsInput = true;
                    break;
                }
            }
            if (needsInput) {
                errors.add(
                    "Error in %s %s: input parameters require explicit control (use %s algebra for symbolic exploration)",
                    action.getKind(), action.getFullName(),
                    AlgebraFamily.POINT.getName(), action);
                continue;
            }
            int priority = action.getPriority();
            Set<Action> priorityActions = priorityMap.get(priority);
            if (priorityActions == null) {
                priorityMap.put(priority, priorityActions =
                    new HashSet<Action>());
            }
            priorityActions.add(action);
        }
        if (!errors.isEmpty()) {
            GraphInfo.addErrors(result, errors);
        }
        // List of control automata for different levels of priority
        List<NewCtrlAut> prioAutList = new ArrayList<NewCtrlAut>();
        for (Set<Action> priorityActions : priorityMap.values()) {
            // collect the names
            Set<String> actionNames = new HashSet<String>();
            for (Action action : priorityActions) {
                actionNames.add(action.getFullName());
            }
            prioAutList.add(buildGroupCall(actionNames));
        }
        if (prioAutList.isEmpty()) {
            addOmega(result);
        } else {
            ListIterator<NewCtrlAut> levelIter =
                prioAutList.listIterator(prioAutList.size());
            while (levelIter.hasPrevious()) {
                result = buildTryElse(result, levelIter.previous());
            }
            result = buildAlap(result);
        }
        GraphInfo.throwException(result);
        result = result.normalise();
        result.setDefault();
        return result;
    }

    /** Constructs an empty control automaton. 
     * @param name name of the new automaton
     */
    private NewCtrlAut createCtrlAut(String name) {
        return new NewCtrlAut(name);
    }

    /** Returns a new instance of this class, for a given name space. */
    public static CtrlBuilder instance(Namespace namespace) {
        return new CtrlBuilder(namespace);
    }

    /** Constant empty set of guard rule names. */
    private static final CtrlGuard EMPTY_GUARD = new CtrlGuard();
}
