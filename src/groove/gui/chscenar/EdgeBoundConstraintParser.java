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
package groove.gui.chscenar;

import java.util.HashMap;
import java.util.Map;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/** Parses a string representing a condition for an edge bound explore condition. 
 * @author Iovka Boneva
 * */
@Deprecated
@SuppressWarnings("all")
public class EdgeBoundConstraintParser implements Parser<Map<Label,Integer>> {

	public Map<Label,Integer> parse(String s) throws IllegalArgumentException {
		Map<Label,Integer> result = new HashMap<Label,Integer>();
		String[] entries = s.split(",");
		for (String entry : entries) {
			String[] keyValue = entry.split("=");
			if (keyValue.length != 2) {
	            throw new IllegalArgumentException("Edge bounds '" + entry
	                    + "' should be formatted as 'key=value'");
	        }
			Label key = DefaultLabel.createLabel(keyValue[0]);
			try {
	            int value = Integer.parseInt(keyValue[1]);
	            result.put(key, value);
	        } catch (NumberFormatException exc) {
	            throw new IllegalArgumentException("Value '" + keyValue[1]
	                    + "' in edge bounds '" + entry
	                    + "' is not a number");
	        }
		}
		return result;
	}
}
