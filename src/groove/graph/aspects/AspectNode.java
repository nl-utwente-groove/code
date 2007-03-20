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
 * $Id: AspectNode.java,v 1.1.1.1 2007-03-20 10:05:37 kastenberg Exp $
 */
package groove.graph.aspects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import groove.graph.DefaultNode;
import groove.graph.GraphFormatException;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class AspectNode extends DefaultNode implements AspectElement {
	/** Constructs an aspect node with a given number. */
    AspectNode(int nr) {
    	super(nr);
        aspectMap = new AspectMap();
        declaredAspectValues = new HashSet<AspectValue>();
    }

    /**
     * Adds an aspect value to the node, or updates an existing value.
     * @param value the aspect value to be added
     * @throws GraphFormatException if the node already has a value for <code>value.getAspect()</code>
     */
    public void setInferredValue(AspectValue value) throws GraphFormatException {
    	Aspect aspect = value.getAspect();
    	AspectValue oldValue = getAspectMap().get(value.getAspect());
    	AspectValue newValue = aspect.getMax(value, oldValue);
    	getAspectMap().put(aspect, newValue);
    }

    /**
     * Adds an aspect value to the node.
     * @param value the aspect value to be added
     * @throws GraphFormatException if the node already has a value for <code>value.getAspect()</code>
     */
    public void setDeclaredValue(AspectValue value) throws GraphFormatException {
    	getDeclaredValues().add(value);
    	setInferredValue(value);
    }
    
    public AspectValue getValue(Aspect aspect) {
    	AspectValue result = getAspectMap().get(aspect);
    	if (result == null) {
    		result = aspect.getDefaultValue();
    	}
    	return result;
    }
    
    /** For nodes, the declared and inferred aspects coincide. */
    public Collection<AspectValue> getDeclaredValues() {
    	return declaredAspectValues;
    }
    
    /**
     * Returns the value for a given aspect for an edge that has this
     * node as its source, in case the node's own value gives a way
     * to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its source,
     * or <code>null</code> if no value can be inferred.
     */
    public AspectValue getSourceToEdgeValue(Aspect aspect) {
    	AspectValue ownValue = getValue(aspect);
    	if (ownValue == null) {
    		return null;
    	} else {
    		return ownValue.sourceToEdge();
    	}
    }
    
    /**
     * Returns the value for a given aspect for an edge that has this
     * node as its target, in case the node's own value gives a way
     * to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its target,
     * or <code>null</code> if no value can be inferred.
     */
    public AspectValue getTargetToEdgeValue(Aspect aspect) {
    	AspectValue ownValue = getValue(aspect);
    	if (ownValue == null) {
    		return null;
    	} else {
    		return ownValue.targetToEdge();
    	}
    }

    /** 
     * Returns the map from aspects to aspect values for this edge,
     * lazily creating it first.
     */
    public AspectMap getAspectMap() {
    	return aspectMap;
    }
    
    /**
     * The internal map from aspects to corresponding values.
     */
    private final AspectMap aspectMap;
    /**
     * The internal map from aspects to corresponding (explicitly declared) values.
     */
    private final Set<AspectValue> declaredAspectValues;
}
