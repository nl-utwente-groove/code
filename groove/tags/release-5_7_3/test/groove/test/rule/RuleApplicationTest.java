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

import static groove.grammar.model.ResourceKind.HOST;
import static groove.grammar.model.ResourceKind.RULE;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import groove.algebra.AlgebraFamily;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.MatchChecker;
import groove.grammar.rule.RuleToHostMap;
import groove.graph.iso.IsoChecker;
import groove.io.FileType;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;
import groove.transform.oracle.ValueOracle;
import groove.util.Groove;
import groove.util.parse.FormatException;
import junit.framework.Assert;

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

    /** Collection of regression tests. */
    @Test
    public void testOracle() {
        test("oracle-default");
        test("oracle-random");
        test("oracle-reader");
    }

    /** Collection of regression tests. */
    @Test
    public void testSums() {
        test("sums");
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String grammarName) {
        try {
            this.grammar = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            this.oracle = this.grammar.getProperties()
                .getValueOracle();
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
            if (startName.parent()
                .equals(ruleName.parent())
                && startName.last()
                    .startsWith(namePrefix)
                && !startName.last()
                    .substring(namePrefix.length())
                    .contains("-")) {
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
        try {
            this.grammar.setLocalActiveNames(HOST, startName);
            List<HostGraph> results = new ArrayList<>();
            AlgebraFamily family = this.grammar.getProperties()
                .getAlgebraFamily();
            for (QualName resultName : this.grammar.getNames(HOST)) {
                String namePrefix = startName.last() + "-";
                if (resultName.parent()
                    .equals(startName.parent())
                    && resultName.last()
                        .startsWith(namePrefix)) {
                    results.add(this.grammar.getHostModel(resultName)
                        .toResource()
                        .clone(family));
                }
            }
            Rule rule = this.grammar.toGrammar()
                .getRule(ruleName);
            if (rule == null) {
                Assert.fail(String.format("Rule '%s' is currently disabled", ruleName));
            }
            test(this.grammar.getStartGraphModel()
                .toHost()
                .clone(family), rule, results);
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests if the application of a given rule to a given start graph
     * results in a given set of result graphs.
     */
    private void test(HostGraph start, Rule rule, List<HostGraph> results)
        throws IllegalArgumentException {
        IsoChecker checker = IsoChecker.getInstance(true);
        BitSet found = new BitSet();
        Set<RuleEvent> eventSet = new HashSet<>();
        Optional<MatchChecker> matchFilter = rule.getMatchFilter();
        for (Proof proof : rule.getAllMatches(start, null)) {
            RuleEvent event = proof.newEvent(null);
            boolean errorExpected = start.getName()
                .endsWith("E");
            try {
                boolean ok = !matchFilter.isPresent() || matchFilter.get()
                    .invoke(start, event.getAnchorMap());
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
        for (RuleEvent event : eventSet) {
            HostGraph target = new RuleApplication(event, start, this.oracle).getTarget();
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
                        File tmpFile = File.createTempFile("error",
                            FileType.STATE.getExtension(),
                            new File(INPUT_DIR));
                        Groove.saveGraph(GraphConverter.toAspect(target), tmpFile);
                        System.out.printf("Graph saved in %s", tmpFile);
                    } catch (IOException e) {
                        Assert.fail(e.toString());
                    }
                }
                Assert.fail(
                    String.format("Rule %s, start graph %s: Found: %s; unexpected target graph %s",
                        rule.getQualName(),
                        start.getName(),
                        found,
                        target));
            }
        }
        int leftOver = found.nextClearBit(0);
        if (leftOver < results.size()) {
            Assert.fail(String.format("Rule %s, start graph %s: Expected target missing: %s",
                rule.getQualName(),
                start.getName(),
                results.get(leftOver)
                    .getName()));
        }
    }

    /** Filter method for {@link #testMatchFilter()}. Returns <code>true</code> only if the
     * match image has a {@code flag} flag. */
    public static boolean filterFlag(HostGraph host, RuleToHostMap anchorMap) {
        HostNode image = anchorMap.nodeMap()
            .values()
            .iterator()
            .next();
        return !host.outEdgeSet(image)
            .stream()
            .anyMatch(e -> e.label()
                .text()
                .equals("flag"));
    }

    /** Filter method that intentionally throws an exception. */
    public static boolean errorFilter(HostGraph host, RuleToHostMap anchorMap) {
        throw new NullPointerException();
    }
}
