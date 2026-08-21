// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2026 University of Twente

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
package nl.utwente.groove.test.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Golden-master characterisation test of the rule-compilation pipeline
 * (gh #893). Every rule of every grammar under {@code junit/rules}, plus a
 * selection of {@code junit/samples}, is compiled and dumped in a canonical
 * textual form (condition tree with sorted node and edge lists, signature,
 * hidden parameters, anchor; or the compilation errors), which is compared
 * against the checked-in expectation file under
 * {@code junit/rulecompilation}.
 * <p>
 * An intentional change to the pipeline output is accepted by regenerating
 * the expectation files: run the test with
 * {@code -Dgroove.test.regenerate=true}, then review the resulting diff.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@RunWith(Parameterized.class)
public class RuleCompilationTest {
    /** System property that switches the test from comparing to regenerating. */
    private static final String REGENERATE_PROPERTY = "groove.test.regenerate";
    /** Root directory of the grammar fixtures. */
    private static final Path FIXTURE_DIR = Path.of("junit");
    /** Directory of the expectation files, mirroring the fixture layout. */
    private static final Path EXPECTED_DIR = FIXTURE_DIR.resolve("rulecompilation");
    /** Grammar extension. */
    private static final String GPS = ".gps";
    /** Selected sample grammars; the {@code junit/rules} grammars are always included. */
    private static final String[] SAMPLES = {"attributes", "circular-buffer", "control",
        "counting", "ferryman", "injective-forall", "injective-nac", "leader-election", "mergers",
        "priorities", "quantifierCounter", "recipes", "regexpr", "rhs-is-nac", "variables",
        "wildcards"};

    /** Collects the grammars to be tested, as pairs of subdirectory and grammar name. */
    @Parameters(name = "{0}/{1}")
    public static Collection<Object[]> grammars() throws IOException {
        List<Object[]> result = new ArrayList<>();
        Set<String> ruleGrammars = new TreeSet<>();
        try (var dirs = Files.list(FIXTURE_DIR.resolve("rules"))) {
            dirs
                .map(p -> p.getFileName().toString())
                .filter(n -> n.endsWith(GPS))
                .map(n -> n.substring(0, n.length() - GPS.length()))
                .forEach(ruleGrammars::add);
        }
        for (var name : ruleGrammars) {
            result.add(new Object[] {"rules", name});
        }
        for (var name : SAMPLES) {
            result.add(new Object[] {"samples", name});
        }
        return result;
    }

    /** Constructs a test instance for a grammar in a given fixture subdirectory. */
    public RuleCompilationTest(String subdir, String name) {
        this.subdir = subdir;
        this.name = name;
    }

    private final String subdir;
    private final String name;

    /** Compiles all rules of the grammar and compares the dump against the expectation file. */
    @Test
    public void testCompilation() throws IOException, FormatException {
        GrammarModel grammar
            = Groove.loadGrammar(FIXTURE_DIR.resolve(this.subdir).resolve(this.name).toString());
        String actual = dump(grammar);
        Path expectedFile = EXPECTED_DIR.resolve(this.subdir).resolve(this.name + ".txt");
        if (Boolean.getBoolean(REGENERATE_PROPERTY)) {
            Files.createDirectories(expectedFile.getParent());
            Files.writeString(expectedFile, actual);
        } else {
            assertTrue("Missing expectation file " + expectedFile + "; run with -D"
                + REGENERATE_PROPERTY + "=true to generate it", Files.exists(expectedFile));
            String expected = Files.readString(expectedFile).replace("\r\n", "\n");
            assertEquals("Rule compilation of " + this.subdir + "/" + this.name
                + " differs from " + expectedFile, expected, actual);
        }
    }

    /** Dumps all rules of a grammar, in the order of their qualified names. */
    static String dump(GrammarModel grammar) throws FormatException {
        StringBuilder result = new StringBuilder();
        for (QualName ruleName : new TreeSet<>(grammar.getNames(ResourceKind.RULE))) {
            RuleModel model = grammar.getRuleModel(ruleName);
            result.append("== rule ").append(ruleName).append('\n');
            if (model.hasErrors()) {
                result.append("errors:\n");
                for (FormatError error : model.getErrors()) {
                    result.append("  - ").append(error).append('\n');
                }
            } else {
                dump(model.toResource(), result);
            }
        }
        return result.toString();
    }

    /** Dumps the signature, hidden parameters, anchor and condition tree of a rule. */
    private static void dump(Rule rule, StringBuilder out) {
        out.append("signature: ").append(rule.getSignature()).append('\n');
        out.append("hidden: ").append(sortedNodes(rule.getHiddenPars())).append('\n');
        out.append("anchor: ").append(sortedAnchor(rule.getAnchor())).append('\n');
        dump(rule.getCondition(), "", out);
    }

    /** Dumps a condition tree, indented with a given prefix. */
    private static void dump(Condition condition, String prefix, StringBuilder out) {
        out.append(prefix).append(condition.getOp()).append(' ').append(condition.getName());
        if (!condition.isPositive()) {
            out.append(" [non-positive]");
        }
        if (condition.hasCountNode()) {
            out.append(" count=").append(condition.getCountNode());
        }
        Set<VariableNode> outputNodes = condition.getOutputNodes();
        if (outputNodes != null && !outputNodes.isEmpty()) {
            out.append(" outputs=").append(sortedNodes(outputNodes));
        }
        out.append('\n');
        String inner = prefix + "  ";
        RuleGraph root = condition.getRoot();
        if (root != null) {
            dump("root", root, inner, out);
        }
        RuleGraph pattern = condition.getPattern();
        if (pattern != null) {
            dump("lhs", pattern, inner, out);
        }
        Rule rule = condition.getRule();
        if (rule != null) {
            dump("rhs", rule.rhs(), inner, out);
        }
        // the subcondition order of a rule is hash-dependent, like the anchor
        // key order (see sortedAnchor), so it is canonicalised by name
        List<Condition> subs = new ArrayList<>(condition.getSubConditions());
        subs.sort(Comparator.comparing(Condition::getName));
        for (Condition sub : subs) {
            dump(sub, inner, out);
        }
    }

    /** Dumps the sorted node and edge lists of a rule graph. */
    private static void dump(String kind, RuleGraph graph, String prefix, StringBuilder out) {
        out.append(prefix).append(kind).append(": nodes ").append(sortedNodes(graph.nodeSet()));
        List<RuleEdge> edges = new ArrayList<>(graph.edgeSet());
        edges.sort(EdgeComparator.instance());
        out.append(" edges ").append(edges);
        if (!graph.varSet().isEmpty()) {
            out.append(" vars ").append(new TreeSet<>(graph.varSet().stream().map(Object::toString).toList()));
        }
        out.append('\n');
    }

    /**
     * Returns the anchor keys sorted by kind (node, edge, label variable) and
     * then by the deterministic element comparators. The anchor's own key
     * order is hash-dependent and varies between JVM runs, so it is not
     * part of the characterised output.
     */
    private static List<String> sortedAnchor(Anchor anchor) {
        List<String> result = new ArrayList<>(sortedNodes(anchor.nodeSet()));
        List<RuleEdge> edges = new ArrayList<>(anchor.edgeSet());
        edges.sort(EdgeComparator.instance());
        edges.stream().map(Object::toString).forEach(result::add);
        result.addAll(new TreeSet<>(anchor.varSet().stream().map(Object::toString).toList()));
        return result;
    }

    /** Returns the descriptions of a set of nodes, sorted by node number. */
    private static List<String> sortedNodes(Collection<? extends RuleNode> nodes) {
        List<RuleNode> sorted = new ArrayList<>(nodes);
        sorted.sort(NodeComparator.instance());
        return sorted.stream().map(RuleCompilationTest::describe).toList();
    }

    /** Returns a description of a rule node: its string form plus its type
     * (sort for variable nodes, operator for operator nodes). */
    private static String describe(RuleNode node) {
        StringBuilder result = new StringBuilder(node.toString());
        if (node instanceof VariableNode var) {
            result.append(':').append(var.getSort());
        } else if (node instanceof OperatorNode op) {
            result.append(':').append(op.getOperator());
        } else {
            result.append(':').append(node.getType().label().text());
            if (node.isSharp()) {
                result.append('#');
            }
        }
        return result.toString();
    }
}
