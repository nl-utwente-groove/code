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
package nl.utwente.groove.test.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporters;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.WriterExporter;
import nl.utwente.groove.io.graph.AutIO;
import nl.utwente.groove.io.graph.GxlIO;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileType;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the headless exporters in {@code io.external.format}: registry
 * lookup, the {@code .aut} export/import round trip, the structure of the
 * {@code .fsm} output, and the LTS-to-control export including a full round
 * trip in which the exported program steers a re-exploration of the grammar
 * it was derived from.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class ExportTest {
    /** Location of the fixture grammar. */
    static private final String GRAMMAR = "junit/samples/control.gps";
    /** Location of a fixture grammar whose rules have parameters and special transition labels. */
    static private final String LABEL_GRAMMAR = "junit/samples/attributes-label.gps";

    /** The headless registry resolves the expected exporter per file type,
     * taking the exportable's content into account. */
    @Test
    public void testRegistry() {
        assertSame(AutPorter.instance(), Exporters.getExporter(FileType.AUT));
        assertTrue(Exporters.getExporter(FileType.FSM) instanceof WriterExporter);
        assertTrue(Exporters.getExporter(FileType.DOT) instanceof WriterExporter);
        // a graph outside the grammar is exported to .gxl by the writer exporter,
        // not by the native resource porter, which handles the grammar extensions
        assertTrue(Exporters
            .getExporter(FileType.GXL, Exportable.graph(createGraph())) instanceof WriterExporter);
        assertEquals(null, Exporters.getExporter(null));
        GTS gts = explore(GRAMMAR);
        var controlExporter = Exporters.getExporter(FileType.CONTROL, Exportable.graph(gts));
        assertTrue(controlExporter instanceof WriterExporter);
        // the control exporter only takes (fragments of) LTSs
        assertNotNull(controlExporter);
        assertFalse(controlExporter.exports(Exportable.graph(createGraph())));
        assertTrue(controlExporter.getFileTypes(Exportable.graph(createGraph())).isEmpty());
        assertEquals(null,
                     Exporters.getExporter(FileType.CONTROL, Exportable.graph(createGraph())));
    }

    /** A graph exported to {@code .aut} and imported back preserves the node
     * count and the edge label multiset. */
    @Test
    public void testAutRoundTrip(@TempDir Path tmp) throws PortException, IOException {
        PlainGraph graph = createGraph();
        File file = tmp.resolve("sample.aut").toFile();
        AutPorter.instance().doExport(Exportable.graph(graph), file, FileType.AUT);
        var imports = AutPorter.instance().doImport(file, FileType.AUT, loadGrammar());
        assertEquals(1, imports.size());
        var imported = imports.iterator().next();
        assertEquals(ResourceKind.HOST, imported.kind());
        assertEquals(QualName.name("sample"), imported.qualName());
        var host = imported.graph();
        assertNotNull(host);
        assertEquals(graph.nodeCount(), host.nodeCount());
        assertEquals(labelBag(graph), labelBag(host));
    }

    /** Labels containing {@code .aut} metacharacters (commas, quotes,
     * backslashes) take the quoted syntax on disk but round-trip verbatim:
     * the saver quotes and escapes, the loader unquotes and unescapes. */
    @Test
    public void testAutSpecialLabels(@TempDir Path tmp) throws IOException {
        List<String> labels = List
            .of("a,b", "\"a\"", "\"a,b\"", "back\\slash", "quo\"te", "esc\\\",aped", "\"");
        PlainGraph graph = new PlainGraph("specials", GraphRole.HOST);
        PlainNode n0 = graph.addNode();
        PlainNode n1 = graph.addNode();
        for (String label : labels) {
            graph.addEdge(n0, label, n1);
        }
        File file = tmp.resolve("specials.aut").toFile();
        AutIO io = new AutIO();
        io.saveGraph(graph, file);
        PlainGraph clone;
        try (var in = Files.newInputStream(file.toPath())) {
            clone = io.loadGraph(in);
        }
        assertEquals(2, clone.nodeCount());
        assertEquals(labels.stream().sorted().collect(Collectors.toList()), labelBag(clone));
    }

    /** The {@code .fsm} output consists of a header, a numbered node section
     * and an edge section with quoted labels. */
    @Test
    public void testFsmExport(@TempDir Path tmp) throws PortException, IOException {
        PlainGraph graph = createGraph();
        File file = tmp.resolve("sample.fsm").toFile();
        var exporter = Exporters.getExporter(FileType.FSM);
        assertNotNull(exporter);
        exporter.doExport(Exportable.graph(graph), file, FileType.FSM);
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals("NodeNumber(0)", lines.get(0));
        assertEquals("---", lines.get(1));
        int sep = lines.subList(2, lines.size()).indexOf("---") + 2;
        assertEquals(2 + graph.nodeCount(), sep);
        List<String> edgeLines = lines.subList(sep + 1, lines.size());
        assertEquals(graph.edgeCount(), edgeLines.size());
        for (String line : edgeLines) {
            assertTrue(line.matches("\\d+ \\d+ \"[^\"]*\""), line);
        }
        // an exportable without a graph is rejected
        var controlModel = loadGrammar().getResource(ResourceKind.CONTROL, QualName.name("control"));
        assertNotNull(controlModel);
        var noGraph = Exportable.resource(controlModel);
        assertThrows(PortException.class, () -> exporter.doExport(noGraph, file, FileType.FSM));
    }

    /** A graph exported to {@code .gxl} through the registry loads back with the
     * same node count and edge label multiset. */
    @Test
    public void testGxlRoundTrip(@TempDir Path tmp) throws PortException, IOException {
        PlainGraph graph = createGraph();
        File file = tmp.resolve("sample.gxl").toFile();
        var exportable = Exportable.graph(graph);
        var exporter = Exporters.getExporter(FileType.GXL, exportable);
        assertNotNull(exporter);
        exporter.doExport(exportable, file, FileType.GXL);
        var clone = GxlIO.instance().loadGraph(file);
        assertEquals(graph.nodeCount(), clone.nodeCount());
        assertEquals(labelBag(graph), labelBag(clone));
    }

    /** An explored GTS exported to a control program yields a program that,
     * activated on (a copy of) the same grammar, steers a successful
     * re-exploration: the round trip proves the program is syntactically
     * valid and calls only existing rules. */
    @Test
    public void testLts2ControlRoundTrip(@TempDir Path tmp) throws PortException, IOException {
        GTS gts = explore(GRAMMAR);
        File file = tmp.resolve("enforce.gcp").toFile();
        List<String> lines = exportControl(gts, file);
        assertTrue(lines.stream().anyMatch(l -> l.trim().startsWith("// state")));
        assertTrue(lines.stream().anyMatch(l -> l.trim().endsWith(";")));
        GTS reGts = reExplore(GRAMMAR, file, tmp);
        // the program drives a genuine exploration; note that it may
        // overapproximate the original LTS, since a control program
        // cannot distinguish two matches of the same rule (the choice
        // branches of this export both start with the same rule call)
        assertTrue(reGts.nodeCount() > 1);
    }

    /** The rule calls of an exported program use the rule names, not the
     * special transition labels of the rules, and node arguments (which a
     * control program cannot refer to) become don't-care arguments, so that
     * the program compiles against the grammar it was derived from (gh #861). */
    @Test
    public void testLts2ControlSpecialLabels(@TempDir Path tmp) throws PortException, IOException {
        GTS gts = explore(LABEL_GRAMMAR);
        File file = tmp.resolve("enforce.gcp").toFile();
        List<String> calls = exportControl(gts, file)
            .stream()
            .map(String::trim)
            .filter(l -> l.endsWith(";"))
            .toList();
        assertFalse(calls.isEmpty());
        // the fixture's special labels are "grav %s" and "sc(%s,%s)"
        assertTrue(calls.stream().noneMatch(l -> l.startsWith("grav") || l.startsWith("sc(")),
                   calls.toString());
        assertTrue(calls.stream().anyMatch(l -> l.startsWith("set_gravity(")), calls.toString());
        assertTrue(calls.stream().anyMatch(l -> l.startsWith("add_score(_,")), calls.toString());
        assertTrue(reExplore(LABEL_GRAMMAR, file, tmp).nodeCount() > 1);
    }

    /** Exports an LTS to a control program file through the registry,
     * and returns the lines of the file. */
    private List<String> exportControl(GTS gts, File file) throws PortException, IOException {
        var exportable = Exportable.graph(gts);
        var exporter = Exporters.getExporter(FileType.CONTROL, exportable);
        assertNotNull(exporter);
        exporter.doExport(exportable, file, FileType.CONTROL);
        return Files.readAllLines(file.toPath());
    }

    /** Copies a grammar, drops a given control program in as "enforce",
     * and returns the GTS explored under that program from the start graph "start".
     * Fails if the program does not compile against the grammar. */
    private GTS reExplore(String grammar, File program, Path tmp) throws IOException {
        Path copy = tmp.resolve("copy.gps");
        Files.createDirectory(copy);
        File[] files = new File(grammar).listFiles();
        assert files != null; // checked-in fixture directory
        for (File f : files) {
            if (f.isFile()) {
                Files.copy(f.toPath(), copy.resolve(f.getName()));
            }
        }
        Files.copy(program.toPath(), copy.resolve("enforce.gcp"));
        try {
            GrammarModel copyModel = SystemStore.newGrammar(copy.toFile());
            copyModel.setLocalActiveNames(ResourceKind.CONTROL, QualName.name("enforce"));
            copyModel.setLocalActiveNames(ResourceKind.HOST, QualName.name("start"));
            GTS reGts = new GTS(copyModel.toGrammar());
            Exploration exploration = Exploration.explore(reGts);
            assertFalse(exploration.isInterrupted());
            return reGts;
        } catch (FormatException exc) {
            fail("Exported control program does not compile: " + exc);
            throw new IllegalStateException(); // unreachable
        }
    }

    /** Creates a small deterministic plain graph with binary edges only. */
    private PlainGraph createGraph() {
        PlainGraph result = new PlainGraph("sample", GraphRole.HOST);
        PlainNode n0 = result.addNode();
        PlainNode n1 = result.addNode();
        PlainNode n2 = result.addNode();
        result.addEdge(n0, "a", n1);
        result.addEdge(n1, "b", n2);
        result.addEdge(n2, "c", n0);
        result.addEdge(n0, "a", n2);
        return result;
    }

    /** Returns the sorted multiset of edge label texts of a graph. */
    private List<String> labelBag(Graph graph) {
        return graph
            .edgeSet()
            .stream()
            .map(Edge::label)
            .map(l -> l.text())
            .sorted()
            .collect(Collectors.toList());
    }

    /** Loads the fixture grammar. */
    private GrammarModel loadGrammar() throws IOException {
        return SystemStore.newGrammar(new File(GRAMMAR));
    }

    /** Explores a fixture grammar with its control program "control" and
     * start graph "start", and returns the resulting GTS. */
    private GTS explore(String grammar) {
        try {
            GrammarModel model = SystemStore.newGrammar(new File(grammar));
            model.setLocalActiveNames(ResourceKind.CONTROL, QualName.name("control"));
            model.setLocalActiveNames(ResourceKind.HOST, QualName.name("start"));
            GTS gts = new GTS(model.toGrammar());
            Exploration exploration = Exploration.explore(gts);
            assertFalse(exploration.isInterrupted());
            return gts;
        } catch (IOException | FormatException exc) {
            fail(exc.toString());
            throw new IllegalStateException(); // unreachable
        }
    }
}
