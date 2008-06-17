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
 * $Id: StrategiesTest.java,v 1.1 2008-03-18 10:02:55 iovka Exp $
 */
package groove.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.explore.util.PriorityCache;
import groove.explore.util.SimpleCache;
import groove.graph.Graph;
import groove.io.AspectualViewGps;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StartGraphState;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.trans.RuleNameLabel;
import groove.trans.SystemRecord;
import groove.trans.Rule;
import groove.view.FormatException;
import junit.framework.TestCase;

/** Simple tests for the utility classes for strategies and for strategies.
 * The samples used by this class are specifically made for the tests.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class StrategiesTest extends TestCase {

	/** Where to find the grammars */
	private String PATH_PREFIX = "junit/samples/";
	
	
	/** Testing a grammar with priorities. */
	public void testMatchesIter1 () {
		{  // first part
		// load the grammar, with different start states
		int nb = 4;  // the number of start states
		GraphGrammar[] grammars = new GraphGrammar[nb];
		
		try {
			for (int i = 0; i < nb; i++) {
				grammars[i] = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"matchesiter.gps"), "start"+i).toGrammar();
			}
		} catch (FormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 

		SystemRecord[] records = new SystemRecord[nb];
		for (int i = 0; i < nb; i++) {
			records[i] = (new GTS(grammars[i])).getRecord();
		}

		Graph[] start = new Graph[nb];
		for (int i = 0; i < nb; i++) {
			start[i] = grammars[i].getStartGraph();
		}

		ArrayList<Collection<RuleMatch>> theMatches = new ArrayList<Collection<RuleMatch>>();
		MatchesIterator[] iter = new MatchesIterator[nb];
		

		for (int i = 0; i < nb; i++) {
			// if (i != 2) { continue; }
			GraphState state = new StartGraphState(records[i], start[i]);
			iter[i] = new MatchesIterator(state, records[i].createCache(state, false, false));
			theMatches.add(i, new ArrayList<RuleMatch>());
			while (iter[i].hasNext()) {
				theMatches.get(i).add(iter[i].next());
			}
		}
		
		// the expected matches (rule names)
		ArrayList<ArrayList<RuleNameLabel>> expected = new ArrayList<ArrayList<RuleNameLabel>>(4);
		for (int i = 0; i < nb; i++) {
			expected.add(new ArrayList<RuleNameLabel>());
		}
		expected.get(0).add(grammars[0].getRule("a").getName());
		expected.get(0).add(grammars[0].getRule("b").getName());
		
		expected.get(1).add(grammars[0].getRule("c").getName());
		expected.get(1).add(grammars[0].getRule("d").getName());
		expected.get(1).add(grammars[0].getRule("d").getName());

		expected.get(2).add(grammars[0].getRule("e").getName());

		// the computed matches (rule names)
		ArrayList<ArrayList<RuleNameLabel>> computed = new ArrayList<ArrayList<RuleNameLabel>>();
		for (int i = 0; i < nb; i++) {
			computed.add(new ArrayList<RuleNameLabel>());
			for (RuleMatch rm : theMatches.get(i)) {
				computed.get(i).add(rm.getRule().getName());
			}
		}
		
		assertEquals(2, theMatches.get(0).size()); // a, b
		assertTrue(computed.get(0).containsAll(expected.get(0)));
		assertTrue(expected.get(0).containsAll(computed.get(0)));
		
		assertEquals(2, theMatches.get(1).size()); // c, d (d only once)
		assertTrue(computed.get(1).containsAll(expected.get(1)));
		assertTrue(expected.get(1).containsAll(computed.get(1)));
		
		
		assertEquals(1, theMatches.get(2).size()); // e
		assertTrue(computed.get(2).containsAll(expected.get(2)));
		assertTrue(expected.get(2).containsAll(computed.get(2)));

		assertEquals(0, theMatches.get(3).size()); // 
		} // end first part
		{ // second part
		
		// Test for the isEndRule flag
		GraphGrammar grammar = null;
		try {
			grammar = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"matchesiter.gps"), "start4").toGrammar();
		} catch (FormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SystemRecord record = (new GTS(grammar).getRecord());
		GraphState state = new StartGraphState(record, grammar.getStartGraph());
		MatchesIterator iter = new MatchesIterator(state, record.createCache(state, false, false));
		
		RuleMatch match;  // for debugging purpose
		assertFalse(iter.isEndRule());
		match = iter.next(); // <g>
		assertFalse(iter.isEndRule());
		match = iter.next(); // <g>
		assertFalse(iter.isEndRule());
		match = iter.next(); // <g>
		assertFalse(iter.isEndRule());
		match = iter.next(); // <g>
		assertTrue(iter.isEndRule());
		match = iter.next(); // <h>
		assertTrue(iter.isEndRule());
		match = iter.next(); // <i>
		assertFalse(iter.isEndRule());
		match = iter.next(); // <i>
		assertTrue(iter.isEndRule());	
		
		iter = new MatchesIterator(state, record.createCache(state, false, false));
		assertFalse(iter.isEndRule());
		iter.hasNext();
		assertFalse(iter.isEndRule());
		match = iter.next(); 
		match = iter.next(); 
		match = iter.next(); // <g> three times
		assertFalse(iter.isEndRule());
		iter.hasNext();
		assertFalse(iter.isEndRule());
		match = iter.next(); // <g> 
		assertTrue(iter.isEndRule());
		iter.hasNext();
		assertTrue(iter.isEndRule());
		match = iter.next(); // <h>
		assertTrue(iter.isEndRule());
		iter.hasNext();
		assertTrue(iter.isEndRule());
				
		} // end second part
	}
	
	/** Testing a grammar without enabled rules. */
	public void testMatchesIter2 () {
		
		// load the grammar
		GraphGrammar grammar = null;
		
		try {
			grammar = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"matchesiter2.gps"), "start").toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		SystemRecord record = new GTS(grammar).getRecord();
		Collection<RuleMatch> theMatches = new ArrayList<RuleMatch>();
		GraphState state = new StartGraphState(record, grammar.getStartGraph());
		MatchesIterator iter = new MatchesIterator(state, record.createCache(state, false, false));
		while (iter.hasNext()) {
				theMatches.add(iter.next());
		}
		
		assertEquals(0, theMatches.size());  
	}

	/** Testing the simple explore cache. */
	public void testExploreCache1 () {
		
		// load the grammar
		GraphGrammar grammar = null;
		
		try {
			grammar = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"exploreCache1.gps")).toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		Rule a = grammar.getRule(new NameLabel("a"));
		Rule b = grammar.getRule(new NameLabel("b"));
		Rule c = grammar.getRule(new NameLabel("c"));
		Rule d = grammar.getRule(new NameLabel("d"));
		ArrayList<RuleNameLabel> allRules = new ArrayList<RuleNameLabel>(4);
		allRules.add(a.getName()); allRules.add(b.getName()); 
		allRules.add(c.getName()); allRules.add(d.getName()); 
		
		
		ExploreCache cache;
		ArrayList<RuleNameLabel> list;
		
		// -----------
		{
			cache = new SimpleCache(grammar.getRules(), false);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(allRules, list);
		}

		// -----------
		{
			cache = new SimpleCache(grammar.getRules(), true);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(4, list.size());
			assertTrue(list.containsAll(allRules));
		}
		
		// ----------- 
		{
			cache = new SimpleCache(grammar.getRules(), false);
			list = new ArrayList<RuleNameLabel>();
			ArrayList<RuleNameLabel> expected = (ArrayList<RuleNameLabel>) allRules.clone();
			expected.remove(b.getName());
			expected.remove(a.getName());
			cache.updateExplored(b);
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(expected, list);

		}
		
		// ----------- 
		{
			cache = new SimpleCache(grammar.getRules(), false);
			list = new ArrayList<RuleNameLabel>();
			cache.updateMatches(b);
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(allRules, list);
		}
		
		// ----------- 
		{
			cache = new SimpleCache(grammar.getRules(), false);
			list = new ArrayList<RuleNameLabel>();
			cache.updateExplored(d);
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(0, list.size());
		}
		
		// ----------- 
		{
			cache = new SimpleCache(grammar.getRules(), true);
			list = new ArrayList<RuleNameLabel>();
			ArrayList<RuleNameLabel> expected = (ArrayList<RuleNameLabel>) allRules.clone();
			expected.remove(b.getName());
			cache.updateExplored(b);
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(3, list.size());
			assertTrue(list.containsAll(expected));
		}
		
		// ----------- 
		{
			cache = new SimpleCache(grammar.getRules(), true);
			list = new ArrayList<RuleNameLabel>();
			cache.updateMatches(b);
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(4, list.size());
			assertTrue(list.containsAll(allRules));
		}
	}
	
	
	/** Testing the explore cache with priorities. */
	public void testExploreCache2 () {
		
		// load the grammar
		GraphGrammar grammar = null;
		
		try {
			grammar = (new AspectualViewGps()).unmarshal(new File(PATH_PREFIX+"exploreCache2.gps")).toGrammar();
		} catch (FormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		Rule a8 = grammar.getRule(new NameLabel("a8"));
		Rule b8 = grammar.getRule(new NameLabel("b8"));
		Rule a5 = grammar.getRule(new NameLabel("a5"));
		Rule b5 = grammar.getRule(new NameLabel("b5"));
		Rule c5 = grammar.getRule(new NameLabel("c5"));
		Rule a0 = grammar.getRule(new NameLabel("a0"));
		Rule b0 = grammar.getRule(new NameLabel("b0"));
		ArrayList<RuleNameLabel> allRules = new ArrayList<RuleNameLabel>();
		allRules.add(a8.getName()); allRules.add(b8.getName()); 
		allRules.add(a5.getName()); allRules.add(b5.getName()); allRules.add(c5.getName());
		allRules.add(a0.getName()); allRules.add(b0.getName());
		allRules.trimToSize();
		
		ExploreCache cache;
		ArrayList<RuleNameLabel> list;

		// all the rules (none matches)
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(allRules, list);
		}
		
		// All the rules in some order (none matches)
		{
			cache = new PriorityCache(grammar.getRuleMap(), true);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(7, list.size());
			assertTrue(list.containsAll(allRules));
		}
		
		// A rule of priority 5 matches and is fully explored
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			
			ArrayList<RuleNameLabel> expected = new ArrayList<RuleNameLabel>();
			expected.add(c5.getName());
			cache.updateMatches(b5);
			cache.updateExplored(b5);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(expected, list);
		}
		
		// A rule of priority 0 matches (not fully explored)
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			ArrayList<RuleNameLabel> expected =  new ArrayList<RuleNameLabel>();
			expected.add(a0.getName()); expected.add(b0.getName()); 
			cache.updateMatches(b0);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(expected, list);
		}
		
		// A rule of priority 8 matches (not fully explored)
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			ArrayList<RuleNameLabel> expected = new ArrayList<RuleNameLabel>();
			expected.add(a8.getName()); expected.add(b8.getName()); 
			cache.updateMatches(a8);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(expected, list);
		}
		
		// A rule of priority 8 matches (fully explored)
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			ArrayList<RuleNameLabel> expected = new ArrayList<RuleNameLabel>();
			expected.add(b8.getName()); 
			cache.updateMatches(a8);
			cache.updateExplored(a8);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(expected, list);
		}
		
		// Nothing to be returned
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			cache.updateMatches(b0);
			cache.updateExplored(b0);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertEquals(0, list.size());
		}
		
		// A rule of priority 5 matches and is fully explored. Randomized.
		{
			cache = new PriorityCache(grammar.getRuleMap(), true);
			ArrayList<RuleNameLabel> expected = new ArrayList<RuleNameLabel>();
			expected.add(a5.getName()); expected.add(c5.getName());
			cache.updateMatches(b5);
			cache.updateExplored(b5);
			list = new ArrayList<RuleNameLabel>();
			while (cache.hasNext()) {
				list.add(cache.next().getName());
			}
			assertTrue(expected.containsAll(list));
			assertTrue(list.containsAll(expected));
		}
	
		// Exceptions and so
		{
			cache = new PriorityCache(grammar.getRuleMap(), false);
			ArrayList<RuleNameLabel> expected = new ArrayList<RuleNameLabel>();
			expected.add(a8.getName()); expected.add(b8.getName());
			list = new ArrayList<RuleNameLabel>();
			list.add(cache.next().getName());
			int i = 0;
			while (cache.hasNext() && i < 20) {
				i++;
			}
			list.add(cache.next().getName());
			assertEquals(expected, list);
		}
		
	}
}
