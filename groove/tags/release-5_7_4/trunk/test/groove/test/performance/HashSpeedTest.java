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
/*
 * $Id$
 */
package groove.test.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.util.Reporter;

/**
 * Tests the speed of various alternatives in a {@link HashSet}:
 * <ul>
 * <li> Creation versus clearing
 * <li> Iterating over the keyset and getting the image versus iterating over
 * the entry set.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class HashSpeedTest {
    static final int CREATE_TRY_COUNT = 500000;
    static final int KEY_TRY_COUNT = 50000;
    static final int KIND_TRY_COUNT = 1000;
    static final int ELEM_COUNT = 100;

    static final int LINKED_KIND = 0;
    static final int ARRAY_KIND = 1;
    static final int HASH_KIND = 2;
    static final int HACK_KIND = 3;

    /** A set of sample data. */
    static private final Integer[] elementArray = new Integer[ELEM_COUNT];
    static private final Collection<Integer> elementSet;
    static private final Map<Integer,Integer> elementMap;
    static {
        // initialize the test data
        elementMap = new HashMap<>();
        for (int i = 0; i < elementArray.length; i++) {
            Integer element = i;
            elementArray[i] = element;
            elementMap.put(element, element);
        }
        elementSet = Arrays.asList(elementArray);
    }

    static public void createVersusClear(boolean create) {
        Set<Integer> testSet = new HashSet<>();
        for (int i = 0; i < CREATE_TRY_COUNT; i++) {
            if (create) {
                CREATE.start();
                testSet = new HashSet<>();
                CREATE.stop();
            } else {
                CLEAR.start();
                testSet.clear();
                CLEAR.stop();
            }
            testSet.addAll(elementSet);
        }
    }

    static public void listVersusSet(int kind) {
        for (int i = 0; i < KIND_TRY_COUNT; i++) {
            Collection<Integer> testSet;
            Reporter measure;
            switch (kind) {
            case LINKED_KIND:
                (measure = LINKED_FILL).start();
                testSet = new LinkedList<>();
                break;
            case ARRAY_KIND:
                (measure = ARRAY_FILL).start();
                testSet = new ArrayList<>();
                break;
            case HASH_KIND:
                (measure = HASH_FILL).start();
                testSet = new HashSet<>();
                break;
            default:
                (measure = HACK_FILL).start();
                Integer[] content = new Integer[elementArray.length];
                System.arraycopy(elementArray, 0, content, 0, content.length);
                testSet = Arrays.asList(content);
            }
            if (kind != HACK_KIND) {
                for (int j = 0; j < ELEM_COUNT; j++) {
                    testSet.addAll(elementSet);
                }
            }
            measure.stop();
            Iterator<Integer> testIter;
            switch (kind) {
            case LINKED_KIND:
                (measure = LINKED_ITER).start();
                break;
            case ARRAY_KIND:
                (measure = ARRAY_ITER).start();
                break;
            case HASH_KIND:
                (measure = HASH_ITER).start();
                break;
            default:
                (measure = HACK_ITER).start();
            }
            int sum = 0;
            testIter = testSet.iterator();
            while (testIter.hasNext()) {
                Integer elem = testIter.next();
                sum += elem.intValue();
            }
            measure.stop();
            switch (kind) {
            case LINKED_KIND:
                (measure = LINKED_CLEAR).start();
                break;
            case ARRAY_KIND:
                (measure = ARRAY_CLEAR).start();
                break;
            case HASH_KIND:
                (measure = HASH_CLEAR).start();
                break;
            default:
                (measure = HACK_CLEAR).start();
            }
            if (kind != HACK_KIND) {
                testSet.clear();
            }
            measure.stop();
        }
    }

    static public void indexVersusIterator(int kind) {
        for (int i = 0; i < KIND_TRY_COUNT; i++) {
            List<Integer> testSet;
            switch (kind) {
            case LINKED_KIND:
                testSet = new LinkedList<>();
                break;
            default: // case ARRAY_KIND:
                testSet = new ArrayList<>();
                break;
            }
            for (int j = 0; j < ELEM_COUNT; j++) {
                testSet.addAll(elementSet);
            }
            Reporter measure;
            switch (kind) {
            case LINKED_KIND:
                (measure = LINKED_INDEX).start();
                break;
            default: // case ARRAY_KIND:
                (measure = ARRAY_INDEX).start();
                break;
            }
            int sum = 0;
            for (int j = 0; j < testSet.size(); j++) {
                Integer elem = testSet.get(j);
                sum += elem.intValue();
            }
            measure.stop();
        }
    }

    static public void keyVersusEntry(boolean key) {
        Map<Integer,Integer> testMap = new HashMap<>(elementMap);
        int sum = 0;
        for (int i = 0; i < KEY_TRY_COUNT; i++) {
            Reporter measure;
            if (key) {
                (measure = KEY).start();
                for (Integer element : testMap.keySet()) {
                    sum += testMap.get(element);
                }
            } else {
                (measure = ENTRY).start();
                for (Map.Entry<Integer,Integer> entry : testMap.entrySet()) {
                    sum += entry.getValue();
                }
            }
            measure.stop();
        }
    }

    static public void testCreateVersusClear() {
        createVersusClear(false);
        createVersusClear(true);
    }

    static public void testKeyVersusEntry() {
        keyVersusEntry(false);
        keyVersusEntry(true);
    }

    static public void testListVersusSet() {
        listVersusSet(LINKED_KIND);
        listVersusSet(ARRAY_KIND);
        listVersusSet(HASH_KIND);
        listVersusSet(HACK_KIND);
    }

    static public void testIndexVersusIterator() {
        indexVersusIterator(LINKED_KIND);
        indexVersusIterator(ARRAY_KIND);
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("create")) {
            testCreateVersusClear();
        }
        if (args.length == 0 || args[0].equals("key")) {
            testKeyVersusEntry();
        }
        if (args.length == 0 || args[0].equals("list")) {
            testListVersusSet();
            testIndexVersusIterator();
        }
        Reporter.report();
    }

    static final Reporter reporter = Reporter.register(HashSpeedTest.class);
    static final Reporter CREATE = reporter.register("create");
    static final Reporter CLEAR = reporter.register("clear");
    static final Reporter KEY = reporter.register("key");
    static final Reporter ENTRY = reporter.register("entry");
    static final Reporter LINKED_FILL = reporter.register("linked fill");
    static final Reporter ARRAY_FILL = reporter.register("array fill");
    static final Reporter HASH_FILL = reporter.register("hash fill");
    static final Reporter HACK_FILL = reporter.register("hack fill");
    static final Reporter LINKED_ITER = reporter.register("linked iterator");
    static final Reporter ARRAY_ITER = reporter.register("array iterator");
    static final Reporter HASH_ITER = reporter.register("hash iterator");
    static final Reporter HACK_ITER = reporter.register("hack iterator");
    static final Reporter LINKED_CLEAR = reporter.register("linked clear");
    static final Reporter ARRAY_CLEAR = reporter.register("array clear");
    static final Reporter HASH_CLEAR = reporter.register("hash clear");
    static final Reporter HACK_CLEAR = reporter.register("hack clear");
    static final Reporter LINKED_INDEX = reporter.register("linked index");
    static final Reporter ARRAY_INDEX = reporter.register("array index");
}
