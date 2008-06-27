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
 * $Id: TestingMyHashSet.java,v 1.1 2007-11-28 15:35:08 iovka Exp $
 */
package groove.test.abs;

import groove.abs.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.abs.MyHashSet.Equalizer;
import groove.abs.MyHashSet.Hasher;

import junit.framework.TestCase;

/** Tests for {@link MyHashSet} and {@link MyHashSetExtended}. 
 * These tests are interesting if the store capacity of {@link MyHashSet} is small (5).
 * @author Iovka Boneva
 * @version $Revision $
 */
public class TestingMyHashSet extends TestCase {

	/** Two strings are equal if they are equal independently on the case.
	 * The hash code is computed from the lower-case of the first and last characters.
	 */
	class StringHasher implements Hasher<String> {
		public int getHashCode(String o) {
			if (o.length() == 0) { return 0; }
			String lower = o.toLowerCase();
			return lower.charAt(0) + lower.charAt(lower.length() - 1);
		}
		public boolean areEqual(String o1, String o2) {
			return o1.toLowerCase().equals(o2.toLowerCase());
		}
	}
	
	class StringConflictHasher extends StringHasher {
		@Override
		public int getHashCode(String o) { return 1; }
	}
	
	/** Two strings are equal if they have same first and last character, independently on the case. */
	class StringEqualizer implements Equalizer<String> {

		public boolean areEqual(String o1, String o2) {
			if (o1.length() == 0 && o2.length() == 0) { return true; }
			if (o1.length() == 0 || o2.length() == 0) { return false; }
			String l1 = o1.toLowerCase();
			String l2 = o2.toLowerCase();
			return l1.charAt(0) == l2.charAt(0) && l1.charAt(l1.length()-1) == l2.charAt(l2.length()-1);
		}
	}

	/** */
	public void testHashSet () throws AssertionError {
		// fill in a hash set
		MyHashSet<String> set =  new MyHashSet<String>(new StringHasher());
		String s, s2, s3;
		
		set.getAndAdd("");
		set.getAndAdd("abC");
		set.getAndAdd("deF");
		set.getAndAdd("hiJ");
		set.getAndAdd("klM");
		s = set.getAndAdd("noP"); assertNull(s);
		set.getAndAdd("qrS");
		set.getAndAdd("tuV");
		set.getAndAdd("wxY");
		set.getAndAdd("acc");
		set.getAndAdd("dff");
		s = set.getAndAdd("hjj"); assertNull(s);
		set.getAndAdd("kmm");
		s2 = "npp"; set.getAndAdd(s2);
		s3 = "qss"; set.getAndAdd(s3);
		set.getAndAdd("tvv"); 
		s = set.getAndAdd("wyy"); assertNull(s);
		
		s = set.getAndAdd("ABC");
		assertEquals(s, "abC");
		s = set.getAndAdd("TUV");
		assertEquals(s, "tuV");
		s = set.getAndAdd("NPP");
		assertTrue(s == s2);
		s = set.getAndAdd("Qss");
		assertTrue(s == s3);
	
		// test the iteration
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			it.next();
		}

		it = set.iterator();
		boolean exc = false;
		try {
			while (true) { it.next(); }
		} catch (java.util.NoSuchElementException e) {
			exc = true;
		}
		assertTrue(exc);
		
		exc = false;
		try {
			set.getAndAdd(null);
		} catch (NullPointerException e) {
			exc = true;
		}
		assertTrue(exc);
		
	}
	
	/** */
	public void testHashSetExtended () throws AssertionError {
		// fill in a hash set
		MyHashSetExtended<String> set =  new MyHashSetExtended<String>(new StringHasher());
		String s;

		Set<String> km_strings = new HashSet<String>();
		
		set.getAndAdd("");
		set.getAndAdd("abC");
		set.getAndAdd("deF");
		set.getAndAdd("hiJ");
		s = "klM"; set.getAndAdd(s); km_strings.add(s);
		set.getAndAdd("noP");
		set.getAndAdd("qrS");
		set.getAndAdd("tuV");
		set.getAndAdd("wxY");
		set.getAndAdd("acc");
		set.getAndAdd("dff");
		set.getAndAdd("hjj");
		s = "kmm"; set.getAndAdd(s); km_strings.add(s);
		set.getAndAdd("npp");
		set.getAndAdd("qss");
		set.getAndAdd("tvv"); 
		set.getAndAdd("wyy");
		s = "kmkmkmkmkmkmkmkm"; set.getAndAdd(s); km_strings.add(s);
		
		Iterator<String> it = set.getAllEquivalent("KM", new StringEqualizer());
		Set<String> values = new HashSet<String>();
		while (it.hasNext()) { values.add(it.next()); }

		assertEquals(values, km_strings);
	}
	/** */
	public void testHashSetConflicting () throws AssertionError {
		MyHashSet<String> set =  new MyHashSet<String>(new StringConflictHasher());
		
		set.getAndAdd("");     // 1
		set.getAndAdd("abC");  // 2
		set.getAndAdd("deF");  // 3
		set.getAndAdd("hiJ");  // 4
		set.getAndAdd("klM");  // 5
		set.getAndAdd("noP");  // 6
		set.getAndAdd("qrS");  // 7
		set.getAndAdd("tuV");  // 8
		set.getAndAdd("wxY");  // 9
		set.getAndAdd("");
		set.getAndAdd("acc");  // 10  
		set.getAndAdd("dff");  // 11
		set.getAndAdd("hjj");  // 12
		set.getAndAdd("KLM");
		set.getAndAdd("kmm");  // 13
		set.getAndAdd("npp");  // 14 
		set.getAndAdd("qss");  // 15
		set.getAndAdd("tvv");  // 16 
		set.getAndAdd("wyy");  // 17 
		set.getAndAdd("");
		
		set.getAndAdd("kMM");  
		set.getAndAdd("TVV");
		set.getAndAdd("KLM");
		set.getAndAdd("zz");  // 18
		
		Iterator<String> it = set.iterator();
		Set<String> values = new HashSet<String>();
		while (it.hasNext()) { values.add(it.next()); }
		
		assertEquals(values.size(), 18);
	}

}
