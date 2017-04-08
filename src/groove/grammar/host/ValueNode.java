/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.grammar.host;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.algebra.syntax.Expression;
import groove.grammar.AnchorKind;
import groove.grammar.aspect.AspectParser;
import groove.grammar.type.TypeNode;
import groove.graph.ANode;

/**
 * Implementation of graph elements that represent algebraic data values.
 *
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class ValueNode extends ANode implements HostNode {
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
        assert algebra != null && value != null && type != null && algebra.isValue(value);
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
        return this.value;
    }

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
        return this.algebra;
    }

    /**
     * Returns the sort to which the value node
     * belongs.
     */
    public Sort getSort() {
        assert this != DUMMY_NODE;
        return this.sort;
    }

    @Override
    public TypeNode getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    /** The sort of this value node. */
    private final Sort sort;
    /** The algebra of this value node. */
    private final Algebra<?> algebra;
    /**
     * The constant represented by this value node (non-null).
     */
    private final Object value;

    /** The type of this value node. */
    private final TypeNode type;
    /** Single dummy node, used in e.g., MergeMap */
    public static final ValueNode DUMMY_NODE = new ValueNode();
}
