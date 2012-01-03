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
package groove.graph;

import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.util.Groove;
import groove.util.Property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encodes a constraint on type labels, which can be used to filter
 * sets of type elements.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeGuard extends Property<TypeElement> {
    /**
     * Constructs a new constraint.
     * @param var the label variable associated with the constraint
     * @param kind The kind of labels tested for; only labels of this type can ever satisfy the constraint
     */
    public TypeGuard(LabelVar var, EdgeRole kind) {
        this.var = var;
        this.kind = kind;
    }

    /** Returns the optional type variable associated with this guard. */
    public LabelVar getVar() {
        return this.var;
    }

    /** Indicates if this guard has a named label variable. */
    public boolean isNamed() {
        return getVar().hasName();
    }

    /** Returns the kind of labels accepted by this constraint. */
    public EdgeRole getKind() {
        return this.kind;
    }

    /** 
     * Sets the set of labels to test for.
     * @param textList List of labels which membership is tested; may be {@code null} if only the label type is tested for
     * @param negated if {@code true}, satisfaction is defined as presence in {@code textList}; otherwise as absence
     */
    public void setLabels(List<String> textList, boolean negated) {
        this.textList = textList;
        this.labelSet = new HashSet<TypeLabel>();
        for (String text : textList) {
            this.labelSet.add(TypeLabel.createLabel(this.kind, text));
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
        if (this.labelSet != null && this.labelSet.contains(oldLabel)) {
            int index = this.textList.indexOf(oldLabel.text());
            List<String> newTextList = new ArrayList<String>(this.textList);
            if (newLabel.getRole() == this.kind) {
                newTextList.set(index, newLabel.text());
            } else {
                newTextList.remove(index);
            }
            result = new TypeGuard(this.var, this.kind);
            result.setLabels(newTextList, this.negated);
        }
        return result;
    }

    /**
     * Returns the (possibly {@code null}) set of labels occurring in this label constraint.
     * @see RegExpr#getTypeLabels()
     */
    public Set<TypeLabel> getLabels() {
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
        if (this.kind != ((type instanceof TypeNode) ? EdgeRole.NODE_TYPE
                : ((TypeEdge) type).getRole())) {
            return false;
        }
        if (this.labelSet == null) {
            return true;
        }
        boolean valueFound = this.labelSet.contains(type.label());
        if (!valueFound) {
            for (TypeElement superType : type.getSupertypes()) {
                if (this.labelSet.contains(superType.label())) {
                    valueFound = true;
                    break;
                }
            }
        }
        return this.negated != valueFound;
    }

    @Override
    public String toString() {
        String result = "";
        if (this.labelSet != null) {
            result += OPEN;
            if (this.negated) {
                result += NEGATOR;
            }
            result =
                Groove.toString(this.textList.toArray(), result, "" + CLOSE, ""
                    + SEPARATOR);
        }
        return result;
    }

    /** The optional label variable associated with the constraint. */
    private final LabelVar var;
    /** The type of label we are testing for. See {@link Label#getRole()} */
    private final EdgeRole kind;
    /** The list of strings indicating the labels to be matched. */
    private List<String> textList;
    /** The set of labels to be tested for inclusion. */
    private Set<TypeLabel> labelSet;
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
