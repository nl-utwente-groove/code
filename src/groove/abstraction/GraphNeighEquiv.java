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
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class GraphNeighEquiv extends EquivRelation<Node> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private int radius;
    private Graph graph;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** EDUARDO */
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

    /** EDUARDO */
    public int getRadius() {
        return this.radius;
    }

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

    /** EDUARDO */
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

    private boolean mustSplit(boolean equiv[][]) {
        assert equiv.length > 0 && equiv[0].length > 0 : "Invalid equivalence matrix";
        boolean result = false;
        for (int j = 1; j < equiv[0].length; j++) {
            if (!equiv[0][j]) {
                // We have two nodes that are no longer equivalent. Split.
                result = true;
                break;
            }
        }
        return result;
    }

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

    private boolean areStillEquivalent(Node n0, Node n1) {
        boolean equiv = true;
        // For all labels.
        labelLoop: for (Label label : Util.labelSet(this.graph)) {
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

    /** EDUARDO */
    public boolean areEquivalent(Edge e0, Edge e1) {
        return e0.label().equals(e1.label())
            && this.areEquivalent(e0.source(), e1.source())
            && this.areEquivalent(e0.opposite(), e1.opposite());
    }

    /** EDUARDO */
    public EquivClass<Edge> getEdgeEquivClass(Edge edge) {
        EquivClass<Edge> ec = new EquivClass<Edge>();
        ec.add(edge);
        for (Edge e : this.graph.edgeSet()) {
            if (areEquivalent(edge, e)) {
                ec.add(e);
            }
        }
        return ec;
    }

    /** EDUARDO */
    public EquivRelation<Edge> getEdgesEquivRel() {
        EquivRelation<Edge> er = new EquivRelation<Edge>();
        for (Edge edge : this.graph.edgeSet()) {
            EquivClass<Edge> ec = this.getEdgeEquivClass(edge);
            er.add(ec);
        }
        return er;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testLevelZeroEquiv() {
        File file = new File("/home/zambon/Temp/abs-list.gps/equiv-test-0.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            System.out.println(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            System.out.println(gne);
            Node n0 = null, n1 = null, n4 = null;
            Iterator<? extends Node> iterator = graph.nodeSet().iterator();
            while (iterator.hasNext()) {
                Node n = iterator.next();
                if (n.getNumber() == 0) {
                    n0 = n;
                } else if (n.getNumber() == 1) {
                    n1 = n;
                } else if (n.getNumber() == 4) {
                    n4 = n;
                }
            }
            System.out.println("Equivalence comparison:");
            System.out.println("n0 equiv n1 = " + gne.areEquivalent(n0, n1));
            System.out.println("n0 equiv n4 = " + gne.areEquivalent(n0, n4));
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testLevelOneEquiv() {
        File file = new File("/home/zambon/Temp/abs-list.gps/equiv-test-1.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            System.out.println(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            System.out.println(gne);
            gne.refineEquivRelation();
            System.out.println(gne);
            gne.refineEquivRelation();
            System.out.println(gne);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testLevelTwoEquiv() {
        File file = new File("/home/zambon/Temp/abs-list.gps/equiv-test-2.gst");
        try {
            Graph graph = Groove.loadGraph(file);
            System.out.println(file);
            GraphNeighEquiv gne = new GraphNeighEquiv(graph);
            System.out.println(gne);
            gne.refineEquivRelation();
            System.out.println(gne);
            gne.refineEquivRelation();
            System.out.println(gne);
            gne.refineEquivRelation();
            System.out.println(gne);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Used for unit testing. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testLevelZeroEquiv();
        testLevelOneEquiv();
        testLevelTwoEquiv();
    }

}
