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
 * $Id: SPORuleMorphism.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import groove.graph.DefaultMorphism;
import groove.graph.Morphism;
import groove.trans.SPORule;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class SPORuleMorphism {

	/** Prototype object, use createMorphism(dom, cod) for new instances */
	public static final SPORuleMorphism prototype = new SPORuleMorphism();
	
	/** Source rule of this mapping */
	private SPORule domain;
	/** Target rule of this mapping */
	private SPORule codomain;
	
	/** Left graph morphism */
	private Morphism left;
	/** Right graph morphism */
	private Morphism right;
	
	/** Variable for fixed state */
	private boolean fixed;
	
	/**
	 * Constructs a new SPORuleMorphism
	 * @param dom the source rule for this mapping
	 * @param cod the target rule for this mapping
	 * @ensure dom() == dom and cod() == cod
	 */
	public SPORuleMorphism(SPORule dom, SPORule cod) {
		this.domain = dom;
		this.codomain = cod;
		this.left = DefaultMorphism.prototype.createMorphism(dom.lhs(), cod.lhs());
		this.right = DefaultMorphism.prototype.createMorphism(dom.rhs(), cod.rhs());
		this.fixed = false;
	}

	/**
	 * Constructs a new SPORuleMorphism
	 * @param dom
	 * @param cod
	 * @param leftMorph
	 * @param rightMorph
	 */
	public SPORuleMorphism(SPORule dom, SPORule cod, Morphism leftMorph, Morphism rightMorph) {
		this.domain = dom;
		this.codomain = cod;
		this.left = leftMorph;
		this.right = rightMorph;
		this.fixed = false;
	}
	
	/**
	 * Constructs an empty SPORuleMorphism
	 */
	private SPORuleMorphism() {
		// Empty constructor
	}
	
	/**
	 * @see groove.graph.Morphism#after(groove.graph.Morphism)
	 */
	public SPORuleMorphism after(SPORuleMorphism morph) {
		SPORuleMorphism concat = SPORuleMorphism.prototype.createMorphism(morph.domain, this.codomain);
		concat.left = this.left.after(morph.left);
		concat.right = this.right.after(morph.right);
		return concat;
	}

	/**
	 * @see groove.graph.Morphism#afterInverse(groove.graph.Morphism)
	 */
	public SPORuleMorphism afterInverse(SPORuleMorphism morph) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the target rule for this mapping
	 * @ensure result != null
	 */
	public SPORule cod() {
		return this.codomain;
	}

	/**
	 * Returns the source rule for this mapping
	 * @ensure result != null
	 */
	public SPORule dom() {
		return this.domain;
	}
	
	/**
	 * Returns the left (graph)morphism of this rule morphism
	 * @ensure result != null and result.dom() == dom().lhs() and result.cod() == cod.lhs()
	 */
	public Morphism getLeftMorphism() {
		return this.left;
	}

	/**
	 * Returns the right (graph)morphism of this rule morphism
	 * @ensure result != null and result.dom() == dom().rhs() and result.cod() == cod.rhs()
	 */
	public Morphism getRightMorphism() {
		return this.right;
	}

	/**
	 * @see groove.graph.Morphism#createMorphism(groove.graph.Graph, groove.graph.Graph)
	 */
	public SPORuleMorphism createMorphism(SPORule dom, SPORule cod) {
		return new SPORuleMorphism(dom, cod);
	}

	/**
	 * @see groove.graph.Morphism#createMorphism(groove.graph.Graph, groove.graph.Graph)
	 */
	public SPORuleMorphism createMorphism(SPORule dom, SPORule cod, Morphism leftMorph, Morphism rightMorph) {
		return new SPORuleMorphism(dom, cod, leftMorph, rightMorph);
	}

	/**
	 * @see groove.graph.Morphism#equals(groove.graph.Morphism)
	 */
	public boolean equals(SPORuleMorphism other) {
		return false;
	}

	/**
	 * @see groove.graph.Morphism#inverseThen(groove.graph.Morphism)
	 */
	public Morphism inverseThen(Morphism morph) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see groove.graph.Morphism#isFixed()
	 */
	public boolean isFixed() {
		return this.fixed;
	}

	/**
	 * @see groove.graph.Morphism#isInjective()
	 */
	public boolean isInjective() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see groove.graph.Morphism#isSurjective()
	 */
	public boolean isSurjective() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see groove.graph.Morphism#isTotal()
	 */
	public boolean isTotal() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * After this, the morphism should no longer be modified. Both
	 * left and right morphisms will become fixed.
	 * @see groove.graph.Morphism#setFixed()
	 * @ensure isFixed() == true
	 */
	public void setFixed() {
		this.fixed = true;
		this.left.setFixed();
		this.right.setFixed();
	}

	/**
	 * @see groove.graph.Morphism#then(groove.graph.Morphism)
	 */
	public SPORuleMorphism then(SPORuleMorphism morph) {
		SPORuleMorphism concat = SPORuleMorphism.prototype.createMorphism(this.domain, morph.codomain);
		concat.left = this.left.then(morph.left);
		concat.right = this.right.then(morph.right);
		return concat;
	}

}
