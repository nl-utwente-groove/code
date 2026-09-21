/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.test.performance;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.MatchApplier;
import nl.utwente.groove.match.plan.PlanSearchStrategy;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Reporter;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Throughput harness for state-space exploration.
 * <p>
 * The harness explores a fixed set of {@link Config configurations} over the
 * grammars in {@code junit/performance}, and reports wall time plus the standing
 * instrumentation counters as one fixed-width table. Its purpose is to give
 * before-and-after numbers for the findings collected in
 * {@code claude/exploration-performance.md}; the expected state and transition
 * counts are asserted on every measured run, so it doubles as a regression
 * check for changes that touch matching, isomorphism or transformation.
 * <p>
 * The grammar files of a configuration are read once, but the grammar is
 * <em>compiled afresh for every run</em>, warm-up runs included, outside the
 * measured region; each run then builds a fresh {@link GTS} and
 * {@link Exploration} and measures nothing but {@link Exploration#play()}. A
 * compiled grammar is not reused because a run leaves state behind in it: as
 * of 2026-09 every rule application is retained for the lifetime of the
 * {@link Grammar} (about 1.4 KB each), so a later run would explore with the
 * leavings of the earlier ones on the heap. The price is that search plans,
 * which are built lazily on the first match of a rule, are now built inside
 * the measured region of every run rather than only of the first; that is
 * milliseconds against seconds and falls on every run alike, so it is
 * accepted rather than worked around.
 * <p>
 * The timing counters ({@link Exploration#getRunningTime()} and friends) are
 * static and accumulate over the JVM's lifetime, so the harness reports
 * deltas.
 * <p>
 * <b>Running it.</b> The intended entry point is {@link #main(String[])}, run
 * outside Surefire so that assertions are off (the assertion-only costs listed
 * in section 6 of the review note distort every timing taken under {@code -ea}).
 * The Eclipse launch configuration {@code launch/GROOVE - exploration
 * benchmark.launch} runs it with {@code -da -Xmx4g -XX:+UseParallelGC} from the
 * project root. The heap matters: at {@code -Xmx2g} the three largest
 * configurations spend most of their time in the collector, which triples the
 * spread of the measurement. Arguments are configuration names (empty means
 * all); the run shape is set through system properties {@code groove.bench.warmups} (default
 * {@value #DEFAULT_WARMUPS}), {@code groove.bench.runs} (default
 * {@value #DEFAULT_RUNS}) and {@code groove.bench.timeout} (seconds, default
 * {@value #DEFAULT_TIMEOUT}). If {@code groove.bench.csv} names a file, the rows
 * are appended to it in CSV form as well.
 * <p>
 * From Maven the same {@code main} can be reached through the JUnit entry
 * {@link #benchmark()}, which is skipped unless {@code groove.bench.run} is set:
 * <pre>
 * mvn -q test "-Dexcluded.test.groups=" -Dtest=ExplorationBenchmark \
 *     "-Dgroove.bench.run=true" -DenableAssertions=false &gt; bench.log 2&gt;&amp;1
 * </pre>
 * The property value may also name the configurations to run, comma-separated.
 * {@code enableAssertions} is Surefire's own user property and does reach the
 * forked JVM; the printed "Assertions:" header line states what the JVM
 * actually has, so a run that silently kept {@code -ea} is visible in the log.
 * Note that {@code -DargLine=-da} is not an alternative: the pom's
 * {@code argLine} carries the preferences factory, the extension directory and
 * the native-access grant, and overriding it breaks the test JVM.
 * <p>
 * {@link #smoke()} runs the configurations marked {@link Config#smoke()} with
 * no warm-up and a single run, so that the harness stays compilable and correct
 * under the full test suite without costing minutes. The four heaviest
 * configurations are left out of it: under Surefire they run with assertions
 * on, which is exactly what the harness is meant to avoid.
 * <p>
 * <b>Comparing runs.</b> All configurations run in one JVM, and the order
 * matters more than one would like: {@code car-platooning-05} measured 5.2 s as
 * the first configuration of a JVM and 12 s as the ninth, with an 85 s outlier.
 * The likely cause is the megamorphic dispatch in the shared matcher record
 * loop (finding 4.1.9), whose call sites are polluted by the preceding
 * grammars. So compare like with like: same configuration set, same order, and
 * read the minimum next to the median. Running one configuration per JVM (pass
 * its name as the only argument) is the cleanest comparison, at the cost of
 * paying class loading each time.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5, 2026-09")
@Category(SlowTest.class)
public class ExplorationBenchmark {
    /** Location of the sample grammars, as in {@code ExplorationTest}. */
    public static final String INPUT_DIR = "junit/performance";

    /** Default number of discarded warm-up runs per configuration. */
    public static final int DEFAULT_WARMUPS = 2;
    /** Default number of measured runs per configuration. */
    public static final int DEFAULT_RUNS = 5;
    /** Default per-run timeout, in seconds. */
    public static final int DEFAULT_TIMEOUT = 300;
    /** Per-run timeout of the smoke test, in seconds. */
    private static final int SMOKE_TIMEOUT = 120;
    /** Grace period, in milliseconds, given to an interrupted run to wind down. */
    private static final long INTERRUPT_GRACE = 10_000L;

    /**
     * A single benchmark configuration.
     * @param name short identifier, used to select configurations on the
     * command line and as the first table column
     * @param grammar name of the grammar directory within {@link #INPUT_DIR}
     * @param startGraph name of the start graph; {@code null} for the
     * grammar's default one
     * @param exploreConfig exploration configuration in {@link ExploreConfig}
     * text form; {@code ""} is the default (breadth-first, full) exploration
     * @param expectedStates expected number of <em>discovered</em> states, or
     * {@code -1} if unknown
     * @param expectedTransitions expected number of <em>discovered</em>
     * transitions, or {@code -1} if unknown
     * @param smoke whether this configuration is fast enough for
     * {@link #smoke()}
     */
    public record Config(String name, String grammar, @Nullable String startGraph,
                         String exploreConfig, int expectedStates, int expectedTransitions,
                         boolean smoke) {
        // no additional members
    }

    /**
     * The benchmark set. The state and transition counts are pinned from the
     * calibration run of 2026-09-20 (exploration is deterministic, so they are
     * exact), and each configuration carries a comment saying which of the
     * findings in {@code claude/exploration-performance.md} it is there for.
     * They are the <em>discovered</em> counts, taken from a
     * {@link DiscoveryCounter} rather than from the GTS: under
     * {@code persistence=none} the GTS retains almost nothing, so its own
     * counts describe the storage policy rather than the work done. For the
     * persistent configurations the two coincide.
     * <p>
     * Sizing is limited by what the sample grammars offer: the calibration run
     * showed a cliff rather than a range. Everything below
     * {@code car-platooning start-05} finishes in well under a second, while
     * the next size up ({@code start-06}) does not fit in a 2 GB heap; the same
     * holds for {@code sierpinsky start13}, for {@code generate-binary-tree} at
     * depth 14 and for As-and-Bs under equality collapse. The set therefore
     * spans about 0.15 s to 10 s per configuration rather than the 2 s to 60 s
     * the review note asked for, and the small
     * entries are kept for mechanism coverage rather than for throughput.
     * Closing the gap needs the dedicated large-graph and symmetric grammars
     * the note proposes, not another start graph. The same cliff bounds the
     * unstored configuration: the tree unfolding of
     * {@code generate-binary-tree} grows by a factor of about eight per depth
     * level, from 5.9k discovered states at depth 6 through 46k at depth 7 to
     * 409k at depth 8, so the depth chosen (8, about 3 s) is the last one that
     * fits; depth 9 was still at 2.4M of its roughly 3.7M states after 90 s.
     * <p>
     * Several candidates from the note turned out to be dead ends: both
     * {@code exploreCache} grammars explore to a single state,
     * {@code petrinet start2} to 38, and {@code attribute-count-to-n},
     * {@code fibonacci}, {@code recipes} and {@code transactions} stay in the
     * tens of states, so none of them measures anything as they stand. The
     * sample's {@code pacman start_four_ghosts} was likewise dead and has
     * been replaced by a hand-made maze in the performance copy. The
     * {@code -init} start graphs of {@code leader-election} also explore to
     * a single state, for a repairable reason: they carry {@code type:} and
     * {@code flag:} prefixes the rules do not use. The generated
     * {@code ring-N} graphs are the same shape with plain labels and grow
     * about fourfold in states and time per two processes.
     * <p>
     * Heap: {@code car-platooning-05}, {@code append-4-list-8-equality} and
     * {@code sierpinsky-11} retain 0.4 to 0.9 GB and allocate several GB, so
     * the benchmark wants {@code -Xmx4g}. Note that a configuration which does
     * not fit the heap does <em>not</em> reliably hit the timeout: under
     * near-OOM collector thrashing the watchdog thread is starved along with
     * everything else. Switching persistence off saves less than one would
     * hope: {@code binary-tree-dfs-unstored} allocates 3.3 GB and still
     * retains 1.4 GB after the run, although the GTS it leaves behind holds
     * nine states. That is finding 3.11, the rule applications a single run
     * leaks into its grammar; compiling the grammar afresh per run keeps it
     * from accumulating but does not make one run cheaper. It is still the
     * most variable configuration of the set — 19 s to 25 s maximum against a
     * 1.9 s minimum over five runs — and the spread is that same live set
     * meeting the heap: the run alone spends about two thirds of the JVM's
     * uptime in the collector, over some forty unforced full collections, and
     * which run draws the long straw is a matter of timing. So the figure to
     * read for this configuration is the minimum, until 3.11 is fixed.
     */
    private static final List<Config> CONFIGS = List
        .of(
            // diamond-rich lattice: the ground for the dead confluent-diamond
            // shortcut (finding 2.1); "confl" must turn non-zero when it is fixed
            new Config("inheritance", "inheritance.gps", "start", "", 756, 5374, true),
            // quantified rules over a small graph
            new Config("pacman", "pacman.gps", "start", "", 256, 1536, true),
            // regular-expression matching with a path cache
            new Config("as-and-bs", "As-and-Bs-reg-exp-benchmark.gps", "start", "", 8240, 44774,
                true),
            // linear exploration over graphs growing to ~800k elements: the
            // large-graph case for the certifier array (3.1) and the per-node
            // edge sets (4.3.2)
            new Config("sierpinsky-11", "sierpinsky.gps", "start11",
                "frontier=single successor=single", 12, 11, true),
            // depth-first with a depth bound: long delta chains, certifying-heavy
            new Config("binary-tree-dfs12", "generate-binary-tree.gps", "start",
                "next=newest cost=uniform bound=cost:12", 4012, 22188, true),
            // the largest breadth-first run among the samples
            new Config("append-4-list-8", "append.gps", "append-4-list-8", "", 31104, 114008,
                false),
            // the same under equality collapse, for findings 4.4.4 and section 6
            new Config("append-4-list-8-equality", "append.gps", "append-4-list-8",
                "collapse=equality", 73792, 268912, false),
            // regular expressions with a high transition-to-state ratio
            new Config("mark-unmark", "Mark-Unmark-List-regexp-benchmark.gps", "start", "", 24576,
                368640, false),
            // NACs, injective matching and the dangling-edge check; watch the
            // factory edge count for the interning probe edge of finding 3.3
            new Config("car-platooning-05", "car-platooning.gps", "start-05", "", 110366, 369601,
                false),
            // the same depth-bounded depth-first run without persistence:
            // unstored states never enter the state set, so nothing collapses
            // and the full tree unfolding is explored — 100 times the states
            // of the stored run two depth levels deeper. The GTS keeps only
            // the retained trace (9 states), so the discovered counts are the
            // only description of the work; pinned from the calibration run
            // 2026-09-20
            new Config("binary-tree-dfs-unstored", "generate-binary-tree.gps", "start",
                "next=newest cost=uniform bound=cost:8 persistence=none", 409114, 409113, true),
            // generated larger start graphs (junit/performance/generate-starts.py);
            // counts pinned from the laptop calibration of 2026-09-21
            new Config("mark-unmark-18", "Mark-Unmark-List-regexp-benchmark.gps", "tree-18", "",
                48384, 870912, false),
            new Config("mark-unmark-21", "Mark-Unmark-List-regexp-benchmark.gps", "tree-21", "",
                169344, 3556224, false),
            new Config("as-and-bs-4-3", "As-and-Bs-reg-exp-benchmark.gps", "start-4-3", "",
                131505, 947824, false),
            new Config("inheritance-12", "inheritance.gps", "start-12", "", 297212, 4317133,
                false),
            new Config("append-4-list-10", "append.gps", "append-4-list-10", "", 1077000,
                4008820, false),
            // hand-made four-ghost maze: 37 transitions per state
            new Config("pacman-four-ghosts", "pacman.gps", "start_four_ghosts", "", 210102,
                7819623, false),
            // leader election on a generated ring of N processes with the
            // numbers pre-assigned (junit/performance/generate-starts.py):
            // the symmetric-ring case of finding 5.6; counts pinned from the
            // desktop calibration of 2026-09-21
            new Config("leader-election-8", "leader-election.gps", "ring-8", "", 820, 3405, true),
            new Config("leader-election-14", "leader-election.gps", "ring-14", "", 49620, 386295,
                false),
            new Config("leader-election-16", "leader-election.gps", "ring-16", "", 197404,
                1772291, false),
            new Config("leader-election-18", "leader-election.gps", "ring-18", "", 787648,
                7737099, false));

    /** Returns the benchmark set. */
    public static List<Config> getConfigs() {
        return CONFIGS;
    }

    /**
     * Runs the configurations marked {@link Config#smoke()} once, without
     * warm-up, asserting the pinned state and transition counts. Keeps the
     * harness honest under the full test suite.
     */
    @Test
    public void smoke() {
        for (Config config : CONFIGS) {
            if (!config.smoke()) {
                continue;
            }
            Result result = run(config, 0, 1, SMOKE_TIMEOUT);
            String failure = result.failure();
            if (failure != null) {
                throw new AssertionError(config.name() + ": " + failure);
            }
            for (Measurement run : result.measured()) {
                if (config.expectedStates() >= 0) {
                    assertEquals(config.name() + " discovered states", config.expectedStates(),
                                 run.discoveredStates());
                }
                if (config.expectedTransitions() >= 0) {
                    assertEquals(config.name() + " discovered transitions",
                                 config.expectedTransitions(), run.discoveredTransitions());
                }
            }
        }
    }

    /**
     * JUnit route to the full benchmark, for running it under Maven. Does
     * nothing unless the system property {@code groove.bench.run} is set, so
     * that a plain full-suite run does not pay for it; see the class comment
     * for the command line. The property value selects the configurations: a
     * comma-separated list of names, or {@code true} for all of them.
     */
    @Test
    public void benchmark() {
        String selection = System.getProperty("groove.bench.run");
        if (selection == null) {
            return;
        }
        int failures = execute(selection.isEmpty() || selection.equals("true")
            ? new String[0]
            : selection.split(","));
        assertEquals("benchmark mismatches", 0, failures);
    }

    /**
     * Command-line entry point.
     * @param args names of the configurations to run; empty means all
     */
    public static void main(String[] args) {
        System.exit(execute(args) == 0
            ? 0
            : 1);
    }

    /**
     * Runs the selected configurations and prints the report.
     * @param args names of the configurations to run; empty means all
     * @return the number of configurations that failed or mismatched
     */
    private static int execute(String[] args) {
        int warmups = intProperty("groove.bench.warmups", DEFAULT_WARMUPS);
        int runs = intProperty("groove.bench.runs", DEFAULT_RUNS);
        int timeout = intProperty("groove.bench.timeout", DEFAULT_TIMEOUT);
        List<String> selection = Arrays.asList(args);
        List<Config> configs = new ArrayList<>();
        for (Config config : CONFIGS) {
            if (selection.isEmpty() || selection.contains(config.name())) {
                configs.add(config);
            }
        }
        if (configs.isEmpty()) {
            System.out.printf(Locale.ROOT, "No configuration matches %s%n", selection);
            return 1;
        }
        printHeader(warmups, runs, timeout);
        System.out.println(HEADER_ROW);
        System.out.println(RULE_ROW);
        List<String> problems = new ArrayList<>();
        for (Config config : configs) {
            Result result = run(config, warmups, runs, timeout);
            String failure = result.failure();
            if (failure != null) {
                System.out.printf(Locale.ROOT, "%-22s %s%n", config.name(), failure);
                System.out.flush();
                problems.add(config.name() + ": " + failure);
                continue;
            }
            Measurement median = result.median();
            System.out.println(formatRow(config, result, median));
            // the report goes to a redirected stdout as often as to a console,
            // where the default 8 KB buffer would hide the progress of a run
            // that takes minutes
            System.out.flush();
            appendCsv(config, result, median);
            problems.addAll(checkCounts(config, result));
        }
        for (String problem : problems) {
            System.out.printf(Locale.ROOT, "MISMATCH %s%n", problem);
        }
        return problems.size();
    }

    /** Checks the pinned counts of every measured run of a result. */
    private static List<String> checkCounts(Config config, Result result) {
        List<String> problems = new ArrayList<>();
        for (Measurement run : result.measured()) {
            if (config.expectedStates() >= 0 && config.expectedStates() != run.discoveredStates()) {
                problems
                    .add(String
                        .format(Locale.ROOT, "%s: expected %d discovered states, found %d",
                                config.name(), config.expectedStates(), run.discoveredStates()));
            }
            if (config.expectedTransitions() >= 0
                && config.expectedTransitions() != run.discoveredTransitions()) {
                problems
                    .add(String
                        .format(Locale.ROOT, "%s: expected %d discovered transitions, found %d",
                                config.name(), config.expectedTransitions(),
                                run.discoveredTransitions()));
            }
        }
        return problems;
    }

    /**
     * Runs a single configuration.
     * The grammar files are read once; every run gets a freshly compiled
     * {@link Grammar} (see the class comment) and with it a fresh GTS and
     * exploration. Warm-up measurements are discarded.
     * @param config the configuration to run
     * @param warmups number of discarded warm-up runs
     * @param runs number of measured runs
     * @param timeoutSeconds per-run timeout, in seconds
     * @return the measurements, or a result carrying a failure message
     */
    public static Result run(Config config, int warmups, int runs, int timeoutSeconds) {
        try {
            SystemStore store = SystemStore
                .newStore(new File(INPUT_DIR, config.grammar()), false, true);
            ExploreType exploreType = ExploreTypeConverter
                .toExploreType(ExploreConfig.parse(config.exploreConfig()));
            List<Measurement> measured = new ArrayList<>();
            for (int i = 0; i < warmups + runs; i++) {
                // outside the measured region, which starts in singleRun
                Grammar grammar = newGrammar(store, config.startGraph());
                Measurement measurement = singleRun(grammar, exploreType, timeoutSeconds);
                if (i >= warmups) {
                    measured.add(measurement);
                }
            }
            if (measured.isEmpty()) {
                return new Result(measured, "no measured runs");
            }
            return new Result(measured, null);
        } catch (Exception exc) {
            String message = exc.getMessage();
            return new Result(List.of(), message == null
                ? exc.toString()
                : message);
        }
    }

    /**
     * Performs one measured exploration.
     * Nothing but {@link Exploration#play()} happens inside the measured
     * region; the counter deltas are taken around it and the heap is measured
     * before and after, both times after two explicit collections.
     */
    private static Measurement singleRun(Grammar grammar, ExploreType exploreType,
                                         int timeoutSeconds) throws Exception {
        long heapBefore = settledHeap();
        GTS gts = new GTS(grammar);
        // registered before the exploration is created, because that
        // materialises the start state and so fires the first add update
        DiscoveryCounter counter = new DiscoveryCounter();
        gts.addLTSListener(counter);
        Exploration exploration = exploreType.newExploration(gts, null);
        long runningBefore = Exploration.getRunningTime();
        long matchingBefore = PlanSearchStrategy.searchFindReporter.getTotalTime();
        long isoBefore = IsoChecker.getTotalTime();
        long certifyingBefore = IsoChecker.getCertifyingTime();
        long generateBefore = MatchApplier.getGenerateTime();
        long reporterBefore = Reporter.getReportTime();
        int confluentBefore = MatchApplier.getConfluentDiamondCount();
        Runner runner = new Runner(exploration);
        Thread thread = new Thread(runner, "exploration-benchmark");
        long startNanos = System.nanoTime();
        thread.start();
        thread.join(timeoutSeconds * 1000L);
        boolean timedOut = thread.isAlive();
        if (timedOut) {
            thread.interrupt();
            thread.join(INTERRUPT_GRACE);
        }
        long wallNanos = System.nanoTime() - startNanos;
        Throwable error = runner.getError();
        if (error != null) {
            throw new BenchmarkException("exploration failed: " + error, error);
        }
        if (timedOut) {
            throw new BenchmarkException(String
                .format(Locale.ROOT, "TIMEOUT after %d s at %d states (alive=%b, interrupted=%b)",
                        timeoutSeconds, counter.getStates(), thread.isAlive(),
                        exploration.isInterrupted()));
        }
        int states = gts.getStateCount();
        int transitions = gts.getTransitionCount();
        int factoryNodes = gts.getHostFactory().getNodeCount();
        int factoryEdges = gts.getHostFactory().getEdgeCount();
        long retained = settledHeap() - heapBefore;
        return new Measurement(wallNanos, states, transitions, counter.getStates(),
            counter.getTransitions(), Exploration.getRunningTime() - runningBefore,
            PlanSearchStrategy.searchFindReporter.getTotalTime() - matchingBefore,
            IsoChecker.getTotalTime() - isoBefore,
            IsoChecker.getCertifyingTime() - certifyingBefore,
            MatchApplier.getGenerateTime() - generateBefore,
            Reporter.getReportTime() - reporterBefore,
            MatchApplier.getConfluentDiamondCount() - confluentBefore, runner.getAllocatedBytes(),
            retained, factoryNodes, factoryEdges);
    }

    /**
     * Compiles a fresh grammar from an already loaded store.
     * <p>
     * A fresh {@link GrammarModel} rather than the store's own
     * ({@link SystemStore#toGrammarModel()}) because both that and
     * {@link GrammarModel#toGrammar()} cache their result: asking the same
     * model for a grammar twice hands back the same object, rules and all, so
     * it would not detach the previous run's leaked rule applications. The
     * constructor is the documented way to build a model over a store, and
     * unlike {@code toGrammarModel()} it does not register the model as a
     * store observer — which is right here, since the benchmark never edits
     * the store.
     * @param store the loaded store holding the grammar's sources
     * @param startGraphName name of the start graph; {@code null} for the
     * grammar's default one
     */
    private static Grammar newGrammar(SystemStore store,
                                      @Nullable String startGraphName) throws FormatException {
        GrammarModel model = new GrammarModel(store);
        if (startGraphName != null) {
            model.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraphName));
        }
        return model.toGrammar();
    }

    /** Returns the used heap after two collections. */
    private static long settledHeap() {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        System.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /** Reads an int-valued system property, falling back to a default. */
    private static int intProperty(String name, int fallback) {
        String value = System.getProperty(name);
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exc) {
            return fallback;
        }
    }

    /** Prints the run's provenance: JVM, flags, assertion status and shape. */
    private static void printHeader(int warmups, int runs, int timeout) {
        System.out.printf(Locale.ROOT, "%n=== GROOVE exploration benchmark ===%n");
        System.out
            .printf(Locale.ROOT, "JVM:        %s %s (%s)%n", System.getProperty("java.vm.name"),
                    System.getProperty("java.version"), System.getProperty("java.vm.vendor"));
        System.out.printf(Locale.ROOT, "VM args:    %s%n", Management.getInputArguments());
        System.out
            .printf(Locale.ROOT, "Assertions: %s%n",
                    ExplorationBenchmark.class.desiredAssertionStatus()
                        ? "ENABLED (timings include assertion-only costs)"
                        : "disabled");
        System.out
            .printf(Locale.ROOT, "Max heap:   %d MB%n",
                    Runtime.getRuntime().maxMemory() / (1024 * 1024));
        System.out
            .printf(Locale.ROOT, "Shape:      %d warm-up(s), %d measured run(s), %d s timeout%n",
                    warmups, runs, timeout);
        System.out
            .printf(Locale.ROOT, "Time:       %s%n%n",
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    /** Format of the table rows, with every column taken as a string. */
    private static final String HEAD_FORMAT
        = "%-22s %7s %8s %8s %9s %8s %8s %8s %9s %9s %7s %7s %7s %7s %6s %7s %8s %8s %7s %8s";

    /** Format of a data row, matching {@link #HEAD_FORMAT} column for column. */
    private static final String ROW_FORMAT
        = "%-22s %7d %8d %8d %9d %8.1f %8.1f %8.1f %9.0f %9.0f %7d %7d %7d %7d %6d %7d %8.1f %8.1f %7d %8d";

    /** Format of a CSV row, in the same column order. */
    private static final String CSV_FORMAT
        = "%s,%d,%d,%d,%d,%.3f,%.3f,%.3f,%.1f,%.1f,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n";

    /** Column headers of the report table. */
    private static final String HEADER_ROW = String
        .format(Locale.ROOT, HEAD_FORMAT, "config", "states", "trans", "disc.st", "disc.tr",
                "med ms", "min ms", "max ms", "states/s", "trans/s", "match", "iso", "cert", "gen",
                "rep", "confl", "allocMB", "retMB", "fNodes", "fEdges");

    /** Separator below the column headers. */
    private static final String RULE_ROW = "-".repeat(HEADER_ROW.length());

    /**
     * Formats one report row from the median measurement of a result. The
     * throughput columns are computed from the discovered rather than the
     * stored counts, since those are what the run did work for.
     */
    private static String formatRow(Config config, Result result, Measurement median) {
        double medianSeconds = median.wallNanos() / 1e9;
        return String
            .format(Locale.ROOT, ROW_FORMAT, config.name(), median.states(), median.transitions(),
                    median.discoveredStates(), median.discoveredTransitions(),
                    median.wallNanos() / 1e6, result.minNanos() / 1e6, result.maxNanos() / 1e6,
                    median.discoveredStates() / medianSeconds,
                    median.discoveredTransitions() / medianSeconds, median.matching(),
                    median.isoTotal(), median.certifying(), median.generate(), median.reporter(),
                    median.confluent(), median.allocatedBytes() / 1048576.0,
                    median.retainedBytes() / 1048576.0, median.factoryNodes(),
                    median.factoryEdges());
    }

    /** Appends a result row to the CSV file named by {@code groove.bench.csv}, if any. */
    private static void appendCsv(Config config, Result result, Measurement median) {
        String fileName = System.getProperty("groove.bench.csv");
        if (fileName == null) {
            return;
        }
        Path path = Path.of(fileName);
        boolean fresh = !Files.exists(path);
        try (PrintWriter writer = new PrintWriter(Files
            .newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                               StandardOpenOption.APPEND))) {
            if (fresh) {
                writer
                    .println("config,states,transitions,discoveredStates,discoveredTransitions,"
                        + "medianMs,minMs,maxMs,statesPerSec,"
                        + "transPerSec,matchingMs,isoMs,certifyingMs,generateMs,reporterMs,"
                        + "confluent,allocBytes,retainedBytes,factoryNodes,factoryEdges");
            }
            double medianSeconds = median.wallNanos() / 1e9;
            writer
                .printf(Locale.ROOT, CSV_FORMAT, config.name(), median.states(),
                        median.transitions(), median.discoveredStates(),
                        median.discoveredTransitions(), median.wallNanos() / 1e6,
                        result.minNanos() / 1e6, result.maxNanos() / 1e6,
                        median.discoveredStates() / medianSeconds,
                        median.discoveredTransitions() / medianSeconds, median.matching(),
                        median.isoTotal(), median.certifying(), median.generate(),
                        median.reporter(), median.confluent(), median.allocatedBytes(),
                        median.retainedBytes(), median.factoryNodes(), median.factoryEdges());
        } catch (IOException exc) {
            System.out
                .printf(Locale.ROOT, "Could not append to %s: %s%n", fileName, exc.getMessage());
        }
    }

    /**
     * The measurements of one configuration.
     * @param measured the measured (non-warm-up) runs, in run order
     * @param failure a message describing why the configuration could not be
     * measured, or {@code null} if it was measured successfully
     */
    public record Result(List<Measurement> measured, @Nullable String failure) {
        /**
         * Returns the measurement with the median wall time. All non-timing
         * metrics are reported from this one run rather than as per-metric
         * medians, so that the reported numbers describe a single coherent
         * exploration.
         */
        public Measurement median() {
            List<Measurement> sorted = new ArrayList<>(this.measured);
            sorted.sort(Comparator.comparingLong(Measurement::wallNanos));
            return sorted.get(sorted.size() / 2);
        }

        /** Returns the shortest wall time over the measured runs. */
        public long minNanos() {
            long result = Long.MAX_VALUE;
            for (Measurement next : this.measured) {
                result = Math.min(result, next.wallNanos());
            }
            return result;
        }

        /** Returns the longest wall time over the measured runs. */
        public long maxNanos() {
            long result = 0;
            for (Measurement next : this.measured) {
                result = Math.max(result, next.wallNanos());
            }
            return result;
        }
    }

    /**
     * The metrics of one exploration run. All times are in milliseconds and
     * are deltas of the corresponding static counters, which accumulate over
     * the JVM's lifetime.
     * @param wallNanos wall time of {@link Exploration#play()}
     * @param states number of states retained in the resulting GTS
     * @param transitions number of transitions retained in the resulting GTS
     * @param discoveredStates number of states the exploration discovered,
     * counted by a {@link DiscoveryCounter}; equals {@link #states()} unless
     * the configuration switches persistence off
     * @param discoveredTransitions number of transitions the exploration
     * discovered, likewise
     * @param running delta of {@link Exploration#getRunningTime()}
     * @param matching delta of the matcher's {@code Search.find()} reporter
     * @param isoTotal delta of {@link IsoChecker#getTotalTime()}
     * @param certifying delta of {@link IsoChecker#getCertifyingTime()}
     * @param generate delta of {@link MatchApplier#getGenerateTime()}
     * @param reporter delta of {@link Reporter#getReportTime()}, i.e. the
     * instrumentation's own cost
     * @param confluent delta of {@link MatchApplier#getConfluentDiamondCount()}
     * @param allocatedBytes bytes allocated on the exploring thread, or
     * {@code -1} if the JVM does not support the measurement
     * @param retainedBytes used heap after the run minus used heap before it,
     * both after two collections and with the GTS still referenced. It is an
     * upper bound on what the run really needs to keep, for two reasons:
     * {@link System#gc()} does not clear soft references, so the figure
     * includes the state caches (in {@code binary-tree-dfs-unstored} about
     * 60 % of it), which a collector under real memory pressure would reclaim;
     * and it includes whatever the run leaked into its {@link Grammar}, which
     * is a per-run object precisely so that this does not accumulate
     * @param factoryNodes number of host nodes the GTS's host factory holds
     * @param factoryEdges number of host edges the GTS's host factory holds
     */
    public record Measurement(long wallNanos, int states, int transitions, int discoveredStates,
                              int discoveredTransitions, long running, long matching, long isoTotal,
                              long certifying, long generate, long reporter, int confluent,
                              long allocatedBytes, long retainedBytes, int factoryNodes,
                              int factoryEdges) {
        // no additional members
    }

    /**
     * GTS listener that counts discovered states and transitions.
     * <p>
     * The GTS notifies its listeners of every state and transition added,
     * whether or not it is stored (see {@link GTS#setStoring}), so this is
     * the only source of the exploration's true size under
     * {@code persistence=none}. Deliberately two unsynchronised {@code int}
     * increments: the exploration path already pays a listener notification
     * per state and per transition, and the counter must not add measurable
     * weight to it.
     */
    private static final class DiscoveryCounter implements GTSListener {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            this.states++;
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            this.transitions++;
        }

        /** Returns the number of states discovered since construction. */
        int getStates() {
            return this.states;
        }

        private int states;

        /** Returns the number of transitions discovered since construction. */
        int getTransitions() {
            return this.transitions;
        }

        private int transitions;
    }

    /**
     * Runnable wrapper that plays an exploration on a dedicated thread,
     * capturing any failure and the thread's allocation count.
     */
    private static final class Runner implements Runnable {
        Runner(Exploration exploration) {
            this.exploration = exploration;
        }

        private final Exploration exploration;

        @Override
        public void run() {
            long before = Management.getAllocatedBytes();
            try {
                this.exploration.play();
            } catch (Throwable exc) {
                this.error = exc;
            } finally {
                long after = Management.getAllocatedBytes();
                this.allocated = before < 0 || after < 0
                    ? -1
                    : after - before;
            }
        }

        /** Returns the failure of the run, if any. */
        @Nullable
        Throwable getError() {
            return this.error;
        }

        private @Nullable Throwable error;

        /** Returns the bytes allocated during the run, or {@code -1}. */
        long getAllocatedBytes() {
            return this.allocated;
        }

        private long allocated = -1;
    }

    /**
     * Reflective access to the platform management beans.
     * <p>
     * The test tree is patched into the product module
     * {@code nl.utwente.groove} — by Surefire and by Eclipse alike — so a
     * direct reference to {@code java.lang.management} or
     * {@code com.sun.management} would oblige the product's
     * {@code module-info} to carry a {@code requires} for a test-only need,
     * which is not wanted. Reflection sidesteps that: the access check on
     * {@link Method#invoke} asks only whether the member's package is exported
     * (both are, unqualified), not whether the calling module reads the
     * defining one. Compilation therefore needs nothing; only run-time module
     * <em>resolution</em> does, since the two modules are not roots when the
     * application module is. On the module path that takes
     * {@code --add-modules java.management,jdk.management}, which the Surefire
     * {@code argLine} and the Eclipse launch configuration both pass; on the
     * class path the unnamed module resolves {@code java.se} and needs no
     * flag. Nothing here throws: a missing class or method yields
     * {@code "n/a"} for the arguments and {@code -1} for the allocation count.
     */
    private static final class Management {
        private Management() {
            // no instances
        }

        /**
         * Returns the JVM's input arguments in list form, or {@code "n/a"} if
         * {@code java.management} is not resolved.
         */
        static String getInputArguments() {
            try {
                Class<?> factoryClass = Class.forName("java.lang.management.ManagementFactory");
                Class<?> beanClass = Class.forName("java.lang.management.RuntimeMXBean");
                Object bean = factoryClass.getMethod("getRuntimeMXBean").invoke(null);
                return String.valueOf(beanClass.getMethod("getInputArguments").invoke(bean));
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exc) {
                return "n/a";
            }
        }

        /**
         * Returns the number of bytes allocated by the current thread since it
         * started, or {@code -1} if {@code jdk.management} is not resolved or
         * the measurement is switched off.
         */
        static long getAllocatedBytes() {
            Object bean = THREAD_BEAN;
            Method method = ALLOCATED_METHOD;
            if (bean == null || method == null) {
                return -1;
            }
            try {
                Object result = method.invoke(bean, Thread.currentThread().threadId());
                // a long-returning method always yields a box, never null
                assert result != null;
                return (Long) result;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exc) {
                return -1;
            }
        }

        /** The {@code com.sun.management.ThreadMXBean}, or {@code null} if unavailable. */
        private static final @Nullable Object THREAD_BEAN;

        /** {@code getThreadAllocatedBytes(long)} of {@link #THREAD_BEAN}, or {@code null}. */
        private static final @Nullable Method ALLOCATED_METHOD;

        static {
            Object bean = null;
            Method method = null;
            try {
                Class<?> factoryClass = Class.forName("java.lang.management.ManagementFactory");
                Class<?> beanClass = Class.forName("com.sun.management.ThreadMXBean");
                Object candidate = factoryClass.getMethod("getThreadMXBean").invoke(null);
                if (beanClass.isInstance(candidate) && Boolean.TRUE
                    .equals(beanClass.getMethod("isThreadAllocatedMemoryEnabled").invoke(candidate))) {
                    bean = candidate;
                    method = beanClass.getMethod("getThreadAllocatedBytes", long.class);
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exc) {
                // both stay null, and the allocation column reports -1
            }
            THREAD_BEAN = bean;
            ALLOCATED_METHOD = method;
        }
    }

    /** Exception signalling that a configuration could not be measured. */
    private static final class BenchmarkException extends Exception {
        BenchmarkException(String message) {
            super(message);
        }

        BenchmarkException(String message, Throwable cause) {
            super(message, cause);
        }

        private static final long serialVersionUID = 1L;
    }
}
