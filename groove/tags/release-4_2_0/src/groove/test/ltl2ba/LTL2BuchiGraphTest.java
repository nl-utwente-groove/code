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
package groove.test.ltl2ba;

import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.ltl2ba.BuchiGraph;
import groove.verify.ltl2ba.BuchiGraphFactory;
import groove.verify.ltl2ba.LTL2BuchiGraph;
import groove.verify.ltl2ba.NASABuchiGraph;
import groove.view.FormatException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class LTL2BuchiGraphTest {

    /**
     * Main method.
     */
    public static void main(String args[]) {
        LTL2BuchiGraphTest test = new LTL2BuchiGraphTest();
        test.testOrFormula();
        test.testAndFormula();
        test.testImplyFormula();
        test.testNegationFormula();
        test.testAndOrFormula();
        test.testOrAndFormula();
        test.testFinallyFormula();
    }

    /**
     * Test method.
     */
    public void testOrFormula() {
        String formula = "[](put || get)";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testAndFormula() {
        String formula = "[](put && get)";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testImplyFormula() {
        String formula = "!([](put-><>get))";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testNegationFormula() {
        String formula = "<>[](!put)";
        String rules[] = {"put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testAndOrFormula() {
        String formula = "<>[](put && (get || empty))";
        String rules[] = {"put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testOrAndFormula() {
        String formula = "<>[](put || (get && !empty))";
        String rules[] = {"get", "put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    public void testFinallyFormula() {
        String formula = "<>(extend)";
        String rules[] = {"get", "put"};
        testFormula(formula, rules);
    }

    /** Tests a given formula (under a given set of rules). */
    private void testFormula(String formula, String[] rules) {
        System.out.printf("Formula: %s%n", formula);
        System.out.printf("--------%n", formula);
        if (isLTL2BuchiEnabled()) {
            testGraph(formula, rules, this.ltl2baFactory);
        }
        testGraph(formula, rules, this.ltl2buchiFactory);
        System.out.printf("========%n%n", formula);
    }

    /** Tests the graph to be created from a given formula, using a given factory. */
    private void testGraph(String formula, String[] rules,
            BuchiGraphFactory factory) {
        try {
            BuchiGraph buchiGraph = factory.newBuchiGraph(formula);
            Set<String> set = new HashSet<String>(Arrays.asList(rules));
            // check whether the Buchi-graph is the one we expected
            for (BuchiLocation initialLocation : buchiGraph.initialLocations()) {
                testAllTransitions(initialLocation, set,
                    new HashSet<BuchiLocation>());
            }
            System.out.printf("Accepting locations: %s%n",
                buchiGraph.acceptingLocations());
        } catch (FormatException e) {
            assert false;
        }
    }

    static private boolean isLTL2BuchiEnabled() {
        return System.getProperty("os.name").startsWith("Windows")
            || System.getProperty("os.name").startsWith("Linux")
            || System.getProperty("os.name").startsWith("FreeBSD");
    }

    private void testAllTransitions(BuchiLocation location,
            Set<String> applicableRules, Set<BuchiLocation> done) {
        if (!done.contains(location)) {
            done.add(location);
            for (BuchiTransition transition : location.outTransitions()) {
                if (transition.isEnabled(applicableRules)) {
                    System.out.println("Transition " + transition.toString()
                        + " is enabled by " + applicableRules.toString());
                } else {
                    System.out.println("Transition " + transition.toString()
                        + " is not enabled by " + applicableRules.toString());
                }
                testAllTransitions(transition.target(), applicableRules, done);
            }
        }
    }

    /** The ltl2ba factory for creating  Büchi graphs. */
    private final BuchiGraphFactory ltl2baFactory =
        BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
    /** The ltl2buchi factory for creating  Büchi graphs. */
    private final BuchiGraphFactory ltl2buchiFactory =
        BuchiGraphFactory.getInstance(NASABuchiGraph.getPrototype());
}