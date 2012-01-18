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

import groove.control.parse.Namespace;
import groove.graph.GraphInfo;
import groove.trans.Action;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlFactory {
    /** Private constructor for the singleton instance. */
    private CtrlFactory() {
        // empty
    }

    /** 
     * Closes a given control automaton under the <i>as long as possible</i>
     * operator.
     */
    public CtrlAut buildAlap(CtrlAut aut) {
        return buildLoop(aut, aut.getStart().getInit());
    }

    /** 
     * Closes a given control automaton under arbitrary repetition
     */
    public CtrlAut buildStar(CtrlAut aut) {
        return buildLoop(aut, Collections.<CtrlCall>emptySet());
    }

    /** Factory method for a rule or function call. */
    public CtrlAut buildCall(CtrlCall call, Namespace namespace) {
        if (call.getKind() == CtrlCall.Kind.RULE) {
            return buildRuleCall(call);
        } else {
            assert !call.isOmega();
            return buildBodyCall(call, namespace);
        }
    }

    /** Factory method for a rule call. */
    private CtrlAut buildRuleCall(CtrlCall call) {
        CtrlAut result = createCtrlAut(call.getName());
        CtrlState middle = result.addState();
        // convert the call arguments using the context
        result.addTransition(result.getStart(), createLabel(call), middle);
        result.addTransition(middle, createOmegaLabel(), result.getFinal());
        return result;
    }

    /** Factory method for a function or transaction call. */
    private CtrlAut buildBodyCall(CtrlCall call, Namespace namespace) {
        CtrlAut body = namespace.getBody(call.getName());
        assert call.getArgs() == null || call.getArgs().isEmpty() : "Function and recipe parameters not yet implemented";
        return body.clone(call.getKind() == CtrlCall.Kind.RECIPE
                ? namespace.getRecipe(call.getName()) : null);
    }

    /**
     * Builds an automaton for a <i>do-until</i> construct.
     * The result is constructed by modifying the first parameter.
     * The second parameter is also modified.
     * @param first the body of the loop; contains the result upon return
     * @param second the condition of the loop; modified in the course 
     * of the construction
     */
    public CtrlAut buildDoUntil(CtrlAut first, CtrlAut second) {
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
    public CtrlAut buildDoWhile(CtrlAut first, CtrlAut second) {
        buildWhileDo(second, first);
        return buildSeq(first, second);
    }

    /** 
     * Builds an <i>if-then-else</i> construct out of three automata,
     * by modifying the first of the three.
     */
    public CtrlAut buildIfThenElse(CtrlAut first, CtrlAut second, CtrlAut third) {
        Set<CtrlCall> guard = first.getStart().getInit();
        buildSeq(first, second);
        if (third == null) {
            third = buildTrue();
        }
        return buildOr(first, third, guard);
    }

    /** Builds an automation for an {@code any}-call. */
    public CtrlAut buildAny(Namespace namespace) {
        return buildGroupCall(namespace.getTopNames(), namespace);
    }

    /** Builds an automation for an {@code other}-call. */
    public CtrlAut buildOther(Namespace namespace) {
        Set<String> unusedRules = new HashSet<String>(namespace.getTopNames());
        unusedRules.removeAll(namespace.getUsedNames());
        return buildGroupCall(unusedRules, namespace);
    }

    /** Builds an automation for a choice among a set of rules. */
    private CtrlAut buildGroupCall(Set<String> ruleNames, Namespace namespace) {
        CtrlAut result = null;
        for (String ruleName : ruleNames) {
            CtrlCall call;
            switch (namespace.getKind(ruleName)) {
            case RULE:
                call = new CtrlCall(namespace.getRule(ruleName), null);
                break;
            case RECIPE:
                call = new CtrlCall(CtrlCall.Kind.RECIPE, ruleName, null);
                break;
            default:
                call = null;
                assert false;
            }
            CtrlAut callAut = buildCall(call, namespace);
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
    public CtrlAut buildSeq(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap =
            copyStates(second, first, null);
        // remove omega-transitions from first
        Set<CtrlTransition> firstOmega = removeOmegas(first);
        // copy transitions from second to first
        for (CtrlTransition trans : second.edgeSet()) {
            CtrlState sourceImage = secondToFirstMap.get(trans.source());
            CtrlState targetImage = secondToFirstMap.get(trans.target());
            CtrlLabel label = trans.label();
            // initial transitions have to be treated separately
            if (sourceImage.equals(first.getStart())) {
                // create a combined transition for every omega transition of first
                for (CtrlTransition omega : firstOmega) {
                    CtrlLabel newLabel =
                        createLabel(label, omega.label().getGuard());
                    first.addTransition(omega.source(), newLabel, targetImage);
                }
            } else {
                first.addTransition(sourceImage, label, targetImage);
            }
        }
        return first;
    }

    /** Adds a second control automaton as alternative to a given one. */
    public CtrlAut buildOr(CtrlAut first, CtrlAut second) {
        return buildOr(first, second, EMPTY_GUARD);
    }

    /** Factory method for immediate, unconditional success. */
    public CtrlAut buildTrue() {
        CtrlAut result = createCtrlAut("true");
        return addOmega(result);
    }

    /** Adds an unconditional terminating transition between start and final state. */
    public CtrlAut addOmega(CtrlAut result) {
        result.addTransition(result.getStart(), createOmegaLabel(),
            result.getFinal());
        return result;
    }

    /** 
     * Adds a second control automaton as <i>else</i> parameter in
     * a <i>try</i> construct with the first automaton as try block.
     */
    public CtrlAut buildTryElse(CtrlAut first, CtrlAut second) {
        if (second == null) {
            second = buildTrue();
        }
        return buildOr(first, second, first.getStart().getInit());
    }

    /**
     * Constructs an until automaton using a given automaton as condition 
     * and adding a second automaton as until body.
     * @param first the condition automaton; contains the result upon return
     * @param second the until body automaton
     */
    public CtrlAut buildUntilDo(CtrlAut first, CtrlAut second) {
        // get the automaton guard before the omegas are removed
        Set<CtrlCall> autGuard = first.getStart().getInit();
        if (autGuard != null) {
            // remove omega-transitions from first
            Set<CtrlTransition> firstOmega = removeOmegas(first);
            // build the until loop
            buildOr(first, second, autGuard);
            buildLoop(first, null);
            // re-attach the omega-transitions
            for (CtrlTransition omega : firstOmega) {
                first.addTransition(omega);
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
    public CtrlAut buildWhileDo(CtrlAut first, CtrlAut second) {
        // get the automaton guard before the omegas are removed
        Set<CtrlCall> autGuard = first.getStart().getInit();
        // sequentially compose first and second
        buildSeq(first, second);
        return buildLoop(first, autGuard);
    }

    /** 
     * Loops a given control automaton, while terminating under a 
     * predefined guard.
     * The result is constructed by modifying the parameter.
     */
    private CtrlAut buildLoop(CtrlAut aut, Set<CtrlCall> guard) {
        Set<CtrlTransition> omegas = removeOmegas(aut);
        // loop back from final to post-initial states
        for (CtrlTransition omega : omegas) {
            // create cycles for all original omega transitions
            for (CtrlTransition init : aut.getStart().getTransitions()) {
                CtrlLabel newLabel =
                    createLabel(init.label(), omega.label().getGuard());
                aut.addTransition(omega.source(), newLabel, init.target());
            }
            // create new omega transitions if the automaton guard is non-degenerate
            if (guard != null) {
                CtrlLabel newLabel = createLabel(omega.label(), guard);
                aut.addTransition(omega.source(), newLabel, aut.getFinal());
            }
        }
        if (guard != null) {
            CtrlLabel newLabel = createLabel(CtrlCall.OMEGA, guard);
            aut.addTransition(aut.getStart(), newLabel, aut.getFinal());
        }
        return aut;
    }

    /** 
     * Adds a second control automaton as alternative, reachable 
     * under a given guard. The guard may be {@code null}, meaning that
     * the second automaton is unreachable.
     */
    private CtrlAut buildOr(CtrlAut first, CtrlAut second,
            Collection<CtrlCall> guard) {
        // if the guard is degenerate, the second automaton is unreachable
        if (guard != null) {
            List<FormatError> errors = new ArrayList<FormatError>();
            Map<CtrlState,CtrlState> secondToFirstMap =
                copyStates(second, first, null);
            // copy transitions from second to first
            for (CtrlTransition trans : second.edgeSet()) {
                CtrlState sourceImage = secondToFirstMap.get(trans.source());
                CtrlState targetImage = secondToFirstMap.get(trans.target());
                CtrlLabel label = trans.label();
                // initial transitions have to be treated separately
                if (sourceImage.equals(first.getStart())) {
                    // create an augmented transition, 
                    CtrlLabel newLabel = createLabel(label, guard);
                    first.addTransition(sourceImage, newLabel, targetImage);
                } else {
                    first.addTransition(sourceImage, label, targetImage);
                }
            }
            first.getInfo().addErrors(errors);
        }
        return first;
    }

    /** 
     * Copies all non-start and non-final states of a given automaton to
     * another, and returns the mapping from original to new states.
     * @param fromAut the automaton from which states are copied
     * @param toAut the automaton to which states are copied
     * @param recipe optional recipe of which the copied states are part
     * @return a map from states in {@code fromAut} to new states in {@code toAut}
     */
    private Map<CtrlState,CtrlState> copyStates(CtrlAut fromAut, CtrlAut toAut,
            Recipe recipe) {
        Map<CtrlState,CtrlState> secondToFirstMap =
            new HashMap<CtrlState,CtrlState>();
        for (CtrlState state : fromAut.nodeSet()) {
            CtrlState image;
            if (state.equals(fromAut.getStart())) {
                image = toAut.getStart();
            } else if (state.equals(fromAut.getFinal())) {
                image = toAut.getFinal();
            } else {
                image = toAut.copyState(state, recipe);
            }
            secondToFirstMap.put(state, image);
        }
        toAut.getInfo().addErrors(fromAut.getInfo().getErrors());
        return secondToFirstMap;
    }

    /** Removes and returns the set of omega transitions from a given automaton. */
    private Set<CtrlTransition> removeOmegas(CtrlAut first) {
        Set<CtrlTransition> firstOmega =
            new HashSet<CtrlTransition>(first.getOmegaTransitions());
        for (CtrlTransition omega : firstOmega) {
            first.removeTransition(omega);
        }
        return firstOmega;
    }

    /**
     * Factory method extending the guard of an existing (virtual) control label.
     */
    private CtrlLabel createLabel(CtrlLabel orig,
            Collection<CtrlCall> extraGuards) {
        CtrlCall origCall = orig.getCall();
        Set<CtrlCall> newGuards = new LinkedHashSet<CtrlCall>(extraGuards);
        newGuards.addAll(orig.getGuard());
        return new CtrlLabel(origCall, newGuards, orig.getRecipe(),
            orig.isStart());
    }

    /** Factory method for control labels with an empty guard. */
    private CtrlLabel createLabel(CtrlCall call) {
        return createLabel(call, EMPTY_GUARD);
    }

    /** Factory method for control labels. */
    private CtrlLabel createLabel(CtrlCall call, Collection<CtrlCall> guard) {
        return new CtrlLabel(call, guard, null, true);
    }

    /** Factory method for omega control labels, with an empty guard. */
    private CtrlLabel createOmegaLabel() {
        return createLabel(CtrlCall.OMEGA);
    }

    /** Builds the default control automaton for a set of actions. */
    public CtrlAut buildDefault(Collection<? extends Action> actions)
        throws FormatException {
        CtrlAut result = new CtrlAut("control");
        Set<FormatError> errors = new HashSet<FormatError>();
        Map<Integer,Set<Action>> priorityMap =
            new HashMap<Integer,Set<Action>>();
        Namespace namespace = new Namespace();
        // first add the names and signatures to the namespace
        for (Action action : actions) {
            boolean needsInput = false;
            for (CtrlPar.Var var : action.getSignature()) {
                if (var.isInOnly()) {
                    needsInput = true;
                    break;
                }
            }
            if (needsInput) {
                errors.add(new FormatError(
                    "Grammar needs explicit control for input action '%s'",
                    action.getFullName()));
                continue;
            }
            if (action instanceof Rule) {
                namespace.addRule((Rule) action);
            } else {
                namespace.addRecipe(action.getFullName(), action.getPriority(),
                    action.getSignature(), null);
                namespace.addBody(action.getFullName(),
                    ((Recipe) action).getBody());
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
        List<CtrlAut> prioAutList = new ArrayList<CtrlAut>();
        for (Set<Action> priorityActions : priorityMap.values()) {
            // collect the names
            Set<String> actionNames = new HashSet<String>();
            for (Action action : priorityActions) {
                actionNames.add(action.getFullName());
            }
            prioAutList.add(buildGroupCall(actionNames, namespace));
        }
        if (prioAutList.isEmpty()) {
            addOmega(result);
        } else {
            ListIterator<CtrlAut> levelIter =
                prioAutList.listIterator(prioAutList.size());
            while (levelIter.hasPrevious()) {
                result = buildTryElse(result, levelIter.previous());
            }
            result = buildAlap(result);
        }
        result = result.normalise();
        result.setDefault();
        return result;
    }

    /** Constructs an empty control automaton. 
     * @param name name of the new automaton
     */
    private CtrlAut createCtrlAut(String name) {
        return new CtrlAut(name);
    }

    /** Returns the singleton instance of this class. */
    public static CtrlFactory instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    private static final CtrlFactory INSTANCE = new CtrlFactory();
    /** Constant empty set of guard rule names. */
    private static final Collection<CtrlCall> EMPTY_GUARD =
        Collections.<CtrlCall>emptyList();
}
