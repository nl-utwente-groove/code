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
 * $Id: MatchesIterator.java,v 1.4 2008-03-17 16:27:16 iovka Exp $
 */
package groove.explore.util;

import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleMatch;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/** Iterates over all matches of a collection of rules into a graph.
 * The rules are given as an {@link ExploreCache}.
 * This implementation is suitable for iterators intended to iterate over 
 * several matches (and not for testing the existence of a match).
 * @author Iovka Boneva
 * @version $Revision: 1.4 $
 */
public class MatchesIterator implements Iterator<RuleMatch> {
	public boolean hasNext() {
		goToNext();
		return matchIter != null && this.matchIter.hasNext();
	}

	public RuleMatch next() {
		goToNext();
		if (this.matchIter == null) { throw new NoSuchElementException(); }
		RuleMatch result = matchIter.next();
		this.rulesIter.updateMatches(this.currentRule);
		this.isEndRule = ! matchIter.hasNext();
		return result;
	}

	/** Allows to know whether there are more matches 
	 * for the rule corresponding to the rule match returned
	 * by the last call of {@link #next()}.
	 * @return <code>true</code> in one of the two situations:
	 * 1) there are no next matches, or 2) there are new matches, and the match
	 * returned by the following call of {@link #next()} is for 
	 * a different rule than the match returned by the previous call
	 * of  {@link #next()}. Returns <code>false</code> in one of the two
	 * situations : 1) {@link #next()} was never called before, 2) the 
	 * match returned by the following call of {@link #next()} is for the
	 * same rule as the match returned by the previous call.
	 */
	public boolean isEndRule() {
		return this.isEndRule;
	}
	
	
	/**
	 * Unsupported method.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/** Increments the rule iterator.
	 * Also initializes {@link #matchIter}, except if this iterator is consumed.
	 * @return <code>true</code> if the rules iterator is not consumed
	 */
	protected boolean nextRule() {
		this.rulesIter.updateExplored(currentRule);
		if (!this.rulesIter.hasNext()) { // this iterator is entirely consumed 
			this.matchIter = null;
			return false;
		}
		this.currentRule = rulesIter.next();  
		this.matchIter = this.currentRule.getMatchIter(this.state.getGraph(), null);
		return true;
	}
	
	/** Increments the rule iterator after the creation of this 
	 * matches iterator. Also initializes {@link #matchIter}, 
	 * except if this iterator is consumed.
	 * This is different from the general {@link #nextRule()}
	 * as some additional treatment is performed for the first rule.
	 */
	protected void firstRule() {
		this.currentRule = rulesIter.last();
		if (this.currentRule == null) {
			// this means that rulesIter is freshly created and has never been incremented before
			if (!this.rulesIter.hasNext()) {  // this iterator is entirely consumed 
				this.matchIter = null;
				return;
			}
			this.currentRule = rulesIter.next();
		}
		this.matchIter = this.currentRule.getMatchIter(this.state.getGraph(), null);
	}
	
	/** This method insures that matchIter is incremented until the next element to be returned,
	 * or set to null if no more elements are available.
	 * The method is idempotent (several successive calls have the same effect as a unique call).
	 */ 
	protected void goToNext () {
		while (this.matchIter != null && !this.matchIter.hasNext() && nextRule()) {
			// empty
		}
		
	}
	
	
	// ---------------------------------------------------------------
	// CONSTRUCTOR, FIELDS ETC.
	// ---------------------------------------------------------------
	/**
	 * Constructs a new matches iterator for a given state, updating the cache given as parameter. 
	 * @param state 
	 * @param rules 
	 */
	public MatchesIterator (GraphState state, ExploreCache rules) {
		this.rulesIter = rules;
		this.state = state;
		firstRule();
		goToNext();
		this.isEndRule = false; 
	}

	/** Collects the remaining matches for the iterator. 
	 * @param matches 
	 * */
	public void collectMatches(Set<RuleMatch> matches) {
		while( this.hasNext()) {
			matches.add(this.next());
		}
	}
	
//	/** True when the last call to {@link #next()} returned
//	 * the last matching for a rule. False otherwise */
//	protected final boolean getEndRule() {
//		return this.isEndRule;
//	}
//	/** Set to true when the */
//	protected final void setEndRule(boolean b) {
//		this.isEndRule = b;
//	}
	
	
	/** The currently explored rule. */
	protected Rule currentRule;
	/** An iterator over the set of rules to be explored. */
	protected ExploreCache rulesIter;
	/** The state for which the matches iterator is computed. Set at construction time. */
	protected final GraphState state;
	/** After initialization, mathIter is null means that the iterator is consumed. */
	protected Iterator<RuleMatch> matchIter;
	/** Set to true when the last match for a given rule has been returned. */
	protected boolean isEndRule;
}
