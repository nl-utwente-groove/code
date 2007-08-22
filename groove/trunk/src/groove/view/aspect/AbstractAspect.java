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
 * $Id: AbstractAspect.java,v 1.7 2007-08-22 15:04:49 rensink Exp $
 */
package groove.view.aspect;

import groove.graph.Label;
import groove.view.DefaultLabelParser;
import groove.view.FormatException;
import groove.view.LabelParser;
import groove.view.RegExprLabelParser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of an aspect, provinding all functionality.
 * Implementers should only statically call {@link #addNodeValue(AspectValue)} and
 * {@link #addEdgeValue(AspectValue)}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractAspect implements Aspect {
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
     * @throws FormatException if <code>name</code> is an already existing aspect value name.
     * The actual aspect value instance is created by {@link #createValue(String)}.
     */
    AspectValue addValue(String name) throws FormatException {
        AspectValue result = createValue(name); 
        addNodeValue(result);
        addEdgeValue(result);
        return result;
    }

    /**
     * Adds an {@link AspectValue} to the node values of this aspect.
     * @param name the name of the new aspect value
     * @throws FormatException if <code>name</code> is an already existing aspect value name.
     * The actual aspect value instance is created by {@link #createValue(String)}.
     */
    AspectValue addNodeValue(String name) throws FormatException {
        AspectValue result = createValue(name); 
        addNodeValue(result);
        return result;
    }

    /**
     * Adds an {@link AspectValue} to the edge values of this aspect.
     * @param name the name of the new aspect value
     * @throws FormatException if <code>name</code> is an already existing aspect value name.
     * The actual aspect value instance is created by {@link #createValue(String)}.
     */
    AspectValue addEdgeValue(String name) throws FormatException {
        AspectValue result = createValue(name); 
        addEdgeValue(result);
        return result;
    }
    
    /** Adds a value to the set of allowed node aspect values. */
    void addNodeValue(AspectValue value) {
        if (!value.getAspect().equals(this)) {
            throw new IllegalArgumentException("Aspect value "+value.getName()+" does not belong to aspect "+this);
        }
        nodeValues.add(value);
        allValues.add(value);
    }
    
    /** Adds a value to the set of allowed edge aspect values. */
    void addEdgeValue(AspectValue value) {
        if (!value.getAspect().equals(this)) {
            throw new IllegalArgumentException("Aspect value "+value.getName()+" does not belong to aspect "+this);
        }
        edgeValues.add(value);
        allValues.add(value);
    }
    
    /**
     * This implementation returns the internally stored set of aspects.
     */
    public Set<AspectValue> getValues() {
        return Collections.unmodifiableSet(nodeValues);
    }
    
    /**
     * This implementation returns the internally stored set of aspects.
     */
    public Set<AspectValue> getNodeValues() {
        return Collections.unmodifiableSet(nodeValues);
    }
    
    /**
     * This implementation returns the internally stored set of aspects.
     */
    public Set<AspectValue> getEdgeValues() {
        return Collections.unmodifiableSet(edgeValues);
    }
    
    /**
     * Returns the name of the aspect.
     */
    @Override
    public String toString() {
        return name;
    }

    /** This default implementation returns <code>null</code> always. */
    final public AspectValue getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets a certain aspect value as default.
     * @param value the aspect value to be set as default
     * @throws IllegalArgumentException if <code>name</code> is not the name of an aspect
     * value of this aspect, or if it cannot be used both for nodes and edges.
     * @see #getDefaultValue()
     */
    protected void setDefaultValue(AspectValue value) {
        if (defaultValue != null) {
            throw new IllegalArgumentException("Default value already set, to "+defaultValue);
        }
        if (!value.getAspect().equals(this)) {
            throw new IllegalArgumentException("Aspect value "+value.getName()+" does not belong to aspect "+this);
        }
        if (!allValues.contains(value)) {
        	throw new IllegalArgumentException("Prospective default aspect value "+name+" not a legal node and edge value");
        }
        defaultValue = value;
    }
    
	/**
     * Factory method for aspect values.
     * This implementation returns an {@link AspectValue}.
     * @param name the name of the new aspect value
     * @return an aspect value such that <code>result.getAspect().equals(this))</code>
     * and <code>result.getName().equals(name)</code>
     * @throws FormatException if <code>name</code> is the name of an already existing aspect value
     */
    protected AspectValue createValue(String name) throws FormatException {
        return new AspectValue(this, name);
    }
//
//	/**
//     * Factory method for aspect values with free text labels.
//     * This implementation returns an {@link AspectValue}.
//     * @param name the name of the new aspect value
//     * @return an aspect value such that <code>result.getAspect().equals(this))</code>
//     * and <code>result.getName().equals(name)</code>
//     * @throws FormatException if <code>name</code> is the name of an already existing aspect value
//     */
//    protected AspectValue createFreeValue(String name) throws FormatException {
//        return new AspectValue(this, name);
//    }

    /**
     * Method to test the validity of an aspect value for use as a node value.
     * The method does nothing, but throws an exception if the aspect value may not
     * be used for nodes.
     * @param nodeValue the node aspect value to be tested
     */
    protected void testNodeValue(AspectValue nodeValue) {
    	if (!nodeValues.contains(nodeValue)) {
    		throw new IllegalArgumentException("Aspect value "+nodeValue+" may not be used for nodes");
    	}
    }

    /**
     * Method to test the validity of an aspect value for use as an edge value.
     * The method does nothing, but throws an exception if the aspect value may not
     * be used for edges.
     * @param edgeValue the edge aspect value to be tested
     */
    protected void testEdgeValue(AspectValue edgeValue) {
    	if (!edgeValues.contains(edgeValue)) {
    		throw new IllegalArgumentException("Aspect value '"+edgeValue+"' may not be used for edges");
    	}
    }
    
	/**
	 * Compares a number of aspect values and returns the most demanding, i.e.,
	 * the value that overrules the others. Throws a {@link FormatException}
	 * if there is no preference. <code>null</code> values are disregarded.
	 * @param values the values to be compared
	 * @return a value from <code>values</code> such that for all others,
	 * either they are <code>null</code> or <code>result = getMax(result, other)</code> 
	 * @throws FormatException if <code>getMax(value1, value2)</code>
	 * throws an exception for two non-<code>null</code> elements of <code>values</code>
	 */
	final public AspectValue getMax(AspectValue... values) throws FormatException {
		AspectValue result = null;
		for (AspectValue value: values) {
			if (result == null) {
				result = value;
			} else if (value != null){
				result = getMaxValue(result, value);
			}
		}
		return result;
	}
	
	/**
	 * Compares two non-<code>null</code>aspect values and returns the most demanding, i.e.,
	 * the value of the two that overrules the other. Throws a {@link FormatException}
	 * if there is no preference. 
	 * This implementation throws a {@link FormatException} always.
	 * @param value1 the first aspect value to be compared; not <code>null</code>
	 * @param value2 the second aspect value to be compared; not <code>null</code>
	 * @return the value of <code>value1</code> and <code>value2</code> that 
	 * overrules the other (according to this aspect)
	 * @throws FormatException if <code>value1</code> and <code>value2</code>
	 * cannot be ordered
	 */
	protected AspectValue getMaxValue(AspectValue value1, AspectValue value2) throws FormatException {
		if (value1 == value2 && value1 != null) {
			return value1;
		} else {
			throw new FormatException("Incompatible aspect values '%s' and '%s'", value1, value2);
		}
	}

	/**
	 * This default implementation never throws the exception.
	 */
	public void testLabel(Label label, AspectValue declaredValue, AspectValue inferredValue) throws FormatException {
		// does nothing
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
    /**
     * The default aspect value, if any.
     */
    private AspectValue defaultValue;
//    /** Source inference rules, as a mapping from edge aspect values to node aspect values. */
//    private final Map<AspectValue,AspectValue> sourceInference = new HashMap<AspectValue,AspectValue>();
//    /** Target inference rules, as a mapping from edge aspect values to node aspect values. */
//    private final Map<AspectValue,AspectValue> targetInference = new HashMap<AspectValue,AspectValue>();

	/** Returns a parser that turns a string into a regular expression label. */
	static LabelParser getRegExprLabelParser() {
		return REG_EXPR_PARSER;
	}

	/** 
	 * Returns a parser that turns a string into a default label, without checking
	 * for any format constraints.
	 */
	static LabelParser getFreeLabelParser() {
		return FREE_PARSER;
	}
	/**
	 * Instance of the regular expression parser. 
	 */
	static private final LabelParser REG_EXPR_PARSER = new RegExprLabelParser();
	/**
	 * Instance of the default label parser. 
	 */
	static private final LabelParser FREE_PARSER = new DefaultLabelParser();
}
