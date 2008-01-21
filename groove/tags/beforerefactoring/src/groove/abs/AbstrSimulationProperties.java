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
 * $Id: AbstrSimulationProperties.java,v 1.1 2007-11-28 15:35:02 iovka Exp $
 */
package groove.abs;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import groove.calc.Property;

/** A set of properties for abstract simulation */
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
	
	/** Flag for the symmetry reduction. */
	public boolean getSymmetryReduction () {
		String p = getProperty(SYMRED_KEY);
		return Boolean.parseBoolean(p);
	}
	
	/** */
	public AbstrSimulationProperties () {
		setProperty(PRECISION_KEY, PRECISION_DEFAULT_VALUE.toString());
		setProperty(RADIUS_KEY, RADIUS_DEFAULT_VALUE.toString());
		setProperty(SYMRED_KEY, SYMRED_DEFAULT_VALUE.toString());
	}
	
	/** The precision of a simulation */
	static public final String PRECISION_KEY = "precision";
	/** The radius of a simulation */
	static public final String RADIUS_KEY = "radius";
	/** Do or not symmetry reduction */
	static public final String SYMRED_KEY = "symmetry reduction";
	
	/** */
	static public final Integer PRECISION_DEFAULT_VALUE = 1;
	/** */
	static public final Integer RADIUS_DEFAULT_VALUE = 1;
	/** */
	static public final Boolean SYMRED_DEFAULT_VALUE = true;
	
	/** Default values for the properties. */
	static public final Map<String,Property<String>> DEFAULT_KEYS;
	
	static {
		Map<String,Property<String>> defaultKeys = new LinkedHashMap<String,Property<String>>(3);
		defaultKeys.put(PRECISION_KEY, new Property.IsPositiveInteger("Should be a natural number.", false));
		defaultKeys.put(RADIUS_KEY, new Property.IsPositiveInteger("Should be a natural number.", false));
		defaultKeys.put(SYMRED_KEY, new Property.IsBoolean("Should be \"true\" or \"false\".", false));
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
