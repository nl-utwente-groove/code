/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parsed aspect, as used in an aspect graph to represent features
 * of the models. An aspect consists of an aspect kind and an optional
 * content field.
 * @author Arend Rensink
 * @version $Revision $
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
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Aspect relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        Aspect result = this;
        AspectContent newContent = getContent().relabel(oldLabel, newLabel);
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

    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static java.util.Map<String,Aspect> aspectNameMap = new HashMap<>();

    static {
        for (AspectKind kind : AspectKind.values()) {
            aspectNameMap.put(kind.getName(), kind.getAspect());
        }
    }

    /** Mapping from aspect kinds to aspects. */
    static public class Map extends EnumMap<AspectKind.Category,Aspect> implements Comparable<Map> {
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
            Aspect old = get(aspect.getCategory());
            boolean add;
            if (old == null) {
                add = true;
            } else if (old.getKind() != aspect.getKind()) {
                throw new FormatException("Conflicting aspects '%s' and '%s'", old, aspect);
            } else if (old.hasContent()) {
                if (aspect.hasContent()) {
                    throw new FormatException("Duplicate aspects '%s' and '%s'", old, aspect);
                }
                add = false;
            } else {
                add = aspect.hasContent();
            }
            if (add) {
                var cat = aspect.getCategory();
                var conflict = conflict(cat);
                if (conflict != null) {
                    throw new FormatException("Conflicting aspects '%s' and '%s'", get(conflict),
                        aspect);
                }
                put(cat, aspect);
            }
        }

        /** Returns a category already in this map that is in conflict with a given category;
         * or {@code null} if there is no conflicting category.
         */
        private @Nullable Category conflict(Category cat) {
            var result
                = keySet().stream().filter(c -> !c.ok(cat, this.forNode, this.role)).findAny();
            return ((Optional<@Nullable Category>) result).orElse(null);
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
    }
}
