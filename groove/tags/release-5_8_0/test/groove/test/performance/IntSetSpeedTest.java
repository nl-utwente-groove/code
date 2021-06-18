/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.test.performance;

import groove.util.Reporter;
import groove.util.collect.HashIntSet;
import groove.util.collect.IntSet;
import groove.util.collect.TreeIntSet;

/**
 * Class to test the various implementations of {@link groove.util.collect.IntSet}
 * regarding speed.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class IntSetSpeedTest {
    static private final int SMALL_SAMPLE_SIZE = 500;
    static private final int LARGE_SAMPLE_SIZE = 10000000;
    static private final int SMALL_REPEAT_FACTOR = 5000;
    static private final int LARGE_REPEAT_FACTOR = 1;

    static private final int[] SMALL_RANDOM_SAMPLE = new int[SMALL_SAMPLE_SIZE];
    static private final int[] LARGE_RANDOM_SAMPLE = new int[LARGE_SAMPLE_SIZE];
    static private final int[] SMALL_REGULAR_SAMPLE =
        new int[SMALL_SAMPLE_SIZE];
    static private final int[] LARGE_REGULAR_SAMPLE =
        new int[LARGE_SAMPLE_SIZE];
    static {
        for (int i = 0; i < SMALL_SAMPLE_SIZE; i++) {
            SMALL_RANDOM_SAMPLE[i] = (int) (Integer.MAX_VALUE * Math.random());
            SMALL_REGULAR_SAMPLE[i] = i - (SMALL_SAMPLE_SIZE / 2);
        }
        for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
            LARGE_RANDOM_SAMPLE[i] = (int) (Integer.MAX_VALUE * Math.random());
            LARGE_REGULAR_SAMPLE[i] = i - (LARGE_SAMPLE_SIZE / 2);
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
        // new IntSetSpeedTest(new MinimizedTreeIntSet()).start();
        new IntSetSpeedTest(new TreeIntSet(1)).start();
        new IntSetSpeedTest(new TreeIntSet(2)).start();
        new IntSetSpeedTest(new TreeIntSet(3)).start();
    }

    public IntSetSpeedTest(IntSet object) {
        this.object = object;
    }

    public void start() {
        test(SMALL_RANDOM_SAMPLE, SMALL_REPEAT_FACTOR, this.TEST_RANDOM_SMALL);
        test(SMALL_REGULAR_SAMPLE, SMALL_REPEAT_FACTOR, this.TEST_REGULAR_SMALL);
        test(LARGE_RANDOM_SAMPLE, LARGE_REPEAT_FACTOR, this.TEST_RANDOM_LARGE);
        test(LARGE_REGULAR_SAMPLE, LARGE_REPEAT_FACTOR, this.TEST_REGULAR_LARGE);
        System.out.println("Results for " + this.object.getClass());
        report(this.TEST_RANDOM_SMALL);
        report(this.TEST_RANDOM_LARGE);
        report(this.TEST_REGULAR_SMALL);
        report(this.TEST_REGULAR_LARGE);
    }

    private void test(int[] sample, int repeatFactor, Reporter[] measures) {
        measures[OVERALL_INDEX].start();
        measures[FIRST_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            this.object.clear(sample.length);
            for (int element : sample) {
                this.object.add(element);
            }
        }
        measures[FIRST_INDEX].stop();
        measures[SECOND_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            for (int element : sample) {
                this.object.add(element);
            }
        }
        measures[SECOND_INDEX].stop();
        measures[OVERALL_INDEX].stop();
    }

    private void report(Reporter[] measures) {
        for (Reporter measure : measures) {
            System.out.println(measure.getName() + measure.getTotalTime());
        }
    }

    private final IntSet object;

    static private final Reporter reporter =
        Reporter.register(IntSetSpeedTest.class);
    private final Reporter[] TEST_RANDOM_SMALL = new Reporter[] {
        reporter.register("Random, small sample:  "),
        reporter.register("          Fresh addition: "),
        reporter.register("           Next addition: ")};
    private final Reporter[] TEST_RANDOM_LARGE = new Reporter[] {
        reporter.register("Random, large sample:  "),
        reporter.register("          Fresh addition: "),
        reporter.register("           Next addition: ")};
    private final Reporter[] TEST_REGULAR_SMALL = new Reporter[] {
        reporter.register("Regular, small sample: "),
        reporter.register("          Fresh addition: "),
        reporter.register("           Next addition: ")};
    private final Reporter[] TEST_REGULAR_LARGE = new Reporter[] {
        reporter.register("Regular, large sample: "),
        reporter.register("          Fresh addition: "),
        reporter.register("           Next addition: ")};
}
