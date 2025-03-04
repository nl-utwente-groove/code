/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.rule;

import static nl.utwente.groove.grammar.model.ResourceKind.HOST;
import static nl.utwente.groove.grammar.model.ResourceKind.RULE;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.rule.MatchChecker;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.transform.oracle.ValueOracle;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/** Set of tests for rule application. */
public class RuleApplicationTest extends TestCase {
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

    /** Tests the rules in the attributes grammar. */
    @Test
    public void testAttributes() {
        test("attributes");
    }

    /** Tests the rules in the regexpr grammar. */
    @Test
    public void testRegexpr() {
        test("regexpr");
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

    /** Tests the rules in the pointMatching grammar. */
    @Test
    public void testPointMatching() {
        test("pointMatching");
    }

    /** Collection of regression tests. */
    @Test
    public void testMatchFilter() {
        test("matchFilter");
    }

    /** Collection of regression tests. */
    @Test
    public void testRegression() {
        test("regression");
    }

    /** Tests the working of some oracles. */
    @Test
    public void testOracle() {
        test("oracle-default");
        test("oracle-random");
        test("oracle-reader");
    }

    /** Test the working of collection operators. */
    @Test
    public void testSums() {
        test("sums");
    }

    /** Tests the integration of user-defined operations. */
    @Test
    public void testUserOps() {
        test("userOps");
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String grammarName) {
        try {
            this.grammar = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            this.oracle = this.grammar.getProperties().getValueOracle();
            for (QualName ruleName : this.grammar.getNames(RULE)) {
                test(ruleName);
            }
        } catch (FormatException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Grammar set for the next rule test. */
    private GrammarModel grammar;
    /** Value oracle set for the next rule test. */
    private ValueOracle oracle;

    /**
     * Tests a named rule from a given grammar view.
     * The test applies the rule to all start graphs named
     * {@code ruleName-<i>i</i>} (for <i>i</i> ranging from zero),
     * and compares the resulting
     * graphs with all graphs named {@code ruleName-<i>i</i>-<i>j</i>}
     * (for <i>j</i> ranging from zero).
     */
    private void test(QualName ruleName) {
        boolean found = false;
        for (QualName startName : this.grammar.getNames(HOST)) {
            String namePrefix = ruleName.last() + "-";
            if (startName.parent().equals(ruleName.parent())
                && startName.last().startsWith(namePrefix)
                && !startName.last().substring(namePrefix.length()).contains("-")) {
                test(ruleName, startName);
                found = true;
            }
        }
        if (!found) {
            Assert.fail(String.format("No appropriate start graph for rule %s", ruleName));
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
    private void test(QualName ruleName, QualName startName) {
        this.grammar.setLocalActiveNames(HOST, startName);
        List<HostGraph> results = new ArrayList<>();
        int errorCount = 0;
        AlgebraFamily family = this.grammar.getProperties().getAlgebraFamily();
        for (QualName resultName : this.grammar.getNames(HOST)) {
            String namePrefix = startName.last() + "-";
            if (resultName.parent().equals(startName.parent())
                && resultName.last().startsWith(namePrefix)) {
                try {
                    var hostGraph = this.grammar.getHostModel(resultName).toResource();
                    results.add(hostGraph.clone(family));
                } catch (FormatException e) {
                    errorCount++;
                }
            }
        }
        try {
            Rule rule = this.grammar.toGrammar().getRule(ruleName);
            if (rule == null) {
                System.err.printf(String.format("Rule '%s' is currently disabled%n", ruleName));
            } else {
                test(this.grammar.getStartGraphModel().toResource().clone(family), rule, results,
                     errorCount);
            }
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests if the application of a given rule to a given start graph
     * results in a given set of result graphs.
     */
    private void test(HostGraph start, Rule rule, List<HostGraph> results,
                      int expectedErrorCount) throws IllegalArgumentException {
        IsoChecker checker = IsoChecker.getInstance(true);
        BitSet found = new BitSet();
        Set<RuleEvent> eventSet = new HashSet<>();
        Optional<MatchChecker> matchFilter = rule.getMatchFilter();
        for (Proof proof : rule.getAllMatches(start, null)) {
            RuleEvent event = proof.newEvent(null);
            boolean errorExpected = start.getName().endsWith("E");
            try {
                boolean ok = !matchFilter.isPresent()
                    || matchFilter.get().invoke(start, event.getAnchorMap());
                if (errorExpected) {
                    Assert.fail("Expected exception for " + start.getName() + " did not occur");
                }
                if (!ok) {
                    continue;
                }
            } catch (InvocationTargetException exc) {
                if (errorExpected) {
                    continue;
                } else {
                    throw new IllegalArgumentException(exc.getTargetException());
                }
            }
            eventSet.add(event);
        }
        int errorCount = 0;
        for (RuleEvent event : eventSet) {
            HostGraph target = new RuleApplication(event, start, this.oracle).getTarget();
            if (target.hasErrors()) {
                errorCount++;
            } else {
                // look up this graph in the intended results
                for (int i = 0; target != null && i < results.size(); i++) {
                    if (!found.get(i) && checker.areIsomorphic(target, results.get(i))) {
                        found.set(i);
                        target = null;
                    }
                }
                if (target != null) {
                    if (SAVE) {
                        try {
                            File tmpFile = File
                                .createTempFile("error", FileType.STATE.getExtension(),
                                                new File(INPUT_DIR));
                            Groove.saveGraph(GraphConverter.toAspect(target), tmpFile);
                            System.out.printf("Graph saved in %s", tmpFile);
                        } catch (IOException e) {
                            Assert.fail(e.toString());
                        }
                    }
                    Assert
                        .fail(String
                            .format("Rule %s, start graph %s: Found: %s; unexpected target graph %s",
                                    rule.getQualName(), start.getName(), found, target));
                }
            }
        }
        int leftOver = found.nextClearBit(0);
        if (leftOver < results.size()) {
            Assert
                .fail(String
                    .format("Rule %s, start graph %s: Expected target missing: %s",
                            rule.getQualName(), start.getName(), results.get(leftOver).getName()));
        }
        assertEquals(expectedErrorCount, errorCount);
    }

    /** Filter method for {@link #testMatchFilter()}. Returns <code>true</code> only if the
     * match image has a {@code flag} flag. */
    public static boolean filterFlag(HostGraph host, RuleToHostMap anchorMap) {
        HostNode image = anchorMap.nodeMap().values().iterator().next();
        return !host.outEdgeSet(image).stream().anyMatch(e -> e.label().text().equals("flag"));
    }

    /** Filter method that intentionally throws an exception. */
    public static boolean errorFilter(HostGraph host, RuleToHostMap anchorMap) {
        throw new NullPointerException();
    }
}
