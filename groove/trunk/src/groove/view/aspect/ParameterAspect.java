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
 * $Id: ParameterAspect.java,v 1.1 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows
 * a complete rule tree to be stored in a flat format.
 * 
 * @author Tom Staijen
 * @version $Revision: 1.1 $
 */
public class ParameterAspect extends AbstractAspect {

	@Override
	public void checkNode(AspectNode node, AspectGraph graph) throws FormatException {
		// TODO: check here if there is a declared value on the node 
	 	// that makes the node an attributenode (?)
		// check also wether the strings are integers 
	}

	@Override
	protected AspectValue createValue(String name) throws FormatException {
		return new NamedAspectValue(this, name); 
	}

	/** The name of the aspect. */
	private static final String PARAMETER_ASPECT_NAME = "parameter";
	
	/** The label for the parameter aspect value **/
	private static final String ID_NAME = "id";
	
	/** The exists aspect value */
	public static final AspectValue ID;
	
	/** The singleton instance of this aspect. */
	private static final ParameterAspect instance = new ParameterAspect();

	static {

		try {
			ID = instance.addValue(ID_NAME);
		} catch( FormatException exc ) {
			throw new Error("Aspect '" + PARAMETER_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
	}
	
	
	public ParameterAspect() {
		super(PARAMETER_ASPECT_NAME);
	}
	
	/**
     * Returns the singleton instance of this aspect.
     */
    public static ParameterAspect getInstance() {
        return instance;
    }
	
    public static Integer getID(AspectNode node) {
    	NamedAspectValue value = (NamedAspectValue) node.getValue(getInstance());
    	if( value == null ) {
    		return null;
    	} else {
    		Integer id = Integer.parseInt(value.getContent().substring(1));
    		return id;
    	}
    }
    
}
