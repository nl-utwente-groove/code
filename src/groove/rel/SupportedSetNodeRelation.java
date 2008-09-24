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
 * $Id: SupportedSetNodeRelation.java,v 1.4 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.util.CollectionOfCollections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class SupportedSetNodeRelation extends AbstractNodeRelation implements SupportedNodeRelation {
    /**
     * Creates a new relation on the basis of a given universe of nodes.
     * @ensure <tt>getUniverse().equals(universe)</tt>
     */
    public SupportedSetNodeRelation(GraphShape graph) {
        super(graph);
    }

    public boolean addRelated(Edge edge) {
        Set<Element> support = new HashSet<Element>();
        support.add(edge);
        return addToSupport(createRelated(edge.source(), edge.opposite()), support);
    }

    public boolean addSelfRelated(Node node) {
        return addToSupport(createRelated(node, node), Collections.<Element>singleton(node));
    }

    public NodeRelation copy() {
        SupportedSetNodeRelation result = newInstance();
        result.supportMap.putAll(supportMap);
        return result;
    }

    public boolean doOr(NodeRelation other) {
        boolean result = false;
        for (Map.Entry<Edge,Collection<Element>> supportEntry: ((SupportedNodeRelation) other).getSupportMap().entrySet()) {
        	Edge rel = supportEntry.getKey();
            result |= addToSupport(rel, supportEntry.getValue());
        }
        return result;
    }

    public NodeRelation doThen(NodeRelation other) {
        supportMap = new HashMap<Edge,Collection<Element>>();
        for (Map.Entry<Edge,Collection<Element>> supportEntry: supportMap.entrySet()) {
        	Edge rel = supportEntry.getKey();
            for (Map.Entry<Edge,Collection<Element>> otherSupportEntry: ((SupportedNodeRelation) other).getSupportMap().entrySet()) {
            	Edge otherRel = otherSupportEntry.getKey();
                if (otherRel.source().equals(rel.opposite())) {
                	RelationEdge<Node> newRel = createRelated(rel.source(), otherRel.opposite());
                    addToSupport(newRel, supportEntry.getValue());
                    addToSupport(newRel, otherSupportEntry.getValue());
                }
            }
        }
        return this;
    }

    @Override
    protected boolean doOrThen() {
        boolean result = false;
        Map<Edge,Collection<Element>> oldSupportEntrySet = new HashMap<Edge,Collection<Element>>(supportMap);
        for (Map.Entry<Edge,Collection<Element>> supportEntry: oldSupportEntrySet.entrySet()) {
        	Edge rel = supportEntry.getKey();
            for (Map.Entry<Edge,Collection<Element>> otherSupportEntry: oldSupportEntrySet.entrySet()) {
            	Edge otherRel = otherSupportEntry.getKey();
                if (otherRel.source().equals(rel.opposite())) {
                	RelationEdge<Node> newRel = createRelated(rel.source(), otherRel.opposite());
                    result |= addToSupport(newRel, supportEntry.getValue());
                    result |= addToSupport(newRel, otherSupportEntry.getValue());
                }
            }
        }
        return result;
    }
    
    /**
     * This implementation iterates over the support map
     * and adds the inverse of each pair, with the same support.
     */
    public NodeRelation getInverse() {
        SupportedSetNodeRelation result = newInstance();
        for (Map.Entry<Edge,Collection<Element>> entry: supportMap.entrySet()) {
        	Edge related = entry.getKey();
            if (related.endCount() >= 2) {                
                result.addToSupport(createRelated(related.end(Edge.TARGET_INDEX), related.source()), entry.getValue());
            }
        }
        return result;
    }

    public Collection<Element> getSupport() {
        return new CollectionOfCollections<Element>(supportMap.values());
    }
    
    public Collection<Element> getSupport(Node pre, Node post) {
        return supportMap.get(createRelated(pre, post));
    }
    
    @Override
    public Set<? extends Edge> getAllRelated() {
        return Collections.unmodifiableSet(supportMap.keySet());
    }
    
    public Map<Edge,Collection<Element>> getSupportMap() {
        return Collections.unmodifiableMap(supportMap);
    }

    public boolean isEmpty() {
        return supportMap.isEmpty();
    }

    public SupportedSetNodeRelation newInstance() {
        return new SupportedSetNodeRelation(getGraph());
    }
    
    @Override
    protected Set<Edge> getRelatedSet() {
        return supportMap.keySet();
    }
    
    /**
     * Augments the support for a given edge.
     * The edge is added to the support map if it was not already there.
     */
    protected boolean addToSupport(Edge rel, Collection<Element> support) {
        boolean result;
        Collection<Element> existingSupport = supportMap.get(rel);
        if (existingSupport == null) {
            supportMap.put(rel, new HashSet<Element>(support));
            result = true;
        } else {
            result = existingSupport.addAll(support);
        }
        return result;
    }
    
    /** 
     * The underlying map containing the data of this relation,
     * stored as a mapping from edges (which encode the related pairs) to
     * collections of elements justifying them.
     */
    private Map<Edge,Collection<Element>> supportMap = new HashMap<Edge,Collection<Element>>();
}