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
package nl.utwente.groove.gui.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeKey;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link JCell}s bearing
 * the entries.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TypeFilter extends LabelFilter<@NonNull AspectGraph> {
    /**
     * Clears the entire filter, and resets it to label- or type-based.
     */
    @Override
    public void clear() {
        super.clear();
        this.stale = true;
    }

    /** Lazily creates and returns a filter entry based on a given element. */
    @Override
    public TypeEntry getEntry(Label label) {
        if (label instanceof TypeElement type) {
            return getEntryMap(type.getGraph()).keyMap().get(type.key());
        }
        throw Exceptions.UNREACHABLE;
    }

    @Override
    protected Set<JCell<@NonNull AspectGraph>> setSelection(Entry entry, boolean selected) {
        Set<JCell<@NonNull AspectGraph>> result = new HashSet<>();
        var superResult = super.setSelection(entry, selected);
        TypeEntry te = (TypeEntry) entry;
        if (selected) {
            if (te.isForNode()) {
                // previously passively selected incident edges may become fully visible
                for (var ee : te.getEdges()) {
                    if (ee.getNodes().stream().allMatch(TypeEntry::isSelected)
                        && ee.setPassive(false)) {
                        result.addAll(getJCells(ee));
                    }
                }
            } else {
                // previously filtered source and target nodes become passively filtered
                te
                    .getNodes()
                    .stream()
                    .filter(ne -> ne.setPassive(true))
                    .map(this::getJCells)
                    .forEach(result::addAll);
            }
        } else {
            if (te.isForNode()) {
                // previously selected incident edges become passively selected
                te
                    .getEdges()
                    .stream()
                    .filter(ce -> ce.setPassive(true))
                    .map(this::getJCells)
                    .forEach(result::addAll);
            } else {
                // previously passively filtered incident nodes may become fully filtered
                for (var ne : te.getNodes()) {
                    if (ne.getEdges().stream().noneMatch(TypeEntry::isSelected)
                        && ne.setPassive(false)) {
                        result.addAll(getJCells(ne));
                    }
                }
            }
        }
        if (result.isEmpty()) {
            result = superResult;
        } else {
            result.addAll(superResult);
        }
        return result;
    }

    /** Updates the type graph on which this filter is based. */
    void update(TypeGraph typeGraph) {
        if (this.stale) {
            var entryMap = this.entryMap;
            if (entryMap == null) {
                this.entryMap = entryMap = new EntryMap(typeGraph);
            } else if (typeGraph != entryMap.typeGraph()) {
                // retrieve the selection status from the previous entry map
                this.entryMap = entryMap = new EntryMap(typeGraph, entryMap.getSelected(false));
            }
            entryMap.entryStream().forEach(this::registerEntry);
            this.stale = false;
        }
        assert typeGraph == this.entryMap
            .typeGraph() : "Type graph has silently changed from %s to %s"
                .formatted(this.entryMap.typeGraph(), typeGraph);
    }

    private EntryMap getEntryMap(TypeGraph typeGraph) {
        update(typeGraph);
        return this.entryMap;
    }

    /** Flag indicating that the {@link #entryMap} might have to be refreshed. */
    private boolean stale = true;
    /** Mapping from known node type labels to corresponding node type entries. */
    private EntryMap entryMap;

    /** Type graph-dependent map from type labels to filter entries.
     * @param typeGraph type graph on which this map is based
     * @param keyMap mapping from node type keys to entries
     */
    static private record EntryMap(TypeGraph typeGraph, Map<TypeKey,TypeEntry> keyMap) {

        /** Constructs a fresh map from a given type graph. */
        EntryMap(TypeGraph typeGraph) {
            this(typeGraph, new HashMap<>());
            for (var n : typeGraph.nodeSet()) {
                keyMap().put(n.key(), new TypeEntry(n));
            }
            for (var e : typeGraph.edgeSet()) {
                var edgeEntry = new TypeEntry(e);
                keyMap().put(e.key(), edgeEntry);
                // add the subtypes of the source and target to the end nodes
                e
                    .source()
                    .getSubtypes()
                    .stream()
                    .map(TypeNode::key)
                    .map(keyMap()::get)
                    .forEach(edgeEntry::addNode);
                e
                    .target()
                    .getSubtypes()
                    .stream()
                    .map(TypeNode::key)
                    .map(keyMap()::get)
                    .forEach(edgeEntry::addNode);
                // add this edge to the incident edges of the subtypes of source and target
                e
                    .source()
                    .getSubtypes()
                    .stream()
                    .map(TypeNode::key)
                    .map(keyMap()::get)
                    .forEach(ne -> ne.addEdge(edgeEntry));
                e
                    .target()
                    .getSubtypes()
                    .stream()
                    .map(TypeNode::key)
                    .map(keyMap()::get)
                    .forEach(ne -> ne.addEdge(edgeEntry));
            }
        }

        /** Constructs a fresh map from a given type graph,
         * taking a set of previously selected elements into account. */
        EntryMap(TypeGraph typeGraph, Collection<TypeKey> unselected) {
            this(typeGraph);
            for (var key : unselected) {
                var entry = keyMap().get(key);
                if (entry != null) {
                    entry.setSelected(false);
                }
            }
        }

        /** Returns a stream of all the type entries in this map object. */
        Stream<TypeEntry> entryStream() {
            return keyMap().values().stream();
        }

        /** Returns the set of currently (un)selected type entries. */
        Collection<TypeKey> getSelected(boolean selected) {
            return keyMap()
                .values()
                .stream()
                .filter(e -> e.isSelected() == selected)
                .map(TypeEntry::getType)
                .map(TypeElement::key)
                .toList();
        }
    }

    /** Filter entry wrapping a label. */
    public static class TypeEntry implements Entry {
        /** Constructs a fresh label entry from a given label. */
        public TypeEntry(TypeElement type) {
            this.type = type;
            this.selected = true;
        }

        /** Returns the type element wrapped in this entry. */
        public TypeElement getType() {
            return this.type;
        }

        @Override
        public Line getLine() {
            return this.type.toLine();
        }

        private final TypeElement type;

        @Override
        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public boolean setSelected(boolean selected) {
            boolean result = this.selected != selected || this.passive;
            this.selected = selected;
            this.passive = false;
            return result;
        }

        /** Flag indicating if this entry is currently selected. */
        private boolean selected;

        /** Sets the passive status to a given value.
         * This is only carried out if the entry is a filtered node type or a selected edge type.
         * The return value indicates if the passive status was changed.
         */
        boolean setPassive(boolean passive) {
            boolean result = false;
            if (isSelected() != isForNode()) {
                result = this.passive != passive;
                this.passive = passive;
            }
            return result;
        }

        @Override
        public boolean isPassive() {
            return this.passive;
        }

        private boolean passive;

        @Override
        public boolean isForNode() {
            return getType() instanceof TypeNode;
        }

        @Override
        public boolean matches(Label label) {
            return getType().label().equals(label);
        }

        /** Returns the (subtype-closed) set of source and target (node) type entry. */
        Set<TypeEntry> getNodes() {
            return this.nodes;
        }

        /** Adds a source or target (node) type entry. */
        void addNode(TypeEntry source) {
            this.nodes.add(source);
        }

        private final Set<TypeEntry> nodes = new HashSet<>();

        /** Returns the set of incident (edge) type entries. */
        Set<TypeEntry> getEdges() {
            return this.edges;
        }

        /** Adds an incident (edge) type entry. */
        void addEdge(TypeEntry child) {
            this.edges.add(child);
        }

        private final Set<TypeEntry> edges = new HashSet<>();

        @Override
        public int compareTo(Entry o) {
            TypeEntry other = (TypeEntry) o;
            TypeElement type = getType();
            TypeElement otherType = other.getType();
            if (type instanceof TypeNode) {
                return type.compareTo(otherType);
            }
            if (otherType instanceof TypeNode) {
                return otherType.compareTo(type);
            }
            TypeEdge edge = (TypeEdge) type;
            TypeEdge otherEdge = (TypeEdge) otherType;
            int result = edge.source().label().compareTo(otherEdge.source().label());
            if (result == 0) {
                result = edge.label().compareTo(otherEdge.label());
            }
            return result;
        }

        @Override
        public int hashCode() {
            if (this.type instanceof TypeNode) {
                return this.type.hashCode();
            } else {
                TypeEdge edge = (TypeEdge) this.type;
                return Objects.hash(edge.source().label(), edge.label(), edge.target().label());
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TypeEntry other)) {
                return false;
            }
            if (!this.type.label().equals(other.type.label())) {
                return false;
            }
            if (!(this.type instanceof TypeEdge edge)) {
                return other.type instanceof TypeNode;
            }
            if (!(other.type instanceof TypeEdge otherEdge)) {
                return false;
            }
            if (!edge.source().label().equals(otherEdge.source().label())) {
                return false;
            }
            if (!edge.target().label().equals(otherEdge.target().label())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return this.type.toString();
        }
    }
}
