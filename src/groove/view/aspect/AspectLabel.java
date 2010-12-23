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
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Label storing a set of aspect values and an inner text.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectLabel extends AbstractLabel implements Cloneable {
    /**
     * Constructs an initially empty label.
     */
    public AspectLabel() {
        // empty
    }

    /** Constructs an as yet unfixed copy of a given aspect label. */
    private AspectLabel(AspectLabel other) {
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

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (AspectValue value : this.aspects) {
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
    void addAspect(AspectValue value) throws FormatException {
        testFixed(false);
        this.aspects.add(value);
        if (!value.isNodeValue()) {
            this.edgeOnly = value;
        } else if (!value.isEdgeValue()) {
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

    /** Returns the set of declared values in this aspect map. */
    public final Collection<AspectValue> getAspects() {
        return Collections.unmodifiableCollection(this.aspects);
    }

    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final List<AspectValue> aspects = new ArrayList<AspectValue>();

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
    private AspectValue edgeOnly;
    /** Node-only aspect value in this label, if any. */
    private AspectValue nodeOnly;

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
}
