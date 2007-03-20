/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: AspectValue.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */
package groove.graph.aspects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.GraphFormatException;

/**
 * Class implementing values of a given aspect.
 * Aspect values are distinguished by name, which should therefore be
 * globally distinct. This is checked at construction time.
 * The clas has functionality to statically retrieve aspect values by name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectValue {
    /** The internally kept register of aspect value names. */
    private static final Map<String,AspectValue> valueMap = new HashMap<String,AspectValue>();
    
    /** 
     * Registers a new aspect value. For this to be successful, the value name must 
     * be fresh; otherwise, themathod throws a {@link GraphFormatException}.
     * If successful, afterwards <code>getValue(value.getName())</code> will yield <code>value</code>. 
     * @param value the new aspect value
     * @throws GraphFormatException if <code>value.getName()</code> is an already existing
     * aspect value name, as attested by {@link #getValue(String)}.
     * @see #getValue(String)
     */
    private static void registerValue(AspectValue value) throws GraphFormatException {
        String name = value.getName();
        AspectValue previous = getValue(name);
        if (previous != null) {
            throw new GraphFormatException("Aspect value name "+name+" already used for "+previous.getAspect());
        }
        valueMap.put(value.getName(), value);
    }
    
    /**
     * Returns the aspect value associated with a given name, if any.
     * Returns <code>null</code> if there is no value associated.
     * @param name the name for which we want the corresponding aspect value.
     */
    public static AspectValue getValue(String name) {
        return valueMap.get(name);
    }
    
    /**
     * Creates a new aspect value, for a given aspect and with a given name.
     * Throws an exception if an aspect value with the same name exists already.
     * @param aspect the aspect for which this is a value
     * @param name the name of the aspect value.
     * @throws groove.graph.GraphFormatException if the value name is already used
     */
    public AspectValue(Aspect aspect, String name) throws GraphFormatException {
        this.aspect = aspect;
        this.name = name;
        this.incompatibles = new HashSet<AspectValue>();
        registerValue(this);
    }
    
    /**
     * Returns the current value of aspect.
     */
    public Aspect getAspect() {
        return aspect;
    }
    
    /**
     * Returns the name of the aspect value.
     * The name uniquely identifies not just the value itself, but also the aspect.
     */
    public String getName() {
        return name;
    }

    /** 
     * Returns the inferred edge value for an {@link AspectEdge}
     * in case the source node has this value.
     */
    public AspectValue sourceToEdge() {
    	return sourceToEdge;
    }

    /** 
     * Sets an inferred edge value for an {@link AspectEdge}
     * in case the source node has this value.
     */
    void setSourceToEdge(AspectValue inferredValue) {
    	assert inferredValue.getAspect() == getAspect() : String.format("Inferred value %s should be of same aspect as premisse %s", inferredValue, this);
    	sourceToEdge = inferredValue;
    }

    /** 
     * Returns the inferred edge value for an {@link AspectEdge}
     * in case the target node has this value.
     */
    public AspectValue targetToEdge() {
    	return targetToEdge;
    }

    /** 
     * Sets an inferred edge value for an {@link AspectEdge}
     * in case the target node has this value.
     */
    void setTargetToEdge(AspectValue inferredValue) {
    	assert inferredValue.getAspect() == getAspect() : String.format("Inferred value %s should be of same aspect as premisse %s", inferredValue, this);
    	targetToEdge = inferredValue;
    }

    /** 
     * Returns the inferred value for the source {@link AspectNode}
     * of an edge with this value.
     */
    public AspectValue edgeToSource() {
    	return edgeToSource;
    }

    /** 
     * Sets an inferred value for the source {@link AspectNode}
     * of an edge with this value.
     */
    void setEdgeToSource(AspectValue inferredValue) {
    	assert inferredValue.getAspect() == getAspect() : String.format("Inferred value %s should be of same aspect as premisse %s", inferredValue, this);
    	edgeToSource = inferredValue;
    }

    /** 
     * Returns the inferred value for the target {@link AspectNode}
     * of an edge with this value.
     */
    public AspectValue edgeToTarget() {
    	return edgeToTarget;
    }

    /** 
     * Sets an inferred value for the target {@link AspectNode}
     * of an edge with this value.
     */
    void setEdgeToTarget(AspectValue inferredValue) {
    	assert inferredValue.getAspect() == getAspect() : String.format("Inferred value %s should be of same aspect as premisse %s", inferredValue, this);
    	edgeToTarget = inferredValue;
    }

    /** 
     * Indicates if another aspect value (of another aspect) is
     * incompatible with this one.
     */
    public boolean isCompatible(AspectValue other) {
    	return other == null || ! incompatibles.contains(other) && ! other.incompatibles.contains(this);
    }
    
    /**
     * Adds an incompatibility with an aspectvalue of another aspect.
     * @param other
     */
    void setIncompatible(AspectValue other) {
    	assert other.getAspect() != getAspect() : String.format("Incompatible values %s and %s are of the same aspect", this, other);
    	incompatibles.add(other);
    }
    
    /**
     * Tests for equality by comparing the names.
     * @see #getName()
     */
    public boolean equals(Object obj) {
        return obj instanceof AspectValue && ((AspectValue) obj).getName().equals(name);
    }

    /**
     * Returns the hash code of the name.
     * @see #getName()
     */
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Returns the name of the aspect.
     * @see #getName()
     */
    public String toString() {
        return getName();
    }
    
    /**
     * The aspect that this value belongs to.
     */
    private final Aspect aspect;
    /**
     * The name of this value.
     */
    private final String name;
    /** Inferred edge aspect value (of the same aspect) if this value is in the source node. */
    private AspectValue sourceToEdge;
    /** Inferred edge aspect value (of the same aspect) if this value is in the target node. */
    private AspectValue targetToEdge;
    /** Inferred source node aspect value (of the same aspect) if this value is in an edge. */
    private AspectValue edgeToSource;
    /** Inferred target node aspect value (of the same aspect) if this value is in an edge. */
    private AspectValue edgeToTarget;
    /** Set of aspect values, possibly of other aspects, that are incompatible with this one. */
    private final Set<AspectValue> incompatibles;
}
