/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.sts;

import groove.lts.MatchResult;
import groove.sts.CompleteSTS;
import groove.sts.Location;
import groove.sts.LocationVariable;
import groove.sts.STSException;
import groove.sts.SwitchRelation;
import groove.trans.HostGraph;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.IOException;

import junit.framework.Assert;

/**
 * Tests for a complete sts.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class CompleteSTSTest extends STSTest {

    private CompleteSTS completeSTS;

    /**
     * Constructor.
     * @param name The name of the test.
     */
    public CompleteSTSTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        super.setUp();
        this.completeSTS = new CompleteSTS();
        this.sts = this.completeSTS;
    }

    /**
     * Tests hostGraphToStartLocation.
     */
    public void testHostGraphToStartLocation() {
        Location l = this.completeSTS.hostGraphToStartLocation(this.g2);
        Assert.assertSame(this.completeSTS.getCurrentLocation(), l);
        Assert.assertSame(this.completeSTS.getStartLocation(), l);

        LocationVariable v = this.completeSTS.getLocationVariable(this.e2[1]);
        Assert.assertNotNull(v);
    }

    /**
     * Tests STSException.
     */
    public void testSTSException() {
        try {
            GrammarModel view =
                Groove.loadGrammar(INPUT_DIR + "/" + "exception");
            HostGraph graph = view.getStartGraphModel().toHost();
            this.completeSTS.hostGraphToStartLocation(graph);
            for (MatchResult next : createMatchSet(view)) {
                try {
                    this.completeSTS.ruleMatchToSwitchRelation(graph, next);
                    Assert.fail("No STSException thrown.");
                } catch (STSException e) {
                    Assert.assertFalse(e.getMessage().isEmpty());
                }
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests the 'final' node in a model.
     */
    public void testFinalNode() {
        test("testCase");
    }

    @Override
    protected void testRuleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) {
        this.completeSTS.hostGraphToStartLocation(sourceGraph);
        try {
            SwitchRelation sr =
                this.completeSTS.ruleMatchToSwitchRelation(sourceGraph, match);

            Assert.assertNotNull(sr);
            Assert.assertEquals(
                this.completeSTS.getSwitchRelation(SwitchRelation.getSwitchIdentifier(
                    sr.getGate(), sr.getGuard(), sr.getUpdate())), sr);
        } catch (STSException e) {
            Assert.fail(e.getMessage());
        }
    }

}
