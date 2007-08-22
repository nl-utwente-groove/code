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
 * $Id: NestingAspect.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import java.util.Comparator;

import groove.graph.Edge;
import groove.view.FormatException;
import groove.view.aspect.AbstractAspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows
 * a complete rule tree to be stored in a flat format.
 * 
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
public class NestingAspect extends AbstractAspect {

	/** Separator for level strings */
	public static final String LEVEL_SEPARATOR = ".";
	
	/** The name of the nesting aspect */
	public static final String NESTING_ASPECT_NAME = "nesting";
	/** Name of the NAC aspect value */
	public static final String NAC_NAME = "nac";
	/** The NAC aspect value */
	public static final AspectValue NAC;
	/** Name of the exists aspect value */
	public static final String EXISTS_NAME = "exists";
	/** The exists aspect value */
	public static final AspectValue EXISTS;
	/** Name of the forall aspect value */
	public static final String FORALL_NAME = "forall";
	/** The forall aspect value */
	public static final AspectValue FORALL;
	
	/** Name of a nesting edge aspect */
	public static final String PARENT_EDGE_NAME = "parent";
	/** Parent edge aspect value */
	public static final AspectValue PARENT_EDGE;
	/** Name of a level allocation edge aspect */
	public static final String LEVEL_EDGE_NAME = "level";
	/** Level edge aspect value */
	public static final AspectValue LEVEL_EDGE;
	
	/** Singleton instance of this class */
	private static final NestingAspect instance = new NestingAspect();
	
	/** Comparator for AspectNodes with a nesting value */
	public static final Comparator<AspectNode> comparator = new NestingComparator();
	
	static {
		try {
			EXISTS = instance.addValue(EXISTS_NAME);
			NAC = instance.addValue(NAC_NAME);
			FORALL = instance.addValue(FORALL_NAME);
			PARENT_EDGE = instance.addEdgeValue(PARENT_EDGE_NAME);
			LEVEL_EDGE = instance.addEdgeValue(LEVEL_EDGE_NAME);
		} catch( FormatException exc ) {
			throw new Error("Aspect '" + NESTING_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
		
	}
	
	public static class NestingComparator implements Comparator<AspectNode> {
		
		public int compare(AspectNode arg0, AspectNode arg1) {
			String level0 = NestingAspect.getLevel(arg0);
			String level1 = NestingAspect.getLevel(arg1);
			if( level0 == null && level1 != null )
				return -1; // No level implicitly means top level
			if( level0 != null && level1 == null )
				return 1;
			int cmp = level0.compareTo(level1);
			return cmp != 0 ? cmp : arg0.compareTo(arg1);
		}
		
		public boolean equals(AspectNode arg0, AspectNode arg1) {
			return arg0.equals(arg1);
		}
	}
	
	/**
	 * Creates a new instance of this Aspect
	 */
	private NestingAspect() {
		super(NESTING_ASPECT_NAME);
	}
	
	/**
	 * Returns the singleton instance of this aspect.
	 * @return the singleton instance of this aspect
	 */
	public static final NestingAspect getInstance() {
		return instance;
	}
	
	/**
	 * Retrieve the level of an element as a String
	 * @param element the element to retrieve the level of
	 * @return the level of the element, or null if it the element does not have a
	 *   NestingAspectValue, or if its level has not yet been assigned
	 */
	public static String getLevel(AspectElement element) {
		NestingAspectValue value = (NestingAspectValue)element.getValue(getInstance());
		if( value == null ) return null; // No Nesting Aspect
		return value.getLevel();
	}

	/**
	 * Retrieve the parent level of an element as a String
	 * @param element
	 * @return the level of the parent of this element, or null if the element does
	 *   not have a NestingAspectValue, or if its level has not yet been assigned, 
	 *   or it is a toplevel element
	 */
	public static String getParentLevel(String level) {
		if( level == null ) {
			return null;
		} else if( level.lastIndexOf(LEVEL_SEPARATOR) > 0 ) {
			level = level.substring(0, level.lastIndexOf(LEVEL_SEPARATOR));
		} else {
			level = null; // No parent
		}
		return level;
	}
	
	/**
	 * Determine whether a certain AspectElement is a meta element with respect
	 * to rule nesting
	 * @param element the element to test
	 * @return <code>true</code> if the element is a meta element wrt rule nesting,
	 *   <code>false</code> if not
	 */
	public static boolean isMetaElement(AspectElement element) {
		AspectValue value = element.getValue(getInstance());
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
	 * Determine whether a certain AspectElement is a Level indicating edge
	 * @param element
	 * @return
	 */
	public static boolean isLevelEdge(AspectEdge element) {
		AspectValue value = element.getValue(getInstance());
		if( value != null ) {
			return value.getName().equals(LEVEL_EDGE_NAME);
		}
		
		return false;
	}
	
	/**
	 * Determine whether a certain AspectElement is a Level indicating edge
	 * @param element
	 * @return
	 */
	public static boolean isParentEdge(AspectEdge element) {
		AspectValue value = element.getValue(getInstance());
		if( value != null ) {
			return value.getName().equals(PARENT_EDGE_NAME);
		}
		
		return false;
	}	

	/* (non-Javadoc)
	 * @see groove.view.aspect.AbstractAspect#createValue(java.lang.String)
	 */
	@Override
	protected NestingAspectValue createValue(String name) throws FormatException {
		NestingAspectValue value = new NestingAspectValue(this, name);
		return value;
	}
	
	/**
	 * Determine whether a level is universal or not (NAC levels are also universal)
	 * @param level
	 * @return
	 */
	public static boolean isUniversalLevel(String level) {
		return level.split("\\.").length % 2 == 0;
	}
	
	/**
	 * Determine whether a level is existential or not
	 * @param level
	 * @return
	 */
	public static boolean isExistentialLevel(String level) {
		return level.split("\\.").length % 2 == 1;
	}
	
	public static String getLevelName(AspectElement element) {
		NestingAspectValue value = (NestingAspectValue) element.getValue(instance);
		return value != null ? (value.getContent()) : null;
	}
	
	public static String getLevelOfNode(AspectGraph context, AspectNode node) {
		for( AspectEdge edge : context.outEdgeSet(node) ) {
			if( isLevelEdge(edge) ) {
				return getLevel(edge.target());
			}
		}
		// Toplevel is now implicit
		return "1";
	}
	

}
