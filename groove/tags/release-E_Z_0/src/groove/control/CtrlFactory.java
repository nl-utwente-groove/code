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

import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
            List<CtrlPar> args) {
        CtrlAut result = new CtrlAut();
        CtrlState middle = result.addState();
        // convert the call arguments using the context
        result.addTransition(result.getStart(),
            createLabel(ruleName, args, EMPTY_GUARD), middle);
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
     * The result is constructed by modifying the first parameter.
     * @param first the calling automaton
     * @param ruleName the rule name of the calls to be replaced
     * @param second the called automaton
     * @throws FormatException if one of the calls is not compatible with the function declaration 
     */
    public void buildInvoke(CtrlAut first, String ruleName, CtrlAut second)
        throws FormatException {
        Set<FormatError> errors = new TreeSet<FormatError>();
        // copy the transition set to avoid concurrent modification exceptions
        Set<CtrlTransition> firstTrans =
            new HashSet<CtrlTransition>(first.edgeSet());
        Set<String> funcInit = second.getStart().getInit();
        for (CtrlTransition trans : firstTrans) {
            CtrlLabel label = trans.label();
            CtrlCall call = label.getCall();
            if (call.getRuleName().equals(ruleName)) {
                // inline the function body for function calls
                try {
                    buildReplace(first, trans, second);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            } else if (label.getGuardNames().contains(ruleName)) {
                // replace by the initial actions of the function
                first.removeTransition(trans);
                // only add a new transition if the function may not
                // immediately terminate
                if (funcInit != null) {
                    Collection<String> newGuard =
                        new LinkedHashSet<String>(label.getGuardNames());
                    newGuard.remove(ruleName);
                    newGuard.addAll(funcInit);
                    CtrlLabel newLabel =
                        createLabel(call.getRuleName(), call.getArgs(),
                            newGuard);
                    first.addTransition(trans.source(), newLabel,
                        trans.target());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /**
     * Builds a new automaton by replacing a given invocation of a function
     * (encoded as a control transition) by the function body.
     * The start state of the function should bind all input arguments of
     * the calls, and the final state should bind all output arguments.
     * The result is constructed by modifying the first parameter.
     * @param first the calling automaton
     * @param callTrans the transition to be replaced
     * @param second the called automaton
     * @throws FormatException if the call is not compatible with the function declaration 
     */
    private void buildReplace(CtrlAut first, CtrlTransition callTrans,
            CtrlAut second) throws FormatException {
        first.removeTransition(callTrans);
        CtrlCall call = callTrans.label().getCall();
        String name = call.getRuleName();
        // check compatibility of the arguments with the function parameters.
        List<CtrlPar.Var> funcPars = second.getPars();
        assert funcPars != null : String.format(
            "Function body of '%s' has no parameters", name);
        List<CtrlPar> callArgs = call.getArgs();
        if (callArgs != null && callArgs.size() != funcPars.size()) {
            throw new FormatException(
                "Function call '%s' should have %d arguments", call,
                funcPars.size());
        }
        Set<FormatError> errors = new TreeSet<FormatError>();
        Map<CtrlVar,CtrlPar> argMap = new HashMap<CtrlVar,CtrlPar>();
        for (int i = 0; i < funcPars.size(); i++) {
            if (funcPars.get(i).compatibleWith(callArgs.get(i))) {
                argMap.put(funcPars.get(i).getVar(), callArgs.get(i));
            } else {
                errors.add(new FormatError(
                    "Function call '%s': argument '%s' not compatible with parameter",
                    call, callArgs.get(i)));
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        Map<CtrlState,CtrlState> secondToFirstMap = copyAut(second, first);
        // change the variable names in the function states
        // to ensure disjointness with the caller's variables
        for (CtrlState funcState : secondToFirstMap.values()) {
            Collection<CtrlVar> newVars = new LinkedHashSet<CtrlVar>();
            for (CtrlVar var : funcState.getBoundVars()) {
                newVars.add(renameVar(var, argMap, name));
            }
            funcState.setBoundVars(newVars);
        }
        // now copy the transitions
        Collection<String> callGuard = callTrans.label().getGuardNames();
        for (CtrlTransition funcTrans : second.edgeSet()) {
            CtrlLabel transLabel = funcTrans.label();
            CtrlCall transCall = transLabel.getCall();
            CtrlState newSource = secondToFirstMap.get(funcTrans.source());
            if (transCall.isOmega()) {
                Collection<String> newGuard =
                    new LinkedHashSet<String>(transLabel.getGuardNames());
                if (newSource == null) {
                    // the function can immediately terminate
                    newSource = callTrans.source();
                    newGuard.addAll(callGuard);
                }
                // add to the call source outgoing transitions from the call target
                for (CtrlTransition targetTrans : callTrans.target().getTransitions()) {
                    first.addTransition(newSource,
                        createLabel(targetTrans.label(), newGuard),
                        targetTrans.target());
                }
            } else {
                CtrlState newTarget = secondToFirstMap.get(funcTrans.target());
                CtrlLabel newLabel = renameLabel(transLabel, argMap, name);
                if (newSource == null) {
                    // this is an initial transition of the function
                    newSource = callTrans.source();
                    newLabel = createLabel(newLabel, callGuard);
                }
                first.addTransition(newSource, newLabel, newTarget);
            }
        }
    }

    private CtrlLabel renameLabel(CtrlLabel label, Map<CtrlVar,CtrlPar> argMap,
            String prefix) {
        CtrlLabel result;
        CtrlCall call = label.getCall();
        List<CtrlPar> transArgs = call.getArgs();
        if (transArgs == null) {
            result = label;
        } else {
            List<CtrlPar> newArgs = new ArrayList<CtrlPar>();
            for (CtrlPar arg : transArgs) {
                newArgs.add(renameArg(arg, argMap, prefix));
            }
            result =
                createLabel(call.getRuleName(), newArgs, label.getGuardNames());
        }
        return result;
    }

    /** 
     * Renames a control variable for the purpose of inlining a function
     * call.
     * The new variable is looked up in a map from function parameters to call
     * arguments. If the image is a constant or wildcard, {@code null} is 
     * returned, otherwise it is a variable, in which case that is used.
     * If the map does not provide
     * an image, the old variable name is prefixed.
     * @param var the variable argument to be renamed
     * @param argMap the map providing an image of {@code varArg}
     * @param prefix text used to prefix the old name if {@code argMap} does
     * not provide an image
     */
    private CtrlVar renameVar(CtrlVar var, Map<CtrlVar,CtrlPar> argMap,
            String prefix) {
        CtrlPar argImage = argMap.get(var);
        if (argImage == null) {
            return new CtrlVar(prefix + ":" + var.getName(), var.getType());
        } else if (argImage instanceof CtrlPar.Var) {
            return ((CtrlPar.Var) argImage).getVar();
        } else {
            // the variable gets mapped to a constant or wildcard
            return null;
        }
    }

    /** 
     * Renames a control argument by adapting any control variable.
     * The new variable is looked up in a map; if this does not provide
     * an image, the old variable name is prefixed.
     * @param arg the argument to be renamed
     * @param argMap the map providing an image of {@code varArg}
     * @param prefix text used to prefix the old name if {@code argMap} does
     * not provide an image
     */
    private CtrlPar renameArg(CtrlPar arg, Map<CtrlVar,CtrlPar> argMap,
            String prefix) {
        CtrlPar result;
        if (arg instanceof CtrlPar.Var) {
            CtrlPar.Var varArg = (CtrlPar.Var) arg;
            result = argMap.get(varArg.getVar());
            if (result == null) {
                CtrlVar newVar = renameVar(varArg.getVar(), argMap, prefix);
                result = new CtrlPar.Var(newVar, varArg.isInOnly());
            }
        } else {
            result = arg;
        }
        return result;
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
        // TODO to be provided
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

    /** Factory method for virtual control labels. */
    private CtrlLabel createLabel(String ruleName, List<CtrlPar> args,
            Collection<String> guard) {
        return new CtrlLabel(ruleName, args, guard);
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
