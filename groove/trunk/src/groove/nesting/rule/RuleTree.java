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
 * $Id: RuleTree.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import groove.nesting.NestingAspectValue;
import groove.trans.SPORule;
import groove.util.Pair;
import groove.view.aspect.AspectValue;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 * 
 */
@Deprecated
public class RuleTree {

	/** Top rule of this RuleTree */
	private SPORule root;
	/** Set of sub RuleTrees */
	private Set<Pair<SPORuleMorphism, RuleTree>> subTrees;
	/** Variable indicating whether this RuleTree is fixed */
	private boolean fixed;
	/** Aspect Value indicating which level was used to create this subtree */
	private AspectValue levelType;
	
	/**
	 * Constructs a new RuleTree
	 * @param root top rule of this RuleTree
	 * @ensure root() == root
	 */
	public RuleTree(SPORule root) {
		this.subTrees = new HashSet<Pair<SPORuleMorphism, RuleTree>> ();
		this.root = root;
		this.fixed = false;
	}
	
	/**
	 * Returns the root of this RuleTree
	 * @ensure result != null
	 */
	public SPORule root() {
		return this.root;
	}
	
	/**
	 * Get the set of subtrees
	 * @ensure result != null
	 */
	public Set<Pair<SPORuleMorphism, RuleTree>> getSubTrees() {
		return Collections.unmodifiableSet(this.subTrees);
	}
	
	/**
	 * Add a new subtree to this ruletree
	 * @param morph morphism from root() to subtree.root()
	 * @param subtree the next ruletree
	 */
	public void addSubTree(SPORuleMorphism morph, RuleTree subtree) {
		Pair<SPORuleMorphism, RuleTree> newSub = new Pair<SPORuleMorphism, RuleTree> (morph, subtree);
		this.subTrees.add(newSub);
	}
	
	/**
	 * Indicates whether this ruletree can still be modified
	 * @return false if it can be modified, true otherwise
	 */
	public boolean isFixed() {
		return this.fixed;
	}
	
	/**
	 * After this, the ruletree can no longer be altered. The
	 * effect of the addSubTree method becomes unpredictable. 
	 * @ensure isFixed() == true
	 */
	public void setFixed() {
		this.fixed = true;
		subTrees = Collections.unmodifiableSet(this.subTrees);
	}
	
	public void setLevelType(AspectValue value) {
		this.levelType = value;
	}
	
	public AspectValue getLevelType() {
		return this.levelType;
	}
	
}

