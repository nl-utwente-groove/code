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
 * $Id: HashSpeedTest.java,v 1.3 2008-01-30 09:33:07 iovka Exp $
 */
package groove.test;

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
        elementMap = new HashMap<Integer,Integer>();
        for (int i = 0; i < elementArray.length; i++) {
            Integer element = new Integer(i);
            elementArray[i] = element;
            elementMap.put(element, element);
        }
        elementSet = Arrays.asList(elementArray);
    }

    static public void createVersusClear(boolean create) {
        Set<Integer> testSet = new HashSet<Integer>();
        for (int i = 0; i < CREATE_TRY_COUNT; i++) {
            if (create) {
                reporter.start(CREATE);
                testSet = new HashSet<Integer>();
                reporter.stop();
            } else {
                reporter.start(CLEAR);
                testSet.clear();
                reporter.stop();
            }
            testSet.addAll(elementSet);
        }
    }

    static public void listVersusSet(int kind) {
        for (int i = 0; i < KIND_TRY_COUNT; i++) {
            Collection<Integer> testSet;
            switch (kind) {
            case LINKED_KIND:
                reporter.start(LINKED_FILL);
                testSet = new LinkedList<Integer>();
                break;
            case ARRAY_KIND:
                reporter.start(ARRAY_FILL);
                testSet = new ArrayList<Integer>();
                break;
            case HASH_KIND:
                reporter.start(HASH_FILL);
                testSet = new HashSet<Integer>();
                break;
            default:
                reporter.start(HACK_FILL);
                Integer[] content = new Integer[elementArray.length];
                System.arraycopy(elementArray, 0, content, 0, content.length);
                testSet = Arrays.asList(content);
            }
            if (kind != HACK_KIND) {
                for (int j = 0; j < ELEM_COUNT; j++) {
                    testSet.addAll(elementSet);
                }
            }
            reporter.stop();
            Iterator<Integer> testIter;
            switch (kind) {
            case LINKED_KIND:
                reporter.start(LINKED_ITER);
                break;
            case ARRAY_KIND:
                reporter.start(ARRAY_ITER);
                break;
            case HASH_KIND:
                reporter.start(HASH_ITER);
                break;
            default:
                reporter.start(HACK_ITER);
            }
            int sum = 0;
            testIter = testSet.iterator();
            while (testIter.hasNext()) {
                Integer elem = testIter.next();
                sum += elem.intValue();
            }
            reporter.stop();
            switch (kind) {
            case LINKED_KIND:
                reporter.start(LINKED_CLEAR);
                break;
            case ARRAY_KIND:
                reporter.start(ARRAY_CLEAR);
                break;
            case HASH_KIND:
                reporter.start(HASH_CLEAR);
                break;
            default:
                reporter.start(HACK_CLEAR);
            }
            if (kind != HACK_KIND) {
                testSet.clear();
            }
            reporter.stop();
        }
    }

    static public void indexVersusIterator(int kind) {
        for (int i = 0; i < KIND_TRY_COUNT; i++) {
            List<Integer> testSet;
            switch (kind) {
            case LINKED_KIND:
                testSet = new LinkedList<Integer>();
                break;
            default: // case ARRAY_KIND:
                testSet = new ArrayList<Integer>();
                break;
            }
            for (int j = 0; j < ELEM_COUNT; j++) {
                testSet.addAll(elementSet);
            }
            switch (kind) {
            case LINKED_KIND:
                reporter.start(LINKED_INDEX);
                break;
            default: // case ARRAY_KIND:
                reporter.start(ARRAY_INDEX);
                break;
            }
            int sum = 0;
            for (int j = 0; j < testSet.size(); j++) {
                Integer elem = testSet.get(j);
                sum += elem.intValue();
            }
            reporter.stop();
        }
    }

    static public void keyVersusEntry(boolean key) {
        Map<Integer,Integer> testMap = new HashMap<Integer,Integer>(elementMap);
        int sum = 0;
        for (int i = 0; i < KEY_TRY_COUNT; i++) {
            if (key) {
                reporter.start(KEY);
                for (Integer element : testMap.keySet()) {
                    sum += testMap.get(element);
                }
            } else {
                reporter.start(ENTRY);
                for (Map.Entry<Integer,Integer> entry : testMap.entrySet()) {
                    sum += entry.getValue();
                }
            }
            reporter.stop();
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
    static final int CREATE = reporter.newMethod("create");
    static final int CLEAR = reporter.newMethod("clear");
    static final int KEY = reporter.newMethod("key");
    static final int ENTRY = reporter.newMethod("entry");
    static final int LINKED_FILL = reporter.newMethod("linked fill");
    static final int ARRAY_FILL = reporter.newMethod("array fill");
    static final int HASH_FILL = reporter.newMethod("hash fill");
    static final int HACK_FILL = reporter.newMethod("hack fill");
    static final int LINKED_ITER = reporter.newMethod("linked iterator");
    static final int ARRAY_ITER = reporter.newMethod("array iterator");
    static final int HASH_ITER = reporter.newMethod("hash iterator");
    static final int HACK_ITER = reporter.newMethod("hack iterator");
    static final int LINKED_CLEAR = reporter.newMethod("linked clear");
    static final int ARRAY_CLEAR = reporter.newMethod("array clear");
    static final int HASH_CLEAR = reporter.newMethod("hash clear");
    static final int HACK_CLEAR = reporter.newMethod("hack clear");
    static final int LINKED_INDEX = reporter.newMethod("linked index");
    static final int ARRAY_INDEX = reporter.newMethod("array index");
}
