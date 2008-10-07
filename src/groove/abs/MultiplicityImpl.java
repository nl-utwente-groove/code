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
import groove.abs.Abstraction.MultInfoRelation;

import java.util.HashMap;
import java.util.Map;

/** Implementation for a multiplicity.
 * @see Multiplicity
 * @author Iovka Boneva
 * @version $Revision $
 */
public class MultiplicityImpl implements Multiplicity {

	/** */
	protected MultiplicityImpl() {
		if (MultiplicityImpl.singleton) {
			throw new RuntimeException("Multiplicity should be singleton");
		}
		this.setOf = new HashMap<MultiplicityInformation, MultSetImpl>();
		this.sets = new MultSetImpl[Abstraction.MAX_ALLOWED_PRECISION+1];
		for (int i = 0; i < this.sets.length; i++) {
			this.sets[i] = new MultSetImpl(i);
		}
		MultiplicityImpl.singleton = true;
	}
	
	
	public MultiplicityInformation getElement(int card, int precision) {
		if (precision < 0 || precision > Abstraction.MAX_ALLOWED_PRECISION || card < 0) {
			throw new RuntimeException("Impossible cardinality or precision");
		}
		return this.sets[precision].getElement(card);
	}
	
	public MultiplicityInformation add(MultiplicityInformation mult, int nb) {
		return this.getSetFor(mult).add(mult, nb);
	}

	public MultiplicityInformation[] getPreciseElements(MultiplicityInformation mult) {
		return this.getSetFor(mult).getPreciseElements(mult);
	}


	public boolean isPrecise(MultiplicityInformation mult) {
		return this.getSetFor(mult).isPrecise(mult);
	}

	public MultiplicityInformation remove(MultiplicityInformation mult, int nb) throws ExceptionRemovalImpossible {
		return this.getSetFor(mult).remove(mult, nb);
	}
	
	public boolean canRemove(MultiplicityInformation mult, int nb) {
		return this.getSetFor(mult).canRemove(mult, nb);
	}
	
	public int getPrecision (MultiplicityInformation mult) {
		return this.getSetFor(mult).precision();
	}
	
	public boolean isZero(MultiplicityInformation mult) {
		return this.getSetFor(mult).zero() == mult;
	}

	public boolean containsOmega (MultiplicityInformation mult) {
		return this.getSetFor(mult).containsOmega(mult);
	}

	public int preciseCard(MultiplicityInformation mult) {
		return this.getSetFor(mult).preciseCard(mult);
	}
	
	public Abstraction.MultInfoRelation compare(MultiplicityInformation one, MultiplicityInformation other) {
		if (one == other) { return Abstraction.MultInfoRelation.M_EQUAL; }
		MultSetImpl setOne = getSetFor(one);
		MultSetImpl setOther = getSetFor(other);
		if (setOne != setOther) { return Abstraction.MultInfoRelation.M_NOTEQUAL; }
		return setOne.compare(one, other);
	}
	
	
	
	
	/**
	 * @param element
	 * @return the set that contains element, null if the element is unknown
	 */
	MultSetImpl getSetFor (MultiplicityInformation element) {
		return this.setOf.get(element);
	}
	
	/** The different multiplicity sets. */
	private MultSetImpl[] sets;
	/** Makes correspond to each MultiplicityInformation ins containing set.*/
	Map<MultiplicityInformation,MultSetImpl> setOf;
	/** Set to true when first initialized. */
	private static boolean singleton = false;
	
	// //////////////////////////////////////////////////////////////
	

	/** An implementation of a Multiplicity Set*/
	private class MultSetImpl {
		
		/** The precise values. pValues = precision + 1
		 * pValues[i] is the value i
		 */
		private MultiplicityInformation[] pValues;
		
		/** The imprecise values. iValues.length = precision + 2
		 * iValues[i] is the set omega - i
		 * Thus, iValues[0] is the omega value (which is precise in the sense of isPrecise()) 
		 */
		private MultiplicityInformation[] iValues;
		
		/** Initializes all the elements of this set.
		 * @param precision
		 */
		MultSetImpl(int precision) {
			this.pValues = new MultInfoImpl[precision + 1];
			this.iValues = new MultInfoImpl[precision + 2];
			for (int i = 0; i < precision+1; i++) {
				this.pValues[i] = new MultInfoImpl();
				MultiplicityImpl.this.setOf.put(this.pValues[i], this);
				this.iValues[i] = new MultInfoImpl();
				MultiplicityImpl.this.setOf.put(this.iValues[i], this);
			}
			this.iValues[precision+1] = new MultInfoImpl();
			MultiplicityImpl.this.setOf.put(this.iValues[precision+1], this);
		}

		/**  
		 * @param i
		 * @return the precise element corresponding to multiplicity i
		 * @require i >= 0
		 */
		MultiplicityInformation getElement(int i) {
			assert i >= 0 : "Negative multiplicity impossible";
			if (i > this.precision()) {
				return this.omega();
			}
			return this.pValues[i];
		}
		
		/** Adds nb to element
		 * @param element
		 * @param nb
		 * @return the resulting multiplicity information
		 * @require element is an element of this multiplicity
		 */
		MultiplicityInformation add(MultiplicityInformation element, int nb) {
			// test if a precise value
			int idx = this.idxInPValues(element);
			if (idx != -1) {
				idx+= nb;
				if (idx < this.pValues.length) {
					return this.pValues[idx];
				}
				return this.omega();
			}
			// test if an imprecise value
			idx = this.idxInIValues(element);
			assert idx != -1 : "Not element of this multiplicity";

			idx -= nb;
			if (idx <= 0) {
				return this.omega();
			}
			return this.iValues[idx];
		}
		
		/** Removes nb from element 
		 * @param element
		 * @param nb
		 * @return the resulting multiplicity information, null if element is not an element of this set
		 * @require element is an element of this multiplicity
		 * @throws ExceptionRemovalImpossible if element.isPrecise() and nb is bigger than the value represented by element
		 */
		MultiplicityInformation remove(MultiplicityInformation element, int nb) throws ExceptionRemovalImpossible {
			// test if a precise value
			int idx = this.idxInPValues(element);
			if (idx != -1) {
				idx -= nb;
				if (idx < 0) {
					throw new ExceptionRemovalImpossible();
				}
				return this.pValues[idx];
			}
			// test if an imprecise value
			idx = this.idxInIValues(element);
			assert idx != -1 : "Not element of this multiplicity";
	
			idx += nb;
			if (idx >= this.iValues.length) {
				return this.iValues[this.iValues.length - 1];
			}
			return this.iValues[idx];
		}
		
		/** Tests whether some quantity can be removed from a multiplicity information.
		 * @param element
		 * @param nb
		 * @return true if the quantity can be removed 
		 */
		boolean canRemove(MultiplicityInformation element, int nb) {
			int idx = this.idxInPValues(element);
			if (idx != -1) {
				idx -= nb;
				if (idx < 0) {
					return false;
				}
			}
			return true;
		}
		
		/** Returns true if an element contains omega
		 * @param element
		 * @return true if <code>element</code> contains omega
		 */
		boolean containsOmega (MultiplicityInformation element) {
			return this.idxInIValues(element) != -1;
		}
		
		/**  
		 * @param element
		 * @return true if element is precise, no guarantee of the behaviour if element is not an element of this set
		 * @require element is an element of this multiplicity set
		 */
		boolean isPrecise (MultiplicityInformation element) {
			// test if a precise value
			int idx = this.idxInPValues(element);
			if (idx != -1 || element == this.omega()) {
				return true;
			}
			// imprecise value
			assert this.idxInIValues(element) != -1 : "Not element of this multiplicity";
			return false;
		}
		
		/** 
		 * @param element
		 * @return the set of precise elements that element contains
		 * @require element is an element of this multiplicity set
		 */
		MultiplicityInformation[] getPreciseElements(MultiplicityInformation element) {
			// test if a precise value
			int idx = this.idxInPValues(element);
			if (idx != -1 || element == this.omega()) {
				return new MultiplicityInformation[] {element};
			}
			// test if a precise value
			idx = this.idxInIValues(element);
			assert idx != -1 : "Not element of this multiplicity";
			MultiplicityInformation[] result = new MultiplicityInformation[idx + 1];
			
			result[0] = this.omega();
			for (int i = 0; i < idx; i++) {
				result[i+1] = this.pValues[this.precision() - i];
			}
			return result;
		}
		
		/** The precise cardinality of an element.
		 * @param element
		 * @return The precise cardinality of <code>element</code>, or -1 if it is not precise.
		 */
		int preciseCard(MultiplicityInformation element) {
			return this.idxInPValues(element);
		}
		
		/**  
		 * @param element
		 * @return the index of element in pValues, -1 if element is not in pValues
		 */
		private int idxInPValues (MultiplicityInformation element) {
			for (int i = 0; i < this.pValues.length; i++) {
				if (this.pValues[i] == element) {
					return i;
				}
			}
			return -1;
		}
		/**  
		 * @param element
		 * @return the index of element in pValues, -1 if element is not in pValues
		 */
		private int idxInIValues (MultiplicityInformation element) {
			for (int i = 0; i < this.iValues.length; i++) {
				if (this.iValues[i] == element) {
					return i;
				}
			}
			return -1;
		}

		/** @return The precision of this set. */
		int precision () {
			return this.pValues.length - 1;
		}
		
		/** @return The omega element of this set. */
		private MultiplicityInformation omega () {
			return this.iValues[0];
		}
		
		/** @return The zero element of this set. */
		MultiplicityInformation zero () {
			return this.pValues[0];
		}
		
		/**
		 * @param element
		 * @return the string representation of element
		 */
		private String toString(MultiplicityInformation element) {
			String result = null;
			// test if a precise value
			int idx = this.idxInPValues(element);
			if (idx != -1) {
				return "" + idx; // + "[" + this.precision() + "]";
			}
			// an imprecise value
			idx = this.idxInIValues(element);
			assert idx != -1 : "Not element of this multiplicity";
			result = "{w";
			for (int i = 0; i < idx; i++) {
				result += ", " + (this.precision() - i); 
			}
			result += "}"; // + "[" + this.precision() + "]";
			return result;
		}		

		/** Compares two MultiplicityInformation for inclusion.
		 * @param one
		 * @param other
		 * @require one and other should be into this set
		 */
		Abstraction.MultInfoRelation compare(MultiplicityInformation one, MultiplicityInformation other) {
			if (one == other) { return MultInfoRelation.M_EQUAL; } 
			int idxOne = this.idxInPValues(one);
			int idxOther = this.idxInPValues(other);
			if (idxOne != -1 && idxOther != -1) { return MultInfoRelation.M_NOTEQUAL; }
			
			// One of the two is a set
			if (idxOne == -1 && idxOther != -1) {
				idxOne = this.idxInIValues(one);
				return (idxOne + idxOther >= this.iValues.length-1) ? Abstraction.MultInfoRelation.M_CONTAINS : Abstraction.MultInfoRelation.M_NOTEQUAL; 
			} else if (idxOne != -1 && idxOther == -1) {
				idxOther = this.idxInIValues(other);
				return (idxOne + idxOther >= this.iValues.length-1) ? Abstraction.MultInfoRelation.M_BELONGS : Abstraction.MultInfoRelation.M_NOTEQUAL;
			} else { // idxOne == -1 && idxOther == -1
				idxOne = this.idxInIValues(one);
				idxOther = this.idxInIValues(other);
				return idxOne > idxOther ? Abstraction.MultInfoRelation.M_SUPERSET : Abstraction.MultInfoRelation.M_SUBSET;
			}
		}
		
	}
	
	

	
	/** An implementation for MultiplicityInformation. */
	class MultInfoImpl implements MultiplicityInformation {
		@Override
		public String toString () {
			MultiplicityImpl impl= (MultiplicityImpl) Abstraction.MULTIPLICITY;
			MultSetImpl mult = impl.getSetFor(this);
			String result = mult.toString(this);
			return result;
		}
	}

}
