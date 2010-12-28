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

/**
 * Default nodes used in host graphs.
 * Node equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision: 2936 $
 */
public class DefaultHostNode extends AbstractNode implements HostNode,
        Node.Factory<DefaultHostNode> {
    /**
     * Constructs a fresh node, with an explicitly given number. Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared. This is achieved by
     * using one of the <code>createNode</code> methods in preference to this
     * constructor.
     * @param nr the number for this node
     */
    protected DefaultHostNode(int nr) {
        super(nr);
    }

    /** Factory constructor. */
    public DefaultHostNode newNode(int nr) {
        return new DefaultHostNode(nr);
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }
}
