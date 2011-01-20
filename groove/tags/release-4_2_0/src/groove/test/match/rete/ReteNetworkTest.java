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
 * $Id$
 */
package groove.test.match.rete;

import groove.match.rete.ConditionChecker;
import groove.match.rete.ProductionNode;
import groove.match.rete.ReteMatch;
import groove.match.rete.ReteNetwork;
import groove.match.rete.ReteNetworkNode;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteNetworkTest extends TestCase {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/samples";

    @Override
    public void setUp() {
        //Nothing in particular to set up.
    }

    /**
     * Tests the static structure of the RETE network for an empty grammar.
     */
    public void testStaticEmptyGrammar() {
        GraphGrammar g = new GraphGrammar("empty");
        ReteNetwork network = new ReteNetwork(g, false);
        assertEquals(0, network.getRoot().getSuccessors().size());
        assertEquals(0, network.getConditonCheckerNodes().size());
        assertEquals(0, network.getProductionNodes().size());
    }

    /**
     * Tests the static structure of the RETE network in the case of a rule with an empty LHS.
     */
    public void testStaticEmptyPriorRules() {
        GraphGrammar g = loadGrammar("emptypriorules.gps", "start");
        ReteNetwork network = new ReteNetwork(g, false);
        testNetworkStructure(network);
    }

    /**
     * RETE static structure test for the simple grammar.
     */
    public void testStaticSimple() {
        GraphGrammar g = loadGrammar("simple.gps", "start");
        ReteNetwork network = new ReteNetwork(g, false);
        testNetworkStructure(network);
    }

    /**
     * RETE static structure test for the Petri Net grammar.
     */
    public void testStaticPetriNet() {
        GraphGrammar g = loadGrammar("petrinet.gps", "start");
        ReteNetwork network = new ReteNetwork(g, false);
        testNetworkStructure(network);
    }

    private void testNetworkStructure(ReteNetwork network) {
        for (ConditionChecker cc : network.getConditonCheckerNodes()) {
            checkBilateralConnectivity(cc);
        }
    }

    private void checkBilateralConnectivity(ReteNetworkNode nnode) {
        Collection<ReteNetworkNode> ants = nnode.getAntecedents();
        for (ReteNetworkNode ant : ants) {
            assertTrue(ant.getSuccessors().contains(nnode));
            checkBilateralConnectivity(ant);
        }
        if (ants.size() == 0) {
            if (nnode instanceof ConditionChecker) {
                assertTrue(((ConditionChecker) nnode).getCondition().getTarget().isEmpty());
            } else {
                assertEquals(nnode.getOwner().getRoot(), nnode);
            }
        }
    }

    /**
     * Dynamic behavior test of the RETE for an empty grammar.
     */
    public void testDynamicEmptyRule() {
        GraphGrammar g = loadGrammar("emptypriorules.gps", "start");
        ReteNetwork network =
            new ReteNetwork(g, g.getProperties().isInjective());
        network.processGraph(g.getStartGraph());
        for (ProductionNode pn : network.getProductionNodes()) {
            Set<ReteMatch> rmList = pn.getConflictSet();
            assertEquals(1, rmList.size());
        }
    }

    /**
     * Dynamic behavior test of the RETE for a grammar with simple injective rules.
     */
    public void testDynamicSimpleInjectiveRule() {
        GraphGrammar g = loadGrammar("simpleInjective.gps", "start");
        ReteNetwork network =
            new ReteNetwork(g, g.getProperties().isInjective());
        network.processGraph(g.getStartGraph());
        for (ProductionNode pn : network.getProductionNodes()) {
            Set<ReteMatch> rmList = pn.getConflictSet();
            if (pn.getProductionRule().getName().toString().equals("addA")) {
                assertEquals(4, rmList.size());
            } else if (pn.getProductionRule().getName().toString().equals(
                "delBNode")) {
                assertEquals(2, rmList.size());
            }
        }
    }

    /**
     * Dynamic behavior test of the RETE for the control grammar.
     */
    public void testDynamicControl() {
        GraphGrammar g = loadGrammar("control.gps", "start");
        ReteNetwork network = new ReteNetwork(g, false);
        network.processGraph(g.getStartGraph());
        for (ProductionNode pn : network.getProductionNodes()) {
            Set<ReteMatch> rmList = pn.getConflictSet();
            if (pn.getProductionRule().getName().toString().equals("move")) {
                assertEquals(pn.toString(), 1, rmList.size());
            } else {
                assertEquals(pn.toString(), 0, rmList.size());
            }
        }
    }

    private GraphGrammar loadGrammar(String grammarName, String startGraphName) {
        GraphGrammar result = null;
        try {
            result =
                StoredGrammarView.newInstance(new File(INPUT_DIR, grammarName),
                    startGraphName, false).toGrammar();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (FormatException ex) {
            Assert.fail("Could not load grammar. " + ex.getMessage());
        }
        return result;
    }
}
