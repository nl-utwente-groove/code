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
 */
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;

/**
 * Seals the visualization-backend boundary (see {@code claude/view-facade.md}): no file
 * outside the backend package {@code nl.utwente.groove.gui.jgraph} may import the JGraph
 * library or the backend package, except for the files listed in {@link #ALLOWED}, each
 * tagged with the phase or slice of the migration that removes it. The test fails both on
 * a new violation and on a stale entry, so the list only ever shrinks.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public class ArchitectureTest {
    /** Import prefixes that are confined to the backend package. */
    private static final String[] BACKEND_IMPORTS
        = {"import org.jgraph.", "import com.jgraph.", "import nl.utwente.groove.gui.jgraph.",
            "import static nl.utwente.groove.gui.jgraph.", "import static org.jgraph."};

    /** The backend package, as a source path fragment. */
    private static final String BACKEND_PACKAGE = "nl/utwente/groove/gui/jgraph/";

    /**
     * Files outside the backend that are still allowed to import backend types,
     * with the migration step that removes them; paths relative to the source roots.
     */
    private static final Map<String,String> ALLOWED = new TreeMap<>(Map.ofEntries(
        // phase 1b, slice 5: export seam
        Map.entry("nl/utwente/groove/gui/action/ExportAction.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/JGraphExportable.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/RasterExporter.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/util/GraphToEPS.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/util/GraphToPDF.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/util/GraphToSVG.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/util/GraphToTikz.java", "1b-5"),
        Map.entry("nl/utwente/groove/gui/export/util/GraphToVector.java", "1b-5"),
        // phase 2: ownership inversion and per-role view models;
        // GraphPreviewDialog and Imager construct canvases (Imager also exports them, slice 5)
        Map.entry("nl/utwente/groove/gui/action/ExploreAction.java", "2"),
        Map.entry("nl/utwente/groove/gui/dialog/GraphPreviewDialog.java", "2"),
        Map.entry("nl/utwente/groove/gui/display/GraphTab.java", "2"),
        Map.entry("nl/utwente/groove/gui/Imager.java", "2"),
        Map.entry("nl/utwente/groove/gui/display/JGraphPanel.java", "2"),
        Map.entry("nl/utwente/groove/gui/display/LTSDisplay.java", "2"),
        Map.entry("nl/utwente/groove/gui/display/StateDisplay.java", "2"),
        Map.entry("nl/utwente/groove/test/DetachedCellVisualsTest.java", "2"),
        Map.entry("nl/utwente/groove/test/LayouterTest.java", "2"),
        Map.entry("nl/utwente/groove/test/SubtypeLabelArrowTest.java", "2"),
        // phase 3: the editor's own undo and edit model
        Map.entry("nl/utwente/groove/gui/display/GraphEditorTab.java", "3")));

    /** Source roots to scan. */
    private static final Path[] SOURCE_ROOTS = {Path.of("src/main/java"), Path.of("src/test/java")};

    /** Checks that backend imports occur only inside the backend package or in allowed files. */
    @Test
    public void testBackendConfined() throws IOException {
        List<String> violations = new ArrayList<>();
        List<String> stale = new ArrayList<>(ALLOWED.keySet());
        for (Path root : SOURCE_ROOTS) {
            assertTrue(Files.isDirectory(root), "Source root " + root + " not found");
            try (Stream<Path> files = Files.walk(root)) {
                for (Path file : (Iterable<Path>) files::iterator) {
                    String relative = root.relativize(file).toString().replace('\\', '/');
                    if (!relative.endsWith(".java") || relative.startsWith(BACKEND_PACKAGE)) {
                        continue;
                    }
                    List<String> offending = offendingImports(file);
                    if (offending.isEmpty()) {
                        continue;
                    }
                    if (ALLOWED.containsKey(relative)) {
                        stale.remove(relative);
                    } else {
                        violations.add(relative + ": " + offending);
                    }
                }
            }
        }
        assertTrue(violations.isEmpty(),
                   "Backend imports outside the backend package:\n" + String.join("\n", violations));
        assertTrue(stale.isEmpty(),
                   "Allowed files no longer import the backend; remove them from the list:\n"
                       + String.join("\n", stale));
    }

    /** Returns the backend imports of a given source file. */
    private static List<String> offendingImports(Path file) throws IOException {
        List<String> result = new ArrayList<>();
        for (String line : Files.readAllLines(file)) {
            String trimmed = line.strip();
            for (String prefix : BACKEND_IMPORTS) {
                if (trimmed.startsWith(prefix)) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }
}
