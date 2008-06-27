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
 * $Id: AbstrSimulationProperties.java,v 1.3 2008-02-05 13:28:22 rensink Exp $
 */
package groove.abs;

import groove.util.Property;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** A set of properties for abstract simulation 
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrSimulationProperties extends Properties {

	/** The precision set. */
	public int getPrecision () {
		String p = getProperty(PRECISION_KEY);
		return Integer.parseInt(p);
	}
	
	/** The radius set. */
	public int getRadius() {
		String p = getProperty(RADIUS_KEY);
		return Integer.parseInt(p);
	}
	
	/** Value for the symmetry reduction. */
	public boolean getSymmetryReduction () {
		String p = getProperty(SYMRED_KEY);
		return Boolean.parseBoolean(p);
	}
	
	/** Value for the precision. */
	public Abstraction.LinkPrecision getLinksPrecision() {
		String p = getProperty(LINKPRECISION_KEY).toUpperCase();
		return Enum.valueOf(Abstraction.LinkPrecision.class, p);
	}
	
	/** Value for the max incidence. */
	public int getMaxIncidence() {
		String p = getProperty(MAXINCIDENCE_KEY);
		return Integer.parseInt(p);
	}
	
	/** */
	public AbstrSimulationProperties () {
		setProperty(PRECISION_KEY, PRECISION_DEFAULT_VALUE.toString());
		setProperty(RADIUS_KEY, RADIUS_DEFAULT_VALUE.toString());
		setProperty(MAXINCIDENCE_KEY, MAXINCIDENCE_DEFAULT_VALUE.toString());
		setProperty(SYMRED_KEY, SYMRED_DEFAULT_VALUE.toString());
		setProperty(LINKPRECISION_KEY, LINKPRECISION_DEFAULT_VALUE.toString());
	}
	
	/** The precision of a simulation */
	static public final String PRECISION_KEY = "precision";
	/** The radius of a simulation */
	static public final String RADIUS_KEY = "radius";
	/** Do or not symmetry reduction */
	static public final String SYMRED_KEY = "symmetry reduction";
	/** Precision of links */
	static public final String LINKPRECISION_KEY = "precision for edges";
	/** Maximal incidence. */
	static public final String MAXINCIDENCE_KEY = "maximal incidence";
	
	/** */
	static public final Integer PRECISION_DEFAULT_VALUE = 1;
	/** */
	static public final Integer RADIUS_DEFAULT_VALUE = 1;
	/** */
	static public final Integer MAXINCIDENCE_DEFAULT_VALUE = 5;
	/** */
	static public final Boolean SYMRED_DEFAULT_VALUE = true;
	/** */
	static public final Abstraction.LinkPrecision LINKPRECISION_DEFAULT_VALUE = Abstraction.LinkPrecision.HIGH;
	
	
	/** Default values for the properties. */
	static public final Map<String,Property<String>> DEFAULT_KEYS;
	
	static {
		Map<String,Property<String>> defaultKeys = new LinkedHashMap<String,Property<String>>(5);
		defaultKeys.put(PRECISION_KEY, new Property.IsPositiveInteger("Should be a natural number.", false));
		defaultKeys.put(RADIUS_KEY, new Property.IsPositiveInteger("Should be a natural number.", false));
		defaultKeys.put(MAXINCIDENCE_KEY, new Property.IsPositiveInteger("Should be a natural number.", false));
		defaultKeys.put(SYMRED_KEY, new Property.IsBoolean("Should be \"true\" or \"false\".", false));
		defaultKeys.put(LINKPRECISION_KEY, new Property.IsEnumValue(Abstraction.LinkPrecision.class, false));
		DEFAULT_KEYS = Collections.unmodifiableMap(defaultKeys);
	}

	/** Updates the properties values according to some map defining new values.
	 * @param newValues
	 */
	public void update(Map<String, String> newValues) {
		for (Map.Entry<String, String> entry : newValues.entrySet()) {
			Object old = this.setProperty(entry.getKey(), entry.getValue());
			assert old != null : "Problem : the property " + entry.getKey() + " didn't exist.";
 		}
	}
	
}
