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
 * $Id: NestingAspect.java,v 1.4 2007-10-08 00:59:25 rensink Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows
 * a complete rule tree to be stored in a flat format.
 * 
 * @author kramor
 * @version 0.1 $Revision: 1.4 $ $Date: 2007-10-08 00:59:25 $
 */
public class NestingAspect extends AbstractAspect {
	/**
	 * Creates a new instance of this Aspect
	 */
	private NestingAspect() {
		super(NESTING_ASPECT_NAME);
	}
	
	@Override
	protected AspectValue createValue(String name) throws FormatException {
		AspectValue result;
		if (contentValues.contains(name)) {
			result = new NestingAspectValue(name);
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
	 * to rule nesting
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
	 */
	public static boolean isLevelEdge(AspectEdge element) {
		AspectValue value = getNestingValue(element);
		return value != null && value.equals(LEVEL_EDGE);
	}
	
	/**
	 * Determine whether a certain AspectElement is a parent-indicating edge.
	 */
	public static boolean isParentEdge(AspectEdge element) {
		AspectValue value = getNestingValue(element);
		return value != null && value.equals(PARENT_EDGE);
	}	

	/**
	 * Determine whether an aspect edge carries the {@link #FORALL} nesting value.
	 */
	public static boolean isForall(AspectElement element) {
		return getNestingValue(element).equals(FORALL);
	}
	
	/**
	 * Determine whether an aspect edge carries the {@link #EXISTS} nesting value.
	 */
	public static boolean isExists(AspectElement element) {
		return getNestingValue(element).equals(EXISTS);
	}
	
	/** Returns the name of a nesting level identified by a given aspect element. */
	public static String getLevelName(AspectElement element) {
		NestingAspectValue value = (NestingAspectValue) getNestingValue(element);
		return value != null ? (value.getContent()) : null;
	}
	
	/** Separator for level strings */
	public static final char LEVEL_SEPARATOR = '.';
	
	/** The name of the nesting aspect */
	public static final String NESTING_ASPECT_NAME = "nesting";
	/** Name of the NAC aspect value */
	public static final String NAC_NAME = "nac";
	/** Name of the exists aspect value */
	public static final String EXISTS_NAME = "exists";
	/** Name of the forall aspect value */
	public static final String FORALL_NAME = "forall";
	/** Name of a nesting edge aspect */
	public static final String PARENT_EDGE_NAME = "parent";
	/** The NAC aspect value */
	public static final String LEVEL_EDGE_NAME = "level";
	/** Name of the generic nesting edge aspect value. */
	public static final String NESTED_NAME = "nested";
	/** The set of aspect value names that are content values. */
	private static final Set<String> contentValues;

	static {
		contentValues = new HashSet<String>();
		contentValues.add(EXISTS_NAME);
		contentValues.add(FORALL_NAME);
		contentValues.add(NAC_NAME);		
	}

	/** Level edge aspect value */
	public static final AspectValue NAC;
	/** The exists aspect value */
	public static final AspectValue EXISTS;
	/** The forall aspect value */
	public static final AspectValue FORALL;
	/** Parent edge aspect value */
	public static final AspectValue PARENT_EDGE;
	/** Name of a level allocation edge aspect */
	public static final AspectValue LEVEL_EDGE;
	/** Nested edge aspect value. */
	public static final AspectValue NESTED;
	
	/** Singleton instance of this class */
	private static final NestingAspect instance = new NestingAspect();
	
	static {
		try {
			EXISTS = instance.addValue(EXISTS_NAME);
			NAC = instance.addValue(NAC_NAME);
			FORALL = instance.addValue(FORALL_NAME);
			PARENT_EDGE = instance.addEdgeValue(PARENT_EDGE_NAME);
			LEVEL_EDGE = instance.addEdgeValue(LEVEL_EDGE_NAME);
			NESTED = instance.addEdgeValue(NESTED_NAME);
		} catch( FormatException exc ) {
			throw new Error("Aspect '" + NESTING_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
	}
}
