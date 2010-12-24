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

import groove.graph.AbstractNode;
import groove.trans.RuleNode;

/**
 * Class of nullary product nodes, used to represent attribute variables in
 * rules and conditions.
 * @author Arend Rensink
 * @version $Revision: 1768 $ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends AbstractNode implements RuleNode {
    /**
     * Constructs a (numbered) variable node,
     * with an optional signature and an optional constant symbol.
     */
    public VariableNode(int nr, String signature, String constant) {
        super(nr);
        this.signature = signature;
        this.constant = constant;
    }

    /**
     * This methods returns description of the variable, based on its number.
     */
    @Override
    public String toString() {
        if (this.constant == null) {
            return "x" + getNumber();
        } else {
            return this.constant;
        }
    }

    /** Superseded by the reimplemented {@link #toString()} method. */
    @Override
    protected String getToStringPrefix() {
        throw new UnsupportedOperationException();
    }

    /** Nodes are now not canonical, so we need to test for the numbers and classes. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VariableNode)) {
            return false;
        }
        VariableNode other = (VariableNode) obj;
        return getNumber() == other.getNumber();
    }

    /**
     * Method returning the (possibly null) signature to which the variable node
     * belongs.
     */
    public String getSignature() {
        return this.signature;
    }

    /**
     * Method returning the (possibly null) constant symbol of the variable node.
     */
    public String getConstant() {
        return this.constant;
    }

    /** The signature name of this variable node, if any. */
    private final String signature;
    /** Optional constant symbol. */
    private final String constant;
}
