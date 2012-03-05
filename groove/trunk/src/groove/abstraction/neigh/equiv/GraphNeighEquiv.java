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
package groove.abstraction.neigh.equiv;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.TreeHashSet;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the neighbourhood equivalence relation on graphs.
 * See Def. 17 on pg. 14 of the technical report "Graph Abstraction and
 * Abstract Graph Transformation."
 * 
 * Each object of this type stores a reference to the graph on which the
 * equivalence relation was computed and the radius of the iteration.
 * 
 * @author Eduardo Zambon
 */
public class GraphNeighEquiv extends EquivRelation<HostNode> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The radius of the iteration. */
    private int radius;
    /** The graph on which the equivalence relation was computed. */
    final HostGraph graph;
    /** The set of binary labels of the given graph. Used to improve performance. */
    final Set<TypeLabel> binaryLabels;
    /** Mapping from host nodes to their cells in this equivalence relation. */
    private Map<HostNode,EquivClass<HostNode>> nodeToCellMap;
    /** Mapping from host nodes to their distinguishing characteristic. */
    private Map<HostNode,NodeInfo> nodeToInfoMap;
    /** The previously computed equivalence relation. */
    private EquivRelation<HostNode> previous;
    /** Temporary store. */
    private TreeHashSet<EdgeEquivData> store;

    /** Global edge multiplicity of one. */
    private final static Multiplicity EDGE_ONE_MULT =
        Multiplicity.getMultiplicity(1, 1, MultKind.EDGE_MULT);

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates a new equivalence relation with given non-null radius. */
    public GraphNeighEquiv(HostGraph graph, int radius) {
        assert graph != null;
        assert radius >= 0;
        this.graph = graph;
        this.binaryLabels = Util.getBinaryLabels(this.graph);
        this.radius = radius;
        this.previous = null;
        // Compute and store the equivalence classes based on node labels.
        // Compute radius 0.
        this.computeInitialEquivClasses();
        // Compute radius i from 1 .. this.radius .
        for (int i = 1; i <= this.radius; i++) {
            this.refineEquivRelation();
        }
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Radius: " + this.radius + ", Equiv. Classes: "
            + super.toString();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the previously computed equivalence relation.
     * Fails on an assertion if this relation is null.
     */
    public EquivRelation<HostNode> getPrevEquivRelation() {
        assert this.previous != null;
        return this.previous;
    }

    /** Computes the initial equivalence classes, based on node labels. */
    private void computeInitialEquivClasses() {
        // Map from node labels to equivalence classes.
        Map<Set<TypeLabel>,EquivClass<HostNode>> labelsToClass =
            new MyHashMap<Set<TypeLabel>,EquivClass<HostNode>>();
        // Get the set of labels to be used in the abstraction.
        Set<TypeLabel> absLabels = Parameters.getAbsLabels();

        // Compute the equivalence classes.
        for (HostNode node : this.graph.nodeSet()) {
            // Collect node labels.
            Set<TypeLabel> nodeLabels = Util.getNodeLabels(this.graph, node);

            EquivClass<HostNode> ec = null;

            if (!absLabels.isEmpty() && !absLabels.containsAll(nodeLabels)) {
                // We have a node label that should not be grouped by the
                // abstraction. This means that the node will be put in a
                // singleton equivalence class.
                ec = createNodeEquivClass();
                ec.add(node);
                this.add(ec);
                continue;
            }

            // Look in the map and try to find an equivalence class.
            ec = labelsToClass.get(nodeLabels);

            // Put the node in the proper equivalence class.
            if (ec == null) {
                // We need to create a new equivalence class.
                ec = createNodeEquivClass();
                labelsToClass.put(nodeLabels, ec);
            }
            // The equivalence class already exists, just put the node in.
            ec.add(node);
        }
        this.addAll(labelsToClass.values());
    }

    /** Computes the next iteration of the equivalence relation. */
    private void refineEquivRelation() {
        // Clone the equivalence relation.
        this.previous = this.clone();

        // We need these two sets because we cannot change the object
        // during iteration.

        // Equivalence classes created by splitting. 
        EquivRelation<HostNode> newEquivClasses = new EquivRelation<HostNode>();
        // Equivalence classes removed by splitting. 
        EquivRelation<HostNode> delEquivClasses = new EquivRelation<HostNode>();

        prepareRefinement();
        // For all equivalence classes.
        for (EquivClass<HostNode> ec : this) {
            this.refineEquivClass(ec, newEquivClasses, delEquivClasses);
        }

        // Update.
        this.removeAll(delEquivClasses);
        this.addAll(newEquivClasses);
    }

    /**
     * Refines the given equivalence class (ec) by one iteration. If ec is
     * already stable, then nothing happens. If ec needs to be split, then
     * the new equivalence classes created are stored in the proper set given
     * as argument and ec is marked to be deleted. 
     */
    private void refineEquivClass(EquivClass<HostNode> ec,
            EquivRelation<HostNode> newEquivClasses,
            EquivRelation<HostNode> delEquivClasses) {

        if (ec.size() == 1) {
            return;
        }

        // partition the nodes in ec according to their node info.
        Map<NodeInfo,EquivClass<HostNode>> partition =
            new HashMap<NodeInfo,EquivClass<HostNode>>();
        Map<HostNode,NodeInfo> nodeToInfoMap = getNodeToInfoMap();
        for (HostNode n : ec) {
            NodeInfo info = nodeToInfoMap.get(n);
            EquivClass<HostNode> cell = partition.get(info);
            if (cell == null) {
                partition.put(info, cell = createNodeEquivClass());
            }
            cell.add(n);
        }

        // Check if we must split the equivalence class.
        if (partition.size() > 1) {
            // Yes, we must. Do it.
            newEquivClasses.addAll(partition.values());
            delEquivClasses.add(ec);
        } // else do nothing.
    }

    /** Prepares the internal data structures for the next refinement iteration. */
    private void prepareRefinement() {
        this.nodeToCellMap = null;
        this.nodeToInfoMap = null;
    }

    /**
     * Returns the mapping from nodes to the corresponding cells in this
     * equivalence relation.
     */
    Map<HostNode,EquivClass<HostNode>> getNodeToCellMap() {
        if (this.nodeToCellMap == null) {
            this.nodeToCellMap = new HashMap<HostNode,EquivClass<HostNode>>();
            for (EquivClass<HostNode> ec : this) {
                for (HostNode node : ec) {
                    this.nodeToCellMap.put(node, ec);
                }
            }
        }
        return this.nodeToCellMap;
    }

    /** Returns the mapping from nodes to their distinguishing multiplicity information. */
    Map<HostNode,NodeInfo> getNodeToInfoMap() {
        if (this.nodeToInfoMap == null) {
            this.nodeToInfoMap = computeNodeToInfoMap();
        }
        return this.nodeToInfoMap;
    }

    /**
     * Constructs the mapping from nodes to their distinguishing
     * multiplicity information.
     */
    Map<HostNode,NodeInfo> computeNodeToInfoMap() {
        Map<HostNode,NodeInfo> result = createNodeToInfoMap();
        Map<HostNode,EquivClass<HostNode>> nodeToCellMap = getNodeToCellMap();
        for (HostEdge edge : this.graph.edgeSet()) {
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                NodeInfo sourceInfo = result.get(dir.incident(edge));
                EquivClass<HostNode> targetEc =
                    nodeToCellMap.get(dir.opposite(edge));
                sourceInfo.add(dir, edge.label(), targetEc, EDGE_ONE_MULT);
            }
        }
        return result;
    }

    /**
     * Constructs a blank map from nodes to information objects.
     * The map is initialised with empty information objects.
     */
    Map<HostNode,NodeInfo> createNodeToInfoMap() {
        Map<HostNode,NodeInfo> result =
            new HashMap<HostNode,GraphNeighEquiv.NodeInfo>();
        for (HostNode node : this.graph.nodeSet()) {
            result.put(node, new NodeInfo());
        }
        return result;
    }

    /** Creates and returns a new node equivalence class object. */
    private NodeEquivClass<HostNode> createNodeEquivClass() {
        return new NodeEquivClass<HostNode>(this.graph.getFactory());
    }

    /**
     * Returns true if the two given nodes are still equivalent in the next
     * iteration. This method implements the second item of Def. 17 (see
     * comment on the class definition, top of this file).
     */
    // EZ says: this method can certainly be optimized. In particular, an
    // implementation for method prepareRefinement should be provided. This
    // optimization is not needed for now because the only time this class is
    // used is when constructing a shape from the start graph.
    boolean areStillEquivalent(HostNode n0, HostNode n1) {
        boolean equiv = true;
        Set<HostEdge> intersectEdges = new MyHashSet<HostEdge>();
        // For all labels.
        labelLoop: for (TypeLabel label : this.binaryLabels) {
            // For all equivalence classes.
            for (EquivClass<HostNode> ec : this) {
                Util.getIntersectEdges(this.graph, n0, ec, label,
                    intersectEdges);
                Multiplicity n0InterEc =
                    Multiplicity.getEdgeSetMult(intersectEdges);
                Util.getIntersectEdges(this.graph, n1, ec, label,
                    intersectEdges);
                Multiplicity n1InterEc =
                    Multiplicity.getEdgeSetMult(intersectEdges);
                Util.getIntersectEdges(this.graph, ec, n0, label,
                    intersectEdges);
                Multiplicity ecInterN0 =
                    Multiplicity.getEdgeSetMult(intersectEdges);
                Util.getIntersectEdges(this.graph, ec, n1, label,
                    intersectEdges);
                Multiplicity ecInterN1 =
                    Multiplicity.getEdgeSetMult(intersectEdges);
                equiv =
                    equiv && n0InterEc.equals(n1InterEc)
                        && ecInterN0.equals(ecInterN1);
                if (!equiv) {
                    break labelLoop;
                }
            }
        }
        return equiv;
    }

    /**
     * Builds and returns the equivalence relation on edges, based on the
     * equivalence relation on nodes. The returned equivalence relation is
     * neither stored nor cached, so call this method consciously.
     */
    public EquivRelation<HostEdge> getEdgesEquivRel() {
        // Initialise the auxiliary store.
        this.store = new TreeHashSet<EdgeEquivData>();
        // Map from the equivalence information on edges to corresponding
        // equivalence classes.
        Map<EdgeEquivData,EquivClass<HostEdge>> edgeMap =
            new MyHashMap<EdgeEquivData,EquivClass<HostEdge>>();

        for (HostEdge edge : this.graph.edgeSet()) {
            // Normalise the object.
            EdgeEquivData eed = this.getNormalEdgeEquivData(edge);
            EquivClass<HostEdge> edges = edgeMap.get(eed);
            if (edges == null) {
                edges = new EdgeEquivClass<HostEdge>();
                edgeMap.put(eed, edges);
            }
            edges.add(edge);
        }

        EquivRelation<HostEdge> er = new EquivRelation<HostEdge>();
        er.addAll(edgeMap.values());

        return er;
    }

    /** Returns the normalised equivalence data for the given edge. */
    private EdgeEquivData getNormalEdgeEquivData(HostEdge edge) {
        // Create a new object to provide the hash code.
        EdgeEquivData eed = new EdgeEquivData(edge);
        // Check the tree hash.
        EdgeEquivData result = this.store.put(eed);
        if (result == null) {
            // We found a new normal eed.
            result = eed;
        }
        return result;
    }

    /** 
     * Directional mapping from type labels and target node equivalence classes
     * to the corresponding multiplicity, for a certain host node.
     */
    class NodeInfo
            extends
            EnumMap<EdgeMultDir,Map<TypeLabel,Map<EquivClass<HostNode>,Multiplicity>>> {
        /** Constructs the equivalence information for a given host node. */
        NodeInfo() {
            super(EdgeMultDir.class);
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                put(dir,
                    new HashMap<TypeLabel,Map<EquivClass<HostNode>,Multiplicity>>());
            }
        }

        void add(EdgeMultDir dir, TypeLabel label, EquivClass<HostNode> ec,
                Multiplicity mult) {
            Map<TypeLabel,Map<EquivClass<HostNode>,Multiplicity>> labelMap =
                get(dir);
            Map<EquivClass<HostNode>,Multiplicity> ecMap = labelMap.get(label);
            if (ecMap == null) {
                labelMap.put(label, ecMap =
                    new HashMap<EquivClass<HostNode>,Multiplicity>());
            }
            Multiplicity oldMult = ecMap.get(ec);
            Multiplicity newMult = oldMult == null ? mult : oldMult.add(mult);
            ecMap.put(ec, newMult);
        }

        @Override
        public int hashCode() {
            if (this.hashcode == 0) {
                this.hashcode = super.hashCode();
                if (this.hashcode == 0) {
                    this.hashcode = 1;
                }
            }
            return this.hashcode;
        }

        private int hashcode;
    }

    /**
     * Auxiliary class use to store the fields used to distinguish edges in the
     * equivalence relation. Objects of this class are essentially used to
     * compute hash codes.
     * 
     * @author Eduardo Zambon
     */
    private class EdgeEquivData {

        /**
         * The equivalence class of the source node of the edge given to the
         * constructor.
         */
        final EquivClass<HostNode> srcEc;
        /** The label of the edge given to the constructor. */
        final TypeLabel label;
        /**
         * The equivalence class of the target node of the edge given to the
         * constructor.
         */
        final EquivClass<HostNode> tgtEc;
        /** The hash code of the object. */
        final int hashCode;

        /** Basic constructor, just fills the fields. */
        EdgeEquivData(HostEdge edge) {
            this.srcEc = GraphNeighEquiv.this.getEquivClassOf(edge.source());
            this.label = edge.label();
            this.tgtEc = GraphNeighEquiv.this.getEquivClassOf(edge.target());
            this.hashCode = this.computeHashCode();
        }

        @Override
        public boolean equals(Object o) {
            boolean result;
            if (this == o) {
                result = true;
            } else if (!(o instanceof EdgeEquivData)) {
                result = false;
            } else {
                EdgeEquivData eed = (EdgeEquivData) o;
                result =
                    this.label.equals(eed.label)
                        && this.srcEc.equals(eed.srcEc)
                        && this.tgtEc.equals(eed.tgtEc);
            }
            // Check for consistency between equals and hashCode.
            assert (!result || this.hashCode() == o.hashCode());
            return result;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        /** Callback method for computing the hash code. */
        private int computeHashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.label.hashCode();
            result = prime * result + this.srcEc.hashCode();
            result = prime * result + this.tgtEc.hashCode();
            return result;
        }

    }

}
