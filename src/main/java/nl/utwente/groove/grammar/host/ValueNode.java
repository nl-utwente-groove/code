/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.host;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.ANode;

/**
 * Implementation of graph elements that represent algebraic data values.
 *
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
@NonNullByDefault
final public class ValueNode extends ANode implements HostNode {
    /** Constructor for the unique dummy node. */
    private ValueNode() {
        super(Integer.MAX_VALUE);
        this.algebra = null;
        this.sort = null;
        this.value = null;
        this.type = null;
    }

    /**
     * Constructs a (numbered) node for a given algebra and value of that
     * algebra.
     * @param nr the number for the new node.
     * @param algebra the algebra that the value belongs to; non-null
     * @param value the value to create a graph node for; non-null
     */
    public ValueNode(int nr, Algebra<?> algebra, Object value, TypeNode type) {
        super(nr);
        assert algebra.isValue(value);
        this.algebra = algebra;
        this.sort = algebra.getSort();
        this.value = value;
        this.type = type;
    }

    /**
     * Returns the (non-null) algebra value this value node is representing.
     */
    public Object getValue() {
        assert this != DUMMY_NODE;
        var result = this.value;
        assert result != null;
        return result;
    }

    /**
     * The constant represented by this value node (non-null).
     */
    private final @Nullable Object value;

    /**
     * Converts the value in this object to the corresponding value in
     * the Java algebra family.
     */
    public Object toJavaValue() {
        return getAlgebra().toJavaValue(getValue());
    }

    /** Converts the value in this node to an {@link Expression}.
     * Typically this is {@link Constant}, but if the underlying algebra is a
     * term algebra, it is the term corresponding to the value itself.
     * @see Algebra#toTerm(Object)
     * */
    public Expression toTerm() {
        return getAlgebra().toTerm(getValue());
    }

    /**
     * Returns a symbolic description of the value, which uniquely identifies
     * the value in the algebra.
     */
    public String getSymbol() {
        return getAlgebra().getSymbol(getValue());
    }

    /**
     * Returns the normalised term for the algebra value.
     */
    public Expression getTerm() {
        return getAlgebra().toTerm(getValue());
    }

    /**
     * This methods returns a description of the value.
     */
    @Override
    public String toString() {
        return getAlgebra().getName() + AspectParser.SEPARATOR + getSymbol();
    }

    /** Superseded by the reimplemented {@link #toString()} method. */
    @Override
    protected String getToStringPrefix() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the algebra to which the value node
     * belongs.
     */
    public Algebra<?> getAlgebra() {
        assert this != DUMMY_NODE;
        var result = this.algebra;
        assert result != null;
        return result;
    }

    /** The algebra of this value node. */
    private final @Nullable Algebra<?> algebra;

    /**
     * Returns the sort to which the value node
     * belongs.
     */
    public Sort getSort() {
        assert this != DUMMY_NODE;
        var result = this.sort;
        assert result != null;
        return result;
    }

    /** The sort of this value node. */
    private final @Nullable Sort sort;

    @Override
    public TypeNode getType() {
        assert this != DUMMY_NODE;
        var result = this.type;
        assert result != null;
        return result;
    }

    /** The type of this value node. */
    private final @Nullable TypeNode type;

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    /** Single dummy node, used in e.g., MergeMap */
    public static final ValueNode DUMMY_NODE = new ValueNode();
}
