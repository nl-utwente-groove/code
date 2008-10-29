/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.graph.algebra;

import static groove.view.aspect.Aspect.CONTENT_SEPARATOR;
import groove.algebra.Constant;
import groove.graph.DefaultNode;
import groove.view.aspect.AttributeAspect;

import java.util.Arrays;
import java.util.List;

/**
 * Node implementing a variable (i.e., unknown) attribute value.
 * Used on the rule level to model non-product attribute nodes.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VariableNode extends ProductNode {
    /** Constructs an arbitrary instance. */
    public VariableNode() {
        this(null);
    }
    
    /** Constructs an arbitrary instance. */
    public VariableNode(Constant value) {
        super(EMPTY_ARGUMENT_LIST);
        this.value = value;
    }
    
    /** Indicates if this variable node has a constant value. */
    public boolean isConstant() {
        return this.value != null;
    }
    
    /** Returns the constant value of this variable node, if any. */
    public Constant getConstant() {
        return this.value;
    }
    
    /**
     * This methods returns an indication of the variable if there is no
     * associated algebra, or a description of the value otherwise.
     */
    @Override
    public String toString() {
        if (!isConstant()) {
            return "x" + (getNumber() - DefaultNode.MAX_NODE_NUMBER);
        } else {
            String algebraName =
                AttributeAspect.getAttributeValueFor(this.value.algebra()).getName();
            return algebraName + CONTENT_SEPARATOR + this.value;
        }
    }

    /**
     * the operation represented by this value node; <code>null</code> if the
     * node stands for a variable.
     */
    private final Constant value;

    static private final List<VariableNode> EMPTY_ARGUMENT_LIST = Arrays.asList();
}
