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
 * Default nodes used in host graphs.
 * Host nodes always have an associated {@link TypeNode};
 * this defaults to {@link TypeNode#TOP_NODE}.
 * @author Arend Rensink
 * @version $Revision: 2936 $
 */
public class DefaultHostNode extends AbstractNode implements HostNode,
        Node.Factory<HostNode> {
    /**
     * Constructs a fresh node, with an explicitly given number. Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared. This is achieved by
     * using one of the <code>createNode</code> methods in preference to this
     * constructor.
     * @param nr the number for this node
     */
    protected DefaultHostNode(int nr) {
        this(nr, TypeNode.TOP_NODE);
    }

    /**
     * Constructs a fresh node, with an explicitly given number and node type.
     * Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared. This is achieved by
     * using one of the <code>createNode</code> methods in preference to this
     * constructor.
     * @param nr the number for this node
     * @param type the node type; may be {@code null}.
     */
    protected DefaultHostNode(int nr, TypeNode type) {
        super(nr);
        assert type != null : "Can't create untyped host node";
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DefaultHostNode other = (DefaultHostNode) obj;
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
    public DefaultHostNode newNode(int nr, TypeNode type) {
        return new DefaultHostNode(nr, type);
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return getType() == TypeNode.TOP_NODE ? "n"
                : getType().getLabel().text() + "-";
    }

    @Override
    final public TypeNode getType() {
        return this.type;
    }

    private final TypeNode type;

    /** Creates a node factory for a given node type.
     * @param type the node type for the factory; may be {@code null}. 
     */
    public static Node.Factory<HostNode> createFactory(TypeNode type) {
        return new DefaultHostNode(0, type);
    }
}
