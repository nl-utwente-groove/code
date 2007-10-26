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
 * $Id: NumberLabelParser.java,v 1.1 2007-10-26 07:07:18 rensink Exp $
 */
package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/** 
 * Parser that turns a string into a default label,
 * after testing the string for correct formatting using a 
 * callback method that can be overridden by subclasses. 
 */
public class NumberLabelParser implements LabelParser {
	public Label parse(String text) throws FormatException {
		int nr;
		try {
			nr = Integer.parseInt(text);
		} catch (NumberFormatException exc) {
			throw new FormatException("String '%s' cannot be parsed as a number");
		}
		if (nr < 0) {
			throw new FormatException("String '%s' is a negative number");
		}
		return DefaultLabel.createLabel(text);
	}

    /** This implementation just takes the label text. */
    public String unparse(Label label) {
        return label.text();
    }
}
