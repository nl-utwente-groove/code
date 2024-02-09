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
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.TEST;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.function.BiPredicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.MultiplicityContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parsed aspect, as used in an aspect graph to represent features
 * of the models. An aspect consists of an aspect kind and an optional
 * content field.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Aspect {
    /** Creates a prototype (i.e., empty) aspect for a given aspect kind. */
    Aspect(AspectKind kind) {
        this.aspectKind = kind;
        this.prototype = true;
        this.content = new NullContent(kind.getContentKind());
    }

    /** Creates a new aspect, wrapping either a number or a text. */
    Aspect(AspectKind kind, AspectContent content) {
        assert kind.getContentKind() == content.kind();
        this.aspectKind = kind;
        this.content = content;
        this.prototype = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.aspectKind.hashCode();
        result = prime * result + this.content.hashCode();
        return result;
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
        Aspect other = (Aspect) obj;
        if (this.aspectKind != other.aspectKind) {
            return false;
        }
        if (!this.content.equals(other.content)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getContent().toParsableString(getKind());
    }

    /**
     * Creates a new aspect, of the same kind of this one (which must be a prototype),
     * wrapping content derived from given (non-{@code null} text.
     * @param role intended graph role of the new aspect
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated
     * @throws FormatException if the text cannot be correctly parsed as content
     * for this aspect
     */
    public Aspect newInstance(String text, GraphRole role) throws FormatException {
        if (!this.prototype) {
            throw Exceptions.unsupportedOp("New aspects can only be created from prototypes");
        }
        return getKind().newAspect(text, role);
    }

    /**
     * Returns an aspect obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @param typing TODO
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Aspect relabel(TypeLabel oldLabel, TypeLabel newLabel, SortMap typing) {
        Aspect result = this;
        AspectContent newContent = getContent().relabel(oldLabel, newLabel, typing);
        if (newContent != getContent()) {
            result = new Aspect(getKind(), newContent);
        }
        return result;
    }

    /** Returns the aspect kind. */
    public AspectKind getKind() {
        return this.aspectKind;
    }

    /** Aspect kind of this aspect. */
    private final AspectKind aspectKind;

    /** Tests if this aspect has a given kind.
     * Convenience method for {@code getKind() == kind}
     */
    public boolean has(AspectKind kind) {
        return getKind() == kind;
    }

    /**
     * Returns the category of this aspect.
     * Convenience methods for {@link AspectKind#getCategory()}.
     */
    public AspectKind.Category getCategory() {
        return getKind().getCategory();
    }

    /** Tests if this aspect kind is of a given category.
     * Convenience method for {@code getCetegory() == cat}
     */
    public boolean has(AspectKind.Category cat) {
        return getCategory() == cat;
    }

    /**
     * Indicates if this aspect has non-null content.
     */
    public boolean hasContent() {
        return !(this.content instanceof NullContent);
    }

    /**
     * Returns the content wrapped by this aspect.
     */
    public AspectContent getContent() {
        return this.content;
    }

    /** Content wrapped in this aspect; {@code null} if this is a prototype. */
    private final AspectContent content;

    /** Convenience method to return the content kind of this aspect. */
    public ContentKind getContentKind() {
        return getKind().getContentKind();
    }

    /**
     * Returns a string description of the aspect content that can be parsed back to the content,
     * or the empty string if the aspect has no content.
     * Convenience method for {@link AspectContent#toParsableString()}
     */
    public String getContentString() {
        return getContent().toParsableString();
    }

    /** Indicates that this aspect kind is allowed to appear on edges of a particular graph kind. */
    public boolean isForEdge(GraphRole role) {
        boolean result = AspectKind.allowedEdgeKinds.get(role).contains(getKind());
        if (result && getKind().hasSort()) {
            result = !(getContent() instanceof ConstContent || getContent() instanceof ExprContent);
        }
        return result;
    }

    /** Indicates that this aspect kind is allowed to appear on nodes of a particular graph kind. */
    public boolean isForNode(GraphRole role) {
        boolean result = AspectKind.allowedNodeKinds.get(role).contains(getKind());
        if (result && getKind().hasSort()) {
            result = switch (role) {
            case TYPE -> !hasContent();
            case RULE -> !hasContent() || (getContent() instanceof ExprContent);
            case HOST -> (getContent() instanceof ConstContent c) && c.get().isTerm();
            default -> throw Exceptions.UNREACHABLE;
            };
        }
        return result;
    }

    /** Flag indicating that this aspect is a prototype. */
    private final boolean prototype;

    /** Returns the prototypical aspect for a given aspect name. */
    public static Aspect getAspect(String name) {
        return aspectNameMap.get(name);
    }

    /** Returns the prototypical aspect for a given data sort. */
    public static Aspect getAspect(Sort sort) {
        return aspectNameMap.get(sort.getName());
    }

    /** Creates a new, sorted aspect containing a given expression.
     * This is only valid for host graphs and rule graphs.
     */
    public static Aspect newAspect(Constant value, GraphRole role) {
        assert role == GraphRole.HOST || role == GraphRole.RULE;
        AspectKind kind = AspectKind.toAspectKind(value.getSort());
        try {
            var content = switch (role) {
            case HOST -> new ConstContent(kind.getContentKind(), value);
            case RULE -> new ExprContent(kind.getContentKind(),
                Expression.parse(value.toParseString()));
            default -> throw Exceptions.UNREACHABLE;
            };
            return new Aspect(kind, content);
        } catch (FormatException exc) {
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static java.util.Map<String,Aspect> aspectNameMap = new HashMap<>();

    static {
        for (AspectKind kind : AspectKind.values()) {
            aspectNameMap.put(kind.getName(), kind.getAspect());
        }
    }

    /** Mapping from aspect kinds to aspects. */
    static public class Map extends EnumMap<AspectKind.Category,Aspect>
        implements Comparable<Map>, Fixable {
        /** Constructs an empty map for a node or edge. */
        public Map(boolean forNode, GraphRole role) {
            super(AspectKind.Category.class);
            this.forNode = forNode;
            this.role = role;
        }

        /** Flag determining whether this is a node or edge map. */
        private final boolean forNode;
        /** Graph role of the aspect element. */
        private final GraphRole role;

        /** Adds a given aspect to this map, as determined by its category.
         * @throws FormatException if an aspect of the same category is already in the map,
         * either of a different kind or of the same kind and both have content.
         */
        @SuppressWarnings("null")
        public void add(Aspect aspect) throws FormatException {
            assert !isFixed();
            Aspect old = get(aspect.getCategory());
            boolean add;
            if (old == null) {
                add = true;
            } else if (old.getKind() != aspect.getKind()) {
                throw new FormatException("Conflicting aspects '%s' and '%s'", old.getKind(),
                    aspect.getKind());
            } else if (old.hasContent()) {
                if (aspect.hasContent()) {
                    throw new FormatException("Duplicate aspects '%s' and '%s'", old, aspect);
                }
                add = false;
            } else {
                add = aspect.hasContent();
            }
            if (add) {
                checkConflict(aspect);
                checkOther(aspect);
                put(aspect);
            }
        }

        /** Checks a new aspect for conflicts with existing aspect categories. */
        private void checkConflict(Aspect newAspect) throws FormatException {
            var result = keySet()
                .stream()
                .filter(c -> !c.ok(newAspect.getCategory(), this.forNode, this.role))
                .findAny();
            if (result.isPresent()) {
                throw new FormatException("Conflicting aspects '%s' and '%s'",
                    get(result.get()).getKind(), newAspect.getKind());
            }
        }

        /** Performs extra compatibility checks before inserting a new aspect into the map. */
        private void checkOther(Aspect newAspect) throws FormatException {
            // insert the aspect in the map for symmetry, to be removed again later
            Aspect oldAspect = put(newAspect);
            try {
                switch (this.role) {
                case HOST: // no extra checks
                    break;
                case RULE:
                    if (containsKey(Category.ROLE) || containsKey(Category.NESTING)) {
                        checkRoleSort(this.forNode);
                        checkRoleAttr();
                        if (this.forNode) {
                            checkRoleParam();
                            checkRoleColor();
                            checkNestingId();
                        } else {
                            checkRoleNesting();
                            checkNestingLabel();
                        }
                    }
                    break;
                case TYPE:
                    checkMultAssoc();
                    break;
                default:
                    throw Exceptions.UNREACHABLE;
                }
            } finally {
                if (oldAspect == null) {
                    remove(newAspect.getCategory());
                } else {
                    put(oldAspect);
                }
            }
        }

        /** Checks for aspect-based conflicts between {@link Category#ROLE} and {@link Category#SORT}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkRoleSort(boolean forNode) throws FormatException {
            check(Category.ROLE, Category.SORT, (role, sort) -> role.getKind().isCreator()
                || forNode && role.getKind().isEraser());
        }

        /** Checks for aspect-based conflicts between {@link Category#ROLE} and {@link Category#ATTR}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkRoleAttr() throws FormatException {
            check(Category.ROLE, Category.ATTR, //
                  (role, attr) -> attr.getKind().isAssign()
                      ? role.has(EMBARGO)
                      : role.getKind().isCreator() || !attr.has(TEST) && role.getKind().isEraser());
        }

        /** Checks for aspect-based conflicts between {@link Category#ROLE} and {@link Category#PARAM}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkRoleParam() throws FormatException {
            check(Category.ROLE, Category.PARAM, //
                  (role, param) -> role.getKind().inNAC()
                      || role.getKind().isCreator() && param.getKind() == AspectKind.PARAM_IN);
        }

        /** Checks for aspect-based conflicts between {@link Category#ROLE} and {@link Category#COLOR}. */
        private void checkRoleColor() throws FormatException {
            check(Category.ROLE, Category.COLOR,
                  (role, color) -> role.getKind().inNAC() || role.getKind().isEraser());
        }

        /** Checks for aspect-based conflicts between {@link Category#ROLE} and {@link Category#NESTING}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkRoleNesting() throws FormatException {
            check(Category.ROLE, Category.NESTING,
                  (role, nesting) -> !nesting.getKind().isQuantifier());
        }

        /** Checks for aspect-based conflicts between {@link Category#NESTING} and {@link Category#ID}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkNestingId() throws FormatException {
            check(Category.NESTING, Category.ID, (nesting, id) -> nesting.hasContent());
        }

        /** Checks for aspect-based conflicts between {@link Category#NESTING} and {@link Category#LABEL}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkNestingLabel() throws FormatException {
            check(Category.NESTING, Category.LABEL,
                  (nesting, label) -> !nesting.getKind().isQuantifier());
        }

        /** Checks for aspect-based conflicts between {@link Category#MULT_IN} and {@link Category#ASSOC}.
         * @throws FormatException if there is a conflict of this kind
         */
        private void checkMultAssoc() throws FormatException {
            check(Category.MULT_IN, Category.ASSOC, //
                  (multIn, assoc) -> ((MultiplicityContent) multIn.getContent()).get().upper() > 1);
        }

        /** Checks for a conflict between aspects of two categories, as encoded in a test.
         * @param one The first category to be tested
         * @param two The second category to be tested
         * @param test the test; if aspects of both categories {@code one} and {@code two}
         * are present and pass the test, there is a conflict
         * @throws FormatException if the test passes
         */
        @SuppressWarnings("null")
        private void check(Category one, Category two,
                           BiPredicate<Aspect,Aspect> test) throws FormatException {
            var aspectOne = get(one);
            var aspectTwo = get(two);
            if (aspectOne != null && aspectTwo != null) {
                if (test.test(aspectOne, aspectTwo)) {
                    throw new FormatException("Conflicting aspects '%s' and '%s'",
                        aspectOne.getKind(), aspectTwo.getKind());
                }
            }
        }

        /** Checks whether this map has any entry for a given aspect kind. */
        @SuppressWarnings("null")
        public boolean has(@Nullable AspectKind kind) {
            if (kind == null) {
                return false;
            }
            var aspect = get(kind.getCategory());
            if (aspect == null) {
                return false;
            }
            if (aspect.getKind() != kind) {
                return false;
            }
            return true;
        }

        @Override
        public int compareTo(Map o) {
            for (Category cat : Category.values()) {
                int result = 0;
                if (containsKey(cat)) {
                    if (o.containsKey(cat)) {
                        result = get(cat).getKind().ordinal() - o.get(cat).getKind().ordinal();
                    } else {
                        result = 1;
                    }
                } else if (o.containsKey(cat)) {
                    result = -1;
                }
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        /** Inserts an aspect into the map without any checks,
         * and returns the previous aspect of that category (if any).
         */
        private @Nullable Aspect put(Aspect newAspect) {
            return put(newAspect.getCategory(), newAspect);
        }

        @Override
        public boolean setFixed() {
            boolean result = !isFixed();
            if (result) {
                this.fixed = true;
            }
            return result;
        }

        @Override
        public boolean isFixed() {
            return this.fixed;
        }

        private boolean fixed;
    }

    /** Returns a normalised version of a (fixed) aspect map. */
    @SuppressWarnings("null")
    static public Map normalise(Map original) {
        assert original.isFixed();
        Map result = normalisedMap.get(original);
        if (result == null) {
            normalisedMap.put(original, original);
            result = original;
        }
        return result;
    }

    static private final java.util.Map<Map,Map> normalisedMap = new HashMap<>();
}
