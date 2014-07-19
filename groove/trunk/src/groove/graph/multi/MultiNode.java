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
package groove.graph.multi;

import groove.graph.ANode;

/**
 * Graph node in a multi-graph. Multi-nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MultiNode extends ANode {
    /**
     * Constructs a fresh node, with an explicitly given number. Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared. This is achieved by
     * only calling the constructor from factory methods.
     * @param nr the number for this node
     */
    MultiNode(int nr) {
        super(nr);
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }
}
