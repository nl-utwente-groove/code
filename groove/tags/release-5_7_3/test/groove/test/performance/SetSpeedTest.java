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
package groove.test.performance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.util.Reporter;
import groove.util.collect.TreeHashSet;

/**
 * Class to test the various implementations of {@link groove.util.collect.IntSet}
 * regarding speed.
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
        Set<Object> sampleSet = new HashSet<>();
        for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
            LARGE_REGULAR_SAMPLE[i] = i - (LARGE_SAMPLE_SIZE / 2);
            sampleSet.add(LARGE_REGULAR_SAMPLE[i] = i - (LARGE_SAMPLE_SIZE / 2));
            if (i < SMALL_SAMPLE_SIZE) {
                SMALL_REGULAR_SAMPLE[i] = LARGE_REGULAR_SAMPLE[i];
            }
        }
        for (int i = 0; i < LARGE_SAMPLE_SIZE; i++) {
            Integer random;
            do {
                random = (int) (Integer.MAX_VALUE * Math.random());
                if (i % 2 == 0) {
                    random = -random;
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
                random = (int) (Integer.MAX_VALUE * Math.random());
                if (i % 2 == 0) {
                    random = -random;
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
        new SetSpeedTest(new HashSet<>(SMALL_SAMPLE_SIZE)).start();
        // new SetSpeedTest(new BitHashSet()).start();
        // new SetSpeedTest(new TreeStoreSet(1, SMALL_SAMPLE_SIZE,
        // TreeStoreSet.HASHCODE_EQUATOR)).start();
        // new SetSpeedTest(new TreeHashSet(2, SMALL_SAMPLE_SIZE,
        // TreeHashSet.HASHCODE_EQUATOR)).start();
        new SetSpeedTest(new TreeHashSet<>(SMALL_SAMPLE_SIZE, 3, 7, TreeHashSet.HASHCODE_EQUATOR))
            .start();
    }

    public SetSpeedTest(Set<Object> object) {
        this.object = object;
    }

    public void start() {
        System.out.println("Results for " + this.object.getClass());
        double smallRegularSpace =
            test(SMALL_REGULAR_SAMPLE, SMALL_REPEAT_FACTOR, this.TEST_REGULAR_SMALL);
        report(this.TEST_REGULAR_SMALL, smallRegularSpace);
        double smallRandomSpace =
            test(SMALL_RANDOM_SAMPLE, SMALL_REPEAT_FACTOR, this.TEST_RANDOM_SMALL);
        report(this.TEST_RANDOM_SMALL, smallRandomSpace);
        double largeRegularpace =
            test(LARGE_REGULAR_SAMPLE, LARGE_REPEAT_FACTOR, this.TEST_REGULAR_LARGE);
        report(this.TEST_REGULAR_LARGE, largeRegularpace);
        double largeRandomSpace =
            test(LARGE_RANDOM_SAMPLE, LARGE_REPEAT_FACTOR, this.TEST_RANDOM_LARGE);
        report(this.TEST_RANDOM_LARGE, largeRandomSpace);
    }

    @SuppressWarnings("unchecked")
    private double test(Object[] sample, int repeatFactor, Reporter[] measures) {
        measures[OVERALL_INDEX].start();
        measures[FIRST_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            this.object.clear();
            for (int i = 0; i < sample.length; i++) {
                if (!this.object.add(sample[i])) {
                    throw new IllegalStateException();
                }
            }
        }
        measures[FIRST_INDEX].stop();
        measures[SECOND_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            for (Object element : sample) {
                if (this.object.add(element)) {
                    throw new IllegalStateException();
                }
            }
        }
        measures[SECOND_INDEX].stop();
        measures[CONTAINS_YES_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            for (int i = 0; i < sample.length; i++) {
                if (!this.object.contains(sample[i])) {
                    throw new IllegalStateException();
                }
            }
        }
        measures[CONTAINS_YES_INDEX].stop();
        Object[] distinct = DISTINCT_SAMPLE;
        measures[CONTAINS_NO_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            for (int i = 0; i < sample.length; i++) {
                Object sampleObject = distinct[i];
                if (this.object.contains(sampleObject)) {
                    throw new IllegalStateException();
                }
            }
        }
        measures[CONTAINS_NO_INDEX].stop();
        measures[ITERATOR_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            Iterator<Object> iter = this.object.iterator();
            while (iter.hasNext()) {
                iter.next();
            }
        }
        measures[ITERATOR_INDEX].stop();
        measures[OVERALL_INDEX].stop();
        Object newObject;
        if (this.object instanceof HashSet) {
            newObject = new HashSet<>(this.object);
        } else {
            newObject = new TreeHashSet<Object>((TreeHashSet) this.object);
        }
        measures[OVERALL_INDEX].start();
        measures[EQUALS_INDEX].start();
        for (int repeat = 0; repeat < repeatFactor; repeat++) {
            if (!this.object.equals(newObject)) {
                throw new IllegalStateException();
            }
        }
        measures[EQUALS_INDEX].stop();
        measures[OVERALL_INDEX].stop();
        if (this.object instanceof TreeHashSet) {
            return ((TreeHashSet) this.object).getBytesPerElement();
        } else {
            return 0;
        }
    }

    private void report(Reporter[] measures, double space) {
        for (Reporter measure : measures) {
            System.out.println(measure.getName() + measure.getTotalTime());
        }
        if (space > 0) {
            System.out.println("Bytes per element: " + space);
        }
    }

    private final Set<Object> object;

    static private final Reporter reporter = Reporter.register(IntSetSpeedTest.class);
    private final Reporter[] TEST_RANDOM_SMALL =
        new Reporter[] {reporter.register("Random, small sample:  "),
            reporter.register("          Fresh addition: "),
            reporter.register("           Next addition: "),
            reporter.register("    Positive containment: "),
            reporter.register("    Negative containment: "),
            reporter.register("                Iterator: "),
            reporter.register("             Equals test: ")};
    private final Reporter[] TEST_RANDOM_LARGE =
        new Reporter[] {reporter.register("Random, large sample:  "),
            reporter.register("          Fresh addition: "),
            reporter.register("           Next addition: "),
            reporter.register("    Positive containment: "),
            reporter.register("    Negative containment: "),
            reporter.register("                Iterator: "),
            reporter.register("             Equals test: ")};
    private final Reporter[] TEST_REGULAR_SMALL =
        new Reporter[] {reporter.register("Regular, small sample: "),
            reporter.register("          Fresh addition: "),
            reporter.register("           Next addition: "),
            reporter.register("    Positive containment: "),
            reporter.register("    Negative containment: "),
            reporter.register("                Iterator: "),
            reporter.register("             Equals test: ")};
    private final Reporter[] TEST_REGULAR_LARGE =
        new Reporter[] {reporter.register("Regular, large sample: "),
            reporter.register("          Fresh addition: "),
            reporter.register("           Next addition: "),
            reporter.register("    Positive containment: "),
            reporter.register("    Negative containment: "),
            reporter.register("                Iterator: "),
            reporter.register("             Equals test: ")};
}
