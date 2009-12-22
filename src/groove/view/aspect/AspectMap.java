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

import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.graph.DefaultLabel;
import groove.view.FormatException;
import groove.view.LabelParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Mapping from aspects to aspect values, associated with an
 * {@link AspectElement}. Also has functionality to store the label text.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectMap implements Iterable<AspectValue> {
    /** Constructs an empty aspect map. */
    public AspectMap() {
        // empty
    }

    /** Constructs a copy of a given aspect map. */
    public AspectMap(AspectMap other) {
        this.aspectMap.putAll(other.aspectMap);
        this.declaredValues.addAll(other.declaredValues);
        this.text = other.getText();
        this.hasEnd = other.hasEnd();
    }

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (AspectValue value : getDeclaredValues()) {
            result.append(AspectParser.toString(value));
        }
        // append the end marking, if any
        if (hasEnd()) {
            result.append(VALUE_SEPARATOR);
        }
        // append the label text, if any
        String label = getText();
        if (label != null) {
            result.append(label);
        }
        return result.toString();
    }

    /** Returns an iterator over the aspect values stored in this object. */
    @Override
    public Iterator<AspectValue> iterator() {
        return this.aspectMap.values().iterator();
    }

    /** Returns the aspect value for a given aspect, if any. */
    public AspectValue get(Aspect key) {
        return this.aspectMap.get(key);
    }

    /** Returns the set of aspect values in this map. */
    public Collection<AspectValue> values() {
        return this.aspectMap.values();
    }

    /** Returns the number of aspect values in this map. */
    public int size() {
        return this.aspectMap.size();
    }

    /** Indicates if this map is empty. */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Adds a declared value to the map, if the value is consistent with the
     * existing values and there is not yet a value for the aspect involved.
     * Throws an exception otherwise, or if if there is already a singular value
     * in this map.
     * @param value the value to be added
     * @throws FormatException if there is already an entry for
     *         <code>value.getAspect()</code>, or if there is already a value in
     *         the map that is incompatible with <code>value</code>, or if there
     *         is already a singular value
     */
    public void addDeclaredValue(AspectValue value) throws FormatException {
        if (hasSingularValue()) {
            throw new FormatException(
                "Declared value '%s' conflicts with singular value '%s'",
                value, getSingularValue());
        } else {
            testCompatible(value);
            Aspect aspect = value.getAspect();
            AspectValue oldValue = put(aspect, value);
            if (oldValue != null) {
                if (this.declaredValues.contains(oldValue)) {
                    throw new FormatException(
                        "Duplicate declared values '%s' and '%s'", oldValue,
                        value);
                } else if (!aspect.getMax(oldValue, value).equals(value)) {
                    throw new FormatException(
                        "Declared value '%s' conflicts with inferred value '%s'",
                        value, oldValue);
                }
            }
            this.declaredValues.add(value);
            if (value.isSingular()) {
                setSingularValue(value, true);
            }
        }
    }

    /**
     * Adds an inferred value to the map, if the value is consistent with the
     * existing values. Throws an exception otherwise. Does not add the value if
     * there is already a singular value in this map.
     * @param value the value to be added
     * @throws FormatException if there is already an entry for
     *         <code>value.getAspect()</code>, or if there is already a value in
     *         the map that is incompatible with <code>value</code>
     */
    public void addInferredValue(AspectValue value) throws FormatException {
        if (hasSingularValue()) {
            // we don't add this value, but it is an error if the new
            // value is also singular
            if (value.isSingular()) {
                throw new FormatException("Two singular values '%s' and '%s'",
                    getSingularValue(), value);
            }
        } else {
            testCompatible(value);
            Aspect aspect = value.getAspect();
            AspectValue oldValue = put(aspect, value);
            if (oldValue != null) {
                AspectValue maxValue = aspect.getMax(value, oldValue);
                if (this.declaredValues.contains(oldValue)
                    && !maxValue.equals(oldValue)) {
                    throw new FormatException(
                        "Declared value '%s' conflicts with inferred value '%s'",
                        oldValue, value);
                } else {
                    put(aspect, maxValue);
                }
            }
            if (value.isSingular()) {
                setSingularValue(value, false);
            }
        }
    }

    /**
     * Tests if the aspect map contains a value that is incompatible with a
     * given new value.
     * @throws FormatException if there is an incompatible value in the aspect
     *         map
     */
    private void testCompatible(AspectValue value) throws FormatException {
        for (AspectValue oldValue : this.aspectMap.values()) {
            if (!oldValue.isCompatible(value)) {
                throw new FormatException(
                    "Aspect values '%s' and '%s' are incompatible", oldValue,
                    value);
            }
        }
    }

    /**
     * Inserts a value into the aspect map, after testing if the label text is
     * compatible with that value.
     * @return the old aspect value stored for {@code aspect}; {@code null} if
     *         there was none
     * @throws FormatException if the label text is not compatible with {@code
     *         value}
     */
    private AspectValue put(Aspect aspect, AspectValue value)
        throws FormatException {
        // check if the edge label complies with the new aspect value
        LabelParser parser = value.getLabelParser();
        if (parser != null && getText() != null) {
            parser.parse(DefaultLabel.createLabel(getText()));
        }
        return this.aspectMap.put(aspect, value);
    }

    /** Returns {@code true} if this aspect map has a singular value. */
    private boolean hasSingularValue() {
        return this.singularValue != null;
    }

    /** Returns the singular aspect value in this map, if any. */
    private AspectValue getSingularValue() {
        return this.singularValue;
    }

    /**
     * Sets a given aspect value as singular value. Removes all other aspect
     * values.
     * @param value the singular aspect value; should satisfy {@code
     *        value.isSingular()}
     * @param declared if {@code true}, the value is declared (rather than
     *        inferred)
     */
    private void setSingularValue(AspectValue value, boolean declared) {
        // clear aspect map except for singular value
        this.aspectMap.clear();
        this.aspectMap.put(value.getAspect(), value);
        // clear declared values set except for singular value
        this.declaredValues.clear();
        if (declared) {
            this.declaredValues.add(value);
        }
        this.singularValue = value;
    }

    /** The singular aspect value contained in this map, if any. */
    private AspectValue singularValue;
    /** The mapping from aspects to (declared or inferred) aspect values. */
    private final Map<Aspect,AspectValue> aspectMap =
        new LinkedHashMap<Aspect,AspectValue>();

    /** Returns the set of declared values in this aspect map. */
    public final Set<AspectValue> getDeclaredValues() {
        return Collections.unmodifiableSet(this.declaredValues);
    }

    /** The (sub)set of declared aspect values. */
    private final Set<AspectValue> declaredValues = new HashSet<AspectValue>();

    /** Sets the label text and the explicit end flag. */
    void setText(String text, boolean hasEnd) {
        this.text = text;
        this.hasEnd = hasEnd;
    }

    /**
     * Returns the label text of this aspect map. The label text may be {@code
     * null} if the associated aspect element is a node.
     */
    String getText() {
        return this.text;
    }

    /** Indicates if the original aspect text has an explicit separator. */
    boolean hasEnd() {
        return this.hasEnd;
    }

    /**
     * Flag indicating if there is an explicit separator between aspect prefixes
     * and label text.
     */
    private boolean hasEnd;
    /** Label text; may be {@code null} if the associated element is a node. */
    private String text;
}
