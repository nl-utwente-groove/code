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
package groove.test.verify;

import static org.junit.Assert.fail;
import groove.verify.BuchiGraph;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.FormulaParser;
import groove.verify.ParseException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

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
        print = true;
    }

    private static void printf(String text, Object... args) {
        if (print) {
            System.out.printf(text, args);
        }
    }

    private static boolean print = false;

    /**
     * Test method.
     */
    @Test
    public void testOrFormula() {
        String formula = "[](put || get)";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testTrue() {
        String formula = "true";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testAndFormula() {
        String formula = "[](put && get)";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testImplyFormula() {
        String formula = "!([](put-><>get))";
        String rules[] = {"put", "get"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testNegationFormula() {
        String formula = "<>[](!put)";
        String rules[] = {"put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testAndOrFormula() {
        String formula = "<>[](put && (get || empty))";
        String rules[] = {"put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testOrAndFormula() {
        String formula = "<>[](put || (get && !empty))";
        String rules[] = {"get", "put"};
        testFormula(formula, rules);
    }

    /**
     * Test method.
     */
    @Test
    public void testFinallyFormula() {
        String formula = "<>(extend)";
        String rules[] = {"get", "put"};
        testFormula(formula, rules);
    }

    /** Tests a given formula (under a given set of rules). */
    private void testFormula(String formula, String[] rules) {
        printf("Formula: %s%n", formula);
        printf("--------%n", formula);
        testGraph(formula, rules);
        printf("========%n%n", formula);
    }

    /** Tests the graph to be created from a given formula, using a given factory. */
    private void testGraph(String formula, String[] rules) {
        try {
            BuchiGraph buchiGraph =
                this.prototype.newBuchiGraph(FormulaParser.parse(formula).toLtlFormula());
            Set<String> set = new HashSet<String>(Arrays.asList(rules));
            // check whether the Büchi-graph is the one we expected
            testAllTransitions(buchiGraph.getInitial(), set,
                new HashSet<BuchiLocation>());
            printf("Initial location: %s%n", buchiGraph.getInitial());
        } catch (ParseException e) {
            fail();
        }
    }

    private void testAllTransitions(BuchiLocation location,
            Set<String> applicableRules, Set<BuchiLocation> done) {
        if (!done.contains(location)) {
            done.add(location);
            for (BuchiTransition transition : location.outTransitions()) {
                if (transition.isEnabled(applicableRules)) {
                    printf("Transition %s is enabled by %s%n", transition,
                        applicableRules);
                } else {
                    printf("Transition %s is not enabled by %s%n", transition,
                        applicableRules);
                }
                testAllTransitions(transition.target(), applicableRules, done);
            }
        }
    }

    /** The ltl2buchi factory for creating  Büchi graphs. */
    private final BuchiGraph prototype = BuchiGraph.getPrototype();
}