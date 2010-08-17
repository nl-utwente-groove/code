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
    /** Factory method for a single rule call. */
    public CtrlAut buildCall(Map<String,CtrlVar> context, String ruleName,
            List<CtrlArg> args) {
        CtrlAut result = new CtrlAut();
        CtrlState middle = result.addState();
        // convert the call arguments using the context
        result.addTransition(result.getStart(), createLabel(ruleName, args,
            EMPTY_GUARD), middle);
        result.addTransition(middle, createOmegaLabel(EMPTY_GUARD),
            result.getFinal());
        return result;
    }

    /** Factory method for immediate, unconditional success. */
    public CtrlAut buildTrue() {
        CtrlAut result = new CtrlAut();
        result.addTransition(result.getStart(), createOmegaLabel(EMPTY_GUARD),
            result.getFinal());
        return result;
    }

    /** Adds a second control automaton sequentially after a given automaton. */
    public void buildSequential(CtrlAut first, CtrlAut second) {
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

    /** 
     * Adds a second control automaton as <i>else</i> parameter in
     * a <i>try</i> construct with the first automaton as try block.
     */
    public void buildTryElse(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
        // get the extra guard resulting from refusing first
        Set<String> firstInit = first.getStart().getInit();
        // copy transitions from second to first
        for (CtrlTransition trans : second.edgeSet()) {
            CtrlState sourceImage = secondToFirstMap.get(trans.source());
            CtrlState targetImage = secondToFirstMap.get(trans.target());
            CtrlLabel label = trans.label();
            // initial transitions have to be treated separately
            if (sourceImage.equals(first.getStart())) {
                // create and augmented transition, or
                // no transition at all if omega is among the first init
                if (firstInit != null) {
                    CtrlLabel newLabel = createLabel(label, firstInit);
                    first.addTransition(sourceImage, newLabel, targetImage);
                }
            } else {
                first.addTransition(sourceImage, label, targetImage);
            }
        }
    }

    /** Adds a second control automaton as alternative to a given one. */
    public void buildOr(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
        // copy transitions from second to first
        for (CtrlTransition trans : second.edgeSet()) {
            CtrlState sourceImage = secondToFirstMap.get(trans.source());
            CtrlState targetImage = secondToFirstMap.get(trans.target());
            first.addTransition(sourceImage, trans.label(), targetImage);
        }
    }

    /** 
     * Closes a given control automaton under the <i>as long as possible</i>
     * operator.
     */
    public void buildAlap(CtrlAut aut) {
        // get the automaton guard before the omegas are removed
        Set<String> autGuard = aut.getStart().getInit();
        Set<CtrlTransition> omegas = removeOmegas(aut);
        // copy the initial transitions to avoid concurrent modification exceptions
        Set<CtrlTransition> inits =
            new HashSet<CtrlTransition>(aut.getStart().getTransitions());
        for (CtrlTransition omega : omegas) {
            // create cycles for all original omega transitions
            for (CtrlTransition init : inits) {
                CtrlLabel newLabel =
                    createLabel(init.label(), omega.label().getGuardNames());
                aut.addTransition(omega.source(), newLabel, init.target());
            }
            // create new omega transitions if the automaton guard is non-degenerate
            if (autGuard != null) {
                CtrlLabel newLabel = createLabel(omega.label(), autGuard);
                aut.addTransition(omega.source(), newLabel, aut.getFinal());
            }
        }
    }

    /**
     * Constructs a while automaton using a given automaton as condition 
     * and adding a second automaton as while block.
     */
    public void buidWhile(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
        // get the automaton guard before the omegas are removed
        Set<String> autGuard = first.getStart().getInit();
        Set<CtrlTransition> omegas = removeOmegas(first);
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

    /** Factory method for virtual control labels. */
    private CtrlLabel createLabel(String ruleName, List<CtrlArg> args,
            Collection<String> guard) {
        return new CtrlLabel(ruleName, args, guard);
    }

    /** Factory method for omega control labels. */
    private CtrlLabel createOmegaLabel(Collection<String> guard) {
        return new CtrlLabel(guard);
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
