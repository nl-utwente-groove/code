// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/**
 * 
 */
package groove.test;

import groove.util.Reporter;
import groove.util.TreeHashSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class to test the various implementations of {@link groove.util.IntSet} regarding speed.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class SetSpeedTest {
	static private final int SMALL_SAMPLE_SIZE = 100;
	static private final int LARGE_SAMPLE_SIZE = 5000000;
	static private final int SMALL_REPEAT_FACTOR = 25000;
	static private final int LARGE_REPEAT_FACTOR = 1;
	
	static private final Object[] SMALL_RANDOM_SAMPLE = new Object[SMALL_SAMPLE_SIZE];
	static private final Object[] LARGE_RANDOM_SAMPLE = new Object[LARGE_SAMPLE_SIZE];
	static private final Object[] SMALL_REGULAR_SAMPLE = new Object[SMALL_SAMPLE_SIZE];
	static private final Object[] LARGE_REGULAR_SAMPLE = new Object[LARGE_SAMPLE_SIZE];
	static private final Object[] DISTINCT_SAMPLE = new Object[LARGE_SAMPLE_SIZE];
	static {
		Set<Object> sampleSet = new HashSet<Object>();
		for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
			LARGE_REGULAR_SAMPLE[i] = new Integer(i - (LARGE_SAMPLE_SIZE/2));
			sampleSet.add(LARGE_REGULAR_SAMPLE[i] = new Integer(i - (LARGE_SAMPLE_SIZE/2)));
			if (i < SMALL_SAMPLE_SIZE) {
				SMALL_REGULAR_SAMPLE[i] = LARGE_REGULAR_SAMPLE[i];
			}
		}
		for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
			Integer random;
			do {
				random = new Integer((int) (Integer.MAX_VALUE * Math.random()));
				if (i % 2 == 0) {
					random = new Integer(-random.intValue());
				}
			} while (sampleSet.contains(random));
			sampleSet.add(LARGE_RANDOM_SAMPLE[i] = random);
			if (i < SMALL_SAMPLE_SIZE) {
				SMALL_RANDOM_SAMPLE[i] = LARGE_RANDOM_SAMPLE[i];
			}
		}
		for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
			Integer random;
			do {
				random = new Integer((int) (Integer.MAX_VALUE * Math.random()));
				if (i % 2 == 0) {
					random = new Integer(-random.intValue());
				}
			} while (sampleSet.contains(random));
			sampleSet.add(DISTINCT_SAMPLE[i] = random);
		}
	}
	
	static int OVERALL_INDEX = 0;
	static int FIRST_INDEX = 1;
	static int SECOND_INDEX = 2;
	static int CONTAINS_YES_INDEX = 3;
	static int CONTAINS_NO_INDEX = 4;
	static int ITERATOR_INDEX = 5;
	static int EQUALS_INDEX = 6;
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		new SetSpeedTest(new HashSet<Object>(SMALL_SAMPLE_SIZE)).start();
//		new SetSpeedTest(new BitHashSet()).start();
//		new SetSpeedTest(new TreeStoreSet(1, SMALL_SAMPLE_SIZE, TreeStoreSet.HASHCODE_EQUATOR)).start();
//		new SetSpeedTest(new TreeHashSet(2, SMALL_SAMPLE_SIZE, TreeHashSet.HASHCODE_EQUATOR)).start();
		new SetSpeedTest(new TreeHashSet<Object>(SMALL_SAMPLE_SIZE, 3, 7, TreeHashSet.HASHCODE_EQUATOR)).start();
	}
	
	public SetSpeedTest(Set<Object> object) {
		this.object = object;
	}
	
	public void start() {
		System.out.println("Results for "+object.getClass());
		double smallRegularSpace = test(SMALL_REGULAR_SAMPLE, SMALL_REPEAT_FACTOR, TEST_REGULAR_SMALL);
		report(TEST_REGULAR_SMALL, smallRegularSpace);
		double smallRandomSpace = test(SMALL_RANDOM_SAMPLE, SMALL_REPEAT_FACTOR, TEST_RANDOM_SMALL);
		report(TEST_RANDOM_SMALL, smallRandomSpace);
		double largeRegularpace = test(LARGE_REGULAR_SAMPLE, LARGE_REPEAT_FACTOR, TEST_REGULAR_LARGE);
		report(TEST_REGULAR_LARGE, largeRegularpace);
		double largeRandomSpace = test(LARGE_RANDOM_SAMPLE, LARGE_REPEAT_FACTOR, TEST_RANDOM_LARGE);
		report(TEST_RANDOM_LARGE, largeRandomSpace);
	}

	@SuppressWarnings("unchecked")
	private double test(Object[] sample, int repeatFactor, int[] measures) {
		reporter.start(measures[OVERALL_INDEX]);
		reporter.start(measures[FIRST_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			object.clear();
			for (int i = 0; i < sample.length; i++) {
				if (!object.add(sample[i])) {
					throw new IllegalStateException();
				}
			}
		}
		reporter.stop();
		reporter.start(measures[SECOND_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			for (int i = 0; i < sample.length; i++) {
				if (object.add(sample[i])) {
					throw new IllegalStateException();
				}
			}
		}
		reporter.stop();
		reporter.start(measures[CONTAINS_YES_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			for (int i = 0; i < sample.length; i++) {
				if (!object.contains(sample[i])) {
					throw new IllegalStateException();
				}
			}
		}
		reporter.stop();
		Object[] distinct = DISTINCT_SAMPLE;
		reporter.start(measures[CONTAINS_NO_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			for (int i = 0; i < sample.length; i++) {
				Object sampleObject = distinct[i];
				if (object.contains(sampleObject)) {
					throw new IllegalStateException();
				}
			}
		}
		reporter.stop();
		reporter.start(measures[ITERATOR_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			Iterator <Object>iter = object.iterator();
			while (iter.hasNext()) {
				iter.next();
			}
		}
		reporter.stop();
		reporter.stop();
		Object newObject;
		if (object instanceof HashSet) {
			newObject = new HashSet<Object>(object);
		} else {
			newObject = new TreeHashSet<Object>((TreeHashSet) object);
		}
		reporter.start(OVERALL_INDEX);
		reporter.start(measures[EQUALS_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			if (!object.equals(newObject)) {
				throw new IllegalStateException();
			}
		}
		reporter.stop();
		reporter.stop();
		if (object instanceof TreeHashSet) {
				return ((TreeHashSet) object).getBytesPerElement();
		} else {
			return 0;
		}
	}
	
	private void report(int[] measures, double space) {
		for (int i = 0; i < measures.length; i++) {
			int measure = measures[i];
			System.out.println(reporter.getMethodName(measure)+reporter.getTotalTime(measure));
		}
		if (space > 0) {
			System.out.println("Bytes per element: "+space);
		}
	}
	
	private final Set<Object> object;
	
	static private final Reporter reporter = Reporter.register(IntSetSpeedTest.class);
	private final int[] TEST_RANDOM_SMALL = new int[] {
			reporter.newMethod("Random, small sample:  "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: "),
			reporter.newMethod("    Positive containment: "),
			reporter.newMethod("    Negative containment: "),
			reporter.newMethod("                Iterator: "),
			reporter.newMethod("             Equals test: ")
			};
	private final int[] TEST_RANDOM_LARGE = new int[] {
			reporter.newMethod("Random, large sample:  "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: "),
			reporter.newMethod("    Positive containment: "),
			reporter.newMethod("    Negative containment: "),
			reporter.newMethod("                Iterator: "),
			reporter.newMethod("             Equals test: ")
			};
	private final int[] TEST_REGULAR_SMALL = new int[] {
			reporter.newMethod("Regular, small sample: "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: "),
			reporter.newMethod("    Positive containment: "),
			reporter.newMethod("    Negative containment: "),
			reporter.newMethod("                Iterator: "),
			reporter.newMethod("             Equals test: ")
			};
	private final int[] TEST_REGULAR_LARGE = new int[] {
			reporter.newMethod("Regular, large sample: "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: "),
			reporter.newMethod("    Positive containment: "),
			reporter.newMethod("    Negative containment: "),
			reporter.newMethod("                Iterator: "),
			reporter.newMethod("             Equals test: ")
			};
}
