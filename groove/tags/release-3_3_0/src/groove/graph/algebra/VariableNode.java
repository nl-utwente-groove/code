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

import java.util.HashMap;
import java.util.Map;

/**
 * Class of nullary product nodes, used to represent attribute variables in rules and conditions. 
 * @author Arend Rensink
 * @version $Revision: 1768 $ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends ProductNode {
    /**
     * Constructs a (numbered) variable node.
     */
    VariableNode(int nr) {
        super(nr, 0);
    }

    /**
     * This methods returns description of the variable, based on its number.
     */
    @Override
    public String toString() {
        return "x" + getNumber();
    }

    /** Modifies the super result by testing whether this is actually a variable node. */
    @Override
    protected int computeHashCode() {
        return super.computeHashCode() * 3;
    }
    
    /** 
     * Returns a new value node, with a given number but without predefined value.
     * Reuses a previously created variable node with the same number, if any.
     */
    static public VariableNode createVariableNode(int nr) {
        VariableNode result = variableNodeStore.get(nr);
        if (result == null) {
            variableNodeStore.put(nr, result = new VariableNode(nr));
        }
        return result;
    }
    
    /** Store of previously created variable nodes. */
    static private final Map<Integer,VariableNode> variableNodeStore = new HashMap<Integer,VariableNode>();
}
