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
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the neighbourhood equivalence relation on graphs.
 * See Def. 17 on pg. 14 of the technical report "Graph Abstraction and
 * Abstract Graph Transformation."
 * 
 * Each object of this type stores a reference to the graph on which the
 * equivalence relation was computed and the radius of the iteration.
 * To compute the relation of radius i, call the constructor and then
 * the method refineEquivRelation() i times. 
 * 
 * @author Eduardo Zambon
 */
public class GraphNeighEquiv extends EquivRelation<Node> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The radius of the iteration. */
    private int radius;
    /** The graph on which the equivalence relation was computed. */
    protected Graph graph;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates a new equivalence relation with radius 0. */
    public GraphNeighEquiv(Graph graph) {
        this.graph = graph;
        // This is the first iteration.
        this.radius = 0;
        // Compute and store the equivalence classes based on node labels.
        for (EquivClass<Node> ec : computeInitialEquivClasses()) {
            this.add(ec);
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

    /** Computes the initial equivalence classes, based on node labels. */
    private Collection<EquivClass<Node>> computeInitialEquivClasses() {
        // Map from node labels to equivalence classes.
        Map<Set<Label>,EquivClass<Node>> labelsToClass =
            new HashMap<Set<Label>,EquivClass<Node>>();

        // Compute the equivalence classes.
        for (Node node : this.graph.nodeSet()) {
            // Collect node labels.
            Set<Label> nodeLabels = Util.getNodeLabels(this.graph, node);

            // Look in all label sets and try to find an equivalence class.
            EquivClass<Node> ec = null;
            for (Set<Label> labels : labelsToClass.keySet()) {
                if (labels.equals(nodeLabels)) {
                    // Found the equivalence class.
                    ec = labelsToClass.get(labels);
                }
            }

            // Put the node in the proper equivalence class.
            if (ec == null) {
                // We need to create a new equivalence class.
                ec = new EquivClass<Node>();
                ec.add(node);
                labelsToClass.put(nodeLabels, ec);
            } else {
                // The equivalence class already exists, just put the node in.
                ec.add(node);
            }
        }

        return labelsToClass.values();
    }

    /**
     * Computes the next iteration of the equivalence relation.
     * This method modifies the object, if you want to preserve the previous
     * iteration, make sure to clone the object first.
     */
    public void refineEquivRelation() {
        // We need these two sets because we cannot change the object
        // during iteration.

        // Equivalence classes created by splitting. 
        Set<EquivClass<Node>> newEquivClasses = new HashSet<EquivClass<Node>>();
        // Equivalence classes removed by splitting. 
        Set<EquivClass<Node>> delEquivClasses = new HashSet<EquivClass<Node>>();

        // For all equivalence classes.
        for (EquivClass<Node> ec : this) {
            this.refineEquivClass(ec, newEquivClasses, delEquivClasses);
        }

        // Update.
        this.removeAll(delEquivClasses);
        this.addAll(newEquivClasses);
        this.radius = this.radius + 1;
    }

    /**
     * Refines the given equivalence class (ec) by one iteration. If ec is
     * already stable, then nothing happens. If ec needs to be split, then
     * the new equivalence classes created are stored in the proper set given
     * as argument and ec is marked to be deleted. 
     */
    private void refineEquivClass(EquivClass<Node> ec,
            Set<EquivClass<Node>> newEquivClasses,
            Set<EquivClass<Node>> delEquivClasses) {

        // Convert the equivalence class to an array for efficiency's sake.
        Node nodes[] = new Node[ec.size()];
        ec.toArray(nodes);

        // Temporary upper triangular matrix to store equivalences.
        // This wastes some memory, but Java data structures are much worse...
        boolean equiv[][] = new boolean[nodes.length][nodes.length];

        // Perform pair-wise comparison with the elements of the array.
        // We know that any pair of nodes are equivalent in the previous
        // iteration because they are in the same equivalence class.
        // We have to check if they are still equivalent in this iteration.
        for (int i = 0; i < nodes.length - 1; i++) {
            Node ni = nodes[i];
            for (int j = i + 1; j < nodes.length; j++) {
                Node nj = nodes[j];
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
    private void doSplit(Node nodes[], boolean equiv[][],
            Set<EquivClass<Node>> newEquivClasses) {

        for (int i = 0; i < nodes.length; i++) {
            Node ni = nodes[i];

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
                EquivClass<Node> ec = new EquivClass<Node>();
                ec.add(ni);
                for (int j = i + 1; j < nodes.length; j++) {
                    if (equiv[i][j]) {
                        Node nj = nodes[j];
                        ec.add(nj);
                    }
                }
                // This is a new equivalence class.
                newEquivClasses.add(ec);
            }
        }
    }

    /**
     * Returns true if the two given nodes are still equivalent in the next
     * iteration. This method implements the second item of Def. 17 (see
     * comment on the class definition, top of this file).
     */
    protected boolean areStillEquivalent(Node n0, Node n1) {
        boolean equiv = true;
        // For all labels.
        labelLoop: for (Label label : Util.binaryLabelSet(this.graph)) {
            // For all equivalence classes.
            for (EquivClass<Node> ec : this) {
                Set<Edge> n0InterEc =
                    Util.getIntersectEdges(this.graph, n0, ec, label);
                Set<Edge> n1InterEc =
                    Util.getIntersectEdges(this.graph, n1, ec, label);
                Set<Edge> ecInterN0 =
                    Util.getIntersectEdges(this.graph, ec, n0, label);
                Set<Edge> ecInterN1 =
                    Util.getIntersectEdges(this.graph, ec, n1, label);
                equiv =
                    equiv && Multiplicity.haveSameMult(n0InterEc, n1InterEc)
                        && Multiplicity.haveSameMult(ecInterN0, ecInterN1);
                if (!equiv) {
                    break labelLoop;
                }
            }
        }
        return equiv;
    }

    /**
     * Returns true if the given edges are equivalent according to the
     * equivalence relation, i.e., if both edges have the same label, and if
     * both sources and targets are equivalent.
     */
    public boolean areEquivalent(Edge e0, Edge e1) {
        return e0.label().equals(e1.label())
            && this.areEquivalent(e0.source(), e1.source())
            && this.areEquivalent(e0.opposite(), e1.opposite());
    }

    /**
     * Builds and returns the equivalence class of the given edge, based on the
     * equivalence relation on nodes. The returned equivalence class is
     * neither stored nor cached, so call this method consciously.
     * Anti-lazy initialisation design pattern... :P
     */
    private EquivClass<Edge> getEdgeEquivClass(Edge edge) {
        EquivClass<Edge> ec = new EquivClass<Edge>();
        ec.add(edge);
        for (Edge e : this.graph.edgeSet()) {
            if (areEquivalent(edge, e)) {
                ec.add(e);
            }
        }
        return ec;
    }

    /**
     * Builds and returns the equivalence relation on edges, based on the
     * equivalence relation on nodes. The returned equivalence relation is
     * neither stored nor cached, so call this method consciously.
     * Anti-lazy initialisation design pattern... :P
     */
    public EquivRelation<Edge> getEdgesEquivRel() {
        EquivRelation<Edge> er = new EquivRelation<Edge>();
        for (Edge edge : this.graph.edgeSet()) {
            EquivClass<Edge> ec = this.getEdgeEquivClass(edge);
            er.add(ec);
        }
        return er;
    }

}
