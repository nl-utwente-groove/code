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
 * $Id: AspectMap.java,v 1.4 2008-01-30 09:31:32 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractLabel;
import groove.graph.GraphRole;
import groove.graph.LabelKind;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Label storing a set of aspect values and an inner text.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectLabel extends AbstractLabel implements Cloneable {
    /**
     * Constructs an initially empty label, for a graph with a particular role.
     */
    public AspectLabel(GraphRole role) {
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
    public String text() {
        setFixed();
        return toString();
    }

    @Override
    public LabelKind getKind() {
        return LabelKind.parse(getInnerText()).one();
    }

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (Aspect value : this.aspects) {
            result.append(value.toString());
        }
        // append the label text, if any
        if (this.innerText != null) {
            result.append(this.innerText);
        }
        return result.toString();
    }

    /**
     * Adds an aspect value to the label.
     * Consistency with existing values is not tested.
     * @param value the value to be added
     */
    void addAspect(Aspect value) throws FormatException {
        testFixed(false);
        this.aspects.add(value);
        boolean notForNode = !value.getKind().isForNode(this.role);
        boolean notForEdge = !value.getKind().isForEdge(this.role);
        if (notForNode) {
            if (notForEdge) {
                throw new FormatException("Aspect %s not allowed", value,
                    this.role);
            }
            this.edgeOnly = value;
        } else if (notForEdge) {
            this.nodeOnly = value;
        }
        if (this.nodeOnly != null && this.edgeOnly != null) {
            throw new FormatException("Conflicting aspects %s and %s",
                this.nodeOnly, this.edgeOnly);
        }
    }

    /** Returns an as yet unfixed clone of this label. */
    @Override
    public AspectLabel clone() {
        return new AspectLabel(this);
    }

    /** Tests if the aspects and text of this object equal those of another. */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof AspectLabel
            && equalsAspects((AspectLabel) obj)
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
        boolean result =
            this.innerText == null ? other.innerText == null
                    : this.innerText.equals(other.innerText);
        return result;
    }

    /** Returns the list of aspects in this label. */
    public List<Aspect> getAspects() {
        return this.aspects;
    }

    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final List<Aspect> aspects = new ArrayList<Aspect>();

    /** 
     * Indicates whether this label is only suited for edges.
     * This is the case if either it contains an aspect that is not
     * suited for nodes, or the label text is non-empty.
     */
    public final boolean isEdgeOnly() {
        return this.edgeOnly != null || this.innerText != null
            && this.innerText.length() > 0;
    }

    /** 
     * Indicates whether this label is only suited for nodes.
     * This is the case if either it contains an aspect that is not suited
     * for edges, or if the label text is empty and the label is not edge-only.
     */
    public final boolean isNodeOnly() {
        return this.nodeOnly != null || this.edgeOnly == null
            && this.innerText != null && this.innerText.length() == 0;
    }

    /** Edge-only aspect value in this label, if any. */
    private Aspect edgeOnly;
    /** Node-only aspect value in this label, if any. */
    private Aspect nodeOnly;

    /** 
     * Sets the label text to a non-{@code null} value.
     * This fixes the label, so that no aspect values can be added any more. 
     */
    void setInnerText(String text) throws FormatException {
        testFixed(false);
        this.innerText = text;
        if (text.length() > 0 && this.nodeOnly != null) {
            throw new FormatException("Aspect %s cannot have label text %s",
                this.nodeOnly, text);
        }
        setFixed();
    }

    /**
     * Returns the label text of this aspect map. The label text may be {@code
     * null} if the associated aspect element is a node.
     */
    String getInnerText() {
        return this.innerText;
    }

    /** Label text; may be {@code null} if the associated element is a node. */
    private String innerText;

    /** The graph role for which this label is intended to be used. */
    private final GraphRole role;

    /** Label used for parent edges (between quantifier nodes). */
    public static final String IN_LABEL = "in";
    /** Label used for level edges (from rule nodes to quantifier nodes). */
    public static final String AT_LABEL = "at";
    /** The set of all allowed nesting labels. */
    static final Set<String> ALLOWED_LABELS = new HashSet<String>();

    static {
        ALLOWED_LABELS.add(IN_LABEL);
        ALLOWED_LABELS.add(AT_LABEL);
    }
}
