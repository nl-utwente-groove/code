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
 * $Id$
 */
package groove.abstraction.neigh.trans;

import groove.abstraction.MyHashSet;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeFactory;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeLabel;
import groove.graph.Edge;
import groove.graph.Node;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Mapping from rules to shapes, used in pre-matches and matches.
 *
 * @author Eduardo Zambon
 */
public final class RuleToShapeMap extends RuleToHostMap {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates an empty rule-to-shape match. */
    public RuleToShapeMap(ShapeFactory factory) {
        super(factory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<RuleEdge,ShapeEdge> edgeMap() {
        return (Map<RuleEdge,ShapeEdge>) super.edgeMap();
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<RuleNode,ShapeNode> nodeMap() {
        return (Map<RuleNode,ShapeNode>) super.nodeMap();
    }

    @Override
    public TypeElement putVar(LabelVar var, TypeElement value) {
        assert !isFixed();
        return super.putVar(var, value);
    }

    /* Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge mapEdge(RuleEdge key) {
        assert !isFixed();
        return (ShapeEdge) super.mapEdge(key);
    }

    /* Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeNode putNode(RuleNode key, HostNode layout) {
        assert !isFixed();
        return (ShapeNode) super.putNode(key, layout);
    }

    /* Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge putEdge(RuleEdge key, HostEdge layout) {
        assert !isFixed();
        return (ShapeEdge) super.putEdge(key, layout);
    }

    /* Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeNode removeNode(RuleNode key) {
        assert !isFixed();
        return (ShapeNode) super.removeNode(key);
    }

    /* Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge removeEdge(RuleEdge key) {
        assert !isFixed();
        return (ShapeEdge) super.removeEdge(key);
    }

    /* Specialises the return type. */
    @Override
    public ShapeNode getNode(Node key) {
        return (ShapeNode) super.getNode(key);
    }

    /* Specialises the return type. */
    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    @Override
    public RuleToShapeMap clone() {
        RuleToShapeMap result = new RuleToShapeMap(getFactory());
        result.putAll(this);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleNode> getPreImages(Node node) {
        assert node instanceof ShapeNode;
        return (Set<RuleNode>) super.getPreImages(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> getPreImages(Edge edge) {
        assert edge instanceof ShapeEdge;
        return (Set<RuleEdge>) super.getPreImages(edge);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns an self edge from given node with given label. Maybe be null. */
    public RuleEdge getSelfEdge(RuleNode node, TypeLabel label) {
        RuleEdge result = null;
        for (RuleEdge edge : this.edgeMap().keySet()) {
            if (edge.label().compareTo(label) == 0 && edge.source().equals(node)
                && edge.target().equals(node)) {
                result = edge;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a set of values for the node map. Contrary to calling
     * nodeMap().values(), the set has no repeated values.
     */
    public Set<ShapeNode> nodeMapValueSet() {
        Set<ShapeNode> result = new MyHashSet<ShapeNode>();
        result.addAll(this.nodeMap().values());
        return result;
    }

    /**
     * Return the set of inconsistent edges, i.e., with a mapping that does not
     * conform to the node map.
     */
    Set<ShapeEdge> getInconsistentEdges() {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (Entry<RuleEdge,ShapeEdge> entry : this.edgeMap().entrySet()) {
            RuleEdge edgeR = entry.getKey();
            ShapeEdge edgeS = entry.getValue();
            if (this.isSrcInconsistent(edgeR, edgeS) || this.isTgtInconsistent(edgeR, edgeS)) {
                result.add(edgeS);
            }
        }
        return result;
    }

    /** Checks the consistency between node and edge maps. */
    boolean isConsistent() {
        boolean result = true;
        for (Entry<RuleEdge,ShapeEdge> entry : this.edgeMap().entrySet()) {
            RuleEdge edgeR = entry.getKey();
            ShapeEdge edgeS = entry.getValue();
            if (this.isSrcInconsistent(edgeR, edgeS) || this.isTgtInconsistent(edgeR, edgeS)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Returns true if the actual image of the given rule node in this map
     * differs from the expected image given.
     */
    private boolean isNodeInconsistent(RuleNode nodeR, ShapeNode expectedImage) {
        return !this.getNode(nodeR).equals(expectedImage);
    }

    /**
     * Returns true if the actual image of the source node of the given rule
     * edge in this map differs from the expected image given.
     */
    private boolean isSrcInconsistent(RuleEdge edgeR, ShapeEdge expectedImage) {
        return this.isNodeInconsistent(edgeR.source(), expectedImage.source());
    }

    /**
     * Returns true if the actual image of the target node of the given rule
     * edge in this map differs from the expected image given.
     */
    private boolean isTgtInconsistent(RuleEdge edgeR, ShapeEdge expectedImage) {
        return this.isNodeInconsistent(edgeR.target(), expectedImage.target());
    }

}
