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
package nl.utwente.groove.control;

import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.CtrlPar.Const;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.grammar.host.HostNode;

/** Individual variable assignment in a control step.
 * @param type the type of source from which the value is to be obtained
 * @param target the target variable or parameter of the assignment
 * @param index the index if this is a {@link Source#VAR}, {@link Source#ANCHOR} or {@link Source#CREATOR} binding
 * @param value the value if this is a {@link Source#CONST} binding
 * @param depth the depth in the call stack from which the value is to be obtained
 */
@NonNullByDefault
public record Binding(Binding.Source type, Object target, int index, @Nullable Const value,
    int depth) {
    /** Constructs a top-level binding (depth {@code 0}). */
    private Binding(Source type, Object target, int index, @Nullable Const value) {
        this(type, target, index, value, 0);
    }

    /** Returns a binding based on this one, with a given depth. */
    public Binding toDepth(int depth) {
        return new Binding(type(), target(), index(), value(), depth);
    }

    /** Returns a binding based on this one, with a given target. */
    public Binding withTarget(Object target) {
        return new Binding(type(), target, index(), value(), depth());
    }

    /** Returns the index, if this is not a value binding. */
    public int index() {
        assert type() != Source.CONST;
        return this.index;
    }

    /** Returns the target of this binding as a (rule or procedure) parameter.
     * Only valid if the target is a non-{@code null} {@link UnitPar} value.
     */
    public UnitPar par() {
        var result = (UnitPar) target();
        assert result != null;
        return result;
    }

    /** Returns the target of this binding as a control variable.
     * Only valid if the target is a non-{@code null} {@link CtrlVar} value.
     */
    public CtrlVar var() {
        var result = (CtrlVar) target();
        assert result != null;
        return result;
    }

    /** Returns a value from the top level of a given call stack,
     * if this ia a {@link Source#VAR} binding. */
    public @Nullable HostNode get(Object[] stack) {
        assert type() == Source.VAR;
        return (HostNode) stack[this.index];
    }

    /** Returns the assigned value, if this is a value binding. */
    public Const value() {
        assert type() == Source.CONST;
        Const result = this.value;
        assert result != null;
        return result;
    }

    @Override
    public String toString() {
        return target() + ":=" + type().name() + ":" + (type() == Source.CONST
            ? value()
            : index());
    }

    /**
     * Applies this assignment to a given call stack, using a retrieval function to
     * get the values for {@link Source#ANCHOR} and {@link Source#CREATOR} bindings.
     * @param stack the call stack to apply this assignment to
     * @param getPar function retrieving the value of {@link Source#CREATOR} and
     * {@link Source#ANCHOR} bindings. If {@code null}, no such bindings may exist.
     * @return the array of values obtained for the individual bindings of this assignment.
     * Note that this is <i>not</i> a call stack, but rather just the top level for such a stack
     */
    public @Nullable HostNode apply(Object[] stack, @Nullable Function<Binding,HostNode> getPar) {
        for (int i = 0; i < depth(); i++) {
            stack = CallStack.pop(stack);
        }
        return switch (type()) {
        case CONST -> value().getNode();
        case VAR -> CallStack.get(stack, index());
        case ANCHOR, CREATOR -> {
            assert getPar != null : String
                .format("Can't apply %s: can't retrieve parameter values", this,
                        CallStack.toString(stack));
            // this is a rule parameter
            yield getPar.apply(this);
        }
        case NONE -> null;
        };
    }

    /** Constructs a new binding that applies this binding
     * (which must be a {@link Source#VAR}) to the outcome of an assignment.
     */
    Binding after(Assignment assign) {
        assert type() == Source.VAR;
        return assign.get(index()).withTarget(target());
    }

    /** Constructs a targeted binding to a constant value.
     * @see Source#CONST
     */
    public static Binding value(Object target, Const value) {
        return new Binding(Source.CONST, target, 0, value);
    }

    /** Constructs a targeted binding to a variable in the source location.
     * @see Source#VAR
     */
    public static Binding var(Object target, int index) {
        return new Binding(Source.VAR, target, index, null);
    }

    /** Constructs a targeted binding to an anchor node of a rule match.
     * @see Source#ANCHOR
     */
    public static Binding anchor(Object target, int index) {
        return new Binding(Source.ANCHOR, target, index, null);
    }

    /** Constructs a targeted binding to a creator node in a rule application.
     * @see Source#CREATOR
     */
    public static Binding creator(Object target, int index) {
        return new Binding(Source.CREATOR, target, index, null);
    }

    /** Constructs targeted a binding to an unspecified value.
     * @see Source#NONE
     */
    public static Binding none(Object target) {
        return new Binding(Source.NONE, target, 0, null);
    }

    /** Kind of source for a variable assignment. */
    public enum Source {
        /** Source location variable. */
        VAR,
        /** Rule anchor. */
        ANCHOR,
        /** Creator node image. */
        CREATOR,
        /** Constant value. */
        CONST,
        /** Null value. */
        NONE,;
    }
}