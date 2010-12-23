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
 * $Id: AbstractAspect.java,v 1.16 2008-02-05 13:28:32 rensink Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of an aspect, providing all functionality.
 * Implementers should only statically call {@link #addNodeValue(AspectValue)}
 * and {@link #addEdgeValue(AspectValue)}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractAspect extends Aspect {
    /**
     * Constructs an aspect with a given name and an initially empty set of
     * aspect values.
     * @param name the name of the aspect
     */
    protected AbstractAspect(String name) {
        this.name = name;
    }

    /**
     * Adds an {@link AspectValue} to the values of this aspect.
     * @param name the name of the new aspect value
     * @throws FormatException if <code>name</code> is an already existing
     *         aspect value name. The actual aspect value instance is created by
     *         {@link #createValue(String)}.
     */
    protected AspectValue addValue(String name) throws FormatException {
        AspectValue result = createValue(name);
        addNodeValue(result);
        addEdgeValue(result);
        return result;
    }

    /**
     * Adds an {@link AspectValue} to the node values of this aspect.
     * @param name the name of the new aspect value
     * @throws FormatException if <code>name</code> is an already existing
     *         aspect value name. The actual aspect value instance is created by
     *         {@link #createValue(String)}.
     */
    protected AspectValue addNodeValue(String name) throws FormatException {
        AspectValue result = createValue(name);
        addNodeValue(result);
        return result;
    }

    /**
     * Adds an {@link AspectValue} to the edge values of this aspect.
     * @param name the name of the new aspect value
     * @throws FormatException if <code>name</code> is an already existing
     *         aspect value name. The actual aspect value instance is created by
     *         {@link #createValue(String)}.
     */
    protected AspectValue addEdgeValue(String name) throws FormatException {
        AspectValue result = createValue(name);
        addEdgeValue(result);
        return result;
    }

    /** Adds a value to the set of allowed node aspect values. */
    void addNodeValue(AspectValue value) {
        if (!value.getAspect().equals(this)) {
            throw new IllegalArgumentException("Aspect value "
                + value.getName() + " does not belong to aspect " + this);
        }
        this.nodeValues.add(value);
        this.allValues.add(value);
    }

    /** Adds a value to the set of allowed edge aspect values. */
    void addEdgeValue(AspectValue value) {
        if (!value.getAspect().equals(this)) {
            throw new IllegalArgumentException("Aspect value "
                + value.getName() + " does not belong to aspect " + this);
        }
        this.edgeValues.add(value);
        this.allValues.add(value);
    }

    /**
     * This implementation returns the internally stored set of aspects.
     */
    @Override
    public Set<AspectValue> getValues() {
        return Collections.unmodifiableSet(this.allValues);
    }

    /**
     * This implementation returns the internally stored set of aspects.
     */
    @Override
    public Set<AspectValue> getNodeValues() {
        return Collections.unmodifiableSet(this.nodeValues);
    }

    /**
     * This implementation returns the internally stored set of aspects.
     */
    @Override
    public Set<AspectValue> getEdgeValues() {
        return Collections.unmodifiableSet(this.edgeValues);
    }

    /**
     * Returns the name of the aspect.
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Factory method for aspect values. This implementation returns an
     * {@link AspectValue}.
     * @param name the name of the new aspect value
     * @return an aspect value such that
     *         <code>result.getAspect().equals(this))</code> and
     *         <code>result.getName().equals(name)</code>
     * @throws FormatException if <code>name</code> is the name of an already
     *         existing aspect value
     */
    protected AspectValue createValue(String name) throws FormatException {
        return new AspectValue(this, name, false);
    }

    /**
     * Compares two non-<code>null</code>aspect values and returns the most
     * demanding, i.e., the value of the two that overrules the other. Throws a
     * {@link FormatException} if there is no preference. This implementation
     * throws a {@link FormatException} always.
     * @param value1 the first aspect value to be compared; not
     *        <code>null</code>
     * @param value2 the second aspect value to be compared; not
     *        <code>null</code>
     * @return the value of <code>value1</code> and <code>value2</code> that
     *         overrules the other (according to this aspect)
     * @throws FormatException if <code>value1</code> and <code>value2</code>
     *         cannot be ordered
     */
    protected AspectValue getMaxValue(AspectValue value1, AspectValue value2)
        throws FormatException {
        if (value1 == null || value2 == null) {
            throw new FormatException("Illegal null aspect value");
        } else if (value1.equals(value2)) {
            return value1;
        } else {
            throw new FormatException(
                "Incompatible values '%s' and '%s' for aspect '%s'", value1,
                value2, value1.getAspect());
        }
    }

    /**
     * The name of this aspect.
     */
    private final String name;
    /**
     * The internally stored set of node aspect values.
     */
    private final Set<AspectValue> nodeValues = new HashSet<AspectValue>();
    /**
     * The internally stored set of edge aspect values.
     */
    private final Set<AspectValue> edgeValues = new HashSet<AspectValue>();
    /**
     * The internally stored set of all aspect values.
     * @invariant allValues = nodeValues \cup edgeValues
     */
    private final Set<AspectValue> allValues = new HashSet<AspectValue>();
}
