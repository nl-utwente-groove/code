/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.shape;

import groove.abstraction.Multiplicity;
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.graph.Morphism;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Morphism between pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShapeMorphism extends
        Morphism<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Creates and returns an identity pattern shape morphism between the two
     * given pattern shapes. Used during the materialisation phase.
     * Fails on an assertion if the given shapes are not identical.
     */
    public static PatternShapeMorphism createIdentityMorphism(
            PatternShape from, PatternShape to) {
        PatternShapeMorphism result =
            new PatternShapeMorphism(from.getFactory());
        for (PatternNode node : from.nodeSet()) {
            assert to.containsNode(node);
            result.putNode(node, node);
        }
        for (PatternEdge edge : from.edgeSet()) {
            assert to.containsEdge(edge);
            result.putEdge(edge, edge);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates a pattern shape morphism with a given element factory. */
    public PatternShapeMorphism(PatternFactory factory) {
        super(factory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public PatternShapeMorphism clone() {
        return (PatternShapeMorphism) super.clone();
    }

    @Override
    public PatternShapeMorphism newMap() {
        return new PatternShapeMorphism(getFactory());
    }

    @Override
    public PatternFactory getFactory() {
        return (PatternFactory) super.getFactory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternNode> getPreImages(PatternNode node) {
        return (Set<PatternNode>) super.getPreImages(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternEdge> getPreImages(PatternEdge edge) {
        return (Set<PatternEdge>) super.getPreImages(edge);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns a set of outgoing edges grouped per source node. */
    private Map<PatternNode,Set<PatternEdge>> getPreImagesMap(PatternEdge edge) {
        // EZ says: this method is inefficient but it is only used in assertions.
        Map<PatternNode,Set<PatternEdge>> result =
            new MyHashMap<PatternNode,Set<PatternEdge>>();
        for (PatternEdge newEdge : getPreImages(edge)) {
            Set<PatternEdge> edgeSet = result.get(newEdge.source());
            if (edgeSet == null) {
                edgeSet = new MyHashSet<PatternEdge>();
                result.put(newEdge.source(), edgeSet);
            }
            edgeSet.add(newEdge);
        }
        return result;
    }

    /** Returns true if all keys are in 'from' and all values in 'to'. */
    public boolean isConsistent(PatternShape from, PatternShape to) {
        for (Entry<PatternNode,PatternNode> entry : this.nodeMap().entrySet()) {
            if (!from.containsNode(entry.getKey())
                || !to.containsNode(entry.getValue())) {
                return false;
            }
        }
        for (Entry<PatternEdge,PatternEdge> entry : this.edgeMap().entrySet()) {
            if (!from.containsEdge(entry.getKey())
                || !to.containsEdge(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /** Implements the conditions of a subsumption pattern shape morphism. */
    public boolean isValid(PatternShape from, PatternShape to) {
        // EZ says: this method is inefficient but it is only used in assertions.
        // Check node multiplicities.
        for (PatternNode nodeT : to.nodeSet()) {
            Multiplicity nodeTMult = to.getMult(nodeT);
            Set<PatternNode> nodesS = getPreImages(nodeT);
            Multiplicity sum = from.getNodeSetMultSum(nodesS);
            // EZ says: subsumption is too strong, we can only check for <=.
            // This is due to the loss of precision when subtracting.
            // For example, 3+ - 2+ = 0+ but then 2+ + 0+ = 2+ which is NOT
            // subsumed by 3+! However, 2+ <= 3+ holds.
            // if (!nodeTMult.subsumes(sum)) {
            if (!sum.le(nodeTMult)) {
                return false;
            }
        }
        // Check edge multiplicities.
        for (PatternEdge edgeT : to.edgeSet()) {
            Multiplicity edgeTMult = to.getMult(edgeT);
            for (Set<PatternEdge> edgesS : getPreImagesMap(edgeT).values()) {
                Multiplicity sum = from.getEdgeSetMultSum(edgesS);
                // EZ says: see comment above.
                // if (!edgeTMult.subsumes(sum)) {
                if (!sum.le(edgeTMult)) {
                    return false;
                }
            }
        }
        return true;
    }
}
