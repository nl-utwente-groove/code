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
 * $Id: DefaultNode.java,v 1.17 2008-02-19 10:35:31 fladder Exp $
 */
package groove.trans;

import groove.graph.AbstractNode;
import groove.graph.Node;
import groove.graph.TypeNode;

/**
 * Default implementation of a graph node. Default nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision: 2971 $
 */
public class DefaultRuleNode extends AbstractNode implements RuleNode,
        Node.Factory<RuleNode> {
    /**
     * Constructs a fresh node, with an explicitly given number.
     * @param nr the number for this node
     */
    protected DefaultRuleNode(int nr) {
        this(nr, TypeNode.TOP_NODE, true);
    }

    /**
     * Constructs a fresh node, with an explicitly given number and node type.
     * @param nr the number for this node
     * @param type the node type; may be {@code null}
     * @param sharp if {@code true}, the node is sharply typed
     */
    protected DefaultRuleNode(int nr, TypeNode type, boolean sharp) {
        super(nr);
        assert type != null : "Can't instantiate untyped rule node";
        this.type = type;
        this.sharp = sharp;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DefaultRuleNode other = (DefaultRuleNode) obj;
        if (getType() != null) {
            return getType().equals(other.getType());
        } else {
            return other.getType() == null;
        }
    }

    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        if (getType() != null) {
            int prime = 31;
            result = prime * result + getType().hashCode();
        }
        return result;
    }

    /** Factory constructor. */
    @Override
    public DefaultRuleNode newNode(int nr, TypeNode type) {
        return new DefaultRuleNode(nr, type, false);
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return getType() == TypeNode.TOP_NODE ? "n"
                : getType().getLabel().text() + "-";
    }

    public TypeNode getType() {
        return this.type;
    }

    public boolean isSharp() {
        return this.sharp;
    }

    /** Flag indicating if this node is sharply typed. */
    private final boolean sharp;
    /** The (possibly {@code null}) type of this rule node. */
    private final TypeNode type;
}
