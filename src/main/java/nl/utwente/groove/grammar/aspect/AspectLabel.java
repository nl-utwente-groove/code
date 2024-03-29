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
import java.util.Iterator;
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
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Label storing a set of aspect values and an inner text.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class AspectLabel extends ALabel implements Fixable {
    /**
     * Constructs an initially empty label, for a graph with a particular role.
     */
    public AspectLabel(GraphRole role) {
        assert role.inGrammar();
        this.role = role;
    }

    /** Constructs an as yet unfixed copy of a given aspect label. */
    private AspectLabel(AspectLabel other) {
        this.role = other.role;
        this.aspects.addAll(other.aspects);
        this.innerText = other.innerText;
        this.edgeOnly = other.edgeOnly;
        this.nodeOnly = other.nodeOnly;
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

    @Override
    public boolean isFixed() {
        return this.fixable.isFixed();
    }

    @Override
    public boolean setFixed() {
        boolean result = this.fixable.setFixed();
        if (result && this.innerText == null) {
            this.innerText = "";
            if (this.errors.isEmpty()) {
                this.errors = FormatErrorSet.EMPTY;
            } else {
                this.errors.setFixed();
            }
            hashCode();
        }
        return result;
    }

    private final DefaultFixable fixable = new DefaultFixable();

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toParsableString() {
        //setFixed();
        StringBuffer result = new StringBuffer();
        for (Aspect value : this.aspects) {
            result.append(value.toString());
        }
        // append the label text, if any
        if (this.innerText != null) {
            result.append(getInnerText());
        }
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
     * Adds an aspect value to the label.
     * Adds an error if the value is not consistent with the graph role.
     * Consistency with existing aspects is not tested.
     * @param aspect the value to be added
     */
    public void addAspect(Aspect aspect) {
        testFixed(false);
        this.aspects.add(aspect);
        boolean notForNode = !aspect.isForNode(getGraphRole());
        boolean notForEdge = !aspect.isForEdge(getGraphRole());
        if (notForNode) {
            if (notForEdge) {
                addError("Aspect '%s' not allowed in %s", aspect, roleDescription.get(this.role),
                         this.role);
            } else {
                this.edgeOnly = aspect;
            }
        } else if (notForEdge) {
            this.nodeOnly = aspect;
        }
        if (this.nodeOnly != null && this.edgeOnly != null) {
            addError("Conflicting aspects '%s' and '%s'", this.nodeOnly, this.edgeOnly);
        }
    }

    /** Returns an as yet unfixed clone of this label. */
    @Override
    public AspectLabel clone() {
        return new AspectLabel(this);
    }

    /**
     * Returns an aspect label obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public AspectLabel relabel(TypeLabel oldLabel, TypeLabel newLabel, SortMap typing) {
        assert isFixed();
        AspectLabel result = this;
        boolean isNew = false;
        List<Aspect> newAspects = new ArrayList<>();
        for (Aspect aspect : getAspects()) {
            Aspect newAspect = aspect.relabel(oldLabel, newLabel, typing);
            isNew |= newAspect != aspect;
            newAspects.add(newAspect);
        }
        if (isNew) {
            result = new AspectLabel(getGraphRole());
            for (Aspect newAspect : newAspects) {
                result.addAspect(newAspect);
            }
            result.setFixed();
        }
        return result;
    }

    /** Tests if the aspects and text of this object equal those of another. */
    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj instanceof AspectLabel label && equalsAspects(label)
            && equalsText((AspectLabel) obj);
    }

    /** Computes a hash code value. */
    @Override
    protected int computeHashCode() {
        int result = this.aspects.hashCode();
        if (this.innerText != null) {
            result += this.innerText.hashCode();
        }
        return result;
    }

    /** Indicates if the aspects in this map equal those in another map. */
    public boolean equalsAspects(AspectLabel other) {
        return this.aspects.equals(other.aspects);
    }

    /**
     * Indicates if the {@link #innerText} of this map equals that of another.
     */
    private boolean equalsText(AspectLabel other) {
        var inner = this.innerText;
        boolean result = inner == null
            ? other.innerText == null
            : inner.equals(other.innerText);
        return result;
    }

    /** Returns the list of aspects in this label. */
    public List<Aspect> getAspects() {
        return this.aspects;
    }

    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final List<Aspect> aspects = new ArrayList<>();

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
        return this.edgeOnly != null || this.innerText != null && this.innerText.length() > 0;
    }

    /**
     * Indicates whether this label is only suited for nodes.
     * This is the case if either it contains an aspect that is not suited
     * for edges, or if the label text is empty and the label is not edge-only.
     */
    public final boolean isNodeOnly() {
        return this.nodeOnly != null
            || this.edgeOnly == null && this.innerText != null && this.innerText.length() == 0;
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
    private @Nullable Aspect edgeOnly;

    /** Node-only aspect value in this label, if any. */
    private @Nullable Aspect nodeOnly;

    /**
     * Sets the label text to a non-{@code null} value.
     * This fixes the label, so that no aspect values can be added any more.
     */
    public void setInnerText(String text) {
        testFixed(false);
        this.innerText = text;
        if (text.length() > 0 && this.nodeOnly != null) {
            addError("Aspect %s cannot have label text %s", this.nodeOnly, text);
        }
        setFixed();
    }

    /**
     * Returns the label text of this aspect label.
     * Calling this method fixes the label.
     * Guaranteed to be non-{@code null}.
     */
    public String getInnerText() {
        setFixed();
        var result = this.innerText;
        assert result != null;
        return result;
    }

    /** Label text; may be {@code null} if the associated element is a node. */
    private @Nullable String innerText;

    /** The graph role for which this label is intended to be used. */
    private final GraphRole role;

    /** Adds an error to the errors of this label. */
    void addError(String message, Object... args) {
        testFixed(false);
        this.errors.add(message, args);
    }

    /** Indicates if there are any errors in this label. */
    public boolean hasErrors() {
        testFixed(true);
        return !this.errors.isEmpty();
    }

    /** Returns the (possibly empty) list of errors in this label. */
    public FormatErrorSet getErrors() {
        testFixed(true);
        return this.errors;
    }

    /** List of errors detected while building this label. */
    private FormatErrorSet errors = new FormatErrorSet();

    /**
     * Returns a fixed copy of this label minus any {@link AspectKind#LITERAL}
     * aspect.
     */
    public AspectLabel unwrap() {
        AspectLabel result = new AspectLabel(this);
        Iterator<Aspect> aspects = result.getAspects().iterator();
        while (aspects.hasNext()) {
            if (aspects.next().getKind() == AspectKind.LITERAL) {
                aspects.remove();
            }
        }
        result.setFixed();
        return result;
    }

    /** The set of all allowed nesting labels. */
    private static final Map<GraphRole,String> roleDescription = new EnumMap<>(GraphRole.class);
    static {
        roleDescription.put(GraphRole.HOST, "host graph");
        roleDescription.put(GraphRole.TYPE, "type graph");
        roleDescription.put(GraphRole.RULE, "rule graph");
    }
}
