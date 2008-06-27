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
package groove.abs;

/** Contains a factory method for getting MultiplicityInformation elements. 
 * A singleton class which unique instance is Abstraction.MULTIPLICITY
 * @author Iovka Boneva
 * @version $Revision $
 */
public interface Multiplicity {

	/** The multiplicity with some precision of a set with given cardinality.
	 * @param card should be >= 0
	 * @param precision should be >= 1 and <= Abstraction.MAX_ALLOWED_PRECISION
	 * @return the multiplicity corresponding to card
	 */ 
	public MultiplicityInformation getElement (int card, int precision);
	
	/** The multiplicity of a set with multiplicity mult to which
	 * nb elements are added. The result has the same precision
	 * as this multiplicity.
	 * @param nb
	 * @param mult
	 * @return -
	 */
	public MultiplicityInformation add (MultiplicityInformation mult, int nb);
	
	/** The multiplicity of a set with multiplicity mult from which
	 * nb elements are removed. The result has the same precision
	 * as this multiplicity.
	 * @param nb
	 * @param mult
	 * @return -
	 * @throws ExceptionRemovalImpossible 
	 */
	public MultiplicityInformation remove(MultiplicityInformation mult, int nb) throws ExceptionRemovalImpossible ;
	
	/** Tests whether some quantity can be removed from a multiplicity information.
	 * @param nb
	 * @param mult
	 * @return -
	 */
	public boolean canRemove(MultiplicityInformation mult, int nb) ;
	
	/** Informs whether this information contains a set of elements, or a 
	 * single one.
	 * @ensure isPrecise() iff getPreciseElements().length == 1 
	 * @param mult
	 * @return -
	 */
	public boolean isPrecise(MultiplicityInformation mult);
	
	/** Returns an array containing the precise MultiplicityInformation
	 * elements contained by this MultiplicityInformation.
	 * @ensure getPreciseElements()[i].isPrecise() == true for all 0 <= i < getPreciseElements().length
	 * @return the set of elements
	 */
	public MultiplicityInformation[] getPreciseElements(MultiplicityInformation mult);
	
	/** 
	 * @param mult
	 * @return the precision of mult
	 */
	public int getPrecision (MultiplicityInformation mult);
	
	/** Checks whether a <code>mult</code> corresponds to zero.
	 * @param mult
	 * @return true if <code>mult</code> corresponds to zero, false otherwise
	 */
	public boolean isZero (MultiplicityInformation mult);
	
	/** Checks whether a <code>mult</code> contains the omega element.
	 * @param mult
	 * @return true if <code>mult</code> contains omega, false otherwise
	 */
	public boolean containsOmega (MultiplicityInformation mult);
	
	/** The cardinality to which a multiplicity information corresponds.
	 * @param mult 
	 * @return -1 if the cardinality is not exact, the precise cardinality otherwise
	 */
	public int preciseCard (MultiplicityInformation mult);
	
	/** Check for inclusion of two multiplicity information elements.
	 * @param one
	 * @param other
	 * @return 
	 *  EQUAL if one and other are the same multiplicity
	 *  INCLUDED if the precise elements of one are included into the precise elements of other
	 *  CONTAINS if compare(other, one) == INCLUDED
	 *  NOT_EQ if none of the three previous holds
	 */
	public Abstraction.MultInfoRelation compare (MultiplicityInformation one, MultiplicityInformation other);
}
