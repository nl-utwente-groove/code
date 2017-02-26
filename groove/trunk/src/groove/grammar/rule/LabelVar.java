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
package groove.grammar.rule;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.automaton.RegExpr;
import groove.grammar.AnchorKind;
import groove.graph.EdgeRole;

/**
 * Encodes a label variable (which may occur in a wildcard expression).
 * Essentially consists of a name and a kind, corresponding to the
 * label kind of the allowed values.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class LabelVar implements AnchorKey, Comparable<LabelVar> {
    /**
     * Private constructor initialising all fields
     * @param nr number of the new variable; 0 unless the name is empty
     * @param name non-{@code null} name of the new variable
     * @param kind label kind of the variable
     */
    private LabelVar(int nr, String name, EdgeRole kind) {
        this.nr = nr;
        this.name = name;
        this.kind = kind;
        assert name != null;
    }

    /**
     * Constructs a label variable from a given name and kind.
     * @param name name of the label variable; non-{@code null} and nonempty
     * @param kind kind of the label variable.
     */
    public LabelVar(String name, EdgeRole kind) {
        this(0, name, kind);
        assert !name.isEmpty();
    }

    /**
     * Constructs a fresh unnamed label variable of a give label kind.
     * @param kind kind of the label variable.
     */
    public LabelVar(EdgeRole kind) {
        this(++unnamedLabelCounter, "", kind);
    }

    /**
     * Indicates if this variable has a nonempty name.
     */
    public final boolean hasName() {
        return this.name.length() > 0;
    }

    /**
     * Returns the name of the variable.
     * Note that the name alone does not uniquely identify the variable,
     * as there may be multiple unnamed variables; see {@link #getKey()}
     */
    public final String getName() {
        return this.name;
    }

    /** Returns an identifying key for this variable. */
    public final String getKey() {
        return getName() + "-" + this.nr;
    }

    /**
     * Returns the kind of this label variable.
     */
    public final EdgeRole getKind() {
        return this.kind;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.LABEL;
    }

    @Override
    public String toString() {
        return RegExpr.WILDCARD_OPERATOR + getName();
    }

    @Override
    public int hashCode() {
        return getKey().hashCode() ^ getKind().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LabelVar other = (LabelVar) obj;
        if (getKind() != other.getKind()) {
            return false;
        }
        return getKey().equals(other.getKey());
    }

    @Override
    public int compareTo(LabelVar o) {
        int result = getName().compareTo(o.getName());
        if (result != 0) {
            return result;
        }
        result = getKind().compareTo(o.getKind());
        if (result != 0) {
            return result;
        }
        return this.nr - o.nr;
    }

    /** The number of the label variable; 0 unless the name is empty. */
    private final int nr;
    /** The name of the label variable. */
    private final String name;
    /** The kind of the label variable. */
    private final EdgeRole kind;

    /** Counter used to make unnamed labels unique. */
    private static int unnamedLabelCounter;
}
