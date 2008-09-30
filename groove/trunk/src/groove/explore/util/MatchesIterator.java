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
 * $Id: MatchesIterator.java,v 1.5 2008-03-17 17:44:06 iovka Exp $
 */
package groove.explore.util;

import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterates over all matches of a collection of rules into a graph.
 * The rules are given as an {@link ExploreCache}.
 * This implementation is suitable for iterators intended to iterate over 
 * several matches (and not for testing the existence of a match).
 * @author Iovka Boneva
 * @version $Revision$
 */
public class MatchesIterator implements Iterator<RuleEvent> {
    /**
     * Constructs a new matches iterator for a given state, updating the cache given as parameter.
     */
    public MatchesIterator (GraphState state, ExploreCache rules, SystemRecord record) {
        this(state, rules, true, record);
        firstRule();
        goToNext();
        this.isEndRule = false; 
    }

    /** A minimal constructor, to be used by sub-classes.
     * @param protect just for different signature 
     * @param record TODO*/
    protected MatchesIterator (GraphState state, ExploreCache rules, boolean protect, SystemRecord record) {
        this.state = state;
        this.rulesIter = rules;
        this.record = record;
    }
    
	public boolean hasNext() {
	    reporter.start(HAS_NEXT);
		goToNext();
		boolean result = eventIter != null && this.eventIter.hasNext();
		reporter.stop();
		return result;
	}

	public RuleEvent next() {
        reporter.start(NEXT);
		goToNext();
		if (this.eventIter == null) { throw new NoSuchElementException(); }
		RuleEvent result = eventIter.next();
		this.rulesIter.updateMatches(this.currentRule);
		this.isEndRule = ! eventIter.hasNext();
        reporter.stop();
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

	/** Increments the rule iterator after the creation of this 
     * matches iterator. Also initializes {@link #eventIter}, 
     * except if this iterator is consumed.
     * This is different from the general {@link #nextRule()}
     * as some additional treatment is performed for the first rule.
     */
    protected void firstRule() {
    	this.currentRule = rulesIter.last();
    	if (this.currentRule == null) {
    		// this means that rulesIter is freshly created and has never been incremented before
    		if (!this.rulesIter.hasNext()) {  // this iterator is entirely consumed 
    			this.eventIter = null;
    			return;
    		}
    		this.currentRule = rulesIter.next();
    	}
    	this.eventIter = createEventIter(currentRule);
    }

    /** Increments the rule iterator.
	 * Also initialises {@link #eventIter}, except if this iterator is consumed.
	 * @return <code>true</code> if the rules iterator is not consumed
	 */
	protected boolean nextRule() {
		this.rulesIter.updateExplored(currentRule);
		if (!this.rulesIter.hasNext()) { // this iterator is entirely consumed 
			this.eventIter = null;
			return false;
		} else {
		    this.currentRule = rulesIter.next();
		    this.eventIter = createEventIter(currentRule);
		    return true;
		}
	}
	
	/** This method insures that matchIter is incremented until the next element to be returned,
	 * or set to null if no more elements are available.
	 * The method is idempotent (several successive calls have the same effect as a unique call).
	 */ 
	protected void goToNext () {
		while (this.eventIter != null && !this.eventIter.hasNext() && nextRule()) {
			// empty
		}
	}
	
	/** Callback method to create an iterator over the matches of a given rule. */
	protected Iterator<RuleEvent> createEventIter(Rule rule) {
		if (COLLECT_ALL_MATCHES) {
			List<RuleEvent> result = new ArrayList<RuleEvent>();
			for (RuleMatch match: rule.getMatches(state.getGraph(), null)) {
				result.add(record.getEvent(match));
			}
			return result.iterator();
		} else {
			return new TransformIterator<RuleMatch,RuleEvent>(rule.getMatchIter(state.getGraph(), null)) {
				@Override
				protected RuleEvent toOuter(RuleMatch from) {
					return record.getEvent(from);
				}
			};
		}
	}
	
	/** The currently explored rule. */
	protected Rule currentRule;
	/** An iterator over the set of rules to be explored. */
	protected ExploreCache rulesIter;
	/** The state for which the matches iterator is computed. Set at construction time. */
	protected final GraphState state;
	/** After initialisation, mathIter is null means that the iterator is consumed. */
	protected Iterator<RuleEvent> eventIter;
	/** Set to true when the last match for a given rule has been returned. */
	protected boolean isEndRule;
	/** System record to create {@link RuleEvent}s out of {@link RuleMatch}es. */
	private final SystemRecord record;
	
	/** Flag to collect all matches at once, rather than doing a true iteration. */
	private final boolean COLLECT_ALL_MATCHES = true;
    static final Reporter reporter = Reporter.register(MatchSetCollector.class);
    static final int CONSTRUCT = reporter.newMethod("constructor");
    private static final int HAS_NEXT = reporter.newMethod("hasNext");
    private static final int NEXT = reporter.newMethod("next");
    static final int COMPUTE_ALIAS_MAP = reporter.newMethod("computeAliasedMatches");
}
