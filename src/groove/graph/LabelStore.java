/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Arend
 * @version $Revision $
 */
public class LabelStore implements Cloneable {
    /**
     * Constructs a new (initially empty) label inheritance relation.
     */
    public LabelStore() {
        // empty
    }

    /** Adds a label to the set of known labels. */
    public void addLabel(Label label) {
        if (!this.subtypeMap.containsKey(label)) {
            Set<Label> subtypes = new TreeSet<Label>();
            subtypes.add(label);
            this.subtypeMap.put(label, subtypes);
            this.directSubtypeMap.put(label, new TreeSet<Label>());
        }
    }

    /** Adds a set of labels to the set of known labels. */
    public void addLabels(Set<Label> labels) {
        for (Label label : labels) {
            addLabel(label);
        }
    }

    /** Adds a direct subtype pair to the subtyping relation. */
    public void addSubtype(Label type, Label subtype) {
        addLabel(type);
        addLabel(subtype);
        if (this.directSubtypeMap.get(type).add(subtype)) {
            Set<Label> currentSubtypes = this.subtypeMap.get(type);
            if (currentSubtypes.add(subtype)) {
                // transitively close the relation
                currentSubtypes.addAll(getSubtypes(subtype));
            }
        }
    }

    /** Removes a direct subtype pair from the subtyping relation. */
    public void removeSubtype(Label type, Label subtype) {
        if (this.directSubtypeMap.get(type).remove(subtype)) {
            // recalculate the transitive closure of the subtypes
            Set<Label> subtypes = new TreeSet<Label>();
            subtypes.add(type);
            for (Label directSubtype : this.directSubtypeMap.get(type)) {
                subtypes.add(directSubtype);
                subtypes.addAll(this.subtypeMap.get(directSubtype));
            }
            this.subtypeMap.put(type, subtypes);
        }
    }

    /**
     * Returns an unmodifiable view on the set of direct subtypes of a given
     * label. Returns <code>null</code> if the label is unknown.
     * @param label the label to determine the direct subtypes for
     * @return the direct subtypes of <code>label</code>, or <code>null</code>
     *         if <code>label</code> is not a known label.
     */
    public Set<Label> getDirectSubtypes(Label label) {
        return Collections.unmodifiableSet(this.directSubtypeMap.get(label));
    }

    /**
     * Returns an unmodifiable view on the set of subtypes of a given * label.
     * The set of subtypes is reflexively defined, i.e., it includes the type
     * itself. Returns <code>null</code> if the label is unknown.
     * @param label the label to determine the subtypes for
     * @return the subtypes of <code>label</code>, or <code>null</code> if
     *         <code>label</code> is not a known type label.
     */
    public Set<Label> getSubtypes(Label label) {
        return Collections.unmodifiableSet(this.subtypeMap.get(label));
    }

    /**
     * Returns the set of supertypes of a given label. The set of supertypes is
     * reflexively defined, i.e., it includes the label itself. Returns
     * <code>null</code> if the label is unknown. Note that this method is
     * inefficient, as it calculates the set of supertypes on the fly.
     * @param label the label to determine the supertypes for
     * @return the supertypes of <code>label</code>, or <code>null</code> if
     *         <code>label</code> is not a known label.
     */
    public Set<Label> getSupertypes(Label label) {
        return getInverse(this.subtypeMap, label);
    }

    /**
     * Returns the set of direct supertypes of a given label. Returns
     * <code>null</code> if the type label is unknown. Note that this method is
     * inefficient, as it calculates the set of supertypes on the fly.
     * @param label the label to determine the direct supertypes for
     * @return the supertypes of <code>label</code>, or <code>null</code> if
     *         <code>label</code> is not a known label.
     */
    public Set<Label> getDirectSupertypes(Label label) {
        return getInverse(this.directSubtypeMap, label);
    }

    /**
     * Returns the set of inverse images according to a relation, given as a
     * mapping from elements to sets of related elements.
     */
    private Set<Label> getInverse(Map<Label,Set<Label>> relation, Label element) {
        Set<Label> result = null;
        if (relation.containsKey(element)) {
            result = new HashSet<Label>();
            for (Map.Entry<Label,Set<Label>> entry : relation.entrySet()) {
                if (relation.get(entry.getValue()).contains(element)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    /** Returns an unmodifiable view on the set of all known type labels. */
    public Set<Label> getLabels() {
        return Collections.unmodifiableSet(this.subtypeMap.keySet());
    }

    /**
     * Two label stores are equal if they have the same direct subtyping
     * relation.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof LabelStore
            && this.directSubtypeMap.equals(((LabelStore) obj).directSubtypeMap);
    }

    /** Returns the hash code of the direct subtyping relation. */
    @Override
    public int hashCode() {
        return this.directSubtypeMap.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("Labels: %s", getLabels()));
        result.append(String.format("Direct subtypes: %s",
            this.directSubtypeMap));
        return result.toString();
    }

    @Override
    public LabelStore clone() {
        LabelStore result = new LabelStore();
        for (Map.Entry<Label,Set<Label>> directSubtypeEntry : this.directSubtypeMap.entrySet()) {
            for (Label subtype : directSubtypeEntry.getValue()) {
                result.addSubtype(directSubtypeEntry.getKey(), subtype);
            }
        }
        return result;
    }

    /** Mapping from a type label to its set of subtypes (including itself). */
    private final Map<Label,Set<Label>> subtypeMap =
        new HashMap<Label,Set<Label>>();
    /** Mapping from a type label to its set of direct subtypes. */
    private final Map<Label,Set<Label>> directSubtypeMap =
        new HashMap<Label,Set<Label>>();
}
