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

import groove.control.parse.NamespaceNew;
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
    public CtrlAut buildCall(CtrlCall call, NamespaceNew namespace) {
        if (call.isRule() || call.isOmega()) {
            return buildRuleCall(call);
        } else {
            return buildFunctionCall(call, namespace);
        }
    }

    /** Factory method for a rule call. */
    private CtrlAut buildRuleCall(CtrlCall call) {
        CtrlAut result = new CtrlAut();
        CtrlState middle = result.addState();
        // convert the call arguments using the context
        result.addTransition(result.getStart(), createLabel(call), middle);
        result.addTransition(middle, createOmegaLabel(), result.getFinal());
        return result;
    }

    /** Factory method for a function call. */
    private CtrlAut buildFunctionCall(CtrlCall call, NamespaceNew namespace) {
        String name = call.getFunction();
        CtrlAut result = namespace.getFunctionBody(name);
        List<CtrlPar.Var> sig = namespace.getSig(name);
        assert sig.isEmpty() : "Function parameters not yet implemented";
        return result.copy();
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
    public CtrlAut buildAny(NamespaceNew namespace) {
        return buildGroupCall(namespace.getAllRules(), namespace);
    }

    /** Builds an automation for an {@code other}-call. */
    public CtrlAut buildOther(NamespaceNew namespace) {
        Set<String> unusedRules = new HashSet<String>(namespace.getAllRules());
        unusedRules.removeAll(namespace.getUsedRules());
        return buildGroupCall(unusedRules, namespace);
    }

    /** Builds an automation for a choice between a set of rules. */
    private CtrlAut buildGroupCall(Set<String> ruleNames, NamespaceNew namespace) {
        CtrlAut result = null;
        for (String ruleName : ruleNames) {
            CtrlAut callAut =
                buildCall(new CtrlCall(namespace.getRule(ruleName), null), null);
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

    /**
     * Builds a new automaton by replacing all invocations of a function
     * (encoded as control calls to a given name) by the function body.
     * The result is constructed by modifying the first parameter.
     * @param first the calling automaton
     * @param name the function name of the calls to be replaced
     * @param second the called automaton
     * @throws FormatException if one of the calls is not compatible with the function declaration 
     */
    public CtrlAut buildInvoke(CtrlAut first, String name, CtrlAut second)
        throws FormatException {
        Set<FormatError> errors = new TreeSet<FormatError>();
        // copy the transition set to avoid concurrent modification exceptions
        Set<CtrlTransition> firstTrans =
            new HashSet<CtrlTransition>(first.edgeSet());
        Set<CtrlCall> funcInit = second.getStart().getInit();
        for (CtrlTransition trans : firstTrans) {
            CtrlLabel label = trans.label();
            CtrlCall call = label.getCall();
            if (call.isFunction() && call.getFunction().equals(name)) {
                // inline the function body for function calls
                try {
                    buildReplace(first, trans, second);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            } else if (label.hasGuardCall(name)) {
                // replace by the initial actions of the function
                first.removeTransition(trans);
                // only add a new transition if the function may not
                // immediately terminate
                if (funcInit != null) {
                    Collection<CtrlCall> newGuard =
                        new LinkedHashSet<CtrlCall>(label.getGuard());
                    newGuard.remove(name);
                    newGuard.addAll(funcInit);
                    CtrlLabel newLabel = createLabel(call, newGuard);
                    first.addTransition(trans.source(), newLabel,
                        trans.target());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return first;
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
        CtrlCall call = callTrans.getCall();
        String name = call.getFunction();
        assert name != null : String.format("%s is not a function call", call);
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
        Map<CtrlState,CtrlState> secondToFirstMap = copyStates(second, first);
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
        Collection<CtrlCall> callGuard = callTrans.label().getGuard();
        for (CtrlTransition funcTrans : second.edgeSet()) {
            CtrlLabel transLabel = funcTrans.label();
            CtrlCall transCall = transLabel.getCall();
            CtrlState newSource = secondToFirstMap.get(funcTrans.source());
            if (transCall.isOmega()) {
                Collection<CtrlCall> newGuard =
                    new LinkedHashSet<CtrlCall>(transLabel.getGuard());
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

    /** 
     * Renames a control label by adapting the arguments in its call and guard
     * @param label the label to be renamed
     * @param argMap mapping from control variables to arguments
     * @param prefix text used to prefix the control variables if {@code argMap} does
     * not provide an image
     * @return a new label where the control variables have been prefixed.
     */
    private CtrlLabel renameLabel(CtrlLabel label, Map<CtrlVar,CtrlPar> argMap,
            String prefix) {
        Collection<CtrlCall> newGuard = new LinkedHashSet<CtrlCall>();
        for (CtrlCall guardCall : label.getGuard()) {
            newGuard.add(renameCall(guardCall, argMap, prefix));
        }
        return createLabel(renameCall(label.getCall(), argMap, prefix),
            newGuard);
    }

    /** 
     * Renames a control call by adapting its arguments.
     * @param call the call to be renamed
     * @param argMap mapping from control variables to arguments
     * @param prefix text used to prefix the control variables if {@code argMap} does
     * not provide an image
     */
    private CtrlCall renameCall(CtrlCall call, Map<CtrlVar,CtrlPar> argMap,
            String prefix) {
        List<CtrlPar> newArgs = new ArrayList<CtrlPar>();
        for (CtrlPar arg : call.getArgs()) {
            newArgs.add(renameArg(arg, argMap, prefix));
        }
        return call.copy(newArgs);
    }

    /** 
     * Renames a control argument by replacing any control variable.
     * The new variable is looked up in a map; if this does not provide
     * an image, the old variable name is prefixed.
     * @param arg the argument to be renamed
     * @param argMap mapping from control variables to arguments
     * @param prefix text used to prefix the control variable if {@code argMap} does
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

    /** Adds a second control automaton sequentially after a given automaton. 
     * The result is constructed by modifying the first parameter.
     * @param first the automaton to be executed first; contains the result upon return
     * @param second the automaton to be executed second
     */
    public CtrlAut buildSeq(CtrlAut first, CtrlAut second) {
        Map<CtrlState,CtrlState> secondToFirstMap = copyStates(second, first);
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
        CtrlAut result = new CtrlAut();
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
            Map<CtrlState,CtrlState> secondToFirstMap =
                copyStates(second, first);
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
        return first;
    }

    /** 
     * Copies all non-start and non-final states of a given automaton to
     * another, and returns the mapping from original to new states.
     * @param fromAut the automaton from which states are copied
     * @param toAut the automaton to which states are copied
     * @return a map from states in {@code fromAut} to new states in {@code toAut}
     */
    private Map<CtrlState,CtrlState> copyStates(CtrlAut fromAut, CtrlAut toAut) {
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
    private CtrlLabel createLabel(CtrlLabel orig,
            Collection<CtrlCall> extraGuards) {
        CtrlCall origCall = orig.getCall();
        Set<CtrlCall> newGuards = new LinkedHashSet<CtrlCall>(extraGuards);
        newGuards.addAll(orig.getGuard());
        return new CtrlLabel(origCall, newGuards);
    }

    /** Factory method for control labels with an empty guard. */
    private CtrlLabel createLabel(CtrlCall call) {
        return createLabel(call, EMPTY_GUARD);
    }

    /** Factory method for control labels. */
    private CtrlLabel createLabel(CtrlCall call, Collection<CtrlCall> guard) {
        return new CtrlLabel(call, guard);
    }

    /** Factory method for omega control labels, with an empty guard. */
    private CtrlLabel createOmegaLabel() {
        return createLabel(CtrlCall.OMEGA);
    }

    /** Returns the singleton instance of this factory class. */
    public static CtrlFactory getInstance() {
        return instance;
    }

    /** The singleton instance of this factory. */
    private static final CtrlFactory instance = new CtrlFactory();
    /** Constant empty set of guard rule names. */
    private static final Collection<CtrlCall> EMPTY_GUARD =
        Collections.<CtrlCall>emptyList();
}
