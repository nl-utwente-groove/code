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
package nl.utwente.groove.control.instance;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.control.instance.CallStackChange.Kind;
import nl.utwente.groove.control.template.Location;
import nl.utwente.groove.control.template.NestedSwitch;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.util.Exceptions;

/**
 * Call stack change to be applied as part of a {@link Step}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public record CallStackChange(Kind kind, List<Assignment> assigns) {

    private CallStackChange(Kind kind, Assignment... assigns) {
        this(kind, Arrays.asList(assigns));
    }

    /**
     * Applies this assignment to a given frame
     * valuation, and returns the modified frame valuation.
     * Only valid for {@link Kind#POP} and {@link Kind#PUSH} assignments;
     * use {@link #apply(Object[], Function)} with non-{@code null} parameter
     * retrieval function to apply {@link Kind#REPLACE}.
     * @return the frame valuation stack obtained by applying this assignment
     */
    public Object[] apply(Object[] stack) {
        assert kind() != Kind.REPLACE;
        return apply(stack, null);
    }

    /** Returns the first assignment in this change. */
    public Assignment assign() {
        return assign(0);
    }

    /** Returns the {@code i}-th assignment in this change. */
    public Assignment assign(int i) {
        return assigns().get(i);
    }

    /**
     * Applies this assignment to a given frame
     * value stack, and returns the modified value stack.
     * @param stack the current frame value stack
     * @param getPar function retrieving the value of {@link Source#CREATOR} and
     * {@link Source#ANCHOR} bindings.
     * @return the frame value stack obtained by applying this assignment
     */
    public Object[] apply(Object[] stack, @Nullable Function<Binding,HostNode> getPar) {
        Object[] result;
        switch (kind()) {
        case POP:
            result = CallStack.pop(stack);
            if (!assign().isNone()) {
                HostNode[] newTop = assign().apply(stack, getPar);
                result = CallStack.modify(result, newTop);
            }
            break;
        case PUSH:
            result = stack;
            for (int i = 0; i < assigns().size(); i++) {
                HostNode[] newTop = assign(i).apply(stack, getPar);
                result = i == 0
                    ? CallStack.replace(result, newTop)
                    : CallStack.push(result, newTop);
            }
            break;
        case REPLACE:
            if (!assign().isIdentity()) {
                HostNode[] newTop = assign().apply(stack, getPar);
                result = CallStack.replace(stack, newTop);
            } else {
                result = stack;
            }
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.assigns.hashCode();
        result = prime * result + this.kind.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CallStackChange other)) {
            return false;
        }
        if (!this.assigns.equals(other.assigns)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.kind.name() + this.assigns;
    }

    /**
     * Computes a multi-level call stack change for the nested switch of a given step.
     * For all but the outer and inner switch of the step, the assignment pushes a new level onto the
     * call stack; for the outer switch, it modifies the call frame at the current top of the stack.
     * All (modified or pushed) levels are assignments to the target variables of the
     * respective switch.
     * @param step the step for which the change is to be computed
     */
    static CallStackChange enter(Step step) {
        NestedSwitch swt = step.getSwitch();
        Assignment[] result = new Assignment[swt.size()];
        Assignment sourceAssign = Assignment.identity(step.getSource().getVars());
        var iter = swt.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Switch s = iter.next();
            result[i] = s.getTargetAssign().then(sourceAssign);
            if (iter.hasNext()) {
                sourceAssign = s.getCalleeAssign().then(sourceAssign);
                i++;
            }
        }
        return CallStackChange.push(result);
    }

    /**
     * Computes the variable assignment for the target location of a caller
     * from the variables in the final location of the callee's template.
     * @param top final location of the callee's template
     * @param swit the caller switch
     */
    static CallStackChange exit(Location top, Switch swit) {
        assert swit.getKind().isProcedure();
        assert top.isFinal();
        Assignment result = new Assignment();
        Signature<UnitPar.ProcedurePar> sig = ((Procedure) swit.getUnit()).getSignature();
        var outVars = swit.getCall().getOutVars();
        var finalVars = top.getVarIxMap();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value does not come from the call
                // which means it has already been copied from the caller's source location
                rhs = Binding.none(var);
            } else {
                // the value comes from an output parameter of the call
                // find the variable of the corresponding formal parameter
                CtrlVar par = sig.getPar(ix).getVar();
                // look it up in the final location variables
                rhs = Binding.var(var, finalVars.get(par));
            }
            result.add(rhs);
        }
        return pop(result);
    }

    /** Creates a new {@link Kind#PUSH} action with a given list of assignments. */
    public static CallStackChange push(Assignment... assigs) {
        return new CallStackChange(Kind.PUSH, assigs);
    }

    /** Creates a new {@link Kind#POP} action with a given assignment. */
    public static CallStackChange pop(Assignment assign) {
        return new CallStackChange(Kind.POP, assign);
    }

    /** Kind of {@link CallStackChange}. */
    public static enum Kind {
        /** Create and initialise a frame instance. */
        PUSH,
        /** Pop a frame instance. */
        POP,
        /** Invoke a rule. */
        REPLACE,;
    }
}
