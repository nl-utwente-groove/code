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
 * $Id: Fixable.java,v 1.3 2008-01-30 09:32:15 iovka Exp $
 */
package groove.util;

import groove.view.FormatException;

/**
 * Interface for objects that know a <i>fixing phase</i> before they can be used.
 * The fixing phase consists of method calls to build the object; after this
 * has finished, the object is fixed, meaning it should not be modified any more.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Fixable {
	/** 
	 * Sets the object to fixed.
	 * @throws FormatException if the object is found to be inconsistent in some way. 
	 */
	void setFixed() throws FormatException ;
	
	/** 
	 * Indicates if the object is fixed, i.e., {@link #setFixed()}
	 * has been called.
	 */
	boolean isFixed();
	
	/** 
	 * Test the fixedness of this object.
	 * Throws an exception if the fixedness does not correspond to
	 * a given value.
	 * @param fixed if <code>true</code>, the object is expected to be fixed;
	 * otherwise, it is expected to be unfixed.
	 * @throws IllegalStateException if the fixedness of the object does not
	 * equal <code>fixed</code>, i.e., if <code>isFixed() != fixed</code>.
	 */
	void testFixed(boolean fixed);
}
