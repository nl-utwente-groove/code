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
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.Util;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.TreeHashSet;

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
    /** The set of binary labels of the given graph. */
    final Set<TypeLabel> binaryLabels;
    /** The previously computed equivalence relation. */
    private EquivRelation<HostNode> previous;
    /** Temporary store. */
    private TreeHashSet<EdgeEquivData> store;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates a new equivalence relation with given radius. */
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

    /** Basic getter method. */
    public int getRadius() {
        return this.radius;
    }

    /** Returns the previously computed equivalence relation. */
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
                ec = newNodeEquivClass();
                ec.add(node);
                this.add(ec);
                continue;
            }

            // Look in the map and try to find an equivalence class.
            ec = labelsToClass.get(nodeLabels);

            // Put the node in the proper equivalence class.
            if (ec == null) {
                // We need to create a new equivalence class.
                ec = newNodeEquivClass();
                ec.add(node);
                labelsToClass.put(nodeLabels, ec);
            } else {
                // The equivalence class already exists, just put the node in.
                ec.add(node);
            }
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

        this.prepareRefinement();

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

        // Convert the equivalence class to an array for efficiency's sake.
        HostNode nodes[] = new HostNode[ec.size()];
        ec.toArray(nodes);

        // Temporary upper triangular matrix to store equivalences.
        // This wastes some memory, but Java data structures are much worse...
        boolean equiv[][] = new boolean[nodes.length][nodes.length];

        // Perform pair-wise comparison with the elements of the array.
        // We know that any pair of nodes are equivalent in the previous
        // iteration because they are in the same equivalence class.
        // We have to check if they are still equivalent in this iteration.
        for (int i = 0; i < nodes.length - 1; i++) {
            HostNode ni = nodes[i];
            for (int j = i + 1; j < nodes.length; j++) {
                HostNode nj = nodes[j];
                equiv[i][j] = this.areStillEquivalent(ni, nj);
            }
        }

        // Check if we must split the equivalence class.
        if (this.mustSplit(equiv)) {
            // Yes, we must. Do it.
            this.doSplit(nodes, equiv, newEquivClasses);
            delEquivClasses.add(ec);
        } // else do nothing.
    }

    /**
     * Returns true if the equivalence relation stored in the equivalence matrix
     * given needs to be split into two or more new classes.
     */
    private boolean mustSplit(boolean equiv[][]) {
        assert equiv.length > 0 && equiv[0].length > 0 : "Invalid equivalence matrix";
        boolean result = false;
        // From the way the matrix was built, we only have to look at its
        // first row. If there is at least one false value in the first row
        // then we know that there are at least two nodes that are no longer
        // equivalent.
        for (int j = 1; j < equiv[0].length; j++) {
            if (!equiv[0][j]) {
                // We have two nodes that are no longer equivalent. Split.
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Split an equivalence class into two or more new equivalence classes.
     * @param nodes - the equivalence class to be split, as an array of nodes.
     * @param equiv - equivalence relation stored in an equivalence matrix.
     * @param newEquivClasses - the set to store the newly created equivalence
     *                          classes.
     */
    private void doSplit(HostNode nodes[], boolean equiv[][],
            EquivRelation<HostNode> newEquivClasses) {

        for (int i = 0; i < nodes.length; i++) {
            HostNode ni = nodes[i];

            // Check first if the i-th element appears in a previous row of the
            // equivalence matrix. Since the matrix is upper triangular, we
            // only have to check the i-th column.
            boolean alreadyIncluded = false;
            // Variable ii ranges from 0 .. i - 1.
            // Variable i has its role reversed and marks the column instead of
            // a row.
            for (int ii = i - 1; ii >= 0; ii--) {
                if (equiv[ii][i]) {
                    alreadyIncluded = true;
                    break;
                }
            }

            if (!alreadyIncluded) {
                // OK, sweep the row and collect the equivalent nodes to
                // create a new equivalence class.
                EquivClass<HostNode> ec = newNodeEquivClass();
                ec.add(ni);
                for (int j = i + 1; j < nodes.length; j++) {
                    if (equiv[i][j]) {
                        HostNode nj = nodes[j];
                        ec.add(nj);
                    }
                }
                // This is a new equivalence class.
                newEquivClasses.add(ec);
            }
        }
    }

    void prepareRefinement() {
        // Empty by design.
    }

    private NodeEquivClass<HostNode> newNodeEquivClass() {
        return new NodeEquivClass<HostNode>(this.graph.getFactory());
    }

    /**
     * Returns true if the two given nodes are still equivalent in the next
     * iteration. This method implements the second item of Def. 17 (see
     * comment on the class definition, top of this file).
     */
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
        this.store = new TreeHashSet<EdgeEquivData>();
        Map<EdgeEquivData,EquivClass<HostEdge>> edgeMap =
            new MyHashMap<EdgeEquivData,EquivClass<HostEdge>>();

        for (HostEdge edge : this.graph.edgeSet()) {
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

    private EdgeEquivData getNormalEdgeEquivData(HostEdge edge) {
        EdgeEquivData eed = new EdgeEquivData(edge);
        EdgeEquivData result = this.store.put(eed);
        if (result == null) {
            result = eed;
        }
        return result;
    }

    private class EdgeEquivData {

        final EquivClass<HostNode> srcEc;
        final TypeLabel label;
        final EquivClass<HostNode> tgtEc;
        final int hashCode;

        EdgeEquivData(HostEdge edge) {
            this(edge.source(), edge.label(), edge.target());
        }

        EdgeEquivData(HostNode source, TypeLabel label, HostNode target) {
            this.srcEc = GraphNeighEquiv.this.getEquivClassOf(source);
            this.label = label;
            this.tgtEc = GraphNeighEquiv.this.getEquivClassOf(target);
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
