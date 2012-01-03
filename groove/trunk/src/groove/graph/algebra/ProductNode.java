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
 * $Id: ProductNode.java,v 1.8 2008-02-12 15:15:32 fladder Exp $
 */
package groove.graph.algebra;

import groove.graph.AbstractNode;
import groove.graph.EdgeRole;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.rel.LabelVar;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Instances of this class represent tuples of data values on which one can
 * perform algebraic operations. A product node has <i>arguments</i>, which are
 * the {@link VariableNode}s attached to it through {@link ArgumentEdge}s, and
 * <i>operands</i>, which are the corresponding constants on those nodes.
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2008-02-12 15:15:32 $
 */
public class ProductNode extends AbstractNode implements RuleNode {
    /**
     * Returns a fresh product node with a given node number and arity. The
     * arguments are initially set to <code>null</code>.
     */
    public ProductNode(int nr, int arity) {
        super(nr);
        this.arguments =
            arity == 0 ? EMPTY_ARGUMENT_LIST : new ArrayList<VariableNode>(
                arity);
        for (int i = 0; i < arity; i++) {
            this.arguments.add(null);
        }
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #equals(Object)}.
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass())
            && ((ProductNode) obj).getNumber() == getNumber();
    }

    /**
     * Sets one of the arguments of the product node.
     * @throws IllegalArgumentException if argument number <code>i</code> has
     *         already been set
     */
    public void setArgument(int i, VariableNode arg) {
        if (arg == null) {
            throw new IllegalArgumentException(
                String.format("Null argument not allowed"));
        }
        VariableNode oldArg = this.arguments.set(i, arg);
        if (oldArg != null && !oldArg.equals(arg)) {
            throw new IllegalArgumentException(String.format(
                "Argument number %d already contains %s", i, oldArg));
        }
    }

    /** Retrieves the list of arguments of the product node. */
    public List<VariableNode> getArguments() {
        return this.arguments;
    }

    /**
     * Returns the arity of this <code>ProductNode</code>
     * @return the arity of this <code>ProductNode</code>
     */
    public int arity() {
        return this.arguments.size();
    }

    @Override
    public String getToStringPrefix() {
        return "p";
    }

    @Override
    public TypeNode getType() {
        return null;
    }

    @Override
    public List<LabelVar> getTypeVars() {
        return EMPTY_VAR_LIST;
    }

    @Override
    public List<TypeGuard> getTypeGuards() {
        return EMPTY_GUARD_LIST;
    }

    @Override
    public boolean isSharp() {
        return true;
    }

    @Override
    public Set<TypeNode> getMatchingTypes() {
        return Collections.emptySet();
    }

    /**
     * The list of arguments of this product node (which are the value nodes to
     * which an outgoing AlgebraEdge is pointing).
     */
    private final List<VariableNode> arguments;

    /** Empty list of value nodes, to be passed to the super constructor. */
    static private final List<VariableNode> EMPTY_ARGUMENT_LIST =
        Arrays.asList();
    static final private char TIMES_CHAR = '\u2a09';
    /** Type label of product nodes. */
    @SuppressWarnings("unused")
    static private final TypeLabel PROD_LABEL = TypeLabel.createLabel(
        EdgeRole.NODE_TYPE, "" + TIMES_CHAR);
    /** Predefined empty list of label variables. */
    static private final List<LabelVar> EMPTY_VAR_LIST =
        Collections.emptyList();
    /** Predefined empty list of type guards. */
    static private final List<TypeGuard> EMPTY_GUARD_LIST =
        Collections.emptyList();
}
