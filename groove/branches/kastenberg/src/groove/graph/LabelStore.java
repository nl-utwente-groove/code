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
        addLabels(other.getLabels());
        for (Map.Entry<Label,Set<Label>> directSubtypeEntry : other.directSubtypeMap.entrySet()) {
            for (Label subtype : directSubtypeEntry.getValue()) {
                addSubtype(directSubtypeEntry.getKey(), subtype);
            }
        }
    }

    /** Adds a label to the set of known labels. */
    public void addLabel(Label label) {
        testFixed(false);
        if (!this.subtypeMap.containsKey(label)) {
            Set<Label> subtypes = new TreeSet<Label>();
            subtypes.add(label);
            this.subtypeMap.put(label, subtypes);
            Set<Label> directSubtypes = new TreeSet<Label>();
            this.directSubtypeMap.put(label, directSubtypes);
        }
    }

    /** Adds a set of labels to the set of known labels. */
    public void addLabels(Set<Label> labels) {
        testFixed(false);
        for (Label label : labels) {
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
    public void addSubtype(Label type, Label subtype)
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
        if (DefaultLabel.isDataType(type)) {
            throw new IllegalArgumentException(String.format(
                "Data type label '%s' cannot be supertype", type));
        }
        if (DefaultLabel.isDataType(subtype)) {
            throw new IllegalArgumentException(String.format(
                "Data type label '%s' cannot be subtype", subtype));
        }
        addLabel(type);
        addLabel(subtype);
        if (getSubtypes(subtype).contains(type)) {
            throw new IllegalArgumentException(String.format(
                "The relation '%s %c %s' introduces a cyclic type dependency",
                type, SUPERTYPE_SYMBOL, subtype));
        }
        if (this.directSubtypeMap.get(type).add(subtype)) {
            // transitively close the relation
            Set<Label> subsubtypes = this.subtypeMap.get(subtype);
            for (Map.Entry<Label,Set<Label>> typeEntry : this.subtypeMap.entrySet()) {
                if (typeEntry.getValue().contains(type)) {
                    typeEntry.getValue().addAll(subsubtypes);
                }
            }
        }
    }

    /** Removes a direct subtype pair from the subtyping relation. */
    public void removeSubtype(Label type, Label subtype) {
        testFixed(false);
        if (this.directSubtypeMap.get(type).remove(subtype)) {
            // recalculate the transitive closure of the subtypes
            calculateSubtypes();
        }
    }

    /**
     * Recalculates the subtype relation by transitively closing the direct
     * subtype relation.
     */
    private void calculateSubtypes() {
        // first order all types consistently with the subtype relation.
        Set<Label> allTypes = new LinkedHashSet<Label>();
        Set<Label> remaining =
            new HashSet<Label>(this.directSubtypeMap.keySet());
        while (!remaining.isEmpty()) {
            Iterator<Label> remainingIter = remaining.iterator();
            boolean bottomTypeFound = false;
            while (remainingIter.hasNext()) {
                Label bottomType = remainingIter.next();
                if (allTypes.containsAll(getDirectSubtypes(bottomType))) {
                    remainingIter.remove();
                    allTypes.add(bottomType);
                    bottomTypeFound = true;
                }
            }
            assert bottomTypeFound : String.format(
                "No bottom type found in %s", remaining);
        }
        // now build up the transitive closure
        this.subtypeMap.clear();
        for (Label type : allTypes) {
            Set<Label> subtypes = new TreeSet<Label>();
            this.subtypeMap.put(type, subtypes);
            subtypes.add(type);
            for (Label directSubtype : getDirectSubtypes(type)) {
                subtypes.addAll(this.subtypeMap.get(directSubtype));
            }
        }
    }

    /**
     * Returns an unmodifiable view on the set of direct subtypes of a given
     * node type label. Returns <code>null</code> if the label is unknown or not
     * a node type label.
     * @param label the label to determine the direct subtypes for
     * @return the direct subtypes of <code>label</code>, or <code>null</code>
     *         if <code>label</code> is not a known node type label.
     */
    public Set<Label> getDirectSubtypes(Label label) {
        Set<Label> result = this.directSubtypeMap.get(label);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /**
     * Returns an unmodifiable view on the map from labels to direct subtypes.
     * @return A map from known labels to their direct subtypes
     */
    public Map<Label,Set<Label>> getDirectSubtypeMap() {
        return Collections.unmodifiableMap(this.directSubtypeMap);
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
        Set<Label> result = this.subtypeMap.get(label);
        return result == null ? null : Collections.unmodifiableSet(result);
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
        return getInverse(this.subtypeMap).get(label);
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
        return getInverse(this.directSubtypeMap).get(label);
    }

    /**
     * Returns the direct supertype map of this label store. Note that this
     * method is inefficient, as it calculates the set of supertypes on the fly.
     * @return A map from known labels to their direct supertypes
     */
    public Map<Label,Set<Label>> getDirectSupertypeMap() {
        return getInverse(this.directSubtypeMap);
    }

    /**
     * Returns the inverse of a relation, given as a mapping from elements to
     * sets of related elements.
     */
    private Map<Label,Set<Label>> getInverse(Map<Label,Set<Label>> relation) {
        Map<Label,Set<Label>> result = new HashMap<Label,Set<Label>>();
        for (Label type : getLabels()) {
            result.put(type, new TreeSet<Label>());
        }
        for (Map.Entry<Label,Set<Label>> entry : relation.entrySet()) {
            for (Label subtype : entry.getValue()) {
                result.get(subtype).add(entry.getKey());
            }
        }
        return result;
    }

    /** Returns an unmodifiable view on the set of all known type labels. */
    public Set<Label> getLabels() {
        return Collections.unmodifiableSet(this.subtypeMap.keySet());
    }

    /**
     * Returns a clone of this label store where all occurrences of a given
     * label are replaced by a new label.
     * @param oldLabel the label to be replaced
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this label store, or the store itself if {@code
     *         oldLabel} did not occur
     */
    public LabelStore relabel(Label oldLabel, Label newLabel) {
        if (getLabels().contains(newLabel)) {
            throw new IllegalArgumentException(String.format(
                "New label '%s' is already in label set", getLabels()));
        }
        LabelStore result = this;
        if (this.subtypeMap.containsKey(oldLabel)) {
            result = clone();
            result.addLabel(newLabel);
            if (newLabel.isNodeType()) {
                result.directSubtypeMap.get(newLabel).addAll(
                    getDirectSubtypes(oldLabel));
            }
            result.directSubtypeMap.remove(oldLabel);
            result.subtypeMap.remove(oldLabel);
            for (Map.Entry<Label,Set<Label>> subtypeEntry : result.directSubtypeMap.entrySet()) {
                Set<Label> subtypes = subtypeEntry.getValue();
                if (subtypes.remove(oldLabel) && newLabel.isNodeType()) {
                    subtypes.add(newLabel);
                }
            }
            result.calculateSubtypes();
        }
        return result;
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
        SortedSet<Label> sortedLabels = new TreeSet<Label>(getLabels());
        for (Label label : sortedLabels) {
            result.append(String.format("%s > %s%n", label,
                this.directSubtypeMap.get(label)));
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
        for (Map.Entry<Label,Set<Label>> directSubtypeEntry : this.directSubtypeMap.entrySet()) {
            // only treat proper entries
            if (directSubtypeEntry.getValue().isEmpty()) {
                continue;
            }
            if (!firstLabel) {
                result.append(MAIN_SEPARATOR + " ");
            } else {
                firstLabel = false;
            }
            result.append(directSubtypeEntry.getKey().text());
            result.append(" " + SUPERTYPE_SYMBOL + " ");
            boolean firstSubtype = true;
            for (Label subType : directSubtypeEntry.getValue()) {
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
        for (Map.Entry<Label,Set<Label>> subtypeEntry : parseDirectSubtypeString(
            directSubtypeString).entrySet()) {
            Label type = subtypeEntry.getKey();
            addLabel(type);
            for (Label subtype : subtypeEntry.getValue()) {
                addLabel(subtype);
                this.directSubtypeMap.get(type).add(subtype);
            }
        }
        calculateSubtypes();
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

    /** Mapping from a type label to its set of subtypes (including itself). */
    private final Map<Label,Set<Label>> subtypeMap =
        new TreeMap<Label,Set<Label>>();
    /** Mapping from a type label to its set of direct subtypes. */
    private final Map<Label,Set<Label>> directSubtypeMap =
        new TreeMap<Label,Set<Label>>();

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
    static public Map<Label,Set<Label>> parseDirectSubtypeString(
            String directSubtypes) throws FormatException {
        Map<Label,Set<Label>> result = new HashMap<Label,Set<Label>>();
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
                Label supertype =
                    DefaultLabel.createLabel(splitDecl[0], Label.NODE_TYPE);
                Set<Label> subtypes = result.get(supertype);
                if (subtypes == null) {
                    result.put(supertype, subtypes = new TreeSet<Label>());
                }
                String[] declSubtypes =
                    splitDecl[1].split(WHITESPACE + SUBTYPE_SEPARATOR
                        + WHITESPACE);
                for (String subtype : declSubtypes) {
                    if (!ExprParser.isIdentifier(subtype)) {
                        throw new FormatException(
                            "Invalid node type identifier '%s'", subtype);
                    }
                    subtypes.add(DefaultLabel.createLabel(subtype,
                        Label.NODE_TYPE));
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
