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
package groove.gui.tree;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.Label;
import groove.gui.jgraph.JCell;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link JCell}s bearing 
 * the entries.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeFilter extends LabelFilter<AspectGraph> {
    /** 
     * Clears the entire filter, and resets it to label- or type-based.
     */
    @Override
    public void clear() {
        super.clear();
        this.nodeTypeEntryMap.clear();
        this.edgeTypeEntryMap.clear();
        this.typeGraph = null;
    }

    /** Lazily creates and returns a filter entry based on a given element. */
    @Override
    public TypeEntry getEntry(Label element) {
        TypeEntry result = null;
        if (element instanceof TypeNode) {
            TypeElement key = (TypeElement) element;
            TypeLabel keyLabel = key.label();
            result = this.nodeTypeEntryMap.get(keyLabel);
            if (result == null) {
                this.nodeTypeEntryMap.put(keyLabel, result = createEntry(key));
            }
        } else if (element instanceof TypeEdge) {
            TypeEdge key = (TypeEdge) element;
            TypeLabel nodeKeyLabel = key.source().label();
            Map<TypeLabel,TypeEntry> entryMap =
                this.edgeTypeEntryMap.get(nodeKeyLabel);
            if (entryMap == null) {
                this.edgeTypeEntryMap.put(nodeKeyLabel, entryMap =
                    new HashMap<>());
            }
            TypeLabel edgeKeyLabel = key.label();
            result = entryMap.get(edgeKeyLabel);
            if (result == null) {
                entryMap.put(edgeKeyLabel, result = createEntry(key));
            }
        }
        return result;
    }

    /** Constructs a filter entry from a given object. */
    private TypeEntry createEntry(TypeElement type) {
        TypeEntry result = new TypeEntry(type);
        assert isTypeGraphConsistent(result);
        return result;
    }

    /** Helper method to check that all type entries are based on the same type graph. */
    private boolean isTypeGraphConsistent(TypeEntry entry) {
        TypeGraph typeGraph = entry.getType().getGraph();
        if (this.typeGraph == null) {
            this.typeGraph = typeGraph;
            return true;
        } else {
            return this.typeGraph == typeGraph;
        }
    }

    /** Mapping from known node type labels to corresponding node type entries. */
    private final Map<TypeLabel,TypeEntry> nodeTypeEntryMap =
        new HashMap<>();
    /** Mapping from known node type labels and edge type labels to corresponding edge type entries. */
    private final Map<TypeLabel,Map<TypeLabel,TypeEntry>> edgeTypeEntryMap =
        new HashMap<>();
    /** Field used to test consistency of the type entries. */
    private TypeGraph typeGraph;

    /** Filter entry wrapping a label. */
    public static class TypeEntry implements Entry {
        /** Constructs a fresh label entry from a given label. */
        public TypeEntry(TypeElement type) {
            this.type = type;
        }

        /** Returns the type element wrapped in this entry. */
        public TypeElement getType() {
            return this.type;
        }

        @Override
        public Label getLabel() {
            return this.type.label();
        }

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
            int result =
                edge.source().label().compareTo(otherEdge.source().label());
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
                return edge.source().label().hashCode()
                    ^ edge.label().hashCode();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TypeEntry)) {
                return false;
            }
            // test for label equality to avoid 
            // comparing type elements from different type graphs
            TypeEntry other = (TypeEntry) obj;
            if (!this.type.label().equals(other.type.label())) {
                return false;
            }
            if (this.type instanceof TypeNode) {
                return other.type instanceof TypeNode;
            }
            if (other.type instanceof TypeNode) {
                return false;
            }
            TypeEdge edge = (TypeEdge) this.type;
            TypeEdge otherEdge = (TypeEdge) other.type;
            if (!edge.source().label().equals(otherEdge.source().label())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return this.type.toString();
        }

        private final TypeElement type;
    }
}
