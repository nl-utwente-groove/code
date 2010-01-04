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
 * $Id: ValueNode.java,v 1.10 2008-02-12 15:15:32 fladder Exp $
 */
package groove.graph.algebra;

import groove.algebra.Algebra;

import java.util.HashMap;
import java.util.Map;

/**
 * Class of nullary product nodes, used to represent attribute variables in
 * rules and conditions.
 * @author Arend Rensink
 * @version $Revision: 1768 $ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends ProductNode {
    /**
     * Constructs a (numbered) variable node.
     */
    VariableNode(int nr, Algebra<?> algebra) {
        super(nr, 0);
        this.algebra = algebra;
    }

    /**
     * This methods returns description of the variable, based on its number.
     */
    @Override
    public String toString() {
        return "x" + getNumber();
    }

    /**
     * Modifies the super result by testing whether this is actually a variable
     * node.
     */
    @Override
    protected int computeHashCode() {
        return super.computeHashCode() * 3;
    }

    /**
     * Method returning the (possibly null) algebra to which the variable node
     * belongs.
     */
    public Algebra<?> getAlgebra() {
        return this.algebra;
    }

    /** The signature name of this variable node, if any. */
    private final Algebra<?> algebra;

    /**
     * Returns a new, untyped variable node, with a given number but without
     * predefined value. Reuses a previously created variable node with the same
     * number, if any.
     */
    static public VariableNode createVariableNode(int nr) {
        return createVariableNode(nr, null);
    }

    /**
     * Returns a new, typed variable node, with a given number but without
     * predefined value. Reuses a previously created variable node with the same
     * number, if any.
     */
    static public VariableNode createVariableNode(int nr, Algebra<?> algebra) {
        Map<Integer,VariableNode> store =
            algebra == null ? generalNodeStore : algebraNodeStore.get(algebra);
        if (store == null) {
            algebraNodeStore.put(algebra, store =
                new HashMap<Integer,VariableNode>());
        }
        VariableNode result = store.get(nr);
        if (result == null) {
            store.put(nr, result = new VariableNode(nr, algebra));
        }
        return result;
    }

    /** Store of previously created variable nodes without associated algebra. */
    static private final Map<Integer,VariableNode> generalNodeStore =
        new HashMap<Integer,VariableNode>();
    /** Store of previously created variable nodes per algebra. */
    static private final Map<Algebra<?>,Map<Integer,VariableNode>> algebraNodeStore =
        new HashMap<Algebra<?>,Map<Integer,VariableNode>>();
}
