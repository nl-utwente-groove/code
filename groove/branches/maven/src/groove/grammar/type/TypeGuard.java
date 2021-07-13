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
package groove.grammar.type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.automaton.RegExpr;
import groove.grammar.rule.LabelVar;
import groove.graph.EdgeRole;
import groove.graph.Label;
import groove.util.NoNonNull;
import groove.util.Property;

/**
 * Encodes a constraint on type labels, which can be used to filter
 * sets of type elements.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class TypeGuard extends Property<TypeElement> {
    /**
     * Constructs a new constraint.
     * @param var the label variable associated with the constraint; non-{@code null}
     */
    public TypeGuard(LabelVar var) {
        this.var = var;
    }

    /** Returns the (non-{@code null}, possibly unnamed) type variable associated with this guard. */
    public LabelVar getVar() {
        return this.var;
    }

    /** Indicates if this guard has a named label variable. */
    public boolean isNamed() {
        return getVar().hasName();
    }

    /** Returns the kind of labels accepted by this constraint. */
    public EdgeRole getKind() {
        return getVar().getKind();
    }

    /**
     * Sets the set of labels to test for.
     * @param textList List of labels which membership is tested; may be {@code null} if only the label type is tested for
     * @param negated if {@code true}, satisfaction is defined as presence in {@code textList}; otherwise as absence
     */
    @SuppressWarnings("null")
    public void setLabels(List<String> textList, boolean negated) {
        this.labelSet = new LinkedHashSet<>();
        for (String text : textList) {
            this.labelSet.add(TypeLabel.createLabel(getKind(), text));
        }
        this.negated = negated;
    }

    /**
     * Returns a copy of this label constraint with given label replaced
     * by another. Returns this constraint if the old label does not
     * occur.
     * @param oldLabel the label to be replaced
     * @param newLabel the new value for {@code oldLabel}
     * @return a copy of this constraint with the old label replaced by
     *         the new, or this constraint itself if the old label did
     *         not occur.
     */
    public TypeGuard relabel(Label oldLabel, Label newLabel) {
        TypeGuard result = this;
        Set<TypeLabel> labelSet = this.labelSet;
        if (oldLabel.getRole() == getKind() && labelSet != null && labelSet.contains(oldLabel)) {
            List<String> textList = new ArrayList<>(labelSet.size());
            if (newLabel.getRole() == getKind() && !labelSet.contains(newLabel)) {
                labelSet.stream()
                    .forEach(l -> textList.add((l.equals(oldLabel) ? newLabel : l).text()));
            } else {
                labelSet.stream()
                    .filter(l -> !l.equals(oldLabel))
                    .forEach(l -> textList.add(l.text()));
            }
            result = new TypeGuard(this.var);
            result.setLabels(textList, this.negated);
        }
        return result;
    }

    /**
     * Returns the (possibly {@code null}) set of labels occurring in this label constraint.
     * @see RegExpr#getTypeLabels()
     */
    public @Nullable Set<TypeLabel> getLabels() {
        return this.labelSet;
    }

    /**
     * Determines if this label constraint is a negative constraint
     * like [^a,b,c].
     */
    public boolean isNegated() {
        return this.negated;
    }

    @Override
    public boolean isSatisfied(TypeElement type) {
        if (getKind() != ((type instanceof TypeNode) ? EdgeRole.NODE_TYPE
            : ((TypeEdge) type).getRole())) {
            return false;
        }
        Set<TypeLabel> labelSet = this.labelSet;
        if (labelSet == null) {
            return true;
        }
        boolean valueFound = labelSet.contains(type.label());
        if (!valueFound) {
            for (TypeElement superType : type.getSupertypes()) {
                if (labelSet.contains(superType.label())) {
                    valueFound = true;
                    break;
                }
            }
        }
        return this.negated != valueFound;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Set<TypeLabel> labelSet = this.labelSet;
        if (labelSet != null) {
            result.append(OPEN);
            if (this.negated) {
                result.append(NEGATOR);
            }
            boolean first = true;
            for (TypeLabel label : labelSet) {
                if (first) {
                    first = false;
                } else {
                    result.append(SEPARATOR);
                }
                result.append(label.text());
            }
            result.append(CLOSE);
        }
        return NoNonNull.toString(result);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        Set<TypeLabel> labelSet = this.labelSet;
        result = prime * result + ((labelSet == null) ? 0 : labelSet.hashCode());
        result = prime * result + (this.negated ? 1231 : 1237);
        result = prime * result + this.var.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeGuard)) {
            return false;
        }
        TypeGuard other = (TypeGuard) obj;
        Set<TypeLabel> labelSet = this.labelSet;
        if (labelSet == null) {
            if (other.labelSet != null) {
                return false;
            }
        } else if (!labelSet.equals(other.labelSet)) {
            return false;
        }
        if (this.negated != other.negated) {
            return false;
        }
        if (!this.var.equals(other.var)) {
            return false;
        }
        return true;
    }

    /** The optional label variable associated with the constraint. */
    private final LabelVar var;
    /** The optional set of labels to be tested for inclusion. */
    private @Nullable Set<TypeLabel> labelSet;
    /** Flag indicating if we are testing for absence or presence. */
    private boolean negated;
    /** Opening bracket of a wildcard constraint. */
    static public final char OPEN = '[';
    /** Closing bracket of a wildcard constraint. */
    static public final char CLOSE = ']';
    /** Character to indicate negation of a constraint. */
    static public final char NEGATOR = '^';
    /** Character to separate constraint parts. */
    static public final char SEPARATOR = ',';
}
