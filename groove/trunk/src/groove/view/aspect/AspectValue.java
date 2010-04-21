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
 * $Id: AspectValue.java,v 1.11 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.view.FormatException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing values of a given aspect. Aspect values are distinguished
 * by name, which should therefore be globally distinct. This is checked at
 * construction time. The class has functionality to statically retrieve aspect
 * values by name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectValue implements Comparable<AspectValue> {
    /**
     * Creates a new aspect value, for a given aspect and with a given name.
     * Throws an exception if an aspect value with the same name exists already.
     * @param aspect the aspect for which this is a value
     * @param name the name of the aspect value.
     * @throws groove.view.FormatException if the value name is already used
     */
    public AspectValue(Aspect aspect, String name) throws FormatException {
        this.aspect = aspect;
        this.name = name;
        this.incompatibles = new HashSet<AspectValue>();
    }

    /**
     * Copies a given aspect value. This is a local constructor, not to be
     * invoked directly.
     * @param original the aspect value that we copy.
     */
    AspectValue(AspectValue original) {
        this.aspect = original.getAspect();
        this.name = original.getName();
        this.incompatibles = original.getIncompatibles();
        this.sourceToEdge = original.sourceToEdge();
        this.targetToEdge = original.targetToEdge();
        this.edgeToSource = original.edgeToSource();
        this.edgeToTarget = original.edgeToTarget();
        this.labelParser = original.getLabelParser();
        // this.freeText = freeText;
    }

    /**
     * Returns the current value of aspect.
     */
    public Aspect getAspect() {
        return this.aspect;
    }

    /**
     * Returns the name of the aspect value. The name uniquely identifies not
     * just the value itself, but also the aspect.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the prefix of the aspect value. The prefix consists of the name
     * followed by the separator.
     * @see #getName()
     * @see Aspect#VALUE_SEPARATOR
     */
    public String getPrefix() {
        return this.name + VALUE_SEPARATOR;
    }

    /**
     * Indicates if this aspect value must be the last in a sequence. This is
     * the case if and only if it has a label parser.
     */
    public final boolean isLast() {
        return this.labelParser != null;
    }

    /**
     * Returns the label parser of this aspect value, if any.
     */
    public final LabelParser getLabelParser() {
        return this.labelParser;
    }

    /**
     * Assigns a label parser to this aspect value.
     */
    final void setLabelParser(LabelParser labelParser) {
        this.labelParser = labelParser;
    }

    /**
     * Returns the inferred edge value for an {@link AspectEdge} in case the
     * source node has this value.
     */
    public AspectValue sourceToEdge() {
        return this.sourceToEdge;
    }

    /**
     * Sets an inferred edge value for an {@link AspectEdge} in case the source
     * node has this value.
     */
    void setSourceToEdge(AspectValue inferredValue) {
        assert inferredValue.getAspect() == getAspect() : String.format(
            "Inferred value %s should be of same aspect as premisse %s",
            inferredValue, this);
        this.sourceToEdge = inferredValue;
    }

    /**
     * Returns the inferred edge value for an {@link AspectEdge} in case the
     * target node has this value.
     */
    public AspectValue targetToEdge() {
        return this.targetToEdge;
    }

    /**
     * Sets an inferred edge value for an {@link AspectEdge} in case the target
     * node has this value.
     */
    void setTargetToEdge(AspectValue inferredValue) {
        assert inferredValue.getAspect() == getAspect() : String.format(
            "Inferred value %s should be of same aspect as premisse %s",
            inferredValue, this);
        this.targetToEdge = inferredValue;
    }

    /**
     * Returns the inferred value for the source {@link AspectNode} of an edge
     * with this value.
     */
    public AspectValue edgeToSource() {
        return this.edgeToSource;
    }

    /**
     * Sets an inferred value for the source {@link AspectNode} of an edge with
     * this value.
     */
    void setEdgeToSource(AspectValue inferredValue) {
        assert inferredValue.getAspect() == getAspect() : String.format(
            "Inferred value %s should be of same aspect as premisse %s",
            inferredValue, this);
        this.edgeToSource = inferredValue;
    }

    /**
     * Returns the inferred value for the target {@link AspectNode} of an edge
     * with this value.
     */
    public AspectValue edgeToTarget() {
        return this.edgeToTarget;
    }

    /**
     * Sets an inferred value for the target {@link AspectNode} of an edge with
     * this value.
     */
    void setEdgeToTarget(AspectValue inferredValue) {
        assert inferredValue.getAspect() == getAspect() : String.format(
            "Inferred value %s should be of same aspect as premisse %s",
            inferredValue, this);
        this.edgeToTarget = inferredValue;
    }

    /**
     * Indicates if another aspect value (of another aspect) is incompatible
     * with this one.
     */
    public boolean isCompatible(AspectValue other) {
        return other == null || !this.incompatibles.contains(other)
            && !other.incompatibles.contains(this);
    }

    /**
     * Adds an incompatibility with a value of another aspect.
     * @param other the incompatible value
     */
    void setIncompatible(AspectValue other) {
        assert other.getAspect() != getAspect() : String.format(
            "Incompatible values %s and %s are of the same aspect", this, other);
        this.incompatibles.add(other);
    }

    /**
     * Adds an incompatibility with all values of another aspect.
     * @param other the incompatible aspect
     */
    void setIncompatible(Aspect other) {
        for (AspectValue value : other.getValues()) {
            setIncompatible(value);
        }
    }

    /**
     * Returns the set of aspect values incompatible with this one.
     */
    Set<AspectValue> getIncompatibles() {
        return this.incompatibles;
    }

    /**
     * Indicates if this aspect value is singular. Being singular means that the
     * value automatically removes all others.
     * @return {@code true} if the aspect value is singular
     */
    final boolean isSingular() {
        return this.singular;
    }

    /**
     * Sets the aspect value to singular.
     * @see #isSingular()
     */
    final void setSingular() {
        this.singular = true;
    }

    /** Indicates if this aspect value may occur on nodes. */
    public boolean isNodeValue() {
        return this.aspect.getNodeValues().contains(this);
    }

    /** Indicates if this aspect value may occur on edges. */
    public boolean isEdgeValue() {
        return this.aspect.getEdgeValues().contains(this);
    }

    /**
     * Tests for equality by comparing the names.
     * @see #getName()
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AspectValue
            && ((AspectValue) obj).getName().equals(this.name);
    }

    /**
     * Returns the hash code of the name.
     * @see #getName()
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Returns the name of the aspect.
     * @see #getName()
     */
    @Override
    public String toString() {
        return getName();
    }

    /** Aspect values are compared by name. */
    @Override
    public int compareTo(AspectValue o) {
        return getName().compareTo(o.getName());
    }

    /**
     * The aspect that this value belongs to.
     */
    private final Aspect aspect;
    /**
     * The name of this value.
     */
    private final String name;
    // /** Flag indicating if this aspect value can have free text as label. */
    // private final boolean freeText;
    /**
     * Inferred edge aspect value (of the same aspect) if this value is in the
     * source node.
     */
    private AspectValue sourceToEdge;
    /**
     * Inferred edge aspect value (of the same aspect) if this value is in the
     * target node.
     */
    private AspectValue targetToEdge;
    /**
     * Inferred source node aspect value (of the same aspect) if this value is
     * in an edge.
     */
    private AspectValue edgeToSource;
    /**
     * Inferred target node aspect value (of the same aspect) if this value is
     * in an edge.
     */
    private AspectValue edgeToTarget;
    /**
     * Set of aspect values, possibly of other aspects, that are incompatible
     * with this one.
     */
    private final Set<AspectValue> incompatibles;
    /** Optional label parser of this aspect value. */
    private LabelParser labelParser;
    /**
     * Flag indicating if this aspect value is singular, meaning that
     */
    private boolean singular;

    /**
     * Returns the aspect value associated with a given name, if any. Returns
     * <code>null</code> if there is no value associated.
     * @param name the name for which we want the corresponding aspect value.
     */
    public static AspectValue getValue(String name) {
        Aspect.getAllAspects();
        return getValueMap().get(name);
    }

    /** Returns an unmodifiable view on the registered value names. */
    public static Set<String> getValueNames() {
        Aspect.getAllAspects();
        return getValueMap().keySet();
    }

    /** Returns the register of aspect value names. */
    private static Map<String,AspectValue> getValueMap() {
        // initialise the value map, if necessary
        if (valueMap == null) {
            valueMap = new HashMap<String,AspectValue>();
            for (Aspect aspect : Aspect.getAllAspects()) {
                for (AspectValue value : aspect.getValues()) {
                    String name = value.getName();
                    AspectValue previous = valueMap.put(value.getName(), value);
                    assert previous == null : String.format("Aspect value name "
                        + name + " already used for " + previous.getAspect());
                }
            }
        }
        return valueMap;
    }

    /** The internally kept register of aspect value names. */
    private static Map<String,AspectValue> valueMap;
}
