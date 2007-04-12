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

import groove.graph.Label;
import groove.util.FormatException;
import groove.util.Groove;

import java.util.Set;

/**
 * Interface for an aspect of graphs.
 * Examples of aspects are: the roles in a rule, typing information, or
 * graph condition information.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Aspect {
	/** 
	 * String used to separate the textual representation of aspect values
	 * in a label. 
	 * When the separator occurs twice in direct succession, this denotes the
	 * end of the aspect prefix.
	 */
    public static final String VALUE_SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
	/** 
	 * String used to separate the name and content of aspect values.
	 */
    public static final String CONTENT_ASSIGN = Groove.getXMLProperty("label.content.assign");
	/** 
	 * String used to separate substrings within the content of an aspect value.
	 */
    public static final String CONTENT_SEPARATOR = Groove.getXMLProperty("label.content.separator");
    

	/** Array of all known aspects. */
	public Aspect[] allAspects = { AttributeAspect.getInstance(), RuleAspect.getInstance() };

	/**
     * Returns the set of all possible aspect values (for either nodes or edges), 
     * as a set of <code>AspectValue</code>s.
     * @see #getNodeValues()
     * @see #getEdgeValues()
     */
    Set<AspectValue> getValues();

    /**
     * Returns the possible node aspect values, 
     * as a set of <code>AspectValue</code>s.
     * @see #getValues()
     */
    Set<AspectValue> getNodeValues();

    /**
     * Returns the possible edge aspect values, 
     * as a set of <code>AspectValue</code>s.
     * @see #getValues()
     */
    Set<AspectValue> getEdgeValues();
    
    /**
     * Returns the default aspect value, if any.
     */
    AspectValue getDefaultValue();
    
    /**
     * Returns the maximum value for a number of aspect values.
     * All values should be values of this aspect.
     * The maximum if the most demanding value, in the sense dictated
     * by the particular aspect. 
     * @param values the aspect values to be compared; there should be at least one.
     * @return the maximum of <code>values</code>
     * @throws IllegalArgumentException if <code>values.length == 0</code>
     * @throws FormatException if the values are incompatible
     */
    AspectValue getMax(AspectValue... values) throws FormatException;
    
    /**
     * Tests if an edge label is allowed, given a declared and an inferred aspect value
     * for this aspect.
     * @param label the label to be tested
     * @param declaredValue the declared aspect value; should be a value of this aspect
     * @param inferredValue the inferred aspect value; should be a value of this aspect
     * @throws FormatException if the label is not correct, given the aspect values
     */
    void testLabel(Label label, AspectValue declaredValue, AspectValue inferredValue) throws FormatException;
}
