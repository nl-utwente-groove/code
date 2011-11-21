/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.test.rule;

import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.RULE;
import groove.graph.iso.IsoChecker;
import groove.io.FileType;
import groove.match.TreeMatch;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/** Set of tests for rule application. */
public class RuleApplicationTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/rules";
    static final private boolean SAVE = false;

    /** Tests the rules in the creators grammar. */
    @Test
    public void testCreators() {
        test("creators");
    }

    /** Tests the rules in the erasers grammar. */
    @Test
    public void testErasers() {
        test("erasers");
    }

    /** Tests the rules in the adders grammar. */
    @Test
    public void testAdders() {
        test("adders");
    }

    /** Tests the rules in the mergers grammar. */
    @Test
    public void testMergers() {
        test("mergers");
    }

    /** Tests the rules in the embargoes grammar. */
    @Test
    public void testEmbargoes() {
        test("embargoes");
    }

    /** Tests the rules in the forallCount grammar. */
    @Test
    public void testForallCount() {
        test("forallCount");
    }

    /** Tests the rules in the existsOptional grammar. */
    @Test
    public void testExistsOptional() {
        test("existsOptional");
    }

    /** Collection of regression tests. */
    @Test
    public void testRegression() {
        test("regression");
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String grammarName) {
        try {
            GrammarModel view =
                Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            for (String ruleName : view.getNames(RULE)) {
                test(view, ruleName);
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** 
     * Tests a named rule from a given grammar view.
     * The test applies the rule to all start graphs named
     * {@code ruleName-<i>i</i>} (for <i>i</i> ranging from zero), 
     * and compares the resulting 
     * graphs with all graphs named {@code ruleName-<i>i</i>-<i>j</i>}
     * (for <i>j</i> ranging from zero).
     */
    private void test(GrammarModel view, String ruleName) {
        boolean cont = true;
        int i;
        for (i = 0; cont; i++) {
            String startName = ruleName + "-" + i;
            cont = view.getNames(HOST).contains(startName);
            if (cont) {
                test(view, ruleName, startName);
            }
        }
        if (i == 0) {
            Assert.fail(String.format("No appropriate start graph for rule %s",
                ruleName));
        }
    }

    /** 
     * Tests a named rule from a given grammar view by applying
     * it to a named host graph.
     * The test applies the rule to the start graph,
     * and compares the resulting 
     * graphs with all graphs named {@code startName-<i>j</i>}
     * (for <i>j</i> ranging from zero).
     */
    private void test(GrammarModel view, String ruleName, String startName) {
        try {
            List<HostGraph> results = new ArrayList<HostGraph>();
            boolean cont = true;
            for (int j = 0; cont; j++) {
                String resultName = startName + "-" + j;
                cont = view.getNames(HOST).contains(resultName);
                if (cont) {
                    results.add(view.getHostModel(resultName).toResource());
                }
            }
            view.setStartGraph(startName);
            Rule rule = view.toGrammar().getRule(ruleName);
            if (rule == null) {
                Assert.fail(String.format("Rule '%s' is currently disabled",
                    ruleName));
            }
            test(view.getStartGraphModel().toHost(), rule, results);
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests if the application of a given rule to a given start graph
     * results in a given set of result graphs.
     */
    private void test(HostGraph start, Rule rule, List<HostGraph> results) {
        IsoChecker<HostNode,HostEdge> checker = IsoChecker.getInstance(true);
        BitSet found = new BitSet();
        Set<TreeMatch> matches =
            new HashSet<TreeMatch>(rule.getMatcher().findAll(start,
                start.getFactory().createRuleToHostMap()));
        Set<RuleEvent> eventSet = new HashSet<RuleEvent>();
        for (TreeMatch match : matches) {
            for (Proof proof : match.toProofSet()) {
                eventSet.add(proof.newEvent(null));
            }
        }
        for (RuleEvent event : eventSet) {
            HostGraph target = new RuleApplication(event, start).getTarget();
            // look up this graph in the intended results
            for (int i = 0; target != null && i < results.size(); i++) {
                if (!found.get(i)
                    && checker.areIsomorphic(target, results.get(i))) {
                    found.set(i);
                    target = null;
                }
            }
            if (target != null) {
                if (SAVE) {
                    try {
                        File tmpFile =
                            File.createTempFile("error",
                                FileType.STATE_FILTER.getExtension(), new File(
                                    INPUT_DIR));
                        Groove.saveGraph(target.toAspectMap().getAspectGraph(),
                            tmpFile);
                        System.out.printf("Graph saved in %s", tmpFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Assert.fail(String.format(
                    "Rule %s, start graph %s: Found: %s; unexpected target graph %s",
                    rule.getName(), start.getName(), found, target));
            }
        }
        int leftOver = found.nextClearBit(0);
        if (leftOver < found.cardinality()) {
            Assert.fail(String.format(
                "Rule %s, start graph %s: Expected target missing: %s",
                rule.getName(), start.getName(),
                results.get(leftOver).getName()));
        }
    }
}
