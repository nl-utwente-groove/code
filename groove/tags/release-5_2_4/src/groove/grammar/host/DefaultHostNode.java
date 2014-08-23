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

import groove.grammar.AnchorKind;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.ANode;

/**
 * Default nodes used in host graphs.
 * Host nodes always have an associated {@link TypeNode};
 * this defaults to {@link TypeLabel#NODE}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultHostNode extends ANode implements HostNode {
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
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return getType() == ((DefaultHostNode) obj).getType();
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }

    @Override
    final public TypeNode getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    private final TypeNode type;
}
