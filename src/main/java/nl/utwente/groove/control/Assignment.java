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

import static nl.utwente.groove.util.Factory.lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.instance.CallStackChange;
import nl.utwente.groove.control.instance.CallStackChange.Kind;
import nl.utwente.groove.grammar.host.HostNode;

/**
 * Control variable assignment, consisting of a list of bindings.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Assignment implements Iterable<Binding> {
    /**
     * Creates an initially empty assignment.
     */
    public Assignment() {
        this(new ArrayList<>());
    }

    /**
     * Creates a new assignment from a given list of bindings.
     */
    public Assignment(List<Binding> bindings) {
        this.bindings = new ArrayList<>(bindings);
    }

    /**
     * Creates a new assignment from a given list of bindings.
     */
    public Assignment(Binding... bindings) {
        this.bindings = Arrays.asList(bindings);
    }

    private final List<Binding> bindings;

    /** Returns a binding based on this one, in which the values are retrieved
     * from a given call stack depth. */
    public Assignment toDepth(int depth) {
        Assignment result = new Assignment();
        stream().map(b -> b.toDepth(depth)).forEach(result::add);
        return result;
    }

    /** Adds a binding to this assignmnent. */
    public void add(Binding bind) {
        assert bind != null;
        this.bindings.add(bind);
    }

    /** Returns the size of this assignment, in terms of number of bindings. */
    public int size() {
        return this.bindings.size();
    }

    /** Retrieves the binding at a given index. */
    public Binding get(int index) {
        return this.bindings.get(index);
    }

    /** Returns the bindings in this assignment as a stream. */
    public Stream<Binding> stream() {
        return this.bindings.stream();
    }

    /** Returns an iterator over the bindings in this assignment. */
    @Override
    public Iterator<Binding> iterator() {
        return this.bindings.iterator();
    }

    /** Concatenates this assignment with another, by applying this one after the other.
     * This results in a new assignment, in which each {@link Source#VAR} binding
     * of this one (call it {@code b}) is replaced with the binding in {@code other}
     * at index {@code b.index()}.
     * @param other the other assignment, to be applied after this one
     * @return the new assignment
     */
    public Assignment after(Assignment other) {
        Assignment result = new Assignment();
        for (Binding bind : this.bindings) {
            if (bind.type() == Source.VAR) {
                result.add(bind.after(other));
            } else {
                result.add(bind);
            }
        }
        return result;
    }

    /** Returns a change of type {@link Kind#POP} based on this assignment. */
    public CallStackChange toPop() {
        return CallStackChange.pop(this);
    }

    /** Indicates if all the bindings in this assignment can be looked up in the call stack.
     * @see Source#isStackBased()
     */
    public boolean isStackBased() {
        return this.stackBased.get();
    }

    /** Lazily computed flag indicating if all the bindings in this assignment are stack-based. */
    private Supplier<Boolean> stackBased
        = lazy(() -> stream().allMatch(b -> b.type().isStackBased()));

    /** Indicates if all the bindings in this assignment are {@link Source#NONE}. */
    public boolean isNone() {
        return this.none.get();
    }

    /** Lazily computed flag indicating if all the bindings in this assignment are {@link Source#NONE}. */
    private Supplier<Boolean> none = lazy(() -> stream().allMatch(b -> b.type() == Source.NONE));

    /** Indicates if all the bindings in this assignment are {@link Source#VAR}
     * with the index of the binding itself. */
    public boolean isIdentity() {
        return this.identity.get();
    }

    /** Lazily computed flag indicating if all the bindings in this assignment are {@link Source#VAR}
     * with the index of the binding itself. */
    private final Supplier<Boolean> identity = lazy(this::computeIdentity);

    /** Computes the value of {@link #identity}. */
    private boolean computeIdentity() {
        boolean result = true;
        for (int i = 0; result && i < this.bindings.size(); i++) {
            var bind = this.bindings.get(i);
            result = bind.type() == Source.VAR && bind.index() == i;
        }
        return result;
    }

    /**
     * Looks up the values for this assignment in a given call stack.
     * Only valid if this assignment is stack-based, i.e., contains
     * only {@link Source#VAR} and {@link Source#NONE} bindings.
     */
    public HostNode[] lookup(Object[] stack) {
        assert isStackBased();
        return Valuator.vars().eval(this, stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bindings);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Assignment other = (Assignment) obj;
        return Objects.equals(this.bindings, other.bindings);
    }

    @Override
    public String toString() {
        return this.bindings.toString();
    }

    /** Returns an identity assignment of a given size. */
    static public Assignment identity(List<CtrlVar> vars) {
        Assignment result = new Assignment();
        for (int i = 0; i < vars.size(); i++) {
            result.add(Binding.var(vars.get(i), i));
        }
        return result;
    }

    /** Applies a node mapping to an assignment record, and returns the result.
     */
    static public HostNode[] map(HostNode[] record, Function<HostNode,HostNode> map) {
        HostNode[] result = new HostNode[record.length];
        boolean changed = false;
        var size = record.length;
        for (int i = 0; i < size; i++) {
            var oldVal = record[i];
            var newVal = map.apply(oldVal);
            changed |= newVal != oldVal;
            result[i] = newVal;
        }
        return changed
            ? result
            : record;
    }
}
