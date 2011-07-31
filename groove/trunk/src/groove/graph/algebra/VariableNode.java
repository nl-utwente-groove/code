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

import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.graph.AbstractNode;
import groove.graph.TypeNode;
import groove.trans.RuleNode;

/**
 * Nodes used to represent attribute variables in rules and conditions.
 * @author Arend Rensink
 * @version $Revision: 1768 $ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends AbstractNode implements RuleNode {
    /**
     * Constructs a (numbered) typed variable node.
     */
    public VariableNode(int nr, SignatureKind signature) {
        super(nr);
        this.signature = signature;
        this.constant = null;
    }

    /**
     * Constructs a (numbered) constant variable node.
     */
    public VariableNode(int nr, Constant constant) {
        super(nr);
        this.signature = constant.getSignature();
        this.constant = constant;
    }

    /**
     * This methods returns description of the variable, based on its number.
     */
    @Override
    public String toString() {
        if (getConstant() == null) {
            return "x" + getNumber();
        } else {
            return getConstant().toString();
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
     * Method returning the (non-{@code null}) signature to which the variable node
     * belongs.
     */
    public SignatureKind getSignature() {
        return this.signature;
    }

    /**
     * Method returning the (possibly null) constant symbol of the variable node.
     */
    public String getSymbol() {
        return this.constant == null ? null : this.constant.getSymbol();
    }

    /**
     * Method returning the (possibly null) constant of the variable node.
     */
    public Constant getConstant() {
        return this.constant;
    }

    @Override
    public TypeNode getType() {
        if (this.signature == null) {
            return null;
        } else {
            return TypeNode.getDataType(this.signature);
        }
    }

    @Override
    public boolean isSharp() {
        return this.signature != null;
    }

    /** The signature name of this variable node, if any. */
    private final SignatureKind signature;
    /** Optional constant symbol. */
    private final Constant constant;
}
