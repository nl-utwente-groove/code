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
package nl.utwente.groove.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.utwente.groove.gui.Imager;
import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.util.io.FileType;

/**
 * Test for the Imager command-line facility
 * @author Arend Rensink
 * @version $Revision$
 */
@Category(SlowTest.class)
@SuppressWarnings("javadoc")
public class ImagerTest {
    private final static String TEST_DIR = "junit/samples/ferryman.gps";
    private final static String OUTPUT_DIR = "junit/try";

    private Set<String> graphFiles;

    @Before
    public void setUp() {
        this.graphFiles = new TreeSet<>();
        File[] files = new File(TEST_DIR).listFiles(FileType.GRAPHS.getFilter());
        assert files != null; // TEST_DIR is a checked-in fixture directory
        for (File file : files) {
            this.graphFiles.add(file.getName());
        }
        new File(OUTPUT_DIR).mkdir();
    }

    @After
    public void tearDown() {
        // the test itself may already have deleted the output directory
        File[] files = new File(OUTPUT_DIR).listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        new File(OUTPUT_DIR).delete();
    }

    @Test
    public void testPng() {
        test(FileType.PNG);
    }

    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    @Test
    public void testSvg() {
        test(FileType.SVG);
    }

    @Test
    public void testTikz() {
        test(FileType.TIKZ);
    }

    /**
     * A backend requested on the command line that is not on the class path (the yFiles
     * backend is never on the class path of the main project) gives way to the default
     * one, with a warning on standard output; the images are still made.
     */
    @Test
    public void testUnavailableBackend() {
        PrintStream out = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true));
        try {
            test(FileType.PNG, "-b", GraphBackend.YFILES);
        } finally {
            System.setOut(out);
        }
        String output = captured.toString();
        Assert.assertTrue(output, output.startsWith("Warning: "));
        Assert.assertTrue(output, output.contains("'" + GraphBackend.JGRAPH + "'"));
    }

    private void test(FileType type, String... options) {
        new File(OUTPUT_DIR).mkdir();
        try {
            List<String> args = new ArrayList<>();
            args.addAll(List.of("-f", type.getExtension().substring(1), "-v", "0"));
            args.addAll(List.of(options));
            args.addAll(List.of(TEST_DIR, OUTPUT_DIR));
            Imager.execute(args.toArray(new String[0]));
        } catch (Exception exc) {
            exc.printStackTrace();
            Assert.fail(exc.getMessage());
        }
        Set<String> imageFiles = new TreeSet<>();
        File[] files = new File(OUTPUT_DIR).listFiles();
        assert files != null; // OUTPUT_DIR was created above
        for (File file : files) {
            imageFiles.add(type.stripExtension(file.getName()));
        }
        Assert.assertEquals(this.graphFiles, imageFiles);
        new File(OUTPUT_DIR).delete();
    }
}
