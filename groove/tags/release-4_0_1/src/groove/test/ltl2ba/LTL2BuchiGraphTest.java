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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class LTL2BuchiGraphTest
{
    /**
     * Main method.
     * @param args
     */
    public static void main(String args[])
    {
        LTL2BuchiGraphTest test = new LTL2BuchiGraphTest();
//        test.testOrFormula();
//        test.testAndFormula();
//        test.testImplyFormula();
//        test.testNegationFormula();
//        test.testAndOrFormula();
//        test.testOrAndFormula();
        test.testFinallyFormula();
    }

    /**
     * Test method.
     */
    public void testOrFormula()
    {
        String formula = "[](put || get)";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        String rules[] = { "put", "get" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testAndFormula()
    {
        String formula = "[](put && get)";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        String rules[] = { "put", "get" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testImplyFormula()
    {
        String formula = "!([](put-><>get))";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        System.out.println("Initial locations: " + buchiGraph.initialLocations());

        String rules[] = { "put", "get" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testNegationFormula()
    {
        String formula = "<>[](!put)";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        String rules[] = { "put" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testAndOrFormula()
    {
        String formula = "<>[](put && (get || empty))";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        String rules[] = { "put" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testOrAndFormula()
    {
        String formula = "<>[](put || (get && !empty))";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        String rules[] = { "get" , "put" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    /**
     * Test method.
     */
    public void testFinallyFormula()
    {
        String formula = "<>(extend)";
        BuchiGraphFactory factory = BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
        BuchiGraph buchiGraph = factory.newBuchiGraph(formula);

        System.out.println("Initial location(s): " + buchiGraph.initialLocations());
        System.out.println("Accepting location(s): " + buchiGraph.acceptingLocations());
        
        String rules[] = { "get" , "put" };
        Set<String> set = new HashSet<String>(Arrays.asList(rules));
        // check whether the Buchi-graph is the one we expected
        for (BuchiLocation initialLocation : buchiGraph.initialLocations())
        {
            testAllTransitions(initialLocation, set, new HashSet<BuchiLocation>());
        }
    }

    private void testAllTransitions(BuchiLocation location, Set<String> applicableRules, Set<BuchiLocation> done)
    {
        if (!done.contains(location))
        {
            done.add(location);
            for (BuchiTransition transition: location.outTransitions())
            {
                if (transition.isEnabled(applicableRules))
                {
                    System.out.println("Transition " + transition.toString() + " is enabled by " + applicableRules.toString());
                }
                else
                {
                    System.out.println("Transition " + transition.toString() + " is not enabled by " + applicableRules.toString());
                }
                testAllTransitions(transition.target(), applicableRules, done);
            }
        }
    }
}