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
 * $Id: InjectionSearchItem.java,v 1.12 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A search item that checks distinctness of two node images.
 * @author Arend Rensink
 * @version $Revision $
 */
public class InjectionSearchItem extends AbstractSearchItem {
    /** 
     * Constructs an injection item, which checks for the injectivity
     * of the match found so far. That is, the item will match if and only if 
     * two given nodes are matched injectively. 
     * @param node1 the first node that should be matched injectively
     * @param node2 the second node that should be matched injectively
     */
    public InjectionSearchItem(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.neededNodes = new HashSet<Node>();
        neededNodes.add(node1);
        neededNodes.add(node2);
    }
    
    /** 
     * Constructs an injection item, which checks for the injectivity
     * of the match found so far. That is, the item will match if and only if the
     * nodes in a given set have been matched injectively.
     * @param nodes the nodes that should be matched injectively
     */
    public InjectionSearchItem(Collection<? extends Node> nodes) {
        assert nodes.size() == 2: String.format("Injection %s should have size 2", nodes);
        Iterator<? extends Node> nodeIter = nodes.iterator();
        this.node1 = nodeIter.next();
        this.node2 = nodeIter.next();
        this.neededNodes = new HashSet<Node>(nodes);
    }
    
	public InjectionRecord getRecord(Search matcher) {
		return new InjectionRecord(matcher);
	}
	
    /**
     * Returns the set consisting of the nodes for which this item checks injectivity.
     */
    @Override
    public Collection<Node> needsNodes() {
        return neededNodes;
    }

    @Override
	public String toString() {
		return String.format("Separate %s and %s", node1, node2); 
	}
    
    /** 
     * Since the order of injection search items does not influence the match,
     * all of them have the same rating.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }
    
	public void activate(SearchPlanStrategy strategy) {
        node1Ix = strategy.getNodeIx(node1);
        node2Ix = strategy.getNodeIx(node2);
    }

    /**
	 * First node which may not be merged.
	 */
	final Node node1;
	/**
	 * Second node which may not be merged.
	 */
    final Node node2;
    /** Collection consisting of <code>node1</code> and <code>node2</code>. */
    private final Collection<Node> neededNodes;
    /** Node index (in the result) of {@link #node1}. */
    int node1Ix;
    /** Node index (in the result) of {@link #node2}. */
    int node2Ix;
    
    /** The record for this search item. */
    private class InjectionRecord extends SingularRecord {
        /** Constructs a fresh record, for a given matcher. */
        InjectionRecord(Search search) {
            super(search);
            assert search.getNode(node1Ix) != null : String.format("Merge embargo node %s not yet matched", node1);
            assert search.getNode(node2Ix) != null: String.format("Merge embargo node %s not yet matched", node2);
        }

        /**
         * Tests if the images of {@link #node1} and {@link #node2} are distinct.
         */
        @Override
        boolean set() {
            return search.getNode(node1Ix) != search.getNode(node2Ix);
        }
    }
}
