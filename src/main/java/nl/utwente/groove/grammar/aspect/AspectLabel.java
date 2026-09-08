/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.grammar.aspect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.ALabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Label storing a set of aspect values and an inner text.
 * <p>
 * Instances are immutable, and are constructed through a {@link Builder}: see
 * {@link #builder(GraphRole)} to build one from scratch, and
 * {@link #toBuilder()} to build a variant of an existing label.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class AspectLabel extends ALabel {
    /**
     * Constructs a label from a builder.
     * This is the only constructor; all fields are set here and never change.
     */
    private AspectLabel(Builder builder) {
        this.role = builder.role;
        this.aspects = List.copyOf(builder.aspects);
        var innerText = builder.innerText;
        this.innerText = innerText == null
            ? ""
            : innerText;
        this.edgeOnly = builder.edgeOnly;
        this.nodeOnly = builder.nodeOnly;
        var errors = builder.errors;
        if (errors.isEmpty()) {
            this.errors = FormatErrorSet.EMPTY;
        } else {
            errors.setFixed();
            this.errors = errors;
        }
    }

    /**
     * Returns a builder for a variant of this label.
     * The aspects and inner text are carried over; the errors are not, since
     * they pertain to the parsing of the original label text.
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.parseLabel(getInnerText()).one();
    }

    @Override
    public int compareTo(Label obj) {
        int result = getRole().compareTo(obj.getRole());
        if (result == 0 && obj instanceof AspectLabel label) {
            // Labels starting with letters precede all other labels
            String myText = EdgeRole.parseLabel(getInnerText()).two();
            boolean myTextIsAlpha
                = myText.length() > 0 && Character.isJavaIdentifierStart(myText.charAt(0));
            String hisText = EdgeRole.parseLabel(label.getInnerText()).two();
            boolean hisTextIsAlpha
                = hisText.length() > 0 && Character.isJavaIdentifierStart(hisText.charAt(0));
            if (myTextIsAlpha != hisTextIsAlpha) {
                result = myTextIsAlpha
                    ? -1
                    : +1;
            }
        }
        if (result == 0) {
            result = text().compareTo(obj.text());
        }
        return result;
    }

    /** Returns the graph role for which this label is intended. */
    public GraphRole getGraphRole() {
        return this.role;
    }

    /** The graph role for which this label is intended to be used. */
    private final GraphRole role;

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toParsableString() {
        StringBuffer result = new StringBuffer();
        for (Aspect value : this.aspects) {
            result.append(value.toString());
        }
        // append the label text, if any
        result.append(getInnerText());
        return result.toString();
    }

    /**
     * Wraps the {@link #toParsableString()} using {@link Line#atom(String)}.
     */
    @Override
    protected Line computeLine() {
        return Line.atom(toParsableString());
    }

    /**
     * Delegates to {@link #toParsableString()}.
     */
    @Override
    public String toString() {
        return toParsableString();
    }

    /**
     * Returns an aspect label obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a variant of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public AspectLabel relabel(TypeLabel oldLabel, TypeLabel newLabel, SortMap typing) {
        boolean isNew = false;
        List<Aspect> newAspects = new ArrayList<>();
        for (Aspect aspect : getAspects()) {
            Aspect newAspect = aspect.relabel(oldLabel, newLabel, typing);
            isNew |= newAspect != aspect;
            newAspects.add(newAspect);
        }
        if (!isNew) {
            return this;
        }
        // note that the inner text is deliberately not carried over:
        // relabelling the inner text is the caller's responsibility
        Builder result = builder(getGraphRole());
        for (Aspect newAspect : newAspects) {
            result.addAspect(newAspect);
        }
        return result.build();
    }

    /**
     * Returns a copy of this label minus any {@link AspectKind#LITERAL}
     * aspect.
     */
    public AspectLabel unwrap() {
        return toBuilder().removeAspects(a -> a.getKind() == AspectKind.LITERAL).build();
    }

    /** Tests if the aspects and text of this object equal those of another. */
    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj
            || obj instanceof AspectLabel label && equalsAspects(label) && equalsText(label);
    }

    /** Computes a hash code value. */
    @Override
    protected int computeHashCode() {
        return this.aspects.hashCode() + this.innerText.hashCode();
    }

    /** Indicates if the aspects in this map equal those in another map. */
    public boolean equalsAspects(AspectLabel other) {
        return this.aspects.equals(other.aspects);
    }

    /**
     * Indicates if the {@link #innerText} of this map equals that of another.
     */
    private boolean equalsText(AspectLabel other) {
        return this.innerText.equals(other.innerText);
    }

    /** Returns the list of aspects in this label. */
    public List<Aspect> getAspects() {
        return this.aspects;
    }

    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final List<Aspect> aspects;

    /** Tests if this label contains an aspect of a given kind. */
    public boolean has(AspectKind kind) {
        return hasAspect(a -> a.getKind() == kind);
    }

    /** Tests if this label contains and aspect with a certain property.
     * @param test the predicate testing for the property.
     */
    public boolean hasAspect(Predicate<Aspect> test) {
        return getAspects().stream().anyMatch(test);
    }

    /**
     * Indicates whether this label is only suited for edges.
     * This is the case if either it contains an aspect that is not
     * suited for nodes, or the label text is non-empty.
     */
    public final boolean isEdgeOnly() {
        return this.edgeOnly != null || this.innerText.length() > 0;
    }

    /**
     * Indicates whether this label is only suited for nodes.
     * This is the case if either it contains an aspect that is not suited
     * for edges, or if the label text is empty and the label is not edge-only.
     */
    public final boolean isNodeOnly() {
        return this.nodeOnly != null || this.edgeOnly == null && this.innerText.length() == 0;
    }

    /** Returns an aspect of this label that makes it suitable for edges only.
     * Returns {@code null} if there is no such aspect.
     */
    public @Nullable Aspect getEdgeOnlyAspect() {
        return this.edgeOnly;
    }

    /** Returns an aspect of this label that makes it suitable for edges only.
     * Returns {@code null} if there is no such aspect.
     */
    public @Nullable Aspect getNodeOnlyAspect() {
        return this.nodeOnly;
    }

    /** Edge-only aspect value in this label, if any. */
    private final @Nullable Aspect edgeOnly;

    /** Node-only aspect value in this label, if any. */
    private final @Nullable Aspect nodeOnly;

    /**
     * Returns the label text of this aspect label.
     * Guaranteed to be non-{@code null}; empty if no text was set.
     */
    public String getInnerText() {
        return this.innerText;
    }

    /** Label text; empty if the associated element is a node. */
    private final String innerText;

    /** Indicates if there are any blocking errors in this label. */
    public boolean hasErrors() {
        return this.errors.hasErrors();
    }

    /** Returns the (possibly empty) list of errors in this label. */
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** List of errors detected while building this label. */
    private final FormatErrorSet errors;

    /** Returns a builder for a label of a given graph role. */
    public static Builder builder(GraphRole role) {
        return new Builder(role);
    }

    /** The set of all allowed nesting labels. */
    private static final Map<GraphRole,String> roleDescription = new EnumMap<>(GraphRole.class);
    static {
        roleDescription.put(GraphRole.HOST, "host graph");
        roleDescription.put(GraphRole.TYPE, "type graph");
        roleDescription.put(GraphRole.RULE, "rule graph");
    }

    /**
     * Builder for {@link AspectLabel}s.
     * Collects aspects, inner text and errors, and assembles them into an
     * immutable label on {@link #build()}. A builder may be built more than
     * once; each call yields an independent label.
     * @author Arend Rensink
     * @version $Revision$
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static class Builder {
        /** Creates a builder for an initially empty label of a given graph role. */
        private Builder(GraphRole role) {
            assert role.inGrammar();
            this.role = role;
        }

        /**
         * Creates a builder seeded with the content of an existing label.
         * The errors of the original are not carried over.
         */
        private Builder(AspectLabel label) {
            this.role = label.role;
            this.aspects.addAll(label.aspects);
            this.innerText = label.innerText;
            this.edgeOnly = label.edgeOnly;
            this.nodeOnly = label.nodeOnly;
        }

        /**
         * Adds an aspect value to the label under construction.
         * Adds an error if the value is not consistent with the graph role.
         * Consistency with existing aspects is not tested.
         * @param aspect the value to be added
         */
        public Builder addAspect(Aspect aspect) {
            this.aspects.add(aspect);
            boolean notForNode = !aspect.isForNode(this.role);
            boolean notForEdge = !aspect.isForEdge(this.role);
            if (notForNode) {
                if (notForEdge) {
                    addError("Aspect '%s' not allowed in %s", aspect,
                             roleDescription.get(this.role), this.role);
                } else {
                    this.edgeOnly = aspect;
                }
            } else if (notForEdge) {
                this.nodeOnly = aspect;
            }
            if (this.nodeOnly != null && this.edgeOnly != null) {
                addError("Conflicting aspects '%s' and '%s'", this.nodeOnly, this.edgeOnly);
            }
            return this;
        }

        /**
         * Removes all aspects satisfying a given predicate.
         * The edge-only and node-only aspects are not recomputed.
         */
        public Builder removeAspects(Predicate<Aspect> test) {
            this.aspects.removeIf(test);
            return this;
        }

        /** The aspects collected so far. */
        private final List<Aspect> aspects = new ArrayList<>();

        /** Sets the label text to a non-{@code null} value. */
        public Builder setInnerText(String text) {
            this.innerText = text;
            if (text.length() > 0 && this.nodeOnly != null) {
                addError("Aspect %s cannot have label text %s", this.nodeOnly, text);
            }
            return this;
        }

        /**
         * Label text; {@code null} if not set, in which case the built label
         * gets an empty inner text.
         */
        private @Nullable String innerText;

        /** Adds an error to the errors of the label under construction. */
        Builder addError(String message, Object... args) {
            this.errors.add(message, args);
            return this;
        }

        /** Errors detected while building the label. */
        private final FormatErrorSet errors = new FormatErrorSet();

        /** Edge-only aspect value seen so far, if any. */
        private @Nullable Aspect edgeOnly;

        /** Node-only aspect value seen so far, if any. */
        private @Nullable Aspect nodeOnly;

        /** Returns the graph role for which the label is intended. */
        public GraphRole getGraphRole() {
            return this.role;
        }

        /** The graph role for which the label is intended. */
        private final GraphRole role;

        /** Assembles the collected content into an immutable aspect label. */
        public AspectLabel build() {
            return new AspectLabel(this);
        }
    }
}
