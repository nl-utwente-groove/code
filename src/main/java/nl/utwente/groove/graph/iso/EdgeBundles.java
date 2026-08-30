/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.graph.iso;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;

/**
 * Grouping of the edges of a graph into <i>bundles</i> of parallel copies,
 * i.e., maximal sets of edges sharing their source, label and target.
 * <p>
 * In a non-simple graph, content-equal edges are distinct objects (their
 * numbers differ, see {@link nl.utwente.groove.graph.ANumberedEdge}), so two
 * graphs that differ only in the identity of such copies have unequal edge
 * sets. They are nevertheless isomorphic, by the identity on the nodes and any
 * per-bundle bijection on the edges. Bundles are therefore the granularity at
 * which the isomorphism machinery works: they are the units compared in
 * {@link #hasSameEdges}, and the units to which {@link CertificateStrategy}
 * assigns edge certificates.
 * <p>
 * Two of the three arrays that make up a bundle - its representative and its
 * size - are filled while the edges are scanned; the parallel copies
 * themselves are collected in a second scan, and only for bundles that have
 * more than one, since a graph typically has few.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5, 2026-08")
class EdgeBundles {
    /** Constructs the bundles of a given graph. */
    private EdgeBundles(Graph graph) {
        int edgeCount = graph.edgeCount();
        this.edgeCount = edgeCount;
        this.reps = new Edge[edgeCount];
        this.hashes = new int[edgeCount];
        this.counts = new int[edgeCount];
        // the table is at least twice the number of bundles, to keep the
        // linear probing short; it stores bundle indices, raised by one so
        // that zero can stand for an empty slot
        int capacity = Integer.highestOneBit(Math.max(edgeCount, 2)) * 4;
        this.table = new int[capacity];
        this.mask = capacity - 1;
        for (Edge edge : graph.edgeSet()) {
            add(edge);
        }
        this.copies = this.bundleCount == edgeCount
            ? null
            : collectCopies(graph);
    }

    /** Adds an edge to the index, as a fresh bundle or a further copy. */
    private void add(Edge edge) {
        int hash = hash(edge);
        int slot = hash & this.mask;
        while (true) {
            int entry = this.table[slot];
            if (entry == 0) {
                int index = this.bundleCount;
                this.reps[index] = edge;
                this.hashes[index] = hash;
                this.counts[index] = 1;
                this.table[slot] = index + 1;
                this.bundleCount = index + 1;
                return;
            }
            int index = entry - 1;
            Edge rep = this.reps[index];
            assert rep != null; // the slot is occupied
            if (this.hashes[index] == hash && hasEqualContent(rep, edge)) {
                this.counts[index]++;
                return;
            }
            slot = (slot + 1) & this.mask;
        }
    }

    /**
     * Collects the parallel copies of every bundle that has more than one, in
     * a second scan over the edges. Called from the constructor, after all
     * bundle sizes are known.
     */
    private @Nullable Edge[] @Nullable [] collectCopies(Graph graph) {
        @Nullable
        Edge[] @Nullable [] result = new Edge[this.bundleCount][];
        int[] filled = new int[this.bundleCount];
        for (Edge edge : graph.edgeSet()) {
            int index = indexOf(edge);
            int count = this.counts[index];
            if (count > 1) {
                @Nullable
                Edge[] bundle = result[index];
                if (bundle == null) {
                    result[index] = bundle = new Edge[count];
                }
                bundle[filled[index]] = edge;
                filled[index]++;
            }
        }
        return result;
    }

    /**
     * Returns the index of the bundle of parallel copies of a given edge, or
     * {@code -1} if there is no such bundle.
     */
    private int indexOf(Edge edge) {
        int hash = hash(edge);
        int slot = hash & this.mask;
        while (true) {
            int entry = this.table[slot];
            if (entry == 0) {
                return -1;
            }
            int index = entry - 1;
            Edge rep = this.reps[index];
            assert rep != null; // the slot is occupied
            if (this.hashes[index] == hash && hasEqualContent(rep, edge)) {
                return index;
            }
            slot = (slot + 1) & this.mask;
        }
    }

    /** Returns the number of bundles. */
    int size() {
        return this.bundleCount;
    }

    /** Returns the first-encountered copy of the bundle at a given index. */
    Edge getRepresentative(int index) {
        Edge result = this.reps[index];
        assert result != null; // the index is that of an existing bundle
        return result;
    }

    /** Returns the number of parallel copies of the bundle at a given index. */
    int getCount(int index) {
        return this.counts[index];
    }

    /**
     * Returns the parallel copies of the bundle containing a given edge, in
     * the order in which the graph presented them. Only defined for bundles of
     * more than one copy; for a singleton bundle the edge itself is the only
     * copy, and no array is stored. Every slot of the result is filled,
     * despite the element type.
     */
    @Nullable
    Edge[] getCopies(Edge edge) {
        @Nullable
        Edge[] @Nullable [] copies = this.copies;
        assert copies != null; // some bundle has more than one copy
        @Nullable
        Edge[] result = copies[indexOf(edge)];
        assert result != null : String.format("Edge %s has no parallel copies", edge);
        return result;
    }

    /**
     * Tests if a given graph has the same edges as the graph of these bundles,
     * up to the identity of parallel copies. Only the edges of the parameter
     * are inspected; the graph of the bundles is not touched.
     */
    boolean hasSameEdges(Graph graph) {
        if (graph.edgeCount() != this.edgeCount) {
            return false;
        }
        int[] seen = new int[this.bundleCount];
        for (Edge edge : graph.edgeSet()) {
            int index = indexOf(edge);
            if (index < 0 || ++seen[index] > this.counts[index]) {
                return false;
            }
        }
        // the edge counts coincide and no bundle was overfilled,
        // so every bundle was filled exactly
        return true;
    }

    /** Content-based hash of an edge, consistent with {@link #hasEqualContent}. */
    private int hash(Edge edge) {
        int result = edge.source().hashCode();
        result = result * 31 + edge.label().hashCode();
        result = result * 31 + edge.target().hashCode();
        return result;
    }

    /** Tests if two edges are parallel copies, i.e., have the same content. */
    private boolean hasEqualContent(Edge one, Edge two) {
        return one.source().equals(two.source()) && one.target().equals(two.target())
            && one.label().equals(two.label());
    }

    /** The number of edges of the underlying graph. */
    private final int edgeCount;
    /** The number of bundles, i.e., the number of used entries of {@link #reps}. */
    private int bundleCount;
    /** For every bundle, its first-encountered copy. */
    private final @Nullable Edge[] reps;
    /** For every bundle, the content hash of its copies. */
    private final int[] hashes;
    /** For every bundle, the number of its parallel copies. */
    private final int[] counts;
    /**
     * For every bundle of more than one copy, its copies; {@code null} if the
     * graph has no such bundle, and {@code null} at the index of every bundle
     * that has a single copy.
     */
    private final @Nullable Edge @Nullable [] @Nullable [] copies;
    /** Open-addressed table from content hash to bundle index, raised by one. */
    private final int[] table;
    /** Bit mask for the length of {@link #table}. */
    private final int mask;

    /** Returns the edge bundles of a given graph. */
    static EdgeBundles newInstance(Graph graph) {
        return new EdgeBundles(graph);
    }
}
