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
package nl.utwente.groove.gui.yfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.gui.Imager;
import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.FileType;

/**
 * Runs the headless {@link Imager} on the yFiles backend, the way the main project's
 * {@code ImagerTest} runs it on the JGraph backend: every graph of a sample grammar is
 * exported in every canvas format, and the output is checked to be a rendering rather
 * than an empty file. The output stays in {@code target/imager} for inspection.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesImagerTest {
    /** The GROOVE checkout the fixture grammars are read from; set by the build. */
    private static final String GROOVE_DIR = System.getProperty("groove.dir", "..");
    private static final String GRAMMAR = GROOVE_DIR + "/junit/samples/ferryman.gps";
    private static final File OUTPUT_DIR = new File("target/imager");

    /** The Imager runs on the yFiles backend, selected by the backend ranking. */
    @Test
    public void backendIsYFiles() {
        assertInstanceOf(YFilesBackend.class, GraphBackend.instance());
    }

    /** Exports to PNG: every graph becomes an image showing something. */
    @Test
    public void png() throws IOException {
        for (File file : export(FileType.PNG)) {
            BufferedImage image = ImageIO.read(file);
            assertNotNull(image, file + " is not an image");
            assertTrue(colourCount(image) > 2, file + " shows nothing");
        }
    }

    /** Exports to JPG: every graph becomes an image showing something. */
    @Test
    public void jpg() throws IOException {
        for (File file : export(FileType.JPG)) {
            BufferedImage image = ImageIO.read(file);
            assertNotNull(image, file + " is not an image");
            assertTrue(colourCount(image) > 2, file + " shows nothing");
        }
    }

    /** Exports to SVG: every graph becomes a drawing with text or paths. */
    @Test
    public void svg() throws IOException {
        for (File file : export(FileType.SVG)) {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            assertTrue(text.contains("<svg"), file + " is not an SVG document");
            assertTrue(text.contains("<text") || text.contains("<path"),
                       file + " shows nothing");
        }
    }

    /** Exports to PDF: every graph becomes a PDF document. */
    @Test
    public void pdf() throws IOException {
        for (File file : export(FileType.PDF)) {
            assertTrue(startsWith(file, "%PDF"), file + " is not a PDF document");
        }
    }

    /** Exports to EPS: every graph becomes a PostScript document. */
    @Test
    public void eps() throws IOException {
        for (File file : export(FileType.EPS)) {
            assertTrue(startsWith(file, "%!PS"), file + " is not a PostScript document");
        }
    }

    /** Exports to TikZ: every graph becomes a figure drawing nodes. */
    @Test
    public void tikz() throws IOException {
        for (File file : export(FileType.TIKZ)) {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            assertTrue(text.contains("\\node"), file + " draws no node");
        }
    }

    /**
     * Exports every graph of the sample grammar in a given format, into a directory
     * named after the format, and returns the files written, after checking that there
     * is exactly one per graph of the grammar.
     */
    private Set<File> export(FileType type) throws IOException {
        String ext = type.getExtension().substring(1);
        File outDir = new File(OUTPUT_DIR, ext);
        outDir.mkdirs();
        File[] stale = outDir.listFiles();
        assertNotNull(stale);
        for (File file : stale) {
            file.delete();
        }
        try {
            Imager.execute(new String[] {"-f", ext, "-v", "0", GRAMMAR, outDir.getPath()});
        } catch (Exception exc) {
            throw new IOException(exc);
        }
        File[] files = outDir.listFiles();
        assertNotNull(files);
        Set<String> imaged = new TreeSet<>();
        Set<File> result = new HashSet<>();
        for (File file : files) {
            assertTrue(file.length() > 0, file + " is empty");
            imaged.add(type.stripExtension(file.getName()));
            result.add(file);
        }
        assertEquals(graphNames(), imaged);
        return result;
    }

    /** Returns the names of the graph files of the sample grammar. */
    private static Set<String> graphNames() {
        Set<String> result = new TreeSet<>();
        File[] files = new File(GRAMMAR).listFiles(FileType.GRAPHS.getFilter());
        assertNotNull(files);
        for (File file : files) {
            result.add(file.getName());
        }
        return result;
    }

    /** Returns the number of distinct colours of an image, up to three. */
    private static int colourCount(BufferedImage image) {
        Set<Integer> colours = new HashSet<>();
        for (int x = 0; x < image.getWidth() && colours.size() <= 2; x++) {
            for (int y = 0; y < image.getHeight() && colours.size() <= 2; y++) {
                colours.add(image.getRGB(x, y));
            }
        }
        return colours.size();
    }

    /** Tests whether the content of a file starts with a given ASCII prefix. */
    private static boolean startsWith(File file, String prefix) throws IOException {
        byte[] head = new byte[prefix.length()];
        try (var in = Files.newInputStream(file.toPath())) {
            return in.read(head) == head.length
                && new String(head, StandardCharsets.US_ASCII).equals(prefix);
        }
    }
}
