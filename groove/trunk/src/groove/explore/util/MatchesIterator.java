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
		this.isEndRule = false;
		return result;
	}

	/** Allows to know whether there are more matches 
	 * for the rule corresponding to the rule match returned
	 * by the last call of {@link #next()}.
	 * @return <code>true</code> if the following call of {@link #next()}
	 * will return a rule match with a rule different from the one
	 * of the previous call of {@link #next()}. Returns <code>false</code> in
	 * all other cases (comprising the case when {@link #next()} was
	 * never called before).
	 */
	public boolean isEndRule() {
		// the following call to goToNext() will set isEndRule to true only if 
		// the rules iterator is incremented
		goToNext();  // no harm, as the goToNext() method is idempotent
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
		this.isEndRule = true;
		return true;
	}
	
	/** Increments the rule iterator after the creation of this 
	 * matches iterator. Also initializes {@link #matchIter}, 
	 * except if this iterator is consumed.
	 * This incrementation is different from the general {@link #nextRule()}
	 * as some additional treatment is performed on the first incrementation 
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
		this.isEndRule = true;
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
	
	
	protected Rule currentRule;
	protected ExploreCache rulesIter;
	protected GraphState state;
	/** After initialization, mathIter is null means that the iterator is consumed. */
	protected Iterator<RuleMatch> matchIter;
	protected boolean isEndRule;
	
}
