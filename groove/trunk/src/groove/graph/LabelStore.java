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

import groove.util.ExprParser;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Set of labels and subtypes. This is used to encode the available labels and
 * node types in a grammar.
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

    /** Adds all labels and subtypes from another label store to this one. */
    public void add(LabelStore other) {
        addLabels(other.getLabels());
        for (Map.Entry<Label,Set<Label>> directSubtypeEntry : other.directSubtypeMap.entrySet()) {
            for (Label subtype : directSubtypeEntry.getValue()) {
                addSubtype(directSubtypeEntry.getKey(), subtype);
            }
        }
    }

    /** Adds a label to the set of known labels. */
    public void addLabel(Label label) {
        if (!this.subtypeMap.containsKey(label)) {
            Set<Label> subtypes = new TreeSet<Label>();
            subtypes.add(label);
            this.subtypeMap.put(label, subtypes);
            Set<Label> directSubtypes = new TreeSet<Label>();
            // if (label.isNodeType()) {
            // directSubtypes.add(label);
            // }
            this.directSubtypeMap.put(label, directSubtypes);
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
        if (getSubtypes(subtype).contains(type)) {
            throw new IllegalArgumentException(String.format(
                "The relation '%s %c %s' introduces a cyclic type dependency",
                type, SUPERTYPE_SYMBOL, subtype));
        }
        if (this.directSubtypeMap.get(type).add(subtype)) {
            // transitively close the relation
            Set<Label> subsubtypes = getSubtypes(subtype);
            for (Label supertype : getSupertypes(type)) {
                this.subtypeMap.get(supertype).addAll(subsubtypes);
            }
        }
    }

    /** Removes a direct subtype pair from the subtyping relation. */
    public void removeSubtype(Label type, Label subtype) {
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
        Set<Label> remaining = new HashSet<Label>(getLabels());
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
        result.append(String.format("Labels: %s%n", getLabels()));
        result.append(String.format("Direct subtypes: %s%n",
            this.directSubtypeMap));
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
            for (Label subtype : subtypeEntry.getValue()) {
                addSubtype(subtypeEntry.getKey(), subtype);
            }
        }
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
                Label supertype = DefaultLabel.createLabel(splitDecl[0], true);
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
                    subtypes.add(DefaultLabel.createLabel(subtype, true));
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
