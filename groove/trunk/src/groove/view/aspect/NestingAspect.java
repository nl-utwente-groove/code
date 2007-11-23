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
 * $Id: NestingAspect.java,v 1.13 2007-11-23 08:20:10 rensink Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;
import groove.view.FreeLabelParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows
 * a complete rule tree to be stored in a flat format.
 * 
 * @author kramor
 * @version 0.1 $Revision: 1.13 $ $Date: 2007-11-23 08:20:10 $
 */
public class NestingAspect extends AbstractAspect {
	/**
	 * Creates a new instance of this Aspect
	 */
	private NestingAspect() {
		super(NESTING_ASPECT_NAME);
	}
	
	/**
	 * Tests whether the nesting value of an aspect edge is correct in the context of the edge.
	 */
	@Override
	public void checkEdge(AspectEdge edge, AspectGraph graph) throws FormatException {
		if (isLevelEdge(edge)) {
			// source nodes should be non-meta with only this level edge
			if (isMetaElement(edge.source())) {
				throw new FormatException("Level edge %s should not have meta-node as source", edge);
			}
			for (AspectEdge outEdge: graph.outEdgeSet(edge.source())) {
				if (isMetaElement(outEdge) && !outEdge.equals(edge)) {
					throw new FormatException("Ambiguous level edges at %s", edge.source());
				}
			}
			// target nodes should be meta
			if (!isMetaElement(edge.opposite())) {
				throw new FormatException("Level edge %s should have a meta-node as target", edge);
			}
		} else if (isParentEdge(edge)) {
			// source and target nodes should be inversely universal and existential
			if (!isMetaElement(edge.source())) {
				throw new FormatException("Parent edge %s should have a meta-node as source", edge);
			}
			if (!isMetaElement(edge.opposite())) {
				throw new FormatException("Parent edge %s should have a meta-node as target", edge);
			}
			if (isExists(edge.source()) == isExists(edge.target())) {
				throw new FormatException("Parent edge %s should be between distinct quantifiers", edge);
			}
		} else if (isExists(edge) || isForall(edge)) {
			// source and target nodes should be inversely universal and existential
			if (isMetaElement(edge.source())) {
				throw new FormatException("Quantified edge %s has a meta-node as source", edge);
			}
			if (isMetaElement(edge.opposite())) {
				throw new FormatException("Quantified edge %s has a meta-node as target", edge);
			}
			AspectValue value = getNestingValue(edge);
			if (!(value instanceof NamedAspectValue) || ((NamedAspectValue) value).getContent().length() == 0) {
				throw new FormatException("Quantified edge %s has empty level name", edge);
			}
		}
	}

	@Override
	public void checkNode(AspectNode node, AspectGraph graph) throws FormatException {
		Set<AspectEdge> outEdgeSet = graph.outEdgeSet(node);
		if (outEdgeSet.size() > 1) {
			throw new FormatException("Meta-node %s has ambiguous parentage", node);
		}
		// test for cyclic parentage
		Set<AspectNode> parents = new HashSet<AspectNode>();
		parents.add(node);
		while (!outEdgeSet.isEmpty()) {
			AspectNode current = outEdgeSet.iterator().next().opposite();
			if (!parents.add(current)) {
				throw new FormatException("Parent edge cycle starting at %s", node);
			}
			outEdgeSet = graph.outEdgeSet(current);
		}
	}

	@Override
	protected AspectValue createValue(String name) throws FormatException {
		AspectValue result;
		if (contentValues.contains(name)) {
			result = new NamedAspectValue(getInstance(), name);
		} else {
			result = super.createValue(name);
		}
		return result;
	}

	/**
	 * Returns the singleton instance of this aspect.
	 * @return the singleton instance of this aspect
	 */
	public static final NestingAspect getInstance() {
		return instance;
	}
	
    /** 
     * Returns the nesting aspect value associated with a given aspect element.
     * Convenience method for {@link AspectElement#getValue(Aspect)} with {@link #getInstance()}
     * as parameter.
     */
    public static AspectValue getNestingValue(AspectElement elem) {
    	return elem.getValue(getInstance());
    }
	
	/**
	 * Determine whether a certain AspectElement is a meta element with respect
	 * to rule nesting.
	 * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
	 * and {@link #checkEdge(AspectEdge, AspectGraph)}).
	 * @param element the element to test
	 * @return <code>true</code> if the element is a meta element wrt rule nesting,
	 *   <code>false</code> if not
	 */
	public static boolean isMetaElement(AspectElement element) {
		AspectValue value = getNestingValue(element);
		if( (element instanceof AspectNode) && value != null ) {
			// Nodes with an aspect are automatically a Meta Element
			return true;
		}
		if( element instanceof AspectEdge ) {
			// If it is a meta edge, its source should be a meta node
			return isMetaElement(((AspectEdge)element).target());
		}
		return false;
	}
	
	/**
	 * Determine whether a certain AspectElement is a Level-indicating edge.
	 * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
	 * and {@link #checkEdge(AspectEdge, AspectGraph)}).
	 */
	public static boolean isLevelEdge(AspectEdge element) {
		AspectValue value = getNestingValue(element);
		return value != null && value.equals(NESTED) && element.label().text().equals(AT_LABEL);
	}
	
	/**
	 * Determine whether a certain AspectElement is a parent-indicating edge.
	 * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
	 * and {@link #checkEdge(AspectEdge, AspectGraph)}).
	 */
	public static boolean isParentEdge(AspectEdge element) {
		AspectValue value = getNestingValue(element);
		return value != null && value.equals(NESTED) && element.label().text().equals(IN_LABEL);
	}	

    /**
     * Determine whether an aspect edge carries the {@link #FORALL} or {@link #FORALL_POS} nesting value.
     * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
     * and {@link #checkEdge(AspectEdge, AspectGraph)}).
     */
    public static boolean isForall(AspectElement element) {
        return getNestingValue(element).equals(FORALL) || getNestingValue(element).equals(FORALL_POS);
    }

    /**
     * Determine whether an aspect edge carries the {@link #FORALL_POS} nesting value.
     * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
     * and {@link #checkEdge(AspectEdge, AspectGraph)}).
     */
    public static boolean isPositive(AspectElement element) {
        return getNestingValue(element).equals(FORALL_POS);
    }
    
	/**
	 * Determine whether an aspect edge carries the {@link #EXISTS} nesting value.
	 * The element is assumed to be checked (see {@link #checkNode(AspectNode, AspectGraph)}
	 * and {@link #checkEdge(AspectEdge, AspectGraph)}).
	 */
	public static boolean isExists(AspectElement element) {
		return getNestingValue(element).equals(EXISTS);
	}
	
	/** 
	 * Returns the name of a nesting level identified by a given aspect element. 
	 */
	public static String getLevelName(AspectElement element) {
		AspectValue value = getNestingValue(element);
		return value instanceof NamedAspectValue ? ((NamedAspectValue) value).getContent() : null;
	}
	
	/** The name of the nesting aspect */
	public static final String NESTING_ASPECT_NAME = "nesting";
//	/** Name of the NAC aspect value */
//	public static final String NAC_NAME = "nac";
	/** Name of the exists aspect value */
	public static final String EXISTS_NAME = "exists";
    /** Name of the forall aspect value */
    public static final String FORALL_NAME = "forall";
    /** Name of the positive forall aspect value */
    public static final String FORALL_POS_NAME = "forallx";
	/** Name of the generic nesting edge aspect value. */
	public static final String NESTED_NAME = "nested";
	/** The set of aspect value names that are content values. */
	private static final Set<String> contentValues;

	static {
		contentValues = new HashSet<String>();
		contentValues.add(EXISTS_NAME);
        contentValues.add(FORALL_NAME);
        contentValues.add(FORALL_POS_NAME);
	}

	/** The exists aspect value */
	public static final AspectValue EXISTS;
    /** The forall aspect value */
    public static final AspectValue FORALL;
    /** The positive forall aspect value */
    public static final AspectValue FORALL_POS;
	/** Nested edge aspect value. */
	public static final AspectValue NESTED;
	
	/** Singleton instance of this class */
	private static final NestingAspect instance = new NestingAspect();
	
	static {
		try {
			EXISTS = instance.addValue(EXISTS_NAME);
            FORALL = instance.addValue(FORALL_NAME);
            FORALL_POS = instance.addValue(FORALL_POS_NAME);
			NESTED = instance.addEdgeValue(NESTED_NAME);
			EXISTS.setSourceToEdge(NESTED);
			EXISTS.setTargetToEdge(NESTED);
            FORALL.setSourceToEdge(NESTED);
            FORALL.setTargetToEdge(NESTED);
            FORALL_POS.setSourceToEdge(NESTED);
            FORALL_POS.setTargetToEdge(NESTED);
		} catch( FormatException exc ) {
			throw new Error("Aspect '" + NESTING_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
	}
	
	/** Label used for parent edges (between meta-nodes). */
	public static final String IN_LABEL = "in";
	/** Label used for level edges (from rule nodes to meta-nodes). */
	public static final String AT_LABEL = "at";
	/** Label used for the to-level meta-node. */
	public static final String TOP_LABEL = "top";
	/** The set of all allowed nesting labels. */
	static final Set<String> ALLOWED_LABELS = new HashSet<String>();
	
	static {
		ALLOWED_LABELS.add(IN_LABEL);
		ALLOWED_LABELS.add(AT_LABEL);
		ALLOWED_LABELS.add(TOP_LABEL);
		NESTED.setLabelParser(new NestingLabelParser());
	}
	
	/** 
	 * Class that attempts to parse a string as the operation of a given
	 * algebra, and returns the result as a DefaultLabel if successful.
	 */
	private static class NestingLabelParser extends FreeLabelParser {
		/** Empty constructor with the correct visibility. */
		NestingLabelParser() {
			// empty
		}

        /** This implementation tests if the text corresponds to an operation of the associated algebra. */
        @Override
        protected boolean isCorrect(String text) {
            return ALLOWED_LABELS.contains(text);
        }

        /** This implementation tests if the text corresponds to an operation of the associated algebra. */
        @Override
        protected String getExceptionText(String text) {
            return String.format("Label %s on nesting edge should be one of %s", text, ALLOWED_LABELS);
        }
	}
}
