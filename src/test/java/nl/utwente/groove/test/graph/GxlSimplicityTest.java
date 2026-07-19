// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.test.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.io.graph.GxlIO;

/**
 * Tests for the GXL round-trip of graph simplicity: non-simple graphs
 * (which may contain parallel equi-labelled edges) are saved with
 * {@code edgeids="true"} and explicit edge identities, and load back
 * as non-simple graphs with their parallel edges intact.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class GxlSimplicityTest {
    /** Saves a graph to a fresh temporary file and returns the file. */
    private File save(PlainGraph graph) throws IOException {
        File file = File.createTempFile("gxl-simplicity-test", ".gxl");
        file.deleteOnExit();
        GxlIO.instance().saveGraph(graph, file);
        return file;
    }

    @Test
    public void testNonSimpleRoundTrip() throws IOException {
        PlainGraph graph = new PlainGraph("lts", GraphRole.LTS, false);
        var source = graph.addNode();
        var target = graph.addNode();
        graph.addEdge(source, "a", target);
        graph.addEdge(source, "a", target);
        graph.addEdge(source, "b", target);
        assertEquals(3, graph.edgeCount());
        File file = save(graph);
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("edgeids=\"true\""));
        assertTrue(content.contains("id=\"e0\""));
        PlainGraph image = GxlIO.instance().loadGraph(file).toPlainGraph();
        assertFalse(image.isSimple());
        assertEquals(2, image.nodeCount());
        assertEquals(3, image.edgeCount());
    }

    @Test
    public void testSimpleRoundTrip() throws IOException {
        PlainGraph graph = new PlainGraph("start", GraphRole.HOST);
        var source = graph.addNode();
        var target = graph.addNode();
        graph.addEdge(source, "a", target);
        graph.addEdge(source, "a", target);
        assertEquals(1, graph.edgeCount());
        File file = save(graph);
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("edgeids=\"false\""));
        assertFalse(content.contains("id=\"e0\""));
        PlainGraph image = GxlIO.instance().loadGraph(file).toPlainGraph();
        assertTrue(image.isSimple());
        assertEquals(1, image.edgeCount());
    }

    /** Loads an LTS file without declared edge identities, as saved by
     * older GROOVE versions: the LTS role alone must make it non-simple,
     * so that its parallel edges survive. */
    @Test
    public void testLegacyLtsLoad() throws IOException {
        String legacy = """
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
                <graph role="lts" edgeids="false" edgemode="directed" id="lts">
                    <node id="n0"/>
                    <node id="n1"/>
                    <edge from="n0" to="n1">
                        <attr name="label"><string>a</string></attr>
                    </edge>
                    <edge from="n0" to="n1">
                        <attr name="label"><string>a</string></attr>
                    </edge>
                </graph>
            </gxl>
            """;
        PlainGraph image = GxlIO
            .instance()
            .loadPlainGraph(new ByteArrayInputStream(legacy.getBytes(StandardCharsets.UTF_8)));
        assertFalse(image.isSimple());
        assertEquals(2, image.nodeCount());
        assertEquals(2, image.edgeCount());
    }

    /** Loads a non-LTS file with declared edge identities: the edgeids
     * flag alone must make it non-simple. */
    @Test
    public void testEdgeidsLoad() throws IOException {
        String stored = """
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
                <graph role="graph" edgeids="true" edgemode="directed" id="multi">
                    <node id="n0"/>
                    <edge id="e0" from="n0" to="n0">
                        <attr name="label"><string>a</string></attr>
                    </edge>
                    <edge id="e1" from="n0" to="n0">
                        <attr name="label"><string>a</string></attr>
                    </edge>
                </graph>
            </gxl>
            """;
        PlainGraph image = GxlIO
            .instance()
            .loadPlainGraph(new ByteArrayInputStream(stored.getBytes(StandardCharsets.UTF_8)));
        assertFalse(image.isSimple());
        assertEquals(1, image.nodeCount());
        assertEquals(2, image.edgeCount());
    }
}
