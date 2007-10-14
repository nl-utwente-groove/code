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
 * $Id: AspectValue.java,v 1.7 2007-10-14 11:17:36 rensink Exp $
 */
package groove.view.aspect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.view.FormatException;
import groove.view.LabelParser;
import static groove.view.aspect.Aspect.VALUE_SEPARATOR;

/**
 * Class implementing values of a given aspect.
 * Aspect values are distinguished by name, which should therefore be
 * globally distinct. This is checked at construction time.
 * The clas has functionality to statically retrieve aspect values by name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectValue {
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
        registerValue(this);
    }
    
    /**
     * Creates a new aspect value, for a given aspect and with a given name
     * and set of incompatible values. This is a local constructor, not to be
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
//        this.freeText = freeText;
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
//
//    /** 
//     * Indicates if labels under this aspect value can be freely formatted.
//     * If <code>false</code>, labels are parsed as regular expressions. 
//     */
//    public final boolean isFreeText() {
//		return freeText;
//	}

	/**
     * Returns the prefix of the aspect value.
     * The prefix consists of the name followed by the separator.
     * @see #getName()
     * @see #VALUE_SEPARATOR
     */
    public String getPrefix() {
        return name + VALUE_SEPARATOR;
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
     * Adds an incompatibility with a value of another aspect.
     * @param other the incompatible value
     */
    void setIncompatible(AspectValue other) {
    	assert other.getAspect() != getAspect() : String.format("Incompatible values %s and %s are of the same aspect", this, other);
    	incompatibles.add(other);
    }

    /**
     * Adds an incompatibility with all values of another aspect.
     * @param other the incompatible aspect
     */
    void setIncompatible(Aspect other) {
    	for (AspectValue value: other.getValues()) {
    		setIncompatible(value);
    	}
    }
    
    /**
     * Returns the set of aspect values incompatible with this one.
     */
    Set<AspectValue> getIncompatibles() {
    	return incompatibles;
    }
    
    /** Indicates if this aspect value may occur on nodes. */
    public boolean isNodeValue() {
    	return aspect.getNodeValues().contains(this);
    }
    
    /** Indicates if this aspect value may occur on edges. */
    public boolean isEdgeValue() {
    	return aspect.getEdgeValues().contains(this);
    }
    
    /**
     * Tests for equality by comparing the names.
     * @see #getName()
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AspectValue && ((AspectValue) obj).getName().equals(name);
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
    
    /**
     * The aspect that this value belongs to.
     */
    private final Aspect aspect;
    /**
     * The name of this value.
     */
    private final String name;
//    /** Flag indicating if this aspect value can have free text as label. */
//    private final boolean freeText;
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
    /** Optional label parser of this aspect value. */
    private LabelParser labelParser;
    
    /** 
     * Registers a new aspect value. For this to be successful, the value name must 
     * be fresh; otherwise, themathod throws a {@link FormatException}.
     * If successful, afterwards <code>getValue(value.getName())</code> will yield <code>value</code>. 
     * @param value the new aspect value
     * @throws FormatException if <code>value.getName()</code> is an already existing
     * aspect value name, as attested by {@link #getValue(String)}.
     * @see #getValue(String)
     */
    private static void registerValue(AspectValue value) throws FormatException {
        String name = value.getName();
        AspectValue previous = getValue(name);
        if (previous != null) {
            throw new FormatException("Aspect value name "+name+" already used for "+previous.getAspect());
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

    /** The internally kept register of aspect value names. */
    private static final Map<String,AspectValue> valueMap = new HashMap<String,AspectValue>();
}
