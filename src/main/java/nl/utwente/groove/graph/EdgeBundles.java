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
package nl.utwente.groove.graph;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Multiset view of the edges of a graph: the edges are grouped into
 * <i>bundles</i> of parallel copies, i.e., edges sharing their source, label
 * and target, and only the size of each bundle is recorded.
 * <p>
 * In a non-simple graph, content-equal edges are distinct objects (their
 * numbers differ, see {@link ANumberedEdge}), so two graphs that differ only
 * in the identity of such copies have unequal edge sets. They are nevertheless
 * isomorphic, by the identity on the nodes and any per-bundle bijection on the
 * edges; comparing bundles rather than edges recognises this (see
 * {@link nl.utwente.groove.graph.iso.IsoChecker}).
 * <p>
 * An index is built for one graph and compared against the <i>edges</i> of
 * another ({@link #hasSameEdges}), rather than against a second index: in the
 * isomorphism checker one side is a state that is compared repeatedly, and so
 * profits from being indexed, whereas the other is a freshly derived graph
 * that would pay for an index it uses once. The comparison allocates nothing
 * beyond a counter per bundle.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5, 2026-08")
public class EdgeBundles {
    /** Constructs the bundle index of a given graph. */
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
     * Returns the index of the bundle of parallel copies of a given edge, or
     * {@code -1} if this index has no such bundle.
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

    /**
     * Tests if a given graph has the same edges as the graph of this index, up
     * to the identity of parallel copies. Only the edges of the parameter are
     * inspected; the graph of the index is not touched.
     */
    public boolean hasSameEdges(Graph graph) {
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

    /** The number of edges of the indexed graph. */
    private final int edgeCount;
    /** The number of bundles, i.e., the number of used entries of {@link #reps}. */
    private int bundleCount;
    /** For every bundle, an arbitrary one of its parallel copies. */
    private final @Nullable Edge[] reps;
    /** For every bundle, the content hash of its copies. */
    private final int[] hashes;
    /** For every bundle, the number of its parallel copies. */
    private final int[] counts;
    /** Open-addressed table from content hash to bundle index, raised by one. */
    private final int[] table;
    /** Bit mask for the length of {@link #table}. */
    private final int mask;

    /** Returns the bundle index of a given graph. */
    static public EdgeBundles newInstance(Graph graph) {
        return new EdgeBundles(graph);
    }
}
