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

import groove.util.DefaultFixable;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Set of labels and subtypes. This is used to encode the available labels and
 * node types in a grammar.
 * @author Arend
 * @version $Revision $
 */
public class LabelStore extends DefaultFixable implements Cloneable {
    /**
     * Constructs a new (initially empty) label inheritance relation.
     */
    public LabelStore() {
        // empty
    }

    /** Adds all labels and subtypes from another label store to this one. */
    public void add(LabelStore other) {
        testFixed(false);
        for (Map.Entry<TypeLabel,Set<TypeLabel>> dirSubEntry : other.getDirSubMap().entrySet()) {
            addLabel(dirSubEntry.getKey());
            for (TypeLabel subtype : dirSubEntry.getValue()) {
                addSubtype(dirSubEntry.getKey(), subtype);
            }
        }
    }

    /** Adds a label to the set of known labels. */
    public void addLabel(TypeLabel label) {
        testFixed(false);
        if (!getDirSubMap().containsKey(label)) {
            Set<TypeLabel> subtypes = new TreeSet<TypeLabel>();
            subtypes.add(label);
            getSubMap().put(label, subtypes);
            Set<TypeLabel> directSubtypes = new TreeSet<TypeLabel>();
            getDirSubMap().put(label, directSubtypes);
            invalidateSupertypes();
        }
    }

    /** Adds a set of labels to the set of known labels. */
    public void addLabels(Set<TypeLabel> labels) {
        testFixed(false);
        for (TypeLabel label : labels) {
            addLabel(label);
        }
    }

    /**
     * Adds a direct subtype pair to the subtyping relation. Both sub- and
     * supertype labels have to be node type labels, and the new subtype pair
     * should not introduce a cycle.
     * @throws IllegalArgumentException if one of the types is a data type or
     *         not a node type, or the new subtype pair introduces a cycle
     */
    public void addSubtype(TypeLabel type, TypeLabel subtype)
        throws IllegalArgumentException {
        testFixed(false);
        if (!type.isNodeType()) {
            throw new IllegalArgumentException(String.format(
                "Non-node type label '%s' cannot get subtype '%s'", type,
                subtype));
        }
        if (!subtype.isNodeType()) {
            throw new IllegalArgumentException(String.format(
                "Non-node type label '%s' cannot be subtype of '%s'", subtype,
                type));
        }
        if (type.isDataType()) {
            throw new IllegalArgumentException(String.format(
                "Data type label '%s' cannot be supertype", type));
        }
        if (subtype.isDataType()) {
            throw new IllegalArgumentException(String.format(
                "Data type label '%s' cannot be subtype", subtype));
        }
        addLabel(type);
        addLabel(subtype);
        if (getSubs(subtype).contains(type)) {
            throw new IllegalArgumentException(String.format(
                "The relation '%s %c %s' introduces a cyclic type dependency",
                type, SUPERTYPE_SYMBOL, subtype));
        }
        if (getDirSubs(type).add(subtype)) {
            // transitively close the relation
            Set<TypeLabel> subsubtypes = getSubs(subtype);
            for (Map.Entry<TypeLabel,Set<TypeLabel>> typeEntry : getSubMap().entrySet()) {
                if (typeEntry.getValue().contains(type)) {
                    typeEntry.getValue().addAll(subsubtypes);
                }
            }
        }
        invalidateSupertypes();
    }

    /** Removes a direct subtype pair from the subtyping relation. */
    public void removeSubtype(TypeLabel type, TypeLabel subtype) {
        testFixed(false);
        if (getDirSubs(type).remove(subtype)) {
            // recalculate the transitive closure of the subtypes
            invalidateSubtypes();
        }
    }

    /**
     * Returns an unmodifiable view on the set of direct subtypes of a given
     * node type label. Returns <code>null</code> if the label is unknown.
     * @param label the label to determine the direct subtypes for
     * @return the direct subtypes of <code>label</code>, or <code>null</code>
     *         if <code>label</code> is not a known label.
     */
    public Set<TypeLabel> getDirectSubtypes(TypeLabel label) {
        Set<TypeLabel> result = getDirSubs(label);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Internal, modifiable version of {@link #getDirectSubtypes(TypeLabel)}.
     */
    private Set<TypeLabel> getDirSubs(TypeLabel label) {
        return getDirSubMap().get(label);
    }

    /**
     * Returns an unmodifiable view on the map from labels to direct subtypes.
     * @return A map from known labels to their direct subtypes
     */
    public Map<TypeLabel,Set<TypeLabel>> getDirectSubtypeMap() {
        return Collections.unmodifiableMap(getDirSubMap());
    }

    /**
     * Internal, modifiable version of {@link #getDirectSubtypeMap()}.
     */
    private Map<TypeLabel,Set<TypeLabel>> getDirSubMap() {
        return this.dirSubMap;
    }

    /**
     * Returns an unmodifiable view on the set of subtypes of a given label.
     * The set of subtypes is reflexively defined, i.e., it includes the type
     * itself. Returns <code>null</code> if the label is unknown.
     * @param label the label to determine the subtypes for
     * @return the subtypes of <code>label</code>, or <code>null</code> if
     *         <code>label</code> is not a known label.
     */
    public Set<TypeLabel> getSubtypes(TypeLabel label) {
        Set<TypeLabel> result = getSubs(label);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Internal, modifiable version of {@link #getSubtypes(TypeLabel)}.
     */
    private Set<TypeLabel> getSubs(TypeLabel label) {
        return getSubMap().get(label);
    }

    /**
     * Returns an unmodifiable transitively and reflexively closed
     * mapping from types to subtypes.
     */
    public Map<TypeLabel,Set<TypeLabel>> getSubtypeMap() {
        return Collections.unmodifiableMap(getSubMap());
    }

    /**
     * Internal, modifiable version of {@link #getSubtypeMap()}.
     */
    private Map<TypeLabel,Set<TypeLabel>> getSubMap() {
        if (this.subMap == null) {
            this.subMap = computeSubtypeMap();
        }
        return this.subMap;
    }

    /**
     * Recalculates the subtype relation by transitively closing the direct
     * subtype relation.
     */
    private Map<TypeLabel,Set<TypeLabel>> computeSubtypeMap() {
        // first order all types consistently with the subtype relation.
        Set<TypeLabel> allTypes = new LinkedHashSet<TypeLabel>();
        Set<TypeLabel> remaining =
            new HashSet<TypeLabel>(getDirSubMap().keySet());
        while (!remaining.isEmpty()) {
            Iterator<TypeLabel> remainingIter = remaining.iterator();
            boolean bottomTypeFound = false;
            while (remainingIter.hasNext()) {
                TypeLabel bottomType = remainingIter.next();
                if (allTypes.containsAll(getDirSubs(bottomType))) {
                    remainingIter.remove();
                    allTypes.add(bottomType);
                    bottomTypeFound = true;
                }
            }
            assert bottomTypeFound : String.format(
                "No bottom type found in %s", remaining);
        }
        // now build up the transitive closure
        Map<TypeLabel,Set<TypeLabel>> result =
            new TreeMap<TypeLabel,Set<TypeLabel>>();
        for (TypeLabel type : allTypes) {
            Set<TypeLabel> subtypes = new TreeSet<TypeLabel>();
            result.put(type, subtypes);
            subtypes.add(type);
            for (Label directSubtype : getDirSubs(type)) {
                subtypes.addAll(result.get(directSubtype));
            }
        }
        return result;
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
    public Set<TypeLabel> getSupertypes(TypeLabel label) {
        Set<TypeLabel> result = getSupertypeMap().get(label);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Returns a reflexively and transitively closed mapping from the type
     * labels in this label store to their supertypes. The map is the inverse 
     * of the subtype map. 
     */
    public Map<TypeLabel,Set<TypeLabel>> getSupertypeMap() {
        if (this.superMap == null) {
            this.superMap = getInverse(getSubMap());
        }
        return Collections.unmodifiableMap(this.superMap);
    }

    /**
     * Returns the set of direct supertypes of a given label. Returns
     * <code>null</code> if the type label is unknown. Note that this method is
     * inefficient, as it calculates the set of supertypes on the fly.
     * @param label the label to determine the direct supertypes for
     * @return the supertypes of <code>label</code>, or <code>null</code> if
     *         <code>label</code> is not a known label.
     */
    public Set<TypeLabel> getDirectSupertypes(TypeLabel label) {
        Set<TypeLabel> result = getDirectSupertypeMap().get(label);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Returns the direct supertype map of this label store. Note that this
     * method is inefficient, as it calculates the set of supertypes on the fly.
     * @return A map from known labels to their direct supertypes
     */
    public Map<TypeLabel,Set<TypeLabel>> getDirectSupertypeMap() {
        if (this.dirSuperMap == null) {
            this.dirSuperMap = getInverse(getDirSubMap());
        }
        return Collections.unmodifiableMap(this.dirSuperMap);
    }

    /**
     * Returns the inverse of a relation, given as a mapping from elements to
     * sets of related elements.
     */
    private Map<TypeLabel,Set<TypeLabel>> getInverse(
            Map<TypeLabel,Set<TypeLabel>> relation) {
        Map<TypeLabel,Set<TypeLabel>> result =
            new HashMap<TypeLabel,Set<TypeLabel>>();
        for (TypeLabel type : getLabels()) {
            result.put(type, new TreeSet<TypeLabel>());
        }
        for (Map.Entry<TypeLabel,Set<TypeLabel>> entry : relation.entrySet()) {
            for (Label subtype : entry.getValue()) {
                result.get(subtype).add(entry.getKey());
            }
        }
        return result;
    }

    /** Returns an unmodifiable view on the set of all known labels. */
    public Set<TypeLabel> getLabels() {
        return getDirectSubtypeMap().keySet();
    }

    /** 
     * Returns the set of labels of a given kind ({@link Label#BINARY}, 
     * {@link Label#FLAG} or {@link Label#NODE_TYPE}).
     */
    public Set<TypeLabel> getLabels(int kind) {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        for (TypeLabel label : getLabels()) {
            if (label.getKind() == kind) {
                result.add(label);
            }
        }
        return result;
    }

    /**
     * Returns a clone of this label store where all occurrences of a given
     * label are replaced by a new label.
     * @param oldLabel the label to be replaced
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this label store, or the store itself if {@code
     *         oldLabel} did not occur
     * @throws FormatException if the renaming causes a subtype cycle
     */
    public LabelStore relabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws FormatException {
        LabelStore result = this;
        if (!oldLabel.equals(newLabel) && getDirSubs(oldLabel) != null) {
            // check for subtype cycles
            if (getLabels().contains(newLabel)
                && (getSubs(oldLabel).contains(newLabel) || getSubs(newLabel).contains(
                    oldLabel))) {
                throw new FormatException(
                    "Renaming '%s' to '%s' causes a subtype cycle", oldLabel,
                    newLabel);
            }
            result = clone();
            result.addLabel(newLabel);
            if (newLabel.isNodeType()) {
                result.getDirSubs(newLabel).addAll(getDirSubs(oldLabel));
            }
            result.getDirSubMap().remove(oldLabel);
            for (Map.Entry<TypeLabel,Set<TypeLabel>> subEntry : result.getDirSubMap().entrySet()) {
                Set<TypeLabel> subtypes = subEntry.getValue();
                if (subtypes.remove(oldLabel) && newLabel.isNodeType()) {
                    subtypes.add(newLabel);
                }
            }
        }
        return result;
    }

    /**
     * Two label stores are equal if they have the same direct subtyping
     * relation.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LabelStore)) {
            return false;
        }
        return getDirSubMap().equals(((LabelStore) obj).getDirSubMap());
    }

    /** Returns the hash code of the direct subtyping relation. */
    @Override
    public int hashCode() {
        return getDirSubMap().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        SortedSet<TypeLabel> sortedLabels = new TreeSet<TypeLabel>(getLabels());
        for (TypeLabel label : sortedLabels) {
            result.append(String.format("%s > %s%n", label, getDirSubs(label)));
        }
        return result.toString();
    }

    /**
     * Returns a string encoding the direct subtyping relation in this label
     * store. The string is formatted according to
     * <ul>
     * <li> <code>RESULT</code> = (<code>DECL</code> ({@link #MAIN_SEPARATOR}
     * <code>DECL</code>)*)?
     * <li> <code>DECL</code> = <code>ID</code> {@link #SUPERTYPE_SYMBOL}
     * <code>ID</code> ({@link #SUBTYPE_SEPARATOR} <code>ID</code>)*
     * <li> <code>ID</code> = Node type identifier
     * </ul>
     * The string can be parsed by
     */
    public String toDirectSubtypeString() {
        StringBuilder result = new StringBuilder();
        boolean firstLabel = true;
        for (Map.Entry<TypeLabel,Set<TypeLabel>> dirSubEntry : getDirSubMap().entrySet()) {
            // only treat proper entries
            if (dirSubEntry.getValue().isEmpty()) {
                continue;
            }
            if (!firstLabel) {
                result.append(MAIN_SEPARATOR + " ");
            } else {
                firstLabel = false;
            }
            result.append(dirSubEntry.getKey().text());
            result.append(" " + SUPERTYPE_SYMBOL + " ");
            boolean firstSubtype = true;
            for (TypeLabel subType : dirSubEntry.getValue()) {
                if (firstSubtype) {
                    firstSubtype = false;
                } else {
                    result.append(SUBTYPE_SEPARATOR + " ");
                }
                result.append(subType.text());
            }
        }
        return result.toString();
    }

    /**
     * Adds direct subtypes to this label store, as encoded in a string. The
     * string is parsed according to the format described in
     * {@link #toDirectSubtypeString()}.
     * @param directSubtypeString the string containing the information about
     *        direct subtypes
     * @throws FormatException if the input string is not correctly formatted
     * @see #toDirectSubtypeString()
     * @see #parseDirectSubtypeString(String)
     */
    public void addDirectSubtypes(String directSubtypeString)
        throws FormatException {
        for (Map.Entry<TypeLabel,Set<TypeLabel>> subtypeEntry : parseDirectSubtypeString(
            directSubtypeString).entrySet()) {
            TypeLabel type = subtypeEntry.getKey();
            addLabel(type);
            for (TypeLabel subtype : subtypeEntry.getValue()) {
                addLabel(subtype);
                getDirSubs(type).add(subtype);
            }
        }
        invalidateSubtypes();
    }

    /**
     * @return true is the store has at least one node type.
     */
    public boolean hasNodeTypes() {
        for (Label label : this.getLabels()) {
            if (label.isNodeType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true is the store has at least one flag.
     */
    public boolean hasFlags() {
        for (Label label : this.getLabels()) {
            if (label.isFlag()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if the store has at least one unary label (i.e., node type or flag).
     */
    public boolean hasUnaryLabels() {
        for (Label label : this.getLabels()) {
            if (!label.isBinary()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LabelStore clone() {
        LabelStore result = new LabelStore();
        result.add(this);
        return result;
    }

    /** Resets the subtype map to {@code null}. */
    private void invalidateSubtypes() {
        this.subMap = null;
        invalidateSupertypes();
    }

    /** Resets the supertype maps to {@code null}. */
    private void invalidateSupertypes() {
        this.dirSuperMap = null;
        this.superMap = null;
    }

    /** Mapping from a type label to its set of direct subtypes. */
    private final Map<TypeLabel,Set<TypeLabel>> dirSubMap =
        new TreeMap<TypeLabel,Set<TypeLabel>>();
    /** Mapping from a type label to its set of subtypes (including itself). */
    private Map<TypeLabel,Set<TypeLabel>> subMap;
    /** Mapping from a type label to its set of direct subtypes. */
    private Map<TypeLabel,Set<TypeLabel>> dirSuperMap;
    /** Mapping from a type label to its set of subtypes (including itself). */
    private Map<TypeLabel,Set<TypeLabel>> superMap;

    /** Creates and prints a label store out of a property string. */
    static public void main(String[] args) {
        for (String arg : args) {
            try {
                System.out.println(createLabelStore(arg).toString());
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Factory method to create a label store from a string description of the
     * subtyping relation. The string description should be formatted according
     * to the specification in {@link #toDirectSubtypeString()}.
     * @throws FormatException if the input string is not formatted correctly.
     */
    static public LabelStore createLabelStore(String directSubtypes)
        throws FormatException {
        LabelStore result = new LabelStore();
        result.addDirectSubtypes(directSubtypes);
        return result;
    }

    /**
     * Parses a string formatted according to the output of
     * {@link #toDirectSubtypeString()} and returns the corresponding mapping
     * from supertypes to sets of direct subtypes.
     * @throws FormatException if the input string is not formatted correctly.
     */
    static public Map<TypeLabel,Set<TypeLabel>> parseDirectSubtypeString(
            String directSubtypes) throws FormatException {
        Map<TypeLabel,Set<TypeLabel>> result =
            new HashMap<TypeLabel,Set<TypeLabel>>();
        if (directSubtypes.trim().length() > 0) {
            String[] declarations =
                directSubtypes.split(WHITESPACE + MAIN_SEPARATOR + WHITESPACE);
            for (String declaration : declarations) {
                String[] splitDecl =
                    declaration.split(WHITESPACE + SUPERTYPE_SYMBOL
                        + WHITESPACE);
                if (splitDecl.length != 2) {
                    throw new FormatException(
                        "Subtype declaration '%s' should contain single instance of supertype symbol '%s'",
                        declaration, SUPERTYPE_SYMBOL);
                }
                if (!ExprParser.isIdentifier(splitDecl[0])) {
                    throw new FormatException(
                        "Invalid node type identifier '%s'", splitDecl[0]);
                }
                TypeLabel supertype =
                    TypeLabel.createLabel(splitDecl[0], Label.NODE_TYPE);
                Set<TypeLabel> subtypes = result.get(supertype);
                if (subtypes == null) {
                    result.put(supertype, subtypes = new TreeSet<TypeLabel>());
                }
                String[] declSubtypes =
                    splitDecl[1].split(WHITESPACE + SUBTYPE_SEPARATOR
                        + WHITESPACE);
                for (String subtype : declSubtypes) {
                    if (!ExprParser.isIdentifier(subtype)) {
                        throw new FormatException(
                            "Invalid node type identifier '%s'", subtype);
                    }
                    subtypes.add(TypeLabel.createLabel(subtype, Label.NODE_TYPE));
                }
            }
        }
        return result;
    }

    /** Regular expression recogniser for a whitespace sequence. */
    static private final String WHITESPACE = "\\s*";
    /** Separator between subtype declarations. */
    static public final char MAIN_SEPARATOR = ';';
    /** Separator between the subtypes in a single subtype declaration. */
    static public final char SUBTYPE_SEPARATOR = ',';
    /** Separator between supertype and the list of subtypes. */
    static public final char SUPERTYPE_SYMBOL = '>';
}
