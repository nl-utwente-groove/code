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
 * $Id: AspectParseData.java,v 1.3 2007-05-11 08:22:03 rensink Exp $
 */
package groove.view.aspect;

import java.util.Collection;
import java.util.Iterator;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.view.FormatException;
import static groove.view.aspect.Aspect.VALUE_SEPARATOR;

/**
 * Combination of declared aspect values and actual label text, as derived from a plain 
 * aspect label.
 * @author Arend Rensink
 * @version $Revision $
 */
class AspectParseData {	
	/**
	 * Construct a label from given aspect value list and label.
	 * @param values the list of aspect values to be used
	 * @param label the existing label 
	 */
	AspectParseData(AspectMap values, Label label) {
		this(values, label.text().contains(VALUE_SEPARATOR), label.text());
	}
	
	/**
	 * Construct a label from given aspect value list, end flag, and label text.
	 * @param values the list of aspect values to be used
	 * @param hasEnd flag indicating the presence of an explicit end marking for the aspect values
	 * @param text actual label text
	 */
	AspectParseData(AspectMap values, boolean hasEnd, String text) {
		this.declaredAspectMap = values;
		this.allAspectMap = new AspectMap();
		allAspectMap.putAll(values);
		this.hasEnd = hasEnd;
		this.text = text;
	}
	
	/**
	 * Returns the aspect values, in the order given in the original label prefix.
	 */
	public Collection<AspectValue> getDeclaredValues() {
		return declaredAspectMap.values();
	}

	/**
	 * Returns the combined map of all aspect values, declared and inferred.
	 */
	public AspectMap getAspectMap() {
		return allAspectMap;
	}

	/**
	 * Adds values to the aspect map that are inferred from source and target nodes.
	 * @param sourceMap map of aspect values for the source node
	 * @param targetMap map of aspect values for the target node
	 * @throws FormatException if an explicitly declared aspect value is overruled
	 */
	void addInferences(AspectMap sourceMap, AspectMap targetMap) throws FormatException {
		for (Aspect aspect: Aspect.allAspects) {
			AspectValue edgeValue = allAspectMap.get(aspect);
			AspectValue sourceValue = sourceMap.get(aspect);
			AspectValue sourceInference = sourceValue == null ? null : sourceValue.sourceToEdge();
			AspectValue targetValue = targetMap.get(aspect);
			AspectValue targetInference = targetValue == null ? null : targetValue.targetToEdge();
			AspectValue result = aspect.getMax(edgeValue, sourceInference, targetInference);
			if (edgeValue != null && edgeValue != result) {
				throw new FormatException("Inferred %s value '%s' differs from declared value '%s'", aspect, result, edgeValue);
			}
			if (result != null) {
				allAspectMap.add(result);
			}
		}
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
	public String getText() {
		return text;
	}

    /**
     * Creates a label from the parse data, based on the text and the aspect values.
     * This implementation parses the string as a regular expression, using
     * {@link RegExpr#parse(String)}, except if one of the aspect values implies free text.
     * If the regular expression yields an atom, or free text is used, a {@link DefaultLabel}
     * is returned, otherwise a {@link RegExprLabel} is returned.
     * @throws FormatException if {@link RegExpr#parse(String)} throws an exception
     */
	public Label getLabel() throws FormatException {
		if (label == null && hasText()) {
			boolean freeText = false;
			Iterator<AspectValue> valueIter = getAspectMap().values().iterator();
			while (!freeText && valueIter.hasNext()) {
				freeText |= valueIter.next().isFreeText();
			}
			if (!freeText) {
				RegExpr textAsRegExpr = RegExpr.parse(text);
				if (!(textAsRegExpr instanceof RegExpr.Atom)) {
					label = textAsRegExpr.toLabel();
				}
			}
			if (label == null) {
				label = DefaultLabel.createLabel(text);
			}
		}
		return label;
	}
	
	/** 
	 * Indicates if there was an actual label text.
	 * @return <code>true</code> if and only if <code>getText() != null</code>
	 */
	public boolean hasText() {
		return text != null;
	}

	/**
	 * Reconstructs the original plain label text from the
	 * list of aspect values, the end flag, and the actual label text.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (AspectValue value: declaredAspectMap.values()) {
			result.append(AspectParser.toString(value));
		}
		// append the end marking, if any
		if (isHasEnd()) {
			result.append(VALUE_SEPARATOR);
		}
		// append the label text, if any
		String label = getText();
		if (label != null) {
			result.append(label);
		}
		return result.toString();
	}

	/** The list of declared aspect values. */
	private final AspectMap declaredAspectMap;
	/** The list of all (declared and inferred) aspect values. */
	private final AspectMap allAspectMap;
	/** Indication that there was an explicit empty value ending the list. */
	private final boolean hasEnd;
	/** The actual label. */
	private final String text;
	/** The label, either set at construction time or to be computed by {@link #getLabel()}. */
	private Label label;
}
