/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control.template;

import groove.control.Call;
import groove.control.Procedure;
import groove.control.term.CallTerm;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
import groove.grammar.Action;
import groove.grammar.Callable;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.util.Duo;
import groove.util.Fixable;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Control program, consisting of a main term
 * and a map from procedure names to procedures.
 * A program can be fixed, after which it has a main template
 * and a map from procedure names to procedure templates.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Program implements Fixable {
    /**
     * Constructs an unnamed, initially empty program.
     */
    public Program() {
        this.properties = new ArrayList<>();
    }

    /** Tests if the main term of this program has been set. */
    public boolean hasMain() {
        return getMain() != null;
    }

    /**
     * Returns the main block of this program.
     * This is never {@code null} if the program is fixed.
     */
    public Term getMain() {
        return this.main;
    }

    /**
     * Returns the name of the fragment containing the main body, if any.
     * @return the name of the fragment containing the main body, of {@code null} if
     * this program has no (non-trivial) main body
     */
    public QualName getMainName() {
        return this.mainName;
    }

    /** The name of the sub-program containing the main body, if any. */
    private QualName mainName;
    /** The main body of this program, if any. */
    private Term main;

    /** Returns an unmodifiable view on the map from names to procedures
     * defined in this program.
     * Should only be invoked after the program is fixed.
     */
    public Map<QualName,Procedure> getProcs() {
        assert isFixed();
        return Collections.unmodifiableMap(this.procs);
    }

    /** Returns the procedure for a given name.
     * Should only be invoked after the program is fixed.
     */
    public Procedure getProc(QualName name) {
        assert isFixed();
        return this.procs.get(name);
    }

    private final Map<QualName,Procedure> procs = new LinkedHashMap<>();

    /** Sets the property actions to be checked at each non-transient state. */
    public void setProperties(Collection<Action> properties) {
        assert this.properties.isEmpty();
        this.properties.addAll(properties);
    }

    /** Returns the list of property actions to be checked at each non-transient state. */
    public List<Action> getProperties() {
        return this.properties;
    }

    /** The property actions to be checked at each non-transient state. */
    private final List<Action> properties;

    /** Returns the main template of this program, if any.
     * Should only be invoked after the program is fixed.
     * May be {@code null} if this program has no main body.
     */
    public Template getTemplate() {
        assert isFixed();
        return this.template;
    }

    private Template template;

    /**
     * Adds all procedures of another, disjoint program to this one.
     * This program may not be fixed.
     * @param other the program to be added
     * @throws FormatException if both {@code this} and {@code other} have a non-trivial main body,
     * or the defined procedures overlap
     */
    public void add(Fragment other) throws FormatException {
        assert !isFixed();
        FormatErrorSet errors = new FormatErrorSet();
        if (this.main == null) {
            this.main = other.getMain();
            this.mainName = other.getControlName();
        } else if (other.hasMain()) {
            errors.add("Duplicate main: %s and %s", getMainName(), other.getControlName());
        }
        for (Procedure proc : other.getProcs()
            .values()) {
            try {
                addProc(proc);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        errors.throwException();
    }

    /**
     * Adds a procedure to this program.
     * @throws FormatException if a procedure with the same name has already been defined
     */
    private void addProc(Procedure proc) throws FormatException {
        assert proc != null;
        Procedure oldProc = this.procs.put(proc.getQualName(), proc);
        if (oldProc != null) {
            throw new FormatException("Duplicate procedure %s in %s and %s", proc.getQualName(),
                oldProc.getControlName(), proc.getControlName());
        }
    }

    @Override
    public boolean setFixed() throws FormatException {
        boolean result = this.fixed;
        if (!result) {
            this.fixed = true;
            FormatErrorSet errors = new FormatErrorSet();
            // check that there is a (possibly trivial) main
            if (this.main == null) {
                errors.add("Program does not have a main body");
            }
            // check that procedure bodies satisfy their requirements
            for (Procedure proc : this.procs.values()) {
                QualName name = proc.getQualName();
                String error = String.format("%s %s", proc.getKind()
                    .getName(true), name);
                if (getRecursion().contains(name)) {
                    errors.add(error + " has unguarded recursion", proc);
                }
                Term body = proc.getTerm();
                if (body.isFinal()) {
                    errors.add(error + " is empty", proc);
                }
                if (proc instanceof Recipe && mayFinalise(proc)) {
                    errors.add(error + " may immediately terminate", proc);
                }
                if (proc instanceof Recipe && !willTerminate(proc)) {
                    errors.add(error + " may fail to terminate", proc);
                }
            }
            errors.throwException();
            this.template = TemplateBuilder.instance(getProperties())
                .build(this);
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    private boolean fixed;

    /** Returns the set of procedure names with initial recursion. */
    public Set<QualName> getRecursion() {
        if (this.recursion == null) {
            this.recursion = computeRecursion();
        }
        return this.recursion;
    }

    private Set<QualName> recursion;

    /** Computes the set of procedure names with initial recursion. */
    private Set<QualName> computeRecursion() {
        Set<QualName> result = new TreeSet<>();
        // collect the unguarded calls into a map
        Map<QualName,Set<QualName>> calls = new HashMap<>();
        for (Procedure proc : this.procs.values()) {
            proc.setFixed();
            calls.put(proc.getQualName(), getUnguardedCalls(proc));
        }
        Set<QualName> changed = new HashSet<>(calls.keySet());
        // iterate until a fixpoint is reached
        do {
            Set<QualName> newChanged = new HashSet<>();
            // propagate the changes
            for (QualName callee : changed) {
                Set<QualName> childCallees = calls.get(callee);
                // add the childcallees to all callers of callee
                for (Map.Entry<QualName,Set<QualName>> callerEntry : calls.entrySet()) {
                    QualName caller = callerEntry.getKey();
                    Set<QualName> callees = callerEntry.getValue();
                    if (callees.contains(callee) && callees.addAll(childCallees)) {
                        // caller got a new callee
                        newChanged.add(caller);
                    }
                    if (callees.contains(caller)) {
                        // we have found a recursion
                        result.add(caller);
                    }
                }
            }
            changed = newChanged;
        } while (!changed.isEmpty());
        return result;
    }

    /** Returns the set of procedure names that appear on initial call switches. */
    private Set<QualName> getUnguardedCalls(Procedure proc) {
        Set<QualName> result = new HashSet<>();
        for (Call call : getInitCalls(proc.getTerm())) {
            Callable unit = call.getUnit();
            if (unit instanceof Procedure) {
                result.add(unit.getQualName());
            }
        }
        return result;
    }

    /** Recursively collects the initial calls of a term, after zero or more verdicts. */
    private Set<Call> getInitCalls(Term term) {
        Set<Call> result = new HashSet<>();
        int arity = term.getOp()
            .getArity();
        Term arg0 = arity >= 1 ? term.getArgs()[0] : null;
        Term arg1 = arity >= 2 ? term.getArgs()[1] : null;
        Term arg2 = arity >= 3 ? term.getArgs()[2] : null;
        Term arg3 = arity >= 4 ? term.getArgs()[3] : null;
        switch (term.getOp()) {
        case ALAP:
        case ATOM:
        case HASH:
        case STAR:
        case TRANSIT:
        case UNTIL:
            result = getInitCalls(arg0);
            break;
        case CALL:
            result.add(((CallTerm) term).getCall());
            break;
        case DELTA:
        case EPSILON:
            break;
        case IF:
            assert arg0 != null; // if has at least one argument
            result.addAll(getInitCalls(arg0));
            Finality condFinal = getFinality(arg0, getFinalityMap());
            if (condFinal.may()) {
                result.addAll(getInitCalls(arg1));
            }
            if (!arg0.isDead()) {
                result.addAll(getInitCalls(arg2));
            }
            if (!condFinal.will()) {
                result.addAll(getInitCalls(arg3));
            }
            break;
        case OR:
            result.addAll(getInitCalls(arg0));
            result.addAll(getInitCalls(arg1));
            break;
        case SEQ:
        case WHILE:
            result.addAll(getInitCalls(arg0));
            if (mayFinalise(arg0)) {
                result.addAll(getInitCalls(arg1));
            }
            break;
        default:
            assert false;
        }
        if (term.isTrial()) {
            DerivationAttempt derivList = term.getAttempt(false);
            for (Derivation edge : derivList) {
                result.add(edge.getCall());
            }
            result.addAll(getInitCalls(derivList.onSuccess()));
            result.addAll(getInitCalls(derivList.onFailure()));
        }
        return result;
    }

    /** Indicates, for a given term, if it can potentially
     * immediately evolve (via verdict transitions only) to a final position. */
    public boolean mayFinalise(Term term) {
        return getFinality(term, getFinalityMap()).may();
    }

    /** Indicates, for a given procedure, if it can potentially
     * immediately evolve (via verdict transitions only) to a final position. */
    public boolean mayFinalise(Procedure proc) {
        return getFinalityMap().get(proc)
            .may();
    }

    /** Indicates, for a given term, if it can potentially
     * immediately evolve (via verdict transitions only) to a final position. */
    public boolean willFinalise(Term term) {
        return getFinality(term, getFinalityMap()).will();
    }

    /** Indicates, for a given procedure, if it will certainly
     * immediately evolve (via verdict transitions only) to a final position. */
    public boolean willFinalise(Procedure proc) {
        return getFinalityMap().get(proc)
            .will();
    }

    /** Returns a mapping from procedures to their potential and certain immediate termination. */
    private Map<Procedure,Finality> getFinalityMap() {
        if (this.finalityMap == null) {
            this.finalityMap = computeFinalityMap();
        }
        return this.finalityMap;
    }

    private Map<Procedure,Finality> finalityMap;

    /**
     * Computes the termination map.
     */
    private Map<Procedure,Finality> computeFinalityMap() {
        Map<Procedure,Finality> result = new HashMap<>();
        for (Procedure proc : this.procs.values()) {
            result.put(proc, new Finality(false, false));
        }
        boolean changed;
        do {
            changed = iterateFinality(result);
        } while (changed);
        return result;
    }

    /** Iterates the computation of the finality map, by modifying the parameter.
     * @return {@code true} if the map was changed in this iteration
     */
    private boolean iterateFinality(Map<Procedure,Finality> finalityMap) {
        boolean result = false;
        for (Procedure proc : this.procs.values()) {
            Finality newResult = getFinality(proc.getTerm(), finalityMap);
            Finality oldResult = finalityMap.put(proc, newResult);
            result |= !newResult.equals(oldResult);
        }
        return result;
    }

    /**
     * Computes the potential and certain transitive termination of a given term,
     * given an assumption about the properties of the procedures called.
     */
    private Finality getFinality(Term term, Map<Procedure,Finality> finalityMap) {
        Finality result = null;
        boolean may = false;
        boolean will = false;
        int arity = term.getOp()
            .getArity();
        Term arg0 = arity >= 1 ? term.getArgs()[0] : null;
        Term arg1 = arity >= 2 ? term.getArgs()[1] : null;
        Term arg2 = arity >= 3 ? term.getArgs()[2] : null;
        Term arg3 = arity >= 4 ? term.getArgs()[3] : null;
        switch (term.getOp()) {
        case ALAP:
            assert arg0 != null; // alap has 1 argument
            may = !arg0.isFinal();
            will = arg0.isDead();
            break;
        case CALL:
            Callable unit = ((CallTerm) term).getCall()
                .getUnit();
            if (unit instanceof Rule) {
                may = will = false;
            } else {
                result = finalityMap.get(unit);
            }
            break;
        case DELTA:
            may = will = false;
            break;
        case EPSILON:
        case HASH:
        case STAR:
            may = will = true;
            break;
        case IF:
            assert arg0 != null; // if has at least one argument
            Finality condFinal = getFinality(arg0, finalityMap);
            Finality thenFinal = getFinality(arg1, finalityMap);
            Finality alsoFinal = getFinality(arg2, finalityMap);
            Finality elseFinal = getFinality(arg3, finalityMap);
            if (condFinal.will()) {
                // we can only reach the then|also-part
                may = thenFinal.may() || alsoFinal.may();
                will = thenFinal.will() || alsoFinal.will();
            } else if (condFinal.may()) {
                // we either reach the then|also-part or the else-part
                may = thenFinal.may() || alsoFinal.may() || elseFinal.may();
                // the possibly unsuccessful cond-part means the then-part is irrelevant
                will = alsoFinal.will() && elseFinal.will();
            } else if (arg0.isDead()) {
                // the if behaves as the else-part
                result = elseFinal;
            } else {
                // we either reach the then|also-part or the else-part
                // but the always unsuccessful cond-part blocks the then-part
                may = alsoFinal.may() || elseFinal.may();
                will = alsoFinal.will() && elseFinal.will();
            }
            break;
        case OR:
            Finality final0 = getFinality(arg0, finalityMap);
            Finality final1 = getFinality(arg1, finalityMap);
            may = final0.may() || final1.may();
            will = final0.will() || final1.will();
            break;
        case SEQ:
            final0 = getFinality(arg0, finalityMap);
            final1 = getFinality(arg1, finalityMap);
            may = final0.may() && final1.may();
            will = final0.will() && final1.will();
            break;
        case ATOM:
        case TRANSIT:
        case UNTIL:
            result = getFinality(arg0, finalityMap);
            break;
        case WHILE:
            assert arg0 != null; // while has one argument
            final0 = getFinality(arg0, finalityMap);
            will = arg0.isDead();
            may = !final0.will();
            break;
        default:
            assert false;
        }
        return result == null ? new Finality(may, will) : result;
    }

    /** Class storing potential and certain finality values. */
    private static class Finality extends Duo<Boolean> {
        /**
         * Constructs a pair of may/will succeed.
         */
        public Finality(boolean may, boolean will) {
            super(may, will);
        }

        boolean may() {
            return one();
        }

        boolean will() {
            return two();
        }
    }

    /** Indicates if the given procedure will always terminate. */
    public boolean willTerminate(Procedure proc) {
        return getTerminationSet().contains(proc);
    }

    /** Returns the set of procedures that will certainly terminate. */
    private Set<Procedure> getTerminationSet() {
        if (this.terminationSet == null) {
            this.terminationSet = computeTerminationSet();
        }
        return this.terminationSet;
    }

    private Set<Procedure> terminationSet;

    /** Computes the set of procedures that will always eventually terminate ({@code GF final}),
     * through a smallest fixpoint computation for {@code F final} followed by a
     * largest fixpoint computation for {@code G(F final)}
     */
    private Set<Procedure> computeTerminationSet() {
        Set<Procedure> result = new HashSet<>();
        // the set of procedures not yet known to satisfy F final
        Set<Procedure> remaining = new HashSet<>(getProcs().values());
        // iterate as long as procedures keep being added to the result set
        boolean modified = true;
        while (modified) {
            modified = false;
            // compute the termination of the remaining procedures
            Iterator<Procedure> procIter = remaining.iterator();
            while (procIter.hasNext()) {
                Procedure proc = procIter.next();
                if (mayTerminate(proc.getTerm(), result)) {
                    // proc will certainly terminate
                    result.add(proc);
                    procIter.remove();
                    modified = true;
                }
            }
        }
        // iterate as long as procedures keep being removed from the result set
        modified = true;
        while (modified) {
            modified = false;
            // compute the termination of the remaining procedures
            Iterator<Procedure> procIter = result.iterator();
            while (procIter.hasNext()) {
                Procedure proc = procIter.next();
                if (!willTerminate(proc.getTerm(), result)) {
                    // proc does not always terminate
                    procIter.remove();
                    modified = true;
                }
            }
        }
        return result;
    }

    /**
     * Computes whether {@code GF final} holds for a given term,
     * given the set of procedures for which it holds.
     */
    private boolean willTerminate(Term term, Set<Procedure> terminationSet) {
        // results for the arguments
        Term[] args = term.getArgs();
        boolean[] r = new boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            r[i] = willTerminate(term.getArgs()[i], terminationSet);
        }
        switch (term.getOp()) {
        case ALAP:
        case ATOM:
        case HASH:
        case TRANSIT:
        case BODY:
        case STAR:
            return r[0];
        case CALL:
            Callable unit = ((CallTerm) term).getCall()
                .getUnit();
            return unit.getKind() == Callable.Kind.RULE || terminationSet.contains(unit);
        case DELTA:
            return false;
        case EPSILON:
            return true;
        case IF:
            switch (args[0].getType()) {
            case DEAD:
                return r[3];
            case FINAL:
                return !term.isDead() && (args[1].isDead() || r[1]) && (args[2].isDead() || r[2]);
            case TRIAL:
                return r[0] && r[1] && (args[2].isDead() || r[2]) && (args[3].isDead() || r[3]);
            default:
                assert false;
                return false;
            }
        case OR:
            return !term.isDead() && (args[0].isDead() || r[0]) && (args[1].isDead() || r[1]);
        case SEQ:
            return r[0] && r[1];
        case UNTIL:
            return args[0].isFinal() || r[0] && r[1];
        case WHILE:
            return !args[0].isFinal() && r[0] && r[1];
        default:
            assert false;
            return false;
        }
    }

    /** Computes whether {@code F final} holds for a given term,
     * given the set of procedures for which it holds.
     */
    private boolean mayTerminate(Term term, Set<Procedure> terminationSet) {
        // results for the arguments
        Term[] args = term.getArgs();
        boolean[] r = new boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            r[i] = mayTerminate(term.getArgs()[i], terminationSet);
        }
        switch (term.getOp()) {
        case ATOM:
        case TRANSIT:
        case BODY:
            return r[0];
        case CALL:
            Callable unit = ((CallTerm) term).getCall()
                .getUnit();
            return unit.getKind() == Callable.Kind.RULE || terminationSet.contains(unit);
        case DELTA:
            return false;
        case ALAP:
        case HASH:
        case STAR:
        case EPSILON:
            return true;
        case IF:
            switch (args[0].getType()) {
            case DEAD:
                return r[3];
            case FINAL:
                return r[1] || r[2];
            case TRIAL:
                return r[0] && r[1] || r[2] || r[3];
            default:
                assert false;
                return false;
            }
        case OR:
            return r[0] || r[1];
        case SEQ:
            return r[0] && r[1];
        case UNTIL:
            return !args[0].isDead();
        case WHILE:
            return !args[0].isFinal();
        default:
            assert false;
            return false;
        }
    }
}
