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
import static groove.graph.aspect.Aspect.VALUE_SEPARATOR;

/**
 * Combination of aspect values and actual label text, as derived from a plain label.
 * @author Arend Rensink
 * @version $Revision $
 */
class AspectParseData {	
	/**
	 * Construct a label from given aspect value list and label text.
	 * @param values the list of aspect values to be used
	 * @param label actual label text
	 */
	AspectParseData(AspectMap values, Label label) {
		this(values, label.text().contains(VALUE_SEPARATOR), label);
	}
	
	/**
	 * Construct a label from given aspect value list, end flag, and label text.
	 * @param values the list of aspect values to be used
	 * @param hasEnd flag indicating the presence of an explicit end marking for the aspect values
	 * @param label actual label text
	 */
	AspectParseData(AspectMap values, boolean hasEnd, Label label) {
		this.aspectMap = values;
		this.hasEnd = hasEnd;
		this.label = label;
	}
	
	/**
	 * Returns the aspect values, in the order given in the original label prefix.
	 */
	public AspectMap getAspectMap() {
		return aspectMap;
	}

	/**
	 * Indicates if the original plain label contains an empty
	 * value explicitly modelling the end of the aspect value list.
	 */
	public boolean isHasEnd() {
		return hasEnd;
	}

	/** 
	 * Returns the actual label text
	 * (which may be <code>null</code> if the plain label was a node decorator).
	 */
	public Label getLabel() {
		return label;
	}

	/** 
	 * Indicates if there was an actual label text.
	 * @return <code>true</code> if and only if <code>getText() != null</code>
	 */
	public boolean hasText() {
		return label != null;
	}

	/**
	 * Reconstructs the original plain label text from the
	 * list of aspect values, the end flag, and the actual label text.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (AspectValue value: aspectMap.values()) {
			result.append(AspectParser.toString(value));
		}
		// append the end marking, if any
		if (isHasEnd()) {
			result.append(VALUE_SEPARATOR);
		}
		// append the label text, if any
		Label label = getLabel();
		if (label != null) {
			result.append(label);
		}
		return result.toString();
	}

	/** The list of aspect values. */
	private final AspectMap aspectMap;
	/** Indication that there was an explicit empty value ending the list. */
	private final boolean hasEnd;
	/** The actual label. */
	private final Label label;
}
