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
 * $Id$
 */
package groove.graph.aspect;

import groove.graph.GraphFormatException;

import java.util.LinkedHashMap;

/**
 * Mapping from aspects to aspect values, 
 * with added functionality for adding an element to it.
 * @author Arend Rensink
 * @version $Revision $
 */
class AspectMap extends LinkedHashMap<Aspect, AspectValue> {
	/**
	 * Adds a value to the map, if the value is consistent with the 
	 * existing values and there is not yet a value for the aspect involved.
	 * Throws and exception otherwise.
	 * @param value the value to be added
	 * @throws GraphFormatException if there is already an entry for
	 * <code>value.getAspect()</code>, or if there is already a value in
	 * the map that is incompatible with <code>value</code>
	 * @see #put(Aspect, AspectValue)
	 */
	public void add(AspectValue value) throws GraphFormatException {
    	for (AspectValue oldValue: values()) {
    		if (! oldValue.isCompatible(value)) {
    			throw new GraphFormatException("Node aspect values %s and %s are incompatible", oldValue, value);
    		}
    	}
		AspectValue oldValue = put(value.getAspect(), value);
		if (oldValue != null) {
			put(value.getAspect(), oldValue);
			throw new GraphFormatException("Aspect %s has duplicate values %s and %s", value.getAspect(), oldValue, value);
		}
	}
}
