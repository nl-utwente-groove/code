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

import groove.view.FormatException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlFactory {
    /** 
     * Closes a given control automaton under the <i>as long as possible</i>
     * operator.
     */
    public void buildAlap(CtrlAut aut) {
        buildLoop(aut, aut.getStart().getInit());
    }

    /** Factory method for a single rule call. */
    public CtrlAut buildCall(Map<String,CtrlVar> context, String ruleName,
            List<CtrlArg> args) {
        CtrlAut result = new CtrlAut();
        CtrlState middle = result.addState();
        // convert the call arguments using the context
        result.addTransition(result.getStart(), createLabel(ruleName, args),
            middle);
        result.addTransition(middle, createOmegaLabel(), result.getFinal());
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
    public void buildDoUntil(CtrlAut first, CtrlAut second) {
        buildUntilDo(second, first);
        buildSeq(first, second);
    }

    /**
     * Builds an automaton for a <i>do-while</i> construct.
     * The result is constructed by modifying the first parameter.
     * The second parameter is also modified.
     * @param first the body of the loop; contains the result upon return
     * @param second the condition of the loop; modified in the course 
     * of the construction
     */
    public void buildDoWhile(CtrlAut first, CtrlAut second) {
        buildWhileDo(second, first);
        buildSeq(first, second);
    }

    /** 
     * Builds an <i>if-then-else</i> construct out of three automata,
     * by modifying the first of the three.
     */
    public void buildIfThenElse(CtrlAut first, CtrlAut second, CtrlAut third) {
        Set<String> guard = first.getStart().getInit();
        buildSeq(first, second);
        buildOr(first, third, guard);
    }

    /**
     * Builds a new automaton by replacing all invocations of a function
     * (encoded as rule calls to a given rule name) by the function body.
     * The start state of the function should bind all input arguments of
     * the calls, and the final state should bind all output arguments.
     * The result is constructed by modifying the first parameter.
     * @param first the calling automaton
     * @param functionName the rule name of the calls to be replaced
     * @param second the called automaton
     */
    public void buildInvoke(CtrlAut first, String functionName, CtrlAut second) {

    }

    /** Adds a second control automaton sequentially after a given automaton. 
     * The result is constructed by modifying the first parameter.
     * @param first the automaton to be executed first; contains the result upon return
     * @param second the automaton to be executed second
     */
    public void buildSeq(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
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
                        createLabel(label, omega.label().getGuardNames());
                    first.addTransition(omega.source(), newLabel, targetImage);
                }
            } else {
                first.addTransition(sourceImage, label, targetImage);
            }
        }
    }

    /** Adds a second control automaton as alternative to a given one. */
    public void buildOr(CtrlAut first, CtrlAut second) {
        buildOr(first, second, Collections.<String>emptySet());
    }

    /** Factory method for immediate, unconditional success. */
    public CtrlAut buildTrue() {
        CtrlAut result = new CtrlAut();
        result.addTransition(result.getStart(), createOmegaLabel(),
            result.getFinal());
        return result;
    }

    /** 
     * Adds a second control automaton as <i>else</i> parameter in
     * a <i>try</i> construct with the first automaton as try block.
     */
    public void buildTryElse(CtrlAut first, CtrlAut second) {
        buildOr(first, second, first.getStart().getInit());
    }

    /**
     * Constructs an until automaton using a given automaton as condition 
     * and adding a second automaton as until body.
     * @param first the condition automaton; contains the result upon return
     * @param second the until body automaton
     */
    public void buildUntilDo(CtrlAut first, CtrlAut second) {
        // get the automaton guard before the omegas are removed
        Set<String> autGuard = first.getStart().getInit();
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
    }

    /**
     * Constructs a while automaton using a given automaton as condition 
     * and adding a second automaton as while body.
     * The result is constructed by modifying the first parameter.
     * @param first the condition automaton; contains the result upon return
     * @param second the while body automaton
     */
    public void buildWhileDo(CtrlAut first, CtrlAut second) {
        // get the automaton guard before the omegas are removed
        Set<String> autGuard = first.getStart().getInit();
        // sequentially compose first and second
        buildSeq(first, second);
        buildLoop(first, autGuard);
    }

    /** 
     * Minimises a given automaton, and tests it for determinism.
     * Also reduces the bound variables in the states to those used
     * in later outgoing transitions.
     * The resulting automaton may fail to satisfy the assumption that
     * the start state has no incoming transitions, and hence it should no
     * longer be used in constructions.
     * @param aut the automaton to be minimised
     * @throws FormatException if the automaton is not deterministic
     */
    public void minimise(CtrlAut aut) throws FormatException {

    }

    /** 
     * Loops a given control automaton, while terminating under a 
     * predefined guard.
     * The result is constructed by modifying the parameter.
     */
    private void buildLoop(CtrlAut aut, Set<String> guard) {
        Set<CtrlTransition> omegas = removeOmegas(aut);
        // copy transitions from second to first
        for (CtrlTransition omega : omegas) {
            // create cycles for all original omega transitions
            for (CtrlTransition init : aut.getStart().getTransitions()) {
                CtrlLabel newLabel =
                    createLabel(init.label(), omega.label().getGuardNames());
                aut.addTransition(omega.source(), newLabel, init.target());
            }
            // create new omega transitions if the automaton guard is non-degenerate
            if (guard != null) {
                CtrlLabel newLabel = createLabel(omega.label(), guard);
                aut.addTransition(omega.source(), newLabel, aut.getFinal());
            }
        }
    }

    /** 
     * Adds a second control automaton as alternative, reachable 
     * under a given guard. The guard may be {@code null}, meaning that
     * the second automaton is unreachable.
     */
    private void buildOr(CtrlAut first, CtrlAut second, Set<String> guard) {
        // if the guard is degenerate, the second automaton is unreachable
        if (guard == null) {
            Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
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
        }
    }

    /** 
     * Copies all non-start and non-final states of a given automaton to
     * another, and returns the mapping from original to new states.
     * @param fromAut the automaton from which states are copied
     * @param toAut the automaton to which states are copied
     * @return a map from states in {@code fromAut} to new states in {@code toAut}
     */
    private Map<CtrlState,CtrlState> copyAut(CtrlAut fromAut, CtrlAut toAut) {
        Map<CtrlState,CtrlState> secondToFirstMap =
            new HashMap<CtrlState,CtrlState>();
        for (CtrlState state : fromAut.nodeSet()) {
            CtrlState image;
            if (state.equals(fromAut.getStart())) {
                image = toAut.getStart();
            } else if (state.equals(fromAut.getFinal())) {
                image = toAut.getFinal();
            } else {
                image = toAut.addState();
            }
            secondToFirstMap.put(state, image);
        }
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
    private CtrlLabel createLabel(CtrlLabel orig, Collection<String> extraGuards) {
        CtrlCall origCall = orig.getCall();
        Set<String> newGuards = new LinkedHashSet<String>(extraGuards);
        newGuards.addAll(orig.getGuardNames());
        return new CtrlLabel(origCall.getRuleName(), origCall.getArgs(),
            newGuards);
    }

    /** Factory method for virtual control labels, with an empty guard. */
    private CtrlLabel createLabel(String ruleName, List<CtrlArg> args) {
        return new CtrlLabel(ruleName, args, EMPTY_GUARD);
    }

    /** Factory method for omega control labels, with an empty guard. */
    private CtrlLabel createOmegaLabel() {
        return new CtrlLabel(EMPTY_GUARD);
    }

    /** Returns the singleton instance of this factory class. */
    public static CtrlFactory getInstance() {
        return instance;
    }

    /** The singleton instance of this factory. */
    private static final CtrlFactory instance = new CtrlFactory();
    /** Constant empty set of guard rule names. */
    private static final Collection<String> EMPTY_GUARD =
        Collections.<String>emptyList();
}
