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

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.util.Exceptions;

/**
 * Class that computes the {@link HostNode} values for {@link Binding}s and {@link Assignment}s.
 * @author Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Valuator {
    /** Creates an initially empty valuator, which can evaluate only {@link Source#NONE} bindings. */
    public Valuator() {
        this.map.put(Source.NONE, b -> null);
    }

    /** Evaluates a {@link Binding} and returns the resulting {@link HostNode}, after setting a given stack.
     * Convenience method for {@link #setVarInfo(Object[])} followed by {@link #eval(Binding)}.
     * @throws UnsupportedOperationException if this valuator does not contain the information
     * necessary to evaluate all bindings in {@code assign}
     */
    public @Nullable HostNode eval(Binding bind,
                                   Object[] stack) throws UnsupportedOperationException {
        setVarInfo(stack);
        return eval(bind);
    }

    /** Evaluates a Binding and returns the resulting {@link HostNode}.
     * @param bind the binding to be evaluated
     * @throws UnsupportedOperationException if this valuator does not contain the information
     * necessary to evaluate {@code bind}
     */
    @SuppressWarnings("null")
    public @Nullable HostNode eval(Binding bind) throws UnsupportedOperationException {
        var map = this.map.get(bind.type());
        if (map == null) {
            throw Exceptions.unsupportedOp("Can't compute value of %s binding", bind);
        } else {
            return map.apply(bind);
        }
    }

    /** Evaluates an {@link Assignment} and returns the resulting {@link HostNode}-array, after setting a given stack.
     * Convenience method for {@link #setVarInfo(Object[])} followed by {@link #eval(Assignment)}.
     * @throws UnsupportedOperationException if this valuator does not contain the information
     * necessary to evaluate all bindings in {@code assign}
     */
    public @Nullable HostNode[] eval(Assignment assign,
                                     Object[] stack) throws UnsupportedOperationException {
        setVarInfo(stack);
        return eval(assign);
    }

    /** Evaluates an {@link Assignment} and returns the resulting {@link HostNode}-array.
     * @param assign the assignment to be evaluated
     * @throws UnsupportedOperationException if this valuator does not contain the information
     * necessary to evaluate all bindings in {@code assign}
     */
    public @Nullable HostNode[] eval(Assignment assign) throws UnsupportedOperationException {
        var size = assign.size();
        var result = new @Nullable HostNode[size];
        for (int i = 0; i < size; i++) {
            result[i] = eval(assign.get(i));
        }
        return result;
    }

    /** Adds information to evaluate {@link Source#ANCHOR} bindings.
     * @param anchorMap mapping from anchor node indices to the corresponding matched host node.
     */
    public void setAnchorInfo(Function<Integer,HostNode> anchorMap) {
        assert this != VARS;
        this.map.put(Source.ANCHOR, b -> anchorMap.apply(b.index()));
    }

    /** Adds information to evaluate {@link Source#CREATOR} bindings.
     * @param creatorMap mapping from creator node indices to the corresponding created host node.
     */
    public void setCreatorInfo(Function<Integer,HostNode> creatorMap) {
        assert this != VARS;
        this.map.put(Source.CREATOR, b -> creatorMap.apply(b.index()));
    }

    /** Adds information to evaluate {@link Source#VAR} bindings.
     * @param stack call stack in whose top level the host node can be looked up by index
     */
    public void setVarInfo(Object[] stack) {
        this.map.put(Source.VAR, b -> (HostNode) stack[b.index()]);
    }

    /** Adds information to evaluate {@link Source#EXPR} bindings.
     * @param family the algebra family from which the expression values arise
     * @param factory the host node factory to convert data values to value nodes
     */
    public void setExprInfo(AlgebraFamily family, HostFactory factory) {
        assert this != VARS;
        @SuppressWarnings("null")
        Function<Variable,Object> valuation
            = v -> ((ValueNode) this.eval((Binding) v.getBinding())).getValue();
        this.map.put(Source.EXPR, b -> {
            var expr = b.expr();
            assert expr != null;
            var algebra = family.getAlgebra(expr.getSort());
            var value = family.computeStrict(expr, valuation);
            return factory.createNode(algebra, value);
        });
    }

    private final Map<Binding.Source,Function<Binding,@Nullable HostNode>> map
        = new EnumMap<>(Binding.Source.class);

    /** Returns the (shared) valuator that can only evaluate {@link Source#VAR}-bindings
     * (after being initialised properly through {@link #setVarInfo(Object[])}). */
    static public Valuator vars() {
        return VARS;
    }

    static private final Valuator VARS = new Valuator();
}
