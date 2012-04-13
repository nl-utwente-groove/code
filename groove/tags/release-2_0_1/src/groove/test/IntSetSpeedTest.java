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
 * $Id: IntSetSpeedTest.java,v 1.3 2007-09-19 09:01:12 rensink Exp $
 */
package groove.test;

import groove.util.HashIntSet;
import groove.util.IntSet;
import groove.util.Reporter;
import groove.util.SplitTreeIntSet;
import groove.util.TreeIntSet;

/**
 * Class to test the various implementations of {@link groove.util.IntSet} regarding speed.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class IntSetSpeedTest {
	static private final int SMALL_SAMPLE_SIZE = 500;
	static private final int LARGE_SAMPLE_SIZE = 10000000;
	static private final int SMALL_REPEAT_FACTOR = 5000;
	static private final int LARGE_REPEAT_FACTOR = 1;
	
	static private final int[] SMALL_RANDOM_SAMPLE = new int[SMALL_SAMPLE_SIZE];
	static private final int[] LARGE_RANDOM_SAMPLE = new int[LARGE_SAMPLE_SIZE];
	static private final int[] SMALL_REGULAR_SAMPLE = new int[SMALL_SAMPLE_SIZE];
	static private final int[] LARGE_REGULAR_SAMPLE = new int[LARGE_SAMPLE_SIZE];
	static {
		for (int i = 0; i < SMALL_SAMPLE_SIZE; i++) {
			SMALL_RANDOM_SAMPLE[i] = (int) (Integer.MAX_VALUE * Math.random());
			SMALL_REGULAR_SAMPLE[i] = i - (SMALL_SAMPLE_SIZE/2);
		}
		for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
			LARGE_RANDOM_SAMPLE[i] = (int) (Integer.MAX_VALUE * Math.random());
			LARGE_REGULAR_SAMPLE[i] = i - (LARGE_SAMPLE_SIZE/2);
		}
	}
	
	static int OVERALL_INDEX = 0;
	static int FIRST_INDEX = 1;
	static int SECOND_INDEX = 2;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new IntSetSpeedTest(new HashIntSet()).start();
//		new IntSetSpeedTest(new MinimizedTreeIntSet()).start();
		new IntSetSpeedTest(new TreeIntSet(1)).start();
		new IntSetSpeedTest(new TreeIntSet(2)).start();
		new IntSetSpeedTest(new TreeIntSet(3)).start();
		new IntSetSpeedTest(new SplitTreeIntSet(1)).start();
		new IntSetSpeedTest(new SplitTreeIntSet(2)).start();
		new IntSetSpeedTest(new SplitTreeIntSet(3)).start();
	}
	
	public IntSetSpeedTest(IntSet object) {
		this.object = object;
	}
	
	public void start() {
		test(SMALL_RANDOM_SAMPLE, SMALL_REPEAT_FACTOR, TEST_RANDOM_SMALL);
		test(SMALL_REGULAR_SAMPLE, SMALL_REPEAT_FACTOR, TEST_REGULAR_SMALL);
		test(LARGE_RANDOM_SAMPLE, LARGE_REPEAT_FACTOR, TEST_RANDOM_LARGE);
		test(LARGE_REGULAR_SAMPLE, LARGE_REPEAT_FACTOR, TEST_REGULAR_LARGE);
		System.out.println("Results for "+object.getClass());
		report(TEST_RANDOM_SMALL);
		report(TEST_RANDOM_LARGE);
		report(TEST_REGULAR_SMALL);
		report(TEST_REGULAR_LARGE);
	}
	
	private void test(int[] sample, int repeatFactor, int[] measures) {
		reporter.start(measures[OVERALL_INDEX]);
		reporter.start(measures[FIRST_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			object.clear(sample.length);
			for (int i = 0; i < sample.length; i++) {
				object.add(sample[i]);
			}
		}
		reporter.stop();
		reporter.start(measures[SECOND_INDEX]);
		for (int repeat = 0; repeat < repeatFactor; repeat++) {
			for (int i = 0; i < sample.length; i++) {
				object.add(sample[i]);
			}
		}
		reporter.stop();
		reporter.stop();
	}
	
	private void report(int[] measures) {
		for (int i = 0; i < measures.length; i++) {
			int measure = measures[i];
			System.out.println(reporter.getMethodName(measure)+reporter.getTotalTime(measure));
		}
	}
	
	private final IntSet object;
	
	static private final Reporter reporter = Reporter.register(IntSetSpeedTest.class);
	private final int[] TEST_RANDOM_SMALL = new int[] {
			reporter.newMethod("Random, small sample:  "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: ")
			};
	private final int[] TEST_RANDOM_LARGE = new int[] {
			reporter.newMethod("Random, large sample:  "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: ")
			};
	private final int[] TEST_REGULAR_SMALL = new int[] {
			reporter.newMethod("Regular, small sample: "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: ")
			};
	private final int[] TEST_REGULAR_LARGE = new int[] {
			reporter.newMethod("Regular, large sample: "),
			reporter.newMethod("          Fresh addition: "),
			reporter.newMethod("           Next addition: ")
			};
}