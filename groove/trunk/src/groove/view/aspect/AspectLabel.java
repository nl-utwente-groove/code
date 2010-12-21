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
     * Constructs an empty aspect map.
     */
    public AspectLabel() {
        // empty
    }

    /** Constructs a copy of a given aspect map. */
    private AspectLabel(AspectLabel other) {
        this.aspects.addAll(other.aspects);
        this.innerText = other.innerText;
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
    void addValue(AspectValue value) {
        testFixed(false);
        this.aspects.add(value);
    }

    /** Returns a clone of this map. */
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
    public final Collection<AspectValue> getValues() {
        return Collections.unmodifiableCollection(this.aspects);
    }

    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final List<AspectValue> aspects = new ArrayList<AspectValue>();

    /** Sets the label text. */
    void setInnerText(String text) {
        testFixed(false);
        this.innerText = text;
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
    /** Pre-computed hash code; if not 0, the label is fixed. */
}
