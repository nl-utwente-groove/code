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
 * $Id: AspectEdge.java,v 1.9 2007-11-19 12:19:14 rensink Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.view.FormatException;

import java.util.Collection;
import java.util.List;

/**
 * Edge enriched with aspect data.
 * Aspect edge labels are interpreted as {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision: 1.9 $
 */
public class AspectEdge extends AbstractBinaryEdge<AspectNode,DefaultLabel,AspectNode> implements AspectElement {
	/**
	 * Constructs a new edge from an array of end nodes, a label,
	 * and a collection of aspect values.
	 * @param ends the end nodes of the new edge
	 * @param label the label of the new edge
	 * @param values the aspect values for the new edge
	 * @throws FormatException
	 */
	public AspectEdge(List<AspectNode> ends, DefaultLabel label, AspectValue... values) throws FormatException {
		super(ends.get(SOURCE_INDEX), label, ends.get(TARGET_INDEX));
		this.parseData = createParseData(label, values);
		testLabel();
	}
	
    /**
     * Constructs a new edge, with source and target node, label, and aspect values as given.
     * @param source the source node for this edge
     * @param target the target node for this edge
     * @param parseData the aspect values for this edge.
     * @throws FormatException if the aspect values of <code>parseData</code>
     * are inconsistent with those of the source or target nodes
     */
    AspectEdge(AspectNode source, AspectNode target, AspectParseData parseData) throws FormatException {
        super(source, parseData.getLabel(), target);
    	this.parseData = parseData;
		testLabel();
    }
//    
//    /** Specialises the return type. */
//	@Override
//	public AspectNode target() {
//		return (AspectNode) super.target();
//	}
//
//    /** Specialises the return type. */
//	@Override
//	public AspectNode source() {
//		return (AspectNode) super.source();
//	}

	public AspectValue getValue(Aspect aspect) {
    	AspectValue result = getAspectMap().get(aspect);
    	if (result == null) {
    		result = aspect.getDefaultValue();
    	}
    	return result;
    }

    public Collection<AspectValue> getDeclaredValues() {
		return parseData.getDeclaredValues();
	}

    /** 
     * Returns the map from aspects to aspect values for this edge,
     * lazily creating it first.
     */
    public AspectMap getAspectMap() {
    	return parseData.getAspectMap();
    }

	/**
	 * Returns the plain text label for the aspect edge.
	 */
	public String getPlainText() {
		return parseData.toString();
	}
//
//	/**
//	 * Has to be overridden to make {@link #imageFor(NodeEdgeMap)} work.
//	 * This implementation returns an {@link AspectEdge}.
//	 */
//	@Override
//	@Deprecated
//	public BinaryEdge newEdge(Node source, Label label, Node target) {
//		if (source instanceof AspectNode && target instanceof AspectNode) {
//			// we certainly want an aspect edge
//			try {
//				AspectParseData declaredData = AspectParser.getInstance().getParseData(label.text());
//				declaredData.addInferences(((AspectNode) source).getAspectMap(), ((AspectNode) target).getAspectMap());
//				return new AspectEdge((AspectNode) source, (AspectNode) target, declaredData);
//			} catch (FormatException exc) {
//				// the edge aspects were incompatible with the node aspects
//				// so the edge has no image
//				return null;
//			}
//		} else {
//			throw new IllegalArgumentException();
//		}
//	}

	/**
     * Includes the hash code of the associated aspect values.
	 */
	@Override
	protected int computeHashCode() {
		int result = super.computeHashCode();
		for (Aspect aspect: Aspect.allAspects) {
			AspectValue value = getValue(aspect);
			if (value != null) {
				result += value.hashCode();
			}
		}
		return result;
	}

	/**
	 * Tests equality of type, ends and aspect values.
	 */
	@Override
	public boolean equals(Object obj) {
		return isTypeEqual(obj) && isEndEqual((Edge) obj) && isLabelEqual((Edge) obj) && isAspectEqual((AspectEdge) obj);
	}

	/**
	 * Tests if the object is an {@link AspectEdge}.
	 */
	@Override
	protected boolean isTypeEqual(Object obj) {
		return obj instanceof AspectEdge;
	}
	
	/** Tests if the aspect map of this edge equals that of the other. */
	protected boolean isAspectEqual(AspectEdge other) {
		for (Aspect aspect: Aspect.allAspects) {
			if (getValue(aspect) != other.getValue(aspect)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This implementation defers to {@link #getPlainText()}
	 */
	@Override
	protected String getLabelText() {
		return getPlainText();
	}

	/**
	 * Tests if the parsed edge label is allowed by all inferred aspects. 
	 * @throws FormatException if there is an aspect whose value
	 * for this edge is incompatible with the edge label
	 * @see Aspect#testLabel(Label, AspectValue, AspectValue)
	 */
	protected void testLabel() throws FormatException {
		for (AspectValue declaredAspectValue: getDeclaredValues()) {
			Aspect aspect = declaredAspectValue.getAspect();
			AspectValue inferredValue = getAspectMap().get(aspect);
			aspect.testLabel(label(), declaredAspectValue, inferredValue);
		}
	}
//	
//	/**
//     * Computes an inferred aspect map by combining the explicitly declared edge
//     * values with the aspect values inferred from the source and target nodes.
//     * @param parseData explicitly declared aspect data
//     */
//    final protected AspectMap computeInferredAspectMap(AspectParseData parseData) throws FormatException {
//    	AspectMap result = new AspectMap();
//    	AspectMap edgeMap = parseData.getAspectMap();
//    	AspectMap sourceMap = source().getAspectMap();
//    	AspectMap targetMap = target().getAspectMap();
//    	for (Aspect aspect: Aspect.allAspects) {
//    		AspectValue inferredValue = getInferredValue(aspect, edgeMap, sourceMap, targetMap);
//    		if (inferredValue != null) {
//				result.add(inferredValue);
//			}
//    	}
//    	return result;
//    }
//
//    /**
//     * Returns an inferred edge aspect value.
//     * @param aspect the aspect for which we want the inferred value
//     * @param edgeMap the map of explicitly declared aspect values
//     * @param sourceMap map of aspect values for the source node
//     * @param targetMap map of aspect values for the target node
//     * @return the maximum aspect value for <code>aspect</code>, 
//     * according to {@link Aspect#getMax(AspectValue[])}.
//     * @throws FormatException if the explicitly declared aspect value is overruled
//     */
//    private AspectValue getInferredValue(Aspect aspect, AspectMap edgeMap, AspectMap sourceMap, AspectMap targetMap) throws FormatException {
//		AspectValue result;
//		AspectValue edgeValue = edgeMap.get(aspect);
//		AspectValue sourceValue = sourceMap.get(aspect);
//		AspectValue sourceInference = sourceValue == null ? null : sourceValue.sourceToEdge();
//		AspectValue targetValue = targetMap.get(aspect);
//		AspectValue targetInference = targetValue == null ? null : targetValue.targetToEdge();
//		result = aspect.getMax(edgeValue, sourceInference, targetInference);
//		if (edgeValue != null && edgeValue != result) {
//			throw new FormatException("Inferred %s value '%s' differs from declared value '%s'", aspect, result, edgeValue);
//		}
//		return result;
//    }
//    
    /** Callback factory method. */
    AspectParseData createParseData(Label label, AspectValue[] values) throws FormatException {
    	AspectParseData result = new AspectParseData(computeDeclaredAspectMap(values), label);
    	result.addInferences(source().getAspectMap(), target().getAspectMap());
    	return result;
    }
    
    /**
     * Converts an array of aspect values into an aspect map, and adds the
     * values inferred from the source and target nodes.
     * @param values the explicit aspect values for the edge
     * @return an aspect map combining the explicit and the inferred aspect values
     * @throws FormatException if <code>values</code> contains duplicate
     * values for an aspect, or the values are inconsistent with the inferred values
     */
    final protected AspectMap computeDeclaredAspectMap(AspectValue[] values) throws FormatException {
    	AspectMap result = new AspectMap();
    	for (AspectValue value: values) {
        	result.add(value);
    	}
    	return result;
    }

    /**
     * The aspect information of the label, set at construction time.
     */
    private final AspectParseData parseData;
//    /**
//     * The aspect map inferred from the aspect label and the source
//     * and target node aspects.
//     */
//    private final AspectMap aspectMap;
}