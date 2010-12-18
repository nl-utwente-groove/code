/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.rete;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;

import java.util.HashMap;

/**
 * A look up table that determines which entry of a {@link ReteMatch}'s list of units
 * should be looked at to find the image of a specific node  
 * @author Arash Jalali
 * @version $Revision $
 */
public class LookupTable {
    private HashMap<Element,int[]> table = new HashMap<Element,int[]>();

    /**
     * Creates a lookup table from the pattern of elements of a RETE node
     * @param nnode The RETE network node the pattern of which 
     */
    public LookupTable(ReteNetworkNode nnode) {
        Element[] pattern = nnode.getPattern();
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] instanceof Node) {
                this.table.put(pattern[i], new int[] {i, -1});
            } else if (pattern[i] instanceof Edge) {
                this.table.put(pattern[i], new int[] {i, 0});
                this.table.put(((Edge) pattern[i]).source(), new int[] {i, 0});
                this.table.put(((Edge) pattern[i]).target(), new int[] {i, 1});
            }
        }
    }

    /**
     * @return the index with a match's array of match units if <code>e</code>
     * is defined in the lookup table, -1 if it's not.
     */
    public int getEdge(Edge e) {
        int[] res = this.table.get(e);
        if (res != null) {
            return res[0];
        } else {
            return -1;
        }
    }

    /**
     * @return if <code>n</code> is defined  an array of <code>int</code> with size of 2,
     * with element at index 0 pointing to the cell in a match's units array, 
     * and the second specifying if the the node is in the source of in the target(0=source, 
     * 1=target) of the edge. The second integer is -1 if the match unit at that position is 
     * a node match. If <code>n</code> is not defined, the return value is <code>null</code>. 
     */
    public int[] getNode(Node n) {
        return this.table.get(n);
    }
}
