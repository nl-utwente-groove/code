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
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.util.LazyFactory;

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
        this(Arrays.asList());
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

    /** Concatenates this assignment with another.
     * This results in a new assignment, in which each {@link Source#VAR} binding
     * of this one (call it {@code b}) is replaced with the binding in {@code other}
     * at index {@code b.index()}.
     * @param other the other assignment, to be applied after this one
     * @return the new assignment
     */
    public Assignment then(Assignment other) {
        Assignment result = new Assignment();
        for (Binding bind : this.bindings) {
            if (bind.type() == Source.VAR) {
                result.add(bind.then(other));
            } else {
                result.add(bind);
            }
        }
        return result;
    }

    /** Indicates if all the bindings in this assignment are {@link Source#NONE}. */
    public boolean isNone() {
        return this.none.get();
    }

    /** Lazily computed flag indicating if all the bindings in this assignment are {@link Source#NONE}. */
    private Supplier<Boolean> none
        = LazyFactory.instance(() -> stream().allMatch(b -> b.type() == Source.NONE));

    /** Indicates if all the bindings in this assignment are {@link Source#VAR}
     * with the index of the binding itself. */
    public boolean isIdentity() {
        return this.identity.get();
    }

    /** Lazily computed flag indicating if all the bindings in this assignment are {@link Source#VAR}
     * with the index of the binding itself. */
    private final Supplier<Boolean> identity = LazyFactory.instance(this::computeIdentity);

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
     * Applies this assignment to a given call stack.
     * Only valid if this assignment contains no {@link Source#ANCHOR} or {@link Source#CREATOR}
     * bindings; otherwise, use {@link #apply(Object[], Function)} with non-{@code null}
     * retrieval function.
     * @return the array of values obtained for the individual bindings of this assignment.
     * Note that this is <i>not</i> a value stack, but rather just the top level level in such a stack
     */
    public HostNode[] apply(Object[] stack) {
        return apply(stack, null);
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
    public HostNode[] apply(Object[] stack, @Nullable Function<Binding,HostNode> getPar) {
        var bindings = this.bindings;
        HostNode[] result = new HostNode[bindings.size()];
        for (int i = 0; i < bindings.size(); i++) {
            result[i] = bindings.get(i).apply(stack, getPar);
        }
        return result;
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

    /**
     * Returns an assignment to the target variables of a
     * switch, based on the source variables and the output parameters of the call.
     * If the switch is a rule call, the output parameter values
     * are available at the moment of applying the assignment and
     * can be retrieved from the rule application; if it is a procedure
     * call, the output parameter values are not yet available and will
     * be set to {@code null}.
     */
    static public Assignment process(Switch swit) {
        Assignment result = new Assignment();
        var sourceVars = swit.getSource().getVarIxMap();
        var outVars = swit.getCall().getOutVars();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the source
                int pos = sourceVars.get(var);
                assert pos >= 0;
                rhs = Binding.var(var, pos);
            } else if (swit.getUnit() instanceof Rule rule) {
                // the value is an output parameter of the rule
                rhs = rule.getParBinding(ix);
            } else {
                rhs = Binding.none(var);
            }
            result.add(rhs);
        }
        return result;
    }

    /** Returns an identity assignment of a given size. */
    static public Assignment identity(List<CtrlVar> vars) {
        Assignment result = new Assignment();
        for (int i = 0; i < vars.size(); i++) {
            result.add(Binding.var(vars.get(i), i));
        }
        return result;
    }
}
