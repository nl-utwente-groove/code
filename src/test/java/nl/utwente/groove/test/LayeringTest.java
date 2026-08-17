/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Assume;
import org.junit.Test;

import nl.utwente.groove.util.AIGenerated;

/**
 * Guards the intended dependency layering of the top-level packages, as
 * established by the 2026-08 dependency cleanup and documented in
 * {@code claude/dependency-analysis.md}. The layering (bottom to top) is
 * <pre>
 * util, annotation, graph, algebra,
 * {grammar+control+automaton+match+transform} (the rule-system cluster),
 * lts, io, verify, explore, prolog, gui
 * </pre>
 * where the braced cluster counts as a single layer. Every remaining
 * upward reference is listed in {@link #WHITELIST}; the whitelist is
 * expected to shrink as the remaining cleanup items land, and the test
 * fails on stale entries so that it cannot silently outlive them.
 * <p>
 * The check runs jdeps on {@code target/classes} (bytecode, so
 * compile-time-constant debug branches do not count) and is skipped if
 * that directory or the jdeps tool is unavailable. Note that a stale
 * {@code target/classes} — e.g. after package moves without a clean
 * build — can produce false violations from left-over class files.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class LayeringTest {
    /** Checks that all internal package dependencies respect the layering. */
    @Test
    public void testLayering() throws IOException {
        Path classes = Path.of("target", "classes");
        Assume.assumeTrue("target/classes not present", Files.isDirectory(classes));
        ToolProvider jdeps = ToolProvider.findFirst("jdeps").orElse(null);
        Assume.assumeTrue("jdeps tool not available", jdeps != null);
        assert jdeps != null; // implied by the assumption above
        // copy the class files to a temporary directory, leaving out
        // module-info.class: jdeps would otherwise demand the full module path
        Path tmp = Files.createTempDirectory("groove-layering");
        try {
            copyClasses(classes, tmp);
            StringWriter out = new StringWriter();
            StringWriter err = new StringWriter();
            int exit = jdeps
                .run(new PrintWriter(out), new PrintWriter(err), "-verbose:package",
                     tmp.toString());
            assertEquals("jdeps failed: " + err, 0, exit);
            checkEdges(out.toString());
        } finally {
            try (Stream<Path> walk = Files.walk(tmp)) {
                walk
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> p.toFile().delete());
            }
        }
    }

    /** Copies all class files except module-info.class. */
    private void copyClasses(Path from, Path to) throws IOException {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(from)) {
            paths = walk.toList();
        }
        for (Path path : paths) {
            Path dest = to.resolve(from.relativize(path));
            if (Files.isDirectory(path)) {
                Files.createDirectories(dest);
            } else if (!path.getFileName().toString().equals("module-info.class")) {
                Files.copy(path, dest);
            }
        }
    }

    /** Parses the jdeps output and collects layering violations. */
    private void checkEdges(String jdepsOutput) {
        Set<String> violations = new TreeSet<>();
        Set<String> usedWhitelist = new LinkedHashSet<>();
        for (String line : jdepsOutput.split("\\R")) {
            Matcher m = EDGE_PATTERN.matcher(line);
            if (!m.matches()) {
                continue;
            }
            String src = m.group(1);
            String dst = m.group(2);
            if (!src.startsWith(PREFIX) || !dst.startsWith(PREFIX)) {
                continue;
            }
            String srcTop = top(src);
            String dstTop = top(dst);
            String srcLayer = layer(srcTop);
            String dstLayer = layer(dstTop);
            if (srcLayer.equals(dstLayer)) {
                continue; // same layer, including within the rule-system cluster
            }
            if (rank(srcLayer) > rank(dstLayer)) {
                continue; // downward dependency
            }
            String edge = shortName(src) + " -> " + dstTop;
            if (WHITELIST.contains(edge)) {
                usedWhitelist.add(edge);
            } else {
                violations.add(edge);
            }
        }
        assertTrue("Dependency layering violations (see claude/dependency-analysis.md):\n  "
            + String.join("\n  ", violations), violations.isEmpty());
        Set<String> stale = new TreeSet<>(WHITELIST);
        stale.removeAll(usedWhitelist);
        assertTrue("Stale whitelist entries, remove them from " + getClass().getSimpleName()
            + ":\n  " + String.join("\n  ", stale), stale.isEmpty());
    }

    /** Returns the top-level package segment of a qualified package name. */
    private String top(String pkg) {
        String rest = pkg.substring(PREFIX.length());
        if (rest.isEmpty()) {
            return ROOT;
        }
        rest = rest.substring(1); // skip the separator dot
        int dot = rest.indexOf('.');
        return dot < 0
            ? rest
            : rest.substring(0, dot);
    }

    /** Returns the layer name of a top-level package segment. */
    private String layer(String top) {
        return CLUSTER.contains(top)
            ? CLUSTER_NAME
            : top;
    }

    /** Returns the rank of a layer; fails the test on an unknown layer. */
    private int rank(String layer) {
        Integer result = RANKS.get(layer);
        if (result == null) {
            throw new AssertionError("Unknown top-level package '" + layer
                + "': assign it a layer in " + getClass().getSimpleName());
        }
        return result;
    }

    /** Returns the package name without the common prefix, or {@link #ROOT}. */
    private String shortName(String pkg) {
        String rest = pkg.substring(PREFIX.length());
        return rest.isEmpty()
            ? ROOT
            : rest.substring(1);
    }

    /** Common package prefix of all GROOVE code. */
    private static final String PREFIX = "nl.utwente.groove";
    /** Pseudo-name for classes directly in the {@link #PREFIX} package. */
    private static final String ROOT = "(root)";
    /** Layer name of the rule-system cluster. */
    private static final String CLUSTER_NAME = "(rule system)";
    /** The mutually dependent packages forming the rule-system layer. */
    private static final Set<String> CLUSTER
        = Set.of("grammar", "control", "automaton", "match", "transform");
    /** Layer ranks; a dependency must go from a higher to a lower rank. */
    private static final Map<String,@Nullable Integer> RANKS = Map
        .ofEntries(Map.entry("util", 0), Map.entry("annotation", 1), Map.entry("graph", 2),
                   Map.entry("algebra", 3), Map.entry(CLUSTER_NAME, 4), Map.entry("lts", 5),
                   Map.entry("io", 6), Map.entry("verify", 7), Map.entry("explore", 8),
                   Map.entry("prolog", 9), Map.entry("gui", 10), Map.entry(ROOT, 11));
    /** jdeps package-level dependency line. */
    private static final Pattern EDGE_PATTERN
        = Pattern.compile("^\\s+(\\S+)\\s+->\\s+(\\S+)\\s+\\S+\\s*$");
    /**
     * Accepted upward references, as {@code sourcePackage -> targetTopPackage}
     * (source without the common prefix). Each entry corresponds to an item in
     * the edge inventory of {@code claude/dependency-analysis.md}; remove
     * entries as the corresponding cleanups land.
     */
    private static final Set<String> WHITELIST = Set
        .of(
            // P2: relabel chain typed on TypeLabel instead of graph.Label
            "algebra.syntax -> grammar",
            // P2: annotation.Help derives sorts for user operations
            "annotation -> algebra",
            // P2: RelationCalculator implements GTSListener
            "automaton -> lts",
            // P2: GrammarKey/GrammarProperties typed accessors for explore types
            "grammar -> explore",
            // P2: Grammar carries the prolog environment
            "grammar -> prolog",
            // P2: GrammarModel explore-configuration resolution
            "grammar.model -> explore",
            // accepted: GrammarModel <-> SystemStore editable-model/backing-store pair
            "grammar.model -> io",
            // P2: GrammarModel loads .pro resources for validation
            "grammar.model -> prolog",
            // P2: GraphProperties is rule-resource metadata; GraphInfo role accessors
            "graph -> grammar",
            // P2: IsoChecker uses CallStack.areEqual for control-stack comparison
            "graph.iso -> control",
            // P2: certificate strategies special-case host-graph nodes
            "graph.iso -> grammar",
            // P2 (gh #891): AutIO uses ExplorationReporter stopwatch calls
            "io.graph -> explore",
            // P2: transform.Phase/Record mention lts types
            "transform -> lts",
            // P2: util.Properties.ValueType hard-codes domain value types
            "util -> algebra", "util -> explore", "util -> grammar", "util -> transform",
            // P2: ATermTreeParser bakes algebra.Sort into the tokenizer
            "util.parse -> algebra",
            // P3: FormatError/SearchResult/SelectableListEntry context dispatch
            "util.parse -> grammar", "util.parse -> graph", "util.parse -> lts",
            // P2: BoundaryParser/CTLModelChecker/ProductStateSet references
            "verify -> explore");
}
