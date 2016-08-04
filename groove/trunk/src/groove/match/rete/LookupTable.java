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

import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleNode;
import groove.match.rete.LookupEntry.Role;

import java.util.HashMap;

/**
 * A look up table that determines which entry of a {@link AbstractReteMatch}'s list of units
 * should be looked at to find the image of a specific node  
 * @author Arash Jalali
 * @version $Revision $
 */
public class LookupTable {
    final private HashMap<RuleNode,LookupEntry> nodeTable =
        new HashMap<>();

    final private HashMap<RuleEdge,Integer> edgeTable =
        new HashMap<>();

    /**
     * Creates a lookup table from the pattern of elements of a RETE node
     * @param nnode The RETE network node the pattern of which 
     */
    public LookupTable(ReteNetworkNode nnode) {
        RuleElement[] pattern = nnode.getPattern();
        for (int i = 0; i < pattern.length; i++) {
            RuleElement elem = pattern[i];
            if (elem instanceof RuleNode) {
                this.nodeTable.put((RuleNode) elem, new LookupEntry(i,
                    Role.NODE));
            } else if (pattern[i] instanceof RuleEdge) {
                RuleEdge edge = (RuleEdge) elem;
                this.edgeTable.put(edge, i);
                this.nodeTable.put(edge.source(), new LookupEntry(i,
                    Role.SOURCE));
                this.nodeTable.put(edge.target(), new LookupEntry(i,
                    Role.TARGET));
            }
        }
    }

    /**
     * @return the index with a match's array of match units if <code>e</code>
     * is defined in the lookup table, -1 if it's not.
     */
    public int getEdge(RuleEdge e) {
        Integer result = this.edgeTable.get(e);
        return result == null ? -1 : result;
    }

    /**
     * Returns a lookup entry for a given rule node.
     * If <code>n</code> is defined, the result consists of the
     * index of the defining element for the node in a pattern or match array,
     * and the second indicates if the defining element is a node, or an edge
     * of which the required node is the source or target. 
     * If <code>n</code> is not defined, the return value is <code>null</code>. 
     */
    public LookupEntry getNode(RuleNode n) {
        return this.nodeTable.get(n);
    }
}
